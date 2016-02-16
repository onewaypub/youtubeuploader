package org.gneisenau.youtube.events;

import org.gneisenau.youtube.to.VideoTO;
import org.springframework.context.ApplicationEvent;

public class YoutubeUpdateEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7091503442040706905L;

	private VideoTO o;

	public YoutubeUpdateEvent(VideoTO v, Object source) {
		super(source);
		this.o = v;
	}

	public VideoTO getO() {
		return o;
	}

}
