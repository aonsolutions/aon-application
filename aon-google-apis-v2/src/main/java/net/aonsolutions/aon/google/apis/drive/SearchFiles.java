package net.aonsolutions.aon.google.apis.drive;

import java.io.IOException;

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
        	fileList = (FileList) drive.files().list().setQ(q).execute();
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
		FileList fl = drive.files().list().setQ("title contains '"+searcher+"'").execute();
		return fl;
	}
	
	public static FileList searchFilesTitleEqual(Drive drive, String searcher) throws IOException{
		FileList fl = drive.files().list().setQ("title = '"+searcher+"'").execute();
		return fl;
	}
	
	public static FileList searchFilesTitleEqualAndMimetype(Drive drive, String searcher) {
		FileList fl = new FileList();
		try {
			fl = drive.files().list().setQ("title = '"+searcher+"' and mimeType = 'application/vnd.google-apps.folder'").execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fl;
	}
	
	public static FileList searchFilesTitleAndParent(Drive drive, String searcher, String parent){
		FileList fl = new FileList();
		try {
			fl = drive.files().list().setQ("'"+parent+"' in parents and title = '"+searcher+"'").execute();
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

	public static FileList searchFilesMimetypeAndTitle(Drive drive, String searcher1, String searcher2) throws IOException{
		
		FileList fl = drive.files().list().setQ("mimetype = '"+searcher1+"' and title = '"+searcher2+"'").execute();
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
