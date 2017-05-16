package com.code.aon.google.apis.calendar;

import static org.apache.commons.cli.HelpFormatter.DEFAULT_SYNTAX_PREFIX;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.code.aon.google.apis.CalendarUtils;
import com.code.aon.google.apis.Utils;
import com.code.aon.google.apis.jooq.DBConsults;
import com.code.aon.google.apis.jooq.DBSync;
import com.code.aon.pool.AonConnectionException;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.AclRule;
import com.google.api.services.calendar.model.AclRule.Scope;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

public class shareEvents {

	public static void act(Domain domain, User user) throws IOException, NamingException, NumberFormatException, KeyStoreException, GeneralSecurityException, SQLException, AonConnectionException {
		// TODO Apéndice de método generado automáticamente
		DomainGserviceaccount g = DBConsults.getServiceAccount(domain, user);
		Calendar calendar = CalendarUtils.serviceInitialize(g);
		View.domain(domain.getName());
		for (String email : emails) {
			SearchEvents.commercial = commercial;
			SearchEvents.values = values;
			SearchEvents.action = action;
			Events events = SearchEvents.act(domain, user);
			for (Event e : events.getItems()) {
				if (email!= null && Utils.isGmail(email)){
					AclRule rule = new AclRule();
					Scope scope = new Scope();
					scope.setType("group");
					scope.setValue(email);
					rule.setRole("reader");
					rule.setScope(scope);
					calendar.acl().insert(e.getId(), rule).execute();
				}
			}
			
		}
	}
	
	public static void main(String[] args) throws NumberFormatException, KeyStoreException, IOException, NamingException, GeneralSecurityException, SQLException, AonConnectionException {
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
			
		}
		else{
			for (String domainName : domains) {
				Domain domain = AON.getDomain(domainName, domainMap.get(domainName), getUser().getLogin());
				act(domain, getUser());
			}
		}
		
	}
	
	private static String domains[];
	private static String commercial;
	private static String emails[];
	private static String action = "all";
	private static String values[] ;
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
		OptionBuilder.withDescription("Emails con los que se quiere compartir un archivo o documento.");
		OptionBuilder.withValueSeparator(',');		
		Option emailOption = OptionBuilder.create("e");
		
		options.addOption(loginOption);
		options.addOption(helpOption);
		options.addOption(domainOption);
		options.addOption(valueOption);
		options.addOption(actionOption);
		options.addOption(commercialOption);
		options.addOption(emailOption);

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
