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


public class HelpController implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private GFile file;
	private DriveService drive;
	private Collection<GFile> content;
	private LinkedHashMap<String,GFile> breadcrumb;
	
	public HelpController() {
		try {
			this.drive = new DriveService();
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
		this.breadcrumb.put(this.file.getId(), this.file);
	}
	
	
	/**
	 * Get the current content 
	 * @return The Collection of files
	 */
	public Collection<GFile> getContent() {
		if (content == null)
			content = drive.ListDirectory(Optional.of(file.getId()));
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
			final GFile temp = drive.getFileById(Optional.of(file.getId())).orElse(null);
			
			if(temp != null && temp.getName() != null) {
				file.setName(temp.getName());
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
		this.file = drive.getFile(Optional.empty(), Optional.of(name)).orElseThrow();
	}

}
