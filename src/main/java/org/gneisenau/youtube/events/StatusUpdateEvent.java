package org.gneisenau.youtube.events;

import org.gneisenau.youtube.model.State;
import org.springframework.context.ApplicationEvent;

public class StatusUpdateEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7091503442040706905L;

	private int percentage;
	private State state;
	private long id;

	public StatusUpdateEvent(long videoId, State state, int percentage, Object source) {
		super(source);
		this.percentage = percentage;
		this.state = state;
		this.id = videoId;
	}

	public int getPercentage() {
		return percentage;
	}

	public State getState() {
		return state;
	}

	public long getId() {
		return id;
	}

	@Override
	public String toString() {
		return "StatusUpdateEvent [percentage=" + percentage + ", state=" + state + ", id=" + id + "]";
	}


}
