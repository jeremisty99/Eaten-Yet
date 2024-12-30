package com.wjy.handler;

import com.wjy.constant.MessageConstant;
import com.wjy.exception.BaseException;
import com.wjy.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.swing.*;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex) {
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 捕获SQL异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        String message = ex.getMessage();
        if (message.contains("Duplicate entry")) { //重复插入
            // Duplicate entry 'admin' for key 'employee.idx_username'
            String patternString = "Duplicate entry '(.*?)' for key '(.*?)'";
            Pattern pattern = Pattern.compile(patternString);
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                String value = matcher.group(1);
                String key = matcher.group(2);
                return Result.error(String.format("Key: %s, Value: %s, %s", key, value, MessageConstant.ALREADY_EXISTS));
            } else {
                return Result.error(MessageConstant.UNKNOWN_ERROR);
            }
        } else {
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    }
}
