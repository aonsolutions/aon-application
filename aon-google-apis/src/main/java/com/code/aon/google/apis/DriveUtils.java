package com.code.aon.google.apis;

import static com.code.aon.google.apis.DatabaseSync.getDomain;
import static com.code.aon.google.apis.DatabaseSync.getDomains;
import static com.code.aon.google.apis.DatabaseSync.getServiceAccount;
import static com.code.aon.google.apis.servlet.GoogleAuthorizationServletUtils.getHttpTransport;
import static com.code.aon.google.apis.servlet.GoogleAuthorizationServletUtils.getJsonFactory;
import static com.code.aon.google.apis.servlet.GoogleAuthorizationServletUtils.getPrincipalShortName;
import static com.code.aon.google.apis.servlet.GoogleAuthorizationServletUtils.newFlow;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestInputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.engine.Collections;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.google.apis.servlet.GoogleAuthorizationCodeCallbackServlet;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.esferalia.aon.google.sql.AbstractSQL.CommercialTracking;
import com.esferalia.aon.google.sql.AbstractSQL.Domain;
import com.esferalia.aon.google.sql.AbstractSQL.DomainGserviceaccount;
import com.esferalia.aon.google.sql.AbstractSQL.Rattach;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeCallbackServlet;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.SecurityUtils;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentList;
import com.google.api.services.drive.model.ParentReference;
import com.google.api.services.drive.model.Permission;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeCallbackServlet;


public class DriveUtils  {

	
	public static class CheckSum {
		    /***
		     * Convierte un arreglo de bytes a String usando valores hexadecimales
		     * @param digest arreglo de bytes a convertir
		     * @return String creado a partir de <code>digest</code>
		     */
		    private static String toHexadecimal(byte[] digest){
		        String hash = "";
		        for(byte aux : digest) {
		            int b = aux & 0xff;
		            if (Integer.toHexString(b).length() == 1) hash += "0";
		            hash += Integer.toHexString(b);
		        }
		        return hash;
		    }
		 
		    /***
		     * Realiza la suma de verificación de un archivo mediante MD5
		     * @param archivo archivo a que se le aplicara la suma de verificación
		     * @return valor de la suma de verificación.
		     */
		    public static String getMD5Checksum(InputStream is) {
		        byte[] textBytes = new byte[1024];
		        MessageDigest md = null;
		        int read = 0;
		        String md5 = null;
		        try {
		            md = MessageDigest.getInstance("MD5");
		            while ((read = is.read(textBytes)) > 0) {
		                md.update(textBytes, 0, read);
		            }
		            is.close();
		            byte[] md5sum = md.digest();
		            md5 = toHexadecimal(md5sum);
		        } catch (FileNotFoundException e) {
		        } catch (NoSuchAlgorithmException e) {
		        } catch (IOException e) {
		        }
		    return md5;
		    }
		}

	public static class View {

		  static void header1(String name) {
		    System.out.println();
		    System.out.println("================== " + name + " ==================");
		    System.out.println();
		  }

		  static void header2(String name) {
		    System.out.println();
		    System.out.println("~~~~~~~~~~~~~~~~~~ " + name + " ~~~~~~~~~~~~~~~~~~");
		    System.out.println();
		  }
		}
	
	public static class Quicksort {
		
		private static FileList files;
		private static int number;

		public static FileList sort(FileList fils){
			files = fils;
			number = fils.getItems().size();
			quicksort(0,number - 1);
			
			//Collections.sort(fils.getItems());
			
			
			
			return files;
		}

		static void quicksort(int low, int high) {
			int i = low, j = high;
			String pivot = files.getItems().get(low + (high - low) / 2).getTitle();
			while (i <= j) {
				while ( files.getItems().get(i).getTitle().compareTo(pivot)<0) {
					i++;
				}
				while (files.getItems().get(j).getTitle().compareTo(pivot)>0) {
					j--;
				}
				if (i <= j) {
					exchange(i, j);
					i++;
					j--;
				}
			}
			if (low < j)
				quicksort(low, j);
			if (i < high)
				quicksort(i, high);
		}


		
		static void exchange(int i, int j) {
			File aux = files.getItems().get(i);
			files.getItems().set(i, files.getItems().get(j));
			files.getItems().set(j, aux);
		}
		
	}
	
	
	//private static Credential credential;	
	private static Drive client;
	
