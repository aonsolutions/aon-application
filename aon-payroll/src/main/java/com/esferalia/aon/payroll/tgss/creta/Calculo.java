package com.esferalia.aon.payroll.tgss.creta;

import static com.esferalia.aon.payroll.tgss.creta.Borrador.getYearOption;

import java.io.OutputStream;
import java.time.Month;
import java.util.Calendar;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import net.aonsolutions.tgss.creta.jaxb.Utils;
import net.aonsolutions.tgss.creta.jaxb.solicitud.borrador.SolicitudBorrador;
import net.aonsolutions.tgss.creta.jaxb.solicitud.borrador.SolicitudBorradorBuilder;
import net.aonsolutions.tgss.creta.jaxb.solicitud.calculos.SolicitudCalculos;
import net.aonsolutions.tgss.creta.jaxb.solicitud.calculos.SolicitudCalculosBuilder;
import net.aonsolutions.tgss.creta.jaxb.solicitud.trabajadorestramos.SolicitudTrabajadoresTramos;
import net.aonsolutions.tgss.creta.jaxb.solicitud.trabajadorestramos.SolicitudTrabajadoresTramosBuilder;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Calculo {

	public static void main(String[] args) throws JAXBException, DatatypeConfigurationException {
		String tipo = "L00";
		String anho = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
		String mes = Integer.toString(Calendar.getInstance().get(Calendar.MONTH)+1);

		//@formatter:off
		Option year =  getYearOption(anho);
		Option month =  Borrador.getMonthOption(mes);
		Option ccc =  Borrador.getCCCOption();
		Option authorized =  Borrador.getAuthorizedOption();
		Option type =  Borrador.getTypeOption(tipo);
		
		Options options = new Options()
		.addOption(authorized)
		.addOption(year)
		.addOption(month)
		.addOption(ccc)
		.addOption(type)
		;
		//@formatter:on

		// create the parser
		CommandLineParser parser = new GnuParser();

		try {

			// parse the command line arguments
			CommandLine cmd = parser.parse(options, args);
			
			mes = cmd.getOptionValue(month.getLongOpt(), mes);
			anho = cmd.getOptionValue(year.getLongOpt(), anho);
			tipo = cmd.getOptionValue(type.getLongOpt(), tipo);
			String cccs [] = cmd.getOptionValues(ccc.getLongOpt());
			String autorizado = cmd.getOptionValue(authorized.getLongOpt());
			
			generate(autorizado, mes, anho, tipo, cccs, System.out);
			
		} catch (ParseException e) {
			// oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + e.getMessage());
			// automatically generate the help statement
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("calc", options);

		}

		
	}
	
	public static void generate(String autorizado, String mes, String anho,
			String tipo, String cccs[], OutputStream os) throws JAXBException {

		int authorized = Integer.parseInt(autorizado);
		Month month = Month.of(Integer.parseInt(mes));
		int year = Integer.parseInt(anho);

		generate(authorized, month, year, tipo, cccs, os);
	}

	public static void generate(int autorizado, Month mes, int anho,
			String tipo, String cccs[], OutputStream os) throws JAXBException {

		SolicitudCalculosBuilder builder = 
				new SolicitudCalculosBuilder()
		.setAutorizado(autorizado);

		for ( String cCC: cccs ) {
			builder
			.setCCC(cCC)
			.setTipo(tipo)
			.setMesDesde(mes)
			.setAnhoDesde(anho)
			.setMesHasta(mes)
			.setAnhoHasta(anho)
			.setMesPresentacion(mes)
			.setAnhoPresentacion(anho)
			.addLiquidacion()
			;
		}
		SolicitudCalculos solicitud = builder.createSolicitudCalculos();

		Utils.marshal(solicitud, os);
	}
	
}
