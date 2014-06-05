package com.code.aon.google.apis;

import static com.code.aon.google.apis.DatabaseSync.getDomain;
import static com.code.aon.google.apis.DatabaseSync.getDomains;
import static com.code.aon.google.apis.DatabaseSync.getServiceAccount;
import static com.code.aon.google.apis.servlet.GoogleAuthorizationServletUtils.getHttpTransport;
import static com.code.aon.google.apis.servlet.GoogleAuthorizationServletUtils.getJsonFactory;
import static com.code.aon.google.apis.servlet.GoogleAuthorizationServletUtils.getPrincipalShortName;
import static com.code.aon.google.apis.servlet.GoogleAuthorizationServletUtils.newFlow;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.hibernate.engine.Collections;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.google.apis.servlet.GoogleAuthorizationCodeCallbackServlet;
import com.code.aon.pool.AonConnectionException;
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
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeCallbackServlet;


public class DriveUtils  {

	

	
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
	private static final String UPLOAD_FILE_PATH = "Enter File Path";
	private static final String DIR_FOR_DOWNLOADS = "Enter Download Directory";
	private static final java.io.File UPLOAD_FILE = new java.io.File(UPLOAD_FILE_PATH);
	private static String AonFolderID = "";
	
	public static void initialize() throws IOException,ServletException {
		//credential = newFlow().loadCredential(getPrincipalShortName(req));/**HttpServletRequest req**/
		
		//client = new Drive.Builder(getHttpTransport(), getJsonFactory(), credential)
		//	.setApplicationName("AON SOLUTIONS").build();
		
		client=GoogleAuthorizationCodeCallbackServlet.drive;
		
		AonFolderID = getAonFolder().getId();

	}
	
	public static Drive serviceInitialize(String domain) throws KeyStoreException, IOException, GeneralSecurityException, SQLException{
		
		DomainGserviceaccount dgserviceaccount= getServiceAccount(domain);
		
		final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
		final JsonFactory JSON_FACTORY = new JacksonFactory();
		final String SERVICE_ACCOUNT_ID = dgserviceaccount.getEmailAddress();

		InputStream keyStream = dgserviceaccount.getPrivateKey();
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
	
	/*************** SOBRE LA CARPETA AON *********************/
	
	public static File getAonFolder() throws IOException {
		FileList list = client.files().list().execute();
		File file= null;
		for(int i=0; i< list.size();i++){
			if (list.getItems().get(i).getTitle().equals("aonSolutions")){
				file=list.getItems().get(i);
				i=list.size();
			}
		}
		if (file==null){
			file = createAonFolder();
		}
		return file;
	}
	
	public static void insertToAonFolder(File folder,File file) throws IOException{
		//suponiendo que el archivo file ya esta en google DRive.
		ParentReference parent = new ParentReference();
		parent.setId(folder.getId());
		client.parents().insert(file.getId(), parent).execute();
		
	}
	
	public static File createAonFolder() throws IOException{
		File folder=new File();
		folder.setTitle("aonSolutions");
		folder.setDescription("aonSolutions files container");
		folder.setMimeType("application/vnd.google-apps.folder");
		folder.setAppDataContents(true);// Indica que es un archivo de la aplicación, por lo tanto, el usuario no podrá borrar el archivo.
		folder = client.files().insert(folder).execute().setId("appdata");
		return folder;
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

	public static FileList getFiles(){
		return new FileList();
		
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
	
	//HACERLO GENERICO
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
	
	private static File insertFile(Rattach rattach, String parentId) throws SQLException, AonConnectionException, IOException {
	    // File's metadata.
	    File file = newFile(rattach);
	    // Set the parent folder.
	    file.setParents(Arrays.asList(new ParentReference().setId(parentId)));
	    //hay que añadir el atributo parent a la BDtable rattach
	 
	 
	    // File's content.
	    java.io.File fileContent = Utils.InputStreamToFile(rattach);
	    FileContent mediaContent = new FileContent("mimeType", fileContent);
	  
	    try {
	      file = client.files().insert(file, mediaContent).execute();
	      DatabaseSync.addDriveIds(file.getId(), file.getParents().get(0).getId());
	      return file;
	    } catch (IOException e) {
	      System.out.println("An error occured: " + e);
	      return null;
	    }
	  }
	
	
	private static File insertFile(Rattach rattach) throws SQLException, AonConnectionException, IOException {
	    // File's metadata.
	    File file = newFile(rattach);

	    // File's content.
	    java.io.File fileContent = Utils.InputStreamToFile(rattach);
	    FileContent mediaContent = new FileContent("mimeType", fileContent);
	  
	    try {
	      file = client.files().insert(file, mediaContent).execute();
	      return file;
	    } catch (IOException e) {
	      System.out.println("An error occured: " + e);
	      return null;
	    }
	  }
	
	public static void synchronize() throws IOException, SQLException, AonConnectionException, KeyStoreException, GeneralSecurityException {
		
		Map<String, String> domains=getDomains();//obtiene todos los dominios de la BD
		for (String key : domains.keySet()) { // recorre todos los dominios de la BD	
			Vector<Domain> companies = getDomain(key);//Obtiene todos los dominios del dominio padre
			Map<Integer,Vector<Rattach>> map=DatabaseSync.getFilesAll(key);
			for(int i=0;i<companies.size();i++){
				serviceInitialize(companies.get(i).getName());
				FileList files = Quicksort.sort(getFiles());
				java.util.Vector<Rattach> rattachs = map.get(companies.get(i).getId());
				for(int j=0;j<rattachs.size();j++){
					//subir archivo de la base de datos
					int aux=searchFiles(files, rattachs.get(j).getDescription(), rattachs.size());
					if(aux==-1){
						File file= insertFile(rattachs.get(j));
					}
				}
			}
		}
		
	}
	public static void uploadBDFiles() throws SQLException, IOException, AonConnectionException{
		java.util.Vector<Rattach> rattachs = DatabaseSync.getFiles("");
		FileList files = Quicksort.sort(getFiles());
		for(int j=0;j<rattachs.size();j++){
			//subir archivo de la base de datos
			int aux=searchFiles(files, rattachs.get(j).getDescription(), rattachs.size());
			if(aux==-1){
				File file= insertFile(rattachs.get(j),AonFolderID);
			}
		}
	}
	
	/* Otro modo */
	public static void uploadFile(FileContent fileContent, File file,String folderId) throws IOException{
		ParentReference parent = new ParentReference();
		parent.setId(folderId);
		file=client.files().insert(file, fileContent).execute();
		client.parents().insert(file.getId(),parent).execute();
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
	  		
}
