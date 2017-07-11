package com.esferalia.aon.payroll.tgss.creta;

import static com.esferalia.aon.watson.util.AonStringUtils.isNoneEmpty;
import static com.esferalia.aon.watson.util.AonStringUtils.isNotEmpty;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
import com.esferalia.aon.occam.api.model.Salary.Cost;
import com.esferalia.aon.occam.api.model.Salary.Deduction;
import com.esferalia.aon.occam.api.model.type.DeductionType;

import net.aonsolutions.core.tgss.creta.jaxb.CtaCot;
import net.aonsolutions.core.tgss.creta.jaxb.Utils;
import net.aonsolutions.core.tgss.creta.jaxb.dcl.LineaDCL;

public class DCL {

	private static final String OTRAS_COTIZACIONES = "OTRAS COTIZACIONES";
	private static final String LIQUIDO_DE_ACCIDENTES_DE_TRABAJO = "LIQUIDO DE ACCIDENTES DE TRABAJO";
	private static final String LIQUIDO_DE_TOTALES = "LIQUIDO DE TOTALES";
	private static final String IMS_DE_ACCIDENTES_DE_TRABAJO = "IMS DE ACCIDENTES DE TRABAJO";
	private static final String IT_DE_ACCIDENTES_DE_TRABAJO = "IT DE ACCIDENTES DE TRABAJO";
	private static final String ANSI_RESET = "\u001B[0m";
	private static final String ANSI_BOLD = "\u001B[1m";
	private static final String ANSI_BLACK = "\u001B[30m";
	private static final String ANSI_RED = "\u001B[31m";
	private static final String ANSI_GREEN = "\u001B[32m";
	private static final String ANSI_YELLOW = "\u001B[33m";
	private static final String ANSI_BLUE = "\u001B[34m";
	private static final String ANSI_PURPLE = "\u001B[35m";
	private static final String ANSI_CYAN = "\u001B[36m";
	private static final String ANSI_WHITE = "\u001B[37m";

	private static final String CONTINGENCIAS_COMUNES = "CONTINGENCIAS COMUNES";



	// ------------------------------------------------------------------------

	public static class LineaSalary {

		long base;
		long importe;
		
		
		public long getBase() {
			return base;
		}
		
		public long getImporte() {
			return importe;
		}

	}

