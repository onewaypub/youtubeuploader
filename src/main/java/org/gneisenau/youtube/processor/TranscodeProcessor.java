package org.gneisenau.youtube.processor;

import java.io.File;
import java.io.IOException;

import org.apache.commons.exec.ExecuteException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.gneisenau.youtube.model.State;
import org.gneisenau.youtube.model.Video;
import org.gneisenau.youtube.video.VideoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value=1)
public class TranscodeProcessor extends AbstractVideoProcessor {

	@Autowired
	private VideoUtils videoProcessor;

	@Autowired
	public TranscodeProcessor(ApplicationEventPublisher publisher) {
		super(publisher);
	}

	@Override
	public int process(Video v) {
		File oldFile = new File(v.getVideo());
		String baseName = FilenameUtils.getBaseName(v.getVideo());
		String path = FilenameUtils.getFullPath(v.getVideo());
		if (!oldFile.exists()) {
			handleError(v, "Datei existiert nicht.");
			return VideoProcessor.STOP;
		}
		File transcodedFile = new File(path + baseName + "_transcoded.mp4");
		try {
			videoProcessor.transcode(oldFile, transcodedFile);
			v.setVideo(transcodedFile.getAbsolutePath());
			oldFile.delete();
		} catch (ExecuteException e) {
			handleError(v, "Fehler beim Ausführen des Transcodings");
			transcodedFile.delete();
			return VideoProcessor.STOP;
		} catch (IOException e) {
			handleError(v, "Fehler beim Zugriff auf die Videodateien während des Transcodings");
			transcodedFile.delete();
			return VideoProcessor.STOP;
		}
		return VideoProcessor.CONTINUE;
	}


}
