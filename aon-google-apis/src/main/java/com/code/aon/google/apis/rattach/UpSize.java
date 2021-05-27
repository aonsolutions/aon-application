package com.code.aon.google.apis.rattach;

import static org.apache.commons.cli.HelpFormatter.DEFAULT_SYNTAX_PREFIX;

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

import com.code.aon.google.apis.FileInfo;
import com.code.aon.google.apis.jooq.DBConsults;
import com.code.aon.google.apis.jooq.DBSync;
import net.aonsolutions.core.pool.AonConnectionException;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.User;

public class UpSize {
	
	public static void main(String[] args) throws AonConnectionException, SQLException  {
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
				Vector<FileInfo> v = DBConsults.getRattach(domain, getUser());
				for (FileInfo fileInfo : v) {
					if(fileInfo.getDriveId()==null){
						DBConsults.upsize(domain, getUser(), fileInfo.getFileId(),fileInfo.getSize());
					}
				}
			}
		}
		else{
			for (String domainName : domains) {
				Domain domain = AON.getDomain(domainName, domainMap.get(domainName), getLogin());
				Vector<FileInfo> v = DBConsults.getRattach(domain, getUser());
				for (FileInfo fileInfo : v) {
					System.out.println(fileInfo.getDriveId());
					if(fileInfo.getDriveId()==null){
						DBConsults.upsize(domain, getUser(), fileInfo.getFileId(),fileInfo.getSize());
					}
				}
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
	
	private static String domains[] = {"all"};
	private static String login;
	
	private static String getLogin(){
		return login;
	}
	
	private static User getUser(){
		return new User().setLogin(getLogin());
	}
	
	private static boolean parse(String  args []) {
		CommandLineParser parser = new PosixParser();
		HelpFormatter helpFormatter = new HelpFormatter();
		
		Options options = new Options();
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(false);
		OptionBuilder.withLongOpt("help");
		OptionBuilder.withDescription("print this help.");
		Option helpOption = OptionBuilder.create('h');
		
		OptionBuilder.isRequired(true);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("Username of application");
		OptionBuilder.withLongOpt("username");
		Option loginOption = OptionBuilder.create("u");
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("Dominio al que se le quiere aplicar la acción. Ej: xxx.net,... ");
		OptionBuilder.withValueSeparator(',');		
		Option domainOption = OptionBuilder.create("d");
		
		options.addOption(helpOption);
		options.addOption(domainOption);
		options.addOption(loginOption);
		
		try {
			CommandLine line = parser.parse(options, args);
			
			if (line.hasOption(helpOption.getOpt())) {
				helpFormatter.printHelp(DEFAULT_SYNTAX_PREFIX, options, true);
				return false;
			}
			
			login = line.getOptionValue(loginOption.getOpt());
			
			String[] domainsaux = line.getOptionValues("d");
			if(domainsaux!=null){domains = domainsaux;}
		} catch (ParseException e) {
			helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX,
					options, true);
			return false;
		}
		return true;
	}

}
