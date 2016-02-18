package org.gneisenau.youtube.processor.task;

import java.io.ByteArrayInputStream;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.gneisenau.youtube.handler.video.exceptions.AuthorizeException;
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
