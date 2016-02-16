package org.gneisenau.youtube.events;

import org.springframework.context.ApplicationEvent;

public class VideoAddEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7091503442040706905L;

	private long videoId;

	public VideoAddEvent(long videoId, Object source) {
		super(source);
		this.videoId = videoId;
	}

	public long getVideoId() {
		return videoId;
	}

}
