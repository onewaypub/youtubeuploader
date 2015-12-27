package org.gneisenau.youtube.to;

import org.gneisenau.youtube.events.StatusUpdateEvent;

public class StatusEventTO {
	private long videoId;
	private String status;
	private int percent;

	public StatusEventTO(StatusUpdateEvent event) {
		this.videoId = event.getVideoId();
		this.status = event.getState().getDisplayName();
		this.percent = event.getPercentage();
	}

	public long getVideoId() {
		return videoId;
	}

	public void setVideoId(long videoId) {
		this.videoId = videoId;
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
