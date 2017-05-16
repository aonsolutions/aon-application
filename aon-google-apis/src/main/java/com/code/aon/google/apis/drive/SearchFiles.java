package com.code.aon.google.apis.drive;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.code.aon.google.apis.DriveUtils;
import com.code.aon.google.apis.jooq.DBConsults;
import com.code.aon.google.apis.jooq.DBSync;
import com.code.aon.pool.AonConnectionException;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class SearchFiles {

	
	public static FileList searchFilesProperties(final Drive drive, final String key, final String property) throws IOException {
        return (FileList) drive.files().list().setQ("properties has {key='" + key + "' and value='" + property + "' and visibility='PRIVATE'}").execute();
    }
	
	public static FileList searchFilesProperties(final Drive drive, final String[] key, final String[] property) {

		String q = "";
        for(Integer i = 0; i < key.length; i++){
        	if(i > 0) q = q + " and ";
        	q = q +  "properties has {key='" + key[i] + "' and value='" + property[i] + "' and visibility='PRIVATE'}";
        }
        FileList fileList = new FileList();
        try {
			fileList = (FileList) drive.files().list().setQ(q).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileList;
    }

	
	public static FileList searchFilesFulltext(Drive drive, String searcher) throws IOException{
		FileList fl = drive.files().list().setQ("fullText contains '"+searcher+"'").execute();
		return fl;
	}
	
	public static FileList searchFilesFulltextAndTypes(Drive drive, String searcher, String domain) throws IOException{
		String type1 = types[0];
		FileList fl= new FileList();
		if(type1.equals("all")){
			fl = drive.files().list().setQ("fullText contains '"+searcher+"'"
					+"and properties has {key='" + "domain" + "' and value='" + domain + "' and visibility='PRIVATE'}").execute();
		}
		else{
			for (String type2 : types) {
				FileList aux = drive.files().list().setQ("properties has {etag='type' and value ='"+type2+"'} and fullText contains '"+searcher+"'"
						+"and properties has {key='" + "domain" + "' and value='" + domain + "' and visibility='PRIVATE'}").execute();
				fl.getItems().addAll(aux.getItems());
				
			}
		}
		return fl;
	}
	
	public static FileList searchFilesTitle(Drive drive, String searcher) throws IOException{
		FileList fl = drive.files().list().setQ("title contains '"+searcher+"'").execute();
		return fl;
	}
	
	public static FileList searchFilesTitleEqual(Drive drive, String searcher) throws IOException{
		FileList fl = drive.files().list().setQ("title = '"+searcher+"'").execute();
		return fl;
	}
	
	public static FileList searchFilesTitleEqualAndMimetype(Drive drive, String searcher) {
		FileList fl = new FileList();
		try {
			fl = drive.files().list().setQ("title = '"+searcher+"' and mimeType = 'application/vnd.google-apps.folder'").execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fl;
	}
	
	public static FileList searchFilesTitleAndParent(Drive drive, String searcher, String parent){
		FileList fl = new FileList();
		try {
			fl = drive.files().list().setQ("'"+parent+"' in parents and title = '"+searcher+"'").execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fl;
	}
	
	public static FileList searchFilesTitleAndTypes(Drive drive, String searcher, String domain) throws IOException{
		String type1 = types[0];
		FileList fl= new FileList();
		if(type1.equals("all")){
			fl = drive.files().list().setQ("title contains '"+searcher+"'"
					+"and properties has {key='" + "domain" + "' and value='" + domain + "' and visibility='PRIVATE'}").execute();
		}
		else{
			for (String type2 : types) {
				FileList aux = drive.files().list().setQ("properties has {etag='type' and value ='"+type2+"'} and title contains '"+searcher+"'"
						+"and properties has {key='" + "domain" + "' and value='" + domain + "' and visibility='PRIVATE'}").execute();
				fl.getItems().addAll(aux.getItems());
			}
		}
		return fl;
	}
	
	public static FileList searchFilesMimetypeAndTitle(Drive drive, String searcher1, String searcher2) throws IOException{
		
		FileList fl = drive.files().list().setQ("mimetype = '"+searcher1+"' and title = '"+searcher2+"'").execute();
		System.out.println(fl);
		return fl;
		
	}

	public static FileList searchFilesMimetype(Drive drive, String searcher) throws IOException{
		
		FileList fl = drive.files().list().setQ("mimetype contains '"+searcher+"'").execute();
		return fl;
		
	}

	public static FileList searchFilesMimetypeAndTypes(Drive drive, String searcher, String domain) throws IOException{
		String type1 = types[0];
		FileList fl= new FileList();
		if(type1.equals("all")){
			fl = drive.files().list().setQ("mimetype contains '"+searcher+"'"
					+"and properties has {key='" + "domain" + "' and value='" + domain + "' and visibility='PRIVATE'}").execute();
		}
		else{
			for (String type2 : types) {
				FileList aux = drive.files().list().setQ("properties has {key='type' and value ='"+type2+"'} and mimetype contains '"+searcher+"'"
						+"and properties has {key='" + "domain" + "' and value='" + domain + "' and visibility='PRIVATE'}").execute();
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

		if(type1.equals("all")){
			fl = drive.files().list().execute();
		}
		else{
	 
			
			for (String type2 : types) {
				//FileList aux = drive.files().list().setQ("properties has {key='type' and value ='"+type2+"'}").execute();
				FileList aux = searchFilesProperties(drive, "type", type2);
				if(fl.getItems() != null)
					fl.getItems().addAll(aux.getItems());	
				else 
					fl = aux;
			}
			
		}
		return fl;
	}
	
	public static FileList searchFilesAllAndTypes(Drive drive,String domain) throws IOException{
		String type1 = types[0];

		FileList fl= new FileList();

		if(type1.equals("all")){
			fl = drive.files().list().setQ("properties has {key='" + "domain" + "' and value='" + domain + "' and visibility='PRIVATE'}").execute();
		}
		else{
	 
			
			for (String type2 : types) {
				String key = "type";
				FileList aux = drive.files().list().setQ("properties has {key='" + key + "' and value='" + type2 + "'and visibility='PRIVATE'}"
						+"and properties has {key='" + "domain" + "' and value='" + domain + "' and visibility='PRIVATE'}").execute();

				if(fl.getItems() != null)
					fl.getItems().addAll(aux.getItems());	
				else 
					fl = aux;
			}
			
		}
		return fl;
	}
	
	public static File searchFile(Drive drive, String id) throws IOException{
		return drive.files().get(id).execute();
	}
	
	private static void act(Domain domain) throws IOException, GeneralSecurityException{
		DomainGserviceaccount g = DBConsults.getServiceAccount(domain,getUser());
		if(g.getClientId()!=null){
			Drive drive = DriveUtils.serviceInitialize(g);

			FileList fl = new FileList();
			View.domain(domain.getName());

			if(action.equals("title")){
				fl = searchFilesTitleAndTypes(drive, value, domain.getName());
			}
			else if(action.equals("fulltext")){
				fl = searchFilesFulltextAndTypes(drive, value, domain.getName());
			}
			else if(action.equals("mimetype")){
				fl = searchFilesMimetypeAndTypes(drive, value, domain.getName());
			}
			else if(action.equals("all")){
				fl = searchFilesAllAndTypes(drive, domain.getName());
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
		Map<String, Integer> domainMap = DBSync.getDomainMap();
		if( domains[0].equals("all")){
			// Obtiene todos los dominios de la BD.
			Map<String, String> domains = DBSync.getDomains();
			
			// Ordena los dominios por orden alfabetico.
			List<String> list = new ArrayList<String>(domains.keySet());
			Collections.sort(list, (String s1, String s2) -> s1.compareTo(s2));
			
			// Recorre todos los dominios de la BD.
			for (String domainName : list){
				Domain domain = AON.getDomain(domainName, domainMap.get(domainName), getLogin());
				act(domain);
			}
		}
		else{
			for (String domainName : domains) {
				Domain domain = AON.getDomain(domainName, domainMap.get(domainName), getLogin());
				act(domain);
			}
		}
	}
	
	public static boolean esta(Map<String,String> schemas, String schema) {
		for (String sch : schemas.keySet()) {
			if (sch.equals(schema)){
				return true;
			}
		}
		return false;
	}
	
	public static String types [] = {"all"};
	private static String domains[] = {"all"};
	private static String action = "all";
	private static String value ;
	private static String out = "normally";
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
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("Tipo de salida al aplicar el comando. Ej: normally");
		OptionBuilder.withValueSeparator(',');		
		Option outOption = OptionBuilder.create("o");
		
		options.addOption(loginOption);
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
			login = line.getOptionValue(loginOption.getOpt());
		} catch (ParseException e) {
			helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX,
					options, true);
		}
	}
}
