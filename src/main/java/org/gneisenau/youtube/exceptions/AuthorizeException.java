package org.gneisenau.youtube.exceptions;

public class AuthorizeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5882692673196114286L;

	public AuthorizeException(Exception e) {
		super(e);
	}

}
