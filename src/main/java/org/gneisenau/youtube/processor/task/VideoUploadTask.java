package org.gneisenau.youtube.processor.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.gneisenau.youtube.events.StatusUpdateEvent;
import org.gneisenau.youtube.handler.video.exceptions.AuthorizeException;
import org.gneisenau.youtube.handler.video.exceptions.PreUploadException;
import org.gneisenau.youtube.handler.video.exceptions.UploadException;
import org.gneisenau.youtube.handler.youtube.ProgressAwareInputStream;
import org.gneisenau.youtube.handler.youtube.VideoHandler;
import org.gneisenau.youtube.model.State;
import org.gneisenau.youtube.model.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class VideoUploadTask extends AbstractYoutubeTask {

	@Autowired
	protected VideoHandler vidUploader;

	@Autowired
	public VideoUploadTask(ApplicationEventPublisher publisher) {
		super(publisher);
	}

	@Override
	public int process(Video v) {
		String mail = userSettingsDAO.findByUserName(v.getUsername()).getMailTo();
		if (mail != null && mail.trim().length() == 0) {
			return VideoTask.CONTINUE;
		}
		File video = new File(v.getVideo());
		v.setState(State.OnUpload);
		try {
			ProgressAwareInputStream inputStream = new ProgressAwareInputStream(new FileInputStream(video),
					video.length(), video);
			inputStream.setOnProgressListener(new ProgressAwareInputStream.OnProgressListener() {
				@Override
				public void onProgress(int percentage, Object tag) {
					StatusUpdateEvent event = new StatusUpdateEvent(v.getId(), State.OnUpload, percentage, this);
					publishEvent(event);
				}
			});
			try {
				List<String> tags = new ArrayList<String>();
				CollectionUtils.addAll(tags, v.getTags().split(","));
				String id = vidUploader.upload(v.getId(), v.getPrivacySetting(), inputStream, tags, v.getTitle(),
						createDescription(v), v.getChannelId(), v.getCategoryId(), v.getPlaylistId(), v.getUsername(),
						v.isAgeRestricted());
				v.setYoutubeId(id);
				v.setVideoUrl("https://www.youtube.com/watch?v=" + id);
			} finally {
				try {
					inputStream.close();
				} catch (IOException e) {
					logger.error("Cannot close progress aware inputstream for video upload", e);
				}
			}
		} catch (FileNotFoundException e) {
			handleError(v, "Video konnte auf der Festplatt nicht mehr gefunden werden", null);
			return VideoTask.STOP;
		} catch (AuthorizeException e) {
			handleError(v, "Authorisierung bei Youttube fehlgeschlagen", e);
			return VideoTask.STOP;
		} catch (PreUploadException e) {
			handleError(v, "Vorbereitung des Videos für Youtube fehlgeschlagen", e);
			return VideoTask.STOP;
		} catch (UploadException e) {
			handleError(v, "Das Video konnte nicht hochgeladen werden", e);
			return VideoTask.STOP;
		}
		v.setState(State.WaitForListing);
		if (userSettingsDAO.findByUserName(v.getUsername()).isNotifyReleaseState()) {
			mailService.sendStatusMail(v.getTitle(), v.getState(), v.getUsername());
		}
		return VideoTask.CONTINUE;
	}

	private String createDescription(Video v) {
		String desc = v.getDescription() + "\n\nTitel: " + v.getShorttitle() + "\nGenre: " + v.getGenre()
				+ "\nEntwickler: " + v.getDeveloper() + "\nPublisher: " + v.getPublisher() + "\nVeröffentlichung: "
				+ v.getPublished() + "\n\nhttps://www.facebook.com/pages/PeachesLp/781275711939550"
				+ "\nhttps://twitter.com/Peaches_LP";
		return desc;
	}

}
