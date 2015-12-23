package org.gneisenau.youtube.scheduler;

import java.io.File;
import java.io.IOException;

import org.apache.commons.exec.ExecuteException;
import org.gneisenau.youtube.model.State;
import org.gneisenau.youtube.model.Video;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
class TranscodeProcessor extends AbstractVideoProcessor {


	@Override
	public void process(Video v) {
		File transcodedFile = null;
		try {
			v.setState(State.OnProcessing);
			transcodedFile = new File(v.getVideo() + ".mp4");
			try {
				videoProcessor.transcode(new File(v.getVideo()), transcodedFile);
			} catch (ExecuteException e) {
				handleError(v, "Fehler beim Ausf�hren des Transcodings");
				return;
			} catch (IOException e) {
				handleError(v, "Fehler beim Zugriff auf die Videodateien w�hrend des Transcodings");
				return;
			}
		} finally {
			transcodedFile.delete();
		}
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

}
