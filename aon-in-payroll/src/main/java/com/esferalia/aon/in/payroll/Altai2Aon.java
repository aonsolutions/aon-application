package com.esferalia.aon.in.payroll;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.esferalia.aon.in.payroll.altai.TSLABEMPParser;
import com.esferalia.aon.in.payroll.altai.TSLABTRABParser;
import com.esferalia.aon.in.payroll.altai.jooq.JooqTSLABEMPHandler;
import com.esferalia.aon.in.payroll.altai.jooq.JooqTSLABTRAHandler;

public class Altai2Aon {
	



	 public static void main(String[] args) {
		
		@SuppressWarnings("static-access")
		Option empOption = OptionBuilder
		     .hasArg()
//			 .isRequired()
		     .withLongOpt("emp")
		     .withArgName("fichero")
		     .withDescription("Altai TSLABEMP file.")
		     .create("e");

		@SuppressWarnings("static-access")
		Option traOption = OptionBuilder
		     .hasArg()
//			 .isRequired()
		     .withLongOpt("tra")
		     .withArgName("fichero")
		     .withDescription("Altai TSLABTRA file.")
		     .create("t");

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
		Option domainOption = OptionBuilder
		     .hasArg()
			 .isRequired()
		     .withLongOpt("domain")
		     .withArgName("name")
		     .withDescription("Parent domain")
		     .create("d");

		@SuppressWarnings("static-access")
		Option domainPreffix = OptionBuilder
		     .hasArg()
		     .withLongOpt("preffix")
		     .withArgName("preffix")
		     .withDescription("Domain names preffix, default (altai)")
		     .create();

		@SuppressWarnings("static-access")
		Option fromOption = OptionBuilder
		     .hasArg()
		     .withLongOpt("from")
		     .withArgName("date")
		     .withDescription("Date from, default 01/01/2020")
		     .create("f");

		@SuppressWarnings("static-access")
		Option helpOption = OptionBuilder
			 .withLongOpt("help")
	         .withDescription("Display this help and exit.")
	         .create("?");

		Options options = new Options();
		options.addOption(empOption);
		options.addOption(traOption);
		options.addOption(helpOption);
		options.addOption(hostOption);
		options.addOption(portOption);
		options.addOption(userOption);
		options.addOption(domainOption);
		options.addOption(domainPreffix);
		options.addOption(databaseOption);
		options.addOption(passwordOption);
		options.addOption(fromOption);
		 
		Connection connection = null;
		try {
			CommandLineParser parser = new GnuParser();
			CommandLine commandLine = parser.parse(options, args);
	
			
			String host = commandLine.getOptionValue(hostOption.getLongOpt());
			String port = commandLine.getOptionValue(portOption.getLongOpt(), "3306");
			String user = commandLine.getOptionValue(userOption.getLongOpt());
			String password = commandLine.getOptionValue(passwordOption.getLongOpt());
			String domain = commandLine.getOptionValue(domainOption.getLongOpt());
			String database = commandLine.getOptionValue(databaseOption.getLongOpt());
			String preffix = commandLine.getOptionValue(domainPreffix.getLongOpt(), "altai");
			String emps [] = commandLine.getOptionValues(empOption.getLongOpt());
			String tras [] = commandLine.getOptionValues(traOption.getLongOpt());
			Date from = new SimpleDateFormat("dd/MM/yyyy").parse(commandLine.getOptionValue(fromOption.getLongOpt(), "01/01/2020"));
	
			Properties properties = new Properties();
			properties.setProperty("user", user);
			properties.setProperty("password", password);
			properties.setProperty("useSSL", "false");
			properties.setProperty("serverTimezone", TimeZone.getDefault().getID());
			String url = String.format("jdbc:mysql://%s:%s/%s", host, port, database);
	
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(url, properties);
			
			List<File> empFiles = 
			new ArrayList<File>();
			
			if ( emps == null ) {
				emps = new String [] {};
			}
			
			empFiles.addAll(
			Arrays.stream(emps)
			.map(emp -> new File(emp))
			.filter(f->f.isFile())
			.collect(Collectors.toList())
			); 
			empFiles.addAll(
			Arrays.stream(emps)
			.map(emp -> new File(emp))
			.filter(f->f.isDirectory())
			.map(d -> Arrays.asList(d.listFiles(f -> f.getName().matches("^TSLABEMP.*ASC"))))
			.flatMap(Collection::stream)
			.collect(Collectors.toList())
			);

			for (File empFile : empFiles) {
				InputStream in = new FileInputStream(empFile);
				JooqTSLABEMPHandler tslabempHandler = 
						new JooqTSLABEMPHandler(connection, domain, preffix, empFile);
				TSLABEMPParser.parse(in, tslabempHandler);
				tslabempHandler.execute();
				in.close();
			}
			
			List<File> traFiles =
			new ArrayList<File>();
			
			if ( tras == null ) {
				tras = new String [] {};
			}
			
			traFiles.addAll(
			Arrays.stream(tras)
			.map(tra -> new File(tra))
			.filter(f->f.isFile())
			.collect(Collectors.toList())
			); 
			traFiles.addAll(
			Arrays.stream(tras)
			.map(emp -> new File(emp))
			.filter(f->f.isDirectory())
			.map(d -> Arrays.asList(d.listFiles(f -> f.getName().matches("^TSLABTRA.*ASC"))))
			.flatMap(Collection::stream)
			.collect(Collectors.toList())
			);

			for (File traFile : traFiles) {
				InputStream in = new FileInputStream(traFile);
				JooqTSLABTRAHandler tslabtraHandler = 
						new JooqTSLABTRAHandler(connection, from, traFile);
				TSLABTRABParser.parse(in, tslabtraHandler);
				tslabtraHandler.execute();
				
				in.close();
			}

		} catch (ParseException e) {
			// oops, somthing went wrong
			System.out.println("Error: " + e.getLocalizedMessage());
			new HelpFormatter().printHelp(Altai2Aon.class.getSimpleName(), options);
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

}
