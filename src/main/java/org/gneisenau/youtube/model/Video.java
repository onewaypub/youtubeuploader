package org.gneisenau.youtube.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Video {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Long id;
	private String video;
	private String thumbnail;
	private Date releaseDate;
	private String tags;
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "errors")
	private List<String> errors;
	private PrivacySetting privacySetting;
	private State state;
	private String youtubeId;
	private String thumbnailUrl;
	private String videoUrl;
	private String title;
	private String description;
	private String categoryId;
	private String channelId;
	private String playlistId;
	private String publisher;
	private String published;
	private String developer;
	private String genre;
	private String shorttitle;
	private String username;
	private boolean ageRestricted;

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

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
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

	public String getDeveloper() {
		return developer;
	}

	public void setDeveloper(String developer) {
		this.developer = developer;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getShorttitle() {
		return shorttitle;
	}

	public void setShorttitle(String shorttitle) {
		this.shorttitle = shorttitle;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isAgeRestricted() {
		return ageRestricted;
	}

	public void setAgeRestricted(boolean ageRestricted) {
		this.ageRestricted = ageRestricted;
	}

	@Override
	public String toString() {
		return "Video [id=" + id + ", video=" + video + ", thumbnail=" + thumbnail + ", releaseDate=" + releaseDate
				+ ", tags=" + tags + ", errors=" + errors + ", privacySetting=" + privacySetting + ", state=" + state
				+ ", youtubeId=" + youtubeId + ", thumbnailUrl=" + thumbnailUrl + ", videoUrl=" + videoUrl + ", title="
				+ title + ", description=" + description + ", categoryId=" + categoryId + ", channelId=" + channelId
				+ ", playlistId=" + playlistId + ", publisher=" + publisher + ", published=" + published
				+ ", developer=" + developer + ", gerne=" + genre + ", shorttitle=" + shorttitle + ", username="
				+ username + ", ageRestricted=" + ageRestricted + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (ageRestricted ? 1231 : 1237);
		result = prime * result + ((categoryId == null) ? 0 : categoryId.hashCode());
		result = prime * result + ((channelId == null) ? 0 : channelId.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((developer == null) ? 0 : developer.hashCode());
		result = prime * result + ((errors == null) ? 0 : errors.hashCode());
		result = prime * result + ((genre == null) ? 0 : genre.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((playlistId == null) ? 0 : playlistId.hashCode());
		result = prime * result + ((privacySetting == null) ? 0 : privacySetting.hashCode());
		result = prime * result + ((published == null) ? 0 : published.hashCode());
		result = prime * result + ((publisher == null) ? 0 : publisher.hashCode());
		result = prime * result + ((releaseDate == null) ? 0 : releaseDate.hashCode());
		result = prime * result + ((shorttitle == null) ? 0 : shorttitle.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((tags == null) ? 0 : tags.hashCode());
		result = prime * result + ((thumbnail == null) ? 0 : thumbnail.hashCode());
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
		Video other = (Video) obj;
		if (ageRestricted != other.ageRestricted)
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
		if (playlistId == null) {
			if (other.playlistId != null)
				return false;
		} else if (!playlistId.equals(other.playlistId))
			return false;
		if (privacySetting != other.privacySetting)
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
		if (thumbnail == null) {
			if (other.thumbnail != null)
				return false;
		} else if (!thumbnail.equals(other.thumbnail))
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
