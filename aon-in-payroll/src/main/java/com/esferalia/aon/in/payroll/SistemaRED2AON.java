package com.esferalia.aon.in.payroll;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.User.USER;
import static com.esferalia.aon.salary.expression.Period.max;
import static com.esferalia.aon.salary.expression.Period.min;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.JAXBException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import com.esferalia.aon.in.payroll.pdf.JooqEnterpriseSalaryBuilder;
import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.in.payroll.tgss.idc.Idc.IdcListener;
import com.esferalia.aon.in.payroll.tgss.idc.PEC;
import com.esferalia.aon.in.payroll.tgss.sld.SLDSalaries;
import com.esferalia.aon.in.payroll.utils.EmployeeParse;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Bonus;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.Cost;
import com.esferalia.aon.occam.api.model.Deduction;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.EmployeeFilter;
import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.payroll.ContractData;
import com.esferalia.aon.occam.api.model.payroll.Employee;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.BonusType;
import com.esferalia.aon.occam.api.model.type.DeductionType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.SalaryType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.core.pool.AonConnectionException;
import net.aonsolutions.core.pool.AonDataSource;
import net.aonsolutions.core.tgss.creta.jaxb.Utils;
import net.aonsolutions.core.tgss.creta.jaxb.respuesta.Liquidacion;
import net.aonsolutions.core.tgss.creta.jaxb.respuesta.Periodo;
import net.aonsolutions.core.tgss.creta.jaxb.respuesta.Respuesta;
import solutions.aon.seg.social.ServicioRED;
import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.SistemaRED.LiquidationOrigin;
import solutions.aon.seg.social.SistemaRED.LiquidationType;
import solutions.aon.seg.social.SistemaRED.Regime;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.object.Calc;
import solutions.aon.seg.social.object.Period;

