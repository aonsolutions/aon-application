package com.code.aon.google.apis.rattach;

import java.sql.SQLException;
import java.util.Hashtable;
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
import com.code.aon.google.apis.FileInfo;
import com.code.aon.google.apis.jooq.DBConsults;
import com.code.aon.pool.AonConnectionException;

public class UpSize {
	public static void main(String[] args) throws AonConnectionException, SQLException  {
		
		parse(args);
		
		if( domains[0].equals("all")){
			Map<String, String> domains1=DatabaseSync.getDomains();
			Hashtable<String,String> schemas = new Hashtable<String, String>();
			
			for (String key : domains1.keySet()) { // recorre todos los dominios de la BD	
				if (!esta(schemas,domains1.get(key))){
					schemas.put(domains1.get(key), key);
				}
			}
			Vector<String> domains2 = new Vector<String>();
			for (String sch : schemas.keySet()){
				domains2.addAll(DBConsults.getParentName(schemas.get(sch)));
			}
			
			for (String key : domains2) { // recorre todos los dominios de la BD	

				Vector<FileInfo> v = DBConsults.getRattach(key);
				for (FileInfo fileInfo : v) {
					if(fileInfo.getDriveId()==null){
						DBConsults.upsize(key,fileInfo.getFileId(),fileInfo.getSize());
					}
				}
			}
		}
		else{
			for (String domain : domains) {
				Vector<FileInfo> v = DBConsults.getRattach(domain);
				for (FileInfo fileInfo : v) {
					System.out.println(fileInfo.getDriveId());
					if(fileInfo.getDriveId()==null){
						DBConsults.upsize(domain,fileInfo.getFileId(),fileInfo.getSize());
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
		OptionBuilder.withDescription("Dominio al que se le quiere aplicar la acción. Ej: xxx.net,... ");
		OptionBuilder.withValueSeparator(',');		
		Option domainOption = OptionBuilder.create("d");
		
		options.addOption(helpOption);
		options.addOption(domainOption);

		
		try {
			CommandLine line = parser.parse(options, args);
			String[] domainsaux = line.getOptionValues("d");
			if(domainsaux!=null){ domains = domainsaux;}
		} catch (ParseException e) {
			helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX,
					options, true);
		}
	}

}
