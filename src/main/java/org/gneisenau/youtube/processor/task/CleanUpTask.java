package org.gneisenau.youtube.processor.task;

import org.gneisenau.youtube.model.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

//@Component
public class CleanUpTask extends AbstractProcessorTask implements YoutubeTask {

	@Autowired
	public CleanUpTask(ApplicationEventPublisher publisher) {
		super(publisher);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public ChainAction process(Video v) {
		return ChainAction.CONTINUE;
	}

}
