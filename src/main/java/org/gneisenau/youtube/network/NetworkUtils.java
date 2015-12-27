package org.gneisenau.youtube.network;

import java.io.InputStream;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.stereotype.Service;

@Service
public class NetworkUtils {

	private Long currentUploadSpeed;

	@Around("execution(* org.gneisenau.youtube.handler.VideoHandler.upload (..))")
	public Object measureUpload(ProceedingJoinPoint joinPoint) throws Throwable {
		long startTime = System.currentTimeMillis();
		Object o = joinPoint.proceed();
		long absTime = System.currentTimeMillis() - startTime;

		Object[] args = joinPoint.getArgs();
		InputStream s = null;
		boolean found = false;
		for (Object obj : args) {
			if (obj instanceof InputStream) {
				s = (InputStream) obj;
				found = true;
			}
		}
		if (found) {
			long numBytesUploaded = Long.valueOf(s.available());

			// Seconds
			absTime = absTime / 1000;
			long numKiloBytesUploaded = numBytesUploaded / 1024;
			long kbPerSecond = numKiloBytesUploaded / absTime;

			if (currentUploadSpeed == 0) {
				currentUploadSpeed = kbPerSecond;
			} else {
				currentUploadSpeed = (currentUploadSpeed + kbPerSecond) / 2;
			}
		}
		return o;

	}

	public long estimatedSecondsForUpload(long kilobytes) {
		return kilobytes / currentUploadSpeed;
	}

	public long estimatedMinutesForUpload(long kilobytes) {
		return (kilobytes / currentUploadSpeed) / 60;
	}

}
