package org.gneisenau.youtube.handler.video.exceptions;

import java.io.IOException;

public class SecretsStoreException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2889121572743290843L;

	public SecretsStoreException(IOException e) {
		super(e);
	}

}
