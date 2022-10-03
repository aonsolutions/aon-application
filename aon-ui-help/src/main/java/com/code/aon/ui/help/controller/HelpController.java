package com.code.aon.ui.help.controller;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;

import com.code.aon.common.domain.DomainManager;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.web.help.service.drive.DriveService;
import com.code.aon.web.help.service.drive.GFile;
import com.code.aon.web.help.service.drive.MimeTypes;
import com.code.aon.web.help.service.drive.exception.GoogleDriveException;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.news.News;
import com.esferalia.aon.occam.api.model.news.NewsType;
import com.esferalia.aon.occam.api.model.registry.Category;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.api.services.drive.Drive;



public class HelpController implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final String HOME_ID = "1Z_DmemsSagq0r5WE3cLDMz4HdHzfofFm";
	private GFile file;
	private GFile homeFile;
	private Drive drive;
	private Collection<GFile> content;
	private Collection<GFile> indexContent;
	private Collection<GFile> faqs;
	private LinkedHashMap<String,GFile> breadcrumb;
	
	private boolean notificationsEnabled;
	private boolean linksEnabled;
	
	private String currentNewsCategory;
	public String getCurrentNewsCategory() {
		return currentNewsCategory;
	}


	public void setCurrentNewsCategory(String currentNewsCategory) {
		this.currentNewsCategory = currentNewsCategory;
	}


	public HelpController() {
		try {
			this.drive = DriveService.connect();
			this.file = new GFile.GFileBuilder()
					.setId(HOME_ID)
					.setType(MimeTypes.FOLDER)
					.setName("Inicio")
					.build();
			this.homeFile = new GFile.GFileBuilder()
					.setId(HOME_ID)
					.setType(MimeTypes.FOLDER)
					.setName("Inicio")
					.build();
			
			
			this.breadcrumb = new LinkedHashMap<>();
			this.breadcrumb.put(this.file.getId(), this.file);
			
			this.currentNewsCategory = null;
			
		} catch (GoogleDriveException e) {
			e.printStackTrace();
		} 
	}
	
	
	/**
	 * reset the breadcrumb
	 */
	private void resetBreadcrumb() {
		final GFile file = new GFile.GFileBuilder()
				.setId(HOME_ID)
				.setType(MimeTypes.FOLDER)
				.setName("Inicio")
				.build();
		
		this.breadcrumb = new LinkedHashMap<String,GFile>();
		this.breadcrumb.put(file.getId(), file);
	}
	
	
	/**
	 * Get the current content 
	 * @return The Collection of files
	 */
	public Collection<GFile> getContent() {
		if (content == null)
			content = DriveService.ListDirectory(drive,file.getId());
		return content;
	}
	
	public Collection<GFile> getIndexContent() {
		if (indexContent == null)
			indexContent = DriveService.ListDirectory(drive,homeFile.getId());
		return indexContent;
	}
	
	/**
	 * Get the current content 
	 * @return The Collection of files
	 */
	public Collection<GFile> getCurrentFaqs() {
		if (faqs == null)
			faqs = DriveService.ListFaqs(drive,file.getId());
		return faqs;
	}
	
	/**
	 * Set the current file
	 * @param file The file to assign
	 */
	public void setFile(GFile file) {
		
		
		if(this.breadcrumb.get(file.getId()) != null) {	
			
			LinkedHashMap<String,GFile> newBreadcrumb = new LinkedHashMap<String, GFile>();
			for (String id : breadcrumb.keySet()) {
				if(id.equals(file.getId())) {
					break;
				}
				
				newBreadcrumb.put(id, breadcrumb.get(id));			
			}
			this.breadcrumb = newBreadcrumb;	
		}
		
		if(file.getName() == null) {
			final Optional<GFile> temp = DriveService.getFileById(drive, file.getId());
			
			if(temp.isPresent() && temp.get().getName() != null) {
				file.setName(temp.get().getName());
			}	
		}
		
		this.breadcrumb.put(file.getId(),file);
		this.file = file;
		this.content = null;
		this.faqs = null;
	}	
	
	
	/**
	 * Get the current file
	 * @return The current file
	 */
	public GFile getFile() {
		return this.file;
	}
	
	public GFile getHomeFile() {
		return this.homeFile;
	}
	
	/*
	 * Set a folder by id
	 */
	public void setFolderById(String id) {
		this.setFile(
			new GFile.GFileBuilder()
			.setType(MimeTypes.FOLDER)
			.setId(id)
			.build()
		);
	}
	
	/**
	 * Set folder by id reseting the breadcrumb
	 * @param file
	 */
	public void setRootFolder(String id) {
		this.resetBreadcrumb();
		this.setFolderById(id);
	}
	
	/**
	 * Get the servlet url for the current file
	 * @return
	 */
	public String getFileServletUrl(GFile file) {
				
		if(file.getParents() == null && file.getParents().size() < 1)
			return null;
		
		if(file.isPDF()) {
			StringBuilder url = new StringBuilder();			
			url.append("../../DriveServlet");
			url.append("?action=pdf");
			url.append("&name=").append(file.getName());
			url.append("&parent=").append(file.getParents().get(0));
		
			
			try {
				return Base64.getEncoder().encodeToString(url.toString().getBytes("utf-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

		}
		
		if(file.isVideo()) {
			StringBuilder url = new StringBuilder();
			
			
			url.append("https://drive.google.com/file/d/");
			url.append(file.getId());
			url.append("/preview");
			
			/*
			url.append("../../DriveServlet");
			url.append("?action=video");
			url.append("&name=").append(file.getName());
			url.append("&parent=").append(file.getParents().get(0));
			*/
			try {
				return Base64.getEncoder().encodeToString(url.toString().getBytes("utf-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		
		return null;
	}
	
	/**
	 * Get the breadcrumb list of files
	 * @return The list of files
	 */
	public Collection<GFile> getBreadcrumb() {
		return this.breadcrumb.values();
	}	

	
	
	public void setFileByName(String name) {
		this.file = DriveService.getFile(drive,null, name).orElseThrow();
	}
	
	
	public void home() {
		this.setFile(new GFile.GFileBuilder()
					.setId(HOME_ID)
					.setType(MimeTypes.FOLDER)
					.setName("Inicio")
					.build());
		this.resetBreadcrumb();
	}
	

	public boolean isAonModule(GFile file) {
		return 	isPayroll(file) 	||
				isAccounting(file) 	||
				isConfig(file) 		||
				isManagement(file)	||
				isFiscal(file)		||
				isConecta(file);
		
	}

	public boolean isHome() {
		return HOME_ID.equals(this.file.getId());
	}
	
	public boolean isPayroll(GFile file) {
		return "1Igr_77rr28FLnvdY9Df4tL4lgTMMRTfB".equals(file.getId());
	}
	
	public boolean isConfig(GFile file) {
		return "1HVUkoicxcxm3jl-Pii6rrIvTn2C7uarY".equals(file.getId());
	}
	
	public boolean isFiscal(GFile file) {
		return "1s3CfKvoleCKQWpBcVv5i_NR9oTxF5aqz".equals(file.getId());
	}
	
	public boolean isConecta(GFile file) {
		return "1hW4Woxq3p0Yho-kyloyFYLXL_D0n1bRG".equals(file.getId());
	}

	public boolean isManagement(GFile file) {
		return "1PDfcIi3dVtc-NKSlPEGQU7h4PgNkqyUw".equals(file.getId());
	}
	
	public boolean isAccounting(GFile file) {
		return "1l9BlhOiHzAWcgUfcYpdSbvdedNnfXCRc".equals(file.getId());
	}


	public boolean isNotificationsEnabled() {
		return notificationsEnabled;
	}

	public boolean isLinksEnabled() {
		return linksEnabled;
	}


	public void setNotificationsEnabled(boolean notificationsEnabled) {
		this.notificationsEnabled = notificationsEnabled;
	}

	public void setLinksEnabled(boolean linksEnabled) {
		this.linksEnabled = linksEnabled;
	}
	
	public String getNewsCategoryName(News news) {
		if (news != null && news.getCategory() != null) {
			return news.getCategory().getName();
		}
		return "";
	}
	
	//NEWS
	public Map<String, List<News>> getNews() {
		Domain domain = new Domain().setName(AonUtil.getDomainName()).setId(DomainManager.getCurrentDomain());
		User user = new User().setName(AonUtil.getRemoteUser());
		
		Timestamp now = new Timestamp(new Date().getTime());
		
		NewsDateComparator comparator = new NewsDateComparator();
		
		Map<String, List<News>> newsMap = new LinkedHashMap<>();
		AON_SOLUTIONS
		.getNewsStream(domain,
				user,
				f -> f.getDomainProperty().eq(domain.getId())//SAME DOMAIN
					  .and(f.getCategoryProperty().isNotNull())//CATEGORY NOT NULL
					  .and(f.getTypeProperty().eq(NewsType.COMMUNICATION.value()))//MUST BE OF TYPE 'COMMUNICATION'
					  .and(f.getInitDateProperty().isNotNull().and(f.getInitDateProperty().le(now)))//INIT DATE NOT NULL AND LOWER THAN TODAY
					  .and(f.getEndDateProperty().isNull().or(f.getEndDateProperty().ge(now)))//END DATE NULL OR HIGHER THAN TODAY
					  
		)
		.sorted(comparator)
		.forEach(news -> {
			Category cat = news.getCategory();
			List<News> newsList = newsMap.getOrDefault(cat.getName(), new LinkedList<>());
			newsList.add(news);
			newsMap.put(cat.getName(), newsList);
		});
		
		return newsMap;
	}
	
	private class NewsDateComparator implements Comparator<News> {

		@Override
		public int compare(News news1, News news2) {
			Date n1 = news1.getInitDate().orElse(null);
			Date n2 = news2.getInitDate().orElse(null);
			if (n1 == n2) {
				return 0;
			}
			else if (n2 == null) {
				return -1;
			}
			else if (n1 == null) {
				return 1;
			}
			return n2.compareTo(n1);
		}
		
	}
	
	public List<News> getNewsByCategory(String category) {
		Map<String, List<News>> allNews = getNews();
		if (allNews != null) {
			if (AonStringUtils.isEmpty(category)) {
				List<News> allNewsList = new LinkedList<>();
				NewsDateComparator comparator = new NewsDateComparator();
				allNews.values().forEach(allNewsList::addAll);
				allNewsList.sort(comparator);
				return allNewsList; 
			}
			
			return allNews.getOrDefault(category, Collections.emptyList());
		}
		return Collections.emptyList();
	}
	
	public String getNewsImgUrl(Integer attachId) {
		Domain domain = new Domain().setName(AonUtil.getDomainName()).setId(DomainManager.getCurrentDomain());
		Attach a = AON.getAttach(domain.getName(), domain.getId(), AonUtil.getRemoteUser(), f -> f.getIdProperty().eq(attachId), AttachType.REGISTRY);
		if(a != null && a.getId() != null && !AonStringUtils.isBlank(domain.getName())) {
			JSONObject data = new JSONObject();
			data.put("domain_name", domain.getName());
			data.put("domain_id", domain.getId());
			data.put(IJsonNames.ID, a.getId());
			data.put("attach_type", AttachType.REGISTRY.getName());
			String result = Base64.getEncoder().encodeToString(data.toString().getBytes(StandardCharsets.UTF_8));
			return "ms/api/file/" +  result;
		}
		return "";
	}
	
	public String getNewsImgStyle(News news) {
		
		if (news.getRattach().isPresent()) {			
			String url = getNewsImgUrl(news.getRattach().orElse(null));
			return !AonStringUtils.isBlank(url) ? "background-image: url(" + url + ");" : "";
		}
		Category cat = news.getCategory();
		if (cat != null && cat.getRattach() != null && cat.getRattach() > 0) {
			String url = getNewsImgUrl(cat.getRattach());
			return !AonStringUtils.isBlank(url) ? "background-image: url(" + url + ");" : "";
		}
		return "";
	}
	
	public static boolean kinouDesuKa(Calendar cal) {
		if (cal == null) {
			return false;
		}
		
		Calendar kinou = Calendar.getInstance();
		kinou.add(Calendar.DAY_OF_YEAR, -1);
		
		return AonDateUtils.isSameDay(kinou, cal);
	}
	
	public String formatDate(News news) {
		Optional<Date> optDate = news.getInitDate();
		if (optDate.isEmpty()) {
			return "";
		}
		Date date = optDate.get();
		
		Calendar currentCalendar = Calendar.getInstance();
		Calendar dateCalendar = Calendar.getInstance();
		dateCalendar.setTime(date);
		
		String tyFormat = "Publicado %s a las %s:%s";
		String airedHour = AonStringUtils.leftPad(AonNumberUtils.toString(dateCalendar.get(Calendar.HOUR_OF_DAY)), 2, '0');
		String airedMinute = AonStringUtils.leftPad(AonNumberUtils.toString(dateCalendar.get(Calendar.MINUTE)), 2, '0');
		if (AonDateUtils.isSameDay(currentCalendar, dateCalendar)) {
			return String.format(tyFormat, "hoy", airedHour, airedMinute);
		} else if (kinouDesuKa(dateCalendar)) {
			return String.format(tyFormat, "ayer", airedHour, airedMinute);
		}
		
		long millisDiff = new Date().getTime() - date.getTime();
		double hoursDiff = millisDiff / (1000*60*60d);
		double daysDiff = hoursDiff / 24;
		double weeksDiff = daysDiff / 7;
		
		
		
		if (weeksDiff >= 1) {
			String simpleDate = AonDateUtils.format(date, AonDateUtils.SIMPLE_DATE_FORMAT);
			return String.format(tyFormat, "el " + simpleDate, airedHour, airedMinute);
		} else if (daysDiff >= 1) {
			String weekDay = AonDateUtils.format(date, "EEEEEEEEEE");
			return String.format(tyFormat, "el " + weekDay, airedHour, airedMinute);
		} else {
			String airedDate = AonDateUtils.format(date, AonDateUtils.SIMPLE_DATE_FORMAT);
			return String.format(tyFormat, "el " + airedDate, airedHour, airedMinute);
		}
		
	}
	
	public String categoryImgClass(News news, String categoryName) {
		if (AonStringUtils.isBlank(getNewsImgStyle(news))) {
			if (AonStringUtils.equalsIgnoreCase("fiscal", categoryName)) {
				return "fiscalImage";
			} else if (AonStringUtils.equalsIgnoreCase("laboral", categoryName)) {
				return "laboralImage";
			} else if (AonStringUtils.equalsIgnoreCase("conecta", categoryName)) {
				return "conectaImage";
			} else if (AonStringUtils.equalsIgnoreCase("contabilidad", categoryName)) {
				return "contabilidadImage";
			} else if (AonStringUtils.equalsIgnoreCase("configuración", categoryName)) {
				return "configImage";
			} else {
				return "otherImage";			
			}
		}
		return "";
	}

}
