package org.gneisenau.youtube.video;

import org.springframework.context.ApplicationEvent;

public class CurrentProcessingTimeEvent extends ApplicationEvent {

	private static final long serialVersionUID = 8239452800721337648L;
	private String matched;

	public CurrentProcessingTimeEvent(Object source, String matchedStr) {
		super(source);
		matched = matchedStr;
	}

	public String getMatched() {
		return matched;
	}

}
