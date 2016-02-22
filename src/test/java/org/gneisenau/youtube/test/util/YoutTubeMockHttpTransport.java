package org.gneisenau.youtube.test.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpMethod;

import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.json.Json;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;

public class YoutTubeMockHttpTransport extends MockHttpTransport {

	private Map<String, MockLowLevelHttpResponse> responses = new HashMap<String, MockLowLevelHttpResponse>();

	@Override
	public LowLevelHttpRequest buildRequest(String method, String url) throws IOException {
		return new MockLowLevelHttpRequest() {
			@Override
			public LowLevelHttpResponse execute() throws IOException {
				MockLowLevelHttpResponse mockLowLevelHttpResponse = responses.get(method + url);
				if(mockLowLevelHttpResponse == null){
					System.err.println("Cannot find response for method " + method + " and url " + url);
				}
				return mockLowLevelHttpResponse;
			}
		};
	}

	public void addResponse(HttpMethod method, int httpstatus, String url, String res) {
		MockLowLevelHttpResponse response = new MockLowLevelHttpResponse();
		response.setStatusCode(httpstatus);
		response.setContentType(Json.MEDIA_TYPE);
		response.setContent(res);
		response.addHeader("Location", url);
		responses.put(method.name() + url, response);
	}
}