	public static void initialize() throws IOException,ServletException {
		//credential = newFlow().loadCredential(getPrincipalShortName(req));/**HttpServletRequest req**/
		
		//client = new Drive.Builder(getHttpTransport(), getJsonFactory(), credential)
		//	.setApplicationName("AON SOLUTIONS").build();
		
		client=GoogleAuthorizationCodeCallbackServlet.drive;
		

	}
	
	public static Drive serviceInitialize(DomainGserviceaccount d) throws KeyStoreException, IOException, GeneralSecurityException, SQLException{
				
		
		final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
		final JsonFactory JSON_FACTORY = new JacksonFactory();
		final String SERVICE_ACCOUNT_ID = d.getEmailAddress();

		InputStream keyStream = d.getPrivateKey();
		PrivateKey serviceAccountPrivateKey = SecurityUtils.loadPrivateKeyFromKeyStore(SecurityUtils.getPkcs12KeyStore(), keyStream, "notasecret",
		          "privatekey", "notasecret");
		
		GoogleCredential credential = new GoogleCredential.Builder()
				.setTransport(HTTP_TRANSPORT)
				.setJsonFactory(JSON_FACTORY)
				.setServiceAccountId(SERVICE_ACCOUNT_ID)
				.setServiceAccountScopes(
						java.util.Collections.singletonList(DriveScopes.DRIVE))
				.setServiceAccountPrivateKey(serviceAccountPrivateKey).build();
		
		client= new com.google.api.services.drive.Drive.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, credential )
				.setApplicationName("AON SOLUTIONS").build();
		
