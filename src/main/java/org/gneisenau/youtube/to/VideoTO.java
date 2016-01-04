package org.gneisenau.youtube.to;

import java.util.List;

import org.gneisenau.youtube.model.PrivacySetting;
import org.gneisenau.youtube.model.State;
import org.gneisenau.youtube.utils.StateDeserializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class VideoTO {

	private Long id;
	private String description;
	private String title;
	private String playlist;
	private String tags;
	private String video;
	private String releaseDate;
	private String publisher;
	private String published;
	private String shorttitle;
	private String developer;
	private String categoryId;
	private String genre;
	private List<String> errors;
	private boolean ageRestricted;
	private PrivacySetting privacySetting;
	@JsonDeserialize(using = StateDeserializer.class)
	private State state;
	private int process;
	private String youtubeId;
	private String thumbnailUrl;
	private String localThumbnailUrl;
	private String localVideoUrl;
	private String videoUrl;
	private String channelId;
	private String playlistId;
	private String category;
	private String username;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getVideo() {
		return video;
	}

	public void setVideo(String video) {
		this.video = video;
	}

	public PrivacySetting getPrivacySetting() {
		return privacySetting;
	}

	public void setPrivacySetting(PrivacySetting privacySetting) {
		this.privacySetting = privacySetting;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public String getYoutubeId() {
		return youtubeId;
	}

	public void setYoutubeId(String youtubeId) {
		this.youtubeId = youtubeId;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getPlaylistId() {
		return playlistId;
	}

	public void setPlaylistId(String playlistId) {
		this.playlistId = playlistId;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	public boolean isAgeRestricted() {
		return ageRestricted;
	}

	public void setAgeRestricted(boolean ageRestricted) {
		this.ageRestricted = ageRestricted;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPlaylist() {
		return playlist;
	}

	public void setPlaylist(String playlist) {
		this.playlist = playlist;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getPublished() {
		return published;
	}

	public void setPublished(String published) {
		this.published = published;
	}

	public String getShorttitle() {
		return shorttitle;
	}

	public void setShorttitle(String shorttitle) {
		this.shorttitle = shorttitle;
	}

	public String getDeveloper() {
		return developer;
	}

	public void setDeveloper(String developer) {
		this.developer = developer;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}

	public int getProcess() {
		return process;
	}

	public void setProcess(int process) {
		this.process = process;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getLocalThumbnailUrl() {
		return localThumbnailUrl;
	}

	public void setLocalThumbnailUrl(String localThumbnailUrl) {
		this.localThumbnailUrl = localThumbnailUrl;
	}

	public String getLocalVideoUrl() {
		return localVideoUrl;
	}

	public void setLocalVideoUrl(String localVideoUrl) {
		this.localVideoUrl = localVideoUrl;
	}

	@Override
	public String toString() {
		return "VideoTO [id=" + id + ", description=" + description + ", title=" + title + ", playlist=" + playlist
				+ ", tags=" + tags + ", video=" + video + ", releaseDate=" + releaseDate + ", publisher=" + publisher
				+ ", published=" + published + ", shorttitle=" + shorttitle + ", developer=" + developer
				+ ", categoryId=" + categoryId + ", genre=" + genre + ", errors=" + errors + ", ageRestricted="
				+ ageRestricted + ", privacySetting=" + privacySetting + ", state=" + state + ", process=" + process
				+ ", youtubeId=" + youtubeId + ", thumbnailUrl=" + thumbnailUrl + ", localThumbnailUrl="
				+ localThumbnailUrl + ", localVideoUrl=" + localVideoUrl + ", videoUrl=" + videoUrl + ", channelId="
				+ channelId + ", playlistId=" + playlistId + ", category=" + category + ", username=" + username + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (ageRestricted ? 1231 : 1237);
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((categoryId == null) ? 0 : categoryId.hashCode());
		result = prime * result + ((channelId == null) ? 0 : channelId.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((developer == null) ? 0 : developer.hashCode());
		result = prime * result + ((errors == null) ? 0 : errors.hashCode());
		result = prime * result + ((genre == null) ? 0 : genre.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((localThumbnailUrl == null) ? 0 : localThumbnailUrl.hashCode());
		result = prime * result + ((localVideoUrl == null) ? 0 : localVideoUrl.hashCode());
		result = prime * result + ((playlist == null) ? 0 : playlist.hashCode());
		result = prime * result + ((playlistId == null) ? 0 : playlistId.hashCode());
		result = prime * result + ((privacySetting == null) ? 0 : privacySetting.hashCode());
		result = prime * result + process;
		result = prime * result + ((published == null) ? 0 : published.hashCode());
		result = prime * result + ((publisher == null) ? 0 : publisher.hashCode());
		result = prime * result + ((releaseDate == null) ? 0 : releaseDate.hashCode());
		result = prime * result + ((shorttitle == null) ? 0 : shorttitle.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((tags == null) ? 0 : tags.hashCode());
		result = prime * result + ((thumbnailUrl == null) ? 0 : thumbnailUrl.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		result = prime * result + ((video == null) ? 0 : video.hashCode());
		result = prime * result + ((videoUrl == null) ? 0 : videoUrl.hashCode());
		result = prime * result + ((youtubeId == null) ? 0 : youtubeId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VideoTO other = (VideoTO) obj;
		if (ageRestricted != other.ageRestricted)
			return false;
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
			return false;
		if (categoryId == null) {
			if (other.categoryId != null)
				return false;
		} else if (!categoryId.equals(other.categoryId))
			return false;
		if (channelId == null) {
			if (other.channelId != null)
				return false;
		} else if (!channelId.equals(other.channelId))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (developer == null) {
			if (other.developer != null)
				return false;
		} else if (!developer.equals(other.developer))
			return false;
		if (errors == null) {
			if (other.errors != null)
				return false;
		} else if (!errors.equals(other.errors))
			return false;
		if (genre == null) {
			if (other.genre != null)
				return false;
		} else if (!genre.equals(other.genre))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (localThumbnailUrl == null) {
			if (other.localThumbnailUrl != null)
				return false;
		} else if (!localThumbnailUrl.equals(other.localThumbnailUrl))
			return false;
		if (localVideoUrl == null) {
			if (other.localVideoUrl != null)
				return false;
		} else if (!localVideoUrl.equals(other.localVideoUrl))
			return false;
		if (playlist == null) {
			if (other.playlist != null)
				return false;
		} else if (!playlist.equals(other.playlist))
			return false;
		if (playlistId == null) {
			if (other.playlistId != null)
				return false;
		} else if (!playlistId.equals(other.playlistId))
			return false;
		if (privacySetting != other.privacySetting)
			return false;
		if (process != other.process)
			return false;
		if (published == null) {
			if (other.published != null)
				return false;
		} else if (!published.equals(other.published))
			return false;
		if (publisher == null) {
			if (other.publisher != null)
				return false;
		} else if (!publisher.equals(other.publisher))
			return false;
		if (releaseDate == null) {
			if (other.releaseDate != null)
				return false;
		} else if (!releaseDate.equals(other.releaseDate))
			return false;
		if (shorttitle == null) {
			if (other.shorttitle != null)
				return false;
		} else if (!shorttitle.equals(other.shorttitle))
			return false;
		if (state != other.state)
			return false;
		if (tags == null) {
			if (other.tags != null)
				return false;
		} else if (!tags.equals(other.tags))
			return false;
		if (thumbnailUrl == null) {
			if (other.thumbnailUrl != null)
				return false;
		} else if (!thumbnailUrl.equals(other.thumbnailUrl))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		if (video == null) {
			if (other.video != null)
				return false;
		} else if (!video.equals(other.video))
			return false;
		if (videoUrl == null) {
			if (other.videoUrl != null)
				return false;
		} else if (!videoUrl.equals(other.videoUrl))
			return false;
		if (youtubeId == null) {
			if (other.youtubeId != null)
				return false;
		} else if (!youtubeId.equals(other.youtubeId))
			return false;
		return true;
	}

}
