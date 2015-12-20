package org.gneisenau.youtube.exceptions;

import java.io.IOException;

public class ClientSecrectsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7976590222074503944L;

	public ClientSecrectsException(IOException e) {
		super(e);
	}

}
