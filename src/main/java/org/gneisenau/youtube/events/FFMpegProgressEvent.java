package org.gneisenau.youtube.events;

import org.springframework.context.ApplicationEvent;

public class FFMpegProgressEvent extends ApplicationEvent{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2437287377674102448L;

	public FFMpegProgressEvent(Object source) {
		super(source);
		// TODO Auto-generated constructor stub
	}

}
