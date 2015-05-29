package com.esferalia.aon.gwt.document.client;

import java.util.Hashtable;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.gwt.document.jooq.DBConsults;
import com.esferalia.aon.gwt.document.shared.Category;
import com.esferalia.aon.gwt.document.shared.ContactList;
import com.esferalia.aon.gwt.document.shared.Document;
import com.esferalia.aon.gwt.document.shared.Domain;
import com.esferalia.aon.gwt.document.shared.Emessage;
import com.esferalia.aon.gwt.document.shared.FileInfo;
import com.esferalia.aon.gwt.document.shared.FilterUtil;
import com.esferalia.aon.gwt.document.shared.Init;
import com.esferalia.aon.gwt.document.shared.Lists;
import com.esferalia.aon.gwt.document.shared.MailAccount;
import com.esferalia.aon.gwt.document.shared.MailAccountList;
import com.esferalia.aon.gwt.document.shared.SearchInfo;
import com.esferalia.aon.gwt.document.shared.Tag;
import com.esferalia.aon.gwt.document.shared.TreeDriveInfo;
import com.google.api.services.drive.model.File;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gwt_document")
public interface IDocument extends RemoteService{

	public Document getAllFiles(Integer domainId);

	public Vector<FileInfo> getServiConveniosFiles();
	
	public Vector<FileInfo> searchFile(String searchStr, Vector<FileInfo> files);
	
	//public Vector<FileInfo> searchFile(SearchInfo si, Vector<FileInfo> files);

	public FilterUtil searchFile2(SearchInfo si, Vector<FileInfo> files);
	
	public Lists getLists(Integer domainId);
	
	public Vector<Domain> getSons(Integer domainId);
	
	public void removeFile(Vector<FileInfo> fvector, Integer domainId);
	
	public Vector<FileInfo> editFile(FileInfo fi, Vector<FileInfo> fvector, Integer domainId);
	
	public Boolean newFile(FileInfo fi);

	public Boolean check();

	public Boolean isGconnection();
	
	public Vector<TreeDriveInfo> myDrive(String id);
	
	public Vector<FileInfo> insertFile(FileInfo fi, Integer domainId);
	
	public void share(String email,Vector<FileInfo> fvector, Integer domainId); 
	
	public Vector<FileInfo> eSearchFile(Vector<FileInfo> v,String s);

	public Vector<FileInfo> searchFile(SearchInfo si, Vector<FileInfo> files,
			Vector<FileInfo> allFiles);
	public String getAsHTML(FileInfo doc, int zoom);
	
	public TreeMap<String, List<FileInfo>> drive(TreeMap<String, List<FileInfo>> folders,String id);
	
	public String getRootId();

	public Vector<FileInfo> getDriveFiles(String id);
	
	public Vector<FileInfo> getDriveFile(String id);
	
	public void upload(FileInfo fi,String parentId);
	
	public void deleteMydrive(Vector<FileInfo> fvector);
	
	public void shareMydrive(String email, Vector<FileInfo> fvector);
	
	public Init initAux();
	
	public Tag newTag(String name, Integer domainId);

	public void editTag(String name, Integer tagId);

	public void deleteTag(Integer tagId);

	public Category newCategory(String name, Integer domainId);

	public void editCategory(String name, Integer categoryId);

	public void deleteCategory(Integer categoryId);

	public MailAccountList getMailAccounts();
	
	public void sendEmail(MailAccount ma, Emessage em);
	
	public void sendGmail(MailAccount ma, Emessage em, Integer domainId);
	
	public  ContactList getContacts();
	
	public void downloadMultiple(Vector<FileInfo> fvector);
	
	public Vector<FileInfo> insertFileMultiple(FileInfo fi, Integer domainId);
	
	public String copyLink(FileInfo doc,String l);
	
	public Boolean checkDomain(Document document, String type, Integer domainId);
	
	
}
