package com.code.aon.aio.service.drive;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Scanner;

import com.code.aon.aio.service.drive.GFile.GFileBuilder;
import com.code.aon.aio.service.drive.exception.GoogleDriveException;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Changes.List;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import net.aonsolutions.aon.google.apis.drive.DriveUtils;
import net.aonsolutions.aon.google.apis.drive.SearchFiles;


public class DriveService {

	private Drive drive;
	
	public DriveService() throws GoogleDriveException{
		
		final InputStream key = DriveService.class.getResourceAsStream("/drive/key.p12");
		final String account = "aula@aonsolutions.info";
		final String id = "103959628161447674997";
		
		if(key == null) {
			throw new GoogleDriveException("Key not found.");
		}
		
		byte[] bytes = new byte[0];
		
		try {
			bytes = key.readAllBytes();
		} catch (IOException e1) {
			throw new GoogleDriveException("Key has invalid format.");
		}
				
		DomainGserviceaccount sa = new DomainGserviceaccount();
		sa.setPrivateKey(bytes);
		sa.setGoogleAccount(account);
		sa.setEmailAddress(id); 
		
		this.drive = DriveUtils.getInstace().serviceInitialize(sa);		
		
	}	
	

	/**
	 * Get the list of files / directories from parent
	 * @param id - The parent id
	 * @return A list of files
	 */
	public LinkedList<GFile> ListDirectory(Optional<String> id) {
		
		LinkedList<GFile> files = new LinkedList<GFile>();
		FileList list = SearchFiles.searchByParentNotTrashed(drive, id.orElse("root"));
		
		list.getFiles().forEach(file -> {
			files.add(GFile.from(file));
			System.out.println(file.getParents());
		});	
		return files;		
	}	
	
	
	public GFile getFile(Optional<String> parent, Optional<String> name) {
		
		final FileList files = SearchFiles.searchFilesTitleAndParent(drive, parent.orElse(""), parent.orElse("1nlCD6BVTPk98UIy96pxd5MevesBCmiIN"));		
	
		if(files == null)
			return null;
		
		// TODO Something is wrong with the API call,
		// we need to figure this out soon.
		System.out.println("FILES SEARCHED : " + files.getFiles().size());
		
		// Nothing showing here
		final File f = (File) files.get(0);
		System.out.println(files);		
		
		//Empty return (temporary)
		return new GFileBuilder().setId("").setDownloadUrl("").setPreviewUrl("").build();
	}
	
}
