package com.xzj.stu.web.common.boot;

import com.alibaba.fastjson.JSONObject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author zhijunxie
 * @date 2019/09/25 15:48
 */
@Aspect
@Component
//@Order() //该值越小，则优先级越高
public class WebRequestLogAspect {
    private static Logger logger = LoggerFactory.getLogger(WebRequestLogAspect.class);

    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    @Pointcut("execution(public * com.xzj.stu.web.controller.*.*(..))")
    public void webLog() {
    }

    /**
     *
     *
     *
     */
    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        // 接收到请求，记录请求内容
        String beanName = proceedingJoinPoint.getSignature().getDeclaringTypeName();
        String methodName = proceedingJoinPoint.getSignature().getName();
        logger.debug("do Around before {}.{}", beanName, methodName);
        Object proceed = proceedingJoinPoint.proceed();
        logger.info("do Around after cost time={}, result={}", (System.currentTimeMillis()-threadLocal.get()), JSONObject.toJSONString(proceed));
        logger.debug("do Around after {}.{}", beanName, methodName);
        return proceed;
    }

    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) {
        try {
            threadLocal.set(System.currentTimeMillis());

            String beanName = joinPoint.getSignature().getDeclaringTypeName();
            String methodName = joinPoint.getSignature().getName();

            logger.debug("do Before {}.{}", beanName, methodName);
        } catch (Exception e) {
            logger.info("****Operation request logging failed doBefore()**** ", e);
        }
    }

    @After(value = "webLog()")
    public void doAfter() {
        logger.debug("do After");
    }

    @AfterReturning(returning = "result", pointcut = "webLog()")
    public void doAfterReturning(Object result) {
        // 处理完请求，返回内容
        logger.debug("do AfterReturning");
    }

    @AfterThrowing(pointcut = "webLog()")
    public void doAfterThrowing(){
        logger.debug("do AfterThrowing");
    }
}
