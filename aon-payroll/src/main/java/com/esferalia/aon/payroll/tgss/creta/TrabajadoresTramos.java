package com.esferalia.aon.payroll.tgss.creta;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.FULL_TIME;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MATERNITY_BASE;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.watson.server.io.ByteArrayOutputStream;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.core.tgss.creta.jaxb.Utils;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Trabajador;
import net.aonsolutions.core.tgss.jaxb.trabajadorestramos.DatoSolicitadoBuilder;
import net.aonsolutions.core.tgss.jaxb.trabajadorestramos.LiquidacionMesBuilder;
import net.aonsolutions.core.tgss.jaxb.trabajadorestramos.TrabajadorBuilder;
import net.aonsolutions.core.tgss.jaxb.trabajadorestramos.TrabajadoresTramosBuilder;
import net.aonsolutions.core.tgss.jaxb.trabajadorestramos.TramoBuilder;

public class TrabajadoresTramos {
	
	
	

	public TrabajadoresTramos() {
		// TODO Auto-generated constructor stub
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
		
		AONContext aonContext = new AONContext(conn);
		
		
		Date fromDate = getFirstDayOf(desdeMes, desdeAnho);
		Date toDate = getLastDayOf(hastaMes, hastaAnho);
		for (Date date = fromDate; date.before(toDate); date = AonDateUtils.add(date,Calendar.MONTH,1))
			for ( String ccc: cccs ) {
				String ccc_prov_num_dc = ccc.substring(4); // PROVINCIA (2) + Nº (7) + DÍGITOS CONTROL (2)
				liquidacionMes(aonContext, trabajadoresTramosBuilder, desdeAnho, desdeMes, ccc_prov_num_dc, tipo);
			}
		

		net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresTramos = trabajadoresTramosBuilder
				.create();

		Utils.marshal(trabajadoresTramos, os);
	}
	
	// ------------------------------------------------------------------------
	
	private static void liquidacionMes(AONContext aonContext, 
			TrabajadoresTramosBuilder trabajadoresTramosBuilder,
			int anho, 
			Month mes,
			String ccc,
			String tipo) {
		
		LiquidacionMesBuilder liquidacionMesBuilder = new LiquidacionMesBuilder();
		
		liquidacionMesBuilder.setMes(mes);
		liquidacionMesBuilder.setAnho(anho);
		
		Date startDate = getFirstDayOf(mes, anho);
		Date endDate = getLastDayOf(mes, anho);
		

		AON.getSalaryData(aonContext, 
				props -> props.getCCCProperty().eq(ccc)
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
					
					for ( ContextData cgcData: salary.getContextData().getOrDefault(CGC_BASE.getName(), Collections.emptyList()) )
						cgcBasePeriods.add( new Period(cgcData.getStartDate(), cgcData.getEndDate()));

					Collections.sort(cgcBasePeriods); // sort & sort & sort again .
					
					for ( ContextData cgcData: salary.getContextData().getOrDefault(MATERNITY_BASE.getName(), Collections.emptyList()) ) {
						Period period = new Period(cgcData.getStartDate(), cgcData.getEndDate());
						int insertionPoint = Collections.binarySearch(cgcBasePeriods, period);
						if ( insertionPoint < 0 ) 
							cgcBasePeriods.add((-(insertionPoint) - 1), period);
					}

					
					List<Period> periods = merge(salary, cgcBasePeriods);//cgcBasePeriods;
					for ( Period p: periods ) {
						
						TramoBuilder tramoBuilder  = new TramoBuilder();

						Calendar start = Calendar.getInstance();
						start.setTime(p.getStart());
						tramoBuilder.setDiaDesde(start.get(Calendar.DATE));
						tramoBuilder.setMesDesde(start.get(Calendar.MONTH)+1);
						tramoBuilder.setAnhoDesde(start.get(Calendar.YEAR));

						Calendar end = Calendar.getInstance();
						end.setTime(p.getEnd());
						
						tramoBuilder.setDiaHasta(end.get(Calendar.DATE));
						tramoBuilder.setMesHasta(end.get(Calendar.MONTH)+1);
						tramoBuilder.setAnhoHasta(end.get(Calendar.YEAR));
						

						Double diasCotizados = getContextData(salary, 
										QUOTE_DAYS, 
										p.getStart(), 
										p.getEnd(),
										Collectors.summingDouble(Double::parseDouble) );
						tramoBuilder.setDiasCotizados(diasCotizados.intValue());
						
						visit(salary, p.getStart(), p.getEnd(), new SalaryVisitor() {
							DatoSolicitadoBuilder dataSolicitadoBuilder  = new DatoSolicitadoBuilder();
							
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
							
							
						});
						
						tramoBuilder.setTipoDeContrato(getContextData(TC2.getName(), salary, p.getStart(), p.getEnd()));
						tramoBuilder.setGrupoCotizacion(getContextData(QUOTE_GROUP.getName(), salary, p.getStart(), p.getEnd()));
						
						

						trabajadorBuilder.addTramo(tramoBuilder.create());
						
					}
					
					Trabajador trabajador = trabajadorBuilder.create();
					liquidacionMesBuilder.add(trabajador);
				}
		)
		;
		
