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
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jooq.impl.DSL;

import com.esferalia.aon.in.payroll.tgss.cra.CRA2Aon;
import com.esferalia.aon.in.payroll.tgss.creta.Bases2Aon;
import com.esferalia.aon.in.payroll.tgss.creta.Calculos2Aon;
import com.esferalia.aon.in.payroll.tgss.ivl.Ivl2Aon;

public class Siltra2Aon {
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/yyyy");

	 public static void main(String[] args) {
		

		Option hostOption = Option.builder("h")
		     .hasArg()
		     .required()
		     .longOpt("host")
		     .argName("name")
		     .desc("Connect to host.")
		     .build();
	 
		 Option portOption = Option.builder("P")
		     .hasArg()
		     .longOpt("port")
		     .argName("name")
		     .desc("Port number to use for connection, default (3306).")
		     .build();

		Option userOption = Option.builder("u")
		     .hasArg()
		     .required()
		     .longOpt("user")
		     .argName("name")
		     .desc("User for login if not current user.")
		     .build();

		Option passwordOption = Option.builder("p")
		     .hasArg()
		     .required()
		     .longOpt("password")
		     .argName("name")
		     .desc("Password to use when connecting to server.")
		     .build();

		Option databaseOption = Option.builder("D")
		     .hasArg()
		     .required()
		     .longOpt("database")
		     .argName("name")
		     .desc("Database to use")
		     .build();

		Option helpOption = Option.builder("?")
			.longOpt("help")
			.desc("Display this help and exit.")
			.build();

		Option calculosOption = Option.builder("c")
		     .hasArg()
		     .longOpt("calculos")
		     .argName("directory")
		     .desc("SLD-Fichero de Calculos directory.")
		     .build();

		Option basesOption = Option.builder("b")
		     .hasArg()
		     .longOpt("bases")
		     .argName("directory")
		     .desc("SLD-Ficheros de Bases directory.")
		     .build();
		
		Option craOption = Option.builder("a")
		     .hasArg()
		     .longOpt("cra")
		     .argName("directory")
		     .desc("Conceptos Retributidos Abonados (CRA) directory.")
		     .build();		

		Option ivlOption = Option.builder("v")
			     .hasArg()
			     .longOpt("ivl")
			     .argName("file")
			     .desc("Informe de vida laboral de un CCC file.")
			     .build();		

		Option whereOption = Option.builder()
		     .hasArg()
		     .longOpt("where")
		     .argName("where")
		     .desc("Only selected contracts. Quotes are mandatory.")
		     .build();

		Option checkOption = Option.builder()
			.longOpt("check")
			.desc("Calculate and check against 'datos' od SDL ")
			.build();	
		
		Option domainOption = Option.builder("d")
			.hasArg()
			.longOpt("domain")
			.argName("name")
			.desc("Domain to import into")
			.build();

		Options options = new Options();
		options.addOption(basesOption);
		options.addOption(helpOption);
		options.addOption(hostOption);
		options.addOption(portOption);
		options.addOption(userOption);
		options.addOption(databaseOption);
		options.addOption(passwordOption);
		options.addOption(calculosOption);
		options.addOption(whereOption);
		options.addOption(craOption);
		options.addOption(checkOption);
		options.addOption(domainOption);
		options.addOption(ivlOption);
		 
		Connection connection = null;
		try {
			CommandLineParser parser = new DefaultParser();
			CommandLine commandLine = parser.parse(options, args);
	
			
			String host = commandLine.getOptionValue(hostOption.getLongOpt());
			String port = commandLine.getOptionValue(portOption.getLongOpt(), "3306");
			String user = commandLine.getOptionValue(userOption.getLongOpt());
			String password = commandLine.getOptionValue(passwordOption.getLongOpt());
			String database = commandLine.getOptionValue(databaseOption.getLongOpt());
			String domain = commandLine.getOptionValue(domainOption.getLongOpt());
			String where = commandLine.getOptionValue(whereOption.getLongOpt(), "1=1"); //"`domain`.`name` LIKE 'altai%'"
			String [] ivlPaths = Optional.ofNullable(commandLine.getOptionValues(ivlOption.getLongOpt())).orElse(new String[] {});
			String [] craPaths = Optional.ofNullable(commandLine.getOptionValues(craOption.getLongOpt())).orElse(new String[] {});
			String [] basesPaths = Optional.ofNullable(commandLine.getOptionValues(basesOption.getLongOpt())).orElse(new String[] {});
			String [] calculosPaths = Optional.ofNullable(commandLine.getOptionValues(calculosOption.getLongOpt())).orElse(new String[] {});
			boolean check = commandLine.hasOption(checkOption.getLongOpt());
			
			Properties properties = new Properties();
			properties.setProperty("user", user);
			properties.setProperty("password", password);
			properties.setProperty("useSSL", "false");
			properties.setProperty("serverTimezone", TimeZone.getDefault().getID());
			String url = String.format("jdbc:mysql://%s:%s/%s", host, port, database);
	
			connection = DriverManager.getConnection(url, properties);
			
			
			Bases2Aon bases2Aon = new Bases2Aon(connection, DSL.condition(where));
			
			for (String basesPath : basesPaths) {
				Arrays.stream(new File(basesPath).listFiles(File::isFile))
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
				Arrays.stream(new File(calculosPath).listFiles(File::isFile ))
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
				Arrays.stream(new File(craPath).listFiles(File::isFile))
				.forEach(f -> {
					try {
						cra2Aon.fix(f);
					} catch (IOException e) {
						System.err.printf("Error: '%s' %s ", f.getPath(), e.getMessage());
					}
				});
			}	
			
			Ivl2Aon ivl2Aon = new Ivl2Aon(connection, domain);

			for (String ivlPath : ivlPaths) {
			    	File f = new File(ivlPath);
				try {
					if ( check )
						ivl2Aon.check(f);
					else
						ivl2Aon.insert(f);
				} catch (Exception e) {
				    e.printStackTrace();
				    System.err.printf("Error: '%s' %s ", f.getPath(), e.getMessage());
				}
			}			

		} catch (ParseException e) {
			// oops, somthing went wrong
			System.out.println("Error: " + e.getLocalizedMessage());
			new HelpFormatter().printHelp(Siltra2Aon.class.getSimpleName(), options);
		} catch (SQLException e) {
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
