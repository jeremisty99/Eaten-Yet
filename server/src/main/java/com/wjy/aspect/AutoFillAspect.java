package com.wjy.aspect;


import com.wjy.annotation.AutoFill;
import com.wjy.constant.AutoFillConstant;
import com.wjy.context.BaseContext;
import com.wjy.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面，实现公共字段自动填充处理逻辑
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    // 定义切入点
    @Pointcut("execution(* com.wjy.mapper.*.*(..)) && @annotation(com.wjy.annotation.AutoFill)")
//    @Pointcut("execution(* com.wjy.mapper.*.*(..))")
    public void autoFillPointCut() {

    }


    /**
     * 前置通知，在通知中进行公共字段的赋值
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {
        log.info("开始进行公共字段的自动填充");
        // 获取到当前被截的方法上的数据库操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            // 获取到拦截方法对应的注解对象
            AutoFill annotation = signature.getMethod().getAnnotation(AutoFill.class);
            // 获取注解对象中的操作类型
            OperationType typeValue = annotation.value();

            // 获取当前被拦截方法参数列表 并约定第一个参数为实体对象
            Object args[] = joinPoint.getArgs();

            if (args == null || args.length == 0) {
                return;
            }
            // 获取到实体对象
            Object entity = args[0];
            Long currentID = BaseContext.getCurrentId();
            // 根据当前数据库操作类型来决定如何处理公共字段
            switch (typeValue) {
                case INSERT:
                    try {
                        // 获取实体对象的相应属性方法
                        Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                        Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                        Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                        Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                        // 通过反射为对象赋值
                        setCreateUser.invoke(entity, currentID);
                        setUpdateUser.invoke(entity, currentID);
                        setCreateTime.invoke(entity, LocalDateTime.now());
                        setUpdateTime.invoke(entity, LocalDateTime.now());
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case UPDATE:
                    try {
                        Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                        Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                        setUpdateUser.invoke(entity, currentID);
                        setUpdateTime.invoke(entity, LocalDateTime.now());
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                    break;
            }
        }
}
