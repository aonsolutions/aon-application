package com.esferalia.aon.payroll.tgss.creta;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ERE_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.EXTRA_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MATERNITY_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTHLY_SALARY;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.NON_STRUCTURAL_OVERTIME_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.OCCUPATION;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PARTIAL_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_GROUP;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.STRUCTURAL_OVERTIME_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TC2;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_HOURS;
import static com.esferalia.aon.watson.server.AonDateUtils.getDaysBetweenDates;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Marshaller.Listener;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;
import org.mvel2.MVEL;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.Salary.ContextData;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.tgss.creta.jaxb.CtaCot;
import net.aonsolutions.tgss.creta.jaxb.Dato;
import net.aonsolutions.tgss.creta.jaxb.DatoSolicitado;
import net.aonsolutions.tgss.creta.jaxb.DatosLiquidacion;
import net.aonsolutions.tgss.creta.jaxb.Fecha;
import net.aonsolutions.tgss.creta.jaxb.Liquidacion;
import net.aonsolutions.tgss.creta.jaxb.LiquidacionMes;
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
import net.aonsolutions.tgss.creta.jaxb.bases.Tramos;

public class Bases {

	@SuppressWarnings("serial")
	public static class EmptyBasesException extends Exception {

		String autorizado;

		public String getAutorizado() {
			return autorizado;
		}

		public EmptyBasesException setAutorizado(String autorizado) {
			this.autorizado = autorizado;
			return this;
		}

	}

	private static class MonthlySalaryCretaData implements CretaData {
		@Override
		public String getComment() {
			return MONTHLY_SALARY.name();
		}

		@Override
		public void add(Salary salary, Tramo tramo, Dato datoSolicitado,
				TramoBuilder tramoBuilder, BasesCallback... cbs) {
			try {
				boolean montly = get(salary,
						new Period(toDate(tramo.getFechaDesde()),
								toDate(tramo.getFechaHasta())));
				if (!montly)
					return;

				DatoBuilder datoBuilder = new DatoBuilder()
						.setCodigo(datoSolicitado.getCodigo())
						.setTipo(datoSolicitado.getTipoDato()).setValor("M");
				tramoBuilder.addDato(datoBuilder.create());
			} catch (NoSuchContextVariableException e) {
				try {
					for (BasesCallback cb : cbs)
						cb.noSuchDato(salary, tramo, datoSolicitado,
								tramoBuilder, true);
				} catch (Cancel c) {

				}

			}

		}

		protected boolean get(Salary salary, Period p)
				throws NoSuchContextVariableException {
			List<ContextData> datas = salary.getContextData()
					.get(MONTHLY_SALARY.getName());

			if (datas == null)
				throw new NoSuchContextVariableException(MONTHLY_SALARY);

			for (ContextData data : datas) {
				Period intersect = p.intersect(
						new Period(data.getStartDate(), data.getEndDate()));
				if (intersect == null)
					continue;

				// TODO : More than one unique value ?
				return ExpressionContext.eval(data.getExpression(),
						Boolean.class);
			}

			throw new NoSuchContextVariableException(MONTHLY_SALARY);
		}
	}

	@SuppressWarnings("serial")
	private static class NoSuchDatoException extends Exception {

		private net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.DatoSolicitado datoSolicitado;

		public NoSuchDatoException(
				net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.DatoSolicitado datoSolicitado) {
			this.datoSolicitado = datoSolicitado;
		}

		public net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.DatoSolicitado getDatoSolicitado() {
			return datoSolicitado;
		}

	}

	@SuppressWarnings("serial")
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

	// TODO: CompositeException ???
	private static class NoSuchContextVariablesException
			extends RuntimeException {
		private ContextVariable contextVariables[];

		@SuppressWarnings("serial")
		public NoSuchContextVariablesException(
				ContextVariable... contextVariables) {
			super();
			this.contextVariables = contextVariables;
		}

		public ContextVariable[] getContextVariables() {
			return contextVariables;
		}

	}

	private static class ZeroValueException extends RuntimeException {
		private ContextVariable contextVariable;

