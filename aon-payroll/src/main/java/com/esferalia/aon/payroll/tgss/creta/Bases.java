package com.esferalia.aon.payroll.tgss;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import net.aonsolutions.tgss.creta.jaxb.ConceptoEconomicoCotizacion;
import net.aonsolutions.tgss.creta.jaxb.ConceptoEconomicoCotizacion.IVisitor;
import net.aonsolutions.tgss.creta.jaxb.Utils;
import net.aonsolutions.tgss.creta.jaxb.bases.Bases;
import net.aonsolutions.tgss.creta.jaxb.bases.BasesBuilder;
import net.aonsolutions.tgss.creta.jaxb.bases.DatoBuilder;
import net.aonsolutions.tgss.creta.jaxb.bases.LiquidacionBuilder;
import net.aonsolutions.tgss.creta.jaxb.bases.LiquidacionMesBuilder;
import net.aonsolutions.tgss.creta.jaxb.bases.TrabajadorBuilder;
import net.aonsolutions.tgss.creta.jaxb.bases.TramoBuilder;
import net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.CtaCot;
import net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.DatoSolicitado;
import net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.Fecha;
import net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.Liquidacion;
import net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.LiquidacionMes;
import net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.Periodo;
import net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.Trabajador;
import net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos;
import net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.Tramo;

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
import com.esferalia.aon.occam.api.model.Salary.ContextData;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.AonUtils;
import com.google.api.services.drive.model.File;

public class Creta {

