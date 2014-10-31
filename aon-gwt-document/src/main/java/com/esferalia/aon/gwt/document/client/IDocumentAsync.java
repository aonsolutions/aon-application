package com.esferalia.aon.gwt.document.client;

import java.util.Vector;

import com.esferalia.aon.gwt.document.shared.Document;
import com.esferalia.aon.gwt.document.shared.FileInfo;
import com.esferalia.aon.gwt.document.shared.Lists;
import com.esferalia.aon.gwt.document.shared.SearchInfo;
import com.esferalia.aon.gwt.document.shared.TreeDriveInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Tree;

public interface IDocumentAsync {

	void getAllFiles(AsyncCallback<Document> callback);

	void getServiConveniosFiles(AsyncCallback<Vector<FileInfo>> callback);

	void searchFile(String searchStr, Vector<FileInfo> files,
			AsyncCallback<Vector<FileInfo>> callback);

	void getLists(AsyncCallback<Lists> callback);

	void searchFile(SearchInfo si, Vector<FileInfo> files,
			AsyncCallback<Vector<FileInfo>> callback);

	void getSons(AsyncCallback<Vector<String>> callback);

	void removeFile(FileInfo fi, AsyncCallback<Void> callback);

	void newFile(FileInfo fi, AsyncCallback<Boolean> callback);

	void editFile(FileInfo fi, AsyncCallback<Void> callback);

	void check(AsyncCallback<Boolean> callback);

	void isGconnection(AsyncCallback<Boolean> callback);

	void myDrive(String id, AsyncCallback<Vector<TreeDriveInfo>> callback);









}
