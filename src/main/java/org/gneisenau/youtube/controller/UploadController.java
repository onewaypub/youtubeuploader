package org.gneisenau.youtube.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.dozer.DozerBeanMapper;
import org.gneisenau.youtube.handler.YoutubeHandler;
import org.gneisenau.youtube.model.State;
import org.gneisenau.youtube.model.Video;
import org.gneisenau.youtube.model.VideoRepository;
import org.gneisenau.youtube.processor.VideoChain;
import org.gneisenau.youtube.processor.VideoProcessor;
import org.gneisenau.youtube.security.SecurityUtil;
import org.gneisenau.youtube.to.ValueTO;
import org.gneisenau.youtube.to.VideoTO;
import org.gneisenau.youtube.utils.IOService;
import org.imgscalr.Scalr;
import org.mortbay.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Controller
public class UploadController {

	private static final String[] dateTimePatterns = { "dd.MM.yyyy hh:mm" };
	private static final String[] datePatterns = { "dd.MM.yyyy" };
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

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView("index");
		return model;
	}

	@RequestMapping(value = "/categorylist", method = RequestMethod.GET, produces = {
			"application/json; charset=utf-8" })
	public @ResponseBody List<ValueTO> getCategoryList() {
		Map<String, String> cats = youtubeHandler.getCategories();
		List<ValueTO> values = new ArrayList<ValueTO>();
		for (Entry<String, String> entry : cats.entrySet()) {
			values.add(new ValueTO(entry.getKey(), entry.getValue()));
		}
		return values;
	}

	@RequestMapping(value = "/playlist", method = RequestMethod.GET, produces = { "application/json; charset=utf-8" })
	public @ResponseBody List<ValueTO> getPlaylist() {
		Map<String, String> playlist = youtubeHandler.getPlaylists(secUtil.getPrincipal());
		List<ValueTO> values = new ArrayList<ValueTO>();
		for (Entry<String, String> entry : playlist.entrySet()) {
			values.add(new ValueTO(entry.getKey(), entry.getValue()));
		}
		return values;
	}

	@RequestMapping(value = "/videos", method = RequestMethod.GET, produces = { "application/json; charset=utf-8" })
	public @ResponseBody List<VideoTO> getVideos() {
		List<Video> list = videoDAO.findAll();
		List<VideoTO> videos = new ArrayList<VideoTO>();
		for (Video v : list) {
			VideoTO bean = dozerBeanMapper.map(v, VideoTO.class);
			videos.add(bean);
			
		}
		return videos;
	}

	@RequestMapping(value = "/upload/video", method = RequestMethod.POST)
	public ResponseEntity<String> uploadVideoFile(MultipartHttpServletRequest request) {
		try {
			File outputDir = new File(ioUtils.getTemporaryFolder());
			// Create a new file upload handler
			List<File> files = new ArrayList<File>();
			try {
				Map<String, MultipartFile> fileMap = request.getFileMap();
				for (Entry<String, MultipartFile> e : fileMap.entrySet()) {
					String name = e.getValue().getOriginalFilename();
					String title = FilenameUtils.getBaseName(name);
					String path2save = outputDir.getAbsolutePath() + File.separatorChar;
					File newFile = new File(path2save + ioUtils.addMilliSecondsToFilename(name));
					e.getValue().transferTo(newFile);
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
				throw e;
			}

		} catch (Exception e) {
			return new ResponseEntity<>("{}", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<>("{}", HttpStatus.OK);
	}

	@RequestMapping(value = "/getThumbnailImage/{id}", method = RequestMethod.GET)
	public void getThumbnail(HttpServletResponse response, @PathVariable("id") long id) throws IOException {
		response.setContentType("image/jpeg");
		Video v = videoDAO.findById(id);
		File f = new File(v.getThumbnail());
		InputStream thumb = new FileInputStream(f);
		BufferedImage buff = ImageIO.read(thumb);
		buff = Scalr.resize(buff, Scalr.Method.SPEED, Scalr.Mode.FIT_TO_WIDTH, 30, 30, Scalr.OP_ANTIALIAS);
		BufferedImage outPad = Scalr.pad(buff, 10);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(outPad, "jpg", os);
		InputStream is = new ByteArrayInputStream(os.toByteArray());
		org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
	}

	@RequestMapping(value = "/update/video", method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<String> saveVideo(@RequestBody String jSONVideo) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
	    mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
	    VideoTO video = mapper.readValue(jSONVideo, VideoTO.class);
		Long id = video.getId();
		Video v = null;
		if(id != null){
			v = videoDAO.findById(id);
		} 
		if(v == null){
			v = dozerBeanMapper.map(video, Video.class);
		} else {
			dozerBeanMapper.map(video, v, "metadata");
		}
		videoDAO.persist(v);
		return new ResponseEntity<>("{}", HttpStatus.OK);
	}

	@RequestMapping(value = "/getVideo/{id}.mp4", method = RequestMethod.GET)
	public void getVideoStream(HttpServletResponse response, @PathVariable("id") long id) throws IOException {
		Video v = videoDAO.findById(id);
		File f = new File(v.getVideo());
		InputStream video = new FileInputStream(f);
		response.setHeader("Content-Type", "video/*");
		response.setHeader("X-Content-Type-Options", "nosniff");
		response.setHeader("Accept-Ranges", "bytes");

		response.setContentLength((int) f.length());
		org.apache.commons.io.IOUtils.copy(video, response.getOutputStream());
	}

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
	public ResponseEntity<String> deleteVideo(@PathVariable("id") long id) {
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
	}

}
