package com.esferalia.aon.gwt.viewer.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.util.HashMap;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.SecurityUtils;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;

public class DriveUtils {

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
		
		Drive client = new com.google.api.services.drive.Drive.Builder(
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
		Drive client = new com.google.api.services.drive.Drive.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(
				"AON SOLUTIONS").build();

		return client;
	}
	
	public static File getDriveFile(Attach attach, User user){
		Domain domain = AON.getDomain(attach.getDomain().getName(), attach.getDomain().getId(), user.getLogin());
		return getDriveFile(domain, user, attach.getDriveId(), attach.getId(), attach.getAttachType());
	}
	
	public static byte[] getByteFile(Attach attach, User user){
		Domain domain = AON.getDomain(attach.getDomain().getName(), attach.getDomain().getId(), user.getLogin());
		return getByteFile(domain, user, attach.getDriveId(), attach.getId(), attach.getAttachType());
	}
	
	public static File getDriveFile(Domain domain, User user, String driveId, Integer attachId, AttachType attachType){
		HashMap<Integer, DomainGserviceaccount> map = AON.getDomainGserviceaccountMap(domain.getName(), domain.getId(), user.getLogin(), domain.getParentId());
		
		if(map.containsKey(domain.getId())){
			try {
				Drive drive = serviceInitialize(map.get(domain.getId()));
				File file = getFile(drive, domain, driveId, attachId, attachType);
				if(file == null){
					drive= serviceInitializeOld(map.get(domain.getId()));
					file = getFile(drive, domain, driveId, attachId, attachType);
				}
				if(file != null) return file;
			} catch (IOException | GeneralSecurityException e) {
				e.printStackTrace();
			}
		}
		
		if(domain.getParentId() != null && map.containsKey(domain.getParentId())){
			try{
				Drive drive = serviceInitialize(map.get(domain.getParentId()));
				File file = getFile(drive, domain, driveId, attachId, attachType);
				if(file == null){
					drive= serviceInitializeOld(map.get(domain.getParentId()));
					file = getFile(drive, domain, driveId, attachId, attachType);
				}
				if(file != null) return file;
			} catch (IOException | GeneralSecurityException e) {
				e.printStackTrace();
			}
		}
		
		if(map.containsKey(0)){
			try {
				Drive drive = serviceInitialize(map.get(0));
				File file = getFile(drive, domain, driveId, attachId, attachType);
				if(file == null){
					drive= serviceInitializeOld(map.get(0));
					file = getFile(drive, domain, driveId, attachId, attachType);
				}
				if(file != null) return file;
			} catch (IOException | GeneralSecurityException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static byte[] getByteFile(Domain domain, User user, String driveId, Integer attachId, AttachType attachType){
		HashMap<Integer, DomainGserviceaccount> map = AON.getDomainGserviceaccountMap(domain.getName(), domain.getId(), user.getLogin(), domain.getParentId());
			
		if(map.containsKey(domain.getId())){
			try {
				Drive drive = serviceInitialize(map.get(domain.getId()));
				File file = getFile(drive, domain, driveId, attachId, attachType);
				if(file == null){
					drive= serviceInitializeOld(map.get(domain.getId()));
					file = getFile(drive, domain, driveId, attachId, attachType);
				}
				if(file != null){
					InputStream data = downloadFile(drive, file);
					return inputStreamToByte(data);
				}
			} catch (IOException | GeneralSecurityException e) {
				e.printStackTrace();
			}
		}
		
		if(domain.getParentId() != null && map.containsKey(domain.getParentId())){
			try{
				Drive drive = serviceInitialize(map.get(domain.getParentId()));
				File file = getFile(drive, domain,  driveId, attachId, attachType);
				if(file == null){
					drive= serviceInitializeOld(map.get(domain.getParentId()));
					file = getFile(drive, domain, driveId, attachId, attachType);
				}
				if(file != null){
					InputStream data = downloadFile(drive, file);
					return inputStreamToByte(data);
				}
			} catch (IOException | GeneralSecurityException e) {
				e.printStackTrace();
			}
		}
		
		if(map.containsKey(0)){
			try {
				Drive drive = serviceInitialize(map.get(0));
				File file = getFile(drive, domain, driveId, attachId, attachType);
				if(file == null){
					drive= serviceInitializeOld(map.get(0));
					file = getFile(drive, domain, driveId, attachId, attachType);
				}
				if(file != null){
					InputStream data = downloadFile(drive, file);
					return inputStreamToByte(data);
				}
			} catch (IOException | GeneralSecurityException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static File getFile(Drive drive, Domain domain, String driveId, Integer attachId, AttachType attachType) throws IOException, KeyStoreException, GeneralSecurityException {
		File f = null;
		FileList fileList = searchFilesProperties(drive, "oldDriveId",driveId);
		if(fileList.getItems().size()>0){
			f = fileList.getItems().get(0);
			if(attachId != null)
				AON.updateAttachDriveId(domain.getName(), domain.getId(), "", attachId, driveId, attachType);
			
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
	
	public static FileList searchFilesProperties(final Drive drive, final String key, final String property) throws IOException {
        return (FileList) drive.files().list().setQ("properties has {key='" + key + "' and value='" + property + "' and visibility='PRIVATE'}").execute();
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
	
	public static byte[] inputStreamToByte(InputStream file) throws IOException{
		byte[] data=AonIOUtils.toByteArray(file);
		return data;	
	}
	

	public static void setPermission(Domain domain, User user, Attach attach, String email) throws IOException, KeyStoreException, GeneralSecurityException{
		HashMap<Integer, DomainGserviceaccount> map = AON.getDomainGserviceaccountMap(domain.getName(), domain.getId(), user.getLogin(), domain.getParentId());
		if(map.containsKey(domain.getId())){
			Drive drive = serviceInitialize(map.get(domain.getId()));
			File file = getFile(drive, domain, attach.getDriveId(), attach.getId(), attach.getAttachType());
			if(file == null){
				drive= serviceInitializeOld(map.get(domain.getId()));
				file = getFile(drive, domain, attach.getDriveId(), attach.getId(), attach.getAttachType());
			}
			if(file != null){
				Permission p=new Permission();
		 		p.setValue(email);
		 		p.setType("user");//user || group || domain || anyone
		 		p.setRole("reader");//owner || reader || writer || commenter		  		
		 		drive.permissions().insert(attach.getDriveId(), p).execute();
		 		return;
			}
		}
		
		if(domain.getParentId() != null && map.containsKey(domain.getParentId())){
			Drive drive = serviceInitialize(map.get(domain.getParentId()));
			File file = getFile(drive, domain, attach.getDriveId(), attach.getId(), attach.getAttachType());
			if(file == null){
				drive= serviceInitializeOld(map.get(domain.getParentId()));
				file = getFile(drive, domain, attach.getDriveId(), attach.getId(), attach.getAttachType());
			}
			if(file != null){
				Permission p=new Permission();
		 		p.setValue(email);
		 		p.setType("user");//user || group || domain || anyone
		 		p.setRole("reader");//owner || reader || writer || commenter		  		
		 		drive.permissions().insert(attach.getDriveId(), p).execute();
		 		return;
			}
		}
		
		if(map.containsKey(0)){
			Drive drive = serviceInitialize(map.get(0));
			File file = getFile(drive, domain, attach.getDriveId(), attach.getId(), attach.getAttachType());
			if(file == null){
				drive= serviceInitializeOld(map.get(0));
				file = getFile(drive, domain, attach.getDriveId(), attach.getId(), attach.getAttachType());
			}
			if(file != null){
				Permission p=new Permission();
		 		p.setValue(email);
		 		p.setType("user");//user || group || domain || anyone
		 		p.setRole("reader");//owner || reader || writer || commenter		  		
		 		drive.permissions().insert(attach.getDriveId(), p).execute();
		 		return;
			}
		}
	}

}
