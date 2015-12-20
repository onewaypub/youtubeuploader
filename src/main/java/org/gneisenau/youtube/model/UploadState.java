package org.gneisenau.youtube.model;

public enum UploadState {
	INITIATION_STARTED, INITIATION_COMPLETE, MEDIA_IN_PROGRESS, MEDIA_COMPLETE, NOT_STARTED;
	
	public String getDisplayName() {
		if (this.equals(UploadState.INITIATION_COMPLETE)) {
			return "Initialisierung abgeschlossen";
		} else if (this.equals(UploadState.INITIATION_STARTED)) {
			return "Initialisierung gestartet";
		} else if (this.equals(UploadState.MEDIA_COMPLETE)) {
			return "Medium abgeschlossen";
		} else if (this.equals(UploadState.MEDIA_IN_PROGRESS)) {
			return "MEdium wird verarbeitet";
		} else if (this.equals(UploadState.NOT_STARTED)) {
			return "Nicht gestartet";
		} 
		return this.name();
	}

}
