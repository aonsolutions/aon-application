package com.esferalia.aon.payroll.tgss.creta;

import java.io.OutputStream;
import java.time.Month;
import java.util.Arrays;
import java.util.Calendar;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller.Listener;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.stream.XMLStreamWriter;

import net.aonsolutions.core.tgss.creta.jaxb.Utils;
import net.aonsolutions.core.tgss.creta.jaxb.solicitud.borrador.SolicitudBorrador;
import net.aonsolutions.core.tgss.creta.jaxb.solicitud.borrador.SolicitudBorradorBuilder;

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
	public static Option getFromYearOption(Object anho) {
		return OptionBuilder
				.withArgName("year")
				.hasArg()
				.withLongOpt("from-year")
				.withDescription("From Year built-in default (" + anho + ")")
				.create();
	}

	@SuppressWarnings("static-access")
	public static Option getFromMonthOption(Object mes) {
		return OptionBuilder
				.withArgName("month")
				.hasArg().withLongOpt("from-month")
				.withDescription("From Month built-in default (" + mes + ")")
				.create();
	}

	@SuppressWarnings("static-access")
	public static Option getToYearOption(Object anho) {
		return OptionBuilder
				.withArgName("year")
				.hasArg()
				.withLongOpt("to-year")
				.withDescription("To Year built-in default (" + anho + ")")
				.create();
	}

	@SuppressWarnings("static-access")
	public static Option getToMonthOption(Object mes) {
		return OptionBuilder
				.withArgName("month")
				.hasArg().withLongOpt("to-month")
				.withDescription("To Month built-in default (" + mes + ")")
				.create();
	}

	@SuppressWarnings("static-access")
	public static Option getCtrlYearOption(Object anho) {
		return OptionBuilder
				.withArgName("year")
				.hasArg()
				.withLongOpt("ctrl-year")
				.withDescription("CTRL Year built-in default (" + anho + ")")
				.create();
	}

	@SuppressWarnings("static-access")
	public static Option getCtrlMonthOption(Object mes) {
		return OptionBuilder
				.withArgName("month")
				.hasArg().withLongOpt("ctrl-month")
				.withDescription("CTRL Month built-in default (" + mes + ")")
				.create();
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

	@SuppressWarnings("static-access")
	public static Option getRequestSendRNTOption() {
		return OptionBuilder.withLongOpt("request-send-rnt")
				.withDescription("Request send RNT.").create("r");
	}

	public static void main(String[] args)
			throws JAXBException, DatatypeConfigurationException {
		String tipo = "L00";
		String desdeAnho = Integer
				.toString(Calendar.getInstance().get(Calendar.YEAR));
		String desdeMes = Integer
				.toString(Calendar.getInstance().get(Calendar.MONTH) + 1);
		String hastaAnho = Integer
				.toString(Calendar.getInstance().get(Calendar.YEAR));
		String hastaMes = Integer
				.toString(Calendar.getInstance().get(Calendar.MONTH) + 1);

		//@formatter:off
		Option fromYear =  getFromYearOption(desdeAnho);
		Option fromMonth =  getFromMonthOption(desdeMes);
		Option toYear =  getToYearOption(hastaAnho);
		Option toMonth =  getToMonthOption(hastaMes);
		Option ccc =  getCCCOption();
		Option authorized =  getAuthorizedOption();
		Option type =  getTypeOption(tipo);
		Option skipPrevBases =  getSkipPrevBasesOption();
		Option requestSendRNT =  getRequestSendRNTOption();
		
		Options options = new Options()
		.addOption(authorized)
		.addOption(fromYear)
		.addOption(fromMonth)
		.addOption(toYear)
		.addOption(toMonth)
		.addOption(ccc)
		.addOption(type)
		.addOption(skipPrevBases)
		.addOption(requestSendRNT)
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
			String cccs[] = cmd.getOptionValues(ccc.getLongOpt());
			String autorizado = cmd.getOptionValue(authorized.getLongOpt());

			boolean aceptarBasesAnteriores = !cmd
					.hasOption(skipPrevBases.getLongOpt());
			boolean solicitudRecepcionRNT = !cmd
					.hasOption(skipPrevBases.getLongOpt());

			generate(autorizado, desdeMes, desdeAnho, hastaMes, hastaAnho, tipo, aceptarBasesAnteriores, solicitudRecepcionRNT, cccs,
					System.out);

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
			String tipo, boolean aceptarBasesAnteriores, boolean solicitudRecepcionRNT, String cccs[],
			OutputStream os) throws JAXBException {

		int authorized = Integer.parseInt(autorizado);
		Month fromMonth = Month.of(Integer.parseInt(desdeMes));
		int fromYear = Integer.parseInt(desdeAnho);
		Month toMonth = Month.of(Integer.parseInt(hastaMes));
		int toYear = Integer.parseInt(hastaAnho);

		generate(authorized, fromMonth, fromYear, toMonth, toYear,tipo, aceptarBasesAnteriores, solicitudRecepcionRNT, cccs, os);
	}

	public static void generate(int autorizado, Month desdeMes, int desdeAnho, Month hastaMes, int hastaAnho,
			String tipo, boolean aceptarBasesAnteriores, boolean solicitudRecepcionRNT, String cccs[],
			OutputStream os) throws JAXBException {

		SolicitudBorradorBuilder builder = new SolicitudBorradorBuilder()
				.setAutorizado(autorizado);

		//@formatter:off
		for ( String cCC: cccs ) {
			builder
			.setCCC(cCC)
			.setTipo(tipo)
			.setSolicitudRecepcionRNT(solicitudRecepcionRNT)
			.setAceptarBasesAnteriores(aceptarBasesAnteriores)
			.setMesDesde(desdeMes)
			.setAnhoDesde(desdeAnho)
			.setMesHasta(hastaMes)
			.setAnhoHasta(hastaAnho)
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

	public static void generate(String autorizado, 
			Month mesControl, Integer anhoControl, 
			String meses[],String anhos[], String tipos[], Boolean aceptarBasesAnteriores[],
			String cccs[], XMLStreamWriter writer, Listener ...listeners) throws JAXBException {

		int authorized = Integer.parseInt(autorizado);
		Integer years[] = Arrays.stream(anhos)
				.map(anho -> Integer.parseInt(anho)).toArray(Integer[]::new);
		Month months[] = Arrays.stream(meses)
				.map(mes -> Month.of(Integer.parseInt(mes)))
				.toArray(Month[]::new);

		//@formatter:off
		generate(authorized, 
				mesControl,
				anhoControl,
				months, 
				years, 
				tipos, 
				aceptarBasesAnteriores, 
				cccs,
				writer,
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

	public static void generate(int autorizado, Month mesControl, Integer anhoControl, Month meses[], Integer anhos[],
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
			.setMesControl(mesControl)
			.setAnhoControl(anhoControl)
			.addLiquidacion()
			;
		}
		//@formatter:on

		SolicitudBorrador solicitudBorrador = builder.createSolicitudBorrador();

		Utils.marshal(solicitudBorrador, writer, listeners);

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
