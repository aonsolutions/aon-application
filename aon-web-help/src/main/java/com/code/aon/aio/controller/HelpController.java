package com.code.aon.aio.controller;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Optional;

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
	
	
	public Collection<GFile> getContent() {
		if (content == null)
			content = drive.ListDirectory(Optional.of(file.getId()));
		return content;
	}
	
	public void setFile(GFile file) {
		
		this.breadcrumb.put(file.getId(),file);
		if(this.breadcrumb.get(file.getId()) != null) {
			
			LinkedHashMap<String,GFile> newBreadcrumb = new LinkedHashMap<String, GFile>();
			
			for (String id : breadcrumb.keySet()) {
				
				newBreadcrumb.put(id, breadcrumb.get(id));
				
				if(id == file.getId()) {
					System.out.println("Name: " + file.getName());
					System.out.println("NEW name: " + breadcrumb.get(id).getName());
					break;
				}
				
			}
			
			
			this.breadcrumb = newBreadcrumb;	
		}
		
		
		this.file = file;
		this.content = null;
	}	
	
	public Collection<GFile> getBreadcrumb() {
		return this.breadcrumb.values();
	}


}
