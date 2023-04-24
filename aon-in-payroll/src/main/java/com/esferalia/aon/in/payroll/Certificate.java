package com.esferalia.aon.in.payroll;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;

public class Certificate {

	 public static void main(String[] args) {
		
		@SuppressWarnings("static-access")
		Option hostOption = Option.builder("h")
		     .hasArg()
			 .required()
		     .longOpt("host")
		     .argName("name")
		     .desc("Connect to host.")
		     .build();
	 
		@SuppressWarnings("static-access")
		 Option portOption = Option.builder("P")
		     .hasArg()
		     .longOpt("port")
		     .argName("name")
		     .desc("Port number to use for connection, default (3306).")
		     .build();

		@SuppressWarnings("static-access")
		Option userOption = Option.builder("u")
		     .hasArg()
			 .required()
		     .longOpt("user")
		     .argName("name")
		     .desc("User for login if not current user.")
		     .build();

		@SuppressWarnings("static-access")
		Option passwordOption = Option.builder("p")
		     .hasArg()
			 .required()
		     .longOpt("password")
		     .argName("name")
		     .desc("Password to use when connecting to server.")
		     .build();

		@SuppressWarnings("static-access")
		Option databaseOption = Option.builder("D")
		     .hasArg()
			 .required()
		     .longOpt("database")
		     .argName("name")
		     .desc("Database to use")
		     .build();


		@SuppressWarnings("static-access")
		Option domainOption = Option.builder()
		     .hasArg()
		     .required()
		     .longOpt("domain")
		     .argName("domain")
		     .desc("Domain's URL.")
		     .build();

		@SuppressWarnings("static-access")
		Option loginOption = Option.builder()
		     .hasArg()
		     .required()
		     .longOpt("login")
		     .argName("login")
		     .desc("Domain's login.")
		     .build();

		@SuppressWarnings("static-access")
		Option outputOption = Option.builder("o")
		     .hasArg()
		     .required()
		     .longOpt("out")
		     .argName("filename")
		     .desc("Output file.")
		     .build();

		@SuppressWarnings("static-access")
		Option helpOption = Option.builder("?")
			 .longOpt("help")
	         .desc("Display this help and exit.")
	         .build();

		Options options = new Options();
		options.addOption(helpOption);
		options.addOption(hostOption);
		options.addOption(portOption);
		options.addOption(userOption);
		options.addOption(loginOption);
		options.addOption(domainOption);
		options.addOption(outputOption);
		options.addOption(databaseOption);
		options.addOption(passwordOption);
		 
		CommandLine commandLine = null;
		
		try {
			CommandLineParser parser = new GnuParser();
			commandLine = parser.parse(options, args);
		} catch (ParseException e) {
			// oops, somthing went wrong
			System.out.println("Error: " + e.getLocalizedMessage());
			new HelpFormatter().printHelp(PDF2Aon.class.getSimpleName(), options);
			return;
		} 
		
		String dbHost = commandLine.getOptionValue(hostOption.getLongOpt());
		String pdbPrt = commandLine.getOptionValue(portOption.getLongOpt(), "3306");
		String dbUser = commandLine.getOptionValue(userOption.getLongOpt());
		String dbPassword = commandLine.getOptionValue(passwordOption.getLongOpt());
		String dbName = commandLine.getOptionValue(databaseOption.getLongOpt());
		String aonDomain = commandLine.getOptionValue(domainOption.getLongOpt());
		String aonUser = commandLine.getOptionValue(loginOption.getLongOpt());
		String outFile = commandLine.getOptionValue(outputOption.getLongOpt());
	
		Properties properties = new Properties();
		properties.setProperty("user", dbUser);
		properties.setProperty("password", dbPassword);
		properties.setProperty("useSSL", "false");
		properties.setProperty("serverTimezone", TimeZone.getDefault().getID());
		String url = String.format("jdbc:mysql://%s:%s/%s", dbHost, pdbPrt, dbName);

		Settings settings;
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Error: " + e.getLocalizedMessage());
		} 
		
		try (CloseableAONContext aonContext = AONContext.getAONContext(aonDomain, aonUser); 
			OutputStream os = new FileOutputStream(outFile)){
			
			int aonDomainId = aonContext.getDomainId();
			
			int aonUserId = AON.getUser(aonDomain, aonDomainId , aonUser).getId();
			
			com.esferalia.aon.occam.api.model.Certificate certificate = 
			
			AON.getCertificate(aonDomain, aonDomainId, aonUser, aonUserId, "SEPE");
			
			os.write(certificate.getData());
			
			System.out.println(certificate.getPassword());

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error: " + e.getLocalizedMessage());
		} 
	 }

}
