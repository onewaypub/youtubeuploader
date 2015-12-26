package org.gneisenau.youtube.utils;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dozer.DozerConverter;

public class CommaSeperatedStringToListConverter extends DozerConverter<String, List<String>>{
	
	public CommaSeperatedStringToListConverter(Class<String> prototypeA, Class<List<String>> prototypeB) {
		super(prototypeA, prototypeB);
	}

	@Override
	public String convertFrom(List<String> arg0, String arg1) {
		return StringUtils.join(arg0, ',');
	}

	@Override
	public List<String> convertTo(String arg0, List<String> arg1) {
		List<String> items = Arrays.asList(arg0.split("\\s*,\\s*"));
		return items;
	}

}
