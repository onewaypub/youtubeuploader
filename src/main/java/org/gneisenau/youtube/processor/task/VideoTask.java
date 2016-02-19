package org.gneisenau.youtube.processor.task;

import org.gneisenau.youtube.model.Video;

public interface VideoTask {

	public ChainAction process(Video v) throws Exception;

}
