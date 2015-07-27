package com.esferalia.aon.payroll.tgss.creta;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import net.aonsolutions.tgss.creta.jaxb.Utils;
import net.aonsolutions.tgss.creta.jaxb.rnt.Liquidacion;
import net.aonsolutions.tgss.creta.jaxb.rnt.RelacionNominalTrabajadores;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.watson.util.AonStringUtils;

public class RNT {
	
	private static void liquidacion(Liquidacion liquidacion) {
	}
	

	// ------------------------------------------------------------------------
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException, JAXBException, IOException {
		//@formatter:off
		Option hostName =  Bases.getHostNameOption();
		Option user =  Bases.getDbUserOption();
		Option password =  Bases.getDbPasswordOption();
		Option database =  Bases.getDatabaseOption();
		Option rntFile =  OptionBuilder.withArgName("file")
						.hasArg()
						.withLongOpt("rnt")
						.withDescription("Fichero Relacion Nominal de Trabajadores.")
						.create("r");

		Options options = new Options()
		.addOption(hostName)
		.addOption(user)
		.addOption(password)
		.addOption(database)
		.addOption(rntFile)
		;
		//@formatter:on

		// create the parser
		CommandLineParser parser = new GnuParser();

		try {
			// parse the command line arguments
			CommandLine cmd = parser.parse(options, args);

			Class.forName(com.mysql.jdbc.Driver.class.getName());

			Connection connection = DriverManager.getConnection(String.format(
					"jdbc:mysql://%s:%d/%s",
					cmd.getOptionValue(hostName.getLongOpt(), "127.0.0.1"),
					3306, cmd.getOptionValue(database.getLongOpt())), cmd
					.getOptionValue(user.getLongOpt()), cmd
					.getOptionValue(password.getLongOpt()));

			AONContext ctx = new AONContext(connection);

			InputStream is = cmd.hasOption(rntFile.getLongOpt()) ? 
					System.in
					: new FileInputStream(cmd.getOptionValue(rntFile.getLongOpt()));


			RelacionNominalTrabajadores rnt = 
					Utils.unmarshal(RelacionNominalTrabajadores.class, is);
			
			rnt.getLiquidacion();
			
			is.close();

		} catch (ParseException e) {
			// oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + e.getMessage());
			// automatically generate the help statement
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("cret@", options);

		}
	}
	
}
