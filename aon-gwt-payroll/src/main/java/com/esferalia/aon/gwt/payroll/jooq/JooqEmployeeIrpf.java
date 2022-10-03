package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Alcatraz.ALCATRAZ;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.EmployeeIrpf;
import com.esferalia.aon.jooq.tables.records.SalaryRecord;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class JooqEmployeeIrpf {
	
	// --------------------------------------------- Constructor
	
	private JooqEmployeeIrpf() {
		super();
	}
	
	// --------------------------------------------- Variables
	
	private static SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
	
	// --------------------------------------------- Settings

	private static Settings settings = null;
	
	protected static Settings getDefaultSettings() {
		if (settings == null) {
			settings = new Settings();
			settings.setRenderSchema(false);
		}
		return settings;
	}
	
	// --------------------------------------------- Methods. getEmployeeIrpf
	
	public static List<EmployeeIrpf> getEmployeeIrpf(Connection conn, Integer domainId, String ssNumber, Date startDate) throws IllegalArgumentException {
		return getEmployeeIrpf(DSL.using(conn, getDefaultSettings()), domainId, ssNumber, startDate);
	}

	private static List<EmployeeIrpf> getEmployeeIrpf(DSLContext dslContext, Integer domainId, String ssNumber, Date startDate) throws IllegalArgumentException {
		List<EmployeeIrpf> employeeIrpfList = new ArrayList<>();
		
		// Iterator Date
		Calendar iteratorCalendar = Calendar.getInstance();
		iteratorCalendar.setTime(startDate);
		iteratorCalendar.set(Calendar.DAY_OF_MONTH, 1);
		iteratorCalendar.set(Calendar.MONTH, 0);
		Date iteratorDate = iteratorCalendar.getTime();
		
		// Current Year
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(iteratorDate);
		Integer year =  calendar.get(Calendar.YEAR);
		
		while (isSameYear(iteratorDate, year)) {
			// Payroll endDate
			Calendar endDateCalendar = Calendar.getInstance();
			endDateCalendar.setTime(iteratorDate);
			endDateCalendar.set(Calendar.DAY_OF_MONTH, endDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			Date endDate = endDateCalendar.getTime();
			
			// Salary Records
			Result<Record> salaryRecords = dslContext.select().from(SALARY)
					.where(SALARY.ISSUE_DATE.between(parseDateToSQL(iteratorDate), parseDateToSQL(endDate)).or(SALARY.ISSUE_DATE.eq(parseDateToSQL(iteratorDate)).or(SALARY.ISSUE_DATE.eq( parseDateToSQL(endDate)))))
					.and(SALARY.SOCIAL_SECURITY_NUMBER.eq(ssNumber))
					.and(SALARY.DOMAIN.eq(domainId))
					.orderBy(SALARY.TYPE)
					.fetch();
			
			if(salaryRecords.isNotEmpty()) {
				if(salaryRecords.size() > 1)
					System.err.println("More than one Salary for a month (contractId : " + salaryRecords.get(0).get(SALARY.CONTRACT) + ", ssNumber : " + ssNumber + ", Date : " + formatDate.format(iteratorDate) + ")");
				
				EmployeeIrpf employeeIrpf = new EmployeeIrpf();
				Integer salaryId = null;
				String salaryType = null;
				Double employeeSSQuoteAcumulate = 0.00;
				Double totalIrpfAcumulate = 0.00;
				Double inkindBaseAcumulate = 0.00;
				Double moneyBaseAcumulate = 0.00;
				Double irpfPercentAcumulate = 0.00;
				Double moneyQuoteAcumulate = 0.00;
				Double inkindQuoteAcumulate = 0.00;
				
				// Get SalaryRecord
				for(Record salaryRecord : salaryRecords) {
					
					salaryId = salaryRecord.get(SALARY.ID);
					salaryType = getSalaryType(salaryRecord.get(SALARY.TYPE));
					
					if(AonStringUtils.equalsIgnoreCase(salaryType, "Manual")) {
						EmployeeIrpf employeeIrpfL190 = createEmployeeIrpfL190(dslContext, iteratorDate, salaryId, salaryType, salaryRecord);
						if(AonStringUtils.isNotBlank(employeeIrpfL190.getSalaryType()))
							employeeIrpfList.add(employeeIrpfL190);
						continue;
					}
					
					Double employeeSSQuote;
					if(AonStringUtils.equalsIgnoreCase(salaryType, "L00") || AonStringUtils.equalsIgnoreCase(salaryType, "L13"))
						employeeSSQuote = salaryRecord.get(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS);
					else {
						employeeSSQuote = salaryRecord.get(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS);
						if(employeeSSQuote == 0.00) {
							Result<Record> salaryDeductions = dslContext.select().from(SALARY_DEDUCTION)
								.where(SALARY_DEDUCTION.SALARY.eq(salaryId))
								.and(
									SALARY_DEDUCTION.DEDUCTION_CONCEPT.eq("CGC")
									.or(SALARY_DEDUCTION.DEDUCTION_CONCEPT.eq("DESMPL"))
									.or(SALARY_DEDUCTION.DEDUCTION_CONCEPT.eq("FP"))
								).fetch();
							Double value = 0.00;
							for(Record salaryDeduction : salaryDeductions)
								value += salaryDeduction.get(SALARY_DEDUCTION.AMOUNT);
							employeeSSQuote = value;
						}
					}
					
					Double totalIrpf = salaryRecord.get(SALARY.TOTAL_IRPF);
					if(employeeSSQuote == 0.00) {
						Double value = dslContext.select(SALARY_DEDUCTION.AMOUNT).from(SALARY_DEDUCTION)
								.where(SALARY_DEDUCTION.SALARY.eq(salaryId))
								.and(SALARY_DEDUCTION.DEDUCTION_CONCEPT.eq("IRPF")).fetchOne(SALARY_DEDUCTION.AMOUNT);
						if(null != value)
							totalIrpf = value;
					}
					
					Double inkindBase = salaryRecord.get(SALARY.INKIND_IRPF_BASE);
					if(inkindBase == 0.00) {
						Double value = dslContext.select(SALARY_DEDUCTION.AMOUNT).from(SALARY_DEDUCTION)
								.where(SALARY_DEDUCTION.SALARY.eq(salaryId))
								.and(SALARY_DEDUCTION.DEDUCTION_CONCEPT.eq("EN_ESPECIE")).fetchOne(SALARY_DEDUCTION.AMOUNT);
						
						List<String> baseCgcStr = dslContext.select(SALARY_DATA.EXPRESSION).from(SALARY_DATA)
								.where(SALARY_DATA.NAME.eq("BASE_CGC"))
								.and(SALARY_DATA.SALARY.eq(salaryId))
								.fetch(SALARY_DATA.EXPRESSION);
						if(null != value && !baseCgcStr.isEmpty())
							inkindBase = Double.parseDouble(baseCgcStr.get(0)) - value;
					}
					
					Double moneyBase = salaryRecord.get(SALARY.MONEY_IRPF_BASE);
					
					// Get irpf percent
					Double moneyQuote = 0.00;
					Double inkindQuote = 0.00;
					Double irpfPercent = 0.00;
					
					Result<Record> irpfPercentRecords = dslContext.select().from(SALARY_DATA)
							.where(SALARY_DATA.SALARY.eq(salaryId))
							.and(SALARY_DATA.NAME.eq("PORCENTAJE_IRPF"))
							.fetch();
					
					if(irpfPercentRecords.isNotEmpty()) {
						if(irpfPercentRecords.size() > 1)
							System.err.println("More than one Salary IRPF percent for a salary (salaryId : " + salaryId + ")");
						
						Record irpfPercentRecord = irpfPercentRecords.get(0);
						try {
							irpfPercent = Double.parseDouble(irpfPercentRecord.get(SALARY_DATA.EXPRESSION));
							moneyQuote = moneyBase * irpfPercent / 100;
							inkindQuote = inkindBase * irpfPercent / 100;
						} catch (NumberFormatException e) {
							System.err.println("Can't format percent : " + irpfPercentRecord.get(SALARY_DATA.EXPRESSION));
						}
					}
					
					// Acumulate
					if(AonStringUtils.equalsIgnoreCase(salaryType, "L00") || AonStringUtils.equalsIgnoreCase(salaryType, "L13"))
						employeeSSQuoteAcumulate = employeeSSQuote;
					else
						employeeSSQuoteAcumulate += employeeSSQuote;
					totalIrpfAcumulate += totalIrpf;
					inkindBaseAcumulate += inkindBase;
					moneyBaseAcumulate += moneyBase;
					irpfPercentAcumulate += irpfPercent;
					moneyQuoteAcumulate += moneyQuote;
					inkindQuoteAcumulate += inkindQuote;
					
					employeeIrpf.setDate(iteratorDate)
								.setSalaryId(salaryId)
								.setSalaryType(salaryType)
								.setMoneyBase(moneyBaseAcumulate)
								.setMoneyQuote(moneyQuoteAcumulate)
								.setInkindBase(inkindBaseAcumulate)
								.setInkindQuote(inkindQuoteAcumulate)
								.setIrpfPercent(irpfPercentAcumulate)
								.setEmployeeSSQuote(employeeSSQuoteAcumulate)
								.setTotalIrpf(totalIrpfAcumulate);
				}
				
				if(AonStringUtils.isNotBlank(salaryType) && !AonStringUtils.equalsIgnoreCase(salaryType, "Manual"))
					employeeIrpfList.add(employeeIrpf);
			}
			
			// Add month to iteratorDate
			iteratorCalendar.add(Calendar.MONTH, 1);
			iteratorDate = iteratorCalendar.getTime();
		}
		
		return employeeIrpfList;
	}

	private static EmployeeIrpf createEmployeeIrpfL190(DSLContext dslContext, Date iteratorDate, Integer salaryId, String salaryType, Record salaryRecord) throws IllegalArgumentException {
		Double employeeSSQuote = salaryRecord.get(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS);
		if(employeeSSQuote == 0.00) {
			Result<Record> salaryDeductions = dslContext.select().from(SALARY_DEDUCTION)
				.where(SALARY_DEDUCTION.SALARY.eq(salaryId))
				.and(
					SALARY_DEDUCTION.DEDUCTION_CONCEPT.eq("CGC")
					.or(SALARY_DEDUCTION.DEDUCTION_CONCEPT.eq("DESMPL"))
					.or(SALARY_DEDUCTION.DEDUCTION_CONCEPT.eq("FP"))
				).fetch();
			Double value = 0.00;
			for(Record salaryDeduction : salaryDeductions)
				value = salaryDeduction.get(SALARY_DEDUCTION.AMOUNT);
			employeeSSQuote = value;
		}
		
		Double totalIrpf = salaryRecord.get(SALARY.TOTAL_IRPF);
		if(employeeSSQuote == 0.00) {
			Double value = dslContext.select(SALARY_DEDUCTION.AMOUNT).from(SALARY_DEDUCTION)
					.where(SALARY_DEDUCTION.SALARY.eq(salaryId))
					.and(SALARY_DEDUCTION.DEDUCTION_CONCEPT.eq("IRPF")).fetchOne(SALARY_DEDUCTION.AMOUNT);
			if(null != value)
				totalIrpf = value;
		}
		
		Double inkindBase = salaryRecord.get(SALARY.INKIND_IRPF_BASE);
		if(inkindBase == 0.00) {
			Double value = dslContext.select(SALARY_DEDUCTION.AMOUNT).from(SALARY_DEDUCTION)
					.where(SALARY_DEDUCTION.SALARY.eq(salaryId))
					.and(SALARY_DEDUCTION.DEDUCTION_CONCEPT.eq("EN_ESPECIE")).fetchOne(SALARY_DEDUCTION.AMOUNT);
			
			String baseCgcStr = dslContext.select(SALARY_DATA.EXPRESSION).from(SALARY_DATA)
					.where(SALARY_DATA.NAME.eq("BASE_CGC"))
					.and(SALARY_DATA.SALARY.eq(salaryId))
					.fetchOne(SALARY_DATA.EXPRESSION);
			if(null != value && AonStringUtils.isNotBlank(baseCgcStr))
				inkindBase = Double.parseDouble(baseCgcStr) - value;
		}
		
		Double moneyBase = salaryRecord.get(SALARY.MONEY_IRPF_BASE);
		
		
		// Get irpf percent
		Double irpfPercent = 0.00;
		Double moneyQuote = 0.00;
		Double inkindQuote = 0.00;
		
		Result<Record> irpfPercentRecords = dslContext.select().from(SALARY_DATA)
				.where(SALARY_DATA.SALARY.eq(salaryId))
				.and(SALARY_DATA.NAME.eq("PORCENTAJE_IRPF"))
				.fetch();
		
		if(irpfPercentRecords.isNotEmpty()) {
			if(irpfPercentRecords.size() > 1)
				System.err.println("More than one Salary IRPF percent for a salary (salaryId : " + salaryId + ")");
			
			Record irpfPercentRecord = irpfPercentRecords.get(0);
			try {
				irpfPercent = Double.parseDouble(irpfPercentRecord.get(SALARY_DATA.EXPRESSION));
				moneyQuote = moneyBase * irpfPercent / 100;
				inkindQuote = inkindBase * irpfPercent / 100;
			} catch (NumberFormatException e) {
				System.err.println("Can't format percent : " + irpfPercentRecord.get(SALARY_DATA.EXPRESSION));
			}
		}
		
		EmployeeIrpf employeeIrpf = new EmployeeIrpf();
		employeeIrpf.setDate(iteratorDate)
					.setSalaryId(salaryId)
					.setSalaryType(salaryType)
					.setMoneyBase(moneyBase)
					.setMoneyQuote(moneyQuote)
					.setInkindBase(inkindBase)
					.setInkindQuote(inkindQuote)
					.setIrpfPercent(irpfPercent)
					.setEmployeeSSQuote(employeeSSQuote)
					.setTotalIrpf(totalIrpf);
		
		return employeeIrpf;
	}

	private static String getSalaryType(Byte type) {
		switch (type) {
		case (byte) 0:
			return "N\u00F3minas";
		case (byte) 1:
			return "N\u00F3minas"; //"Extra";
		case (byte) 2:
			return "N\u00F3minas"; //"Finiquito";
		case (byte) 3:
			return "N\u00F3minas"; //"Retraso";
		case (byte) 4:
			return "L00";
		case (byte) 5:
			return "L03";
		case (byte) 6:
			return "L13";
		case (byte) 7: // M190
			return "Manual";
		default:
			return "N/D";
		}
	}

	private static boolean isSameYear(Date iteratorDate, Integer year) {
		Calendar calendarIterator = Calendar.getInstance();
		calendarIterator.setTime(iteratorDate);
		Integer iteratorYear = calendarIterator.get(Calendar.YEAR);
		
		return AonNumberUtils.equals(iteratorYear, year);
	}

	// --------------------------------------------- Methods. setEmployeeIrpf
	
	public static void setEmployeeIrpf(Connection conn, Integer domainId, Integer contractId, String fullName, String document, String ssNumber, List<EmployeeIrpf> employeeIrpfs) throws IllegalArgumentException {
		setEmployeeIrpf(DSL.using(conn, getDefaultSettings()), domainId, contractId, fullName, document, ssNumber, employeeIrpfs);
	}

	private static void setEmployeeIrpf(DSLContext dslContext, Integer domainId, Integer contractId, String fullName, String document, String ssNumber, List<EmployeeIrpf> employeeIrpfs) throws IllegalArgumentException {
		// Salary L131 equals salary type DB (byte) 7
		for(EmployeeIrpf employeeIrpf : employeeIrpfs) {
			if(!employeeIrpf.isNew() && !employeeIrpf.isDelete())
				continue;
			
			if(employeeIrpf.isDelete()) {
				dslContext.delete(SALARY_DATA).where(SALARY_DATA.SALARY.eq(employeeIrpf.getSalaryId())).execute();
				dslContext.delete(SALARY_PAYMENT).where(SALARY_PAYMENT.SALARY.eq(employeeIrpf.getSalaryId())).execute();
				dslContext.delete(ALCATRAZ).where(ALCATRAZ.SALARY.eq(employeeIrpf.getSalaryId())).execute();
				dslContext.delete(SALARY).where(SALARY.ID.eq(employeeIrpf.getSalaryId())).execute();
			} else {
					
				Calendar startDateCalendar = Calendar.getInstance();
				startDateCalendar.setTime(employeeIrpf.getDate());
				startDateCalendar.set(Calendar.DAY_OF_MONTH, 1);
				Date startDate = startDateCalendar.getTime();
				
				Calendar endDateCalendar = Calendar.getInstance();
				endDateCalendar.setTime(employeeIrpf.getDate());
				endDateCalendar.set(Calendar.DAY_OF_MONTH, endDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
				Date endDate = endDateCalendar.getTime();
				
				double irpfBase = employeeIrpf.getMoneyBase() + employeeIrpf.getInkindBase();
				try {
					irpfBase = round(irpfBase, 2);
				} catch (Exception e) {
					// Nothing to do here
				}
				
				SalaryRecord salaryRecord = dslContext.insertInto(SALARY)
					.set(SALARY.DOMAIN, domainId)
					.set(SALARY.TYPE, (byte)7)
					.set(SALARY.CONTRACT, contractId)
					.set(SALARY.START_DATE, parseDateToSQL(startDate))
					.set(SALARY.END_DATE, parseDateToSQL(endDate))
					.set(SALARY.SOCIAL_SECURITY_NUMBER, ssNumber)
					.set(SALARY.EMPLOYEE_NAME, fullName)
					.set(SALARY.EMPLOYEE_DOCUMENT, document)
					.set(SALARY.REGISTRATION, 0)
					.set(SALARY.TIME_UNITS, 30)
					.set(SALARY.ISSUE_DATE, parseDateToSQL(endDate))
					.set(SALARY.MONEY_IRPF_BASE, employeeIrpf.getMoneyBase())
					.set(SALARY.INKIND_IRPF_BASE, employeeIrpf.getInkindBase())
					.set(SALARY.IRPF_BASE,	irpfBase)
					.set(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS, employeeIrpf.getEmployeeSSQuote())
					.set(SALARY.TOTAL_IRPF, employeeIrpf.getTotalIrpf())
					.set(SALARY.CHARGE_DATE, parseDateToSQL(endDate))
					.returning(SALARY.ID)
					.fetchOne();
				
				Integer newSalaryId = salaryRecord.getId();
				
				dslContext.insertInto(SALARY_DATA)
					.set(SALARY_DATA.DOMAIN, domainId)
					.set(SALARY_DATA.NAME, "PORCENTAJE_IRPF")
					.set(SALARY_DATA.EXPRESSION, employeeIrpf.getIrpfPercent().toString())
					.set(SALARY_DATA.START_DATE, parseDateToSQL(startDate))
					.set(SALARY_DATA.END_DATE, parseDateToSQL(endDate))
					.set(SALARY_DATA.SALARY, newSalaryId)
					.execute();
				
				dslContext.insertInto(SALARY_PAYMENT)
					.set(SALARY_PAYMENT.DOMAIN, domainId)
					.set(SALARY_PAYMENT.SALARY, newSalaryId)
					.set(SALARY_PAYMENT.TYPE, (byte)1)
					.set(SALARY_PAYMENT.DESCRIPTION, "RETRIBUCI\u00d3N NO INCLUIDA OTROS APARTADOS (M190)")
					.set(SALARY_PAYMENT.AMOUNT, employeeIrpf.getMoneyBase())
					.set(SALARY_PAYMENT.IRPF, employeeIrpf.getMoneyBase())
					.set(SALARY_PAYMENT.QUOTE, employeeIrpf.getMoneyQuote())
					.execute();
				
				dslContext.insertInto(SALARY_PAYMENT)
					.set(SALARY_PAYMENT.DOMAIN, domainId)
					.set(SALARY_PAYMENT.SALARY, newSalaryId)
					.set(SALARY_PAYMENT.TYPE, (byte)13)
					.set(SALARY_PAYMENT.DESCRIPTION, "RETRIBUCI\u00d3N EN ESPECIE (M190)")
					.set(SALARY_PAYMENT.AMOUNT, employeeIrpf.getInkindBase())
					.set(SALARY_PAYMENT.IRPF, employeeIrpf.getInkindBase())
					.set(SALARY_PAYMENT.QUOTE, employeeIrpf.getInkindQuote())
					.execute();
				
			}
		}
	}

	// --------------------------------------------- Auxiliar Methods
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = BigDecimal.valueOf(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	private static java.sql.Date parseDateToSQL(java.util.Date dateJava) {
		if(null == dateJava)
			return null;
		
		DateUtils.resetTime(dateJava);
		return new java.sql.Date(dateJava.getTime());
	}

	private static java.util.Date parseDateToJava(Date dateSQL) {
		if(null == dateSQL)
			return null;
		
		java.util.Date dateJava = new java.util.Date(dateSQL.getTime());
		DateUtils.resetTime(dateJava);
		
		return dateJava;
	}

}
