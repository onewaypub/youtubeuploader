package org.gneisenau.youtube.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.dozer.DozerBeanMapper;
import org.gneisenau.youtube.model.PrivacySetting;
import org.gneisenau.youtube.model.Video;
import org.gneisenau.youtube.model.VideoRepository;
import org.gneisenau.youtube.security.SecurityUtil;
import org.gneisenau.youtube.to.VideoTO;
import org.gneisenau.youtube.utils.IOService;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UploadController {

	private static final String[] dateTimePatterns = { "dd.MM.yyyy hh:mm" };
	private static final String[] datePatterns = { "dd.MM.yyyy" };
	private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
	@Autowired
	private SecurityUtil secUtil;
	// @Autowired
	// private ConnectionRepository connectionRepository;

	@Autowired
	private DozerBeanMapper dozerBeanMapper;
	@Autowired
	private WebsocketController websocketController;
	
	@Autowired
	private VideoRepository videoDAO;
	@Autowired
	private IOService ioUtils;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView("index");
		return model;
	}

	@RequestMapping(value = "/videos", method = RequestMethod.GET, produces = { "application/json; charset=utf-8" })
	public @ResponseBody List<VideoTO> getVideos() {
		List<Video> list = videoDAO.findAll();
		List<VideoTO> videos = new ArrayList<VideoTO>();
		for (Video v : list) {
			videos.add(dozerBeanMapper.map(v, VideoTO.class));
		}
		return videos;
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public ResponseEntity<String> uploadFile(MultipartHttpServletRequest request) {
		try {
			Iterator<String> itr = request.getFileNames();
			while (itr.hasNext()) {
				String uploadedFile = itr.next();
				MultipartFile file = request.getFile(uploadedFile);
				String filename = file.getOriginalFilename();
				//ioUtils.writeMultipart2File(file, filename);
			}
		} catch (Exception e) {
			return new ResponseEntity<>("{}", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		Video v = new Video();
		v.setTitle("test");
		v.setDescription("test");
		websocketController.sendNewVideo(v);
		return new ResponseEntity<>("{}", HttpStatus.OK);
	}

	@RequestMapping(value = "/getThumbnailImage/{id}")
	public void getUserImage(HttpServletResponse response, @PathVariable("id") long id) throws IOException {
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

	@RequestMapping(value = "/getVideo/{id}")
	public void getVideo(HttpServletResponse response, @PathVariable("id") long id) throws IOException {
		Video v = videoDAO.findById(id);
		File f = new File(v.getVideo());
		InputStream video = new FileInputStream(f);
		response.setHeader("Content-Type", "video/*");
		response.setHeader("X-Content-Type-Options", "nosniff");
		response.setHeader("Accept-Ranges", "bytes");

		response.setContentLength((int) f.length());
		org.apache.commons.io.IOUtils.copy(video, response.getOutputStream());
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public String deleteVideo(@RequestParam(name = "id") String id) {
		videoDAO.delete(Long.valueOf(id));
		return "redirect:/list.do";
	}

}
