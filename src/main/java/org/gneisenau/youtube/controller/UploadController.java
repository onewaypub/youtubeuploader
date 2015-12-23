package org.gneisenau.youtube.controller;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
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
import org.dozer.DozerBeanMapper;
import org.gneisenau.youtube.handler.YoutubeHandler;
import org.gneisenau.youtube.model.PrivacySetting;
import org.gneisenau.youtube.model.Video;
import org.gneisenau.youtube.model.VideoRepository;
import org.gneisenau.youtube.security.SecurityUtil;
import org.gneisenau.youtube.to.FileMeta;
import org.gneisenau.youtube.to.VideoTO;
import org.gneisenau.youtube.utils.IOService;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.google.api.client.util.IOUtils;

@Controller
public class UploadController {

	private static final String[] dateTimePatterns = { "dd.MM.yyyy hh:mm" };
	private static final String[] datePatterns = { "dd.MM.yyyy" };
	private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
	@Autowired
	private SecurityUtil secUtil;
//	@Autowired
//	private ConnectionRepository connectionRepository;

	@Autowired
	private DozerBeanMapper dozerBeanMapper;
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

//		Connection<Google> findPrimaryConnection = connectionRepository.findPrimaryConnection(Google.class);
//		Connection<Twitter> findPrimaryConnection2 = connectionRepository.findPrimaryConnection(Twitter.class);

		model.addObject("playlist", playlists);
		Map<String, String> categories;
		categories = youtubeService.getCategories();
		model.addObject("categories", categories);
		model.addObject("random", new Random().nextInt());
		return model;
	}

	LinkedList<FileMeta> files = new LinkedList<FileMeta>();
	FileMeta fileMeta = null;

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody LinkedList<FileMeta> upload(MultipartHttpServletRequest request,
			HttpServletResponse response) {

		// 1. build an iterator
		Iterator<String> itr = request.getFileNames();
		MultipartFile mpf = null;

		// 2. get each file
		while (itr.hasNext()) {

			// 2.1 get next MultipartFile
			mpf = request.getFile(itr.next());
			System.out.println(mpf.getOriginalFilename() + " uploaded! " + files.size());

			// 2.2 if files > 10 remove the first from the list
			if (files.size() >= 10)
				files.pop();

			// 2.3 create new fileMeta
			fileMeta = new FileMeta();
			fileMeta.setFileName(mpf.getOriginalFilename());
			fileMeta.setFileSize(mpf.getSize() / 1024 + " Kb");
			fileMeta.setFileType(mpf.getContentType());

			try {
				String fileName = "D:/TEMP/" + File.separator + UUID.randomUUID().toString() + mpf.getOriginalFilename();
				writeMultipart2File(mpf, fileName);

			} catch (IOException e) {
				logger.error("", e);
			}
			// 2.4 add to files
			files.add(fileMeta);
		}
		return files;
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
			String videoFileName = videofile.getOriginalFilename();
			videoFileName = name + videoFileName;
			writeMultipart2File(videofile, videoFileName);

			String thumbnailFileName = thumbnailfile.getOriginalFilename();
			thumbnailFileName = name + thumbnailFileName;
			writeMultipart2File(thumbnailfile, thumbnailFileName);

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

			Video video = dozerBeanMapper.map(to, Video.class);
			video.setPrivacySetting(privacySetting);
			video.setTags(tags);
			video.setReleaseDate(date);
			videoDAO.persist(video);
		} catch (Exception e) {
			logger.error("",e);
		}
		return "redirect:/list.do";
	}

	private void writeMultipart2File(MultipartFile file, String fileName)
			throws IOException, FileNotFoundException {
		InputStream inputStream = file.getInputStream();
		BufferedOutputStream outputStream = new BufferedOutputStream(
				new FileOutputStream(new File(fileName)));
		IOUtils.copy(inputStream, outputStream);
		outputStream.close();
		inputStream.close();
	}

	private ModelAndView getCurrentVideoList() {
		List<Video> videos = videoDAO.findAll();
		List<VideoTO> videoToList = new ArrayList<VideoTO>();
		for (Video v : videos) {
			videoToList.add(dozerBeanMapper.map(v, VideoTO.class));
		}
		ModelAndView model = new ModelAndView("index");
		model.addObject("videolist", videos);
		return model;
	}

}
