package org.gneisenau.youtube.events;

import org.springframework.context.ApplicationEvent;

public class ErrorEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7091503442040706905L;

	private long videoId;
	private String text;

	public ErrorEvent(String text, Object source) {
		super(source);
		this.videoId = videoId;
	}

	public long getVideoId() {
		return videoId;
	}

	public String getText() {
		return text;
	}

}
