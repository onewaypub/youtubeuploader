package org.gneisenau.youtube.processor;

import org.gneisenau.youtube.model.Video;
import org.springframework.stereotype.Component;

@Component
class CleanUpProcessor extends AbstractVideoProcessor{

	@Override
	public int process(Video v) {		
		return VideoProcessor.CONTINUE;
	}


}
