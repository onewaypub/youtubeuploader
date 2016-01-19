package org.gneisenau.youtube.handler.video.exceptions;

public class NotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5882692673196114286L;

	public NotFoundException(Exception e) {
		super(e);
	}

	public NotFoundException(String string) {
		super(string);
	}

}
