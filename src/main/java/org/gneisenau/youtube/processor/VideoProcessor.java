package org.gneisenau.youtube.processor;

import org.gneisenau.youtube.model.Video;

interface VideoProcessor {
	
	public static final int CONTINUE = 0;
	public static final int STOP = 0;

	public int process(Video v);

}
