package com.esferalia.aon.in.payroll.pdf;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryBonus.SALARY_BONUS;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static com.esferalia.aon.watson.util.AonNumberUtils.zeroIfNull;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.DSLContext;

import com.code.aon.person.Person;
import com.esferalia.aon.in.payroll.pdf.maker.PdfMaker;
import com.esferalia.aon.in.payroll.pdf.maker.enterprisepayroll.beans.EnterprisePayroll;
import com.esferalia.aon.in.payroll.pdf.maker.enterprisepayroll.beans.EnterprisePayrollEntry;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.jooq.tables.Enterprise;
import com.esferalia.aon.jooq.tables.Salary;
import com.esferalia.aon.jooq.tables.records.EnterpriseRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.type.DeductionType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.enumeration.SalaryTypeVisitor;
import com.esferalia.aon.watson.util.AonDateUtils;

public class JooqEnterpriseSalaryBuilder {
	
	public static Byte [] LIQUIDATIONS = { 
			(byte) com.esferalia.aon.occam.api.model.type.SalaryType.L00.ordinal(),
			(byte) com.esferalia.aon.occam.api.model.type.SalaryType.L13.ordinal(),
			(byte) com.esferalia.aon.occam.api.model.type.SalaryType.L03.ordinal()
		};
	
	/**
	 * Method to generate the PDF enterprise payroll and place it into the OutputStream passed as parameter
	 * @param outputStream The OutputStream which will contain the pdf
	 * @param domain The domain name
	 * @param startDate The start date of the enterprise payroll
	 * @param endDate The end date of the enterprise payroll
	 * @param enterpriseId
	 * @param workplaceId
	 * @param types Salary types that will be displayed
	 */
	public static void generateEnterprisePayroll (OutputStream outputStream, String domain, String user, Date startDate, Date endDate, int enterpriseId, Integer workplaceId, com.esferalia.aon.occam.api.model.type.SalaryType types[]) {
		generateEnterprisePayroll(outputStream, domain, null, user, startDate, endDate, enterpriseId, workplaceId, types);
	}
	
	public static void generateEnterprisePayroll (OutputStream outputStream, String domain, Integer domainId, String user, Date startDate, Date endDate, int enterpriseId, Integer workplaceId, com.esferalia.aon.occam.api.model.type.SalaryType types[]) {
		Condition condition;
		if (workplaceId != null && workplaceId != 0)
			condition = WORKPLACE.ID.eq(workplaceId);
		else
			condition = ENTERPRISE.REGISTRY.eq(enterpriseId);
		condition = condition.and(SALARY.ISSUE_DATE.between(new java.sql.Date(startDate.getTime())
				, new java.sql.Date(endDate.getTime())));
		
		Collection<Integer> typeInts = Arrays.stream(types).map(t -> t.ordinal()).collect(Collectors.toList());
		
		condition = condition.and(SALARY.TYPE.in(typeInts));
		
		generateEnterprisePayroll(outputStream, domain, domainId, user, condition, startDate, enterpriseId, workplaceId);
	}
	
	public static void generateEnterprisePayrollByEmployee (OutputStream outputStream, String domain, Integer domainId, String user, Date startDate, Date endDate, int enterpriseId, Integer workplaceId, com.esferalia.aon.occam.api.model.type.SalaryType types[], Person ...persons) {
		Condition condition;
		if (workplaceId != null && workplaceId != 0)
			condition = WORKPLACE.ID.eq(workplaceId);
		else
			condition = ENTERPRISE.REGISTRY.eq(enterpriseId);
		condition = condition.and(SALARY.ISSUE_DATE.between(new java.sql.Date(startDate.getTime())
				, new java.sql.Date(endDate.getTime())));
		
		
		if (persons != null && persons.length > 0) {
			Set<String> ssNumbers = new LinkedHashSet<>();
			for (Person person : persons) {
				if (person != null && person.getSocialSecurityNumber() != null && !person.getSocialSecurityNumber().isEmpty()) {
					ssNumbers.add(person.getSocialSecurityNumber());
				}
			}
			
			if (!ssNumbers.isEmpty()) {
				condition = condition.and(SALARY.SOCIAL_SECURITY_NUMBER.in(ssNumbers));
			}
			
		}
		
		Collection<Integer> typeInts = Arrays.stream(types)
				.map(com.esferalia.aon.occam.api.model.type.SalaryType::ordinal)
				.collect(Collectors.toList());
		
		condition = condition.and(SALARY.TYPE.in(typeInts));
		
		try (AONContext aonContext = AONContext.getAONContext(domain, user)) {
			DSLContext ctx = aonContext.getDslContext();
			
			
			Byte[] enumBytes = new Byte[types != null ? types.length : 0];
			int index = 0;
			for (com.esferalia.aon.occam.api.model.type.SalaryType type : types) {
				enumBytes[index++] = type.value();
			}
			
			Map<String, Map<String, EnterprisePayrollEntry>> payrolls = 
					getEnterprisePayrollsByEmployee(ctx, condition.and(SALARY.TYPE.in(enumBytes)));
			
			String enterpriseName = "";
			if (enterpriseId <= 0) {
				if (workplaceId != null && workplaceId > 0) {
					enterpriseId = AON.getWorkplace(aonContext.getDomainName()
							, aonContext.getDomainId()
							, aonContext.getUser()
							, d -> d.getIdProperty().eq(workplaceId)).getEnterprise();
				} else {
					Optional<EnterpriseRecord> optEnterprise = ctx.select(ENTERPRISE.REGISTRY).from(ENTERPRISE).where(ENTERPRISE.DOMAIN.eq(domainId)).fetchStreamInto(ENTERPRISE).filter(Objects::nonNull).findFirst();
					if (optEnterprise.isPresent()) {
						enterpriseId = optEnterprise.get().getRegistry();
					}
				}
			}
			
			try {				
				AtomicInteger entId = new AtomicInteger(enterpriseId);
				enterpriseName = AON.getRegistry(aonContext.getDomainName(), aonContext.getDomainId(), user, r -> r.getIdProperty().eq(entId.get())).getName();
			} catch (Exception e) {
				enterpriseName = "";
			}
			
			
			
			Attach attach1 = AON.getAttach(
					aonContext.getDomainName()
					, aonContext.getDomainId()
					, aonContext.getUser(),
					f -> f.getTypeProperty().eq(RegistryAttachmentType.LOGO.value())
							.and(f.getDomainProperty().eq(aonContext.getDomainId()))
					, AttachType.REGISTRY
					);
			
			byte[] byteLogo = attach1.getData();
			
			InputStream logo = null;
			
			try {
				logo = new ByteArrayInputStream(byteLogo);
			} catch (NullPointerException e) {
			}
			String subheader = "Empresa: ";
			if (enterpriseName != null) {
				subheader = subheader.concat(enterpriseName);
			}
			
			EnterprisePayroll enterprisePayroll = new EnterprisePayroll(logo, startDate, null, subheader, payrolls, null);
			PdfMaker.printEnterprisePayroll(enterprisePayroll, outputStream, Optional.of(new Locale("es")), startDate, endDate);
		} catch (CanNotCreatePdfException e) {			
		} catch (IOException e) {}
		
		
	}
	
