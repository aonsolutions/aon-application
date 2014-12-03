package com.esferalia.aon.gwt.document.client;

import java.util.Vector;

import com.esferalia.aon.gwt.document.shared.Document;
import com.esferalia.aon.gwt.document.shared.FileInfo;
import com.esferalia.aon.gwt.document.shared.FilterUtil;
import com.esferalia.aon.gwt.document.shared.Lists;
import com.esferalia.aon.gwt.document.shared.SearchInfo;
import com.esferalia.aon.gwt.document.shared.TreeDriveInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gwt_document")
public interface IDocument extends RemoteService{

	public Document getAllFiles();

	public Vector<FileInfo> getServiConveniosFiles();
	
	public Vector<FileInfo> searchFile(String searchStr, Vector<FileInfo> files);
	
	//public Vector<FileInfo> searchFile(SearchInfo si, Vector<FileInfo> files);

	public FilterUtil searchFile2(SearchInfo si, Vector<FileInfo> files);
	
	public Lists getLists();
	
	public Vector<String> getSons();
	
	public void removeFile(FileInfo fi);
	
	public void editFile(FileInfo fi);
	
	public Boolean newFile(FileInfo fi);

	public Boolean check();

	public Boolean isGconnection();
	
	public Vector<TreeDriveInfo> myDrive(String id);
	
	public FileInfo insertFile(FileInfo fi);
	
	public void share(String email,String driveId); 
	
	public Vector<FileInfo> eSearchFile(Vector<FileInfo> v,String s);

	public Vector<FileInfo> searchFile(SearchInfo si, Vector<FileInfo> files,
			Vector<FileInfo> allFiles);
	public String getAsHTML(FileInfo doc, int zoom);
	
}
