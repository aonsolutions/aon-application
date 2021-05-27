package com.code.aon.google.apis;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;

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
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.FileContent;
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

import net.aonsolutions.aon.google.apis.drive.AonDrive;

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

		GoogleCredential credential = new GoogleCredential.Builder()
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
	
	public static Drive serviceInitialize(DomainGserviceaccount d){
		try {
			final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
			final JsonFactory JSON_FACTORY = new JacksonFactory();
			final String SERVICE_ACCOUNT_ID = d.getEmailAddress();
	
			InputStream keyStream = new ByteArrayInputStream(d.getPrivateKey());
			PrivateKey serviceAccountPrivateKey;
		
			serviceAccountPrivateKey = SecurityUtils
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
		} catch (IOException e) {
			LOGGER.error("I/O Error connecting to Drive: {}", e.getMessage());
		} catch (GeneralSecurityException e1) {
			LOGGER.error("Security Error connecting to Drive: {}", e1.getMessage());
		}
		return client;
	}
	
	public static File getFileN(Drive drive, String id){
		return AonDrive.getInstace().getFile(drive, id);
	}
	
	public static File insertFile(Drive drive, File file){
		return AonDrive.getInstace().createFile(drive, file);
	}
	
	public static About getAbout(Drive drive){
		About about = new About();
		try {
			about = drive.about().get().execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return about;
	}
	
	public static File principal(Drive drive, Attach attach) {
		return AonDrive.getInstace().principal(drive, attach);
	}
	
	/************************** OBTENER TODOS LOS ARCHIVOS **************************/
	
	static Integer cont = -1; 
	@Deprecated
	public static File getFile(Drive drive, Domain domain, User user, String fileId, Integer id) throws IOException, KeyStoreException, GeneralSecurityException {
		File f = null;
		if(cont == -1)cont = 0;
		if(cont == 1) cont = -1;
		FileList fileList =SearchFiles.searchFilesProperties(drive, "oldDriveId",fileId);
		if(fileList.getFiles().size()>0){
			f = fileList.getFiles().get(0);
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
	
	public static byte[] getByteFile(Domain domain, User user, String driveId, Integer attachId){
		DomainGserviceaccount d = AON.getDomainGserviceaccount(domain.getName(), domain.getId(), user.getLogin());
		Drive drive = AonDrive.getInstace().serviceInitialize(d);
		byte[] b = AonDrive.getInstace().downloadFileByteArray(drive, driveId);
		if(b == null) {
			FileList fl = net.aonsolutions.aon.google.apis.drive.SearchFiles.searchFilesAppProperties(drive, "oldDriveId", driveId);
			if(fl.getFiles().size() > 0) {
				String newDriveId = fl.getFiles().get(0).getId();
				b = AonDrive.getInstace().downloadFileByteArray(drive, newDriveId);	
			}
		}
		if(b == null) {
			Drive driveOld = AonDrive.getInstace().serviceInitializeOld(d);
			b = AonDrive.getInstace().downloadFileByteArray(driveOld, driveId);
		}
		return b;
	}

	
	public static File getFile(Drive drive, String driveId){
		try {
			return drive.files().get(driveId).setFields("*").execute();
		} catch (IOException e) {
			return null;
		}
	}
	
	public static File getFile(Drive drive, String driveId, String fields){
		try {
			return drive.files().get(driveId).setFields(fields).execute();
		} catch (IOException e) {
			return null;
		}
	}

	public static FileList getParentFiles(Drive drive, String parent) throws IOException {
		return net.aonsolutions.aon.google.apis.drive.SearchFiles.searchByParent(drive, parent);
	}

	public static FileList getRootFiles2(Drive drive, String email) throws IOException {
		return drive.files().list()
				.setQ("root in parents and '" + email + "' in readers")
				.execute();
	}

	public static FileList getRootFiles3(Drive drive, String title)
			throws IOException {

		String a = "'" + title + "'";
		FileList aux = drive.files().list().setQ("title contains " + a)
				.execute();
		return aux;
	}

	public static FileList getRootFiles(Drive drive) throws IOException {
		return net.aonsolutions.aon.google.apis.drive.SearchFiles.searchByParent(drive, "root");
	}

	/********************* UTILS *********************/

	public static int searchFiles(FileList files, String dato, int n) {

		int centro;
		int inf = 0;
		int sup = n - 1;
		while (inf <= sup) {
			centro = (sup + inf) / 2;
			if (files.getFiles().get(centro).getName().compareTo(dato) == 0) {
				return centro;
			} else if (files.getFiles().get(centro).getName().compareTo(dato) > 0) {
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
				.setName(attach.getDescription())
				.setMimeType(attach.getMimeType().getName());
	}
	
	public static File newFile(Byte mimetype, String title) {
		return new File()
				.setShared(true)
				.setName(title)
				.setMimeType(MimeType.values()[mimetype].getName());
	}

	public static File insertFile(Drive drive, java.io.File file, 
			DriveFile driveFile) throws IOException {
		File fileAux = new File()
				.setName(driveFile.getDescription())
				.setMimeType(driveFile.getMimetype());
		FileContent mediaContent = new FileContent(driveFile.getMimetype(), file);
		return drive.files().create(fileAux, mediaContent).execute();
	}
	
	public static File updateFile(FileInfo fileInfo) throws IOException, KeyStoreException, GeneralSecurityException {
		Domain domain = getDomain();
		return updateFile(domain, fileInfo);
	}
	
	public static File updateFile(Domain domain, FileInfo fileInfo) throws IOException, KeyStoreException, GeneralSecurityException {
		File file = null;
		try {
			file = getFile(client, domain, new User().setLogin(""), fileInfo.getDriveId(), fileInfo.getFileId());
			file.setName(fileInfo.getTitle());
			if(file.getDescription().equals("OLDRIVE")){
				DomainGserviceaccount g = DBConsults.getServiceAccount(domain, new User().setLogin(""));
				DriveUtils.serviceInitializeOld(g);
			}
		} catch (GeneralSecurityException e1) {
			e1.printStackTrace();
		}
		file.setMimeType(MimeType.values()[fileInfo.getMimetype()].getName());
		file.setModifiedTime(new DateTime(new Date()));

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
	
	public static File updateFile(Drive drive, File file, byte[] data){
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		InputStreamContent isc = new InputStreamContent(file.getMimeType(), bais);
		try {
			file = drive.files().update(file.getId(), file, isc).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}


	public static void setPermissions(String fileId, Vector<String> emails,
			Vector<String> pemails) throws IOException {

		for (int i = 0; i < emails.size(); i++) {
			AonDrive.getInstace().setPermission(client, fileId, emails.get(i));
		}
		for (int j = 0; j < pemails.size(); j++) {
			AonDrive.getInstace().setPermission(client, fileId, pemails.get(j));
		}

	}

	/*********************** Sincronizar BD a Google Drive ***************************/

	public static Boolean paysheet(DomainGserviceaccount d, Attach attach, LinkedList<String> emails) {
		return AonDrive.getInstace().paysheet(d, attach, emails);
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
		file.setModifiedTime(new DateTime(new Date()));
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

	public static DriveData getAttachs(Domain domain){
		DriveData dd = new DriveData();dd.setAttachs(new Vector<FileInfo>());
		DomainGserviceaccount g = DBConsults.getServiceAccount(domain, new User().setLogin(""));
		dd.setGservice(g);
		dd.setDomain(domain.getName());
		// de momento solo se sincroniza con Rattach
		// REGISTRY ATTACH
		Vector<FileInfo> rattach = DBDrive.getRegistryAttach(domain, dd.getAttachs());
		dd.setAttachs(rattach);
		
		return dd;
	}

	/************************* DESCARGAR ARCHIVO DE DRIVE *********************/
	
	public static byte[] getByteFile(Domain domain, String login, Attach attach) {
		DomainGserviceaccount d = AON.getDomainGserviceaccount(domain.getName(), domain.getId(), login);
		Drive drive = AonDrive.getInstace().serviceInitialize(d);
		byte[] b = AonDrive.getInstace().downloadFileByteArray(drive, attach.getDriveId());
		if(b == null) {
			FileList fl = net.aonsolutions.aon.google.apis.drive.SearchFiles.searchFilesAppProperties(drive, "oldDriveId", attach.getDriveId());
			if(fl.getFiles().size() > 0) {
				String newDriveId = fl.getFiles().get(0).getId();
				AonDrive.getInstace().updateDriveId(attach);
				b = AonDrive.getInstace().downloadFileByteArray(drive, newDriveId);	
			}
		}
		if(b == null) {
			Drive driveOld = AonDrive.getInstace().serviceInitializeOld(d);
			b = AonDrive.getInstace().downloadFileByteArray(driveOld, attach.getDriveId());
		} 
		return b;
	}
	
	public static byte[] getByteFile(String domainName, Integer domainId, String login, String driveId, Integer attachId) {
		Domain domain = AON.getDomain(domainName, domainId, login);
		User user = new User().setLogin(login);
		return getByteFile(domain, user, driveId, attachId);
	}
	
	public static InputStream downloadFile(Drive drive, File file) {
		return AonDrive.getInstace().downloadFile(drive, file.getId());
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
			} catch (IOException e) {} 
		}
		else if(domain.getParentId() != null && map.containsKey(domain.getParentId())){
			try{
				Drive drive = serviceInitialize(map.get(domain.getParentId()));
				drive.files().delete(fileId).execute();
			} catch (IOException e) {}
		}
		else if(map.containsKey(0)){
			try {
				Drive drive = serviceInitialize(map.get(0));
				drive.files().delete(fileId).execute();
			} catch (IOException e) {}
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
			Drive drive = serviceInitialize(sa);
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

	private Attach getAttach( IBlobObject blobObject ) {
		Attach attach = new Attach();	
		attach.setId(blobObject.getId());
		if (blobObject instanceof IAttachment) {
			IAttachment ia = (IAttachment) blobObject;
			Domain domain = AON.getDomain(AonUtil.getDomainName(), ia.getDomain(), "");
			attach = AON.getAttach(domain.getName(), domain.getId(), "", f-> f.getIdProperty().eq(blobObject.getId()), AttachType.getAttachType(ia.getAonType()));
			if(attach.getId() == null) attach.setId(ia.getId());
			attach.setDescription(ia.getDescription());
			attach.setDriveId(ia.getDriveId());
			attach.setMimeType(com.esferalia.aon.occam.api.model.type.MimeType.values()[ia.getMimeType().ordinal()]);
			attach.setDomain(AON.getDomain(AonUtil.getDomainName(), ia.getDomain(), ""));
			attach.setAttachType(AttachType.getAttachType(ia.getAonType()));
		}
		return attach;
	}
	
	@Override
	public void setBlobs(boolean insert, IBlobObject blobObject) {
		Domain domain = getDomain();
		User user = new User().setLogin("");
		DomainGserviceaccount sa = DBConsults.getServiceAccount(domain, new User().setLogin(""));
		Drive drive = serviceInitialize(sa);
	
		if (blobObject.getId() != null) {
			Attach attach = getAttach(blobObject);
			for(String property : blobObject.getBlobProperties() ) {
				if (! insert) {
					attach.setDriveId((String) blobObject.getReference(property));	
				}
				byte[] data = BlobObjectUtil.getProperty(blobObject, property);
				if (! ArrayUtils.isEmpty(data) ) {
					attach.setData(data);						
				}
				AonDrive.getInstace().sync(drive, user, attach, false);			
			}
		}
	}

	@Override
	public void deleteBlobs(IBlobObject blobObject) {
		Domain domain = getDomain();

		if ( blobObject.getId() != null ) {
			DomainGserviceaccount sa = DBConsults.getServiceAccount(domain, new User().setLogin(""));
	
			Drive drive = serviceInitialize(sa);
	
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
	
	private static Domain getDomain(){
		return AON.getDomain(AonUtil.getDomainName(), DomainManager.getCurrentDomain(), "");
	}
}
