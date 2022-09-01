package com.code.aon.web.help.service.drive;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Optional;

import com.code.aon.web.help.service.drive.exception.GoogleDriveException;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import net.aonsolutions.aon.google.apis.drive.DriveUtils;
import net.aonsolutions.aon.google.apis.drive.SearchFiles;

public class DriveService {

	public static String BASE_ID = /*"1nlCD6BVTPk98UIy96pxd5MevesBCmiIN"*/"1Z_DmemsSagq0r5WE3cLDMz4HdHzfofFm";
	public static String EDGE_ID = "1HYkbKveuq7mr27-2OmvVlQy2tA6mac2i";

	public static Drive connect() throws GoogleDriveException {

		final InputStream key = DriveService.class.getResourceAsStream("key.p12");
		final String account = "aula@aonsolutions.info";
		final String id = "103959628161447674997";

		if (key == null) {
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
	 * 
	 * @param id - The parent id
	 * @return A list of files
	 */
	public static LinkedList<GFile> ListDirectory(Drive connection, String id) {

		LinkedList<GFile> files = new LinkedList<GFile>();
		FileList list = SearchFiles.searchByParentNotTrashed(connection, id != null ? id : "root");

		if (list.getFiles() == null) {
			return files;
		}

		list.getFiles().forEach(file -> files.add(GFile.from(file)));

		// TO DO clean this
		LinkedList<GFile> orderedFiles = new LinkedList<GFile>();
		files.forEach(f -> {
			if (f.isPDF())
				orderedFiles.add(f);
		});

		files.forEach(f -> {
			if (f.isVideo())
				orderedFiles.add(f);
		});

		files.forEach(f -> {
			if (f.isFolder())
				orderedFiles.add(f);
		});

		return orderedFiles;
	}

	/**
	 * Get the list of files / directories from parent
	 * 
	 * @param id - The parent id
	 * @return A list of files
	 */
	public static LinkedList<GFile> ListFaqs(Drive connection, String id) {

		LinkedList<GFile> files = new LinkedList<GFile>();
		FileList list = SearchFiles.searchByNameAndParentNotThrashed(connection, "FAQs", id != null ? id : "root");

		if (list.getFiles() == null || list.getFiles().size() == 0) {
			return files;
		}

		String folder = list.getFiles().get(0).getId();
		FileList contents = SearchFiles.searchByParentNotTrashed(connection, folder);
		contents.getFiles().forEach(file -> files.add(GFile.from(file)));

		return files;
	}

	/**
	 * Get the list of files / directories from parent
	 * 
	 * @param id - The parent id
	 * @return A list of files
	 */
	public static LinkedList<GFile> ListDirectoryByName(Drive connection, String name) {

		LinkedList<GFile> files = new LinkedList<GFile>();
		FileList list = SearchFiles.searchByParentNameNotTrashed(connection, name != null ? name : "root");

		if (list.getFiles() == null) {
			return files;
		}

		list.getFiles().forEach(file -> files.add(GFile.from(file)));

		return files;
	}

	/**
	 * Download a file from google drive by name and parent
	 * 
	 * @param name   The name of the file
	 * @param parent The parent
	 * @return InputStream containing the data
	 */
	public static Optional<InputStream> downloadByNameAndParentNotTrashed(Drive connection, String name,
			String parent) {

		if (name == null) {
			return Optional.empty();
		}

		InputStream response = SearchFiles.downloadByNameAndParentAndNotTrashed(connection, name,
				parent != null ? parent : DriveService.BASE_ID);
		return Optional.ofNullable(response);
	}

	/**
	 * Get a GFile by parent and name
	 * 
	 * @param parent - The parent ID
	 * @param name   - The name of the file
	 * @return optional File or empty
	 */
	public static Optional<GFile> getFile(Drive connection, String parent, String name) {

		if (name == null) {
			return Optional.empty();
		}

		final FileList files = SearchFiles.searchByNameAndParentNotThrashed(connection, name,
				parent != null ? parent : BASE_ID);
		if (files == null || files.size() == 0) {
			return Optional.empty();
		}

		final File firstEntry = (File) files.getFiles().get(0);
		return Optional.ofNullable(GFile.from(firstEntry));
	}

	/**
	 * Get a file by ID from drive
	 * 
	 * @param id The file ID
	 * @return Optional File or empty
	 */
	public static Optional<GFile> getFileById(Drive connection, String id) {

		if (id == null)
			return Optional.empty();

		Optional<GFile> file = Optional.empty();
		final GFile gfile = GFile.from(SearchFiles.searchFile(connection, id));
		file = Optional.ofNullable(gfile);

		return file;
	}

	public static LinkedList<GFile> searchMatching(Drive connection, String name) {

		LinkedList<GFile> results = new LinkedList<GFile>();

		if (name == null) {
			return new LinkedList<GFile>();
		}

		FileList fl = searchMatchingNameFrom(connection, name, BASE_ID);
		if (fl.getFiles() == null || fl.getFiles().size() == 0) {
			return results;
		}

		fl.getFiles().forEach(file -> {
			results.add(GFile.from(file));
		});

		return results;
	}

	/**
	 * -----------------------------------------------------------------
	 *  SMART SEARCH
	 * -----------------------------------------------------------------
	 */
	public static FileList getFolders(Drive drive) {
			
		FileList fl = new FileList();
		try {
			 fl = drive.files()
					 .list()
					 .setQ("mimeType='application/vnd.google-apps.folder' and trashed=false")
					 .setFields("files(parents, id, name)")
					 .execute();						
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		return fl;
	}

	/**
	 * Search
	 * @param drive
	 * @param searcher
	 * @param id
	 * @param list
	 * @return
	 */
	public static LinkedList<DriveResult> search(Drive drive, String searcher, String id, LinkedList<DriveResult> list) {
	
			// Matching?
			FileList matching = searchMatchingNameFrom(drive, searcher, id); 
			System.out.println("Calling API to match.");

			if(matching.getFiles() != null && matching.getFiles().size()  > 0) {
				matching.getFiles().forEach(m -> {
					
					DriveResult result = new DriveResult();
					result.setText(m.getName());
					list.add(result);
				});
			}
				
			// List folders 
			// For each folder, search();
			FileList dir = SearchFiles.searchByParentNotTrashed(drive, id);
			System.out.println("Calling API to list.");
	
			if(dir.getFiles() != null && dir.getFiles().size()  > 0) {
				dir.getFiles().forEach(item ->  {
					if("application/vnd.google-apps.folder".equals(item.getMimeType()))
					{
						System.out.println(item.getName() + " : " + item.getId());
						search(drive, searcher, item.getId(), list);
					}
				});
			}
			
		return list;
	}	
	
	
	/**
	 * Search files matching name from parent (recursive)
	 * @param drive The drive connection
	 * @param parent The folder to search in
	 * @param searcher The text to search for
	 * @param directory The directory to search in
	 * @return 
	 */
	public static FileList searchMatchingNameFrom(Drive drive, String searcher, String parent) {
		FileList fl = new FileList();
		try {
			System.out.println("[SearchFiles] Searching containing '" + searcher + "' in name. Parent: " + parent );
			fl = drive.files().list().setQ("'" + parent + "' in parents and name contains '" + searcher + "'").execute();			
		} catch (IOException e) {
			//e.printStackTrace();
			System.out.println("[SearchFiles] Found 0 files.");
		}
		
		return fl;
	}
	
	
	
	/**
	 * Main for testing
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			
			final String LABORAL_ID = "1eu_q6nnoiJk3r4fjJH0aCDJIg0UiodhX";
			final String GESTION_ID = "18gvAogUtWHepFo4P4cLAXE1G733sey";
			final String CONFIG_ID = "17-Lj464oga01HUQAhHf_3jW8V_3__zXZ";
			final String CONTA_ID = "1XA7CYnRLJ5uhkJCy-QqBGEm-o4f3qY1V";
			
			LinkedList<DriveResult> file = search(DriveService.connect(), "LAB.", EDGE_ID ,new LinkedList<DriveResult>());

			;
			System.out.println("\n SEARCH RESULTS :                              ");
			System.out.println("-----------------------------------------------");
			file.forEach(f -> System.out.println(f));
		} catch (GoogleDriveException e) {
			e.printStackTrace();
		}

	}

}
