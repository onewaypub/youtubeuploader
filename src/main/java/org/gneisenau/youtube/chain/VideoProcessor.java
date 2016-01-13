package org.gneisenau.youtube.chain;

import org.gneisenau.youtube.model.Video;

public interface VideoProcessor {
	
	public static final int CONTINUE = 0;
	public static final int STOP = 1;

	public int process(Video v);

}
