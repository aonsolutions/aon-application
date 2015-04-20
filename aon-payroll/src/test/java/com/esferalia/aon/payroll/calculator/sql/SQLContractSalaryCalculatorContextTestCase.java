package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_DAYS;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getMax;
import static java.util.Calendar.DATE;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.SystemData;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.IrpfOutcome;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext.IListener;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.ExpressionContext.ExpressionExceptionWrapper;
import com.esferalia.aon.watson.util.AonDateUtils;

public class SQLContractSalaryCalculatorContextTestCase extends AbstractSQLTestCase {
	
	
	
	
	@Test
	public void testRedefinedImplicit() throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		long customVarI = Math.round( Math.random() * 1000 );
		long customVarII = Math.round( Math.random() * 1000 );
		long customVarIII = Math.round( Math.random() * 1000 );
		long customVarVI = Math.round( Math.random() * 1000 );
		addSystemData(aonContext, AonDateUtils.getFirstDayOfYear(getToday()), null, 
				new HashMap<String,String>(){
			{
				put("_" + Long.toString(customVarI) , Long.toString(customVarI));
				put("_" + Long.toString(customVarII) , Long.toString(customVarII));
				put("_" + Long.toString(customVarIII) , Long.toString(customVarIII));
				put("_" + Long.toString(customVarVI) , Long.toString(customVarVI));
			}});

		AgreementRecord agreement = newAgreement(aonContext);
		addData(aonContext, agreement, 
				AonDateUtils.getFirstDayOfYear(getToday()), 
				new HashMap<String,String>(){
			{
				put("_" + Long.toString(customVarII) , Long.toString(customVarII*1000));
				put("_" + Long.toString(customVarVI) , String.format("%d * X",customVarVI));
			}});
		
		AgreementLevelCategoryRecord category = newAgreementCategory(aonContext, agreement);
		
		addData(aonContext, category, 
				AonDateUtils.getFirstDayOfYear(getToday()), 
				null,
				new HashMap<String,String>(){
			{
				put("_" + Long.toString(customVarIII) , Long.toString(customVarIII*10000));
			}});
		
