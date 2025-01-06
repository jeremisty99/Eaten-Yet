package com.wjy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wjy.constant.StatusConstant;
import com.wjy.entity.Dish;
import com.wjy.entity.Orders;
import com.wjy.entity.Setmeal;
import com.wjy.entity.User;
import com.wjy.mapper.DishMapper;
import com.wjy.mapper.OrderMapper;
import com.wjy.mapper.SetmealMapper;
import com.wjy.mapper.UserMapper;
import com.wjy.service.WorkspaceService;
import com.wjy.vo.BusinessDataVO;
import com.wjy.vo.DishOverViewVO;
import com.wjy.vo.OrderOverViewVO;
import com.wjy.vo.SetmealOverViewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class WorkspaceServiceImpl implements WorkspaceService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 根据时间段统计营业数据
     *
     * @param begin
     * @param end
     * @return
     */
    public BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end) {
        Integer totalOrderCount = Math.toIntExact(orderMapper.selectCount(new QueryWrapper<Orders>()
                .between("order_time", begin, end)
        ));
        Integer validOrderCount = Math.toIntExact(orderMapper.selectCount(new QueryWrapper<Orders>()
                .between("order_time", begin, end)
                .eq("status", 5)
        ));
        Object orderSum = orderMapper.selectObjs(new QueryWrapper<Orders>()
                .between("order_time", begin, end)
                .eq("status", 5)
                .select("sum(amount)")
        ).get(0);
        Double turnover = orderSum == null ? 0.0 : Double.parseDouble(orderSum.toString());
        Integer newUsers = Math.toIntExact(userMapper.selectCount(new QueryWrapper<User>()
                .between("create_time", begin, end)
        ));
        Double unitPrice = 0.0;
        Double orderCompletionRate = 0.0; // 避免分母为0
        if (totalOrderCount != 0) {
            orderCompletionRate = validOrderCount / (double) totalOrderCount;
            unitPrice = turnover / (double) validOrderCount;
        }
        return BusinessDataVO.builder()
                .turnover(turnover)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .newUsers(newUsers)
                .build();
    }

    /**
     * 查询订单管理数据
     *
     * @return
     */
    public OrderOverViewVO getOrderOverView() {
        //待接单
        Integer waitingOrders = Math.toIntExact(orderMapper.selectCount(new QueryWrapper<Orders>()
                        .ge("order_time", LocalDateTime.now().with(LocalTime.MIN))
                        .eq("status", Orders.TO_BE_CONFIRMED)
                )
        );
        //待派送
        Integer deliveredOrders =
                Math.toIntExact(orderMapper.selectCount(new QueryWrapper<Orders>()
                                .ge("order_time", LocalDateTime.now().with(LocalTime.MIN))
                                .eq("status", Orders.DELIVERY_IN_PROGRESS)
                        )
                );
        //已完成
        Integer completedOrders = Math.toIntExact(orderMapper.selectCount(new QueryWrapper<Orders>()
                        .ge("order_time", LocalDateTime.now().with(LocalTime.MIN))
                        .eq("status", Orders.COMPLETED)
                )
        );
        //已取消
        Integer cancelledOrders = Math.toIntExact(orderMapper.selectCount(new QueryWrapper<Orders>()
                        .ge("order_time", LocalDateTime.now().with(LocalTime.MIN))
                        .eq("status", Orders.CANCELLED)
                )
        );
        //全部订单
        Integer allOrders = Math.toIntExact(orderMapper.selectCount(new QueryWrapper<Orders>()
                        .ge("order_time", LocalDateTime.now().with(LocalTime.MIN))
                )
        );
        return OrderOverViewVO.builder()
                .waitingOrders(waitingOrders)
                .deliveredOrders(deliveredOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .allOrders(allOrders)
                .build();
    }

    /**
     * 查询菜品总览
     *
     * @return
     */
    public DishOverViewVO getDishOverView() {
        Integer sold = Math.toIntExact(dishMapper.selectCount(new QueryWrapper<Dish>()
                .eq("status", StatusConstant.ENABLE)
        ));

        Integer discontinued = Math.toIntExact(dishMapper.selectCount(new QueryWrapper<Dish>()
                .eq("status", StatusConstant.DISABLE)
        ));
        return DishOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }

    /**
     * 查询套餐总览
     *
     * @return
     */
    public SetmealOverViewVO getSetmealOverView() {
        Integer sold = Math.toIntExact(setmealMapper.selectCount(new QueryWrapper<Setmeal>()
                .eq("status", StatusConstant.ENABLE)
        ));

        Integer discontinued = Math.toIntExact(setmealMapper.selectCount(new QueryWrapper<Setmeal>()
                .eq("status", StatusConstant.DISABLE)
        ));
        return SetmealOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }

}
