package com.esferalia.aon.payroll.tgss.creta;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.EXTRA_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.NON_STRUCTURAL_OVERTIME_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.OCCUPATION;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PARTIAL_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_GROUP;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.STRUCTURAL_OVERTIME_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TC2;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_HOURS;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller.Listener;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import net.aonsolutions.tgss.creta.jaxb.CtaCot;
import net.aonsolutions.tgss.creta.jaxb.Dato;
import net.aonsolutions.tgss.creta.jaxb.DatoSolicitado;
import net.aonsolutions.tgss.creta.jaxb.Fecha;
import net.aonsolutions.tgss.creta.jaxb.Liquidacion;
import net.aonsolutions.tgss.creta.jaxb.LiquidacionMes;
import net.aonsolutions.tgss.creta.jaxb.Peculiaridad;
import net.aonsolutions.tgss.creta.jaxb.Periodo;
import net.aonsolutions.tgss.creta.jaxb.Trabajador;
import net.aonsolutions.tgss.creta.jaxb.Tramo;
import net.aonsolutions.tgss.creta.jaxb.Utils;
import net.aonsolutions.tgss.creta.jaxb.bases.BasesBuilder;
import net.aonsolutions.tgss.creta.jaxb.bases.DatoBuilder;
import net.aonsolutions.tgss.creta.jaxb.bases.LiquidacionBuilder;
import net.aonsolutions.tgss.creta.jaxb.bases.LiquidacionMesBuilder;
import net.aonsolutions.tgss.creta.jaxb.bases.TrabajadorBuilder;
import net.aonsolutions.tgss.creta.jaxb.bases.TramoBuilder;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;

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

public class Bases {

	private static class NoSuchContextVariableException extends Exception {
		private ContextVariable contextVariable;

		public NoSuchContextVariableException(ContextVariable contextVariable) {
			super();
			this.contextVariable = contextVariable;
		}

		public ContextVariable getContextVariable() {
			return contextVariable;
		}

	}

	private static class AmbiguousContextVariableException extends Exception {

		private String values[];
		private ContextVariable contextVariable;

		public AmbiguousContextVariableException(
				ContextVariable contextVariable, String... values) {
			super();
			this.values = values;
			this.contextVariable = contextVariable;
		}

		public String[] getValues() {
			return values;
		}

		public ContextVariable getContextVariable() {
			return contextVariable;
		}
	}

	private static abstract class HContextCretaData extends CContextCretaData {

		public HContextCretaData(ContextVariable contextVariable) {
			super(contextVariable);
		}

		@Override
		public void add(Salary salary, Tramo tramo, Dato datoSolicitado,
				TramoBuilder tramoBuilder, BasesCallback... cb) {
			// B -> El código del dato solicitado es obligatorio.
			// P -> El código del dato solicitado es opcional.

			double newValue = get(salary, tramo.getFechaDesde(),
					tramo.getFechaHasta());
			boolean optional = isOptional();
			if (optional && newValue == 0.00)
				return;

			DatoBuilder datoBuilder = new DatoBuilder();
			datoBuilder.setCodigo(datoSolicitado.getCodigo());
			datoBuilder.setTipo(datoSolicitado.getTipoDato());
			datoBuilder.setHoras((int) Math.round(newValue));
			tramoBuilder.addDato(datoBuilder.create());
		}

		protected abstract boolean isOptional();
	}

	private static class OptionalHContextCretaData extends HContextCretaData {

		public OptionalHContextCretaData(ContextVariable contextVariable) {
			super(contextVariable);
		}

		@Override
		protected boolean isOptional() {
			return true;
		}
	}

	private static class MandatoryHContextCretaData extends HContextCretaData {

		public MandatoryHContextCretaData(ContextVariable contextVariable) {
			super(contextVariable);
		}

		@Override
		protected boolean isOptional() {
			return false;
		}
	}

	private static interface BasesCallback {

		default void trabajadorAdded(Trabajador trabajador, Salary salary) {
		};

		default void unknownSalary(Salary salary) {
		};

		default void unknownDato(Dato datoSolicitado) {
		};

		default void unknownTrabajador(Trabajador trabajador) {
		};

		default void rightContextVariable(ContextVariable var, Period p,
				String right) {
		};

		default void noSuchContextVariable(ContextVariable var, Period p,
				String right) {
		};

