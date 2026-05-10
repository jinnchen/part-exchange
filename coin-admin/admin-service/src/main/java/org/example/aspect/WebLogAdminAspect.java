package org.example.aspect;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.domain.SysUserLog;
import org.example.model.WebLog;
import org.example.service.SysUserLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Aspect
@Order(1)
@Slf4j
public class WebLogAdminAspect {

    @Autowired
    private SysUserLogService sysUserLogService;

    @Pointcut("execution(* org.example.controller.*.*(..))")
    public void webLog() {}

    @Around("webLog()")
    public Object recodeWebLog(ProceedingJoinPoint pjp) throws Throwable {
        Object result = null;
        WebLog webLog = new WebLog();
        long startTime = System.currentTimeMillis();

        result = pjp.proceed(pjp.getArgs());

        long endTime = System.currentTimeMillis();
        webLog.setSpendTime(String.valueOf((startTime - endTime) / 1000));

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        webLog.setUri(request.getRequestURI());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String url = request.getRequestURL().toString();
        webLog.setUrl(url);

        String regex = "(https?://[^:/]+(?::\\d+)?)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            webLog.setBasePath(matcher.group(1));
        }

        webLog.setUsername(authentication == null ? "anonymous" : authentication.getPrincipal().toString());
        webLog.setIp(request.getRemoteAddr());

        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String targetClassName = pjp.getTarget().getClass().getName();
        Method method = signature.getMethod();
        Operation annotation = method.getAnnotation(Operation.class);
        webLog.setDescription(annotation == null ? "no description" : annotation.description());
        webLog.setMethod(targetClassName + "." + method.getName());
        webLog.setParameter(getMethodParameter(method, pjp.getArgs()));
        webLog.setResult(result);

        SysUserLog  sysUserLog = new SysUserLog();
        sysUserLog.setId(IdWorker.getId());
        sysUserLog.setCreated(new Date());
        sysUserLog.setDescription(webLog.getDescription());
        sysUserLog.setGroup(webLog.getDescription());
        sysUserLog.setUserId(0L);
        sysUserLog.setMethod(webLog.getMethod());
        sysUserLog.setIp(webLog.getIp());
        sysUserLogService.save(sysUserLog);

        return result;
    }

    private String getMethodParameter(Method method, Object[] args) {
        Map<String, Object> methodParameterWithValues = new HashMap<>();
        DefaultParameterNameDiscoverer parameterNameDiscoverer =
                new DefaultParameterNameDiscoverer();
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
        for (int i = 0; i < parameterNames.length; i++) {
            methodParameterWithValues.put(parameterNames[i], args[i]);
        }
        return methodParameterWithValues.toString();
    }
}
