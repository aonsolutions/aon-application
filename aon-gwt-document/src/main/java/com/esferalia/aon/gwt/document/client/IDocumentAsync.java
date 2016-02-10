package com.esferalia.aon.gwt.document.client;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

import com.esferalia.aon.gwt.document.shared.Category;
import com.esferalia.aon.gwt.document.shared.ContactList;
import com.esferalia.aon.gwt.document.shared.Document;
import com.esferalia.aon.gwt.document.shared.Emessage;
import com.esferalia.aon.gwt.document.shared.FileInfo;
import com.esferalia.aon.gwt.document.shared.FilterUtil;
import com.esferalia.aon.gwt.document.shared.Init;
import com.esferalia.aon.gwt.document.shared.Lists;
import com.esferalia.aon.gwt.document.shared.MailAccountList;
import com.esferalia.aon.gwt.document.shared.SearchInfo;
import com.esferalia.aon.gwt.document.shared.Tag;
import com.esferalia.aon.gwt.document.shared.TreeDriveInfo;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IDocumentAsync {

	void getAllFiles(Domain domain, AsyncCallback<Document> callback);

	void getServiConveniosFiles(Domain domain, AsyncCallback<Vector<FileInfo>> callback);

	void searchFile(String searchStr, Vector<FileInfo> files,
			AsyncCallback<Vector<FileInfo>> callback);

	void getLists(Domain domain, AsyncCallback<Lists> callback);

	void getSons(Domain domain, AsyncCallback<LinkedList<Domain>> callback);

	void removeFile(Domain domain, Vector<FileInfo> fvector, AsyncCallback<Void> callback);

	void newFile(FileInfo fi, AsyncCallback<Boolean> callback);

	void editFile(Domain domain, FileInfo fi, Vector<FileInfo> fvector, AsyncCallback<Vector<FileInfo>> callback);

	void check(AsyncCallback<Boolean> callback);

	void isGconnection(AsyncCallback<Boolean> callback);

	void myDrive(String id, AsyncCallback<Vector<TreeDriveInfo>> callback);

	void insertFile(Domain domain, FileInfo fi, AsyncCallback<Vector<FileInfo>> callback);

	void share(Domain domain, String email, Vector<FileInfo> fvector, AsyncCallback<Void> callback);

	void eSearchFile(Vector<FileInfo> v, String s,
			AsyncCallback<Vector<FileInfo>> callback);

	void searchFile2(SearchInfo si, Vector<FileInfo> files,
			AsyncCallback<FilterUtil> callback);

	void searchFile(SearchInfo si, Vector<FileInfo> files,
			Vector<FileInfo> allFiles, AsyncCallback<Vector<FileInfo>> callback);

	void drive(TreeMap<String, List<FileInfo>> folders,String id,AsyncCallback<TreeMap<String, List<FileInfo>>> callback);

	void getRootId(AsyncCallback<String> callback);

	void getDriveFiles(String id, AsyncCallback<Vector<FileInfo>> callback);

	void getDriveFile(String id, AsyncCallback<Vector<FileInfo>> callback);

	void upload(FileInfo fi, String parentId, AsyncCallback<Void> callback);

	void deleteMydrive(Vector<FileInfo> fvector, AsyncCallback<Void> callback);

	void shareMydrive(String email, Vector<FileInfo> fvector,
			AsyncCallback<Void> callback);

	void initAux(Domain domain, AsyncCallback<Init> callback);

	void newTag(Domain domain, String name, AsyncCallback<Tag> callback);

	void editTag(Domain domain, String name, Integer tagId, AsyncCallback<Void> callback);

	void deleteTag(Domain domain, Integer tagId, AsyncCallback<Void> callback);

	void newCategory(Domain domain, String name, AsyncCallback<Category> callback);

	void editCategory(Domain domain, String name, Integer categoryId, AsyncCallback<Void> callback);

	void deleteCategory(Domain domain, Integer categoryId, AsyncCallback<Void> callback);

	void getMailAccounts(Domain domain, AsyncCallback<MailAccountList> callback);

	void sendEmail(Domain domain, com.esferalia.aon.occam.api.model.MailAccount ma, Emessage em,
			AsyncCallback<Void> callback);

	void getContacts(Domain domain, AsyncCallback<ContactList> callback);

	void sendGmail(Domain domain, MailAccount ma, Emessage em, AsyncCallback<Void> callback);

	void downloadMultiple(Vector<FileInfo> fvector, AsyncCallback<Void> callback);

	void insertFileMultiple(Domain domain, FileInfo fi, AsyncCallback<Vector<FileInfo>> callback);

	void copyLink(FileInfo doc, String l, AsyncCallback<String> callback);

	void checkDomain(Domain domain, Document document, String type, AsyncCallback<Boolean> callback);

	void selectedMenu(Domain domain, AsyncCallback<Void> callback);

	void clearOuts(AsyncCallback<Void> callback);

}
