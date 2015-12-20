package org.gneisenau.youtube.exceptions;

public class ReleaseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5882692673196114286L;

	public ReleaseException(Exception e) {
		super(e);
	}

	public ReleaseException(String string) {
		super(string);
	}

}
