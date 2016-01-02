package org.gneisenau.youtube.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggingAspect {

	// @After("execution(void aspects.TestAspect.hello())")
	// public void afterGetPanel(JoinPoint joinPoint) {
	// System.err.println("after " + joinPoint);
	// }
	// @Before("execution(void aspects.TestAspect.hello())")
	// public void beforeHello(JoinPoint joinPoint) {
	// System.err.println("before " + joinPoint);
	// }

	@Around("execution(* org.gneisenau.youtube..*(..))")
	public Object generalTraceLog(ProceedingJoinPoint joinPoint) throws Throwable {
		Logger localLog = LoggerFactory.getLogger(joinPoint.getClass());
		if (localLog.isTraceEnabled()) {
			Object[] args = joinPoint.getArgs();
			if (args !=  null && args.length > 0) {
				localLog.trace("", args);
			}
		}
		Object o = joinPoint.proceed();
		if (localLog.isTraceEnabled()) {
			if (o != null) {
				localLog.trace("", o);
			}
		}
		return o;
	}

	@Around("execution(* org.gneisenau.youtube..* (..))")
	public Object generalDebugLogVoid(ProceedingJoinPoint joinPoint) throws Throwable {
		Logger localLog = LoggerFactory.getLogger(joinPoint.getClass());
		if (localLog.isDebugEnabled()) {
			localLog.debug("Entering method " + joinPoint.getSignature() + " in class " + joinPoint.getClass());
		}
		Object o = joinPoint.proceed();
		if (localLog.isDebugEnabled()) {
			localLog.debug("Exiting method " + joinPoint.getSignature() + " in class " + joinPoint.getClass());
		}
		return o;
	}

	@Around("execution(* org.gneisenau.youtube..* (..))")
	public Object generalDebugLog(ProceedingJoinPoint joinPoint) throws Throwable {
		Logger localLog = LoggerFactory.getLogger(joinPoint.getClass());
		if (localLog.isDebugEnabled()) {
			localLog.debug("Entering method " + joinPoint.getSignature() + " in class " + joinPoint.getClass());
		}
		Object o = joinPoint.proceed();
		if (localLog.isDebugEnabled()) {
			localLog.debug("Exiting method " + joinPoint.getSignature() + " in class " + joinPoint.getClass());
		}
		return o;
	}

	@AfterThrowing(pointcut = "execution(* org.gneisenau.youtube..* (..))", throwing = "ex")
	public void errorInterceptor(JoinPoint joinPoint, Exception ex) {
		Logger localLog = LoggerFactory.getLogger(joinPoint.getClass());
		localLog.error("", ex);
	}

}
