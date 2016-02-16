package org.gneisenau.youtube.model;

public enum PrivacySetting {
	Private, Public, Unlisted;

	public static PrivacySetting getState(String state) {
		if ("private".equalsIgnoreCase(state)) {
			return PrivacySetting.Private;
		} else if ("oeffentlich".equalsIgnoreCase(state)) {
			return PrivacySetting.Public;
		} else {
			// Default
			return PrivacySetting.Unlisted;
		}
	}

	public String getDisplayName() {
		if (this.equals(PrivacySetting.Private)) {
			return "Privat";
		} else if (this.equals(PrivacySetting.Public)) {
			return "�ffentlich";
		} else if (this.equals(PrivacySetting.Unlisted)) {
			return "Nicht gelistet";
		}
		return this.name();
	}

}
