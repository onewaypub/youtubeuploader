package org.gneisenau.youtube.utils;

import org.dozer.DozerConverter;
import org.gneisenau.youtube.model.State;

public class StateConverter extends DozerConverter<String, State>{
	
	public StateConverter() {
		super(String.class, State.class);	
	}

	@Override
	public String convertFrom(State arg0, String arg1) {
		return arg0.getDisplayName();
	}

	@Override
	public State convertTo(String arg0, State arg1) {
		return State.fromString(arg0);
	}

}
