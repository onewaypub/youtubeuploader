package org.gneisenau.youtube.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class UserSettings {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Long id;
	private String username;
	private String intro;
	private String outro;
	private String mailTo;
	private boolean notifyUploadState;
	private boolean notifyProcessedState;
	private boolean notifyReleaseState;
	private boolean notifyErrorState;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getOutro() {
		return outro;
	}

	public void setOutro(String outro) {
		this.outro = outro;
	}

	public String getMailTo() {
		return mailTo;
	}

	public void setMailTo(String mailTo) {
		this.mailTo = mailTo;
	}

	public boolean isNotifyUploadState() {
		return notifyUploadState;
	}

	public void setNotifyUploadState(boolean notifyUploadState) {
		this.notifyUploadState = notifyUploadState;
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

	public boolean isNotifyErrorState() {
		return notifyErrorState;
	}

	public void setNotifyErrorState(boolean notifyErrorState) {
		this.notifyErrorState = notifyErrorState;
	}

	@Override
	public String toString() {
		return "UserSettings [id=" + id + ", username=" + username + ", intro=" + intro + ", outro=" + outro
				+ ", mailTo=" + mailTo + ", notifyUploadState=" + notifyUploadState + ", notifyProcessedState="
				+ notifyProcessedState + ", notifyReleaseState=" + notifyReleaseState + ", notifyErrorState="
				+ notifyErrorState + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((intro == null) ? 0 : intro.hashCode());
		result = prime * result + ((mailTo == null) ? 0 : mailTo.hashCode());
		result = prime * result + (notifyErrorState ? 1231 : 1237);
		result = prime * result + (notifyProcessedState ? 1231 : 1237);
		result = prime * result + (notifyReleaseState ? 1231 : 1237);
		result = prime * result + (notifyUploadState ? 1231 : 1237);
		result = prime * result + ((outro == null) ? 0 : outro.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
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
		UserSettings other = (UserSettings) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (intro == null) {
			if (other.intro != null)
				return false;
		} else if (!intro.equals(other.intro))
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
		if (outro == null) {
			if (other.outro != null)
				return false;
		} else if (!outro.equals(other.outro))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
}
