package com.esferalia.aon.payroll.tgss.creta;

import static net.aonsolutions.core.tgss.creta.jaxb.dba.AccionDatosBancariosBuilder.valueOfTipo;

import java.io.OutputStream;

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

import net.aonsolutions.core.tgss.creta.jaxb.Utils;
import net.aonsolutions.core.tgss.creta.jaxb.dba.AccionDatosBancariosBuilder;
import net.aonsolutions.core.tgss.creta.jaxb.dba.AccionDatosBancariosBuilder.TipoAccion;
import net.aonsolutions.core.tgss.creta.jaxb.dba.AccionDatosBancariosBuilder.TipoDocumento;
import net.aonsolutions.core.tgss.creta.jaxb.dba.AccionDatosBancariosBuilder.TipoMoviento;
import net.aonsolutions.core.tgss.creta.jaxb.dba.ComunicacionDatosBancarios;
import net.aonsolutions.core.tgss.creta.jaxb.dba.ComunicacionDatosBancariosBuilder;

public class DBA {

	public static void main(String[] args) throws JAXBException,
			DatatypeConfigurationException {

		//@formatter:off
		Option authOption =  Borrador.getAuthorizedOption();
		Option cccOption =  Borrador.getCCCOption();
		
		@SuppressWarnings("static-access")
		Option movementOption = OptionBuilder
				.hasArg()
				.isRequired()
				.withArgName("tipo")
				.withLongOpt("movement")
				.withDescription("Modalidad de pago sobre la que se solicita la acción:\r\n" + 
				"C: Liquidaciones deudoras.\r\n" +
				"S: Liquidaciones acreedoras.\r\n" +
				"A: Liquidaciones deudoras y acreedoras."
				)
				.create("m")
				;
		@SuppressWarnings("static-access")
		Option actionOption = OptionBuilder
				.hasArg()
				.isRequired()
				.withArgName("tipo")
				.withDescription("Acción que se quiere realizar con los datos bancarios.\r\n" + 
				"1: Alta de nuevos datos o modificación de los datos bancarios existentes.\r\n" +
				"2: Eliminación de los datos bancarios existentes."
				) 
				.withLongOpt("action")
				.create("A")
				;
		@SuppressWarnings("static-access")
		Option ibanOption = OptionBuilder
				.hasArg()
				.isRequired()
				.withArgName("clave")
				.withLongOpt("iban")
				.withDescription("Código de identificación bancaria asignado por la Entidad Financiera a la cuenta cliente." 
				) 
				.create("i")
				;
		@SuppressWarnings("static-access")
		Option nameOption = OptionBuilder
				.hasArg()
				.isRequired()
				.withArgName("nombre")
				.withDescription("Nombre del titular") 
				.withLongOpt("name")
				.create("n")
				;
		@SuppressWarnings("static-access")
		Option docNumOption = OptionBuilder
				.hasArg()
				.isRequired()
				.withArgName("clave")
				.withLongOpt("document")
				.withDescription("Identificador persona física.") 
				.create("d")
				;
		@SuppressWarnings("static-access")
		Option docTypeOption = OptionBuilder
				.hasArg()
				.isRequired()
				.withArgName("tipo")
				.withLongOpt("type")
				.withDescription("Identificador de tipo de documento :\r\n"+
				"1: DNI.\r\n" +
				"6: NIE."
				) 
				.create("t")
				;

		Options options = new Options()
		.addOption(authOption)
		.addOption(cccOption)
		.addOption(movementOption)
		.addOption(actionOption)
		.addOption(ibanOption)
		.addOption(nameOption)
		.addOption(docNumOption)
		.addOption(docTypeOption)
		
		;
		//@formatter:on

		// create the parser
		CommandLineParser parser = new GnuParser();

		try {

			// parse the command line arguments
			CommandLine cmd = parser.parse(options, args);

			String autorizado = cmd.getOptionValue(authOption.getLongOpt());
			
			String cccs [] = cmd.getOptionValues(cccOption.getLongOpt());
			
			String tipoMoviento = cmd.getOptionValue(movementOption.getLongOpt());
			String tipoAccion = cmd.getOptionValue(actionOption.getLongOpt());
			String titular = cmd.getOptionValue(nameOption.getLongOpt());
			String iban = cmd.getOptionValue(ibanOption.getLongOpt());
			String documento = cmd.getOptionValue(docNumOption.getLongOpt());
			String tipoDocumento = cmd.getOptionValue(docTypeOption.getLongOpt());

			generate(autorizado
					, cccs
					,tipoMoviento
					,tipoAccion
					,iban
					,titular
					,documento
					,tipoDocumento
					, System.out);

		} catch (ParseException e) {
			// oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + e.getMessage());
			// automatically generate the help statement
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("calc", options);

		}

	}

	public static void generate(
			String autorizado,  
			String cccs [], 
			String tipoMoviento, 
			String tipoAccion, 
			String iban,
			String titular,
			String documento,
			String tipoDocumento,
			OutputStream os) throws JAXBException {

		int authorized = Integer.parseInt(autorizado);
		
		

		generate(authorized, 
				cccs, 
				valueOfTipo(TipoMoviento.class , tipoMoviento), 
				valueOfTipo(TipoAccion.class , tipoAccion), 
				iban, 
				titular, 
				documento, 
				valueOfTipo(TipoDocumento.class , tipoDocumento), 
				os);
	}

	//@formatter:off
	public static void generate(
			int autorizado, 
			String cccs [], 
			TipoMoviento tipoMoviento, 
			TipoAccion tipoAccion, 
			String iban,
			String titular,
			String documento,
			TipoDocumento tipoDocumento,
			OutputStream os) throws JAXBException {
		
		ComunicacionDatosBancariosBuilder builder = new 
		ComunicacionDatosBancariosBuilder()
		.setAutorizado(autorizado);
		
		for ( String ccc: cccs )
			builder.addAccionDatosBancarios( 
			new AccionDatosBancariosBuilder()
			.setCCC(ccc)
			.setIban(iban)
			.setTipoMoviento(tipoMoviento)
			.setTipoAccion(tipoAccion)
			.setNombreTitular(titular)
			.setNumeroDocumento(documento)
			.setTipoDocumento(tipoDocumento)
			.createAccionDatosBancarios());

		ComunicacionDatosBancarios comunicacionDatosBancarios  = 
				builder
				.createComunicacionDatosBancarios();

		Utils.marshal(comunicacionDatosBancarios, os);
	}
	//@formatter:on
}
