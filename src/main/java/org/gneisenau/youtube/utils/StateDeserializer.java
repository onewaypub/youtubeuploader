package org.gneisenau.youtube.utils;

import java.io.IOException;

import org.gneisenau.youtube.model.State;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class StateDeserializer extends JsonDeserializer<State> {

	@Override
	public State deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		return State.fromString(p.getValueAsString());
	}

}
