package org.gneisenau.youtube.processor.task;

import java.util.List;

import org.gneisenau.youtube.model.Video;
import org.gneisenau.youtube.model.VideoRepository;
import org.gneisenau.youtube.processor.VideoProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class Scheduler {

	@Autowired
	private VideoRepository videoDAO;
	@Autowired
	private VideoProcessor chain;

	@Scheduled(fixedDelay = 60000)
	public void run() {
		chain.execute();
	}

}
