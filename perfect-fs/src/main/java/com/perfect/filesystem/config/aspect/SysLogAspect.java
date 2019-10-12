package com.perfect.filesystem.config.aspect;

import com.alibaba.fastjson.JSON;
import com.perfect.filesystem.config.annotation.SysLog;
import com.perfect.filesystem.myfs.bean.pojo.SysLogPojo;
import com.perfect.filesystem.myfs.util.IPUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class SysLogAspect {

    @Pointcut("@annotation(com.perfect.filesystem.config.annotation.SysLog)")
    public void sysLogAspect() {}

    // @Before("webLog()")
    // public void doBefore(JoinPoint joinPoint) throws Throwable {
    // // 接收到请求，记录请求内容
    // ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    // HttpServletRequest request = attributes.getRequest();
    //
    // // 记录下请求内容
    // log.debug("======================日志开始================================");
    // log.debug("URL : " + request.getRequestURL().toString());
    // log.debug("HTTP_METHOD : " + request.getMethod());
    // log.debug("IP : " + request.getRemoteAddr());
    // log.debug("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." +
    // joinPoint.getSignature().getName());
    // log.debug("ARGS : " + Arrays.toString(joinPoint.getArgs()));
    // log.debug("======================日志结束================================");
    // }
    //
    // @AfterReturning(returning = "result", pointcut = "webLog()")
    // public void doAfterReturning(Object result) throws Throwable {
    // // 处理完请求，返回内容
    // log.debug("执行结果 : " + result);
    // }

    /**
     * 环绕通知 @Around ， 当然也可以使用 @Before (前置通知) @After (后置通知)
     * 
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("sysLogAspect()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        Object result = point.proceed();
        BigDecimal time = new BigDecimal(System.currentTimeMillis() - beginTime);
        try {
            SysLogPojo sysLogBO = printLog(point, time.longValue());

        } catch (Exception e) {
            log.error("发生异常");
            log.error(e.getMessage());

        }
        return result;
    }

    /**
     * 保存日志
     * 
     * @param joinPoint
     * @param time
     */
    private SysLogPojo printLog(ProceedingJoinPoint joinPoint, Long time) {

        // 获取request
        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Method method = signature.getMethod();
        SysLog sysLog = method.getAnnotation(SysLog.class);
        SysLogPojo sysLogBO = SysLogPojo.builder().class_name(joinPoint.getTarget().getClass().getName())
            .http_method(request.getMethod()).class_method(((MethodSignature)joinPoint.getSignature()).getName())
            .params(JSON.toJSONString(joinPoint.getArgs())).execTime(time).remark(sysLog.value())
            .createDate(LocalDateTime.now()).url(request.getRequestURL().toString()).ip(IPUtil.getIpAdd()).build();
        log.debug("======================日志开始================================");
        log.debug("日志名称         : " + sysLogBO.getRemark());
        log.debug("URL             : " + sysLogBO.getUrl());
        log.debug("HTTP方法         : " + sysLogBO.getHttp_method());
        log.debug("IP               : " + sysLogBO.getIp());
        log.debug("类名             : " + sysLogBO.getClass_name());
        log.debug("类方法           : " + sysLogBO.getClass_method());
        log.debug("执行时间         : " + new BigDecimal(sysLogBO.getExecTime()).divide(BigDecimal.valueOf(1000)).toString()
            + "秒");
        log.debug("执行日期         : " + sysLogBO.getCreateDate());
        log.debug("参数             : " + sysLogBO.getParams());
        log.debug("======================日志结束================================");
        return sysLogBO;
    }
}