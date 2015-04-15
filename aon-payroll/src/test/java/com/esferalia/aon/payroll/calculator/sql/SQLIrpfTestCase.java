package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.util.Calendar.DAY_OF_MONTH;
import static junit.framework.Assert.assertEquals;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.function.Consumer;

import junit.framework.Assert;

import org.junit.Test;

import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.IrpfOutcome;
import com.esferalia.aon.payroll.IrpfResult;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext.IListener;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.IExpression;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.watson.util.AonDateUtils;

public class SQLIrpfTestCase extends AbstractSQLTestCase {
	

	private static class Listener implements IListener {

		@Override
		public void onIrpf(IrpfOutcome irpfOutcome) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onUndefinedData(IExpression expression,
				String variableName, String message, java.util.Date start,
				java.util.Date end) {
		}

		@Override
		public void onRedefinedImplicit(String name,
				ITimedVariable<?> redefined, ITimedVariable<?> implicit) {
		}

	}
	
	// ------------------------------------------------------------------------

	@Test
	public void testSimple() throws ExpressionException, SQLException {

		Consumer<IrpfResult> asserts = 
				result -> assertEquals(CommonUtil.round((1500.00 + 250.00) * 1.10
						* (12 - result.getEffectiveDate().getMonth()), 3),
				result.getAnnualRemuneration());
		asserts = asserts.andThen(result -> assertEquals(CommonUtil.round(result.getAnnualRemuneration() * 0.15,3), result.getDeducciblesExpenses()));
				
		test(asserts, 
				new String[]{
				"( P_1 + P_2 ) * 0.10 ",
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
				"250.00 * DIAS_TRABAJADOS / DIAS_MES"},
				new String[]{
				"BASE_CGC * 0.10",
				"BASE_CGP * 0.05",
				"BASE_ESTR * 0.10",
				"BASE_NESTR * 0.20",
				"BASE_IRPF * PORCENTAJE_IRPF"
				}
				);
	}

	@Test
	public void testTotalPayment() throws ExpressionException, SQLException {
		
		Consumer<IrpfResult> asserts = 
				result -> assertEquals(CommonUtil.round(2500.00
						* (12 - result.getEffectiveDate().getMonth()), 3),
				result.getAnnualRemuneration());
		asserts = asserts.andThen(result -> assertEquals(CommonUtil.round(result.getAnnualRemuneration() * 0.15,3), 
				result.getDeducciblesExpenses()));
		
		test(asserts, 
				new String[]{
				"BRUTO(2500.00) ",
				"( P_2 + P_3 ) * 0.10 ",
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
				"250.00 * DIAS_TRABAJADOS / DIAS_MES"},
				new String[]{
				"BASE_CGC * 0.10",
				"BASE_CGP * 0.05",
				"BASE_ESTR * 0.10",
				"BASE_NESTR * 0.20",
				"BASE_IRPF * PORCENTAJE_IRPF"
				}
				);

	}
	
	@Test
	public void testTotalLiquid() throws ExpressionException, SQLException {
		
		Consumer<IrpfResult> asserts = 
				result -> assertEquals(CommonUtil.round(2500.00
						* (12 - result.getEffectiveDate().getMonth()), 3),
				
				result.getAnnualRemuneration() 
				- result.getDeducciblesExpenses()
				- (result.getAnnualRemuneration() * result.getIrpf()/100)
				, result.getAnnualRemuneration() * 0.0001);
		
		asserts = asserts.andThen(result -> assertEquals(CommonUtil.round(result.getAnnualRemuneration() * 0.15,3), 
						result.getDeducciblesExpenses(),
						result.getDeducciblesExpenses() * 0.0001));

		test(asserts, 
			new String[]{
			"NETO(2500.00) ",
			"( P_2 + P_3 ) * 0.10 ",
			"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
			"250.00 * DIAS_TRABAJADOS / DIAS_MES"
			},
			new String[]{
			"BASE_CGC * 0.10",
			"BASE_CGP * 0.05",
			"BASE_ESTR * 0.10",
			"BASE_NESTR * 0.20",
			"BASE_IRPF * PORCENTAJE_IRPF/100"
			}
			);

	}
	
	

	@Test
	public void testSimpleExtras() throws ExpressionException, SQLException {
		
		Consumer<IrpfResult> asserts = 
				result -> {
					int month = result.getEffectiveDate().getMonth();
					
					assertEquals(
							CommonUtil.round(
									1000.00 +
									( month < 07 ? 1000.00/2 : 0.00) 
									, 3),
							result.getAnnualRemuneration());
				};
		
		asserts = asserts.andThen(
				result -> {
					int month = result.getEffectiveDate().getMonth();
					assertEquals(
							CommonUtil.round(
									(1000.00 * 2 / 12 ) * 0.15 * (12 - month),3), 
							result.getDeducciblesExpenses());
				});
		
		
		test(asserts, 
				new String[]{
				},
				new String[]{
				"BASE_CGC * 0.10",
				"BASE_CGP * 0.05",
				"BASE_ESTR * 0.10",
				"BASE_NESTR * 0.20",
				"BASE_IRPF * PORCENTAJE_IRPF/100"
				},
				new Extra []{
					new  Extra(){{this.expression="1000.00 * DIAS_TRABAJADOS / DIAS_MES "; this.month=Month.DECEMBER; this.start="01/01"; this.end="31/12"; this.issue="15/12";}},
					new  Extra(){{this.expression="1000.00 * DIAS_TRABAJADOS / DIAS_MES "; this.month=Month.JULY; this.start="01/07 -1"; this.end="30/06"; this.issue="01/07";}},
				}
				);
	}

