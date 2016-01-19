package org.gneisenau.youtube.processor.task;

import java.io.File;
import java.io.IOException;

import org.apache.commons.exec.ExecuteException;
import org.apache.commons.io.FilenameUtils;
import org.gneisenau.youtube.handler.video.FfmpegHandler;
import org.gneisenau.youtube.model.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value=1)
public class TranscodeTask extends AbstractVideoTask {

	@Autowired
	private FfmpegHandler videoProcessor;

	@Autowired
	public TranscodeTask(ApplicationEventPublisher publisher) {
		super(publisher);
	}

	@Override
	public int process(Video v) {
		File oldFile = new File(v.getVideo());
		String baseName = FilenameUtils.getBaseName(v.getVideo());
		String path = FilenameUtils.getFullPath(v.getVideo());
		if (!oldFile.exists()) {
			handleError(v, "Datei existiert nicht.");
			return VideoTask.STOP;
		}
		File transcodedFile = new File(path + baseName + "_transcoded.mp4");
		try {
			videoProcessor.transcode(oldFile, transcodedFile, v.getId());
			v.setVideo(transcodedFile.getAbsolutePath());
			oldFile.delete();
		} catch (ExecuteException e) {
			handleError(v, "Fehler beim Ausführen des Transcodings");
			transcodedFile.delete();
			return VideoTask.STOP;
		} catch (IOException e) {
			handleError(v, "Fehler beim Zugriff auf die Videodateien während des Transcodings");
			transcodedFile.delete();
			return VideoTask.STOP;
		}
		return VideoTask.CONTINUE;
	}


}
