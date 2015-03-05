package com.code.aon.google.apis.drive;

import static org.apache.commons.cli.HelpFormatter.DEFAULT_SYNTAX_PREFIX;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.IOUtils;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
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
import com.google.api.services.drive.model.Property;
import com.google.api.services.drive.model.PropertyList;

public class CopyFiles {
	
	private static String EMAIL_ADDRESS; //= "1081696571072-94k21clt33bit00a871gdttfhfrle0v5@developer.gserviceaccount.com";
	private static String GOOGLE_ACCOUNT; //= "aio@aonsolutions.net";
	//static byte[] PRIVATE_KEY;
	private static InputStream PRIVATE_KEY;
	private static String pkeyPath;
	public static void setPrivateKey() throws IOException{
		 //PRIVATE_KEY = Files.readAllBytes(Paths.get("novus.p12"));/home/aibanez/Descargas/AON SOLUTIONS-52faf5279077.p12
		PRIVATE_KEY = new FileInputStream(pkeyPath);
	}
		
	public static Drive oldService() throws KeyStoreException, IOException, GeneralSecurityException{
		setPrivateKey();
		
		final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
		final JsonFactory JSON_FACTORY = new JacksonFactory();
		final String SERVICE_ACCOUNT_ID = EMAIL_ADDRESS;
		
		//InputStream keyStream = new ByteArrayInputStream(PRIVATE_KEY);
		InputStream keyStream = PRIVATE_KEY;
		PrivateKey serviceAccountPrivateKey = SecurityUtils
				.loadPrivateKeyFromKeyStore(SecurityUtils.getPkcs12KeyStore(),
						keyStream, "notasecret", "privatekey", "notasecret");
		GoogleCredential credential= new GoogleCredential.Builder()
				.setTransport(HTTP_TRANSPORT)
				.setJsonFactory(JSON_FACTORY)
				.setServiceAccountId(SERVICE_ACCOUNT_ID)
				.setServiceAccountScopes(
						java.util.Collections.singletonList(DriveScopes.DRIVE))
				.setServiceAccountPrivateKey(serviceAccountPrivateKey)
				.build();
		
		Drive drive = new com.google.api.services.drive.Drive.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(
				"AON SOLUTIONS").build();

		return drive;
		
	}
	
	public static Drive newService() throws KeyStoreException, IOException, GeneralSecurityException{
		setPrivateKey();
		
		final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
		final JsonFactory JSON_FACTORY = new JacksonFactory();
		final String SERVICE_ACCOUNT_ID = EMAIL_ADDRESS;
		
		//InputStream keyStream = new ByteArrayInputStream(PRIVATE_KEY);
		InputStream keyStream = PRIVATE_KEY;
		PrivateKey serviceAccountPrivateKey = SecurityUtils
				.loadPrivateKeyFromKeyStore(SecurityUtils.getPkcs12KeyStore(),
						keyStream, "notasecret", "privatekey", "notasecret");
		
		String googleAccount = GOOGLE_ACCOUNT;
		GoogleCredential credential  = new GoogleCredential.Builder()
				.setTransport(HTTP_TRANSPORT)
				.setJsonFactory(JSON_FACTORY)
				.setServiceAccountId(SERVICE_ACCOUNT_ID)
				.setServiceAccountScopes(
						java.util.Collections.singletonList(DriveScopes.DRIVE))
				.setServiceAccountPrivateKey(serviceAccountPrivateKey)
				.setServiceAccountUser(googleAccount)
				.build();
		
		Drive drive = new com.google.api.services.drive.Drive.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(
				"AON SOLUTIONS").build();

		return drive;
	}

	private static File createFolder(Drive drive, String title, String parent) throws IOException{
		File folder = new File();
		folder.setParents(Arrays.asList(new ParentReference().setId(parent)));
		folder.setTitle(title);
		folder.setMimeType("application/vnd.google-apps.folder");
		folder = drive.files().insert(folder).execute();
		return folder;
	}

