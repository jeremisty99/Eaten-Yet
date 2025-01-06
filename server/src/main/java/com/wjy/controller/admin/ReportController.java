package com.wjy.controller.admin;

import com.wjy.result.Result;
import com.wjy.service.ReportService;
import com.wjy.vo.OrderReportVO;
import com.wjy.vo.SalesTop10ReportVO;
import com.wjy.vo.TurnoverReportVO;
import com.wjy.vo.UserReportVO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/admin/report")
@Slf4j
public class ReportController {
    @Autowired
    private ReportService reportService;

    @GetMapping("/turnoverStatistics")
    @Operation(summary = "营业额统计")
    public Result<TurnoverReportVO> turnoverStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") String begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") String end
    ) {
        return reportService.turnoverStatistics(begin, end);
    }

    @GetMapping("/userStatistics")
    @Operation(summary = "用户统计")
    public Result<UserReportVO> userStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") String begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") String end
    ) {
        return reportService.userStatistics(begin, end);
    }

    @GetMapping("/ordersStatistics")
    @Operation(summary = "订单统计")
    public Result<OrderReportVO> ordersStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") String begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") String end
    ) {
        return reportService.ordersStatistics(begin, end);
    }

    @GetMapping("/top10")
    @Operation(summary = "销量排名统计")
    public Result<SalesTop10ReportVO> salesTop10(
            @DateTimeFormat(pattern = "yyyy-MM-dd") String begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") String end
    ) {
        return reportService.salesTop10(begin, end);
    }

    /**
     * 导出运营数据报表
     * @param response
     */
    @GetMapping("/export")
    @Operation(summary = "导出运营数据报表")
    public void export(HttpServletResponse response){
        reportService.export(response);
    }
}
