package com.esferalia.aon.payroll.tgss.creta;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.NON_STRUCTURAL_OVERTIME_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.STRUCTURAL_OVERTIME_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_HOURS;

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
import javax.xml.bind.Marshaller.Listener;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import net.aonsolutions.tgss.creta.jaxb.Utils;
import net.aonsolutions.tgss.creta.jaxb.bases.BasesBuilder;
import net.aonsolutions.tgss.creta.jaxb.bases.Dato;
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
import com.esferalia.aon.payroll.Pair;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.AonUtils;

public class Bases {

	@SuppressWarnings("static-access")
	public static void main(String[] args) throws JAXBException, SQLException,
			ClassNotFoundException, IOException, XMLStreamException,
			FactoryConfigurationError, TransformerConfigurationException,
			TransformerFactoryConfigurationError {

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
									.create("x");
		Option comments =  OptionBuilder.withLongOpt("comments")
										.withDescription("Write additional information.")
										.create("c");
		
		Option skiptPrevBases =  OptionBuilder.withLongOpt("skip-prev-bases")
				.withDescription("Skip previous bases.")
				.create("b");

		Option pretty =  OptionBuilder.withLongOpt("pretty")
				  .withDescription("Makes the output readable to a human.")
				  .create();

		Options options = new Options()
		.addOption(hostName)
		.addOption(user)
		.addOption(password)
		.addOption(database)
		.addOption(xml)
		.addOption(comments)
		.addOption(skiptPrevBases)
		.addOption(pretty)
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
			
			boolean aceptarBasesAnteriores = !cmd.hasOption(skiptPrevBases.getLongOpt());
			
			TrabajadoresTramos trabajadoresTramos = Utils.unmarshal(
					TrabajadoresTramos.class, is);

			AONContext ctx = new AONContext(connection);
			BasesBuilder builder = new BasesBuilder()
					.setAutorizado(trabajadoresTramos.getAutorizado());

			XMLStreamWriter xsw = new IndentXMLStreamWriter(XMLOutputFactory
					.newInstance().createXMLStreamWriter(System.out), "  ");
			
			Errors errors = new Errors();
			Comments comment = new Comments(xsw);

			bases(builder, ctx, trabajadoresTramos.getLiquidacion(), aceptarBasesAnteriores, errors, comment);
			is.close();

			
			net.aonsolutions.tgss.creta.jaxb.bases.Bases bases = builder
					.create();
			
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

	public static interface BasesCallback {

		default void trabajadorAdded(Trabajador trabajador, Salary salary) {
		};

		default void unknownDato(DatoSolicitado datoSolicitado) {
		};

	}
	
	private static class Errors implements BasesCallback {
		@Override
		public void unknownDato(DatoSolicitado datoSolicitado) {
			System.err.println(String.format("ERROR: Unknown Dato '%s'", datoSolicitado.getCodigo()));
		}
	}
	

	private static class Comments extends Listener implements BasesCallback {

		private static class TrabajadorData {

			private String ss;
			private String name;

			public TrabajadorData(String name, String ss) {
				super();
				this.ss = ss;
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
			trabajadorDataMap.put(
					trabajador.getNaf(),
					new TrabajadorData(salary.getEmployeeName(), salary
							.getEmployeeSSNumber()));
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
						.format("\r\n%s\r\nNúmero afiliación a la Seguridad Social:\r\n%s\r\n",
								data.name, data.ss));
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	// ------------------------------------------------------------------------

	private static void bases(BasesBuilder basesBuilder, AONContext ctx,
			Liquidacion liquidacion, boolean  aceptarBasesAnteriores, BasesCallback... cbs) {
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
		AON.getSalaryData(ctx, props->
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

				CretaData data = getCretaData(datoSolicitado.getCodigo());

				if (data == null) {
					for (BasesCallback cb : cbs)
						cb.unknownDato(datoSolicitado);
					continue;
				}

				data.add(salary, tramo, datoSolicitado, tramoBuilder);
			}

			trabajadorBuilder.addTramo(tramoBuilder.create());
		}

		for (BasesCallback cb : cbs)
			cb.trabajadorAdded(trabajador, salary);

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

	private static long days(Period p) {
		return AonDateUtils.getDaysBetweenDates(p.getStart(), p.getEnd()) + 1;
	}

	private static interface CretaData {

		String getComment();

		void add(Salary salary, Tramo tramo, DatoSolicitado datoSolicitado,
				TramoBuilder tramoBuilder);

	}

	private static class CContextCretaData implements CretaData {

		private ContextVariable contextVariable;

		public CContextCretaData(ContextVariable contextVariable) {
			this.contextVariable = contextVariable;
		}

		@Override
		public String getComment() {
			return contextVariable.name();
		}

		@Override
		public void add(Salary salary, Tramo tramo,
				DatoSolicitado datoSolicitado, TramoBuilder tramoBuilder) {
			//B -> El código del dato solicitado es obligatorio.
			//P -> El código del dato solicitado es opcional.
			
			double newValue = get(salary, tramo.getFechaDesde(),
					tramo.getFechaHasta());
			boolean optional = "P".equals(datoSolicitado.getIndicadorObligatoriedad());
			if ( optional && newValue == 0.00 ) 
				return;

//			String oldValor = datoSolicitado.getValor();
//			Double oldValue = Double.parseDouble(oldValor);

			
			DatoBuilder datoBuilder = new DatoBuilder();
			datoBuilder.setCodigo(datoSolicitado.getCodigo());
			datoBuilder.setTipo(datoSolicitado.getTipoDato());
			datoBuilder.setImporteEuros(newValue);
			tramoBuilder.addDato(datoBuilder.create());
		}

		public Double get(Salary salary, Fecha desde, Fecha hasta) {
			return get(salary, new Period(toDate(desde),
					toDate(hasta)));
		}

		protected Double get(Salary salary, Period p) {
			List<ContextData> datas = salary.getContextData()
					.get(contextVariable.getName());
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

	}
	
	private static class HContextCretaData extends CContextCretaData {

		public HContextCretaData(ContextVariable contextVariable) {
			super(contextVariable);
		}
		
		@Override
		public void add(Salary salary, Tramo tramo,
				DatoSolicitado datoSolicitado, TramoBuilder tramoBuilder) {
			//B -> El código del dato solicitado es obligatorio.
			//P -> El código del dato solicitado es opcional.
			
			double newValue = get(salary, tramo.getFechaDesde(),
					tramo.getFechaHasta());
			boolean optional = "P".equals(datoSolicitado.getIndicadorObligatoriedad());
			if ( optional && newValue == 0.00 ) 
				return;

//			String oldValor = datoSolicitado.getValor();
//			Double oldValue = Double.parseDouble(oldValor);

			DatoBuilder datoBuilder = new DatoBuilder();
			datoBuilder.setCodigo(datoSolicitado.getCodigo());
			datoBuilder.setTipo(datoSolicitado.getTipoDato());
			datoBuilder.setHoras((int)Math.round(newValue));
			tramoBuilder.addDato(datoBuilder.create());
		}
	}

	private static Map<String, CretaData> CONTEXT_VARIABLE_MAP = new HashMap<String, CretaData>() {
		{
			put("500", new CContextCretaData(CGC_BASE));

			put("501", new CContextCretaData(STRUCTURAL_OVERTIME_BASE));
			put("502", new CContextCretaData(NON_STRUCTURAL_OVERTIME_BASE));
			put("537", new CContextCretaData(NON_STRUCTURAL_OVERTIME_BASE));
			put("601", new CContextCretaData(CGP_BASE));
			put("611", new CContextCretaData(CGP_BASE));

			put("01", new HContextCretaData(WORKED_HOURS));
//			put("02", new ContextCretaData(WORKED_HOURS));
		}
	};

	private static CretaData getCretaData(String codigo) {
		return CONTEXT_VARIABLE_MAP.get(codigo);
	}

}
