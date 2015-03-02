package com.esferalia.aon.gwt.document.client;

import java.util.Hashtable;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

import com.esferalia.aon.gwt.document.shared.Category;
import com.esferalia.aon.gwt.document.shared.ContactList;
import com.esferalia.aon.gwt.document.shared.Document;
import com.esferalia.aon.gwt.document.shared.Domain;
import com.esferalia.aon.gwt.document.shared.Emessage;
import com.esferalia.aon.gwt.document.shared.FileInfo;
import com.esferalia.aon.gwt.document.shared.FilterUtil;
import com.esferalia.aon.gwt.document.shared.Lists;
import com.esferalia.aon.gwt.document.shared.MailAccount;
import com.esferalia.aon.gwt.document.shared.MailAccountList;
import com.esferalia.aon.gwt.document.shared.SearchInfo;
import com.esferalia.aon.gwt.document.shared.Tag;
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

	void removeFile(Vector<FileInfo> fvector, AsyncCallback<Void> callback);

	void newFile(FileInfo fi, AsyncCallback<Boolean> callback);

	void editFile(FileInfo fi, Vector<FileInfo> fvector,
			AsyncCallback<Vector<FileInfo>> callback);

	void check(AsyncCallback<Boolean> callback);

	void isGconnection(AsyncCallback<Boolean> callback);

	void myDrive(String id, AsyncCallback<Vector<TreeDriveInfo>> callback);

	void insertFile(FileInfo fi, AsyncCallback<Vector<FileInfo>> callback);

	void share(String email, Vector<FileInfo> fvector,
			AsyncCallback<Void> callback);

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

	void upload(FileInfo fi, String parentId, AsyncCallback<Void> callback);

	void deleteMydrive(Vector<FileInfo> fvector, AsyncCallback<Void> callback);

	void shareMydrive(String email, Vector<FileInfo> fvector,
			AsyncCallback<Void> callback);

	void initAux(AsyncCallback<Vector<Boolean>> callback);

	void newTag(String name, AsyncCallback<Tag> callback);

	void editTag(String name, Integer tagId, AsyncCallback<Void> callback);

	void deleteTag(Integer tagId, AsyncCallback<Void> callback);

	void newCategory(String name, AsyncCallback<Category> callback);

	void editCategory(String name, Integer categoryId,
			AsyncCallback<Void> callback);

	void deleteCategory(Integer categoryId, AsyncCallback<Void> callback);

	void addToLote(Vector<FileInfo> fvector,
			AsyncCallback<Vector<FileInfo>> callback);

	void getLote(AsyncCallback<Vector<FileInfo>> callback);

	void resetLote(AsyncCallback<Void> callback);

	void getMailAccounts(AsyncCallback<MailAccountList> callback);

	void sendEmail(MailAccount ma, Emessage em, AsyncCallback<Void> callback);

	void getContacts(AsyncCallback<ContactList> callback);

	void sendGmail(MailAccount ma, Emessage em, AsyncCallback<Void> callback);

	void downloadMultiple(Vector<FileInfo> fvector, AsyncCallback<Void> callback);

	void insertFileMultiple(FileInfo fi,
			AsyncCallback<Vector<FileInfo>> callback);

	void copyLink(FileInfo doc, String l, AsyncCallback<Void> callback);





}
