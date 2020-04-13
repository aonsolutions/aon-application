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
import org.jooq.impl.DSL;

import com.esferalia.aon.in.payroll.tgss.cra.CRA2Aon;
import com.esferalia.aon.in.payroll.tgss.creta.Bases2Aon;
import com.esferalia.aon.in.payroll.tgss.creta.Calculos2Aon;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Siltra2Aon {
	
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
		Option calculosOption = OptionBuilder
		     .hasArg()
		     .withLongOpt("calculos")
		     .withArgName("directory")
		     .withDescription("SLD-Fichero de Calculos directory.")
		     .create("c");

		@SuppressWarnings("static-access")
		Option basesOption = OptionBuilder
		     .hasArg()
		     .withLongOpt("bases")
		     .withArgName("directory")
		     .withDescription("SLD-Ficheros de Bases directory.")
		     .create("b");
		
		@SuppressWarnings("static-access")
		Option craOption = OptionBuilder
		     .hasArg()
		     .withLongOpt("cra")
		     .withArgName("directory")
		     .withDescription("Conceptos Retributidos Abonados (CRA) directory.")
		     .create("a");		

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
		Option checkOption = OptionBuilder
			 .withLongOpt("check")
	         .withDescription("Calculate and check against 'datos' od SDL ")
	         .create();	
		
		Options options = new Options();
		options.addOption(basesOption);
		options.addOption(helpOption);
		options.addOption(hostOption);
		options.addOption(portOption);
		options.addOption(userOption);
		options.addOption(databaseOption);
		options.addOption(passwordOption);
		options.addOption(calculosOption);
		options.addOption(dateOption);
		options.addOption(whereOption);
		options.addOption(craOption);
		options.addOption(checkOption);
		 
		Connection connection = null;
		try {
			CommandLineParser parser = new GnuParser();
			CommandLine commandLine = parser.parse(options, args);
	
			
			String host = commandLine.getOptionValue(hostOption.getLongOpt());
			String port = commandLine.getOptionValue(portOption.getLongOpt(), "3306");
			String user = commandLine.getOptionValue(userOption.getLongOpt());
			String password = commandLine.getOptionValue(passwordOption.getLongOpt());
			String database = commandLine.getOptionValue(databaseOption.getLongOpt());
			String where = commandLine.getOptionValue(whereOption.getLongOpt(), "`domain`.`name` LIKE 'altai%'");
			String craPaths [] = Optional.ofNullable(commandLine.getOptionValues(craOption.getLongOpt())).orElse(new String[] {});;
			String basesPaths [] = Optional.ofNullable(commandLine.getOptionValues(basesOption.getLongOpt())).orElse(new String[] {});;
			String calculosPaths [] = Optional.ofNullable(commandLine.getOptionValues(calculosOption.getLongOpt())).orElse(new String[] {});;
			Date date = Optional.ofNullable(commandLine.getOptionValue(dateOption.getLongOpt())).map(s -> parse(s)).orElse(null);
			boolean check = commandLine.hasOption(checkOption.getLongOpt());
			
			Properties properties = new Properties();
			properties.setProperty("user", user);
			properties.setProperty("password", password);
			properties.setProperty("useSSL", "false");
			properties.setProperty("serverTimezone", TimeZone.getDefault().getID());
			String url = String.format("jdbc:mysql://%s:%s/%s", host, port, database);
	
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(url, properties);
			
			
			Bases2Aon bases2Aon = new Bases2Aon(connection, DSL.condition(where));
			
			for (String basesPath : basesPaths) {
				Arrays.stream(new File(basesPath).listFiles(f-> f.isFile()))
				.forEach(f -> {
					try {
						if ( check )
							bases2Aon.check(f);
						else
							bases2Aon.fix(f);
					} catch (IOException e) {
						System.err.printf("Error: '%s' %s ", f.getPath(), e.getMessage());
					}
				});
			}
			
			Calculos2Aon calculos2Aon = new Calculos2Aon(connection, DSL.condition(where));
			
			for (String calculosPath : calculosPaths) {
				Arrays.stream(new File(calculosPath).listFiles(f-> f.isFile() ))
				.forEach(f -> {
					try {
						if ( check )
							calculos2Aon.check(f);
						else
							calculos2Aon.fix(f);
					} catch (IOException e) {
						System.err.printf("Error: '%s' %s ", f.getPath(), e.getMessage());
					}
				});
			}
			
			CRA2Aon cra2Aon = new CRA2Aon(connection, DSL.condition(where));
			
			for (String craPath : craPaths) {
				Arrays.stream(new File(craPath).listFiles(f-> f.isFile() ))
				.forEach(f -> {
					try {
						cra2Aon.fix(f);
					} catch (IOException e) {
						System.err.printf("Error: '%s' %s ", f.getPath(), e.getMessage());
					}
				});
			}			

		} catch (ParseException e) {
			// oops, somthing went wrong
			System.out.println("Error: " + e.getLocalizedMessage());
			new HelpFormatter().printHelp(Siltra2Aon.class.getSimpleName(), options);
		} catch (SQLException e) {
			System.out.println("Error: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			System.out.println("Error: " + e.getLocalizedMessage());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error: " + e.getLocalizedMessage());
		} finally {
			try {
				if ( connection != null )
					connection.close();
			} catch ( SQLException e ) {
				System.err.println("Oops, something went wrong, " + e.getLocalizedMessage());
			}
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
