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
import com.code.aon.pool.AonConnectionException;
import com.esferalia.aon.google.sql.AbstractSQL.DomainGserviceaccount;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class SearchFiles {

	public static FileList searchFilesFulltext(Drive drive, String searcher) throws IOException{
		FileList fl = drive.files().list().setQ("fullText contains '"+searcher+"'").execute();
		return fl;
	}
	
	public static FileList searchFilesFulltextAndTypes(Drive drive, String searcher) throws IOException{
		String type1 = types[0];
		FileList fl= new FileList();
		if(type1.equals("all")){
			fl = drive.files().list().setQ("fullText contains '"+searcher+"'").execute();
		}
		else{
			for (String type2 : types) {
				FileList aux = drive.files().list().setQ("properties has {etag='type' and value ='"+type2+"'}").setQ("fullText contains '"+searcher+"'").execute();
				fl.getItems().addAll(aux.getItems());
				
			}
		}
		return fl;
	}
	
	public static FileList searchFilesTitle(Drive drive, String searcher) throws IOException{
		FileList fl = drive.files().list().setQ("title contains '"+searcher+"'").execute();
		return fl;
	}
	
	public static FileList searchFilesTitleAndTypes(Drive drive, String searcher) throws IOException{
		String type1 = types[0];
		FileList fl= new FileList();
		if(type1.equals("all")){
			fl = drive.files().list().setQ("title contains '"+searcher+"'").execute();
		}
		else{
			for (String type2 : types) {
				FileList aux = drive.files().list().setQ("properties has {etag='type' and value ='"+type2+"'}").setQ("title contains '"+searcher+"'").execute();
				fl.getItems().addAll(aux.getItems());
			}
		}
		return fl;
	}
	
	public static FileList searchFilesMimetype(Drive drive, String searcher) throws IOException{
		
		FileList fl = drive.files().list().setQ("mimetype contains '"+searcher+"'").execute();
		return fl;
		
	}

	public static FileList searchFilesMimetypeAndTypes(Drive drive, String searcher) throws IOException{
		String type1 = types[0];
		FileList fl= new FileList();
		if(type1.equals("all")){
			fl = drive.files().list().setQ("mimetype contains '"+searcher+"'").execute();
		}
		else{
			for (String type2 : types) {
				FileList aux = drive.files().list().setQ("properties has {key='type' and value ='"+type2+"'}").setQ("mimetype contains '"+searcher+"'").execute();
				fl.getItems().addAll(aux.getItems());
			}
		}
		return fl;
	}
	
	public static FileList searchFilesAll(Drive drive) throws IOException{
		FileList fl = drive.files().list().execute();
		return fl;
	}
	
	public static FileList searchFilesAllAndTypes(Drive drive) throws IOException{
		String type1 = types[0];

		FileList fl= new FileList();
		FileList fl2= new FileList();

		if(type1.equals("all")){
			fl = drive.files().list().execute();
		}
		else{
	 
			
			for (String type2 : types) {
				FileList aux = drive.files().list().setQ("properties has {key='type' and value ='"+type2+"'}").execute();
				fl.getItems().addAll(aux.getItems());
				
				
			}
			
		}
		return fl2;
	}
	
	public static File searchFile(Drive drive, String id) throws IOException{
		return drive.files().get(id).execute();
	}
	
	private static void act(String domain) throws SQLException, KeyStoreException, IOException, GeneralSecurityException{


		DomainGserviceaccount d = DatabaseSync.getServiceAccount(domain);
		System.out.println(d.getEmailAddress());
		if(d.getClientId()!=null){
			Drive drive = DriveUtils.serviceInitialize(d);

			FileList fl = new FileList();
			View.domain(domain);

			if(action.equals("title")){
				fl = searchFilesTitleAndTypes(drive, value);
			}
			else if(action.equals("fulltext")){
				fl = searchFilesFulltextAndTypes(drive, value);
			}
			else if(action.equals("mimetype")){
				fl = searchFilesMimetypeAndTypes(drive, value);
			}
			else if(action.equals("all")){
				fl = searchFilesAllAndTypes(drive);
			}
			else if(action.equals("id")){
				File f = searchFile(drive, value);
				View.file(f);
			}
			else{
				View.error1();
				System.exit(0);
			}
			if(fl.getItems()!=null){
				for (File f : fl.getItems()) {
					if(out.equals("normally"))
						View.file(f);
					else if(out.equals("id"))
						View.fileOut(f);
				}
			}
		}
	}
	
	public static void main(String[] args) throws IOException, SQLException, KeyStoreException, GeneralSecurityException, AonConnectionException {
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
	
	public static String types [] = {"all"};
	private static String domains[] = {"all"};
	private static String action = "all";
	private static String value ;
	private static String out = "normally";
	
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
			String valueaux= line.getOptionValue("v");
			if(valueaux!=null){ value = valueaux;}
			String outaux = line.getOptionValue("o");
			if(outaux!=null) out = outaux; 
		} catch (ParseException e) {
			helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX,
					options, true);
		}
	}
}
