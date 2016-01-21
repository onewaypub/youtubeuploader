package org.gneisenau.youtube.handler.video;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.gneisenau.youtube.events.StatusUpdateEvent;
import org.gneisenau.youtube.model.State;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Output Stream which scans the stream for the keywords time and duration. If it find them the next 11 chars will be used to parse a hh:mm:ss.SS timestamp.
 * The timestamp of the duration and the current position will be used to generate a precentage of the video processing
 *
 */
public class ProgressAwareFFmpegOutputfilterStream extends ByteArrayOutputStream {

	private final ApplicationEventPublisher publisher;
	private char[] timePrefix = { 't', 'i', 'm', 'e' };
	private char[] durationPrefix = { 'D', 'u', 'r', 'a', 't', 'i', 'o', 'n', ':' };
	private StringMatcher timeMatcher = new StringMatcher(timePrefix, 11);
	private StringMatcher durationMatcher = new StringMatcher(durationPrefix, 11);
	private long durationMilliseconds = 0;
	private boolean durationFound = false;
	private long id;
	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SS");
	private long startMilliseconds = 0;
	private int lastFiredPercentage = 0;

	public ProgressAwareFFmpegOutputfilterStream(ApplicationEventPublisher publisher, long videoId) {
		this.publisher = publisher;
		this.id = videoId;
		this.startMilliseconds = getMillisecondsFromString("00:00:00.00");
	}

	public void publishEvent(StatusUpdateEvent event) {
		if (lastFiredPercentage != event.getPercentage()) {
			this.publisher.publishEvent(event);
			lastFiredPercentage = event.getPercentage();
		}
	}

	@Override
	public synchronized void write(int b) {
		findAndCalcPercentage(b);
		super.write(b);
	}

	private void findAndCalcPercentage(int b) {
		String time = timeMatcher.searchAndFireEvent(b);
		if (durationFound && time != null) {
			long currentMilliseconds = getMillisecondsFromString(time);
			int percentage = getPercentageLeft(startMilliseconds, durationMilliseconds, currentMilliseconds);
			StatusUpdateEvent event = new StatusUpdateEvent(id, State.OnProcessing, percentage, this);
			publishEvent(event);
		}

		if (!durationFound) {
			String duration = durationMatcher.searchAndFireEvent(b);
			if (duration != null) {
				durationFound = true;
				durationMilliseconds = getMillisecondsFromString(duration);
			}
		}
	}

	private static int getPercentageLeft(long start, long end, long now) {
		if (start >= end) {
			return 0;
		}
		if (now >= end) {
			return 100;
		}
		if (now <= start) {
			return 100;
		}
		return (int) (100 - ((end - now) * 100 / (end - start)));
	}

	private long getMillisecondsFromString(String time) {
		try {
			java.util.Date parse = sdf.parse(time);
			if (parse != null) {
				return parse.getTime();
			}
		} catch (ParseException e) {
			System.out.println(e);
		}
		return 0;
	}

	@Override
	public synchronized void write(byte[] b, int off, int len) {
		for (byte c : b) {
			findAndCalcPercentage(c);
		}
		super.write(b, off, len);
	}

	public long getId() {
		return id;
	}

	private class StringMatcher {
		private char[] timePrefix;
		private int index = 0;
		private int foundIdx = 11;
		private boolean found = false;
		private StringBuffer buffer = new StringBuffer();

		public StringMatcher(char[] timePrefix, int suffixLength) {
			super();
			this.timePrefix = timePrefix;
			this.foundIdx = suffixLength;
		}

		public String searchAndFireEvent(int b) {
			if (!found) {
				if (index < timePrefix.length && timePrefix[index] == (char) b) {
					index++;
				} else if (index < timePrefix.length && timePrefix[index] != (char) b) {
					index = 0;
				} else if (index >= timePrefix.length) {
					found = true;
				}
			} else {
				if (foundIdx > 0) {
					if ((foundIdx % 3 == 0) && ((char) b) != ':' && ((char) b) != '.') {
						resetCounterAndBuffer();
					} else {
						buffer.append((char) b);
						foundIdx--;
					}
				} else if (foundIdx <= 0) {
					String result = buffer.toString();
					// Init counter and buffer
					resetCounterAndBuffer();
					return result;
				}
			}
			return null;
		}

		private void resetCounterAndBuffer() {
			buffer = new StringBuffer();
			found = false;
			foundIdx = 11;
			index = 0;
		}
	}

}
