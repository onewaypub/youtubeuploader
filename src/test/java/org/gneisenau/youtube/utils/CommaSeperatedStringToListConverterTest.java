package org.gneisenau.youtube.utils;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class CommaSeperatedStringToListConverterTest {

	@Test
	public void testConvertFromListString() {
		List<String> source = new ArrayList<String>();
		source.add("1");
		source.add("2");
		CommaSeperatedStringToListConverter conv = new CommaSeperatedStringToListConverter();
		assertEquals("1,2", conv.convertFrom(source));
		assertNull(conv.convertFrom(null));
		assertNull(conv.convertFrom(new ArrayList<String>()));

	}

	@Test
	public void testConvertToStringList() {
		List<String> source = new ArrayList<String>();
		source.add("1");
		source.add("2");
		CommaSeperatedStringToListConverter conv = new CommaSeperatedStringToListConverter();
		assertEquals(source, conv.convertTo("1,2"));
		assertNull(conv.convertTo(null));
		assertNull(conv.convertTo(""));
	}

}
