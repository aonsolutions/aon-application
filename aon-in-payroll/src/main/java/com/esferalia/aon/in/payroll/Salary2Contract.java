package com.esferalia.aon.in.payroll;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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
import com.esferalia.aon.in.payroll.salary.jooq.JooqSalary2Contract;

public class Salary2Contract {
	



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
		Option whereOption = OptionBuilder
		     .hasArg()
		     .withLongOpt("where")
		     .withArgName("where")
		     .withDescription("Only selected contracts.")
		     .create();

		@SuppressWarnings("static-access")
		Option helpOption = OptionBuilder
			 .withLongOpt("help")
	         .withDescription("Display this help and exit.")
	         .create("?");

		Options options = new Options();
		options.addOption(helpOption);
		options.addOption(hostOption);
		options.addOption(portOption);
		options.addOption(userOption);
		options.addOption(whereOption);
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
		
		String host = commandLine.getOptionValue(hostOption.getLongOpt());
		String port = commandLine.getOptionValue(portOption.getLongOpt(), "3306");
		String user = commandLine.getOptionValue(userOption.getLongOpt());
		String password = commandLine.getOptionValue(passwordOption.getLongOpt());
		String database = commandLine.getOptionValue(databaseOption.getLongOpt());
		String where = commandLine.getOptionValue(whereOption.getLongOpt(), String.format("`salary`.`registration` = %d", Integer.MIN_VALUE));
	
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
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Error: " + e.getLocalizedMessage());
		} 
		
		try (Connection connection = DriverManager.getConnection(url, properties);
			DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings)) {
			
			dslContext.transaction(c -> {
				JooqSalary2Contract.pullUp(dslContext, DSL.condition(where));
//				throw new RollbackException();
			});
			

		} catch (RollbackException e) {
		} catch (SQLException e) {
			System.out.println("Error: " + e.getLocalizedMessage());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error: " + e.getLocalizedMessage());
		} 
	 }

}
