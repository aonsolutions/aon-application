package com.esferalia.aon.gwt.document.server;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.sql.Date;
import java.text.Collator;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.util.AonFile;
import com.code.aon.faces.controller.IRichConstants;
import com.code.aon.faces.controller.SelectedMenuController;
import com.code.aon.google.apis.DriveFile;
import com.code.aon.google.apis.DriveUtils;
import com.code.aon.google.apis.GmailUtils;
import com.code.aon.google.apis.UrlShortenerUtils;
import com.code.aon.google.apis.drive.ShareFiles;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
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
import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.document.client.IDocument;
import com.esferalia.aon.gwt.document.client.Utils;
import com.esferalia.aon.gwt.document.jooq.DBConsults;
import com.esferalia.aon.gwt.document.jooq.SendEmailDialogJooq;
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
import com.esferalia.aon.gwt.document.shared.Tags;
import com.esferalia.aon.gwt.document.shared.TreeDriveInfo;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.io.AonFileUtils;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.About;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.Property;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.urlshortener.Urlshortener;


public class DocumentsServlet extends AonRemoteServiceServlet implements IDocument{

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DocumentsServlet.class.getName());
	
	private static final long serialVersionUID = 6871016881549113129L;

	private static InputStream file;
	private static String mimetype;
	public static Boolean serviconvenios;
	public static byte[] out;
	Boolean confidential;
	Map<Integer, SelectedMenuController> smc = new HashMap<Integer, SelectedMenuController>();
	
	public static Boolean getServiconvenios() {
		return serviconvenios;
	}

	public static void setServiconvenios(Boolean serviconvenios) {
		DocumentsServlet.serviconvenios = serviconvenios;
	}

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
	
	public User getUser(){
		return new User().setId(getUserID())
				.setLogin(getUserLogin())
				.setDomain(getUserDomainID());
	}


	protected void initFacesContext() {
		setServiconvenios(GoogleDriveController.serviconvenios);
		ServletContext context = getServletContext();
		HttpServletRequest request = getThreadLocalRequest();
		HttpServletResponse response = getThreadLocalResponse();
		AonServletUtils.initFacesContext(context, request, response);
	}

	protected void releaseFacesContext() {
		AonServletUtils.releaseFacesContext();
	}
	
	public Init initAux(Domain domain){
		try{
			initFacesContext();
			Vector<Boolean> v = new Vector<Boolean>();
			Init init = new Init();
			init.setDomainId(domain.getId());
			smc.put(domain.getId(),(SelectedMenuController) AonUtil.getRegisteredBean(IRichConstants.SELECTED_MENU_CONTROLLER_NAME));		
			confidential = AonUtil.getRoleManager().isConfidentiality();
			Boolean documentManager = AonUtil.getRoleManager().isDocumentManager();
			v.add(documentManager);
			v.add(confidential);
			init.setVector(v);
			return init;
		}finally{
			releaseFacesContext();
		}
	}
	
	public Document getAllFiles(Domain domain){
		Domain actualDomain = AON.getDomain(domain.getName(), domain.getId(), getUser().getLogin());
		Document docs  = DBConsults.getAllRattachNew(actualDomain, getUser(), domain.getName(), confidential);
		if(serviconvenios != null && getServiconvenios())
			docs.setServiconvenios(DBConsults.getServiConvenios(domain, getUser()));
	
		docs.setDomain(domain.getName());
		docs.setIsServiconvenios(getServiconvenios());
		return docs;
	}
	
	public Vector<FileInfo> getServiConveniosFiles(Domain domain){
		System.out.println("DOMAIN ID --> "+domain.getId());
		System.out.println("DOMAIN NAME --> "+domain.getName());
		System.out.println("");
		return DBConsults.getServiConvenios(domain, getUser());
	}
	
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
		for (FileInfo fileInfo : files){
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
			if(!containsIgnoreCase2(fi.getTitle(), si.getName())){
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
			if(fi.getScope() == null || !si.getScope().equals(fi.getScope().getName())){
				return false;
			}
		}
		if(si.getDomain() != null){
			if(!si.getDomain().equals(fi.getDomain()))
				return false;;
		}
		if(si.getCategoryList() != null && si.getCategoryList().size() > 0){
			Boolean bool = false;
			for(String s : si.getCategoryList()){
				if(s.equals(fi.getCategoryStr())) 
					bool = true;
			}
			if(!bool) return false;
		}
		if(si.getTagList() != null && si.getTagList().size() > 0){
			Boolean bool = false;
			for(String s : si.getTagList()){
				if(!Tags.contain(s,fi.getTags())) 
					bool = true;
			}
			if(bool) return false;
		}
		return true;
	}

	public LinkedList<Domain> getSons(Domain domain){		
		LinkedList<Domain> list = DBConsults.getSons(DBConsults.getDomain(domain, getUser()), getUser());
		list.add(domain.setDescription(""));
		return list;
	}
	
	public Lists getLists(Domain domain){
		Lists lists= new Lists();
		
		String currentDomain = DBConsults.getDomainName(domain, getUser());
		String domainZero = DBConsults.getDomainName(new Domain().setName(currentDomain).setId(0), getUser());
		lists.setCategoryListDomainZero(DBConsults.getCategoryList(new Domain().setName(domainZero).setId(domain.getId()), getUser()));
		lists.setCategoryList(DBConsults.getCategoryList(new Domain().setName(currentDomain).setId(domain.getId()), getUser()));
		lists.setCategoryListSon(DBConsults.getCategoryListSon(new Domain().setName(currentDomain).setId(domain.getId()), getUser()));
		lists.setScopeList(DBConsults.getScopeList(new Domain().setName(currentDomain).setId(domain.getId()), getUser()));
		lists.setScopeListSon(DBConsults.getScopeListSon(new Domain().setName(currentDomain).setId(domain.getId()), getUser()));
		lists.setTagListDomainZero(DBConsults.getTagList(new Domain().setName(domainZero).setId(domain.getId()), getUser()));
		lists.setTagList(DBConsults.getTagList(new Domain().setName(currentDomain).setId(domain.getId()), getUser()));
		lists.setTagListSon(DBConsults.getTagListSon(new Domain().setName(currentDomain).setId(domain.getId()), getUser()));
		return lists;
	}
	
	public void removeFile(Domain domain, Vector<FileInfo> fvector){
		for(FileInfo fi : fvector){
			try {
				DBConsults.removeFile(domain, getUser(), fi.getFileId());
				if(fi.getDriveId()!=null){
					DomainGserviceaccount g = com.code.aon.google.apis.jooq.DBConsults.getServiceAccount(domain.getName(),domain.getId());
					Drive d = DriveUtils.serviceInitialize(g);
					d.files().delete(fi.getDriveId()).execute();
				}
			} catch (KeyStoreException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (GeneralSecurityException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static Vector<FileInfo> getOuts(String dialogCode, HttpServletRequest request){
		Vector<FileInfo> outs = (Vector<FileInfo>) request.getSession().getAttribute("documentalDataOuts"+dialogCode);
		if(outs == null){
			outs = new Vector<FileInfo>();
			request.getSession().setAttribute("documentalDataOuts"+dialogCode, new Vector<FileInfo>());
		}
		return outs;
	}
	
	public static void clearOuts(String dialogCode, Vector<FileInfo> outs, HttpServletRequest request){
		if(outs == null)
			request.getSession().setAttribute("documentalDataOuts"+dialogCode, new Vector<FileInfo>());
		else
			request.getSession().setAttribute("documentalDataOuts"+dialogCode, outs);
	}
	
	public void clearOuts(String dialogCode){
		clearOuts(dialogCode, new Vector<FileInfo>(), getThreadLocalRequest());
	}
	
	public static void addOuts(String dialogCode, FileInfo fi, HttpServletRequest request){
		Vector<FileInfo> outs = (Vector<FileInfo>) request.getSession().getAttribute("documentalDataOuts"+dialogCode);
		if(outs == null){
			outs = new Vector<FileInfo>();
		}
		outs.add(fi);
		request.getSession().setAttribute("documentalDataOuts"+dialogCode, outs);
	}
	
	public static byte[] getOut() {
		return out;
	}

	public static void setOut(byte[] out2) {
		out = out2;
	}

	public Boolean newFile(String dialogCode, FileInfo fi) {
		Vector<FileInfo> files = getOuts(dialogCode, getThreadLocalRequest());
		if(files.size()>1 && !fi.getDomain().equals("false"))
			return true;
		if (files.size()>0
				&& !fi.getTitle().equals("") && !fi.getDomain().equals("false")) {
		
			return true;
		}
		return false;
	}
	
	public Vector<FileInfo> insertFile(Domain domain, String dialogCode, FileInfo fi) {
		long start = System.currentTimeMillis();

		Domain domainAux = DBConsults.getDomain(domain, getUser());
		Vector<FileInfo> files = getOuts(dialogCode, getThreadLocalRequest());
		Vector<FileInfo> vector = new Vector<FileInfo>();

		for (FileInfo f : files) {
			Date date = null;
			if (fi.getDate() != null){
				date = new Date(fi.getDate().getTime());
				f.setDate(fi.getDate());
			}
			com.code.aon.google.apis.FileInfo fileInfo = new com.code.aon.google.apis.FileInfo();
			fileInfo.setAonType("registry");
			if (fi.getCategory() != -1){
				fileInfo.setCategory(fi.getCategory());
			}
			else
				fileInfo.setCategory(null);
			fileInfo.setDateSql(date);
			fileInfo.setMimetype((byte) MimeType.get(f.getMimeString()).ordinal());
			if(files.size()<=1 && fi.getTitle()!=null){ 
				fileInfo.setTitle(fi.getTitle());
			}
			else fileInfo.setTitle(f.getTitle());
			fileInfo.setType((short) RegistryAttachmentType.CORPORATE_IDENTITY
					.ordinal());
			if (fi.getScope().getId() != -1){
				fileInfo.setScopeId(fi.getScope().getId());
			}
			else
				fileInfo.setScopeId(null);
			Byte conf;
			if (fi.getConfidential())
				conf = 1;
			else conf = 0;
			fileInfo.setSecurityLevel(conf);
			if(!fi.getDomain().equals("")){
				Integer domainId = DBConsults.getDomainId(domainAux, getUser(), fi.getDomain());
				domainAux = DBConsults.getDomain(new Domain().setName(fi.getDomain()).setId(domainId), getUser());
			}
			try {
				fileInfo.setDomainId(domain.getId());
				fileInfo.setSize((Integer) f.getSize());
				Integer id = DBConsults.insertFile(domainAux, getUser(),fileInfo);
				DBConsults.insertTagsFile(domainAux, getUser(), id, fi.getTags());
				byte[] b = f.getData();
				fileInfo.setFileId(id);
				fileInfo.setData(b);
				DomainGserviceaccount g = com.code.aon.google.apis.jooq.DBConsults.getServiceAccount(domainAux, getUser());
				if (g.getClientId() != null) {
					Drive d = DriveUtils.serviceInitialize(g);
					String[] types = { RegistryAttachmentType.CORPORATE_IDENTITY
							.toString() };// TODO
					DriveUtils.types = types;
					fileInfo.setDomainId(domainAux.getId());
					fileInfo.setDomain(domainAux.getName());
					DriveUtils.sync2(d, domainAux, getUser(), fileInfo);
				} else
					DBConsults.insertFileData(domain, getUser(), id, b);
							
				FileInfo fil = DBConsults.getFile(domainAux, getUser(), id);
				if (fil.getDomain() == null)
					fil.setDomain(domainAux.getName());
				if(fil.getDomainId() == null)
					fil.setDomainId(domainAux.getId());
				vector.add(fil);
				
			} catch (KeyStoreException e) {
				LOGGER.error(e.getMessage());
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
			} catch (GeneralSecurityException e) {
			}
		}
		clearOuts(dialogCode, new Vector<FileInfo>(), getThreadLocalRequest());
		long time = System.currentTimeMillis() - start;
		System.out.println("time: " + (time/1000d));
		return vector;
	}
	
	public Vector<FileInfo> insertFileBD(Domain domain, String dialogCode, FileInfo fi) {
		long start = System.currentTimeMillis();

		Domain domainAux = DBConsults.getDomain(domain, getUser());
		Vector<FileInfo> files = getOuts(dialogCode, getThreadLocalRequest());
		Vector<FileInfo> vector = new Vector<FileInfo>();

		for (FileInfo f : files) {
			Date date = null;
			if (fi.getDate() != null){
				date = new Date(fi.getDate().getTime());
				f.setDate(fi.getDate());
			}
			com.code.aon.google.apis.FileInfo fileInfo = new com.code.aon.google.apis.FileInfo();
			fileInfo.setAonType("registry");
			if (fi.getCategory() != -1){
				fileInfo.setCategory(fi.getCategory());
			}
			else
				fileInfo.setCategory(null);
			fileInfo.setDateSql(date);
			fileInfo.setMimetype((byte) MimeType.get(f.getMimeString()).ordinal());
			if(files.size()<=1 && fi.getTitle()!=null){ 
				fileInfo.setTitle(fi.getTitle());
			}
			else fileInfo.setTitle(f.getTitle());
			fileInfo.setType((short) RegistryAttachmentType.CORPORATE_IDENTITY
					.ordinal());
			if (fi.getScope().getId() != -1){
				fileInfo.setScopeId(fi.getScope().getId());
			}
			else
				fileInfo.setScopeId(null);
			Byte conf;
			if (fi.getConfidential())
				conf = 1;
			else conf = 0;
			fileInfo.setSecurityLevel(conf);
			if(!fi.getDomain().equals("")){
				Integer domainId = DBConsults.getDomainId(domainAux, getUser(), fi.getDomain());
				domainAux = DBConsults.getDomain(new Domain().setName(fi.getDomain()).setId(domainId), getUser());
			}
			fileInfo.setDomainId(domain.getId());
			fileInfo.setSize((Integer) f.getSize());
			Integer id = DBConsults.insertFile(domainAux, getUser(),fileInfo);
			DBConsults.insertTagsFile(domainAux, getUser(), id, fi.getTags());
			byte[] b = f.getData();
			fileInfo.setFileId(id);
			fileInfo.setData(b);
			DBConsults.insertFileData(domain, getUser(), id, b);
							
			FileInfo fil = DBConsults.getFile(domainAux, getUser(), id);
			if (fil.getDomain() == null)
				fil.setDomain(domainAux.getName());
			if(fil.getDomainId() == null)
				fil.setDomainId(domainAux.getId());
			vector.add(fil);
		}
		clearOuts(dialogCode, new Vector<FileInfo>(), getThreadLocalRequest());
		long time = System.currentTimeMillis() - start;
		System.out.println("time: " + (time/1000d));
		return vector;
	}
	
	public Boolean check(){
		return getMimetype()==null;
	}
	
	public Vector<FileInfo> editFile(Domain domain, String dialogCode, FileInfo fi,Vector<FileInfo> fvector){
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
					date = new Date(fi.getDate().getTime());
					fileInfo.setDateSql(date);
					
					f.setDate(fi.getDate());
					f.setDateSql(fi.getDateSql());
					f.setDateStr(fi.getDateStr());
				}else{
					fileInfo.setDate(f.getDate());
					Date date = null;
					if(f.getDate()!= null)
						date = new Date(f.getDate().getTime());
					fileInfo.setDateSql(date);
				}
				if(fi.getScope() != null){
					fileInfo.setScopeId(fi.getScope().getId());
					f.setScope(fi.getScope());
				}
				else if(f.getScope()!= null && f.getScope().getId() != null)
					fileInfo.setScopeId(f.getScope().getId());
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
				
				f.setModificationUser(getUser().getLogin());
	
				Calendar cal = Calendar.getInstance();
				String dateStr = cal.get(Calendar.DATE)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.YEAR);
				f.setModificationDateStr(dateStr);
				
				
				DBConsults.updateFile(domain, getUser(), fileInfo,tags);
				
				
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
				date = new Date(fi.getDate().getTime());
				fileInfo.setDateSql(date);
			}
			if(getMimetype()!=null){
				fileInfo.setMimetype((byte)MimeType.get(getMimetype()).ordinal());
				fi.setMimetype((byte)MimeType.get(getMimetype()).ordinal());
				fi.setIcon(Utils.icon(getMimetype()));
				fi.setSize(getSize());
				fi.setSizeStr(FileUtils.byteCountToDisplaySize(fi.getSize()));
			} 
			else fileInfo.setMimetype(fi.getMimetype());
			fileInfo.setSize(fi.getSize());
			fileInfo.setTitle(fi.getTitle());
			if(fi.getScope() != null)fileInfo.setScopeId(fi.getScope().getId());
			Byte conf;if(fi.getConfidential())conf=1; else conf=0;
			fileInfo.setSecurityLevel(conf);
		
			fi.setModificationUser(getUser().getLogin());
		
			Calendar cal = Calendar.getInstance();
			String dateStr = cal.get(Calendar.DATE)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.YEAR);
			fi.setModificationDateStr(dateStr);

			try {
				DBConsults.updateFile(domain, getUser(), fileInfo,fi.getTags());
				
				if(getMimetype()!=null){
					//InputStream file = getFile();
					byte[] b = getOut();//.toByteArray();
					fileInfo.setData(b);
					DomainGserviceaccount g = com.code.aon.google.apis.jooq.DBConsults.getServiceAccount(domain.getName(),domain.getId());
					if(g.getClientId()!=null){
						Drive d = DriveUtils.serviceInitialize(g);
						String[] types = { RegistryAttachmentType.CORPORATE_IDENTITY
								.toString() };// TODO
						DriveUtils.types = types;
						DriveUtils.sync2(d, domain, getUser(), fileInfo);
					}
					else{
						DBConsults.insertFileData(domain, getUser(), fileInfo.getFileId(), b );
					}
				}
			} catch (KeyStoreException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (GeneralSecurityException e) {
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

		DriveFile file=new DriveFile("","", fi.getTitle(), getMimetype());
		
		try {
			DriveUtils.insertFile(drive, new ByteArrayInputStream(b), file, parents);
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
	
	public void share(Domain domain, String email, Vector<FileInfo> fvector) {
		DomainGserviceaccount g;
		Drive d = null;
		try {
			g = com.code.aon.google.apis.jooq.DBConsults.getServiceAccount(domain.getName(), domain.getId());
			d = DriveUtils.serviceInitialize(g);
		} catch ( IOException | GeneralSecurityException e1) {
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
	
	
	public String getLink(Domain domain, FileInfo fileInfo, String l){
		Domain d = AON.getDomain(domain.getName(), domain.getId(), getUser().getLogin());
		String link;
		if(fileInfo.getDriveId() != null){
			File file =	DriveUtils.getDriveFile(d, getUser(), fileInfo.getDriveId(), fileInfo.getFileId(), true);
			link = file.getAlternateLink();
		} else{
			fileInfo = setmd5(domain, fileInfo);
			String md5 =fileInfo.getMd5();
			if(l.contains("aon_gwt_document")){
				Integer pos = l.lastIndexOf("/");
				Integer pos2 = l.substring(0, pos).lastIndexOf("/");
				l = l.substring(0,pos2);
			}
			link = l+"/aonDocuments/"+ fileInfo.getFileId() +"-"+ md5;
		}
		return link;
	}
	
	public String copyLink(FileInfo doc,String l){
		String link;
		Urlshortener u = null;
		DomainGserviceaccount g = null;
		Domain domain = DBConsults.getDomain(new Domain().setName(doc.getDomain()).setId(doc.getDomainId()), getUser());
		g = com.code.aon.google.apis.jooq.DBConsults.getServiceAccount(domain, getUser());
		try {
			u = UrlShortenerUtils.serviceInitialize(g);
		} catch (IOException | GeneralSecurityException e) {
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
				f = DriveUtils.getFile(d, domain, getUser(), doc.getDriveId(),doc.getFileId());
				if(f.getTitle().equals("OLDRIVE"))
					d = DriveUtils.serviceInitializeOld(g);
			} catch (IOException | GeneralSecurityException e) {
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
			doc = setmd5(domain, doc);
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
		}
		
		return link;
	}
	
	public FileInfo setmd5(Domain domain, FileInfo doc){
		Attach  rattach = ViewerUtils.getRAttach(domain, getUser(), doc.getFileId());
		Domain domainAux = DBConsults.getDomain(domain.setId(rattach.getDomain().getId()), getUser());
		doc.setDriveId(rattach.getDriveId());
		doc.setDomainId(domainAux.getId());
		doc.setDomain(domainAux.getName());
		doc.setDomainDescription(domainAux.getDescription());
		if(doc.getDriveId()!= null){
        	if(doc.getIsDrive()){
        		Drive drive = GoogleDriveController.dconnection;
        		File file = DriveUtils.getFile(drive, doc.getDriveId());
        		doc.setMd5(file.getMd5Checksum());
        	}
        	else{
        		File file = DriveUtils.getDriveFile(domainAux, getUser(), doc.getDriveId(), doc.getFileId(), false);
        		doc.setMd5(file.getMd5Checksum());
        	}
		}
		else {
			byte[] b = rattach.getData();
			doc.setMd5(AonFileUtils.getMD5Checksum(b));
		}
		return doc;
	}	
	
	
	static Integer size;

	public Integer getSize() {
		return size;
	}

	public static void setSize(Integer sizea) {
		size = sizea;
	}
//-------------------- Administrar tags & categories

public Tag newTag(Domain domain, String name) {
	initAux(domain);
	Integer id = null;
	String dom = "";
	
	dom = DBConsults.getDomainName(domain, getUser());
	id = DBConsults.newTag(domain, getUser(),name);

	Tag t = new Tag();
	t.setId(id);
	t.setIsParent(false);
	t.setIsSon(false);
	t.setDomain(dom);
	t.setName(name);
	
	return t;
}

public void editTag(Domain domain, String name, Integer id) {
	initAux(domain);
	DBConsults.editTag(domain, getUser(),name,id);
}

public void deleteTag(Domain domain, Integer tagId) {
	initAux(domain);
	DBConsults.deleteTag(domain,getUser(),tagId);
}

public Category newCategory(Domain domain, String name) {
	initAux(domain);
	Integer id = null;
	String dom ="";
	dom = DBConsults.getDomainName(domain, getUser());
	id = DBConsults.newCategory(domain,getUser(),name);
	Category c = new Category();
	c.setDomain(dom);
	c.setId(id);
	c.setIsParent(false);
	c.setIsSon(false);
	c.setName(name);
	return c;
}

public void editCategory(Domain domain, String name, Integer id) {
	initAux(domain);
	DBConsults.editCategory(domain, getUser(),name,id);
}

public void deleteCategory(Domain domain, Integer categoryId) {
	initAux(domain);
	DBConsults.deleteCategory(domain, getUser(), categoryId);
}

//-------------------- Enviar Email

public MailAccountList getMailAccounts(Domain domain) {
	MailAccountList mal = new MailAccountList();
	mal = SendEmailDialogJooq.getMailAccounts(domain, getUser(), getUser().getDomain());
	mal.setContactList(getContacts(domain));
	return mal;
}

	public void sendEmail(Domain domain, String dialogCode, MailAccount ma, Emessage em) {
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

				Vector<FileInfo> files = getOuts(dialogCode, getThreadLocalRequest());

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
								} catch (KeyStoreException e) {
									e.printStackTrace();
								} catch (GeneralSecurityException e) {
									e.printStackTrace();
								}
							}
							com.google.api.services.drive.model.File f = d
									.files().get(fi.getDriveId()).execute();
							InputStream in = DriveUtils.downloadFile(d, f);
							byte[] b = AonIOUtils.toByteArray(in);
							fi.setData(b);
						} else if ((Integer) fi.getFileId() != null) {
							com.code.aon.google.apis.FileInfo fi2 = DBConsults
									.getDataAndName(domain, getUser(), fi.getFileId());
							fi.setData(fi2.getData());
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
					fileInfo.setMimetype((byte) MimeType.ZIP.ordinal());
					files.add(fileInfo);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				mc.initNewMsgFileList();
				for (FileInfo fi : files) {
					AonFile aonFile = new AonFile();
					java.io.File file = new java.io.File("/tmp/" + fi.getTitle());
					file.deleteOnExit();
					try {
						org.apache.commons.io.FileUtils.writeByteArrayToFile(
								file, fi.getData());
					} catch (IOException e) {
						e.printStackTrace();
					}
					aonFile.setFile(file);
					aonFile.setData(fi.getData());
					aonFile.setFileName(fi.getTitle());
					aonFile.setMimeType(com.code.aon.common.enumeration.MimeType.get(fi.getMimeString()));
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
			clearOuts(dialogCode,new Vector<FileInfo>(), getThreadLocalRequest());

	}
	
	public void sendGmail(Domain domain,String dialogCode, MailAccount ma, Emessage em) {
		domain = AON.getDomain(domain.getName(), domain.getId(), getUser().getLogin());
		Gmail gmail = GoogleDriveController.uconnection.getGmail();

		Vector<FileInfo> files = getOuts(dialogCode, getThreadLocalRequest());
		
		ZipOutputStream zos;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			zos = new ZipOutputStream(baos);
			for (FileInfo fi : em.getFiles()) {
				if (fi.getDriveId() != null) {
					byte[] b;
					if (fi.getIsDrive()) {
		        		Drive drive = GoogleDriveController.dconnection;
		        		com.google.api.services.drive.model.File file = 
		        				DriveUtils.getFile(drive, fi.getDriveId());
		        		InputStream in = DriveUtils.downloadFile(drive, file);
		    			b = AonIOUtils.toByteArray(in);
					} else {
						b = DriveUtils.getByteFile(domain, getUser(), fi.getDriveId(), fi.getFileId());
					}
					fi.setData(b);
				} else if ((Integer) fi.getFileId() != null) {
					com.code.aon.google.apis.FileInfo fi2 = DBConsults
							.getDataAndName(domain, getUser(), fi.getFileId());
					fi.setData(fi2.getData());
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
			fileInfo.setMimetype((byte) MimeType.ZIP.ordinal());
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
			file.deleteOnExit();
			try {
				org.apache.commons.io.FileUtils.writeByteArrayToFile(file,
						fi.getData());
			} catch (IOException e) {
				e.printStackTrace();
			}
			aonFile.setFile(file);
			aonFile.setData(fi.getData());
			aonFile.setFileName(fi.getTitle());
			aonFile.setMimeType(com.code.aon.common.enumeration.MimeType.get(fi.getMimeString()));
			BodyPart bodyPart = null;
			try {
				bodyPart = WebmailUtil.getBodyPart(aonFile);
			} catch (MessagingException e) {
				LOGGER.error(e.getMessage());
			}
			bodyParts.add(bodyPart);
		}
		try {
			MimeMessage email = GmailUtils.createEmailWithAttachments(
					em.getRecipientsTo(), ma.getEmail(), em.getSubject(),
					em.getContent(), bodyParts);
			GmailUtils.sendMessage(gmail, "me", email); 

		} catch (MessagingException e) {
			LOGGER.error(e.getMessage());
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
		clearOuts(dialogCode, new Vector<FileInfo>(), getThreadLocalRequest());

	}

public  ContactList getContacts(Domain domain) {	
	return DBConsults.getContacts(domain, getUser());
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

public Vector<FileInfo> insertFileMultiple(Domain domain, String dialogCode, FileInfo fi) {
	Domain domainAux = DBConsults.getDomain(domain, getUser());
	
	Vector<FileInfo> files = getOuts(dialogCode, getThreadLocalRequest());
	for(FileInfo f : files){
		Date date = null;
		if (fi.getDate() != null)
			date = new Date(fi.getDate().getTime());
	
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
		if (fi.getConfidential()) conf = 1;
		else conf = 0;
		fileInfo.setSecurityLevel(conf);
		f.setConfidential(fi.getConfidential());
		if(fi.getDomainId() != domainAux.getId()){
			domainAux = DBConsults.getDomain(new Domain().setName(domain.getName()).setId(fi.getDomainId()), getUser());
		}
		f.setDomain(domainAux.getName());
		f.setDomainId(domainAux.getId());
		f.setDomainDescription(domainAux.getDescription());
	
		try {
			fileInfo.setDomainId(domain.getId());
			fileInfo.setSize(f.getSize());
			Integer id = DBConsults.insertFile(domain, getUser(), fileInfo);
			f.setFileId(id);
		
			DBConsults.insertTagsFile(domainAux, getUser(),id, fi.getTags());
			byte[] b = f.getData();//.toByteArray();
			fileInfo.setFileId(id);
			fileInfo.setData(b);
			DomainGserviceaccount g = com.code.aon.google.apis.jooq.DBConsults.getServiceAccount(domain,getUser());
			if (g.getClientId()!=null){
				Drive d = DriveUtils.serviceInitialize(g);
				String[] types = { RegistryAttachmentType.CORPORATE_IDENTITY.toString() };// TODO
				DriveUtils.types = types;
				fileInfo.setDomainId(domainAux.getId());
				fileInfo.setDomain(domainAux.getName());
				DriveUtils.sync2(d, domainAux, getUser(), fileInfo);
			}
			else DBConsults.insertFileData(domain, getUser(), id, b );
			fi = DBConsults.getFile(domainAux, getUser(), id);
			if(fi.getDomain()==null) {
				fi.setDomainId(domainAux.getId());
				fi.setDomainDescription(domainAux.getDescription());
				fi.setDomain(domainAux.getName());
			}

		} catch (KeyStoreException e) {
			LOGGER.error(e.getMessage());
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		} catch (GeneralSecurityException e) {
			LOGGER.error(e.getMessage());
		}
	}
	clearOuts(dialogCode, new Vector<FileInfo>(), getThreadLocalRequest());
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
	 * StringUtils.contains("�bc", "a") = true
	 * StringUtils.contains("abc", "z") = false
	 * StringUtils.contains("abc", "A") = true
	 * StringUtils.contains("�bc", "A") = true
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
	
	public Boolean checkDomain(Domain domain, Vector<FileInfo> vector){
		Integer domainID = DBConsults.getDomainId(domain, getUser(), domain.getName());
		
		for (FileInfo fileInfo : vector) {
			Integer fdomainId = fileInfo.getDomainId();
			String fdomain = fileInfo.getDomain();
			if((fdomainId != null && fdomainId != 0 && domainID != null) && fdomainId != domain.getId() && fdomainId != domainID && !domain.equals(fdomain)){
				if(!isParent(new Domain().setId(fdomainId).setName(domain.getName()), domain.getId()) 
						&& !isParent(domain, fdomainId)
						&& !isParent(new Domain().setId(fdomainId).setName(domain.getName()), domainID) 
						&& !isParent(new Domain().setId(domainID).setName(domain.getName()), fdomainId)){
					return false;
				}
			}
		}
		return true;
	}
	
	public Boolean isParent(Domain domain, Integer parent){
		Integer par = DBConsults.getDomainParent(domain, getUser());
		return par != null && par == parent;
	}
	
	public void selectedMenu(Domain domain){
		System.out.println(smc.size());
		smc.get(domain.getId()).setLastMenuAction(null);
	}
	
	public LinkedList<Attach>  getAttachList(Vector<FileInfo> fileInfoList){
		return  fileInfoList.stream().map(new attachFiller()).collect(Collectors.toCollection(LinkedList::new));
	}
	
	private static class attachFiller implements Function<FileInfo, Attach> {
		
		@Override
		public Attach apply(FileInfo r) {
			return new Attach()
					.setId(r.getFileId())
					.setAttachType(AttachType.REGISTRY)
					.setDescription(r.getTitle())
					.setDomain(new Domain().setName(r.getDomain()).setId(r.getDomainId()))
					.setDriveId(r.getDriveId())
					.setIcon(r.getIcon())
					.setMimeType(MimeType.values()[r.getMimetype()]);
		}
	}
	
	public void print(String head, String msg){
		System.out.println("+++++++++++++++ GWT DOCUMENT +++++++++++++++");
		System.out.println();
		System.out.println(head);
		System.out.println();
		System.out.println(msg);
		System.out.println();
		System.out.println("++++++++++++++++++++++++++++++++++++++++++");
	}
	
	public LinkedList<FileInfo> decompress(Domain domain, FileInfo fileInfo){
		LinkedList<FileInfo> fileList = new LinkedList<FileInfo>();
		try {
			java.io.File file = new java.io.File("file.zip");
			Integer id = fileInfo.getFileId();
			byte[] data  = null;
			if(fileInfo.getDriveId() != null) 
				data = DriveUtils.getByteFile(domain, getUser(), fileInfo.getDriveId(), id);
			else data = AON.getAttach(domain.getName(), domain.getId(), getUserLogin(), f -> f.getIdProperty().eq(id), AttachType.REGISTRY).getData();
			AonFileUtils.writeByteArrayToFile(file, data);
			ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
			ZipEntry entrada;
			while (null != (entrada=zis.getNextEntry()) ){			
				System.out.println(entrada.getName());
			   
			   ByteArrayOutputStream bos = new ByteArrayOutputStream();
			   int leido;
			   byte [] buffer = new byte[1024];
			   while (0<(leido=zis.read(buffer))){
			      bos.write(buffer,0,leido);
			   }

			   
			   java.io.File f = new java.io.File("aux");
			   AonFileUtils.writeByteArrayToFile(f, bos.toByteArray());
			 
			   eu.medsea.mimeutil.MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
			   Collection<?> mimeTypes = eu.medsea.mimeutil.MimeUtil.getMimeTypes(f);
			   if(MimeType.get(mimeTypes.toString()) != null && !MimeType.get(mimeTypes.toString()).equals(MimeType.OCTECT_STREAM)){
				   FileInfo fileInfoAux = fileInfo;
				   fileInfoAux.setData(bos.toByteArray());
				   if(entrada.getName().contains("/")){
					   Integer pos = entrada.getName().lastIndexOf("/");
					   fileInfoAux.setTitle(entrada.getName().substring(pos+1));
				   }else fileInfoAux.setTitle(entrada.getName());
				   
				   fileInfoAux.setMimetype(MimeType.get(mimeTypes.toString()).value());
				   fileInfoAux.setMimeString(mimeTypes.toString());
				   fileInfoAux.setIcon(icon(mimeTypes.toString()));
				   fileInfoAux.setSize(bos.size());
				   Integer fid = insertFile(domain, fileInfoAux);
				   fileInfoAux.setFileId(fid);
				   fileList.add(fileInfoAux);
			   }	
			   bos.close();
			   zis.closeEntry();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileList;
	}
	
	public Integer insertFile(Domain domain, FileInfo fi) {
		Attach attach = new Attach();
		attach.setAttachType(AttachType.REGISTRY);
		attach.setAttachModule(DBConsults.getRegistry(domain, getUserLogin()));
		attach.setCategory(fi.getCategory());
		attach.setDate(new java.util.Date());
		attach.setMimeType(MimeType.values()[fi.getMimetype()]);
		attach.setDescription(fi.getTitle());
		attach.setType(com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType.CORPORATE_IDENTITY.value());
		if(fi.getScope()!= null) attach.setScope(fi.getScope().getId());
		attach.setConfidential(fi.getConfidential());
		attach.setDomain(domain.setId(fi.getDomainId()));
		attach.setDparentId(fi.getSize().toString());
		attach.setData(fi.getData());
		attach.setCreationDate(new java.util.Date());
		attach.setModificationDate(new java.util.Date());
		attach.setCreationUser(getUserLogin());
		attach.setModificationUser(getUserLogin());
		Integer id = AON.insert(domain.getName(), domain.getId(), getUserLogin(), attach);
		fi.getTags().stream().forEach(tag ->{
			AON.insertRegistryAttachTag(domain.getName(), domain.getId(), getUserLogin(), id, tag.getId());
		});
		return id;
	}
	
	public static String icon(String m) {
		if(m.contains("audio")) return "aon-icon-google-drive-audio";
		if(m.contains("image")) return "aon-icon-google-drive-image";
		if(m.contains("video")) return "aon-icon-google-drive-mov";
		switch (m) {
		case "application/vnd.google-apps.audio":return "aon-icon-google-drive-audio";
		case "application/vnd.google-apps.document":return "aon-icon-google-drive-docs";
		case "application/vnd.google-apps.drawing":return "aon-icon-google-drive-drawing";
		case "application/vnd.google-apps.folder":return "aon-icon-google-drive-folder";
		case "application/vnd.google-apps.form":return "aon-icon-google-drive-form";
		case "application/vnd.google-apps.photo":return "aon-icon-google-drive-image";
		case "application/vnd.google-apps.presentation":return "aon-icon-google-drive-presentation";
		case "application/vnd.google-apps.spreadsheet":return "aon-icon-google-drive-calc";
		case "application/vnd.google-apps.video":return "aon-icon-google-drive-mov";
		
		case "application/msword":
		case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
		case "application/vnd.openxmlformats-officedocument.wordprocessingml.template":
		case "application/vnd.ms-word.document.macroEnabled.12":
		case "application/vnd.ms-word.template.macroEnabled.12":
			return "aon-icon-google-drive-word";
		case "application/vnd.ms-excel": 
		case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet": 
		case "application/vnd.openxmlformats-officedocument.spreadsheetml.template": 
		case "application/vnd.ms-excel.sheet.macroEnabled.12": 
		case "application/vnd.ms-excel.template.macroEnabled.12": 
		case "application/vnd.ms-excel.addin.macroEnabled.12": 
		case "application/vnd.ms-excel.sheet.binary.macroEnabled.12": 
			return "aon-icon-google-drive-excel";
		case "application/vnd.ms-powerpoint": 
		case "application/vnd.openxmlformats-officedocument.presentationml.presentation": 
		case "application/vnd.openxmlformats-officedocument.presentationml.template": 
		case "application/vnd.openxmlformats-officedocument.presentationml.slideshow": 
		case "application/vnd.ms-powerpoint.addin.macroEnabled.12": 
		case "application/vnd.ms-powerpoint.presentation.macroEnabled.12": 
		case "application/vnd.ms-powerpoint.slideshow.macroEnabled.12": 
			return "aon-icon-google-drive-power-point";
		case "application/pdf": return "aon-icon-google-drive-pdf-sinfondo";
		
		case "application/x-rar-compressed": return "aon-icon-google-drive-zip";
		case "application/zip": return "aon-icon-google-drive-zip";
		default:
			return "aon-icon-google-drive-unknown";
		}
	}
	
}
