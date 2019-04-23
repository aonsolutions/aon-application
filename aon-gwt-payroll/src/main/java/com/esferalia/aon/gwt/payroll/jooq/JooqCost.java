package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.MainCost;

public class JooqCost {
	
	private static Settings SETTINGS = null;
	
	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}

	// ********************************************************************************************************************************************
	//													GENERATE SALARY LIST
	// ********************************************************************************************************************************************
	
	public static Stream<MainCost> getSalaries(String _domainName, String _enterpriseId, String _workplaceId, String _month, String _year) {
		//Connecction initialize
		Connection connection = null;
		
		//Costs - Stream and Lisy
		Stream<MainCost> stream;
		List<MainCost> costs = new ArrayList<MainCost>();
		
		//Finding Date
		Integer month = Integer.parseInt(_month);
		Integer year = Integer.parseInt(_year);
		Date startDate = new Date(year - 1900, month, 1);
		
		Date dateAux = new Date(year - 1900, month, 1);
		java.util.Date endDateAux = DateUtils.getLastDayOfMonth(dateAux);
		Date endDate = new Date(endDateAux.getTime());
		
		//Workplace and Enterprise
		Integer workplaceId = Integer.parseInt(_workplaceId);
		Integer enterpriseId = Integer.parseInt(_enterpriseId);
		
		try {
			connection = AonServletUtils.getConnection(_domainName);
			DSLContext dslContext = DSL.using(connection, getDefaultSettings());
			
			Result<Record> salaryRecords = null;
			if(0 == workplaceId) { //Enterprise Costs
				Result<Record1<Integer>> enterpriseContractRecords = dslContext.select(CONTRACT.ID).from(CONTRACT)
						.where(CONTRACT.WORKPLACE.in(
								dslContext.select(WORKPLACE.ID).from(WORKPLACE)
									.where(WORKPLACE.ENTERPRISE.eq(enterpriseId)))
						).orderBy(CONTRACT.WORKPLACE)
						.fetch();
				
				salaryRecords = dslContext.select().from(SALARY)
						.where(SALARY.CONTRACT.in(enterpriseContractRecords))
						.and(SALARY.CHARGE_DATE.between(startDate, endDate))
						.and(SALARY.TOTAL_LIQUID.ne(0.00))
						.fetch();
			}else { //Workplace Costs
				salaryRecords = dslContext.select().from(SALARY)
						.where(SALARY.CONTRACT.in(
							dslContext.select(CONTRACT.ID).from(CONTRACT).where(CONTRACT.WORKPLACE.eq(workplaceId)).fetch()
						))
						.and(SALARY.CHARGE_DATE.between(startDate, endDate))
						.and(SALARY.TOTAL_LIQUID.ne(0.00))
						.orderBy(SALARY.EMPLOYEE_NAME)
						.fetch();
			}
			
			//Enterprise Total
			Double acumulateTotalPayment = 0.00;
			Double acumulateEmployeeSS = 0.00;
			Double acumulateTotalIRPF = 0.00;
			Double acumulateDeductions = 0.00;
			Double acumulateTotalLiquid = 0.00;
			Double acumulateEnterpriseSS = 0.00;
			Double acumulateTotalCost = 0.00;
			Double acumulateTotalSS = 0.00;
			
			for(Record salary : salaryRecords) {
				//Workplace Name
				String workplaceName = dslContext.select(WORKPLACE.DESCRIPTION).from(WORKPLACE)
						.where(WORKPLACE.ID.eq(
								dslContext.select(CONTRACT.WORKPLACE).from(CONTRACT)
									.where(CONTRACT.ID.eq(salary.get(SALARY.CONTRACT)))
									.fetch(CONTRACT.WORKPLACE)
									.get(0)
						))
						.fetchOne(WORKPLACE.DESCRIPTION);
				
				//Deductions
				Result<Record> deductionRecords = dslContext.select().from(SALARY_DEDUCTION)
						.where(SALARY_DEDUCTION.SALARY.eq(salary.get(SALARY.ID)))
						.and(SALARY_DEDUCTION.TYPE.eq((byte)7).or(SALARY_DEDUCTION.TYPE.eq((byte)9)).or(SALARY_DEDUCTION.TYPE.eq((byte)11)))
						.fetch();
				
				Double deductions = 0.00;
				
				for(Record deduction: deductionRecords)
					deductions += deduction.get(SALARY_DEDUCTION.AMOUNT);
				
				//MainCost
				MainCost cost = new MainCost();
				cost.setEmployeeName(salary.get(SALARY.EMPLOYEE_NAME));
				cost.setWorkplaceName(workplaceName);
				cost.setSalaryType(parseSalaryType(salary.get(SALARY.TYPE)));
				
				cost.setTotalPayment(salary.get(SALARY.TOTAL_PAYMENT));
				acumulateTotalPayment += cost.getTotalPayment();
				
				cost.setEmployeeSS(salary.get(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS));
				acumulateEmployeeSS += cost.getEmployeeSS();
				
				cost.setTotalIRPF(salary.get(SALARY.TOTAL_IRPF));
				acumulateTotalIRPF += cost.getTotalIRPF();
				
				cost.setTotalDeductions(deductions);
				acumulateDeductions += deductions;
				
				cost.setTotalLiquid(salary.get(SALARY.TOTAL_LIQUID));
				acumulateTotalLiquid += cost.getTotalLiquid();
				
				cost.setEnterpriseSS(salary.get(SALARY.TOTAL_ENTERPRISE));
				acumulateEnterpriseSS += cost.getEnterpriseSS();
				
				cost.setTotalCost(salary.get(SALARY.TOTAL_PAYMENT) + salary.get(SALARY.TOTAL_ENTERPRISE));
				acumulateTotalCost += cost.getTotalCost();
				
				cost.setTotalSS(salary.get(SALARY.TOTAL_ENTERPRISE) + salary.get(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS));
				acumulateTotalSS += cost.getTotalSS();
				
				costs.add(cost);
			}
			
			//MainCost Enterprise
			MainCost cost = new MainCost();
			cost.setEmployeeName("TOTAL EMPRESA");
			cost.setWorkplaceName("");
			cost.setSalaryType("");
			cost.setTotalPayment(acumulateTotalPayment);
			cost.setEmployeeSS(acumulateEmployeeSS);
			cost.setTotalIRPF(acumulateTotalIRPF);
			cost.setTotalDeductions(acumulateDeductions);
			cost.setTotalLiquid(acumulateTotalLiquid);
			cost.setEnterpriseSS(acumulateEnterpriseSS);
			cost.setTotalCost(acumulateTotalCost);
			cost.setTotalSS(acumulateTotalSS);
			acumulateTotalSS += cost.getTotalSS();
			costs.add(cost);
			
			//List to Stream
			stream = costs.stream();
			
			return stream;
			
		}catch (SQLException e) {
			throw new RuntimeException(e);
		} 
	}

	private static String parseSalaryType(Byte salaryType) {
		switch (salaryType) {
		case ((byte)2):	
			return "Finiquito";
		case ((byte)3):
			return "Atrasos";
		default:
			return "Nomina";
		}
	}
}
