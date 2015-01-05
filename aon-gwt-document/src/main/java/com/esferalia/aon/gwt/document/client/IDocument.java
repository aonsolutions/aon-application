package com.esferalia.aon.gwt.document.client;

import java.util.Hashtable;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.gwt.document.jooq.DBConsults;
import com.esferalia.aon.gwt.document.shared.Category;
import com.esferalia.aon.gwt.document.shared.Document;
import com.esferalia.aon.gwt.document.shared.Domain;
import com.esferalia.aon.gwt.document.shared.FileInfo;
import com.esferalia.aon.gwt.document.shared.FilterUtil;
import com.esferalia.aon.gwt.document.shared.Lists;
import com.esferalia.aon.gwt.document.shared.SearchInfo;
import com.esferalia.aon.gwt.document.shared.Tag;
import com.esferalia.aon.gwt.document.shared.TreeDriveInfo;
import com.google.api.services.drive.model.File;
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
	
	public Vector<Domain> getSons();
	
	public void removeFile(FileInfo fi);
	
	public FileInfo editFile(FileInfo fi);
	
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
	
	public TreeMap<String, List<FileInfo>> drive(TreeMap<String, List<FileInfo>> folders,String id);
	
	public String getRootId();

	public Vector<FileInfo> getDriveFiles(String id);
	
	public Vector<FileInfo> getDriveFile(String id);
	
	public void upload(FileInfo fi,String parentId);
	
	public void deleteMydrive(FileInfo fi);
	
	public void shareMydrive(String email, String driveId);
	
	public Boolean initAux();
	
	public Tag newTag(String name);

	public void editTag(String name, Integer tagId);

	public void deleteTag(Integer tagId);

	public Category newCategory(String name);

	public void editCategory(String name, Integer categoryId);

	public void deleteCategory(Integer categoryId);
}
