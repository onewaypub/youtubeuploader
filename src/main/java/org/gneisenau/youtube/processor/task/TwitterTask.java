package org.gneisenau.youtube.processor.task;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.gneisenau.youtube.model.UserSettings;
import org.gneisenau.youtube.model.UserSettingsRepository;
import org.gneisenau.youtube.model.Video;
import org.gneisenau.youtube.utils.TextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.Order;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Component;

@Component
@Order(value = 3)
public class TwitterTask extends AbstractProcessorTask implements PublishTask {

	@Autowired
	private UsersConnectionRepository usersConnectionFactory;
	@Autowired
	private UserSettingsRepository userSettings;
	@Autowired
	private TextUtil textUtil;

	@Autowired
	public TwitterTask(ApplicationEventPublisher publisher) {
		super(publisher);
	}

	@Override
	public int process(Video v) {
		ConnectionRepository repository = usersConnectionFactory.createConnectionRepository(v.getUsername());
		List<Connection<Twitter>> connections = repository.findConnections(Twitter.class);
		if (connections.size() != 1) {
			return PublishTask.CONTINUE;
		}
		if (!connections.get(0).test()) {
			return PublishTask.CONTINUE;
		}

		UserSettings settings = userSettings.findByUserName(v.getUsername());
		if (!settings.isPostOnTwitter() || StringUtils.isBlank(settings.getTwitterPost())) {
			return PublishTask.CONTINUE;
		}

		String text = textUtil.replacePlaceholder(settings.getTwitterPost(), v);

		Connection<Twitter> fConnection = connections.get(0);
		Twitter twitter = fConnection.getApi();
		twitter.timelineOperations().updateStatus(text);

		return PublishTask.CONTINUE;
	}

}
