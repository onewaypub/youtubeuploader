package org.gneisenau.youtube.network;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;

import java.io.ByteArrayInputStream;

import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.lang.reflect.SourceLocation;
import org.aspectj.runtime.internal.AroundClosure;

import junit.framework.TestCase;

public class NetworkUtilsTest extends TestCase {

	public void testEstimatedSecondsForUpload() throws Throwable {
		NetworkUtils utils = new NetworkUtils();
		MyProceedingJoinPoint p1 = new MyProceedingJoinPoint(4000);
		utils.measureUpload(p1);
		assertEquals(24576, utils.estimatedSecondsForUpload(188743680));
		MyProceedingJoinPoint p2 = new MyProceedingJoinPoint(8000);
		utils.measureUpload(p2);
		assertEquals(32768, utils.estimatedSecondsForUpload(188743680));		
	}

	public void testEstimatedMinutesForUpload() throws Throwable {
		NetworkUtils utils = new NetworkUtils();
		MyProceedingJoinPoint p1 = new MyProceedingJoinPoint(4000);
		utils.measureUpload(p1);
		assertEquals(24576/60, utils.estimatedMinutesForUpload(188743680));
		MyProceedingJoinPoint p2 = new MyProceedingJoinPoint(8000);
		utils.measureUpload(p2);
		assertEquals(32768/60, utils.estimatedMinutesForUpload(188743680));		
	}

	private class MyProceedingJoinPoint implements ProceedingJoinPoint {

		private long waitFor;
		
		
		public MyProceedingJoinPoint(long waitFor) {
			super();
			this.waitFor = waitFor;
		}

		@Override
		public String toShortString() {
			return null;
		}

		@Override
		public String toLongString() {
			return null;
		}

		@Override
		public Object getThis() {
			return null;
		}

		@Override
		public Object getTarget() {
			return null;
		}

		@Override
		public StaticPart getStaticPart() {
			return null;
		}

		@Override
		public SourceLocation getSourceLocation() {
			return null;
		}

		@Override
		public Signature getSignature() {
			return null;
		}

		@Override
		public String getKind() {
			return null;
		}

		@Override
		public Object[] getArgs() {
			Object[] oArray = new Object[1];
			oArray[0] = new ByteArrayInputStream(new byte[31457280]);
			return oArray;
		}

		@Override
		public void set$AroundClosure(AroundClosure arg0) {
		}

		@Override
		public Object proceed(Object[] arg0) throws Throwable {
			return null;
		}

		@Override
		public Object proceed() throws Throwable {
			Thread.sleep(waitFor);
			return null;
		}

	}

}
