package org.gneisenau.youtube.to;

import org.gneisenau.youtube.events.StatusUpdateEvent;

public class StatusEventTO extends EventTO {
	private String status;
	private int percent;

	public StatusEventTO(Object o, StatusUpdateEvent event) {
		super(o, event);
		this.status = event.getState().getDisplayName();
		this.percent = event.getPercentage();
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getPercent() {
		return percent;
	}

	public void setPercent(int percent) {
		this.percent = percent;
	}

}
