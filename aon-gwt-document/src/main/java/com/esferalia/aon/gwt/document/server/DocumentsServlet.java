package com.esferalia.aon.gwt.document.server;

import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.getConnection;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Collator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.google.apis.DriveFile;
import com.code.aon.google.apis.DriveUtils;
import com.code.aon.google.apis.GmailUtils;
import com.code.aon.google.apis.UrlShortenerUtils;
import com.code.aon.google.apis.drive.SearchFiles;
import com.code.aon.google.apis.drive.ShareFiles;
import com.code.aon.google.apis.jooq.DomainGserviceaccount;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.google.apis.controller.GoogleDriveController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.IWebMailConstants;
import com.code.aon.ui.webmail.controller.MailConfigController;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.webmail.IMailAccount;
import com.code.aon.webmail.WebmailException;
import com.code.aon.webmail.WebmailUtil;
import com.code.aon.webmail.bean.AonMessage;
import com.code.aon.webmail.bean.AonServer;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.document.client.IDocument;
import com.esferalia.aon.gwt.document.client.Utils;
import com.esferalia.aon.gwt.document.jooq.DBConsults;
import com.esferalia.aon.gwt.document.jooq.SendEmailDialogJooq;
import com.esferalia.aon.gwt.document.server.OpenDocument2ImageServlet.CheckSum;
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
import com.esferalia.aon.gwt.document.shared.Tags;
import com.esferalia.aon.gwt.document.shared.TreeDriveInfo;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.RattachColumns;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.About;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.Property;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.urlshortener.Urlshortener;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;


public class DocumentsServlet extends RemoteServiceServlet implements IDocument{

	private static final long serialVersionUID = 6871016881549113129L;

	private static InputStream file;
	private static String mimetype;
	
	public static String getMimetype() {
		return mimetype;
	}

	public static void setMimetype(String mimetype) {
		DocumentsServlet.mimetype = mimetype;
	}

	public InputStream getFile() {
		return file;
	}

	public static void setFile(InputStream file2) {
		file = file2;
	}

	void initFacesContext() {
		ServletContext context = getServletContext();
		HttpServletRequest request = getThreadLocalRequest();
		HttpServletResponse response = getThreadLocalResponse();
		AonServletUtils.initFacesContext(context, request, response);
	}

	void releaseFacesContext() {
		AonServletUtils.releaseFacesContext();
	}
	Boolean confidential;
	public Vector<Boolean> initAux(){
		try{initFacesContext();
		Vector<Boolean> v = new Vector<Boolean>();
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		domainId = ds.getDomainId();
		confidential = AonUtil.getRoleManager().isConfidentiality();
		Boolean documentManager = AonUtil.getRoleManager().isDocumentManager();
		v.add(documentManager);
		v.add(confidential);
		return v;
		}
		finally{releaseFacesContext();}
	}
	
