package net.aonsolutions.dump;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.esferalia.aon.occam.impl.jooq.console.ConsoleParams;
import com.esferalia.aon.occam.impl.jooq.console.IsolateDomain;


public class CmdIsolateDomain {
	
	public static void main(String[] args) throws SQLException, SecurityException {

		Option hostNameOpt = Option.builder().hasArg().argName("name").required().longOpt("host").desc("Host name which we want to connect to.").build();
		Option portOpt = Option.builder().hasArg().argName("name").longOpt("port").desc("Port to connect to host.").type(Integer.class).build();
		Option dataBaseOpt = Option.builder().hasArg().argName("name").required().longOpt("database").desc("DataBase we are going to use.").build();
		Option userOpt = Option.builder().hasArg().argName("name").required().longOpt("user").desc("User for connecting to server.").build();
		Option passwordOpt = Option.builder().hasArg().argName("name").required().longOpt("password").desc("Password for connecting to server.").build();
		Option domainOpt = Option.builder().hasArg().argName("name").required().longOpt("domain").desc("Name of the domain to be duplicated.").build();
		Option nameOpt = Option.builder().hasArg().argName("name").required().longOpt("name").desc("New Domain's name.").build();
		Option helpOpt = Option.builder().longOpt("help").desc("Shows help for entrys arguments.").build();
		Options options = new Options().addOption(hostNameOpt).addOption(portOpt).addOption(dataBaseOpt).addOption(userOpt).addOption(passwordOpt).addOption(domainOpt).addOption(nameOpt).addOption(helpOpt);

		// Parser create
		CommandLineParser parser = new DefaultParser();
		try (PrintWriter printStream = new PrintWriter(new FileOutputStream("/home/ecastellano/TRABAJO/isolateDomain.log"))) {

			// Parse the command line arguments
			CommandLine cmd = parser.parse(options, args);

			if (cmd.hasOption(helpOpt.getLongOpt())) {
				// Automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("aon-dump", options);
				return;
			}

			// Get all the information we have gotten from the comand line
			ConsoleParams params = new ConsoleParams()
					.setHostName(cmd.getOptionValue(hostNameOpt.getLongOpt()))
					.setUser(cmd.getOptionValue(userOpt.getLongOpt()))
					.setPassword(cmd.getOptionValue(passwordOpt.getLongOpt()))
					.setDatabase(cmd.getOptionValue(dataBaseOpt.getLongOpt()))
					.setPort(cmd.hasOption(portOpt.getLongOpt()) ? cmd.getOptionValue(portOpt.getLongOpt()) : "3306")
					.setDomainName(cmd.getOptionValue(domainOpt.getLongOpt()))
					.setNewDomainName(cmd.getOptionValue(nameOpt.getLongOpt()))
					.setPrinter(printStream);
			
			IsolateDomain.isolate(params);
			printStream.flush();
		} catch (ParseException | FileNotFoundException e) {
			e.printStackTrace();
			// Automatically generate the help statement
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("aon-dump", options);
		}
	}
}
