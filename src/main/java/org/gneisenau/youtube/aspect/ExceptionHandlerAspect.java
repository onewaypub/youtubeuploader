package org.gneisenau.youtube.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class ExceptionHandlerAspect {
	@AfterThrowing(pointcut = "execution(* org.gneisenau.youtube.scheduler..* (..))", throwing = "ex")
	public void errorInterceptor(JoinPoint joinPoint, Exception ex) {
		Logger localLog = LoggerFactory.getLogger(joinPoint.getClass());
		localLog.error("Error during runtime", ex);
	}

}