	public static void generateEnterprisePayrollByPeriod (OutputStream outputStream, String domain, Integer domainId, String user, Date startDate, Date endDate, int enterpriseId, Integer workplaceId, com.esferalia.aon.occam.api.model.type.SalaryType types[], Person ...persons) {
		Condition condition;
		if (workplaceId != null && workplaceId != 0)
			condition = WORKPLACE.ID.eq(workplaceId);
		else
			condition = ENTERPRISE.REGISTRY.eq(enterpriseId);
		condition = condition.and(SALARY.ISSUE_DATE.between(new java.sql.Date(startDate.getTime())
				, new java.sql.Date(endDate.getTime())));
		
		
		if (persons != null && persons.length > 0) {
			Set<String> ssNumbers = new LinkedHashSet<>();
			for (Person person : persons) {
				if (person != null && person.getSocialSecurityNumber() != null && !person.getSocialSecurityNumber().isEmpty()) {
					ssNumbers.add(person.getSocialSecurityNumber());
				}
			}
			
			if (!ssNumbers.isEmpty()) {
				condition = condition.and(SALARY.SOCIAL_SECURITY_NUMBER.in(ssNumbers));
			}
			
		}
		
		Collection<Integer> typeInts = Arrays.stream(types)
				.map(com.esferalia.aon.occam.api.model.type.SalaryType::ordinal)
				.collect(Collectors.toList());
		
		condition = condition.and(SALARY.TYPE.in(typeInts));
		
		try (AONContext aonContext = AONContext.getAONContext(domain, user)) {
			DSLContext ctx = aonContext.getDslContext();
			
			
			Byte[] enumBytes = new Byte[types != null ? types.length : 0];
			int index = 0;
			for (com.esferalia.aon.occam.api.model.type.SalaryType type : types) {
				enumBytes[index++] = type.value();
			}
			
			Map<String, Map<String, EnterprisePayrollEntry>> payrolls = 
					getEnterprisePayrollsByPeriod(ctx, condition.and(SALARY.TYPE.in(enumBytes)));
			
			String enterpriseName = "";
			if (enterpriseId <= 0) {
				if (workplaceId != null && workplaceId > 0) {
					enterpriseId = AON.getWorkplace(aonContext.getDomainName()
							, aonContext.getDomainId()
							, aonContext.getUser()
							, d -> d.getIdProperty().eq(workplaceId)).getEnterprise();
				} else {
					Optional<EnterpriseRecord> optEnterprise = ctx.select(ENTERPRISE.REGISTRY).from(ENTERPRISE).where(ENTERPRISE.DOMAIN.eq(domainId)).fetchStreamInto(ENTERPRISE).filter(Objects::nonNull).findFirst();
					if (optEnterprise.isPresent()) {
						enterpriseId = optEnterprise.get().getRegistry();
					}
				}
			}
			
			try {				
				AtomicInteger entId = new AtomicInteger(enterpriseId);
				enterpriseName = AON.getRegistry(aonContext.getDomainName(), aonContext.getDomainId(), user, r -> r.getIdProperty().eq(entId.get())).getName();
			} catch (Exception e) {
				enterpriseName = "";
			}
			
			
			
			Attach attach1 = AON.getAttach(
					aonContext.getDomainName()
					, aonContext.getDomainId()
					, aonContext.getUser(),
					f -> f.getTypeProperty().eq(RegistryAttachmentType.LOGO.value())
					.and(f.getDomainProperty().eq(aonContext.getDomainId()))
					, AttachType.REGISTRY
					);
			
			byte[] byteLogo = attach1.getData();
			
			InputStream logo = null;
			
			try {
				logo = new ByteArrayInputStream(byteLogo);
			} catch (NullPointerException e) {
			}
			String subheader = "Empresa: ";
			if (enterpriseName != null) {
				subheader = subheader.concat(enterpriseName);
			}
			
			EnterprisePayroll enterprisePayroll = new EnterprisePayroll(logo, startDate, null, subheader, payrolls, null);
			PdfMaker.printEnterprisePayroll(enterprisePayroll, outputStream, Optional.of(new Locale("es")), startDate, endDate);
		} catch (CanNotCreatePdfException e) {			
		} catch (IOException e) {}
		
		
	}
	
	
	/**
	 * Method to generate the PDF enterprise payroll and place it into the OutputStream passed as parameter
	 * @param outputStream The OutputStream which will contain the pdf
	 * @param domain The domain name
	 * @param salaryIds The ids of the salaries
	 * @param month A date containing the month and the year of the payroll
	 * @param enterpriseId
	 */
	public static void generateEnterprisePayroll (OutputStream outputStream, String domain, String user, Integer[] salaryIds, Date month, Integer enterpriseId) {
		generateEnterprisePayroll (outputStream, domain, null, user, salaryIds, month, enterpriseId);
	}
	public static void generateEnterprisePayroll (OutputStream outputStream, String domain, Integer domainId, String user, Integer[] salaryIds, Date month, Integer enterpriseId) {
		Condition condition = SALARY.ID.in(salaryIds);
		generateEnterprisePayroll(outputStream, domain, domainId, user, condition, month, enterpriseId, null);
	}
	/**
	 * Method to generate the PDF enterprise payroll and place it into the OutputStream passed as parameter
	 * @param outputStream The OutputStream which will contain the pdf
	 * @param domain The domain name
	 * @param salaryIds The ids of the salaries
	 * @param month A date containing the month and the year of the payroll
	 * @param enterpriseId
	 * @param types Salary types that will be displayed
	 */
	public static void generateEnterprisePayroll (OutputStream outputStream, String domain, Integer domainId, String user, Integer[] salaryIds, Date month, Integer enterpriseId, com.esferalia.aon.occam.api.model.type.SalaryType types[]) {
		Condition condition = SALARY.ID.in(salaryIds);
		
		Collection<Integer> typeInts = Arrays.stream(types).map(t -> t.ordinal()).collect(Collectors.toList());
		condition = condition.and(SALARY.TYPE.in(typeInts));
		
		generateEnterprisePayroll(outputStream, domain, domainId, user, condition, month, enterpriseId, null);
	}

