package org.gneisenau.youtube.to;

import org.springframework.web.bind.annotation.RequestParam;

public class UserSettingsTO {

	private String mailTo;
	private boolean notifyProcessedState;
	private boolean notifyReleaseState;
	private boolean notifyUploadState;
	private boolean notifyErrorState;
	private String videoFooter;
	private String defaultTags;
	private String twitterPost;
	private String facebookPost;
	private boolean postOnFacebook;
	private boolean postOnTwitter;

	public String getMailTo() {
		return mailTo;
	}

	public void setMailTo(String mailTo) {
		this.mailTo = mailTo;
	}

	public boolean isNotifyProcessedState() {
		return notifyProcessedState;
	}

	public void setNotifyProcessedState(boolean notifyProcessedState) {
		this.notifyProcessedState = notifyProcessedState;
	}

	public boolean isNotifyReleaseState() {
		return notifyReleaseState;
	}

	public void setNotifyReleaseState(boolean notifyReleaseState) {
		this.notifyReleaseState = notifyReleaseState;
	}

	public boolean isNotifyUploadState() {
		return notifyUploadState;
	}

	public void setNotifyUploadState(boolean notifyUploadState) {
		this.notifyUploadState = notifyUploadState;
	}

	public boolean isNotifyErrorState() {
		return notifyErrorState;
	}

	public void setNotifyErrorState(boolean notifyErrorState) {
		this.notifyErrorState = notifyErrorState;
	}

	public String getVideoFooter() {
		return videoFooter;
	}

	public void setVideoFooter(String videoFooter) {
		this.videoFooter = videoFooter;
	}

	public String getDefaultTags() {
		return defaultTags;
	}

	public void setDefaultTags(String defaultTags) {
		this.defaultTags = defaultTags;
	}

	public String getTwitterPost() {
		return twitterPost;
	}

	public void setTwitterPost(String twitterPost) {
		this.twitterPost = twitterPost;
	}

	public String getFacebookPost() {
		return facebookPost;
	}

	public void setFacebookPost(String facebookPost) {
		this.facebookPost = facebookPost;
	}

	public boolean isPostOnFacebook() {
		return postOnFacebook;
	}

	public void setPostOnFacebook(boolean postOnFacebook) {
		this.postOnFacebook = postOnFacebook;
	}

	public boolean isPostOnTwitter() {
		return postOnTwitter;
	}

	public void setPostOnTwitter(boolean postOnTwitter) {
		this.postOnTwitter = postOnTwitter;
	}

	@Override
	public String toString() {
		return "UserSettingsTO [mailTo=" + mailTo + ", notifyProcessedState=" + notifyProcessedState
				+ ", notifyReleaseState=" + notifyReleaseState + ", notifyUploadState=" + notifyUploadState
				+ ", notifyErrorState=" + notifyErrorState + ", videoFooter=" + videoFooter + ", defaultTags="
				+ defaultTags + ", twitterPost=" + twitterPost + ", facebookPost=" + facebookPost + ", postOnFacebook="
				+ postOnFacebook + ", postOnTwitter=" + postOnTwitter + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((defaultTags == null) ? 0 : defaultTags.hashCode());
		result = prime * result + ((facebookPost == null) ? 0 : facebookPost.hashCode());
		result = prime * result + ((mailTo == null) ? 0 : mailTo.hashCode());
		result = prime * result + (notifyErrorState ? 1231 : 1237);
		result = prime * result + (notifyProcessedState ? 1231 : 1237);
		result = prime * result + (notifyReleaseState ? 1231 : 1237);
		result = prime * result + (notifyUploadState ? 1231 : 1237);
		result = prime * result + (postOnFacebook ? 1231 : 1237);
		result = prime * result + (postOnTwitter ? 1231 : 1237);
		result = prime * result + ((twitterPost == null) ? 0 : twitterPost.hashCode());
		result = prime * result + ((videoFooter == null) ? 0 : videoFooter.hashCode());
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
		UserSettingsTO other = (UserSettingsTO) obj;
		if (defaultTags == null) {
			if (other.defaultTags != null)
				return false;
		} else if (!defaultTags.equals(other.defaultTags))
			return false;
		if (facebookPost == null) {
			if (other.facebookPost != null)
				return false;
		} else if (!facebookPost.equals(other.facebookPost))
			return false;
		if (mailTo == null) {
			if (other.mailTo != null)
				return false;
		} else if (!mailTo.equals(other.mailTo))
			return false;
		if (notifyErrorState != other.notifyErrorState)
			return false;
		if (notifyProcessedState != other.notifyProcessedState)
			return false;
		if (notifyReleaseState != other.notifyReleaseState)
			return false;
		if (notifyUploadState != other.notifyUploadState)
			return false;
		if (postOnFacebook != other.postOnFacebook)
			return false;
		if (postOnTwitter != other.postOnTwitter)
			return false;
		if (twitterPost == null) {
			if (other.twitterPost != null)
				return false;
		} else if (!twitterPost.equals(other.twitterPost))
			return false;
		if (videoFooter == null) {
			if (other.videoFooter != null)
				return false;
		} else if (!videoFooter.equals(other.videoFooter))
			return false;
		return true;
	}
}
