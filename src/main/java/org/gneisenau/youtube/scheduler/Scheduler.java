package org.gneisenau.youtube.scheduler;

import java.util.List;

import org.gneisenau.youtube.model.Video;
import org.gneisenau.youtube.model.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class Scheduler {

	@Autowired
	private VideoRepository videoDAO;
	@Autowired
	private VideoChain chain;

	@Scheduled(fixedDelay = 60000)
	public void run() {
		List<Video> videos = videoDAO.findAllWaitForPorcessing();
		chain.execute(videos);
	}

}
