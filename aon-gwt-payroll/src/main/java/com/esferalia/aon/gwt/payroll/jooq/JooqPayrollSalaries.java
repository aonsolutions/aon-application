package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryBonus.SALARY_BONUS;
import static com.esferalia.aon.jooq.tables.SalaryCost.SALARY_COST;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SalaryEmbargo.SALARY_EMBARGO;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
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
	//									CONTRACT BY REGISTRY ID
	// --------------------------------------------------------------------------------------------
	
	public static Integer getContractByRegistry(Connection connection, Integer registryId) {
		return getContractByRegistryDB(DSL.using(connection, getDefaultSettings()), registryId);
	}

	private static Integer getContractByRegistryDB(DSLContext dslContext, Integer registryId) {
		// Get current date
		Date currentDate = new Date(new java.util.Date().getTime());
		
		Integer contractId = dslContext.select(CONTRACT.ID).from(CONTRACT)
			.where(CONTRACT.PERSON.eq(registryId))
			.and(CONTRACT.END_DATE.isNull().or(CONTRACT.END_DATE.ge(currentDate)))
			.fetchOne(CONTRACT.ID);
		
		return contractId;
	}

	// --------------------------------------------------------------------------------------------
	//									SALARY METHODS
	// --------------------------------------------------------------------------------------------
	
	public static List<SalaryInfo> getSalaries(Connection connection, SalaryInfoFilter filter) {
		return getSalariesDB(DSL.using(connection, getDefaultSettings()), filter);
	}
	
	// --------------------------------------------------------------------------------------------
	//									WORKPLACE METHODS
	// --------------------------------------------------------------------------------------------
	
	public static WorkplaceEmployees getWorkplaceActiveEmployees(Connection connection, Integer workplaceId) {
		return getWorkplaceActiveEmployeesDB(DSL.using(connection, getDefaultSettings()), workplaceId);
	}
	
	// --------------------------------------------------------------------------------------------
	//									ENTERPRISE METHODS
	// --------------------------------------------------------------------------------------------
	
	public static List<EmployeeInfo> getEnterpriseActiveEmployees(Connection connection, Integer enterpriseId) {
		return getEnterpriseActiveEmployeesDB(DSL.using(connection, getDefaultSettings()), enterpriseId);
	}
	
	// --------------------------------------------------------------------------------------------
	//									DELETE SALARY METHOD
	// --------------------------------------------------------------------------------------------

	public static void deleteSalaries(Connection connection, ArrayList<Integer> ids) {
		deleteSalariesDB(DSL.using(connection, getDefaultSettings()), ids);
	}

	// --------------------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------------------
	
	// --------------------------------------------------------------------------------------------
	//									SALARY METHODS IMPL
	// --------------------------------------------------------------------------------------------
	
	private static List<SalaryInfo> getSalariesDB(DSLContext dslContext, SalaryInfoFilter filter) {
		List<SalaryInfo> salaries = new ArrayList<SalaryInfo>();
		
		// SalaryType
		Condition salaryTypeCondition = getSalaryTypeCondition(filter);
		
		// Contracts
		Condition contractsCondition = getContractCondition(dslContext, filter);
		
		// Dates
		Condition datesCondition = getDatesCondition(filter);
		
		// Get salaries
		Result<Record> salaryRecords = dslContext.select().from(SALARY)
				.where(contractsCondition)
				.and(salaryTypeCondition)
				.and(datesCondition)
				.orderBy(SALARY.END_DATE.desc())
				.fetch();
		
		// Enterprise Id
		Integer enterpriseId = null;
		
		if(salaryRecords.isNotEmpty()) {
			Integer contractId = salaryRecords.get(0).get(SALARY.CONTRACT);
			enterpriseId = getEnterpriseId(dslContext, contractId);
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
			
			Integer contractId = salaryRecord.get(SALARY.CONTRACT);
			
			Record workplaceRecord = getWorkplaceRecord(dslContext, contractId);
			
			String workplaceName = workplaceRecord.get(WORKPLACE.DESCRIPTION);
			Integer workplaceId =  workplaceRecord.get(WORKPLACE.ID);
			
			// Workplace
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
	//									AUXLIAR METHODS
	// --------------------------------------------------------------------------------------------

	private static Condition getDatesCondition(SalaryInfoFilter filter) {
		Condition condition = DSL.noCondition();
		
		if(null != filter.getDateTillT()) {
			Date startDate = new Date(filter.getDateTillT().getTime());
			Date endDate = new Date(filter.getDateTTo().getTime());
			condition = SALARY.END_DATE.between(startDate, endDate);
		}
		
		return condition;
	}

	private static Condition getContractCondition(DSLContext dslContext, SalaryInfoFilter filter) {
		Condition condition = DSL.noCondition();
		
		if(isNumberValid(filter.getEnterpriseId())) {
			condition = SALARY.CONTRACT.in(
					dslContext.select(CONTRACT.ID).from(CONTRACT)
					.where(CONTRACT.ID.ge(0))
					.and(CONTRACT.WORKPLACE.in(
							dslContext.select(WORKPLACE.ID).from(WORKPLACE)
								.where(WORKPLACE.ID.gt(0))
								.and(WORKPLACE.ENTERPRISE.eq(filter.getEnterpriseId()))
					)));
		} else if(isNumberValid(filter.getWorkplaceId())) {
			condition = SALARY.CONTRACT.in(
					dslContext.select(CONTRACT.ID).from(CONTRACT)
					.where(CONTRACT.ID.ge(0))
					.and(CONTRACT.WORKPLACE.eq(filter.getWorkplaceId())));
		} else if(isNumberValid(filter.getEmployeeId())) 
			condition = SALARY.CONTRACT.eq(filter.getEmployeeId());
		
		return condition;
	}

	private static Condition getSalaryTypeCondition(SalaryInfoFilter filter) {
		Condition condition = DSL.noCondition();
		
		if(null != filter.getSalaryType() && -1 != filter.getSalaryType()) 
			condition = SALARY.TYPE.eq((byte)filter.getSalaryType().intValue());
		
		return condition;
	}
	
	private static Record getWorkplaceRecord(DSLContext dslContext, Integer contractId) {
		Record workplaceRecord = dslContext.select()
				.from(WORKPLACE)
				.where(WORKPLACE.ID.eq(
						dslContext.select(CONTRACT.WORKPLACE).from(CONTRACT)
						.where(CONTRACT.ID.eq(contractId))))
				.fetchOne();
		
		
		return workplaceRecord;
	}

	private static Integer getEnterpriseId(DSLContext dslContext, Integer contractId) {
		Integer enterpriseId =  dslContext.select(WORKPLACE.ENTERPRISE).from(WORKPLACE)
				.where(WORKPLACE.ID.eq(
						dslContext.select(CONTRACT.WORKPLACE).from(CONTRACT)
							.where(CONTRACT.ID.eq(contractId))
				)).fetchOne()
				.get(WORKPLACE.ENTERPRISE);
		
		return enterpriseId;
	}

	private static boolean isNumberValid(Integer number) {
		return number != null;
	}

	// --------------------------------------------------------------------------------------------
	//									WORKPLACE METHODS IMPL
	// --------------------------------------------------------------------------------------------
	
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
	
	// --------------------------------------------------------------------------------------------
	//									ENTERPRISE METHODS IMPL
	// --------------------------------------------------------------------------------------------
	
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
	
	// --------------------------------------------------------------------------------------------
	//									DELETE SALARY METHOD IMPL
	// --------------------------------------------------------------------------------------------
	
	private static void deleteSalariesDB(DSLContext dslContext, ArrayList<Integer> ids) {
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
	}
	
	
}
