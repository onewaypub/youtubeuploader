package org.gneisenau.youtube.processor.task;

import org.gneisenau.youtube.model.Video;

@FunctionalInterface
public interface PublishTask {

	public ChainAction process(Video v) throws Exception;

}
