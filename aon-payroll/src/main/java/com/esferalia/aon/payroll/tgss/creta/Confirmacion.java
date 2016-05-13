package com.esferalia.aon.payroll.tgss.creta;

import java.io.OutputStream;
import java.time.Month;
import java.util.Calendar;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import net.aonsolutions.tgss.creta.jaxb.Utils;
import net.aonsolutions.tgss.creta.jaxb.solicitud.confirmacion.SolicitudConfirmacion;
import net.aonsolutions.tgss.creta.jaxb.solicitud.confirmacion.SolicitudConfirmacionBuilder;

public class Confirmacion {

	
	public static void main(String[] args) throws JAXBException, DatatypeConfigurationException {
		String tipo = "L00";
		String desdeAnho = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
		String desdeMes = Integer.toString(Calendar.getInstance().get(Calendar.MONTH)+1);
		String hastaAnho = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
		String hastaMes = Integer.toString(Calendar.getInstance().get(Calendar.MONTH)+1);

		//@formatter:off
		Option fromYear =  Borrador.getFromYearOption(desdeAnho);
		Option fromMonth =  Borrador.getFromMonthOption(desdeMes);
		Option toYear =  Borrador.getToYearOption(hastaAnho);
		Option toMonth =  Borrador.getToMonthOption(hastaMes);
		Option ccc =  Borrador.getCCCOption();
		Option authorized =  Borrador.getAuthorizedOption();
		Option type =  Borrador.getTypeOption(tipo);
		
		Options options = new Options()
		.addOption(authorized)
		.addOption(fromYear)
		.addOption(fromMonth)
		.addOption(toYear)
		.addOption(toMonth)
		.addOption(ccc)
		.addOption(type)
		;
		//@formatter:on

		// create the parser
		CommandLineParser parser = new GnuParser();

		try {

			// parse the command line arguments
			CommandLine cmd = parser.parse(options, args);
			
			desdeMes = cmd.getOptionValue(fromMonth.getLongOpt(), desdeMes);
			desdeAnho = cmd.getOptionValue(fromYear.getLongOpt(), desdeAnho);
			hastaMes = cmd.getOptionValue(toMonth.getLongOpt(), hastaMes);
			hastaAnho = cmd.getOptionValue(toYear.getLongOpt(), hastaAnho);
			tipo = cmd.getOptionValue(type.getLongOpt(), tipo);
			String cccs [] = cmd.getOptionValues(ccc.getLongOpt());
			String autorizado = cmd.getOptionValue(authorized.getLongOpt());

			generate(autorizado, desdeMes, desdeAnho, hastaMes, hastaAnho, tipo, cccs, System.out);
			
		} catch (ParseException e) {
			// oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + e.getMessage());
			// automatically generate the help statement
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("calc", options);

		}

		
	}
	public static void generate(String autorizado, String desdeMes, String desdeAnho,
			String hastaMes, String hastaAnho,
			String tipo, String cccs[], OutputStream os) throws JAXBException {

		int authorized = Integer.parseInt(autorizado);
		Month fromMonth = Month.of(Integer.parseInt(desdeMes));
		int fromYear = Integer.parseInt(desdeAnho);
		Month toMonth = Month.of(Integer.parseInt(hastaMes));
		int toYear = Integer.parseInt(hastaAnho);

		generate(authorized, fromMonth, fromYear, toMonth, toYear, tipo, cccs, os);
	}

	public static void generate(int autorizado, Month desdeMes, int desdeAnho,Month hastaMes, int hastaAnho,
			String tipo, String cccs[], OutputStream os) throws JAXBException {

		SolicitudConfirmacionBuilder builder = 
				new SolicitudConfirmacionBuilder()
		.setAutorizado(autorizado);

		for ( String cCC: cccs ) {
			builder
			.setCCC(cCC)
			.setTipo(tipo)
			.setMesDesde(desdeMes)
			.setAnhoDesde(desdeAnho)
			.setMesHasta(hastaMes)
			.setAnhoHasta(hastaAnho)
			.addLiquidacion()
			;
		}
		SolicitudConfirmacion solicitudConfirmacion = builder.createSolicitudConfirmacion();

		Utils.marshal(solicitudConfirmacion, os);
		
	}
	
}
