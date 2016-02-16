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
	public int process(Video v) {
		// ConnectionRepository repository =
		// usersConnectionFactory.createConnectionRepository(v.getUsername());
		// List<Connection<Google>> connections =
		// repository.findConnections(Google.class);
		// if (connections.size() != 1) {
		// return PublishTask.CONTINUE;
		// }
		// if (!connections.get(0).test()) {
		// return PublishTask.CONTINUE;
		// }
		//
		// UserSettings settings = userSettings.findByUserName(v.getUsername());
		// if (!settings.isPostOnGoogle() ||
		// StringUtils.isBlank(settings.getGooglePost())) {
		// return PublishTask.CONTINUE;
		// }
		//
		// String text = textUtil.replacePlaceholder(settings.getGooglePost(),
		// v);
		//
		// Connection<Google> fConnection = connections.get(0);
		// Google google = fConnection.getApi();
		// Moment m = new Moment();
		// m.
		// google.plusOperations().insertMoment(moment);

		return PublishTask.CONTINUE;
	}

}
