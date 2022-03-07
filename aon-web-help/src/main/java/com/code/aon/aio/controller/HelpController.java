package com.code.aon.aio.controller;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Optional;

import com.code.aon.aio.service.drive.DriveService;
import com.code.aon.aio.service.drive.GFile;
import com.code.aon.aio.service.drive.MimeTypes;
import com.code.aon.aio.service.drive.exception.GoogleDriveException;

public class HelpController implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private DriveService drive;

	
	public HelpController() {
		try {
			this.drive = new DriveService();
		} catch (GoogleDriveException e) {
			e.printStackTrace();
		}
	}
	
	public LinkedList<GFile> getCreta() {
		LinkedList<GFile> files = drive.ListDirectory(Optional.of("1nlCD6BVTPk98UIy96pxd5MevesBCmiIN"));
		return files;
	}

	
	public LinkedList<GFile> getFolder(final String id) {
		return null;
	}
	
	
	/**
	 * ---------------------------------------------------------------------------
	 *   
	 *   BAREBONE HELP PAGE FUNCTIONS
	 *   
	 * ---------------------------------------------------------------------------
	 */
	
	
	public String getPayrollVideo() {
		LinkedList<GFile> files = drive.ListDirectory(Optional.of("1nlCD6BVTPk98UIy96pxd5MevesBCmiIN"));
		return files.get(0).getPreviewUrl();
	}
	
	
	
	
}
