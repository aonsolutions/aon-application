package com.esferalia.aon.payroll.tgss.creta;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.CCC_TYPE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.FULL_TIME;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MATERNITY_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.NO_HOLIDAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_GROUP;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TC2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringBufferInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Month;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.Salary.ContextData;
import com.esferalia.aon.payroll.calculator.ExcelFunctions;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.watson.server.io.ByteArrayOutputStream;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.core.tgss.creta.jaxb.Utils;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.DatoSolicitado;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.LiquidacionMes;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Trabajador;
import net.aonsolutions.core.tgss.jaxb.trabajadorestramos.DatoSolicitadoBuilder;
import net.aonsolutions.core.tgss.jaxb.trabajadorestramos.LiquidacionMesBuilder;
import net.aonsolutions.core.tgss.jaxb.trabajadorestramos.TrabajadorBuilder;
import net.aonsolutions.core.tgss.jaxb.trabajadorestramos.TrabajadoresTramosBuilder;
import net.aonsolutions.core.tgss.jaxb.trabajadorestramos.TramoBuilder;

public class TrabajadoresTramos {
	
	
	

	public TrabajadoresTramos() {
	}

	public static void main(String[] args) throws JAXBException,
			DatatypeConfigurationException, ClassNotFoundException, SQLException, IOException {
		String tipo = "L00";
		String desdeAnho = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
		String desdeMes = Integer.toString(Calendar.getInstance().get(Calendar.MONTH) + 1);
		String hastaAnho = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
		String hastaMes = Integer.toString(Calendar.getInstance().get(Calendar.MONTH) + 1);
		String ctrlAnho = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
		String ctrlMes = Integer.toString(Calendar.getInstance().get(Calendar.MONTH) + 1);

		Option hostName = Bases.getHostNameOption();
		Option user = Bases.getDbUserOption();
		Option password = Bases.getDbPasswordOption();
		Option database = Bases.getDatabaseOption();

		//@formatter:off
		Option fromYear =  SolicitudBorrador.getFromYearOption(desdeAnho);
		Option fromMonth =  SolicitudBorrador.getFromMonthOption(desdeMes);
		Option toYear =  SolicitudBorrador.getToYearOption(hastaAnho);
		Option toMonth =  SolicitudBorrador.getToMonthOption(hastaMes);
		Option ctrlYear =  SolicitudBorrador.getCtrlYearOption(ctrlAnho);
		Option ctrlMonth =  SolicitudBorrador.getCtrlMonthOption(ctrlMes);
		Option ccc =  SolicitudBorrador.getCCCOption();
		Option authorized =  SolicitudBorrador.getAuthorizedOption();
		Option type =  SolicitudBorrador.getTypeOption(tipo);
		
		Options options = new Options()
		.addOption(hostName)
		.addOption(user)
		.addOption(password)
		.addOption(database)
		.addOption(authorized)
		.addOption(fromYear)
		.addOption(fromMonth)
		.addOption(toYear)
		.addOption(toMonth)
		.addOption(ctrlYear)
		.addOption(ctrlMonth)
		.addOption(ccc)
		.addOption(type)
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

			desdeMes = cmd.getOptionValue(fromMonth.getLongOpt(), desdeMes);
			desdeAnho = cmd.getOptionValue(fromYear.getLongOpt(), desdeAnho);
			hastaMes = cmd.getOptionValue(toMonth.getLongOpt(), hastaMes);
			hastaAnho = cmd.getOptionValue(toYear.getLongOpt(), hastaAnho);
			tipo = cmd.getOptionValue(type.getLongOpt(), tipo);
			String cccs[] = cmd.getOptionValues(ccc.getLongOpt());
			String autorizado = cmd.getOptionValue(authorized.getLongOpt());
			
			ByteArrayOutputStream trabajadoresYTramosOs = new ByteArrayOutputStream();
			generate(connection, autorizado, desdeMes, desdeAnho, hastaMes, hastaAnho, ctrlMes, ctrlAnho, tipo, cccs, trabajadoresYTramosOs);
			trabajadoresYTramosOs.close();
			
			InputStream trabajadoresTramosIs= new StringBufferInputStream(String.format("%s", trabajadoresYTramosOs.toString(), "UTF-8"));
			
			net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresTramos = Utils
					.unmarshal(
							net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos.class,
							trabajadoresTramosIs);
			
			Utils.marshal(trabajadoresTramos, System.out);
			
			trabajadoresTramosIs.close();

		} catch (ParseException e) {
			// oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + e.getMessage());
			// automatically generate the help statement
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("calc", options);

		}

	}

	public static void generate(Connection conn, String autorizado, String desdeMes, String desdeAnho,
			String hastaMes, String hastaAnho, String ctrlMes, String ctrlAnho,  String tipo, String cccs[], OutputStream os) throws JAXBException {

		int authorized = Integer.parseInt(autorizado);
		Month fromMonth = Month.of(Integer.parseInt(desdeMes));
		int fromYear = Integer.parseInt(desdeAnho);
		Month toMonth = Month.of(Integer.parseInt(hastaMes));
		int toYear = Integer.parseInt(hastaAnho);
		Month ctrlMonth = Month.of(Integer.parseInt(ctrlMes));
		int ctrlYear = Integer.parseInt(ctrlAnho);

		generate(conn, authorized, fromMonth, fromYear, toMonth, toYear, ctrlMonth, ctrlYear, tipo, cccs, os);
	}

	public static void generate(Connection conn, int autorizado, Month desdeMes, int desdeAnho, Month hastaMes, int hastaAnho, 
			Month ctrlMes, int ctrlAnho,
			String tipo, String cccs[], OutputStream os) throws JAXBException {

		TrabajadoresTramosBuilder trabajadoresTramosBuilder = new TrabajadoresTramosBuilder()
				.setAutorizado(autorizado);

		for (String cCC : cccs) {
			trabajadoresTramosBuilder.setCCC(cCC)
			.setTipo(tipo)
			.setMesDesde(desdeMes)
			.setAnhoDesde(desdeAnho)
			.setMesHasta(hastaMes)
			.setAnhoHasta(hastaAnho)
			.setMesControl(ctrlMes)
			.setAnhoControl(ctrlAnho)
			.addLiquidacion();
		}
		
		datosSolicitados(trabajadoresTramosBuilder);

		AONContext aonContext = new AONContext(conn);
		
		Date fromDate = getFirstDayOf(desdeMes, desdeAnho);
		Date toDate = getLastDayOf(hastaMes, hastaAnho);
		for (Date date = fromDate; date.before(toDate); date = AonDateUtils.add(date,Calendar.MONTH,1)) {
			int anho = AonDateUtils.get(date, Calendar.YEAR);
			Month  mes = Month.values()[AonDateUtils.get(date, Calendar.MONTH)];
			for ( String ccc: cccs ) {
				String ccc_prov_num_dc = ccc.substring(4); // PROVINCIA (2) + Nº (7) + DÍGITOS CONTROL (2)
				liquidacionMes(aonContext, trabajadoresTramosBuilder, anho, mes, ccc_prov_num_dc, tipo);
			}
		}		
		
		net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresTramos = trabajadoresTramosBuilder
				.create();

		Utils.marshal(trabajadoresTramos, os);
	}
	
	// ------------------------------------------------------------------------
	
	private static void datosSolicitados(TrabajadoresTramosBuilder trabajadoresTramosBuilder) {
		DatoSolicitadoBuilder datoSolicitadoBuilder = new DatoSolicitadoBuilder();
		datoSolicitadoBuilder.setTipo("C");
		datoSolicitadoBuilder.setCodigo("763");
		datoSolicitadoBuilder.setObligatorio(false);
		trabajadoresTramosBuilder.addDatoSolicitado(datoSolicitadoBuilder.create());
	}
	
	private static void liquidacionMes(AONContext aonContext, 
			TrabajadoresTramosBuilder trabajadoresTramosBuilder,
			int anho, 
			Month mes,
			String ccc,
			String tipo) {
		
		
		Set<Date> mesesLiquidativos = new HashSet<Date>();
		
		LiquidacionMesBuilder liquidacionMesBuilder = new LiquidacionMesBuilder();
		
		Date startDate = getFirstDayOf(mes, anho);
		Date endDate = AonStringUtils.equalsIgnoreCase("L13", tipo) ? 
				getLastDayOf(mes, anho + 2) : getLastDayOf(mes, anho);
		

		AON.getSalaryData(aonContext, 
				props -> 
				props.getCCCProperty().eq(ccc)
				.and(props.getEndDateProperty().ge(startDate))
				.and(props.getStartDateProperty().le(endDate))
				.and(props.getIsDelayProperty().eq(AonStringUtils.equalsIgnoreCase("L03", tipo)))
				.and(props.getIsSettlementProperty().eq(AonStringUtils.equalsIgnoreCase("L13", tipo)))
				.and(props.getIsSalaryProperty().eq(AonStringUtils.containsIgnoreCase("L00,L91,L90", tipo)))
				)
		.forEach(
				salary -> {
					TrabajadorBuilder trabajadorBuilder  = new TrabajadorBuilder();
					trabajadorBuilder.setNaf(salary.getEmployeeSSNumber());
					//trabajadorBuilder.setFullName(salary.getEmployeeName());
					trabajadorBuilder.setTipoIpf(TrabajadorBuilder.TipoIpf.DNI); // TODO: Hardwired... To Self-Destructed 
					trabajadorBuilder.setNumeroIpf(salary.getEmployeeDocument());
					
					List<Period> cgcBasePeriods = new LinkedList<Period>();
					
					ContextVariable contextVariable = visit(tipo, new TypeVisitor<ContextVariable>() {

						@Override
						public ContextVariable visitL03() {
							return CGC_BASE;
						}

						@Override
						public ContextVariable visitL00() {
							return CGC_BASE;
						}

						@Override
						public ContextVariable visitL02() {
							return CGC_BASE;
						}

						@Override
						public ContextVariable visitL13() {
							return NO_HOLIDAYS;
						}

						@Override
						public ContextVariable visitL91() {
							return CGC_BASE;
						}

						@Override
						public ContextVariable visitL90() {
							return CGC_BASE;
						}
						
					});

					for ( ContextData cgcData: salary.getContextData().getOrDefault(contextVariable.getName(), Collections.emptyList()) ) {
						cgcBasePeriods.add( new Period(cgcData.getStartDate(), cgcData.getEndDate()));
					}

					Collections.sort(cgcBasePeriods); // sort & sort & sort again .
					
					
					for ( ContextData cgcData: salary.getContextData().getOrDefault(MATERNITY_BASE.getName(), Collections.emptyList()) )
						insert(cgcBasePeriods, new Period(cgcData.getStartDate(), cgcData.getEndDate()));
					

					for ( ContextVariable var : ContextVariable.ERE_BASES )
						for ( ContextData cgcData: salary.getContextData().getOrDefault(var.getName(), Collections.emptyList()) )
							insert(cgcBasePeriods, new Period(cgcData.getStartDate(), cgcData.getEndDate()));
					
					
					List<Period> periods = merge(salary, cgcBasePeriods);//cgcBasePeriods;
					for ( Period p: periods ) {
						
						if ( p.getStart().after(endDate) )
							continue;
						if ( p.getEnd().before(startDate) )
							continue;
						
						TramoBuilder tramoBuilder  = new TramoBuilder();

						Calendar start = Calendar.getInstance();
						start.setTime(p.getStart());
						tramoBuilder.setDiaDesde(start.get(Calendar.DATE));
						tramoBuilder.setMesDesde(start.get(Calendar.MONTH)+1);
						tramoBuilder.setAnhoDesde(start.get(Calendar.YEAR));
						
						start.set(Calendar.DAY_OF_MONTH, 1);
						mesesLiquidativos.add(start.getTime());

						Calendar end = Calendar.getInstance();
						end.setTime(p.getEnd());
						
						tramoBuilder.setDiaHasta(end.get(Calendar.DATE));
						tramoBuilder.setMesHasta(end.get(Calendar.MONTH)+1);
						tramoBuilder.setAnhoHasta(end.get(Calendar.YEAR));

						end.set(Calendar.DAY_OF_MONTH, 1);
						mesesLiquidativos.add(end.getTime());

						int diasCotizados = getQuoteDays(salary, p);
						
						class SalaryFilter implements SalaryVisitor {
							
							boolean grupoCotizacionDiario = false;
							boolean incapacidadTemporalPagoDelegado = false;
							

							public boolean isGrupoCotizacionDiario() {
								return grupoCotizacionDiario;
							}

							public boolean isIncapacidadTemporalPagoDelegado() {
								return incapacidadTemporalPagoDelegado;
							}
							

							@Override
							public void visitFormacionNormal() {
							}

							@Override
							public void visitTiempoParcialNormal() {
							}

							@Override
							public void visitTiempoCompletoNormal() {
							}
							
							@Override
							public void visitRegimenArtistasNormal() {
							}
							

							@Override
							public void visitGrupoCotizacionDiario() {
								grupoCotizacionDiario = true;
							}

							@Override
							public void visitGrupoCotizacionMensual() {
							}

							@Override
							public void visitIncapacidadTemporal15PrimerosDias() {
							}

							@Override
							public void visitIncapacidadTemporalPagoDelegado() {
								incapacidadTemporalPagoDelegado = true;
							}

							@Override
							public void visitIncapacidadTemporalPagoDirecto() {
							}

							@Override
							public void visitIncapacidadTemporalATEPPagoDelegado() {
							}

							@Override
							public void visitMaternidadPaternidadTiempoCompleto() {
							}

							@Override
							public void visitMaternidadPaternidadTiempoParcial() {
							}

							@Override
							public void visitExpedienteRegulacionEmpleoTotal() {
							}

							@Override
							public void visitExpedienteRegulacionEmpleoParcial() {
							}

							@Override
							public void visitIncapacidadTemporalPagoDelegadoFormacion() {
								incapacidadTemporalPagoDelegado = true;
							}

							@Override
							public void visitExpedienteRegulacionEmpleoParcialFormacion() {
							}

							@Override
							public void visitMaternidadPaternidadTiempoParcialFormacion() {
							}

							@Override
							public void visitIncapacidadTemporalATEPPagoDelegadoFormacion() {
							}
						};
						
						SalaryFilter filter = new SalaryFilter();
						visit(salary, p.getStart(), p.getEnd(), filter );

						// No deben de enviarse tramos con cero "días cotizados"
						// con las siguientes excepciones: 
						// - Grupos de cotización diario con indicador mensual. 
						// - Situaciones de IT de pago delegado .
						//
						if ( diasCotizados == 0 
								&& !filter.isGrupoCotizacionDiario() 
								&& !filter.isIncapacidadTemporalPagoDelegado()  )
							continue;
						
						tramoBuilder.setDiasCotizados( diasCotizados );
						
						DatoSolicitadoBuilder dataSolicitadoBuilder  = new DatoSolicitadoBuilder();
						
						
						class DefaultSalaryVisitor implements SalaryVisitor {
							
							@Override
							public void visitFormacionNormal() {
								// 3.1 Contratos para la formación (TRL 087)  
								// 3.1.1 Tramo en situación de activo "normal"  

								// N horas formación teórica presencial 
								dataSolicitadoBuilder.setTipo("H");
								dataSolicitadoBuilder.setCodigo("03");
								dataSolicitadoBuilder.setObligatorio(false);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
								// N horas formación teórica a distancia 
								dataSolicitadoBuilder.setTipo("H");
								dataSolicitadoBuilder.setCodigo("04");
								dataSolicitadoBuilder.setObligatorio(false);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
								// N horas tutoría 
								dataSolicitadoBuilder.setTipo("H");
								dataSolicitadoBuilder.setCodigo("06");
								dataSolicitadoBuilder.setObligatorio(false);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
								// Bonificación tutoría
								dataSolicitadoBuilder.setTipo("C");
								dataSolicitadoBuilder.setCodigo("737");
								dataSolicitadoBuilder.setObligatorio(false);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
								// Base de Horas Extras Fuerza Mayor
								dataSolicitadoBuilder.setTipo("C");
								dataSolicitadoBuilder.setCodigo("501");
								dataSolicitadoBuilder.setObligatorio(false);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
								// Base Otras Horas Extras
								dataSolicitadoBuilder.setTipo("C");
								dataSolicitadoBuilder.setCodigo("502");
								dataSolicitadoBuilder.setObligatorio(false);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
							}

							@Override
							public void visitTiempoCompletoNormal() {
								// 2.1 Situación de activo "normal" 
								// 2.1.1 Trabajador a Tiempo Completo  

								// Base de contingencias comunes
								dataSolicitadoBuilder.setTipo("C");
								dataSolicitadoBuilder.setCodigo("500");
								dataSolicitadoBuilder.setObligatorio(true);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
								// Base de Horas Extras Fuerza Mayor
								dataSolicitadoBuilder.setTipo("C");
								dataSolicitadoBuilder.setCodigo("501");
								dataSolicitadoBuilder.setObligatorio(false);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
								// Base Otras Horas Extras
								dataSolicitadoBuilder.setTipo("C");
								dataSolicitadoBuilder.setCodigo("502");
								dataSolicitadoBuilder.setObligatorio(false);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
								// Base de Accidentes de Trabajo
								dataSolicitadoBuilder.setTipo("C");
								dataSolicitadoBuilder.setCodigo("601");
								dataSolicitadoBuilder.setObligatorio(true);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
																
							}
							
							@Override
							public void visitRegimenArtistasNormal() {
								// 5 Régimen Especial de Artistas 
								// 5.1 Trabajador en situación de activo "normal"  

								// Percepciones íntegras
								dataSolicitadoBuilder.setTipo("C");
								dataSolicitadoBuilder.setCodigo("300");
								dataSolicitadoBuilder.setObligatorio(true);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
								// Base de Horas Extras Fuerza Mayor
								dataSolicitadoBuilder.setTipo("C");
								dataSolicitadoBuilder.setCodigo("501");
								dataSolicitadoBuilder.setObligatorio(false);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
								// Base Otras Horas Extras
								dataSolicitadoBuilder.setTipo("C");
								dataSolicitadoBuilder.setCodigo("502");
								dataSolicitadoBuilder.setObligatorio(false);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
							}
							
							@Override
							public void visitTiempoParcialNormal() {
								// 2.1 Situación de activo "normal" 
								visitTiempoCompletoNormal();
								// 2.1.1 Trabajador a Tiempo Parcial
								// N horas realizadas  a tiempo parcial 
								dataSolicitadoBuilder.setTipo("H");
								dataSolicitadoBuilder.setCodigo("01");
								dataSolicitadoBuilder.setObligatorio(true);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
								// N horas complementarias 
								dataSolicitadoBuilder.setTipo("H");
								dataSolicitadoBuilder.setCodigo("02");
								dataSolicitadoBuilder.setObligatorio(false);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
								// Base de horas complementarias
								dataSolicitadoBuilder.setTipo("C");
								dataSolicitadoBuilder.setCodigo("537");
								dataSolicitadoBuilder.setObligatorio(false);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
							}
							
							@Override
							public void visitGrupoCotizacionDiario() {
								// Modalidad de Salario (Para grupos de cotización diario con retribución mensual.)
								dataSolicitadoBuilder.setTipo("I");
								dataSolicitadoBuilder.setCodigo("51");
								dataSolicitadoBuilder.setObligatorio(false);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
							}
							
							@Override
							public void visitGrupoCotizacionMensual() {
							}

							@Override
							public void visitIncapacidadTemporal15PrimerosDias() {
								// 2.2 Situaciones de Incapacidad Temporal  
								// 2.2.1 Incapacidad Temporal 15 primeros días 
								// Base de contingencias comunes en situación de IT
								dataSolicitadoBuilder.setTipo("C");
								dataSolicitadoBuilder.setCodigo("500");
								dataSolicitadoBuilder.setObligatorio(true);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
								// Base de Accidentes de Trabajo en situación de IT
								dataSolicitadoBuilder.setTipo("C");
								dataSolicitadoBuilder.setCodigo("603");
								dataSolicitadoBuilder.setObligatorio(true);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
							}
							
							@Override
							public void visitIncapacidadTemporalPagoDelegado() {
								// 2.2 Situaciones de Incapacidad Temporal  
								// 2.2.2 Incapacidad Temporal pago delegado 
								// Base de contingencias comunes en situación de IT
								dataSolicitadoBuilder.setTipo("C");
								dataSolicitadoBuilder.setCodigo("500");
								dataSolicitadoBuilder.setObligatorio(true);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
								// Compensación IT Contingencias Comunes
								dataSolicitadoBuilder.setTipo("C");
								dataSolicitadoBuilder.setCodigo("563");
								dataSolicitadoBuilder.setObligatorio(true);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
								// Base de Accidentes de Trabajo en situación de IT
								dataSolicitadoBuilder.setTipo("C");
								dataSolicitadoBuilder.setCodigo("603");
								dataSolicitadoBuilder.setObligatorio(true);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
							}
							
							@Override
							public void visitIncapacidadTemporalPagoDirecto() {
								// 2.2 Situaciones de Incapacidad Temporal  
								// 2.2.2 Incapacidad Temporal pago directo 
								// Base de contingencias comunes en situación de IT
								dataSolicitadoBuilder.setTipo("C");
								dataSolicitadoBuilder.setCodigo("509");
								dataSolicitadoBuilder.setObligatorio(true);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
								// Base de Accidentes de Trabajo en situación de IT
								dataSolicitadoBuilder.setTipo("C");
								dataSolicitadoBuilder.setCodigo("603");
								dataSolicitadoBuilder.setObligatorio(true);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
							}
							@Override
							public void visitIncapacidadTemporalATEPPagoDelegado() {
								// 2.2 Situaciones de Incapacidad Temporal  
								// 2.2.3 Incapacidad Temporal de AT Y EP pago delegado 
								// Base de contingencias comunes en situación de IT
								dataSolicitadoBuilder.setTipo("C");
								dataSolicitadoBuilder.setCodigo("500");
								dataSolicitadoBuilder.setObligatorio(true);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
								// Compensación IT AT y EP
								dataSolicitadoBuilder.setTipo("C");
								dataSolicitadoBuilder.setCodigo("663");
								dataSolicitadoBuilder.setObligatorio(true);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
								// Base de Accidentes de Trabajo en situación de IT
								dataSolicitadoBuilder.setTipo("C");
								dataSolicitadoBuilder.setCodigo("603");
								dataSolicitadoBuilder.setObligatorio(true);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
							}

							@Override
							public void visitMaternidadPaternidadTiempoCompleto() {
								// 2.3 Situaciones de Maternidad/Paternidad a 
								//	   Tiempo Completo y Riesgos durante el
								//	   embarazo/lactancia  
								// 2.3.1 Maternidad/Paternidad Tiempo Completo 
								// Base de contingencias comunes en situación de 
								// Maternidad/Paternidad
								dataSolicitadoBuilder.setTipo("C");
								dataSolicitadoBuilder.setCodigo("509");
								dataSolicitadoBuilder.setObligatorio(true);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
								// Base de Accidentes de Trabajo en situación de 
								// Maternidad/Paternidad
								dataSolicitadoBuilder.setTipo("C");
								dataSolicitadoBuilder.setCodigo("603");
								dataSolicitadoBuilder.setObligatorio(true);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
							}
							
							@Override
							public void visitMaternidadPaternidadTiempoParcial() {
								// 2.7 Reducciones de Jornada por Descanso por 
								//	   Maternidad/Paternidad a Tiempo Parcial  
								// 2.7.1 Maternidad/Paternidad Tiempo Parcial 

								// N horas complementarias 
								dataSolicitadoBuilder.setTipo("H");
								dataSolicitadoBuilder.setCodigo("02");
								dataSolicitadoBuilder.setObligatorio(false);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
								// Base de horas complementarias
								dataSolicitadoBuilder.setTipo("C");
								dataSolicitadoBuilder.setCodigo("537");
								dataSolicitadoBuilder.setObligatorio(false);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
								// Base de contingencias comunes 
								dataSolicitadoBuilder.setTipo("C");
								dataSolicitadoBuilder.setCodigo("500");
								dataSolicitadoBuilder.setObligatorio(true);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
								// Base de Horas Extras Fuerza Mayor
								dataSolicitadoBuilder.setTipo("C");
								dataSolicitadoBuilder.setCodigo("501");
								dataSolicitadoBuilder.setObligatorio(false);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
								// Base de Accidentes de Trabajo
								dataSolicitadoBuilder.setTipo("C");
								dataSolicitadoBuilder.setCodigo("601");
								dataSolicitadoBuilder.setObligatorio(true);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
								// N horas realizadas  a tiempo parcial 
								dataSolicitadoBuilder.setTipo("H");
								dataSolicitadoBuilder.setCodigo("01");
								dataSolicitadoBuilder.setObligatorio(true);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
								// N horas complementarias 
								dataSolicitadoBuilder.setTipo("H");
								dataSolicitadoBuilder.setCodigo("02");
								dataSolicitadoBuilder.setObligatorio(false);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
								// Base de horas complementarias
								dataSolicitadoBuilder.setTipo("C");
								dataSolicitadoBuilder.setCodigo("537");
								dataSolicitadoBuilder.setObligatorio(false);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
								
								// Base de contingencias comunes en situación de Maternidad
								dataSolicitadoBuilder.setTipo("C");
								dataSolicitadoBuilder.setCodigo("535");
								dataSolicitadoBuilder.setObligatorio(true);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
								// Base de Accidentes de Trabajo en situación de Maternidad
								dataSolicitadoBuilder.setTipo("C");
								dataSolicitadoBuilder.setCodigo("635");
								dataSolicitadoBuilder.setObligatorio(true);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
								
							}

							@Override
							public void visitExpedienteRegulacionEmpleoTotal() {
								visitMaternidadPaternidadTiempoCompleto();
							}

							@Override
							public void visitExpedienteRegulacionEmpleoParcial() {

								visitTiempoParcialNormal();
								
								// Base de contingencias comunes en situación de Expediente de Regulación de Empleo
								dataSolicitadoBuilder.setTipo("C");
								dataSolicitadoBuilder.setCodigo("536");
								dataSolicitadoBuilder.setObligatorio(true);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
								// Base de Accidentes de Trabajo en situación de Expediente de Regulación de Empleo
								dataSolicitadoBuilder.setTipo("C");
								dataSolicitadoBuilder.setCodigo("636");
								dataSolicitadoBuilder.setObligatorio(true);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
								// Coeficiente a tiempo parcial en situación de Expediente de Regulación de Empleo 
								dataSolicitadoBuilder.setTipo("H");
								dataSolicitadoBuilder.setCodigo("05");
								dataSolicitadoBuilder.setObligatorio(true);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
							}

							@Override
							public void visitIncapacidadTemporalPagoDelegadoFormacion() {
								// 3.2.1 Tramos en situación de IT pago delegado contingencias comunes 
								// Compensación IT contingencias comunes 
								dataSolicitadoBuilder.setTipo("C");
								dataSolicitadoBuilder.setCodigo("563");
								dataSolicitadoBuilder.setObligatorio(true);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
								
							}

							@Override
							public void visitExpedienteRegulacionEmpleoParcialFormacion() {
								//3.1.5 Tramos en situación de ERE Parcial
								visitMaternidadPaternidadTiempoParcialFormacion();
								// Coeficiente a tiempo parcial en situación de Expediente de Regulación de Empleo 
								dataSolicitadoBuilder.setTipo("H");
								dataSolicitadoBuilder.setCodigo("05");
								dataSolicitadoBuilder.setObligatorio(true);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
							}

							@Override
							public void visitMaternidadPaternidadTiempoParcialFormacion() {
								// 3.1.4 Tramo en situación de Maternidad/Paternidad a Tiempo Parcial 
								// N horas formación teórica presencial 
								dataSolicitadoBuilder.setTipo("H");
								dataSolicitadoBuilder.setCodigo("03");
								dataSolicitadoBuilder.setObligatorio(false);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
								// N horas formación teórica a distancia 
								dataSolicitadoBuilder.setTipo("H");
								dataSolicitadoBuilder.setCodigo("04");
								dataSolicitadoBuilder.setObligatorio(false);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
								// N horas tutoría 
								dataSolicitadoBuilder.setTipo("H");
								dataSolicitadoBuilder.setCodigo("06");
								dataSolicitadoBuilder.setObligatorio(false);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
								// Bonificación tutoría
								dataSolicitadoBuilder.setTipo("C");
								dataSolicitadoBuilder.setCodigo("737");
								dataSolicitadoBuilder.setObligatorio(false);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
								// Base de Horas Extras Fuerza Mayor
								dataSolicitadoBuilder.setTipo("C");
								dataSolicitadoBuilder.setCodigo("501");
								dataSolicitadoBuilder.setObligatorio(false);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
							}

							@Override
							public void visitIncapacidadTemporalATEPPagoDelegadoFormacion() {
								//3.1.3 Tramo en situación de IT pago delegado AT y EP
								// Compensación por AT EP 
								dataSolicitadoBuilder.setTipo("C");
								dataSolicitadoBuilder.setCodigo("663");
								dataSolicitadoBuilder.setObligatorio(true);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());								
							}
							
							
							
						}
						
						
						
						SalaryVisitor salaryVisitor = 
						visit(tipo, new TypeVisitor<SalaryVisitor>() {

							@Override
							public SalaryVisitor visitL03() {
								// La causa que da lugar a la obligación de cotizar.
								dataSolicitadoBuilder.setTipo("I");
								dataSolicitadoBuilder.setCodigo("54");
								dataSolicitadoBuilder.setObligatorio(true);
								tramoBuilder.addDato(dataSolicitadoBuilder.create());
								return new DefaultSalaryVisitor();
							}

							@Override
							public SalaryVisitor visitL00() {
								return new DefaultSalaryVisitor();
							}

							@Override
							public SalaryVisitor visitL02() {
								return new DefaultSalaryVisitor();
							}

							@Override
							public SalaryVisitor visitL13() {
								return new DefaultSalaryVisitor() {
									public void visitTiempoParcialNormal(){
										visitTiempoCompletoNormal();
									}
								};
							}

							@Override
							public SalaryVisitor visitL91() {
								return new DefaultSalaryVisitor();
							}

							@Override
							public SalaryVisitor visitL90() {
								return new DefaultSalaryVisitor();
							}
							
						});
						
						
						
						visit(salary, p.getStart(), p.getEnd(), salaryVisitor );
						
						tramoBuilder.setTipoDeContrato(getContextData(TC2.getName(), salary, p.getStart(), p.getEnd(), "-"));
						try {
							tramoBuilder.setGrupoCotizacion(getContextData(QUOTE_GROUP.getName(), salary, p.getStart(), p.getEnd()));
						} catch (Exception e ) {
							//TODO: Log this please
						}
						
						trabajadorBuilder.addTramo(tramoBuilder.create());
						
					}
					
					
					
					Trabajador trabajador = trabajadorBuilder.create();
					liquidacionMesBuilder.add(trabajador);
				}
		)
		;
		
		if ( mesesLiquidativos.size() == 1 ) {
			Date mesLiquidativo = mesesLiquidativos.iterator().next();
			anho = AonDateUtils.get(mesLiquidativo, Calendar.YEAR);
			mes = Month.values()[AonDateUtils.get(mesLiquidativo, Calendar.MONTH)];
		}
		liquidacionMesBuilder.setMes(mes);
		liquidacionMesBuilder.setAnho(anho);
		
		LiquidacionMes liquidacionMes = liquidacionMesBuilder.create();
		trabajadoresTramosBuilder.addLiquidacionMes(liquidacionMes);
	}

	private static void insert(List<Period> periods, Period period) {
		int insertionPoint = Collections.binarySearch(periods, period);
		if ( insertionPoint < 0 ) 
			periods.add((-(insertionPoint) - 1), period);
	}
	
	private static List<Period> merge(Salary salary, List<Period> periods) {
		LinkedList<Period> cretaPeriods = new LinkedList<Period>();
		
		class Visitor implements  SalaryVisitor {
			
			SalaryVisitor standard = new SalaryVisitor(){
				
				@Override
				public void visitFormacionNormal() {
					cretaPeriods.add(new Period(period.getStart(), period.getEnd()));
				}
				
				@Override
				public void visitTiempoParcialNormal() {
					cretaPeriods.add(new Period(period.getStart(), period.getEnd()));
				}

				@Override
				public void visitTiempoCompletoNormal() {
					cretaPeriods.add(new Period(period.getStart(), period.getEnd()));
				}
				
				@Override
				public void visitRegimenArtistasNormal() {
					cretaPeriods.add(new Period(period.getStart(), period.getEnd()));
				}

				@Override
				public void visitGrupoCotizacionDiario() {
					//cretaPeriods.add(new Period(period.getStart(), period.getEnd()));
				}

				@Override
				public void visitGrupoCotizacionMensual() {
				}

				@Override
				public void visitIncapacidadTemporal15PrimerosDias() {
					cretaPeriods.add(new Period(period.getStart(), period.getEnd()));
					state = it15PrimerosDias;
				}

				@Override
				public void visitIncapacidadTemporalPagoDelegado() {
					cretaPeriods.add(new Period(period.getStart(), period.getEnd()));
					state = itDelegate;
				}

				public void visitIncapacidadTemporalPagoDirecto() {
					cretaPeriods.add(new Period(period.getStart(), period.getEnd()));
				}

				@Override
				public void visitIncapacidadTemporalATEPPagoDelegado() {
					cretaPeriods.add(new Period(period.getStart(), period.getEnd()));
				}
				
				@Override
				public void visitMaternidadPaternidadTiempoCompleto() {
					cretaPeriods.add(new Period(period.getStart(), period.getEnd()));
				}
				
				@Override
				public void visitMaternidadPaternidadTiempoParcial() {
					cretaPeriods.add(new Period(period.getStart(), period.getEnd()));
				}
				
				@Override
				public void visitExpedienteRegulacionEmpleoTotal() {
					cretaPeriods.add(new Period(period.getStart(), period.getEnd()));
				}
				
				@Override
				public void visitExpedienteRegulacionEmpleoParcial() {
					cretaPeriods.add(new Period(period.getStart(), period.getEnd()));
				}

				@Override
				public void visitIncapacidadTemporalPagoDelegadoFormacion() {
					cretaPeriods.add(new Period(period.getStart(), period.getEnd()));
				}

				@Override
				public void visitExpedienteRegulacionEmpleoParcialFormacion() {
					cretaPeriods.add(new Period(period.getStart(), period.getEnd()));
				}

				@Override
				public void visitMaternidadPaternidadTiempoParcialFormacion() {
					cretaPeriods.add(new Period(period.getStart(), period.getEnd()));
				}

				@Override
				public void visitIncapacidadTemporalATEPPagoDelegadoFormacion() {
					cretaPeriods.add(new Period(period.getStart(), period.getEnd()));
				}
			};

			SalaryVisitor it15PrimerosDias = new SalaryVisitor(){
				
				private void visitOthers(){
					cretaPeriods.add(new Period(period.getStart(), period.getEnd()));
					state = standard;
				}
				
				@Override
				public void visitFormacionNormal() {
					visitOthers();
				}
				
				@Override
				public void visitTiempoParcialNormal() {
					visitOthers();
				}

				@Override
				public void visitTiempoCompletoNormal() {
					visitOthers();
				}
				
				@Override
				public void visitRegimenArtistasNormal() {
					visitOthers();
				}

				@Override
				public void visitGrupoCotizacionDiario() {
					// noop
				}

				@Override
				public void visitGrupoCotizacionMensual() {
				}

				@Override
				public void visitIncapacidadTemporal15PrimerosDias() {
					Period last = cretaPeriods.removeLast();
					cretaPeriods.add(new Period(last.getStart(), period.getEnd()));
				}

				@Override
				public void visitIncapacidadTemporalPagoDelegado() {
					cretaPeriods.add(new Period(period.getStart(), period.getEnd()));
					state = itDelegate;
				}

				@Override
				public void visitIncapacidadTemporalPagoDirecto() {
					cretaPeriods.add(new Period(period.getStart(), period.getEnd()));
				}

				@Override
				public void visitIncapacidadTemporalATEPPagoDelegado() {
					visitOthers();
				}
				
				@Override
				public void visitMaternidadPaternidadTiempoCompleto() {
					visitOthers();
				}
				
				@Override
				public void visitMaternidadPaternidadTiempoParcial() {
					visitOthers();
				}
				
				@Override
				public void visitExpedienteRegulacionEmpleoTotal() {
					visitOthers();
				}
				
				@Override
				public void visitExpedienteRegulacionEmpleoParcial() {
					visitOthers();
				}

				@Override
				public void visitIncapacidadTemporalPagoDelegadoFormacion() {
					visitOthers();					
				}

				@Override
				public void visitExpedienteRegulacionEmpleoParcialFormacion() {
					visitOthers();					
				}

				@Override
				public void visitMaternidadPaternidadTiempoParcialFormacion() {
					visitOthers();					
				}

				@Override
				public void visitIncapacidadTemporalATEPPagoDelegadoFormacion() {
					visitOthers();					
				}
			};
			
			SalaryVisitor itDelegate = new SalaryVisitor(){
				
				private void visitOthers(){
					cretaPeriods.add(new Period(period.getStart(), period.getEnd()));
					state = standard;
				}
				
				@Override
				public void visitFormacionNormal() {
					visitOthers();
				}
				
				@Override
				public void visitTiempoParcialNormal() {
					visitOthers();
				}

				@Override
				public void visitTiempoCompletoNormal() {
					visitOthers();
				}
				
				@Override
				public void visitRegimenArtistasNormal() {
					visitOthers();
				}

				@Override
				public void visitGrupoCotizacionDiario() {
					// noop
				}

				@Override
				public void visitGrupoCotizacionMensual() {
				}

				@Override
				public void visitIncapacidadTemporal15PrimerosDias() {
					cretaPeriods.add(new Period(period.getStart(), period.getEnd()));
					state = it15PrimerosDias;
				}

				@Override
				public void visitIncapacidadTemporalPagoDelegado() {
					Period last = cretaPeriods.removeLast();
					cretaPeriods.add(new Period(last.getStart(), period.getEnd()));
				}


				@Override
				public void visitIncapacidadTemporalPagoDirecto() {
					cretaPeriods.add(new Period(period.getStart(), period.getEnd()));
				}

				@Override
				public void visitIncapacidadTemporalATEPPagoDelegado() {
					visitOthers();
				}
				
				@Override
				public void visitMaternidadPaternidadTiempoCompleto() {
					visitOthers();
				}
				
				@Override
				public void visitMaternidadPaternidadTiempoParcial() {
					visitOthers();
				}
				
				@Override
				public void visitExpedienteRegulacionEmpleoTotal() {
					visitOthers();
				}
				
				@Override
				public void visitExpedienteRegulacionEmpleoParcial() {
					visitOthers();
				}

				@Override
				public void visitIncapacidadTemporalPagoDelegadoFormacion() {
					visitOthers();
				}

				@Override
				public void visitExpedienteRegulacionEmpleoParcialFormacion() {
					visitOthers();
				}

				@Override
				public void visitMaternidadPaternidadTiempoParcialFormacion() {
					visitOthers();
				}

				@Override
				public void visitIncapacidadTemporalATEPPagoDelegadoFormacion() {
					visitOthers();
				}
			};

			SalaryVisitor _fullMaternity = new SalaryVisitor(){
				
				private void visitOthers(){
					cretaPeriods.add(new Period(period.getStart(), period.getEnd()));
					state = standard;
				}
				
				@Override
				public void visitFormacionNormal() {
					visitOthers();
				}
				
				@Override
				public void visitTiempoParcialNormal() {
					visitOthers();
				}

				@Override
				public void visitTiempoCompletoNormal() {
					visitOthers();
				}
				
				@Override
				public void visitRegimenArtistasNormal() {
					visitOthers();
				}

				@Override
				public void visitGrupoCotizacionDiario() {
					// noop
				}

				@Override
				public void visitGrupoCotizacionMensual() {
				}

				@Override
				public void visitIncapacidadTemporal15PrimerosDias() {
					cretaPeriods.add(new Period(period.getStart(), period.getEnd()));
					state = it15PrimerosDias;
				}

				@Override
				public void visitIncapacidadTemporalPagoDelegado() {
					cretaPeriods.add(new Period(period.getStart(), period.getEnd()));
					state = itDelegate;
				}

				@Override
				public void visitIncapacidadTemporalPagoDirecto() {
					cretaPeriods.add(new Period(period.getStart(), period.getEnd()));
				}

				@Override
				public void visitIncapacidadTemporalATEPPagoDelegado() {
					visitOthers();
				}
				
				@Override
				public void visitMaternidadPaternidadTiempoCompleto() {
					Period last = cretaPeriods.removeLast();
					cretaPeriods.add(new Period(last.getStart(), period.getEnd()));
				}
				
				@Override
				public void visitMaternidadPaternidadTiempoParcial() {
					visitOthers();
				}
				
				@Override
				public void visitExpedienteRegulacionEmpleoTotal() {
					visitOthers();
				}
				
				@Override
				public void visitExpedienteRegulacionEmpleoParcial() {
					visitOthers();
				}

				@Override
				public void visitIncapacidadTemporalPagoDelegadoFormacion() {
					visitOthers();
				}

				@Override
				public void visitExpedienteRegulacionEmpleoParcialFormacion() {
					visitOthers();
				}

				@Override
				public void visitMaternidadPaternidadTiempoParcialFormacion() {
					visitOthers();
				}

				@Override
				public void visitIncapacidadTemporalATEPPagoDelegadoFormacion() {
					visitOthers();
				}
			};

			private Period period ;
			private SalaryVisitor state = standard;
			
			
			public void setPeriod(Period period) {
				this.period = period;
			}
			
			@Override
			public void visitFormacionNormal() {
				state.visitFormacionNormal();
			}
			
			@Override
			public void visitTiempoParcialNormal() {
				state.visitTiempoParcialNormal();
			}

			@Override
			public void visitTiempoCompletoNormal() {
				state.visitTiempoCompletoNormal();
			}

			@Override
			public void visitRegimenArtistasNormal() {
				state.visitRegimenArtistasNormal();
			}

			@Override
			public void visitGrupoCotizacionDiario() {
				state.visitGrupoCotizacionDiario();
			}

			@Override
			public void visitGrupoCotizacionMensual() {
				state.visitGrupoCotizacionMensual();
			}

			@Override
			public void visitIncapacidadTemporal15PrimerosDias() {
				state.visitIncapacidadTemporal15PrimerosDias();
			}

			@Override
			public void visitIncapacidadTemporalPagoDelegado() {
				state.visitIncapacidadTemporalPagoDelegado();
			}

			@Override
			public void visitIncapacidadTemporalPagoDirecto() {
				state.visitIncapacidadTemporalPagoDirecto();
			}

			@Override
			public void visitIncapacidadTemporalATEPPagoDelegado() {
				state.visitIncapacidadTemporalATEPPagoDelegado();
			}
			
			@Override
			public void visitMaternidadPaternidadTiempoCompleto() {
				state.visitMaternidadPaternidadTiempoCompleto();
			}
			
			@Override
			public void visitMaternidadPaternidadTiempoParcial() {
				state.visitMaternidadPaternidadTiempoParcial();
			}

			@Override
			public void visitExpedienteRegulacionEmpleoTotal() {
				state.visitExpedienteRegulacionEmpleoTotal();
			}

			@Override
			public void visitExpedienteRegulacionEmpleoParcial() {
				state.visitExpedienteRegulacionEmpleoParcial();
			}

			@Override
			public void visitIncapacidadTemporalPagoDelegadoFormacion() {
				state.visitIncapacidadTemporalPagoDelegadoFormacion();
			}

			@Override
			public void visitExpedienteRegulacionEmpleoParcialFormacion() {
				state.visitExpedienteRegulacionEmpleoParcialFormacion();
			}

			@Override
			public void visitMaternidadPaternidadTiempoParcialFormacion() {
				state.visitMaternidadPaternidadTiempoParcialFormacion();
			}

			@Override
			public void visitIncapacidadTemporalATEPPagoDelegadoFormacion() {
				state.visitIncapacidadTemporalATEPPagoDelegadoFormacion();
			}

		};
		
		Visitor visitor = new Visitor();

		for ( Period p: periods ) {
			visitor.setPeriod(p);
			visit(salary, p.getStart(), p.getEnd(), visitor );
		}
		
		return cretaPeriods;
		
	}
	
	
	private static Date getFirstDayOf(Month mes, int anho) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR , anho);
		calendar.set(Calendar.MONTH , mes.getValue()-1);
		calendar.set(Calendar.DAY_OF_MONTH,1);
		// Set time fields to zero
		calendar.set(Calendar.HOUR_OF_DAY , 0);
		calendar.set(Calendar.MINUTE , 0);
		calendar.set(Calendar.SECOND , 0);
		calendar.set(Calendar.MILLISECOND , 0);

		return calendar.getTime();
	}
	
	private static Date getLastDayOf(Month mes, int anho) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR , anho);
		calendar.set(Calendar.DAY_OF_MONTH,1);
		calendar.set(Calendar.MONTH , mes.getValue()-1);
		calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		// Set time fields to zero
		calendar.set(Calendar.HOUR_OF_DAY , 0);
		calendar.set(Calendar.MINUTE , 0);
		calendar.set(Calendar.SECOND , 0);
		calendar.set(Calendar.MILLISECOND , 0);

		return calendar.getTime();
	}
	
	
	private static int getQuoteDays(Salary salary,Period period) {
		int quoteDays = 0;
		long days = period.daysStream().count();
		List<ContextData> datas = salary.getContextData(QUOTE_DAYS.getName(), period.getStart(), period.getEnd());
		
		if ( datas == null || datas.isEmpty() )
			return (int) days;
		 
		for ( ContextData data: datas  ) {
			Period dataPeriod = new Period(data.getStartDate(), data.getEndDate());
			Period intersectPeriod = period.intersect(dataPeriod);
			if ( intersectPeriod == null )
				continue;
			long dataDays = dataPeriod.daysStream().count();
			long intersectDays = intersectPeriod.daysStream().count();
			quoteDays += Double.parseDouble(data.getExpression()) / dataDays * intersectDays; 
		}
		return quoteDays;
	}

	private static interface SalaryVisitor {
		void visitFormacionNormal();
		void visitTiempoParcialNormal();
		void visitTiempoCompletoNormal();
		void visitRegimenArtistasNormal();
		void visitGrupoCotizacionDiario();
		void visitGrupoCotizacionMensual();
		void visitIncapacidadTemporal15PrimerosDias();
		void visitIncapacidadTemporalPagoDelegado();
		void visitIncapacidadTemporalPagoDirecto();
		void visitIncapacidadTemporalATEPPagoDelegado();
		void visitMaternidadPaternidadTiempoCompleto();
		void visitMaternidadPaternidadTiempoParcial();
		void visitExpedienteRegulacionEmpleoTotal();
		void visitExpedienteRegulacionEmpleoParcial();
		void visitIncapacidadTemporalPagoDelegadoFormacion();
		void visitExpedienteRegulacionEmpleoParcialFormacion();
		void visitMaternidadPaternidadTiempoParcialFormacion();
		void visitIncapacidadTemporalATEPPagoDelegadoFormacion();
		
	}
	
	@FunctionalInterface
	private static interface Visit {
		void visit();
	}

	private static void visit(Salary salary, Date startDate, Date endDate, SalaryVisitor visitor) {
		String tc2 = getContextData(TC2.getName(),salary, startDate, endDate, "-");
		boolean fullTime = getContextData(FULL_TIME.getName(), salary, startDate, endDate,  true);
		int cccType = getContextData(CCC_TYPE.getName() ,salary, startDate, endDate, 0);
		
		boolean artistas = CCCType.ARTIST.ordinal() == cccType;
		
		boolean iT15primerosDias = (
		getSumContextData(ContextVariable.COMMON_DISEASE_DAYS_1_3.getName(), salary, startDate, endDate)
		+ getSumContextData(ContextVariable.COMMON_DISEASE_DAYS_4_15.getName(), salary, startDate, endDate) 
		) > 0.00;
		
		boolean iTPagoDelegado = (
		getSumContextData(ContextVariable.COMMON_DISEASE_DAYS_16_20.getName(), salary, startDate, endDate)
		+ getSumContextData(ContextVariable.COMMON_DISEASE_DAYS_21.getName(), salary, startDate, endDate) 
		) > 0.00;

		boolean atEPPagoDelegado = (
		getSumContextData(ContextVariable.OCCUPATIONAL_DISEASE_DAYS.getName(), salary, startDate, endDate)
		) > 0.00;
		
		boolean fullMaternity = 
		getContextData(ContextVariable.MATERNITY_FACTOR.getName(), salary, startDate, endDate, 0.00)
		 == 1.00;

		boolean fullPaternity = 
		getContextData(ContextVariable.PATERNITY_FACTOR.getName(), salary, startDate, endDate, 0.00)
		 == 1.00;

		boolean partialMaternity = 
		getContextData(ContextVariable.MATERNITY_FACTOR.getName(), salary, startDate, endDate, 1.00)
		 < 1.00;

		boolean partialPaternity = 
		getContextData(ContextVariable.PATERNITY_FACTOR.getName(), salary, startDate, endDate, 1.00)
		 < 1.00;
		
		boolean iTPagoDirecto = (
		getSumContextData(ContextVariable.COMMON_DISEASE_LACK_DAYS.getName(), salary, startDate, endDate)
		+ getSumContextData(ContextVariable.COMMON_DISEASE_DAYS_366.getName(), salary, startDate, endDate)
		+ getSumContextData(ContextVariable.OCCUPATIONAL_DISEASE_DAYS_366.getName(), salary, startDate, endDate)
		) > 0.00;
		
		double ereFactor = 0.00;
		for ( ContextVariable ere : ContextVariable.ERE_FACTORS ) 
			ereFactor += getContextData(ere.getName(), salary, startDate, endDate,  0.00) ;

		boolean ereTotal = ( ereFactor == 1.00 ) ; 
		boolean ereParcial = ( ereFactor > 0.00 && ereFactor < 1.00 ) ; 
		

		boolean tiempoCompleto = fullTime && ("14".indexOf(tc2.charAt(0)) != -1);
		
		
		boolean formacion = "421".equals(tc2) ;
		
		boolean becarios = CCCType.FELLOWS.ordinal() == cccType;;
		
		
		if ( becarios )
			if ( iTPagoDelegado )
				visitor.visitIncapacidadTemporalPagoDelegadoFormacion();
			else if ( atEPPagoDelegado )
				visitor.visitIncapacidadTemporalATEPPagoDelegadoFormacion();
			else
				;
		else if ( formacion )
			if ( iT15primerosDias )
				;
			else if ( iTPagoDelegado )
				visitor.visitIncapacidadTemporalPagoDelegadoFormacion();
			else if ( fullMaternity  )
				;
			else if ( fullPaternity  )
				;
			else if ( partialMaternity )
				visitor.visitMaternidadPaternidadTiempoParcialFormacion();
			else if ( partialPaternity )
				visitor.visitMaternidadPaternidadTiempoParcialFormacion();
			else if ( atEPPagoDelegado )
				visitor.visitIncapacidadTemporalATEPPagoDelegadoFormacion();
			else if ( iTPagoDirecto )
				;
			else if ( ereTotal )
				;
			else if ( ereParcial )
				visitor.visitExpedienteRegulacionEmpleoParcialFormacion();
			else
				visitor.visitFormacionNormal();
		else if ( iT15primerosDias )
			visitor.visitIncapacidadTemporal15PrimerosDias();
		else if ( iTPagoDelegado )
			visitor.visitIncapacidadTemporalPagoDelegado();
		else if ( fullMaternity  )
			visitor.visitMaternidadPaternidadTiempoCompleto();
		else if ( fullPaternity  )
			visitor.visitMaternidadPaternidadTiempoCompleto();
		else if ( partialMaternity )
			visitor.visitMaternidadPaternidadTiempoParcial();
		else if ( partialPaternity )
			visitor.visitMaternidadPaternidadTiempoParcial();
		else if ( atEPPagoDelegado )
			visitor.visitIncapacidadTemporalATEPPagoDelegado();
		else if ( iTPagoDirecto )
			visitor.visitIncapacidadTemporalPagoDirecto();
		else if ( ereTotal )
			visitor.visitExpedienteRegulacionEmpleoTotal();
		else if ( ereParcial )
			visitor.visitExpedienteRegulacionEmpleoParcial();
		else if ( artistas )
			visitor.visitRegimenArtistasNormal();
		else if (tiempoCompleto)
			visitor.visitTiempoCompletoNormal();
		else 
			visitor.visitTiempoParcialNormal();
		
		Visit grupoCotizacion ;
		String quoteGroup = getContextData(QUOTE_GROUP.getName(), salary, startDate, endDate,  "01");
		if ( Integer.parseInt(quoteGroup ) >= 8 )
			grupoCotizacion = visitor::visitGrupoCotizacionDiario;
		else
			grupoCotizacion = visitor::visitGrupoCotizacionMensual;
		
		
		if ( becarios )
			;
		else if ( formacion )
			;		
		else if ( iT15primerosDias )
			grupoCotizacion.visit();		
		else if ( iTPagoDelegado )
			grupoCotizacion.visit();		
		else if ( fullMaternity  )
			grupoCotizacion.visit();		
		else if ( fullPaternity  )
			grupoCotizacion.visit();		
		else if ( partialMaternity )
			;		
		else if ( partialPaternity )
			;
		else if ( atEPPagoDelegado )
			grupoCotizacion.visit();		
		else if ( iTPagoDirecto )
			grupoCotizacion.visit();		
		else if ( ereTotal )
			grupoCotizacion.visit();		
		else if ( ereParcial )
			;		
		else if ( artistas )
			;		
		else if (tiempoCompleto)
			grupoCotizacion.visit();		
		else 
			grupoCotizacion.visit();		
		
		
	}

	private static interface TypeVisitor<T> {
		T visitL00();
		T visitL02();
		T visitL13();
		T visitL03();
		T visitL91();
		T visitL90();
	}

	private static <T> T visit(String tipo, TypeVisitor<T> visitor) {
		if ( "L00".equalsIgnoreCase(tipo)) 
			return visitor.visitL00();
		if ( "L02".equalsIgnoreCase(tipo)) 
			return visitor.visitL02();
		if ( "L13".equalsIgnoreCase(tipo)) 
			return visitor.visitL13();
		if ( "L03".equalsIgnoreCase(tipo)) 
			return visitor.visitL03();
		if ( "L91".equalsIgnoreCase(tipo)) 
			return visitor.visitL91();
		if ( "L90".equalsIgnoreCase(tipo)) 
			return visitor.visitL90();
		return null;
	}

	private static String getContextData(String name, Salary salary, Date startDate, Date endDate) {
		List<ContextData> datas= salary.getContextData(name, startDate, endDate);
		String data = datas.get(0).getExpression().trim();
		for ( int i= 1; i < datas.size(); i++) {
			String nextData = datas.get(i).getExpression().trim();
			if ( !data.equalsIgnoreCase(nextData))
				throw new RuntimeException();
			data = nextData;
		}
		return data;
	}

	private static double getSumContextData(String name, Salary salary, Date startDate, Date endDate) {
		List<ContextData> datas= salary.getContextData(name, startDate, endDate);
		double data = 0.00;
		for ( int i= 0; i < datas.size(); i++)
			data += Double.parseDouble(datas.get(i).getExpression());
		return data;
	}
	
	private static String getContextData(String name, Salary salary, Date startDate, Date endDate, String def ) {
		String data = def;
		try {
			data = getContextData(name, salary, startDate, endDate);
			Map <String,Object> context = ExcelFunctions.load( new HashMap<String,Object>());
			data = ExpressionContext.eval(data.trim(), context, def.getClass());
		} catch ( Exception e  ) {
			// Default?
		}
		return data == null ? def : data ;
	}

	private static <T extends Object > T getContextData(String name, Salary salary, Date startDate, Date endDate, T def ) {
		Map <String,Object> context = ExcelFunctions.load( new HashMap<String,Object>());
		List<ContextData> datas= salary.getContextData(name, startDate, endDate);
		T data = null;
		for ( int i= 0; i < datas.size(); i++) {
			try {
				T nextData = (T) ExpressionContext.eval(datas.get(i).getExpression().trim(), context, def.getClass());
				if ( data != null && !data.equals(nextData))
					throw new RuntimeException();
				data = nextData;
			} catch ( Exception e  ) {
				// Default?
			}
		}
		return data == null ? def : data ;
	}
	
	
}
