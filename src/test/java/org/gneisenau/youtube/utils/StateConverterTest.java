package org.gneisenau.youtube.utils;

import static org.junit.Assert.*;

import org.gneisenau.youtube.model.State;
import org.junit.Test;

public class StateConverterTest {

	@Test
	public void test() {
		StateConverter conv = new StateConverter();
		assertEquals(State.OnProcessing.getDisplayName(), conv.convertFrom(State.OnProcessing));
		assertEquals(State.OnProcessing, conv.convertTo(State.OnProcessing.getDisplayName()));
		assertNull(conv.convertFrom(null));
		assertNull(conv.convertTo(null));
		assertNull(conv.convertTo(""));
	}

}
