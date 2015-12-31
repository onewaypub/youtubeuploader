package org.gneisenau.youtube.to;

public class ValueTO {

	private String id;
	private String value;

	public ValueTO(String key, String value2) {
		id = key;
		value = value2;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
