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
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

public class DeleteEvents {

	public static void deleteEvent(Calendar calendar, Domain domain, User user) throws IOException{
		for (String eventId : values) {
			Event e = calendar.events().get(calId, eventId).execute();
			if(commercial.equals("all")|| (e.getExtendedProperties().getPrivate().get("AonId")!= null && esta(DBConsults.getCommercial(domain, user, Integer.valueOf(e.getExtendedProperties().getPrivate().get("AonId")))))){	
				CalendarUtils.removeEvent(calendar,calId, eventId);
				View.delete(e);
			}
		}
	}
	
	public static void deleteEventsAll(Calendar calendar, Domain domain, User user) throws IOException{
		com.google.api.services.calendar.model.CalendarList cl = CalendarUtils.getCalendars();
		
		for (CalendarListEntry cal : cl.getItems()) {
			Events e = CalendarUtils.getEvents(cal.getId());

			for (Event event : e.getItems()) {
				if(commercial.equals("all")|| (event.getExtendedProperties().getPrivate().get("AonId")!= null && esta(DBConsults.getCommercial(domain, user, Integer.valueOf(event.getExtendedProperties().getPrivate().get("AonId")))))){	
					CalendarUtils.removeEvent(calendar, cal.getId(), event.getId());
					View.delete(event);
				}
			}
		}
	}
	
	public static boolean esta(Vector<String> aux){
		for (String string : aux) {
			if (string.equals(commercial)) return true;
		}
		return false;
	}
	

	public static void act(Domain domain, User user) throws IOException, GeneralSecurityException{
		DomainGserviceaccount g = DBConsults.getServiceAccount(domain, user);
		View.domain(domain.getName());
		if (g.getClientId()!= null){
			Calendar calendar = CalendarUtils.serviceInitialize(g);
			
			if(action.equals("all")){
				deleteEventsAll(calendar, domain, user);
			}
			else if(action.equals("id")){
				
				deleteEvent(calendar, domain, user);
			}
			else{
				View.error2();
			}
		}
	}
	
	public static void main(String[] args) throws IOException, GeneralSecurityException {
		parse(args);
		Map<String, Integer> domainMap = DBSync.getDomainMap();
		if( domains[0].equals("all")){
			// Obtiene todos los dominios de la BD.
			Map<String, String> domains1=DBSync.getDomains();
			
			// Ordena los dominios por orden alfabetico.
			List<String> list = new ArrayList<String>(domains1.keySet());
			Collections.sort(list, (String s1, String s2) -> s1.compareTo(s2));
			
			// Recorre todos los dominios de la BD.
			for (String domainName : list) {
				Domain domain = AON.getDomain(domainName, domainMap.get(domainName), getUser().getLogin());
				act(domain, getUser());
			}
		}
		else{
			for (String domainName : domains) {
				Domain domain = AON.getDomain(domainName, domainMap.get(domainName), getUser().getLogin());
				act(domain, getUser());
			}
		}
	}
	
	
	private static String domains[]={"all"};
	private static String commercial = "all";
	private static String action = "all";
	private static String values[] ;
	private static String calId;
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
		OptionBuilder.withDescription("Comercial al que pertenece el evento.");
		OptionBuilder.withValueSeparator(',');		
		Option commercialOption = OptionBuilder.create("c");
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("Identificador del calendario al que pertenece el evento.");
		OptionBuilder.withValueSeparator(',');		
		Option calIdOption = OptionBuilder.create("C");
		
		options.addOption(helpOption);
		options.addOption(domainOption);
		options.addOption(valueOption);
		options.addOption(actionOption);
		options.addOption(commercialOption);
		options.addOption(calIdOption);
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
			
			String commercialsaux = line.getOptionValue("c");
			if(commercialsaux!=null) commercial = commercialsaux;
			
			String calIdsaux = line.getOptionValue("C");
			if(calIdsaux!=null) calId = calIdsaux;
		} catch (ParseException e) {
			helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX,
					options, true);
			return false;
		}
		return true;
	}
}
