package org.gneisenau.youtube.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource("file:${user.home}/youtubeuploader.properties")
public class IOService {

	private static final Logger logger = LoggerFactory.getLogger(IOService.class);
	@Value("${ffmpeg.home}")
	private String ffmpegHome;
	@Value("${home.temp.dir}")
	private String homeTempDir;

	public String findFFMPEG() throws ExecuteException, IOException {
		return ffmpegHome;
	}

	public void executeCommandLine(String line) throws ExecuteException, IOException {
		CommandLine cmdLine = CommandLine.parse(line);
		DefaultExecutor executor = new DefaultExecutor();
		executor.setExitValue(0);
		ExecuteWatchdog watchdog = new ExecuteWatchdog(7200000);
		executor.setWatchdog(watchdog);
		executor.setWorkingDirectory(new File(getTemporaryProcessingFolder()));
		int exitValue = executor.execute(cmdLine);
		if (exitValue != 0) {
			throw new ExecuteException("Error running command", exitValue);
		}

	}

	public String executeCommandLineWithReturn(String line) throws ExecuteException, IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		CommandLine cmdLine = CommandLine.parse(line);
		DefaultExecutor executor = new DefaultExecutor();
		executor.setExitValue(0);
		ExecuteWatchdog watchdog = new ExecuteWatchdog(7200000);
		PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
		executor.setStreamHandler(streamHandler);
		executor.setWatchdog(watchdog);
		executor.setWorkingDirectory(new File(getTemporaryProcessingFolder()));
		int exitValue = executor.execute(cmdLine);
		if (exitValue != 0) {
			throw new ExecuteException("Error running command", exitValue);
		}
		String output = outputStream.toString();
		logger.debug(output);
		return output;
	}

	public String getTemporaryFolder() {
		String tempDirPath = getSystemTemporaryFolder() + File.separator + "youtubeuploader";
		createDirIfNotPresent(tempDirPath);
		return tempDirPath;
	}

	public String getTemporaryProcessingFolder() {
		String tempDirPath = getSystemTemporaryFolder() + File.separator + "processing";
		createDirIfNotPresent(tempDirPath);
		return tempDirPath;
	}

	public String getSystemTemporaryFolder() {
		if (homeTempDir != null) {
			return homeTempDir;
		}
		return System.getProperty("java.io.tmpdir");
	}

	private void createDirIfNotPresent(String tempDirPath) {
		File tempDir = new File(tempDirPath);
		if (!tempDir.exists()) {
			tempDir.mkdirs();
		}
	}

	public List<String> getAllTemporaryFiles() {
		createDirIfNotPresent(getTemporaryFolder());
		List<String> files = new ArrayList<String>();
		File tempDir = new File(getTemporaryFolder());
		File[] listOfFiles = tempDir.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				files.add(getTemporaryFolder() + File.separator + listOfFiles[i].getName());
			}
		}
		return files;
	}

}
