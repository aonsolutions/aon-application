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
	public static String BASE_ID = "1nlCD6BVTPk98UIy96pxd5MevesBCmiIN";
	
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
		});	
		return files;		
	}	
	
	/**
	 * Get the list of files / directories from parent
	 * @param id - The parent id
	 * @return A list of files
	 */
	public LinkedList<GFile> ListDirectoryByName(Optional<String> name) {
		
		LinkedList<GFile> files = new LinkedList<GFile>();
		FileList list = SearchFiles.searchByParentNameNotTrashed(drive, name.orElse("root"));
		
		list.getFiles().forEach(file -> {
			files.add(GFile.from(file));
		});	
		return files;		    
	}	
	
	/**
	 * Download a file from google drive by name and parent
	 * @param name The name of the file
	 * @param parent The parent
	 * @return InputStream containing the data
	 */
	public Optional<InputStream> downloadByNameAndParentNotTrashed(Optional<String> name, Optional<String> parent) {
		InputStream response = SearchFiles.downloadByNameAndParentAndNotTrashed(drive, name.orElse(""), parent.orElse(BASE_ID));		
		return Optional.ofNullable(response);
	}
	
	/**
	 * Get a GFile by parent and name
	 * @param parent - The parent ID
	 * @param name - The name of the file
	 * @return optional File or empty 
	 */
	public Optional<GFile> getFile(Optional<String> parent, Optional<String> name) {
		
		final FileList files = SearchFiles.searchByNameAndParentNotThrashed(drive, name.orElse(""), parent.orElse(BASE_ID));		
		if(files.size() == 0) {
			return Optional.empty();
		}
		
		final File firstEntry = (File) files.getFiles().get(0);
		return Optional.ofNullable(GFile.from(firstEntry));
	}
	
}
