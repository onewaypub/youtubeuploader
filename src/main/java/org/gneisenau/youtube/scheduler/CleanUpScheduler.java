package org.gneisenau.youtube.scheduler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gneisenau.youtube.controller.IOService;
import org.gneisenau.youtube.model.State;
import org.gneisenau.youtube.model.Video;
import org.gneisenau.youtube.model.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class CleanUpScheduler {

	@Autowired
	private VideoRepository videoDAO;
	@Autowired
	private IOService ioService;

	private static final Logger logger = LogManager.getLogger(CleanUpScheduler.class);

	@Scheduled(cron = "0 4 * * * *")//one time per day
	public void cleanUpTempDir() {
		List<String> filesInDB = new ArrayList<String>();
		List<Video> videos = videoDAO.findAll();
		for (Video v : videos) {
			if (!State.Done.equals(v.getState())) {
				filesInDB.add(v.getThumbnail());
				filesInDB.add(v.getVideo());
			}
		}
		List<String> allTemporaryFiles = ioService.getAllTemporaryFiles();
		allTemporaryFiles.removeAll(filesInDB);
		for(String tempFiles : allTemporaryFiles){			
			logger.debug("Remove file: " + tempFiles);
			new File(tempFiles).delete();
		}
	}
	@Scheduled(cron = "0 4 * * * *")//one time per day
	public void cleanUpDB() {
		List<String> allTemporaryFiles = ioService.getAllTemporaryFiles();
		List<Video> videos = videoDAO.findAll();
		for (Video v : videos) {
			if(!(allTemporaryFiles.contains(v.getThumbnail()) || allTemporaryFiles.contains(v.getVideo()))){
				videoDAO.delete(v.getId());
			}
		}
	}
}
