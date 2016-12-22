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
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import net.aonsolutions.tgss.creta.jaxb.Utils;
import net.aonsolutions.tgss.creta.jaxb.solicitud.calculos.SolicitudCalculosBuilder;

public class SolicitudCalculos {

	public static void main(String[] args) throws JAXBException, DatatypeConfigurationException {
		String tipo = "L00";
		String desdeAnho = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
		String desdeMes = Integer.toString(Calendar.getInstance().get(Calendar.MONTH)+1);
		String hastaAnho = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
		String hastaMes = Integer.toString(Calendar.getInstance().get(Calendar.MONTH)+1);

		//@formatter:off
		Option fromYear =  SolicitudBorrador.getFromYearOption(desdeAnho);
		Option fromMonth =  SolicitudBorrador.getFromMonthOption(desdeMes);
		Option toYear =  SolicitudBorrador.getToYearOption(hastaAnho);
		Option toMonth =  SolicitudBorrador.getToMonthOption(hastaMes);
		Option ccc =  SolicitudBorrador.getCCCOption();
		Option authorized =  SolicitudBorrador.getAuthorizedOption();
		Option type =  SolicitudBorrador.getTypeOption(tipo);
		Option calcDetailed = OptionBuilder
				.withLongOpt("calc-detailed")
				.withDescription("Results detailed by each employee and each  period")
				.create();
		
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
			boolean calculosDesglosados = cmd.hasOption(calcDetailed.getLongOpt());

			generate(autorizado, desdeMes, desdeAnho, hastaMes, hastaAnho, tipo, calculosDesglosados, cccs, System.out);
			
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
			String tipo, boolean calculosDesglosados, String cccs[], OutputStream os) throws JAXBException {

		int authorized = Integer.parseInt(autorizado);
		Month fromMonth = Month.of(Integer.parseInt(desdeMes));
		int fromYear = Integer.parseInt(desdeAnho);
		Month toMonth = Month.of(Integer.parseInt(hastaMes));
		int toYear = Integer.parseInt(hastaAnho);
		Month showMonth = toMonth == Month.DECEMBER ? Month.JANUARY: Month.values()[toMonth.ordinal()+1];
		int showYear = showMonth == Month.JANUARY ? toYear + 1: toYear;

		generate(authorized, fromMonth, fromYear, toMonth, toYear, showMonth, showYear, tipo, calculosDesglosados, cccs, os);
	}

	public static void generate(int autorizado, Month desdeMes, int desdeAnho,Month hastaMes, int hastaAnho,
			Month presentacionMes, int presentacionAnho, String tipo, boolean calculosDesglosados, String cccs[], OutputStream os) throws JAXBException {

		SolicitudCalculosBuilder builder = 
				new SolicitudCalculosBuilder()
		.setAutorizado(autorizado);

		for ( String cCC: cccs ) {
			builder
			.setCCC(cCC)
			.setTipo(tipo)
			.setMesDesde(desdeMes)
			.setAnhoDesde(desdeAnho)
			.setMesHasta(hastaMes)
			.setAnhoHasta(hastaAnho)
			.setMesPresentacion(presentacionMes) 
			.setAnhoPresentacion(presentacionAnho)
			.setIndicadorCalculosDesglosados(calculosDesglosados)
			.addLiquidacion()
			;
		}
		net.aonsolutions.tgss.creta.jaxb.solicitud.calculos.SolicitudCalculos solicitud = builder.createSolicitudCalculos();

		Utils.marshal(solicitud, os);
	}
	
}
