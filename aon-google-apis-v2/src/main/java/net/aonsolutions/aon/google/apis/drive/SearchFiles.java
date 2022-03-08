package net.aonsolutions.aon.google.apis.drive;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class SearchFiles {

	
	public static FileList searchFilesProperties(final Drive drive, final String key, final String property) throws IOException {
        return (FileList) drive.files().list().setQ("properties has {key='" + key + "' and value='" + property + "' and visibility='PRIVATE'}").execute();
    }
	
	public static FileList searchFilesAppProperties(final Drive drive, final String key, final String property){
		FileList fileList = new FileList();
		try {
			fileList = drive.files().list().setQ("appProperties has {key='" + key + "' and value='" + property + "'}").execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileList;
    }
	
	public static FileList searchFilesProperties(final Drive drive, final String[] key, final String[] property) {
		String q = "";
        for(Integer i = 0; i < key.length; i++){
        	if(i > 0) q = q + " and ";
        	q = q +  "properties has {key='" + key[i] + "' and value='" + property[i] + "' and visibility='PRIVATE'}";
        }
        FileList fileList = new FileList();
        try {
        	fileList = (FileList) drive.files().list().setQ(q).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileList;
    }
	
	public static FileList searchFilesAppProperties(final Drive drive, final String[] key, final String[] property) {
		String q = "";
        for(Integer i = 0; i < key.length; i++){
        	if(i > 0) q = q + " and ";
        	q = q +  "appProperties has {key='" + key[i] + "' and value='" + property[i] + "'}";
        }
        FileList fileList = new FileList();
        try {
        	fileList = (FileList) drive.files().list().setQ(q).setFields("*").execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileList;
    }
	
	public static FileList searchFilesFulltext(Drive drive, String searcher) throws IOException{
		FileList fl = drive.files().list().setQ("fullText contains '"+searcher+"'").execute();
		return fl;
	}
	
	public static FileList searchFilesTitle(Drive drive, String searcher) throws IOException{
		FileList fl = drive.files().list().setQ("name contains '"+searcher+"'").execute();
		return fl;
	}
	
	public static FileList searchFilesTitleEqual(Drive drive, String searcher) throws IOException{
		FileList fl = drive.files().list().setQ("name = '"+searcher+"'").execute();
		return fl;
	}
	
	public static FileList searchFilesTitleEqualAndMimetype(Drive drive, String searcher) {
		FileList fl = new FileList();
		try {
			fl = drive.files().list().setQ("name = '"+searcher+"' and mimeType = 'application/vnd.google-apps.folder'").execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fl;
	}
	
	public static FileList searchFilesTitleAndParent(Drive drive, String searcher, String parent){
		FileList fl = new FileList();
		try {
			fl = drive.files().list().setQ("'"+parent+"' in parents and name = '"+searcher+"'").execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fl;
	}
	
	public static FileList searchByParent(Drive drive, String parent){
		FileList fl = new FileList();
		try {
			fl = drive.files().list().setQ("'"+parent+"' in parents").execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fl;		
	}

	/**
	 * Search files and folders by parent that aren't deleted.
	 * @param drive - drive object
	 * @param parent - parent ID
	 * @return a FileList with the files and folders given by the API
	 */
	public static FileList searchByParentNotTrashed(Drive drive, String parent){
		FileList fl = new FileList();
		try {
			fl = drive.files()
					.list()
					.setQ("'"+parent+"' in parents and trashed=false")
					.setFields("files(parents, id, name, webContentLink, webViewLink)")
					.execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fl;		
	}
	
	/**
	 * Seach files and folders by parent and name that aren't deleted
	 * @param drive
	 * @param name
	 * @param parent
	 * @return
	 */
	public static FileList searchByNameAndParentNotThrashed(Drive drive, String name, String parent) {
		
		FileList fl = new FileList();
		try {
			fl = drive.files()
					.list()
					.setQ("'"+parent+"' in parents and trashed=false and name='" + name + "'")
					.setFields("files(parents, id, name, webContentLink, webViewLink)")
					.execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fl;		
	}
	
	/**
	 * Download a file by name and parent that is not deleted.
	 * @param drive - The drive object 
	 * @param name - The name of the file
	 * @param parent - The parent ID
	 * @return an inputStream with the downloaded data.
	 */
	public static InputStream downloadByNameAndParentAndNotTrashed(Drive drive, String name, String parent) {
		
		final FileList list = searchByNameAndParentNotThrashed(drive, name, parent);
		System.out.println("NAME REQUESTED: " + name);
		System.out.println("PARENT REQUESTED: " + parent);
		System.out.println("FILES MATCHING: " + list);
		if(list.getFiles().size() == 0) {
			return null;
		}
		
		final File firstEntry = list.getFiles().get(0);
		if(firstEntry.getId() == null) {
			return null;
		}
		
		InputStream response = downloadById(drive, firstEntry.getId());
		return response;		
				
	}
	
	
	/**
	 * Download a file by ID
	 * @param drive - The drive object
	 * @param id - The file ID 
	 * @return InputStream containing the downloaded data.
	 */
	public static InputStream downloadById(Drive drive, String id) {

		InputStream response = new ByteArrayInputStream(new byte[0]);
		try {
			response = drive.files()
					.get(id)
					.executeMediaAsInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return response;		
	}
	
	
	public static FileList searchFilesMimetypeAndTitle(Drive drive, String searcher1, String searcher2) throws IOException{
		FileList fl = drive.files().list().setQ("mimetype = '"+searcher1+"' and name = '"+searcher2+"'").execute();
		System.out.println(fl);
		return fl;
	}

	public static FileList searchFilesMimetype(Drive drive, String searcher) throws IOException{
		FileList fl = drive.files().list().setQ("mimetype contains '"+searcher+"'").execute();
		return fl;
	}

	public static FileList searchFilesAll(Drive drive) throws IOException{
		FileList fl = drive.files().list().execute();
		return fl;
	}

	public static File searchFile(Drive drive, String id) throws IOException{
		return drive.files().get(id).execute();
	}
}
