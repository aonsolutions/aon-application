package com.esferalia.aon.dsi.util;

import java.net.URL;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;


public class ParseArgs {


	private Date fromDate;

	private String dsiURL;
	private String aonURL;
	private String aonUser;
	private String aonPasswd;
	private boolean dryRun;
	private boolean drop;
	private String domainName;


	public ParseArgs(String[] args) {

		Options options = new Options();

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(false);
		OptionBuilder.withDescription("Print this help.");
		Option helpOption = OptionBuilder.create("help");

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(false);
		OptionBuilder.withDescription("Not modify database. Only to check that all works fine.");
		Option dryRunOption = OptionBuilder.create("dryrun");

		OptionBuilder.isRequired(true);
		OptionBuilder.hasArg(true);
		OptionBuilder.withArgName("JDBC URL");
		OptionBuilder.withType(String.class);
		OptionBuilder.withDescription("DSI database JDBC URL (jdbc:paradox:/<dir>)");
		Option dsiURLOption = OptionBuilder.create("dsiurl");

		OptionBuilder.isRequired(true);
		OptionBuilder.hasArg(true);
		OptionBuilder.withArgName("JDBC URL");
		OptionBuilder.withType(String.class);
		OptionBuilder.withDescription("AON database JDBC URL (jdbc:aon://<host>[:<port>]/[<database>]");
		Option aonURLOption = OptionBuilder.create("aonurl");

		OptionBuilder.isRequired(true);
		OptionBuilder.hasArg(true);
		OptionBuilder.withArgName("name");
		OptionBuilder.withType(String.class);
		OptionBuilder.withDescription("AON database user");
		Option aonUserOption = OptionBuilder.create("aonuser");

		OptionBuilder.isRequired(true);
		OptionBuilder.hasArg(true);
		OptionBuilder.withArgName("name");
		OptionBuilder.withType(String.class);
		OptionBuilder.withDescription("AON database user's password.");
		Option aonPasswdOption = OptionBuilder.create("aonpasswd");

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withArgName("date");
		OptionBuilder.withType(String.class);
		OptionBuilder.withDescription("Only load data from this date (M/d/Y)");
		Option fromDateOption = OptionBuilder.create("from");

		OptionBuilder.isRequired(true);
		OptionBuilder.hasArg(true);
		OptionBuilder.withArgName("domain");
		OptionBuilder.withType(String.class);
		OptionBuilder.withDescription("Main domain");
		Option domainOption = OptionBuilder.create("domain");

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(false);
		OptionBuilder.withDescription("Drop database.");
		Option dropOption = OptionBuilder.create("drop");

		options.addOption(helpOption);
		options.addOption(dryRunOption);
		options.addOption(dsiURLOption);
		options.addOption(aonURLOption);
		options.addOption(aonUserOption);
		options.addOption(aonPasswdOption);
		options.addOption(fromDateOption);
		options.addOption(domainOption);
		options.addOption(dropOption);

		CommandLineParser parser = new PosixParser();

		HelpFormatter helpFormatter = new HelpFormatter();

		try {

			CommandLine line = parser.parse(options, args);

			if (line.hasOption(helpOption.getOpt())) {
				helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX,
						options, true);
				return;
			}

			dsiURL = line.getOptionValue(dsiURLOption.getOpt());

			aonURL = line.getOptionValue(aonURLOption.getOpt());
			aonUser = line.getOptionValue(aonUserOption.getOpt());
			aonPasswd = line.getOptionValue(aonPasswdOption.getOpt());

			dryRun = line.hasOption(dryRunOption.getOpt());

			domainName = line.getOptionValue(domainOption.getOpt());

			String fromString = line.getOptionValue(fromDateOption.getOpt());
			if (fromString != null) {
				fromDate = DateFormat.getDateInstance(DateFormat.SHORT).parse(
						fromString);
			}

			drop = line.hasOption(dropOption.getOpt());

		} catch (Exception e) {
			helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX,
					options, true);
		}
	}

	public boolean drop() {
		return drop;
	}

	public boolean dryRun() {
		return dryRun;
	}
	
	public String getDsiURL() {
		return dsiURL;
	}

	public String getAonURL() {
		return aonURL;
	}
	
	public String getAonUser() {
		return aonUser;
	}
	
	public String getAonPasswd() {
		return aonPasswd;
	}
	
	public String getDomainName() {
		return domainName;
	}
	

	// --------------------------------------------------------------------

	public static int main(String args []) {
		new ParseArgs(args);
		return 0;
	}

}