		default void wrongContextVariable(ContextVariable var, Period p,
				String right, String wrong) {
		};

		default void ambigousContextVariable(ContextVariable var, Period p,
				String right, String... wrongs) {
		};

		default void zeroDato(ContextVariable var, Dato datoSolicitado,
				Tramo tramo, Salary salary) {
		};

	}

	private static class Errors implements BasesCallback {
		@Override
		public void unknownDato(Dato datoSolicitado) {
			System.err.println(String.format("ERROR: Unknown Dato '%s'",
					datoSolicitado.getCodigo()));
		}

		@Override
		public void unknownTrabajador(Trabajador trabajador) {
			System.err.println(String.format(
					"FATAL : Employee %s, not at aon ",
					trabajador.getNaf()));
		}

		@Override
		public void unknownSalary(Salary salary) {
			System.err.println(String.format(
					"WARNING: Employee %s (%s), not at Cret@ ",
					salary.getEmployeeName(), salary.getEmployeeDocument()));
		}

		@Override
		public void noSuchContextVariable(ContextVariable var, Period p,
				String right) {
			System.err.println(String.format(
					"WARNING: '%s' (%s) not in salary data", var, right));
		}

		@Override
		public void rightContextVariable(ContextVariable var, Period p,
				String right) {
			System.err.println(String.format(
					"INFO: '%s' (%s) right value at salary data", var, right));
		}

		@Override
		public void wrongContextVariable(ContextVariable var, Period p,
				String right, String wrong) {
			System.err.println(String.format(
					"ERROR: '%s' (%s) wrong value (%s) at salary data", var,
					right, wrong));
		}

		@Override
		public void zeroDato(ContextVariable var, Dato datoSolicitado,
				Tramo tramo, Salary salary) {
			System.err.println(String.format(
					"WARNING: %s (%s) for %s [%s-%s-%s...%s-%s-%s] is zero",
					var.getName(), datoSolicitado.getCodigo(), salary
							.getEmployeeName(), tramo.getFechaDesde().getDia(),
					tramo.getFechaDesde().getMes(), tramo.getFechaDesde()
							.getAnho(), tramo.getFechaHasta().getDia(), tramo
							.getFechaHasta().getMes(), tramo.getFechaHasta()
							.getAnho()));
			for (Entry<String, List<ContextData>> entry : salary
					.getContextData().entrySet()) {
				for (ContextData data : entry.getValue())
					System.err
							.println(String
									.format("         %s for %s [%3$td-%3$tm-%3$tY...%4$td-%4$tm-%4$tY] is %s",
											entry.getKey(),
											salary.getEmployeeName(),
											data.getStartDate(),
											data.getEndDate(),
											data.getExpression()));
			}
		};
	}

	private static class Comments extends Listener implements BasesCallback {

		private static class TrabajadorData {

			private String ss;
			private String nif;
			private String name;

			public TrabajadorData(String name, String ss, String nif) {
				super();
				this.ss = ss;
				this.nif = nif;
				this.name = name;
			}

		}

		private XMLStreamWriter xsw;
		private Map<String, TrabajadorData> trabajadorDataMap;

		public Comments(XMLStreamWriter xsw) {
			super();
			this.xsw = xsw;
			this.trabajadorDataMap = new HashMap<String, TrabajadorData>();
		}

		@Override
		public void beforeMarshal(Object source) {

			if (source instanceof net.aonsolutions.tgss.creta.jaxb.bases.Trabajador)
				beforeMarshalTrabajador((net.aonsolutions.tgss.creta.jaxb.bases.Trabajador) source);
			else if (source instanceof Dato)
				beforeMarshalDato((Dato) source);

			super.beforeMarshal(source);
		}

		@Override
		public void trabajadorAdded(Trabajador trabajador, Salary salary) {
			trabajadorDataMap.put(trabajador.getNaf(), new TrabajadorData(
					salary.getEmployeeName(), salary.getEmployeeSSNumber(),
					salary.getEmployeeDocument()));
		};

