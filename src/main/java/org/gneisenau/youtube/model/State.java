package org.gneisenau.youtube.model;

public enum State {

	WaitForProcessing, OnProcessing,  WaitForUpload, OnUpload, WaitForListing, Done, Error;
	
	public String getDisplayName() {
		if (this.equals(State.Done)) {
			return "Veröffentlicht";
		} else if (this.equals(State.Error)) {
			return "Fehler";
		} else if (this.equals(State.OnProcessing)) {
			return "In Bearbeitung";
		} else if (this.equals(State.OnUpload)) {
			return "Wird hochgeladen";
		} else if (this.equals(State.WaitForListing)) {
			return "Warte auf Veröffentlichung";
		} else if (this.equals(State.WaitForProcessing)) {
			return "Warte auf Verarbeitung";
		} else if (this.equals(State.WaitForUpload)) {
			return "Warte auf Hochladen";
		}
		return this.name();
	}
	
}