		public ZeroValueException(ContextVariable contextVariable) {
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

	@SuppressWarnings("serial")
	private static class UnMatchedContextVariableException extends Exception {

		private ContextData contextData;
		private ContextVariable contextVariable;

		public UnMatchedContextVariableException(
				ContextVariable contextVariable, ContextData contextData) {
			super();
			this.contextVariable = contextVariable;
			this.contextData = contextData;

		}

		public ContextData getContextData() {
			return contextData;
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
				TramoBuilder tramoBuilder, BasesCallback... cbs) {
			// B -> El código del dato solicitado es obligatorio.
			// P -> El código del dato solicitado es opcional.

			boolean optional = isOptional();
			try {
				double newValue = get(salary, tramo.getFechaDesde(),
						tramo.getFechaHasta());

				if (optional && newValue == 0.00)
					return;

				if (newValue == 0.00)
					for (BasesCallback cb : cbs)
						cb.zeroDato(contextVariable, datoSolicitado, tramo,
								salary);

				DatoBuilder datoBuilder = new DatoBuilder();
				datoBuilder.setCodigo(datoSolicitado.getCodigo());
				datoBuilder.setTipo(datoSolicitado.getTipoDato());
				datoBuilder.setHoras((int) Math.round(newValue));
				tramoBuilder.addDato(datoBuilder.create());

			} catch (NoSuchContextVariableException e) {
				try {
					for (BasesCallback cb : cbs)
						cb.noSuchDato(salary, tramo, datoSolicitado,
								tramoBuilder, optional);
				} catch (Cancel c) {
				}
			} catch (UnMatchedContextVariableException e) {
				for (BasesCallback cb : cbs)
					cb.unMatchedContextVariable(salary, e.getContextVariable(),
							e.getContextData(), datoSolicitado, tramo,
							tramoBuilder, optional);
			}
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

	public static interface BasesCallback {

		default void noDiffs(
				net.aonsolutions.tgss.creta.jaxb.bases.Liquidacion liquidacion) {

		}

		default void trabajadorAdded(
				net.aonsolutions.tgss.creta.jaxb.bases.Trabajador trabajadorAon,
				Trabajador trabajadorCreta, Salary salary) {
		};

		default void trabajadorSkipped(
				net.aonsolutions.tgss.creta.jaxb.bases.Trabajador trabajadorAon,
				Trabajador trabajadorCreta, Salary salary) {
		};

		default void trabajadorEmpty(
				net.aonsolutions.tgss.creta.jaxb.bases.Trabajador trabajadorAon,
				Trabajador trabajadorCreta, Salary salary) {
		};

		default void unknownSalary(Salary salary) {
		};

		default void salaryNotFound(String ccc, Trabajador<?> trabajador,
				Periodo mes) {
		};

		default void rightContextVariable(ContextVariable var, Period p,
				String right) {
		};

		default void noSuchContextVariable(Salary salary, ContextVariable var,
				Period p, String right) {
		};

		default void wrongContextVariable(Salary salary, ContextVariable var,
				Period p, String right, String wrong) {
		};

		default void ambigousContextVariable(Salary salary, ContextVariable var,
				Period p, String right, String... wrongs) {
		};

		default void unMatchedContextVariable(Salary salary,
				ContextVariable var, ContextData contextData,
				Dato datoSolicitado, Tramo tramo, TramoBuilder tramoBuilder,
				boolean optional) {
		};

		default void zeroDato(ContextVariable var, Dato datoSolicitado,
				Tramo tramo, Salary salary) {
		};

		default void noSuchDato(Salary salary, Tramo tramo, Dato datoSolicitado,
				TramoBuilder tramoBuilder, boolean optional) {
		};

		default void unknownDato(Liquidacion<?, ?, ?, ?> liquidacion,
				DatoSolicitado datoSolicitado,
				LiquidacionBuilder liquidacionBuilder) {
		};

		default void unknownDato(Salary salary, Tramo tramo,
				DatoSolicitado datoSolicitado, TramoBuilder tramoBuilder) {
		};

		default void wrongRespuestaIs(InputStream respuestaIs, Exception e) {

		};

		default void wrongTrabajadoresTramosIs(InputStream trabajadoresTramosIs,
				Exception e) {

		};

	}

	@SuppressWarnings("serial")
	private static class Cancel extends RuntimeException {

	}

	@SuppressWarnings("serial")
	private static class Different extends RuntimeException {

	}

	@SuppressWarnings("serial")
	private static class SkipExisting extends RuntimeException {

	}

	@SuppressWarnings("serial")
	private static class InvalidTramo extends RuntimeException {

	}

	@SuppressWarnings("serial")
	private static class InvalidTrabajador extends RuntimeException {

	}

	private static class SkipExistingCallback implements BasesCallback {
		@Override
		public void trabajadorAdded(
				net.aonsolutions.tgss.creta.jaxb.bases.Trabajador trabajadorAon,
				Trabajador trabajadorCreta, Salary salary) {
			skip(trabajadorAon, trabajadorCreta);
		}
	}

	private static class NAFFilterCallback implements BasesCallback {

		private Set<String> nafs = new HashSet<String>();

		public NAFFilterCallback(String... nafs) {
			this.nafs = new HashSet<String>();
			for (String naf : nafs)
				this.nafs.add(naf);
		}

		public void add(String... nafs) {
			for (String naf : nafs)
				this.nafs.add(naf);
		}

		@Override
		public void trabajadorAdded(
				net.aonsolutions.tgss.creta.jaxb.bases.Trabajador trabajadorAon,
				Trabajador trabajadorCreta, Salary salary) {
			if (!nafs.contains(trabajadorAon.getNaf()))
				throw new SkipExisting();
		}
	}

	private static class DefaultsCallback implements BasesCallback {

		private Map<String, String> defaults;

		public DefaultsCallback() {
			defaults = new HashMap<String, String>();
		}

		void add(String codigo, String valor) {
			defaults.put(codigo, valor);
		}

		@Override
		public void unknownDato(Liquidacion<?, ?, ?, ?> liquidacion,
				DatoSolicitado datoSolicitado,
				LiquidacionBuilder liquidacionBuilder) {
			addDefault(datoSolicitado, liquidacion, liquidacionBuilder);
		}

		@Override
		public void noSuchDato(Salary salary, Tramo tramo, Dato datoSolicitado,
				TramoBuilder tramoBuilder, boolean optional) {
			addDefault(salary, tramo, datoSolicitado, tramoBuilder);
		}

		@Override
		public void unknownDato(Salary salary, Tramo tramo,
				DatoSolicitado datoSolicitado, TramoBuilder tramoBuilder) {
			addDefault(salary, tramo, datoSolicitado, tramoBuilder);
		}

		// --------------------------------------------------------------------

		private void addDefault(Salary salary, Tramo tramo, Dato datoSolicitado,
				TramoBuilder tramoBuilder) {
			if (defaults.containsKey(datoSolicitado.getCodigo())) {
				String valor = defaults.get(datoSolicitado.getCodigo());
				DatoBuilder datoBuilder = new DatoBuilder();
				datoBuilder.setCodigo(datoSolicitado.getCodigo());
				datoBuilder.setTipo(datoSolicitado.getTipoDato());
				datoBuilder.setValor(valor);
				tramoBuilder.addDato(datoBuilder.create());
				System.err.printf(
						"WARN: %s for %s (%s) [%s-%s-%s...%s-%s-%s] is default value %s \r\n",
						datoSolicitado.getCodigo(),
						salary.getEmployeeSSNumber(),
						salary.getEmployeeDocument(),
						tramo.getFechaDesde().getDia(),
						tramo.getFechaDesde().getMes(),
						tramo.getFechaDesde().getAnho(),
						tramo.getFechaHasta().getDia(),
						tramo.getFechaHasta().getMes(),
						tramo.getFechaHasta().getAnho(), valor);

				throw new Cancel();
			}
		}

		private void addDefault(Dato datoSolicitado,
				Liquidacion<?, ?, ?, ?> liquidacion,
				LiquidacionBuilder liquidacionBuilder) {
			if (defaults.containsKey(datoSolicitado.getCodigo())) {
				String valor = defaults.get(datoSolicitado.getCodigo());
				DatoBuilder datoBuilder = new DatoBuilder();
				datoBuilder.setCodigo(datoSolicitado.getCodigo());
				datoBuilder.setTipo(datoSolicitado.getTipoDato());
				datoBuilder.setValor(valor);
				liquidacionBuilder.addDato(datoBuilder.create());
				System.err.printf("WARN: %s for %s default value %s \r\n",
						datoSolicitado.getCodigo(),
						liquidacion.getCcc().getNumero(), valor);
				throw new Cancel();
			}
		}

	}

	private static class FixConceptUnMatchedCallback implements BasesCallback {

		private String datos[];

		public FixConceptUnMatchedCallback(String... datos) {
			this.datos = datos;
			Arrays.sort(this.datos);
		}

		@Override
		public void unMatchedContextVariable(Salary salary, ContextVariable var,
				ContextData contextData, Dato datoSolicitado, Tramo tramo,
				TramoBuilder tramoBuilder, boolean optional) {

			if (Arrays.binarySearch(datos, datoSolicitado.getCodigo()) < 0)
				return;

			DatoBuilder datoBuilder = new DatoBuilder();
			datoBuilder.setCodigo(datoSolicitado.getCodigo());
			datoBuilder.setTipo(datoSolicitado.getTipoDato());

			Double total = MVEL.eval(contextData.getExpression(), Double.class);
			Double interpolated = total * getDaysOf(tramo)
					/ getDaysOf(contextData);

			if (total == 0.00 && optional) {
				System.err.printf(
						"WARN: %s for %s (%s) [%s-%s-%s...%s-%s-%s] fixed value is zero. Not send because is optional  \r\n",
						datoSolicitado.getCodigo(),
						salary.getEmployeeSSNumber(),
						salary.getEmployeeDocument(),
						tramo.getFechaDesde().getDia(),
						tramo.getFechaDesde().getMes(),
						tramo.getFechaDesde().getAnho(),
						tramo.getFechaHasta().getDia(),
						tramo.getFechaHasta().getMes(),
						tramo.getFechaHasta().getAnho(), total);
				throw new Cancel();
			}

			datoBuilder.setImporteEuros(interpolated);

			tramoBuilder.addDato(datoBuilder.create());
			System.err.printf(
					"WARN: %s for %s (%s) [%s-%s-%s...%s-%s-%s] fixed value %f \r\n",
					datoSolicitado.getCodigo(), salary.getEmployeeSSNumber(),
					salary.getEmployeeDocument(),
					tramo.getFechaDesde().getDia(),
					tramo.getFechaDesde().getMes(),
					tramo.getFechaDesde().getAnho(),
					tramo.getFechaHasta().getDia(),
					tramo.getFechaHasta().getMes(),
					tramo.getFechaHasta().getAnho(), total);
		}
	}

	private static class Errors implements BasesCallback {

		private static Map<String, String> DESCRIPTIONS_MAP = new HashMap<String, String>() {
			{

				put("01", "Número de horas realizadas a tiempo parcial");
				put("02", "Número de horas complementarias");

				put("51", "Modalidad de salario");

				put("500", "Base de contingencias comunes");
				put("501", "Base de Horas Extras Fuerza Mayor");
				put("502", "Base de Otras Horas Extras");
				put("537", "Base de horas complementarias");
				put("563", "Compensación IT contingencias comunes");

				put("601", "Base de Accidentes de Trabajo");
				put("611", "Base de Accidentes de Trabajo");
				put("603", "Base de Accidentes de Trabajo en situación de IT");
				put("613", "Base de Accidentes de Trabajo en situación de IT");
				put("663", "Compensación IT AT y EP");
			}
		};

		public void unknownDato(Salary salary, Tramo tramo,
				DatoSolicitado datoSolicitado, TramoBuilder tramoBuilder) {
			boolean mandatory = "B".equalsIgnoreCase(
					datoSolicitado.getIndicadorObligatoriedad());
			System.err.println(String.format("%s: Unknown %s Dato '%s'",
					mandatory ? "ERROR" : "WARN",
					mandatory ? "Mandatory" : "Optional",
					datoSolicitado.getCodigo()));
		}

		@Override
		public void salaryNotFound(String ccc, Trabajador trabajador,
				Periodo mes) {
			System.err.println(
					String.format("ERROR : Salary not found for %s (%s/%s)",
							trabajador.getNaf(), mes.getMes(), mes.getAnho()));
		}

		@Override
		public void unknownSalary(Salary salary) {
			System.err.println(String.format(
					"WARN: Employee %s (%s), not at Cret@ ",
					salary.getEmployeeName(), salary.getEmployeeDocument()));
		}

		@Override
		public void noSuchContextVariable(Salary salary, ContextVariable var,
				Period p, String right) {
			System.err.println(String
					.format("WARN: '%s' (%s) not in salary data", var, right));
		}

		@Override
		public void rightContextVariable(ContextVariable var, Period p,
				String right) {
			System.err.println(String.format(
					"INFO: '%s' (%s) right value at salary data", var, right));
		}

		@Override
		public void wrongContextVariable(Salary salary, ContextVariable var,
				Period p, String right, String wrong) {
			System.err.println(String.format(
					"ERROR: '%s' (%s) wrong value (%s) at salary data", var,
					right, wrong));
		}

		@Override
		public void zeroDato(ContextVariable var, Dato datoSolicitado,
				Tramo tramo, Salary salary) {
			System.err.println(String.format(
					"WARN: %s (%s) for %s [%s-%s-%s...%s-%s-%s] is zero",
					var.getName(), datoSolicitado.getCodigo(),
					salary.getEmployeeName(), tramo.getFechaDesde().getDia(),
					tramo.getFechaDesde().getMes(),
					tramo.getFechaDesde().getAnho(),
					tramo.getFechaHasta().getDia(),
					tramo.getFechaHasta().getMes(),
					tramo.getFechaHasta().getAnho()));
		};

		@Override
		public void noSuchDato(Salary salary, Tramo tramo, Dato datoSolicitado,
				TramoBuilder tramoBuilder, boolean optional) {

			System.err.println(String.format(
					"%s: Dato '%s' for %s (%s) [%s-%s-%s...%s-%s-%s] not found",
					(optional ? "WARN" : "ERROR"), datoSolicitado.getCodigo(),
					salary.getEmployeeName(), salary.getEmployeeSSNumber(),
					tramo.getFechaDesde().getDia(),
					tramo.getFechaDesde().getMes(),
					tramo.getFechaDesde().getAnho(),
					tramo.getFechaHasta().getDia(),
					tramo.getFechaHasta().getMes(),
					tramo.getFechaHasta().getAnho()));
		}

		@Override
		public void unMatchedContextVariable(Salary salary, ContextVariable var,
				ContextData contextData, Dato datoSolicitado, Tramo tramo,
				TramoBuilder tramoBuilder, boolean optional) {
			// @formatter:off
			System.err
					.println(String
							.format("ERROR: %s for %s [%3$td-%3$tm-%3$tY...%4$td-%4$tm-%4$tY] not match %5$s [%6$s-%7$s-%8$s...%9$s-%10$s-%11$s]",

							var.getName(), salary.getEmployeeName(),
									contextData.getStartDate(), contextData
											.getEndDate(),

									datoSolicitado.getCodigo(), tramo
											.getFechaDesde().getDia(), tramo
											.getFechaDesde().getMes(), tramo
											.getFechaDesde().getAnho(), tramo
											.getFechaHasta().getDia(), tramo
											.getFechaHasta().getMes(), tramo
											.getFechaHasta().getAnho()));
			// @formatter:on
		}

		@Override
		public void wrongTrabajadoresTramosIs(InputStream trabajadoresTramosIs,
				Exception e) {
			// @formatter:off
			System.err
					.println(String
							.format("ERROR: Fichero de Trabajadores y Tramos no válido " ));
			// @formatter:on
		}
	}

	private static class Comments extends Listener implements BasesCallback {

		private static class Data {

			String ss;
			String doc;
			String name;

			public Data(String name, String doc, String ss) {
				this.ss = ss;
				this.doc = doc;
				this.name = name;
			}
		}

		private static class TrabajadorData extends Data {

			Trabajador<?> trabajadorCreta;
			net.aonsolutions.tgss.creta.jaxb.bases.Trabajador trabajadorAon;

			public TrabajadorData(String name, String nif, String ss,
					Trabajador<?> trabajadorCreta,
					net.aonsolutions.tgss.creta.jaxb.bases.Trabajador trabajadorAon) {
				super(name, nif, ss);
				this.trabajadorCreta = trabajadorCreta;
				this.trabajadorAon = trabajadorAon;
			}
		}

		private XMLStreamWriter xsw;

		private Map<String, Data> addedEnterpriseDataMap;

		private Stack<TrabajadorData> skippedTrabajadorList;
		private Map<String, TrabajadorData> addedTrabajadorDataMap;

		private Tramo<?> tramoCreta;
		private Trabajador<?> trabajadorCreta;

		public Comments(XMLStreamWriter xsw) {
			super();
			this.xsw = xsw;
			this.skippedTrabajadorList = new Stack<TrabajadorData>();
			this.addedEnterpriseDataMap = new HashMap<String, Data>();
			this.addedTrabajadorDataMap = new HashMap<String, TrabajadorData>();
		}

		@Override
		public void beforeMarshal(Object source) {

			if (source instanceof net.aonsolutions.tgss.creta.jaxb.bases.Trabajador)
				beforeMarshalTrabajador(
						(net.aonsolutions.tgss.creta.jaxb.bases.Trabajador) source);
			else if (source instanceof net.aonsolutions.tgss.creta.jaxb.bases.Dato)
				beforeMarshalDato(
						(net.aonsolutions.tgss.creta.jaxb.bases.Dato) source);
			else if (source instanceof net.aonsolutions.tgss.creta.jaxb.bases.CtaCot)
				beforeMarshalCtaCot(
						(net.aonsolutions.tgss.creta.jaxb.bases.CtaCot) source);
			else if (source instanceof net.aonsolutions.tgss.creta.jaxb.bases.Tramo)
				beforeMarshalTramo(
						(net.aonsolutions.tgss.creta.jaxb.bases.Tramo) source);

			super.beforeMarshal(source);
		}

		@Override
		public void trabajadorAdded(
				net.aonsolutions.tgss.creta.jaxb.bases.Trabajador trabajadorAon,
				Trabajador trabajadorCreta, Salary salary) {

			if (!addedTrabajadorDataMap.containsKey(trabajadorCreta.getNaf()))
				addedTrabajadorDataMap.put(trabajadorCreta.getNaf(),
						new TrabajadorData(salary.getEmployeeName(),
								salary.getEmployeeDocument(),
								salary.getEmployeeSSNumber(), trabajadorCreta,
								trabajadorAon));

			if (!addedEnterpriseDataMap.containsKey(salary.getEnterpriseCCC()))
				addedEnterpriseDataMap.put(salary.getEnterpriseCCC(),
						new Data(salary.getEnterpriseName(),
								salary.getEmployeeDocument(),
								salary.getEnterpriseCCC()));
		};

		@Override
		public void trabajadorSkipped(
				net.aonsolutions.tgss.creta.jaxb.bases.Trabajador trabajadorAon,
				Trabajador trabajadorCreta, Salary salary) {
			// skippedTrabajadorList.add(new TrabajadorData(salary
			// .getEmployeeName(), salary.getEmployeeDocument(), salary
			// .getEmployeeSSNumber(), trabajadorAon));
		};

		public void beforeMarshalTrabajador(
				net.aonsolutions.tgss.creta.jaxb.bases.Trabajador trabajador) {
			marshallSkipped();
			TrabajadorData data = addedTrabajadorDataMap
					.get(trabajador.getNaf());
			try {
				xsw.writeComment(String.format("%s [%s]", data.name, data.doc));
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			trabajadorCreta = data.trabajadorCreta;
		}

		public void beforeMarshalCtaCot(
				net.aonsolutions.tgss.creta.jaxb.bases.CtaCot ctaCot) {
			String ccc = ctaCot.getProvincia() + ctaCot.getNumero();
			Data data = addedEnterpriseDataMap.get(ccc);
			try {
				xsw.writeComment(String.format("%s [%s]", data.name, data.doc));
			} catch (XMLStreamException e) {
			} catch (NullPointerException e) {
			}
		}

		public void beforeMarshalDato(
				net.aonsolutions.tgss.creta.jaxb.bases.Dato datoAon) {

			if (tramoCreta == null)
				return;

			for (Dato datoCreta : tramoCreta.getDatosTramo().getDato()) {
				if (!datoCreta.getTipoDato()
						.equalsIgnoreCase(datoAon.getTipoDato()))
					continue;
				if (!datoCreta.getCodigo()
						.equalsIgnoreCase(datoAon.getCodigo()))
					continue;

				if (AonStringUtils.isEmpty(datoCreta.getValor()))
					return;

				try {
					different(datoAon, datoCreta);
				} catch (Different d) {
					try {
						if (datoCreta.getTipoDato().equalsIgnoreCase("I"))
							xsw.writeComment(String.format("SDL-Cret@ : %s",
									datoCreta.getValor()));
						else
							xsw.writeComment(String.format("SDL-Cret@ : %d",
									Long.parseLong(datoCreta.getValor())));
					} catch (XMLStreamException e) {
					}
				}

				return;
			}
		}

		public void beforeMarshalTramo(
				net.aonsolutions.tgss.creta.jaxb.bases.Tramo tramoAon) {
			for (Tramo<?> tramo : trabajadorCreta.getTramos().getTramo()) {
				if (compare(tramo, tramoAon) == 0) {
					tramoCreta = tramo;
					return;
				}
			}
		}

		// --------------------------------------------------------------------

		private void marshallSkipped() {
			while (!skippedTrabajadorList.isEmpty()) {
				TrabajadorData skippedTrabajador = skippedTrabajadorList.pop();
				try {
					StringWriter writer = new StringWriter();
					Marshaller marshaller = JAXBContext
							.newInstance(
									skippedTrabajador.trabajadorAon.getClass())
							.createMarshaller();
					// marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
					// "");
					marshaller.setProperty(Marshaller.JAXB_FRAGMENT,
							Boolean.TRUE);
					marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
							Boolean.TRUE);
					// marshaller.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION,
					// "http://www.seg-social.es/creta/esquemas/V100/Bases");
					marshaller.marshal(skippedTrabajador.trabajadorAon, writer);

					xsw.writeComment(String.format(
							"\r\n%s\r\nNúmero afiliación a la Seguridad Social:\r\n%s\r\nNúmero de identificación fiscal de las personas físicas:\r\n%s\r\n%s\r\n",
							skippedTrabajador.name, skippedTrabajador.ss,
							skippedTrabajador.doc, writer.toString()));

				} catch (XMLStreamException e) {
					// TODO Auto-generated catch block
				} catch (JAXBException e) {
					e.printStackTrace();
					// TODO Auto-generated catch block
				}

			}

		}

	}

	private static interface CretaData {

		String getComment();

		void add(Salary salary, Tramo tramo, Dato datoSolicitado,
				TramoBuilder tramoBuilder, BasesCallback... cb);

	}

	private static abstract class AbstractCCretaData implements CretaData {

		protected abstract boolean isOptional();

		protected abstract Double get(Salary salary, Fecha desde, Fecha hasta)
				throws NoSuchContextVariableException,
				UnMatchedContextVariableException;

		protected abstract void zeroValue(Salary salary, Tramo tramo,
				Dato datoSolicitado, TramoBuilder tramoBuilder,
				BasesCallback... cbs);

		// CretaData ----------------------------------------------------------
		@Override
		public void add(Salary salary, Tramo tramo, Dato datoSolicitado,
				TramoBuilder tramoBuilder, BasesCallback... cbs) {
			// B -> El código del dato solicitado es obligatorio.
			// P -> El código del dato solicitado es opcional.

			boolean optional = isOptional();
			try {
				double newValue = get(salary, tramo.getFechaDesde(),
						tramo.getFechaHasta());

				if (optional && newValue == 0.00)
					return;

				if (newValue == 0.00)
					zeroValue(salary, tramo, datoSolicitado, tramoBuilder, cbs);

				DatoBuilder datoBuilder = new DatoBuilder();
				datoBuilder.setCodigo(datoSolicitado.getCodigo());
				datoBuilder.setTipo(datoSolicitado.getTipoDato());
				datoBuilder.setImporteEuros(newValue);
				tramoBuilder.addDato(datoBuilder.create());
			} catch (NoSuchContextVariableException e) {
				for (BasesCallback cb : cbs)
					cb.noSuchDato(salary, tramo, datoSolicitado, tramoBuilder,
							optional);
			} catch (UnMatchedContextVariableException e) {
				try {
					for (BasesCallback cb : cbs)
						cb.unMatchedContextVariable(salary,
								e.getContextVariable(), e.getContextData(),
								datoSolicitado, tramo, tramoBuilder, optional);
				} catch (Cancel c) {

				}
			}
		}

	}

	private static abstract class CContextCretaData extends AbstractCCretaData {

		protected ContextVariable contextVariable;

		public CContextCretaData(ContextVariable contextVariable) {
			this.contextVariable = contextVariable;
		}

		// AbstractCCretaData -------------------------------------------------

		@Override
		public String getComment() {
			return contextVariable.name();
		}

		@Override
		public Double get(Salary salary, Fecha desde, Fecha hasta)
				throws NoSuchContextVariableException,
				UnMatchedContextVariableException {
			return get(contextVariable, salary,
					new Period(toDate(desde), toDate(hasta)));
		}

		@Override
		protected void zeroValue(Salary salary, Tramo tramo,
				Dato datoSolicitado, TramoBuilder tramoBuilder,
				BasesCallback... cbs) {
			for (BasesCallback cb : cbs)
				cb.zeroDato(contextVariable, datoSolicitado, tramo, salary);
		}

		// --------------------------------------------------------------------

		protected static Double get(ContextVariable contextVariable,
				Salary salary, Period p) throws NoSuchContextVariableException,
						UnMatchedContextVariableException {
			List<ContextData> datas = salary.getContextData()
					.get(contextVariable.getName());
			if (datas == null || datas.isEmpty())
				throw new NoSuchContextVariableException(contextVariable);

			double ret = 0.00;
			boolean found = false;

			for (ContextData data : datas) {

				Period dataPeriod = new Period(data.getStartDate(),
						data.getEndDate());

				Period intersect = p.intersect(dataPeriod);
				if (intersect == null)
					continue;

				if (data.getStartDate().before(p.getStart())
						|| data.getEndDate().after(p.getEnd()))
					throw new UnMatchedContextVariableException(contextVariable,
							data);

				found = true;
				ret += ExpressionContext.eval(data.getExpression(),
						Double.class) * days(intersect) / days(dataPeriod);
			}

			if (!found)
				throw new UnMatchedContextVariableException(contextVariable,
						datas.get(0));

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

	private static abstract class CompositeCContextCretaData
			extends AbstractCCretaData {

		private ContextVariable contextVariables[];

		public CompositeCContextCretaData(ContextVariable... contextVariables) {
			this.contextVariables = contextVariables;
		}

		// AbstractCCretaData -------------------------------------------------

		@Override
		public String getComment() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void add(Salary salary, Tramo tramo, Dato datoSolicitado,
				TramoBuilder tramoBuilder, BasesCallback... cbs) {
			try {
				super.add(salary, tramo, datoSolicitado, tramoBuilder, cbs);
			} catch (ZeroValueException e) {
				for (BasesCallback cb : cbs)
					cb.zeroDato(e.getContextVariable(), datoSolicitado, tramo,
							salary);
			} catch (NoSuchContextVariablesException e) {
				for (BasesCallback cb : cbs)
					cb.noSuchDato(salary, tramo, datoSolicitado, tramoBuilder,
							isOptional());
			}
		}

		@Override
		public Double get(Salary salary, Fecha desde, Fecha hasta)
				throws NoSuchContextVariableException,
				UnMatchedContextVariableException {
			Period p = new Period(toDate(desde), toDate(hasta));
			for (ContextVariable contextVariable : contextVariables) {
				try {
					Double value = CContextCretaData.get(contextVariable,
							salary, p);
					if (value == 0.00)
						throw new ZeroValueException(contextVariable);
					// System.err.println("--- CompositeCContextCretaData " +
					// contextVariable.getName() + ", " +
					// salary.getEmployeeName() + " = " + value );
					return value;
				} catch (NoSuchContextVariableException e) {
					// Try next variable
				}
			}
			throw new NoSuchContextVariablesException(contextVariables);
		}

		@Override
		protected void zeroValue(Salary salary, Tramo tramo,
				Dato datoSolicitado, TramoBuilder tramoBuilder,
				BasesCallback... cbs) {
			// Nothing 'ZeroValueException' .
		}

		// --------------------------------------------------------------------
	}

	private static class MandatoryCompositecContextData
			extends CompositeCContextCretaData {

		public MandatoryCompositecContextData(
				ContextVariable... contextVariables) {
			super(contextVariables);
		}

		// CompositeCContextCretaData -----------------------------------------
		@Override
		protected boolean isOptional() {
			return false;
		}
	}

	private static Map<String, CretaData> CONTEXT_VARIABLE_MAP = new HashMap<String, CretaData>() {
		{
			put("500", new MandatoryCContextCretaData(CGC_BASE));

			put("501", new OptionalCContextCretaData(STRUCTURAL_OVERTIME_BASE));
			put("502", new OptionalCContextCretaData(
					NON_STRUCTURAL_OVERTIME_BASE));
			// put("537", new OptionalCContextCretaData(
			// NON_STRUCTURAL_OVERTIME_BASE));
			put("601", new MandatoryCContextCretaData(CGP_BASE));
			put("611", new MandatoryCContextCretaData(CGP_BASE));

			put("01", new MandatoryHContextCretaData(WORKED_HOURS) {
				@Override
				public Double get(Salary salary, Fecha desde, Fecha hasta)
						throws NoSuchContextVariableException,
						UnMatchedContextVariableException {
					try {
						return super.get(salary, desde, hasta);
					} catch (NoSuchContextVariableException e) {
						return getWorkedHours(salary, desde, hasta);
					}
				};
			});
			put("02", new OptionalHContextCretaData(EXTRA_HOURS));

			put("51", new MonthlySalaryCretaData());

			put("509", new MandatoryCompositecContextData(MATERNITY_BASE,
					ERE_BASE));
			put("603", new MandatoryCompositecContextData(MATERNITY_BASE,
					ERE_BASE, CGP_BASE));
			put("613", new MandatoryCompositecContextData(MATERNITY_BASE,
					ERE_BASE));
		}
	};

	// ------------------------------------------------------------------------

	private static <D extends DatoSolicitado> void bases(
			BasesBuilder basesBuilder, AONContext ctx,
			Liquidacion<?, ?, ?, ?> liquidacion, boolean aceptarBasesAnteriores,
			BasesCallback... cbs) {
		basesBuilder.addLiquidacion(
				liquidacion(ctx, liquidacion, aceptarBasesAnteriores, cbs));
	}

	private static <D extends DatoSolicitado> net.aonsolutions.tgss.creta.jaxb.bases.Liquidacion liquidacion(
			AONContext ctx, Liquidacion<?, ?, ?, ?> liquidacion,
			boolean aceptarBasesAnteriores, BasesCallback... cbs) {
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
			liquidacionBuilder.setCCCConcertado(
					liquidacion.getCccConcertado().getRegimen(),
					liquidacion.getCccConcertado().getProvincia(),
					liquidacion.getCccConcertado().getNumero());

		if (liquidacion.getFechaControl() != null)
			liquidacionBuilder
					.setMesControl(liquidacion.getFechaControl().getMes())
					.setAnhoControl(liquidacion.getFechaControl().getAnho());

		datosLiquidacion(liquidacion, liquidacionBuilder, cbs);

		for (LiquidacionMes<?> liquidacionMes : liquidacion
				.getLiquidacionMes()) {

			Map<String, Trabajador<D>> trabajadores = new HashMap<String, Trabajador<D>>();

			for (Trabajador<D> trabajador : liquidacionMes.getTrabajadores()
					.getTrabajador())
				trabajadores.put(trabajador.getNaf(), trabajador);

			LiquidacionMesBuilder liquidacionMesBuilder = new LiquidacionMesBuilder();

			if (liquidacionMes.getMesLiquidativo() != null)
				liquidacionMesBuilder
						.setMes(liquidacionMes.getMesLiquidativo().getMes())
						.setAnho(liquidacionMes.getMesLiquidativo().getAnho());

			if (liquidacionMes.getDatosMes() != null)
				for (Dato dato : liquidacionMes.getDatosMes()
						.getDatoSolicitado())
					liquidacionMesBuilder
							.add(new DatoBuilder().setCodigo(dato.getCodigo())
									.setTipo(dato.getTipoDato()).create());

			trabajadores(liquidacionMesBuilder, ctx, liquidacion.getCcc(),
					liquidacionMes.getMesLiquidativo(), trabajadores, cbs);

			liquidacionBuilder
					.addLiquidacionMes(liquidacionMesBuilder.create());

		}

		return liquidacionBuilder.create();
	}

	private static void datosLiquidacion(Liquidacion<?, ?, ?, ?> liquidacion,
			LiquidacionBuilder liquidacionBuilder, BasesCallback... cbs) {

		DatosLiquidacion<DatoSolicitado> datosLiquidacion = liquidacion
				.getDatosLiquidacion();
		if (datosLiquidacion == null)
			return;

		for (DatoSolicitado dato : datosLiquidacion.getDato()) {
			try {
				for (BasesCallback cb : cbs)
					cb.unknownDato(liquidacion, dato, liquidacionBuilder);
			} catch (Cancel e) {
			}
		}

	}

	private static <D extends DatoSolicitado> void trabajadores(
			LiquidacionMesBuilder liquidacionMesBuilder, AONContext ctx,
			CtaCot ctaCot, Periodo mesLiquidativo,
			Map<String, Trabajador<D>> trabajadores, BasesCallback... cbs) {

		String ccc = String.format("%s%s", ctaCot.getProvincia(),
				ctaCot.getNumero());

		Calendar calendar = toCalendar(mesLiquidativo);
		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		Date startDate = calendar.getTime();
		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date endDate = calendar.getTime();

		// @formatter:off
		AON.getSalaryData(
				ctx,
				props -> props.getCCCProperty().eq(ccc)
						.and(props.getEndDateProperty().ge(startDate))
						.and(props.getStartDateProperty().le(endDate))
						.and(props.getIsSalaryProperty().eq(true))).forEach(
				salary -> trabajador(liquidacionMesBuilder, salary,
						trabajadores, cbs));
		;
		// @formatter:on

		trabajadores.values().forEach(t -> {
			for (BasesCallback cb : cbs)
				cb.salaryNotFound(ccc, t, mesLiquidativo);
		});

	}

	private static <D extends DatoSolicitado> void trabajador(
			LiquidacionMesBuilder liquidacionMesBuilder, Salary salary,
			Map<String, Trabajador<D>> trabajadores, BasesCallback... cbs) {

		TrabajadorBuilder trabajadorBuilder = new TrabajadorBuilder();
		Trabajador<D> trabajador = trabajadores
				.get(salary.getEmployeeSSNumber());
		if (trabajador == null) {
			for (BasesCallback cb : cbs)
				cb.unknownSalary(salary);
			return;
		}

		trabajadorBuilder.setNaf(trabajador.getNaf());
		for (Tramo<D> tramo : trabajador.getTramos().getTramo()) {

			try {
				checkTramo(tramo, salary, cbs);
			} catch (UnsupportedOperationException e) {

			}
			
			// Tramo without Dato, that's imposible . TODO: MarcaBorrado ?
			if ( tramo.getDatosTramo() == null || 
				tramo.getDatosTramo().getDatoSolicitado() == null ||
				tramo.getDatosTramo().getDatoSolicitado().isEmpty())
				continue;

			TramoBuilder tramoBuilder = new TramoBuilder();

			tramoBuilder.setDiaDesde(tramo.getFechaDesde().getDia());
			tramoBuilder.setMesDesde(tramo.getFechaDesde().getMes());
			tramoBuilder.setAnhoDesde(tramo.getFechaDesde().getAnho());

			tramoBuilder.setDiaHasta(tramo.getFechaHasta().getDia());
			tramoBuilder.setMesHasta(tramo.getFechaHasta().getMes());
			tramoBuilder.setAnhoHasta(tramo.getFechaHasta().getAnho());

			for (DatoSolicitado datoSolicitado : tramo.getDatosTramo()
					.getDatoSolicitado()) {

				CretaData data = getCretaData(datoSolicitado.getCodigo());

				if (data == null) {
					try {
						for (BasesCallback cb : cbs)
							cb.unknownDato(salary, tramo, datoSolicitado,
									tramoBuilder);
					} catch (Cancel e) {
					}
					continue;
				}

				data.add(salary, tramo, datoSolicitado, tramoBuilder, cbs);
			}
			
			trabajadorBuilder.addTramo(tramoBuilder.create());
		}
		

		trabajadores.remove(salary.getEmployeeSSNumber());

		net.aonsolutions.tgss.creta.jaxb.bases.Trabajador trabajadorAon = trabajadorBuilder
				.create();
		
		try {
			checkTrabajador(trabajadorAon);
		} catch ( InvalidTrabajador e ){
			return;
		}
		
		if ( trabajadorAon.getTramos().getTramo().isEmpty() ) {
			for (BasesCallback cb : cbs)
				cb.trabajadorEmpty(trabajadorAon, trabajador, salary);
			return;
		}
		
		try {
			for (BasesCallback cb : cbs)
				cb.trabajadorAdded(trabajadorAon, trabajador, salary);

			liquidacionMesBuilder.add(trabajadorAon);
		} catch (SkipExisting e) {
			for (BasesCallback cb : cbs)
				cb.trabajadorSkipped(trabajadorAon, trabajador, salary);

		}
	}

	private static void checkTramo(Tramo<?> tramo, Salary salary,
			BasesCallback... cbs) {
		Period p = new Period(toDate(tramo.getFechaDesde()),
				toDate(tramo.getFechaHasta()));

		checkContextVariable(QUOTE_GROUP,
				Integer.parseInt(
						tramo.getInformacionAfiliacion().getGrupoCotizacion()),
				p, salary, cbs);
		checkContextVariable(TC2,
				tramo.getInformacionAfiliacion().getTipoContrato(), p, salary,
				cbs);
		checkContextVariable(OCCUPATION,
				tramo.getInformacionAfiliacion().getOcupacion(), p, salary,
				cbs);

		String partialFactor = tramo.getInformacionAfiliacion()
				.getCoeficienteTiempoParcial();
		if (partialFactor != null)
			checkContextVariable(PARTIAL_FACTOR,
					(partialFactor != null
							? Integer.parseInt(partialFactor) / 1000.00 : null),
					3, p, salary, cbs);
		// for (Peculiaridad peculiaridad : tramo.getInformacionAfiliacion()
		// .getPeculiaridades().getPeculiaridad()) {
		//
		// }

	}

	private static void checkContextVariable(ContextVariable contextVariable,
			String rigthValue, Period p, Salary salary, BasesCallback... cbs) {

		try {
			String salaryValue = get(salary, contextVariable, p);

			if (!StringUtils.equalsIgnoreCase(rigthValue, salaryValue))
				for (BasesCallback cb : cbs)
					cb.wrongContextVariable(salary, contextVariable, p,
							rigthValue, salaryValue);
			else
				for (BasesCallback cb : cbs)
					cb.rightContextVariable(contextVariable, p, rigthValue);

		} catch (NoSuchContextVariableException e) {
			if (rigthValue != null)
				for (BasesCallback cb : cbs)
					cb.noSuchContextVariable(salary, contextVariable, p,
							rigthValue);
		} catch (AmbiguousContextVariableException e) {
			for (BasesCallback cb : cbs)
				cb.ambigousContextVariable(salary, contextVariable, p,
						rigthValue, e.getValues());
		}
	}

	private static void checkContextVariable(ContextVariable contextVariable,
			Integer rigthValue, Period p, Salary salary, BasesCallback... cbs) {
		String salaryString = null;
		try {

			salaryString = get(salary, contextVariable, p);
			
			if (rigthValue == null && salaryString == null)
				return;

			if (rigthValue == null && salaryString != null)
				for (BasesCallback cb : cbs)
					cb.wrongContextVariable(salary, contextVariable, p, null,
							salaryString);

			Integer salaryValue = Integer.parseInt(salaryString);

			if (!rigthValue.equals(salaryValue))
				for (BasesCallback cb : cbs)
					cb.wrongContextVariable(salary, contextVariable, p,
							Integer.toString(rigthValue), salaryString);
			else
				for (BasesCallback cb : cbs)
					cb.rightContextVariable(contextVariable, p,
							Integer.toString(rigthValue));

		} catch (NumberFormatException e) {
			for (BasesCallback cb : cbs)
				cb.wrongContextVariable(salary, contextVariable, p,
						Double.toString(rigthValue), salaryString);

		} catch (NoSuchContextVariableException e) {
			if (rigthValue != null)
				for (BasesCallback cb : cbs)
					cb.noSuchContextVariable(salary, contextVariable, p,
							Integer.toString(rigthValue));
		} catch (AmbiguousContextVariableException e) {
			for (BasesCallback cb : cbs)
				cb.ambigousContextVariable(salary, contextVariable, p,
						Integer.toString(rigthValue), e.getValues());
		}
	}

	private static void checkContextVariable(ContextVariable contextVariable,
			Double rigthValue, int decimals, Period p, Salary salary,
			BasesCallback... cbs) {

		String salaryString = null;
		try {
			salaryString = get(salary, contextVariable, p);

			if (rigthValue == null && salaryString == null)
				return;

			if (rigthValue == null && salaryString != null)
				for (BasesCallback cb : cbs)
					cb.wrongContextVariable(salary, contextVariable, p, null,
							salaryString);

			Double salaryValue = Double.parseDouble(salaryString);
			salaryValue = Math.round(salaryValue * Math.pow(10.00, decimals))
					/ Math.pow(10.00, decimals);

			if (!rigthValue.equals(salaryValue))
				for (BasesCallback cb : cbs)
					cb.wrongContextVariable(salary, contextVariable, p,
							Double.toString(rigthValue), salaryString);
			else
				for (BasesCallback cb : cbs)
					cb.rightContextVariable(contextVariable, p,
							Double.toString(rigthValue));

		} catch (NumberFormatException e) {
			for (BasesCallback cb : cbs)
				cb.wrongContextVariable(salary, contextVariable, p,
						Double.toString(rigthValue), salaryString);

		} catch (NoSuchContextVariableException e) {
			if (rigthValue != null)
				for (BasesCallback cb : cbs)
					cb.noSuchContextVariable(salary, contextVariable, p,
							Double.toString(rigthValue));
		} catch (AmbiguousContextVariableException e) {
			for (BasesCallback cb : cbs)
				cb.ambigousContextVariable(salary, contextVariable, p,
						Double.toString(rigthValue), e.getValues());
		}
	}

	private static long days(Period p) {
		return AonDateUtils.getDaysBetweenDates(p.getStart(), p.getEnd()) + 1;
	}

	private static String get(Salary salary, ContextVariable contextVariable,
			Period p) throws NoSuchContextVariableException,
					AmbiguousContextVariableException {
		List<ContextData> datas = salary.getContextData()
				.get(contextVariable.getName());

		if (datas == null)
			throw new NoSuchContextVariableException(contextVariable);

		String expression = null;
		for (ContextData data : datas) {
			Period intersect = p.intersect(
					new Period(data.getStartDate(), data.getEndDate()));
			if (intersect == null)
				continue;
			String dataExpression;

			try {
				dataExpression = MVEL.eval(data.getExpression()).toString();
			} catch (Throwable t) {
				dataExpression = data.getExpression();
			}

			if (expression == null || expression.equals(dataExpression))
				expression = dataExpression;
			else
				throw new AmbiguousContextVariableException(contextVariable,
						expression, dataExpression);
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
			AONContext ctx, boolean aceptarBasesAnteriores, XMLStreamWriter xsw,
			BasesCallback... cbs) {

		BasesBuilder builder = new BasesBuilder()
				.setAutorizado(trabajadoresTramos.getAutorizado());

		bases(builder, ctx, trabajadoresTramos.getLiquidacion(),
				aceptarBasesAnteriores, cbs);

		return builder.create();

	}

	private static net.aonsolutions.tgss.creta.jaxb.bases.Liquidacion liquidacion(
			net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresTramos,
			AONContext ctx, boolean aceptarBasesAnteriores, XMLStreamWriter xsw,
			BasesCallback... cbs) {
		return liquidacion(ctx, trabajadoresTramos.getLiquidacion(),
				aceptarBasesAnteriores, cbs);
	}

	private static net.aonsolutions.tgss.creta.jaxb.bases.Bases bases(
			net.aonsolutions.tgss.creta.jaxb.respuesta.Respuesta respuesta,
			AONContext ctx, boolean aceptarBasesAnteriores, XMLStreamWriter xsw,
			BasesCallback... cbs) {

		BasesBuilder builder = new BasesBuilder()
				.setAutorizado(respuesta.getAutorizado());

		for (Liquidacion<?, ?, ?, ?> liquidacion : respuesta.getLiquidacion())
			bases(builder, ctx, liquidacion, aceptarBasesAnteriores, cbs);

		return builder.create();

	}

	private static List<net.aonsolutions.tgss.creta.jaxb.bases.Liquidacion> liquidaciones(
			net.aonsolutions.tgss.creta.jaxb.respuesta.Respuesta respuesta,
			AONContext ctx, boolean aceptarBasesAnteriores, XMLStreamWriter xsw,
			BasesCallback... cbs) {

		List<net.aonsolutions.tgss.creta.jaxb.bases.Liquidacion> liquidaciones = new ArrayList<net.aonsolutions.tgss.creta.jaxb.bases.Liquidacion>();

		for (Liquidacion<?, ?, ?, ?> liquidacion : respuesta.getLiquidacion())
			liquidaciones.add(
					liquidacion(ctx, liquidacion, aceptarBasesAnteriores, cbs));

		return liquidaciones;

	}

	private static Double getWorkedHours(Salary salary, Fecha desde,
			Fecha hasta) throws NoSuchContextVariableException {

		final Map<Integer, ContextVariable> DAYS_HOURS = new HashMap<Integer, ContextVariable>() {
			{
				put(Calendar.MONDAY, ContextVariable.MONDAY_HOURS);
				put(Calendar.TUESDAY, ContextVariable.TUESDAY_HOURS);
				put(Calendar.WEDNESDAY, ContextVariable.WEDNESDAY_HOURS);
				put(Calendar.THURSDAY, ContextVariable.THURSDAY_HOURS);
				put(Calendar.FRIDAY, ContextVariable.FRIDAY_HOURS);
				put(Calendar.SATURDAY, ContextVariable.SATURDAY_HOURS);
				put(Calendar.SUNDAY, ContextVariable.SUNDAY_HOURS);
			}
		};

		double hours = 0;

		Date end = toDate(hasta);

		Calendar calendar = toCalendar(desde);
		Date date = calendar.getTime();
		while (date.compareTo(end) <= 0) {

			ContextVariable var = DAYS_HOURS
					.get(calendar.get(Calendar.DAY_OF_WEEK));

			List<ContextData> hourDatas = salary.getContextData(var.getName(),
					date, date);

			for (ContextData hourData : hourDatas)
				hours += MVEL.eval(hourData.getExpression(), Double.class);

			calendar.add(Calendar.DAY_OF_MONTH, 1);
			date = calendar.getTime();
		}

		return hours;
	}

	// ------------------------------------------------------------------------

	private static int compare(Dato d1, Dato d2) {
		int compare = d1.getTipoDato().compareToIgnoreCase(d2.getTipoDato());
		if (compare != 0)
			return compare;

		return d1.getCodigo().compareToIgnoreCase(d2.getCodigo());
	}

	private static int compare(net.aonsolutions.tgss.creta.jaxb.bases.Dato d1,
			net.aonsolutions.tgss.creta.jaxb.bases.Dato d2) {
		int compare = d1.getTipoDato().compareToIgnoreCase(d2.getTipoDato());
		if (compare != 0)
			return compare;

		return d1.getCodigo().compareToIgnoreCase(d2.getCodigo());
	}

	private static int compare(Tramo t1, Tramo t2) {
		return toDate(t1.getFechaDesde()).compareTo(toDate(t2.getFechaDesde()));
	}

	private static int compare(net.aonsolutions.tgss.creta.jaxb.bases.Tramo t1,
			net.aonsolutions.tgss.creta.jaxb.bases.Tramo t2) {
		return toDate(t1.getFechaDesde().getAnho(), t1.getFechaDesde().getMes(),
				t1.getFechaDesde().getDia())
						.compareTo(toDate(t2.getFechaDesde().getAnho(),
								t2.getFechaDesde().getMes(),
								t2.getFechaDesde().getDia()));
	}

	private static int compare(Tramo t1,
			net.aonsolutions.tgss.creta.jaxb.bases.Tramo t2) {
		return toDate(t1.getFechaDesde()).compareTo(toDate(
				t2.getFechaDesde().getAnho(), t2.getFechaDesde().getMes(),
				t2.getFechaDesde().getDia()));
	}

	private static void different(
			net.aonsolutions.tgss.creta.jaxb.bases.Dato datoAon,
			Dato datoCreta) {

		if (AonStringUtils.isEmpty(datoCreta.getValor()))
			throw new Different(); // Nuevo trabajador ???.

		if ("C".equalsIgnoreCase(datoCreta.getTipoDato())) {
			if (Long.parseLong(datoAon.getValor()) != Long
					.parseLong(datoCreta.getValor()))
				throw new Different(); // El tipo de dato se refiere a
										// "Concepto".
		} else if ("H".equalsIgnoreCase(datoCreta.getTipoDato())) {
			if (Long.parseLong(datoAon.getValor()) != Long
					.parseLong(datoCreta.getValor()))
				throw new Different(); // El tipo de dato se refiere a "Horas".
		} else if ("I".equalsIgnoreCase(datoCreta.getTipoDato())) {
			if (!datoAon.getValor().equalsIgnoreCase(datoCreta.getValor()))
				throw new Different(); // El tipo de dato se refiere a
										// "Indicador".
		}
	}

	private static void different(
			net.aonsolutions.tgss.creta.jaxb.bases.Tramo tramoAon,
			Tramo tramoCreta) {
		Date dateFromAon = toDate(tramoAon.getFechaDesde().getAnho(),
				tramoAon.getFechaDesde().getMes(),
				tramoAon.getFechaDesde().getDia());
		Date dateFromCreta = toDate(tramoCreta.getFechaDesde());
		if (dateFromAon.compareTo(dateFromCreta) != 0)
			throw new Different(); // Don't skip. Diffente 'FechaDesde'.

		Date dateToAon = toDate(tramoAon.getFechaHasta().getAnho(),
				tramoAon.getFechaHasta().getMes(),
				tramoAon.getFechaHasta().getDia());
		Date dateToCreta = toDate(tramoCreta.getFechaHasta());
		if (dateToAon.compareTo(dateToCreta) != 0)
			throw new Different(); // Don't skip. Diffente 'FechaHasta'.

		List<DatoSolicitado> datosCreta = new ArrayList<DatoSolicitado>(
				tramoCreta.getDatosTramo().getDato());
		// List<net.aonsolutions.tgss.creta.jaxb.DatoSolicitado> datosCreta =
		// tramoCreta
		// .getDatosTramo().getDato();
		Collections.sort(datosCreta, Bases::compare);
		List<net.aonsolutions.tgss.creta.jaxb.bases.Dato> datosAon = new ArrayList<net.aonsolutions.tgss.creta.jaxb.bases.Dato>(
				tramoAon.getDatosTramo().getDato());
		Collections.sort(datosAon, Bases::compare);

		int j = 0;
		for (int i = 0; i < datosCreta.size(); i++) {
			DatoSolicitado datoCreta = datosCreta.get(i);
			// net.aonsolutions.tgss.creta.jaxb.DatoSolicitado datoCreta =
			// datosCreta
			// .get(i);

			if (j >= datosAon.size())
				throw new Different();

			net.aonsolutions.tgss.creta.jaxb.bases.Dato datoAon = datosAon
					.get(j);

			if (!datoCreta.getTipoDato().equalsIgnoreCase(datoAon.getTipoDato())
					|| !datoCreta.getCodigo()
							.equalsIgnoreCase(datoAon.getCodigo())) {
				// El dato solicitado es obligatorio.
				if ("B".equalsIgnoreCase(
						datoCreta.getIndicadorObligatoriedad()))
					throw new Different(); // ???

				if (AonStringUtils.isNotEmpty(datoCreta.getValor()))
					throw new Different(); // ???

				continue;
			}

			different(datoAon, datoCreta);
			j++;

		}

	}

	private static void skip(
			net.aonsolutions.tgss.creta.jaxb.bases.Trabajador trabajadorAon,
			Trabajador trabajadorCreta) {

		List<net.aonsolutions.tgss.creta.jaxb.bases.Tramo> tramosAon = new ArrayList<net.aonsolutions.tgss.creta.jaxb.bases.Tramo>(
				(trabajadorAon.getTramos().getTramo()));

		List<Tramo> tramosCreta = trabajadorCreta.getTramos().getTramo();

		if (tramosAon.size() != tramosCreta.size())
			return; // Don't skip. Different number of 'tramos'.

		Collections.sort(tramosAon, Bases::compare);

		Collections.sort(tramosCreta, Bases::compare);

		try {
			for (int i = 0; i < tramosAon.size(); i++)
				different(tramosAon.get(i), tramosCreta.get(i));
		} catch (Different e) {
			return;
		}

		throw new SkipExisting();
	}

	private static String toString(CtaCot ctaCot) {
		return String.format("%s%s%s", ctaCot.getProvincia(),
				ctaCot.getRegimen(), ctaCot.getNumero());

	}

	private static String toString(
			net.aonsolutions.tgss.creta.jaxb.bases.CtaCot ctaCot) {
		return String.format("%s%s%s", ctaCot.getProvincia(),
				ctaCot.getRegimen(), ctaCot.getNumero());

	}

	private static int compare(Liquidacion l1,
			net.aonsolutions.tgss.creta.jaxb.bases.Liquidacion l2) {
		if (l2 == null)
			return l1 == null ? 0 : 1;
		if (l1 == null)
			return l2 == null ? 0 : -1;

		String s1 = String.format("%s%s-%s%s", l1.getPeriodoDesde().getAnho(),
				l1.getPeriodoDesde().getMes(), l1.getPeriodoHasta().getAnho(),
				l1.getPeriodoHasta().getMes());
		String s2 = String.format("%s%s-%s%s", l1.getPeriodoDesde().getAnho(),
				l2.getPeriodoDesde().getMes(), l2.getPeriodoHasta().getAnho(),
				l2.getPeriodoHasta().getMes());
		return s1.compareTo(s2);
	}

	// ------------------------------------------------------------------------

	@SuppressWarnings("static-access")
	public static Option getHostNameOption() {
		return OptionBuilder.withArgName("name").hasArg().withLongOpt("host")
				.withDescription("Connect to host.").create("h");
	}

	@SuppressWarnings("static-access")
	public static Option getDbUserOption() {
		return OptionBuilder.withArgName("name").hasArg().isRequired(true)
				.withLongOpt("user").withDescription("User for login.")
				.create("u");
	}

	@SuppressWarnings("static-access")
	public static Option getDbPasswordOption() {
		return OptionBuilder.withArgName("name").hasArg().isRequired(true)
				.withLongOpt("password")
				.withDescription("Password to use when connecting to server.")
				.create("p");
	}

	@SuppressWarnings("static-access")
	public static Option getDatabaseOption() {
		return OptionBuilder.withArgName("name").hasArg().isRequired(true)
				.withLongOpt("database").withDescription("Database to use.")
				.create("D");
	}

	@SuppressWarnings("static-access")
	public static Option getCommentsOption() {
		return OptionBuilder.withLongOpt("comments")
				.withDescription("Write additional information.").create("c");
	}

	@SuppressWarnings("static-access")
	public static Option getPrettyOption() {
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
		return toCalendar(fecha).getTime();
	}

	public static Calendar toCalendar(Fecha fecha) {
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

		return calendar;
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
	public static void main(String[] args)
			throws EmptyBasesException, JAXBException, SQLException,
			ClassNotFoundException, IOException, XMLStreamException,
			FactoryConfigurationError, TransformerConfigurationException,
			TransformerFactoryConfigurationError {

		//@formatter:off
		Option hostName = getHostNameOption();
		Option user = getDbUserOption();
		Option password = getDbPasswordOption();
		Option database = getDatabaseOption();
		Option trabajadoresTramosFile = 
				OptionBuilder
				.hasArg()
				.withArgName("file")
				.withLongOpt("trabajadores-tramos")
				.withDescription("Fichero de Trabajadores y Tramos.")
				.create("t");

		Option respuestaFile = OptionBuilder
				.hasArgs()
				.withArgName("file")
				.withLongOpt("respuesta")
				.withDescription("Fichero de Respuesta.")
				.create("r");

		Option comments = getCommentsOption();

		Option skipPrevBases = OptionBuilder
				.withLongOpt("skip-prev-bases")
				.withDescription("Skip previous bases.")
				.create("b");

		Option skipExisting = OptionBuilder
				.withLongOpt("skip-existing")
				.withDescription(
						"Informar únicamente de las bases y resto de datos de trabajadores que sufren variaciones o de las de nuevos trabajadores.")
				.create();

		Option defaults = OptionBuilder
				.hasArg()
				.withArgName("<code>=<value>")
				.withLongOpt("default")
				.withDescription("Set a default data value")
				.create('d');

		Option naf = OptionBuilder
				.hasArg()
				.withArgName("naf")
				.withLongOpt("naf")
				.withDescription("Only send this NAF")
				.create();

		Option pretty = getPrettyOption();

		Options options = new Options()
				.addOption(hostName)
				.addOption(user)
				.addOption(password)
				.addOption(database)
				.addOption(comments)
				.addOption(skipPrevBases)
				.addOption(pretty)
				.addOption(trabajadoresTramosFile)
				.addOption(respuestaFile)
				.addOption(defaults)
				.addOption(skipExisting)
				.addOption(naf);
		//@formatter:on

		// create the parser
		CommandLineParser parser = new GnuParser();

		try {
			// parse the command line arguments
			CommandLine cmd = parser.parse(options, args);

			Class.forName(com.mysql.jdbc.Driver.class.getName());

			Connection connection = DriverManager.getConnection(
					String.format("jdbc:mysql://%s:%d/%s",
							cmd.getOptionValue(hostName.getLongOpt(),
									"127.0.0.1"),
							3306, cmd.getOptionValue(database.getLongOpt())),
					cmd.getOptionValue(user.getLongOpt()),
					cmd.getOptionValue(password.getLongOpt()));

			List<File> trabajadoresTramosFiles = new ArrayList<File>();
			if (cmd.hasOption(trabajadoresTramosFile.getLongOpt())) {
				for (String path : cmd
						.getOptionValues(trabajadoresTramosFile.getLongOpt()))
					trabajadoresTramosFiles.add(new File(path));
			}

			List<File> respuestaFiles = new ArrayList<File>();
			if (cmd.hasOption(respuestaFile.getLongOpt())) {
				for (String path : cmd
						.getOptionValues(respuestaFile.getLongOpt()))
					respuestaFiles.add(new File(path));
			}

			// @formatter:off
			generate(connection,
					cmd.hasOption(comments.getLongOpt()),
					cmd.hasOption(skipExisting.getLongOpt()),
					!cmd.hasOption(skipPrevBases.getLongOpt()),
					cmd.getOptionValues(naf.getLongOpt()),
					cmd.getOptionValues(defaults.getLongOpt()), 
					trabajadoresTramosFiles,
					respuestaFiles, 
					System.out);
			// @formatter:on

		} catch (ParseException e) {
			// oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + e.getMessage());
			// automatically generate the help statement
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("cret@", options);

		}

	}

	public static void generate(Connection connection, boolean comments,
			boolean skipExisting, boolean acceptPrevBases, String nafs[],
			String defaultsValues[], InputStream trabajadoresTramosIs,
			InputStream respuestaIs, OutputStream os, BasesCallback... cbs)
					throws EmptyBasesException, JAXBException,
					XMLStreamException, FactoryConfigurationError, IOException {
		//@formatter:off
		generate(connection, 
				comments, 
				skipExisting, 
				acceptPrevBases, 
				nafs,
				defaultsValues, 
				trabajadoresTramosIs!=null ? Collections.singletonList(trabajadoresTramosIs): Collections.emptyList(),
				respuestaIs != null ? Collections.singletonList(respuestaIs): Collections.emptyList(), 
				os, 
				cbs);
		//@formatter:on
	}

	public static void generate(Connection connection, boolean comments,
			boolean skipExisting, boolean acceptPrevBases, String nafs[],
			String defaultsValues[], List<File> trabajadoresTramosFiles,
			List<File> respuestaFiles, OutputStream os, BasesCallback... cbs)
					throws EmptyBasesException, JAXBException,
					XMLStreamException, FactoryConfigurationError, IOException {
		List<InputStream> trabajadoresTramosIsList = new ArrayList<InputStream>();
		for (File trabajadoresTramosFile : trabajadoresTramosFiles)
			trabajadoresTramosIsList
					.add(new FileInputStream(trabajadoresTramosFile));

		List<InputStream> respuestaIsList = new ArrayList<InputStream>();
		for (File respuestaFile : respuestaFiles)
			respuestaIsList.add(new FileInputStream(respuestaFile));

		//@formatter:off
		generate(connection, 
				comments,
				skipExisting,
				acceptPrevBases, 
				nafs,
				defaultsValues,
				trabajadoresTramosIsList,
				respuestaIsList,
				os,
				cbs);
		//@formatter:on
	}

	public static void generate(Connection connection, boolean comments,
			boolean skipExisting, boolean acceptPrevBases, String nafs[],
			String defaultsValues[],
			Collection<InputStream> trabajadoresTramosIss,
			Collection<InputStream> respuestaIss, OutputStream os,
			BasesCallback... cbs) throws EmptyBasesException, JAXBException,
					XMLStreamException, FactoryConfigurationError, IOException {
		AONContext ctx = new AONContext(connection);

		XMLStreamWriter xsw = new IndentXMLStreamWriter(
				XMLOutputFactory.newInstance().createXMLStreamWriter(os), "  ");

		List<BasesCallback> callbacksList = new LinkedList<BasesCallback>();

		if (skipExisting)
			callbacksList.add(new SkipExistingCallback());

		Errors errors = new Errors();
		callbacksList.add(errors);

		Comments comment = new Comments(xsw);
		callbacksList.add(comment);

		FixConceptUnMatchedCallback fixConceptsUnMatched = new FixConceptUnMatchedCallback(
				"500", "501", "502", "601", "611");
		callbacksList.add(fixConceptsUnMatched);

		DefaultsCallback defaultsCb = new DefaultsCallback();

		if (defaultsValues != null) {
			for (String defaultValue : defaultsValues) {
				String codeValue[] = defaultValue.split("=");
				defaultsCb.add(codeValue[0], codeValue[1]);
			}
		}
		callbacksList.add(defaultsCb);

		if (nafs != null) {
			NAFFilterCallback nafFilterCb = new NAFFilterCallback(nafs);
			callbacksList.add(nafFilterCb);
		}

		if (cbs != null)
			for (BasesCallback cb : cbs)
				callbacksList.add(cb);

		BasesCallback callbacks[] = callbacksList
				.toArray(new BasesCallback[callbacksList.size()]);

		Set<String> autorizados = new HashSet<String>();

		BasesBuilder builder = new BasesBuilder();

		Map<String, net.aonsolutions.tgss.creta.jaxb.bases.Liquidacion> liquidaciones = new HashMap<String, net.aonsolutions.tgss.creta.jaxb.bases.Liquidacion>();

		for (InputStream trabajadoresTramosIs : trabajadoresTramosIss) {

			try {
				net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresTramos = Utils
						.unmarshal(
								net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos.class,
								trabajadoresTramosIs);
				String ccc = toString(
						trabajadoresTramos.getLiquidacion().getCcc());
				net.aonsolutions.tgss.creta.jaxb.bases.Liquidacion liquidacion = liquidaciones
						.get(ccc);

				if (compare(trabajadoresTramos.getLiquidacion(),
						liquidacion) <= 0) {
					continue;
				}

				autorizados.add(trabajadoresTramos.getAutorizado());

				liquidacion = liquidacion(trabajadoresTramos, ctx,
						acceptPrevBases, xsw, callbacks);

				boolean noTrabajadores = liquidacion.getLiquidacionMes()
						.stream().map(l -> l.getTrabajadores()).allMatch(
								t -> t == null || t.getTrabajador().isEmpty());

				if (noTrabajadores)
					for (BasesCallback cb : cbs)
						cb.noDiffs(liquidacion);
				else
					liquidaciones.put(ccc, liquidacion);

			} catch (JAXBException e) {
				for (BasesCallback cb : callbacks)
					cb.wrongTrabajadoresTramosIs(trabajadoresTramosIs, e);

			}

			trabajadoresTramosIs.close();
		}

		for (InputStream respuestaIs : respuestaIss) {
			try {
				net.aonsolutions.tgss.creta.jaxb.respuesta.Respuesta respuesta = Utils
						.unmarshal(
								net.aonsolutions.tgss.creta.jaxb.respuesta.Respuesta.class,
								respuestaIs);

				autorizados.add(respuesta.getAutorizado());

				liquidaciones(respuesta, ctx, acceptPrevBases, xsw, callbacks)
						.stream().forEach(l -> liquidaciones
								.put(toString(l.getCcc()), l));
				;

			} catch (JAXBException e) {
				for (BasesCallback cb : callbacks)
					cb.wrongTrabajadoresTramosIs(respuestaIs, e);
			}
			respuestaIs.close();
		}

		String autorizado = null;

		if (autorizados.size() > 1) {
			// TODO
		} else if (autorizados.isEmpty()) {
			// TODO
		} else {
			autorizado = autorizados.stream().findFirst().get();
			builder.setAutorizado(autorizado);
		}

		if (liquidaciones.isEmpty())
			throw new EmptyBasesException().setAutorizado(autorizado);

		builder.addLiquidaciones(liquidaciones.values());
		net.aonsolutions.tgss.creta.jaxb.bases.Bases bases = builder.create();

		if (comments)
			Utils.marshal(bases, xsw, comment);
		else
			Utils.marshal(bases, xsw);

	}

	private static int getDaysOf(Tramo<?> tramo) {
		int hasta = Integer.parseInt(tramo.getFechaHasta().getDia());
		int desde = Integer.parseInt(tramo.getFechaDesde().getDia());
		return hasta - desde + 1;

	}

	private static int getDaysOf(ContextData contextData) {
		return (int) getDaysBetweenDates(contextData.getStartDate(),
				contextData.getEndDate()) + 1;

	}

	// return an integer stating how many decimal points the number has
	// assume the number provided is a positive double
	private static int getNumberOfDecimals(double number) {
		// convert the number to a string
		String strNumber = Double.toString(number);

		// get the length of the number as a string
		int stringLength = strNumber.length();
		int numberOfDecimals = 0;
		char theChar = 'e';
		int counter;

		// check what number the decimal point character is in the string
		for (counter = 1; theChar != '.'; counter++) {
			theChar = strNumber.charAt(counter);
		}

		// calculate the number of decimals the double has
		numberOfDecimals = stringLength - counter;

		return numberOfDecimals;
	}
	
	private static void checkTrabajador(net.aonsolutions.tgss.creta.jaxb.bases.Trabajador trabajador) {
		Tramos tramos = trabajador.getTramos();
		if ( tramos == null )
			throw new InvalidTrabajador();
		List<net.aonsolutions.tgss.creta.jaxb.bases.Tramo> tramo = tramos.getTramo();
		if ( tramo == null || tramo.isEmpty() )
			throw new InvalidTrabajador();
		
	}
}
