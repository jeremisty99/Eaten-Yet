package com.wjy.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wjy.entity.Orders;
import com.wjy.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;

    /*
     * 每分钟执行一次定时任务
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeOutOrder() {
        log.info("定时处理超时订单,{}", LocalDateTime.now());
        List<Orders> orders = orderMapper.selectList(
                new QueryWrapper<Orders>()
                        .eq("status", Orders.PENDING_PAYMENT)
                        .le("order_time", LocalDateTime.now().minusMinutes(15)));
        if (orders != null && !orders.isEmpty()) {
            for (Orders order : orders) {
                order.setStatus(Orders.CANCELLED);
                order.setCancelReason("订单超时，自动取消");
                order.setCancelTime(LocalDateTime.now());
                orderMapper.updateById(order);
            }
        }
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder() {
        log.info("定时处理派送中订单,{}", LocalDateTime.now());
        List<Orders> orders = orderMapper.selectList(
                new QueryWrapper<Orders>()
                        .eq("status", Orders.DELIVERY_IN_PROGRESS)
                        .le("order_time", LocalDateTime.now().minusMinutes(60)));
        // 查出上一个工作日所有派送中的订单
        if (orders != null && !orders.isEmpty()) {
            for (Orders order : orders) {
                order.setStatus(Orders.COMPLETED);
                orderMapper.updateById(order);
            }
        }
    }
}
