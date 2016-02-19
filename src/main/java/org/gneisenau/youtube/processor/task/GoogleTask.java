package org.gneisenau.youtube.processor.task;

import org.gneisenau.youtube.model.UserSettingsRepository;
import org.gneisenau.youtube.model.Video;
import org.gneisenau.youtube.utils.TextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.Order;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.stereotype.Component;

@Component
@Order(value = 3)
public class GoogleTask extends AbstractProcessorTask implements PublishTask {

	@Autowired
	private UsersConnectionRepository usersConnectionFactory;
	@Autowired
	private UserSettingsRepository userSettings;
	@Autowired
	private TextUtil textUtil;

	@Autowired
	public GoogleTask(ApplicationEventPublisher publisher) {
		super(publisher);
	}

	@Override
	public ChainAction process(Video v) {

		return ChainAction.CONTINUE;
	}

}