	/**
	 * Method to generate the PDF enterprise payroll and place it into the OutputStream passed as parameter
	 * @param outputStream The OutputStream which will contain the pdf
	 * @param domain The domain name
	 * @param condition The condition to pick up the salaries
	 * @param month A date containing the month and the year of the payroll
	 * @param enterpriseId
	 * @param workplaceId
	 */
	private static void generateEnterprisePayroll (OutputStream outputStream, String domain, Integer domainId, String user, Condition condition, Date month, Integer enterpriseId, Integer workplaceId) {
		try (AONContext aonContext = AONContext.getAONContext(domain, user)) {
			DSLContext ctx = aonContext.getDslContext();
			
			Map<String, Map<String, EnterprisePayrollEntry>> map =
					getEnterprisePayrolls(ctx, condition.and(SALARY.TYPE.in(LIQUIDATIONS)));
			
//					new HashMap<String, Map<String,EnterprisePayrollEntry>>();
			Map<String, Map<String, EnterprisePayrollEntry>> payrolls = 
					getEnterprisePayrolls(ctx, condition.and(SALARY.TYPE.notIn(LIQUIDATIONS)));
			
			String enterpriseName = "";
			if (enterpriseId == null || enterpriseId == 0) {
				if (workplaceId != null && workplaceId > 0) {
					enterpriseId = AON.getWorkplace(aonContext.getDomainName()
							, aonContext.getDomainId()
							, aonContext.getUser()
							, d -> d.getIdProperty().eq(workplaceId)).getEnterprise();
				} else {
					Optional<EnterpriseRecord> optEnterprise = ctx.select(ENTERPRISE.REGISTRY).from(ENTERPRISE).where(ENTERPRISE.DOMAIN.eq(domainId)).fetchStreamInto(ENTERPRISE).filter(Objects::nonNull).findFirst();
					if (optEnterprise.isPresent()) {
						enterpriseId = optEnterprise.get().getRegistry();
					}
				}
			}
			
			try {				
				AtomicInteger entId = new AtomicInteger(enterpriseId);
				enterpriseName = AON.getRegistry(aonContext.getDomainName(), aonContext.getDomainId(), user, r -> r.getIdProperty().eq(entId.get())).getName();
			} catch (Exception e) {
				enterpriseName = "";
			}
			
			
			
			Attach attach1 = AON.getAttach(
					aonContext.getDomainName()
					, aonContext.getDomainId()
					, aonContext.getUser(),
					f -> f.getTypeProperty().eq(RegistryAttachmentType.LOGO.value())
							.and(f.getDomainProperty().eq(aonContext.getDomainId()))
					, AttachType.REGISTRY
					);
			
			byte[] byteLogo = attach1.getData();
			
			InputStream logo = null;
			
			try {
				logo = new ByteArrayInputStream(byteLogo);
			} catch (NullPointerException e) {
			}
			String subheader = "Empresa: ";
			if (enterpriseName != null) {
				subheader = subheader.concat(enterpriseName);
			}
			
			EnterprisePayroll enterprisePayroll = new EnterprisePayroll(logo, month, null, subheader, payrolls, map);
			PdfMaker.printEnterprisePayroll(enterprisePayroll, outputStream, Optional.of(new Locale("es")));
		} catch (CanNotCreatePdfException e) {			
		} catch (IOException e) {}
	}

