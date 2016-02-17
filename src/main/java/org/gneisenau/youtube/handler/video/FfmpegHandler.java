package org.gneisenau.youtube.handler.video;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.exec.ExecuteException;
import org.apache.commons.lang3.Validate;
import org.gneisenau.youtube.events.StatusUpdateEvent;
import org.gneisenau.youtube.handler.youtube.exceptions.VideoMergeException;
import org.gneisenau.youtube.handler.youtube.exceptions.VideoTranscodeException;
import org.gneisenau.youtube.utils.IOService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class FfmpegHandler {

	private static final Logger logger = LoggerFactory.getLogger(FfmpegHandler.class);
	@Autowired
	private IOService ioService = new IOService();
	private final ApplicationEventPublisher publisher;

	@Autowired
	public FfmpegHandler(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}

	public void publishEvent(StatusUpdateEvent event) {
		this.publisher.publishEvent(event);
	}

	public String transcode(File inputFile, File outputFile, long id) throws ExecuteException, IOException {
		Validate.notNull(inputFile, "Source video is null");
		Validate.notNull(outputFile, "Output video is null");
		
		if(!inputFile.exists()){
			throw new IOException("Source video not existing");
		}
		if(outputFile.exists()){
			throw new IOException("Target video already existing");
		}
		
		String line = ioService.findFFMPEG() + " -i " + inputFile.getAbsolutePath()
				+ " -codec:v libx264 -bf 2 -flags +cgop -pix_fmt yuv420p -codec:a aac -strict -2 -b:a 384k -r:a 48000 -movflags faststart "
				+ outputFile.getAbsolutePath();
		String newFile = outputFile.getAbsolutePath();
		String output = null;
		ProgressAwareFFmpegOutputfilterStream stream = null;
		try {
			stream = new ProgressAwareFFmpegOutputfilterStream(publisher, id);
			output = ioService.executeCommandLineWithReturn(line, stream, ioService.getTemporaryProcessingFolder());
			boolean delete = inputFile.delete();
			logger.debug("Delete old file " + inputFile.getAbsolutePath() + ", State: " + delete);
		} catch (IOException e) {
			logger.warn("Error on exit ffmpeg with errorcode: " + output, e);
			boolean delete = outputFile.delete();
			logger.debug("Delete transcoded file " + outputFile.getAbsolutePath() + ", State: " + delete);
			return inputFile.getPath();
		} finally {
			stream.close();
		}
		return newFile;
	}

	public void merge(String target, File intro, File main, File outro)
			throws VideoTranscodeException, VideoMergeException, ExecuteException, IOException {
		List<File> streamFiles = new ArrayList<File>();
		String ffmpeg = ioService.findFFMPEG();
		try {
			long counter = System.currentTimeMillis();
			String concatList = "";
			boolean first = true;
			List<File> files = new ArrayList<File>();
			if (intro != null) {
				files.add(intro);
			}
			if (main != null) {
				files.add(main);
			}
			if (outro != null) {
				files.add(outro);
			}
			for (File f : files) {
				String filename = "intermediate" + counter + ".ts";
				streamFiles.add(new File(ioService.getTemporaryProcessingFolder() + File.separator + filename));
				counter++;
				concatList = concatList + (first ? "" : "|") + filename;
				first = false;
				String line = ffmpeg + " -i " + f + " -c copy -bsf:v h264_mp4toannexb -f mpegts " + filename;
				String output = null;
				try {
					output = ioService.executeCommandLineWithReturn(line, ioService.getTemporaryProcessingFolder());
				} catch (IOException e) {
					logger.warn("Error on exit ffmpeg with errorcode: " + output, e);
					throw new VideoTranscodeException(e);
				}
			}
			String line = ffmpeg + " -i \"concat:" + concatList + "\" -c copy -bsf:a aac_adtstoasc  " + target;
			String output = null;
			try {
				ioService.executeCommandLineWithReturn(line, ioService.getTemporaryProcessingFolder());
				boolean deleted = main.delete();
				logger.debug("Delete main video file " + main.getAbsolutePath() + ", State: " + deleted);
			} catch (IOException e) {
				logger.warn("Error on exit ffmpeg with errorcode: " + output, e);
				throw new VideoMergeException(e);
			}
		} finally {
			for (File f : streamFiles) {
				boolean deleted = f.delete();
				logger.debug("Delete intermediate file " + f.getAbsolutePath() + ", State: " + deleted);
			}
		}
	}

}
