package com.code.aon.hhg.webservice.rpm;

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

import com.code.aon.hhg.webservice.dialog.GetNonce;
import com.code.aon.hhg.webservice.dialog.HHGPost;
import com.code.aon.hhg.webservice.dialog.Hello;
import com.code.aon.hhg.webservice.dialog.Response;

public class AuthRPM {
	private static final Logger LOGGER = LoggerFactory.getLogger(HelloRPM.class.getName());
	private static String user;
	private static String password;

	public static void main(String[] args) {
		parse(args);
		
		//***** GET-NONCE *****/
		JSONObject json1 = HHGPost.post(GetNonce.URL, null);
		Response response1 = new Response(json1);
		LOGGER.info("POST " + GetNonce.URL);
		View.response(response1);
		
		//***** HELLO *****/
		if(!response1.getResult().getType().equals("error")){		
			JSONObject json2 = new Hello().setUsername(user)
				.setNonce(response1.getResult().getPayload().getNonce())
				.calculateHash(password)
				.toJSON();
		
			JSONObject jsonResponse = HHGPost.post(GetNonce.URL, json2);
			Response response2 = new Response(jsonResponse);
			LOGGER.info("POST " + GetNonce.URL);
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
			
		} catch (ParseException e) {
			System.out.print(e.getMessage());
			helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX, options, true);
			return false;
		}
		return true;
	}
}
