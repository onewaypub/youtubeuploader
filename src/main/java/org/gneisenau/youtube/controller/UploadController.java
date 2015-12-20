package org.gneisenau.youtube.controller;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.gneisenau.youtube.handler.YoutubeHandler;
import org.gneisenau.youtube.model.PrivacySetting;
import org.gneisenau.youtube.model.State;
import org.gneisenau.youtube.model.UploadState;
import org.gneisenau.youtube.model.Video;
import org.gneisenau.youtube.model.VideoRepository;
import org.gneisenau.youtube.security.SecurityUtil;
import org.gneisenau.youtube.to.VideoTO;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.google.api.client.util.IOUtils;

@Controller
public class UploadController {

	private static final String[] dateTimePatterns = { "dd.MM.yyyy hh:mm" };
	private static final String[] datePatterns = { "dd.MM.yyyy" };
	private static final Logger logger = LogManager.getLogger(UploadController.class);
	@Autowired
	private SecurityUtil secUtil;
	@Autowired
	private VideoRepository videoDAO;
	@Autowired
	private YoutubeHandler youtubeService;
	@Autowired
	private IOService ioUtils;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = getCurrentVideoList();
		Map<String, String> playlists = null;
		playlists = youtubeService.getPlaylists(secUtil.getPrincipal());
		model.addObject("playlist", playlists);
		Map<String, String> categories;
		categories = youtubeService.getCategories();
		model.addObject("categories", categories);
		model.addObject("random", new Random().nextInt());
		return model;
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

	@RequestMapping(value = "/modify", method = RequestMethod.POST)
	public String modifyTags(@RequestParam("video") MultipartFile videofile,
			@RequestParam("thumbnail") MultipartFile thumbnailfile, @RequestParam("state") String videostate,
			@RequestParam("tags") String videotags, @RequestParam("timestamp") String timestamp) {
		return "redirect:/list.do";
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String handleFileUpload(@Valid VideoTO to, @RequestPart("video") MultipartFile videofile,
			@RequestPart("thumbnail") MultipartFile thumbnailfile, BindingResult result, Model m) {
		String name = ioUtils.getTemporaryFolder() + File.separator + UUID.randomUUID().toString();
		try {
			InputStream videoInputStream = videofile.getInputStream();
			String videoFileName = videofile.getOriginalFilename();
			videoFileName = name + videoFileName;
			BufferedOutputStream videoOutputStream = new BufferedOutputStream(
					new FileOutputStream(new File(videoFileName)));
			IOUtils.copy(videoInputStream, videoOutputStream);
			videoOutputStream.close();
			videoInputStream.close();

			InputStream thumbnialInputStream = thumbnailfile.getInputStream();
			String thumbnailFileName = thumbnailfile.getOriginalFilename();
			thumbnailFileName = name + thumbnailFileName;
			BufferedOutputStream thumbnailOutputStream = new BufferedOutputStream(
					new FileOutputStream(new File(thumbnailFileName)));
			IOUtils.copy(thumbnialInputStream, thumbnailOutputStream);
			thumbnailOutputStream.close();
			thumbnialInputStream.close();

			PrivacySetting privacySetting = PrivacySetting.Unlisted;
			Date date = DateUtils.parseDate(to.getTimestamp(), dateTimePatterns);
			DateUtils.parseDate(to.getPublished(), datePatterns);

			String[] tagsArray = to.getTags().split(",");
			String[] trimmedArray = new String[tagsArray.length];
			for (int i = 0; i < tagsArray.length; i++) {
				trimmedArray[i] = tagsArray[i].trim();
			}
			List<String> tags = new ArrayList<String>();
			CollectionUtils.addAll(tags, trimmedArray);
			tags.add("Peaches");
			tags.add("PeachesLP");
			tags.add("Let's play");

			Video video = new Video();

			video.setPrivacySetting(privacySetting);
			video.setTags(tags);
			video.setThumbnail(thumbnailFileName);
			video.setVideo(videoFileName);
			video.setReleaseDate(date);
			video.setTitle(to.getTitle());
			video.setPlaylistId(to.getPlaylist());
			video.setDescription(to.getDescription());
			video.setState(State.WaitForProcessing);
			video.setVideoUploadState(UploadState.NOT_STARTED);
			video.setThumbnailUploadState(UploadState.NOT_STARTED);
			video.setPublisher(to.getPublisher());
			video.setPublished(to.getPublished());
			video.setDeveloper(to.getDeveloper());
			video.setGerne(to.getGerne());
			video.setShorttitle(to.getShorttitle());
			video.setCategory(youtubeService.getCategories().get(to.getCategoryId()));
			video.setCategoryId(to.getCategoryId());
			video.setUsername(secUtil.getPrincipal());
			video.setAgeRestricted(to.isAgeRestricted());

			videoDAO.persist(video);
		} catch (Exception e) {
			logger.error(e);
		}
		return "redirect:/list.do";
	}

	@SuppressWarnings("unchecked")
	private ModelAndView getCurrentVideoList() {
		List<Video> videos = videoDAO.findAll();
		videos = new DozerBeanMapper().map(videos, List.class);
		ModelAndView model = new ModelAndView("index");
		model.addObject("videolist", videos);
		return model;
	}

}
