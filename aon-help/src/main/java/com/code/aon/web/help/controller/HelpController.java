package com.code.aon.web.help.controller;

import java.io.IOException;
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
	private GFile file;
	private Drive drive;
	private Collection<GFile> content;
	private LinkedHashMap<String,GFile> breadcrumb;
	
	public HelpController() {
		try {
			this.drive = DriveService.connect();
			this.file = new GFile.GFileBuilder()
					.setId("1nlCD6BVTPk98UIy96pxd5MevesBCmiIN")
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
				.setId("1nlCD6BVTPk98UIy96pxd5MevesBCmiIN")
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
	}	
	
	
	/**
	 * Get the current file
	 * @return The current file
	 */
	public GFile getFile() {
		return this.file;
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
			url.append("../DriveServlet");
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
			url.append("../DriveServlet");
			url.append("?action=video");
			url.append("&name=").append(file.getName());
			url.append("&parent=").append(file.getParents().get(0));
			
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
					.setId("1nlCD6BVTPk98UIy96pxd5MevesBCmiIN")
					.setType(MimeTypes.FOLDER)
					.setName("Inicio")
					.build());
		this.resetBreadcrumb();
	}
	

	public boolean isAonModule(GFile file) {
		return (isPayroll(file) || isAccounting(file) || isConfig(file) || isManagement(file));
		
	}

	public boolean isHome() {
		return "1nlCD6BVTPk98UIy96pxd5MevesBCmiIN".equals(this.file.getId());
	}
	
	public boolean isPayroll(GFile file) {
		return "1p_vxSdxJATMaAm36DxIu8Yrb3DjFdO9l".equals(file.getId());
	}
	
	public boolean isConfig(GFile file) {
		return "1hdAHjI7LPKkUY3qrfK_OoYUHNST0Vu-c".equals(file.getId());
	}

	public boolean isManagement(GFile file) {
		return "1PDfcIi3dVtc-NKSlPEGQU7h4PgNkqyUw".equals(file.getId());
	}
	
	public boolean isAccounting(GFile file) {
		return "1WwyfJ8em7pGt0ordWiMWtoEgAvaanF1X".equals(file.getId());
	}

}
