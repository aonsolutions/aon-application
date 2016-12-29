package com.code.aon.google.apis.drive;

import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.tools.csv.CSVReader;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.google.apis.DriveUtils;
import com.code.aon.google.apis.FileInfo;
import com.code.aon.google.apis.jooq.DBConsults;
import com.code.aon.google.apis.jooq.DBDrive;
import com.code.aon.google.apis.jooq.DBSync;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.pool.ConnectionInfo;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.server.io.AonFileUtils;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.google.api.client.http.FileContent;
import com.google.api.client.util.DateTime;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Property;



public class ServiconveniosSynchronize {

	static FTPClient client = new FTPClient();
	
	static String sFTP = "ftp.aonsolutions.net";
	static String sUser = "serviconvenios";
	static String sPassword = "aon2014SC";

	public static void sync(Domain domain) throws AonConnectionException{
		try {
			client.connect(server);
			client.login(user, password);

			File convenios = download(client, "/convenios.csv");
			
			
			FileInputStream fis = new FileInputStream(convenios);
			
			InputStreamReader fileReader = new InputStreamReader(fis, Charset.forName("UTF-8") );
			CSVReader reader = new CSVReader(fileReader,';');
			Integer nuevos = 0, actualizados = 0;
			while (reader.hasNext() ) {
				String[] tokens = reader.readNext();
				if(tokens != null){
					String modificationDate = tokens[8];
					String fileName = tokens[6];
					String description = tokens[3];
					String tag1 = tokens[1];
					String tag2 = tokens[2];
				
					String idStr = fileName.substring(4, 8);
					Integer id = -Integer.parseInt(idStr);
					FileInfo fi = DBDrive.getServiConvenio(domain, getUser(), id);
					if(fi != null && fi.getFileId() != null) {
						update(fi, modificationDate, fileName, domain, tag1, tag2);
						actualizados++;
					}
					else{
						newFile(modificationDate, fileName, description, domain, tag1, tag2, id);
						nuevos++;
					}
				}
			}
			System.out.println("nuevos: "+nuevos+" - actualizados: "+actualizados);
			reader.close();
			client.logout();
			client.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static File download(FTPClient client, String path) throws IOException{
		client.enterLocalPassiveMode();
        client.setFileType(FTP.BINARY_FILE_TYPE); 	       
        File downloadFile = new File("/tmp"+path);
        OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile));
        client.retrieveFile(path, outputStream1);
        outputStream1.close();

        return downloadFile;
	}
	
	
	public static void update(FileInfo fileInfo, String modificationDate, String fileName, Domain domain, String tag1, String tag2){
		Domain domainAux = new Domain().setName(domain.getName()).setId(0);
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date date = null;
		try {
			date = formatter.parse(modificationDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(date != null && (fileInfo.getModificationDate() == null || date.after(fileInfo.getModificationDate()))){
			Drive drive = null;  
			try {
				DomainGserviceaccount g = DBConsults.getServiceAccount(domainAux, getUser());
				if(g.getGoogleAccount() == null){
					String googleAccount = "aio@aonsolutions.net";
					DBConsults.updateGoogleAccount(domainAux, getUser(), googleAccount);
					g.setGoogleAccount(googleAccount);
				}
				drive = DriveUtils.serviceInitialize(g);
			
				File file = download(client, "/"+fileName.substring(0,12).toLowerCase());
				FileInputStream fis2 = new FileInputStream(file);

				String md5 = AonFileUtils.getMD5Checksum(fis2);
				
				com.google.api.services.drive.model.File fdrive = null;
				
				try {
					fdrive = DriveUtils.getFile(drive, domainAux, getUser(), fileInfo.getDriveId(),fileInfo.getFileId());//TODO error dominio 
				} catch (IOException | GeneralSecurityException e) {
					e.printStackTrace();
				}
				System.out.println(md5+" - "+fdrive.getMd5Checksum() + fdrive.getId());
				if(!md5.equals(fdrive.getMd5Checksum())){
					
					Boolean hasKey = false;
					for (Property p : fdrive.getProperties()) {
						if(p.getKey().equals("fileId"))
							hasKey = true;
					}		
					if(!hasKey){
						Property p = new Property();
						p.put("fileId", fileInfo.getFileId());
						if(fdrive.getProperties()!= null)fdrive.getProperties().add(p);
						else{
							List<Property> ps = new ArrayList<Property>();
							ps.add(p);
						}
					}
					
					fdrive.setModifiedDate(new DateTime(date.getTime()));
					//TODO ACTUALIZAR FICHERO EN GOOGLE DRIVE
					FileContent mediaContent = new FileContent(fdrive.getMimeType(),
							file);
					
					drive.files()
						.update(fileInfo.getDriveId(), fdrive, mediaContent)
						.execute();
				}
				DBDrive.updateSCModificationDate(domainAux, getUser(), fileInfo, date);
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}	
		
		Long l1 = fileInfo.getTags().stream().filter(tag -> tag.equals(tag1)).count();		
		if(l1 == 0)
			DBDrive.setSCTag(domainAux, getUser(), fileInfo, tag1);
		Long l2 = fileInfo.getTags().stream().filter(tag -> tag.equals(tag2)).count();		
		if(l2 == 0)
			DBDrive.setSCTag(domainAux, getUser(), fileInfo, tag2);
		
	}
	
	public static void newFile(String modificationDate,String fileName,String description,Domain domain, String tag1, String tag2, Integer id){
		try {
			Domain domainAux = new Domain().setName(domain.getName()).setId(0);
			File file = download(client, "/"+fileName);
			FileInputStream fis2 = new FileInputStream(file);

			
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			
			Date date = formatter.parse(modificationDate);
		
			DomainGserviceaccount g = DBConsults.getServiceAccount(domain,getUser());
			System.out.println(g.getGoogleAccount());
			Drive drive = DriveUtils.serviceInitialize(g);
		
			FileInfo fi = new FileInfo();
			fi.setAonType("registry");
			fi.setDomainId(0);
			fi.setType((short)3);
			fi.setSecurityLevel((byte) 0);
			fi.setFileId(id);
			fi.setCategory(-1001);
			fi.setMimetype((byte)MimeType.MIME_PDF.ordinal());
			fi.setTitle(description);
			Long size = file.length();
			fi.setSize(size.intValue());
			fi.setData(AonIOUtils.toByteArray(fis2));
			
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domainAux.getName(), domainAux.getId(), tag2);
			
				Result<Record1<Integer>> reg = ctx.getDslContext().select(REGISTRY.ID)
					.from(REGISTRY)
					.where(REGISTRY.DOMAIN.eq(0)).fetch();

				ctx.getDslContext().insertInto(RATTACH,RATTACH.ID,RATTACH.REGISTRY,RATTACH.DOMAIN,RATTACH.CATEGORY,RATTACH.MIMETYPE,RATTACH.DESCRIPTION,RATTACH.TYPE,RATTACH.SCOPE,RATTACH.SECURITY_LEVEL,RATTACH.ATTACH_DATE,RATTACH.DATA,RATTACH.DRIVE_ID,RATTACH.DPARENT_ID, RATTACH.MODIFICATION_DATE)
							.values(fi.getFileId(),reg.get(0).value1(),fi.getDomainId(),fi.getCategory(),fi.getMimetype(),fi.getTitle().substring(0, 64),(byte)fi.getType(),fi.getScopeId(),fi.getSecurityLevel(),fi.getDateSql(),null,null,fi.getSize().toString(),new Timestamp(date.getTime())).execute();
		
				DBDrive.setSCTag(domain, getUser(), fi, tag1);
				DBDrive.setSCTag(domain, getUser(), fi, tag2);
			} finally {
				if (ctx != null)
					ctx.close();
			}
			FileList fl = SearchFiles.searchFilesProperties(drive, "fileId", Integer.toString(fi.getFileId()));
			System.out.println( Integer.toString(fi.getFileId()));
			System.out.println(fl.getItems().size());
			if(fl.getItems().size()>0){
				DBDrive.updateDriveId(fl.getItems().get(0), fi.getFileId());
			}
			else{
				String[] types = { RegistryAttachmentType.CORPORATE_IDENTITY
						.toString() };
				DriveUtils.types = types;
				DriveUtils.sync2(drive, domainAux, getUser(), fi);
			}
		} catch (Exception e) {
		}
		
	}

	public static void main(String[] args) {
		parse(args);
		Map<String, Integer> domainMap = initializeDomainMap();
		try {
			ConnectionInfo connectionInfo = ConnectionInfo.getDefaultConnectionInfo();
			List<String> schemas;
			schemas = connectionInfo.getSchemas();
			System.out.println("NÚMERO DE SCHEMAS: "+ schemas.size());
			schemas.stream().forEach(s -> {
				System.out.println("> SCHEMA: "+ s);
				try {
					List<String> domains = connectionInfo.getSchemaDomains(s);
					System.out.println(">> NÚMERO DE DOMINIOS: "+ domains.size());
					if(domains.size()!=0){
						
						System.out.println(">> DOMINIO: "+domains.get(0));
						Domain domain = AON.getDomain(domains.get(0), domainMap.get(domains.get(0)), getUser().getLogin());
						sync(domain);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		} catch (AonConnectionException e1) {
			e1.printStackTrace();
		}
		
	}
	
	public static Map<String, Integer> initializeDomainMap(){
		Map<String, Integer> map  = new HashMap<String, Integer>();
		try {
			map =  DBSync.getDomainMap();
		} catch (AonConnectionException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	private static String server;
	private static String user;
	private static String password;
	private static String login;
	
	public static String getLogin(){
		return login;
	}
	
	public static User getUser(){
		return new User().setLogin(getLogin());
	}
	
	private static boolean parse(String args[])  {

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

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("Server name of FTP.");
		OptionBuilder.withLongOpt("server");
		Option serverOption = OptionBuilder.create("server");

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("Username of FTP.");
		OptionBuilder.withLongOpt("user");
		Option userOption = OptionBuilder.create("user");
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("Password of FTP.");
		OptionBuilder.withLongOpt("password");
		Option passwordOption = OptionBuilder.create("password");

		options.addOption(helpOption);
		options.addOption(serverOption);
		options.addOption(userOption);
		options.addOption(passwordOption);
		options.addOption(loginOption);

		try {
			CommandLine line = parser.parse(options, args);

			if (line.hasOption(helpOption.getOpt())) {
				helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX, options, true);
				return false;
			}

			server = line.getOptionValue(serverOption.getOpt());
			if(server == null)
				server = sFTP;
			
			
			user = line.getOptionValue(userOption.getOpt());
			if(user == null)
				user = sUser;
			
			password = line.getOptionValue(serverOption.getOpt());
			if(password == null)
				password = sPassword;
			
			login = line.getOptionValue(loginOption.getOpt());
			
		} catch (org.apache.commons.cli.ParseException e) {
			System.out.print(e.getMessage());
			helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX, options, true);
			return false;
		}
		return true;
	}
	
	

}
