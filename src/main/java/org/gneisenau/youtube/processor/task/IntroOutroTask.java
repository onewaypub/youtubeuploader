package org.gneisenau.youtube.processor.task;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.Validate;
import org.gneisenau.youtube.handler.video.FfmpegHandler;
import org.gneisenau.youtube.handler.youtube.exceptions.VideoMergeException;
import org.gneisenau.youtube.handler.youtube.exceptions.VideoTranscodeException;
import org.gneisenau.youtube.model.Video;
import org.gneisenau.youtube.utils.IOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(value = 2)
public class IntroOutroTask extends AbstractProcessorTask implements VideoTask {

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
	@Transactional(propagation = Propagation.MANDATORY)
	public int process(Video v) {
		Validate.notNull(v, "No Video provided");
		Validate.notEmpty(v.getVideo(), "No video for processing found");

		// Merge Videos
		File oldFile = new File(v.getVideo());

		Validate.isTrue(oldFile.exists(), "Video file does not exist on harddisk");

		File intro = new File(introOutroDir + "/intro.mp4");
		File outro = new File(introOutroDir + "/outro.mp4");
		String baseName = FilenameUtils.getBaseName(v.getVideo());
		String extension = FilenameUtils.getExtension(v.getVideo());
		File newFile = new File(ioService.getTemporaryFolder() + File.separator + baseName + "_merged." + extension);
		try {
			videoProcessor.merge(newFile.getAbsolutePath(), intro, new File(v.getVideo()), outro);
		} catch (IOException e) {
			handleError(v, "Fehler beim Zugriff auf die zusammenzuf\u00fchrenden Videodatei w\u00f6hrend des Merges");
			return VideoTask.STOP;
		} catch (VideoTranscodeException e) {
			handleError(v, "Zusammenzuf\u00fchrenden Videodateien k\u00f6nnten nicht transcodiert werden ");
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
