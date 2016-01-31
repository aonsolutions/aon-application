package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.COMPENSATION_CAUSE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.OBJECTIVE;
import static com.esferalia.aon.watson.util.AonDateUtils.add;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfYear;
import static java.lang.String.format;
import static java.util.Calendar.DAY_OF_YEAR;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;

import junit.framework.Assert;

import org.junit.Test;

import com.code.aon.common.enumeration.Month;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.SalaryDeduction;
import com.esferalia.aon.payroll.SalaryEmbargo;
import com.esferalia.aon.payroll.SalaryPayment;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.watson.util.AonDateUtils;

public class SQLSettleTestCase extends AbstractSQLTestCase {
	
	
	
	@Test
	public void testSettleContractEnd() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.DECEMBER;
						this.start = "01/12";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, });
		
		Date contractStart = add(getToday(), Calendar.MONTH, -2);
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				new HashMap<String, String>() {
					{
						put(MONTH_DAYS.getName(), format("%d", 30));
						put(COMPENSATION_CAUSE.getName(), ContextVariable.CONTRACT_END.getName());
					}
				}, new String[] { "( P_1 + P_2 ) * 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" }, 
						new String[] {
						"BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" }, category);
		//@formatter:off
		
		addSSRegimeStuff(aonContext);
		
		ISQLContractSalaryCalculatorContext ctx = 
				getSQLContractSettleContext(connection, contractStart, contract);
		
		
		Salary settle = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		double br = (1750.00 * 1.10) * (1 + 1.00 / 12 + 1.00 / 12) * 12 / 365; //AonDateUtils.getMax(getToday(), DAY_OF_YEAR);

		Assert.assertEquals( 12 * (2/12.00) * br, settle.getTotalPayment());
		Assert.assertEquals( 12 * (2/12.00) * br, settle.getTotalLiquid());
	}

	@Test
	public void testSettleObjective() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.DECEMBER;
						this.start = "01/12";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, });
		
		
		Date contractStart = add(getToday(), Calendar.MONTH, -2);
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				new HashMap<String, String>() {
					{
						put(MONTH_DAYS.getName(), format("%d", 30));
						put(COMPENSATION_CAUSE.getName(), OBJECTIVE.getName());
					}
				}, new String[] { "( P_1 + P_2 ) * 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" }, 
						new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" }, category);
		//@formatter:off
		
		addSSRegimeStuff(aonContext);
		
		ISQLContractSalaryCalculatorContext ctx = 
				getSQLContractSettleContext(connection, contractStart, contract);
		
		
		Salary settle = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		double br = (1750.00 * 1.10) * (1 + 1.00 / 12 + 1.00 / 12) * 12 / 365; //AonDateUtils.getMax(getToday(), DAY_OF_YEAR);

		Assert.assertEquals( 20 * (2/12.00) * br, settle.getTotalPayment());
		
		System.out.println( (20 * (2/12.00) * br ) + " = " + settle.getTotalPayment() );
		
		Assert.assertEquals( 20 * (2/12.00) * br, settle.getTotalLiquid());
	}


	private void addSSRegimeStuff(AONContext aonContext) {
		
		Date startDate = AonDateUtils.add(getFirstDayOfYear(getToday()), Calendar.YEAR, -2);
		
		addSSRegimeData(aonContext, 
				SSRegimeType.GENERAL, 
				startDate, 
				null, 
				new HashMap<String, String>() {
					{
						put("DIAS_INDEMNIZACION_FIN", format("%d", 12));
					}
				});
		
		// INDEMNIZACION POR CESE
		addSSRegimePayment(aonContext, 
				SSRegimeType.GENERAL, 
				startDate, 
				PaymentType.CRA_0054, 
				"(CAUSA_INDEMNIZACION == FIN) ? DIAS_INDEMNIZACION_FIN(FIN_CONTRATO) * AÑOS_TRABAJADOS * SALARIO_DIA : REMOVE()",
				null ,
				null, 
				SalaryType.SETTLE);
		
		//INDEMNIZACION POR DESPIDO IMPROCEDENTE (< 13 DE FEBRERO DE 2012)
		addSSRegimePayment(aonContext, 
				SSRegimeType.GENERAL, 
				add(getFirstDayOfYear(getToday()),Calendar.YEAR,-1), 
				PaymentType.CRA_0054, 
				"(CAUSA_INDEMNIZACION == IMPROCEDENTE) ? MIN(45 * AÑOS_TRABAJADOS * SALARIO_DIA,SALARIO_DIA * 365 / 12 * 42 ): REMOVE()",
				null ,
				null, 
				SalaryType.SETTLE);
		
		//INDEMNIZACION POR DESPIDO IMPROCEDENTE (>= 13 DE FEBRERO DE 2012)
		addSSRegimePayment(aonContext, 
				SSRegimeType.GENERAL, 
				startDate, 
				PaymentType.CRA_0054, 
				"(CAUSA_INDEMNIZACION==IMPROCEDENTE)?MIN(33*AÑOS_TRABAJADOS*SALARIO_DIA,ABS(SALARIO_DIA*365/12*24-INDEMNIZACION)):REMOVE()",
				null ,
				null, 
				SalaryType.SETTLE);
		
		//INDEMNIZACION POR DESPIDO POR CAUSAS OBJETIVAS
		addSSRegimePayment(aonContext, 
				SSRegimeType.GENERAL, 
				startDate, 
				PaymentType.CRA_0054, 
				" (CAUSA_INDEMNIZACION == PROCEDENTE ) ? MIN(20 * AÑOS_TRABAJADOS * SALARIO_DIA, SALARIO_DIA * 365 / 12 * 12 ) : REMOVE()",
				null ,
				null, 
				SalaryType.SETTLE);
		
	}
	
	

	private ISQLContractSalaryCalculatorContext getSQLContractSettleContext(Connection connection,
			Date contractStart, ContractRecord contract) throws SQLException,
			ExpressionException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		ISQLContractSalaryCalculatorContext ctx = new SQLContractSettleCalculatorContext(
				connection, contractStart, getToday(), getToday(), criteria);
		ctx.next();
		return ctx;
	}
	
	
	
	
	
}
