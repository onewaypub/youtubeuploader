package org.gneisenau.youtube.processor.task;

import org.gneisenau.youtube.model.Video;

public class YoutubeException extends TaskException{
	
	private Video video;

	public YoutubeException(Video v, String string) {
		super(v, string);
	}

	public YoutubeException(Video v, String string, Exception e) {
		super(v, string, e);
	}


}
