package com.esferalia.aon.gwt.document.server;

import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.getConnection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.google.apis.DatabaseSync;
import com.code.aon.google.apis.DriveFile;
import com.code.aon.google.apis.DriveUtils;
import com.code.aon.google.apis.drive.ShareFiles;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.google.apis.controller.GoogleDriveController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.google.sql.AbstractSQL.DomainGserviceaccount;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.document.client.IDocument;
import com.esferalia.aon.gwt.document.client.Utils;
import com.esferalia.aon.gwt.document.jooq.DBConsults;
import com.esferalia.aon.gwt.document.shared.Category;
import com.esferalia.aon.gwt.document.shared.Document;
import com.esferalia.aon.gwt.document.shared.Domain;
import com.esferalia.aon.gwt.document.shared.FileInfo;
import com.esferalia.aon.gwt.document.shared.FilterUtil;
import com.esferalia.aon.gwt.document.shared.Lists;
import com.esferalia.aon.gwt.document.shared.SearchInfo;
import com.esferalia.aon.gwt.document.shared.Tag;
import com.esferalia.aon.gwt.document.shared.Tags;
import com.esferalia.aon.gwt.document.shared.TreeDriveInfo;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.RattachColumns;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.About;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;
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
	public Boolean initAux(){
		try{initFacesContext();
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		domainId = ds.getDomainId();
		confidential = AonUtil.getRoleManager().isConfidentiality();
		Boolean documentManager = AonUtil.getRoleManager().isDocumentManager();
		return documentManager;
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
			if(StringUtils.containsIgnoreCase(fileInfo.getTitle(), searchStr)){
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
		Lists lists= new Lists();
		
		try {
			domain = DBConsults.getDomain(domain, domainId);
			lists.setCategoryList(DBConsults.getCategoryList(domain));
			lists.setCategoryListSon(DBConsults.getCategoryListSon(domain));
			lists.setScopeList(DBConsults.getScopeList(domain,user_id));
			lists.setScopeListSon(DBConsults.getScopeListSon(domain,user_id));
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

		/*initFacesContext();
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		Integer domainId2 = ds.getDomainId();*/
		String domain = AonUtil.getDomainName();
		try {
			domain= DBConsults.getDomain(domain, domainId);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		// Integer registry = AdminUtil.getCompanyId(fi.getDomainId());
		com.code.aon.google.apis.FileInfo fileInfo = new com.code.aon.google.apis.FileInfo();
		fileInfo.setAonType("registry");
		if (fi.getCategory() != -1)
			fileInfo.setCategory(fi.getCategory());
		else fileInfo.setCategory(null);
		fileInfo.setDateSql(date);
		fileInfo.setMimetype((byte) MimeType.get(getMimetype()).ordinal());
		fileInfo.setTitle(fi.getTitle());
		fileInfo.setType((short)RegistryAttachmentType.CORPORATE_IDENTITY.ordinal());// TODO tipo correcto!!
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
			//Integer domainId = DBConsults.getDomainId(domain, dom);
			fileInfo.setDomainId(domainId);
			fileInfo.setSize((Integer) getSize());
			Integer id = DBConsults.insertFile(domain, fileInfo);
			DBConsults.insertTagsFile(id, fi.getTags(), domain, domainId);
			byte[] b = getOut();//.toByteArray();
			fileInfo.setFileId(id);
			fileInfo.setData(b);
			DomainGserviceaccount g = DatabaseSync
					.getServiceAccount(domain);
			if (g.getClientId()!=null){
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
		if(fi.getCategory()!=null)fileInfo.setCategory(fi.getCategory());
		if(fi.getDate() != null){
			fileInfo.setDate(fi.getDate());
			Date date = null;
			if (fi.getDate() != null)
			date = new Date(fi.getDate().getYear(),
					fi.getDate().getMonth(), fi.getDate().getDate());
			fileInfo.setDateSql(date);
		}
		if(getMimetype()!=null)fileInfo.setMimetype((byte)MimeType.get(getMimetype()).ordinal());
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
				DomainGserviceaccount g = DatabaseSync.getServiceAccount(domain);
				if(g.getClientId()!=null){
					Drive d = DriveUtils.serviceInitialize(g);
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
			fi.setDateStr("-");
			fi.setSizeStr("-");
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
			fi.setDateStr("-");
			fi.setSize(0);
			fi.setSizeStr("-");
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

	public void upload(FileInfo fi){
		Drive drive = GoogleDriveController.dconnection;
		byte[] b = getOut();
		java.io.File aux = new java.io.File("/tmp/" + fi.getTitle());
		
		DriveFile file=new DriveFile("","", fi.getTitle(), getMimetype());

		try {
			org.apache.commons.io.FileUtils.writeByteArrayToFile(aux, b);
			DriveUtils.insertFile(drive,aux, file);
		} catch (IOException e) {
 			e.printStackTrace();
		}
	}
	
	public void deleteMydrive(FileInfo fi){
		Drive drive = GoogleDriveController.dconnection;
		try {
			DriveUtils.deleteFile(drive, fi.getDriveId());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void shareMydrive(String email, String driveId){
		Drive drive = GoogleDriveController.dconnection;
		try {
			ShareFiles.setPermission(drive, driveId, email);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void share(String email, String driveId) {
		String domain = AonUtil.getDomainName();
		if (driveId != null) {
			try {
				DomainGserviceaccount g = DatabaseSync.getServiceAccount(domain);
				Drive d = DriveUtils.serviceInitialize(g);
				ShareFiles.setPermission(d, driveId, email);
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
				DomainGserviceaccount g = DatabaseSync.getServiceAccount(AonUtil.getDomainName());
				Drive d = DriveUtils.serviceInitialize(g);
				File f = d.files().get(doc.getDriveId()).execute();
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
				
				printStream.printf("<div class='page' style='width:%dpx;height:%dpx;'   ><img src='openDocument2Image/%d.png?%s=%d&%s=%d'></img> </div>",
						(long)width,
						(long)height,
						doc.getFileId(),
						OpenDocument2ImageServlet.PAGE_PARAM,
						page,
						OpenDocument2ImageServlet.ZOOM_PARAM,
						zoom);
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
		
		printStream.printf("<div class='page'  ><img src='openDocumentConverter/%d.%s'></img> </div>",
				doc.getFileId(),
				mimeType.getExtension());
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
}
