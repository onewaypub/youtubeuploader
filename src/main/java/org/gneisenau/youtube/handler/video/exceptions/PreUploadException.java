package org.gneisenau.youtube.handler.video.exceptions;

import java.io.IOException;

public class PreUploadException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5882692673196114286L;

	public PreUploadException(IOException e) {
		super(e);
	}

}
