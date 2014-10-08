package com.code.aon.google.apis;

import static com.code.aon.google.apis.DatabaseSync.getDomains;
import static com.code.aon.google.apis.jooq.DBConsults.getCategory;
import static com.code.aon.google.apis.jooq.DBConsults.getCategoryName;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.mail.MessagingException;
import javax.naming.NamingException;
import javax.servlet.ServletException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.IBlobManager;
import com.code.aon.common.IBlobObject;
import com.code.aon.common.dao.hibernate.HibernateBlobManager;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.google.apis.drive.SearchFiles;
import com.code.aon.google.apis.jooq.DBConsults;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.google.sql.AbstractSQL.DomainGserviceaccount;
import com.esferalia.aon.google.sql.AbstractSQL.Rattach;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.FileContent;
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
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.Property;

public class DriveUtils implements IBlobManager {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DriveUtils.class.getName());

	public static class CheckSum {
		/***
		 * Convierte un arreglo de bytes a String usando valores hexadecimales
		 * 
		 * @param digest
		 *            arreglo de bytes a convertir
		 * @return String creado a partir de <code>digest</code>
		 */
		private static String toHexadecimal(byte[] digest) {
			String hash = "";
			for (byte aux : digest) {
				int b = aux & 0xff;
				if (Integer.toHexString(b).length() == 1)
					hash += "0";
				hash += Integer.toHexString(b);
			}
			return hash;
		}

		/***
		 * Realiza la suma de verificación de un archivo mediante MD5
		 * 
		 * @param archivo
		 *            archivo a que se le aplicara la suma de verificación
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
			System.out.println("================== " + name
					+ " ==================");
			System.out.println();
		}

		static void header2(String name) {
			System.out.println();
			System.out.println("~~~~~~~~~~~~~~~~~~ " + name
					+ " ~~~~~~~~~~~~~~~~~~");
			System.out.println();
		}
	}

	public static class Quicksort {

		private static FileList files;
		private static int number;

		public static FileList sort(FileList fils) {
			files = fils;
			number = fils.getItems().size();
			quicksort(0, number - 1);

			// Collections.sort(fils.getItems());

			return files;
		}

		static void quicksort(int low, int high) {
			int i = low, j = high;
			String pivot = files.getItems().get(low + (high - low) / 2)
					.getTitle();
			while (i <= j) {
				while (files.getItems().get(i).getTitle().compareTo(pivot) < 0) {
					i++;
				}
				while (files.getItems().get(j).getTitle().compareTo(pivot) > 0) {
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

	private static final DriveUtils DRIVEUTILS = new DriveUtils();

	// private static Credential credential;
	private static Drive client;

	public static DriveUtils getInstace() {
		// TODO Apéndice de método generado automáticamente
		return DRIVEUTILS;
	}

	public static Drive serviceInitialize(DomainGserviceaccount d)
			throws KeyStoreException, IOException, GeneralSecurityException {

		final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
		final JsonFactory JSON_FACTORY = new JacksonFactory();
		final String SERVICE_ACCOUNT_ID = d.getEmailAddress();

		InputStream keyStream = d.getPrivateKey();
		PrivateKey serviceAccountPrivateKey = SecurityUtils
				.loadPrivateKeyFromKeyStore(SecurityUtils.getPkcs12KeyStore(),
						keyStream, "notasecret", "privatekey", "notasecret");

		GoogleCredential credential = new GoogleCredential.Builder()
				.setTransport(HTTP_TRANSPORT)
				.setJsonFactory(JSON_FACTORY)
				.setServiceAccountId(SERVICE_ACCOUNT_ID)
				.setServiceAccountScopes(
						java.util.Collections.singletonList(DriveScopes.DRIVE))
				.setServiceAccountPrivateKey(serviceAccountPrivateKey).build();

		client = new com.google.api.services.drive.Drive.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(
				"AON SOLUTIONS").build();

		return client;

	}

	/****
	 * CREAR CARPETA
	 * 
	 * @throws SQLException
	 * @throws AonConnectionException
	 *****/

	public static void createFolders(Drive drive, String domain, String parent)
			throws IOException, AonConnectionException, SQLException {
		Result<Record3<Integer, String, String>> category = getCategory(domain);

		for (Record3<Integer, String, String> record3 : category) {
			createFolder(drive, record3.value2(), record3.value3(), true,
					parent, domain);

		}
		createFolder(drive, "otros", "", true, parent, domain);
	}

	public static void createAttachFolders(Drive drive, String domain,
			String parent) throws IOException, AonConnectionException,
			SQLException {
		File file1 = createFolder(drive, "contract", "", true, parent, domain);
		createFolder(drive, "nominas", "", true, file1.getId(), domain);
		createFolder(drive, "item", "", true, parent, domain);
		createFolder(drive, "invoice", "", true, parent, domain);
		createFolder(drive, "offer", "", true, parent, domain);
		createFolder(drive, "payroll", "", true, parent, domain);
		createFolder(drive, "project", "", true, parent, domain);
		File file2 = createFolder(drive, "registry", "", true, parent, domain);
		createFolders(drive, domain, file2.getId());
		createFolder(drive, "sepe", "", true, parent, domain);
	}

	public static File createFolder(Drive drive, String title,
			String description, Boolean hasParent, String parent, String domain)
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
			property.setValue(domain);
			property.setKey("domain");
			drive.properties().insert(folder.getId(), property).execute();

		}
		return folder;
	}

	public static File principal(Drive drive, String domain, FileInfo fileInfo)
			throws IOException, AonConnectionException, SQLException,
			NamingException, KeyStoreException, GeneralSecurityException {
		Vector<String> emails = new Vector<String>();
		Vector<ParentReference> parents = new Vector<ParentReference>();
		// fileInfo.getEmails().add("aibanezdegau004@gmail.com");
		/*
		 * if(fileInfo.getEmails()!=null &&
		 * fileInfo.getAonType().equals("registry")){ for (int
		 * i=0;i<fileInfo.getEmails().size();i++) { if
		 * (Utils.isGmail(fileInfo.getEmails().get(i))){ String id =
		 * insertToFolder(drive, fileInfo.getEmails().get(i), domain, fileInfo);
		 * parents.add(new ParentReference().setId(id));
		 * emails.add(fileInfo.getEmails().get(i)); } }
		 * 
		 * }
		 */
		File file = new File();
		try {
			file = insertFile(drive, fileInfo, parents, emails, domain);
		} catch (MessagingException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		}
		return file;
	}

	public static String insertToFolder(Drive drive, String email,
			String domain, FileInfo fileInfo) throws IOException,
			AonConnectionException, SQLException {
		FileList fl = getRootFiles2(drive, email);
		Boolean esta = false;
		int i = 0;
		File file = new File();
		String hijo = new String();
		int pos = email.indexOf('@');
		String title = email.substring(0, pos) + "." + domain;
		// String title1 = "AONSOLUTIONS-"+email;
		System.out.println(fl.getItems().size());

		while (!esta && i < fl.getItems().size()) {

			file = fl.getItems().get(i);
			System.out.println(file.getTitle() + " - " + title + " - "
					+ file.getTitle().equalsIgnoreCase(title));
			if (file.getTitle().equalsIgnoreCase(title)) {// file.getUserPermission().getEmailAddress()==email){
				// tratando todos los attach a la hora de compartir la carpeta (
				// registry, invoice, project ,...)
				// hijo = searchParent(drive,fileInfo,domain,file.getId());

				hijo = insertFile(drive, fileInfo.getCategory(), domain, file);

				// tratando unicamente rattach (registry) a la hora de compartir
				// la carpeta.
				esta = true;
			}
			i++;
		}
		if (!esta) {
			// String username="";
			file = createFolder(drive, title, "", false, null, domain);
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
			hijo = insertFile(drive, fileInfo.getCategory(), domain, file);
		}

		return hijo;

	}

	private static String searchParent(Drive drive, FileInfo fileInfo,
			String domain, String parent) throws IOException,
			AonConnectionException, SQLException {
		String a = "'" + parent + "'";
		FileList aux = drive.files().list().setQ(a + " in parents").execute();

		// FileList aux=getParentFiles(drive, parent);

		for (File f : aux.getItems()) {
			if (f.getTitle().equals(fileInfo.getAonType())) {
				if (fileInfo.getAonType().equals("registry")) {
					return insertFile(drive, fileInfo.getCategory(), domain, f);
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

	private static String insertFile(Drive drive, int category, String domain,
			File file) throws AonConnectionException, SQLException, IOException {
		String name = "otros";
		if (category != -1) {
			Result<Record1<String>> categoryName = getCategoryName(category,
					domain);
			for (Record1<String> record1 : categoryName) {
				name = record1.value1();
			}
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
			String domain) throws SQLException, AonConnectionException,
			IOException, MessagingException, KeyStoreException,
			GeneralSecurityException {
		// File's metadata.
		File file = newFile(fileInfo.getMimetype(), fileInfo.getTitle());
		file.setModifiedDate(new DateTime(new Date()));
		file.setParents(parents);
		// File's content.

		java.io.File fileContent = Utils.InputStreamToFile(fileInfo);
		FileContent mediaContent = new FileContent(file.getMimeType(),
				fileContent);

		try {
			file = drive.files().insert(file, mediaContent).execute();
			// GmailUtils.createEmail("aibanezdegau004@gmail.com",
			// "aonsolutions@gmail.com", "", "hola");
			setProperties(drive, file.getId(), fileInfo, domain);
			LOGGER.info("Document uploaded: {}-{}",
					file.getId() + file.getTitle());
			// Dar permisos al archivo
			fileContent.delete();
			return file;
		} catch (IOException e) {
			LOGGER.error("Error uploading document: {}-{}. {}", file.getId()
					+ file.getTitle(), e.getMessage());
			return null;
		}
	}

	private static void setProperties(Drive drive, String id,
			FileInfo fileInfo, String domain) throws AonConnectionException,
			SQLException, IOException {
		String name = "otros";
		Result<Record1<String>> categoryName = getCategoryName(
				fileInfo.getCategory(), domain);
		for (Record1<String> record1 : categoryName) {
			name = record1.value1();
		}
		Property property1 = new Property();
		property1.setValue(name);
		// property1.setEtag("category");
		property1.setKey("category");

		String type = RegistryAttachmentType.values()[fileInfo.getType()]
				.toString();
		Property property2 = new Property();
		property2.setValue(type);
		// property2.setEtag("type");
		property2.setKey("type");
		property2.setVisibility("PRIVATE");

		Property property3 = new Property();
		property3.setValue(domain);
		// property2.setEtag("type");
		property3.setKey("domain");

		Property property4 = new Property();
		property4.setValue(fileInfo.getAonType());
		property4.setKey("aontype");

		drive.properties().insert(id, property1).execute();
		drive.properties().insert(id, property2).execute();
		drive.properties().insert(id, property3).execute();
		drive.properties().insert(id, property4).execute();

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

	public static File getFile(String fileId) throws IOException {
		return client.files().get(fileId).execute();
	}

	public static File getFile(Drive drive, String fileId) throws IOException {
		return drive.files().get(fileId).execute();
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

	public static File newFile(Rattach rattach) {
		short type = rattach.getMimeType();
		MimeType t = MimeType.values()[type];
		File file = new File();

		file.setShared(true);
		file.setTitle(rattach.getDescription());
		file.setMimeType(t.getName());
		// file.setAppDataContents(true);// Indica que es un archivo de la
		// aplicación, por lo tanto, el usuario no podrá borrar el archivo.
		return file;
	}

	public static File newFile(short mimetype, String title) {

		MimeType t = MimeType.values()[mimetype];
		File file = new File();

		file.setShared(true);
		file.setTitle(title);
		file.setMimeType(t.getName());

		// file.setAppDataContents(true);// Indica que es un archivo de la
		// aplicación, por lo tanto, el usuario no podrá borrar el archivo.
		return file;
	}

	public static File insertFile(Drive drive, java.io.File file,
			DriveFile driveFile) throws IOException {

		File fileAux = new File();
		fileAux.setTitle(driveFile.getDescription());
		fileAux.setMimeType(driveFile.getMimetype());

		FileContent mediaContent = new FileContent(driveFile.getMimetype(),
				file);

		File fileDrive = drive.files().insert(fileAux, mediaContent).execute();

		// file.setAppDataContents(true);// Indica que es un archivo de la
		// aplicación, por lo tanto, el usuario no podrá borrar el archivo.
		return fileDrive;

	}

	public static File updateFile(FileInfo fileInfo) throws IOException {
		File file = getFile(fileInfo.getDriveId());
		file.setModifiedDate(new DateTime(new Date()));

		// File's content.
		java.io.File fileContent = Utils.InputStreamToFile(fileInfo);
		FileContent mediaContent = new FileContent(file.getMimeType(),
				fileContent);

		try {
			file = client.files()
					.update(fileInfo.getDriveId(), file, mediaContent)
					.execute();
			return file;
		} catch (IOException e) {
			System.out.println("An error occured: " + e);
			return null;
		}
	}

	private static File insertFile(Rattach rattach, Vector<String> emails,
			Vector<String> pemails) throws SQLException,
			AonConnectionException, IOException {
		// File's metadata.
		File file = newFile(rattach);
		file.setParents(Arrays.asList(new ParentReference().setId(rattach
				.getCategory().toString())));

		// File's content.
		java.io.File fileContent = Utils.InputStreamToFile(rattach);
		FileContent mediaContent = new FileContent(file.getMimeType(),
				fileContent);
		try {
			file = client.files().insert(file, mediaContent).execute();
			setPermissions(file.getId(), emails, pemails);

			System.out.println("Subido archivo: " + file.getTitle());
			// Dar permisos al archivo

			return file;
		} catch (IOException e) {
			System.out.println("An error occured: " + e);
			return null;
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

	public static boolean checkTypes(short fileType) {
		boolean bool = false;
		if (fileType != -1) {
			String rat = RegistryAttachmentType.values()[fileType].toString();
			for (String type : types) {

				if (rat.equals(type) && type != null) {
					bool = true;
				}
			}
		}
		return bool;
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

	/*
	 * old public static void sync(Drive drive,Rattach rattach,String domain)
	 * throws SQLException, AonConnectionException, IOException,
	 * NoSuchAlgorithmException{
	 * 
	 * 
	 * if(checkTypes(rattach.getType())){
	 * 
	 * Vector<String> emails= new
	 * Vector<String>();//DatabaseSync.getEmails(rattach.getId(),domain);
	 * Vector<String> pemails=new
	 * Vector<String>();//DatabaseSync.getPersonEmails(rattach.getId(),domain);
	 * 
	 * pemails.add("aibanezdegau004@gmail.com");
	 * pemails.add("ibznav@gmail.com"); emails.add("ander.ibz@gmail.com");
	 * emails.add("aonsolutions@gmail.com");
	 * 
	 * 
	 * if(rattach.getDriveId()==null){ InputStream i
	 * =DatabaseSync.getFileData(rattach.getId(),domain); rattach.setData(i);
	 * 
	 * File file = principal(drive, domain, emails, pemails, rattach);
	 * 
	 * //File file= insertFile(rattach,emails,pemails); if(file!=null){
	 * DatabaseSync.addDriveId(file.getId(),rattach.getId(),domain);
	 * //DatabaseSync.deleteBlob(rattach.getId(),domain); } } else{ InputStream
	 * is =DatabaseSync.getFileData(rattach.getId(),domain);
	 * rattach.setData(is); File fileAux = getFile(rattach.getDriveId());
	 * 
	 * if(!fileAux.getMd5Checksum().equals(CheckSum.getMD5Checksum(is))){
	 * InputStream i =DatabaseSync.getFileData(rattach.getId(),domain);
	 * rattach.setData(i); File file= updateFile(new FileInfo());
	 * if(file!=null){
	 * DatabaseSync.addDriveId(file.getId(),rattach.getId(),domain);
	 * //DatabaseSync.deleteBlob(rattach.getId(),domain); } } } } }
	 */
	// nominaas!!!
	public static File sync(Rattach rattach, Vector<String> emails,
			DomainGserviceaccount d) throws SQLException,
			AonConnectionException, IOException, KeyStoreException,
			GeneralSecurityException, NamingException {
		// HAY QUE CONVERTIR EL RATTACH EN FILEINFO PARA LLAMAR A
		// PRINCIPAL!!!!!!
		FileInfo fileInfo = new FileInfo("registry", rattach.getType(),
				rattach.getData(), rattach.getDriveId(), rattach.getId(),
				rattach.getDescription(), rattach.getMimeType());
		fileInfo.setEmails(emails);
		fileInfo.setIsNomina(true);
		Drive drive = serviceInitialize(d);
		File file = null;
		String domain = AonUtil.getDomainName();
		if (rattach.getDriveId() == null) {
			file = principal(drive, domain, fileInfo);
		} else {
			File fileAux = getFile(rattach.getDriveId());
			if (!fileAux.getMd5Checksum().equals(
					CheckSum.getMD5Checksum(rattach.getData()))) {
				file = updateFile(new FileInfo());
			}
		}
		return file;
	}

	public static boolean sync2(Drive drive, FileInfo fileInfo, String domain)
			throws SQLException, AonConnectionException, IOException,
			NamingException, KeyStoreException, GeneralSecurityException {

		if (fileInfo.getAonType().equals("project")
				|| fileInfo.getAonType().equals("offer")
				|| checkTypes(fileInfo.getType())) {

			if (fileInfo.getDriveId() == null) {
				String type = RegistryAttachmentType.values()[fileInfo.getType()]
						.name();
				if ( dryRun ) {
					LOGGER.info("Dry Run {} '{}': Not at Drive. It will be created & uploaded.", type, fileInfo.getTitle());
					return true;
				}
				// viewFile(fileInfo);
				File file = principal(drive, domain, fileInfo);

				if (file != null) {
					fileInfo.setDriveId(file.getId());
					setDriveId(fileInfo, domain, file.getFileSize().toString());
				}
				LOGGER.info("{} '{}': Not at Drive. It was created & uploaded [{}].", type, fileInfo.getTitle(), file.getId());
				return true;
			} else {
				
				if (fileInfo.getData() == null) {
					LOGGER.debug("Skip '{}': No new data.", fileInfo.getTitle());
					return false;
				}

				File fileAux = getFile(fileInfo.getDriveId());

				if (!fileAux.getMd5Checksum().equals(
								CheckSum.getMD5Checksum(fileInfo.getData()))) {

					String type = RegistryAttachmentType.values()[fileInfo.getType()]
							.name();
					if ( dryRun ) {
						LOGGER.info("Dry Run {} '{}': Changed. It will be synchronized/uploaded.", type, fileInfo.getTitle());
						return true;
					}
					File file = updateFile(fileInfo);
					if (file != null) {
						fileInfo.setDriveId(file.getId());
						setDriveId(fileInfo, domain, file.getFileSize()
								.toString());
					}
					LOGGER.info("{} '{}': Changed. It was synchronized/uploaded [{}].", type, fileInfo.getTitle(), file.getId());
					return true;
				} else {
					LOGGER.debug(
							"Skip '{}': New data it's the same that at drive ( MD5s are the same ).",
							fileInfo.getTitle());
					return false;
				}
				// else updateDateSync(drive,fileInfo);
			}
		} else {
			String type = RegistryAttachmentType.values()[fileInfo.getType()]
					.name();
			LOGGER.debug("Skip '{}': {} won't be synchronized.",
					fileInfo.getTitle(), type);
			return false;
		}
		/*
		 * else{ if(fileInfo.getDriveId()!=null) updateDateSync(drive,fileInfo);
		 * }
		 */

	}

	public static void updateDateSync(Drive drive, FileInfo fileInfo)
			throws IOException {
		File file = getFile(fileInfo.getDriveId());
		file.setModifiedDate(new DateTime(new Date()));
		drive.files().update(fileInfo.getDriveId(), file).execute();
	}

	public static void setDriveId(FileInfo fileInfo, String domain, String size)
			throws SQLException, AonConnectionException {

		if (fileInfo.getAonType().equals("registry")) {
			DatabaseSync.addDriveId(fileInfo.getDriveId(),
					fileInfo.getFileId(), domain, size);
			DatabaseSync.deleteBlob(fileInfo.getFileId(), domain);
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

	public static void synchronize(Integer id, String domain)
			throws SQLException, AonConnectionException, IOException,
			KeyStoreException, GeneralSecurityException {
		Drive drive = serviceInitialize(DatabaseSync.getServiceAccount(domain));
		Rattach rattach = DatabaseSync.getFile(id, domain);

		// sync(drive,rattach,domain);
	}

	public static void synchronize(String domain) throws SQLException,
			AonConnectionException, IOException, KeyStoreException,
			GeneralSecurityException, NamingException {
		// DriveData dd=DatabaseSync.getDomainFiles(domain);
		// get iattachs, get contract attachs,.... y añadir a DriveData

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
					InputStream i = DatabaseSync.getFileData(
							attach.getFileId(), domain);
					Vector<String> emails = DatabaseSync.getEmails(
							attach.getFileId(), domain);
					Vector<String> pemails = DatabaseSync.getPersonEmails(
							attach.getFileId(), domain);
					emails.addAll(pemails);
					attach.setEmails(emails);
					attach.setData(i);
				} else if (attach.getAonType().equals("contract")) {
					attach = DBConsults.getDataContractAttach(domain, attach);
					attach = DBConsults.getEmailsContractAttach(domain, attach);
				} else if (attach.getAonType().equals("item")) {
					attach = DBConsults.getDataIattach(domain, attach);
				} else if (attach.getAonType().equals("invoice")) {
					attach = DBConsults.getDataInvoiceAttach(domain, attach);
					attach = DBConsults.getEmailsInvoiceAttach(domain, attach);
				} else if (attach.getAonType().equals("offer")) {
					attach = DBConsults.getDataOfferAttach(domain, attach);
				} else if (attach.getAonType().equals("payroll")) {
					attach = DBConsults.getDataPayrollAttach(domain, attach);
				} else if (attach.getAonType().equals("project")) {
					attach = DBConsults.getDataProjectAttach(domain, attach);
					attach = DBConsults.getEmailsProjectAttach(domain, attach);
				} else if (attach.getAonType().equals("sepe")) {
					attach = DBConsults.getDataSepeAttach(domain, attach);
				}
				
				sync2(drive, attach, domain);
				// long size = DriveUtils.totalSize(domain);
				// DBConsults.setDriveSize(domain, size,
				// dd.getGservice().getDomain());
			}

			// }
		}
		// Iattach

		/*
		 * rattach viejo if(dd.getRattachs()!=null &&
		 * dd.getRattachs().size()>0){ for(int
		 * j=0;j<dd.getRattachs().size();j++){
		 * sync(drive,dd.getRattachs().get(j),domain); } }
		 */
	}

	public static void viewFile(FileInfo fileInfo) {
		System.out.println("------------------------------------------");
		System.out.println("AONTYPE: " + fileInfo.getAonType());
		System.out.println("TITLE: " + fileInfo.getTitle());
		if (fileInfo.getEmails() != null) {
			System.out.println("EMAILS: ");
			for (String email : fileInfo.getEmails()) {
				System.out.println("	" + email);
			}
		}

		System.out.println("------------------------------------------");
	}

	public static void synchronize() throws IOException, SQLException,
			AonConnectionException, KeyStoreException,
			GeneralSecurityException, NamingException {

		Map<String, String> domains = getDomains();// obtiene todos los dominios
													// de la BD
		for (String key : domains.keySet()) { // recorre todos los dominios de
												// la BD
			synchronize(key);

		}

	}

	public static DriveData getAttachs(String domain) throws SQLException,
			AonConnectionException {
		DriveData dd = new DriveData();

		dd.setDomain(domain);
		// de momento solo se sincroniza con Rattach
		// REGISTRY ATTACH
		dd = DatabaseSync.getDomainFiles(domain);
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

	public static InputStream downloadFile(Drive drive, File file) {
		if (file.getDownloadUrl() != null && file.getDownloadUrl().length() > 0) {
			try {
				HttpResponse resp = drive.getRequestFactory()
						.buildGetRequest(new GenericUrl(file.getDownloadUrl()))
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

	public static void deleteFile(Drive drive, String fileId)
			throws IOException {
		drive.files().delete(fileId).execute();
	}

	public static void delete(String fileId, String domain, int id)
			throws IOException, SQLException {
		client.files().delete(fileId).execute();
		DatabaseSync.deleteDriveID(fileId, domain);
	}

	public static void deleteAll(Drive drive, String domain)
			throws IOException, SQLException, AonConnectionException {
		FileList fl = drive.files().list().execute();
		for (File f : fl.getItems()) {
			deleteFile(drive, f.getId());
			DatabaseSync.delRattachDriveId(f.getId(), domain);
			System.out.println("--------------------------------------");
			System.out.println("Archivo eliminado:");
			System.out.println("   --> ID:" + f.getId());
			System.out.println("   --> TITLE:" + f.getTitle());
			System.out.println("--------------------------------------");
		}
	}

	public static void deleteDriveId(String driveId, String domain)
			throws SQLException {
		DatabaseSync.deleteDriveID(driveId, domain);
		DBConsults.deleteDriveIdContractAttach(domain, driveId);
		DBConsults.deleteDriveIdIattach(domain, driveId);
		DBConsults.deleteDriveIdInvoiceAttach(domain, driveId);
		DBConsults.deleteDriveIdOfferAttach(domain, driveId);
		DBConsults.deleteDriveIdPayrollAttach(domain, driveId);
		DBConsults.deleteDriveIdProjectAttach(domain, driveId);
		DBConsults.deleteDriveIdSepeAttach(domain, driveId);
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

	/***********************
	 * MAIN
	 * 
	 * @throws NamingException
	 ******************************/
	public static void main(String[] args) throws GeneralSecurityException,
			IOException, ServletException, SQLException,
			AonConnectionException, NamingException {

		parse(args);
		/*
		 * 
		 * if (domains==null || domains.length==0 ||
		 * domains[0].equals("TODOS")){ synchronize(); } else{ for (String
		 * string : domains) { synchronize(string); } }
		 */

		// Rattach rattach = null;

		String domain = "novus.aibanez.net";
		// synchronize(domain);

		DriveData dd = DatabaseSync.getDomainFiles(domain);

		Drive drive = serviceInitialize(dd.getGservice());

		/*
		 * FileList fl = drive.files().list().execute(); for (File file :
		 * fl.getItems()) {
		 * System.out.println(file.getId()+" - "+file.getTitle()); }* /**Rattach
		 * rattach = dd.getRattachs().get(0); InputStream i
		 * =DatabaseSync.getFileData(rattach.getId(),domain);
		 * rattach.setData(i);
		 * 
		 * 
		 * Vector<String> a = new Vector<String>();
		 * a.add("aibanezdegau004@gmail.com"); principal(drive,domain,a,new
		 * Vector<String>(), rattach);
		 */

		deleteAll(drive, domain);

		/**
		 * FileList fl = searchFiles(drive, "'aon'"); for (File f :
		 * fl.getItems()) { System.out.println(f.getTitle()); }
		 **/
		/*
		 * String domain2="clividerm-exem.aibanez.net";
		 * 
		 * 
		 * String domain="audibal.aonsolutions.net";
		 * 
		 * //synchronize(domain); Vector<String>
		 * pemails=DatabaseSync.getPersonEmails(10000, domain2); Vector<String>
		 * emails=DatabaseSync.getEmails(365 , domain); for (String string :
		 * emails) { System.out.print(string); } DriveData
		 * dd=DatabaseSync.getDomainFiles(domain);
		 * 
		 * serviceInitialize(dd.getGservice());
		 * 
		 * for(int j=0;j<dd.getRattachs().size();j++){ String id =
		 * dd.getRattachs().get(j).getDriveId(); if(id!=null){ File
		 * file=getFile(id);
		 * 
		 * System.out.println(file.getTitle()+" link: "+file.getAlternateLink());
		 * 
		 * //delete(id, "audibal.aonsolutions.net",
		 * dd.getRattachs().get(j).getId());
		 * 
		 * } }
		 */

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
		System.out.println("GEEETT BLOB DRIVEEEEEE");

		String domain = AonUtil.getDomainName();
		DomainGserviceaccount sa = null;

		FileInfo file = null;
		file = DatabaseSync.getDriveId(domain,
				(Integer) blobObject.getReference());
		if (file.getDriveId() != null) {
			System.out.println("GEEETT BLOB DRIVEEEEEE");

			try {
				sa = DatabaseSync.getServiceAccount(domain);
			} catch (SQLException e) {
				// TODO Bloque catch generado automáticamente
				e.printStackTrace();
			}

			Drive drive = null;

			try {
				drive = serviceInitialize(sa);
			} catch (KeyStoreException e) {
				// TODO Bloque catch generado automáticamente
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Bloque catch generado automáticamente
				e.printStackTrace();
			} catch (GeneralSecurityException e) {
				// TODO Bloque catch generado automáticamente
				e.printStackTrace();
			}

			File file2 = null;

			try {
				file2 = getFile(drive, file.getDriveId());
			} catch (IOException e) {
				// TODO Bloque catch generado automáticamente
				e.printStackTrace();
			}

			InputStream data = downloadFile(drive, file2);

			try {
				return Utils.InputStreamToByte(data);
			} catch (IOException e) {
				// TODO Bloque catch generado automáticamente
				e.printStackTrace();
			}
		}

		return HibernateBlobManager.getInstance().getBlob(blobObject, property);

	}

	@Override
	public void setBlobs(IBlobObject blobObject) {
		// TODO Apéndice de método generado automáticamente
		System.out.println("lalalaalalalal");
		HibernateBlobManager.getInstance().setBlobs(blobObject);

		String[] aux = { "LOGO", "DOCUMENT" };
		types = aux;
		String domain = AonUtil.getDomainName();
		DomainGserviceaccount sa = null;
		try {
			sa = DatabaseSync.getServiceAccount(domain);
		} catch (SQLException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		}

		Drive drive = null;

		try {
			drive = serviceInitialize(sa);
		} catch (KeyStoreException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		}

		if (blobObject.getReference() != null) {

			FileInfo file = null;
			file = DatabaseSync.getDriveId(domain,
					(Integer) blobObject.getReference());
			try {
				sync2(drive, file, domain);
			} catch (NoSuchAlgorithmException e) {
				// TODO Bloque catch generado automáticamente
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Bloque catch generado automáticamente
				e.printStackTrace();
			} catch (AonConnectionException e) {
				// TODO Bloque catch generado automáticamente
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Bloque catch generado automáticamente
				e.printStackTrace();
			} catch (NamingException e) {
				// TODO Bloque catch generado automáticamente
				e.printStackTrace();
			} catch (KeyStoreException e) {
				// TODO Bloque catch generado automáticamente
				e.printStackTrace();
			} catch (GeneralSecurityException e) {
				// TODO Bloque catch generado automáticamente
				e.printStackTrace();
			}
		}
	}

	public static long totalSize(String domain) throws IOException,
			SQLException, KeyStoreException, GeneralSecurityException {
		DomainGserviceaccount d = DatabaseSync.getServiceAccount(domain);
		Drive drive = serviceInitialize(d);
		long size = 0;
		FileList fl = SearchFiles
				.searchFilesProperties(drive, "domain", domain);
		for (File f : fl.getItems()) {
			size = size + f.getFileSize();
		}
		return size;
	}
}
