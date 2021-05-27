package com.esferalia.aon.payroll.tgss.creta;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.stream.Stream;

import javax.xml.bind.JAXBException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary;

import net.aonsolutions.core.tgss.creta.jaxb.Utils;
import net.aonsolutions.core.tgss.creta.jaxb.rnt.Liquidacion;
import net.aonsolutions.core.tgss.creta.jaxb.rnt.RelacionNominalTrabajadores;

public class RNT {
	
	
	public static interface RNTCallback {
		
	}
	
	public static void check(Connection connection, RelacionNominalTrabajadores rnt, RNTCallback ...cbs) {
		AONContext ctx = new AONContext(connection);
		
		Liquidacion liquidacion = rnt.getLiquidacion();
		
		String ccc = Utils.toString(liquidacion.getCcc());

		Date startDate = Utils.toCalendar(liquidacion.getPeriodoDesde()).getTime();
		Date endDate = Utils.toCalendar(liquidacion.getPeriodoHasta()).getTime();
		
		Stream<Salary> salaries = AON.getSalaryData(ctx, 
				props->props.getCCCProperty().eq(ccc)
					.and(props.getStartDateProperty().ge(startDate))
					.and(props.getEndDateProperty().le(endDate))
		);
		
		
	}
	
	// ------------------------------------------------------------------------
	
	
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

			InputStream is = cmd.hasOption(rntFile.getLongOpt()) ? 
					System.in
					: new FileInputStream(cmd.getOptionValue(rntFile.getLongOpt()));


			RelacionNominalTrabajadores rnt = 
					Utils.unmarshal(RelacionNominalTrabajadores.class, is);
			
			check(connection, rnt);
			
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
