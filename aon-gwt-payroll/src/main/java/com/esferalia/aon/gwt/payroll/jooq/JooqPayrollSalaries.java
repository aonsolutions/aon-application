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

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo;

public class JooqPayrollSalaries {

	private static Settings SETTINGS = null;
	
	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}

	public static List<SalaryInfo> getEmployeeSalaries(Connection connection, Integer employeeId) {
		return getEmployeeSalariesDB(DSL.using(connection, getDefaultSettings()), employeeId);
	}
	
	public static String deleteSalaries(Connection connection, ArrayList<Integer> ids) {
		return deleteSalariesDB(DSL.using(connection, getDefaultSettings()), ids);
	}

	/**
	 * @param dslContext
	 * @param employeeId = contractId
	 * @return List of employee salaries
	 */
	private static List<SalaryInfo> getEmployeeSalariesDB(DSLContext dslContext, Integer employeeId) {
		List<SalaryInfo> salaries = new ArrayList<SalaryInfo>();
		
		Result<Record> salaryRecords = dslContext.select().from(SALARY)
				.where(SALARY.CONTRACT.eq(employeeId))
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
			
			// Add to salaries list
			salaries.add(salaryInfo);

		}
		
		return salaries;
	}
	
	/**
	 * @param dslContext
	 * @param ids to be deleted
	 * @return
	 */
	private static String deleteSalariesDB(DSLContext dslContext, ArrayList<Integer> ids) {
		// Delete SalaryData, SalaryBonus, SalaryCost, SalaryPayment, SalaryDeduction, SalaryEmbargo, Salary
		for(Integer id : ids) {
			System.out.println(id);
			
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
