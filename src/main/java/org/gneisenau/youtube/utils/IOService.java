package org.gneisenau.youtube.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.google.api.client.util.IOUtils;

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

	public long getFileSize(File f) {
		return f.length();
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
		return outputStream.toString();
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

	public List<File> writeMultipart2Files(MultipartHttpServletRequest request, File directory)
			throws FileUploadException, IOException {
		// Create a new file upload handler
		List<File> files = new ArrayList<File>();
		try {
			Map<String, MultipartFile> fileMap = request.getFileMap();
			for (Entry<String, MultipartFile> e : fileMap.entrySet()) {
				String name = e.getValue().getOriginalFilename();

				String path2save = directory.getAbsolutePath() + File.separatorChar;
				File newFile = new File(path2save + name);
				if (newFile.exists()) {
					newFile = new File(path2save + addMilliSecondsToFilename(name));
				}
				InputStream inputStream = e.getValue().getInputStream();
				BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(newFile));
				try {
					IOUtils.copy(inputStream, outputStream);
				} finally {
					outputStream.close();
					inputStream.close();
				}
				files.add(newFile);
			}
		} catch (IOException e) {
			// Cleanup on exception
			for (File f : files) {
				if (f.exists()) {
					f.delete();
				}
			}
			throw e;
		}
		return files;
	}

	private String addMilliSecondsToFilename(String name) {
		String baseName = FilenameUtils.getBaseName(name);
		String extension = FilenameUtils.getExtension(name);
		return baseName + System.currentTimeMillis() + "." + extension;
	}

}