	public static void check(Connection connection, net.aonsolutions.core.tgss.creta.jaxb.dcl.DCL dcl, BiConsumer<LineaDCL, LineaSalary> biConsumer ) {

		Map<String, LineaSalary> lineasSalary = new HashMap<String, LineaSalary>();
		lineasSalary.put(CONTINGENCIAS_COMUNES, new LineaSalary());
		lineasSalary.put(IT_DE_ACCIDENTES_DE_TRABAJO, new LineaSalary());
		lineasSalary.put(IMS_DE_ACCIDENTES_DE_TRABAJO, new LineaSalary());
		lineasSalary.put(OTRAS_COTIZACIONES, new LineaSalary());

		lineasSalary.put(LIQUIDO_DE_TOTALES, new LineaSalary());

		CtaCot ctaCot = dcl.getLiquidacion().getCcc();
		String ccc = String.format("%s%s", ctaCot.getProvincia(), ctaCot.getNumero());

		Calendar desde = Utils.toCalendar(dcl.getLiquidacion().getPeriodoDesde());
		desde.set(Calendar.DAY_OF_MONTH, desde.getActualMinimum(Calendar.DAY_OF_MONTH));
		Date startDate = desde.getTime();

		Calendar hasta = Utils.toCalendar(dcl.getLiquidacion().getPeriodoHasta());
		hasta.set(Calendar.DAY_OF_MONTH, hasta.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date endDate = hasta.getTime();

		LineaSalary cgcLineaSalary = lineasSalary.get(CONTINGENCIAS_COMUNES);
		LineaSalary itLineaSalary = lineasSalary.get(IT_DE_ACCIDENTES_DE_TRABAJO);
		LineaSalary imsLineaSalary = lineasSalary.get(IMS_DE_ACCIDENTES_DE_TRABAJO);
		LineaSalary otherLineaSalary = lineasSalary.get(OTRAS_COTIZACIONES);


		LineaSalary bonusLineaSalary = new LineaSalary();

		AONContext ctx = new AONContext(connection);
		AON.getSalaries(ctx, props -> props.getCCCProperty().eq(ccc)
				.and(props.getStartDateProperty().le(endDate))
				.and(props.getEndDateProperty().between(startDate, endDate)))
		.forEach(salary -> {
					
					cgcLineaSalary.base += Math.round(salary.getCommonContingenciesBase() * 100.00);
					cgcLineaSalary.importe += Math.round(getImporte(salary, DeductionType.COMMON_CONTINGENCY));

					itLineaSalary.base += Math.round(salary.getProfessionalContingenciesBase() * 100.00);
					itLineaSalary.importe += Math.round(getImporte(salary, DeductionType.IT));

					imsLineaSalary.base += Math.round(salary.getProfessionalContingenciesBase() * 100.00);
					imsLineaSalary.importe += Math.round(getImporte(salary, DeductionType.IMS));

					otherLineaSalary.base += Math.round(salary.getCommonContingenciesBase() * 100.00);
					otherLineaSalary.importe += Math.round(getImporte(salary, DeductionType.JOB_TRAINING));
					otherLineaSalary.importe += Math.round(getImporte(salary,DeductionType.UNEMPLOYMENT));
					otherLineaSalary.importe += Math.round(getImporte(salary, DeductionType.FOGASA));
					
					bonusLineaSalary.importe += Math.round(getBonificaciones(salary));
					
		});
		;

		LineaSalary liquidTotalLineaSalary = lineasSalary.get(LIQUIDO_DE_TOTALES);
		liquidTotalLineaSalary.base = 0;
		liquidTotalLineaSalary.importe = 
				cgcLineaSalary.importe + 
				itLineaSalary.importe + 
				imsLineaSalary.importe + 
				otherLineaSalary.importe ; 
		liquidTotalLineaSalary.importe -= bonusLineaSalary.importe; 

		for (LineaDCL lineaDCL : dcl.getLiquidacion().getDatosCuerpoDCL().getLineasDCL().getLineaDCL())
			if (lineasSalary.containsKey(lineaDCL.getDescripcionLDCL()))
				biConsumer.accept(lineaDCL, lineasSalary.get(lineaDCL.getDescripcionLDCL()));
	}

	// ------------------------------------------------------------------------

	public static void main(String[] args) throws ClassNotFoundException, SQLException, JAXBException, IOException {

		// @formatter:off
		Option hostName = Bases.getHostNameOption();
		Option user = Bases.getDbUserOption();
		Option password = Bases.getDbPasswordOption();
		Option database = Bases.getDatabaseOption();
		Option pretty = Bases.getPrettyOption();

		@SuppressWarnings("static-access")
		Option delta = OptionBuilder.withArgName("delta").hasArg().withType(Integer.class).withLongOpt("delta")
				.withDescription("Asserts that two values are equal concerning a delta.").create();

		Options options = new Options().addOption(hostName).addOption(user).addOption(password).addOption(database)
				.addOption(pretty).addOption(delta);
				// @formatter:on

		// create the parser
		CommandLineParser parser = new GnuParser();

		try {
			// parse the command line arguments
			CommandLine cmd = parser.parse(options, args);

			Class.forName(com.mysql.jdbc.Driver.class.getName());

			Connection connection = DriverManager.getConnection(
					String.format("jdbc:mysql://%s:%d/%s", cmd.getOptionValue(hostName.getLongOpt(), "127.0.0.1"), 3306,
							cmd.getOptionValue(database.getLongOpt())),
					cmd.getOptionValue(user.getLongOpt()), cmd.getOptionValue(password.getLongOpt()));

			InputStream is = cmd.getArgList().isEmpty() ? System.in : new FileInputStream(cmd.getArgs()[0]);

			net.aonsolutions.core.tgss.creta.jaxb.dcl.DCL dcl = Utils
					.unmarshal(net.aonsolutions.core.tgss.creta.jaxb.dcl.DCL.class, is);

			String ansiError = cmd.hasOption(pretty.getLongOpt()) ? ANSI_RED : "";
			String ansiSuccess = cmd.hasOption(pretty.getLongOpt()) ? ANSI_GREEN : "";
			String ansiWarning = cmd.hasOption(pretty.getLongOpt()) ? ANSI_YELLOW : "";

			int deltaValue = Integer.parseInt(cmd.getOptionValue(delta.getLongOpt(), "0"));

			check(connection, dcl, (lineaDCL,lineaSalary)-> checkEquals(lineaDCL, lineaSalary, deltaValue, ansiSuccess, ansiError, ansiWarning)) ;

			dcl.getLiquidacion();

			is.close();

		} catch (ParseException e) {
			// oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + e.getMessage());
			// automatically generate the help statement
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("cret@ [OPTION]... [FILE]", options);

		}
	}

	// ------------------------------------------------------------------------
	
	private static boolean checkEquals(LineaDCL lineaDCL, LineaSalary lineaSalary, int delta, String ansiSuccess,
			String ansiError, String ansiWarn) {
		int baseSalary = (int) Math.round(lineaSalary.base);
		int importeSalary = (int) Math.round(lineaSalary.importe);
		int baseLDCL = Integer.parseInt(lineaDCL.getBaseLDCL());
		int importeLDCL = Integer.parseInt(lineaDCL.getImporteLDCL());

		if (Math.abs(baseLDCL - baseSalary) <= delta) {
			if (Math.abs(importeLDCL - importeSalary) <= delta) {
				boolean baseEquals = baseLDCL == baseSalary;
				boolean importeEquals = importeLDCL == importeSalary;
				if (baseEquals && importeEquals)
					System.out.printf("SUCCESS: %s%s%s , Base : %s%d%s, Importe : %s%d%s \r\n", ansiSuccess,
							lineaDCL.getDescripcionLDCL(), isNotEmpty(ansiSuccess) ? ANSI_RESET : "", ansiSuccess,
							baseLDCL, isNotEmpty(ansiSuccess) ? ANSI_RESET : "", ansiSuccess, importeLDCL,
							isNotEmpty(ansiSuccess) ? ANSI_RESET : "");
				else if (baseEquals)
					System.err.printf("WARNING: %s%s%s, Base : %s%d%s, Importe : %s%d (%d)%s\r\n", ansiWarn,
							lineaDCL.getDescripcionLDCL(), isNotEmpty(ansiError) ? ANSI_RESET : "", ansiSuccess,
							baseLDCL, isNotEmpty(ansiSuccess) ? ANSI_RESET : "", ansiWarn, importeLDCL, importeSalary,
							isNotEmpty(ansiError) ? ANSI_RESET : "");
				else if (importeEquals)
					System.err.printf("WARNING: %s%s%s , Base : %s%d (%d)%s, Importe : %s%d%s \r\n", ansiWarn,
							lineaDCL.getDescripcionLDCL(), isNotEmpty(ansiError) ? ANSI_RESET : "", ansiWarn, baseLDCL,
							baseSalary, isNotEmpty(ansiError) ? ANSI_RESET : "", ansiSuccess, importeLDCL,
							isNotEmpty(ansiSuccess) ? ANSI_RESET : "");
				else
					System.err.printf("WARNING: %s%s%s , Base : %s%d (%d)%s, Importe : %s%d (%d)%s\r\n", ansiWarn,
							lineaDCL.getDescripcionLDCL(), isNoneEmpty(ansiError) ? ANSI_RESET : "", ansiWarn, baseLDCL,
							baseSalary, isNoneEmpty(ansiError) ? ANSI_RESET : "", ansiWarn, importeLDCL, importeSalary,
							isNoneEmpty(ansiError) ? ANSI_RESET : "");

				return true;
			} else {
				System.err.printf("ERROR: %s%s%s, Base : %s%d%s, Importe : %s%d (%d)%s\r\n", ansiError,
						lineaDCL.getDescripcionLDCL(), isNotEmpty(ansiError) ? ANSI_RESET : "", ansiSuccess, baseLDCL,
						isNotEmpty(ansiSuccess) ? ANSI_RESET : "", ansiError, importeLDCL, importeSalary,
						isNotEmpty(ansiError) ? ANSI_RESET : "");
				return false;
			}

		} else {
			if (Math.abs(importeLDCL - importeSalary) <= delta) {
				System.err.printf("ERROR: %s%s%s , Base : %s%d (%d)%s, Importe : %s%d%s \r\n", ansiError,
						lineaDCL.getDescripcionLDCL(), isNotEmpty(ansiError) ? ANSI_RESET : "", ansiError, baseLDCL,
						baseSalary, isNotEmpty(ansiError) ? ANSI_RESET : "", ansiSuccess, importeLDCL,
						isNotEmpty(ansiSuccess) ? ANSI_RESET : "");
				return false;
			} else {
				System.err.printf("ERROR: %s%s%s , Base : %s%d (%d)%s, Importe : %s%d (%d)%s\r\n", ansiError,
						lineaDCL.getDescripcionLDCL(), isNoneEmpty(ansiError) ? ANSI_RESET : "", ansiError, baseLDCL,
						baseSalary, isNoneEmpty(ansiError) ? ANSI_RESET : "", ansiError, importeLDCL, importeSalary,
						isNoneEmpty(ansiError) ? ANSI_RESET : "");
				return false;
			}
		}
	}

	public static boolean checkEquals(LineaDCL lineaDCL, LineaSalary lineaSalary) {
		int baseSalary = (int) Math.round(lineaSalary.base);
		int importeSalary = (int) Math.round(lineaSalary.importe);
		int baseLDCL = Integer.parseInt(lineaDCL.getBaseLDCL());
		int importeLDCL = Integer.parseInt(lineaDCL.getImporteLDCL());
		
		return ( baseLDCL == baseSalary && importeLDCL == importeSalary );
		
	}

	private static boolean isCotizacion(Deduction deduction) {
		if (!(deduction.isJobTraining() || deduction.isUnemployment() || deduction.isCommonContingency()
				|| deduction.isProfesionalContingency()))
			System.out
					.println(
							deduction.getDescription() + " " + deduction.getAmount() + "("
									+ (deduction.isJobTraining() || deduction.isUnemployment()
											|| deduction.isCommonContingency() || deduction.isProfesionalContingency())
							+ ")");

		return deduction.isJobTraining() || deduction.isUnemployment() || deduction.isCommonContingency()
				|| deduction.isProfesionalContingency();
	}

	private static double getCotizaciones(Salary salary) {
		double importe = 0.00;

		importe = salary.getDeductions().stream().filter(DCL::isCotizacion)
				.collect(Collectors.summingDouble(deduction -> deduction.getAmount() * 100.00));
		importe += salary.getCosts().stream().collect(Collectors.summingDouble(cost -> cost.getAmount() * 100.00));

		return importe;
	}

	private static double getBonificaciones(Salary salary) {
		double importe = 0.00;

		importe = salary.getBonuses().stream().collect(Collectors.summingDouble(bonus -> bonus.getAmount() * 100.00));

		return importe;
	}

	private static double getImporte(Salary salary, DeductionType... types) {
		double importe = 0.00;
		for (DeductionType type : types) {
			importe += type.accept(new DeductionType.Visitor<Double>() {
				@Override
				public Double visitIT(DeductionType deductionType) {
					return 0.00;
				}

				@Override
				public Double visitIMS(DeductionType deductionType) {
					return 0.00;
				}

				@Override
				public Double visitFogasa(DeductionType deductionType) {
					return 0.00;
				}

				@Override
				public Double visitJobTraining(DeductionType deductionType) {
					return visit(deduction -> deduction.isJobTraining());
				}

				@Override
				public Double visitUnemployent(DeductionType deductionType) {
					return visit(deduction -> deduction.isUnemployment());
				}

				@Override
				public Double visitCommonContigency(DeductionType deductionType) {
					return visit(deduction -> deduction.isCommonContingency());
				}

				private Double visit(Predicate<Deduction> filter) {
					return salary.getDeductions().stream().filter(filter)
							.collect(Collectors.summingDouble(deduction -> deduction.getAmount() * 100.00));
				}
			});
			importe += type.accept(new DeductionType.Visitor<Double>() {
				@Override
				public Double visitIT(DeductionType deductionType) {
					return visit(cost -> cost.isIT());
				}

				@Override
				public Double visitIMS(DeductionType deductionType) {
					return visit(cost -> cost.isIMS());
				}

				@Override
				public Double visitFogasa(DeductionType deductionType) {
					return visit(cost -> cost.isFogasa());
				}

				@Override
				public Double visitJobTraining(DeductionType deductionType) {
					return visit(cost -> cost.isJobTraining());
				}

				@Override
				public Double visitUnemployent(DeductionType deductionType) {
					return visit(cost -> cost.isUnemployment());
				}

				@Override
				public Double visitCommonContigency(DeductionType deductionType) {
					return visit(cost -> cost.isCommonContingency());
				}

				private Double visit(Predicate<Cost> filter) {
					return salary.getCosts().stream().filter(filter)
							.collect(Collectors.summingDouble(cost -> cost.getAmount() * 100.00));
				}
			});
		}
		return importe;
	}
	

}
