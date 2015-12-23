package org.gneisenau.youtube.processor;

import java.io.File;
import java.io.IOException;

import org.apache.commons.exec.ExecuteException;
import org.gneisenau.youtube.model.State;
import org.gneisenau.youtube.model.Video;
import org.gneisenau.youtube.video.VideoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
class TranscodeProcessor extends AbstractVideoProcessor {

	@Autowired
	private VideoUtils videoProcessor;

	@Override
	public int process(Video v) {
		File transcodedFile = null;
		try {
			v.setState(State.OnProcessing);
			transcodedFile = new File(v.getVideo() + ".mp4");
			try {
				videoProcessor.transcode(new File(v.getVideo()), transcodedFile);
			} catch (ExecuteException e) {
				handleError(v, "Fehler beim Ausführen des Transcodings");
				return VideoProcessor.STOP;
			} catch (IOException e) {
				handleError(v, "Fehler beim Zugriff auf die Videodateien während des Transcodings");
				return VideoProcessor.STOP;
			}
			return VideoProcessor.CONTINUE;
		} finally {
			transcodedFile.delete();
		}
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

}