	/**
	 * 
	 * @param ctx The db context
	 * @param condition The condition to pick up the salaries
	 * @return <p>A map containing all the EnterprisePayrollEntries, which main key is the workplace's name and the secondary is the salary id</p>
	 * @throws IOException
	 */
	private static Map<String, Map<String, EnterprisePayrollEntry>> getEnterprisePayrolls(DSLContext ctx, Condition condition)
			throws IOException {

		Map<Integer, Double> bonusesMap = 
		ctx.select()
		.from(SALARY)
		.innerJoin(CONTRACT).on(CONTRACT.ID.eq(SALARY.CONTRACT))
		.innerJoin(WORKPLACE).on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID))
		.innerJoin(ENTERPRISE).onKey()
		.innerJoin(SALARY_BONUS).on(SALARY.ID.eq(SALARY_BONUS.SALARY))
		.where(condition)
		.fetchStreamInto(SALARY_BONUS)
		.collect(Collectors.toMap(s -> s.getSalary(), s -> s.getAmount(), (a1, a2) -> a1 + a2));
		
		Map<Integer, Map<Integer, Double>> deductions = new HashMap<Integer, Map<Integer, Double>>();
		ctx.select()
		.from(SALARY)
		.innerJoin(CONTRACT).on(CONTRACT.ID.eq(SALARY.CONTRACT))
		.innerJoin(WORKPLACE).on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID))
		.innerJoin(ENTERPRISE).onKey()
		.innerJoin(SALARY_DEDUCTION).on(SALARY.ID.eq(SALARY_DEDUCTION.SALARY))
		.where(condition)
		.fetchStream().forEach(s -> {
					
					if (deductions.get(s.get(SALARY.ID)) != null) {
						deductions.get(s.get(SALARY.ID)).put(s.get(SALARY_DEDUCTION.TYPE) != null ? s.get(SALARY_DEDUCTION.TYPE).intValue() : null,
								s.get(SALARY_DEDUCTION.AMOUNT));
					} else {
						Map<Integer, Double> map = new HashMap<Integer, Double>();
						map.put(s.get(SALARY_DEDUCTION.TYPE) != null ? s.get(SALARY_DEDUCTION.TYPE).intValue() : null
								, s.get(SALARY_DEDUCTION.AMOUNT));
						deductions.put(s.get(SALARY.ID), map);

					}
				});
		Map<String, Map<String, EnterprisePayrollEntry>> map = new LinkedHashMap<String, Map<String, EnterprisePayrollEntry>>();
		
		ctx.select()
		.from(SALARY)
		.innerJoin(CONTRACT).onKey()
		.innerJoin(WORKPLACE).onKey()
		.innerJoin(ENTERPRISE).onKey()
		.where(condition)
		.orderBy(SALARY.EMPLOYEE_NAME)
		.fetchStream()
		.forEach(r -> {
			SalaryType salaryType = typeOf(r.get(SALARY.TYPE), SalaryType.class);
			
			Double totalCost = null;
			Double totalSS = null;
			
			try {
				totalCost = r.get(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS) + r.get(SALARY.TOTAL_ENTERPRISE);
			} catch (NullPointerException e) {}
			try {
				totalSS = totalCost + r.get(SALARY.TOTAL_IRPF);
			} catch (NullPointerException e) {}
			
			EnterprisePayrollEntry.EnterpriseEntryType enterpriseEntryType = 
			getEnterpriseEntryType(salaryType);
			
			EnterprisePayrollEntry entry = new EnterprisePayrollEntry(
					enterpriseEntryType
					, r.get(SALARY.EMPLOYEE_NAME)
					, salaryType.getName(new Locale("es"))
					, r.get(SALARY.TOTAL_PAYMENT)
					, r.get(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS)
					, r.get(SALARY.TOTAL_IRPF)
					, r.get(SALARY.TOTAL_DEDUCTION)
					, r.get(SALARY.TOTAL_LIQUID)
					, r.get(SALARY.TOTAL_ENTERPRISE)
					, totalCost
					, totalSS
					, bonusesMap.get(r.get(SALARY.ID)));
			
			Double otherDeductions;
			try {
				otherDeductions = deductions.get(r.get(SALARY.ID)).get(DeductionType.OTHER.ordinal());
			} catch (NullPointerException e) {
				otherDeductions = null;
			}
			
			try {
				Double advanced = deductions.get(r.get(SALARY.ID)).get(DeductionType.ADVANCE_PAYMENT.ordinal());
				otherDeductions = otherDeductions != null ? otherDeductions+advanced : advanced;
			} catch (NullPointerException e) {}
			
			Double employeeSS = r.get(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS);
			
			if ( enterpriseEntryType == EnterprisePayrollEntry.EnterpriseEntryType.AON_SYSTEM) {
				entry.setEmpleado(Optional.ofNullable(r.get(SALARY.EMPLOYEE_NAME)));
				entry.setTipo(Optional.ofNullable(salaryType.getName(new Locale("es"))));
				entry.setDevengado(Optional.ofNullable(r.get(SALARY.TOTAL_PAYMENT)));
				entry.setSsTrab(Optional.ofNullable(employeeSS));
				entry.setIrpf(Optional.ofNullable(r.get(SALARY.TOTAL_IRPF)));
				entry.setDeducciones(Optional.ofNullable(otherDeductions));
				entry.setLiquido(Optional.ofNullable(r.get(SALARY.TOTAL_LIQUID)));
				entry.setSsEmpr(Optional.ofNullable(r.get(SALARY.TOTAL_ENTERPRISE)));
				entry.setSsTotal(Optional.ofNullable(entry.getSsEmpr().orElse(0d) + entry.getSsTrab().orElse(0d)));
				entry.setCosteTotal(
						Optional.ofNullable(entry.getDevengado().orElse(0d) + entry.getSsEmpr().orElse(0d))
						);
			} else {
				entry.setIrpfSS(Optional.empty());
				entry.setLiquidoSS(Optional.empty());
				entry.setDevengadoSS(Optional.empty());
				entry.setCosteTotalSS(Optional.empty());

				entry.setEmpleadoSS(Optional.ofNullable(r.get(SALARY.EMPLOYEE_NAME)));
				entry.setTipoSS(Optional.ofNullable(salaryType.getName(new Locale("es"))));
				entry.setSsTrabSS(Optional.ofNullable(employeeSS));
				entry.setDeduccionesSS(Optional.ofNullable(otherDeductions));
				entry.setSsEmprSS(Optional.ofNullable(r.get(SALARY.TOTAL_ENTERPRISE)));
				entry.setSsTotalSS(Optional.ofNullable(entry.getSsEmprSS().orElse(0d) + entry.getSsTrabSS().orElse(0d)));
			}
			
			if (!map.containsKey(r.get(WORKPLACE.DESCRIPTION)))
				map.put(r.get(WORKPLACE.DESCRIPTION), new LinkedHashMap<String, EnterprisePayrollEntry>());
			
			String key  = String.format("%s-%s-%3$td", 
			r.get(SALARY.SOCIAL_SECURITY_NUMBER), 
			getSalaryTypeKey(salaryType),
			r.get(SALARY.END_DATE)
			);
			
			map.get(r.get(WORKPLACE.DESCRIPTION)).put(key, entry);
			
		});
			
		return map;
	}
	
	
	private static Map<String, Map<String, EnterprisePayrollEntry>> getEnterprisePayrollsByEmployee(DSLContext ctx, Condition condition)
			throws IOException {

		Map<Integer, Double> bonusesMap = 
		ctx.select()
		.from(SALARY)
		.innerJoin(CONTRACT).on(CONTRACT.ID.eq(SALARY.CONTRACT))
		.innerJoin(WORKPLACE).on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID))
		.innerJoin(ENTERPRISE).onKey()
		.innerJoin(SALARY_BONUS).on(SALARY.ID.eq(SALARY_BONUS.SALARY))
		.where(condition)
		.fetchStreamInto(SALARY_BONUS)
		.collect(Collectors.toMap(s -> s.getSalary(), s -> s.getAmount(), (a1, a2) -> a1 + a2));
		
		Map<Integer, Map<Integer, Double>> deductions = new HashMap<>();
		ctx.select()
		.from(SALARY)
		.innerJoin(CONTRACT).on(CONTRACT.ID.eq(SALARY.CONTRACT))
		.innerJoin(WORKPLACE).on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID))
		.innerJoin(ENTERPRISE).onKey()
		.innerJoin(SALARY_DEDUCTION).on(SALARY.ID.eq(SALARY_DEDUCTION.SALARY))
		.where(condition)
		.fetchStream().forEach(s -> {
					
					if (deductions.get(s.get(SALARY.ID)) != null) {
						deductions.get(s.get(SALARY.ID)).put(s.get(SALARY_DEDUCTION.TYPE) != null ? s.get(SALARY_DEDUCTION.TYPE).intValue() : null,
								s.get(SALARY_DEDUCTION.AMOUNT));
					} else {
						Map<Integer, Double> map = new HashMap<>();
						map.put(s.get(SALARY_DEDUCTION.TYPE) != null ? s.get(SALARY_DEDUCTION.TYPE).intValue() : null
								, s.get(SALARY_DEDUCTION.AMOUNT));
						deductions.put(s.get(SALARY.ID), map);

					}
				});
		Map<String, Map<String, EnterprisePayrollEntry>> map = new LinkedHashMap<>();
		
		ctx.select()
		.from(SALARY)
		.innerJoin(CONTRACT).onKey()
		.innerJoin(WORKPLACE).onKey()
		.innerJoin(ENTERPRISE).onKey()
		.where(condition)
		.orderBy(SALARY.EMPLOYEE_NAME)
		.fetchStream()
		.forEach(r -> {
			SalaryType salaryType = typeOf(r.get(SALARY.TYPE), SalaryType.class);
			
			Double totalCost = null;
			Double totalSS = null;
			
			try {
				totalCost = r.get(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS) + r.get(SALARY.TOTAL_ENTERPRISE);
			} catch (NullPointerException e) {}
			try {
				totalSS = totalCost + r.get(SALARY.TOTAL_IRPF);
			} catch (NullPointerException e) {}
			
			EnterprisePayrollEntry.EnterpriseEntryType enterpriseEntryType = 
			getEnterpriseEntryType(salaryType);
			
			EnterprisePayrollEntry entry = new EnterprisePayrollEntry(
					enterpriseEntryType
					, r.get(SALARY.EMPLOYEE_NAME)
					, null
					, r.get(SALARY.TOTAL_PAYMENT)
					, r.get(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS)
					, r.get(SALARY.TOTAL_IRPF)
					, r.get(SALARY.TOTAL_DEDUCTION)
					, r.get(SALARY.TOTAL_LIQUID)
					, r.get(SALARY.TOTAL_ENTERPRISE)
					, totalCost
					, totalSS
					, bonusesMap.get(r.get(SALARY.ID)));
			
			Double otherDeductions;
			try {
				otherDeductions = deductions.get(r.get(SALARY.ID)).get(DeductionType.OTHER.ordinal());
			} catch (NullPointerException e) {
				otherDeductions = null;
			}
			
			try {
				Double advanced = deductions.get(r.get(SALARY.ID)).get(DeductionType.ADVANCE_PAYMENT.ordinal());
				otherDeductions = otherDeductions != null ? otherDeductions+advanced : advanced;
			} catch (NullPointerException e) {}
			
			Double employeeSS = r.get(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS);
			
			entry.setEmpleado(Optional.ofNullable(r.get(SALARY.EMPLOYEE_NAME)));
			entry.setDevengado(Optional.ofNullable(r.get(SALARY.TOTAL_PAYMENT)));
			entry.setSsTrab(Optional.ofNullable(employeeSS));
			entry.setIrpf(Optional.ofNullable(r.get(SALARY.TOTAL_IRPF)));
			entry.setDeducciones(Optional.ofNullable(otherDeductions));
			entry.setLiquido(Optional.ofNullable(r.get(SALARY.TOTAL_LIQUID)));
			entry.setSsEmpr(Optional.ofNullable(r.get(SALARY.TOTAL_ENTERPRISE)));
			entry.setSsTotal(Optional.ofNullable(entry.getSsEmpr().orElse(0d) + entry.getSsTrab().orElse(0d)));
			entry.setCosteTotal(
					Optional.ofNullable(entry.getDevengado().orElse(0d) + entry.getSsEmpr().orElse(0d))
					);
			
			if (!map.containsKey(r.get(WORKPLACE.DESCRIPTION)))
				map.put(r.get(WORKPLACE.DESCRIPTION), new LinkedHashMap<>());
			
			String key  = r.get(SALARY.SOCIAL_SECURITY_NUMBER);
			
			if (map.get(r.get(WORKPLACE.DESCRIPTION)).containsKey(key)) {
				
				EnterprisePayrollEntry ent = map.get(r.get(WORKPLACE.DESCRIPTION)).get(key);
				
				ent.setDevengado(sumOptionalThings(ent.getDevengado(), entry.getDevengado()));
				ent.setSsTrab(sumOptionalThings(ent.getSsTrab(), entry.getSsTrab()));
				ent.setIrpf(sumOptionalThings(ent.getIrpf(), entry.getIrpf()));
				ent.setDeducciones(sumOptionalThings(ent.getDeducciones(), entry.getDeducciones()));
				ent.setLiquido(sumOptionalThings(ent.getLiquido(), entry.getLiquido()));
				ent.setSsEmpr(sumOptionalThings(ent.getSsEmpr(), entry.getSsEmpr()));
				ent.setCosteTotal(sumOptionalThings(ent.getCosteTotal(), entry.getCosteTotal()));
				ent.setSsTotal(sumOptionalThings(ent.getSsTotal(), entry.getSsTotal()));
				ent.setBonificaciones(sumOptionalThings(ent.getBonificaciones(), entry.getBonificaciones()));
				
				
				
			} else {
				map.get(r.get(WORKPLACE.DESCRIPTION)).put(key, entry);				
			}
			
			
			
		});
			
		return map;
	}
	
	private static Map<String, Map<String, EnterprisePayrollEntry>> getEnterprisePayrollsByPeriod(DSLContext ctx, Condition condition)
			throws IOException {
		
		Map<Integer, Double> bonusesMap = 
			ctx.select()
			.from(SALARY)
			.innerJoin(CONTRACT).on(CONTRACT.ID.eq(SALARY.CONTRACT))
			.innerJoin(WORKPLACE).on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID))
			.innerJoin(ENTERPRISE).onKey()
			.innerJoin(SALARY_BONUS).on(SALARY.ID.eq(SALARY_BONUS.SALARY))
			.where(condition)
			.fetchStreamInto(SALARY_BONUS)
			.collect(Collectors.toMap(s -> s.getSalary(), s -> s.getAmount(), (a1, a2) -> a1 + a2));
		
		Map<Integer, Map<Integer, Double>> deductions = new HashMap<>();
			ctx.select()
			.from(SALARY)
			.innerJoin(CONTRACT).on(CONTRACT.ID.eq(SALARY.CONTRACT))
			.innerJoin(WORKPLACE).on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID))
			.innerJoin(ENTERPRISE).onKey()
			.innerJoin(SALARY_DEDUCTION).on(SALARY.ID.eq(SALARY_DEDUCTION.SALARY))
			.where(condition)
			.fetchStream().forEach(s -> {
			
				if (deductions.get(s.get(SALARY.ID)) != null) {
					deductions.get(s.get(SALARY.ID)).put(s.get(SALARY_DEDUCTION.TYPE) != null ? s.get(SALARY_DEDUCTION.TYPE).intValue() : null,
							s.get(SALARY_DEDUCTION.AMOUNT));
				} else {
					Map<Integer, Double> map = new HashMap<>();
					map.put(s.get(SALARY_DEDUCTION.TYPE) != null ? s.get(SALARY_DEDUCTION.TYPE).intValue() : null
							, s.get(SALARY_DEDUCTION.AMOUNT));
					deductions.put(s.get(SALARY.ID), map);
					
				}
			});
		Map<String, Map<String, EnterprisePayrollEntry>> map = new LinkedHashMap<String, Map<String, EnterprisePayrollEntry>>();
		
		ctx.select()
		.from(SALARY)
		.innerJoin(CONTRACT).onKey()
		.innerJoin(WORKPLACE).onKey()
		.innerJoin(ENTERPRISE).onKey()
		.where(condition)
		.orderBy(SALARY.EMPLOYEE_NAME)
		.fetchStream()
		.forEach(r -> {
			SalaryType salaryType = typeOf(r.get(SALARY.TYPE), SalaryType.class);
			
			Double totalCost = null;
			Double totalSS = null;
			
			try {
				totalCost = r.get(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS) + r.get(SALARY.TOTAL_ENTERPRISE);
			} catch (NullPointerException e) {}
			try {
				totalSS = totalCost + r.get(SALARY.TOTAL_IRPF);
			} catch (NullPointerException e) {}
			
			EnterprisePayrollEntry.EnterpriseEntryType enterpriseEntryType = 
					getEnterpriseEntryType(salaryType);
			
			EnterprisePayrollEntry entry = new EnterprisePayrollEntry(
					enterpriseEntryType
					, r.get(SALARY.EMPLOYEE_NAME)
					, null
					, r.get(SALARY.TOTAL_PAYMENT)
					, r.get(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS)
					, r.get(SALARY.TOTAL_IRPF)
					, r.get(SALARY.TOTAL_DEDUCTION)
					, r.get(SALARY.TOTAL_LIQUID)
					, r.get(SALARY.TOTAL_ENTERPRISE)
					, totalCost
					, totalSS
					, bonusesMap.get(r.get(SALARY.ID)));
			
			Double otherDeductions;
			try {
				otherDeductions = deductions.get(r.get(SALARY.ID)).get(DeductionType.OTHER.ordinal());
			} catch (NullPointerException e) {
				otherDeductions = null;
			}
			
			try {
				Double advanced = deductions.get(r.get(SALARY.ID)).get(DeductionType.ADVANCE_PAYMENT.ordinal());
				otherDeductions = otherDeductions != null ? otherDeductions+advanced : advanced;
			} catch (NullPointerException e) {}
			
			Double employeeSS = r.get(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS);
			String key  = getMonthYearKey(r.get(SALARY.END_DATE));
			String name = getMonthYearName(r.get(SALARY.END_DATE));
			
			entry.setEmpleado(Optional.ofNullable(name));
			entry.setDevengado(Optional.ofNullable(r.get(SALARY.TOTAL_PAYMENT)));
			entry.setSsTrab(Optional.ofNullable(employeeSS));
			entry.setIrpf(Optional.ofNullable(r.get(SALARY.TOTAL_IRPF)));
			entry.setDeducciones(Optional.ofNullable(otherDeductions));
			entry.setLiquido(Optional.ofNullable(r.get(SALARY.TOTAL_LIQUID)));
			entry.setSsEmpr(Optional.ofNullable(r.get(SALARY.TOTAL_ENTERPRISE)));
			entry.setSsTotal(Optional.ofNullable(entry.getSsEmpr().orElse(0d) + entry.getSsTrab().orElse(0d)));
			entry.setCosteTotal(
					Optional.ofNullable(entry.getDevengado().orElse(0d) + entry.getSsEmpr().orElse(0d))
					);
			
			if (!map.containsKey(r.get(WORKPLACE.DESCRIPTION)))
				map.put(r.get(WORKPLACE.DESCRIPTION), new LinkedHashMap<>());
			
			
			if (map.get(r.get(WORKPLACE.DESCRIPTION)).containsKey(key)) {
				
				EnterprisePayrollEntry ent = map.get(r.get(WORKPLACE.DESCRIPTION)).get(key);
				
				ent.setDevengado(sumOptionalThings(ent.getDevengado(), entry.getDevengado()));
				ent.setSsTrab(sumOptionalThings(ent.getSsTrab(), entry.getSsTrab()));
				ent.setIrpf(sumOptionalThings(ent.getIrpf(), entry.getIrpf()));
				ent.setDeducciones(sumOptionalThings(ent.getDeducciones(), entry.getDeducciones()));
				ent.setLiquido(sumOptionalThings(ent.getLiquido(), entry.getLiquido()));
				ent.setSsEmpr(sumOptionalThings(ent.getSsEmpr(), entry.getSsEmpr()));
				ent.setCosteTotal(sumOptionalThings(ent.getCosteTotal(), entry.getCosteTotal()));
				ent.setSsTotal(sumOptionalThings(ent.getSsTotal(), entry.getSsTotal()));
				ent.setBonificaciones(sumOptionalThings(ent.getBonificaciones(), entry.getBonificaciones()));
				
				
			} else {
				map.get(r.get(WORKPLACE.DESCRIPTION)).put(key, entry);				
			}
			
			
			
		});
		
		return map;
	}
	
	private static String getMonthYearName(Date endDate) {
		if (endDate == null)
			return "FECHA INDEFINIDA";
		Locale esLocale = new Locale("es", "ES");
		DateFormat df = new SimpleDateFormat("MMMMMMMMMM 'de' YYYY", esLocale);
		return df.format(endDate).toUpperCase(esLocale);
	}
	
	private static String getMonthYearKey(Date endDate) {
		if (endDate == null)
			return "FECHA INDEFINIDA";
		Locale esLocale = new Locale("es", "ES");
		DateFormat df = new SimpleDateFormat("YYYY_MM", esLocale);
		return df.format(endDate).toUpperCase(esLocale);
	}
	
	@SafeVarargs
	private static Optional<Double> sumOptionalThings(Optional<Double> ...optNumbers) {
		
		if (optNumbers == null)
			return null;
		
		Double[] numbers = new Double[optNumbers.length];
		
		
		int ind = 0;
		for (Optional<Double> number : optNumbers) {
			numbers[ind++] = number.orElse(null);
		}
		return Optional.ofNullable(sumThings(numbers));
		
	}
	
	private static Double sumThings(Double ...numbers) {
		if (numbers == null)
			return null;
		
		if (Arrays.stream(numbers).allMatch(Objects::isNull))
			return null;
		else {
			Double acum = 0.0;
			for (Double number : numbers) {
				acum += zeroIfNull(number);
			}
			return acum;
		}
	}
	
	
	private static String getSalaryTypeKey(SalaryType salaryType) {
		return salaryType.accept(new SalaryTypeVisitor<String>() {

			@Override
			public String visitSalary(SalaryType salaryType) {
				return SalaryType.SALARY.name();
			}

			@Override
			public String visitExtra(SalaryType salaryType) {
				return SalaryType.EXTRA.name();
			}

			@Override
			public String visitSettle(SalaryType salaryType) {
				return SalaryType.SETTLE.name();
			}

			@Override
			public String visitDelay(SalaryType salaryType) {
				return SalaryType.DELAY.name();
			}
			
		});
	}

	private static com.esferalia.aon.in.payroll.pdf.maker.enterprisepayroll.beans.EnterprisePayrollEntry.EnterpriseEntryType getEnterpriseEntryType(
			SalaryType st) {
		return st.accept(new SalaryTypeVisitor<EnterprisePayrollEntry.EnterpriseEntryType>() {
			
			@Override
			public EnterprisePayrollEntry.EnterpriseEntryType visitL00(SalaryType salaryType) {
				return EnterprisePayrollEntry.EnterpriseEntryType.SEG_SOCIAL;
			}

			@Override
			public EnterprisePayrollEntry.EnterpriseEntryType visitL13(SalaryType salaryType) {
				return EnterprisePayrollEntry.EnterpriseEntryType.SEG_SOCIAL;
			}

			@Override
			public EnterprisePayrollEntry.EnterpriseEntryType visitL03(SalaryType salaryType) {
				return EnterprisePayrollEntry.EnterpriseEntryType.SEG_SOCIAL;
			}

			@Override
			public EnterprisePayrollEntry.EnterpriseEntryType visitSalary(SalaryType salaryType) {
				return EnterprisePayrollEntry.EnterpriseEntryType.AON_SYSTEM;
			}

			@Override
			public EnterprisePayrollEntry.EnterpriseEntryType visitExtra(SalaryType salaryType) {
				return EnterprisePayrollEntry.EnterpriseEntryType.AON_SYSTEM;
			}

			@Override
			public EnterprisePayrollEntry.EnterpriseEntryType visitSettle(SalaryType salaryType) {
				return EnterprisePayrollEntry.EnterpriseEntryType.AON_SYSTEM;
			}

			@Override
			public EnterprisePayrollEntry.EnterpriseEntryType visitDelay(SalaryType salaryType) {
				return EnterprisePayrollEntry.EnterpriseEntryType.AON_SYSTEM;
			}
		});
	}
	
	
	private static Condition getConditionSalaryIds(Integer... salaryId) {
		Condition condition;
		condition = Salary.SALARY.ID.in(salaryId);

		return condition;
	}
	
	
	
	private static <T extends Enum<?>> T typeOf(Byte ordinal, Class<T> type) {
		if (ordinal == null)
			return null;
		try {
			return type.getEnumConstants()[ordinal];
		} catch (Exception e) {
			return null;
		}
	}
	
	
	/*public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		
		int arg = 0; 
		
		try (FileOutputStream os = new FileOutputStream(args[arg++])) {
			
			String domain = args [arg++];
			
			Date month = new SimpleDateFormat("MM/yyyy").parse(args[arg++]);
			Date startDate = AonDateUtils.getFirstDayOfMonth(month);
			Date endDate = AonDateUtils.getLastDayOfMonth(month);
			
			Integer enterpriseId = Integer.parseInt(args [arg++]);

			Integer workplaceId  = null;
			
			List<com.esferalia.aon.occam.api.model.type.SalaryType> types = 
			new ArrayList<>();
			while (arg < args.length)
				types.add(com.esferalia.aon.occam.api.model.type.SalaryType.valueOf(args[arg++]));
			
			
			JooqEnterpriseSalaryBuilder.generateEnterprisePayroll(
					os, 
					domain,
					domainId, 
					"user", 
					startDate, 
					endDate, 
					enterpriseId, 
					workplaceId, 
					types.toArray(new com.esferalia.aon.occam.api.model.type.SalaryType[types.size()]));
			
		}
		
		
	}*/
	
}