package com.code.aon.aio.controller;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Optional;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.code.aon.aio.service.drive.DriveService;
import com.code.aon.aio.service.drive.GFile;
import com.code.aon.aio.service.drive.MimeTypes;
import com.code.aon.aio.service.drive.exception.GoogleDriveException;


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
					System.out.println("Name: " + file.getName());
					System.out.println("NEW name: " + breadcrumb.get(id).getName());
					break;
				}
				
				newBreadcrumb.put(id, breadcrumb.get(id));
				System.out.println("Loading breadcrumb: " + breadcrumb.get(id).getName());				
			}
			
			
			this.breadcrumb = newBreadcrumb;	
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
	
	/**
	 * Get the servlet url for the current file
	 * @return
	 */
	public String getFileServletUrl() {
				
		if(this.file.getParents() == null && this.file.getParents().size() < 1)
			return null;
		
		if(this.file.isPDF()) {
			StringBuilder url = new StringBuilder();
			url.append("../DriveServlet");
			url.append("?action=pdf");
			url.append("&name=").append(file.getName());
			url.append("&parent=").append(file.getParents().get(0));
			
			
			System.out.println(url);
			try {
				return Base64.getEncoder().encodeToString(url.toString().getBytes("utf-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		if(this.file.isVideo()) {
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
