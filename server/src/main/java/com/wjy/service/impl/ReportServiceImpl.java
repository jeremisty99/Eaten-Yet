package com.wjy.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.wjy.entity.OrderDetail;
import com.wjy.entity.Orders;
import com.wjy.entity.User;
import com.wjy.mapper.OrderDetailMapper;
import com.wjy.mapper.OrderMapper;
import com.wjy.mapper.UserMapper;
import com.wjy.result.Result;
import com.wjy.service.ReportService;
import com.wjy.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;

    /**
     * 营业额统计
     *
     * @return
     */
    @Override
    public Result<TurnoverReportVO> turnoverStatistics(String begin, String end) {
        TurnoverReportVO turnoverReportVO = new TurnoverReportVO();
        List<LocalDate> dateList = getDateList(begin, end);
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("sum(amount)")
                    .between("order_time", beginTime, endTime)
                    .eq("status", 5);
            Object orderSum = orderMapper.selectObjs(queryWrapper).get(0);
            Double orderSumData = orderSum == null ? 0.0 : Double.parseDouble(orderSum.toString());
            turnoverList.add(orderSumData);
        }
        turnoverReportVO.setDateList(StringUtils.join(dateList, ","));
        turnoverReportVO.setTurnoverList(StringUtils.join(turnoverList, ","));
        return Result.success(turnoverReportVO);
    }

    /**
     * 用户统计
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public Result<UserReportVO> userStatistics(String begin, String end) {
        List<LocalDate> dateList = getDateList(begin, end);
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Integer newUser = Math.toIntExact(
                    userMapper.selectCount(new QueryWrapper<User>()
                            .between("create_time", beginTime, endTime)
                    )
            );
            newUserList.add(newUser);
            Integer totalUser = Math.toIntExact(
                    userMapper.selectCount(new QueryWrapper<User>()
                            .le("create_time", endTime)
                    )
            );
            totalUserList.add(totalUser);
        }
        return Result.success(UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build());
    }


    /**
     * 订单统计
     *
     * @param begin
     * @param end
     * @return
     */

    @Override
    public Result<OrderReportVO> ordersStatistics(String begin, String end) {
        List<LocalDate> dateList = getDateList(begin, end);
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            orderCountList.add(Math.toIntExact(
                    orderMapper.selectCount(new QueryWrapper<Orders>()
                            .between("order_time", beginTime, endTime)
                    )
            ));
            validOrderCountList.add(Math.toIntExact(
                    orderMapper.selectCount(new QueryWrapper<Orders>()
                            .between("order_time", beginTime, endTime)
                            .eq("status", 5)
                    )
            ));
        }
        // 使用stream流计算总数
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();
        Double orderCompletionRate = 0.0; // 避免分母为0
        if (totalOrderCount != 0) {
            orderCompletionRate = validOrderCount / (double) totalOrderCount;
        }
        return Result.success(OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build());
    }

    /**
     * 销量排名统计
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public Result<SalesTop10ReportVO> salesTop10(String begin, String end) {
        MPJLambdaWrapper<OrderDetail> wrapper = new MPJLambdaWrapper<OrderDetail>()
                .select(OrderDetail::getName) // 选择 order_detail 表的 name 字段
                .selectSum(OrderDetail::getNumber) // 对 number 字段求和
                .leftJoin(Orders.class, Orders::getId, OrderDetail::getOrderId) // 左连接 orders 表
                .eq(Orders::getStatus, 5)
                .between(Orders::getOrderTime, begin, end)
                .groupBy(OrderDetail::getName);
        List<OrderDetail> orderDetailList = orderDetailMapper.selectJoinList(OrderDetail.class, wrapper);
        List<String> nameList = new ArrayList<>();
        List<Integer> numberList = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetailList) {
            nameList.add(orderDetail.getName());
            numberList.add(orderDetail.getNumber());
        }
        return Result.success(SalesTop10ReportVO.builder()
                .numberList(StringUtils.join(numberList, ","))
                .nameList(StringUtils.join(nameList, ","))
                .build());
    }

    private List<LocalDate> getDateList(String begin, String end) {
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate beginDate = LocalDate.parse(begin);
        LocalDate endDate = LocalDate.parse(end);
        while (!beginDate.equals(endDate)) {
            log.info(String.valueOf(beginDate));
            log.info(String.valueOf(endDate));
            dateList.add(beginDate);
            beginDate = beginDate.plusDays(1);
        }
        dateList.add(endDate);
        return dateList;
    }
}
