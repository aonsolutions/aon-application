package com.esferalia.aon.in.payroll;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.in.payroll.altai.jooq.RollbackException;
import com.esferalia.aon.in.payroll.pdf.SalaryPDFBuilder;
import com.esferalia.aon.in.payroll.pdf.SalaryPDFException;
import com.esferalia.aon.in.payroll.pdf.SalaryPDFParser;
import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.in.payroll.pdf.jooq.JooqPDFSalaryBuilder;
import com.esferalia.aon.in.payroll.pdf.jooq.check.CheckSalaryPDFBuilder;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.salary.ISalaryBuilder;

public class PDF2Aon {
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/yyyy");

	 public static void main(String[] args) { 
		

		@SuppressWarnings("static-access")
		Option hostOption = OptionBuilder
		     .hasArg()
			 .isRequired()
		     .withLongOpt("host")
		     .withArgName("name")
		     .withDescription("Connect to host.")
		     .create("h");
	 
		@SuppressWarnings("static-access")
		 Option portOption = OptionBuilder
		     .hasArg()
		     .withLongOpt("port")
		     .withArgName("name")
		     .withDescription("Port number to use for connection, default (3306).")
		     .create("P");

		@SuppressWarnings("static-access")
		Option userOption = OptionBuilder
		     .hasArg()
			 .isRequired()
		     .withLongOpt("user")
		     .withArgName("name")
		     .withDescription("User for login if not current user.")
		     .create("u");

		@SuppressWarnings("static-access")
		Option passwordOption = OptionBuilder
		     .hasArg()
			 .isRequired()
		     .withLongOpt("password")
		     .withArgName("name")
		     .withDescription("Password to use when connecting to server.")
		     .create("p");

		@SuppressWarnings("static-access")
		Option databaseOption = OptionBuilder
		     .hasArg()
			 .isRequired()
		     .withLongOpt("database")
		     .withArgName("name")
		     .withDescription("Database to use")
		     .create("D");


		@SuppressWarnings("static-access")
		Option helpOption = OptionBuilder
			 .withLongOpt("help")
	         .withDescription("Display this help and exit.")
	         .create("?");

		@SuppressWarnings("static-access")
		Option skipOption = OptionBuilder
		     .hasArg()
		     .withLongOpt("skip")
		     .withArgName("cif")
		     .withDescription("Skips this enterprises (cifs)")
		     .create();

		@SuppressWarnings("static-access")
		Option includeOption = OptionBuilder
		     .hasArg()
		     .withLongOpt("include")
		     .withArgName("cif")
		     .withDescription("Only include this enterprises (cifs)")
		     .create();

		@SuppressWarnings("static-access")
		Option dateOption = OptionBuilder
		     .hasArg()
		     .withLongOpt("date")
		     .withArgName("date")
		     .withDescription("Date to compare.")
		     .create();

		@SuppressWarnings("static-access")
		Option whereOption = OptionBuilder
		     .hasArg()
		     .withLongOpt("where")
		     .withArgName("where")
		     .withDescription("Only selected contracts. Quotes are mandatory.")
		     .create();

		@SuppressWarnings("static-access")
		Option domainOption = OptionBuilder
		     .hasArg()
//			 .isRequired()
		     .withLongOpt("domain")
		     .withArgName("name")
		     .withDescription("Parent domain")
		     .create("d");

		@SuppressWarnings("static-access")
		Option preffixOption = OptionBuilder
		     .hasArg()
		     .withLongOpt("preffix")
		     .withArgName("preffix")
		     .withDescription("Domain names preffix, default (altai)")
		     .create();
		
		@SuppressWarnings("static-access")
		Option checkOption = OptionBuilder
			 .withLongOpt("check")
	         .withDescription("Calculate and check against 'datos' od SDL ")
	         .create();	
		
		Options options = new Options();
		options.addOption(helpOption);
		options.addOption(hostOption);
		options.addOption(portOption);
		options.addOption(userOption);
		options.addOption(databaseOption);
		options.addOption(passwordOption);
		options.addOption(dateOption);
		options.addOption(whereOption);
		options.addOption(checkOption);
		options.addOption(domainOption);
		options.addOption(preffixOption);
		options.addOption(skipOption);
		options.addOption(includeOption);

		
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
		
		String host = commandLine.getOptionValue(hostOption.getLongOpt());
		String port = commandLine.getOptionValue(portOption.getLongOpt(), "3306");
		String user = commandLine.getOptionValue(userOption.getLongOpt());
		String password = commandLine.getOptionValue(passwordOption.getLongOpt());
		String database = commandLine.getOptionValue(databaseOption.getLongOpt());
		String domain = commandLine.getOptionValue(domainOption.getLongOpt(), "ayudat.aonsolutions.net");
		String preffix = commandLine.getOptionValue(preffixOption.getLongOpt(), "altai");
		String where = commandLine.getOptionValue(whereOption.getLongOpt(), "`domain`.`name` LIKE 'altai%'");
		String skipCifs [] = Optional.ofNullable(commandLine.getOptionValues(skipOption.getLongOpt())).orElse(new String[] {});;
		String includeCifs [] = Optional.ofNullable(commandLine.getOptionValues(includeOption.getLongOpt())).orElse(new String[] {});;
		Date date = Optional.ofNullable(commandLine.getOptionValue(dateOption.getLongOpt())).map(s -> parse(s)).orElse(null);
		Boolean check = commandLine.hasOption(checkOption.getLongOpt());
		String leftOverArgs []  = commandLine.getArgs();

		Properties properties = new Properties();
		properties.setProperty("user", user);
		properties.setProperty("password", password);
		properties.setProperty("useSSL", "false");
		properties.setProperty("serverTimezone", TimeZone.getDefault().getID());
		String url = String.format("jdbc:mysql://%s:%s/%s", host, port, database);

		Settings settings;
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
			
		try (Connection connection = DriverManager.getConnection(url, properties)) {
			
			DSLContext dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
			
	
			Class.forName("com.mysql.cj.jdbc.Driver");
			
			
			
			try {
				dslContext.transaction((c)->{
					SalaryPDFBuilder<?>  salaryBuilder = 
					check ? 
					new CheckSalaryPDFBuilder() : 
					new JooqPDFSalaryBuilder(dslContext, domain).setFilter((pdf) -> { 
						
						if (includeCifs.length >0 && !Arrays.asList(includeCifs).contains(pdf.getCif())) {
							System.err.println("WARN: SKIPPED [" + pdf.getCif() + "]: " + pdf.getEnterpriseName());
							throw new SalaryPDFException("WARN: SKIPPED [" + pdf.getCif() + "]: " + pdf.getEnterpriseName() ); 	
						}
						
						if (Arrays.asList(skipCifs).contains(pdf.getCif())) {
							System.err.println("WARN: SKIPPED [" + pdf.getCif() + "]: " + pdf.getEnterpriseName());
							throw new SalaryPDFException("WARN: SKIPPED [" + pdf.getCif() + "]: " + pdf.getEnterpriseName() ); 
						}
					});
					
					
					for ( String arg : leftOverArgs  ) {
						try {
							SalaryPDFParser.parse(new File(arg), salaryBuilder);
						} catch (IOException | UnknownPDFException e) {
							System.err.printf("Error: %s \r\n", e.getMessage());
						}
					}
					
//					try {
//						SalaryPDFParser.parse(System.in, salaryBuilder);
//					} catch (IOException | UnknownPDFException e) {
//						System.err.printf("Error: %s \r\n", e.getMessage());
//					}
					
					salaryBuilder.execute();
//					throw new RollbackException();				
				});
			} catch ( RollbackException e) {
				
			}
			
		
			

		} catch (SQLException e) {
			System.out.println("Error: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			System.out.println("Error: " + e.getLocalizedMessage());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error: " + e.getLocalizedMessage());
		} finally {
		}
		 
	 }
	 
	 private static Date parse(String str) {
		 try {
			 return DATE_FORMAT.parse(str);
		 } catch ( Exception e ) {
			 e.printStackTrace();
			 return null;
		 }
	 }
	

}
