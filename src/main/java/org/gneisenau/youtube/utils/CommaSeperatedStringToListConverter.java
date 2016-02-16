package org.gneisenau.youtube.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dozer.DozerConverter;

public class CommaSeperatedStringToListConverter extends DozerConverter<String, List> {

	public CommaSeperatedStringToListConverter() {
		super(String.class, List.class);
	}

	@Override
	public String convertFrom(List arg0, String arg1) {
		if (arg0 == null || arg0.size() == 0) {
			return null;
		}
		return StringUtils.join(arg0, ',');
	}

	@Override
	public List<String> convertTo(String arg0, List arg1) {
		if (StringUtils.isBlank(arg0)) {
			return null;
		}
		List<String> items = Arrays.asList(arg0.split("\\s*,\\s*"));
		return items;
	}

}
