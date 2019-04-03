/**
 * 
 */
package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.lang.String.format;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;

import org.junit.Test;

import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedVariable;

import junit.framework.Assert;

/**
 * @author rtrepiana
 *
 */
public class SQLExtraHoursTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.000000001;

	@Test
	public void testProfessionalBase()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfMonth(getToday()), new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								ContractCode.C100.getValue()));
					}
				});
		
		
		
		addPayment(aonContext, 
				contract, 
				contract.getStartDate(), 
				contract.getEndDate(), 
				"SALARIO_BASE", 
				"1000.00 * DIAS_TRABAJADOS / DIAS_MES", 
				"_P", 
				"_P", 
				PaymentType.CRA_0001, 
				SalaryType.SALARY);

		addPayment(aonContext, 
				contract, 
				contract.getStartDate(), 
				contract.getEndDate(), 
				"SALARIO_BASE", 
				"100.00 * DIAS_TRABAJADOS / DIAS_MES", 
				"_P", 
				"_P", 
				PaymentType.CRA_0002, 
				SalaryType.SALARY);
		
		//addSSRegimeCost(aonContext, ssRegimetype, startDate, code, type, expression);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);

		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>() {
			@Override
			protected void fillData(IContractSalaryCalculatorContext ctx) throws SalaryException {
				super.fillData(ctx);
				fillData(ctx, new String [] {
					ContextVariable.CGC_BASE_ENTERPRISE.getName(),
					ContextVariable.CGP_BASE_ENTERPRISE.getName()
				});
			}
		};
		calculator.setSalaryBuilder(getSalaryBuilder());
		Salary salary = calculator.calculate(ctx);
		
		Assert.assertEquals(1000.00, salary.getCommonBase(), DELTA);
		Assert.assertEquals(1100.00, salary.getProfessionalBase(), DELTA);
		
		String cgcBase = salary.getSalaryData(ContextVariable.CGC_BASE.getName());
		Assert.assertEquals(1000.00, Double.parseDouble(cgcBase), DELTA);
		String cgpBase = salary.getSalaryData(ContextVariable.CGP_BASE.getName());
		Assert.assertEquals(1100.00, Double.parseDouble(cgpBase), DELTA);
		
		
		String cgcBaseEnterprise = salary.getSalaryData(ContextVariable.CGC_BASE_ENTERPRISE.getName());
		Assert.assertEquals(1000.00, Double.parseDouble(cgcBaseEnterprise), DELTA);
		String cgpBaseEnterprise = salary.getSalaryData(ContextVariable.CGP_BASE_ENTERPRISE.getName());
		Assert.assertEquals(1100.00, Double.parseDouble(cgpBaseEnterprise), DELTA);

	}
	
	
	protected ISalaryBuilder<Salary> getSalaryBuilder() {
		return new SalaryBuilder();
	}

}
