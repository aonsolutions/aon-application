package com.code.aon.google.apis.drive;

import static org.apache.commons.cli.HelpFormatter.DEFAULT_SYNTAX_PREFIX;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.security.PrivateKey;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.esferalia.aon.occam.api.model.security.User;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.SecurityUtils;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class RemoveFiles {
	
	private static String EMAIL_ADDRESS; //= "1081696571072-94k21clt33bit00a871gdttfhfrle0v5@developer.gserviceaccount.com";
	//static byte[] PRIVATE_KEY;
	private static InputStream PRIVATE_KEY;
	private static String pkeyPath;
	private static String login;
	
	public static String getLogin(){
		return login;
	}
	
	public static User getUser(){
		return new User().setLogin(getLogin());
	}
	
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
	
	public static void main(String[] args) throws KeyStoreException, IOException, GeneralSecurityException {
		parse(args);
		Drive oldDrive = oldService();

		FileList fileList = oldDrive.files().list().execute();
		for(File file : fileList.getItems()){
			try {
				oldDrive.files().delete(file.getId()).execute();
				View.file(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static boolean parse(String args[]) {

		CommandLineParser parser = new PosixParser();
		HelpFormatter helpFormatter = new HelpFormatter();

		Options options = new Options();

		OptionBuilder.isRequired(true);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("Username of application");
		OptionBuilder.withLongOpt("username");
		Option loginOption = OptionBuilder.create('u');
		
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
				.withDescription("private key path to connect with the Service Account");
		OptionBuilder.withLongOpt("pkey");
		Option pkeyOption = OptionBuilder.create("pkey");

		options.addOption(loginOption);
		options.addOption(helpOption);
		options.addOption(clientOption);
		options.addOption(pkeyOption);


		try {
			CommandLine line = parser.parse(options, args);

			if (line.hasOption(helpOption.getOpt())) {
				helpFormatter.printHelp(DEFAULT_SYNTAX_PREFIX, options, true);
				return false;
			}

			EMAIL_ADDRESS = line.getOptionValue(clientOption.getOpt(), "");
			pkeyPath = line.getOptionValue(pkeyOption.getOpt(),"");
			


		} catch (ParseException e) {
			System.out.print(e.getMessage());
			helpFormatter.printHelp(DEFAULT_SYNTAX_PREFIX, options, true);
			return false;
		}
		return true;
	}
}
