package com.wjy.service;

import com.wjy.dto.*;
import com.wjy.result.PageResult;
import com.wjy.vo.OrderPaymentVO;
import com.wjy.vo.OrderStatisticsVO;
import com.wjy.vo.OrderSubmitVO;
import com.wjy.vo.OrderVO;

public interface OrderService {
    /**
     * 用户下单
     *
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO);

    /**
     * 历史订单分页查询
     *
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    PageResult pageQuery4User(int page, int pageSize, Integer status);

    /**
     * 订单详情
     *
     * @param id
     * @return
     */
    OrderVO details(Long id);

    /**
     * 用户取消订单
     *
     * @param id
     */
    void userCancelById(Long id);

    /**
     * 再来一单
     *
     * @param id
     */
    void repetition(Long id);

    /**
     * 管理端订单分页查询
     *
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult pageQuery4Admin(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 管理端订单数量统计
     *
     * @return
     */
    OrderStatisticsVO getStatistics();

    /**
     * 接单
     *
     * @param ordersConfirmDTO
     */
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 拒单
     *
     * @param ordersRejectionDTO
     */
    void rejection(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 取消订单
     *
     * @param ordersCancelDTO
     */
    void cancel(OrdersCancelDTO ordersCancelDTO);

    /**
     * 派送订单
     *
     * @param id
     */
    void delivery(Long id);

    /**
     * 完成订单
     *
     * @param id
     */
    void complete(Long id);
}
