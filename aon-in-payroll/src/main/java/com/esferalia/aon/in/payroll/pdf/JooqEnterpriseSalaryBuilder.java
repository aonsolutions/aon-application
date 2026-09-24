package com.esferalia.aon.in.payroll.pdf;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryBonus.SALARY_BONUS;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static com.esferalia.aon.watson.util.AonNumberUtils.zeroIfNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
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
import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.code.aon.person.Person;
import com.esferalia.aon.in.payroll.pdf.maker.PdfMaker;
import com.esferalia.aon.in.payroll.pdf.maker.enterprisepayroll.beans.EnterprisePayroll;
import com.esferalia.aon.in.payroll.pdf.maker.enterprisepayroll.beans.EnterprisePayrollEntry;
import com.esferalia.aon.in.payroll.pdf.maker.enterprisepayroll.beans.EnterprisePayrollEntry.EnterpriseEntryType;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.jooq.tables.records.EnterpriseRecord;
import com.esferalia.aon.jooq.tables.records.SalaryBonusRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.payroll.ContractData;
import com.esferalia.aon.occam.api.model.type.DeductionType;
import com.esferalia.aon.occam.impl.jooq.dao.WorkplaceDAO;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.enumeration.SalaryTypeVisitor;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class JooqEnterpriseSalaryBuilder {
	
	private static final Locale LOCALE_ES = new Locale("es");
	
	private static final String FUNDAE = "BONIFICACION_FORMACION_CONTINUA";
	
	public static Byte [] LIQUIDATIONS = { 
			(byte) com.esferalia.aon.occam.api.model.type.SalaryType.L00.ordinal(),
			(byte) com.esferalia.aon.occam.api.model.type.SalaryType.L02.ordinal(),
			(byte) com.esferalia.aon.occam.api.model.type.SalaryType.L13.ordinal(),
			(byte) com.esferalia.aon.occam.api.model.type.SalaryType.L03.ordinal()
		};
	
		private static final Byte[] PAYMENTS_INKIND = Arrays.stream(PaymentType.values())
				.filter(PaymentType::isSalaryInKind).map(PaymentType::ordinal).map(AonNumberUtils::toByte)
				.toArray(Byte[]::new);

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
	public static void generateEnterprisePayroll (OutputStream outputStream, String domain, String user, Date startDate, Date endDate, Integer enterpriseId, Integer workplaceId, com.esferalia.aon.occam.api.model.type.SalaryType types[]) {
		generateEnterprisePayroll(outputStream, domain, null, user, startDate, endDate, enterpriseId, workplaceId, types);
	}
	
	public static void generateEnterprisePayroll (OutputStream outputStream, String domain, Integer domainId, String user, Date startDate, Date endDate, Integer enterpriseId, Integer workplaceId, com.esferalia.aon.occam.api.model.type.SalaryType types[]) {
		Condition condition;
		if (workplaceId != null && workplaceId != 0)
			condition = WORKPLACE.ID.eq(workplaceId);
		else
			condition = ENTERPRISE.REGISTRY.eq(enterpriseId);
		
		
		
		
		condition = condition.and(
				(
						SALARY.TYPE.notIn(AonEnumUtils.getByte(SalaryType.DELAY), AonEnumUtils.getByte(SalaryType.PROCEDURAL)).and(
						SALARY.ISSUE_DATE.between(new java.sql.Date(startDate.getTime()), new java.sql.Date(endDate.getTime())))
				).or(
						SALARY.TYPE.in(AonEnumUtils.getByte(SalaryType.DELAY), AonEnumUtils.getByte(SalaryType.PROCEDURAL)).and(
						SALARY.CHARGE_DATE.between(new java.sql.Date(startDate.getTime()), new java.sql.Date(endDate.getTime())))
				)
		);
		
		Collection<Integer> typeInts = Arrays.stream(types).map(com.esferalia.aon.occam.api.model.type.SalaryType::ordinal).collect(Collectors.toList());
		
		condition = condition.and(SALARY.TYPE.in(typeInts));
		
		generateEnterprisePayroll(outputStream, domain, domainId, user, condition, startDate, endDate, enterpriseId, workplaceId);
	}
	
	public static void generateEnterprisePayrollByEmployee (OutputStream outputStream, String domain, Integer domainId, String user, Date startDate, Date endDate, int enterpriseId, Integer workplaceId, com.esferalia.aon.occam.api.model.type.SalaryType types[], Person ...persons) {
		Condition condition;
		if (workplaceId != null && workplaceId != 0)
			condition = WORKPLACE.ID.eq(workplaceId);
		else
			condition = ENTERPRISE.REGISTRY.eq(enterpriseId);
		
		condition = condition.and(
				(
						SALARY.TYPE.notIn(AonEnumUtils.getByte(SalaryType.DELAY), AonEnumUtils.getByte(SalaryType.PROCEDURAL)).and(
						SALARY.ISSUE_DATE.between(new java.sql.Date(startDate.getTime()), new java.sql.Date(endDate.getTime())))
				).or(
						SALARY.TYPE.in(AonEnumUtils.getByte(SalaryType.DELAY), AonEnumUtils.getByte(SalaryType.PROCEDURAL)).and(
						SALARY.CHARGE_DATE.between(new java.sql.Date(startDate.getTime()), new java.sql.Date(endDate.getTime())))
				)
		);
		
		if (persons != null && persons.length > 0) {
			Set<Integer> personIds = new LinkedHashSet<>();
			for (Person person : persons) {
				if (person != null && person.getId() != null && person.getId() > 0) {
					personIds.add(person.getId());
				}
			}
			
			if (!personIds.isEmpty()) {
				condition = condition.and(PERSON.REGISTRY.in(personIds));
			}
			
		}
		
		Collection<Integer> typeInts = Arrays.stream(types)
				.map(com.esferalia.aon.occam.api.model.type.SalaryType::ordinal)
				.collect(Collectors.toList());
		
		condition = condition.and(SALARY.TYPE.in(typeInts));
		
		try (CloseableAONContext aonContext = AONContext.getAONContext(domain, user)) {
			DSLContext ctx = aonContext.getDslContext();
			
			
			Byte[] enumBytes = new Byte[types != null ? types.length : 0];
			int index = 0;
			for (com.esferalia.aon.occam.api.model.type.SalaryType type : types) {
				enumBytes[index++] = type.value();
			}
			
			Map<Integer, Map<String, Map<String, List<ContractData>>>> contractData = getContractDataByWorkplace(aonContext, startDate, endDate, enterpriseId, workplaceId);
			
			Map<String, Map<String, EnterprisePayrollEntry>> payrolls = 
					getEnterprisePayrollsByEmployee(ctx, condition.and(SALARY.TYPE.in(enumBytes)), Optional.ofNullable(contractData));
			
			String enterpriseName = "";
			if (enterpriseId <= 0) {
				if (workplaceId != null && workplaceId > 0) {
					enterpriseId = WorkplaceDAO.get(aonContext, workplaceId, new Options().setSecurity(false)).getEnterprise();
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
			PdfMaker.printEnterprisePayroll(enterprisePayroll, outputStream, Optional.of(new Locale("es")), startDate, endDate, false);
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
		
		try (CloseableAONContext aonContext = AONContext.getAONContext(domain, user)) {
			DSLContext ctx = aonContext.getDslContext();
			
			
			Byte[] enumBytes = new Byte[types != null ? types.length : 0];
			int index = 0;
			for (com.esferalia.aon.occam.api.model.type.SalaryType type : types) {
				enumBytes[index++] = type.value();
			}
			
			Map<Integer, Map<String, Map<String, List<ContractData>>>> contractDatas = getContractDataByWorkplace(aonContext, startDate, endDate, enterpriseId, workplaceId);
			
			Map<String, Map<String, EnterprisePayrollEntry>> payrolls = 
					getEnterprisePayrollsByPeriod(ctx, condition.and(SALARY.TYPE.in(enumBytes)), Optional.ofNullable(contractDatas));
			
			String enterpriseName = "";
			if (enterpriseId <= 0) {
				if (workplaceId != null && workplaceId > 0) {
					enterpriseId = WorkplaceDAO.get(aonContext, workplaceId, new Options().setSecurity(false)).getEnterprise();
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
			PdfMaker.printEnterprisePayroll(enterprisePayroll, outputStream, Optional.of(new Locale("es")), startDate, endDate, true);
		} catch (CanNotCreatePdfException e) {			
		} catch (IOException e) {}
		
		
	}

	public static void generateEnterprisePayrollByPeriod (OutputStream outputStream, String domain, String user, Integer domainId,  Date startDate, Date endDate, Integer enterpriseId, Integer workplaceId, com.esferalia.aon.occam.api.model.type.SalaryType types[]) {
		Condition condition;
		if (workplaceId != null && workplaceId != 0)
			condition = WORKPLACE.ID.eq(workplaceId);
		else
			condition = ENTERPRISE.REGISTRY.eq(enterpriseId);
		condition = condition.and(SALARY.ISSUE_DATE.between(new java.sql.Date(startDate.getTime())
				, new java.sql.Date(endDate.getTime())));
		
		Collection<Integer> typeInts = Arrays.stream(types)
				.map(com.esferalia.aon.occam.api.model.type.SalaryType::ordinal)
				.collect(Collectors.toList());
		
		condition = condition.and(SALARY.TYPE.in(typeInts));
		
		try (CloseableAONContext aonContext = AONContext.getAONContext(domain, user)) {
			DSLContext ctx = aonContext.getDslContext();
			
			
			Byte[] enumBytes = new Byte[types != null ? types.length : 0];
			int index = 0;
			for (com.esferalia.aon.occam.api.model.type.SalaryType type : types) {
				enumBytes[index++] = type.value();
			}
			
			Map<Integer, Map<String, Map<String, List<ContractData>>>> contractDatas = getContractDataByWorkplace(aonContext, startDate, endDate, enterpriseId, workplaceId);
			
			Map<String, Map<String, EnterprisePayrollEntry>> payrolls = 
					getEnterprisePayrollsByPeriod(ctx, condition.and(SALARY.TYPE.in(enumBytes)), Optional.ofNullable(contractDatas));
			
			String enterpriseName = "";
			if (null == enterpriseId || enterpriseId <= 0) {
				if (workplaceId != null && workplaceId > 0) {
					enterpriseId = WorkplaceDAO.get(aonContext, workplaceId, new Options().setSecurity(false)).getEnterprise();
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
			PdfMaker.printEnterprisePayroll(enterprisePayroll, outputStream, Optional.of(new Locale("es")), startDate, endDate, true);
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
		
		Collection<Integer> typeInts = Arrays.stream(types).map(com.esferalia.aon.occam.api.model.type.SalaryType::ordinal).collect(Collectors.toList());
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
	private static void generateEnterprisePayroll (OutputStream outputStream, String domain, Integer domainId, String user, Condition condition, Date startDate, Date endDate, Integer enterpriseId, Integer workplaceId) {
		try (CloseableAONContext aonContext = AONContext.getAONContext(domain, user)) {
			DSLContext ctx = aonContext.getDslContext();
			
			Map<Integer, Map<String, Map<String, List<ContractData>>>> contractDatas = getContractDataByWorkplace(aonContext, startDate, endDate, enterpriseId, workplaceId);
			
			Map<String, Map<String, EnterprisePayrollEntry>> liquidations =
					getEnterprisePayrolls(ctx, condition.and(SALARY.TYPE.in(LIQUIDATIONS)));
			
			Map<String, Map<String, EnterprisePayrollEntry>> payrolls = 
					getEnterprisePayrolls(ctx, condition.and(SALARY.TYPE.notIn(LIQUIDATIONS)), Optional.ofNullable(contractDatas));
			
			String enterpriseName = "";
			if (enterpriseId == null || enterpriseId == 0) {
				if (workplaceId != null && workplaceId > 0) {
					enterpriseId = WorkplaceDAO.get(aonContext, workplaceId, new Options().setSecurity(false)).getEnterprise();
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
			
			EnterprisePayroll enterprisePayroll = new EnterprisePayroll(logo, startDate, null, subheader, payrolls, liquidations);
			PdfMaker.printEnterprisePayroll(enterprisePayroll, outputStream, Optional.of(new Locale("es")), startDate, endDate, false);
		} catch (CanNotCreatePdfException e) {			
		} catch (IOException e) {}
	}
	
	private static void generateEnterprisePayroll (OutputStream outputStream, String domain, Integer domainId, String user, Condition condition, Date month, Integer enterpriseId, Integer workplaceId) {
		try (CloseableAONContext aonContext = AONContext.getAONContext(domain, user)) {
			DSLContext ctx = aonContext.getDslContext();
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(month);
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.HOUR, 0);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			
			Date startDate = calendar.getTime();
			
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			
			Date endDate = calendar.getTime();
			
			Map<Integer, Map<String, Map<String, List<ContractData>>>> contractDatas = getContractDataByWorkplace(aonContext, startDate, endDate, enterpriseId, workplaceId);
			
			Map<String, Map<String, EnterprisePayrollEntry>> map =
					getEnterprisePayrolls(ctx, condition.and(SALARY.TYPE.in(LIQUIDATIONS)));
			
			Map<String, Map<String, EnterprisePayrollEntry>> payrolls = 
					getEnterprisePayrolls(ctx, condition.and(SALARY.TYPE.notIn(LIQUIDATIONS)), Optional.ofNullable(contractDatas));
			
			
			String enterpriseName = "";
			if (enterpriseId == null || enterpriseId == 0) {
				if (workplaceId != null && workplaceId > 0) {
					enterpriseId = WorkplaceDAO.get(aonContext, workplaceId, new Options().setSecurity(false)).getEnterprise();
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
	
	
	public static Map<Integer/*WORKPLACE*/, Map<String/*EMPLOYEEID*/, Map<String/*DATA TYPE*/, List<ContractData>>>> getContractDataByWorkplace(AONContext aonContext, Date startDate, Date endDate, Integer enterpriseId, Integer workplaceId) {
		if (startDate == null || endDate == null)
			return null;
		
		java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
		java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());
		
		if ((enterpriseId == null || enterpriseId <= 0) && workplaceId != null && workplaceId > 0) {
			try {				
				enterpriseId = aonContext.getDslContext()
						.select(WORKPLACE.ENTERPRISE)
						.from(WORKPLACE)
						.where(WORKPLACE.ID.eq(workplaceId))
						.fetchOneInto(WORKPLACE)
						.getEnterprise();
			} catch (Exception e) {}
		}
		
		SelectConditionStep<Record> query = aonContext.getDslContext()
		.select(WORKPLACE.ID, PERSON.SOCIAL_SECURITY_NUM, PERSON.REGISTRY, REGISTRY.DOCUMENT, CONTRACT_DATA.asterisk())
		.from(CONTRACT)
		.innerJoin(PERSON).on(CONTRACT.PERSON.eq(PERSON.REGISTRY))
		.innerJoin(REGISTRY).on(REGISTRY.ID.eq(PERSON.REGISTRY))
		.innerJoin(WORKPLACE).on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID))
		.innerJoin(ENTERPRISE).on(WORKPLACE.ENTERPRISE.eq(ENTERPRISE.REGISTRY))
		.leftJoin(CONTRACT_DATA).on(CONTRACT.ID.eq(CONTRACT_DATA.CONTRACT))
			.and(CONTRACT_DATA.END_DATE.ge(sqlStartDate))
			.and(CONTRACT_DATA.END_DATE.le(sqlEndDate))
			.and(CONTRACT_DATA.NAME.eq(FUNDAE))
			.and(CONTRACT_DATA.END_DATE.ge(CONTRACT_DATA.START_DATE))
		.where(CONTRACT.START_DATE.le(sqlEndDate))
		.and(CONTRACT.END_DATE.isNull().or(CONTRACT.END_DATE.ge(sqlStartDate)))
		.and(ENTERPRISE.REGISTRY.eq(enterpriseId))
		;
		
		if (workplaceId != null && workplaceId > 0) {
			query = query.and(WORKPLACE.ID.eq(workplaceId));
		}
		
		LinkedHashMap<Integer, Map<String, Map<String, List<ContractData>>>> contractDataMap = new LinkedHashMap<>();
		
		query.fetchStream()
		.filter(Objects::nonNull)
		.forEach(res -> {
			//----KEYS----
			String name = res.get(CONTRACT_DATA.NAME);
			String ssNum = res.get(PERSON.SOCIAL_SECURITY_NUM);
			String document = res.get(REGISTRY.DOCUMENT);
			String employeeId = AonNumberUtils.toString(res.get(PERSON.REGISTRY));
			Integer workplace = res.get(WORKPLACE.ID);
			//------------
			
			
			ContractData cd = new ContractData()
				.setDomain(res.get(CONTRACT_DATA.DOMAIN))
				.setContract(res.get(CONTRACT_DATA.CONTRACT))
				.setEndDate(res.get(CONTRACT_DATA.END_DATE))
				.setStartDate(res.get(CONTRACT_DATA.START_DATE))
				.setExpression(res.get(CONTRACT_DATA.EXPRESSION))
				.setId(res.get(CONTRACT_DATA.ID))
				.setName(res.get(CONTRACT_DATA.NAME));
			
			
			if (name != null && ssNum != null) {
				addToContractDataMap(contractDataMap, name, ssNum, document, employeeId, workplace, cd);
			}
		});
		
		return contractDataMap;
	}
	
	private static void addToContractDataMap(LinkedHashMap<Integer, Map<String, Map<String, List<ContractData>>>> contractDataMap,
			String name, String ssNum, String document, String personId, Integer workplace, ContractData cd) {
		
		String key = /*!AonStringUtils.isEmpty(ssNum) ? ssNum : document*/personId;
		
		if (contractDataMap.containsKey(workplace)) {
			Map<String, Map<String, List<ContractData>>> contractData = contractDataMap.get(workplace);
			if (contractData.containsKey(key)) {
				Map<String, List<ContractData>> employeeData = contractData.get(key);
				if (employeeData.containsKey(name)) {
					employeeData.get(name).add(cd);
				} else {
					LinkedList<ContractData> cdList = new LinkedList<>();
					cdList.add(cd);
					employeeData.put(name, cdList);
				}
			} else {
				LinkedHashMap<String, List<ContractData>> employeeData= new LinkedHashMap<>();
				LinkedList<ContractData> cdList = new LinkedList<>();
				cdList.add(cd);						
				employeeData.put(name, cdList);
				contractData.put(key, employeeData);
			}
		} else {
			LinkedHashMap<String, Map<String, List<ContractData>>> contractData = new LinkedHashMap<>();
			LinkedHashMap<String, List<ContractData>> employeeData= new LinkedHashMap<>();
			LinkedList<ContractData> cdList = new LinkedList<>();
			cdList.add(cd);
			employeeData.put(name, cdList);
			contractData.put(key, employeeData);
			contractDataMap.put(workplace, contractData);
		}
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
		return getEnterprisePayrolls(ctx, condition, Optional.empty());
	}
	
	private static Map<String, Map<String, EnterprisePayrollEntry>> getEnterprisePayrolls(DSLContext ctx, Condition condition, Optional<Map<Integer, Map<String, Map<String, List<ContractData>>>>> optContractDatas)
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
		.collect(Collectors.toMap(SalaryBonusRecord::getSalary, SalaryBonusRecord::getAmount, (a1, a2) -> a1 + a2));
		
		Map<Integer, Double> inKindDeductions = new LinkedHashMap<>();
		ctx
		.select()
		.from(SALARY_PAYMENT)
		.innerJoin(SALARY).onKey()
		.innerJoin(CONTRACT).onKey()
		.innerJoin(WORKPLACE).onKey()
		.innerJoin(ENTERPRISE).onKey()
		.where(condition)
		.and(SALARY_PAYMENT.TYPE.in(PAYMENTS_INKIND))
		.fetchStreamInto(SALARY_PAYMENT)
		.filter(Objects::nonNull)
		.forEach(sp -> {
			double amount = AonNumberUtils.zeroIfNull(inKindDeductions.getOrDefault(sp.getSalary(), 0d)) + AonNumberUtils.zeroIfNull(sp.getAmount());
			inKindDeductions.put(sp.getSalary(), amount);
		});
		
		
		ctx
		.select()
		.from(SALARY_PAYMENT)
		.innerJoin(SALARY).onKey()
		.innerJoin(CONTRACT).onKey()
		.innerJoin(WORKPLACE).onKey()
		.innerJoin(ENTERPRISE).onKey()
		.where(condition)
		.and(SALARY_PAYMENT.PAYMENT_CONCEPT.eq(ContextVariable.PPE))
		.fetchStreamInto(SALARY_PAYMENT)
		.filter(Objects::nonNull)
		.forEach(sp -> {
			double amount = firstNonZeroOrZero(sp.getQuote(), sp.getAmount());
			inKindDeductions.compute(sp.getSalary(), ( k, v ) -> v == null ? amount : amount + v);
		});
		
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
						
						// Accumulate value if exists previus
						Integer type = s.get(SALARY_DEDUCTION.TYPE) != null
						        ? s.get(SALARY_DEDUCTION.TYPE).intValue()
						        : null;
						Double amount = s.get(SALARY_DEDUCTION.AMOUNT);
			
						deductions
						    .computeIfAbsent(s.get(SALARY.ID), k -> new HashMap<>())
						    .merge(type, amount, Double::sum);
						
					} else {
						Map<Integer, Double> map = new HashMap<>();
						map.put(s.get(SALARY_DEDUCTION.TYPE) != null ? s.get(SALARY_DEDUCTION.TYPE).intValue() : null
								, s.get(SALARY_DEDUCTION.AMOUNT));
						deductions.put(s.get(SALARY.ID), map);

					}
				});
		
		Map<Integer, Map<String, Map<String, List<ContractData>>>> contractDatas;
		if (optContractDatas.isPresent()) {
			contractDatas = optContractDatas.get();
		} else {
			contractDatas = Collections.emptyMap();
		}
		
		Map<String, Map<String, EnterprisePayrollEntry>> map = new LinkedHashMap<>();
		
		ctx.select()
		.from(SALARY)
		.innerJoin(CONTRACT).onKey()
		.innerJoin(PERSON).onKey()
		.innerJoin(WORKPLACE).onKey()
		.innerJoin(ENTERPRISE).onKey()
		.where(condition)
		.and(SALARY.TYPE.lt((byte)SalaryType.M190.ordinal()))
		.orderBy(SALARY.EMPLOYEE_NAME, SALARY.ISSUE_DATE, SALARY.TYPE)
		.fetchStream()
		.forEach(r -> {
			SalaryType salaryType = typeOf(r.get(SALARY.TYPE), SalaryType.class);
			
			Double totalCost = null;
			Double totalSS = null;
			
			try {
				totalCost = r.get(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS) + r.get(SALARY.TOTAL_ENTERPRISE);
			} catch (NullPointerException e) {
			}
			try {
				totalSS = totalCost + r.get(SALARY.TOTAL_IRPF);
			} catch (NullPointerException e) {
			}
			
			EnterprisePayrollEntry.EnterpriseEntryType enterpriseEntryType = 
			getEnterpriseEntryType(salaryType);
			
			EnterprisePayrollEntry entry = new EnterprisePayrollEntry(
					enterpriseEntryType
					, r.get(SALARY.SOCIAL_SECURITY_NUMBER)
					, r.get(SALARY.CCC)
					, r.get(SALARY.START_DATE)
					, r.get(SALARY.END_DATE)
					, r.get(SALARY.ISSUE_DATE)
					, r.get(SALARY.EMPLOYEE_NAME)
					, salaryType.getName(LOCALE_ES)
					, r.get(SALARY.TOTAL_PAYMENT)
					, r.get(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS)
					, r.get(SALARY.TOTAL_IRPF)
					, r.get(SALARY.TOTAL_DEDUCTION)
					, r.get(SALARY.TOTAL_LIQUID)
					, r.get(SALARY.TOTAL_ENTERPRISE)
					, totalCost
					, totalSS
					, bonusesMap.get(r.get(SALARY.ID)));
			
			
			manageContractDatas(entry, contractDatas, r);
			
			
			Double otherDeductions;
			try {
				otherDeductions = deductions.get(r.get(SALARY.ID)).get(DeductionType.OTHER.ordinal());
			} catch (NullPointerException e) {
				otherDeductions = null;
			}
			
			try {
				Double advanced = deductions.get(r.get(SALARY.ID)).get(DeductionType.ADVANCE_PAYMENT.ordinal());
				otherDeductions = otherDeductions != null ? otherDeductions+advanced : advanced;
			} catch (NullPointerException e) {
			}
			
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
				entry.setInKind(Optional.ofNullable(inKindDeductions.getOrDefault(r.get(SALARY.ID), null)));
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
				map.put(r.get(WORKPLACE.DESCRIPTION), new LinkedHashMap<>());
			
			// TODO: Esto acumula las nominas agrupandolas por trabajador en vez por contrato, los clientes han dicho que lo quieren desglosado
			
//			String key  = String.format("%s-%s-%3$td-%3$tm-%4$tY-%4$td-%4$tm-%3$tY",
//			isEmpty(r.get(SALARY.SOCIAL_SECURITY_NUMBER)) ? r.get(CONTRACT.ID) : r.get(SALARY.SOCIAL_SECURITY_NUMBER) , 
//			getSalaryTypeKey(salaryType),
//			r.get(SALARY.START_DATE),
//			r.get(SALARY.END_DATE)
//			);
			
			String key  = String.format("%s-%s-%3$td-%3$tm-%3$tY",
			r.get(CONTRACT.ID), 
			getSalaryTypeKey(salaryType),
			r.get(SALARY.END_DATE)
			);
			
			Map<String, EnterprisePayrollEntry> eMap = map.get(r.get(WORKPLACE.DESCRIPTION));
			eMap.merge(key, entry, EnterprisePayrollEntry::merge );
		});
			
		return map;
	}
	
	private static Locale ES = new Locale("es");
	
	private static Map<String, Map<String, EnterprisePayrollEntry>> getEnterprisePayrollsByEmployee(DSLContext ctx, Condition condition, Optional<Map<Integer, Map<String, Map<String, List<ContractData>>>>> optContractDatas)
			throws IOException {

		Map<Integer, Double> bonusesMap = 
		ctx.select()
		.from(SALARY)
		.innerJoin(CONTRACT).on(CONTRACT.ID.eq(SALARY.CONTRACT))
		.innerJoin(PERSON).on(PERSON.REGISTRY.eq(CONTRACT.PERSON))
		.innerJoin(WORKPLACE).on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID))
		.innerJoin(ENTERPRISE).onKey()
		.innerJoin(SALARY_BONUS).on(SALARY.ID.eq(SALARY_BONUS.SALARY))
		.where(condition)
		.fetchStreamInto(SALARY_BONUS)
		.collect(Collectors.toMap(SalaryBonusRecord::getSalary, SalaryBonusRecord::getAmount, (a1, a2) -> a1 + a2));
		
		
		Map<Integer, Double> inKindDeductions = new LinkedHashMap<>();
		ctx
		.select()
		.from(SALARY_PAYMENT)
		.innerJoin(SALARY).onKey()
		.innerJoin(CONTRACT).onKey()
		.innerJoin(PERSON).on(PERSON.REGISTRY.eq(CONTRACT.PERSON))
		.innerJoin(WORKPLACE).onKey()
		.innerJoin(ENTERPRISE).onKey()
		.where(condition)
		.and(SALARY_PAYMENT.TYPE.in(PAYMENTS_INKIND))
		.fetchStreamInto(SALARY_PAYMENT)
		.filter(Objects::nonNull)
		.forEach(sp -> {
			double amount = AonNumberUtils.zeroIfNull(inKindDeductions.getOrDefault(sp.getSalary(), 0d)) + AonNumberUtils.zeroIfNull(sp.getAmount());
			inKindDeductions.put(sp.getSalary(), amount);
		});
		
		ctx
		.select()
		.from(SALARY_PAYMENT)
		.innerJoin(SALARY).onKey()
		.innerJoin(CONTRACT).onKey()
		.innerJoin(PERSON).on(PERSON.REGISTRY.eq(CONTRACT.PERSON))
		.innerJoin(WORKPLACE).onKey()
		.innerJoin(ENTERPRISE).onKey()
		.where(condition)
		.and(SALARY_PAYMENT.PAYMENT_CONCEPT.eq(ContextVariable.PPE))
		.fetchStreamInto(SALARY_PAYMENT)
		.filter(Objects::nonNull)
		.forEach(sp -> {
			double amount = AonNumberUtils.zeroIfNull(inKindDeductions.getOrDefault(sp.getSalary(), 0d)) + AonNumberUtils.zeroIfNull(sp.getQuote());
			inKindDeductions.compute(sp.getSalary(), ( k, v ) -> v == null ? amount : amount + v);
		});

		Map<Integer, Map<Integer, Double>> deductions = new HashMap<>();
		ctx.select()
		.from(SALARY)
		.innerJoin(CONTRACT).on(CONTRACT.ID.eq(SALARY.CONTRACT))
		.innerJoin(PERSON).on(PERSON.REGISTRY.eq(CONTRACT.PERSON))
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
		
		Map<Integer, Map<String, Map<String, List<ContractData>>>> contractDatas = optContractDatas.orElse(Collections.emptyMap());
		
		Map<String, Map<String, EnterprisePayrollEntry>> map = new LinkedHashMap<>();
		
		ctx.select()
		.from(SALARY)
		.innerJoin(CONTRACT).onKey()
		.innerJoin(PERSON).onKey()
		.innerJoin(WORKPLACE).onKey()
		.innerJoin(ENTERPRISE).onKey()
		.where(condition)
		.orderBy(SALARY.EMPLOYEE_NAME, SALARY.TYPE)
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
					, r.get(SALARY.SOCIAL_SECURITY_NUMBER)
					, r.get(SALARY.CCC)
					, r.get(SALARY.START_DATE)
					, r.get(SALARY.END_DATE)
					, r.get(SALARY.ISSUE_DATE)
					, r.get(SALARY.EMPLOYEE_NAME)
					, salaryType.getName(ES) 
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
			entry.setInKind(Optional.ofNullable(inKindDeductions.getOrDefault(r.get(SALARY.ID), null)));
			entry.setCosteTotal(
					Optional.ofNullable(entry.getDevengado().orElse(0d) + entry.getSsEmpr().orElse(0d))
					);
			
			manageContractDatas(entry, contractDatas, r);
			
			if (!map.containsKey(r.get(WORKPLACE.DESCRIPTION)))
				map.put(r.get(WORKPLACE.DESCRIPTION), new LinkedHashMap<>());
			
			String key  = AonNumberUtils.toString(r.get(PERSON.REGISTRY));
			
			if (map.get(r.get(WORKPLACE.DESCRIPTION)).containsKey(key)) {
				
				EnterprisePayrollEntry ent = map.get(r.get(WORKPLACE.DESCRIPTION)).get(key);
				
				ent.setDevengado(sumOptionalThings(ent.getDevengado(), entry.getDevengado()));
				ent.setSsTrab(sumOptionalThings(ent.getSsTrab(), entry.getSsTrab()));
				ent.setIrpf(sumOptionalThings(ent.getIrpf(), entry.getIrpf()));
				ent.setDeducciones(sumOptionalThings(ent.getDeducciones(), entry.getDeducciones()));
				ent.setLiquido(sumOptionalThings(ent.getLiquido(), entry.getLiquido()));
				ent.setSsEmpr(sumOptionalThings(ent.getSsEmpr(), entry.getSsEmpr()));
				ent.setCosteTotal(sumOptionalThings(ent.getCosteTotal(), entry.getCosteTotal()));
				ent.setInKind(sumOptionalThings(ent.getInKind(), entry.getInKind()));
				ent.setSsTotal(sumOptionalThings(ent.getSsTotal(), entry.getSsTotal()));
				ent.setBonificaciones(sumOptionalThings(ent.getBonificaciones(), entry.getBonificaciones()));
				ent.setFundae(sumOptionalThings(ent.getFundae(), entry.getFundae()));
				
				
				
			} else {
				map.get(r.get(WORKPLACE.DESCRIPTION)).put(key, entry);				
			}
			
		});
			
		return map;
	}
	
	private static Map<String, Map<String, EnterprisePayrollEntry>> getEnterprisePayrollsByPeriod(DSLContext ctx, Condition condition, Optional<Map<Integer, Map<String, Map<String, List<ContractData>>>>> optContractDatas)
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
			.collect(Collectors.toMap(SalaryBonusRecord::getSalary, SalaryBonusRecord::getAmount, (a1, a2) -> a1 + a2));
		
		Map<Integer, Double> inKindDeductions = new LinkedHashMap<>();
		ctx
		.select()
		.from(SALARY_PAYMENT)
		.innerJoin(SALARY).onKey()
		.innerJoin(CONTRACT).onKey()
		.innerJoin(WORKPLACE).onKey()
		.innerJoin(ENTERPRISE).onKey()
		.where(condition)
		.and(SALARY_PAYMENT.TYPE.in(PAYMENTS_INKIND))
		.fetchStreamInto(SALARY_PAYMENT)
		.filter(Objects::nonNull)
		.forEach(sp -> {
			double amount = AonNumberUtils.zeroIfNull(inKindDeductions.getOrDefault(sp.getSalary(), 0d)) + AonNumberUtils.zeroIfNull(sp.getAmount());
			inKindDeductions.put(sp.getSalary(), amount);
		});
		
		ctx
		.select()
		.from(SALARY_PAYMENT)
		.innerJoin(SALARY).onKey()
		.innerJoin(CONTRACT).onKey()
		.innerJoin(WORKPLACE).onKey()
		.innerJoin(ENTERPRISE).onKey()
		.where(condition)
		.and(SALARY_PAYMENT.PAYMENT_CONCEPT.eq(ContextVariable.PPE))
		.fetchStreamInto(SALARY_PAYMENT)
		.filter(Objects::nonNull)
		.forEach(sp -> {
			double amount = AonNumberUtils.zeroIfNull(inKindDeductions.getOrDefault(sp.getSalary(), 0d)) + AonNumberUtils.zeroIfNull(sp.getQuote());
			inKindDeductions.compute(sp.getSalary(), ( k, v ) -> v == null ? amount : amount + v);
		});

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
		
		Map<Integer, Map<String, Map<String, List<ContractData>>>> contractDatas = optContractDatas.orElse(Collections.emptyMap());
		
		Map<String, Map<String, EnterprisePayrollEntry>> map = new LinkedHashMap<>();
		
		ctx.select()
		.from(SALARY)
		.innerJoin(CONTRACT).onKey()
		.innerJoin(PERSON).onKey()
		.innerJoin(WORKPLACE).onKey()
		.innerJoin(ENTERPRISE).onKey()
		.where(condition)
		.orderBy(SALARY.EMPLOYEE_NAME, SALARY.ISSUE_DATE, SALARY.TYPE)
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
					, r.get(SALARY.SOCIAL_SECURITY_NUMBER)
					, r.get(SALARY.CCC)
					, r.get(SALARY.START_DATE)
					, r.get(SALARY.END_DATE)
					, r.get(SALARY.ISSUE_DATE)
					, r.get(SALARY.EMPLOYEE_NAME)
					, salaryType.getName(LOCALE_ES)
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
			String key  = getMonthYearKey(r.get(SALARY.ISSUE_DATE));
			String name = getMonthYearName(r.get(SALARY.ISSUE_DATE));
			
			entry.setEmpleado(Optional.ofNullable(name));
			entry.setDevengado(Optional.ofNullable(r.get(SALARY.TOTAL_PAYMENT)));
			entry.setSsTrab(Optional.ofNullable(employeeSS));
			entry.setIrpf(Optional.ofNullable(r.get(SALARY.TOTAL_IRPF)));
			entry.setDeducciones(Optional.ofNullable(otherDeductions));
			entry.setLiquido(Optional.ofNullable(r.get(SALARY.TOTAL_LIQUID)));
			entry.setSsEmpr(Optional.ofNullable(r.get(SALARY.TOTAL_ENTERPRISE)));
			entry.setSsTotal(Optional.ofNullable(entry.getSsEmpr().orElse(0d) + entry.getSsTrab().orElse(0d)));
			entry.setInKind(Optional.ofNullable(inKindDeductions.getOrDefault(r.get(SALARY.ID), null)));
			entry.setCosteTotal(
					Optional.ofNullable(entry.getDevengado().orElse(0d) + entry.getSsEmpr().orElse(0d))
					);
			
			
			manageContractDatas(entry, contractDatas, r);
			
			
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
				ent.setInKind(sumOptionalThings(ent.getInKind(), entry.getInKind()));
				ent.setSsTotal(sumOptionalThings(ent.getSsTotal(), entry.getSsTotal()));
				ent.setBonificaciones(sumOptionalThings(ent.getBonificaciones(), entry.getBonificaciones()));
				ent.setFundae(sumOptionalThings(ent.getFundae(), entry.getFundae()));
				
				
			} else {
				map.get(r.get(WORKPLACE.DESCRIPTION)).put(key, entry);				
			}
			
		});
		
		
		return map;
	}

	private static void manageContractDatas(EnterprisePayrollEntry entry,
			Map<Integer, Map<String, Map<String, List<ContractData>>>> contractDatas, Record r) {
		if (r.get(SALARY.TYPE) != null && r.get(SALARY.TYPE).equals((byte)SalaryType.SALARY.ordinal()) && contractDatas.containsKey(r.get(WORKPLACE.ID)) && contractDatas.get(r.get(WORKPLACE.ID)).containsKey(AonNumberUtils.toString(r.get(PERSON.REGISTRY)))) {
			Map<String, List<ContractData>> fundaeData = contractDatas.get(r.get(WORKPLACE.ID)).get(AonNumberUtils.toString(r.get(PERSON.REGISTRY)));
			if (fundaeData.containsKey(FUNDAE)) {
				List<ContractData> fundaeList = fundaeData.get(FUNDAE) != null ? fundaeData.get(FUNDAE) : Collections.emptyList();
				Date salaryStart = r.get(SALARY.START_DATE); //NOT NULL FIELD
				Date salaryEnd = r.get(SALARY.END_DATE); //NOT NULL FIELD
				Double fundaeAmount = 0d;
				LinkedHashSet<ContractData> removeable = new LinkedHashSet<>();
				for(ContractData cd : fundaeList) {
					if (salaryStart.compareTo(cd.getEndDate()) <= 0 && salaryEnd.compareTo(cd.getStartDate()) >= 0) {
						try {
							Double value = (-1) * Double.parseDouble(cd.getExpression());
							fundaeAmount += value;
						} catch (NullPointerException | NumberFormatException e) {
						} finally {								
							removeable.add(cd);
						}
					}
				}
				fundaeList.removeAll(removeable);
				
				
				if (fundaeAmount != 0)
					entry.setFundae(Optional.ofNullable(fundaeAmount));
			}
		}
	}
	
	private static String getMonthYearName(Date date) {
		if (date == null)
			return "FECHA INDEFINIDA";
		
		LocalDate localDate = LocalDate.of(
				date.getYear() + 1900, 
				date.getMonth() + 1,   
				date.getDate()         
	        );
		
		Locale locale = new Locale("es", "ES");
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM 'de' yyyy", locale);
		
		return localDate.format(formatter).toUpperCase(locale);
	}
	
	private static String getMonthYearKey(Date date) {
		if (date == null)
			return "FECHA INDEFINIDA";
		
		LocalDate localDate = LocalDate.of(
				date.getYear() + 1900, 
				date.getMonth() + 1,   
				date.getDate()         
	        );
		
		Locale locale = new Locale("es", "ES");
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM", locale);
		
		return localDate.format(formatter).toUpperCase(locale);
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
			
			@Override
			public String visitProcedural(SalaryType salaryType) {
				return SalaryType.PROCEDURAL.name();
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
			public EnterprisePayrollEntry.EnterpriseEntryType visitL02(SalaryType salaryType) {
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
			
			@Override
			public EnterpriseEntryType visitProcedural(SalaryType salaryType) {
				return EnterprisePayrollEntry.EnterpriseEntryType.AON_SYSTEM;
			}
		});
	}
	
	
//	private static Condition getConditionSalaryIds(Integer... salaryId) {
//		Condition condition;
//		condition = Salary.SALARY.ID.in(salaryId);
//
//		return condition;
//	}
	
	
	
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
	
	private static double firstNonZeroOrZero (  Double ...values) {
		return Arrays.stream(values).filter(AonNumberUtils::isValid).filter( d -> d != 0.00 ).findFirst().orElse(0.00);
	}
	
	
}