package com.esferalia.aon.payroll.tgss.creta;

import static net.aonsolutions.tgss.creta.jaxb.solicitud.borrador.SolicitudBorradorBuilder.createL00;

import java.time.Month;
import java.util.Calendar;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import net.aonsolutions.tgss.creta.jaxb.Utils;
import net.aonsolutions.tgss.creta.jaxb.solicitud.borrador.SolicitudBorradorBuilder;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class TrabajadoresTramos {

	public TrabajadoresTramos() {
		// TODO Auto-generated constructor stub
	}
	
	
	public static void main(String[] args) throws JAXBException, DatatypeConfigurationException {
		int anho = Calendar.getInstance().get(Calendar.YEAR);
		Month mes = Month.of(Calendar.getInstance().get(Calendar.MONTH)+1);

		//@formatter:off
		Option year =  OptionBuilder.withArgName("year")
									.hasArg()
									.withLongOpt("year")
									.withDescription("Year built-in default (" + anho + ")")
									.create("y");
		Option month =  OptionBuilder.withArgName("month")
									.hasArg()
									.withLongOpt("month")
									.withDescription("Month built-in default (" + mes.getValue() + ")")
									.create("m");
		Option ccc =  OptionBuilder.withArgName("name")
								   .hasArg()
								   .isRequired()
								   .withLongOpt("ccc")
								   .withDescription("Add the Ccc to the Cccs.")
								   .create("c");
		Option authorized =  OptionBuilder.withArgName("name")
										  .hasArg()
										  .isRequired()
										  .withLongOpt("authorized")
										  .withDescription("Number for use 'Sistem RED'. ")
										  .create("a");
		
		Options options = new Options()
		.addOption(authorized)
		.addOption(year)
		.addOption(month)
		.addOption(ccc)
		;
		//@formatter:on

		// create the parser
		CommandLineParser parser = new GnuParser();

		try {
			
			boolean aceptarBasesAnteriores = false;

			// parse the command line arguments
			CommandLine cmd = parser.parse(options, args);
			
			String cccs [] = cmd.getOptionValues(ccc.getLongOpt());
			
			if ( cmd.hasOption(month.getLongOpt()))
				mes = Month.of(Integer.parseInt(cmd.getOptionValue(month.getLongOpt())));
			if ( cmd.hasOption(year.getLongOpt()))
					anho = Integer.parseInt(cmd.getOptionValue(year.getLongOpt()));
			
			int autorizado = Integer.parseInt(cmd.getOptionValue(authorized.getLongOpt()));
			
			
			Utils.marshal(
					createL00(autorizado, mes, anho, aceptarBasesAnteriores, cccs),
					System.out
					);
// -a 228115
// -c 011101105360062 
// -c 011101105577910
			
		} catch (ParseException e) {
			// oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + e.getMessage());
			// automatically generate the help statement
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("calc", options);

		}

		
	}
}
