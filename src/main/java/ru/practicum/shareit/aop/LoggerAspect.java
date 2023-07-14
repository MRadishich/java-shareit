package ru.practicum.shareit.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggerAspect {

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *) " +
            "|| within(@org.springframework.stereotype.Service *)")
    public void pointcut() {
    }

    @Before("pointcut()")
    public void logMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Object className = signature.getMethod().getDeclaringClass();
        String methodName = signature.getName();
        Object[] arguments = joinPoint.getArgs();

        log.info("Class: {}, methodName: {}, arguments: {} ", className, methodName, arguments);
    }
}
