package org.gneisenau.youtube.model;

public enum State {

	WaitForProcessing {
		public State nextState() {
			return State.OnProcessing;
		}

		public State errorState() {
			return State.Error;
		}

		public String getDisplayName() {
			return "Warte auf Verarbeitung";
		}
	},
	OnProcessing {
		public State nextState() {
			return State.WaitForUpload;
		}

		public State errorState() {
			return State.Error;
		}

		public String getDisplayName() {
			return "In Bearbeitung";
		}
	},
	WaitForUpload {
		public State nextState() {
			return State.OnUpload;
		}

		public State errorState() {
			return State.Error;
		}

		public String getDisplayName() {
			return "Warte auf Hochladen";
		}
	},
	OnUpload {
		public State nextState() {
			return State.WaitForListing;
		}

		public State errorState() {
			return State.Error;
		}

		public String getDisplayName() {
			return "Wird hochgeladen";
		}
	},
	OnListing {
		public State nextState() {
			return State.Done;
		}

		public State errorState() {
			return State.Error;
		}

		public String getDisplayName() {
			return "Wird hochgeladen";
		}
	},
	WaitForListing {
		public State nextState() {
			return State.OnListing;
		}

		public State errorState() {
			return State.Error;
		}

		public String getDisplayName() {
			return "Warte auf Ver\u00f6ffentlichung";
		}
	},
	Done {
		public State nextState() {
			return State.OnProcessing;
		}

		public State errorState() {
			return State.Error;
		}

		public String getDisplayName() {
			return "Ver\u00f6ffentlicht";
		}
	},
	Error {
		public State nextState() {
			return State.Error;
		}

		public State errorState() {
			return State.Error;
		}

		public String getDisplayName() {
			return "Fehler";
		}
	};

	public abstract State nextState();

	public abstract State errorState();

	public abstract String getDisplayName();

	public static final State getInitialState() {
		return State.WaitForProcessing;
	}

	public static State fromString(String value) {
		for (State state : values()) {
			if (state.name().equalsIgnoreCase(value)) {
				return state;
			}
			if (state.getDisplayName().equalsIgnoreCase(value)) {
				return state;
			}
		}

		return Error;
	}
}
