package org.gneisenau.youtube.processor;

import org.gneisenau.youtube.model.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class Scheduler {

	@Autowired
	private VideoRepository videoDAO;
	@Autowired
	private VideoProcessor videoChain;
	@Autowired
	private YoutubeProcessor youtubeChain;

	@Scheduled(fixedDelay = 60000)
	public void runVideoChain() {
		videoChain.execute();
	}

	@Scheduled(fixedDelay = 60000)
	public void runYoutubeChain() {
		youtubeChain.execute();
	}

}
