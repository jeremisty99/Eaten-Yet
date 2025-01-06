package com.wjy.service;

import com.wjy.result.Result;
import com.wjy.vo.OrderReportVO;
import com.wjy.vo.SalesTop10ReportVO;
import com.wjy.vo.TurnoverReportVO;
import com.wjy.vo.UserReportVO;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface ReportService {
    /**
     * 营业额统计
     *
     * @param begin
     * @param end
     * @return
     */
    Result<TurnoverReportVO> turnoverStatistics(String begin, String end);

    /**
     * 用户统计
     *
     * @param begin
     * @param end
     * @return
     */
    Result<UserReportVO> userStatistics(String begin, String end);

    /**
     * 订单统计
     *
     * @param begin
     * @param end
     * @return
     */
    Result<OrderReportVO> ordersStatistics(String begin, String end);

    /**
     * 销售top10
     * @param begin
     * @param end
     * @return
     */
    Result<SalesTop10ReportVO> salesTop10(String begin, String end);

    /**
     * 导出数据报表
     * @param response
     */
    void export(HttpServletResponse response);
}
