package com.code.aon.google.apis.calendar;

import static org.apache.commons.cli.HelpFormatter.DEFAULT_SYNTAX_PREFIX;

import java.io.IOException;
import java.security.GeneralSecurityException;
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
import com.code.aon.google.apis.jooq.DBConsults;
import com.code.aon.google.apis.jooq.DBSync;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.security.User;
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
	
	
	public static Events searchAll(Calendar calendar,Domain domain, User user) throws IOException{
		CalendarList cl = CalendarUtils.getCalendars(calendar); 
		Events events = new Events();
		events.setItems(new Vector<Event>());
		for (CalendarListEntry aux : cl.getItems()) {
			View.calendarListEntry(aux);
			Events es = CalendarUtils.getEvents(aux.getId(), calendar);
			for (Event e : es.getItems()) {
				System.out.println(e.getExtendedProperties().getPrivate().get("AonId"));
				if(commercial.equals("all")|| (e.getExtendedProperties().getPrivate().get("AonId")!= null && esta(DBConsults.getCommercial(domain, user, Integer.valueOf(e.getExtendedProperties().getPrivate().get("AonId")))))){	
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
	
	public static Events searchId(Calendar calendar, Domain domain, User user) throws IOException{
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
				if(commercial.equals("all")|| (e.getExtendedProperties().getPrivate().get("AonId")!= null && esta(DBConsults.getCommercial(domain, user, Integer.valueOf(e.getExtendedProperties().getPrivate().get("AonId")))))){	
					
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
	public static Events act(Domain domain, User user) throws IOException, GeneralSecurityException {
		DomainGserviceaccount g = DBConsults.getServiceAccount(domain, user);
		Calendar calendar = CalendarUtils.serviceInitialize(g);
		View.domain(domain.getName());
		Events events = null;
		if (action.equals("all")){
			events = searchAll(calendar,domain, user);
		}
		else if( action.equals("id")){
			events = searchId(calendar, domain, user);
		}
		
		return events;
	}
	
	public static void main(String[] args) throws IOException, GeneralSecurityException {
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
				Domain domain = AON.getDomain(domainName, domainMap.get(domainName), getUser().getLogin());
				act(domain, getUser());
			}
			//for (String key : domains1.keySet()) { // recorre todos los dominios de la BD	
				//act(key);
			//}
		}
		else{
			for (String domainName : domains) {
				Domain domain = AON.getDomain(domainName, domainMap.get(domainName), getUser().getLogin());
				act(domain, getUser());
			}
		}

	}
	
	
	public static String domains[]={"all"};
	public static String commercial="all";
	public static String emails[];
	public static String action = "all";
	public static String values[] ;
	public static String out = "normally";
	
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
		options.addOption(loginOption);

		try {
			CommandLine line = parser.parse(options, args);

			if (line.hasOption(helpOption.getOpt())) {
				helpFormatter.printHelp(DEFAULT_SYNTAX_PREFIX, options, true);
				return false;
			}
			
			login = line.getOptionValue(loginOption.getOpt());
			
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
			return false;
		}
		return true;
	}
}
