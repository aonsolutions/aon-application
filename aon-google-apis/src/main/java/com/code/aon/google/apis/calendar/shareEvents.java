package com.code.aon.google.apis.calendar;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.sql.SQLException;
import java.util.Vector;

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
import com.code.aon.google.apis.jooq.DomainGserviceaccount;
import com.code.aon.pool.AonConnectionException;
import com.esferalia.aon.google.sql.AbstractSQL.Domain;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.AclRule;
import com.google.api.services.calendar.model.AclRule.Scope;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

public class shareEvents {

	public static void act(String domain) throws IOException, NamingException, NumberFormatException, KeyStoreException, GeneralSecurityException, SQLException, AonConnectionException {
		// TODO Apéndice de método generado automáticamente
		Domain d = DBConsults.getDomain(domain);
		DomainGserviceaccount g = DBConsults.getServiceAccount(domain, d.getId());		Calendar calendar = CalendarUtils.serviceInitialize(g);
		View.domain(domain);
		for (String email : emails) {
			SearchEvents.commercial = commercial;
			SearchEvents.values = values;
			SearchEvents.action = action;
			Events events = SearchEvents.act(domain);
			for (Event e : events.getItems()) {
				if (email!= null && Utils.isGmail(email)){
					AclRule rule = new AclRule();
					Scope scope = new Scope();
					scope.setType("group");
					scope.setValue(email);
					rule.setRole("reader");
					rule.setScope(scope);
					AclRule createdRule = calendar.acl().insert(e.getId(), rule).execute();
				}
			}
			
		}
	}
	
	public static void main(String[] args) throws NumberFormatException, KeyStoreException, IOException, NamingException, GeneralSecurityException, SQLException, AonConnectionException {
		parse(args);

		if( domains[0].equals("all")){
			
			//Map<String, String> domains1=DatabaseSync.getDomains();
			Vector<String> domains2 = DBConsults.getParentName("novus.aibanez.net");//**CAMBIAR!!!
			
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
	
	private static String domains[];
	private static String commercial;
	private static String emails[];
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
