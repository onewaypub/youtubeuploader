package org.gneisenau.youtube.handler.video.exceptions;

public class UpdateException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5882692673196114286L;

	public UpdateException(Exception e) {
		super(e);
	}

	public UpdateException(String string) {
		super(string);
	}

}
