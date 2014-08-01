package com.code.aon.google.apis.drive;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.sql.SQLException;
import java.util.Map;

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
import com.code.aon.google.apis.jooq.DBConsults;
import com.code.aon.pool.AonConnectionException;
import com.esferalia.aon.google.sql.AbstractSQL.DomainGserviceaccount;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class DeleteFiles {
	
	private static void deleteDriveIds(String driveId, String domain) throws SQLException, AonConnectionException
	{
		DatabaseSync.delDriveId(driveId, domain);
		DBConsults.deleteDriveIdContractAttach(domain, driveId);
		DBConsults.deleteDriveIdIattach(domain, driveId);
		DBConsults.deleteDriveIdInvoiceAttach(domain, driveId);
		DBConsults.deleteDriveIdOfferAttach(domain, driveId);
		DBConsults.deleteDriveIdOfferAttach(domain, driveId);
		DBConsults.deleteDriveIdPayrollAttach(domain, driveId);
		DBConsults.deleteDriveIdProjectAttach(domain, driveId);
		DBConsults.deleteDriveIdSepeAttach(domain, driveId);
	}
	
	public static void deleteFile(Drive drive,File f, String domain) throws IOException, SQLException, AonConnectionException{
		drive.files().delete(f.getId()).execute();
		deleteDriveIds(f.getId(), domain);//si se consigue que funcione properties o labels, eliminar solo del Aontype k sea, no de todos.
		View.delete(f);
	}
	
	public static void deleteFilesId(Drive drive,String domain) throws IOException, SQLException, AonConnectionException{
		
		if(values.length == 0){
			View.error3();
		}
		else{
			for (String fileId : values) {
				File f = SearchFiles.searchFile(drive, fileId);
				deleteFile(drive, f, domain);
			}
		}	
		
	}
	
	public static void deleteFilesAll(Drive drive, String domain) throws IOException, SQLException, AonConnectionException{
		SearchFiles.types=types;
		FileList fl = SearchFiles.searchFilesAllAndTypes(drive);
		for (File f : fl.getItems()) {
			deleteFile(drive,f,domain);
		}
	}
	
	public static void act(String domain) throws KeyStoreException, IOException, GeneralSecurityException, SQLException, AonConnectionException{
		DomainGserviceaccount d = DatabaseSync.getServiceAccount(domain);
		Drive drive = DriveUtils.serviceInitialize(d);
		View.domain(domain);
	
		if(action.equals("all")){
			deleteFilesAll(drive, domain);
		}
		else if(action.equals("id")){
			deleteFilesId(drive,domain);
		}
		else{
			View.error2();
		}
	}
	
	public static void main(String[] args) throws SQLException, KeyStoreException, IOException, GeneralSecurityException, AonConnectionException {
		parse(args);
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
	
	private static String types[]={"all"};
	private static String domains[];
	private static String action = "all";
	private static String values[] ;
	private static String out = "normally";
	
	private static void parse(String  args []) {
		CommandLineParser parser = new PosixParser();
		HelpFormatter helpFormatter = new HelpFormatter();
		
		Options options = new Options();
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(false);
		OptionBuilder.withDescription("imprime esta ayuda.");
		Option helpOption = OptionBuilder.create("help");
		
		OptionBuilder.isRequired(true);
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

		options.addOption(helpOption);
		options.addOption(outOption);
		options.addOption(domainOption);
		options.addOption(typeOption);
		options.addOption(valueOption);
		options.addOption(actionOption);
		
		try {
			CommandLine line = parser.parse(options, args);
			
			String[] typesaux  = line.getOptionValues("t");
			if(typesaux!=null){ types = typesaux;}
			String[] domainsaux = line.getOptionValues("d");
			if(domainsaux!=null){ domains = domainsaux;}
			String actionaux = line.getOptionValue("a");
			if(actionaux!=null){ action = actionaux;}
			String[] valuesaux= line.getOptionValues("v");
			if(valuesaux!=null){ values = valuesaux;}
			String outaux = line.getOptionValue("o");
			if(outaux!=null) out = outaux; 
		} catch (ParseException e) {
			helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX,
					options, true);
		}
	}
}
