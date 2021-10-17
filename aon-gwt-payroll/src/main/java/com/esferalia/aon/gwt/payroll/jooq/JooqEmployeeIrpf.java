package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;

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
	
	public static List<EmployeeIrpf> getEmployeeIrpf(Connection conn, Integer contractId, Date startDate) {
		return getEmployeeIrpf(DSL.using(conn, getDefaultSettings()), contractId, startDate);
	}

	private static List<EmployeeIrpf> getEmployeeIrpf(DSLContext dslContext, Integer contractId, Date startDate) {
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
					.where(SALARY.START_DATE.ge(parseDateToSQL(iteratorDate)))
					.and(SALARY.END_DATE.le(parseDateToSQL(endDate)))
					.and(SALARY.CONTRACT.eq(contractId))
					.fetch();
			
			if(salaryRecords.isNotEmpty()) {
				EmployeeIrpf employeeIrpf = new EmployeeIrpf();
				
				if(salaryRecords.size() > 1)
					System.err.println("More than one Salary for a month (contractId : " + contractId + ", Date : " + formatDate.format(iteratorDate) + ")");
				
				// Get SalaryRecord
				Record salaryRecord = salaryRecords.get(0);
				
				String salaryType = getSalaryType(salaryRecord.get(SALARY.TYPE));
				
				Double baseCgc = salaryRecord.get(SALARY.CGC_BASE);
				Double baseCgp = salaryRecord.get(SALARY.CGP_BASE);
				
				Double employeeSSQuote = salaryRecord.get(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS);
				Double totalIrpf = salaryRecord.get(SALARY.TOTAL_IRPF);
				
				Double moneyBase = salaryRecord.get(SALARY.MONEY_IRPF_BASE);
				Double inkindBase = salaryRecord.get(SALARY.INKIND_IRPF_BASE);
				
				// Get irpf percent
				Integer salaryId = salaryRecord.get(SALARY.ID);
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
//						moneyQuote = moneyBase * irpfPercent / 100;
//						inkindQuote = inkindBase * irpfPercent / 100;
					} catch (NumberFormatException e) {
						System.err.println("Can't format percent : " + irpfPercentRecord.get(SALARY_DATA.EXPRESSION));
					}
				}
				
				employeeIrpf.setDate(iteratorDate)
							.setSalaryId(salaryId)
							.setSalaryType(salaryType)
							.setMoneyBase(moneyBase)
							.setMoneyQuote(moneyQuote)
							.setInkindBase(inkindBase)
							.setInkindQuote(inkindQuote)
							.setIrpfPercent(irpfPercent)
							.setBaseCgc(baseCgc)
							.setBaseCgp(baseCgp)
							.setEmployeeSSQuote(employeeSSQuote)
							.setTotalIrpf(totalIrpf);
				
				employeeIrpfList.add(employeeIrpf);
			}
			
			// Add month to iteratorDate
			iteratorCalendar.add(Calendar.MONTH, 1);
			iteratorDate = iteratorCalendar.getTime();
		}
		
		return employeeIrpfList;
	}

	private static String getSalaryType(Byte type) {
		switch (type) {
		case (byte) 0:
			return "N\u00F3mina";
		case (byte) 1:
			return "Extra";
		case (byte) 2:
			return "Finiquito";
		case (byte) 3:
			return "Retraso";
		case (byte) 7:
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
	
	public static void setEmployeeIrpf(Connection conn, Integer domainId, Integer contractId, List<EmployeeIrpf> employeeIrpfs) {
		setEmployeeIrpf(DSL.using(conn, getDefaultSettings()), domainId, contractId, employeeIrpfs);
	}

	private static void setEmployeeIrpf(DSLContext dslContext, Integer domainId, Integer contractId, List<EmployeeIrpf> employeeIrpfs) {
		// Salary L131 equals salary type DB (byte) 7
		for(EmployeeIrpf employeeIrpf : employeeIrpfs) {
			if(!employeeIrpf.isNew() && !employeeIrpf.isDelete())
				continue;
			
			if(employeeIrpf.isDelete()) {
				dslContext.delete(SALARY_DATA).where(SALARY_DATA.SALARY.eq(employeeIrpf.getSalaryId())).execute();
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
				
				SalaryRecord salaryRecord = dslContext.insertInto(SALARY)
					.set(SALARY.DOMAIN, domainId)
					.set(SALARY.TYPE, (byte)7)
					.set(SALARY.CONTRACT, contractId)
					.set(SALARY.START_DATE, parseDateToSQL(startDate))
					.set(SALARY.END_DATE, parseDateToSQL(endDate))
					.set(SALARY.REGISTRATION, 0)
					.set(SALARY.TIME_UNITS, 30)
					.set(SALARY.ISSUE_DATE, parseDateToSQL(endDate))
					.set(SALARY.CGC_BASE, employeeIrpf.getBaseCgc())
					.set(SALARY.CGP_BASE, employeeIrpf.getBaseCgp())
					.set(SALARY.MONEY_IRPF_BASE, employeeIrpf.getMoneyBase())
					.set(SALARY.INKIND_IRPF_BASE, employeeIrpf.getInkindBase())
					.set(SALARY.IRPF_BASE,	employeeIrpf.getMoneyBase() + employeeIrpf.getInkindBase())
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
			}
		}
	}

	// --------------------------------------------- Auxiliar Methods
	
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
