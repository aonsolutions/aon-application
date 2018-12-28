package net.aonsolutions.aon.google.apis.drive;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.SecurityUtils;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;

import net.aonsolutions.aon.google.apis.Utils;

public class DriveUtils {

	public static DriveUtils getInstace() {
		return new DriveUtils();
	}
	private static final Logger LOGGER  = Logger.getLogger(DriveUtils.class.getName());
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private static final List<String> SCOPES = Arrays.asList(DriveScopes.DRIVE);
	
	public Drive serviceInitializeOld(DomainGserviceaccount d){
		try{
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
			.setServiceAccountScopes(SCOPES)
			.setServiceAccountPrivateKey(serviceAccountPrivateKey)
			.build();
		
			return getDrive(credential);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "I/O Error connecting to Drive: {}", e.getMessage());
		} catch (GeneralSecurityException e1) {
			LOGGER.log(Level.SEVERE, "Security Error connecting to Drive: {}", e1.getMessage());
		}
		return null;
	}

	public Drive serviceInitialize(DomainGserviceaccount d){
		try {
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
						.setServiceAccountScopes(SCOPES)
						.setServiceAccountPrivateKey(serviceAccountPrivateKey)
						.setServiceAccountUser(googleAccount)
					.build();
			}
			else{
				credential = new GoogleCredential.Builder()
						.setTransport(HTTP_TRANSPORT)
						.setJsonFactory(JSON_FACTORY)
						.setServiceAccountId(SERVICE_ACCOUNT_ID)
						.setServiceAccountScopes(SCOPES)
						.setServiceAccountPrivateKey(serviceAccountPrivateKey)
					.build();
			}
			return getDrive(credential);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "I/O Error connecting to Drive: {}", e.getMessage());
		} catch (GeneralSecurityException e1) {
			LOGGER.log(Level.SEVERE, "Security Error connecting to Drive: {}", e1.getMessage());
		}
		return null;
	}
	
	public Drive getDrive(Credential credential) {
		return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				credential).setApplicationName("AON SOLUTIONS").build();
	}

	public File getFile(Drive drive, String id){
		File file  = new File();
		try {
			file = drive.files().get(id).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	public File createFile(Drive drive, File file){
		try {
			file = drive.files().create(file).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	public File createFile(Drive drive, File file, byte[] data){
		try {
			ByteArrayContent  mediaContent = new ByteArrayContent (
					file.getMimeType(), data);
			file = drive.files().create(file, mediaContent).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	public File updateFile(Drive drive, File file, byte[] data){
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		InputStreamContent isc = new InputStreamContent(file.getMimeType(), bais);
		try {
			file = drive.files().update(file.getId(), new File(), isc).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	public void deleteFile(Drive drive, String fileId){
		try {
			drive.files().delete(fileId).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public InputStream downloadFile(Drive drive, String id){
		InputStream is = null;
		try {
			is = drive.files().get(id).executeMediaAsInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return is;
	}
	
	public byte[] downloadFileByteArray(Drive drive, String id){
		byte[] data = null;
		try {
			InputStream is = drive.files().get(id).executeMediaAsInputStream();
			data = AonIOUtils.toByteArray(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public void downloadFile(Drive drive, String id, OutputStream outputStream){
		try {
			drive.files().get(id).setAlt("media").executeMediaAndDownloadTo(outputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Boolean setPermission(Drive drive, String fileId, String email){
		Permission permission =new Permission()
				.setEmailAddress(email)
				.setType(Utils.isGmail(email) ? "user" : "anyone")//user || group || domain || anyone
				.setRole("reader");//owner || reader || writer || commenter		  		
		try {
			drive.permissions().create(fileId, permission).execute();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public Boolean setPermissionDomain(Drive drive, String fileId, String domain){
		Permission permission =new Permission()
				.setDomain(domain)
				.setType("anyone")//user || group || domain || anyone
				.setRole("reader");//owner || reader || writer || commenter		  		
		try {
			drive.permissions().create(fileId, permission).execute();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
    public Boolean setPermissions(Drive drive, String fileId, Vector<String> emails){
   	 	for (String email : emails) {
   	 		if(!setPermission(drive, fileId,email))
   	 			return false;
   	 	}
   	 	return true;
	}	

}
