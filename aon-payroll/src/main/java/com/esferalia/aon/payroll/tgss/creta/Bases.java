package com.esferalia.aon.payroll.tgss.creta;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.ADDITIONAL_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ADDITIONAL_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE_ENTERPRISE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE_ENTERPRISE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.DIRECT_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ERE_BASES;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ERE_FACTORS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MATERNITY_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.NON_STRUCTURAL_OVERTIME_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.OCCUPATION;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PARTIAL_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_GROUP;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SALARY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SLD_C737;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SLD_H03;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SLD_H04;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SLD_H06;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.STRUCTURAL_OVERTIME_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TC2;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TOTAL_PAYMENT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_HOURS;
import static com.esferalia.aon.watson.server.AonDateUtils.getDay;
import static com.esferalia.aon.watson.server.AonDateUtils.getDaysBetweenDates;
import static com.esferalia.aon.watson.server.AonDateUtils.getMonthLastDay;

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
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.function.DoubleSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

import net.aonsolutions.core.tgss.creta.jaxb.CtaCot;
import net.aonsolutions.core.tgss.creta.jaxb.Dato;
import net.aonsolutions.core.tgss.creta.jaxb.DatoSolicitado;
import net.aonsolutions.core.tgss.creta.jaxb.DatosLiquidacion;
import net.aonsolutions.core.tgss.creta.jaxb.Fecha;
import net.aonsolutions.core.tgss.creta.jaxb.Liquidacion;
import net.aonsolutions.core.tgss.creta.jaxb.LiquidacionMes;
import net.aonsolutions.core.tgss.creta.jaxb.Periodo;
import net.aonsolutions.core.tgss.creta.jaxb.Trabajador;
import net.aonsolutions.core.tgss.creta.jaxb.Tramo;
import net.aonsolutions.core.tgss.creta.jaxb.Utils;
import net.aonsolutions.core.tgss.creta.jaxb.bases.BasesBuilder;
import net.aonsolutions.core.tgss.creta.jaxb.bases.DatoBuilder;
import net.aonsolutions.core.tgss.creta.jaxb.bases.LiquidacionBuilder;
import net.aonsolutions.core.tgss.creta.jaxb.bases.LiquidacionMesBuilder;
import net.aonsolutions.core.tgss.creta.jaxb.bases.TrabajadorBuilder;
import net.aonsolutions.core.tgss.creta.jaxb.bases.TramoBuilder;
import net.aonsolutions.core.tgss.creta.jaxb.bases.Tramos;

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
	
	public static class ConstantDatoBasesCallback implements BasesCallback {
		
		private String valor;
		private String codigo;
		private String tipoDato;
		
		
		
		public ConstantDatoBasesCallback(String codigo, String tipoDato, String valor) {
			this.valor = valor;
			this.codigo = codigo;
			this.tipoDato = tipoDato;
		}



		@Override
		public void unknownDato(Salary salary, Trabajador<?> trabajador, Tramo tramo, DatoSolicitado datoSolicitado,
				TramoBuilder tramoBuilder) {
			if ( !StringUtils.equalsIgnoreCase(codigo, datoSolicitado.getCodigo()))
				return;
			if ( !StringUtils.equalsIgnoreCase(tipoDato, datoSolicitado.getTipoDato()))
				return;
			
			DatoBuilder datoBuilder = new DatoBuilder()
					.setCodigo(datoSolicitado.getCodigo())
					.setTipo(datoSolicitado.getTipoDato())
					.setValor(valor);
			tramoBuilder.addDato(datoBuilder.create());
			
			throw new Cancel();
			
		}
	}

	public static class CustomizeBasesCallback implements BasesCallback {
		boolean reftificationMark = false;
		boolean solicitudRecepcionRNT = false;
		
		public CustomizeBasesCallback setReftificationMark(boolean reftificationMark) {
			this.reftificationMark = reftificationMark;
			return this;
		}
		
		public CustomizeBasesCallback setSolicitudRecepcionRNT(boolean solicitudRecepcionRNT) {
			this.solicitudRecepcionRNT = solicitudRecepcionRNT;
			return this;
		}

		// ------------------------------------------------------ BasesCallback
		@Override
		public void bases(net.aonsolutions.core.tgss.creta.jaxb.bases.Bases bases) {
			if ( reftificationMark)
				bases.setIndicadorRectificacion("S");
			if ( solicitudRecepcionRNT)
				bases.getLiquidacion().forEach( l -> l.setSolicitudRecepcionRNT("S"));
		}
	}

	private static class MonthlySalaryCretaData implements CretaData {
		@Override
		public String getComment() {
			return "COTIZACION_MENSUAL";
		}

		@Override
		public void add(Salary salary, Tramo<?> tramo, DatoSolicitado datoSolicitado,
				TramoBuilder tramoBuilder, BasesCallback... cbs) {
			try {
				Date fromDate = toDate(tramo.getFechaDesde());
				Date toDate = toDate(tramo.getFechaHasta());
				Period period = new Period(fromDate,toDate);
				
				
				double monthDays = getMonthDays(salary, period, 30);
				if ( monthDays != 30 ) 
					return;  // daily for sure, skip 
				
				
				int lastDayOfMonth = getLastDayOfMonth(period);
				
				// from here MONTH_DAYS == 30, so  
				boolean monthly = lastDayOfMonth != 30 
						|| getQuoteDays(salary, period) == 0
						|| getCotizacionMensual(salary,period);
				
				if (!monthly)
					return; 
				
				DatoBuilder datoBuilder = new DatoBuilder()
						.setCodigo(datoSolicitado.getCodigo())
						.setTipo(datoSolicitado.getTipoDato()).setValor("M");
				tramoBuilder.addDato(datoBuilder.create());
				
			} catch (NoSuchVariableException e) {
				try {
					for (BasesCallback cb : cbs)
						cb.noSuchDato(salary, tramo, datoSolicitado,
								tramoBuilder, true);
				} catch (Cancel c) {

				} catch ( Default d ) {
					for (BasesCallback cb : cbs)
						cb.defaultDato(salary, 
								tramo, 
								datoSolicitado, 
								tramoBuilder, 
								d.getValue());
				}

			}

		}
		

		protected int getLastDayOfMonth(Period period) {
			return getDay(getMonthLastDay(period.getEnd()));			
		}

		protected double getQuoteDays(Salary salary, Period p){
			return getContextVariable(salary, ContextVariable.QUOTE_DAYS, p, () -> -1) ;
		}

		protected double getMonthDays(Salary salary, Period p, double def ){
			return getContextVariable(salary, ContextVariable.MONTH_DAYS, p, () -> def);
		}

		protected boolean getCotizacionMensual(Salary salary, Period p)
				throws NoSuchVariableException {
			List<ContextData> datas = salary.getContextData()
					.get("COTIZACION_MENSUAL");
			
			if (datas == null)
				throw new NoSuchVariableException("COTIZACION_MENSUAL");

			for (ContextData data : datas) {
				Period intersect = p.intersect(
						new Period(data.getStartDate(), data.getEndDate()));
				if (intersect == null)
					continue;

				// TODO : More than one unique valor ?
				return ExpressionContext.eval(data.getExpression(),
						Boolean.class);
			}

			throw new NoSuchVariableException("COTIZACION_MENSUAL");
		}
		
		private double getContextVariable(Salary salary, ContextVariable contextVariable, Period p, DoubleSupplier def){
			List<ContextData> datas = salary.getContextData()
					.get(contextVariable.getName());
			
			if (datas == null)
				return def.getAsDouble();

			for (ContextData data : datas) {
				Period intersect = p.intersect(
						new Period(data.getStartDate(), data.getEndDate()));
				if (intersect == null)
					continue;

				// TODO : More than one unique valor ?
				try {
					return ExpressionContext.eval(data.getExpression(),
							Number.class).doubleValue();
				} catch ( Exception e ) {
					
				}
			}

			return def.getAsDouble();
		}
	}


	@SuppressWarnings("serial")
	private static class NoSuchVariableException extends Exception {
		public NoSuchVariableException(String variable) {
			super();
		}

	}

	// TODO: CompositeException ???
	@SuppressWarnings("serial")
	private static class NoSuchVariablesException
			extends RuntimeException {

		public NoSuchVariablesException(
				String... variables) {
			super();
		}


	}

	@SuppressWarnings("serial")
	private static class ZeroValueException extends RuntimeException {
		private String variable;

		public ZeroValueException(String variable) {
			this.variable = variable;
		}

		public String getVariable() {
			return variable;
		}
	}

	@SuppressWarnings("serial")
	private static class NegativeValueException extends ZeroValueException {

		public NegativeValueException(String variable) {
			super(variable);
		}
		
	}

	@SuppressWarnings("serial")
	private static class AmbiguousVariableException extends Exception {

		private String values[];

		public AmbiguousVariableException(
				String variable, String... values) {
			super();
			this.values = values;
		}

		public String[] getValues() {
			return values;
		}

	}

	@SuppressWarnings("serial")
	private static class UnMatchedVariableException extends Exception {

		private String variable;
		private ContextData contextData;

		public UnMatchedVariableException(
				String variable, ContextData contextData) {
			super();
			this.variable = variable;
			this.contextData = contextData;

		}

		public ContextData getContextData() {
			return contextData;
		}

		public String getVariable() {
			return variable;
		}

	}

	
	private static class HCretaData extends CCretaData {

		public HCretaData(String variable) {
			super(variable);
		}

		@Override
		public void add(Salary salary, Tramo tramo, DatoSolicitado datoSolicitado,
				TramoBuilder tramoBuilder, BasesCallback... cbs) {
			// B -> El código del dato solicitado es obligatorio.
			// P -> El código del dato solicitado es opcional.

			boolean optional = isOptional(datoSolicitado);
			try {
				double newValue = get(salary, tramo.getFechaDesde(),
						tramo.getFechaHasta());

				if (optional && newValue == 0.00)
					return;

				if (newValue == 0.00)
					for (BasesCallback cb : cbs)
						cb.zeroDato(variable, datoSolicitado, tramo,
								salary);

				DatoBuilder datoBuilder = new DatoBuilder();
				datoBuilder.setCodigo(datoSolicitado.getCodigo());
				datoBuilder.setTipo(datoSolicitado.getTipoDato());
				datoBuilder.setHoras((int)newValue); // truncate decimal points
				tramoBuilder.addDato(datoBuilder.create());

			} catch (NoSuchVariableException e) {
				try {
					for (BasesCallback cb : cbs)
						cb.noSuchDato(salary, tramo, datoSolicitado,
								tramoBuilder, optional);
				} catch (Cancel c) {
				}
			} catch (UnMatchedVariableException e) {
				for (BasesCallback cb : cbs)
					cb.unMatchedVariable(salary, e.getVariable(),
							e.getContextData(), datoSolicitado, tramo,
							tramoBuilder, optional);
			}
		}

	}

	public static interface BasesCallback {

		default void bases(net.aonsolutions.core.tgss.creta.jaxb.bases.Bases bases) {
		};


		default void noDiffs(
				net.aonsolutions.core.tgss.creta.jaxb.bases.Liquidacion liquidacion) {

		}
		
		default void trabajadorFound(
				Trabajador trabajadorCreta, Salary salary) {
		};

		default void trabajadorAdded(
				net.aonsolutions.core.tgss.creta.jaxb.bases.Trabajador trabajadorAon,
				Trabajador trabajadorCreta, Salary salary) {
		};

		default void trabajadorSkipped(
				net.aonsolutions.core.tgss.creta.jaxb.bases.Trabajador trabajadorAon,
				Trabajador trabajadorCreta, Salary salary) {
		};

		default void trabajadorEmpty(
				net.aonsolutions.core.tgss.creta.jaxb.bases.Trabajador trabajadorAon,
				Trabajador trabajadorCreta, Salary salary) {
		};

		default void unknownSalary(Salary salary) {
		};

		default void salaryNotFound(String ccc, Trabajador<?> trabajador,
				Periodo mes) {
		};

		default void rightVariable(String var, Period p,
				String right) {
		};

		default void noSuchVariable(Salary salary, String var,
				Period p, String right) {
		};

		default void wrongVariable(Salary salary, String var,
				Period p, String right, String wrong) {
		};

		default void ambigousVariable(Salary salary, String var,
				Period p, String right, String... wrongs) {
		};

		default void unMatchedVariable(Salary salary,
				String var, ContextData contextData,
				Dato datoSolicitado, Tramo tramo, TramoBuilder tramoBuilder,
				boolean optional) {
		};

		default void zeroDato(String var, Dato datoSolicitado,
				Tramo tramo, Salary salary) {
		};

		default void negativeDato(String var, Double value, Dato datoSolicitado,
				Tramo tramo, Salary salary) {
		};

		default void noSuchDato(Salary salary, Tramo tramo, Dato datoSolicitado,
				TramoBuilder tramoBuilder, boolean optional) {
		};

		default void unknownDato(Liquidacion<?, ?, ?, ?, ?> liquidacion,
				DatoSolicitado datoSolicitado,
				LiquidacionBuilder liquidacionBuilder) {
		};

		default void defaultDato(Liquidacion<?, ?, ?, ?, ?> liquidacion,
				DatoSolicitado datoSolicitado,
				LiquidacionBuilder liquidacionBuilder,
				String value) {
		};

		default void unknownDato(Salary salary, Trabajador<?> trabajador, Tramo tramo,
				DatoSolicitado datoSolicitado, TramoBuilder tramoBuilder) {
		};

		default void defaultDato(Salary salary, Tramo tramo,
				Dato dato, TramoBuilder tramoBuilder, String value) {
		};

		default void defaultDato(Salary salary, Trabajador<?> trabajador, Tramo tramo,
				DatoSolicitado datoSolicitado, TramoBuilder tramoBuilder, String value) {
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
	private static class Default extends RuntimeException {
		private String value ;

		public Default(String value) {
			super();
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

	@SuppressWarnings("serial")
	private static class Different extends RuntimeException {

	}

	@SuppressWarnings("serial")
	private static class FilterOut extends RuntimeException {

	}

	@SuppressWarnings("serial")
	private static class SkipExisting extends RuntimeException {

	}


	@SuppressWarnings("serial")
	private static class InvalidTrabajador extends RuntimeException {

	}


	private static class SkipExistingCallback implements BasesCallback {
		@Override
		public void trabajadorAdded(
				net.aonsolutions.core.tgss.creta.jaxb.bases.Trabajador trabajadorAon,
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
		public void trabajadorFound(
				Trabajador trabajadorCreta, Salary salary) {
			
			if (!nafs.contains(trabajadorCreta.getNaf())) {
				throw new FilterOut();
			}
			
		}
		
		@Override
		public void salaryNotFound(String ccc, Trabajador<?> trabajador, Periodo mes) {
			if (!nafs.contains(trabajador.getNaf())) {
				throw new FilterOut();
			}
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
		public void unknownDato(Liquidacion<?, ?, ?, ?, ?> liquidacion,
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
		public void unknownDato(Salary salary, Trabajador<?> trabajador, Tramo tramo,
				DatoSolicitado datoSolicitado, TramoBuilder tramoBuilder) {
			addDefault(salary, tramo, datoSolicitado, tramoBuilder);
		}

		// --------------------------------------------------------------------

		private void addDefault(Salary salary, Tramo tramo, Dato datoSolicitado,
				TramoBuilder tramoBuilder) {

			String key = datoSolicitado.getCodigo() + salary.getEmployeeSSNumber();
			if (!defaults.containsKey(key))
				key = datoSolicitado.getCodigo();
			
			if (defaults.containsKey(key)) {
				String valor = defaults.get(key);

				System.err.printf(
						"WARN: %s for %s (%s) [%s-%s-%s...%s-%s-%s] is default valor %s \r\n",
						datoSolicitado.getCodigo(),
						salary.getEmployeeSSNumber(),
						salary.getEmployeeDocument(),
						tramo.getFechaDesde().getDia(),
						tramo.getFechaDesde().getMes(),
						tramo.getFechaDesde().getAnho(),
						tramo.getFechaHasta().getDia(),
						tramo.getFechaHasta().getMes(),
						tramo.getFechaHasta().getAnho(), valor);

				if ( AonStringUtils.isBlank(valor) ) 
					throw new Default(valor);

				DatoBuilder datoBuilder = new DatoBuilder();
				datoBuilder.setCodigo(datoSolicitado.getCodigo());
				datoBuilder.setTipo(datoSolicitado.getTipoDato());
				datoBuilder.setValor(valor);
				tramoBuilder.addDato(datoBuilder.create());

				throw new Default(valor);
			}
		}

		private void addDefault(Dato datoSolicitado,
				Liquidacion<?, ?, ?, ?, ?> liquidacion,
				LiquidacionBuilder liquidacionBuilder) {

			if (defaults.containsKey(datoSolicitado.getCodigo())) {
				String valor = defaults.get(datoSolicitado.getCodigo());
				DatoBuilder datoBuilder = new DatoBuilder();
				datoBuilder.setCodigo(datoSolicitado.getCodigo());
				datoBuilder.setTipo(datoSolicitado.getTipoDato());
				datoBuilder.setValor(valor);
				liquidacionBuilder.addDato(datoBuilder.create());
				System.err.printf("WARN: %s for %s default valor %s \r\n",
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
		public void unMatchedVariable(Salary salary, String var,
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
						"WARN: %s for %s (%s) [%s-%s-%s...%s-%s-%s] fixed valor is zero. Not send because is optional  \r\n",
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
					"WARN: %s for %s (%s) [%s-%s-%s...%s-%s-%s] fixed valor %f \r\n",
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
				put("54", "Causa que da lugar a la obligación de cotizar");

				put("500", "Base de contingencias comunes");
				put("509", "Base de contingencias comunes");
				put("501", "Base de Horas Extras Fuerza Mayor");
				put("502", "Base de Otras Horas Extras");
				put("537", "Base de horas complementarias");
				put("563", "Compensación IT contingencias comunes");

				put("601", "Base de Accidentes de Trabajo");
				put("611", "Base de Accidentes de Trabajo");
				put("603", "Base de Accidentes de Trabajo en situación de IT");
				put("613", "Base de Accidentes de Trabajo en situación de IT");
				put("663", "Compensación IT AT y EP");
				
				put("535", "Base de contingencias comunes Maternidad Tiempo Parcial");
				put("635", "Base AT Maternidad Tiempo Parcial");
				put("634", "Base AT Maternidad Tiempo Parcial");
				
				put("536", "Base de contingencias comunes Expediente de Regulación de Empleo Parcial");
				put("636", "Base AT Expediente de Regulación de Empleo Parcial");
				put("637", "Base AT Expediente de Regulación de Empleo Parcial");
				
				put("300", "Percepciones íntegras Régimen Especial de Artistas");

				put("702", "Base de FOGASA");
				
				put("03", "Número de horas de formación teórica presencial");
				put("04", "Número de horas de formación teórica a distancia");
				put("04", "Número de horas de tutoría");
				put("737", "Bonificación tutoría");

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
		public void noSuchVariable(Salary salary, String var,
				Period p, String right) {
			System.err.println(String
					.format("WARN: '%s' (%s) not in salary data", var, right));
		}

		@Override
		public void rightVariable(String  var, Period p,
				String right) {
			System.err.println(String.format(
					"INFO: '%s' (%s) right valor at salary data", var, right));
		}

		@Override
		public void wrongVariable(Salary salary, String  var,
				Period p, String right, String wrong) {
			System.err.println(String.format(
					"ERROR: '%s' (%s) wrong valor (%s) at salary data", var,
					right, wrong));
		}

		@Override
		public void zeroDato(String  var, Dato datoSolicitado,
				Tramo tramo, Salary salary) {
			System.err.println(String.format(
					"WARN: %s (%s) for %s [%s-%s-%s...%s-%s-%s] is zero",
					var, datoSolicitado.getCodigo(),
					salary.getEmployeeName(), tramo.getFechaDesde().getDia(),
					tramo.getFechaDesde().getMes(),
					tramo.getFechaDesde().getAnho(),
					tramo.getFechaHasta().getDia(),
					tramo.getFechaHasta().getMes(),
					tramo.getFechaHasta().getAnho()));
		};

		@Override
		public void negativeDato(String  var, Double value, Dato datoSolicitado,
				Tramo tramo, Salary salary) {
			System.err.println(String.format(
					"WARN: %s (%s) for %s [%s-%s-%s...%s-%s-%s] is negative %.2f",
					var, datoSolicitado.getCodigo(),
					salary.getEmployeeName(), tramo.getFechaDesde().getDia(),
					tramo.getFechaDesde().getMes(),
					tramo.getFechaDesde().getAnho(),
					tramo.getFechaHasta().getDia(),
					tramo.getFechaHasta().getMes(),
					tramo.getFechaHasta().getAnho(),
					value));
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
		public void unMatchedVariable(Salary salary, String var,
				ContextData contextData, Dato datoSolicitado, Tramo tramo,
				TramoBuilder tramoBuilder, boolean optional) {
			// @formatter:off
			System.err
					.println(String
							.format("ERROR: %s for %s [%3$td-%3$tm-%3$tY...%4$td-%4$tm-%4$tY] not match %5$s [%6$s-%7$s-%8$s...%9$s-%10$s-%11$s]",

							var, salary.getEmployeeName(),
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
			net.aonsolutions.core.tgss.creta.jaxb.bases.Trabajador trabajadorAon;

			public TrabajadorData(String name, String nif, String ss,
					Trabajador<?> trabajadorCreta,
					net.aonsolutions.core.tgss.creta.jaxb.bases.Trabajador trabajadorAon) {
				super(name, nif, ss);
				this.trabajadorCreta = trabajadorCreta;
				this.trabajadorAon = trabajadorAon;
			}
		}

		private static class TrabajadorTramoDato {
			

			String ss;
			String tipo;
			String codigo;
			String diaDesde; 
			String mesDesde; 
			String anhoDesde; 
			String diaHasta; 
			String mesHasta; 
			String anhoHasta; 
			
			String expression;
			Date startDate;
			Date endDate;
			Double negative;
			
			
			@Override
			public boolean equals(Object obj) {
				if ( !(obj instanceof TrabajadorTramoDato) )
					return false;
				TrabajadorTramoDato that = (TrabajadorTramoDato) obj;
				
				return AonStringUtils.equals(this.ss, that.ss)
					&& AonStringUtils.equals(this.tipo, that.tipo)
					&& AonStringUtils.equals(this.codigo, that.codigo)
					&& AonStringUtils.equals(this.diaDesde, that.diaDesde)
					&& AonStringUtils.equals(this.mesDesde, that.mesDesde)
					&& AonStringUtils.equals(this.anhoDesde, that.anhoDesde)
					&& AonStringUtils.equals(this.diaHasta, that.diaHasta)
					&& AonStringUtils.equals(this.mesHasta, that.mesHasta)
					&& AonStringUtils.equals(this.anhoHasta, that.anhoHasta)
						;
			}
			
			@Override
			public int hashCode() {
				return (this.ss
						+ this.tipo
						+ this.codigo
						+ this.diaDesde
						+ this.mesDesde
						+ this.anhoDesde
						+ this.diaHasta
						+ this.mesHasta
						+ this.anhoHasta).hashCode();
			}
			
			@Override
			public String toString() {
				return (this.ss
						+ ", " + this.tipo
						+ ", " + this.codigo
						+ ", " + this.diaDesde
						+ "/" + this.mesDesde
						+ "/" + this.anhoDesde
						+ ".." + this.diaHasta
						+ "/" + this.mesHasta
						+ "/" + this.anhoHasta);
			}
			
			public boolean equalsTramo(Object obj) {
				if ( !(obj instanceof TrabajadorTramoDato) )
					return false;
				TrabajadorTramoDato that = (TrabajadorTramoDato) obj;
				
				return AonStringUtils.equals(this.ss, that.ss)
					&& AonStringUtils.equals(this.diaDesde, that.diaDesde)
					&& AonStringUtils.equals(this.mesDesde, that.mesDesde)
					&& AonStringUtils.equals(this.anhoDesde, that.anhoDesde)
					&& AonStringUtils.equals(this.diaHasta, that.diaHasta)
					&& AonStringUtils.equals(this.mesHasta, that.mesHasta)
					&& AonStringUtils.equals(this.anhoHasta, that.anhoHasta)
						;
			}
			
			
		}

		private XMLStreamWriter xsw;

		private Set<TrabajadorTramoDato> defau1t;
		private Set<TrabajadorTramoDato> negative;
		private Set<TrabajadorTramoDato> unmatched;

		private Map<String, Data> addedEnterpriseDataMap;

		private Stack<TrabajadorData> skippedTrabajadorList;
		private Map<String, TrabajadorData> addedTrabajadorDataMap;

		private Tramo<?> tramoCreta;
		private Trabajador<?> trabajadorCreta;
		
		private net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramoAon;
		private net.aonsolutions.core.tgss.creta.jaxb.bases.Trabajador trabajadorAon;


		public Comments(XMLStreamWriter xsw) {
			super();
			this.xsw = xsw;
			this.defau1t = new HashSet<TrabajadorTramoDato>();
			this.negative = new HashSet<TrabajadorTramoDato>();
			this.unmatched = new HashSet<TrabajadorTramoDato>();
			this.skippedTrabajadorList = new Stack<TrabajadorData>();
			this.addedEnterpriseDataMap = new HashMap<String, Data>();
			this.addedTrabajadorDataMap = new HashMap<String, TrabajadorData>();
			
		}
		
		@Override
		public void beforeMarshal(Object source) {

			if (source instanceof net.aonsolutions.core.tgss.creta.jaxb.bases.Trabajador)
				beforeMarshalTrabajador(
						(net.aonsolutions.core.tgss.creta.jaxb.bases.Trabajador) source);
			else if (source instanceof net.aonsolutions.core.tgss.creta.jaxb.bases.Dato)
				beforeMarshalDato(
						(net.aonsolutions.core.tgss.creta.jaxb.bases.Dato) source);
			else if (source instanceof net.aonsolutions.core.tgss.creta.jaxb.bases.CtaCot)
				beforeMarshalCtaCot(
						(net.aonsolutions.core.tgss.creta.jaxb.bases.CtaCot) source);
			else if (source instanceof net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo)
				beforeMarshalTramo(
						(net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo) source);

			super.beforeMarshal(source);
		}
		
		@Override
		public void salaryNotFound(String ccc, Trabajador<?> trabajador, Periodo mes) {
		}
		
		@Override
		public void unMatchedVariable(Salary salary, String var, ContextData contextData, Dato datoSolicitado,
				Tramo tramo, TramoBuilder tramoBuilder, boolean optional) {
			unmatched.add(new TrabajadorTramoDato() {{
					this.ss = salary.getEmployeeSSNumber();
					this.codigo = datoSolicitado.getCodigo();
					this.tipo = datoSolicitado.getTipoDato();
					this.diaDesde = tramo.getFechaDesde().getDia();
					this.mesDesde = tramo.getFechaDesde().getMes();
					this.anhoDesde = tramo.getFechaDesde().getAnho();
					this.diaHasta = tramo.getFechaHasta().getDia();
					this.mesHasta = tramo.getFechaHasta().getMes();
					this.anhoHasta = tramo.getFechaHasta().getAnho();
					
					this.startDate = contextData.getStartDate();
					this.endDate = contextData.getEndDate();
					this.expression = contextData.getExpression();
					
					}}
					);
		}
		
		@Override
		public void negativeDato(String var, Double value, Dato datoSolicitado, Tramo tramo, Salary salary) {
			negative.add(new TrabajadorTramoDato() {{
				this.ss = salary.getEmployeeSSNumber();
				this.codigo = datoSolicitado.getCodigo();
				this.tipo = datoSolicitado.getTipoDato();
				this.diaDesde = tramo.getFechaDesde().getDia();
				this.mesDesde = tramo.getFechaDesde().getMes();
				this.anhoDesde = tramo.getFechaDesde().getAnho();
				this.diaHasta = tramo.getFechaHasta().getDia();
				this.mesHasta = tramo.getFechaHasta().getMes();
				this.anhoHasta = tramo.getFechaHasta().getAnho();
				
				this.negative = value;
				
				}}
				);
		}
		
		@Override
		public void defaultDato(Salary salary, Trabajador<?> trabajador, Tramo tramo, DatoSolicitado datoSolicitado,
				TramoBuilder tramoBuilder, String value) {
			defau1t.add(new TrabajadorTramoDato() {{
				this.ss = salary.getEmployeeSSNumber();
				this.codigo = datoSolicitado.getCodigo();
				this.tipo = datoSolicitado.getTipoDato();
				this.diaDesde = tramo.getFechaDesde().getDia();
				this.mesDesde = tramo.getFechaDesde().getMes();
				this.anhoDesde = tramo.getFechaDesde().getAnho();
				this.diaHasta = tramo.getFechaHasta().getDia();
				this.mesHasta = tramo.getFechaHasta().getMes();
				this.anhoHasta = tramo.getFechaHasta().getAnho();
				
				this.expression = value;
				
				}}
				);
		}

		@Override
		public void trabajadorAdded(
				net.aonsolutions.core.tgss.creta.jaxb.bases.Trabajador trabajadorAon,
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
				net.aonsolutions.core.tgss.creta.jaxb.bases.Trabajador trabajadorAon,
				Trabajador trabajadorCreta, Salary salary) {
			// skippedTrabajadorList.add(new TrabajadorData(salary
			// .getEmployeeName(), salary.getEmployeeDocument(), salary
			// .getEmployeeSSNumber(), trabajadorAon));
		};

		public void beforeMarshalTrabajador(
				net.aonsolutions.core.tgss.creta.jaxb.bases.Trabajador trabajador) {
			marshallSkipped();
			TrabajadorData data = addedTrabajadorDataMap
					.get(trabajador.getNaf());
			try {
				xsw.writeComment(String.format("%s [%s]", data.name, data.doc));
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			trabajadorAon = trabajador;
			trabajadorCreta = data.trabajadorCreta;
		}

		public void beforeMarshalCtaCot(
				net.aonsolutions.core.tgss.creta.jaxb.bases.CtaCot ctaCot) {
			String ccc = ctaCot.getProvincia() + ctaCot.getNumero();
			Data data = addedEnterpriseDataMap.get(ccc);
			try {
				xsw.writeComment(String.format("%s [%s]", data.name, data.doc));
			} catch (XMLStreamException e) {
			} catch (NullPointerException e) {
			}
		}

		public void beforeMarshalDato(
				net.aonsolutions.core.tgss.creta.jaxb.bases.Dato datoAon) {

			unMatched(datoAon).ifPresent(t -> {
				try {
					xsw.writeComment(String.format("Arreglado [%s%s%s%s%s%s]: '%s' (%8$td/%8$tm/%8$tY..%9$td/%9$tm/%9$tY) " ,
							t.ss,
							t.codigo,
							t.diaDesde,
							t.mesDesde,
							t.diaHasta,
							t.mesHasta,
							
							t.expression , 
							t.startDate, 
							t.endDate ));
				} catch (XMLStreamException e) {
				}
			});

			defau1t(datoAon).ifPresent(t -> {
				try {
					xsw.writeComment(String.format("Valor por defecto [%s%s%s%s%s%s]: '%s' (%s/%s/%s..%s/%s/%s) " ,
							t.ss,
							t.codigo,
							t.diaDesde,
							t.mesDesde,
							t.diaHasta,
							t.mesHasta,
							
							t.expression , 
							t.diaDesde,
							t.mesDesde,
							t.anhoDesde,
							t.diaHasta,
							t.mesHasta,
							t.anhoHasta
							));
				} catch (XMLStreamException e) {
				}
			});

			if (tramoCreta == null)
				return;
			

			for (Dato datoCreta : tramoCreta.getDatosTramo().getDato()) {
				if (!AonStringUtils.equalsIgnoreCase(datoCreta.getTipoDato(),
						datoAon.getTipoDato()))
					continue;
				if (!AonStringUtils.equalsIgnoreCase(datoCreta.getCodigo(),
						datoAon.getCodigo()))
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
				net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramoAon) {
			this.tramoAon = tramoAon;

			negative(tramoAon).forEach(t -> {
				try {
					xsw.writeComment(String.format("Eliminado [%s%s%s%s%s%s]: %s:%s '%s' (%s/%s/%s..%s/%s/%s) " ,
							t.ss,
							t.codigo,
							t.diaDesde,
							t.mesDesde,
							t.diaHasta,
							t.mesHasta,
							
							t.tipo , 
							t.codigo , 
							t.negative , 
							t.diaDesde,
							t.mesDesde,
							t.anhoDesde,
							t.diaHasta,
							t.mesHasta,
							t.anhoHasta
							));
				} catch (XMLStreamException e) {
				}
			});

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
		
		private Optional<TrabajadorTramoDato> unMatched(net.aonsolutions.core.tgss.creta.jaxb.bases.Dato datoAon) {
			if ( trabajadorAon == null )
				return Optional.empty();
			
			TrabajadorTramoDato t = new TrabajadorTramoDato() {{
				this.ss = trabajadorAon.getNaf();
				this.codigo = datoAon.getCodigo();
				this.tipo = datoAon.getTipoDato();
				this.diaDesde = tramoAon.getFechaDesde().getDia();
				this.mesDesde = tramoAon.getFechaDesde().getMes();
				this.anhoDesde = tramoAon.getFechaDesde().getAnho();
				this.diaHasta = tramoAon.getFechaHasta().getDia();
				this.mesHasta = tramoAon.getFechaHasta().getMes();
				this.anhoHasta = tramoAon.getFechaHasta().getAnho();
			}};
			
			return unmatched.stream().filter( t1 -> t1.equals(t) ).findFirst() ;
		}

		private Stream<TrabajadorTramoDato> negative(net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo) {
			if ( trabajadorAon == null )
				return Stream.empty();
			
			TrabajadorTramoDato t = new TrabajadorTramoDato() {{
				this.ss = trabajadorAon.getNaf();
				//this.codigo = datoAon.getCodigo();
				//this.tipo = datoAon.getTipoDato();
				this.diaDesde = tramoAon.getFechaDesde().getDia();
				this.mesDesde = tramoAon.getFechaDesde().getMes();
				this.anhoDesde = tramoAon.getFechaDesde().getAnho();
				this.diaHasta = tramoAon.getFechaHasta().getDia();
				this.mesHasta = tramoAon.getFechaHasta().getMes();
				this.anhoHasta = tramoAon.getFechaHasta().getAnho();
			}};
			
			return negative.stream().filter( t1 -> t1.equalsTramo(t) );
		}

		private Optional<TrabajadorTramoDato> defau1t(net.aonsolutions.core.tgss.creta.jaxb.bases.Dato datoAon) {
			if ( trabajadorAon == null )
				return Optional.empty();
			
			TrabajadorTramoDato t = new TrabajadorTramoDato() {{
				this.ss = trabajadorAon.getNaf();
				this.codigo = datoAon.getCodigo();
				this.tipo = datoAon.getTipoDato();
				this.diaDesde = tramoAon.getFechaDesde().getDia();
				this.mesDesde = tramoAon.getFechaDesde().getMes();
				this.anhoDesde = tramoAon.getFechaDesde().getAnho();
				this.diaHasta = tramoAon.getFechaHasta().getDia();
				this.mesHasta = tramoAon.getFechaHasta().getMes();
				this.anhoHasta = tramoAon.getFechaHasta().getAnho();
			}};
			
			return defau1t.stream().filter( t1 -> t1.equals(t) ).findFirst() ;
		}
	}

	private static interface CretaData {

		String getComment();

		void add(Salary salary, Tramo<?> tramo, DatoSolicitado datoSolicitado,
				TramoBuilder tramoBuilder, BasesCallback... cb);

	}

	private static abstract class AbstractCCretaData implements CretaData {

		protected boolean isOptional(DatoSolicitado datoSolicitado) {
			String indicadorObligatoriedad = datoSolicitado.getIndicadorObligatoriedad();
			if ( indicadorObligatoriedad == null )
				return false;
			return "P".equalsIgnoreCase(indicadorObligatoriedad.trim());
			
		}

		protected abstract Double get(Salary salary, Fecha desde, Fecha hasta)
				throws NoSuchVariableException,
				UnMatchedVariableException;

		protected abstract void zeroValue(Salary salary, Tramo tramo,
				Dato datoSolicitado, TramoBuilder tramoBuilder,
				BasesCallback... cbs);

		// CretaData ----------------------------------------------------------
		@Override
		public void add(Salary salary, Tramo tramo, DatoSolicitado datoSolicitado,
				TramoBuilder tramoBuilder, BasesCallback... cbs) {
			// B -> El código del dato solicitado es obligatorio.
			// P -> El código del dato solicitado es opcional.

			boolean optional = isOptional(datoSolicitado) ;
			try {
				double newValue = get(salary, tramo.getFechaDesde(),
						tramo.getFechaHasta());

				if (optional && newValue == 0.00)
					return;
				
				newValue = check(newValue, salary, tramo, datoSolicitado, tramoBuilder, cbs);

				if (optional && newValue == 0.00)
					return;

				DatoBuilder datoBuilder = new DatoBuilder();
				datoBuilder.setCodigo(datoSolicitado.getCodigo());
				datoBuilder.setTipo(datoSolicitado.getTipoDato());
				datoBuilder.setImporteEuros(newValue);
				tramoBuilder.addDato(datoBuilder.create());
			} catch (NoSuchVariableException e) {
				for (BasesCallback cb : cbs)
					cb.noSuchDato(salary, tramo, datoSolicitado, tramoBuilder,
							optional);
			} catch (UnMatchedVariableException e) {
				try {
					for (BasesCallback cb : cbs)
						cb.unMatchedVariable(salary,
								e.getVariable(), e.getContextData(),
								datoSolicitado, tramo, tramoBuilder, optional);
				} catch (Cancel c) {

				}
			}
		}
		
		protected Double check(Double value, Salary salary, Tramo tramo, DatoSolicitado datoSolicitado,
				TramoBuilder tramoBuilder, BasesCallback... cbs ) {
			if (value == 0.00)
				zeroValue(salary, tramo, datoSolicitado, tramoBuilder, cbs);
			return value;
		}

	}

	private static class CCretaData extends AbstractCCretaData {

		protected String variable;

		public CCretaData(String variable) {
			this.variable = variable;
		}

		// AbstractCCretaData -------------------------------------------------

		@Override
		public String getComment() {
			return variable;
		}

		@Override
		public Double get(Salary salary, Fecha desde, Fecha hasta)
				throws NoSuchVariableException,
				UnMatchedVariableException {
			return get(variable, salary,
					new Period(toDate(desde), toDate(hasta)));
		}

		@Override
		protected void zeroValue(Salary salary, Tramo tramo,
				Dato datoSolicitado, TramoBuilder tramoBuilder,
				BasesCallback... cbs) {
			for (BasesCallback cb : cbs)
				cb.zeroDato(variable, datoSolicitado, tramo, salary);
		}

		// --------------------------------------------------------------------

		protected static Double get(String variable,
				Salary salary, Period p) throws NoSuchVariableException,
						UnMatchedVariableException {
			List<ContextData> datas = salary.getContextData()
					.get(variable);
			if (datas == null || datas.isEmpty())
				throw new NoSuchVariableException(variable);

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
					throw new UnMatchedVariableException(variable,
							data);

				found = true;
				ret += ExpressionContext.eval(data.getExpression(),
						Double.class) * days(intersect) / days(dataPeriod);
			}

			if (!found)
				throw new NoSuchVariableException(variable);
//				throw new UnMatchedVariableException(variable,
//						datas.get(0));

			return ret;
		}

	}
	

	private static class NonNegativeCCretaData extends CCretaData {

		public NonNegativeCCretaData(String variable) {
			super(variable);
		}
		
		@Override
		protected Double check(Double value, Salary salary, Tramo tramo, DatoSolicitado datoSolicitado,
				TramoBuilder tramoBuilder, BasesCallback... cbs) {
 			if ( value < 0.00 ) {
				negativeValue(value, salary, tramo, datoSolicitado, tramoBuilder, cbs);
				return 0.00;
 			}
			
 			return super.check(value, salary, tramo, datoSolicitado, tramoBuilder, cbs);
		}
		

		protected void negativeValue(Double value, Salary salary, Tramo tramo,
				Dato datoSolicitado, TramoBuilder tramoBuilder,
				BasesCallback... cbs) {
			for (BasesCallback cb : cbs)
				cb.negativeDato(variable, value, datoSolicitado, tramo, salary);
		}
	}
	
	private static class DistributeHCretaData extends HCretaData {
		
		public DistributeHCretaData(String variable) {
			super(variable);
		}
		
		@Override
		public Double get(Salary salary, Fecha desde, Fecha hasta)
				throws NoSuchVariableException, UnMatchedVariableException {
			return DistributeCCretaData.get(variable, salary,new Period(toDate(desde), toDate(hasta)));
		}
	}

	private static class DistributeCCretaData extends CCretaData {


		public DistributeCCretaData(String variable) {
			super(variable);
		}

		// CCretaData ---------------------------------------------------------

		@Override
		public Double get(Salary salary, Fecha desde, Fecha hasta)
				throws NoSuchVariableException,
				UnMatchedVariableException {
			return get(variable, salary,
					new Period(toDate(desde), toDate(hasta)));
		}

		// --------------------------------------------------------------------

		protected static Double get(String variable,
				Salary salary, Period p) throws NoSuchVariableException,
						UnMatchedVariableException {
			List<ContextData> datas = salary.getContextData()
					.getOrDefault(variable, Collections.emptyList());
						
			double ret = datas.stream()
			.collect(Collectors.summingDouble(DistributeCCretaData::eval));

			if (ret == 0.00)
				throw new NoSuchVariableException(variable);
			
			long workedDays = 
			salary.getContextData()
			.getOrDefault(WORKED_DAYS.getName(), Collections.emptyList())
			.stream().collect(Collectors.summingLong(DistributeCCretaData::days))
			;
			if ( workedDays == 0 )
				workedDays = 
				salary.getContextData()
				.getOrDefault(CGC_BASE.getName(), Collections.emptyList())
				.stream().collect(Collectors.summingLong(DistributeCCretaData::days))
				;
			
			long days = p.daysStream().count();
			

			return ret / workedDays * days;
		}
		
		private static long days(ContextData d) {
			return new Period(d.getStartDate(),d.getEndDate()).daysStream().count();
		}

		private static Double eval(ContextData d) {
			return ExpressionContext.eval(d.getExpression(), Double.class);
		}

	}
	


	private static class CompositeCCretaData
			extends AbstractCCretaData {

		protected List<String> variables;

		public CompositeCCretaData() {
			this.variables = new ArrayList<String>();
		}
		
		public CompositeCCretaData add(String name) {
			this.variables.add(name);
			return this;
		}
		
		public CompositeCCretaData add(ContextVariable var) {
			this.variables.add(var.getName());
			return this;
		}
		
		public CompositeCCretaData add(ContextVariable ...vars) {
			for ( ContextVariable var : vars)
				this.variables.add(var.getName());
			
			return this;
		}		
		// AbstractCCretaData -------------------------------------------------

		@Override
		public String getComment() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void add(Salary salary, Tramo tramo, DatoSolicitado datoSolicitado,
				TramoBuilder tramoBuilder, BasesCallback... cbs) {
			try {
				super.add(salary, tramo, datoSolicitado, tramoBuilder, cbs);
			} catch (ZeroValueException e) {
				for (BasesCallback cb : cbs)
					cb.zeroDato(e.getVariable(), datoSolicitado, tramo,
							salary);
			} catch (NoSuchVariablesException e) {
				for (BasesCallback cb : cbs)
					cb.noSuchDato(salary, tramo, datoSolicitado, tramoBuilder,
							isOptional(datoSolicitado));
			}
		}

		@Override
		public Double get(Salary salary, Fecha desde, Fecha hasta)
				throws NoSuchVariableException,
				UnMatchedVariableException {
			Period p = new Period(toDate(desde), toDate(hasta));
			for (String variable : variables) {
				try {
					Double value = CCretaData.get(variable,
							salary, p);
					if (value == 0.00)
						throw new ZeroValueException(variable);
					// System.err.println("--- CompositeCContextCretaData " +
					// contextVariable.getName() + ", " +
					// salary.getEmployeeName() + " = " + valor );
					return value;
				} catch (NoSuchVariableException e) {
					// Try next variable
				}
			}
			throw new NoSuchVariablesException(variables.toArray(String[]::new));
		}

		@Override
		protected void zeroValue(Salary salary, Tramo tramo,
				Dato datoSolicitado, TramoBuilder tramoBuilder,
				BasesCallback... cbs) {
			// Nothing 'ZeroValueException' .
		}

		// --------------------------------------------------------------------
	}
	
	private static class CompositeHCretaData
	extends HCretaData {
		
		protected List<String> variables;
		
		public CompositeHCretaData() {
			super(null);
			this.variables = new ArrayList<String>();
		}
		
		public CompositeHCretaData add(String name) {
			this.variables.add(name);
			return this;
		}
		
		public CompositeHCretaData add(ContextVariable var) {
			this.variables.add(var.getName());
			return this;
		}
		
		public CompositeHCretaData add(ContextVariable ...vars) {
			for ( ContextVariable var : vars)
				this.variables.add(var.getName());
			
			return this;
		}		
		// AbstractCCretaData -------------------------------------------------
		
		@Override
		public void add(Salary salary, Tramo tramo, DatoSolicitado datoSolicitado, TramoBuilder tramoBuilder,
				BasesCallback... cbs) {
			try {
				super.add(salary, tramo, datoSolicitado, tramoBuilder, cbs);
			} catch ( NoSuchVariablesException e ) {
				for (BasesCallback cb : cbs)
					cb.noSuchDato(salary, tramo, datoSolicitado, tramoBuilder,
							isOptional(datoSolicitado));
			}
			
		}
		
		@Override
		public Double get(Salary salary, Fecha desde, Fecha hasta)
				throws NoSuchVariableException, UnMatchedVariableException {			
			for ( String var: variables ) {
				try {
					variable = var;
					Period p = new Period(toDate(desde), toDate(hasta));
					return get(var, salary, p);
				} catch ( NoSuchVariableException e ) {
				} catch ( UnMatchedVariableException e ) {
					
				}
			}
			throw new NoSuchVariablesException(variables.toArray(String[]::new));
		}
		
		protected static Double get(String variable,
				Salary salary, Period p) throws NoSuchVariableException,
						UnMatchedVariableException {
			List<ContextData> datas = salary.getContextData()
					.get(variable);
			if (datas == null || datas.isEmpty())
				throw new NoSuchVariableException(variable);
			
			boolean found=false;
			double ret = 0.00;
			for (ContextData data : datas) {

				Period dataPeriod = new Period(data.getStartDate(),
						data.getEndDate());

				Period intersect = p.intersect(dataPeriod);
				if (intersect == null)
					continue;

				if (data.getStartDate().before(p.getStart())
						|| data.getEndDate().after(p.getEnd()))
					throw new UnMatchedVariableException(variable,
							data);
				found = true;
				ret += ExpressionContext.eval(data.getExpression(),
						Double.class) * days(intersect) / days(p);
				
			}
			if ( found )
				return ret;

			throw new NoSuchVariableException(variable);

		}		
	}

	private static class NonNegativeCompositeCCretaData extends  CompositeCCretaData {

		@Override
		protected Double check(Double value, Salary salary, Tramo tramo, DatoSolicitado datoSolicitado,
				TramoBuilder tramoBuilder, BasesCallback... cbs) {
 			if ( value < 0.00 ) {
				negativeValue(value, salary, tramo, datoSolicitado, tramoBuilder, cbs);
				return 0.00;
 			}
			return super.check(value, salary, tramo, datoSolicitado, tramoBuilder, cbs);
		}
		
		protected void negativeValue(Double value, Salary salary, Tramo tramo,
				Dato datoSolicitado, TramoBuilder tramoBuilder,
				BasesCallback... cbs) {
			for (BasesCallback cb : cbs)  {
				cb.negativeDato(variables.get(variables.size()-1), value, datoSolicitado, tramo, salary);
			}
		}
	}

	private static class FirstGreaterThanZeroCompositeCCretaData extends  NonNegativeCompositeCCretaData {

		@Override
		public Double get(Salary salary, Fecha desde, Fecha hasta)
				throws NoSuchVariableException,
				UnMatchedVariableException {
			Period p = new Period(toDate(desde), toDate(hasta));
			for (String variable : variables) {
				try {
					Double value = CCretaData.get(variable,
							salary, p);
					if (value <= 0.00)
						continue;
					return value;
				} catch (NoSuchVariableException e) {
					// Try next variable
				}
			}
			throw new NoSuchVariablesException(variables.toArray(String[]::new));
		}
		

	}

	
	private static Map<String, CretaData> CONTEXT_VARIABLE_MAP = new HashMap<String, CretaData>() {
		{
			put("500", new NonNegativeCCretaData(CGC_BASE.getName()));
			put("535", new NonNegativeCCretaData(MATERNITY_BASE.getName()));
			put("536", new NonNegativeCompositeCCretaData().add(ERE_BASES));
			
			put("537", new NonNegativeCCretaData(ADDITIONAL_BASE.getName()));

			put("501", new CCretaData(STRUCTURAL_OVERTIME_BASE.getName()));
			put("502", new CCretaData(
					NON_STRUCTURAL_OVERTIME_BASE.getName()));
			put("563", new CCretaData(ContextVariable.PREST_IT));

			put("601", new NonNegativeCCretaData(CGP_BASE.getName()));
			put("611", new NonNegativeCCretaData(CGP_BASE.getName()));
			put("635", new NonNegativeCCretaData(MATERNITY_BASE.getName()));
			put("634", new NonNegativeCCretaData(MATERNITY_BASE.getName()));
			put("636", new NonNegativeCompositeCCretaData().add(ERE_BASES));
			put("637", new NonNegativeCompositeCCretaData().add(ERE_BASES));

			put("663", new CCretaData(ContextVariable.PREST_IT));

			put("01", new HCretaData(WORKED_HOURS.getName()) {
				@Override
				public Double get(Salary salary, Fecha desde, Fecha hasta)
						throws NoSuchVariableException,
						UnMatchedVariableException {
					try {
						return super.get(salary, desde, hasta);
					} catch (NoSuchVariableException e) {
					}
					
					try {
						Period p = new Period(toDate(desde), toDate(hasta));
						return get(SALARY_HOURS.getName(),salary,  p);
					} catch (NoSuchVariableException e) {
					}

					return getWorkedHours(salary, desde, hasta);
				};
			});
			put("02", new HCretaData(ADDITIONAL_HOURS.getName()));

			put("05", new CompositeHCretaData() {
					public Double get(Salary salary, Fecha desde, Fecha hasta) 
					throws NoSuchVariableException 
					,UnMatchedVariableException {
						double h05 = 0.00;
						Period p = new Period(toDate(desde), toDate(hasta));
						for ( String var: variables ) {
							try {								
								h05 = get(var, salary, p);								
							} catch (NoSuchVariableException e) {
								continue;
							}
							catch ( UnMatchedVariableException e) {
								h05 = ExpressionContext.eval(e.getContextData().getExpression(),Double.class);
							}
							return (1.00 - h05 ) * 1000.00;
						} 
						throw new NoSuchVariablesException(variables.toArray(String[]::new));				
					};
					}.add(ERE_FACTORS)
			);

			put("51", new MonthlySalaryCretaData());

			
			put("509", new NonNegativeCompositeCCretaData()
					.add(MATERNITY_BASE) 		
					.add(ERE_BASES)
					.add(DIRECT_BASE)
					.add(CGC_BASE)
					.add(CGC_BASE_ENTERPRISE)
					);
			
			put("603", new FirstGreaterThanZeroCompositeCCretaData()
					.add(MATERNITY_BASE)
					.add(DIRECT_BASE)		//   
					.add(CGP_BASE)
					.add(ERE_BASES )
					.add(CGC_BASE_ENTERPRISE)	  
					);
			put("613", new NonNegativeCompositeCCretaData()
					.add(MATERNITY_BASE)
					.add(ERE_BASES)
					.add(CGP_BASE)
					.add(DIRECT_BASE)			//   
					.add(CGC_BASE_ENTERPRISE)	  
					);
			
			put("702", new NonNegativeCCretaData(CGP_BASE_ENTERPRISE.getName()));
		
			put("300", new NonNegativeCCretaData(TOTAL_PAYMENT.getName()));
			
			put("737", new DistributeCCretaData(SLD_C737.getName()));
			put("06", new DistributeHCretaData(SLD_H06.getName()));
			put("03", new DistributeHCretaData(SLD_H03.getName()));
			put("04", new DistributeHCretaData(SLD_H04.getName()));
		}
	};

	// ------------------------------------------------------------------------

	private static <D extends DatoSolicitado> void bases(
			BasesBuilder basesBuilder, 
			AONContext ctx,
			Liquidacion<?, ?, ?, ?, ?> liquidacion, 
			boolean aceptarBasesAnteriores,
			BasesCallback... cbs) {
		
		basesBuilder.addLiquidacion(
				liquidacion(ctx, liquidacion, aceptarBasesAnteriores, cbs));
	}

	private static <D extends DatoSolicitado> net.aonsolutions.core.tgss.creta.jaxb.bases.Liquidacion liquidacion(
			AONContext ctx, Liquidacion<?, ?, ?, ?, ?> liquidacion,
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

		datosLiquidacion(ctx, liquidacion, liquidacionBuilder, cbs);

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

			trabajadores(liquidacionMesBuilder, 
					ctx, 
					liquidacion.getCcc(),
					liquidacion.getTipo(),
					liquidacionMes.getMesLiquidativo(), 
					trabajadores, 
					cbs);
			
			net.aonsolutions.core.tgss.creta.jaxb.bases.LiquidacionMes l = 
					liquidacionMesBuilder.create();
			if ( isNotEmpty(l) )
				liquidacionBuilder.addLiquidacionMes(l);

		}

		return liquidacionBuilder.create();
	}
	
	private static boolean isNotEmpty(net.aonsolutions.core.tgss.creta.jaxb.bases.LiquidacionMes l){
		return ( l.getDatosMes() != null && l.getDatosMes().getDato().size() > 0 ) 
			|| ( l.getTrabajadores() != null && l.getTrabajadores().getTrabajador().size() > 0 );
	}
	

	private static void datosLiquidacion(AONContext ctx, Liquidacion<?, ?, ?, ?, ?> liquidacion,
			LiquidacionBuilder liquidacionBuilder, BasesCallback... cbs) {

		DatosLiquidacion<DatoSolicitado> datosLiquidacion = liquidacion
				.getDatosLiquidacion();
		if (datosLiquidacion == null)
			return;
		
		String tipo = liquidacion.getTipo();
		CtaCot ctaCot = liquidacion.getCcc();
		String ccc = String.format("%s%s", ctaCot.getProvincia(),
				ctaCot.getNumero());
		Periodo desde = liquidacion.getPeriodoDesde();
		Periodo hasta = liquidacion.getPeriodoHasta();
		Calendar calendar = Utils.toCalendar(desde);
		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		Date startDate = calendar.getTime();
		calendar = Utils.toCalendar(hasta);
		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date endDate = calendar.getTime();
		
		Map<String,Double> codigoValorMap = new HashMap<String, Double>();
		// @formatter:off
		AON.getSalaryData(
		ctx,
		props -> props.getCCCProperty().eq(ccc)
				.and(props.getEndDateProperty().ge(startDate))
				.and(props.getStartDateProperty().le(endDate))
				.and(props.getIsSalaryProperty().eq(AonStringUtils.containsIgnoreCase("L00,L91", tipo)))
		)
		.forEach( salary -> {
			Double c763 = salary.getContextData(ContextVariable.SLD_C763.getName(), Collectors.summingDouble(s -> Double.parseDouble(s)));
			if ( c763 != null && c763 > 0.00 )
				codigoValorMap.put("763", codigoValorMap.getOrDefault("763", 0.00) + c763);
		} );
		// @formatter:on

		for (DatoSolicitado dato : datosLiquidacion.getDato()) {
			if ( codigoValorMap.containsKey(dato.getCodigo()))
				liquidacionBuilder.addDato(
				new DatoBuilder()
				.setTipo(dato.getTipoDato())
				.setCodigo(dato.getCodigo())
				.setImporteEuros(codigoValorMap.get("763"))
				.create()
				);
			else 
				try {
					for (BasesCallback cb : cbs)
						cb.unknownDato(liquidacion, dato, liquidacionBuilder);
				} catch (Cancel e) {
				}
		}

	}

	private static <D extends DatoSolicitado> void trabajadores(
			LiquidacionMesBuilder liquidacionMesBuilder, 
			AONContext ctx,
			CtaCot ctaCot, 
			String tipo,
			Periodo mesLiquidativo,
			Map<String, Trabajador<D>> trabajadores, 
			BasesCallback... cbs) {

		String ccc = String.format("%s%s", ctaCot.getProvincia(),
				ctaCot.getNumero());

		Calendar calendar = Utils.toCalendar(mesLiquidativo);
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
						.and(props.getIsSalaryProperty().eq(AonStringUtils.containsIgnoreCase("L00,L91", tipo)))
						.and(props.getIsSettlementProperty().eq(AonStringUtils.equalsIgnoreCase("L13", tipo)))
						.and(props.getIsDelayProperty().eq(AonStringUtils.containsIgnoreCase("L03,L90", tipo)))
						)
						.forEach(
				salary -> trabajador(liquidacionMesBuilder, salary,
						trabajadores, cbs));
		;
		// @formatter:on

		trabajadores.values().forEach(t -> {
			try {
				for (BasesCallback cb : cbs) {
						cb.salaryNotFound(ccc, t, mesLiquidativo);
				}
			} catch ( FilterOut f ) {
				
			}
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

		try {
			for (BasesCallback cb : cbs)
				cb.trabajadorFound(trabajador, salary);

		} catch (FilterOut e) {
			trabajadores.remove(salary.getEmployeeSSNumber());
			return;
		}

		trabajadorBuilder.setNaf(trabajador.getNaf());
		for (Tramo<D> tramo : trabajador.getTramos().getTramo()) {

			try {
				checkTramo(tramo, salary, cbs);
			} catch (Throwable t) {

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
							cb.unknownDato(salary, trabajador, tramo, datoSolicitado,
									tramoBuilder);
					}
					catch (Cancel e) {
					}
					catch (Default e) {
						for (BasesCallback cb : cbs)
							cb.defaultDato(salary, trabajador, tramo, datoSolicitado,
									tramoBuilder, e.getValue());
					}
					continue;
				}

				data.add(salary, tramo, datoSolicitado, tramoBuilder, cbs);
				
			}
			net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo basesTramo = tramoBuilder.create();
			if ( basesTramo.getDatosTramo().getDato().isEmpty())
				;
			else 
				trabajadorBuilder.addTramo(basesTramo);
		}
		

		trabajadores.remove(salary.getEmployeeSSNumber());

		net.aonsolutions.core.tgss.creta.jaxb.bases.Trabajador trabajadorAon = trabajadorBuilder
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

		checkVariable(QUOTE_GROUP.getName(),
				Integer.parseInt(
						tramo.getInformacionAfiliacion().getGrupoCotizacion()),
				p, salary, cbs);
		checkVariable(TC2.getName(),
				tramo.getInformacionAfiliacion().getTipoContrato(), p, salary,
				cbs);
		checkVariable(OCCUPATION.getName(),
				tramo.getInformacionAfiliacion().getOcupacion(), p, salary,
				cbs);

		String partialFactor = tramo.getInformacionAfiliacion()
				.getCoeficienteTiempoParcial();
		if (partialFactor != null)
			checkVariable(PARTIAL_FACTOR.getName(),
					(partialFactor != null
							? Integer.parseInt(partialFactor) / 1000.00 : null),
					3, p, salary, cbs);
		// for (Peculiaridad peculiaridad : tramo.getInformacionAfiliacion()
		// .getPeculiaridades().getPeculiaridad()) {
		//
		// }

	}

	private static void checkVariable(String var,
			String rigthValue, Period p, Salary salary, BasesCallback... cbs) {

		try {
			String salaryValue = get(salary, var, p);

			if (!StringUtils.equalsIgnoreCase(rigthValue, salaryValue))
				for (BasesCallback cb : cbs)
					cb.wrongVariable(salary, var, p,
							rigthValue, salaryValue);
			else
				for (BasesCallback cb : cbs)
					cb.rightVariable(var, p, rigthValue);

		} catch (NoSuchVariableException e) {
			if (rigthValue != null)
				for (BasesCallback cb : cbs)
					cb.noSuchVariable(salary, var, p,
							rigthValue);
		} catch (AmbiguousVariableException e) {
			for (BasesCallback cb : cbs)
				cb.ambigousVariable(salary, var, p,
						rigthValue, e.getValues());
		}
	}

	private static void checkVariable(String variable,
			Integer rigthValue, Period p, Salary salary, BasesCallback... cbs) {
		String salaryString = null;
		try {

			salaryString = get(salary, variable, p);
			
			if (rigthValue == null && salaryString == null)
				return;

			if (rigthValue == null && salaryString != null)
				for (BasesCallback cb : cbs)
					cb.wrongVariable(salary, variable, p, null,
							salaryString);

			Integer salaryValue = Integer.parseInt(salaryString);

			if (!rigthValue.equals(salaryValue))
				for (BasesCallback cb : cbs)
					cb.wrongVariable(salary, variable, p,
							Integer.toString(rigthValue), salaryString);
			else
				for (BasesCallback cb : cbs)
					cb.rightVariable(variable, p,
							Integer.toString(rigthValue));

		} catch (NumberFormatException e) {
			for (BasesCallback cb : cbs)
				cb.wrongVariable(salary, variable, p,
						Double.toString(rigthValue), salaryString);

		} catch (NoSuchVariableException e) {
			if (rigthValue != null)
				for (BasesCallback cb : cbs)
					cb.noSuchVariable(salary, variable, p,
							Integer.toString(rigthValue));
		} catch (AmbiguousVariableException e) {
			for (BasesCallback cb : cbs)
				cb.ambigousVariable(salary, variable, p,
						Integer.toString(rigthValue), e.getValues());
		} 
		catch ( IllegalArgumentException e) {
			// ??? 
		}
		catch ( Exception e) {
			// ??? 
		}
	}

	private static void checkVariable(String variable,
			Double rigthValue, int decimals, Period p, Salary salary,
			BasesCallback... cbs) {

		String salaryString = null;
		try {
			salaryString = get(salary, variable, p);

			if (rigthValue == null && salaryString == null)
				return;

			if (rigthValue == null && salaryString != null)
				for (BasesCallback cb : cbs)
					cb.wrongVariable(salary, variable, p, null,
							salaryString);

			Double salaryValue = Double.parseDouble(salaryString);
			salaryValue = Math.round(salaryValue * Math.pow(10.00, decimals))
					/ Math.pow(10.00, decimals);

			if (!rigthValue.equals(salaryValue))
				for (BasesCallback cb : cbs)
					cb.wrongVariable(salary, variable, p,
							Double.toString(rigthValue), salaryString);
			else
				for (BasesCallback cb : cbs)
					cb.rightVariable(variable, p,
							Double.toString(rigthValue));

		} catch (NumberFormatException e) {
			for (BasesCallback cb : cbs)
				cb.wrongVariable(salary, variable, p,
						Double.toString(rigthValue), salaryString);

		} catch (NoSuchVariableException e) {
			if (rigthValue != null)
				for (BasesCallback cb : cbs)
					cb.noSuchVariable(salary, variable, p,
							Double.toString(rigthValue));
		} catch (AmbiguousVariableException e) {
			for (BasesCallback cb : cbs)
				cb.ambigousVariable(salary, variable, p,
						Double.toString(rigthValue), e.getValues());
		}
	}

	private static long days(Period p) {
		return AonDateUtils.getDaysBetweenDates(p.getStart(), p.getEnd()) + 1;
	}

	private static String get(Salary salary, String variable,
			Period p) throws NoSuchVariableException,
					AmbiguousVariableException {
		List<ContextData> datas = salary.getContextData()
				.get(variable);

		if (datas == null)
			throw new NoSuchVariableException(variable);

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
				throw new AmbiguousVariableException(variable,
						expression, dataExpression);
		}
		if (expression == null)
			throw new NoSuchVariableException(variable);

		return expression;
	}

	private static CretaData getCretaData(String codigo) {
		return CONTEXT_VARIABLE_MAP.get(codigo);
	}


	private static net.aonsolutions.core.tgss.creta.jaxb.bases.Liquidacion liquidacion(
			net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresTramos,
			AONContext ctx, boolean aceptarBasesAnteriores, XMLStreamWriter xsw,
			BasesCallback... cbs) {
		return liquidacion(ctx, trabajadoresTramos.getLiquidacion(),
				aceptarBasesAnteriores, cbs);
	}

	private static List<net.aonsolutions.core.tgss.creta.jaxb.bases.Liquidacion> liquidaciones(
			net.aonsolutions.core.tgss.creta.jaxb.respuesta.Respuesta respuesta,
			AONContext ctx, boolean aceptarBasesAnteriores, XMLStreamWriter xsw,
			BasesCallback... cbs) {

		List<net.aonsolutions.core.tgss.creta.jaxb.bases.Liquidacion> liquidaciones = new ArrayList<net.aonsolutions.core.tgss.creta.jaxb.bases.Liquidacion>();

		for (Liquidacion<?, ?, ?, ?, ?> liquidacion : respuesta.getLiquidacion())
			liquidaciones.add(
					liquidacion(ctx, liquidacion, aceptarBasesAnteriores, cbs));

		return liquidaciones;

	}

	private static Double getWorkedHours(Salary salary, Fecha desde,
			Fecha hasta) throws NoSuchVariableException {

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

			for (ContextData hourData : hourDatas) {
				if ( AonStringUtils.isNotBlank(hourData.getExpression())) {
					hours += MVEL.eval(hourData.getExpression(), Double.class);
				}
			}

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

	private static int compare(net.aonsolutions.core.tgss.creta.jaxb.bases.Dato d1,
			net.aonsolutions.core.tgss.creta.jaxb.bases.Dato d2) {
		int compare = d1.getTipoDato().compareToIgnoreCase(d2.getTipoDato());
		if (compare != 0)
			return compare;

		return d1.getCodigo().compareToIgnoreCase(d2.getCodigo());
	}

	private static int compare(Tramo t1, Tramo t2) {
		return toDate(t1.getFechaDesde()).compareTo(toDate(t2.getFechaDesde()));
	}

	private static int compare(net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo t1,
			net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo t2) {
		return toDate(t1.getFechaDesde().getAnho(), t1.getFechaDesde().getMes(),
				t1.getFechaDesde().getDia())
						.compareTo(toDate(t2.getFechaDesde().getAnho(),
								t2.getFechaDesde().getMes(),
								t2.getFechaDesde().getDia()));
	}

	private static int compare(Tramo t1,
			net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo t2) {
		return toDate(t1.getFechaDesde()).compareTo(toDate(
				t2.getFechaDesde().getAnho(), t2.getFechaDesde().getMes(),
				t2.getFechaDesde().getDia()));
	}

	private static void different(
			net.aonsolutions.core.tgss.creta.jaxb.bases.Dato datoAon,
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
			net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramoAon,
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
		// List<net.aonsolutions.core.tgss.creta.jaxb.DatoSolicitado> datosCreta =
		// tramoCreta
		// .getDatosTramo().getDato();
		Collections.sort(datosCreta, Bases::compare);
		List<net.aonsolutions.core.tgss.creta.jaxb.bases.Dato> datosAon = new ArrayList<net.aonsolutions.core.tgss.creta.jaxb.bases.Dato>(
				tramoAon.getDatosTramo().getDato());
		Collections.sort(datosAon, Bases::compare);

		int j = 0;
		for (int i = 0; i < datosCreta.size(); i++) {
			DatoSolicitado datoCreta = datosCreta.get(i);
			// net.aonsolutions.core.tgss.creta.jaxb.DatoSolicitado datoCreta =
			// datosCreta
			// .get(i);

			if (j >= datosAon.size())
				throw new Different();

			net.aonsolutions.core.tgss.creta.jaxb.bases.Dato datoAon = datosAon
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
			net.aonsolutions.core.tgss.creta.jaxb.bases.Trabajador trabajadorAon,
			Trabajador trabajadorCreta) {

		List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> tramosAon = new ArrayList<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo>(
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

	private static String toString(
			net.aonsolutions.core.tgss.creta.jaxb.bases.CtaCot ctaCot) {
		return String.format("%s%s%s", ctaCot.getProvincia(),
				ctaCot.getRegimen(), ctaCot.getNumero());

	}

	private static int compare(Liquidacion<?,?,?,?,?> l1,
			net.aonsolutions.core.tgss.creta.jaxb.bases.Liquidacion l2) {
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
				.withArgName("<code>=<valor>")
				.withLongOpt("default")
				.withDescription("Set a default data valor")
				.create('d');

		Option naf = OptionBuilder
				.hasArg()
				.withArgName("naf")
				.withLongOpt("naf")
				.withDescription("Only send this NAF")
				.create();

		Option reftificationMark = OptionBuilder
				.withLongOpt("reftification-mark")
				.withDescription("Adds reftification mark '<IndicadorRectificacion>S</IndicadorRectificacion>'")
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
				.addOption(naf)
				.addOption(reftificationMark)
				;
				
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
					System.out,
					new CustomizeBasesCallback()
					.setReftificationMark(cmd.hasOption(reftificationMark.getLongOpt()))
					);
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
			boolean skipExisting, 
			boolean acceptPrevBases,
			String nafs[],
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
				"500", "501", "502", "509",
				"601", "603",  "611"
				);
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

		Map<String, net.aonsolutions.core.tgss.creta.jaxb.bases.Liquidacion> liquidaciones = new HashMap<String, net.aonsolutions.core.tgss.creta.jaxb.bases.Liquidacion>();

		for (InputStream trabajadoresTramosIs : trabajadoresTramosIss) {

			try {
				net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresTramos = Utils
						.unmarshal(
								net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos.class,
								trabajadoresTramosIs);
				String ccc = Utils.toString(
						trabajadoresTramos.getLiquidacion().getCcc());
				net.aonsolutions.core.tgss.creta.jaxb.bases.Liquidacion liquidacion = liquidaciones
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

				boolean noDatos = liquidacion.getDatosLiquidacion() == null ||  
						liquidacion.getDatosLiquidacion().getDato().isEmpty();

				if (noTrabajadores && noDatos)
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
				net.aonsolutions.core.tgss.creta.jaxb.respuesta.Respuesta respuesta = Utils
						.unmarshal(
								net.aonsolutions.core.tgss.creta.jaxb.respuesta.Respuesta.class,
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
		net.aonsolutions.core.tgss.creta.jaxb.bases.Bases bases = builder.create();

		for (BasesCallback cb : callbacks)
			cb.bases(bases);

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

	
	private static void checkTrabajador(net.aonsolutions.core.tgss.creta.jaxb.bases.Trabajador trabajador) {
		Tramos tramos = trabajador.getTramos();
		if ( tramos == null )
			throw new InvalidTrabajador();
		List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> tramo = tramos.getTramo();
		if ( tramo == null || tramo.isEmpty() )
			throw new InvalidTrabajador();
		
	}
	
	

}
