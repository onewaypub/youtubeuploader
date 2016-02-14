package org.gneisenau.youtube.utils;

import org.dozer.DozerBeanMapper;
import org.gneisenau.youtube.handler.youtube.YouTubeUtils;
import org.gneisenau.youtube.model.Video;
import org.gneisenau.youtube.to.VideoTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BeanMapper {

	@Autowired
	private DozerBeanMapper dozerBeanMapper;
	@Autowired
	private YouTubeUtils youtubeUtils;

	public Video createVideo(VideoTO to, String username) {
		Video v = dozerBeanMapper.map(to, Video.class);
		copyAdditionalValues(to, v, username);
		return v;
	}

	public VideoTO createVideo(Video v, String username) {
		VideoTO bean = dozerBeanMapper.map(v, VideoTO.class);
		copyAdditionalValues(v, bean, username);
		return bean;
	}

	public void copyVideo(VideoTO to, Video v, String username) {
		dozerBeanMapper.map(to, v);
		copyAdditionalValues(to, v, username);
	}

	public void copyVideo(Video v, VideoTO to, String username) {
		dozerBeanMapper.map(v, to);
		copyAdditionalValues(v, to, username);
	}

	private void copyAdditionalValues(VideoTO to, Video v, String username) {
		v.setPlaylistId(youtubeUtils.getPaylistId(to.getPlaylist(), username));
		v.setCategoryId(youtubeUtils.getCategoryId(to.getCategory()));
	}

	private void copyAdditionalValues(Video v, VideoTO bean, String username) {
		bean.setLocalVideoUrl("getVideo/" + bean.getId() + ".mp4");
		bean.setLocalThumbnailUrl("getThumbnailImage/" + bean.getId());
		String playlistDisplayName = youtubeUtils.getPlaylistDisplayName(v.getPlaylistId(), username);
		String categoryDisplayName = youtubeUtils.getCategoryDisplayName(v.getCategoryId());
		bean.setPlaylist(playlistDisplayName);
		bean.setPlaylistId(playlistDisplayName);
		bean.setCategory(categoryDisplayName);
		bean.setCategoryId(categoryDisplayName);
	}

}