	public Document getAllFiles(){
		
		/*DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		Integer domainId = ds.getDomainId();*/
		String domain = AonUtil.getDomainName();
		Integer userDomainId = AonUtil.getAuthPrincipal().getUserDomainId();
		Integer user_id=AonUtil.getAuthPrincipal().getUserId();
		Document docs = new Document();
		try {
			String domainUrl = DBConsults.getDomain(domain, domainId);
			docs  = DBConsults.getAllRattach(domain,domainUrl,user_id, confidential,domainId, userDomainId);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//docs.setFiles(DBConsults.getFilesGwt());
		docs.setDomain(domain);
		return docs;
	}
	
	public Vector<FileInfo> getServiConveniosFiles(){
	
		String domain = AonUtil.getDomainName();
		Vector<FileInfo> v = new Vector<FileInfo>();
		try {
			v= DBConsults.getServiConvenios(domain);
			

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return v;
	}
	
	Vector<String> types;
	public Vector<String> getTypes(){
		Locale locale = AonUtil.getCurrentLocale();
		Vector<String> vector = new Vector<String>();
		for (int i = 0 ; i<RegistryAttachmentType.values().length ; i++){
			String type = RegistryAttachmentType.values()[i].getName(locale);
			vector.add(type);
		}
		return vector;
	}
	
	public Vector<FileInfo> searchFile(String searchStr, Vector<FileInfo> files){
		Vector<FileInfo> vector = new Vector<FileInfo>();
		for (FileInfo fileInfo : files) {
			if(containsIgnoreCase2(fileInfo.getTitle(), searchStr)){
				vector.add(fileInfo);
			}
		}
		return vector;
	}
	
	public Vector<FileInfo> searchFile(SearchInfo si, Vector<FileInfo> files, Vector<FileInfo> allFiles){
		if(si.getDomain()!=null){
			files = eSearchFile(allFiles, si.getDomain());
		}
		Vector<FileInfo> vector = new Vector<FileInfo>();
		for (FileInfo fileInfo : files) {
			if(filter(si, fileInfo)){
				vector.add(fileInfo);
			}
		}
		return vector;
		
	}
	
	public FilterUtil searchFile2(SearchInfo si, Vector<FileInfo> files){

		Vector<FileInfo> vector = new Vector<FileInfo>();
		for (FileInfo fileInfo : files) {
			if(filter(si, fileInfo)){
				vector.add(fileInfo);
			}
		}
		
		return new FilterUtil(vector,si.getCategory()!=null?si.getCategory():"",si.getTag()!=null?si.getTag().get(0):"");
		
	}
	
	public Boolean filter(SearchInfo si,FileInfo fi){
		if(si.getName() != null){
			if(!AonStringUtils.contains(fi.getTitle(), si.getName())){
				return false;
			}
		}
		if(si.getConfidential()!=null && fi.getConfidential()!=null){
			if(!si.getConfidential().equals(fi.getConfidential())){
				return false;
			}
		}
		if(si.getDate()!=null){
			
			if(!si.getDate().replace('/', '-').equals(fi.getDateStr())){
				return false;
			}
		}
		if(si.getCategory()!=null){
			if(!si.getCategory().equals(fi.getCategoryStr())){
				return false;
			}
		}
		if(si.getTag()!=null){
			Vector<String> v = si.getTag();
			Vector<String> ops = si.getYoTag();
			Boolean b = Tags.contain(v.get(0),fi.getTags());
			for(int i =1;i<v.size();i++){
				if(ops.get(i-1).equals("Y")){		
					if(!(b && Tags.contain(v.get(i),fi.getTags()))){
						b = false;
					}
				}
				else{
					if(b){
						i=v.size();
					}
					else{
						b= Tags.contain(v.get(i),fi.getTags());
					}
				}
			}
			if(!b) 
				return false;
		}
		if(si.getScope()!=null){
			if(!si.getScope().equals(fi.getScope().getName())){
				return false;
			}
		}
		if(si.getDomain() != null){
			if(!si.getDomain().equals(fi.getDomain()))
				return false;;
		}
		return true;
	}
	Integer domainId;
	public Vector<Domain> getSons(){
		
		String domain = AonUtil.getDomainName();
		
		Vector<Domain> vector = new Vector<Domain>();
		try {
			String domainUrl= DBConsults.getDomain(domain, domainId);
			vector = DBConsults.getSons(domainUrl);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Domain d = new Domain();
		d.setName(domain);
		d.setDescription("");
		vector.add(d);
		return vector;
		
		
		
	}
	
	public Lists getLists(){
		/*initFacesContext();
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		Integer domainId = ds.getDomainId();*/
		String domain = AonUtil.getDomainName();
		Integer user_id=AonUtil.getAuthPrincipal().getUserId();
		Integer userDomainId = AonUtil.getAuthPrincipal().getUserDomainId();

		Lists lists= new Lists();
		
		try {
			domain = DBConsults.getDomain(domain, domainId);
			lists.setCategoryList(DBConsults.getCategoryList(domain));
			lists.setCategoryListSon(DBConsults.getCategoryListSon(domain));
			lists.setScopeList(DBConsults.getScopeList(domain,domainId,user_id,userDomainId));
			lists.setScopeListSon(DBConsults.getScopeListSon(domain,domainId,user_id,userDomainId));
			lists.setTagList(DBConsults.getTagList(domain));
			lists.setTagListSon(DBConsults.getTagListSon(domain));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		return lists;
	}
	
	public void removeFile(Vector<FileInfo> fvector){

		String domain = AonUtil.getDomainName();
		for(FileInfo fi : fvector){
			try {
				DBConsults.removeFile(domain, fi.getFileId());
				if(fi.getDriveId()!=null){
					DomainGserviceaccount g = com.code.aon.google.apis.jooq.DBConsults.getServiceAccount(domain,domainId);
					Drive d = DriveUtils.serviceInitialize(g);
					d.files().delete(fi.getDriveId()).execute();
				}
			
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (KeyStoreException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (GeneralSecurityException e) {
				e.printStackTrace();
			}
		}

	}
	public static byte[] out;
	public static Vector<FileInfo> outs = new Vector<FileInfo>();
	
	public static Vector<FileInfo> getOuts(){
		return outs;
	}
	public static void setOuts(Vector<FileInfo> outs2){
		outs = outs2;
	}
	
	public static void addOuts(FileInfo fi){
		outs.add(fi);
	}
	
	public static byte[] getOut() {
		return out;
	}

	public static void setOut(byte[] out2) {
		out = out2;
	}

	public Boolean newFile(FileInfo fi) {
		Vector<FileInfo> files = getOuts();
		if(files.size()>1 && !fi.getDomain().equals("false"))
			return true;
		if (files.size()>0
				&& !fi.getTitle().equals("") && !fi.getDomain().equals("false")) {
		
			return true;
		}
		return false;

	}
	
	public Vector<FileInfo> insertFile(FileInfo fi) {
		Vector<FileInfo> files = getOuts();
		Vector<FileInfo> vector = new Vector<FileInfo>();

		for (FileInfo f : files) {
			Date date = null;
			if (fi.getDate() != null){
				date = new Date(fi.getDate().getYear(),
						fi.getDate().getMonth(), fi.getDate().getDate());
				f.setDate(fi.getDate());
			}
			/*
			 * initFacesContext(); DomainSwitcher ds = (DomainSwitcher)
			 * AonUtil.getRegisteredBean(DOMAIN_SWITCHER); Integer domainId2 =
			 * ds.getDomainId();
			 */
			String domain = AonUtil.getDomainName();
			try {
				domain = DBConsults.getDomain(domain, domainId);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			// Integer registry = AdminUtil.getCompanyId(fi.getDomainId());
			com.code.aon.google.apis.FileInfo fileInfo = new com.code.aon.google.apis.FileInfo();
			fileInfo.setAonType("registry");
			if (fi.getCategory() != -1){
				fileInfo.setCategory(fi.getCategory());
				//f.setCategory(fi.getCategory());
				//f.setCategoryStr(fi.getCategoryStr());
			}
			else
				fileInfo.setCategory(null);
			fileInfo.setDateSql(date);
			//f.setDateSql(date);
			fileInfo.setMimetype((byte) MimeType.get(f.getMimeString()).ordinal());
			//f.setMimetype((byte) MimeType.get(f.getMimeString()).ordinal());
			if(files.size()<=1 && fi.getTitle()!=null){ 
				fileInfo.setTitle(fi.getTitle());
				//f.setTitle(fi.getTitle());
			}
			else fileInfo.setTitle(f.getTitle());
			fileInfo.setType((short) RegistryAttachmentType.CORPORATE_IDENTITY
					.ordinal());
			//f.setType((short) RegistryAttachmentType.CORPORATE_IDENTITY
					//.ordinal());
			if (fi.getScope().getId() != -1){
				fileInfo.setScopeId(fi.getScope().getId());
				//f.setScope(fi.getScope());
			}
			else
				fileInfo.setScopeId(null);
			Byte conf;
			if (fi.getConfidential())
				conf = 1;
			else
				conf = 0;
			fileInfo.setSecurityLevel(conf);
			//f.setConfidential(fi.getConfidential());
			if (!fi.getDomain().equals("")) {
				domain = fi.getDomain();
			}

			try {
				Integer domainId = DBConsults.getDomainId(domain, domain);
				fileInfo.setDomainId(domainId);
				//f.setDomain(domain);
				//f.setDomainId(domainId);
				fileInfo.setSize((Integer) f.getSize());
				//f.setSizeStr(FileUtils.byteCountToDisplaySize(f.getSize()!=null?f.getSize():0));
				Integer id = DBConsults.insertFile(domain, fileInfo);
				//f.setTags(fi.getTags());
				DBConsults.insertTagsFile(id, fi.getTags(), domain, domainId);
				byte[] b = f.getData();// .toByteArray();
				fileInfo.setFileId(id);
				fileInfo.setData(b);
				DomainGserviceaccount g = com.code.aon.google.apis.jooq.DBConsults.getServiceAccount(domain, domainId);
				if (g.getClientId() != null) {
					Drive d = DriveUtils.serviceInitialize(g);
					String[] types = { RegistryAttachmentType.CORPORATE_IDENTITY
							.toString() };// TODO
					DriveUtils.types = types;
					fileInfo.setDomain(domain);
					DriveUtils.sync2(d, fileInfo, domain);
				} else
					DBConsults.insertFileData(domain, id, b);
				// insertFile(d, fileInfo,b);//DriveUtils.insertFile(d,
				// fileInfo, new Vector<ParentReference>(), new
				// Vector<String>(), domain);
				
				FileInfo fil = DBConsults.getFile(domain, id);
				if (fil.getDomain() == null)
					fil.setDomain(domain);
				if(fil.getDomainId() == null)
					fil.setDomainId(domainId);
				vector.add(fil);
				
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (KeyStoreException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (GeneralSecurityException e) {
				e.printStackTrace();
			} catch (AonConnectionException e) {
				e.printStackTrace();
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
		setOuts(new Vector<FileInfo>());
		return vector;
	}
	
	public Boolean check(){
		return getMimetype()==null;//.equals("application/octet-stream");
			
		
	}
	
	public Vector<FileInfo> editFile(FileInfo fi,Vector<FileInfo> fvector){
		String domain = AonUtil.getDomainName();
		//Integer registry = AdminUtil.getCompanyId(fi.getDomainId());
		com.code.aon.google.apis.FileInfo fileInfo = new com.code.aon.google.apis.FileInfo();
		if(fvector != null){
			for(FileInfo f : fvector){
				fileInfo.setFileId(f.getFileId());
				fileInfo.setAonType("registry");
				fileInfo.setType((short)RegistryAttachmentType.CORPORATE_IDENTITY.ordinal());
				if(fi.getCategory()!=null){
					fileInfo.setCategory(fi.getCategory());
					f.setCategory(fi.getCategory());	
					f.setCategoryStr(fi.getCategoryStr());
				}
				else fileInfo.setCategory(f.getCategory());
				if(fi.getDate() != null){
					fileInfo.setDate(fi.getDate());
					Date date = null;
					if (fi.getDate() != null)
					date = new Date(fi.getDate().getYear(),
							fi.getDate().getMonth(), fi.getDate().getDate());
					fileInfo.setDateSql(date);
					
					f.setDate(fi.getDate());
					f.setDateSql(fi.getDateSql());
					f.setDateStr(fi.getDateStr());
				}else{
					fileInfo.setDate(f.getDate());
					Date date = null;
					if(f.getDate()!= null)
						date = new Date(f.getDate().getYear(),
							f.getDate().getMonth(), f.getDate().getDate());
					fileInfo.setDateSql(date);
				}
				if(fi.getScope() != null){
					fileInfo.setScopeId(fi.getScope().getId());
					f.setScope(fi.getScope());
				}
				else fileInfo.setScopeId(f.getScope().getId());
				Byte conf;if(fi.getConfidential())conf=1; else conf=0;
				fileInfo.setSecurityLevel(conf);
				f.setConfidential(fi.getConfidential());
				fileInfo.setMimetype(f.getMimetype());
				fileInfo.setTitle(f.getTitle());
				Vector<Tag> tags;
				if(fi.getTags().size()>0){
					tags= fi.getTags();
					f.setTagsStr(fi.getTagsStr());
				}
				else tags = f.getTags();
				try {
					DBConsults.updateFile(domain, fileInfo,tags,domainId);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
			}
		}else{
			fileInfo.setFileId(fi.getFileId());
			fileInfo.setAonType("registry");
			fileInfo.setDriveId(fi.getDriveId());
			fileInfo.setType((short)RegistryAttachmentType.CORPORATE_IDENTITY.ordinal());
			if(fi.getCategory()!=null)fileInfo.setCategory(fi.getCategory());
			if(fi.getDate() != null){
				fileInfo.setDate(fi.getDate());
				Date date = null;
				if (fi.getDate() != null)
				date = new Date(fi.getDate().getYear(),
						fi.getDate().getMonth(), fi.getDate().getDate());
				fileInfo.setDateSql(date);
			}
			if(getMimetype()!=null){
				fileInfo.setMimetype((byte)MimeType.get(getMimetype()).ordinal());
				fi.setMimetype((byte)MimeType.get(getMimetype()).ordinal());
				fi.setIcon(Utils.icon(getMimetype()));
			}
		
			else fileInfo.setMimetype(fi.getMimetype());
			fileInfo.setTitle(fi.getTitle());
			if(fi.getScope() != null)fileInfo.setScopeId(fi.getScope().getId());
			Byte conf;if(fi.getConfidential())conf=1; else conf=0;
			fileInfo.setSecurityLevel(conf);
			
			try {
				DBConsults.updateFile(domain, fileInfo,fi.getTags(),domainId);
				
				if(getMimetype()!=null){
					//InputStream file = getFile();
					byte[] b = getOut();//.toByteArray();
					fileInfo.setData(b);
					DomainGserviceaccount g = com.code.aon.google.apis.jooq.DBConsults.getServiceAccount(domain,domainId);
					if(g.getClientId()!=null){
						Drive d = DriveUtils.serviceInitialize(g);
						String[] types = { RegistryAttachmentType.CORPORATE_IDENTITY
								.toString() };// TODO
						DriveUtils.types = types;
						DriveUtils.sync2(d, fileInfo, domain);
					}
					else{
						DBConsults.insertFileData(domain, fileInfo.getFileId(), b );
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (KeyStoreException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (GeneralSecurityException e) {
				e.printStackTrace();
			} catch (AonConnectionException e) {
				e.printStackTrace();
			} catch (NamingException e) {
				e.printStackTrace();
			}
			fvector = new Vector<FileInfo>();
			fvector.add(fi);
		}
		return fvector;
		
	}
	
	//-------------------- My Drive
	
	public Boolean isGconnection() {
		return GoogleDriveController.gconnection;	
	}
	
	public String getRootId() {
		if(GoogleDriveController.gconnection){
			Drive drive = GoogleDriveController.dconnection;
			About about = null;
			try {
				about = drive.about().get().execute();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return about.getRootFolderId();
		}
		else return "";
	}
	
	public FileList getFileList(String id){
		Drive drive = GoogleDriveController.dconnection;
		FileList fl=null;
		try {
			fl = drive.files().list().setQ("'"+id+"' in parents and mimeType = 'application/vnd.google-apps.folder'").execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fl;
	}
	
	public FileList getFileList2(String id){
		Drive drive = GoogleDriveController.dconnection;
		FileList fl=null;
		try {
			fl = drive.files().list().setQ("'"+id+"' in parents ").execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fl;
	}
	
	public Vector<FileInfo> getDriveFiles(String id){
		Vector<FileInfo> v = new Vector<FileInfo>();
		FileList fl = getFileList2(id);
		for (File f : fl.getItems()) {
			FileInfo fi = new FileInfo();
			fi.setTitle(f.getTitle());
			fi.setCategoryStr("-");	
			fi.setDateStr(f.getCreatedDate().toString());
			fi.setSizeStr(FileUtils.byteCountToDisplaySize(fi.getSize()!=null?f.getFileSize():0));
			fi.setTagsStr("-");
			v.add(fi);
		}
		return v; 
	}
	
	public Vector<FileInfo> getDriveFile(String id) {
		Vector<FileInfo> v = new Vector<FileInfo>();
		Drive drive = GoogleDriveController.dconnection;
		FileList fl=null;
		try {
			fl = drive.files().list().setQ("'"+id+"' in parents and mimeType != 'application/vnd.google-apps.folder'").execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		fl.getItems().stream().forEach(f->{
			FileInfo fi = new FileInfo();
			fi.setDriveId(f.getId());
			fi.setTitle(f.getTitle());
			fi.setCategoryStr("-");	
			fi.setDateStr(f.getCreatedDate().toString());
			if(f.getFileSize() != null){fi.setSize(f.getFileSize().intValue());
			fi.setSizeStr(FileUtils.byteCountToDisplaySize(f.getFileSize()!=null?f.getFileSize():0));}
			else {fi.setSize(0);fi.setSizeStr("-");}
			fi.setTagsStr("-");
			fi.setIcon(Utils.icon(f.getMimeType()));
			fi.setConfidential(false);
			fi.setAonType("registry");
			fi.setCategory(0);
			fi.setDate(null);
			fi.setDomain("");
			fi.setFileId(0);
			fi.setType((short) RegistryAttachmentType.CORPORATE_IDENTITY.ordinal());
			fi.setIsDrive(true);
			if(MimeType.get(f.getMimeType())!=null)
				fi.setMimetype((byte)MimeType.get(f.getMimeType()).ordinal());
			fi.setIsGdocs(Utils.isGdocs(f.getMimeType()));
			v.add(fi);	
		});
		return v;
	}
	
	public TreeMap<String, List<FileInfo>> drive(TreeMap<String, List<FileInfo>> folders , String id)  {
		if (GoogleDriveController.gconnection){
			if(id.equals("")){
				id= getRootId();
				folders.put(getRootId(), new Vector<FileInfo>());
			}
		
			FileList fl=  getFileList(id);
	
			for (File f : fl.getItems()) {
			
				FileInfo fi = new FileInfo();
				fi.setTitle(f.getTitle());
				fi.setDriveId(f.getId());
				for (ParentReference pr : f.getParents()) {
					if(folders.containsKey(pr.getId())){
						folders.get(pr.getId()).add(fi);
					}
					else{
						folders.put(pr.getId(), new Vector<FileInfo>());
						folders.get(pr.getId()).add(fi);
					}
				}
			}
		}
		return folders;
	}
	
	public Vector<TreeDriveInfo> myDrive(String id){
		Drive drive = GoogleDriveController.dconnection;
		FileList fl = null;
		About about;
			try {
				if(id.equals("")){
					about = drive.about().get().execute();
					id = "'" + about.getRootFolderId() + "'";
				}
				else id = "'"+id+"'";
			
				fl = drive.files().list().setQ(id+" in parents and mimeType = 'application/vnd.google-apps.folder'").execute();
			
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		Vector<TreeDriveInfo> v = new Vector<TreeDriveInfo>();
		
		 if(fl!=null){
		 for (File f : fl.getItems()) {
			TreeDriveInfo tdi = new TreeDriveInfo();
			FileInfo fi = new FileInfo();
			fi.setTitle(f.getTitle());
			fi.setDriveId(f.getId());
			tdi.setParent(fi);
			tdi.setSons(myDrive(fi.getDriveId()));

		}
		}
		 return v;
	
	}

	public void upload(FileInfo fi,String parentId){
		Vector<ParentReference> parents = null;
		if(parentId != null){
			parents = new Vector<ParentReference>();
			ParentReference p = new ParentReference();
			p.setId(parentId);
			parents.add(p);
		}
		Drive drive = GoogleDriveController.dconnection;
		byte[] b = getOut();
		java.io.File aux = new java.io.File("/tmp/" + fi.getTitle());
	
		DriveFile file=new DriveFile("","", fi.getTitle(), getMimetype());
		
		try {
			org.apache.commons.io.FileUtils.writeByteArrayToFile(aux, b);
			DriveUtils.insertFile(drive,aux, file,parents);
		} catch (IOException e) {
 			e.printStackTrace();
		}
	}
	
	public void deleteMydrive(Vector<FileInfo> fvector){
		Drive drive = GoogleDriveController.dconnection;
		for(FileInfo fi : fvector){
			try {
				DriveUtils.deleteFile(drive, fi.getDriveId());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void shareMydrive(String email, Vector<FileInfo> fvector){

		Drive drive = GoogleDriveController.dconnection;
		for(FileInfo f: fvector){
			try {
				ShareFiles.setPermission(drive, f.getDriveId(), email);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void share(String email, Vector<FileInfo> fvector) {
		String domain = AonUtil.getDomainName();
		DomainGserviceaccount g;
		Drive d = null;
		try {
			g = com.code.aon.google.apis.jooq.DBConsults.getServiceAccount(domain, domainId);
			d = DriveUtils.serviceInitialize(g);
		} catch (SQLException | IOException | GeneralSecurityException e1) {
			e1.printStackTrace();
		}
		for(FileInfo f : fvector){
			if (f.getDriveId() != null) {
				try {
					ShareFiles.setPermission(d, f.getDriveId(), email);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else{
				//TODO  dbn badago! Drive-ra igo ta banatu!
			}
			
		}
		
	}
	public Vector<FileInfo> eSearchFile(Vector<FileInfo> v,String str) {
		Vector<FileInfo> aux = new Vector<FileInfo>();//=  filesGwt.stream().filter(d -> d.getDomain().equalsIgnoreCase(domain));
		v.stream().forEach(f -> {
			if(f.getDomain().equalsIgnoreCase(str))
				aux.add(f);
		});
		return aux;
	}
	
	//-------------------- Visualizar Archivo

	public String getAsHTML(FileInfo doc, int zoom) {
		

		doc = setmd5(doc);
	
		
		IDocument2HtmlConverter converter = 
				getDocument2HtmlConverter(doc);
		try {
			ByteArrayOutputStream os = 
					new ByteArrayOutputStream();
			converter.transform(doc, os, zoom);
			os.flush();
			return os.toString();
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}
	public String copyLink(FileInfo doc,String l){
		String link;
		Urlshortener u = null;
		DomainGserviceaccount g = null;
		try {
			g = com.code.aon.google.apis.jooq.DBConsults.getServiceAccount(doc.getDomain(),doc.getDomainId());
			try {
				u = UrlShortenerUtils.serviceInitialize(g);
			} catch (IOException | GeneralSecurityException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(doc.getDriveId()!=null){
			Drive d = null;
        	if(doc.getIsDrive()){
        		d = GoogleDriveController.dconnection;
        	}
        	else{
				try {
					d = DriveUtils.serviceInitialize(g);
				} catch (IOException | GeneralSecurityException e) {
					e.printStackTrace();
				}
        	}
			com.google.api.services.drive.model.File f = null;
			try {
				f = DriveUtils.getFile(d, doc.getDriveId(),doc.getFileId());
				if(f.getTitle().equals("OLDRIVE"))
					d = DriveUtils.serviceInitializeOld(g);
			} catch (IOException | SQLException | GeneralSecurityException e) {
				e.printStackTrace();
			}
		
			Permission p = new Permission();
			p.setValue(doc.getDomain());
			p.setType("anyone");// user || group || domain || anyone
			p.setRole("reader");// owner || reader || writer || commenter
			try {
				d.permissions().insert(f.getId(), p).execute();
			} catch (IOException e) {
				try {
					d = DriveUtils.serviceInitializeOld(g);
					d.permissions().insert(f.getId(), p).execute();
				} catch (IOException | GeneralSecurityException e1) {
					e1.printStackTrace();
				}
			}
			link= f.getAlternateLink();
			Property property;
			try {
				property = d.properties().get(f.getId(), "shortUrl").execute();
				link = property.getValue();
			} catch (IOException e) {
				Property property1 = new Property();
				property1.setKey("shortUrl");
				try {
					property1.setValue(UrlShortenerUtils.getShortUrl(u, f.getAlternateLink()));
					property = d.properties().insert(f.getId(), property1).execute();
					link = property.getValue();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
		}
		else{
			doc = setmd5(doc);
			String md5 =doc.getMd5();
			if(l.contains("aon_gwt_document")){
				Integer pos = l.lastIndexOf("/");
				Integer pos2 = l.substring(0, pos).lastIndexOf("/");
				l = l.substring(0,pos2);
			}
			link = l+"/aonDocuments/"+ doc.getFileId() +"-"+ md5;
			try {
				link = UrlShortenerUtils.getShortUrl(u, l+"/aonDocuments/"+ doc.getFileId() +"-"+ md5);
			} catch (IOException e) {
				e.printStackTrace();
			}

			//link = l+"/aonDocumentsViewer/"+ "?file_id="+ doc.getFileId();
		}
		//Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		//StringSelection data = new StringSelection(link);
		//clipboard.setContents(data, data);
		
		return link;
	}
	public FileInfo setmd5(FileInfo doc){
		ViewerUtils.RAttach rattach = null;
		try {
			rattach = ViewerUtils.getRAttach(doc.getFileId());
		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		doc.setDriveId(rattach.driveId);
		doc.setDomainId(rattach.domainId);
		com.esferalia.aon.google.sql.AbstractSQL.Domain dom = null;
		try {
			dom = com.code.aon.google.apis.jooq.DBConsults.getDomain(AonUtil.getDomainName(), rattach.domainId);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		doc.setDomain(dom.getName());
		if(doc.getDriveId()!= null){
        	Drive d = null;
        	DomainGserviceaccount g = null;
        	if(doc.getIsDrive()){
        		d = GoogleDriveController.dconnection;
        	}
        	else{
        		
        		try {
					g = com.code.aon.google.apis.jooq.DBConsults.getServiceAccount(doc.getDomain(), doc.getDomainId());
				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					d = DriveUtils.serviceInitialize(g);
				} catch (IOException | GeneralSecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
			com.google.api.services.drive.model.File f = null;
			try {
				f = DriveUtils.getFile(d, doc.getDriveId(),doc.getFileId());
			} catch (IOException | SQLException | GeneralSecurityException e) {
				e.printStackTrace();
			}
			doc.setMd5(f.getMd5Checksum());
			
		}
		else {
			byte[] b = rattach.bytes;
			doc.setMd5(CheckSum.getMD5Checksum(b));
		}
		return doc;
	}
	
	
	private IDocument2HtmlConverter getDocument2HtmlConverter(FileInfo document) {
		
		MimeType mimeType = MimeType.values()[document.getMimetype()];
		return DOC2HTML_CONVERTERS.get(mimeType);
		
	}
	
	
	public static final Map<MimeType, IDocument2HtmlConverter> DOC2HTML_CONVERTERS = 
			new HashMap<MimeType, IDocument2HtmlConverter>(){
		{
			put(MimeType.MIME_PDF, OpenDocument2HtmlConverter.INSTANCE );
			put(MimeType.MIME_MS_WORD, OpenDocument2HtmlConverter.INSTANCE );
			put(MimeType.MIME_MS_WORD_2007, OpenDocument2HtmlConverter.INSTANCE );
			put(MimeType.MIME_MS_EXCEL, OpenDocument2HtmlConverter.INSTANCE );
			put(MimeType.MIME_MS_EXCEL_2007, OpenDocument2HtmlConverter.INSTANCE );
			put(MimeType.MIME_MS_POWER_POINT, OpenDocument2HtmlConverter.INSTANCE );
			put(MimeType.MIME_MS_POWER_POINT_2007, OpenDocument2HtmlConverter.INSTANCE );
			put(MimeType.MIME_HTML, Noop2HtmlConverter.INSTANCE );
			put(MimeType.MIME_TXT, Noop2HtmlConverter.INSTANCE );

			put(MimeType.MIME_BMP, Image2HtmlConverter.INSTANCE );
			put(MimeType.MIME_JPEG, Image2HtmlConverter.INSTANCE );
			put(MimeType.MIME_PNG, Image2HtmlConverter.INSTANCE );
			put(MimeType.MIME_GIF, Image2HtmlConverter.INSTANCE );
			
			put(MimeType.MIME_ZIP, Zip2HtmlConverter.INSTANCE);
		}
	};
	
	
	private static interface IDocument2HtmlConverter {
		void transform(FileInfo doc, OutputStream os, int zoom) throws Exception;

	}

	private static class Zip2HtmlConverter implements IDocument2HtmlConverter{
		private static IDocument2HtmlConverter INSTANCE = new Zip2HtmlConverter();
		
		@Override
		public void transform(FileInfo doc, OutputStream os, int zoom)
				throws Exception {
			
			PrintStream printStream = new PrintStream(os);
			InputStream in = null;
			if(doc.getDriveId() != null){ 
				DomainGserviceaccount g = com.code.aon.google.apis.jooq.DBConsults.getServiceAccount(doc.getDomain(), doc.getDomainId());
				Drive d = DriveUtils.serviceInitialize(g);
				File f = DriveUtils.getFile(d, doc.getDriveId(), doc.getFileId());//d.files().get(doc.getDriveId()).execute();
				
				in = DriveUtils.downloadFile(d, f);
			}
			else {
				ViewerUtils.RAttach rattach = ViewerUtils.getRAttach(doc.getFileId());
				byte[] b = rattach.bytes;				
				in = new ByteArrayInputStream(b);
			}
			ZipInputStream zip = new ZipInputStream(in);
			ZipEntry entry;
			String html="<div class='page' style=' width:150%s; background-color:#FFF;border-radius: 5px 5px 5px 5px;'>"
					+ "<table style='padding-top:10px; padding-bottom:5px;'>";
			while (null != (entry=zip.getNextEntry()) ){
				String icon = entry.isDirectory()?"aon-icon-google-drive-folder":"aon-icon-google-drive-unknown";
				if(!entry.getName().substring(0,entry.getName().length()-1).contains("/")){
					if(entry.isDirectory())
						html = html + "<tr><td style='padding-left:5px;'><button onclick='alert(hola);' class='aon-editDataTable-button "+icon+"' style='padding-left: 20px;'>"+entry.getName()+"</button></td></tr>";
					else{
						html = html + "<tr><td style='padding-left:5px;'><span class='"+icon+"' style='padding-left: 20px;'>"+entry.getName()+"</span></td></tr>";
					}
				}
			}
			html = html + "</table></div>";
			System.out.println(html);
			printStream.printf(html,"%");
		}

	}
private static class OpenDocument2HtmlConverter implements IDocument2HtmlConverter {
		
		private static  IDocument2HtmlConverter INSTANCE = new OpenDocument2HtmlConverter();

		@Override
		public void transform(FileInfo doc, OutputStream os, int zoom) throws Exception {
			
			PrintStream printStream = new PrintStream(os);
			
			PDFFile pdfFile = OpenDocument2ImageServlet.getPDFFile(doc);
			
			for (int page = 1; page <= pdfFile.getNumPages(); page++) {
				
				PDFPage pdfPage = pdfFile.getPage(page);

				// get the width and height for the doc at the default zoom
				double width =  pdfPage.getBBox().getWidth() * zoom / 100 ;
				double height = pdfPage.getBBox().getHeight() * zoom / 100 ;
				
				printStream.printf("<div class='page' style='width:%dpx;height:%dpx;'   ><img src='openDocument2Image/%s.png?%s=%d&%s=%d&id=%d'></img> </div>",
						(long)width,
						(long)height,
						doc.getMd5(),
						OpenDocument2ImageServlet.PAGE_PARAM,
						page,
						OpenDocument2ImageServlet.ZOOM_PARAM,
						zoom,
						doc.getFileId());
			}		
		}

	}

private static class Noop2HtmlConverter  extends  Document2HtmlConverter  {
	
	private static  IDocument2HtmlConverter INSTANCE = new Noop2HtmlConverter();
	
	
	@Override
	void transform(InputStream is, OutputStream os) throws Exception {
		int read ;
		byte buffer [] = new byte [256];
		while ( ( read = is.read(buffer)) == buffer.length ) {
			os.write(buffer, 0, read);
		}
	}
}
private static class Image2HtmlConverter implements IDocument2HtmlConverter {
	
	private static  IDocument2HtmlConverter INSTANCE = new Image2HtmlConverter();

	@Override
	public void transform(FileInfo doc, OutputStream os, int zoom) throws Exception {
		
		PrintStream printStream = new PrintStream(os);
		
		MimeType mimeType = MimeType.values()[doc.getMimetype()];
		
		printStream.printf("<div class='page'  ><img src='openDocumentConverter/%s.%s?id=%d'></img> </div>",
				doc.getMd5(),
				mimeType.getExtension(),
				doc.getFileId());
	}
}
private abstract static class Document2HtmlConverter implements IDocument2HtmlConverter{
	@Override
	public void transform(FileInfo doc, OutputStream os, int zoom) throws Exception {
		Connection conn = null;

		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(
					"SELECT " + RattachColumns.DATA
					+ " FROM " + SQLConstants.RATTACH + " WHERE "
					+ RattachColumns.ID + "= ? ");

			stmt.setInt(1, doc.getFileId());
			
			rs = stmt.executeQuery();
			
			if (!rs.next()) {
				throw new IllegalArgumentException();
			}

			InputStream is = rs.getBinaryStream(RattachColumns.DATA);
			transform(is, os);
		}
		catch (Exception e ) {
			throw new IllegalArgumentException(e);
		}
		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					throw new IllegalArgumentException(e);
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					throw new IllegalArgumentException(e);
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					throw new IllegalArgumentException(e);
				}
			}
		}
	}
	



	abstract void transform(InputStream is, OutputStream os) throws Exception;
	
}
static Integer size;


public Integer getSize() {
	return size;
}


public static void setSize(Integer sizea) {
	size = sizea;
}

//-------------------- Administrar tags & categories

public Tag newTag(String name) {
	initAux();
	String domain = AonUtil.getDomainName();
	Integer id = null;
	String dom = "";
	try {
		dom = DBConsults.getDomain(domain, domainId);
		id = DBConsults.newTag(domain,domainId,name);
	} catch (SQLException e) {
		e.printStackTrace();
	}
	Tag t = new Tag();
	t.setId(id);
	t.setIsParent(false);
	t.setIsSon(false);
	t.setDomain(dom);
	t.setName(name);
	
	return t;
}

public void editTag(String name, Integer id) {
	initAux();
	String domain = AonUtil.getDomainName();
	try {
		DBConsults.editTag(domain,name,id);
	} catch (SQLException e) {
		e.printStackTrace();
	}

}

public void deleteTag(Integer tagId) {
	initAux();
	String domain = AonUtil.getDomainName();
	try {
		DBConsults.deleteTag(domain,tagId);
	} catch (SQLException e) {
		e.printStackTrace();
	}
}

public Category newCategory(String name) {
	initAux();
	String domain = AonUtil.getDomainName();
	Integer id = null;
	String dom ="";
	try {
		dom = DBConsults.getDomain(domain, domainId);
		id = DBConsults.newCategory(domain,domainId,name);
	} catch (SQLException e) {
		e.printStackTrace();
	}
	Category c = new Category();
	c.setDomain(dom);
	c.setId(id);
	c.setIsParent(false);
	c.setIsSon(false);
	c.setName(name);
	return c;
}

public void editCategory(String name, Integer id) {
	initAux();
	String domain = AonUtil.getDomainName();
	try {
		DBConsults.editCategory(domain,name,id);
	} catch (SQLException e) {
		e.printStackTrace();
	}

}

public void deleteCategory(Integer categoryId) {
	initAux();
	String domain = AonUtil.getDomainName();
	try {
		DBConsults.deleteCategory(domain,categoryId);
	} catch (SQLException e) {
		e.printStackTrace();
	}
}

//-------------------- Enviar Email

public MailAccountList getMailAccounts() {
	String domain = AonUtil.getDomainName();
	Integer user_id=AonUtil.getAuthPrincipal().getUserId();
	Integer userDomainId = AonUtil.getAuthPrincipal().getUserDomainId();

	MailAccountList mal = new MailAccountList();
	try {
		mal = SendEmailDialogJooq.getMailAccounts(domain, user_id,userDomainId);
	} catch (SQLException e) {
		e.printStackTrace();
	}
	mal.setContactList(getContacts());
	return mal;
}

	public void sendEmail(MailAccount ma, Emessage em) {
		// IMailAccount ima = Utils.getIMailAccount(ma);
		String domain = AonUtil.getDomainName();
			try {
				initFacesContext();
				MailConfigController mcg = (MailConfigController) AonUtil
						.getRegisteredBean(IWebMailConstants.BEAN_MAIL_CONFIG);

				IMailAccount ima2 = mcg.getDefaultMailAccount(true);
				List<IMailAccount> imas = mcg.getIMailAccounts();
				for (IMailAccount iMailAccount : imas) {
					if (ma.getEmail().equals(iMailAccount.getEmail())) {
						ima2 = iMailAccount;
					}
				}
				System.out.println(ima2.getDisplayName());
				AonServer server = new AonServer(ima2);

				MessageController mc = new MessageController();
				mc.setRecipientsBcc(em.getRecipientsBcc());
				mc.setRecipientsCc(em.getRecipientsCc());
				mc.setRecipientsTo(em.getRecipientsTo());
				mc.setContent(em.getContent());
				mc.setSenderMailAccount(ima2);
				mc.setSubject(em.getSubject());

				Vector<FileInfo> files = getOuts();

				ZipOutputStream zos;
				try {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					zos = new ZipOutputStream(baos);
					for (FileInfo fi : em.getFiles()) {
						if (fi.getDriveId() != null) {
							Drive d = null;
							if (fi.getIsDrive()) {
								d = GoogleDriveController.dconnection;
							} else {
								DomainGserviceaccount g;
								try {
									g = com.code.aon.google.apis.jooq.DBConsults.getServiceAccount(fi.getDomain(),fi.getDomainId());
									d = DriveUtils.serviceInitialize(g);
								} catch (SQLException e) {
									e.printStackTrace();
								} catch (KeyStoreException e) {
									e.printStackTrace();
								} catch (GeneralSecurityException e) {
									e.printStackTrace();
								}
							}
							com.google.api.services.drive.model.File f = d
									.files().get(fi.getDriveId()).execute();
							InputStream in = DriveUtils.downloadFile(d, f);
							byte[] b = com.code.aon.google.apis.Utils
									.InputStreamToByte(in);
							fi.setData(b);
						} else if ((Integer) fi.getFileId() != null) {
							try {
								com.code.aon.google.apis.FileInfo fi2 = DBConsults
										.getDataAndName(fi.getFileId(), domain);
								fi.setData(fi2.getData());
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}

						zos.putNextEntry(new ZipEntry(fi.getTitle()
								+ "."
								+ MimeType.values()[fi.getMimetype()]
										.getExtension()));
						zos.write(fi.getData());
						zos.closeEntry();
					}
					zos.close();
					FileInfo fileInfo = new FileInfo();
					fileInfo.setData(baos.toByteArray());
					fileInfo.setTitle("lote.zip");
					fileInfo.setMimetype((byte) MimeType.MIME_ZIP.ordinal());
					files.add(fileInfo);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				mc.initNewMsgFileList();
				for (FileInfo fi : files) {
					AonFile aonFile = new AonFile();
					java.io.File file = new java.io.File("/tmp/"
							+ fi.getTitle());
					try {
						org.apache.commons.io.FileUtils.writeByteArrayToFile(
								file, fi.getData());
					} catch (IOException e) {
						e.printStackTrace();
					}
					aonFile.setFile(file);
					aonFile.setData(fi.getData());
					aonFile.setFileName(fi.getTitle());
					aonFile.setMimeType(MimeType.get(fi.getMimeString()));
					mc.addAttachment(aonFile);
				}

				try {
					AonMessage sentMessage = mc.compoundMessage(server);// Utils.getAonMessage(server,ma);
					server.sendMessage(sentMessage);
				} catch (WebmailException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			} finally {
				releaseFacesContext();
			}
			setOuts(new Vector<FileInfo>());

	}
	
	public void sendGmail(MailAccount ma, Emessage em) {
		String domain = AonUtil.getDomainName();
		Gmail gmail = GoogleDriveController.uconnection.getGmail();

		Vector<FileInfo> files = getOuts();

		ZipOutputStream zos;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			zos = new ZipOutputStream(baos);
			for (FileInfo fi : em.getFiles()) {
				if (fi.getDriveId() != null) {
					Drive d = null;
					if (fi.getIsDrive()) {
						d = GoogleDriveController.dconnection;
					} else {
						DomainGserviceaccount g;
						try {
							g = com.code.aon.google.apis.jooq.DBConsults.getServiceAccount(domain, domainId);
							d = DriveUtils.serviceInitialize(g);
						} catch (SQLException e) {
							e.printStackTrace();
						} catch (KeyStoreException e) {
							e.printStackTrace();
						} catch (GeneralSecurityException e) {
							e.printStackTrace();
						}
					}
					com.google.api.services.drive.model.File f= null;
					try {
						f = DriveUtils.getFile(d, fi.getDriveId(), fi.getFileId());
					} catch (SQLException | GeneralSecurityException e) {
						e.printStackTrace();
					}
					InputStream in = DriveUtils.downloadFile(d, f);
					byte[] b = com.code.aon.google.apis.Utils
							.InputStreamToByte(in);
					fi.setData(b);
				} else if ((Integer) fi.getFileId() != null) {
					try {
						com.code.aon.google.apis.FileInfo fi2 = DBConsults
								.getDataAndName(fi.getFileId(), domain);
						fi.setData(fi2.getData());
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}

				zos.putNextEntry(new ZipEntry(fi.getTitle()
						+ "."
						+ MimeType.values()[fi.getMimetype()]
								.getExtension()));
				zos.write(fi.getData());
				zos.closeEntry();
			}
			zos.close();
			FileInfo fileInfo = new FileInfo();
			fileInfo.setData(baos.toByteArray());
			fileInfo.setTitle("lote.zip");
			fileInfo.setMimetype((byte) MimeType.MIME_ZIP.ordinal());
			files.add(fileInfo);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Vector<BodyPart> bodyParts = new Vector<BodyPart>();
		for (FileInfo fi : files) {
			AonFile aonFile = new AonFile();
			java.io.File file = new java.io.File("/tmp/" + fi.getTitle());
			try {
				org.apache.commons.io.FileUtils.writeByteArrayToFile(file,
						fi.getData());
			} catch (IOException e) {
				e.printStackTrace();
			}
			aonFile.setFile(file);
			aonFile.setData(fi.getData());
			aonFile.setFileName(fi.getTitle());
			aonFile.setMimeType(MimeType.get(fi.getMimeString()));
			BodyPart bodyPart = null;
			try {
				bodyPart = WebmailUtil.getBodyPart(aonFile);
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			bodyParts.add(bodyPart);
		}
		try {
			MimeMessage email = GmailUtils.createEmailWithAttachments(
					em.getRecipientsTo(), ma.getEmail(), em.getSubject(),
					em.getContent(), bodyParts);
			GmailUtils.sendMessage(gmail, "me", email); 

		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setOuts(new Vector<FileInfo>());

	}

public  ContactList getContacts() {	
	String domain = AonUtil.getDomainName();
	Integer user_id=AonUtil.getAuthPrincipal().getUserId();
	Integer userDomainId = AonUtil.getAuthPrincipal().getUserDomainId();

	ContactList cl = new ContactList();
	try {
		cl = DBConsults.getContacts(user_id, domain, userDomainId);
	} catch (AonConnectionException e) {
		e.printStackTrace();
	} catch (SQLException e) {
		e.printStackTrace();
	}
	return cl;
}

public static Vector<FileInfo> down;

public void downloadMultiple(Vector<FileInfo> fvector){
	setDown(fvector);

}

public static Vector<FileInfo> getDown() {
	return down;
}

public static void setDown(Vector<FileInfo> down) {
	DocumentsServlet.down = down;
}

public Vector<FileInfo> insertFileMultiple(FileInfo fi) {
	Vector<FileInfo> files = getOuts();
	for(FileInfo f : files){
	Date date = null;
	if (fi.getDate() != null)
		date = new Date(fi.getDate().getYear(),
				fi.getDate().getMonth(), fi.getDate().getDate());


	String domain = AonUtil.getDomainName();
	try {
		domain= DBConsults.getDomain(domain, domainId);
	} catch (SQLException e1) {
		e1.printStackTrace();
	}
	// Integer registry = AdminUtil.getCompanyId(fi.getDomainId());
	com.code.aon.google.apis.FileInfo fileInfo = new com.code.aon.google.apis.FileInfo();
	fileInfo.setAonType("registry");
	if (fi.getCategory() != -1){
		fileInfo.setCategory(fi.getCategory());
		f.setCategory(fi.getCategory());
		f.setCategoryStr(fi.getCategoryStr());
	}
	else fileInfo.setCategory(null);
	
	fileInfo.setDateSql(date);
	f.setDateSql(date);
	f.setDate(fi.getDate());
	f.setDateStr(fi.getDateStr());
	
	fileInfo.setMimetype(f.getMimetype());
	f.setIcon(Utils.icon(MimeType.values()[f.getMimetype()].getName()));
	fileInfo.setTitle(f.getTitle());
	
	fileInfo.setType((short)RegistryAttachmentType.CORPORATE_IDENTITY.ordinal());// TODO tipo correcto!!
	f.setType((short)RegistryAttachmentType.CORPORATE_IDENTITY.ordinal());
	if (fi.getScope().getId() != -1){
		fileInfo.setScopeId(fi.getScope().getId());
		f.setScope(fi.getScope());
	}
	else fileInfo.setScopeId(null);
	
	Byte conf;
	if (fi.getConfidential())
		conf = 1;
	else
		conf = 0;
	fileInfo.setSecurityLevel(conf);
	f.setConfidential(fi.getConfidential());
	if (!fi.getDomain().equals("")){
		domain = fi.getDomain();
	
	}
	f.setDomain(domain);
		
	try {
		Integer domainId = DBConsults.getDomainId(domain, domain);
		fileInfo.setDomainId(domainId);
		fileInfo.setSize(f.getSize());
		Integer id = DBConsults.insertFile(domain, fileInfo);
		f.setFileId(id);
		DBConsults.insertTagsFile(id, fi.getTags(), domain, domainId);
		byte[] b = f.getData();//.toByteArray();
		fileInfo.setFileId(id);
		fileInfo.setData(b);
		DomainGserviceaccount g = com.code.aon.google.apis.jooq.DBConsults.getServiceAccount(domain,domainId);
		if (g.getClientId()!=null){
			Drive d = DriveUtils.serviceInitialize(g);
			String[] types = { RegistryAttachmentType.CORPORATE_IDENTITY.toString() };// TODO
			DriveUtils.types = types;
			fileInfo.setDomain(domain);
			DriveUtils.sync2(d, fileInfo, domain);
		}
		else DBConsults.insertFileData(domain, id, b );
		//insertFile(d, fileInfo,b);//DriveUtils.insertFile(d, fileInfo, new Vector<ParentReference>(), new Vector<String>(), domain);
		fi = DBConsults.getFile(domain, id);
		if(fi.getDomain()==null) fi.setDomain(domain);

	} catch (SQLException e) {
		e.printStackTrace();
	} catch (KeyStoreException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} catch (GeneralSecurityException e) {
		e.printStackTrace();
	} catch (AonConnectionException e) {
		e.printStackTrace();
	} catch (NamingException e) {
		e.printStackTrace();
	}
	}
	setOuts(new Vector<FileInfo>());
	return files;
}
// ----------------------------------------------------------------------------
	/**
	 * <p>
	 * Checks if CharSequence contains a search CharSequence irrespective of
	 * case, handling {@code null}. Case-insensitivity is defined as by
	 * {@link String#equalsIgnoreCase(String)}.
	 *
	 * <p>
	 * A {@code null} CharSequence will return {@code false}.
	 * </p>
	 *
	 * <pre>
	 * StringUtils.contains(null, *) = false
	 * StringUtils.contains(*, null) = false
	 * StringUtils.contains("", "") = true
	 * StringUtils.contains("abc", "") = true
	 * StringUtils.contains("abc", "a") = true
	 * StringUtils.contains("ábc", "a") = true
	 * StringUtils.contains("abc", "z") = false
	 * StringUtils.contains("abc", "A") = true
	 * StringUtils.contains("ábc", "A") = true
	 * StringUtils.contains("abc", "Z") = false
	 * </pre>
	 * @param str
	 * @param searchStr
	 * @return
	 */
	public static boolean containsIgnoreCase2(String str, String searchStr) {
	    Locale locale = new Locale("es_ES");
		Collator c = Collator.getInstance(locale);
		c.setStrength(Collator.PRIMARY);
	    if (str == null || searchStr == null) {
	        return false;
	    }
	    int len = searchStr.length();
	    int max = str.length() - len;
	    for (int i = 0; i <= max; i++) {   	
	    	if (c.compare(str.substring(i, i+len), searchStr) == 0)
	    		return true;  
	    }
	    return false;
	}
}
