package com.wjy.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wjy.constant.MessageConstant;
import com.wjy.context.BaseContext;
import com.wjy.dto.*;
import com.wjy.entity.*;
import com.wjy.exception.AddressBookBusinessException;
import com.wjy.exception.OrderBusinessException;
import com.wjy.exception.ShoppingCartBusinessException;
import com.wjy.mapper.AddressBookMapper;
import com.wjy.mapper.OrderDetailMapper;
import com.wjy.mapper.OrderMapper;
import com.wjy.mapper.ShoppingCartMapper;
import com.wjy.result.PageResult;
import com.wjy.service.OrderService;
import com.wjy.vo.*;
import com.wjy.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private WebSocketServer webSocketServer;

    /**
     * 用户下单
     *
     * @param ordersSubmitDTO
     * @return
     */
    @Transactional
    @Override
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        // 处理业务异常（地址为空、购物车为空）
        AddressBook addressBook = addressBookMapper.selectById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectList(
                new QueryWrapper<ShoppingCart>().eq("user_id", BaseContext.getCurrentId())
        );
        if (shoppingCarts == null || shoppingCarts.isEmpty()) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        // 向订单表插入一条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(addressBook.getDetail());
        orders.setConsignee(addressBook.getConsignee());
        orders.setUserId(BaseContext.getCurrentId());
        orderMapper.insert(orders);
        // 向订单明细表插入多条数据
        Long id = orders.getId();
        ArrayList<OrderDetail> orderDetails = new ArrayList<>();
        for (ShoppingCart cart : shoppingCarts) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(id);
            orderDetails.add(orderDetail);
        }
        orderDetailMapper.insert(orderDetails);
        // 清空购物车表数据
        shoppingCartMapper.delete(
                new QueryWrapper<ShoppingCart>().eq("user_id", BaseContext.getCurrentId())
        );
        // 封装返回结果
        return OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
//        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );

//        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
//            throw new OrderBusinessException("该订单已支付");
//        }

