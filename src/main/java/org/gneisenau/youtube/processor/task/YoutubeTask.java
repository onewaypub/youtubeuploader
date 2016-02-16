package org.gneisenau.youtube.processor.task;

import org.gneisenau.youtube.model.Video;

public interface YoutubeTask {

	public static final int CONTINUE = 0;
	public static final int STOP = 1;

	public int process(Video v);

}
