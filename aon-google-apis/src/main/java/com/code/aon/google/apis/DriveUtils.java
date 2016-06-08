package com.code.aon.google.apis;

import static com.code.aon.google.apis.jooq.DBConsults.getCategory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.mail.MessagingException;
import javax.naming.NamingException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BlobObjectUtil;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IBlobManager;
import com.code.aon.common.IBlobObject;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.google.apis.drive.SearchFiles;
import com.code.aon.google.apis.jooq.DBConsults;
import com.code.aon.google.apis.jooq.DBDrive;
import com.code.aon.google.apis.jooq.DBSync;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.google.sql.AbstractSQL.Rattach;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.registry.Category;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.server.io.AonFileUtils;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.SecurityUtils;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.About;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.Property;

public class DriveUtils implements IBlobManager {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DriveUtils.class.getName());

	private static final DriveUtils DRIVEUTILS = new DriveUtils();

	private static Drive client;

	public static DriveUtils getInstace() {
		return DRIVEUTILS;
	}
	
	public static Drive serviceInitializeOld(DomainGserviceaccount d)
			throws KeyStoreException, IOException, GeneralSecurityException {

		final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
		final JsonFactory JSON_FACTORY = new JacksonFactory();
		final String SERVICE_ACCOUNT_ID = d.getEmailAddress();

		InputStream keyStream = new ByteArrayInputStream(d.getPrivateKey());
		PrivateKey serviceAccountPrivateKey = SecurityUtils
				.loadPrivateKeyFromKeyStore(SecurityUtils.getPkcs12KeyStore(),
						keyStream, "notasecret", "privatekey", "notasecret");
		GoogleCredential credential;

			credential = new GoogleCredential.Builder()
			.setTransport(HTTP_TRANSPORT)
			.setJsonFactory(JSON_FACTORY)
			.setServiceAccountId(SERVICE_ACCOUNT_ID)
			.setServiceAccountScopes(
					java.util.Collections.singletonList(DriveScopes.DRIVE))
			.setServiceAccountPrivateKey(serviceAccountPrivateKey)
			.build();
		
		client = new com.google.api.services.drive.Drive.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(
				"AON SOLUTIONS").build();

		return client;

	}
	
	public static Drive serviceInitialize(DomainGserviceaccount d)
			throws KeyStoreException, IOException, GeneralSecurityException {
		
		final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
		final JsonFactory JSON_FACTORY = new JacksonFactory();
		final String SERVICE_ACCOUNT_ID = d.getEmailAddress();
		
		
		
		InputStream keyStream = new ByteArrayInputStream(d.getPrivateKey());
		PrivateKey serviceAccountPrivateKey = SecurityUtils
				.loadPrivateKeyFromKeyStore(SecurityUtils.getPkcs12KeyStore(),
						keyStream, "notasecret", "privatekey", "notasecret");
		String googleAccount = d.getGoogleAccount();
		GoogleCredential credential;
		if(googleAccount != null){
			credential = new GoogleCredential.Builder()
				.setTransport(HTTP_TRANSPORT)
				.setJsonFactory(JSON_FACTORY)
				.setServiceAccountId(SERVICE_ACCOUNT_ID)
				.setServiceAccountScopes(
						java.util.Collections.singletonList(DriveScopes.DRIVE))
				.setServiceAccountPrivateKey(serviceAccountPrivateKey)
				.setServiceAccountUser(googleAccount)
				.build();
		}
		else{
			credential = new GoogleCredential.Builder()
			.setTransport(HTTP_TRANSPORT)
			.setJsonFactory(JSON_FACTORY)
			.setServiceAccountId(SERVICE_ACCOUNT_ID)
			.setServiceAccountScopes(
					java.util.Collections.singletonList(DriveScopes.DRIVE))
			.setServiceAccountPrivateKey(serviceAccountPrivateKey)
			.build();
		}
		client = new com.google.api.services.drive.Drive.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(
				"AON SOLUTIONS").build();

		return client;

	}

	/****
	 * CREAR CARPETA
	 * 
	 *****/

	public static void createFolders(Drive drive, Domain domain, String parent)
			throws IOException{

		LinkedList<Category> categoryList = DBConsults.getCategoryList(domain, new User().setLogin(""));
		for (Category category : categoryList) {
			createFolder(drive, domain, category.getName(), category.getDescription(), true,
					parent);
		}
		createFolder(drive, domain, "otros", "", true, parent);
	}

	public static void createAttachFolders(Drive drive, Domain domain,
			String parent) throws IOException {
		File file1 = createFolder(drive, domain, "contract", "", true, parent);
		createFolder(drive, domain, "nominas", "", true, file1.getId());
		createFolder(drive, domain, "item", "", true, parent);
		createFolder(drive, domain, "invoice", "", true, parent);
		createFolder(drive, domain, "offer", "", true, parent);
		createFolder(drive, domain, "payroll", "", true, parent);
		createFolder(drive, domain, "project", "", true, parent);
		File file2 = createFolder(drive, domain, "registry", "", true, parent);
		createFolders(drive, domain, file2.getId());
		createFolder(drive, domain, "sepe", "", true, parent);
	}

	public static File createFolder(Drive drive, Domain domain, String title,
			String description, Boolean hasParent, String parent)
			throws IOException {

		File folder = new File();
		if (hasParent)
			folder.setParents(Arrays.asList(new ParentReference().setId(parent)));
		folder.setTitle(title);
		folder.setDescription(description);
		folder.setMimeType("application/vnd.google-apps.folder");
		folder = drive.files().insert(folder).execute();
		if (!title.contains("AONSOLUTIONS-")) {
			Property property = new Property();
			property.setValue(domain.getName());
			property.setKey("domain");
			drive.properties().insert(folder.getId(), property).execute();

		}
		return folder;
	}

	private static File createFolder(Drive drive, String title, String parent) throws IOException{
		File f = drive.files().get(parent).execute();
		if(!f.getMimeType().equals("application/vnd.google-apps.folder")){
			LOGGER.info(f.getMimeType());
			LOGGER.error(title + "- The specified parent is not a folder.");
			return null;
		}
		else{
			File folder = new File();
			folder.setParents(Arrays.asList(new ParentReference().setId(parent)));
			folder.setTitle(title);
			folder.setMimeType("application/vnd.google-apps.folder");
			folder = drive.files().insert(folder).execute();
			return folder;
		}	
	}
	
	public static File principal(Drive drive, Domain domain, FileInfo fileInfo)
			throws IOException, KeyStoreException, GeneralSecurityException {
		About about = drive.about().get().execute();
		String rootId = about.getRootFolderId();
		Vector<String> emails = new Vector<String>();
		Vector<ParentReference> parents = new Vector<ParentReference>();
		FileList domainFolders = SearchFiles.searchFilesTitleEqualAndMimetype(drive, fileInfo.getDomain());
		File domainFolder;
		if(domainFolders.getItems().size()>0){
			domainFolder = domainFolders.getItems().get(0);
		}
		else domainFolder = createFolder(drive, fileInfo.getDomain(), rootId);
		
		
		FileList typeFolders = SearchFiles.searchFilesTitleAndParent(drive,  fileInfo.getAonType(),domainFolder.getId());
		File typeFolder;
		if(typeFolders.getItems().size()>0){
			typeFolder = typeFolders.getItems().get(0);
		}
		else typeFolder = createFolder(drive, fileInfo.getAonType(), domainFolder.getId());
		
		if(domainFolder != null && typeFolder != null){
			parents.add(new ParentReference().setId(typeFolder.getId()));
			File file = new File();
			try {
				file = insertFile(drive, fileInfo, parents, emails, domain);
			} catch (MessagingException e) {
				LOGGER.error(e.getMessage(), e);
			}
			return file;
		}
		else return null;
	}

	public static String insertToFolder(Drive drive, String email,
			Domain domain, FileInfo fileInfo) throws IOException {
		FileList fl = getRootFiles2(drive, email);
		Boolean esta = false;
		int i = 0;
		File file = new File();
		String hijo = new String();
		int pos = email.indexOf('@');
		String title = email.substring(0, pos) + "." + domain.getName();
		System.out.println(fl.getItems().size());

		while (!esta && i < fl.getItems().size()) {

			file = fl.getItems().get(i);
			System.out.println(file.getTitle() + " - " + title + " - "
					+ file.getTitle().equalsIgnoreCase(title));
			if (file.getTitle().equalsIgnoreCase(title)) {// file.getUserPermission().getEmailAddress()==email){
				// tratando todos los attach a la hora de compartir la carpeta (
				// registry, invoice, project ,...)
				// hijo = searchParent(drive,fileInfo,domain,file.getId());

				hijo = insertFile(drive, domain, fileInfo.getCategory(), file);

				// tratando unicamente rattach (registry) a la hora de compartir
				// la carpeta.
				esta = true;
			}
			i++;
		}
		if (!esta) {
			// String username="";
			file = createFolder(drive, domain, title, "", false, null);
			// SessionInfo.table.get(domain).getUsers().get(username).getGoogleUsers().get(email).setFl(getRootFiles2(drive));
			Permission p = new Permission();
			p.setValue("aibanez@aonsolutions.es");// poner email en vez de
													// aibane...
			p.setType("user");// user || group || domain || anyone
			p.setRole("reader");// owner || reader || writer || commenter
			drive.permissions().insert(file.getId(), p)
					.setSendNotificationEmails(false).execute();

			// File f = new File();

			// todos las carpetas de los tipos de attach de la base de datos
			// createAttachFolders(drive, domain, file.getId());

			// todos las carpetas de las categorias de rattach.
			createFolders(drive, domain, file.getId());
			// tratando todos los attach a la hora de compartir la carpeta (
			// registry, invoice, project ,...)
			// hijo= searchParent(drive,fileInfo,domain,file.getId());

			// tratando unicamente rattach (registry) a la hora de compartir la
			// carpeta.
			hijo = insertFile(drive, domain, fileInfo.getCategory(), file);
		}

		return hijo;

	}

	private static String searchParent(Drive drive, FileInfo fileInfo,
			Domain domain, String parent) throws IOException {
		String a = "'" + parent + "'";
		FileList aux = drive.files().list().setQ(a + " in parents").execute();

		// FileList aux=getParentFiles(drive, parent);

		for (File f : aux.getItems()) {
			if (f.getTitle().equals(fileInfo.getAonType())) {
				if (fileInfo.getAonType().equals("registry")) {
					return insertFile(drive,domain, fileInfo.getCategory(), f);
				}
				if (fileInfo.getAonType().equals("contract")) {
					if (fileInfo.getIsNomina()) {
						FileList aux2 = drive
								.files()
								.list()
								.setQ("'" + f.getId()
										+ "' in parents and title = 'nominas'")
								.execute();
						for (File f1 : aux2.getItems()) {
							return f1.getId();
						}
					}
				} else
					return f.getId();
			}
		}
		return "";
	}

	private static String insertFile(Drive drive, Domain domain, Integer categoryId,
			File file) throws IOException {
		String name = "otros";
		if (categoryId != -1) {
			Category c = DBConsults.getCategory(domain, new User().setLogin(""), categoryId);
			name = c.getName() != null ? c.getName() : "otros";
		}
		String a = "'" + file.getId() + "'";
		FileList aux = drive.files().list().setQ(a + " in parents").execute();
		// FileList aux=getParentFiles(drive, file.getId());
		File f2 = null;

		for (File f : aux.getItems()) {
			if (f.getTitle().equalsIgnoreCase(name)) {
				f2 = f;

			}
		}

		return f2.getId();// insertFile(rattach, f2.getId());
	}

	private static File insertFile(Drive drive, FileInfo fileInfo,
			Vector<ParentReference> parents, Vector<String> emails,
			Domain domain) throws IOException, MessagingException, KeyStoreException,
			GeneralSecurityException {
		// File's metadata.
		File file = newFile(fileInfo.getMimetype(), fileInfo.getTitle());
		file.setModifiedDate(new DateTime(new Date()));
		file.setParents(parents);
		file.setProperties(setProperties(drive, fileInfo, domain));
		// File's content.

		//java.io.File fileContent = Utils.InputStreamToFile(fileInfo);
		// FileContent mediaContent = new FileContent(file.getMimeType(),
		// fileContent);
		
		LOGGER.info("Document info, mimetype:{} , file_ID {}", file.getMimeType(),fileInfo.getFileId());
		ByteArrayContent  mediaContent = new ByteArrayContent (
				file.getMimeType(), fileInfo.getData());

		try {
			
			long start = System.currentTimeMillis();
			
			file = drive.files().insert(file, mediaContent).execute();

			long time = System.currentTimeMillis() - start;
			
			LOGGER.info("Uploading document: {}-{} ({}s)", file.getId()
					+ file.getTitle(), (time/1000d));
			return file;
		} catch (IOException e) {
			LOGGER.error("Error uploading document: {}-{}. {}", file.getId()
					+ file.getTitle(), e.getMessage());
			return null;
		}
	}

	private static List<Property> setProperties(Drive drive, FileInfo fileInfo, Domain domain) throws IOException {
		String name = "otros";
		if(fileInfo.getCategory()!= null){
			User user = new User().setLogin(""); 
			Category c = getCategory(domain, user, fileInfo.getCategory());
			name = c.getName() != null ? c.getName() : "otros";
		}
		Property property1 = new Property();
		property1.setValue(name);
		
		// property1.setEtag("category");
		property1.setKey("category");

		String type = fileInfo.getType() != -1 ? RegistryAttachmentType
				.values()[fileInfo.getType()].name() : "UNKNOWN";
		Property property2 = new Property();
		property2.setValue(type);
		// property2.setEtag("type");
		property2.setKey("type");
		property2.setVisibility("PRIVATE");

		Property property3 = new Property();
		property3.setValue(fileInfo.getDomain());
		// property2.setEtag("type");
		property3.setKey("domain");

		Property property4 = new Property();
		property4.setValue(fileInfo.getAonType());
		property4.setKey("aontype");
		
		Property property5 = new Property();
		property5.setValue(Integer.toString(fileInfo.getFileId()));
		property5.setKey("fileId");
		
		List<Property> properties = new ArrayList<Property>(4);
		properties.add(property1);
		properties.add(property2);
		properties.add(property3);
		properties.add(property4);
		properties.add(property5);
		
		
		
		return properties;

		//drive.properties().insert(id, property1).execute();
		//drive.properties().insert(id, property2).execute();
		//drive.properties().insert(id, property3).execute();
		//drive.properties().insert(id, property4).execute();
		

	}

	/************************** OBTENER TODOS LOS ARCHIVOS **************************/

	public static DriveFile[] getFiles(Drive drive) throws IOException {
		FileList files = drive.files().list().execute();

		DriveFile[] driveFiles = new DriveFile[files.getItems().size()];
		for (int i = 0; i < driveFiles.length; i++) {
			driveFiles[i] = new DriveFile(files.getItems().get(i).getId(),
					files.getItems().get(i).getDownloadUrl(), files.getItems()
							.get(i).getTitle(), "");
		}
		return driveFiles;
	}

	public static FileList getFiles2(Drive drive) throws IOException {
		return drive.files().list().execute();
	}

	public static File getFile(Drive drive, Domain domain, User user, FileInfo fileInfo) throws IOException, GeneralSecurityException{		
		File f = null;
		
		FileList fileList =SearchFiles.searchFilesProperties(drive, "oldDriveId",fileInfo.getDriveId());
		if(fileList.getItems().size()>0){
			f = fileList.getItems().get(0);
			if(fileInfo.getFileId() != null) DBDrive.updateDriveId(f,fileInfo.getFileId());
		} else
			try {
				f = drive.files().get(fileInfo.getDriveId()).execute();
			} catch (IOException e) {
				DomainGserviceaccount g = DBConsults.getServiceAccount(domain, new User().setLogin(""));
				Drive oldDrive = serviceInitializeOld(g);
				f = oldDrive.files().get(fileInfo.getDriveId()).execute();
				f.setDescription("OLDRIVE");
			}
		if(f.getDescription() == null) f.setDescription("");
		return f;
	}
	
	static Integer cont = -1; 
	@Deprecated
	public static File getFile(Drive drive, Domain domain, User user, String fileId, Integer id) throws IOException, KeyStoreException, GeneralSecurityException {
		File f = null;
		if(cont == -1)cont = 0;
		if(cont == 1) cont = -1;
		FileList fileList =SearchFiles.searchFilesProperties(drive, "oldDriveId",fileId);
		if(fileList.getItems().size()>0){
			f = fileList.getItems().get(0);
			//TODO update bd with new driveId.
			if(id != null)
				DBDrive.updateDriveId(f,id);
			
		} else
			try {
				f = drive.files().get(fileId).execute();
			} catch (IOException e) {
				DomainGserviceaccount g = DBConsults.getServiceAccount(domain, user);
				Drive oldDrive = serviceInitializeOld(g);
				try{
					f = oldDrive.files().get(fileId).execute();
					f.setDescription("OLDRIVE");
				}catch(IOException e1){
					if(cont == 0){
						Domain domain2 = new Domain().setName(domain.getName()).setId(0);
						DomainGserviceaccount g2 = DBConsults.getServiceAccount(domain2, user);	
						Drive drive2 = serviceInitialize(g2);		
						cont = 1;
						f = getFile(drive2, domain2, user, fileId, id);
						if(f.getDescription().equals("OLDRIVE"))
							f.setDescription("DOMAINZEROOLDDRIVE");
						else f.setDescription("DOMAINZERODRIVE");
					}
				}	
			}
		if(f.getDescription() == null) f.setDescription("");
		return f;
	}
	
	public static File getDriveFile(Domain domain, User user, String driveId, Integer attachId, Boolean permission){
		HashMap<Integer, DomainGserviceaccount> map = DBConsults.getServiceAccountMap(domain, user);
		
		if(map.containsKey(domain.getId())){
			try {
				Drive drive = serviceInitialize(map.get(domain.getId()));
				File file = getFileApp(drive, domain, user, driveId, attachId );
				if(file == null){
					drive= serviceInitializeOld(map.get(domain.getId()));
					file = getFileApp(drive, domain, user, driveId, attachId);
				}
				if(file != null){
					if(permission){
						Permission p = new Permission();
						p.setValue(domain.getName());
						p.setType("anyone");// user || group || domain || anyone
						p.setRole("reader");// owner || reader || writer || commenter
						drive.permissions().insert(file.getId(), p).execute();
					}
					return file;
				}
			} catch (IOException | GeneralSecurityException e) {
				e.printStackTrace();
			}
		}
		
		if(domain.getParentId() != null && map.containsKey(domain.getParentId())){
			try{
				Drive drive = serviceInitialize(map.get(domain.getParentId()));
				File file = getFileApp(drive, domain, user, driveId, attachId );
				if(file == null){
					drive= serviceInitializeOld(map.get(domain.getParentId()));
					file = getFileApp(drive, domain, user, driveId, attachId);
				}
				if(file != null){
					if(permission){
						Permission p = new Permission();
						p.setValue(domain.getName());
						p.setType("anyone");// user || group || domain || anyone
						p.setRole("reader");// owner || reader || writer || commenter
						drive.permissions().insert(file.getId(), p).execute();
					}
					return file;
				}
			} catch (IOException | GeneralSecurityException e) {
				e.printStackTrace();
			}
		}
		
		if(map.containsKey(0)){
			try {
				Drive drive = serviceInitialize(map.get(0));
				File file = getFileApp(drive, domain, user, driveId, attachId );
				if(file == null){
					drive= serviceInitializeOld(map.get(0));
					file = getFileApp(drive, domain, user, driveId, attachId);
				}
				if(file != null){
					if(permission){
						Permission p = new Permission();
						p.setValue(domain.getName());
						p.setType("anyone");// user || group || domain || anyone
						p.setRole("reader");// owner || reader || writer || commenter
						drive.permissions().insert(file.getId(), p).execute();
					}
					return file;
				}
			} catch (IOException | GeneralSecurityException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static byte[] getByteFile(Domain domain, User user, String driveId, Integer attachId){
		HashMap<Integer, DomainGserviceaccount> map = DBConsults.getServiceAccountMap(domain, user);
		
		if(map.containsKey(domain.getId())){
			try {
				Drive drive = serviceInitialize(map.get(domain.getId()));
				File file = getFileApp(drive, domain, user, driveId, attachId );
				if(file == null){
					drive= serviceInitializeOld(map.get(domain.getId()));
					file = getFileApp(drive, domain, user, driveId, attachId);
				}
				if(file != null){
					InputStream data = downloadFile(drive, file);
					return AonIOUtils.toByteArray(data);
				}
			} catch (IOException | GeneralSecurityException e) {
				e.printStackTrace();
			}
		}
		
		if(domain.getParentId() != null && map.containsKey(domain.getParentId())){
			try{
				Drive drive = serviceInitialize(map.get(domain.getParentId()));
				File file = getFileApp(drive, domain, user, driveId, attachId );
				if(file == null){
					drive= serviceInitializeOld(map.get(domain.getParentId()));
					file = getFileApp(drive, domain, user, driveId, attachId);
				}
				if(file != null){
					InputStream data = downloadFile(drive, file);
					return AonIOUtils.toByteArray(data);
				}
			} catch (IOException | GeneralSecurityException e) {
				e.printStackTrace();
			}
		}
		
		if(map.containsKey(0)){
			try {
				Drive drive = serviceInitialize(map.get(0));
				File file = getFileApp(drive, domain, user, driveId, attachId );
				if(file == null){
					drive= serviceInitializeOld(map.get(0));
					file = getFileApp(drive, domain, user, driveId, attachId);
				}
				if(file != null){
					InputStream data = downloadFile(drive, file);
					return AonIOUtils.toByteArray(data);
				}
			} catch (IOException | GeneralSecurityException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static File getFileApp(Drive drive, Domain domain, User user, String driveId, Integer attachId) throws IOException {
		File f = null;
		FileList fileList =SearchFiles.searchFilesProperties(drive, "oldDriveId",driveId);
		if(fileList.getItems().size()>0){
			f = fileList.getItems().get(0);
			if(attachId != null){
				DBDrive.updateDriveId(domain, user, f, attachId);
			}
		} else
			f = getFile(drive, driveId);
		return f;
	}
	
	public static File getFile(Drive drive, String driveId){
		try {
			return drive.files().get(driveId).execute();
		} catch (IOException e) {
			return null;
		}
	}

	public static FileList getParentFiles(Drive drive, String parent)
			throws IOException {
		FileList aux = drive.files().list().execute();
		FileList nuevo = new FileList();
		Vector<File> files = new Vector<File>();
		for (File f : aux.getItems()) {
			List<ParentReference> parents = f.getParents();
			for (ParentReference p : parents) {
				if (p.getId().equals(parent)) {
					files.add(f);
					nuevo.setItems(files);
				}
			}

		}
		return nuevo;
	}

	public static FileList getRootFiles2(Drive drive, String email)
			throws IOException {
		About about = drive.about().get().execute();
		String a = "'" + about.getRootFolderId() + "'";
		FileList aux = drive.files().list()
				.setQ(a + " in parents and '" + email + "' in readers")
				.execute();
		return aux;
	}

	public static FileList getRootFiles3(Drive drive, String title)
			throws IOException {

		String a = "'" + title + "'";
		FileList aux = drive.files().list().setQ("title contains " + a)
				.execute();
		return aux;
	}

	public static FileList getRootFiles(Drive drive) throws IOException {
		FileList aux = drive.files().list().execute();
		FileList nuevo = aux;
		for (int i = 0; i < aux.getItems().size(); i++) {
			List<ParentReference> parents = aux.getItems().get(i).getParents();
			boolean a = false;
			for (int j = 0; j < parents.size(); j++) {
				if (parents.get(j).getIsRoot()) {
					// nuevo.getItems().add(aux.getItems().get(i));
					a = true;
				}
			}
			if (a == false) {
				nuevo.getItems().remove(i);
			}
		}
		return nuevo;
	}

	/********************* UTILS *********************/

	public static int searchFiles(FileList files, String dato, int n) {

		int centro;
		int inf = 0;
		int sup = n - 1;
		while (inf <= sup) {
			centro = (sup + inf) / 2;
			if (files.getItems().get(centro).getTitle().compareTo(dato) == 0) {
				return centro;
			} else if (files.getItems().get(centro).getTitle().compareTo(dato) > 0) {
				sup = centro - 1;
			} else {
				inf = centro + 1;
			}
		}
		return -1;
	}

	/*************************** SUBIR ARCHIVO A DRIVE ***********************/
	public static File newFile(Attach attach){
		return new File()
				.setShared(true)
				.setTitle(attach.getDescription())
				.setMimeType(attach.getMimeType().getName());
	}
	
	public static File newFile(Byte mimetype, String title) {
		return new File()
				.setShared(true)
				.setTitle(title)
				.setMimeType(MimeType.values()[mimetype].getName());
	}

	public static File insertFile(Drive drive, java.io.File file,
			DriveFile driveFile,List<ParentReference> parents) throws IOException {
		File fileAux = new File()
				.setTitle(driveFile.getDescription())
				.setMimeType(driveFile.getMimetype());
		if(parents != null) fileAux.setParents(parents);
		
		FileContent mediaContent = new FileContent(driveFile.getMimetype(), file);
		return drive.files().insert(fileAux, mediaContent).execute();
	}

	public static File insertFile(Drive drive, InputStream is,
			DriveFile driveFile, List<ParentReference> parents) throws IOException{
		File fileAux = new File()
				.setTitle(driveFile.getDescription())
				.setMimeType(driveFile.getMimetype());
		if(parents != null) fileAux.setParents(parents);

		InputStreamContent isc = new InputStreamContent(driveFile.getMimetype(), is);
		return drive.files().insert(fileAux, isc).execute();
	}
	
	public static File updateFile(FileInfo fileInfo) throws IOException, KeyStoreException, GeneralSecurityException {
		Domain domain = getDomain();
		return updateFile(domain, fileInfo);
	}
	
	public static File updateFile(Domain domain, FileInfo fileInfo) throws IOException, KeyStoreException, GeneralSecurityException {
		File file = null;
		try {
			file = getFile(client, domain, new User().setLogin(""), fileInfo.getDriveId(), fileInfo.getFileId());
			file.setTitle(fileInfo.getTitle());
			if(file.getDescription().equals("OLDRIVE")){
				DomainGserviceaccount g = DBConsults.getServiceAccount(domain, new User().setLogin(""));
				DriveUtils.serviceInitializeOld(g);
			}
		} catch (GeneralSecurityException e1) {
			e1.printStackTrace();
		}
		file.setMimeType(MimeType.values()[fileInfo.getMimetype()].getName());
		file.setModifiedDate(new DateTime(new Date()));

		// File's content.
		ByteArrayInputStream bais = new ByteArrayInputStream(fileInfo.getData());
		InputStreamContent isc = new InputStreamContent(file.getMimeType(), bais);

		try {
			file = client.files()
					.update(fileInfo.getDriveId(), file, isc)
					.execute();
			return file;
		} catch (IOException e) {
			try {
				DomainGserviceaccount g = DBConsults.getServiceAccount(domain, new User().setLogin(""));
				Drive drive = DriveUtils.serviceInitializeOld(g);
				file = drive.files()
						.update(fileInfo.getDriveId(), file, isc)
						.execute();
				return file;
			} catch (IOException e1) {
				System.out.println("An error occured: " + e1);
				return null;			
			}
		}
	}

	public static void setTypes(Vector<RegistryAttachmentType> types2) {
		int i = 0;
		for (RegistryAttachmentType type : types2) {
			int aux = type.ordinal();
			types[i] = Integer.toString(aux);
			i++;
		}

	}

	public static void setPermissions(String fileId, Vector<String> emails,
			Vector<String> pemails) throws IOException {

		for (int i = 0; i < emails.size(); i++) {
			Permission p = new Permission();
			p.setValue(emails.get(i));
			p.setType("user");// user || group || domain || anyone
			p.setRole("reader");// owner || reader || writer || commenter
			client.permissions().insert(fileId, p).execute();
		}
		for (int j = 0; j < pemails.size(); j++) {
			Permission p2 = new Permission();
			p2.setValue(pemails.get(j));
			p2.setType("user");// user || group || domain || anyone
			p2.setRole("reader");// owner || reader || writer || commenter
			client.permissions().insert(fileId, p2).execute();
		}

	}

	/*********************** Sincronizar BD a Google Drive ***************************/

	public static boolean checkTypes(FileInfo file) {
		if(file.getAonType().equals("registry")){
			String rat;
			if(file.getType() != -1) rat = RegistryAttachmentType.values()[file.getType()].toString();
			else rat="";	
			for (String type : types) {
					if (rat.equals(type) && type != null) {
						return true;
					}
					if(type.equals("registry")){
						if(file.getType() == RegistryAttachmentType.LOGO.value()
							|| file.getType() == RegistryAttachmentType.AON_TEMPLATES.value() 
							|| file.getType() == RegistryAttachmentType.D2_DEPOSIT.value()
							
							|| file.getType() == RegistryAttachmentType.DOMAIN_BOOK_HISTORY.value() // Historial en configuración
							
							|| file.getType() == RegistryAttachmentType.CRETA_RESPUESTA.value() 
							|| file.getType() == RegistryAttachmentType.CRETA_TRABAJADORES_Y_TRAMOS.value() 
							
							|| file.getType() == RegistryAttachmentType.ECOMMERCE_PRODUCT_TEMPLATES.value()){
							return false;
						}
						return true;
					}
			}
		}
		else{
			return true;
		}
		return false;
	}

	public static boolean checkType(Rattach rattach,
			Vector<RegistryAttachmentType> types) {
		boolean bool = false;
		short type = rattach.getType();
		RegistryAttachmentType t = RegistryAttachmentType.values()[type];
		int i = 0;
		while (i < types.size() && !bool) {
			if (types.get(i).equals(t))
				bool = true;
			i++;
		}
		return bool;
	}
	
	// nominaas!!!
	public static File sync(Rattach rattach, Vector<String> emails,
			DomainGserviceaccount d) throws  IOException, KeyStoreException,
			GeneralSecurityException, NamingException {
		
		Domain domain = getDomain();
		// HAY QUE CONVERTIR EL RATTACH EN FILEINFO PARA LLAMAR A
		// PRINCIPAL!!!!!!
		
		InputStream is = rattach.getData();
		byte data [] = AonIOUtils.toByteArray(is);
		is.close();
		
		FileInfo fileInfo = new FileInfo("registry", rattach.getType(),
				data, rattach.getDriveId(), rattach.getId(),
				rattach.getDescription(),
				rattach.getMimeType() != null ? rattach.getMimeType()
						.byteValue() : null);
		fileInfo.setEmails(emails);
		fileInfo.setIsNomina(true);
		Drive drive = serviceInitialize(d);
		File file = null;
		
		if (rattach.getDriveId() == null) {
			file = principal(drive, domain, fileInfo);
		} else {
			File fileAux = getFile(drive, domain, new User().setLogin(""), rattach.getDriveId(), fileInfo.getFileId());
			if (!fileAux.getMd5Checksum().equals(
					AonFileUtils.getMD5Checksum(rattach.getData()))) {
				file = updateFile(new FileInfo());
			}
		}
		return file;
	}

	public static boolean sync2(Drive drive, Domain domain, User user, FileInfo fileInfo) throws IOException, GeneralSecurityException {
		if (checkTypes(fileInfo)) {
			if (fileInfo.getDriveId() == null) {
				String type = fileInfo.getType() != -1 ? RegistryAttachmentType
						.values()[fileInfo.getType()].name() : "UNKNOWN";
				if (dryRun) {
					LOGGER.info(
							"Dry Run {} '{}': Not at Drive. It will be created & uploaded.",
							type, fileInfo.getTitle());
					return true;
				}
				// viewFile(fileInfo);
				File file = principal(drive, domain, fileInfo);

				if (file != null) {
					fileInfo.setDriveId(file.getId());
					setDriveId(fileInfo, domain, file.getFileSize().toString());
					LOGGER.info(
							"'{}': Not at Drive. It was created & uploaded [{}].",
							fileInfo.getTitle(), file.getId());
					return true;
				}
				else{
					LOGGER.error("Parent of file is null");
					//Para que pase al siguiente archivo a subir devuelve true
					return true;
				}
				
			} else {

				if (fileInfo.getData() == null) {
					LOGGER.debug("Skip '{}': No new data.", fileInfo.getTitle());
					return false;
				}

				File fileAux = getFile(drive, domain, user, fileInfo);
				
				if(fileAux.getDescription().equals("OLDRIVE")){
					DomainGserviceaccount g = DBConsults.getServiceAccount(domain, new User().setLogin(""));
					drive = DriveUtils.serviceInitializeOld(g);
				}
				if (!fileAux.getMd5Checksum().equals(
						AonFileUtils.getMD5Checksum(fileInfo.getData()))) {

					String type = RegistryAttachmentType.values()[fileInfo
							.getType()].name();
					if (dryRun) {
						LOGGER.info(
								"Dry Run '{}': Changed. It will be synchronized/uploaded.",
								fileInfo.getTitle());
						return true;
					}
					File file = updateFile(domain, fileInfo);
					if (file != null) {
						fileInfo.setDriveId(file.getId());
						setDriveId(fileInfo, domain, file.getFileSize()
								.toString());
						LOGGER.info(
								"'{}': Changed. It was synchronized/uploaded [{}].",
								fileInfo.getTitle(), file.getId());
						return true;
					}
					LOGGER.warn(
							"{} '{}': Changed. It was NOT synchronized/uploaded",
							type, fileInfo.getTitle() );
					return false;
				} else {
					LOGGER.debug(
							"Skip '{}': New data it's the same that at drive ( MD5s are the same ).",
							fileInfo.getTitle());
					return false;
				}
			}
		} else {
			String type = fileInfo.getType() != -1 ? RegistryAttachmentType
					.values()[fileInfo.getType()].name() : "UNKNOWN";
			LOGGER.warn("Skip '{}': {} won't be synchronized.",
					fileInfo.getTitle(), type);
			return false;
		}
	}

	public static void updateDateSync(Drive drive, FileInfo fileInfo) throws IOException, GeneralSecurityException {
		String domainName = AonUtil.getDomainName();
		Integer domainId = DomainManager.getCurrentDomain();
		Domain domain = AON.getDomain(domainName, domainId, "");
		
		File file = null;
		try {
			file = getFile(drive, domain, new User().setLogin(""), fileInfo.getDriveId(), fileInfo.getFileId());
			if(file.getDescription().equals("OLDRIVE")){
				DomainGserviceaccount g = DBConsults.getServiceAccount(domain, new User().setLogin(""));
				drive = DriveUtils.serviceInitializeOld(g);
			}
		} catch ( GeneralSecurityException e) {
			e.printStackTrace();
		}
		file.setModifiedDate(new DateTime(new Date()));
		try {
			drive.files().update(fileInfo.getDriveId(), file).execute();
		} catch (IOException e) {
			DomainGserviceaccount g = DBConsults.getServiceAccount(domain, new User().setLogin(""));
			drive = DriveUtils.serviceInitializeOld(g);
			drive.files().update(fileInfo.getDriveId(), file).execute();
		}
	}

	public static void setDriveId(FileInfo fileInfo, Domain domain, String size){
		if(fileInfo.getDriveId() != null){
			if (fileInfo.getAonType().equals("registry")) {
				DBConsults.setDriveIdRegistryAttach(domain, fileInfo);
				DBConsults.deleteBlobRegistryAttach(domain, fileInfo);
			} else if (fileInfo.getAonType().equals("contract")) {
				DBConsults.setDriveIdContractAttach(domain, fileInfo);
				DBConsults.deleteBlobContractAttach(domain, fileInfo);
			} else if (fileInfo.getAonType().equals("item")) {
				DBConsults.setDriveIdIattach(domain, fileInfo);
				DBConsults.deleteBlobIattach(domain, fileInfo);
			} else if (fileInfo.getAonType().equals("invoice")) {
				DBConsults.setDriveIdInvoiceAttach(domain, fileInfo);
				DBConsults.deleteBlobInvoiceAttach(domain, fileInfo);
			} else if (fileInfo.getAonType().equals("offer")) {
				DBConsults.setDriveIdOfferAttach(domain, fileInfo);
				DBConsults.deleteBlobOfferAttach(domain, fileInfo);
			} else if (fileInfo.getAonType().equals("payroll")) {
				DBConsults.setDriveIdPayrollAttach(domain, fileInfo);
				DBConsults.deleteBlobPayrollAttach(domain, fileInfo);
			} else if (fileInfo.getAonType().equals("project")) {
				DBConsults.setDriveIdProjectAttach(domain, fileInfo);
				DBConsults.deleteBlobProjectAttach(domain, fileInfo);
			} else if (fileInfo.getAonType().equals("sepe")) {
				DBConsults.setDriveIdSepeAttach(domain, fileInfo);
				DBConsults.deleteBlobSepeAttach(domain, fileInfo);
			}
		}
	}

	public static void synchronize(Domain domain, User user) throws IOException,
			GeneralSecurityException {
		DriveData dd = getAttachs(domain);

		// long max= 100000;

		// if (max >= dd.getGservice().getSize()){
		// Rattach
		if (dd.getGservice().getClientId() != null && dd.getAttachs() != null
				&& dd.getAttachs().size() > 0) {
			Drive drive = serviceInitialize(dd.getGservice());
			for (int j = 0; j < dd.getAttachs().size(); j++) {
				FileInfo attach = dd.getAttachs().get(j);

				if (attach.getAonType().equals("registry")) {
					attach = DBDrive.getDataAttach(domain, user, AttachType.REGISTRY, attach);
					attach = DBDrive.getEmailsRegistryAttach(domain, attach);					
				} else if (attach.getAonType().equals("contract")) {
					attach = DBDrive.getDataAttach(domain, user, AttachType.CONTRACT, attach);
					attach = DBDrive.getEmailsContractAttach(domain, attach);
				} else if (attach.getAonType().equals("item")) {
					attach = DBDrive.getDataAttach(domain, user, AttachType.ITEM, attach);
				} else if (attach.getAonType().equals("invoice")) {
					attach = DBDrive.getDataAttach(domain, user, AttachType.INVOICE, attach);
					attach = DBDrive.getEmailsInvoiceAttach(domain, attach);
				} else if (attach.getAonType().equals("offer")) {
					attach = DBDrive.getDataAttach(domain, user, AttachType.OFFER, attach);
				} else if (attach.getAonType().equals("payroll")) {
					attach = DBDrive.getDataAttach(domain, user, AttachType.PAYROLL, attach);
				} else if (attach.getAonType().equals("project")) {
					attach = DBDrive.getDataAttach(domain, user, AttachType.PROJECT, attach);
					attach = DBDrive.getEmailsProjectAttach(domain, attach);
				} else if (attach.getAonType().equals("sepe")) {
					attach = DBDrive.getDataAttach(domain, user, AttachType.SEPE, attach);
				}

				sync2(drive, domain, user, attach);
				
				/***** Tamaño ocupado en Google Drive para el dominio dado *****/
				// long size = DriveUtils.totalSize(domain);
				// DBConsults.setDriveSize(domain, size,
				// dd.getGservice().getDomain());
			}
		}
	}

	public static void synchronize() throws IOException, KeyStoreException,
			GeneralSecurityException {
		// Obtiene todos los dominios de la BD.
		Map<String, String> domains = DBSync.initializeDomains();
		Map<String, Integer> domainMap = DBSync.initializeDomainMap();
		// Ordena los dominios por orden alfabetico.
		List<String> list = new ArrayList<String>(domains.keySet());
		Collections.sort(list, (String s1, String s2) -> s1.compareTo(s2));
		
		// Recorre todos los dominios de la BD
		for (String domainName : domains.keySet()) {
			Domain domain = getDomain(domainName, domainMap.get(domainName));
			User user = new User().setLogin("");
			synchronize(domain, user);
		}
	}
	


	public static DriveData getAttachs(Domain domain){
		DriveData dd = new DriveData();dd.setAttachs(new Vector<FileInfo>());
		DomainGserviceaccount g = DBConsults.getServiceAccount(domain, new User().setLogin(""));
		dd.setGservice(g);
		dd.setDomain(domain.getName());
		// de momento solo se sincroniza con Rattach
		// REGISTRY ATTACH
		Vector<FileInfo> rattach = DBDrive.getRegistryAttach(domain, dd.getAttachs());
		dd.setAttachs(rattach);
		/*
		 * //CONTRACT ATTACH dd.setAttachs(DBConsults.getContractAttach(domain,
		 * dd.getAttachs()));
		 * 
		 * //INCOME ATTACH dd.setAttachs(DBConsults.getIattach(domain,
		 * dd.getAttachs()));
		 * 
		 * //INVOICE ATTACH dd.setAttachs(DBConsults.getInvoiceAttach(domain,
		 * dd.getAttachs()));
		 * 
		 * //OFFER ATTACH dd.setAttachs(DBConsults.getOfferAttach(domain,
		 * dd.getAttachs()));
		 * 
		 * //PAYROLL ATTACH dd.setAttachs(DBConsults.getPayrollAttach(domain,
		 * dd.getAttachs()));
		 * 
		 * //PROJECT ATTACH dd.setAttachs(DBConsults.getProjectAttach(domain,
		 * dd.getAttachs()));
		 * 
		 * //SEPE ATTACH dd.setAttachs(DBConsults.getSepeAttach(domain,
		 * dd.getAttachs()));
		 */
		return dd;
	}

	/************************* DESCARGAR ARCHIVO DE DRIVE *********************/
	
	
	public static byte[] getByteFile(String domainName, Integer domainId, String login, String driveId, Integer attachId) {
		Domain domain = AON.getDomain(domainName, domainId, login);
		User user = new User().setLogin(login);
		return getByteFile(domain, user, driveId, attachId);
	}
	
	public static InputStream downloadFile(Drive drive, File file) {
		if (file.getDownloadUrl() != null && file.getDownloadUrl().length() > 0) {
			try {
				HttpResponse resp = drive.getRequestFactory()
						.buildGetRequest(new GenericUrl(file.getDownloadUrl()))
						.execute();
				return resp.getContent();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			// The file doesn't have any content stored on Drive.
			return null;
		}
	}

	/************************ Eliminar archivo **************************/

	public static void deleteFile(String domainName, Integer domainId, String login, String fileId) {
		Domain domain = AON.getDomain(domainName, domainId, login);
		User user = new User().setLogin(login);
		HashMap<Integer, DomainGserviceaccount> map = DBConsults.getServiceAccountMap(domain, user);
		if(map.containsKey(domain.getId())){ 
			try {
				Drive drive = serviceInitialize(map.get(domain.getId()));
				drive.files().delete(fileId).execute();
			} catch (IOException | GeneralSecurityException e) {
			} 
		}
		else if(domain.getParentId() != null && map.containsKey(domain.getParentId())){
			try{
				Drive drive = serviceInitialize(map.get(domain.getParentId()));
				drive.files().delete(fileId).execute();
			} catch (IOException | GeneralSecurityException e) {
			}
		}
		else if(map.containsKey(0)){
			try {
				Drive drive = serviceInitialize(map.get(0));
				drive.files().delete(fileId).execute();
			} catch (IOException | GeneralSecurityException e) {
			}
		}
	}

	public static void deleteFile(Drive drive, String fileId) throws IOException {
		drive.files().delete(fileId).execute();
	}

	public static void deleteDriveId(String driveId, Domain domain, Integer id) {
		DBConsults.deleteDriveIdRegistryAttach(domain, driveId, id);
		DBConsults.deleteDriveIdContractAttach(domain, driveId, id);
		DBConsults.deleteDriveIdIattach(domain, driveId, id);
		DBConsults.deleteDriveIdInvoiceAttach(domain, driveId, id);
		DBConsults.deleteDriveIdOfferAttach(domain, driveId, id);
		DBConsults.deleteDriveIdPayrollAttach(domain, driveId, id);
		DBConsults.deleteDriveIdProjectAttach(domain, driveId, id);
		DBConsults.deleteDriveIdSepeAttach(domain, driveId, id);
	}

	/**********************
	 * SEARCH
	 * 
	 * @throws IOException
	 *****************************/

	public static FileList searchFiles(Drive drive, String searcher)
			throws IOException {

		FileList fl = drive.files().list()
				.setQ("fullText contains " + searcher).execute();
		return fl;

	}


	public static void main(String[] args){
		parse(args);
	}

	public static String types[] = {};
	public static String domains[];
	public static boolean dryRun;

	private static void parse(String args[]) {
		CommandLineParser parser = new PosixParser();
		HelpFormatter helpFormatter = new HelpFormatter();

		Options options = new Options();

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(false);
		OptionBuilder.withDescription("imprime esta ayuda.");
		Option helpOption = OptionBuilder.create("help");

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("dominio a sincronizar.");
		Option domainOption = OptionBuilder.create("domain");

		OptionBuilder.isRequired(true);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("documento a sincronizar.");
		Option typeOption = OptionBuilder.create("type");

		options.addOption(helpOption);
		options.addOption(domainOption);
		options.addOption(typeOption);

		try {
			CommandLine line = parser.parse(options, args);

			types = line.getOptionValues("type");
			domains = line.getOptionValues("domain");

		} catch (ParseException e) {
			helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX,
					options, true);
		}
	}

	@Override
	public byte[] getBlob(IBlobObject blobObject, String property) {
		Domain domain = getDomain();
		String driveId = (String) blobObject.getReference(property);
		if (driveId != null) {
			User user = new User().setLogin(AonUtil.getRemoteUser());
			return getByteFile(domain, user, driveId, blobObject.getId());
		}
		return null;
	}
	
	public byte[] getBlobOld(IBlobObject blobObject, String property) {
		Domain domain = getDomain();
		String driveId = (String) blobObject.getReference(property);
		if (driveId != null) {
			DomainGserviceaccount sa = DBConsults.getServiceAccount(domain, new User().setLogin(""));			

			Drive drive = null;

			try {
				drive = serviceInitialize(sa);
			} catch (KeyStoreException e) {
				LOGGER.error(e.getMessage(), e);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			} catch (GeneralSecurityException e) {
				LOGGER.error(e.getMessage(), e);
			}

			InputStream data = null;

			try {
				File file = null;
				try {
					file = getFile(drive, domain, new User().setLogin(""), driveId,null);
					if(file.getDescription().equals("OLDRIVE"))
						drive = DriveUtils.serviceInitializeOld(sa);
				} catch ( GeneralSecurityException e) {
					e.printStackTrace();
				}
				if ( file != null ) {
					data = downloadFile(drive, file);
					return AonIOUtils.toByteArray(data);
				}
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);					
			} finally {
				IOUtils.closeQuietly(data);
			}
		}
		return null;
	}

	private FileInfo getFileInfo( IBlobObject blobObject ) {
		FileInfo file = new FileInfo();
		file.setFileId(blobObject.getId());
		file.setAonType("registry");
		file.setType((byte)3);
		if ( blobObject instanceof IAttachment ) {
			IAttachment ia = (IAttachment) blobObject;
			file.setTitle( ia.getDescription() );
			file.setDriveId( ia.getDriveId() );
			file.setMimetype( (byte) ia.getMimeType().ordinal() );
			file.setDomainId(ia.getDomain());
			Domain d = null;
			d = DBConsults.getDomain(AonUtil.getDomainName(), ia.getDomain());
			file.setDomain(d.getName());
		}
		return file;
	}
	
	@Override
	public void setBlobs(boolean insert, IBlobObject blobObject) {
		Domain domain = getDomain();
		User user = new User().setLogin("");
		String[] aux = { "LOGO", "DOCUMENT", "CORPORATE_IDENTITY" };
		types = aux;
		DomainGserviceaccount sa = DBConsults.getServiceAccount(domain, new User().setLogin(""));
		Drive drive = null;

		try {
			drive = serviceInitialize(sa);
		} catch (KeyStoreException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (GeneralSecurityException e) {
			LOGGER.error(e.getMessage(), e);
		}

		if (blobObject.getId() != null) {

			FileInfo file = getFileInfo(blobObject);
			if ( file != null ) {
				for( String property : blobObject.getBlobProperties() ) {
					if (! insert) {
						file.setDriveId((String) blobObject.getReference(property));	
					}
					byte[] data = BlobObjectUtil.getProperty(blobObject, property);
					if (! ArrayUtils.isEmpty(data) ) {
						file.setData(data);						
					}
					try {
						sync2(drive, domain, user, file);							
					} catch (IOException e) {
						LOGGER.error(e.getMessage(), e);													
					} catch (GeneralSecurityException e) {
						LOGGER.error(e.getMessage(), e);
					}							
				}		
			}
		}
	}

	@Override
	public void deleteBlobs(IBlobObject blobObject) {
		Domain domain = getDomain();

		if ( blobObject.getId() != null ) {
			DomainGserviceaccount sa = DBConsults.getServiceAccount(domain, new User().setLogin(""));
	
			Drive drive = null;

			try {
				drive = serviceInitialize(sa);
			} catch (KeyStoreException e) {
				LOGGER.error(e.getMessage(), e);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			} catch (GeneralSecurityException e) {
				LOGGER.error(e.getMessage(), e);
			}
			
			for( String property : blobObject.getBlobProperties() ) {
				String driveId = (String) blobObject.getReference(property);
				if (driveId != null) {
					try {
						deleteFile(drive, driveId);
					} catch (IOException e) {
						LOGGER.error(e.getMessage(), e);
					}
				}
			}			
		}
	}	
	
	public static long totalSize(Domain domain) throws IOException, GeneralSecurityException {
		DomainGserviceaccount g = DBConsults.getServiceAccount(domain,new User().setLogin(""));
		Drive drive = serviceInitialize(g);
		long size = 0;
		FileList fl = SearchFiles
				.searchFilesProperties(drive, "domain", domain.getName());
		for (File f : fl.getItems()) {
			size = size + f.getFileSize();
		}
		return size;
	}
	
	
	private static Domain getDomain(){
		return AON.getDomain(AonUtil.getDomainName(), DomainManager.getCurrentDomain(), "");
	}
	
	private static Domain getDomain(Integer domainId){
		return AON.getDomain(AonUtil.getDomainName(), domainId, "");
	}
	
	private static Domain getDomain(String domainName, Integer domainId){
		return AON.getDomain(domainName, domainId, "");
	}
	
}
