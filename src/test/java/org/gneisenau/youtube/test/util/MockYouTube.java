package org.gneisenau.youtube.test.util;

import java.io.IOException;

import org.mockito.Mockito;

import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.Videos.Delete;
import com.google.api.services.youtube.YouTube.Videos.GetRating;
import com.google.api.services.youtube.YouTube.Videos.Insert;
import com.google.api.services.youtube.YouTube.Videos.List;
import com.google.api.services.youtube.YouTube.Videos.Rate;
import com.google.api.services.youtube.YouTube.Videos.Update;
import com.google.api.services.youtube.model.Video;

public class MockYouTube extends YouTube {

	public MockYouTube(HttpTransport transport, JsonFactory jsonFactory,
			HttpRequestInitializer httpRequestInitializer) {
		super(transport, jsonFactory, httpRequestInitializer);
	}

	@Override
	protected void initialize(AbstractGoogleClientRequest<?> httpClientRequest) throws IOException {
		// TODO Auto-generated method stub
		super.initialize(httpClientRequest);
	}

	@Override
	public Activities activities() {
		// TODO Auto-generated method stub
		return super.activities();
	}

	@Override
	public ChannelBanners channelBanners() {
		// TODO Auto-generated method stub
		return super.channelBanners();
	}

	@Override
	public ChannelSections channelSections() {
		// TODO Auto-generated method stub
		return super.channelSections();
	}

	@Override
	public Channels channels() {
		// TODO Auto-generated method stub
		return super.channels();
	}

	@Override
	public GuideCategories guideCategories() {
		// TODO Auto-generated method stub
		return super.guideCategories();
	}

	@Override
	public I18nLanguages i18nLanguages() {
		// TODO Auto-generated method stub
		return super.i18nLanguages();
	}

	@Override
	public I18nRegions i18nRegions() {
		// TODO Auto-generated method stub
		return super.i18nRegions();
	}

	@Override
	public LiveBroadcasts liveBroadcasts() {
		// TODO Auto-generated method stub
		return super.liveBroadcasts();
	}

	@Override
	public LiveStreams liveStreams() {
		// TODO Auto-generated method stub
		return super.liveStreams();
	}

	@Override
	public PlaylistItems playlistItems() {
		// TODO Auto-generated method stub
		return super.playlistItems();
	}

	@Override
	public Playlists playlists() {
		// TODO Auto-generated method stub
		return super.playlists();
	}

	@Override
	public Search search() {
		// TODO Auto-generated method stub
		return super.search();
	}

	@Override
	public Subscriptions subscriptions() {
		// TODO Auto-generated method stub
		return super.subscriptions();
	}

	@Override
	public Thumbnails thumbnails() {
		// TODO Auto-generated method stub
		return super.thumbnails();
	}

	@Override
	public VideoCategories videoCategories() {
		// TODO Auto-generated method stub
		return super.videoCategories();
	}

	@Override
	public Videos videos() {
//		Videos vs = Mockito.mock(Videos.class);
//		Delete d = Mockito.mock(Delete.class);
//		when(d.)
//		when(vs.delete(id))
	}

	@Override
	public Watermarks watermarks() {
		// TODO Auto-generated method stub
		return super.watermarks();
	}

	@Override
	public JsonObjectParser getObjectParser() {
		// TODO Auto-generated method stub
		return super.getObjectParser();
	}

	
}
