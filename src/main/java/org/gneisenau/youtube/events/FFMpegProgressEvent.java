package org.gneisenau.youtube.events;

import org.springframework.context.ApplicationEvent;

public class FFMpegProgressEvent extends ApplicationEvent{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2437287377674102448L;
	private long id;

	public FFMpegProgressEvent(long id, Object source) {
		super(source);
		this.id = id;
	}

	public long getId() {
		return id;
	}
	
	

}
