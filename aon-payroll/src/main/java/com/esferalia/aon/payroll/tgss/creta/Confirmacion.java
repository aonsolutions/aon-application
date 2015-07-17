package com.esferalia.aon.payroll.tgss.creta;

import static com.esferalia.aon.payroll.tgss.creta.Borrador.getYearOption;

import java.time.Month;
import java.util.Calendar;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import net.aonsolutions.tgss.creta.jaxb.Utils;
import net.aonsolutions.tgss.creta.jaxb.solicitud.confirmacion.SolicitudConfirmacion;
import net.aonsolutions.tgss.creta.jaxb.solicitud.confirmacion.SolicitudConfirmacionBuilder;
import net.aonsolutions.tgss.creta.jaxb.solicitud.trabajadorestramos.SolicitudTrabajadoresTramos;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Confirmacion {

	
	public static void main(String[] args) throws JAXBException, DatatypeConfigurationException {
		String tipo = "L00";
		int anho = Calendar.getInstance().get(Calendar.YEAR);
		Month mes = Month.of(Calendar.getInstance().get(Calendar.MONTH)+1);

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
			
			String cccs [] = cmd.getOptionValues(ccc.getLongOpt());
			
			if ( cmd.hasOption(month.getLongOpt()))
				mes = Month.of(Integer.parseInt(cmd.getOptionValue(month.getLongOpt())));
			if ( cmd.hasOption(year.getLongOpt()))
					anho = Integer.parseInt(cmd.getOptionValue(year.getLongOpt()));
			if ( cmd.hasOption(type.getLongOpt())) 
				tipo = cmd.getOptionValue(type.getLongOpt());
			
			int autorizado = Integer.parseInt(cmd.getOptionValue(authorized.getLongOpt()));
			
			
			SolicitudConfirmacionBuilder builder = 
					new SolicitudConfirmacionBuilder()
			.setAutorizado(autorizado);

			for ( String cCC: cccs ) {
				builder
				.setCCC(cCC)
				.setTipo("L00")
				.setMesDesde(mes)
				.setAnhoDesde(anho)
				.setMesHasta(mes)
				.setAnhoHasta(anho)
				.addLiquidacion()
				;
			}
			SolicitudConfirmacion solicitudConfirmacion = builder.createSolicitudConfirmacion();

			Utils.marshal(solicitudConfirmacion, System.out);
			
		} catch (ParseException e) {
			// oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + e.getMessage());
			// automatically generate the help statement
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("calc", options);

		}

		
	}
}
