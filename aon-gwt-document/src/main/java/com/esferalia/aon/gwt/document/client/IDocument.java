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
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gwt_document")
public interface IDocument extends RemoteService{

	public Document getAllFiles(Domain domain);
	
	public Vector<FileInfo> searchFile(String searchStr, Vector<FileInfo> files);
	
	public FilterUtil searchFile2(SearchInfo si, Vector<FileInfo> files);
	
	public Lists getLists(Domain domain);
	
	public LinkedList<Domain> getSons(Domain domain);
	
	public void removeFile(Domain domain, Vector<FileInfo> fvector);
	
	public Vector<FileInfo> editFile(Domain domain, String dialogCode, FileInfo fi, Vector<FileInfo> fvector);
	
	public Boolean newFile(String dialogCode, FileInfo fi);

	public Boolean check();

	public Boolean isGconnection();
	
	public Vector<TreeDriveInfo> myDrive(String id);
		
	public Vector<FileInfo> insertFileBD(Domain domain, String dialogCode, FileInfo fi);
	
	public void share(Domain domain, String email,Vector<FileInfo> fvector); 
	
	public Vector<FileInfo> eSearchFile(Vector<FileInfo> v,String s);

	public Vector<FileInfo> searchFile(SearchInfo si, Vector<FileInfo> files, Vector<FileInfo> allFiles);
		
	public TreeMap<String, List<FileInfo>> drive(TreeMap<String, List<FileInfo>> folders,String id);
	
	public String getRootId();

	public Vector<FileInfo> getDriveFiles(String id);
	
	public Vector<FileInfo> getDriveFile(String id);
	
	public void upload(FileInfo fi,String parentId);
	
	public void deleteMydrive(Vector<FileInfo> fvector);
	
	public void shareMydrive(String email, Vector<FileInfo> fvector);
	
	public Init initAux(Domain domain);
	
	public Tag newTag(Domain domain, String name);

	public void editTag(Domain domain, String name, Integer tagId);

	public void deleteTag(Domain domain, Integer tagId);

	public Category newCategory(Domain domain, String name);

	public void editCategory(Domain domain, String name, Integer categoryId);

	public void deleteCategory(Domain domain, Integer categoryId);

	public MailAccountList getMailAccounts(Domain domain);
	
	public void sendEmail(Domain domain, String dialogCode, MailAccount ma, Emessage em);
	
	public void sendGmail(Domain domain, String dialogCode, MailAccount ma, Emessage em);
	
	public  ContactList getContacts(Domain domain);
	
	public void downloadMultiple(Vector<FileInfo> fvector);
	
	public String copyLink(FileInfo doc,String l);
	
	public void clearOuts(String dialogCode);
	
	public LinkedList<Attach>  getAttachList(Vector<FileInfo> fileInfoList);

	public void print(String head, String msg);
	
	public String getLink(Domain domain, FileInfo fileInfo, String l);
	
	public LinkedList<FileInfo> decompress(Domain domain, FileInfo fileInfo);
	
	public LinkedList<FileInfo> getSystemFiles(Domain domain);
	
	public Boolean isAlpha(Domain domain);
	
	public Boolean isBeta(Domain domain);

}
