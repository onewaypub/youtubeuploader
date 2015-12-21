package org.gneisenau.youtube.video;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.exec.ExecuteException;
import org.apache.log4j.Logger;
import org.gneisenau.youtube.controller.IOService;
import org.gneisenau.youtube.exceptions.VideoMergeException;
import org.gneisenau.youtube.exceptions.VideoTranscodeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource("file:${user.home}/youtubeuploader.properties")
public class VideoUtils {

	private static final Logger logger = Logger.getLogger(VideoUtils.class);
	@Autowired
	IOService ioService;

	public String transcode(File inputFile, File outputFile) throws ExecuteException, IOException {
		String line = ioService.findFFMPEG() + " -i " + inputFile.getAbsolutePath()
				+ " -codec:v libx264 -bf 2 -flags +cgop -pix_fmt yuv420p -codec:a aac -strict -2 -b:a 384k -r:a 48000 -movflags faststart "
				+ outputFile.getAbsolutePath();
		String newFile = outputFile.getAbsolutePath();
		try {
			ioService.executeCommandLine(line);
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
				String line = ffmpeg + " -i " + f + " -c copy -bsf:v h264_mp4toannexb -f mpegts " + file.getName();
				try {
					ioService.executeCommandLine(line);
				} catch (IOException e) {
					throw new VideoTranscodeException(e);
				}
			}
			String line = ffmpeg + " -i \"concat:" + concatList + "\" -c copy -bsf:a aac_adtstoasc  " + target;
			try {
				ioService.executeCommandLine(line);
			} catch (IOException e) {
				throw new VideoMergeException(e);
			}
		} finally {
			for (File f : streamFiles) {
				f.delete();
			}
		}
	}

}
