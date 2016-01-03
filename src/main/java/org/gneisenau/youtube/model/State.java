package org.gneisenau.youtube.model;

public enum State {

	WaitForProcessing {
		public State nextState() {
			return State.OnProcessing;
		}

		public String getDisplayName() {
			return "Warte auf Verarbeitung";
		}
	},
	OnProcessing {
		public State nextState() {
			return State.WaitForUpload;
		}

		public String getDisplayName() {
			return "In Bearbeitung";
		}
	},
	WaitForUpload {
		public State nextState() {
			return State.OnUpload;
		}

		public String getDisplayName() {
			return "Warte auf Hochladen";
		}
	},
	OnUpload {
		public State nextState() {
			return State.WaitForListing;
		}

		public String getDisplayName() {
			return "Wird hochgeladen";
		}
	},
	WaitForListing {
		public State nextState() {
			return State.Done;
		}

		public String getDisplayName() {
			return "Warte auf Veröffentlichung";
		}
	},
	Done {
		public State nextState() {
			return State.OnProcessing;
		}

		public String getDisplayName() {
			return "Veröffentlicht";
		}
	},
	Error {
		public State nextState() {
			return State.Error;
		}

		public String getDisplayName() {
			return "Fehler";
		}
	};

	public abstract State nextState();

	public abstract String getDisplayName();
	
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
