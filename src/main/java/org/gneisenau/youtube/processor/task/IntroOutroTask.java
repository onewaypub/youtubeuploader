package org.gneisenau.youtube.processor.task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.gneisenau.youtube.handler.video.FfmpegHandler;
import org.gneisenau.youtube.handler.youtube.exceptions.VideoMergeException;
import org.gneisenau.youtube.handler.youtube.exceptions.VideoTranscodeException;
import org.gneisenau.youtube.model.Video;
import org.gneisenau.youtube.utils.IOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(value=2)
@PropertySource("file:${user.home}/youtubeuploader.properties")
public class IntroOutroTask extends AbstractVideoTask {

	@Autowired
	private FfmpegHandler videoProcessor;
	@Autowired
	protected IOService ioService;
	@Value("${tomcat.home.dir}")
	protected String introOutroDir;

	@Autowired
	public IntroOutroTask(ApplicationEventPublisher publisher) {
		super(publisher);
	}

	@Override
	@Transactional
	public int process(Video v) {
		// Merge Videos
		File oldFile = new File(v.getVideo());
		File intro = new File(introOutroDir + "/intro.mp4");
		File outro = new File(introOutroDir + "/outro.mp4");
		String baseName = FilenameUtils.getBaseName(v.getVideo());
		String extension = FilenameUtils.getExtension(v.getVideo());
		File newFile = new File(ioService.getTemporaryFolder() + File.separator + baseName + "_merged." + extension);

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
			return VideoTask.STOP;
		} catch (VideoTranscodeException e) {
			handleError(v, "Zusammenzuführenden Videodateien könnten nicht transcodiert werden ");
			return VideoTask.STOP;
		} catch (VideoMergeException e) {
			handleError(v, "Fehler beim Merge der Videodateien");
			return VideoTask.STOP;
		}
		v.setVideo(newFile.getAbsolutePath());
		oldFile.delete();
		return VideoTask.CONTINUE;
	}

}
