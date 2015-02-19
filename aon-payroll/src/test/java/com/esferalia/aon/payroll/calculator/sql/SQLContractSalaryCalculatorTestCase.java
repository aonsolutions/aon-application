/**
 * 
 */
package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

import junit.framework.Assert;

import org.junit.Test;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;

/**
 * @author rtrepiana
 *
 */
public class SQLContractSalaryCalculatorTestCase extends
		AbstractSQLTestCase {


	@Test
	public void testLiquidAndPayment() throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection, "", 0);

		
		ContractRecord contract = newContract(aonContext, 
				new String[]{
				"NETO(2500.00) ",
				"( P_2 + P_3 ) * 0.10 ",
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
				"BRUTO(4000.00) * DIAS_TRABAJADOS / DIAS_MES",
				"BRUTO(80.00) * DIAS_TRABAJADOS / DIAS_MES"
				},
				new String[]{
				"BASE_CGC * 0.10",
				"BASE_CGP * 0.05",
				"BASE_ESTR * 0.10",
				"BASE_NESTR * 0.20",
				"BASE_IRPF * PORCENTAJE_IRPF/100"
				}
				);

		
		Date start = getFirstDayOfMonth();
		Date end = getLastDayOfMonth(start);
		
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, start, end, end, criteria);

		ctx.next();
		
		ContractSalaryCalculator calculator = new ContractSalaryCalculator();
		calculator.setSalaryBuilder(new SalaryBuilder());
		
		calculator.setListener(new ContractSalaryCalculator.Listener(){
			@Override
			public void onCheckError(IContractPayment payment, String message) {
				System.out.println(message);
			}
		});

		ISalary salary = calculator.calculate(ctx);
		
		Assert.assertEquals(1500.00, salary.getTotalPayment());
		
	}

	
	
}
