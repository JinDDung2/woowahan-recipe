package com.woowahan.recipe.aop;

import com.woowahan.recipe.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
public class AspectOrder {

    @Component
    @Aspect
    @Order
    public static class LogAspect {
        @Around("com.woowahan.recipe.aop.PointCuts.all()")
        public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
            long startTime = System.currentTimeMillis();
            try {
                return joinPoint.proceed();
            } catch (AppException e) {
                e.getStackTrace();
                return joinPoint.proceed();
            }finally {
                long endTime = System.currentTimeMillis();
            }
        }
    }

}
