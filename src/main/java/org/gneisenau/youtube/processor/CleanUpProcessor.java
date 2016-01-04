package org.gneisenau.youtube.processor;

import org.gneisenau.youtube.model.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

//@Component
public class CleanUpProcessor extends AbstractVideoProcessor{

	@Autowired
	public CleanUpProcessor(ApplicationEventPublisher publisher) {
		super(publisher);
	}

	@Override
	public int process(Video v) {		
		return VideoProcessor.CONTINUE;
	}


}