public class SistemaRED2AON {

	
	public static void main(String[] args) throws java.text.ParseException, SQLException, AonConnectionException {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yyyy");
		Map<Byte, String> regimeMap = new HashMap<Byte, String>(){
			{
				put((byte)6,"0138");
				put((byte)7,"0163");
			}
		};
	
	
		Option helpOption = Option.builder()
		.longOpt("help")
		.desc("Display this help and exit.")
		.build();
	
		Option dateOption = Option.builder("d")
		.hasArg()
		.longOpt("date")
		.argName("date")
		.desc("Date to sync ( MM/yyyy ), default "+ dateFormat.format(new Date())+" .")
		.build();
	
		Option fromOption = Option.builder()
		.hasArg()
		.longOpt("from")
		.argName("date")
		.desc("Date to sync from ( MM/yyyy ), default "+ dateFormat.format(new Date())+" .")
		.build();

		Option toOption = Option.builder()
		.hasArg()
		.longOpt("to")
		.argName("date")
		.desc("Date to sync to ( MM/yyyy ), default "+ dateFormat.format(new Date())+" .")
		.build();

		Option whereOption = Option.builder("w")
		.hasArg()
		.longOpt("where")
		.argName("where")
		.desc("Sync only selected CCCs. Quotes are mandatory.")
		.build();
		
		Option databasesOption = Option.builder("B")
		.hasArg()
		.required()
		.longOpt("databases")
		.argName("databases")
		.desc("Sync several databases.")
		.build();
		
		Option idcOption = Option.builder()
		.longOpt("idc")
		.desc("Adds Bonuses, Quotes from IDCs.")
		.build();

		Option calcsOption = Option.builder()
		.longOpt("calcs")
		.desc("Adds Calculations from SLD.")
		.build();

		Option nafsOption = Option.builder()
		.hasArg()
		.longOpt("nafs")
		.argName("nafs")
		.desc("Sync selected nafs.")
		.build();
		

		Options options = new Options();
		options.addOption(helpOption);
		options.addOption(toOption);
		options.addOption(fromOption);
		options.addOption(dateOption);
		options.addOption(whereOption);
		options.addOption(databasesOption);
		options.addOption(idcOption);
		options.addOption(calcsOption);
		options.addOption(nafsOption);
	
		
		CommandLine commandLine = null;
		
		try {
			CommandLineParser parser = new DefaultParser();
			commandLine = parser.parse(options, args);
			
			String dateArg = commandLine.getOptionValue(dateOption.getLongOpt(), dateFormat.format(new Date()));
			
			Date  fromDate = dateFormat.parse(commandLine.getOptionValue(fromOption.getLongOpt(), dateArg ));
			Date  toDate = dateFormat.parse(commandLine.getOptionValue(toOption.getLongOpt(), dateArg ));

			
			java.sql.Date startDate = AonDateUtils.getFirstDayOfMonth(new java.sql.Date(fromDate.getTime()));
			java.sql.Date endDate = AonDateUtils.getLastDayOfMonth(new java.sql.Date(toDate.getTime()));
			
			Condition condition = DSL.condition(commandLine.getOptionValue(whereOption.getLongOpt(), "1=1"));
			
			boolean idc = commandLine.hasOption(idcOption.getLongOpt());
			boolean calcs = commandLine.hasOption(calcsOption.getLongOpt());
			
			String nafs [] = Optional.ofNullable(commandLine.getOptionValues(nafsOption.getLongOpt())).orElse(new String[0]);
			
			for( String schema : commandLine.getOptionValues(databasesOption.getLongOpt()) ) {
				System.out.println(schema);;
				try (Connection connection = AonDataSource.getInstance().getDatabaseConnection(schema)) {
					AONContext aonContext = new AONContext(connection);
					DSLContext dslContext = aonContext.getDslContext();
					
					dslContext
					.select()
					.from(DOMAIN)
					.innerJoin(ENTERPRISE_CCC).on(DOMAIN.ID.eq(ENTERPRISE_CCC.DOMAIN))
					.innerJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY.eq(ENTERPRISE_ACTIVITY.ID))
					.leftJoin(USER).on(DOMAIN.PARENT.eq(USER.DOMAIN))
					.where(condition)
					.groupBy(ENTERPRISE_CCC.CCC)
					.orderBy(USER.ID)
					.fetchStream()
					.forEach( domain -> {
						Integer userId = domain.get(USER.ID);
						String login = domain.get(USER.LOGIN);
						Integer domainId = domain.get(DOMAIN.ID);
						String domainName = domain.get(DOMAIN.NAME);
						
						String ccc = domain.get(ENTERPRISE_CCC.CCC);
						String regimen = regimeMap.getOrDefault(domain.get(ENTERPRISE_CCC.TYPE), "0111");
						
						Certificate certificate = AON.getCertificate(domainName, domainId, login, userId, "TGSS");	
						
						String certificateType = MimeType.PKCS12.name();
						byte [] certificateData=  certificate.getData();
						String certificatePassword =  certificate.getPassword();
						
						
						try {
							if ( calcs ) {
								addCalcs(aonContext, 
										login, 
										domainName, 
										domainId, 
										certificateData, 
										certificatePassword, 
										certificateType, 
										regimen, 
										ccc, 
										startDate, 
										//endDate,
										LiquidationType.L03_COMP_ABONO_SALARIOS_CARACTER_RETROACTIV,
										nafs);
								
								Integer enterpriseId = domain.get(ENTERPRISE_ACTIVITY.ENTERPRISE);

								JooqEnterpriseSalaryBuilder.generateEnterprisePayroll(
									new FileOutputStream("/tmp/costs.pdf"), 
									domainName, 
									domainId, 
									login, 
									startDate, 
									endDate, 
									enterpriseId, 
									null, 
									SalaryType.values());
							}
							if ( idc )
								syncWithIdcs(
										login, 
										userId, 
										domainName, 
										domainId, 
										certificateData, 
										certificatePassword, 
										certificateType, 
										regimen, 
										ccc, 
										startDate, 
										endDate,
										nafs);
							
//							SistemaRED.getCosts(certificateData, certificatePassword, certificateType, regime, ccc, startDate, endDate);
							
//							System.out.printf("SUCCES: %s-%s %d [%s]\r\n", regime, ccc, added, login );
						}
//						catch (NotAllowedContributionAccount e) {
//							System.err.printf("ERROR: %s-%s %s [%s]\r\n", regime, ccc, e.getMessage(), login);
//						}
//						catch (InvalidDataException e) {
//							System.err.printf("ERROR: %s-%s %s\r\n", regime, ccc, e.getMessage());
//						}
//						catch (InvalidCertificateException e) {
//							System.err.printf("ERROR: %s-%s %s [%s]\r\n]", regime, ccc, "Invalid Certificate", login);
//						}
						catch (Exception e) {
							e.printStackTrace();
							System.out.printf("ERROR: %s-%s %s \r\n", regimen, ccc, e.getMessage());
						}
					});
					
				}
			}
			
	
		} catch (ParseException e) {
			// oops, somthing went wrong
			new HelpFormatter().printHelp(SistemaRED2AON.class.getSimpleName() + " [options] [domains]", options );
		} 
			
			

	}


	
	
	// ------------------------------------------------------------------------

	public static void addCalcs( 
		AONContext aonContext,
		String login,
		String domainName, 
		Integer domainId,
		byte [] certificateData,
		String certificatePassword, 
		String certificateType, 
		String regimen, 
		String ccc, 
		java.sql.Date month,
		LiquidationType liquidationType,
		String ...nafs) {
	    
	    java.sql.Date startDate = AonDateUtils.getFirstDayOfMonth(month);
	    
	    Set<String> added = new HashSet<>();
	    
	    List<Liquidacion> liquidations = getLiquidations(login, domainId, domainName, ccc, startDate, liquidationType, nafs);
	    
	    for (Liquidacion liquidation : liquidations) {
		
		
		// Liquidacion Confirmada
		if ( liquidation.getErrores().getError().stream()
		.noneMatch( error -> error.getCodigoErr().equals("R9529")) )
		    continue;
		
		if ( !added.add(liquidation.getNumeroLiquidacion()) )
		    continue;
		
		try {
        		addCalcs(aonContext, 
        			login, 
        			domainName, 
        			domainId, 
        			certificateData, 
        			certificatePassword, 
        			certificateType, 
        			liquidation,
        			nafs);
		} catch ( Exception e ) {
		    e.printStackTrace();
		    // TODO:
		}
	    }
	}

	public static void addCalcs( 
		AONContext aonContext,
		String login,
		String domainName, 
		Integer domainId,
		byte [] certificateData,
		String certificatePassword, 
		String certificateType, 
		String regimen, 
		String ccc, 
		java.sql.Date startDate, 
		java.sql.Date endDate ,
		String ...nafs) throws SegSocialException {
	    	
	    	// first of all liquidations from with PeriodoDesde=startDate & PeriodoHasta=endDate 
		addCalcs(aonContext, 
			    login, 
			    domainName, 
			    domainId, 
			    certificateData, 
			    certificatePassword, 
			    certificateType, 
			    regimen, 
			    ccc, 
			    startDate, 
			    endDate, 
			    LiquidationType.TODAS, 
			    nafs);
	}

	public static void addCalcs( 
			AONContext aonContext,
			String login,
			String domainName, 
			Integer domainId,
			byte [] certificateData,
			String certificatePassword, 
			String certificateType, 
			String regimen, 
			String ccc, 
			java.sql.Date startDate, 
			java.sql.Date endDate ,
			LiquidationType liquidationType,
			String ...nafs) throws SegSocialException {
		
		String authorized = getAuthorized(login, domainId, domainName, ccc);
		
		Map<String,List<Employee>> employees = getEmployees(login, domainId, domainName, ccc, startDate, endDate, nafs);
		
		nafs = employees.keySet().toArray(new String[employees.size()]);
		
		Map<String, Map<String, Map<Period, Map<String, Calc>>>> allCalcs = 
		SistemaRED.getCalcByNAF(
				certificateData, 
				certificatePassword, 
				certificateType, 
				ccc, 
				Regime.fromValue(regimen), 
				startDate, 
				endDate, 
				liquidationType, 
				LiquidationOrigin.TODAS, 
				authorized,
				nafs
				);
		
		
		
		allCalcs.forEach((liq, liqCalcs) -> liqCalcs.forEach(( naf, nafCalcs ) -> {
				
				employees.get(naf).forEach(employee -> {
					try {
						Period period = new Period(employee.getStartDate(), 
								employee.getEndDate().orElse(null));
						
						
						Salary salary = SLDSalaries.getSalary(liq, ccc, naf, nafCalcs, period);
						salary.setEmployeeDocument(employee.getDni());
						
						
						Date l13startDate = null;
						if ( salary.getSalaryType() == SalaryType.L13 ) {
							l13startDate = salary.getStartDate();
							salary.setStartDate(employee.getStartDate());
							salary.setIssueDate(min(salary.getEndDate(), endDate));
						}
						
						employee.getName().ifPresent(salary::setEmployeeName);
						
						try {
							AON.saveSalaries(aonContext, domainId, Collections.singleton(salary));
						} catch ( Exception e ) {
							java.sql.Date settleEndDate = addDays(l13startDate, -1);
							Map<String,List<Employee>> oldEmployees = 
									getEmployees(login, domainId, domainName, ccc, settleEndDate, settleEndDate, p -> p.getNafProperty().eq(naf));
							oldEmployees.get(naf).forEach(oldEmployee -> {
								salary.setStartDate(oldEmployee.getStartDate());
								salary.setIssueDate(min(salary.getEndDate(), endDate));
								AON.saveSalaries(aonContext, domainId, Collections.singleton(salary));	
							} );
						}
						
					} catch ( Exception e ) {
						e.printStackTrace();
						System.err.println(e.getMessage());
					}
				});
		
		}));
		
		
	}
	
	public static void addCalcs( 
		AONContext aonContext,
		String login,
		String domainName, 
		Integer domainId,
		byte [] certificateData,
		String certificatePassword, 
		String certificateType, 
		Liquidacion liquidacion,
		String ...nafs) throws SegSocialException {
	
	
	
	java.sql.Date endDate = toSqlDate(liquidacion.getPeriodoHasta());
	java.sql.Date startDate = toSqlDate(liquidacion.getPeriodoDesde());
	java.sql.Date ctrlDate = toSqlDate(liquidacion.getFechaControl());
	
	String regimen = liquidacion.getCcc().getRegimen();
	String ccc = liquidacion.getCcc().getProvincia() + liquidacion.getCcc().getNumero();
	
	String authorized = getAuthorized(login, domainId, domainName, ccc);

	Map<String,List<Employee>> employees = getEmployees(login, domainId, domainName, ccc, startDate, endDate, nafs);
	
	nafs = employees.keySet().toArray(new String[employees.size()]);

	LiquidationType liquidationType = 
	Arrays.stream(LiquidationType.values())
	.filter(l -> AonStringUtils.equals(l.getValue(), liquidacion.getTipo()))
	.findAny().orElseThrow(NoSuchElementException::new);
	
	Map<String, Map<String, Map<Period, Map<String, Calc>>>> allCalcs = 
	SistemaRED.getCalcByNAF(
			certificateData, 
			certificatePassword, 
			certificateType, 
			ccc, 
			Regime.fromValue(regimen), 
			startDate, 
			endDate, 
			liquidationType, 
			LiquidationOrigin.TODAS, 
			authorized,
			nafs
			);
	
	
	
	allCalcs.forEach((liq, liqCalcs) -> liqCalcs.forEach(( naf, nafCalcs ) -> {
			
			employees.get(naf).forEach(employee -> {
				try {
					Period period = new Period(employee.getStartDate(), 
							employee.getEndDate().orElse(null));
					
					
					Salary salary = SLDSalaries.getSalary(liq, ccc, naf, nafCalcs, period);
					salary.setIssueDate(ctrlDate);
					salary.setEmployeeDocument(employee.getDni());
					
					
					Date l13startDate = null;
					if ( salary.getSalaryType() == SalaryType.L13 ) {
						l13startDate = salary.getStartDate();
						salary.setStartDate(employee.getStartDate());
						salary.setIssueDate(min(salary.getEndDate(), endDate));
					}
					
					employee.getName().ifPresent(salary::setEmployeeName);
					
					try {
						AON.saveSalaries(aonContext, domainId, Collections.singleton(salary));
					} catch ( Exception e ) {
						java.sql.Date settleEndDate = addDays(l13startDate, -1);
						Map<String,List<Employee>> oldEmployees = 
								getEmployees(login, domainId, domainName, ccc, settleEndDate, settleEndDate, p -> p.getNafProperty().eq(naf));
						oldEmployees.get(naf).forEach(oldEmployee -> {
							salary.setStartDate(oldEmployee.getStartDate());
							salary.setIssueDate(min(salary.getEndDate(), endDate));
							AON.saveSalaries(aonContext, domainId, Collections.singleton(salary));	
						} );
					}
					
				} catch ( Exception e ) {
					e.printStackTrace();
					System.err.println(e.getMessage());
				}
			});
	
	}));
	
	
}


	private static java.sql.Date addDays( java.util.Date date, int days ) {
		return new java.sql.Date(AonDateUtils.addDays(date, -1).getTime());		
	}

	private static void syncWithIdcs(
			String login,
			Integer userId,
			String domainName, 
			Integer domainId,
			byte certificateData[],
			String certificatePassword, 
			String certificateType, 
			String regimen, 
			String ccc, 
			java.sql.Date startDate, 
			java.sql.Date endDate,
			String ...nafs
			) throws SegSocialException {
		
			// TC2 : 
			// 130-230-330-430-530 
			// 150-250-350 
		    
			// 100-200-300
			// 109,209,309
			// 139,239,339
			// 189,289,389
		   
		
			Map<String,List<Employee>> employees = getEmployees(login, domainId, domainName, ccc, startDate, endDate, nafs);
			
			employees.forEach((naf, list) -> System.out.println(naf + " :" + list.stream().map(e ->e.getName().orElse("") + "," + e.getContractType()).collect(Collectors.joining(","))) );
			
			// TODO : all employees
			employees.keySet().forEach(naf -> syncWithIdcs(login, domainName, domainId, userId, regimen, ccc, naf, endDate));
			
	}
	
	private static String getAuthorized(String login, Integer domainId, String domainName, String ccc) {
		ApplicationParameter payAuthorizationKeyPay = 
		AON.getApplicationParameter(domainName, domainId, login, AppParam.PAY_authorization_key_PAY);
		String authorized = payAuthorizationKeyPay.getValue();
		if ( AonStringUtils.isBlank(authorized)) { 
		    Domain domain = AON.getDomain(domainName, domainId, login);
		    payAuthorizationKeyPay = AON.getApplicationParameter(domain.getName(), domain.getParentId(), login,
			    AppParam.PAY_authorization_key_PAY);
		    authorized = payAuthorizationKeyPay.getValue();
		}
		return authorized;
	}
	
	private static List<Liquidacion> getLiquidations(String login, Integer domainId, String domainName, String ccc,
		java.sql.Date startDate, LiquidationType liquidationType, String [] nafs) {
	    
	    
	    RegistryAttachmentType type = RegistryAttachmentType.CRETA_RESPUESTA;
	    
	    Domain domain = AON.getDomain(domainName, domainId, login);
	    
	    Stream<Attach> respuestas = 
	    AON.getAttachStream(
		    domain.getName(), 
		    domain.getId(), 
		    login, 
		    p -> 
		    	p.getDomainProperty().in(new Integer[]{domain.getId(), domain.getParentId()})
			.and(p.getTypeProperty().eq((byte)type.ordinal()))
			.and(p.getAttachDateProperty().ge(startDate))
			.and(p.getDataProperty().like(("%"+ccc.substring(2)+"%"+liquidationType.getValue()+"%" ).getBytes())),
		    AttachType.REGISTRY, 
		    true);

	    Stream<Liquidacion> l03 = respuestas
	    //.peek(attach -> System.out.println(new String(attach.getData())))
	    .map(attach -> unmarshall(Respuesta.class, attach.getData()))
	    .filter(Optional::isPresent).map(Optional::get).map( Respuesta::getLiquidacion)
	    .flatMap(List::stream).filter(liquidacion -> toDate(liquidacion.getFechaControl()).compareTo(startDate) >= 0 )
	    ;
	    
	    return l03.toList();
	}
	
	private static <T> Optional<T> unmarshall( Class<T> clazz, byte [] data ) {
	    try ( InputStream is = new ByteArrayInputStream(data) ){
		return Optional.of(Utils.unmarshal(clazz, is));
	    } catch ( Exception e ) {
		return Optional.empty();
	    }
	}
	
	private static Date toDate(Periodo periodo) {
	    Calendar calendar = Calendar.getInstance();
	    
	    calendar.set(Calendar.MILLISECOND, 0 );
	    calendar.set(Calendar.SECOND, 0 );
	    calendar.set(Calendar.MINUTE, 0 );
	    calendar.set(Calendar.HOUR_OF_DAY, 0 );

	    calendar.set(Calendar.DAY_OF_MONTH, 1 );
	    calendar.set(Calendar.YEAR, Integer.parseInt(periodo.getAnho()));
	    calendar.set(Calendar.MONTH, Integer.parseInt(periodo.getMes())-1);
	    
	    return calendar.getTime();
	}

	private static java.sql.Date toSqlDate(Periodo periodo) {
	    return new java.sql.Date(toDate(periodo).getTime());
	}

	private static Map<String,List<Employee>> getEmployees(String login, Integer domainId, String domainName, String ccc,
			java.sql.Date firstDayOfMonth, java.sql.Date lastDayOfMonth, String ...nafs) {
		if ( nafs == null || nafs.length == 0 )
			return 
					PAYROLL.getEmployees(domainName, 
					domainId, 
					login, 
					p -> p.getDomainProperty().eq(domainId) 
					.and(p.getCCCProperty().eq(ccc))
					.and(p.getStartDateProperty().le(lastDayOfMonth))
					.and(p.getEndDateProperty().isNull().or(p.getEndDateProperty().ge(firstDayOfMonth))))
					.collect(Collectors.toMap(Employee::getNaf, Collections::singletonList, (l1,l2) -> Stream.concat(l1.stream(), l2.stream()).collect(Collectors.toList())))
					;
		
		return 
		PAYROLL.getEmployees(domainName, 
		domainId, 
		login, 
		p -> p.getDomainProperty().eq(domainId) 
		.and(p.getCCCProperty().eq(ccc))
		.and(p.getNafProperty().in(nafs))
		.and(p.getStartDateProperty().le(lastDayOfMonth))
		.and(p.getEndDateProperty().isNull().or(p.getEndDateProperty().ge(firstDayOfMonth))))
		.collect(Collectors.toMap(Employee::getNaf, Collections::singletonList, (l1,l2) -> Stream.concat(l1.stream(), l2.stream()).collect(Collectors.toList())))
		;
	}

	private static Map<String,List<Employee>> getEmployees(String login, Integer domainId, String domainName, String ccc,
			java.sql.Date firstDayOfMonth, java.sql.Date lastDayOfMonth, EmployeeFilter filter) {
		return 
		PAYROLL.getEmployees(domainName, 
		domainId, 
		login, 
		p -> p.getDomainProperty().eq(domainId) 
		.and(p.getCCCProperty().eq(ccc))
		.and(p.getStartDateProperty().le(lastDayOfMonth))
		.and(p.getEndDateProperty().isNull().or(p.getEndDateProperty().ge(firstDayOfMonth)))
		.and(filter.filter(p))
		)
		.collect(Collectors.toMap(e -> e.getNaf(), e -> Collections.singletonList(e), (l1,l2) -> List.of(l1.get(0), l2.get(0))))
		;
	}
	
	
	private static DeductionType getDeductionType(ContextVariable var) {
		switch (var) {
		case FP_EMPLOYEE:
		case FP_ENTERPRISE:
			return DeductionType.JOB_TRAINING;
		case UNEMPLOY_EMPLOYEE:
		case UNEMPLOY_ENTERPRISE:
			return DeductionType.UNEMPLOYMENT;
		case CGC_EMPLOYEE:
		case CGC_ENTERPRISE:
			return DeductionType.COMMON_CONTINGENCY;
		case IT_ENTERPRISE:
			return DeductionType.IT;
		case IMS_ENTERPRISE:
			return DeductionType.IMS;
		case FOGASA_ENTERPRISE:
			return DeductionType.FOGASA;
		default:
			return DeductionType.BONUS;
		}
	}

	private static DeductionType getDeductionType(String name) {
		ContextVariable var = ContextVariable.getVariableByName(name);
		if ( var != null )
			return getDeductionType(var );
		
		DeductionType type = DeductionType.valueOf(name); 
		if ( type != null ) 
			return type;

		return DeductionType.BONUS;
	}


	public static void syncWithIdc(byte data[], String userLogin, String domainName, Integer domainId, Date date, String ccc, String  naf) throws IOException, UnknownPDFException {
	
		com.esferalia.aon.in.payroll.tgss.idc.Idc.parse(data, new com.esferalia.aon.in.payroll.tgss.idc.Idc.IdcListener() {
			
			
			@Override
			public void onSSPECs(Date startDate, Date endDate, Collection<PEC> ssPECs) {
				addPECs(ssPECs, userLogin, domainName, domainId, startDate, endDate, ccc, naf);
			}
			
			@Override
			public void onContractData(Date startDate, Date endDate, Map<ContextVariable, Object> contractData) {
				addData(contractData, userLogin, domainName, domainId, startDate, endDate, ccc, naf);
			}
		});
	}

	public static void syncWithIdc(String userLogin, String domainName, Integer domainId, Integer userId, Date date, String regime,
			String ccc, String  naf) {
		
		try {
	
//			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId);
			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");
			
			byte data [] = SistemaRED.getIDC(certificate.getData(), certificate.getPassword(), certificate.getType(), regime, ccc, naf, date);
			
			syncWithIdc(data, userLogin, domainName, domainId, date, ccc, naf);
			
		} catch ( Throwable e ) {
			e.printStackTrace();
		}
	}

	public static void addPECs(String userLogin, String domainName, Integer domainId, Integer userId, Date date, String regime,
			String ccc, String  naf) {
		
		try {
	
//			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId);
			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");
			
			byte data [] = SistemaRED.getIDC(certificate.getData(), certificate.getPassword(), certificate.getType(), regime, ccc, naf, date);
			com.esferalia.aon.in.payroll.tgss.idc.Idc.parse(data, new IdcListener() {
				
				@Override
				public void onSSPECs(Date startDate, Date endDate, Collection<PEC> SSPecs) {
					addPECs(SSPecs, userLogin, domainName, domainId, startDate, endDate, ccc, naf);
					
				}
				
				@Override
				public void onContractData(Date startDate, Date endDate, Map<ContextVariable, Object> contractData) {
				}
			});

			
		} catch ( Throwable e ) {
			e.printStackTrace();
		}
	}
	
	public static void addPECs(Collection<com.esferalia.aon.in.payroll.tgss.idc.PEC> pecs, String userLogin, String domainName, Integer domainId, Date start, Date end, 
			String ccc, String  naf) {
		
		if(pecs.isEmpty()) return;
		
		Bonus bonuses [] =
		pecs.stream()
		.filter(b -> AonStringUtils.equals(b.getSsNum(), naf))
		.filter(pec -> PEC.isBonus(pec) )
		.map( b -> 
		new Bonus()
		.setExpression(b.getFormula())
		.setDescription(b.getDescription())
		.setType(BonusType.SOCIAL_SECURITY)
		.setStartDate(b.getStartDate())
		.setEndDate(b.getEndDate())
		)
		.toArray(Bonus[]::new)
		;
		
		Date startDate = Arrays.stream(bonuses).map(b -> b.getStartDate()).reduce(start, (d1,d2) -> min(d1,d2));
		Date endDate = Arrays.stream(bonuses).map(b -> b.getEndDate()).reduce(end, (d1,d2) -> max(d1,d2));
		

		PAYROLL.setBonuses(domainName, domainId, userLogin, ccc, naf, startDate, endDate, bonuses);					
		
		Deduction deductions [] =
		pecs.stream()
		.filter(b -> AonStringUtils.equals(b.getSsNum(), naf))
		.filter(pec -> PEC.isDeduction(pec))
		.map( d -> 
		new Deduction()
		.setName(d.getName())
		.setExpression(d.getFormula())
		.setDescription(d.getDescription())
		.setStartDate(d.getStartDate())
		.setEndDate(d.getEndDate())
		.setType(getDeductionType(d.getName()))
		)
		.toArray(Deduction[]::new)
		;
		
		startDate = Arrays.stream(deductions).map(b -> b.getStartDate()).reduce(start, (d1,d2) -> max(d1,d2));
		endDate = Arrays.stream(deductions).map(b -> b.getEndDate()).reduce(end, (d1,d2) -> min(d1,d2));

		PAYROLL.setDeductions(domainName, domainId, userLogin, ccc, naf, startDate, endDate, deductions);
		
		Cost costs [] =
		pecs.stream()
		.filter(b -> AonStringUtils.equals(b.getSsNum(), naf))
		.filter(pec -> PEC.isCost(pec))
		.map( c -> 
		new Cost()
		.setName(c.getName())
		.setExpression(c.getFormula())
		.setDescription(c.getDescription())
		.setStartDate(c.getStartDate())
		.setEndDate(c.getEndDate())
		.setType(getDeductionType(c.getName()))
		)
		.toArray(Cost[]::new)
		;
		
		startDate = Arrays.stream(costs).map(b -> b.getStartDate()).reduce(start, (d1,d2) -> max(d1,d2));
		endDate = Arrays.stream(costs).map(b -> b.getEndDate()).reduce(end, (d1,d2) -> min(d1,d2));

		PAYROLL.setCosts(domainName, domainId, userLogin, ccc, naf, startDate, endDate, costs);

	}
	
	public static void addData(
	Map<ContextVariable, Object> data, 
	String userLogin, 
	String domainName, 
	Integer domainId, 
	Date startDate, 
	Date endDate, 
	String ccc, 
	String  naf) {
		
		ContractData contractDatas [] =
		data.entrySet().stream()
		.map( e ->
			new ContractData()
			.setEndDate(endDate)
			.setStartDate(startDate)
			.setName(e.getKey().getName())
			.setExpression(toString(e.getValue()))
		)
		.toArray(ContractData[]::new)
		;
		
		PAYROLL.setData(
		domainName, 
		domainId, 
		userLogin, 
		ccc,
		naf,
		startDate,
		endDate,
		contractDatas);					

	}

	public static void addBonus(String userLogin, String domainName, Integer domainId, Integer userId, String regime,
			String ccc, String naf, Date endDate) {
		Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");
		try {
			Collection<solutions.aon.seg.social.object.Idc> idcs = 
			SistemaRED.getIDCDates(certificate.getData(), certificate.getPassword(), certificate.getType(), regime, ccc, naf);
			
			addBonus(userLogin, domainName, domainId, userId, regime, ccc, naf, endDate, idcs);
		} catch (SegSocialException e) {
			e.printStackTrace();
		}
	}
	
	public static void addBonus(String userLogin, String domainName, Integer domainId, Integer userId, String regime,
			String ccc, String naf, Date endDate, Collection<solutions.aon.seg.social.object.Idc> idcs) {
		
		System.out.println("-------ADD_BONUS "+naf+" -------");
		
		Date idcDates [] = 
		idcs.stream()
		.filter(idc -> AonStringUtils.equals("ALTA", idc.getDescripcion()))
		.filter(idc -> endDate == null || idc.getFecha().compareTo(endDate) <= 0 )
		.map(idc ->idc.getFecha()).sorted()
		.toArray(Date[]::new);
		
		if ( idcDates.length == 0 )
			return;
		
		Date firstDayOfMonth = AonDateUtils.getFirstDayOfMonth(endDate == null ? new Date() : endDate);
		Date ssStartDate = AonDateUtils.add(firstDayOfMonth, Calendar.MONTH, -1);

		for ( int i = 1; i < idcDates.length ; i++ ) {
			Date start = idcDates[i-1];
			Date end = AonDateUtils.add(idcDates[i], Calendar.DAY_OF_MONTH,-1);
			if ( end.before(ssStartDate)) 
				continue;
			addPECs(userLogin, domainName, domainId, userId, start, regime, ccc, naf);
		}
		
		Date last = idcDates[idcDates.length-1];
		System.out.println("\tIDC : " + last );
		addPECs(userLogin, domainName, domainId, userId, last, regime, ccc, naf);
		
		//.peek( d -> System.out.println("IDC : " + d ))
		//.reduce( (d1,d2) -> d2 )
		//.filter( d -> true )
		//.ifPresent( date -> addPECs(userLogin, domainName, domainId, userId, date, regime, ccc, naf));			

		System.out.println("\tSUCCESS: " + naf );
	}
	
	public static void syncWithIdcs(String userLogin, String domainName, Integer domainId, Integer userId, String regime,
			String ccc, String naf, Date startDate, Date endDate) throws IllegalArgumentException {
	
		Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");
		try {
			solutions.aon.seg.social.object.Idc [] idcs = 
			SistemaRED.getIDCDates(certificate.getData(), certificate.getPassword(), certificate.getType(), regime, ccc, naf)
			.stream()
			.sorted((idc1, idc2) -> AonDateUtils.compare(idc1.getFecha(), idc2.getFecha()))
			.toArray(solutions.aon.seg.social.object.Idc[]::new);
			
			List<com.esferalia.aon.salary.expression.Period> periods = new ArrayList<>();
			for (int i = 1; i < idcs.length; i+=2) {
				periods.add(new com.esferalia.aon.salary.expression.Period(idcs[i-1].getFecha(), idcs[i].getFecha()));
			}
			if ( idcs.length % 2 != 0 ) {
				periods.add(new com.esferalia.aon.salary.expression.Period(idcs[idcs.length -1 ].getFecha(), null));
			}
			
			com.esferalia.aon.salary.expression.Period period = 
			new com.esferalia.aon.salary.expression.Period(startDate, endDate);
			
			Date idcDates [] = 
			periods.stream()
			.filter(p -> period.intersects(p) )
			.map(p -> p.getStart() )
			.sorted().distinct()
			.toArray(Date[]::new);
			
			if ( idcDates.length == 0 )
				return;
			
			for ( int i = 0; i < idcDates.length ; i++ ) {
				Date date = idcDates[i];
				syncWithIdc(userLogin, domainName, domainId, userId, date, regime, ccc, naf);
				System.out.println("IDC : " + date );
			}
			
			System.out.println("\tSUCCESS: " + naf );
		
		} catch (SegSocialException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	public static void syncWithIdcs(String userLogin, String domainName, Integer domainId, Integer userId, String regime,
			String ccc, String naf, Date endDate) throws IllegalArgumentException {
	
//		Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId);
		Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");
		try {
			Collection<solutions.aon.seg.social.object.Idc> idcs = 
			SistemaRED.getIDCDates(certificate.getData(), certificate.getPassword(), certificate.getType(), regime, ccc, naf);
			
			Date idcDates [] = 
			idcs.stream()
			.filter(idc -> AonStringUtils.equals("ALTA", idc.getDescripcion()))
			.filter(idc -> endDate == null || idc.getFecha().compareTo(endDate) <= 0 )
			.map(idc ->idc.getFecha()).sorted()
			.toArray(Date[]::new);
			
			if ( idcDates.length == 0 )
				return;
			
			Date firstDayOfMonth = AonDateUtils.getFirstDayOfMonth(endDate == null ? new Date() : endDate);
			Date ssStartDate = AonDateUtils.add(firstDayOfMonth, Calendar.MONTH, -1);

			for ( int i = 1; i < idcDates.length ; i++ ) {
				Date start = idcDates[i-1];
				Date end = AonDateUtils.add(idcDates[i], Calendar.DAY_OF_MONTH,-1);
				if ( end.before(ssStartDate)) 
					continue;
				syncWithIdc(userLogin, domainName, domainId, userId, start, regime, ccc, naf);
			}
			
			Date last = idcDates[idcDates.length-1];
			System.out.println("IDC : " + last );
			syncWithIdc(userLogin, domainName, domainId, userId, last, regime, ccc, naf);
			
			//.peek( d -> System.out.println("IDC : " + d ))
			//.reduce( (d1,d2) -> d2 )
			//.filter( d -> true )
			//.ifPresent( date -> addPECs(userLogin, domainName, domainId, userId, date, regime, ccc, naf));			

			System.out.println("\tSUCCESS: " + naf );
		
		} catch (SegSocialException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	/**
	 * 
	 * @param userLogin
	 * @param userId
	 * @param domainName
	 * @param domainId
	 * @param regime
	 * @param ccc
	 * @param naf
	 * @param date if the date is empty, look for the most recent.
	 * @return
	 * @throws SegSocialException
	 * @throws UnknownPDFException
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public static Employee getEmployeeToIDC(String userLogin, Integer userId, String domainName, Integer domainId, String regime,
			String ccc, String naf, Optional<Date> date) throws SegSocialException, UnknownPDFException, IllegalArgumentException, IOException {
		Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");
		
		Date startDate = date.orElseGet(()->{
			try {
				return SistemaRED.getIDCDates(certificate.getData(), certificate.getPassword(), certificate.getType(), regime, ccc, naf)
				.stream()
				.filter(d-> d.getDescripcion().equals("ALTA"))
				.sorted((o1, o2) -> o2.getFecha().compareTo(o1.getFecha()))
				.findFirst()
				.get()
				.getFecha();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		});

        return EmployeeParse.IdcToEmployeeOccam(
    		ServicioRED.getIDCPOST(
				new ByteArrayInputStream(certificate.getData()), certificate.getPassword(), certificate.getType(), 
                naf, regime, ccc, startDate
            )
        );
	}

	private static String toString(Object obj) {
		if ( obj == null )
			return null;
		if ( obj instanceof String) {
			return String.format("\"%s\"", obj);
		}else if ( obj instanceof Integer) {
			return Integer.toString((Integer) obj);
		}else if ( obj instanceof Number) {
			return Double.toString((Double) obj) ;
		}
		return obj.toString();
	}
	
}
