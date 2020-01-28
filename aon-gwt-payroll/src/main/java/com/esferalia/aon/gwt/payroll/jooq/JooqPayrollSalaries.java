package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryBonus.SALARY_BONUS;
import static com.esferalia.aon.jooq.tables.SalaryCost.SALARY_COST;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SalaryEmbargo.SALARY_EMBARGO;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.Period;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfoFilter;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceEmployees;

public class JooqPayrollSalaries {

	private static Settings SETTINGS = null;
	
	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}

	// --------------------------------------------------------------------------------------------
	//									EMPLOYEE METHODS
	// --------------------------------------------------------------------------------------------
	
	public static List<SalaryInfo> getEmployeeSalaries(Connection connection, Integer employeeId) {
		return getEmployeeSalariesDB(DSL.using(connection, getDefaultSettings()), employeeId);
	}
	
	public static List<SalaryInfo> getFilterEmployeeSalaries(Connection connection, SalaryInfoFilter filter) {
		return getFilterEmployeeSalariesDB(DSL.using(connection, getDefaultSettings()), filter);
	}
	
	// --------------------------------------------------------------------------------------------
	//									WORKPLACE METHODS
	// --------------------------------------------------------------------------------------------
	
	public static WorkplaceEmployees getWorkplaceActiveEmployees(Connection connection, Integer workplaceId) {
		return getWorkplaceActiveEmployeesDB(DSL.using(connection, getDefaultSettings()), workplaceId);
	}
	
	public static List<SalaryInfo> getWorkplaceSalaries(Connection connection, Integer workplaceId) {
		return getWorkplaceSalariesDB(DSL.using(connection, getDefaultSettings()), workplaceId);
	}
	
	public static List<SalaryInfo> getFilterWorkplaceSalaries(Connection connection, Integer workplaceId,
			SalaryInfoFilter filter) {
		return getFilterWorkplaceSalariesDB(DSL.using(connection, getDefaultSettings()), workplaceId, filter);
	}

	// --------------------------------------------------------------------------------------------
	//									ENTERPRISE METHODS
	// --------------------------------------------------------------------------------------------
	
	public static List<EmployeeInfo> getEnterpriseActiveEmployees(Connection connection, Integer enterpriseId) {
		return getEnterpriseActiveEmployeesDB(DSL.using(connection, getDefaultSettings()), enterpriseId);
	}
	
	public static List<SalaryInfo> getEnterpriseSalaries(Connection connection, Integer enterpriseId) {
		return getEnterpriseSalariesDB(DSL.using(connection, getDefaultSettings()), enterpriseId);
	}
	
	public static List<SalaryInfo> getFilterEnterpriseSalaries(Connection connection, Integer enterpriseId,
			SalaryInfoFilter filter) {
		return getFilterEnterpriseSalariesDB(DSL.using(connection, getDefaultSettings()), enterpriseId, filter);
	}
	
	// --------------------------------------------------------------------------------------------
	//									SALARY METHODS
	// --------------------------------------------------------------------------------------------

	public static String deleteSalaries(Connection connection, ArrayList<Integer> ids) {
		return deleteSalariesDB(DSL.using(connection, getDefaultSettings()), ids);
	}
	

	// --------------------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------------------
	
	// --------------------------------------------------------------------------------------------
	//									EMPLOYEE METHODS
	// --------------------------------------------------------------------------------------------
	
	/**
	 * @param dslContext
	 * @param employeeId = contractId
	 * @return List of employee salaries
	 */
	private static List<SalaryInfo> getEmployeeSalariesDB(DSLContext dslContext, Integer employeeId) {
		List<SalaryInfo> salaries = new ArrayList<SalaryInfo>();
		
		Integer enterpriseId =  dslContext.select(WORKPLACE.ENTERPRISE).from(WORKPLACE)
				.where(WORKPLACE.ID.eq(
						dslContext.select(CONTRACT.WORKPLACE).from(CONTRACT)
							.where(CONTRACT.ID.eq(employeeId))
				)).fetchOne()
				.get(WORKPLACE.ENTERPRISE);
		
		Result<Record> salaryRecords = dslContext.select().from(SALARY)
				.where(SALARY.CONTRACT.eq(employeeId))
				.orderBy(SALARY.END_DATE.desc())
				.fetch();
		
		for(Record salaryRecord : salaryRecords) {
			
			SalaryInfo salaryInfo = new SalaryInfo();
			salaryInfo.setId(salaryRecord.get(SALARY.ID));
			salaryInfo.setDomain(salaryRecord.get(SALARY.DOMAIN));
			salaryInfo.setContract(salaryRecord.get(SALARY.CONTRACT));
			salaryInfo.setStartDate(salaryRecord.get(SALARY.START_DATE));
			salaryInfo.setEndDate(salaryRecord.get(SALARY.END_DATE));
			salaryInfo.setType(Salary.Type.values()[salaryRecord.get(SALARY.TYPE)]);
			salaryInfo.setEnterpriseName(salaryRecord.get(SALARY.ENTERPRISE_NAME));
			salaryInfo.setEmployeeName(salaryRecord.get(SALARY.EMPLOYEE_NAME));
			salaryInfo.setTotalPayment(salaryRecord.get(SALARY.TOTAL_PAYMENT));
			salaryInfo.setTotalDeduction(salaryRecord.get(SALARY.TOTAL_DEDUCTION));
			salaryInfo.setTotalLiquid(salaryRecord.get(SALARY.TOTAL_LIQUID));
			
			Record workplaceRecord = dslContext.select()
					.from(WORKPLACE)
					.where(WORKPLACE.ID.eq(
							dslContext.select(CONTRACT.WORKPLACE).from(CONTRACT)
							.where(CONTRACT.ID.eq(salaryRecord.get(SALARY.CONTRACT)))))
					.fetchOne();
			
			salaryInfo.setWorkplaceName(workplaceRecord.get(WORKPLACE.DESCRIPTION));
			salaryInfo.setWorkplaceId(workplaceRecord.get(WORKPLACE.ID));
			
			// Enterprise ID
			salaryInfo.setEnterpriseId(enterpriseId);
			
			// Add to salaries list
			salaries.add(salaryInfo);

		}
		
		return salaries;
	}
	
	/**
	 * @param dslContext
	 * @param employeeId = contractId
	 * @param filter
	 * @return List of salaries
	 */
	private static List<SalaryInfo> getFilterEmployeeSalariesDB(DSLContext dslContext, SalaryInfoFilter filter) {
		List<SalaryInfo> salaries = new ArrayList<SalaryInfo>();
		
		Result<Record> salaryRecords = null;
		
		// SalaryType
		Condition salaryTypeCondition = SALARY.TYPE.ne((byte)5); // Always TRUE
		if(null != filter.getSalaryType()) {
			salaryTypeCondition = SALARY.TYPE.eq((byte)filter.getSalaryType().intValue());
		}
		
		// Contracts condition
		Condition contractsCondition = DSL.noCondition();
		if(null != filter.getEnterpriseId()) {
			contractsCondition = SALARY.CONTRACT.in(
					dslContext.select(CONTRACT.ID).from(CONTRACT)
					.where(CONTRACT.ID.ge(0))
					.and(CONTRACT.WORKPLACE.in(
							dslContext.select(WORKPLACE.ID).from(WORKPLACE)
								.where(WORKPLACE.ID.gt(0))
								.and(WORKPLACE.ENTERPRISE.eq(filter.getEnterpriseId()))
					)));
		} else if(null != filter.getWorkplaceId()) {
			contractsCondition = SALARY.CONTRACT.in(
					dslContext.select(CONTRACT.ID).from(CONTRACT)
					.where(CONTRACT.ID.ge(0))
					.and(CONTRACT.WORKPLACE.eq(filter.getWorkplaceId())));
		} else if(null != filter.getEmployeeId()) {
			contractsCondition = SALARY.CONTRACT.eq(filter.getEmployeeId());
		}
		
		
		if(filter.isNoDateFilter()) {
			salaryRecords = dslContext.select().from(SALARY)
					.where(contractsCondition)
					.and(salaryTypeCondition)
					.orderBy(SALARY.END_DATE.desc())
					.fetch();
		} else if(filter.isDateMYFilter()) {
			// TODO: PORQUE NO ME BUSCA BIEN LAS FECHAS A PESAR DE LLEGARLE BIEN
			Date startDate = new Date(filter.getDateMY().getYear(), filter.getDateMY().getMonth(), filter.getDateMY().getDate()+1);
			java.util.Date lastDayOfMonth = DateUtils.addDays2Date(DateUtils.getLastDayOfMonth(filter.getDateMY()), 1);
			Date endDate = new Date(lastDayOfMonth.getYear(), lastDayOfMonth.getMonth(), lastDayOfMonth.getDate());
			
			salaryRecords = dslContext.select().from(SALARY)
					.where(contractsCondition)
					.and(salaryTypeCondition)
					.and(SALARY.END_DATE.between(startDate, endDate))
					.orderBy(SALARY.END_DATE.desc())
					.fetch();
		} else if(filter.isDateTTFilter()) {
			// TODO: PORQUE NO ME BUSCA BIEN LAS FECHAS
			Period filterPeriod = createPeriod(filter.getDateTillT(), filter.getDateTTo());
			Date startDate = new Date(filterPeriod.getStart().getTime());
			Date endDate = new Date(filterPeriod.getEnd().getTime());
			
			salaryRecords = dslContext.select().from(SALARY)
					.where(contractsCondition)
					.and(salaryTypeCondition)
					.and(SALARY.END_DATE.between(startDate, endDate))
					.orderBy(SALARY.END_DATE.desc())
					.fetch();
		}
		
		for(Record salaryRecord : salaryRecords) {
			
			SalaryInfo salaryInfo = new SalaryInfo();
			salaryInfo.setId(salaryRecord.get(SALARY.ID));
			salaryInfo.setDomain(salaryRecord.get(SALARY.DOMAIN));
			salaryInfo.setContract(salaryRecord.get(SALARY.CONTRACT));
			salaryInfo.setStartDate(salaryRecord.get(SALARY.START_DATE));
			salaryInfo.setEndDate(salaryRecord.get(SALARY.END_DATE));
			salaryInfo.setType(Salary.Type.values()[salaryRecord.get(SALARY.TYPE)]);
			salaryInfo.setEnterpriseName(salaryRecord.get(SALARY.ENTERPRISE_NAME));
			salaryInfo.setEmployeeName(salaryRecord.get(SALARY.EMPLOYEE_NAME));
			salaryInfo.setTotalPayment(salaryRecord.get(SALARY.TOTAL_PAYMENT));
			salaryInfo.setTotalDeduction(salaryRecord.get(SALARY.TOTAL_DEDUCTION));
			salaryInfo.setTotalLiquid(salaryRecord.get(SALARY.TOTAL_LIQUID));
			
			Record workplaceRecord = dslContext.select()
					.from(WORKPLACE)
					.where(WORKPLACE.ID.eq(
							dslContext.select(CONTRACT.WORKPLACE).from(CONTRACT)
							.where(CONTRACT.ID.eq(salaryRecord.get(SALARY.CONTRACT)))))
					.fetchOne();
			
			salaryInfo.setWorkplaceName(workplaceRecord.get(WORKPLACE.DESCRIPTION));
			salaryInfo.setWorkplaceId(workplaceRecord.get(WORKPLACE.ID));
			
			// Enterprise ID
			Integer enterpriseId =  dslContext.select(WORKPLACE.ENTERPRISE).from(WORKPLACE)
					.where(WORKPLACE.ID.eq(
							dslContext.select(CONTRACT.WORKPLACE).from(CONTRACT)
								.where(CONTRACT.ID.eq(salaryRecord.get(SALARY.CONTRACT)))
					)).fetchOne()
					.get(WORKPLACE.ENTERPRISE);
			salaryInfo.setEnterpriseId(enterpriseId);
			
			// Add to salaries list
			salaries.add(salaryInfo);

		}
		
		return salaries;
	}
	
	private static Period createPeriod(java.util.Date dateTillT, java.util.Date dateTTo) {
		Period period = new Period();
		
		java.util.Date startDate = DateUtils.copyDateOnly(dateTillT);
		java.util.Date endDate = DateUtils.copyDateOnly(dateTTo);
		
		if(dateTillT.after(dateTTo)) {
			startDate = DateUtils.copyDateOnly(dateTTo);
			endDate = DateUtils.copyDateOnly(dateTillT);
		}
		
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(startDate);
		startCal.set(Calendar.DAY_OF_MONTH, 1);
		
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(endDate);
		endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
		
		period.setStart(startCal.getTime());
		period.setEnd(endCal.getTime());
		
		return period;
	}

	// --------------------------------------------------------------------------------------------
	//									WORKPLACE METHODS
	// --------------------------------------------------------------------------------------------
	
	/**
	 * @param dslContext
	 * @param workplaceId
	 * @return WorkplaceEmployees
	 */
	private static WorkplaceEmployees getWorkplaceActiveEmployeesDB(DSLContext dslContext, Integer workplaceId) {
		WorkplaceEmployees workplaceEmployees = new WorkplaceEmployees();
		
		Result<Record> contractRecords = dslContext.select().from(CONTRACT)
				.where(CONTRACT.ID.gt(0))
				.and(CONTRACT.WORKPLACE.eq(workplaceId))
				.fetch();
		
		for(Record contractRecord : contractRecords){
			Record personRecord = dslContext.select().from(PERSON)
					.where(PERSON.REGISTRY.eq(contractRecord.get(CONTRACT.PERSON)))
					.fetchOne();
			
			Record registryRecord = dslContext.select().from(REGISTRY)
					.where(REGISTRY.ID.eq(contractRecord.get(CONTRACT.PERSON)))
					.fetchOne();
			
			workplaceEmployees.addEmployee(
					contractRecord.get(CONTRACT.ID), 
					personRecord.get(PERSON.NAME), 
					personRecord.get(PERSON.FIRST_SURNAME) + " " + personRecord.get(PERSON.SECOND_SURNAME), 
					registryRecord.get(REGISTRY.DOCUMENT), 
					personRecord.get(PERSON.SOCIAL_SECURITY_NUM));
		}
		
		return workplaceEmployees;
	}
	
	/**
	 * @param dslContext
	 * @param workplaceId
	 * @return List of workplace salaries
	 */
	private static List<SalaryInfo> getWorkplaceSalariesDB(DSLContext dslContext, Integer workplaceId) {
		List<SalaryInfo> salaries = new ArrayList<SalaryInfo>();
		
		Integer enterpriseId =  dslContext.select(WORKPLACE.ENTERPRISE).from(WORKPLACE)
				.where(WORKPLACE.ID.eq(workplaceId))
				.fetchOne()
				.get(WORKPLACE.ENTERPRISE);
		
		Result<Record> salaryRecords = dslContext.select().from(SALARY)
				.where(SALARY.CONTRACT.in(
						dslContext.select(CONTRACT.ID).from(CONTRACT)
							.where(CONTRACT.ID.ge(0))
							.and(CONTRACT.WORKPLACE.eq(workplaceId))
				))
				.orderBy(SALARY.END_DATE.desc())
				.fetch();
		
		for(Record salaryRecord : salaryRecords) {
			
			SalaryInfo salaryInfo = new SalaryInfo();
			salaryInfo.setId(salaryRecord.get(SALARY.ID));
			salaryInfo.setDomain(salaryRecord.get(SALARY.DOMAIN));
			salaryInfo.setContract(salaryRecord.get(SALARY.CONTRACT));
			salaryInfo.setStartDate(salaryRecord.get(SALARY.START_DATE));
			salaryInfo.setEndDate(salaryRecord.get(SALARY.END_DATE));
			salaryInfo.setType(Salary.Type.values()[salaryRecord.get(SALARY.TYPE)]);
			salaryInfo.setEnterpriseName(salaryRecord.get(SALARY.ENTERPRISE_NAME));
			salaryInfo.setEmployeeName(salaryRecord.get(SALARY.EMPLOYEE_NAME));
			salaryInfo.setTotalPayment(salaryRecord.get(SALARY.TOTAL_PAYMENT));
			salaryInfo.setTotalDeduction(salaryRecord.get(SALARY.TOTAL_DEDUCTION));
			salaryInfo.setTotalLiquid(salaryRecord.get(SALARY.TOTAL_LIQUID));
			
			String workplaceName = dslContext.select(WORKPLACE.DESCRIPTION)
					.from(WORKPLACE)
					.where(WORKPLACE.ID.eq(
							dslContext.select(CONTRACT.WORKPLACE).from(CONTRACT)
							.where(CONTRACT.ID.eq(salaryRecord.get(SALARY.CONTRACT)))))
					.fetchOne()
					.get(WORKPLACE.DESCRIPTION);
			
			salaryInfo.setWorkplaceName(workplaceName);
			salaryInfo.setWorkplaceId(workplaceId);
			
			// Enterprise ID
			salaryInfo.setEnterpriseId(enterpriseId);
			
			// Add to salaries list
			salaries.add(salaryInfo);

		}
		
		return salaries;
	}

	/**
	 * @param dslContext
	 * @param workplaceId
	 * @param filter
	 * @return List of salaries
	 */
	private static List<SalaryInfo> getFilterWorkplaceSalariesDB(DSLContext dslContext, Integer workplaceId,
			SalaryInfoFilter filter) {
		List<SalaryInfo> salaries = new ArrayList<SalaryInfo>();
		
		Integer enterpriseId =  dslContext.select(WORKPLACE.ENTERPRISE).from(WORKPLACE)
				.where(WORKPLACE.ID.eq(workplaceId))
				.fetchOne()
				.get(WORKPLACE.ENTERPRISE);
		
		Result<Record> salaryRecords = null;
		
		if(filter.isNoDateFilter()) {
			salaryRecords = dslContext.select().from(SALARY)
					.where(SALARY.CONTRACT.in(
							dslContext.select(CONTRACT.ID).from(CONTRACT)
								.where(CONTRACT.ID.ge(0))
								.and(CONTRACT.WORKPLACE.eq(workplaceId))
					))
					.orderBy(SALARY.END_DATE.desc())
					.fetch();
		} else if(filter.isDateMYFilter()) {
			Date startDate = new Date(filter.getDateMY().getYear(), filter.getDateMY().getMonth(), filter.getDateMY().getDate()+1);
			java.util.Date lastDayOfMonth = DateUtils.addDays2Date(DateUtils.getLastDayOfMonth(filter.getDateMY()), 1);
			Date endDate = new Date(lastDayOfMonth.getYear(), lastDayOfMonth.getMonth(), lastDayOfMonth.getDate());
			
			salaryRecords = dslContext.select().from(SALARY)
					.where(SALARY.CONTRACT.in(
							dslContext.select(CONTRACT.ID).from(CONTRACT)
								.where(CONTRACT.ID.ge(0))
								.and(CONTRACT.WORKPLACE.eq(workplaceId))
					))
					.and(SALARY.END_DATE.between(startDate, endDate))
					.orderBy(SALARY.END_DATE.desc())
					.fetch();
		} else if(filter.isDateTTFilter()) {
			// TODO: PORQUE NO ME BUSCA BIEN LAS FECHAS
			Date startDate = new Date(filter.getDateTillT().getYear(), filter.getDateTillT().getMonth(), filter.getDateTillT().getDate()+1);
			java.util.Date lastDayOfMonth = DateUtils.addDays2Date(DateUtils.getLastDayOfMonth(filter.getDateTTo()), 1);
			Date endDate = new Date(lastDayOfMonth.getYear(), lastDayOfMonth.getMonth(), lastDayOfMonth.getDate());
						
			salaryRecords = dslContext.select().from(SALARY)
					.where(SALARY.CONTRACT.in(
							dslContext.select(CONTRACT.ID).from(CONTRACT)
								.where(CONTRACT.ID.ge(0))
								.and(CONTRACT.WORKPLACE.eq(workplaceId))
					))
					.and(SALARY.END_DATE.between(startDate, endDate))
					.orderBy(SALARY.END_DATE.desc())
					.fetch();			
		}
		
		for(Record salaryRecord : salaryRecords) {
			
			SalaryInfo salaryInfo = new SalaryInfo();
			salaryInfo.setId(salaryRecord.get(SALARY.ID));
			salaryInfo.setDomain(salaryRecord.get(SALARY.DOMAIN));
			salaryInfo.setContract(salaryRecord.get(SALARY.CONTRACT));
			salaryInfo.setStartDate(salaryRecord.get(SALARY.START_DATE));
			salaryInfo.setEndDate(salaryRecord.get(SALARY.END_DATE));
			salaryInfo.setType(Salary.Type.values()[salaryRecord.get(SALARY.TYPE)]);
			salaryInfo.setEnterpriseName(salaryRecord.get(SALARY.ENTERPRISE_NAME));
			salaryInfo.setEmployeeName(salaryRecord.get(SALARY.EMPLOYEE_NAME));
			salaryInfo.setTotalPayment(salaryRecord.get(SALARY.TOTAL_PAYMENT));
			salaryInfo.setTotalDeduction(salaryRecord.get(SALARY.TOTAL_DEDUCTION));
			salaryInfo.setTotalLiquid(salaryRecord.get(SALARY.TOTAL_LIQUID));
			
			String workplaceName = dslContext.select(WORKPLACE.DESCRIPTION)
					.from(WORKPLACE)
					.where(WORKPLACE.ID.eq(
							dslContext.select(CONTRACT.WORKPLACE).from(CONTRACT)
							.where(CONTRACT.ID.eq(salaryRecord.get(SALARY.CONTRACT)))))
					.fetchOne()
					.get(WORKPLACE.DESCRIPTION);
			
			salaryInfo.setWorkplaceName(workplaceName);
			salaryInfo.setWorkplaceId(workplaceId);
			
			// Enterprise ID
			salaryInfo.setEnterpriseId(enterpriseId);
			
			// Add to salaries list
			salaries.add(salaryInfo);

		}
		
		return salaries;
	}

	// --------------------------------------------------------------------------------------------
	//									ENTERPRISE METHODS
	// --------------------------------------------------------------------------------------------
	
	/**
	 * @param dslContext
	 * @param enterpriseId
	 * @return List of employees
	 */
	private static List<EmployeeInfo> getEnterpriseActiveEmployeesDB(DSLContext dslContext, Integer enterpriseId) {
		List<EmployeeInfo> enterpriseEmployees = new ArrayList<EmployeeInfo>();
		
		Result<Record> contractRecords = dslContext.select().from(CONTRACT)
				.where(CONTRACT.ID.gt(0))
				.and(CONTRACT.WORKPLACE.in(
						dslContext.select(WORKPLACE.ID).from(WORKPLACE)
							.where(WORKPLACE.ID.gt(0))
							.and(WORKPLACE.ENTERPRISE.eq(enterpriseId))
				))
				.fetch();
		
		for(Record contractRecord : contractRecords){
			Record personRecord = dslContext.select().from(PERSON)
					.where(PERSON.REGISTRY.eq(contractRecord.get(CONTRACT.PERSON)))
					.fetchOne();
			
			Record registryRecord = dslContext.select().from(REGISTRY)
					.where(REGISTRY.ID.eq(contractRecord.get(CONTRACT.PERSON)))
					.fetchOne();
			
			EmployeeInfo employee = new EmployeeInfo();
			employee.setEmployeeId(contractRecord.get(CONTRACT.ID));
			employee.setContractId(contractRecord.get(CONTRACT.ID));
			employee.setName(personRecord.get(PERSON.NAME));
			employee.setSurName(personRecord.get(PERSON.FIRST_SURNAME) + " " + personRecord.get(PERSON.SECOND_SURNAME));
			employee.setDocument(registryRecord.get(REGISTRY.DOCUMENT));
			employee.setSsNumber(personRecord.get(PERSON.SOCIAL_SECURITY_NUM));
			
			enterpriseEmployees.add(employee);
		}
		
		return enterpriseEmployees;
	}
	
	/**
	 * @param dslContext
	 * @param enterpriseId
	 * @return List of salaries
	 */
	private static List<SalaryInfo> getEnterpriseSalariesDB(DSLContext dslContext, Integer enterpriseId) {
		List<SalaryInfo> salaries = new ArrayList<SalaryInfo>();
		
		Result<Record> salaryRecords = dslContext.select().from(SALARY)
				.where(SALARY.CONTRACT.in(
						dslContext.select(CONTRACT.ID).from(CONTRACT)
							.where(CONTRACT.ID.ge(0))
							.and(CONTRACT.WORKPLACE.in(
									dslContext.select(WORKPLACE.ID).from(WORKPLACE)
										.where(WORKPLACE.ID.gt(0))
										.and(WORKPLACE.ENTERPRISE.eq(enterpriseId))
							))
				))
				.orderBy(SALARY.END_DATE.desc())
				.fetch();
		
		for(Record salaryRecord : salaryRecords) {
			
			SalaryInfo salaryInfo = new SalaryInfo();
			salaryInfo.setId(salaryRecord.get(SALARY.ID));
			salaryInfo.setDomain(salaryRecord.get(SALARY.DOMAIN));
			salaryInfo.setContract(salaryRecord.get(SALARY.CONTRACT));
			salaryInfo.setStartDate(salaryRecord.get(SALARY.START_DATE));
			salaryInfo.setEndDate(salaryRecord.get(SALARY.END_DATE));
			salaryInfo.setType(Salary.Type.values()[salaryRecord.get(SALARY.TYPE)]);
			salaryInfo.setEnterpriseName(salaryRecord.get(SALARY.ENTERPRISE_NAME));
			salaryInfo.setEmployeeName(salaryRecord.get(SALARY.EMPLOYEE_NAME));
			salaryInfo.setTotalPayment(salaryRecord.get(SALARY.TOTAL_PAYMENT));
			salaryInfo.setTotalDeduction(salaryRecord.get(SALARY.TOTAL_DEDUCTION));
			salaryInfo.setTotalLiquid(salaryRecord.get(SALARY.TOTAL_LIQUID));
			
			Record workplaceRecord = dslContext.select()
					.from(WORKPLACE)
					.where(WORKPLACE.ID.eq(
							dslContext.select(CONTRACT.WORKPLACE).from(CONTRACT)
							.where(CONTRACT.ID.eq(salaryRecord.get(SALARY.CONTRACT)))))
					.fetchOne();
			
			salaryInfo.setWorkplaceName(workplaceRecord.get(WORKPLACE.DESCRIPTION));
			salaryInfo.setWorkplaceId(workplaceRecord.get(WORKPLACE.ID));
			
			// Enterprise ID
			salaryInfo.setEnterpriseId(enterpriseId);
			
			// Add to salaries list
			salaries.add(salaryInfo);

		}
		
		return salaries;
	}
	
	/**
	 * @param dslContext
	 * @param enterpriseId
	 * @param filter
	 * @return List of salaries
	 */
	private static List<SalaryInfo> getFilterEnterpriseSalariesDB(DSLContext dslContext, Integer enterpriseId, SalaryInfoFilter filter) {
		
		List<SalaryInfo> salaries = new ArrayList<SalaryInfo>();
		
		Result<Record> salaryRecords = null;
		
		if(filter.isNoDateFilter()) {
			salaryRecords = dslContext.select().from(SALARY)
					.where(SALARY.CONTRACT.in(
							dslContext.select(CONTRACT.ID).from(CONTRACT)
								.where(CONTRACT.ID.ge(0))
								.and(CONTRACT.WORKPLACE.in(
										dslContext.select(WORKPLACE.ID).from(WORKPLACE)
											.where(WORKPLACE.ID.gt(0))
											.and(WORKPLACE.ENTERPRISE.eq(enterpriseId))
								))
					))
					.orderBy(SALARY.END_DATE.desc())
					.fetch();
		} else if(filter.isDateMYFilter()) {
			Date startDate = new Date(filter.getDateMY().getYear(), filter.getDateMY().getMonth(), filter.getDateMY().getDate()+1);
			java.util.Date lastDayOfMonth = DateUtils.addDays2Date(DateUtils.getLastDayOfMonth(filter.getDateMY()), 1);
			Date endDate = new Date(lastDayOfMonth.getYear(), lastDayOfMonth.getMonth(), lastDayOfMonth.getDate());
			
			salaryRecords = dslContext.select().from(SALARY)
					.where(SALARY.CONTRACT.in(
							dslContext.select(CONTRACT.ID).from(CONTRACT)
								.where(CONTRACT.ID.ge(0))
								.and(CONTRACT.WORKPLACE.in(
										dslContext.select(WORKPLACE.ID).from(WORKPLACE)
										.where(WORKPLACE.ID.gt(0))
										.and(WORKPLACE.ENTERPRISE.eq(enterpriseId))
								))
					))
					.and(SALARY.END_DATE.between(startDate, endDate))
					.orderBy(SALARY.END_DATE.desc())
					.fetch();
		} else if(filter.isDateTTFilter()) {
			// TODO: PORQUE NO ME BUSCA BIEN LAS FECHAS
			Date startDate = new Date(filter.getDateTillT().getYear(), filter.getDateTillT().getMonth(), filter.getDateTillT().getDate()+1);
			java.util.Date lastDayOfMonth = DateUtils.addDays2Date(DateUtils.getLastDayOfMonth(filter.getDateTTo()), 1);
			Date endDate = new Date(lastDayOfMonth.getYear(), lastDayOfMonth.getMonth(), lastDayOfMonth.getDate());
						
			salaryRecords = dslContext.select().from(SALARY)
					.where(SALARY.CONTRACT.in(
							dslContext.select(CONTRACT.ID).from(CONTRACT)
								.where(CONTRACT.ID.ge(0))
								.and(CONTRACT.WORKPLACE.in(
										dslContext.select(WORKPLACE.ID).from(WORKPLACE)
										.where(WORKPLACE.ID.gt(0))
										.and(WORKPLACE.ENTERPRISE.eq(enterpriseId))
								))
					))
					.and(SALARY.END_DATE.between(startDate, endDate))
					.orderBy(SALARY.END_DATE.desc())
					.fetch();			
		}
		
		for(Record salaryRecord : salaryRecords) {
			
			SalaryInfo salaryInfo = new SalaryInfo();
			salaryInfo.setId(salaryRecord.get(SALARY.ID));
			salaryInfo.setDomain(salaryRecord.get(SALARY.DOMAIN));
			salaryInfo.setContract(salaryRecord.get(SALARY.CONTRACT));
			salaryInfo.setStartDate(salaryRecord.get(SALARY.START_DATE));
			salaryInfo.setEndDate(salaryRecord.get(SALARY.END_DATE));
			salaryInfo.setType(Salary.Type.values()[salaryRecord.get(SALARY.TYPE)]);
			salaryInfo.setEnterpriseName(salaryRecord.get(SALARY.ENTERPRISE_NAME));
			salaryInfo.setEmployeeName(salaryRecord.get(SALARY.EMPLOYEE_NAME));
			salaryInfo.setTotalPayment(salaryRecord.get(SALARY.TOTAL_PAYMENT));
			salaryInfo.setTotalDeduction(salaryRecord.get(SALARY.TOTAL_DEDUCTION));
			salaryInfo.setTotalLiquid(salaryRecord.get(SALARY.TOTAL_LIQUID));
			
			Record workplaceRecord = dslContext.select()
					.from(WORKPLACE)
					.where(WORKPLACE.ID.eq(
							dslContext.select(CONTRACT.WORKPLACE).from(CONTRACT)
							.where(CONTRACT.ID.eq(salaryRecord.get(SALARY.CONTRACT)))))
					.fetchOne();
			
			salaryInfo.setWorkplaceName(workplaceRecord.get(WORKPLACE.DESCRIPTION));
			salaryInfo.setWorkplaceId(workplaceRecord.get(WORKPLACE.ID));
			
			// Enterprise ID
			salaryInfo.setEnterpriseId(enterpriseId);
			
			// Add to salaries list
			salaries.add(salaryInfo);

		}
		
		return salaries;
	}
	
	// --------------------------------------------------------------------------------------------
	//									SALARY METHODS
	// --------------------------------------------------------------------------------------------
	
	/**
	 * @param dslContext
	 * @param ids to be deleted
	 * @return
	 */
	private static String deleteSalariesDB(DSLContext dslContext, ArrayList<Integer> ids) {
		// Delete SalaryData, SalaryBonus, SalaryCost, SalaryPayment, SalaryDeduction, SalaryEmbargo, Salary
		for(Integer id : ids) {
			
			dslContext.delete(SALARY_DATA).where(SALARY_DATA.SALARY.eq(id)).execute();
			dslContext.delete(SALARY_BONUS).where(SALARY_BONUS.SALARY.eq(id)).execute();
			dslContext.delete(SALARY_COST).where(SALARY_COST.SALARY.eq(id)).execute();
			dslContext.delete(SALARY_PAYMENT).where(SALARY_PAYMENT.SALARY.eq(id)).execute();
			dslContext.delete(SALARY_DEDUCTION).where(SALARY_DEDUCTION.SALARY.eq(id)).execute();
			dslContext.delete(SALARY_EMBARGO).where(SALARY_EMBARGO.SALARY.eq(id)).execute();
			dslContext.delete(SALARY).where(SALARY.ID.eq(id)).execute();
		}
		return "";
	}
	
	
}
