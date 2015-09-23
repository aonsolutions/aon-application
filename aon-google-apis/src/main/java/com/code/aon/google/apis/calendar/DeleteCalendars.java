package com.code.aon.google.apis.calendar;

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

import com.code.aon.google.apis.CalendarUtils;
import com.code.aon.google.apis.DatabaseSync;
import com.code.aon.google.apis.jooq.DBConsults;
import com.code.aon.google.apis.jooq.DomainGserviceaccount;
import com.code.aon.pool.AonConnectionException;
import com.esferalia.aon.google.sql.AbstractSQL.Domain;
import com.google.api.services.calendar.Calendar;

public class DeleteCalendars {

	
	public static void deleteCalendarsAll(Calendar calendar , String domain) {

	}
	

	public static void deleteCalendar(Calendar calendar , String domain) {

		for (String calendarId : values) {
				try {
					CalendarUtils.removeCalendar(calendarId);
				} catch (IOException e) {
					e.printStackTrace();
				}
			
		}
	}

	
	public static boolean esta(Vector<String> aux){
		for (String string : aux) {
			if (string.equals(commercial)) return true;
		}
		
		return false;
	}
	

	public static void act(String domain) throws KeyStoreException, IOException, GeneralSecurityException, SQLException, AonConnectionException{
		Domain d = DBConsults.getDomain(domain);
		DomainGserviceaccount g = DBConsults.getServiceAccount(domain, d.getId());
				//DatabaseSync.getServiceAccount(domain);
		View.domain(domain);
		if (g.getClientId()!= null){
			Calendar calendar = CalendarUtils.serviceInitialize(g);
		
	
			if(action.equals("all")){
				deleteCalendarsAll(calendar, domain);
			}
			else if(action.equals("id")){
			
				deleteCalendar(calendar, domain);
			}
			else{
				View.error2();
			}
		}
	}
	
	public static void main(String[] args) throws AonConnectionException, KeyStoreException, IOException, GeneralSecurityException, SQLException, InterruptedException {
		parse(args);
		/*
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader bf = new BufferedReader (isr);
		String id = bf.readLine();	
		bf.wait(2);	
		if (id != null && values.length<=0){
			action="id";
			String[] values1 = {id};
			values= values1;
		}*/
		
		if( domains[0].equals("all")){
			// Obtiene todos los dominios de la BD.
			Map<String, String> domains1=DatabaseSync.getDomains();
			
			// Ordena los dominios por orden alfabetico.
			List<String> list = new ArrayList<String>(domains1.keySet());
			Collections.sort(list, (String s1, String s2) -> s1.compareTo(s2));
			
			// Recorre todos los dominios de la BD.
			for (String key : list) 
				act(key);
		}
		else{
			for (String domain : domains) 
				act(domain);
		}
	}
	
	
	private static String domains[]={"all"};
	private static String commercial = "all";
	private static String emails[];
	private static String action = "all";
	private static String values[] ;
	private static String out = "normally";
	private static String calId;
	
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
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("Tipo de acción que se va aplicar. ");
		OptionBuilder.withValueSeparator(',');		
		Option actionOption = OptionBuilder.create("a");
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("Valor que acompaña al tipo de acción a aplicar. ");
		OptionBuilder.withValueSeparator(',');		
		Option valueOption = OptionBuilder.create("v");
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("Tipo de salida al aplicar el comando. Ej: normally");
		OptionBuilder.withValueSeparator(',');		
		Option outOption = OptionBuilder.create("o");
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("Comercial al que pertenece el evento.");
		OptionBuilder.withValueSeparator(',');		
		Option commercialOption = OptionBuilder.create("c");

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("Emails con los que se quiere compartir un archivo o documento.");
		OptionBuilder.withValueSeparator(',');		
		Option emailOption = OptionBuilder.create("e");
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("Identificador del calendario al que pertenece el evento.");
		OptionBuilder.withValueSeparator(',');		
		Option calIdOption = OptionBuilder.create("C");
		
		options.addOption(helpOption);
		options.addOption(outOption);
		options.addOption(domainOption);
		options.addOption(valueOption);
		options.addOption(actionOption);
		options.addOption(commercialOption);
		options.addOption(emailOption);
		options.addOption(calIdOption);

		try {
			CommandLine line = parser.parse(options, args);
			
			
			String[] domainsaux = line.getOptionValues("d");
			if(domainsaux!=null){ domains = domainsaux;}
			String actionaux = line.getOptionValue("a");
			if(actionaux!=null){ action = actionaux;}
			String[] valuesaux= line.getOptionValues("v");
			if(valuesaux!=null){ values = valuesaux;}
			String outaux = line.getOptionValue("o");
			if(outaux!=null) out = outaux; 
			String commercialsaux = line.getOptionValue("c");
			if(commercialsaux!=null) commercial = commercialsaux;
			String[] emailsaux = line.getOptionValues("e");
			if(emailsaux!=null) emails = emailsaux;
			String calIdsaux = line.getOptionValue("C");
			if(calIdsaux!=null) calId = calIdsaux;
		} catch (ParseException e) {
			helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX,
					options, true);
		}
	}
}