		return client;
		
	}
	
	/************************** OBTENER TODOS LOS ARCHIVOS **************************/
	
	public static DriveFile[] getFiles(Drive drive) throws IOException {
		FileList files=drive.files().list().execute();
		
		DriveFile[] driveFiles= new DriveFile[files.getItems().size()];
		for(int i=0;i<driveFiles.length;i++){
			driveFiles[i]=new DriveFile(files.getItems().get(i).getId(),files.getItems().get(i).getDownloadUrl(),files.getItems().get(i).getTitle(),"");
		}
		return driveFiles;
	}

	public static FileList getFiles() throws IOException{
		
		return client.files().list().execute();
		
	}
	
	public static File getFile(String fileId) throws IOException{
		return client.files().get(fileId).execute();
	}
	
	public static FileList getRootFiles(Drive drive) throws IOException {
		FileList aux=drive.files().list().execute();
		FileList nuevo=aux;
		for(int i = 0;i<aux.getItems().size();i++){
			List<ParentReference> parents=aux.getItems().get(i).getParents();
			boolean a=false;
			for(int j=0;j<parents.size();j++){
				if (parents.get(j).getIsRoot()){
					//nuevo.getItems().add(aux.getItems().get(i));
					a=true;
				}
			}
			if(a==false){
				nuevo.getItems().remove(i);
			}
		}
		return nuevo;
	}
	
	/********************* UTILS *********************/
	
	public static int searchFiles(FileList files , String dato, int n) {

		int centro;
		int inf = 0;
		int sup = n - 1;
		while (inf <= sup) {
			centro = (sup + inf) / 2;
			if (files.getItems().get(centro).getTitle().compareTo(dato)== 0) {
				return centro;
			} else if (files.getItems().get(centro).getTitle().compareTo(dato)>0) {
				sup = centro - 1;
			} else {
				inf = centro + 1;
			}
		}
		return -1;
	}
	
	/*************************** SUBIR ARCHIVO A DRIVE ***********************/
	
	public static File newFile(Rattach rattach){
		short type = rattach.getMimeType();
		MimeType t = MimeType.values()[type];
		File file=new File();
		
		

		
		file.setTitle(rattach.getDescription());
		file.setMimeType(t.getName());
		//file.setAppDataContents(true);// Indica que es un archivo de la aplicación, por lo tanto, el usuario no podrá borrar el archivo.
		return file;
	}
		
	public static File insertFile(Drive drive,java.io.File file, DriveFile driveFile) throws IOException{
		
		File fileAux=new File();
		fileAux.setTitle(driveFile.getDescription());
		fileAux.setMimeType(driveFile.getMimetype());
	    
	    FileContent mediaContent = new FileContent(driveFile.getMimetype(), file);
	    
	    File fileDrive = drive.files().insert(fileAux, mediaContent).execute();

		//file.setAppDataContents(true);// Indica que es un archivo de la aplicación, por lo tanto, el usuario no podrá borrar el archivo.
		return fileDrive;
		
	}
	
	private static File updateFile(Rattach rattach) throws IOException{
	    File file=getFile(rattach.getDriveId());
		

		// File's content.
	    java.io.File fileContent = Utils.InputStreamToFile(rattach);
	    FileContent mediaContent = new FileContent(file.getMimeType(), fileContent);
	  
	    try {
	      file = client.files().update(rattach.getDriveId(),file, mediaContent).execute();
	      return file;
	    } catch (IOException e) {
	      System.out.println("An error occured: " + e);
	      return null;
	    }
	}
	
	private static File insertFile(Rattach rattach,Vector<String> emails) throws SQLException, AonConnectionException, IOException {
	    // File's metadata.
	    File file = newFile(rattach);
	    
	    Permission p=new Permission();
		p.setValue("aibanezdegau004@gmail.com");
		p.setType("user");//user || group || domain || anyone
		p.setRole("reader");//owner || reader || writer || commenter
		
		
		file.setShared(true);
		
		
		
	    // File's content.
	    java.io.File fileContent = Utils.InputStreamToFile(rattach);
	    FileContent mediaContent = new FileContent(file.getMimeType(), fileContent);
	  
	    try {
	      file = client.files().insert(file, mediaContent).execute();
	      Permission permission=client.permissions().insert(file.getId(), p).execute();
	      //Dar permisos al archivo
	      System.out.println(emails.size());
		 for (int i=0;i<emails.size();i++) {
			System.out.println(emails.get(i));
			  
			 	/*Permission p=new Permission();
			  p.setValue(string);
			  p.setType("user");//user || group || domain || anyone
			  p.setRole("reader");//owner || reader || writer || commenter
			  p.setEmailAddress(string);
		  		
			  Permission permission=client.permissions().insert(file.getId(), p).execute();
		  */
		  }
	      System.out.println(file.getId());
	      return file;
	    } catch (IOException e) {
	      System.out.println("An error occured: " + e);
	      return null;
	    }
	  }
	
	/*********************** Sincronizar BD a Google Drive ***************************/
	
	public static boolean checkType(Rattach rattach, Vector<RegistryAttachmentType> types){
		boolean bool=false;
		short type = rattach.getType();
		RegistryAttachmentType t = RegistryAttachmentType.values()[type];
		int i=0;
		while(i<types.size() && !bool){
			if(types.get(i).equals(t))
				bool=true;
			i++;
		}
		return bool;
	}
	
	public static void sync(Rattach rattach,String domain,Vector<RegistryAttachmentType> types) throws SQLException, AonConnectionException, IOException, NoSuchAlgorithmException{
		System.out.println(checkType(rattach,types));
		if(checkType(rattach,types)){
			Vector<String> emails=DatabaseSync.getEmails(domain);
			if(rattach.getDriveId()==null){
				InputStream i =DatabaseSync.getData(rattach.getId(),domain);
				rattach.setData(i);
				System.out.println(i+"   "+rattach.getId());
				File file= insertFile(rattach,emails);
				if(file!=null){
					DatabaseSync.addDriveId(file.getId(),rattach.getId(),domain);
					//DatabaseSync.deleteBlob(rattach.getId(),domain);
				}
			}
			else{
				InputStream is =DatabaseSync.getData(rattach.getId(),domain);
				rattach.setData(is);
				File fileAux = getFile(rattach.getDriveId());
				
				System.out.println("MD5 --> "+fileAux.getMd5Checksum()+"  :  "+ CheckSum.getMD5Checksum(is));
				
				if(!fileAux.getMd5Checksum().equals(CheckSum.getMD5Checksum(is))){
					InputStream i =DatabaseSync.getData(rattach.getId(),domain);
					rattach.setData(i);
					System.out.println(i+"   "+rattach.getId());
					File file= updateFile(rattach);
					if(file!=null){
						DatabaseSync.addDriveId(file.getId(),rattach.getId(),domain);
						//DatabaseSync.deleteBlob(rattach.getId(),domain);
					}
				}	
			}
		}
	}
	
	public static void synchronize(Integer id, String domain,Vector<RegistryAttachmentType> types) throws SQLException, AonConnectionException, IOException, KeyStoreException, GeneralSecurityException{
		
		
		serviceInitialize(DatabaseSync.getServiceAccount(domain));
		Rattach rattach=DatabaseSync.getFile(id,domain); 
		
		sync(rattach,domain,types);
	}
	
	public static void synchronize(String domain,Vector<RegistryAttachmentType> types) throws SQLException, AonConnectionException, IOException, KeyStoreException, GeneralSecurityException{
		DriveData dd=DatabaseSync.getDomainFiles(domain);
		
		serviceInitialize(dd.getGservice());
			
		if(dd.getRattachs()!=null && dd.getRattachs().size()>0){
			for(int j=0;j<dd.getRattachs().size();j++){
				sync(dd.getRattachs().get(j),domain,types);
			}
		}	
		
	}
	
	public static void synchronize(Vector<RegistryAttachmentType> types) throws IOException, SQLException, AonConnectionException, KeyStoreException, GeneralSecurityException {
		
		Map<String, String> domains=getDomains();//obtiene todos los dominios de la BD
		for (String key : domains.keySet()) { // recorre todos los dominios de la BD	
				
			synchronize(key,types);
		}
		
	}
	
	
	/************************* DESCARGAR ARCHIVO DE DRIVE *********************/
	
	public static InputStream downloadFile(Drive drive,File file) {
	    if (file.getDownloadUrl() != null && file.getDownloadUrl().length() > 0) {
	      try {
	        HttpResponse resp =
	            drive.getRequestFactory().buildGetRequest(new GenericUrl(file.getDownloadUrl()))
	                .execute();
	        return resp.getContent();
	      } catch (IOException e) {
	        // An error occurred.
	        e.printStackTrace();
	        return null;
	      }
	    } else {
	      // The file doesn't have any content stored on Drive.
	      return null;
	    }
	  }
	

	
	/************************ Eliminar archivo **************************/
	
	public static void deleteFile(Drive drive,String fileId ) throws IOException{
		drive.files().delete(fileId).execute();
	}
	
	public static void delete(String fileId,String domain,int id) throws IOException, SQLException{
		client.files().delete(fileId).execute();
		DatabaseSync.deleteDriveID(id, domain);
	}
	
	/*********************** MAIN ******************************/
	public static void main(String[] args) throws GeneralSecurityException,
	IOException, ServletException, SQLException, AonConnectionException {
		
			parse(args);
			
		
			String domain = "audibal.aonsolutions.net";
			
			
			
			Vector<RegistryAttachmentType> types= new Vector<RegistryAttachmentType>();
			//types.add(RegistryAttachmentType.DOCUMENT);
			types.add(RegistryAttachmentType.MARKETING_TEMPLATE);
			/*types.add(RegistryAttachmentType.DOMAIN_BOOK_HISTORY);
			
			types.add(RegistryAttachmentType.ENTERPRISE_CONTRACT_CLAUSES);
			*/
			
			synchronize(domain,types);
			
			
			
			
			
			DomainGserviceaccount d=DatabaseSync.getServiceAccount(domain);
			serviceInitialize(d);
			DriveData dd=DatabaseSync.getDomainFiles(domain);
			for(int j=0;j<dd.getRattachs().size();j++){
				String id = dd.getRattachs().get(j).getDriveId();
				if(id!=null){
					File file=getFile(id);
				
					System.out.println(file.getTitle()+" link: "+file.getAlternateLink());
					
					//delete(id, "audibal.aonsolutions.net", dd.getRattachs().get(j).getId());
					
				}
			}
			
			
			
		
	
			
	}
	private static String types [];
	private static String domains[];
	
	private static void parse(String  args []) {
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
			
			types  = line.getOptionValues("type");
			domains= line.getOptionValues("domain");
			
		} catch (ParseException e) {
			helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX,
					options, true);
		}
	}
	  		
}
