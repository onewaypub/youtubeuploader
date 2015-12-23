package org.gneisenau.youtube.scheduler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.gneisenau.youtube.exceptions.VideoMergeException;
import org.gneisenau.youtube.exceptions.VideoTranscodeException;
import org.gneisenau.youtube.model.State;
import org.gneisenau.youtube.model.Video;
import org.gneisenau.youtube.utils.IOService;
import org.gneisenau.youtube.video.VideoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@PropertySource("file:${user.home}/youtubeuploader.properties")
class IntroOutroProcessor extends AbstractVideoProcessor {

	@Autowired
	private VideoUtils videoProcessor;
	@Autowired
	protected IOService ioService;
	@Value("${tomcat.home.dir}")
	protected String introOutroDir;

	@Override
	@Transactional
	public void process(Video v) {
		// Merge Videos
		File oldFile = new File(v.getVideo());
		File intro = new File(introOutroDir + "/intro.mp4");
		File outro = new File(introOutroDir + "/outro.mp4");
		File newFile = new File(ioService.getTemporaryFolder() + File.separator + System.currentTimeMillis() + ".mp4");

		List<File> files = new ArrayList<File>();
		if (intro != null) {
			files.add(intro);
		}
		files.add(new File(v.getVideo()));
		if (outro != null) {
			files.add(outro);
		}
		try {
			videoProcessor.merge(newFile.getAbsolutePath(), files);
		} catch (IOException e) {
			handleError(v, "Fehler beim Zugriff auf die zusammenzuführenden Videodatei während des Merges");
			return;
		} catch (VideoTranscodeException e) {
			handleError(v, "Zusammenzuführenden Videodateien könnten nicht transcodiert werden ");
			return;
		} catch (VideoMergeException e) {
			handleError(v, "Fehler beim Merge der Videodateien");
			return;
		}
		v.setVideo(newFile.getAbsolutePath());
		v.setState(State.WaitForUpload);
		if (userSettingsDAO.findByUserName(v.getUsername()).isNotifyProcessedState()) {
			mailService.sendStatusMail(v.getTitle(), v.getState(), v.getUsername());
		}
		oldFile.delete();
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

}
