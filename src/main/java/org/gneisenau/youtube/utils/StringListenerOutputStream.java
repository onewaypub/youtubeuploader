package org.gneisenau.youtube.utils;

import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

public class StringListenerOutputStream extends ByteArrayOutputStream {

	private final ApplicationEventPublisher publisher;
	private Pattern p;
	private StringBuffer substr;

	public StringListenerOutputStream(ApplicationEventPublisher publisher, String regex) {
		this.publisher = publisher;
		p = Pattern.compile(regex);

	}

	public void publishEvent(StringFoundEvent event) {
		this.publisher.publishEvent(event);
	}

	public void write(int b) {
		findPattern(b);
		super.write(b);
	}

	private synchronized void findPattern(int b) {
		substr.append(b);
		if ((char) b == '\n') {
			Matcher m = p.matcher(substr);
			if (m.find()) {
				String str = m.group(0);
				StringFoundEvent event = new StringFoundEvent(this, str);
				publishEvent(event);
				substr = new StringBuffer();
			}
		}
	}

	@Override
	public synchronized void write(byte[] b, int off, int len) {
		for (byte i : b) {
			findPattern(i);
		}
		super.write(b, off, len);
	}

	public class StringFoundEvent extends ApplicationEvent {

		private static final long serialVersionUID = 8239452800721337648L;
		private String matched;

		public StringFoundEvent(Object source, String matchedStr) {
			super(source);
			matched = matchedStr;
		}

		public String getMatched() {
			return matched;
		}

	}

}
