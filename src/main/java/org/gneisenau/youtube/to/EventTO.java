package org.gneisenau.youtube.to;

import org.springframework.context.ApplicationEvent;

public class EventTO {

	private Object o;
	private String typ;

	public EventTO(Object o, ApplicationEvent event) {
		super();
		this.o = o;
		this.typ = event.getClass().getSimpleName();
	}

	public Object getO() {
		return o;
	}

	public String getTyp() {
		return typ;
	}

}