		public void beforeMarshalDato(Dato dato) {
			CretaData cretaData = getCretaData(dato.getCodigo());
			try {
				xsw.writeComment(String.format("%s", cretaData.getComment()));
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void beforeMarshalTrabajador(
				net.aonsolutions.tgss.creta.jaxb.bases.Trabajador trabajador) {
			TrabajadorData data = trabajadorDataMap.get(trabajador.getNaf());
			try {
				xsw.writeComment(String
						.format("\r\n%s\r\nNúmero afiliación a la Seguridad Social:\r\n%s\r\nNúmero de identificación fiscal de las personas físicas:\r\n%s\r\n",
								data.name, data.ss, data.nif));
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private static interface CretaData {

		String getComment();

		void add(Salary salary, Tramo tramo, Dato datoSolicitado,
				TramoBuilder tramoBuilder, BasesCallback... cb);

	}

	private static abstract class CContextCretaData implements CretaData {

		private ContextVariable contextVariable;

		public CContextCretaData(ContextVariable contextVariable) {
			this.contextVariable = contextVariable;
		}

		@Override
		public String getComment() {
			return contextVariable.name();
		}

		@Override
		public void add(Salary salary, Tramo tramo, Dato datoSolicitado,
				TramoBuilder tramoBuilder, BasesCallback... cbs) {
			// B -> El código del dato solicitado es obligatorio.
			// P -> El código del dato solicitado es opcional.

			double newValue = get(salary, tramo.getFechaDesde(),
					tramo.getFechaHasta());
			boolean optional = isOptional();
			if (optional && newValue == 0.00)
				return;

			if (newValue == 0.00)
				for (BasesCallback cb : cbs)
					cb.zeroDato(contextVariable, datoSolicitado, tramo, salary);

			DatoBuilder datoBuilder = new DatoBuilder();
			datoBuilder.setCodigo(datoSolicitado.getCodigo());
			datoBuilder.setTipo(datoSolicitado.getTipoDato());
			datoBuilder.setImporteEuros(newValue);
			tramoBuilder.addDato(datoBuilder.create());
		}

		public Double get(Salary salary, Fecha desde, Fecha hasta) {
			return get(salary, new Period(toDate(desde), toDate(hasta)));
		}

		protected Double get(Salary salary, Period p) {
			List<ContextData> datas = salary.getContextData().get(
					contextVariable.getName());
			if (datas == null)
				return 0.00;

			double ret = 0.00;
			for (ContextData data : datas) {
				Period intersect = p.intersect(new Period(data.getStartDate(),
						data.getEndDate()));
				if (intersect == null)
					continue;

				ret += ExpressionContext.eval(data.getExpression(),
						Double.class) * days(intersect) / days(p);
			}
			return ret;
		}

		protected abstract boolean isOptional();
	}

	private static class MandatoryCContextCretaData extends CContextCretaData {

		public MandatoryCContextCretaData(ContextVariable contextVariable) {
			super(contextVariable);
		}

		@Override
		protected boolean isOptional() {
			return false;
		}
	}

	private static class OptionalCContextCretaData extends CContextCretaData {

		public OptionalCContextCretaData(ContextVariable contextVariable) {
			super(contextVariable);
		}

		@Override
		protected boolean isOptional() {
			return true;
		}
	}

	private static Map<String, CretaData> CONTEXT_VARIABLE_MAP = new HashMap<String, CretaData>() {
		{
			put("500", new MandatoryCContextCretaData(CGC_BASE));

			put("501", new OptionalCContextCretaData(STRUCTURAL_OVERTIME_BASE));
			put("502", new OptionalCContextCretaData(
					NON_STRUCTURAL_OVERTIME_BASE));
			put("537", new OptionalCContextCretaData(
					NON_STRUCTURAL_OVERTIME_BASE));
			put("601", new MandatoryCContextCretaData(CGP_BASE));
			put("611", new MandatoryCContextCretaData(CGP_BASE));

			put("01", new MandatoryHContextCretaData(WORKED_HOURS));
			put("02", new OptionalHContextCretaData(EXTRA_HOURS));
		}
	};

	// ------------------------------------------------------------------------

	private static void bases(BasesBuilder basesBuilder, AONContext ctx,
			Liquidacion<?,?,?> liquidacion, boolean aceptarBasesAnteriores,
			BasesCallback... cbs) {
		LiquidacionBuilder liquidacionBuilder = new LiquidacionBuilder()
				.setAceptarBasesAnteriores(aceptarBasesAnteriores)
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

		for (LiquidacionMes<?> liquidacionMes : liquidacion.getLiquidacionMes()) {

			Map<String, Trabajador> trabajadores = new HashMap<String, Trabajador>();

			for (Trabajador trabajador : liquidacionMes.getTrabajadores()
					.getTrabajador())
				trabajadores.put(
						trabajador.getNaf(), trabajador);

			LiquidacionMesBuilder liquidacionMesBuilder = new LiquidacionMesBuilder();

			if (liquidacionMes.getMesLiquidativo() != null)
				liquidacionMesBuilder.setMes(
						liquidacionMes.getMesLiquidativo().getMes()).setAnho(
						liquidacionMes.getMesLiquidativo().getAnho());

			if (liquidacionMes.getDatosMes() != null)
				for (Dato dato : liquidacionMes.getDatosMes()
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
		AON.getSalaryData(ctx, props->
			props.getCCCProperty().eq(ccc)
			.and(props.getEndDateProperty().ge(startDate))
			.and(props.getStartDateProperty().le(endDate))
			.and(props.getIsSalaryProperty().eq(true))
		).forEach(salary -> trabajador(liquidacionMesBuilder, salary, trabajadores, cbs));
		;
		//@formatter:on

		trabajadores.values().forEach(t -> {
			for (BasesCallback cb : cbs)
				cb.unknownTrabajador(t);
		});

	}

	private static void trabajador(LiquidacionMesBuilder liquidacionMesBuilder,
			Salary salary, Map<String, Trabajador> trabajadores,
			BasesCallback... cbs) {

		TrabajadorBuilder trabajadorBuilder = new TrabajadorBuilder();
		Trabajador trabajador = trabajadores.get(salary.getEmployeeSSNumber());
		if (trabajador == null) {
			for (BasesCallback cb : cbs)
				cb.unknownSalary(salary);
			return;
		}

		trabajadorBuilder.setNaf(trabajador.getNaf());
		for (Tramo<?> tramo : trabajador.getTramos().getTramo()) {
			
			try {
				checkTramo(tramo, salary, cbs);
			} catch ( UnsupportedOperationException e){
				
			}

			TramoBuilder tramoBuilder = new TramoBuilder();

			tramoBuilder.setDiaDesde(tramo.getFechaDesde().getDia());
			tramoBuilder.setMesDesde(tramo.getFechaDesde().getMes());
			tramoBuilder.setAnhoDesde(tramo.getFechaDesde().getAnho());

			tramoBuilder.setDiaHasta(tramo.getFechaHasta().getDia());
			tramoBuilder.setMesHasta(tramo.getFechaHasta().getMes());
			tramoBuilder.setAnhoHasta(tramo.getFechaHasta().getAnho());

			for (Dato datoSolicitado : tramo.getDatosTramo()
					.getDatoSolicitado()) {

				CretaData data = getCretaData(datoSolicitado.getCodigo());

				if (data == null) {
					for (BasesCallback cb : cbs)
						cb.unknownDato(datoSolicitado);
					continue;
				}

				data.add(salary, tramo, datoSolicitado, tramoBuilder, cbs);
			}

			trabajadorBuilder.addTramo(tramoBuilder.create());
		}

		for (BasesCallback cb : cbs)
			cb.trabajadorAdded(trabajador, salary);

		liquidacionMesBuilder.add(trabajadorBuilder.create());

		trabajadores.remove(salary.getEmployeeSSNumber());

	}

	private static void checkTramo(Tramo<?> tramo, Salary salary,
			BasesCallback... cbs) {
		Period p = new Period(toDate(tramo.getFechaDesde()),
				toDate(tramo.getFechaHasta()));

		checkContextVariable(QUOTE_GROUP, tramo.getInformacionAfiliacion()
				.getGrupoCotizacion(), p, salary, cbs);
		checkContextVariable(TC2, tramo.getInformacionAfiliacion()
				.getTipoContrato(), p, salary, cbs);
		checkContextVariable(OCCUPATION, tramo.getInformacionAfiliacion()
				.getOcupacion(), p, salary, cbs);

		String partialFactor = tramo.getInformacionAfiliacion()
				.getCoeficienteTiempoParcial();
		if ( partialFactor != null )
			checkContextVariable(
					PARTIAL_FACTOR,
					(partialFactor != null ? Double.toString(Integer
							.parseInt(partialFactor) / 1000.00) : null), p, salary,
					cbs);
//		for (Peculiaridad peculiaridad : tramo.getInformacionAfiliacion()
//				.getPeculiaridades().getPeculiaridad()) {
//
//		}

	}

	private static void checkContextVariable(ContextVariable contextVariable,
			String rigthValue, Period p, Salary salary, BasesCallback... cbs) {

		try {
			String salaryValue = get(salary, contextVariable, p);
			if (!StringUtils.equalsIgnoreCase(rigthValue, salaryValue))
				for (BasesCallback cb : cbs)
					cb.wrongContextVariable(contextVariable, p, rigthValue,
							salaryValue);
			else
				for (BasesCallback cb : cbs)
					cb.rightContextVariable(contextVariable, p, rigthValue);

		} catch (NoSuchContextVariableException e) {
			if (rigthValue != null)
				for (BasesCallback cb : cbs)
					cb.noSuchContextVariable(contextVariable, p, rigthValue);
		} catch (AmbiguousContextVariableException e) {
			for (BasesCallback cb : cbs)
				cb.ambigousContextVariable(contextVariable, p, rigthValue,
						e.getValues());
		}
	}


	private static long days(Period p) {
		return AonDateUtils.getDaysBetweenDates(p.getStart(), p.getEnd()) + 1;
	}

	private static String get(Salary salary, ContextVariable contextVariable,
			Period p) throws NoSuchContextVariableException,
			AmbiguousContextVariableException {
		List<ContextData> datas = salary.getContextData().get(
				contextVariable.getName());

		if (datas == null)
			throw new NoSuchContextVariableException(contextVariable);

		String expression = null;
		for (ContextData data : datas) {
			Period intersect = p.intersect(new Period(data.getStartDate(), data
					.getEndDate()));
			if (intersect == null)
				continue;
			if (expression == null || expression.equals(data.getExpression()))
				expression = data.getExpression();
			else
				throw new AmbiguousContextVariableException(contextVariable,
						expression, data.getExpression());
		}
		if (expression == null)
			throw new NoSuchContextVariableException(contextVariable);

		return expression;
	}

	private static CretaData getCretaData(String codigo) {
		return CONTEXT_VARIABLE_MAP.get(codigo);
	}

	private static net.aonsolutions.tgss.creta.jaxb.bases.Bases bases(
			net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresTramos,
			AONContext ctx, boolean aceptarBasesAnteriores,
			XMLStreamWriter xsw, BasesCallback... cbs) {

		BasesBuilder builder = new BasesBuilder()
				.setAutorizado(trabajadoresTramos.getAutorizado());

		bases(builder, ctx, trabajadoresTramos.getLiquidacion(),
				aceptarBasesAnteriores, cbs);

		return builder.create();

	}

	private static net.aonsolutions.tgss.creta.jaxb.bases.Bases bases(
			net.aonsolutions.tgss.creta.jaxb.respuesta.Respuesta respuesta,
			AONContext ctx, boolean aceptarBasesAnteriores,
			XMLStreamWriter xsw, BasesCallback... cbs) {

		BasesBuilder builder = new BasesBuilder().setAutorizado(respuesta
				.getAutorizado());

		for (Liquidacion<?,?,?> liquidacion : respuesta.getLiquidacion())
			bases(builder, ctx, liquidacion, aceptarBasesAnteriores, cbs);

		return builder.create();

	}
	
	// ------------------------------------------------------------------------
	
	@SuppressWarnings("static-access")
	public static Option getHostNameOption(){
		return OptionBuilder.withArgName("name")
							.hasArg()
							.withLongOpt("host")
							.withDescription("Connect to host.")
							.create("h");
	}
	
	@SuppressWarnings("static-access")
	public static Option getDbUserOption(){
		return OptionBuilder.withArgName("name")
							.hasArg()
							.isRequired(true)
							.withLongOpt("user")
							.withDescription("User for login.")
							.create("u");
	}	
	
	@SuppressWarnings("static-access")
	public static Option getDbPasswordOption(){
		return OptionBuilder.withArgName("name")
				.hasArg()
				.isRequired(true)
				.withLongOpt("password")
				.withDescription("Password to use when connecting to server.")
				.create("p");
	}
	
	@SuppressWarnings("static-access")
	public static Option getDatabaseOption(){
		return OptionBuilder.withArgName("name")
				.hasArg()
				.isRequired(true)
				.withLongOpt("database")
				.withDescription("Database to use.")
				.create("D");
	}
	
	
	@SuppressWarnings("static-access")
	public static Option getCommentsOption(){
		return OptionBuilder.withLongOpt("comments")
				.withDescription("Write additional information.")
				.create("c");
	}	
	
	@SuppressWarnings("static-access")
	public static Option getPrettyOption(){
		return OptionBuilder.withLongOpt("pretty")
		  .withDescription("Makes the output readable to a human.")
		  .create();		
	}
	
	public static Calendar toCalendar(Periodo periodo) {
		return toCalendar(periodo.getAnho(), periodo.getMes());
	}

	public static Calendar toCalendar(String anho, String mes) {
		int month = Integer.parseInt(mes) - 1;
		int year = Integer.parseInt(anho);

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);

		// Reset time
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar;
	}

	public static Date toDate(Fecha fecha) {
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

	public static Date toDate(String anho, String mes, String dia) {
		int day = Integer.parseInt(dia);
		int month = Integer.parseInt(mes) - 1;
		int year = Integer.parseInt(anho);

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, day);

		// Reset time
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}

	// ------------------------------------------------------------------------
	// Main
	//

	@SuppressWarnings("static-access")
	public static void main(String[] args) throws JAXBException, SQLException,
			ClassNotFoundException, IOException, XMLStreamException,
			FactoryConfigurationError, TransformerConfigurationException,
			TransformerFactoryConfigurationError {

		//@formatter:off
		Option hostName =  getHostNameOption();
		Option user =  getDbUserOption();
		Option password =  getDbPasswordOption();
		Option database =  getDatabaseOption();
		Option trabajadoresTramosFile =  OptionBuilder.withArgName("file")
									.hasArg()
									.withLongOpt("trabajadores-tramos")
									.withDescription("Fichero de Trabajadores y Tramos.")
									.create("t");
		Option respuestaFile =  OptionBuilder.withArgName("file")
				.hasArg()
				.withLongOpt("respuesta")
				.withDescription("Fichero de Respuesta.")
				.create("r");

		Option comments = getCommentsOption();
		
		Option skiptPrevBases =  OptionBuilder.withLongOpt("skip-prev-bases")
				.withDescription("Skip previous bases.")
				.create("b");

		Option pretty =  getPrettyOption();

		Options options = new Options()
		.addOption(hostName)
		.addOption(user)
		.addOption(password)
		.addOption(database)
		.addOption(comments)
		.addOption(skiptPrevBases)
		.addOption(pretty)
		.addOption(trabajadoresTramosFile)
		.addOption(respuestaFile)
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

			boolean aceptarBasesAnteriores = !cmd.hasOption(skiptPrevBases
					.getLongOpt());

			AONContext ctx = new AONContext(connection);

			XMLStreamWriter xsw = new IndentXMLStreamWriter(XMLOutputFactory
					.newInstance().createXMLStreamWriter(System.out), "  ");

			Errors errors = new Errors();
			Comments comment = new Comments(xsw);

			InputStream is = null;
			net.aonsolutions.tgss.creta.jaxb.bases.Bases bases = null;

			if (cmd.hasOption(trabajadoresTramosFile.getLongOpt())) {

				String path = cmd.getOptionValue(trabajadoresTramosFile
						.getLongOpt());
				is = AonStringUtils.isEmpty(path) ? System.in
						: new FileInputStream(path);

				net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresTramos = Utils
						.unmarshal(
								net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos.class,
								is);

				bases = bases(trabajadoresTramos, ctx, aceptarBasesAnteriores,
						xsw, errors, comment);
			} else {
				String path = cmd.getOptionValue(respuestaFile.getLongOpt());
				is = AonStringUtils.isEmpty(path) ? System.in
						: new FileInputStream(path);

				net.aonsolutions.tgss.creta.jaxb.respuesta.Respuesta respuesta = Utils
						.unmarshal(
								net.aonsolutions.tgss.creta.jaxb.respuesta.Respuesta.class,
								is);

				bases = bases(respuesta, ctx, aceptarBasesAnteriores, xsw,
						errors, comment);
			}

			is.close();

			if (cmd.hasOption(comments.getLongOpt()))
				Utils.marshal(bases, xsw, comment);
			else
				Utils.marshal(bases, xsw);

		} catch (ParseException e) {
			// oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + e.getMessage());
			// automatically generate the help statement
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("cret@", options);

		}

	}

}
