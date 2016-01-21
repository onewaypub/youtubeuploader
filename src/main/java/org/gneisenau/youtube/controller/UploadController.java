package org.gneisenau.youtube.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.dozer.DozerBeanMapper;
import org.gneisenau.youtube.events.ErrorEvent;
import org.gneisenau.youtube.events.VideoUpdateEvent;
import org.gneisenau.youtube.handler.video.exceptions.AuthorizeException;
import org.gneisenau.youtube.handler.youtube.Auth;
import org.gneisenau.youtube.handler.youtube.YoutubeHandler;
import org.gneisenau.youtube.model.State;
import org.gneisenau.youtube.model.Video;
import org.gneisenau.youtube.model.VideoRepository;
import org.gneisenau.youtube.to.ValueTO;
import org.gneisenau.youtube.to.VideoTO;
import org.gneisenau.youtube.utils.IOService;
import org.gneisenau.youtube.utils.SecurityUtil;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Controller
public class UploadController {

	private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
	@Autowired
	private SecurityUtil secUtil;
	@Autowired
	private IOService ioUtils;
	@Autowired
	private YoutubeHandler youtubeHandler;
	@Autowired
	private DozerBeanMapper dozerBeanMapper;
	@Autowired
	private WebsocketEventBus websocketEventBus;
	@Autowired
	private VideoRepository videoDAO;
	@Autowired
	private Auth auth;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView("index");
		return model;
	}

	@RequestMapping(value = "/categorylist", method = RequestMethod.GET, produces = {
			"application/json; charset=utf-8" })
	public @ResponseBody List<ValueTO> getCategoryList() {
		List<ValueTO> values = new ArrayList<ValueTO>();
		try {
			Map<String, String> cats = youtubeHandler.getCategories();
			for (Entry<String, String> entry : cats.entrySet()) {
				values.add(new ValueTO(entry.getKey(), entry.getValue()));
			}
			return values;
		} catch (Exception e) {
			logger.error("Fehler beim Laden der Kategorien", e);
			ErrorEvent event = new ErrorEvent("Fehler beim Laden der Youtube Katagorien", this);
			websocketEventBus.onApplicationEvent(event);
			return values;
		}
	}

	@RequestMapping(value = "/playlist", method = RequestMethod.GET, produces = { "application/json; charset=utf-8" })
	public @ResponseBody List<ValueTO> getPlaylist() {
		List<ValueTO> values = new ArrayList<ValueTO>();
		try {
			Map<String, String> playlist = youtubeHandler.getPlaylists(secUtil.getPrincipal());
			for (Entry<String, String> entry : playlist.entrySet()) {
				values.add(new ValueTO(entry.getKey(), entry.getValue()));
			}
			return values;
		} catch (Exception e) {
			logger.error("Fehler beim Laden der Playlist", e);
			ErrorEvent event = new ErrorEvent("Fehler beim Laden der Youtube Playlist", this);
			websocketEventBus.onApplicationEvent(event);
			return values;
		}
	}

	@RequestMapping(value = "/videos", method = RequestMethod.GET, produces = { "application/json; charset=utf-8" })
	public @ResponseBody List<VideoTO> getVideos() {
		List<VideoTO> videos = new ArrayList<VideoTO>();
		try {
			List<Video> list = videoDAO.findAll();
			for (Video v : list) {
				VideoTO bean = dozerBeanMapper.map(v, VideoTO.class);
				bean.setLocalVideoUrl("getVideo/" + bean.getId() + ".mp4");
				bean.setLocalThumbnailUrl("getThumbnailImage/" + bean.getId());
				videos.add(bean);
			}
			return videos;
		} catch (Exception e) {
			logger.error("Fehler beim Laden der Videos", e);
			ErrorEvent event = new ErrorEvent("Fehler beim Laden der Videos", this);
			websocketEventBus.onApplicationEvent(event);
			return videos;
		}
	}

	@RequestMapping(value = "/upload/video", method = RequestMethod.POST)
	public ResponseEntity<String> uploadVideoFile(MultipartHttpServletRequest request) {
		List<File> files = new ArrayList<File>();
		try {
			File outputDir = new File(ioUtils.getTemporaryFolder());
			// Create a new file upload handler
			Map<String, MultipartFile> fileMap = request.getFileMap();
			for (Entry<String, MultipartFile> e : fileMap.entrySet()) {
				if(ioUtils.canBeSaved(e.getValue().getSize())){
					ErrorEvent event = new ErrorEvent("Video kann nicht gespeichert werden, da die Datei zu groß ist", this);
					websocketEventBus.onApplicationEvent(event);
					break;
				}
				String name = e.getValue().getOriginalFilename();
				String title = FilenameUtils.getBaseName(name);
				String path2save = outputDir.getAbsolutePath() + File.separatorChar;
				File newFile = new File(path2save + ioUtils.addMilliSecondsToFilename(name));
				e.getValue().transferTo(newFile);
				files.add(newFile);
				Video v = new Video();
				v.setTitle(title);
				v.setVideo(newFile.getAbsolutePath());
				v.setState(State.WaitForProcessing);
				v.setUsername(secUtil.getPrincipal());
				videoDAO.persist(v);
				VideoTO to = dozerBeanMapper.map(v, VideoTO.class);
				websocketEventBus.notifyNewVideo(to);
			}
		} catch (IOException e) {
			// Cleanup on exception
			for (File f : files) {
				if (f.exists()) {
					f.delete();
				}
			}
			logger.error("Fehler beim Hochladen der Videos", e);
			ErrorEvent event = new ErrorEvent("Fehler beim Hochladen des Videos", this);
			websocketEventBus.onApplicationEvent(event);
		}
		return new ResponseEntity<>("{}", HttpStatus.OK);
	}
	
	@RequestMapping(value = "/upload/thumbnail/{id}", method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<String> uploadThumbnail(@PathVariable Long id, MultipartHttpServletRequest request) {
		List<File> files = new ArrayList<File>();
		try {
			File outputDir = new File(ioUtils.getTemporaryFolder());
			// Create a new file upload handler
			Map<String, MultipartFile> fileMap = request.getFileMap();
			for (Entry<String, MultipartFile> e : fileMap.entrySet()) {
				String name = e.getValue().getOriginalFilename();
				String path2save = outputDir.getAbsolutePath() + File.separatorChar;
				File newFile = new File(path2save + ioUtils.addMilliSecondsToFilename(name));
				e.getValue().transferTo(newFile);
				Video v = videoDAO.findById(id);
				v.setThumbnail(newFile.getAbsolutePath());
				videoDAO.persist(v);
			}
		} catch (IOException e) {
			// Cleanup on exception
			for (File f : files) {
				if (f.exists()) {
					f.delete();
				}
			}
			logger.error("Fehler beim Hochladen der Thumbnails", e);
			ErrorEvent event = new ErrorEvent("Fehler beim Hochladen des Thumbnails", this);
			websocketEventBus.onApplicationEvent(event);
		}
		return new ResponseEntity<>("{}", HttpStatus.OK);
	}


	@RequestMapping(value = "/getThumbnailImage/{id}", method = RequestMethod.GET)
	public void getThumbnail(HttpServletResponse response, @PathVariable("id") long id) throws IOException {
		try {
			response.setContentType(MediaType.IMAGE_JPEG_VALUE);
			Video v = videoDAO.findById(id);
			InputStream is = null;
			if (StringUtils.isNotBlank(v.getThumbnail())) {
				File f = new File(v.getThumbnail());
				InputStream thumb = new FileInputStream(f);
				BufferedImage buff = ImageIO.read(thumb);
				buff = Scalr.resize(buff, Scalr.Method.SPEED, Scalr.Mode.FIT_TO_WIDTH, 75, 75, Scalr.OP_ANTIALIAS);
				BufferedImage outPad = Scalr.pad(buff, 10);
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				ImageIO.write(outPad, "jpg", os);
				is = new ByteArrayInputStream(os.toByteArray());
			} else {
				is = UploadController.class.getResourceAsStream("/thumbnail_placeholder.jpg");
			}
			org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
		} catch (Exception e) {
			logger.error("Fehler beim Holen des Thumbnails", e);
			ErrorEvent event = new ErrorEvent("Thumbnail konnte nicht geladen werden", this);
			websocketEventBus.onApplicationEvent(event);
		}
	}

	@RequestMapping(value = "/update/video", method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<String> saveVideo(@RequestBody String jSONVideo) throws IOException {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
			VideoTO video = mapper.readValue(jSONVideo, VideoTO.class);
			Long id = video.getId();
			Video v = null;
			if (id != null) {
				v = videoDAO.findById(id);
			}
			if (v == null) {
				v = dozerBeanMapper.map(video, Video.class);
			} else {
				dozerBeanMapper.map(video, v, "metadata");
			}
			videoDAO.persist(v);
			VideoUpdateEvent event = new VideoUpdateEvent(video, this);
			websocketEventBus.onApplicationEvent(event);
			return new ResponseEntity<>("{}", HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Fehler beim Akutalisieren des Videos", e);
			ErrorEvent event = new ErrorEvent("Metadaten des Videos konnten nicht akutalisiert werden", this);
			websocketEventBus.onApplicationEvent(event);
			return new ResponseEntity<>("{}", HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/getVideo/{id}.mp4", method = RequestMethod.GET)
	public void getVideoStream(HttpServletResponse response, @PathVariable("id") long id) throws IOException {
		try {
			Video v = videoDAO.findById(id);
			if (v == null) {
				return;
			}
			File f = new File(v.getVideo());
			InputStream video = new FileInputStream(f);
			response.setHeader("Content-Type", "video/*");
			response.setHeader("X-Content-Type-Options", "nosniff");
			response.setHeader("Accept-Ranges", "bytes");

			response.setContentLength((int) f.length());
			org.apache.commons.io.IOUtils.copy(video, response.getOutputStream());
		} catch (Exception e) {
			logger.error("Fehler beim Holen des Videostreams", e);
			ErrorEvent event = new ErrorEvent("Video-Stream konnte nicht geladen werden", this);
			websocketEventBus.onApplicationEvent(event);
		}
	}

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
	public ResponseEntity<String> deleteVideo(@PathVariable("id") long id) {
		try {
			Video video = videoDAO.findById(Long.valueOf(id));
			VideoTO videoTO = dozerBeanMapper.map(video, VideoTO.class);
			if (video.getVideo() != null) {
				File f = new File(video.getVideo());
				f.delete();
			}
			if (video.getThumbnail() != null) {
				File f = new File(video.getThumbnail());
				f.delete();
			}
			videoDAO.delete(Long.valueOf(id));
			websocketEventBus.notifyDeleteVideo(videoTO);
			return new ResponseEntity<>("{}", HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Fehler beim Löschen des Videos", e);
			ErrorEvent event = new ErrorEvent("Video konnte nicht gelöscht werden", this);
			websocketEventBus.onApplicationEvent(event);
			return new ResponseEntity<>("{}", HttpStatus.OK);
		}
	}

}
