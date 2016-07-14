package com.code.aon.hhg.webservice.rpm;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;

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
import com.code.aon.hhg.webservice.dialog.GetNonce;
import com.code.aon.hhg.webservice.dialog.HHGPost;
import com.code.aon.hhg.webservice.dialog.Response;
import com.code.aon.hhg.webservice.jooq.DBConsults;
import com.code.aon.hhg.webservice.jooq.DBSync;

public class BookingHarvestRPM {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BookingHarvestRPM.class.getName());
	private static String domain;
 	private static String user;
	private static String password;
	private static String nonce;
	private static HashMap<String, Integer> domainMap;
	
	private static BookingHarvest buildBookingHarvest() throws java.text.ParseException {
		return DBConsults.getHHG(domain, domainMap.get(domain), user)
				.setUsername(user)
				.setNonce(nonce)
				.calculateHash(password);
	}
	
	public static void main(String[] args) throws java.text.ParseException {
		domain="test.grupoplayasol.com";
		nonce = "12345678912345678912";
		user = "aibanez";
		password = "aiekba";
		domainMap = DBSync.initializeDomainMap();
		if(domain == null)
			domain = domainMap.keySet().stream().findFirst().orElse("");
		
		System.out.println(buildBookingHarvest().toString());

		parse(args);
		if(nonce == null){
			//***** GET-NONCE *****/
			JSONObject json1 = HHGPost.post(GetNonce.URL);
			Response response1 = new Response(json1);
			LOGGER.info("POST " + GetNonce.URL);
			View.response(response1);
			if(!response1.getResult().getType().equals("error"))
				nonce = response1.getResult().getPayload().getNonce();
		}
		if(nonce != null){
			//***** BOOKING-HARVEST *****/
			BookingHarvest bh = buildBookingHarvest();
			JSONObject jsonResponse = HHGPost.post(GetNonce.URL, bh.toJSON());
			Response response2 = new Response(jsonResponse);
			if(!response2.getResult().getType().equals("error")){
				LinkedList<Integer> ps = bh.getPayload().stream().map(r -> r.getReservation().getProjectAttachId()).collect(Collectors.toCollection(LinkedList::new));
				DBConsults.updateHHGProjectAttachDate(domain, domainMap.get(domain), user,ps.toArray(new Integer[ps.size()]),  new Date());
			}
			LOGGER.info("POST " + BookingHarvest.URL);
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
		
		OptionBuilder.isRequired(true);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("nonce, value received from GET-NONCE call");
		OptionBuilder.withValueSeparator(',');
		OptionBuilder.withLongOpt("nonce");
		Option nonceOption = OptionBuilder.create("n");

		options.addOption(helpOption);
		options.addOption(domainOption);
		options.addOption(userOption);
		options.addOption(passwordOption);
		options.addOption(nonceOption);

		try {
			CommandLine line = parser.parse(options, args);

			if (line.hasOption(helpOption.getOpt())) {
				helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX, options, true);
				return false;
			}
			
			user = line.getOptionValue(userOption.getOpt());
			password = line.getOptionValue(passwordOption.getOpt());
			nonce = line.getOptionValue(nonceOption.getOpt());
			domain = line.getOptionValue(domainOption.getOpt());			
		} catch (ParseException e) {
			System.out.print(e.getMessage());
			helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX, options, true);
			return false;
		}
		return true;
	}
}
