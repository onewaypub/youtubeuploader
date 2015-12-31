package org.gneisenau.youtube.video;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.exec.ExecuteException;
import org.gneisenau.youtube.exceptions.VideoMergeException;
import org.gneisenau.youtube.exceptions.VideoTranscodeException;
import org.gneisenau.youtube.utils.IOService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource("file:${user.home}/youtubeuploader.properties")
public class VideoUtils {

	private static final Logger logger = LoggerFactory.getLogger(VideoUtils.class);
	@Autowired
	IOService ioService;

	public String transcode(File inputFile, File outputFile) throws ExecuteException, IOException {
		String line = ioService.findFFMPEG() + " -threads 3 -i " + inputFile.getAbsolutePath()
				+ " -codec:v libx264 -bf 2 -flags +cgop -pix_fmt yuv420p -codec:a aac -strict -2 -b:a 384k -r:a 48000 -movflags faststart "
				+ outputFile.getAbsolutePath();
		String newFile = outputFile.getAbsolutePath();
		try {
			ioService.executeCommandLineWithReturn(line);
		} catch (IOException e) {
			logger.warn("Error on exit ffmpeg with errorcode", e);
			return inputFile.getPath();
		}
		return newFile;
	}

	public void merge(String target, List<File> files)
			throws VideoTranscodeException, VideoMergeException, ExecuteException, IOException {
		List<File> streamFiles = new ArrayList<File>();
		String ffmpeg = ioService.findFFMPEG();
		try {
			long counter = System.currentTimeMillis();
			String concatList = "";
			boolean first = true;
			for (File f : files) {
				File file = new File("intermediate" + counter + ".ts");
				streamFiles.add(file.getAbsoluteFile());
				counter++;
				concatList = concatList + (first ? "" : "|") + file.getName();
				first = false;
				String line = ffmpeg + " -threads 3 -i " + f + " -c copy -bsf:v h264_mp4toannexb -f mpegts " + file.getName();
				try {
					ioService.executeCommandLineWithReturn(line);
				} catch (IOException e) {
					throw new VideoTranscodeException(e);
				}
			}
			String line = ffmpeg + " -threads 3 -i \"concat:" + concatList + "\" -c copy -bsf:a aac_adtstoasc  " + target;
			try {
				ioService.executeCommandLineWithReturn(line);
			} catch (IOException e) {
				throw new VideoMergeException(e);
			}
		} finally {
			for (File f : streamFiles) {
				f.delete();
			}
		}
	}
	
//	private long getNumberOfFrames(File inputFile) throws ExecuteException, IOException{
//		String ffprobe = ioService.findFFPROBE();
//		String line = ffprobe + "  -v error -count_frames -select_streams v:0 -show_entries stream=nb_read_frames -of default=nokey=1:noprint_wrappers=1 " + inputFile.getAbsolutePath();		
//		try {
//			String frames = ioService.executeCommandLineWithReturn(line);
//			return Long.valueOf(frames);
//		} catch (IOException e) {
//			logger.warn("Error on exit ffmpeg with errorcode", e);
//			return 0L;
//		}
//	}

}