//        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
//        vo.setPackageStr(jsonObject.getString("package"));
        // 跳过微信支付逻辑
        OrderPaymentVO vo = OrderPaymentVO.builder()
                .nonceStr("")
                .paySign("")
                .packageStr("")
                .signType("")
                .timeStamp("")
                .build();
        paySuccess(ordersPaymentDTO.getOrderNumber());
        return vo;
    }

    /**
     * 用户历史订单分页查询
     *
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    @Override
    public PageResult pageQuery4User(int page, int pageSize, Integer status) {
        // 分页查Orders 按下单时间倒序
        IPage<Orders> ordersPage = new Page<>(page, pageSize);
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        if (status != null)
            queryWrapper.eq("status", status);
        queryWrapper.eq("user_id", BaseContext.getCurrentId()).orderByDesc("order_time");
        orderMapper.selectPage(ordersPage, queryWrapper);
        List<OrderVO> list = new ArrayList<OrderVO>();
        // 遍历Orders 补全 orderDetail 封装进OrderVO
        if (ordersPage.getTotal() > 0) {
            for (Orders orders : ordersPage.getRecords()) {
                List<OrderDetail> orderDetailList = orderDetailMapper.selectList(
                        new QueryWrapper<OrderDetail>().eq("order_id", orders.getId())
                );
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(orderDetailList);
                list.add(orderVO);
            }
        }
        return new PageResult(ordersPage.getTotal(), list);
    }

    /**
     * 管理端分页查询
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery4Admin(OrdersPageQueryDTO ordersPageQueryDTO) {
        IPage<Orders> ordersPage = new Page<>(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        if (ordersPageQueryDTO.getUserId() != null)
            queryWrapper.eq("user_id", BaseContext.getCurrentId());
        if (ordersPageQueryDTO.getStatus() != null)
            queryWrapper.eq("status", ordersPageQueryDTO.getStatus());
        if (ordersPageQueryDTO.getPhone() != null) // 模糊查询
            queryWrapper.like("phone", ordersPageQueryDTO.getPhone());
        if (ordersPageQueryDTO.getNumber() != null)
            queryWrapper.eq("number", ordersPageQueryDTO.getNumber());
        if (ordersPageQueryDTO.getBeginTime() != null)
            queryWrapper.ge("order_time", ordersPageQueryDTO.getBeginTime());
        if (ordersPageQueryDTO.getEndTime() != null)
            queryWrapper.le("order_time", ordersPageQueryDTO.getEndTime());
        queryWrapper.orderByDesc("order_time");
        orderMapper.selectPage(ordersPage, queryWrapper);
        List<OrderVO> list = new ArrayList<OrderVO>();
        // 遍历Orders 补全 orderDetail 封装进OrderVO
        if (ordersPage.getTotal() > 0) {
            for (Orders orders : ordersPage.getRecords()) {
                // 查询订单菜品详情信息（订单中的菜品和数量）
                List<OrderDetail> orderDetailList = orderDetailMapper.selectList(
                        new QueryWrapper<OrderDetail>().eq("order_id", orders.getId())
                );
                // 将每一项订单菜品信息拼接为字符串（格式：宫保鸡丁*3；）
                StringBuilder orderDishes = new StringBuilder();
                for (OrderDetail orderDetail : orderDetailList) {
                    orderDishes.append(orderDetail.getName()).append("*").append(orderDetail.getNumber()).append(";");
                }
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDishes(String.valueOf(orderDishes));
                orderVO.setOrderDetailList(orderDetailList);
                list.add(orderVO);
            }
        }
        log.info("订单分页查询：{}", list);
        return new PageResult(ordersPage.getTotal(), list);
    }

    /**
     * 管理端订单数量统计
     *
     * @return
     */
    @Override
    public OrderStatisticsVO getStatistics() {
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO
                .setToBeConfirmed(Math.toIntExact(orderMapper.selectCount(new QueryWrapper<Orders>().eq("status", (Orders.TO_BE_CONFIRMED)))));
        orderStatisticsVO
                .setConfirmed(Math.toIntExact(orderMapper.selectCount(new QueryWrapper<Orders>().eq("status", (Orders.CONFIRMED)))));
        orderStatisticsVO
                .setDeliveryInProgress(Math.toIntExact(orderMapper.selectCount(new QueryWrapper<Orders>().eq("status", (Orders.DELIVERY_IN_PROGRESS)))));
        return orderStatisticsVO;
    }

    /**
     * 接单
     *
     * @param ordersConfirmDTO
     */
    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = Orders.builder()
                .id(ordersConfirmDTO.getId())
                .status(Orders.CONFIRMED)
                .build();
        orderMapper.updateById(orders);
    }

    /**
     * 拒单
     *
     * @param ordersRejectionDTO
     */
    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        // 订单只有存在且状态为2（待接单）才可以拒单
        Orders orders = orderMapper.selectById(ordersRejectionDTO.getId());
        if (orders == null || !orders.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        // 拒单时，如果用户已经完成了支付，需要为用户退款
        if (orders.getPayStatus().equals(Orders.PAID)) {
            // 假装完成了退款
        }
        // 更新订单状态、取消原因、取消时间
        orders.setStatus(Orders.CANCELLED);
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.updateById(orders);
    }

    /**
     * 取消订单
     *
     * @param ordersCancelDTO
     */
    @Override
    public void cancel(OrdersCancelDTO ordersCancelDTO) {
        Orders orders = orderMapper.selectById(ordersCancelDTO.getId());
        if (orders.getPayStatus().equals(Orders.PAID)) {
            // 假装完成了退款
        }
        // 更新订单状态、取消原因、取消时间
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.updateById(orders);
    }

    /**
     * 派送订单
     *
     * @param id
     */
    @Override
    public void delivery(Long id) {
        Orders orders = orderMapper.selectById(id);
        if (orders == null || !orders.getStatus().equals(Orders.CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
        orderMapper.updateById(orders);
    }

    /**
     * 完成订单
     *
     * @param id
     */
    @Override
    public void complete(Long id) {
        Orders orders = orderMapper.selectById(id);
        if (orders == null || !orders.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        orders.setStatus(Orders.COMPLETED);
        orderMapper.updateById(orders);
    }

    /**
     * 催单
     *
     * @param id
     */
    @Override
    public void reminder(Long id) {
        Orders orders = orderMapper.selectById(id);
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Map map = new HashMap();
        map.put("type", 2); // 来单提醒
        map.put("orderId", orders.getId());
        map.put("content", "订单号：" + orders.getNumber());
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
    }

    /**
     * 订单详情
     *
     * @param id
     * @return
     */
    @Override
    public OrderVO details(Long id) {
        Orders orders = orderMapper.selectById(id);
        List<OrderDetail> orderDetails = orderDetailMapper.selectList(new QueryWrapper<OrderDetail>().eq("order_id", id));
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetails);
        return orderVO;
    }

    /**
     * 用户取消订单
     *
     * @param id
     */
    @Override
    public void userCancelById(Long id) {
        // 先查询订单状态
        Orders order = orderMapper.selectById(id);
        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        // 如果是 3已接单 4派送中 5已完成 6已取消 不能直接取消
        if (order.getStatus() > 2) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        // 待接单状态下取消 需要退款
        if (order.getStatus() == 1) {
            // 假装已经退款
            order.setPayStatus(Orders.REFUND);
        }
        order.setStatus(Orders.CANCELLED);
        order.setCancelReason("用户取消");
        order.setCancelTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    /**
     * 再来一单
     *
     * @param id
     */
    @Override
    public void repetition(Long id) {
        // 将本单内容重新插入到购物车
        // 获取当前用户id
        Long userId = BaseContext.getCurrentId();
        // 查询订单详情
        List<OrderDetail> orderDetailList = orderDetailMapper.selectList(new QueryWrapper<OrderDetail>().eq("order_id", id));
        // 转换成购物车对象
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map(orderDetail -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());
            BeanUtils.copyProperties(orderDetail, shoppingCart, "id"); // 忽略id属性
            return shoppingCart;
        }).collect(Collectors.toList());
        /*
            .stream() 将 orderDetailList 转换为流。
            .map(...)：对每个 OrderDetail 对象应用 lambda 表达式，创建并配置新的 ShoppingCart 对象。
            .collect(Collectors.toList())：将流中的元素收集到一个新的 List<ShoppingCart> 中。
         */
        shoppingCartMapper.insert(shoppingCartList);
    }


    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.selectOne(new QueryWrapper<Orders>().eq("number", outTradeNo));

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.updateById(orders);
        // 发送消息，通知下单成功
        Map map = new HashMap();
        map.put("type", 1); // 来单提醒
        map.put("orderId", orders.getId());
        map.put("content", "订单号：" + outTradeNo);
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
    }
}
