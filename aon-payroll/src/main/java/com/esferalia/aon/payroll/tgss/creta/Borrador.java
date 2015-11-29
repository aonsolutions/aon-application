package com.esferalia.aon.payroll.tgss.creta;

import java.io.OutputStream;
import java.time.Month;
import java.util.Arrays;
import java.util.Calendar;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller.Listener;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.stream.XMLStreamWriter;

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
	public static Option getTypeOption(String tipo) {
		return OptionBuilder.withArgName("name").hasArg().withLongOpt("type")
				.withDescription("Type of Settlement : \r\n"
						+ "L00 (normal)\r\n" + "L03 (complement.)\r\n"
						+ "Built-in default (" + tipo + ")  ")
				.create("t");
	}

	@SuppressWarnings("static-access")
	public static Option getCCCOption() {
		return OptionBuilder.withArgName("name").hasArg().isRequired()
				.withLongOpt("ccc").withDescription("Add the Ccc to the Cccs.")
				.create("c");
	}

	@SuppressWarnings("static-access")
	public static Option getYearOption(Object anho) {
		return OptionBuilder.withArgName("year").hasArg().withLongOpt("year")
				.withDescription("Year built-in default (" + anho + ")")
				.create("y");
	}

	@SuppressWarnings("static-access")
	public static Option getMonthOption(Object mes) {
		return OptionBuilder.withArgName("month").hasArg().withLongOpt("month")
				.withDescription("Month built-in default (" + mes + ")")
				.create("m");
	}

	@SuppressWarnings("static-access")
	public static Option getAuthorizedOption() {
		return OptionBuilder.withArgName("name").hasArg().isRequired()
				.withLongOpt("authorized")
				.withDescription("Number for use 'Sistem RED'. ").create("a");
	}

	@SuppressWarnings("static-access")
	public static Option getSkipPrevBasesOption() {
		return OptionBuilder.withLongOpt("skip-prev-bases")
				.withDescription("Skip previous bases.").create("b");
	}

	public static void main(String[] args)
			throws JAXBException, DatatypeConfigurationException {
		String tipo = "L00";
		String anho = Integer
				.toString(Calendar.getInstance().get(Calendar.YEAR));
		String mes = Integer
				.toString(Calendar.getInstance().get(Calendar.MONTH) + 1);

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

			mes = cmd.getOptionValue(month.getLongOpt(), mes);
			anho = cmd.getOptionValue(year.getLongOpt(), anho);
			tipo = cmd.getOptionValue(type.getLongOpt(), tipo);
			String cccs[] = cmd.getOptionValues(ccc.getLongOpt());
			String autorizado = cmd.getOptionValue(authorized.getLongOpt());

			boolean aceptarBasesAnteriores = !cmd
					.hasOption(skipPrevBases.getLongOpt());

			generate(autorizado, mes, anho, tipo, aceptarBasesAnteriores, cccs,
					System.out);

		} catch (ParseException e) {
			// oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + e.getMessage());
			// automatically generate the help statement
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("calc", options);

		}

	}

	public static void generate(String autorizado, String mes, String anho,
			String tipo, boolean aceptarBasesAnteriores, String cccs[],
			OutputStream os) throws JAXBException {

		int authorized = Integer.parseInt(autorizado);
		Month month = Month.of(Integer.parseInt(mes));
		int year = Integer.parseInt(anho);

		generate(authorized, month, year, tipo, aceptarBasesAnteriores, cccs,
				os);
	}

	public static void generate(int autorizado, Month mes, int anho,
			String tipo, boolean aceptarBasesAnteriores, String cccs[],
			OutputStream os) throws JAXBException {

		SolicitudBorradorBuilder builder = new SolicitudBorradorBuilder()
				.setAutorizado(autorizado);

		//@formatter:off
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
		//@formatter:on

		SolicitudBorrador solicitudBorrador = builder.createSolicitudBorrador();

		Utils.marshal(solicitudBorrador, os);
	}

	public static void generate(String autorizado, String meses[],
			String anhos[], String tipos[], Boolean aceptarBasesAnteriores[],
			String cccs[], OutputStream os, Listener ...listeners) throws JAXBException {

		int authorized = Integer.parseInt(autorizado);
		Integer years[] = Arrays.stream(anhos)
				.map(anho -> Integer.parseInt(anho)).toArray(Integer[]::new);
		Month months[] = Arrays.stream(meses)
				.map(mes -> Month.of(Integer.parseInt(mes)))
				.toArray(Month[]::new); 

		//@formatter:off
		generate(authorized, 
				months, 
				years, 
				tipos, 
				aceptarBasesAnteriores, 
				cccs,
				os,
				listeners);
		//@formatter:on

	}

	public static void generate(String autorizado, String meses[],
			String anhos[], String tipos[], Boolean aceptarBasesAnteriores[],
			String cccs[], XMLStreamWriter writer, Listener ...listeners) throws JAXBException {

		int authorized = Integer.parseInt(autorizado);
		Integer years[] = Arrays.stream(anhos)
				.map(anho -> Integer.parseInt(anho)).toArray(Integer[]::new);
		Month months[] = Arrays.stream(meses)
				.map(mes -> Month.of(Integer.parseInt(mes)))
				.toArray(Month[]::new);

		//@formatter:off
		generate(authorized, 
				months, 
				years, 
				tipos, 
				aceptarBasesAnteriores, 
				cccs,
				writer,
				listeners);
		//@formatter:on

	}

	public static void generate(int autorizado, Month meses[], Integer anhos[],
			String tipos[], Boolean aceptarBasesAnteriores[], String cccs[],
			OutputStream os, Listener ...listeners) throws JAXBException {

		SolicitudBorradorBuilder builder = new SolicitudBorradorBuilder()
				.setAutorizado(autorizado);

		//@formatter:off
		for (int i = 0; i < cccs.length; i++) {
			builder
			.setCCC(cccs[i])
			.setTipo(tipos[i])
			.setAceptarBasesAnteriores(aceptarBasesAnteriores[i])
			.setMesDesde(meses[i])
			.setAnhoDesde(anhos[i])
			.setMesHasta(meses[i])
			.setAnhoHasta(anhos[i])
			.addLiquidacion()
			;
		}
		//@formatter:on

		SolicitudBorrador solicitudBorrador = builder.createSolicitudBorrador();

		Utils.marshal(solicitudBorrador, os, listeners);

	}

	public static void generate(int autorizado, Month meses[], Integer anhos[],
			String tipos[], Boolean aceptarBasesAnteriores[], String cccs[],
			XMLStreamWriter writer, Listener ...listeners) throws JAXBException {

		SolicitudBorradorBuilder builder = new SolicitudBorradorBuilder()
				.setAutorizado(autorizado);

		//@formatter:off
		for (int i = 0; i < cccs.length; i++) {
			builder
			.setCCC(cccs[i])
			.setTipo(tipos[i])
			.setAceptarBasesAnteriores(aceptarBasesAnteriores[i])
			.setMesDesde(meses[i])
			.setAnhoDesde(anhos[i])
			.setMesHasta(meses[i])
			.setAnhoHasta(anhos[i])
			.addLiquidacion()
			;
		}
		//@formatter:on

		SolicitudBorrador solicitudBorrador = builder.createSolicitudBorrador();

		Utils.marshal(solicitudBorrador, writer, listeners);

	}
}
