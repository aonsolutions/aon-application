package com.code.aon.web.help.service.drive;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Scanner;

import com.code.aon.web.help.service.drive.GFile.GFileBuilder;
import com.code.aon.web.help.service.drive.exception.GoogleDriveException;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Changes.List;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import net.aonsolutions.aon.google.apis.drive.DriveUtils;
import net.aonsolutions.aon.google.apis.drive.SearchFiles;


public class DriveService {

	public static String BASE_ID = "1nlCD6BVTPk98UIy96pxd5MevesBCmiIN";	
	
	public static Drive connect() throws GoogleDriveException {
		
		final InputStream key = DriveService.class.getResourceAsStream("key.p12");
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
		
		return DriveUtils.getInstace().serviceInitialize(sa);		
		
	}
	

	/**
	 * Get the list of files / directories from parent
	 * @param id - The parent id
	 * @return A list of files
	 */
	public static LinkedList<GFile> ListDirectory(Drive connection, String id) {
		
 		LinkedList<GFile> files = new LinkedList<GFile>();
		FileList list = SearchFiles.searchByParentNotTrashed(connection, id != null? id : "root");
		
		if(list.getFiles() == null) {
			return files;
		}
		
		list.getFiles().forEach(file -> files.add(GFile.from(file)));	
		
		return files;		
	}	
	
	/**
	 * Get the list of files / directories from parent
	 * @param id - The parent id
	 * @return A list of files
	 */
	public static  LinkedList<GFile> ListDirectoryByName(Drive connection, String name) {
		
		LinkedList<GFile> files = new LinkedList<GFile>();
		FileList list = SearchFiles.searchByParentNameNotTrashed(connection, name != null ? name : "root");
		
		if(list.getFiles() == null) {
			return files;
		}
		
		list.getFiles().forEach(file -> files.add(GFile.from(file)));	
		
		return files;		    
	}	
	
	/**
	 * Download a file from google drive by name and parent
	 * @param name The name of the file
	 * @param parent The parent
	 * @return InputStream containing the data
	 */
	public static Optional<InputStream> downloadByNameAndParentNotTrashed(Drive connection, String name, String parent) {
		
		if(name == null) {
			return Optional.empty();
		}
		
		InputStream response = SearchFiles.downloadByNameAndParentAndNotTrashed(connection, name, parent != null ? parent : DriveService.BASE_ID);		
		return Optional.ofNullable(response);
	}
	
	/**
	 * Get a GFile by parent and name
	 * @param parent - The parent ID
	 * @param name - The name of the file
	 * @return optional File or empty 
	 */
	public static Optional<GFile> getFile(Drive connection, String parent, String name) {
		
		if(name == null) {
			return Optional.empty();
		}
		
		
		final FileList files = SearchFiles.searchByNameAndParentNotThrashed(connection, name, parent != null? parent: BASE_ID);		
		if(files.size() == 0) {
			return Optional.empty();
		}
		
		final File firstEntry = (File) files.getFiles().get(0);
		return Optional.ofNullable(GFile.from(firstEntry));
	}
	
	/**
	 * Get a file by ID from drive
	 * @param id The file ID
	 * @return Optional File or empty
	 */
	public static Optional<GFile> getFileById(Drive connection, String id){
		
		if(id == null)
			return Optional.empty();
		
		Optional<GFile> file = Optional.empty();
		final GFile gfile = GFile.from(SearchFiles.searchFile(connection, id));
		file = Optional.ofNullable(gfile);

		
		return file;
	}
	
	
	public static void main(String[] args) throws IOException {
		System.out.println(DriveService.class.getResourceAsStream("key.p12").available());
	}
	
}
