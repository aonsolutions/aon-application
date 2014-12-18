package com.esferalia.aon.gwt.document.client;

import java.util.Hashtable;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

import com.esferalia.aon.gwt.document.shared.Document;
import com.esferalia.aon.gwt.document.shared.Domain;
import com.esferalia.aon.gwt.document.shared.FileInfo;
import com.esferalia.aon.gwt.document.shared.FilterUtil;
import com.esferalia.aon.gwt.document.shared.Lists;
import com.esferalia.aon.gwt.document.shared.SearchInfo;
import com.esferalia.aon.gwt.document.shared.TreeDriveInfo;
import com.google.api.services.drive.model.File;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IDocumentAsync {

	void getAllFiles(AsyncCallback<Document> callback);

	void getServiConveniosFiles(AsyncCallback<Vector<FileInfo>> callback);

	void searchFile(String searchStr, Vector<FileInfo> files,
			AsyncCallback<Vector<FileInfo>> callback);

	void getLists(AsyncCallback<Lists> callback);

	//void searchFile(SearchInfo si, Vector<FileInfo> files,
		//	AsyncCallback<Vector<FileInfo>> callback);

	void getSons(AsyncCallback<Vector<Domain>> callback);

	void removeFile(FileInfo fi, AsyncCallback<Void> callback);

	void newFile(FileInfo fi, AsyncCallback<Boolean> callback);

	void editFile(FileInfo fi, AsyncCallback<Void> callback);

	void check(AsyncCallback<Boolean> callback);

	void isGconnection(AsyncCallback<Boolean> callback);

	void myDrive(String id, AsyncCallback<Vector<TreeDriveInfo>> callback);

	void insertFile(FileInfo fi, AsyncCallback<FileInfo> callback);

	void share(String email,String driveId, AsyncCallback<Void> callback);

	void eSearchFile(Vector<FileInfo> v, String s,
			AsyncCallback<Vector<FileInfo>> callback);

	void searchFile2(SearchInfo si, Vector<FileInfo> files,
			AsyncCallback<FilterUtil> callback);

	void searchFile(SearchInfo si, Vector<FileInfo> files,
			Vector<FileInfo> allFiles, AsyncCallback<Vector<FileInfo>> callback);

	void getAsHTML(FileInfo doc, int zoom, AsyncCallback<String> callback);

	void drive(TreeMap<String, List<FileInfo>> folders,String id,AsyncCallback<TreeMap<String, List<FileInfo>>> callback);

	void getRootId(AsyncCallback<String> callback);

	void getDriveFiles(String id, AsyncCallback<Vector<FileInfo>> callback);

	void getDriveFile(String id, AsyncCallback<Vector<FileInfo>> callback);

	void upload(FileInfo fi, AsyncCallback<Void> callback);

	void deleteMydrive(FileInfo fi, AsyncCallback<Void> callback);

	void shareMydrive(String email, String driveId, AsyncCallback<Void> callback);

	void initAux(AsyncCallback<Boolean> callback);


}
