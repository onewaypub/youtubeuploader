package org.gneisenau.youtube.processor.task;

import org.gneisenau.youtube.model.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

//@Component
public class CleanUpTask extends AbstractVideoTask{

	@Autowired
	public CleanUpTask(ApplicationEventPublisher publisher) {
		super(publisher);
	}

	@Override
	public int process(Video v) {		
		return VideoTask.CONTINUE;
	}


}