	@SuppressWarnings("static-access")
	public static void main(String[] args) throws JAXBException, SQLException,
			ClassNotFoundException, IOException {

		//@formatter:off
		Option hostName =  OptionBuilder.withArgName("name")
					 					.hasArg()
										.withLongOpt("host")
										.withDescription("Connect to host.")
										.create("h");
		Option user =  OptionBuilder.withArgName("name")
					 				.hasArg()
									.isRequired(true)
									.withLongOpt("user")
									.withDescription("User for login.")
									.create("u");
		Option password =  OptionBuilder.withArgName("name")
										.hasArg()
										.isRequired(true)
										.withLongOpt("password")
										.withDescription("Password to use when connecting to server.")
										.create("p");
		Option database =  OptionBuilder.withArgName("name")
										.hasArg()
										.isRequired(true)
										.withLongOpt("database")
										.withDescription("Database to use.")
										.create("D");
		Option xml =  OptionBuilder.withArgName("file")
									.hasArg()
									.withLongOpt("xml")
									.withDescription("XML file.")
									.withType(File.class)
									.create("x");
		Options options = new Options()
		.addOption(hostName)
		.addOption(user)
		.addOption(password)
		.addOption(database)
		.addOption(xml)
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

			String path = cmd.getOptionValue(xml.getLongOpt());
			InputStream is = AonStringUtils.isEmpty(path) ? System.in
					: new FileInputStream(cmd.getOptionValue(xml.getLongOpt()));

			TrabajadoresTramos trabajadoresTramos = Utils.unmarshal(
					TrabajadoresTramos.class, is);

			AONContext ctx = new AONContext(connection);
			BasesBuilder builder = new BasesBuilder()
					.setAutorizado(trabajadoresTramos.getAutorizado());

			bases(builder, ctx, trabajadoresTramos.getLiquidacion());
			is.close();

			Bases bases = builder.create();
			Utils.marshal(bases, System.out);

		} catch (ParseException e) {
			// oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + e.getMessage());
			// automatically generate the help statement
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("cret@", options);

		}

	}

	public static interface BasesCallback {
		void unknownDato(DatoSolicitado datoSolicitado) ;
		
	}

	// ------------------------------------------------------------------------

	private static void bases(BasesBuilder basesBuilder, AONContext ctx,
			Liquidacion liquidacion, BasesCallback... cbs) {
		LiquidacionBuilder liquidacionBuilder = new LiquidacionBuilder()
				.setCCC(liquidacion.getCcc().getRegimen(),
						liquidacion.getCcc().getProvincia(),
						liquidacion.getCcc().getNumero())
				.setAnhoDesde(liquidacion.getPeriodoDesde().getAnho())
				.setMesDesde(liquidacion.getPeriodoDesde().getMes())
				.setAnhoHasta(liquidacion.getPeriodoHasta().getAnho())
				.setMesHasta(liquidacion.getPeriodoHasta().getMes());

		liquidacionBuilder.setTipo(liquidacion.getTipo());
		if (liquidacion.getCccConcertado() != null)
			liquidacionBuilder
					.setCCCConcertado(liquidacion.getCccConcertado()
							.getRegimen(), liquidacion.getCccConcertado()
							.getProvincia(), liquidacion.getCccConcertado()
							.getNumero());

		if (liquidacion.getFechaControl() != null)
			liquidacionBuilder.setMesControl(
					liquidacion.getFechaControl().getMes()).setAnhoControl(
					liquidacion.getFechaControl().getAnho());

		for (LiquidacionMes liquidacionMes : liquidacion.getLiquidacionMes()) {
			Map<String, Trabajador> trabajadores = new HashMap<String, Trabajador>();
			for (Trabajador trabajador : liquidacionMes.getTrabajadores()
					.getTrabajador())
				trabajadores.put(
						trabajador.getIpf().getNumeroIpf()
								.replaceAll("^0+", ""), trabajador);

			LiquidacionMesBuilder liquidacionMesBuilder = new LiquidacionMesBuilder();

			if (liquidacionMes.getMesLiquidativo() != null)
				liquidacionMesBuilder.setMes(
						liquidacionMes.getMesLiquidativo().getMes()).setAnho(
						liquidacionMes.getMesLiquidativo().getAnho());

			if (liquidacionMes.getDatosMes() != null)
				for (DatoSolicitado dato : liquidacionMes.getDatosMes()
						.getDatoSolicitado())
					liquidacionMesBuilder.add(new DatoBuilder()
							.setCodigo(dato.getCodigo())
							.setTipo(dato.getTipoDato()).create());

			trabajadores(liquidacionMesBuilder, ctx, liquidacion.getCcc(),
					liquidacionMes.getMesLiquidativo(), trabajadores, cbs);

			liquidacionBuilder
					.addLiquidacionMes(liquidacionMesBuilder.create());

		}

		basesBuilder.addLiquidacion(liquidacionBuilder.create());
	}

	private static void trabajadores(
			LiquidacionMesBuilder liquidacionMesBuilder, AONContext ctx,
			CtaCot ctaCot, Periodo mesLiquidativo,
			Map<String, Trabajador> trabajadores, BasesCallback... cbs) {

		String ccc = String.format("%s%s", ctaCot.getProvincia(),
				ctaCot.getNumero());

		Calendar calendar = toCalendar(mesLiquidativo);
		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		Date startDate = calendar.getTime();
		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date endDate = calendar.getTime();

		//@formatter:off
		AON.getSalaries(ctx, props->
			props.getCCCProperty().eq(ccc)
			.and(props.getEndDateProperty().ge(startDate))
			.and(props.getStartDateProperty().le(endDate))
			.and(props.getIsSalaryProperty().eq(true))
		).forEach(salary -> trabajador(liquidacionMesBuilder, salary, trabajadores, cbs));
		;
		//@formatter:on

	}

	private static void trabajador(LiquidacionMesBuilder liquidacionMesBuilder,
			Salary salary, Map<String, Trabajador> trabajadores,
			BasesCallback... cbs) {

		TrabajadorBuilder trabajadorBuilder = new TrabajadorBuilder();
		Trabajador trabajador = trabajadores.get(salary.getEmployeeDocument());
		if (trabajador == null) {
			// TODO:
			return;
		}

		trabajadorBuilder.setNaf(trabajador.getNaf());
		for (Tramo tramo : trabajador.getTramos().getTramo()) {
			TramoBuilder tramoBuilder = new TramoBuilder();

			tramoBuilder.setDiaDesde(tramo.getFechaDesde().getDia());
			tramoBuilder.setMesDesde(tramo.getFechaDesde().getMes());
			tramoBuilder.setAnhoDesde(tramo.getFechaDesde().getAnho());

			tramoBuilder.setDiaHasta(tramo.getFechaHasta().getDia());
			tramoBuilder.setMesHasta(tramo.getFechaHasta().getMes());
			tramoBuilder.setAnhoHasta(tramo.getFechaHasta().getAnho());

			for (DatoSolicitado datoSolicitado : tramo.getDatosTramo()
					.getDatoSolicitado()) {
				DatoBuilder datoBuilder = new DatoBuilder();

				datoBuilder.setCodigo(datoSolicitado.getCodigo());
				datoBuilder.setTipo(datoSolicitado.getTipoDato());
				

				ContextVariable var = getContextVariable(datoSolicitado
						.getCodigo());
				
				if ( var == null ) {
					for(BasesCallback cb: cbs) 
						cb.unknownDato(datoSolicitado);
					continue;
				}

				Double newValue = get(var, salary, tramo.getFechaDesde(),
						tramo.getFechaHasta());
				try {
					Double oldValue = Double.parseDouble(datoSolicitado
							.getValor());
					if (AonUtils.equals(oldValue, newValue))
						datoBuilder.setValor(newValue);
				} catch (NullPointerException e) {
					datoBuilder.setValor(newValue);
				}

				tramoBuilder.addDato(datoBuilder.create());
			}

			trabajadorBuilder.addTramo(tramoBuilder.create());
		}

		liquidacionMesBuilder.add(trabajadorBuilder.create());
	}

	private static Calendar toCalendar(Periodo periodo) {
		int mes = Integer.parseInt(periodo.getMes()) - 1;
		int anho = Integer.parseInt(periodo.getAnho());

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, anho);
		calendar.set(Calendar.MONTH, mes);

		// Reset time
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar;
	}

	private static Date toDate(Fecha fecha) {
		int dia = Integer.parseInt(fecha.getDia());
		int mes = Integer.parseInt(fecha.getMes()) - 1;
		int anho = Integer.parseInt(fecha.getAnho());

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, anho);
		calendar.set(Calendar.MONTH, mes);
		calendar.set(Calendar.DAY_OF_MONTH, dia);

		// Reset time
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}

	private static Double get(ContextVariable var, Salary salary, Fecha desde,
			Fecha hasta) {
		return get(var, salary, new Period(toDate(desde), toDate(hasta)));
	}

	private static Double get(ContextVariable var, Salary salary, Period p) {
		List<ContextData> datas = salary.getContextData().get(var.getName());
		if (datas == null)
			return 0.00;

		double ret = 0.00;
		for (ContextData data : datas) {
			Period intersect = p.intersect(new Period(data.getStartDate(), data
					.getEndDate()));
			if (intersect == null)
				continue;

			ret += ExpressionContext.eval(data.getExpression(), Double.class)
					* days(intersect) / days(p);
		}
		return ret;
	}

	private static long days(Period p) {
		return AonDateUtils.getDaysBetweenDates(p.getStart(), p.getEnd()) + 1;
	}

	private static ContextVariable getContextVariable(String codigo) {
		try {
		return ConceptoEconomicoCotizacion.conceptoOf(codigo).visit(
				new IVisitor<ContextVariable>() {

					@Override
					public ContextVariable visitPerception(
							ConceptoEconomicoCotizacion cec) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public ContextVariable visitCommonContingency(
							ConceptoEconomicoCotizacion cec) {
						return ContextVariable.CGC_BASE;
					}

					@Override
					public ContextVariable visitForceOvertime(
							ConceptoEconomicoCotizacion cec) {
						return ContextVariable.STRUCTURAL_OVERTIME_BASE;
					}

					@Override
					public ContextVariable visitOtherOvertime(
							ConceptoEconomicoCotizacion cec) {
						return ContextVariable.NON_STRUCTURAL_OVERTIME_BASE;
					}

					@Override
					public ContextVariable visitEnterpriseCommonContingency(
							ConceptoEconomicoCotizacion cec) {
						return ContextVariable.CGP_BASE;
					}

					@Override
					public ContextVariable visitMaternityPartCommonContingency(
							ConceptoEconomicoCotizacion cec) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public ContextVariable visitErePartCommonContingency(
							ConceptoEconomicoCotizacion cec) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public ContextVariable visitAdditionalHours(
							ConceptoEconomicoCotizacion cec) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public ContextVariable visitCommonIT(
							ConceptoEconomicoCotizacion cec) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public ContextVariable visitWorkIT(
							ConceptoEconomicoCotizacion cec) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public ContextVariable visitSpecialWorkIT(
							ConceptoEconomicoCotizacion cec) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public ContextVariable visitWorkIMS(
							ConceptoEconomicoCotizacion cec) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public ContextVariable visitSpecialWorkIMS(
							ConceptoEconomicoCotizacion cec) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public ContextVariable visitMaternityPartIT(
							ConceptoEconomicoCotizacion cec) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public ContextVariable visitMaternityPartIMS(
							ConceptoEconomicoCotizacion cec) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public ContextVariable visitErePartIT(
							ConceptoEconomicoCotizacion cec) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public ContextVariable visitErePartIMS(
							ConceptoEconomicoCotizacion cec) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public ContextVariable visitWorkITComplement(
							ConceptoEconomicoCotizacion cec) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public ContextVariable visitUnemployment(
							ConceptoEconomicoCotizacion cec) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public ContextVariable visitEnterpriseUnemployment(
							ConceptoEconomicoCotizacion cec) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public ContextVariable visitMaternityPartUnemployment(
							ConceptoEconomicoCotizacion cec) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public ContextVariable visitErePartUnemployment(
							ConceptoEconomicoCotizacion cec) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public ContextVariable visitEducationBonus(
							ConceptoEconomicoCotizacion cec) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public ContextVariable visitTutorshipBonus(
							ConceptoEconomicoCotizacion cec) {
						// TODO Auto-generated method stub
						return null;
					}

				});
		} catch ( IllegalArgumentException e){
			return null;
		}
	}

}
