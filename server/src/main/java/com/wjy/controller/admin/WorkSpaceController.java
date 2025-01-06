package com.wjy.controller.admin;

import com.wjy.result.Result;
import com.wjy.service.WorkspaceService;
import com.wjy.vo.BusinessDataVO;
import com.wjy.vo.DishOverViewVO;
import com.wjy.vo.OrderOverViewVO;
import com.wjy.vo.SetmealOverViewVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/admin/workspace")
@Slf4j
@Tag(name = "工作台相关接口")
public class WorkSpaceController {
    @Autowired
    private WorkspaceService workspaceService;

    /**
     * 工作台今日数据查询
     *
     * @return
     */
    @GetMapping("/businessData")
    @Operation(summary = "工作台今日数据查询")
    public Result<BusinessDataVO> businessData() {
        //获得当天的开始时间
        LocalDateTime begin = LocalDateTime.now().with(LocalTime.MIN);
        //获得当天的结束时间
        LocalDateTime end = LocalDateTime.now().with(LocalTime.MAX);

        BusinessDataVO businessDataVO = workspaceService.getBusinessData(begin, end);
        return Result.success(businessDataVO);
    }

    /**
     * 查询订单管理数据
     *
     * @return
     */
    @GetMapping("/overviewOrders")
    @Operation(summary = "查询订单管理数据")
    public Result<OrderOverViewVO> orderOverView() {
        return Result.success(workspaceService.getOrderOverView());
    }

    /**
     * 查询菜品总览
     *
     * @return
     */
    @GetMapping("/overviewDishes")
    @Operation(summary = "查询菜品总览")
    public Result<DishOverViewVO> dishOverView() {
        return Result.success(workspaceService.getDishOverView());
    }

    /**
     * 查询套餐总览
     *
     * @return
     */
    @GetMapping("/overviewSetmeals")
    @Operation(summary = "查询套餐总览")
    public Result<SetmealOverViewVO> setmealOverView() {
        return Result.success(workspaceService.getSetmealOverView());
    }
}