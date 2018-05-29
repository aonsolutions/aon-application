package com.code.aon.google.apis.drive;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.code.aon.google.apis.DriveUtils;
import com.code.aon.google.apis.Utils;
import com.code.aon.google.apis.jooq.DBConsults;
import com.code.aon.google.apis.jooq.DBSync;
import net.aonsolutions.core.pool.AonConnectionException;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;

public class ShareFiles {

	public static Boolean setPermission(Drive drive, String fileId, String email){
		Permission p=new Permission();
		p.setValue(email);
		p.setType(Utils.isGmail(email) ? "user" : "anyone");//user || group || domain || anyone
		p.setRole("reader");//owner || reader || writer || commenter		  		
		try {
			drive.permissions().insert(fileId, p).execute();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
    public static Boolean setPermissions(Drive drive, String fileId, Vector<String> emails){
   	 	for (String email : emails) {
   	 		if(!setPermission(drive, fileId,email));
   	 			return false;
   	 	}
   	 	return true;
   }
    
    public static void setPermissions(Drive drive, String fileId) throws IOException{
   	 	for (String email : emails) {
    		setPermission(drive, fileId,email);
		}
    }
    
    private static void act(Domain domain) throws IOException, SQLException, KeyStoreException, GeneralSecurityException {
    	
    	DomainGserviceaccount g = DBConsults.getServiceAccount(domain,getUser());
		Drive drive = DriveUtils.serviceInitialize(g);
		View.domain(domain.getName());
    	
    	if (action.equals("all")){
			shareAll(drive);
		}
		if (action.equals("id")){
			setPermissions(drive, value);
		}
	}

    public static void shareAll(Drive drive) throws IOException{
    	SearchFiles.types=types;
		FileList fl = SearchFiles.searchFilesAllAndTypes(drive);
		for (File f : fl.getItems()) {
			setPermissions(drive, f.getId());
		}
    }
    
    public static void main(String[] args) throws IOException, KeyStoreException, SQLException, GeneralSecurityException, AonConnectionException {
		parse(args);
		Map<String, Integer> domainMap = DBSync.getDomainMap();
		if(emails.length != 0){
			if( domains[0].equals("all")){
				// Obtiene todos los dominios de la BD.
				Map<String, String> domains1=DBSync.getDomains();
				
				// Ordena los dominios por orden alfabetico.
				List<String> list = new ArrayList<String>(domains1.keySet());
				Collections.sort(list, (String s1, String s2) -> s1.compareTo(s2));
				
				// Recorre todos los dominios de la BD.
				for (String domainName : list) {
					Domain domain = AON.getDomain(domainName, domainMap.get(domainName), getUser().getLogin());
					act(domain);
				}
			}
			else{
				for (String domainName : domains) {
					Domain domain = AON.getDomain(domainName, domainMap.get(domainName), getUser().getLogin());
					act(domain);
				}
			}
			
		}
		else View.error4();
    }
	
	public static String types [] = {"all"};
	private static String domains[] = {"all"};
	private static String action = "all";
	private static String value ;
	private static String emails [];
	private static String login;
	
	public static String getLogin(){
		return login;
	}
	
	public static User getUser(){
		return new User().setLogin(getLogin());
	}
	
	private static void parse(String  args []) {
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
		OptionBuilder.withDescription("imprime esta ayuda.");
		Option helpOption = OptionBuilder.create("help");
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("Tipo de archivo que se quiere tratar. Ej: LOGO,SIGNATURE,DOCUMENT,... ");
		OptionBuilder.withValueSeparator(',');
		Option typeOption = OptionBuilder.create("t");
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("Dominio al que se le quiere aplicar la acción. Ej: xxx.net,... ");
		OptionBuilder.withValueSeparator(',');		
		Option domainOption = OptionBuilder.create("d");
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("Tipo de acción que se va aplicar a la búsqueda o al borrado. Ej: -a title (buscar por título)");
		OptionBuilder.withValueSeparator(',');		
		Option actionOption = OptionBuilder.create("a");
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("Valor que acompaña al tipo de acción a aplicar. Ej: -a title -v hola (Buscar archivos que contenga 'hola' en el título. ");
		OptionBuilder.withValueSeparator(',');		
		Option valueOption = OptionBuilder.create("v");
		
		OptionBuilder.isRequired(true);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("Emails con los que se quiere compartir un archivo.");
		OptionBuilder.withValueSeparator(',');
		Option emailsOption = OptionBuilder.create("e");
		
		options.addOption(helpOption);
		options.addOption(domainOption);
		options.addOption(typeOption);
		options.addOption(valueOption);
		options.addOption(actionOption);
		options.addOption(emailsOption);
		options.addOption(loginOption);
		
		
		try {
			CommandLine line = parser.parse(options, args);
			
			String[] typesaux  = line.getOptionValues("t");
			if(typesaux!=null){ types = typesaux;}
			String[] domainsaux = line.getOptionValues("d");
			if(domainsaux!=null){ domains = domainsaux;}
			String actionaux = line.getOptionValue("a");
			if(actionaux!=null){ action = actionaux;}
			String valueaux= line.getOptionValue("v");
			if(valueaux!=null){ value = valueaux;}
			String[] emailsaux = line.getOptionValues("e");
			if(emailsaux!=null){ emails = emailsaux;}
			
			login = line.getOptionValue(loginOption.getOpt());
			
		} catch (ParseException e) {
			helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX,
					options, true);
		}
	}
}
