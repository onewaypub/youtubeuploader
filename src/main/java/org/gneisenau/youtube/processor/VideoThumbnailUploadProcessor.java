package org.gneisenau.youtube.processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.gneisenau.youtube.exceptions.AuthorizeException;
import org.gneisenau.youtube.exceptions.PreUploadException;
import org.gneisenau.youtube.exceptions.UploadException;
import org.gneisenau.youtube.handler.ImageHandler;
import org.gneisenau.youtube.model.State;
import org.gneisenau.youtube.model.UploadState;
import org.gneisenau.youtube.model.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class VideoThumbnailUploadProcessor extends AbstractVideoProcessor {

	@Autowired
	private ImageHandler imgUploader;

	@Override
	public int process(Video v) {
		String mail = userSettingsDAO.findByUserName(v.getUsername()).getMailTo();
		if (mail != null && mail.trim().length() == 0) {
			return VideoProcessor.STOP;
		}
		File thumb = new File(v.getThumbnail());
		try {
			v.setState(State.OnUpload);
			try {
				uploadThumbnail(v, thumb);
			} catch (FileNotFoundException e) {
				handleError(v, "Das Thumbnail konnte auf der Festplatt nicht mehr gefunden werden", null);
				return VideoProcessor.STOP;
			} catch (PreUploadException e) {
				handleError(v, "Vorbereitung des Thumbnails für Youtube fehlgeschlagen", e);
				return VideoProcessor.STOP;
			} catch (AuthorizeException e) {
				handleError(v, "Authorisierung bei Youtube fehlgeschlagen", e);
				return VideoProcessor.STOP;
			} catch (UploadException e) {
				handleError(v, "Das Thumbnails konnte nicht hochgeladen werden", e);
				return VideoProcessor.STOP;
			}
			v.setState(State.WaitForListing);
			if (userSettingsDAO.findByUserName(v.getUsername()).isNotifyReleaseState()) {
				mailService.sendStatusMail(v.getTitle(), v.getState(), v.getUsername());
			}
			return VideoProcessor.CONTINUE;
		} catch (Throwable e) {
			throw e;
		}
	}

	private void uploadThumbnail(Video v, File thumb)
			throws FileNotFoundException, PreUploadException, AuthorizeException, UploadException {
		v.setThumbnailUploadState(UploadState.MEDIA_IN_PROGRESS);
		imgUploader.upload(v.getId(), v.getYoutubeId(), new FileInputStream(thumb), v.getUsername(), thumb.length());
		v.setThumbnailUploadState(UploadState.MEDIA_COMPLETE);
	}

}
