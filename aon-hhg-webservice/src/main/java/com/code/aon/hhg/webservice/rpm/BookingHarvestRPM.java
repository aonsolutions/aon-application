package com.code.aon.hhg.webservice.rpm;

import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.hhg.webservice.dialog.BookingHarvest;
import com.code.aon.hhg.webservice.dialog.HHGPost;
import com.code.aon.hhg.webservice.dialog.Response;
import com.code.aon.hhg.webservice.jooq.DBConsults;
import com.code.aon.hhg.webservice.jooq.DBSync;
import com.esferalia.aon.occam.api.model.attachment.Attach;

public class BookingHarvestRPM {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BookingHarvestRPM.class.getName());
	private static String domain;
 	private static String user;
	private static String password;
	private static Map<String, Integer> domainMap;
	
	private static BookingHarvest buildBookingHarvest() throws java.text.ParseException {
		return DBConsults.getHHG(domain, domainMap.get(domain), user)
				.setUsername(user)
				.calculateNonce(password)
				.calculateHash(password);
	}
	
	private static String getDescriptionName(String method) {
		if(method.equals("ADD")) return "CRS ALTA";
		if(method.equals("CANCEL")) return "CRS CANCELACION";
		if(method.equals("MODIFY")) return "CRS MODIFICACION";
		return "";
	}
	
	/*public static void main(String[] args) throws java.text.ParseException {
		domain="test.grupoplayasol.com";
		user = "pruebas.api@hhg-hotels.net";
		password = "c802f1e2aa51";
		domainMap = DBSync.initializeDomainMap();
		if(domain == null)
			domain = domainMap.keySet().stream().findFirst().orElse("");
		System.out.println(buildBookingHarvest().toJSON().toString());
	}*/
	
	public static void main(String[] args) throws java.text.ParseException {
		domainMap = DBSync.initializeDomainMap();
		if(domain == null)
			domain = domainMap.keySet().stream().findFirst().orElse("");
		
		parse(args);
		
		/*domain="test.grupoplayasol.com";
		user = "pruebas.api@hhg-hotels.net";
		password = "c802f1e2aa51";
		*/
		String url = DBConsults.getHHGUrl(domain, domainMap.get(domain), user);
		if(url == null) url = BookingHarvest.URL;

		//***** BOOKING-HARVEST *****/
		BookingHarvest bh = buildBookingHarvest();
		if(bh.getPayload().getReservation() == null || bh.getPayload().getReservation().getCrscode() == null)
			LOGGER.info("No hay ninguna reserva para procesar.");
		else {
			System.out.println("json -> ");
			System.out.println(bh.toJSON());
			System.out.println(" ");
			JSONObject jsonResponse = HHGPost.post2(url, bh.toJSON());
			Response response2 = new Response(jsonResponse);
			if(!response2.getResult().getType().equals("error")){
				Attach attach = new Attach().setId(bh.getPayload().getReservation().getProjectAttachId())
					.setDescription(getDescriptionName(bh.getPayload().getReservation().getMethod()));
				DBConsults.updateHHGProjectAttach(domain, domainMap.get(domain), user, attach);
			}
			LOGGER.info("POST " + url);
			View.response(response2);
		}
	}
	
	private static boolean parse(String args[]) {

		CommandLineParser parser = new PosixParser();
		HelpFormatter helpFormatter = new HelpFormatter();

		Options options = new Options();

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(false);
		OptionBuilder.withLongOpt("help");
		OptionBuilder.withDescription("print this help.");
		Option helpOption = OptionBuilder.create('h');

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("specify a domain, e.g., \"aon.esferalia.net,sig.aonsolutions.org\"");
		OptionBuilder.withValueSeparator(',');
		OptionBuilder.withLongOpt("domain");
		Option domainOption = OptionBuilder.create('d');
		
		OptionBuilder.isRequired(true);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("specify a Username of hhg-hotels");
		OptionBuilder.withValueSeparator(',');
		OptionBuilder.withLongOpt("user");
		Option userOption = OptionBuilder.create('u');

		OptionBuilder.isRequired(true);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("specify a password of hhg-hotels");
		OptionBuilder.withValueSeparator(',');
		OptionBuilder.withLongOpt("password");
		Option passwordOption = OptionBuilder.create("p");

		options.addOption(helpOption);
		options.addOption(domainOption);
		options.addOption(userOption);
		options.addOption(passwordOption);

		try {
			CommandLine line = parser.parse(options, args);

			if (line.hasOption(helpOption.getOpt())) {
				helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX, options, true);
				return false;
			}
			
			user = line.getOptionValue(userOption.getOpt());
			password = line.getOptionValue(passwordOption.getOpt());
			domain = line.getOptionValue(domainOption.getOpt());			
		} catch (ParseException e) {
			System.out.print(e.getMessage());
			helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX, options, true);
			return false;
		}
		return true;
	}
}
