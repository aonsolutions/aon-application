package com.code.aon.google.apis.drive;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.sql.SQLException;
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

import com.code.aon.google.apis.DatabaseSync;
import com.code.aon.google.apis.DriveUtils;
import com.code.aon.pool.AonConnectionException;
import com.esferalia.aon.google.sql.AbstractSQL.DomainGserviceaccount;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;

public class ShareFiles {

	public static void setPermission(Drive drive, String fileId, String email) throws IOException{
			Permission p=new Permission();
	 		p.setValue(email);
	 		p.setType("user");//user || group || domain || anyone
	 		p.setRole("reader");//owner || reader || writer || commenter		  		
	 		drive.permissions().insert(fileId, p).execute();
	}
	
    public static void setPermissions(Drive drive, String fileId) throws IOException{
   	 	for (String email : emails) {
    		setPermission(drive, fileId,email);

		}
    }
    
    private static void act(String domain) throws IOException, SQLException, KeyStoreException, GeneralSecurityException {
    	DomainGserviceaccount d = DatabaseSync.getServiceAccount(domain);
		Drive drive = DriveUtils.serviceInitialize(d);
		View.domain(domain);
    	
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
		if(emails.length != 0){
			if( domains[0].equals("all")){
				Map<String, String> domains1=DatabaseSync.getDomains();
				for (String key : domains1.keySet()) { // recorre todos los dominios de la BD	
					act(key);
				}
			}
			else{
				for (String domain : domains) {
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
	private static String out = "normally";
	private static String categories [] = {"all"};
	private static String emails [];
	
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
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("Tipo de salida al aplicar el comando. Ej: normally");
		OptionBuilder.withValueSeparator(',');		
		Option outOption = OptionBuilder.create("o");

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("Categoria a la que pertenece el archivo que se quiere tratar. Ej: otros, LABORAL, ...");
		OptionBuilder.withValueSeparator(',');
		Option categoriesOption = OptionBuilder.create("c");

		OptionBuilder.isRequired(true);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("Emails con los que se quiere compartir un archivo.");
		OptionBuilder.withValueSeparator(',');
		Option emailsOption = OptionBuilder.create("e");
		
		options.addOption(helpOption);
		options.addOption(outOption);
		options.addOption(domainOption);
		options.addOption(typeOption);
		options.addOption(valueOption);
		options.addOption(actionOption);
		options.addOption(categoriesOption);
		options.addOption(emailsOption);
		
		
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
			String outaux = line.getOptionValue("o");
			if(outaux!=null) out = outaux; 
			String[] categoriesaux = line.getOptionValues("a");
			if(categoriesaux!=null){ categories = categoriesaux;}
			String[] emailsaux = line.getOptionValues("e");
			if(emailsaux!=null){ emails = emailsaux;}
		} catch (ParseException e) {
			helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX,
					options, true);
		}
	}
}