	private static File insertFile(Drive old, Drive drive,File f,PropertyList pList, String parent) throws IOException{
		File file = new File();
		file.setTitle(f.getTitle());
		file.setShared(true);
		file.setModifiedDate(new DateTime(new Date()));
		file.setMimeType(f.getMimeType());
		file.setParents(Arrays.asList(new ParentReference().setId(parent)));
		InputStream data = downloadFile(old, f);
		ByteArrayContent  mediaContent = new ByteArrayContent (
				file.getMimeType(), IOUtils.toByteArray(data));
		
		
		try {
			file = drive.files().insert(file, mediaContent).execute();

			for(Property p : pList.getItems()){
				drive.properties().insert(file.getId(), p).execute();
			}
			Property property = new Property();
			property.setValue(f.getId());
			property.setKey("oldDriveId");
			drive.properties().insert(file.getId(), property).execute();
			return file;
		} catch (IOException e) {
			return null;
		}
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
			return null;
		}
	}
	
	public static FileList searchFilesTitle(Drive drive, String searcher1, String searcher2){
		
		FileList fl;
		try {
			fl = drive.files().list().setQ("title = '"+searcher2+"'").execute();
		} catch (IOException e) {
			return null;
		}
		return fl;
		
	}
	
	public static void main(String[] args) throws KeyStoreException, IOException, GeneralSecurityException {
		parse(args);
		Drive oldDrive = oldService();
		Drive newDrive = newService();
		
		About about = newDrive.about().get().execute();
		String rootId = about.getRootFolderId();
		
		FileList fileList = oldDrive.files().list().setMaxResults(1000).execute();
		for(File file : fileList.getItems()){
			String driveId = file.getId();
			

			FileList fl = SearchFiles.searchFilesProperties(newDrive, "oldDriveId", driveId);
			if(fl.getItems().size() == 0){
				
				PropertyList l = oldDrive.properties().list(driveId).execute();
				Boolean bdomain = false ,baontype = false;
				for (Property p : l.getItems()) {
					if(p.getKey().equals("domain")) bdomain = true;
					if(p.getKey().equals("domain")) baontype = true;
				}
				
				String domain;
				if(bdomain)
					domain = oldDrive.properties().get(driveId, "domain").execute().getValue();
				else domain = "without.domain";
				String aonType;
				if(baontype)
					aonType = oldDrive.properties().get(driveId, "aontype").execute().getValue();
				else aonType = "without.type";
				try {
					FileList domainFolders = SearchFiles.searchFilesTitleEqual(newDrive, domain);
					File domainFolder;
					if(domainFolders.getItems().size()>0){
						domainFolder = domainFolders.getItems().get(0);
					}
					else domainFolder = createFolder(newDrive, domain, rootId);
				
					FileList typeFolders = SearchFiles.searchFilesTitleAndParent(newDrive,  aonType,domainFolder.getId());
					File typeFolder;
					if(typeFolders.getItems().size()>0){
						typeFolder = typeFolders.getItems().get(0);
					}
					else typeFolder = createFolder(newDrive, aonType, domainFolder.getId());
				
					PropertyList p = oldDrive.properties().list(file.getId()).execute();
					File f = insertFile(oldDrive, newDrive, file, p, typeFolder.getId());
					View.file(f,driveId);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static boolean parse(String args[]) {

		CommandLineParser parser = new PosixParser();
		HelpFormatter helpFormatter = new HelpFormatter();

		Options options = new Options();

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(false);
		OptionBuilder.withLongOpt("help");
		OptionBuilder.withDescription("print this help.");
		Option helpOption = OptionBuilder.create('h');

		OptionBuilder.isRequired(true);
		OptionBuilder.hasArg(true);
		OptionBuilder
				.withDescription("email address of Service Account");
		OptionBuilder.withLongOpt("client");
		Option clientOption = OptionBuilder.create("client");

		OptionBuilder.isRequired(true);
		OptionBuilder.hasArg(true);
		OptionBuilder
				.withDescription("google email address that Service Account has its permissions");
		OptionBuilder.withLongOpt("account");
		Option accountOption = OptionBuilder.create("account");
		
		OptionBuilder.isRequired(true);
		OptionBuilder.hasArg(true);
		OptionBuilder
				.withDescription("private key path to connect with the Service Account");
		OptionBuilder.withLongOpt("pkey");
		Option pkeyOption = OptionBuilder.create("pkey");

		options.addOption(helpOption);
		options.addOption(clientOption);
		options.addOption(accountOption);
		options.addOption(pkeyOption);


		try {
			CommandLine line = parser.parse(options, args);

			if (line.hasOption(helpOption.getOpt())) {
				helpFormatter.printHelp(DEFAULT_SYNTAX_PREFIX, options, true);
				return false;
			}

			EMAIL_ADDRESS = line.getOptionValue(clientOption.getOpt(), "");
			GOOGLE_ACCOUNT = line.getOptionValue(accountOption.getOpt(), "");
			pkeyPath = line.getOptionValue(pkeyOption.getOpt(),"");
			


		} catch (ParseException e) {
			System.out.print(e.getMessage());
			helpFormatter.printHelp(DEFAULT_SYNTAX_PREFIX, options, true);
			return false;
		}
		return true;
	}
}
