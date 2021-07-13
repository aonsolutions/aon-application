package com.esferalia.aon.in.payroll;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.DataAttach.DATA_ATTACH;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Raddinfo.RADDINFO;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.User.USER;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;
import static com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType.DIGITAL_CERTIFICATE;
import static com.esferalia.aon.salary.expression.Period.max;
import static com.esferalia.aon.salary.expression.Period.min;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

import com.esferalia.aon.in.payroll.tgss.idc.PEC;
import com.esferalia.aon.in.payroll.tgss.sld.SLDSalaries;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.model.Bonus;
import com.esferalia.aon.occam.api.model.Deduction;
import com.esferalia.aon.occam.api.model.Filter.EmployeeFilter;
import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.DataAttachType;
import com.esferalia.aon.occam.api.model.payroll.Employee;
import com.esferalia.aon.occam.api.model.security.Certificate;
import com.esferalia.aon.occam.api.model.type.BonusType;
import com.esferalia.aon.occam.api.model.type.DeductionType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.SalaryType;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.core.pool.AonConnectionException;
import net.aonsolutions.core.pool.AonDataSource;
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
		
		Option bonusOption = Option.builder()
		.longOpt("bonus")
		.desc("Adds Bonuses from PECs.")
		.build();

		Option calcsOption = Option.builder()
		.longOpt("calcs")
		.desc("Adds Calculations from SLD.")
		.build();

		Options options = new Options();
		options.addOption(helpOption);
		options.addOption(dateOption);
		options.addOption(whereOption);
		options.addOption(databasesOption);
		options.addOption(bonusOption);
		options.addOption(calcsOption);
	
		
		CommandLine commandLine = null;
		
		try {
			CommandLineParser parser = new DefaultParser();
			commandLine = parser.parse(options, args);
			
			Date firstDayOfMonth = AonDateUtils.getFirstDayOfMonth(dateFormat.parse(commandLine.getOptionValue(dateOption.getLongOpt(), dateFormat.format(new Date()))));
			
			java.sql.Date date = new java.sql.Date(firstDayOfMonth.getTime());
			
			Condition condition = DSL.condition(commandLine.getOptionValue(whereOption.getLongOpt(), "1=1"));
			
			boolean calcs = commandLine.hasOption(calcsOption.getLongOpt());
			boolean bonus = commandLine.hasOption(bonusOption.getLongOpt());
			
			
			for( String schema : commandLine.getOptionValues(databasesOption.getLongOpt()) ) {
				System.out.println(schema);;
				try (Connection connection = AonDataSource.getInstance().getDatabaseConnection(schema);
					AONContext aonContext = new AONContext(connection);
					DSLContext dslContext = aonContext.getDslContext() ) {
					
					dslContext
					.select()
					.from(DOMAIN)
					.innerJoin(CONTRACT).on(DOMAIN.ID.eq(CONTRACT.DOMAIN))
					.innerJoin(ENTERPRISE_CCC).on(DOMAIN.ID.eq(ENTERPRISE_CCC.DOMAIN))
					
					.leftJoin(USER).on(DOMAIN.PARENT.eq(USER.DOMAIN))
					.leftJoin(REGISTRY).on(USER.REGISTRY.eq(REGISTRY.ID))
					.leftJoin(RATTACH).on(REGISTRY.ID.eq(RATTACH.REGISTRY), RATTACH.TYPE.eq(DIGITAL_CERTIFICATE.value()) )
					.leftJoin(RADDINFO).on(REGISTRY.ID.eq(RADDINFO.REGISTRY), RADDINFO.ATTRIBUTE.eq("DIGITAL_CERTIFICATE_PASSWORD"))
					.leftJoin(USER_SCOPE).on(USER.ID.eq(USER_SCOPE.USER_ID).and(DOMAIN.SCOPE.eq(USER_SCOPE.SCOPE)))
					
					.leftJoin(DATA_ATTACH).on(DOMAIN.PARENT.eq(DATA_ATTACH.DOMAIN), DATA_ATTACH.SOURCE.eq(DataAttachSource.SISTEMA_RED.value()), DATA_ATTACH.TYPE.eq(DataAttachType.DIGITAL_CERTIFICATE.value()) )

					.where(condition)
					.and(RATTACH.DATA.isNotNull().or(DATA_ATTACH.DATA.isNotNull()))
					.and(CONTRACT.START_DATE.le(date).and(CONTRACT.END_DATE.isNull().or(CONTRACT.END_DATE.ge(date))))
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
						
						byte rattachData [] = domain.get(RATTACH.DATA);
						byte dataAttachData [] = domain.get(DATA_ATTACH.DATA);
						String raddInfoPassword = domain.get(RADDINFO.VALUE);
						String dataAttachPassword = domain.get(DATA_ATTACH.DESCRIPTION);
						
						String certificateType = MimeType.PKCS12.name();
						byte certificateData [] =  Optional.ofNullable(dataAttachData).orElse(rattachData);
						String certificatePassword =  Optional.ofNullable(dataAttachPassword).orElse(raddInfoPassword);
						
						java.sql.Date startDate = AonDateUtils.getFirstDayOfMonth(date);
						java.sql.Date endDate = AonDateUtils.getLastDayOfMonth(date);
						
						try {
							if ( calcs )
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
										endDate);
							if ( bonus )
								addBonus(
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
										endDate);
							
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
			byte certificateData[],
			String certificatePassword, 
			String certificateType, 
			String regimen, 
			String ccc, 
			java.sql.Date startDate, 
			java.sql.Date endDate ) throws SegSocialException {
		
		Map<String,List<Employee>> employees = getEmployees(login, domainId, domainName, ccc, startDate, endDate);
		
		String nafs [] = employees.keySet().toArray(new String[employees.size()]);
		
		
		Map<String, Map<String, Map<Period, Map<String, Calc>>>> allCalcs = 
		SistemaRED.getCalcByNAF(
				certificateData, 
				certificatePassword, 
				certificateType, 
				ccc, 
				Regime.fromValue(regimen), 
				startDate, 
				endDate, 
				LiquidationType.TODAS, 
				LiquidationOrigin.TODAS, 
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
						
						employee.getName().ifPresent(name -> salary.setEmployeeName(name) );
						
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

	private static void addBonus(
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
			java.sql.Date endDate
			) throws SegSocialException {
		
			// TC2 : 
			// 130-230-330-430-530 
			// 150-250-350 
		    
			// 100-200-300
			// 109,209,309
			// 139,239,339
			// 189,289,389
		   
		
			Map<String,List<Employee>> employees = getEmployees(login, domainId, domainName, ccc, startDate, endDate);
			
			employees.forEach((naf, list) -> System.out.println(naf + " :" + list.stream().map(e ->e.getName().orElse("") + "," + e.getContractType()).collect(Collectors.joining(","))) );
			
			// TODO : all employees
			employees.keySet().forEach(naf -> addBonus(login, domainName, domainId, userId, regimen, ccc, naf, null));
			
	}
	
	private static Map<String,List<Employee>> getEmployees(String login, Integer domainId, String domainName, String ccc,
			java.sql.Date firstDayOfMonth, java.sql.Date lastDayOfMonth) {
		return 
		PAYROLL.getEmployees(domainName, 
		domainId, 
		login, 
		p -> p.getDomainProperty().eq(domainId) 
		.and(p.getCCCProperty().eq(ccc))
		.and(p.getStartDateProperty().le(lastDayOfMonth))
		.and(p.getEndDateProperty().isNull().or(p.getEndDateProperty().ge(firstDayOfMonth)))
		)
		.collect(Collectors.toMap(e -> e.getNaf(), e -> Collections.singletonList(e), (l1,l2) -> List.of(l1.get(0), l2.get(0))))
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



	public static void addPECs(String userLogin, String domainName, Integer domainId, Integer userId, Date date, String regime,
			String ccc, String  naf) {
		
		try {
	
			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId);
			
			byte data [] = SistemaRED.getIDC(certificate.getCertificate(), certificate.getPassword(), certificate.getType(), regime, ccc, naf, date);
			Collection<com.esferalia.aon.in.payroll.tgss.idc.PEC> ssBonus = com.esferalia.aon.in.payroll.tgss.idc.Idc.getSSPECs(data);
			Bonus bonuses [] =
			ssBonus.stream()
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
			
			Date startDate = Arrays.stream(bonuses).map(b -> b.getStartDate()).reduce(date, (d1,d2) -> min(d1,d2));
			Date endDate = Arrays.stream(bonuses).map(b -> b.getEndDate()).reduce(date, (d1,d2) -> max(d1,d2));
			
	
			PAYROLL.setBonuses(domainName, domainId, userLogin, ccc, naf, startDate, endDate, bonuses);					
			
			Deduction deductions [] =
			ssBonus.stream()
			.filter(b -> AonStringUtils.equals(b.getSsNum(), naf))
			.filter(pec -> PEC.isDeduction(pec))
			.map( b -> 
			new Deduction()
			.setName(b.getName())
			.setExpression(b.getFormula())
			.setDescription(b.getDescription())
			.setStartDate(b.getStartDate())
			.setEndDate(b.getEndDate())
			.setType(DeductionType.BONUS)
			)
			.toArray(Deduction[]::new)
			;
			
			PAYROLL.setDeductions(domainName, domainId, userLogin, ccc, naf, startDate, endDate, deductions);
			
		} catch ( Throwable e ) {
			e.printStackTrace();
		}
	}



	public static void addBonus(String userLogin, String domainName, Integer domainId, Integer userId, String regime,
			String ccc, String naf, Date endDate) {
	
		Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId);
		try {
			Collection<solutions.aon.seg.social.object.Idc> idcDates = 
			SistemaRED.getIDCDates(certificate.getCertificate(), certificate.getPassword(), certificate.getType(), regime, ccc, naf);
			
			idcDates.stream()
			.filter(idc -> AonStringUtils.equals("ALTA", idc.getDescripcion()))
			.filter(idc -> endDate == null || idc.getFecha().compareTo(endDate) <= 0 )
			.map(idc ->idc.getFecha()).sorted().reduce( (d1,d2) -> d2 )
			.ifPresent( date -> addPECs(userLogin, domainName, domainId, userId, date, regime, ccc, naf));			
		
		} catch (SegSocialException e) {
			
		}
	}
	
}