	@Test
	public void testPaymentExtras() throws ExpressionException, SQLException {
		
		Consumer<IrpfResult> asserts = 
				result -> {
					int month = result.getEffectiveDate().getMonth();
					
					assertEquals(
							CommonUtil.round(
									1000.00 *
									( 12 -month) 
									, 3)+
							CommonUtil.round(
									1000.00 +
									( month < 07 ? 1000.00/2 : 0.00) 
									, 3)
							,
							result.getAnnualRemuneration());
				};
		asserts = asserts.andThen(
				result -> {
					int month = result.getEffectiveDate().getMonth();
					assertEquals(
							CommonUtil.round(
									(1000.00 ) * 0.15 * (12 - month),3)+							
							CommonUtil.round(
									(1000.00 * 2 / 12 ) * 0.15 * (12 - month),3), 
							result.getDeducciblesExpenses());
				});
		
		test(asserts, 
				new String[]{
				"BRUTO(1000.00 * DIAS_TRABAJADOS / DIAS_MES )",
				},
				new String[]{
				"BASE_CGC * 0.10",
				"BASE_CGP * 0.05",
				"BASE_ESTR * 0.10",
				"BASE_NESTR * 0.20",
				"BASE_IRPF * PORCENTAJE_IRPF/100"
				},
				new Extra []{
					new  Extra(){{this.expression="P_0"; this.month=Month.DECEMBER; this.start="01/12"; this.end="31/12"; this.issue="15/12";}},
					new  Extra(){{this.expression="P_0"; this.month=Month.JULY; this.start="01/07 -1"; this.end="30/06"; this.issue="01/07";}},
				}
				);
	}

	@Test
	public void testLiquidExtras() throws ExpressionException, SQLException {
		
		Consumer<IrpfResult> asserts = 
				result -> {
					Assert.fail();
				};
		
		try {
			test(asserts, 
					new String[]{
					"NETO(1000.00 * DIAS_TRABAJADOS / DIAS_MES )",
					},
					new String[]{
					"BASE_CGC * 0.10",
					"BASE_CGP * 0.05",
					"BASE_ESTR * 0.10",
					"BASE_NESTR * 0.20",
					"BASE_IRPF * PORCENTAJE_IRPF/100"
					},
					new Extra []{
						new  Extra(){{this.expression="P_0"; this.month=Month.DECEMBER; this.start="01/12"; this.end="31/12"; this.issue="15/12";}},
						new  Extra(){{this.expression="P_0"; this.month=Month.JULY; this.start="01/07 -1"; this.end="30/06"; this.issue="01/07";}},
					}
					);
		} catch ( RuntimeException e ) {
			
		}
	}


	@Test
	public void testSettle() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		
		ContractRecord contract = newContract(aonContext, 
				add(getToday(), Calendar.YEAR, -10),
				Collections.emptyMap(),
				new String[]{
				} ,
				new String[]{
				"BASE_CGC * 0.10",
				"BASE_CGP * 0.05",
				"BASE_ESTR * 0.10",
				"BASE_NESTR * 0.20",
				"BASE_IRPF * PORCENTAJE_IRPF/100"
				}, 
				null);
		
		addPayment(aonContext, contract, getFirstDayOfYear(getToday()), "10000.00");
		
		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);
		Date issue = getLastDayOfMonth(start);;

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		
		addPayment(aonContext, contract, getFirstDayOfYear(getToday()), "99999.00", SalaryType.SETTLE);
		SQLContractSettleCalculatorContext ctx = new SQLContractSettleCalculatorContext(
				connection, start, end, issue, criteria);

		ctx.next();

		ctx.setListener(new Listener() {
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				int month = irpfOutcome.getIrpfResult().getEffectiveDate().getMonth();
				assertEquals((10000.00 * (month +1)) + 99999.00,
								irpfOutcome.getIrpfResult().getAnnualRemuneration());
				throw new OnIrpfOutcome(irpfOutcome);
			}
		});
		
		try {
			ctx.getIrpf();
			Assert.fail();
		} catch ( OnIrpfOutcome e ) {
			System.out.println(e.getMessage());
		}
	}

	// ------------------------------------------------------------------------

	private void test(Consumer<IrpfResult> c, String [] payments, String [] deductions)
			throws ExpressionException, SQLException {
		test(c, payments, deductions, new Extra []{});
	}
	
	private void test(Consumer<IrpfResult> c, String [] payments, String [] deductions, Extra extras [])
			throws ExpressionException, SQLException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		AgreementLevelCategoryRecord category = null;
		if ( extras!= null && extras.length > 0 )
			category = newAgreement(aonContext, extras);
		
		ContractRecord contract = newContract(aonContext, payments ,
				deductions, category);

		Calendar calendar = Calendar.getInstance();
		// Be care that the first day of the month has value 1.
		calendar.set(DAY_OF_MONTH, 1);
		Date start = new Date(calendar.getTimeInMillis());

		calendar.set(DAY_OF_MONTH, calendar.getActualMaximum(DAY_OF_MONTH));
		Date end = new Date(calendar.getTimeInMillis());

		Date issue = new Date(calendar.getTimeInMillis());

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, start, end, issue, criteria);

		ctx.next();

		ctx.setListener(new Listener() {

			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				c.accept(irpfOutcome.getIrpfResult());
			}
		});

		ctx.getIrpf();
	}
	
	private static class OnIrpfOutcome extends RuntimeException {
		IrpfOutcome irpfOutcome;
		public OnIrpfOutcome(IrpfOutcome irpfOutcome) {
			this.irpfOutcome = irpfOutcome;
		}
		
		@Override
		public String getMessage() {
			return irpfOutcome.getIrpfResult().getAnnualRemuneration() + ":" + irpfOutcome.getIrpfResult().getIrpf() ;
		}
	}
}