		ContractRecord contract = newContract(aonContext, 
				new String[]{
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
				String.format("_%d * DIAS_TRABAJADOS / DIAS_MES",customVarVI)
				},
				new String[]{
				"BASE_CGC * 0.10",
				"BASE_CGP * 0.05",
				"BASE_ESTR * 0.10",
				"BASE_NESTR * 0.20",
				"BASE_IRPF * PORCENTAJE_IRPF/100"
				},
				category);
		

		
		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);
		
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		addData(aonContext, 
				contract, 
				contract.getStartDate(), 
				contract.getEndDate(), 
				new HashMap<String,String>(){
			{
				put(WORKED_DAYS.getName(), "666");
				put(MONTH_DAYS.getName(), "33");
				put("_" + Long.toString(customVarI) , Long.toString(customVarI*100));
				put("X", "2000");
			}
		});
		
		final Map<String, ITimedVariable<?>> redefinedMap = new HashMap<String, ITimedVariable<?>>();
		final Map<String, ITimedVariable<?>> implicitMap = new HashMap<String, ITimedVariable<?>>();
		
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, start, end, end, criteria);
		ctx.setListener(new IListener() {
			
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				// Nothing
			}
			
			@Override
			public void onRedefinedImplicit(String name,
					ITimedVariable<?> redefined, ITimedVariable<?> implicit) {
				redefinedMap.put(name, redefined);
				implicitMap.put(name, implicit);
			}
		});
		ctx.next();
		new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
			
		ITimedVariable<?> userWorkedDays = redefinedMap.get(WORKED_DAYS.getName());
		Assert.assertEquals(String.format("%s", WORKED_DAYS), 666, userWorkedDays.getValue(userWorkedDays.getPeriod()));
		ITimedVariable<?> systemWorkedDays = implicitMap.get(WORKED_DAYS.getName());
		Assert.assertEquals(String.format("%s", WORKED_DAYS), 33.00 , systemWorkedDays.getValue(systemWorkedDays.getPeriod()));

		ITimedVariable<?> userMonthDays = redefinedMap.get(MONTH_DAYS.getName());
		Assert.assertEquals(String.format("%s", MONTH_DAYS), 33, userMonthDays.getValue(userMonthDays.getPeriod()));
		ITimedVariable<?> systemMonthDays = implicitMap.get(MONTH_DAYS.getName());
		Assert.assertEquals(String.format("%s", MONTH_DAYS), getMax(getToday(), DATE) , systemMonthDays.getValue(systemMonthDays.getPeriod()));

		ITimedVariable<?> userCustomDays = redefinedMap.get("_" + Long.toString(customVarI) );
		Assert.assertEquals("_" + Long.toString(customVarI), customVarI*100, ((Number)userCustomDays.getValue(userCustomDays.getPeriod())).longValue());
		ITimedVariable<?> systemCustomDays = implicitMap.get("_" + Long.toString(customVarI) );
		Assert.assertEquals("_" + Long.toString(customVarI), customVarI , ((Number)systemCustomDays.getValue(systemCustomDays.getPeriod())).longValue());

		ITimedVariable<?> userCustomIIDays = redefinedMap.get("_" + Long.toString(customVarII) );
		Assert.assertEquals("_" + Long.toString(customVarII), customVarII*1000, ((Number)userCustomIIDays.getValue(userCustomIIDays.getPeriod())).longValue());
		ITimedVariable<?> systemCustomIIDays = implicitMap.get("_" + Long.toString(customVarII) );
		Assert.assertEquals("_" + Long.toString(customVarII), customVarII , ((Number)systemCustomIIDays.getValue(systemCustomIIDays.getPeriod())).longValue());

		ITimedVariable<?> userCustomIIIDays = redefinedMap.get("_" + Long.toString(customVarIII) );
		Assert.assertEquals("_" + Long.toString(customVarIII), customVarIII*10000, ((Number)userCustomIIIDays.getValue(userCustomIIIDays.getPeriod())).longValue());
		ITimedVariable<?> systemCustomIIIDays = implicitMap.get("_" + Long.toString(customVarIII) );
		Assert.assertEquals("_" + Long.toString(customVarIII), customVarIII , ((Number)systemCustomIIIDays.getValue(systemCustomIIIDays.getPeriod())).longValue());

		ITimedVariable<?> userCustomVIDays = redefinedMap.get("_" + Long.toString(customVarVI) );
		try {
		Assert.assertEquals("_" + Long.toString(customVarVI), customVarVI*2000, ((Number)userCustomVIDays.getValue(userCustomVIDays.getPeriod())).longValue());
		} catch ( ExpressionExceptionWrapper e ){
			
		}
		ITimedVariable<?> systemCustomVIDays = implicitMap.get("_" + Long.toString(customVarVI) );
		Assert.assertEquals("_" + Long.toString(customVarVI), customVarVI , ((Number)systemCustomVIDays.getValue(systemCustomVIDays.getPeriod())).longValue());
	}

	// ------------------------------------------------------------------------
	
	protected final void addSystemData(AONContext aonContext,
			Date startDate, Date endDate,
			Map<String, String> datas) {
		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=0");

		for (Map.Entry<String, String> data : datas.entrySet()) {
			aonContext.getDslContext().insertInto(SYSTEM_DATA)
					.set(SYSTEM_DATA.DOMAIN, 0)
					.set(SYSTEM_DATA.START_DATE, startDate)
					.set(SYSTEM_DATA.END_DATE, endDate)
					.set(SYSTEM_DATA.NAME, data.getKey())
					.set(SYSTEM_DATA.EXPRESSION, data.getValue()).execute();

		}
		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=1");
	}
	

}
