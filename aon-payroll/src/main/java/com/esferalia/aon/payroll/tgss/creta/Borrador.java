package com.esferalia.aon.payroll.tgss.creta;

import java.time.Month;
import java.util.Calendar;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import net.aonsolutions.tgss.creta.jaxb.Utils;
import net.aonsolutions.tgss.creta.jaxb.solicitud.borrador.SolicitudBorrador;
import net.aonsolutions.tgss.creta.jaxb.solicitud.borrador.SolicitudBorradorBuilder;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Borrador {
	
	@SuppressWarnings("static-access")
	public static Option getTypeOption(String tipo){
		return OptionBuilder.withArgName("name")
		  .hasArg()
		  .withLongOpt("type")
		  .withDescription(
				  "Type of Settlement : \r\n"+
				  "L00 (normal)\r\n"+
				  "L03 (complement.)\r\n"+
				  "Built-in default ("+tipo+")  ")
		  .create("t");
	}
	
	@SuppressWarnings("static-access")
	public static Option getCCCOption(){
		return OptionBuilder.withArgName("name")
				   .hasArg()
				   .isRequired()
				   .withLongOpt("ccc")
				   .withDescription("Add the Ccc to the Cccs.")
				   .create("c");
	}
	

	@SuppressWarnings("static-access")
	public static Option getYearOption(int anho){
		return OptionBuilder.withArgName("year")
		.hasArg()
		.withLongOpt("year")
		.withDescription("Year built-in default (" + anho + ")")
		.create("y");		
	}
	
	@SuppressWarnings("static-access")
	public static Option getMonthOption(Month mes){
		return OptionBuilder.withArgName("month")
				.hasArg()
				.withLongOpt("month")
				.withDescription("Month built-in default (" + mes.getValue() + ")")
				.create("m");
	}
	
	@SuppressWarnings("static-access")
	public static Option getAuthorizedOption(){
		return OptionBuilder.withArgName("name")
				  .hasArg()
				  .isRequired()
				  .withLongOpt("authorized")
				  .withDescription("Number for use 'Sistem RED'. ")
				  .create("a");
	}
	
	@SuppressWarnings("static-access")
	public static Option getSkipPrevBasesOption() {
		return  OptionBuilder.withLongOpt("skip-prev-bases")
				.withDescription("Skip previous bases.")
				.create("b");
	}
	
	
	public static void main(String[] args) throws JAXBException, DatatypeConfigurationException {
		String tipo = "L00";
		int anho = Calendar.getInstance().get(Calendar.YEAR);
		Month mes = Month.of(Calendar.getInstance().get(Calendar.MONTH)+1);

		//@formatter:off
		Option year =  getYearOption(anho);
		Option month =  getMonthOption(mes);
		Option ccc =  getCCCOption();
		Option authorized =  getAuthorizedOption();
		Option type =  getTypeOption(tipo);
		Option skipPrevBases =  getSkipPrevBasesOption();
		
		Options options = new Options()
		.addOption(authorized)
		.addOption(year)
		.addOption(month)
		.addOption(ccc)
		.addOption(type)
		.addOption(skipPrevBases)
		;
		//@formatter:on

		// create the parser
		CommandLineParser parser = new GnuParser();

		try {
			

			// parse the command line arguments
			CommandLine cmd = parser.parse(options, args);
			
			String cccs [] = cmd.getOptionValues(ccc.getLongOpt());
			
			if ( cmd.hasOption(month.getLongOpt()))
				mes = Month.of(Integer.parseInt(cmd.getOptionValue(month.getLongOpt())));
			if ( cmd.hasOption(year.getLongOpt()))
					anho = Integer.parseInt(cmd.getOptionValue(year.getLongOpt()));
			if ( cmd.hasOption(type.getLongOpt()))
				tipo = cmd.getOptionValue(type.getLongOpt());
			
			int autorizado = Integer.parseInt(cmd.getOptionValue(authorized.getLongOpt()));

			boolean aceptarBasesAnteriores = !cmd.hasOption(skipPrevBases.getLongOpt());

			SolicitudBorradorBuilder builder = 
					new SolicitudBorradorBuilder()
			.setAutorizado(autorizado);

			for ( String cCC: cccs ) {
				builder
				.setCCC(cCC)
				.setTipo(tipo)
				.setAceptarBasesAnteriores(aceptarBasesAnteriores)
				.setMesDesde(mes)
				.setAnhoDesde(anho)
				.setMesHasta(mes)
				.setAnhoHasta(anho)
				.addLiquidacion()
				;
			}
			
			SolicitudBorrador solicitudBorrador = builder.createSolicitudBorrador();
			
			Utils.marshal(
					solicitudBorrador,
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
