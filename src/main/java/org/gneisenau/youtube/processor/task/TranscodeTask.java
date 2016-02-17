package org.gneisenau.youtube.processor.task;

import java.io.File;
import java.io.IOException;

import org.apache.commons.exec.ExecuteException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.Validate;
import org.gneisenau.youtube.handler.video.FfmpegHandler;
import org.gneisenau.youtube.model.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(value = 1)
public class TranscodeTask extends AbstractProcessorTask implements VideoTask {

	@Autowired
	private FfmpegHandler videoProcessor;

	@Autowired
	public TranscodeTask(ApplicationEventPublisher publisher) {
		super(publisher);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public int process(Video v) {
		Validate.notNull(v, "Video is not given");
		Validate.notEmpty(v.getVideo(), "Video is empty");

		File oldFile = new File(v.getVideo());
		if (!oldFile.exists()) {
			handleError(v, "Datei existiert nicht.");
			return VideoTask.STOP;
		}
		
		String baseName = FilenameUtils.getBaseName(v.getVideo());
		String path = FilenameUtils.getFullPath(v.getVideo());
		File transcodedFile = new File(path + baseName + "_transcoded.webm");
		try {
			videoProcessor.transcode(oldFile, transcodedFile, v.getId());
			v.setVideo(transcodedFile.getAbsolutePath());
			oldFile.delete();
		} catch (ExecuteException e) {
			handleError(v, "Fehler beim Ausf\u00fchren des Transcodings");
			transcodedFile.delete();
			return VideoTask.STOP;
		} catch (IOException e) {
			handleError(v, "Fehler beim Zugriff auf die Videodateien w\u00e4hrend des Transcodings");
			transcodedFile.delete();
			return VideoTask.STOP;
		}
		return VideoTask.CONTINUE;
	}

}
