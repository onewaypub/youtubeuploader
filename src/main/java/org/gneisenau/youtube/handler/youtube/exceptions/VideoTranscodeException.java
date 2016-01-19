package org.gneisenau.youtube.handler.youtube.exceptions;

import java.io.IOException;

public class VideoTranscodeException extends Exception {

	public VideoTranscodeException(IOException e) {
		super(e);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 6796609521130399709L;

}
