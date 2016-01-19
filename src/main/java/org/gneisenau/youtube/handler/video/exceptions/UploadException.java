package org.gneisenau.youtube.handler.video.exceptions;

import java.io.IOException;

public class UploadException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7137117448680499148L;

	public UploadException(IOException e) {
		super(e);
	}

}
