package com.code.aon.ui.help.controller;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Optional;

import com.code.aon.web.help.service.drive.DriveService;
import com.code.aon.web.help.service.drive.GFile;
import com.code.aon.web.help.service.drive.MimeTypes;
import com.code.aon.web.help.service.drive.exception.GoogleDriveException;
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
			
			
			
			this.breadcrumb = new LinkedHashMap<String,GFile>();
			this.breadcrumb.put(this.file.getId(), this.file);
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

}
