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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

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

import net.aonsolutions.tgss.creta.jaxb.CtaCot;
import net.aonsolutions.tgss.creta.jaxb.Dato;
import net.aonsolutions.tgss.creta.jaxb.DatoSolicitado;
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


public class Bases {

	private static class MonthlySalaryCretaData implements CretaData {
		@Override
		public String getComment() {
			return MONTHLY_SALARY.name();
		}

		@Override
		public void add(Salary salary, Tramo tramo, Dato datoSolicitado,
				TramoBuilder tramoBuilder, BasesCallback... cbs) {
			try {
				boolean montly = get(
						salary,
						new Period(toDate(tramo.getFechaDesde()), toDate(tramo
								.getFechaHasta())));
				if (!montly)
					return;

				DatoBuilder datoBuilder = new DatoBuilder()
						.setCodigo(datoSolicitado.getCodigo())
						.setTipo(datoSolicitado.getTipoDato()).setValor("M");
				tramoBuilder.addDato(datoBuilder.create());
			} catch (NoSuchContextVariableException e) {
				for (BasesCallback cb : cbs)
					cb.noSuchDato(salary, tramo, datoSolicitado, tramoBuilder,
							true);

			}

		}

		protected boolean get(Salary salary, Period p)
				throws NoSuchContextVariableException {
			List<ContextData> datas = salary.getContextData().get(
					MONTHLY_SALARY.getName());

			if (datas == null)
				throw new NoSuchContextVariableException(MONTHLY_SALARY);

			for (ContextData data : datas) {
				Period intersect = p.intersect(new Period(data.getStartDate(),
						data.getEndDate()));
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

		public NoSuchDatoException(net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.DatoSolicitado datoSolicitado) {
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
	private static class NoSuchContextVariablesException extends RuntimeException {
		private ContextVariable contextVariables [];

	@SuppressWarnings("serial")
		public NoSuchContextVariablesException(ContextVariable ...contextVariables) {
			super();
			this.contextVariables = contextVariables;
		}

		public ContextVariable [] getContextVariables() {
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
				for (BasesCallback cb : cbs)
					cb.noSuchDato(salary, tramo, datoSolicitado, tramoBuilder,
							optional);
			} catch (UnMatchedContextVariableException e) {
				for (BasesCallback cb : cbs)
					cb.unMatchedContextVariable(salary, e.getContextVariable(),
							e.getContextData(), datoSolicitado, tramo, tramoBuilder, optional);
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

	private static interface BasesCallback {

		default void trabajadorAdded(
				net.aonsolutions.tgss.creta.jaxb.bases.Trabajador trabajadorAon,
				Trabajador trabajadorCreta, Salary salary) {
		};

		default void trabajadorSkipped(
				net.aonsolutions.tgss.creta.jaxb.bases.Trabajador trabajadorAon,
				Trabajador trabajadorCreta, Salary salary) {
		};

		default void unknownSalary(Salary salary) {
		};

		default void unknownDato(DatoSolicitado datoSolicitado) {
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

		default void unMatchedContextVariable(Salary salary, ContextVariable var,
				ContextData contextData, Dato datoSolicitado, Tramo tramo, TramoBuilder tramoBuilder, boolean optional) {
		};

		default void zeroDato(ContextVariable var, Dato datoSolicitado,
				Tramo tramo, Salary salary) {
		};

		default void noSuchDato(Salary salary, Tramo tramo,
				Dato datoSolicitado, TramoBuilder tramoBuilder, boolean optional) {
		};
	}

	@SuppressWarnings("serial")
	private static class Different extends RuntimeException {

	}

	@SuppressWarnings("serial")
	private static class SkipExisting extends RuntimeException {

	}

	private static class SkipExistingCallback implements BasesCallback {
		@Override
		public void trabajadorAdded(
				net.aonsolutions.tgss.creta.jaxb.bases.Trabajador trabajadorAon,
				Trabajador trabajadorCreta, Salary salary) {
			skip(trabajadorAon, trabajadorCreta);
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
		public void noSuchDato(Salary salary, Tramo tramo, Dato datoSolicitado,
				TramoBuilder tramoBuilder, boolean optional) {

			if (defaults.containsKey(datoSolicitado.getCodigo())) {
				String valor = defaults.get(datoSolicitado.getCodigo());
				DatoBuilder datoBuilder = new DatoBuilder();
				datoBuilder.setCodigo(datoSolicitado.getCodigo());
				datoBuilder.setTipo(datoSolicitado.getTipoDato());
				datoBuilder.setValor(valor);
				tramoBuilder.addDato(datoBuilder.create());
				System.err
						.printf("WARN: %s for %s (%s) [%s-%s-%s...%s-%s-%s] is default value %s \r\n",
								datoSolicitado.getCodigo(), salary
										.getEmployeeSSNumber(), salary
										.getEmployeeDocument(), tramo
										.getFechaDesde().getDia(), tramo
										.getFechaDesde().getMes(), tramo
										.getFechaDesde().getAnho(), tramo
										.getFechaHasta().getDia(), tramo
										.getFechaHasta().getMes(), tramo
										.getFechaHasta().getAnho(), valor);
			}
		}
		
	}

	private static class FixConceptUnMatchedCallback implements BasesCallback {
		
		private String datos [] ;
		
		public FixConceptUnMatchedCallback(String ...datos) {
			this.datos = datos;
			Arrays.sort(this.datos);
		}
		
		@Override
		public void unMatchedContextVariable(Salary salary,
				ContextVariable var, ContextData contextData,
				Dato datoSolicitado, Tramo tramo, TramoBuilder tramoBuilder, boolean optional) {
			
			if ( Arrays.binarySearch(datos, datoSolicitado.getCodigo())< 0)
				return;
				
			DatoBuilder datoBuilder = new DatoBuilder();
			datoBuilder.setCodigo(datoSolicitado.getCodigo());
			datoBuilder.setTipo(datoSolicitado.getTipoDato());
			
			Double total = MVEL.eval(contextData.getExpression(), Double.class);
			if ( total == 0.00 && optional) {
				System.err
				.printf("WARN: %s for %s (%s) [%s-%s-%s...%s-%s-%s] fixed value is zero. Not send because is optional  \r\n",
						datoSolicitado.getCodigo(), salary
								.getEmployeeSSNumber(), salary
								.getEmployeeDocument(), tramo
								.getFechaDesde().getDia(), tramo
								.getFechaDesde().getMes(), tramo
								.getFechaDesde().getAnho(), tramo
								.getFechaHasta().getDia(), tramo
								.getFechaHasta().getMes(), tramo
								.getFechaHasta().getAnho(), 
								total);
				return;
			}
				
			datoBuilder.setImporteEuros(total);
			
			tramoBuilder.addDato(datoBuilder.create());
			System.err
					.printf("WARN: %s for %s (%s) [%s-%s-%s...%s-%s-%s] fixed value %f \r\n",
							datoSolicitado.getCodigo(), salary
									.getEmployeeSSNumber(), salary
									.getEmployeeDocument(), tramo
									.getFechaDesde().getDia(), tramo
									.getFechaDesde().getMes(), tramo
									.getFechaDesde().getAnho(), tramo
									.getFechaHasta().getDia(), tramo
									.getFechaHasta().getMes(), tramo
									.getFechaHasta().getAnho(), 
									total);
		}
	}
	
	private static class Errors implements BasesCallback {
		
		private static Map<String,String> DESCRIPTIONS_MAP = new HashMap<String,String>(){
			{
				
				put("01","Número de horas realizadas a tiempo parcial");
				put("02","Número de horas complementarias");
				
				put("51","Modalidad de salario");

				put("500","Base de contingencias comunes");
				put("501","Base de Horas Extras Fuerza Mayor");
				put("502","Base de Otras Horas Extras");
				put("537","Base de horas complementarias");
				put("563","Compensación IT contingencias comunes");
				
				put("601","Base de Accidentes de Trabajo");
				put("611","Base de Accidentes de Trabajo");
				put("603","Base de Accidentes de Trabajo en situación de IT");
				put("613","Base de Accidentes de Trabajo en situación de IT");
				put("663","Compensación IT AT y EP");
			}
		};
		
		@Override
		public void unknownDato(DatoSolicitado datoSolicitado) {
			boolean mandatory ="B".equalsIgnoreCase(datoSolicitado.getIndicadorObligatoriedad());
			System.err.println(String.format("%s: Unknown %s Dato '%s'",
					mandatory ? "ERROR" : "WARN",
					mandatory ? "Mandatory" : "Optional",
					datoSolicitado.getCodigo()));
		}

		@Override
		public void unknownTrabajador(Trabajador trabajador) {
			System.err.println(String.format(
					"ERROR : Employee %s, not at aon ", trabajador.getNaf()));
		}

		@Override
		public void unknownSalary(Salary salary) {
			System.err.println(String.format(
					"WARN: Employee %s (%s), not at Cret@ ",
					salary.getEmployeeName(), salary.getEmployeeDocument()));
		}

		@Override
		public void noSuchContextVariable(ContextVariable var, Period p,
				String right) {
			System.err.println(String.format(
					"WARN: '%s' (%s) not in salary data", var, right));
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
					"WARN: %s (%s) for %s [%s-%s-%s...%s-%s-%s] is zero",
					var.getName(), datoSolicitado.getCodigo()
					, salary
							.getEmployeeName(), tramo.getFechaDesde().getDia(),
					tramo.getFechaDesde().getMes(), tramo.getFechaDesde()
							.getAnho(), tramo.getFechaHasta().getDia(), tramo
							.getFechaHasta().getMes(), tramo.getFechaHasta()
							.getAnho()));
		};

		@Override
		public void noSuchDato(Salary salary, Tramo tramo, Dato datoSolicitado,
				TramoBuilder tramoBuilder, boolean optional) {

			System.err
					.println(String
							.format("%s: Dato '%s' for %s (%s) [%s-%s-%s...%s-%s-%s] not found",
									(optional ? "WARN" : "ERROR"),
									datoSolicitado.getCodigo(), salary
											.getEmployeeName(), salary
											.getEmployeeSSNumber(), tramo
											.getFechaDesde().getDia(), tramo
											.getFechaDesde().getMes(), tramo
											.getFechaDesde().getAnho(), tramo
											.getFechaHasta().getDia(), tramo
											.getFechaHasta().getMes(), tramo
											.getFechaHasta().getAnho()));
		}
		
		
		@Override
		public void unMatchedContextVariable(Salary salary, ContextVariable var,
 ContextData contextData,
				Dato datoSolicitado, Tramo tramo, TramoBuilder tramoBuilder, boolean optional) {
			//@formatter:off
			System.err.println(String.format(
					"ERROR: %s for %s [%3$td-%3$tm-%3$tY...%4$td-%4$tm-%4$tY] not match %5$s [%6$s-%7$s-%8$s...%9$s-%10$s-%11$s]",

					var.getName(), 
					salary.getEmployeeName(), 
					contextData.getStartDate(),
					contextData.getEndDate(),
					
					datoSolicitado.getCodigo(),
					tramo.getFechaDesde().getDia(),
					tramo.getFechaDesde().getMes(), 
					tramo.getFechaDesde().getAnho(), 
					tramo.getFechaHasta().getDia(), 
					tramo.getFechaHasta().getMes(), 
					tramo.getFechaHasta().getAnho()
					));
			//@formatter:on
		}
	}

	private static class Comments extends Listener implements BasesCallback {

		private static class TrabajadorData {

			private String ss;
			private String nif;
			private String name;
			private net.aonsolutions.tgss.creta.jaxb.bases.Trabajador trabajador;
			
			public TrabajadorData(String name, String nif, String ss, net.aonsolutions.tgss.creta.jaxb.bases.Trabajador trabajador) {
				this.ss = ss;
				this.nif = nif;
				this.name = name;
				this.trabajador = trabajador;
			}
		}

		private XMLStreamWriter xsw;
		private Map<String, TrabajadorData> addedTrabajadorDataMap;
		private Stack<TrabajadorData> skippedTrabajadorList;

		public Comments(XMLStreamWriter xsw) {
			super();
			this.xsw = xsw;
			this.addedTrabajadorDataMap = new HashMap<String, TrabajadorData>();
			this.skippedTrabajadorList = new Stack<TrabajadorData>();
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
		public void trabajadorAdded(
				net.aonsolutions.tgss.creta.jaxb.bases.Trabajador trabajadorAon,
				Trabajador trabajadorCreta, Salary salary) {
			addedTrabajadorDataMap.put(trabajadorCreta.getNaf(), new TrabajadorData(
					salary.getEmployeeName(), 
					salary.getEmployeeDocument(),
					salary.getEmployeeSSNumber(),
					trabajadorAon));
		};

		@Override
		public void trabajadorSkipped(
				net.aonsolutions.tgss.creta.jaxb.bases.Trabajador trabajadorAon,
				Trabajador trabajadorCreta, Salary salary) {
			skippedTrabajadorList.add(new TrabajadorData(
					salary.getEmployeeName(), 
					salary.getEmployeeDocument(),
					salary.getEmployeeSSNumber(),
					trabajadorAon));
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
			marshallSkipped();
			TrabajadorData data = addedTrabajadorDataMap.get(trabajador.getNaf());
			try {
				xsw.writeComment(String
						.format("\r\n%s\r\nNúmero afiliación a la Seguridad Social:\r\n%s\r\nNúmero de identificación fiscal de las personas físicas:\r\n%s\r\n",
								data.name, data.ss, data.nif));
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// --------------------------------------------------------------------
		
		private void marshallSkipped() {
			while( !skippedTrabajadorList.isEmpty() ){
				TrabajadorData skippedTrabajador = skippedTrabajadorList.pop();
				try {
					StringWriter writer = new StringWriter();
					Marshaller marshaller = JAXBContext.newInstance(skippedTrabajador.trabajador.getClass()).createMarshaller();
//					marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "");
					marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
					marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//					marshaller.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, "http://www.seg-social.es/creta/esquemas/V100/Bases");
					marshaller.marshal(skippedTrabajador.trabajador, writer);

					xsw.writeComment(String
							.format("\r\n%s\r\nNúmero afiliación a la Seguridad Social:\r\n%s\r\nNúmero de identificación fiscal de las personas físicas:\r\n%s\r\n%s\r\n",
									skippedTrabajador.name, skippedTrabajador.ss, skippedTrabajador.nif, writer.toString()));
					
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
		throws NoSuchContextVariableException, UnMatchedContextVariableException;

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
				for (BasesCallback cb : cbs)
					cb.unMatchedContextVariable(salary, e.getContextVariable(),
							e.getContextData(), datoSolicitado, tramo, tramoBuilder, optional);
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
			return get(contextVariable, salary, new Period(toDate(desde), toDate(hasta)));
		}
		
		@Override
		protected void zeroValue(Salary salary, Tramo tramo,
				Dato datoSolicitado, TramoBuilder tramoBuilder,
				BasesCallback... cbs) {
			for (BasesCallback cb : cbs)
				cb.zeroDato(contextVariable, datoSolicitado, tramo, salary);
		}

		// --------------------------------------------------------------------

		protected static Double get(ContextVariable contextVariable, Salary salary, Period p)
				throws NoSuchContextVariableException,
				UnMatchedContextVariableException {
			List<ContextData> datas = salary.getContextData().get(
					contextVariable.getName());
			if (datas == null)
				throw new NoSuchContextVariableException(contextVariable);

			double ret = 0.00;
			boolean found = false;

			for (ContextData data : datas) {
				if (Period.compare(data.getStartDate(), p.getStart()) > 0
						|| Period.compare(data.getEndDate(), p.getEnd()) > 0)
					throw new UnMatchedContextVariableException(
							contextVariable, data);

				Period intersect = p.intersect(new Period(data.getStartDate(),
						data.getEndDate()));
				if (intersect == null)
					continue;

				found = true;
				ret += ExpressionContext.eval(data.getExpression(),
						Double.class) * days(intersect) / days(p);
			}
			
			if (!found)
				throw new NoSuchContextVariableException(contextVariable);

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

	private static abstract class CompositeCContextCretaData extends AbstractCCretaData {

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
			} catch ( ZeroValueException e){
				for (BasesCallback cb : cbs)
					cb.zeroDato(e.getContextVariable(), datoSolicitado, tramo, salary);
			}catch ( NoSuchContextVariablesException e){
				for (BasesCallback cb : cbs)
					cb.noSuchDato(salary, tramo, datoSolicitado, tramoBuilder, isOptional());
			}
		}
		
		@Override
		public Double get(Salary salary, Fecha desde, Fecha hasta)
				throws NoSuchContextVariableException,
				UnMatchedContextVariableException {
			Period p = new Period(toDate(desde), toDate(hasta));
			for(ContextVariable  contextVariable: contextVariables ) {
				try {
					Double value = CContextCretaData.get(contextVariable, salary, p);
					if ( value == 0.00 )
						throw new ZeroValueException(contextVariable);
//					System.err.println("--- CompositeCContextCretaData " + contextVariable.getName() + ", " + salary.getEmployeeName() + " = "  + value );
					return value;
				} catch ( NoSuchContextVariableException e) {
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
	
	private static class MandatoryCompositecContextData extends CompositeCContextCretaData{

		public MandatoryCompositecContextData(
				ContextVariable... contextVariables) {
			super(contextVariables);
		}
		
		// CompositeCContextCretaData -----------------------------------------
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

			put("509", new MandatoryCompositecContextData(MATERNITY_BASE, ERE_BASE));
			put("603", new MandatoryCompositecContextData(MATERNITY_BASE, ERE_BASE));
			put("613", new MandatoryCompositecContextData(MATERNITY_BASE, ERE_BASE));
		}
	};

	// ------------------------------------------------------------------------

	private static <D extends DatoSolicitado> void bases(BasesBuilder basesBuilder, AONContext ctx,
			Liquidacion<?, ?, ?> liquidacion, boolean aceptarBasesAnteriores,
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

			Map<String, Trabajador<D>> trabajadores = new HashMap<String, Trabajador<D>>();

			for (Trabajador<D> trabajador : liquidacionMes.getTrabajadores()
					.getTrabajador())
				trabajadores.put(trabajador.getNaf(), trabajador);

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
				cb.unknownTrabajador(t);
		});

	}

	private static <D extends DatoSolicitado> void trabajador(LiquidacionMesBuilder liquidacionMesBuilder,
			Salary salary, Map<String, Trabajador<D>> trabajadores,
			BasesCallback... cbs) {

		TrabajadorBuilder trabajadorBuilder = new TrabajadorBuilder();
		Trabajador<D> trabajador = trabajadores.get(salary.getEmployeeSSNumber());
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
					for (BasesCallback cb : cbs)
						cb.unknownDato(datoSolicitado);
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

		checkContextVariable(QUOTE_GROUP, tramo.getInformacionAfiliacion()
				.getGrupoCotizacion(), p, salary, cbs);
		checkContextVariable(TC2, tramo.getInformacionAfiliacion()
				.getTipoContrato(), p, salary, cbs);
		checkContextVariable(OCCUPATION, tramo.getInformacionAfiliacion()
				.getOcupacion(), p, salary, cbs);

		String partialFactor = tramo.getInformacionAfiliacion()
				.getCoeficienteTiempoParcial();
		if (partialFactor != null)
			checkContextVariable(
					PARTIAL_FACTOR,
					(partialFactor != null ? Double.toString(Integer
							.parseInt(partialFactor) / 1000.00) : null), p,
					salary, cbs);
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

		for (Liquidacion<?, ?, ?> liquidacion : respuesta.getLiquidacion())
			bases(builder, ctx, liquidacion, aceptarBasesAnteriores, cbs);

		return builder.create();

	}

	private static Double getWorkedHours(Salary salary, Fecha desde, Fecha hasta)
			throws NoSuchContextVariableException {

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

			ContextVariable var = DAYS_HOURS.get(calendar
					.get(Calendar.DAY_OF_WEEK));

			List<ContextData> hourDatas = salary.getContextData(var.getName(),
					date, date);

			// if ( hourDatas.isEmpty() )
			// throw new NoSuchContextVariableException(var);

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
		return toDate(t1.getFechaDesde().getAnho(),
				t1.getFechaDesde().getMes(), t1.getFechaDesde().getDia())
				.compareTo(
						toDate(t2.getFechaDesde().getAnho(), t2.getFechaDesde()
								.getMes(), t2.getFechaDesde().getDia()));
	}

	private static void different(
			net.aonsolutions.tgss.creta.jaxb.bases.Dato datoAon, Dato datoCreta) {

		if (AonStringUtils.isEmpty(datoCreta.getValor()))
			throw new Different(); // Nuevo trabajador ???.

		if ("C".equalsIgnoreCase(datoCreta.getTipoDato())) {
			if (Long.parseLong(datoAon.getValor()) != Long.parseLong(datoCreta
					.getValor()))
				throw new Different(); // El tipo de dato se refiere a
										// "Concepto".
		} else if ("H".equalsIgnoreCase(datoCreta.getTipoDato())) {
			if (Long.parseLong(datoAon.getValor()) != Long.parseLong(datoCreta
					.getValor()))
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
		Date dateFromAon = toDate(tramoAon.getFechaDesde().getAnho(), tramoAon
				.getFechaDesde().getMes(), tramoAon.getFechaDesde().getDia());
		Date dateFromCreta = toDate(tramoCreta.getFechaDesde());
		if (dateFromAon.compareTo(dateFromCreta) != 0)
			throw new Different(); // Don't skip. Diffente 'FechaDesde'.

		Date dateToAon = toDate(tramoAon.getFechaHasta().getAnho(), tramoAon
				.getFechaHasta().getMes(), tramoAon.getFechaHasta().getDia());
		Date dateToCreta = toDate(tramoCreta.getFechaHasta());
		if (dateToAon.compareTo(dateToCreta) != 0)
			throw new Different(); // Don't skip. Diffente 'FechaHasta'.

		List<DatoSolicitado> datosCreta = tramoCreta.getDatosTramo().getDato();
//		List<net.aonsolutions.tgss.creta.jaxb.DatoSolicitado> datosCreta = tramoCreta
//				.getDatosTramo().getDato();
		Collections.sort(datosCreta, Bases::compare);
		List<net.aonsolutions.tgss.creta.jaxb.bases.Dato> datosAon = tramoAon
				.getDatosTramo().getDato();
		Collections.sort(datosAon, Bases::compare);

		int j = 0;
		for (int i = 0; i < datosCreta.size(); i++) {
			DatoSolicitado datoCreta = datosCreta.get(i);
//			net.aonsolutions.tgss.creta.jaxb.DatoSolicitado datoCreta = datosCreta
//					.get(i);
			net.aonsolutions.tgss.creta.jaxb.bases.Dato datoAon = datosAon
					.get(j);

			if (!datoCreta.getTipoDato()
					.equalsIgnoreCase(datoAon.getTipoDato())
					|| !datoCreta.getCodigo().equalsIgnoreCase(
							datoAon.getCodigo())) {
				// El dato solicitado es obligatorio.
				if ("B".equalsIgnoreCase(datoCreta.getIndicadorObligatoriedad()))
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

		List<net.aonsolutions.tgss.creta.jaxb.bases.Tramo> tramosAon = trabajadorAon
				.getTramos().getTramo();
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
	public static void main(String[] args) throws JAXBException, SQLException,
			ClassNotFoundException, IOException, XMLStreamException,
			FactoryConfigurationError, TransformerConfigurationException,
			TransformerFactoryConfigurationError {

		// @formatter:off
		Option hostName = getHostNameOption();
		Option user = getDbUserOption();
		Option password = getDbPasswordOption();
		Option database = getDatabaseOption();
		Option trabajadoresTramosFile = OptionBuilder.withArgName("file")
				.hasArg().withLongOpt("trabajadores-tramos")
				.withDescription("Fichero de Trabajadores y Tramos.")
				.create("t");
		Option respuestaFile = OptionBuilder.withArgName("file").hasArg()
				.withLongOpt("respuesta")
				.withDescription("Fichero de Respuesta.").create("r");

		Option indicador51 = OptionBuilder
				.withArgName("valor")
				.withLongOpt("modalidad-salario")
				.withDescription(
						"Modalidad Salario. Para grupos de cotización diario (GC del 08 al 11) con retribución mensual")
				.create();

		Option comments = getCommentsOption();

		Option skipPrevBases = OptionBuilder.withLongOpt("skip-prev-bases")
				.withDescription("Skip previous bases.").create("b");

		Option skipExisting = OptionBuilder
				.withLongOpt("skip-existing")
				.withDescription(
						"Informar únicamente de las bases y resto de datos de trabajadores que sufren variaciones o de las de nuevos trabajadores.")
				.create();

		Option pretty = getPrettyOption();

		Options options = new Options().addOption(hostName).addOption(user)
				.addOption(password).addOption(database).addOption(comments)
				.addOption(skipPrevBases).addOption(pretty)
				.addOption(trabajadoresTramosFile).addOption(respuestaFile)
				.addOption(indicador51).addOption(skipExisting);
		// @formatter:on

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

			boolean acceptPrevBases = !cmd
					.hasOption(skipPrevBases.getLongOpt());

			AONContext ctx = new AONContext(connection);

			XMLStreamWriter xsw = new IndentXMLStreamWriter(XMLOutputFactory
					.newInstance().createXMLStreamWriter(System.out), "  ");

			List<BasesCallback> callbacksList = new LinkedList<BasesCallback>();
			if (cmd.hasOption(skipExisting.getLongOpt()))
				callbacksList.add(new SkipExistingCallback());

			Errors errors = new Errors();
			callbacksList.add(errors);

			Comments comment = new Comments(xsw);
			callbacksList.add(comment);
			
			FixConceptUnMatchedCallback fixConceptsUnMatched = 
					new FixConceptUnMatchedCallback("500", "501","502", "601", "611");
			callbacksList.add(fixConceptsUnMatched);
			

			DefaultsCallback defaults = new DefaultsCallback();
			if (cmd.hasOption(indicador51.getLongOpt())) {
				defaults.add("51", "M");
			}
			callbacksList.add(defaults);
			BasesCallback callbacks[] = callbacksList
					.toArray(new BasesCallback[callbacksList.size()]);

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

				bases = bases(trabajadoresTramos, ctx, acceptPrevBases, xsw,
						callbacks);
			} else {
				String path = cmd.getOptionValue(respuestaFile.getLongOpt());
				is = AonStringUtils.isEmpty(path) ? System.in
						: new FileInputStream(path);

				net.aonsolutions.tgss.creta.jaxb.respuesta.Respuesta respuesta = Utils
						.unmarshal(
								net.aonsolutions.tgss.creta.jaxb.respuesta.Respuesta.class,
								is);

				bases = bases(respuesta, ctx, acceptPrevBases, xsw, callbacks);
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
