package org.gneisenau.youtube.processor.task;

import org.gneisenau.youtube.handler.video.exceptions.AuthorizeException;
import org.gneisenau.youtube.model.Video;

public class TaskException extends Exception{
	
	private Video video;

	public TaskException(Video v, String string) {
		super(string);
		this.video = v;
	}

	public TaskException(Video v, String string, Exception e) {
		super(string, e);
		this.video = v;
	}

	public Video getVideo() {
		return video;
	}


}
