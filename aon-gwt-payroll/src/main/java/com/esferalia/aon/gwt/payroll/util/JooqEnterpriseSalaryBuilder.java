package com.esferalia.aon.gwt.payroll.util;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryBonus.SALARY_BONUS;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.jooq.Condition;
import org.jooq.DSLContext;

import com.code.aon.common.enumeration.MimeType;
import com.esferalia.aon.in.payroll.pdf.creators.PdfMaker;
import com.esferalia.aon.in.payroll.pdf.creators.enterprise_payroll.beans.EnterprisePayroll;
import com.esferalia.aon.in.payroll.pdf.creators.enterprise_payroll.beans.EnterprisePayrollEntry;
import com.esferalia.aon.in.payroll.pdf.creators.exceptions.CanNotCreatePdfException;
import com.esferalia.aon.jooq.tables.Salary;
import com.esferalia.aon.jooq.tables.Workplace;
import com.esferalia.aon.jooq.tables.records.SalaryRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.type.DeductionType;
import com.esferalia.aon.salary.enumeration.SalaryType;

public class JooqEnterpriseSalaryBuilder {
	public static void generateEnterprisePayroll (OutputStream outputStream, String domain, Condition condition, Date month, Integer enterpriseId, Integer workplaceId) {
		try (AONContext aonContext = AONContext.getAONContext(domain, "")) {
			DSLContext ctx = aonContext.getDslContext();
			
			Map<String, Map<String, EnterprisePayrollEntry>> map = new HashMap<String, Map<String,EnterprisePayrollEntry>>();
			Map<String, Map<String, EnterprisePayrollEntry>> payrolls = getEnterprisePayrolls(ctx, condition);
			
			if (enterpriseId == null || enterpriseId == 0)
				enterpriseId = AON.getWorkplace(aonContext.getDomainName(), aonContext.getDomainId(), "", d -> d.getIdProperty().eq(workplaceId)).getEnterprise();
			
			AtomicInteger entId = new AtomicInteger(enterpriseId);
			
			String enterpriseName = AON.getRegistry(aonContext.getDomainName(), aonContext.getDomainId(), "", r -> r.getIdProperty().eq(entId.get())).getName();
			
			
			Attach attach1 = AON.getAttach(aonContext.getDomainName(), aonContext.getDomainId(), aonContext.getUser(),
					f -> f.getTypeProperty().eq(RegistryAttachmentType.LOGO.value())
							.and(f.getDomainProperty().eq(aonContext.getDomainId())),
					AttachType.REGISTRY);
			
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
			
			System.out.println("\t" + month);
			System.out.println("\t" + subheader);
			System.out.println("\t" + payrolls);
			System.out.println("\t" + map);
			System.out.println("\t" + logo);
			
			EnterprisePayroll enterprisePayroll = new EnterprisePayroll(logo, month, null, subheader, payrolls, map);
			PdfMaker.print_enterprise_payroll(enterprisePayroll, outputStream, Optional.of(new Locale("es")));
		} catch (CanNotCreatePdfException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void generateEnterprisePayroll (OutputStream outputStream, String domain, Date startDate, Date endDate, int enterpriseId, Integer workplaceId) {
		Condition condition;
		if (workplaceId != null && workplaceId != 0)
			condition = WORKPLACE.ID.eq(workplaceId);
		else
			condition = ENTERPRISE.REGISTRY.eq(enterpriseId);
		condition = condition.and(SALARY.ISSUE_DATE.between(new java.sql.Date(startDate.getTime())
				, new java.sql.Date(endDate.getTime())));
		generateEnterprisePayroll(outputStream, domain, condition, startDate, enterpriseId, workplaceId);
	}
	
	public static void generateEnterprisePayroll (OutputStream outputStream, String domain, Date startDate, Date endDate, int enterpriseId, Integer workplaceId, com.esferalia.aon.gwt.payroll.shared.Salary.Type types[]) {
		Condition condition;
		if (workplaceId != null && workplaceId != 0)
			condition = WORKPLACE.ID.eq(workplaceId);
		else
			condition = ENTERPRISE.REGISTRY.eq(enterpriseId);
		condition = condition.and(SALARY.ISSUE_DATE.between(new java.sql.Date(startDate.getTime())
				, new java.sql.Date(endDate.getTime())));
		
		Collection<Integer> typeInts = Arrays.stream(types).map(t -> t.ordinal()).collect(Collectors.toList());
		
		condition = condition.and(SALARY.TYPE.in(typeInts));
		generateEnterprisePayroll(outputStream, domain, condition, startDate, enterpriseId, workplaceId);
	}
	
	public static void generateEnterprisePayroll (OutputStream outputStream, String domain, Integer[] salaryIds, Date month, Integer enterpriseId) {
		Condition condition = SALARY.ID.in(salaryIds);
		generateEnterprisePayroll(outputStream, domain, condition, month, enterpriseId, null);
	}
	
	public static void generateEnterprisePayroll (OutputStream outputStream, String domain, Integer[] salaryIds, Date month, Integer enterpriseId, com.esferalia.aon.gwt.payroll.shared.Salary.Type types[]) {
		Condition condition = SALARY.ID.in(salaryIds);
		
		Collection<Integer> typeInts = Arrays.stream(types).map(t -> t.ordinal()).collect(Collectors.toList());
		condition = condition.and(SALARY.TYPE.in(typeInts));
		
		generateEnterprisePayroll(outputStream, domain, condition, month, enterpriseId, null);
	}

	
	private static Map<String, Map<String, EnterprisePayrollEntry>> getEnterprisePayrolls(DSLContext ctx, Condition condition)
			throws IOException {
		Map<Integer, Double> bonusesMap = ctx.select().from(SALARY).innerJoin(CONTRACT)
				.on(CONTRACT.ID.eq(SALARY.CONTRACT)).innerJoin(WORKPLACE).on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID))
				.innerJoin(ENTERPRISE).onKey()
				.innerJoin(SALARY_BONUS).on(SALARY.ID.eq(SALARY_BONUS.SALARY)).where(condition)
				.fetchStreamInto(SALARY_BONUS)
				.collect(Collectors.toMap(s -> s.getSalary(), s -> s.getAmount(), (a1, a2) -> a1 + a2));
		
		ArrayList<Double> dedBonuses = new ArrayList<Double>();
		
		Map<Integer, Map<Integer, Double>> deductions = new HashMap<Integer, Map<Integer, Double>>();
		ctx.select().from(SALARY).innerJoin(CONTRACT).on(CONTRACT.ID.eq(SALARY.CONTRACT)).innerJoin(WORKPLACE)
				.on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID)).innerJoin(ENTERPRISE).onKey().innerJoin(SALARY_DEDUCTION)
				.on(SALARY.ID.eq(SALARY_DEDUCTION.SALARY)).where(condition).fetchStream().forEach(s -> {
					
					if (s.get(SALARY_DEDUCTION.TYPE) == null &&
							s.get(SALARY_DEDUCTION.AMOUNT) != null &&
							s.get(SALARY_DEDUCTION.AMOUNT) < 0
						)
						dedBonuses.add(s.get(SALARY_DEDUCTION.AMOUNT));
					
					if (deductions.get(s.get(SALARY.ID)) != null) {
						deductions.get(s.get(SALARY.ID)).put(s.get(SALARY_DEDUCTION.TYPE).intValue(),
								s.get(SALARY_DEDUCTION.AMOUNT));
					} else {
						Map<Integer, Double> map = new HashMap<Integer, Double>();
						map.put(s.get(SALARY_DEDUCTION.TYPE).intValue(), s.get(SALARY_DEDUCTION.AMOUNT));
						deductions.put(s.get(SALARY.ID), map);

					}
				});
		Map<String, Map<String, EnterprisePayrollEntry>> map = new LinkedHashMap<String, Map<String, EnterprisePayrollEntry>>();
		ctx.select().from(SALARY).innerJoin(CONTRACT).onKey()
			.innerJoin(WORKPLACE).onKey().innerJoin(ENTERPRISE).onKey().where(condition).orderBy(SALARY.EMPLOYEE_NAME).fetchStream()
			.forEach(r -> {
				SalaryType st = typeOf(r.get(SALARY.TYPE), SalaryType.class);
				
				Double totalCost = null;
				Double totalSS = null;
				
				try {
					totalCost = r.get(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS) + r.get(SALARY.TOTAL_ENTERPRISE);
				} catch (NullPointerException e) {}
				try {
					totalSS = totalCost + r.get(SALARY.TOTAL_IRPF);
				} catch (NullPointerException e) {}
				
				EnterprisePayrollEntry entry = new EnterprisePayrollEntry(
						EnterprisePayrollEntry.EnterpriseEntryType.AON_SYSTEM
						, r.get(SALARY.EMPLOYEE_NAME)
						, st.getName(new Locale("es"))
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
				} catch (NullPointerException e) {
					
				}
				
				Double employeeSS = r.get(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS);
				if (!dedBonuses.isEmpty()) {
					Double amount = dedBonuses.stream().mapToDouble(a -> a).sum();
					
					if (employeeSS != null && amount != null)
						employeeSS += amount;
					else if (amount != null)
						employeeSS = amount;
				}
					
				
				entry.setEmpleado(Optional.ofNullable(r.get(SALARY.EMPLOYEE_NAME)));
				entry.setTipo(Optional.ofNullable(st.getName(new Locale("es"))));
				entry.setDevengado(Optional.ofNullable(r.get(SALARY.TOTAL_PAYMENT)));
				entry.setSsTrab(Optional.ofNullable(employeeSS));
				entry.setIrpf(Optional.ofNullable(r.get(SALARY.TOTAL_IRPF)));
				entry.setDeducciones(Optional.ofNullable(otherDeductions));
				entry.setLiquido(Optional.ofNullable(r.get(SALARY.TOTAL_LIQUID)));
				entry.setSsEmpr(Optional.ofNullable(r.get(SALARY.TOTAL_ENTERPRISE)));
				entry.setSsTotal(Optional.ofNullable(entry.getSsEmpr().orElse(0d) + entry.getSsTrab().orElse(0d)));
				entry.setCosteTotal(
						Optional.ofNullable(entry.getDevengado().orElse(0d) + entry.getSsEmpr().orElse(0d)));
				if (!map.containsKey(r.get(WORKPLACE.DESCRIPTION)))
					map.put(r.get(WORKPLACE.DESCRIPTION), new LinkedHashMap<String, EnterprisePayrollEntry>());
				map.get(r.get(WORKPLACE.DESCRIPTION)).put(String.valueOf(r.get(SALARY.ID)), entry);
				
			});
			
			
			
		return map;
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
	
}
