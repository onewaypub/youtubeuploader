package org.gneisenau.youtube.handler.video;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.exec.ExecuteException;
import org.gneisenau.youtube.events.StatusUpdateEvent;
import org.gneisenau.youtube.handler.youtube.exceptions.VideoMergeException;
import org.gneisenau.youtube.handler.youtube.exceptions.VideoTranscodeException;
import org.gneisenau.youtube.utils.IOService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource("file:${user.home}/youtubeuploader.properties")
public class FfmpegHandler {

	private static final Logger logger = LoggerFactory.getLogger(FfmpegHandler.class);
	@Autowired
	IOService ioService = new IOService();
	private final ApplicationEventPublisher publisher;

	@Autowired
	public FfmpegHandler(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}

	public void publishEvent(StatusUpdateEvent event) {
		this.publisher.publishEvent(event);
	}

	public String transcode(File inputFile, File outputFile, long id) throws ExecuteException, IOException {
		// 
		String line = ioService.findFFMPEG()+ " -i " + inputFile.getAbsolutePath()
				+ " -codec:v libx264 -bf 2 -flags +cgop -pix_fmt yuv420p -codec:a aac -strict -2 -b:a 384k -r:a 48000 -movflags faststart "
				+ outputFile.getAbsolutePath();
		String newFile = outputFile.getAbsolutePath();
		String output = null;
		ProgressAwareFFmpegOutputfilterStream stream = null;
		try {
			stream = new ProgressAwareFFmpegOutputfilterStream(publisher, id);
			output = ioService.executeCommandLineWithReturn(line, stream);
			boolean delete = inputFile.delete();
			logger.debug("Delete old file " + inputFile.getAbsolutePath() +", State: " + delete);			
		} catch (IOException e) {
			logger.warn("Error on exit ffmpeg with errorcode: " + output, e);
			boolean delete = outputFile.delete();
			logger.debug("Delete transcoded file " + outputFile.getAbsolutePath() +", State: " + delete);			
			return inputFile.getPath();
		} finally{
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
			if(intro != null){
				files.add(intro);
			}
			if(main != null){
				files.add(main);
			}
			if(outro != null){
				files.add(outro);
			}
			for (File f : files) {
				File file = new File("intermediate" + counter + ".ts");
				streamFiles.add(file.getAbsoluteFile());
				counter++;
				concatList = concatList + (first ? "" : "|") + file.getName();
				first = false;
				String line = ffmpeg + " -i " + f + " -c copy -bsf:v h264_mp4toannexb -f mpegts " + file.getName();
				String output = null;
				try {
					output = ioService.executeCommandLineWithReturn(line);
				} catch (IOException e) {
					logger.warn("Error on exit ffmpeg with errorcode: " + output, e);
					throw new VideoTranscodeException(e);
				}
			}
			String line = ffmpeg + " -i \"concat:" + concatList + "\" -c copy -bsf:a aac_adtstoasc  " + target;
			String output = null;
			try {
				ioService.executeCommandLineWithReturn(line);
				boolean deleted = main.delete();
				logger.debug("Delete main video file " + main.getAbsolutePath() +", State: " + deleted);			
			} catch (IOException e) {
				logger.warn("Error on exit ffmpeg with errorcode: " + output, e);
				throw new VideoMergeException(e);
			}
		} finally {
			for (File f : streamFiles) {
				boolean deleted = f.delete();
				logger.debug("Delete intermediate file " + f.getAbsolutePath() +", State: " + deleted);			
			}
		}
	}

}
