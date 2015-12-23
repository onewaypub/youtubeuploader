package org.gneisenau.youtube.scheduler;

import org.gneisenau.youtube.model.Video;

interface VideoProcessor {

	public void process(Video v);

}
