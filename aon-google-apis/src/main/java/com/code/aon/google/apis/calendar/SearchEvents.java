package com.code.aon.google.apis.calendar;

import static com.code.aon.google.apis.DatabaseSync.getDomains;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
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

import com.code.aon.google.apis.CalendarUtils;
import com.code.aon.google.apis.jooq.DBConsults;
import com.code.aon.google.apis.jooq.DomainGserviceaccount;
import com.code.aon.pool.AonConnectionException;
import com.esferalia.aon.google.sql.AbstractSQL.Domain;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

public class SearchEvents {

	public static boolean esta(Vector<String> aux){
		for (String string : aux) {
			if (string.equals(commercial)) return true;
		}
		
		return false;
	}
	
	
	public static Events searchAll(Calendar calendar,String domain) throws IOException, NumberFormatException, AonConnectionException, SQLException{
		CalendarList cl = CalendarUtils.getCalendars(calendar); 
		Events events = new Events();
		events.setItems(new Vector<Event>());
		for (CalendarListEntry aux : cl.getItems()) {
			View.calendarListEntry(aux);
			Events es = CalendarUtils.getEvents(aux.getId(), calendar);
			for (Event e : es.getItems()) {
				System.out.println(e.getExtendedProperties().getPrivate().get("AonId"));
				if(commercial.equals("all")|| (e.getExtendedProperties().getPrivate().get("AonId")!= null && esta(DBConsults.getCommercial(domain,Integer.valueOf(e.getExtendedProperties().getPrivate().get("AonId")))))){	
					events.getItems().add(e);
					if(out.equals("normally")){
						View.event(e);
					}
					else if (out.equals("id")){
						View.eventOut(e);
					}
				}
			}
		}
		return events;
	}
	
	public static Events searchId(Calendar calendar, String domain) throws IOException, NumberFormatException, AonConnectionException, SQLException{
		CalendarList cl = CalendarUtils.getCalendars();
		String calendarId = null;
		Events events = new Events();
		events.setItems(new Vector<Event>());
		for (CalendarListEntry cle : cl.getItems()) {
			if (cle.getSummary().equals(domain)){
				calendarId = cle.getId();
				View.calendarListEntry(cle);
			}
		}
		if ( calendarId != null){
			for (String s : values) {
				Event e = calendar.events().get(calendarId, s).execute();
				if(commercial.equals("all")|| (e.getExtendedProperties().getPrivate().get("AonId")!= null && esta(DBConsults.getCommercial(domain,Integer.valueOf(e.getExtendedProperties().getPrivate().get("AonId")))))){	
					
					events.getItems().add(e);
					if(out.equals("normally")){
						View.event(e);
					}
					else if (out.equals("id")){
						View.eventOut(e);
					}
				}
			}
		}
		return events;
	}
	public static Events act(String domain) throws IOException, KeyStoreException, GeneralSecurityException, SQLException, NumberFormatException, AonConnectionException {
		Domain d = DBConsults.getDomain(domain);
		DomainGserviceaccount g = DBConsults.getServiceAccount(domain, d.getId());
		Calendar calendar = CalendarUtils.serviceInitialize(g);
		View.domain(domain);
		Events events = null;
		if (action.equals("all")){
			events = searchAll(calendar,domain);
		}
		else if( action.equals("id")){
			events = searchId(calendar, domain);
		}
		
		return events;
	}
	
	private static boolean esta(Map<String,String> schemas, String schema) {
		for (String sch : schemas.keySet()) {
			if (sch.equals(schema)){
				return true;
			}
		}
		return false;
	}
	
	public static void main(String[] args) throws IOException, AonConnectionException, SQLException, KeyStoreException, GeneralSecurityException {
		parse(args);
		if( domains[0].equals("all")){
			Map<String, String> domains=getDomains();//obtiene todos los dominios de la BD
			Hashtable<String,String> schemas = new Hashtable<String, String>();
			
			for (String key : domains.keySet()) { // recorre todos los dominios de la BD	
				if (!esta(schemas,domains.get(key))){
					schemas.put(domains.get(key), key);
				}
			}
			Vector<String> domains2 = new Vector<String>();
			for (String sch : schemas.keySet()){
				domains2.addAll(DBConsults.getParentName(schemas.get(sch)));
			}
			
			
			for (String key : domains2) {
				act(key);
			}
			//for (String key : domains1.keySet()) { // recorre todos los dominios de la BD	
				//act(key);
			//}
		}
		else{
			for (String domain : domains) {
				act(domain);
			}
		}

	}
	
	
	public static String domains[]={"all"};
	public static String commercial="all";
	public static String emails[];
	public static String action = "all";
	public static String values[] ;
	public static String out = "normally";
	
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
		
		options.addOption(helpOption);
		options.addOption(outOption);
		options.addOption(domainOption);
		options.addOption(valueOption);
		options.addOption(actionOption);
		options.addOption(commercialOption);
		options.addOption(emailOption);

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
			String commercialaux = line.getOptionValue("c");
			if(commercialaux!=null) commercial = commercialaux;
			String[] emailsaux = line.getOptionValues("e");
			if(emailsaux!=null) emails = emailsaux;
		} catch (ParseException e) {
			helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX,
					options, true);
		}
	}
}