		trabajadoresTramosBuilder.addLiquidacionMes(liquidacionMesBuilder.create());
	}
	
	private static List<Period> merge(Salary salary, List<Period> periods) {
		LinkedList<Period> cretaPeriods = new LinkedList<Period>();
		
		class Visitor implements  SalaryVisitor {
			
			SalaryVisitor standard = new SalaryVisitor(){
				@Override
				public void visitTiempoParcialNormal() {
					cretaPeriods.add(new Period(period.getStart(), period.getEnd()));
				}

				@Override
				public void visitTiempoCompletoNormal() {
					cretaPeriods.add(new Period(period.getStart(), period.getEnd()));
				}

				@Override
				public void visitGrupoCotizacionDiario() {
					//cretaPeriods.add(new Period(period.getStart(), period.getEnd()));
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
			};

			SalaryVisitor it15PrimerosDias = new SalaryVisitor(){
				
				private void visitOthers(){
					cretaPeriods.add(new Period(period.getStart(), period.getEnd()));
					state = standard;
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
				public void visitGrupoCotizacionDiario() {
					// noop
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
			};
			
			SalaryVisitor itDelegate = new SalaryVisitor(){
				
				private void visitOthers(){
					cretaPeriods.add(new Period(period.getStart(), period.getEnd()));
					state = standard;
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
				public void visitGrupoCotizacionDiario() {
					// noop
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
			};

			SalaryVisitor _fullMaternity = new SalaryVisitor(){
				
				private void visitOthers(){
					cretaPeriods.add(new Period(period.getStart(), period.getEnd()));
					state = standard;
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
				public void visitGrupoCotizacionDiario() {
					// noop
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
			};

			private Period period ;
			private SalaryVisitor state = standard;
			
			
			public void setPeriod(Period period) {
				this.period = period;
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
			public void visitGrupoCotizacionDiario() {
				state.visitGrupoCotizacionDiario();
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
		return calendar.getTime();
	}
	
	private static Date getLastDayOf(Month mes, int anho) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR , anho);
		calendar.set(Calendar.MONTH , mes.getValue()-1);
		calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return calendar.getTime();
	}
	
	private static <A, R> R getContextData(Salary salary, ContextVariable var, 
			Date startDate, Date endDate, Collector<? super String, A, R> collector) {
		List<ContextData> datas = salary.getContextData().get(var.getName());
		if (datas == null)
			return null;
		R r = datas.stream()
				.filter(d->d.getStartDate().compareTo(startDate) >= 0)
				.filter(d->d.getEndDate().compareTo(endDate) <= 0)
				.map(d -> d.getExpression()).collect(collector);
		return r;
	}
	
	private static interface SalaryVisitor {
		void visitTiempoParcialNormal();
		void visitTiempoCompletoNormal();
		void visitGrupoCotizacionDiario();
		void visitIncapacidadTemporal15PrimerosDias();
		void visitIncapacidadTemporalPagoDelegado();
		void visitIncapacidadTemporalATEPPagoDelegado();
		void visitMaternidadPaternidadTiempoCompleto();
		void visitMaternidadPaternidadTiempoParcial();
		
	}
	
	private static void visit(Salary salary, Date startDate, Date endDate, SalaryVisitor visitor) {
		String tc2 = getContextData(TC2.getName(),salary, startDate, endDate);
		boolean fullTime = getContextData(FULL_TIME.getName(), salary, startDate, endDate,  true);
		
		
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

		boolean tiempoCompleto = fullTime && ("14".indexOf(tc2.charAt(0)) != -1);

		if ( iT15primerosDias )
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
		else if (tiempoCompleto)
			visitor.visitTiempoCompletoNormal();
		else 
			visitor.visitTiempoParcialNormal();
			
		//String quoteGroup = getContextData(QUOTE_GROUP.getName(),salary, startDate, endDate);
		String quoteGroup = getContextData(QUOTE_GROUP.getName(), salary, startDate, endDate,  "01");
		if ( Integer.parseInt(quoteGroup ) >= 8 )
			visitor.visitGrupoCotizacionDiario();
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
