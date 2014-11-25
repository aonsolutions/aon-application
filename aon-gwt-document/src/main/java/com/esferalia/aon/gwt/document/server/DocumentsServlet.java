package com.esferalia.aon.gwt.document.server;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Vector;

import javax.naming.NamingException;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.google.apis.DatabaseSync;
import com.code.aon.google.apis.DriveUtils;
import com.code.aon.google.apis.drive.ShareFiles;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.google.apis.controller.GoogleDriveController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.google.sql.AbstractSQL.DomainGserviceaccount;
import com.esferalia.aon.gwt.document.client.IDocument;
import com.esferalia.aon.gwt.document.jooq.DBConsults;
import com.esferalia.aon.gwt.document.shared.Document;
import com.esferalia.aon.gwt.document.shared.FileInfo;
import com.esferalia.aon.gwt.document.shared.FilterUtil;
import com.esferalia.aon.gwt.document.shared.Lists;
import com.esferalia.aon.gwt.document.shared.SearchInfo;
import com.esferalia.aon.gwt.document.shared.Tags;
import com.esferalia.aon.gwt.document.shared.TreeDriveInfo;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.About;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;


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

	public Document getAllFiles(){
		String domain = AonUtil.getDomainName();
		Document docs = new Document();
		try {
			docs  = DBConsults.getAllRattach(domain);
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//docs.setFiles(DBConsults.getFilesGwt());
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
			if(StringUtils.contains(fileInfo.getTitle(), searchStr)){
				
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
			if(!StringUtils.contains(fi.getTitle(), si.getName())){
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
	
	public Vector<String> getSons(){
		String domain = AonUtil.getDomainName();
		
		Vector<String> vector = new Vector<String>();
		try {
			vector = DBConsults.getSons(domain);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		vector.add(domain);
		return vector;
		
		
		
	}
	
	public Lists getLists(){
		String domain = AonUtil.getDomainName();
		Lists lists= new Lists();
		
		try {
			lists.setCategoryList(DBConsults.getCategoryList(domain));
			lists.setCategoryListSon(DBConsults.getCategoryListSon(domain));
			lists.setScopeList(DBConsults.getScopeList(domain));
			lists.setScopeListSon(DBConsults.getScopeListSon(domain));
			lists.setTagList(DBConsults.getTagList(domain));
			lists.setTagListSon(DBConsults.getTagListSon(domain));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		return lists;
	}
	
	public void removeFile(FileInfo fi){
		
		String domain = AonUtil.getDomainName();
		try {
			DBConsults.removeFile(domain, fi.getFileId());
			if(fi.getDriveId()!=null){
				DomainGserviceaccount g = DatabaseSync.getServiceAccount(domain);
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
	public static byte[] out;
	
	
	public static byte[] getOut() {
		return out;
	}

	public static void setOut(byte[] out2) {
		out = out2;
	}

	public Boolean newFile(FileInfo fi) {
		if (getMimetype()!=null
				&& !fi.getTitle().equals("") && !fi.getDomain().equals("false")) {
		
			return true;
		}
		return false;

	}
	
	public FileInfo insertFile(FileInfo fi){
		Date date = null;
		if (fi.getDate() != null)
			date = new Date(fi.getDate().getYear(),
					fi.getDate().getMonth(), fi.getDate().getDate());

		String domain = AonUtil.getDomainName();
		// Integer registry = AdminUtil.getCompanyId(fi.getDomainId());
		com.code.aon.google.apis.FileInfo fileInfo = new com.code.aon.google.apis.FileInfo();
		fileInfo.setAonType("registry");
		if (fi.getCategory() != -1)
			fileInfo.setCategory(fi.getCategory());
		else fileInfo.setCategory(null);
		fileInfo.setDateSql(date);
		fileInfo.setMimetype((byte) MimeType.get(getMimetype()).ordinal());
		fileInfo.setTitle(fi.getTitle());
		fileInfo.setType((short) 5);// TODO tipo correcto!!
		if (fi.getScope().getId() != -1)
			fileInfo.setScopeId(fi.getScope().getId());
		else fileInfo.setScopeId(null);
		Byte conf;
		if (fi.getConfidential())
			conf = 1;
		else
			conf = 0;
		fileInfo.setSecurityLevel(conf);
		String dom;
		if (fi.getDomain().equals("")){
			dom = domain;
			fi.setDomain(domain);
		}
		else
			dom = fi.getDomain();
		try {
			Integer domainId = DBConsults.getDomainId(domain, dom);
			fileInfo.setDomainId(domainId);
			Integer id = DBConsults.insertFile(domain, fileInfo);
			DBConsults.insertTagsFile(id, fi.getTags(), domain, domainId);
			byte[] b = getOut();//.toByteArray();
			fileInfo.setFileId(id);
			fileInfo.setData(b);
			DomainGserviceaccount g = DatabaseSync
					.getServiceAccount(domain);
			if (g!=null){
				Drive d = DriveUtils.serviceInitialize(g);
				String[] types = { RegistryAttachmentType.DOCUMENT.toString() };// TODO
				DriveUtils.types = types;
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
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		}
		return fi;
	}
	
	public Boolean check(){
		return getMimetype()==null;//.equals("application/octet-stream");
			
		
	}
	
	public void editFile(FileInfo fi){
		String domain = AonUtil.getDomainName();
		//Integer registry = AdminUtil.getCompanyId(fi.getDomainId());
		com.code.aon.google.apis.FileInfo fileInfo = new com.code.aon.google.apis.FileInfo();
		fileInfo.setFileId(fi.getFileId());
		fileInfo.setAonType("registry");
		fileInfo.setCategory(fi.getCategory());
		fileInfo.setDate(fi.getDate());
		if(getMimetype()!=null)fileInfo.setMimetype((byte)MimeType.get(getMimetype()).ordinal());
		else fileInfo.setMimetype(fi.getMimetype());
		fileInfo.setTitle(fi.getTitle());
		fileInfo.setScopeId(fi.getScope().getId());
		Byte conf;if(fi.getConfidential())conf=1; else conf=0;
		fileInfo.setSecurityLevel(conf);
		try {
			DBConsults.updateFile(domain, fileInfo);
			if(getMimetype()!=null){
				//InputStream file = getFile();
				byte[] b = getOut();//.toByteArray();
				fileInfo.setData(b);
				DomainGserviceaccount g = DatabaseSync.getServiceAccount(domain);
				Drive d = DriveUtils.serviceInitialize(g);
				DriveUtils.sync2(d, fileInfo, domain);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public Boolean isGconnection() {
		return GoogleDriveController.gconnection;
		
		
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

	public void share(String email, String driveId) {
		String domain = AonUtil.getDomainName();
		if (driveId != null) {
			try {
				DomainGserviceaccount g = DatabaseSync.getServiceAccount(domain);
				Drive d = DriveUtils.serviceInitialize(g);
				ShareFiles.setPermission(d, driveId, email);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GeneralSecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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

}
