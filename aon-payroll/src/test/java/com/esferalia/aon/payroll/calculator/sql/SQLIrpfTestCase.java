package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.IrpfData.IRPF_DATA;
import static com.esferalia.aon.jooq.tables.IrpfDataDescendients.IRPF_DATA_DESCENDIENTS;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.occam.api.model.type.SalaryType.M190;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.COMMON_DISEASE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ERE_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PREST_IT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TC2;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C100;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C401;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C501;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import org.jooq.Record;
import org.jooq.impl.DSL;
import org.junit.Ignore;
import org.junit.Test;

import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.EnterpriseActivity;
import com.esferalia.aon.jooq.tables.EnterpriseCcc;
import com.esferalia.aon.jooq.tables.records.AgreementExtraRecord;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.IrpfDataRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.IrpfData;
import com.esferalia.aon.payroll.IrpfOutcome;
import com.esferalia.aon.payroll.IrpfRegularization;
import com.esferalia.aon.payroll.IrpfResult;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.SalaryDeduction;
import com.esferalia.aon.payroll.SalaryPayment;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext.IListener;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.DeductHomeLoan;
import com.esferalia.aon.payroll.enumeration.DisabilityLevel;
import com.esferalia.aon.payroll.enumeration.FamilySituation;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext;
import com.esferalia.aon.payroll.irpf.IrpfCalculator;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.IExpression;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.CheckException;
import com.esferalia.aon.salary.expression.ExpressionContext.ExpressionExceptionWrapper;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import junit.framework.Assert;

public class SQLIrpfTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.0000001;
	
	private static class SuccesExcpetion extends RuntimeException {
		
	}

	private static class DefaultIrpfCalculatorContext implements IIrpfCalculatorContext {
		@Override
		public boolean next() {
			return true;
		}

		@Override
		public String getRetenedorNif() {
			return "P7198848I"; 
		}

		@Override
		public String getRetenedorApellidosNombre() {
			return "RETENEDOR APELLIDOS, NOMBRE";
		}

		@Override
		public String getNif() {
			return "89105637A";
		}

		@Override
		public String getApellidosNombre() {
			return "APELLIDOS, NOMBRE";
		}

		@Override
		public int getAñoNacimiento() {
			return 1975;
		}

		@Override
		public String getComunidadAutonoma() {
			return "ARABA/ÁLAVA";
		}

		@Override
		public SituacionLaboral getSituacionLaboral() {
			return SituacionLaboral.TRABAJADOR_ACTIVO;
		}

		@Override
		public Contrato getContrato() {
			// General
			return Contrato.UNO;
		}

		@Override
		public boolean getMovilidadGeografica() {
			return false;
		}

		@Override
		public boolean getProlongacionLaboral() {
			return false;
		}

		@Override
		public SituacionFamiliar getSituacionFamiliar() {
			// Otra ...
			return SituacionFamiliar.TRES;
		}

		@Override
		public String getNifConyuge() {
			return "65950716X";
		}

		@Override
		public boolean getMovilidadReducida() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Discapacidad getDiscapacidad() {
			return Discapacidad.GRADO0;
		}

		@Override
		public boolean getResidenciaCeutaMelilla() {
			return false;
		}

		@Override
		public boolean getRdtosObtenidosCeutaMelilla() {
			return false;
		}

		@Override
		public BigDecimal getRetribAnuales() {
			return BigDecimal.valueOf(40000);
		}

		@Override
		public BigDecimal getGastosAnuales() {
			return null; //BigDecimal.ZERO;
		}

		@Override
		public BigDecimal getIrregularidad1() {
			return null; //BigDecimal.ZERO;
		}

		@Override
		public BigDecimal getIrregularidad2() {
			return null; //BigDecimal.ZERO;
		}

		@Override
		public BigDecimal getPensionCompensatoria() {
			return null; //BigDecimal.ZERO;
		}

		@Override
		public BigDecimal getAnualidadesHijos() {
			return null; //BigDecimal.ZERO;
		}

		@Override
		public boolean getPagoPrestamosVivienda() {
			return false;
		}

		@Override
		public Iterable<Ascendiente> getAscendientes() {
			return Collections.emptyList();
		}

		@Override
		public Iterable<Descendiente> getDescendientes() {
			return Collections.emptyList();
		}

		@Override
		public BigDecimal getRetribSatisfechas() {
			return null; //BigDecimal.ZERO;
		}

		@Override
		public BigDecimal getRetencionPracticada() {
			return null; //BigDecimal.ZERO;
		}

		@Override
		public BigDecimal getRetribAnualesIniciales() {
			return null; //BigDecimal.ZERO;
		}

		@Override
		public BigDecimal getRetencionAnualInicial() {
			return null; //BigDecimal.ZERO;
		}

		@Override
		public boolean getResidenciaInicialCeutaMelilla() {
			return false;
		}

		@Override
		public BigDecimal getBaseRetencion() {
			return null; //BigDecimal.ZERO;
		}

		@Override
		public BigDecimal getMinimoPersonalFamiliarInicial() {
			return null; //BigDecimal.ZERO;
		}

		@Override
		public BigDecimal getMinoracionPrestamosVivienda() {
			return null; //BigDecimal.ZERO;
		}

		@Override
		public BigDecimal getTipoRetencion() {
			return null; //BigDecimal.ZERO;
		}

		@Override
		public CausaRegularizacion getCausaRegularizacion() {
			return null;
		}
	}

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

	private static class OnIrpfOutcome extends RuntimeException {
		IrpfOutcome irpfOutcome;

		public OnIrpfOutcome(IrpfOutcome irpfOutcome) {
			this.irpfOutcome = irpfOutcome;
		}

		@Override
		public String getMessage() {
			return irpfOutcome.getIrpfResult().getAnnualRemuneration() + ":"
					+ irpfOutcome.getIrpfResult().getIrpf();
		}
	}

	// ------------------------------------------------------------------------

	@Test
	public void testSimpleI() throws ExpressionException, SQLException {

		Consumer<IrpfResult> asserts = result -> assertAnnualRemuneration(
				CommonUtil.round((1500.00 + 250.00) * 1.10
						* (12 /*-result.getEffectiveDate().getMonth()*/ ), 3),
				result.getAnnualRemuneration());
		asserts = asserts.andThen(result -> assertEquals(
				CommonUtil.round(result.getAnnualRemuneration() * 0.15, 3),
				result.getDeducciblesExpenses()));

		test(asserts, new String[] { 
				"( P_1 + P_2 ) * 0.10 ",
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" 
				}, 
				new String[] {
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05", 
				"BASE_ESTR * 0.10",
				"BASE_NESTR * 0.20", 
				"BASE_IRPF * PORCENTAJE_IRPF" 
				});
	}

	@Test
	public void testSimpleStartAt() throws ExpressionException, SQLException {

		Consumer<IrpfResult> asserts = result -> assertAnnualRemuneration(
				CommonUtil.round((1500.00 + 250.00) * 1.10
						* (12 /*-result.getEffectiveDate().getMonth()*/ ), 3),
				result.getAnnualRemuneration());
		asserts = asserts.andThen(result -> assertEquals(
				CommonUtil.round(result.getAnnualRemuneration() * 0.15, 3),
				result.getDeducciblesExpenses()));

		
		Date contractStart = getFirstDayOfMonth(getToday());
		contractStart = add(contractStart, DAY_OF_MONTH, 14);
		
		test(
			asserts, 
			contractStart,
			null,
			new String[] { 
			"( P_1 + P_2 ) * 0.10 ",
			"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
			"250.00 * DIAS_TRABAJADOS / DIAS_MES" 
			}, 
			new String[] {
			"BASE_CGC * 0.10", 
			"BASE_CGP * 0.05", 
			"BASE_ESTR * 0.10",
			"BASE_NESTR * 0.20", 
			"BASE_IRPF * PORCENTAJE_IRPF" 
			},
			new Extra [] {}) ;
			

	}

	@Test
	public void testShortWithExtras() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		
		Calendar calendar = Calendar.getInstance();
		// Be care that the first day of the month has value 1.
		calendar.set(DAY_OF_MONTH, 1);
		Date startDate = new Date(calendar.getTimeInMillis());
		Date endDate = getLastDayOfMonth(add(startDate, Calendar.MONTH, 1));

		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
				new Extra() {
					{
						this.expression = "P_1 + P_2";
						this.month = Month.JULY;
						this.start = "01/07";
						this.end = "31/12";
						this.issue = "01/07";
					}
				}, });
		
		
		ContractRecord contract = newContract(aonContext, SSRegimeType.GENERAL,
				CCCType.PRINCIPAL, 
				startDate,
				endDate,
				new HashMap<String, String>() {
					{
						put(TC2.getName(), C401.getValue());
						put(ContextVariable.QUOTE_GROUP.getName(), "'07'");
					}
				}, new String[] { 
						"GTZDO(P_1 + P_2 ,1,3)",
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						"25.00 * DIAS_TRABAJADOS / DIAS_MES" 
				},
						new String[] {
						"BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_ESTR * 0.10", "BASE_NESTR * 0.20",
						"BASE_IRPF * PORCENTAJE_IRPF / 100.00" 
				}
				, category
				,null);

		Date start = startDate;
		Date end = getLastDayOfMonth(startDate);

		Date issue = new Date(calendar.getTimeInMillis());

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, start, end, issue, criteria);

		Assert.assertEquals(2.00 , ctx.getIrpf());

	}

	@Test
	public void testTotalPayment() throws ExpressionException, SQLException {

		Consumer<IrpfResult> asserts = result -> assertAnnualRemuneration(
				CommonUtil.round(2500.00 * (12 /*- result.getEffectiveDate()
						.getMonth()*/), 3), result.getAnnualRemuneration());
		asserts = asserts.andThen(result -> assertEquals(
				CommonUtil.round(result.getAnnualRemuneration() * 0.15, 3),
				result.getDeducciblesExpenses()));

		test(asserts, new String[] { 
				"BRUTO(2500.00) ",
				"( P_2 + P_3 ) * 0.10 ",
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
				"250.00 * DIAS_TRABAJADOS / DIAS_MES", 
				"TRACE('BRUTO %f\r\n', P_0); 0.00"
				}, 
				new String[] {
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05", 
				"BASE_ESTR * 0.10",
				"BASE_NESTR * 0.20", 
				"BASE_IRPF * PORCENTAJE_IRPF" });

	}

	@Test
	public void testTotalLiquid() throws ExpressionException, SQLException {

		Consumer<IrpfResult> asserts = result -> assertEquals(CommonUtil.round(
				2500.00 * (12 /*- result.getEffectiveDate().getMonth()*/), 3),

		result.getAnnualRemuneration() - result.getDeducciblesExpenses()
				- (result.getAnnualRemuneration() * result.getIrpf() / 100),
				result.getAnnualRemuneration() * 0.0001);

		asserts = asserts.andThen(result -> assertEquals(
				CommonUtil.round(result.getAnnualRemuneration() * 0.15, 3),
				result.getDeducciblesExpenses(),
				result.getDeducciblesExpenses() * 0.0001));
		//@formatter:off
		test(asserts, new String[] 
				{ "NETO(2500.00) ", 
				"( P_2 + P_3 ) * 0.10 ",
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
				"250.00 * DIAS_TRABAJADOS / DIAS_MES",
				//"TRACE('NETO %f\r\n', P_0); 0.00"
				}
				, new String[] {
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05", 
				"BASE_ESTR * 0.10",
				"BASE_NESTR * 0.20", 
				"BASE_IRPF * PORCENTAJE_IRPF/100" 
				});
		//@formatter:on

	}

	@Test
	public void testSimpleExtras() throws ExpressionException, SQLException {

		Consumer<IrpfResult> asserts = result -> {
			int month = result.getEffectiveDate().getMonth();

			assertAnnualRemuneration(CommonUtil.round(
					1000.00 * 2 /*+ (month < 07 ? 1000.00 / 2 : 0.00)*/, 3),
					result.getAnnualRemuneration());
		};

		asserts = asserts.andThen(result -> {
			int month = result.getEffectiveDate().getMonth();
			assertDeduccibleExpenses(CommonUtil.round((1000.00 * 2 / 12) * 0.15
					* (12 /*- month*/), 3), result.getDeducciblesExpenses());
		});

		test(asserts, 
				new String[] {}, 
				new String[] { 
				"TRACE('BASE_CGC=%f\r\n',BASE_CGC);BASE_CGC * 0.10",
				"TRACE('BASE_CGP=%f\r\n',BASE_CGP);BASE_CGP * 0.05", 
				"BASE_ESTR * 0.10", 
				"BASE_NESTR * 0.20",
				"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, 
				new Extra[] { 
				new Extra() {
			{
				this.expression = "1000.00 * DIAS_TRABAJADOS / DIAS_MES ";
				this.month = Month.DECEMBER;
				this.start = "01/01";
				this.end = "31/12";
				this.issue = "15/12";
			}
		}, new Extra() {
			{
				this.expression = "1000.00 * DIAS_TRABAJADOS / DIAS_MES ";
				this.month = Month.JULY;
				this.start = "01/07 -1";
				this.end = "30/06";
				this.issue = "01/07";
			}
		}, });
	}

	@Test
	public void testExtrasAtSalary() throws ExpressionException, SQLException {

		Consumer<IrpfResult> asserts = result -> {
			int month = result.getEffectiveDate().getMonth();

			assertAnnualRemuneration(CommonUtil.round(
					1000.00 * 14  , 3),
					result.getAnnualRemuneration());
		};

//		asserts = asserts.andThen(result -> {
//			int month = result.getEffectiveDate().getMonth();
//			assertDeduccibleExpenses(CommonUtil.round((1000.00 * 2 / 12) * 0.15
//					* (12 ), 3), result.getDeducciblesExpenses());
//		});
		
		//Date firstDayOfMonth = getFirstDayOfMonth(getToday());
		Date lastDayOfMonth = getLastDayOfMonth(getToday());
		

		test(asserts, 
				new String[] {
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES ",
						"TRACE('TOTAL=%f\r\n',TOTAL_DEVENGADO);0.00"
				}, 
				new String[] { 
				"TRACE('BASE_CGC=%f\r\n',BASE_CGC);BASE_CGC * 0.10",
				"TRACE('BASE_CGP=%f\r\n',BASE_CGP);BASE_CGP * 0.05", 
				"BASE_ESTR * 0.10", 
				"BASE_NESTR * 0.20",
				"BASE_IRPF * PORCENTAJE_IRPF/100",
				}, 
				new Payment[] { 
				new Payment() {
			{
				this.expression = "1000.00 * DIAS_TRABAJADOS / DIAS_MES ";
				this.month = Month.DECEMBER;
			}
		}, new Payment() {
			{
				this.expression = "1000.00 * DIAS_TRABAJADOS / DIAS_MES ";
				this.month = Month.getMonthByValue(AonDateUtils.get(getToday(), MONTH));
			}
		}, });
	}

	@Test
	public void testExtrasAtSalarySelfEmployee() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()), 
				null, 
				Collections.emptyMap(), 
				new String[] {
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES ",
						"TRACE('TOTAL=%f\r\n',TOTAL_DEVENGADO);0.00"
				}, 
				new String[] { 
					"TRACE('BASE_CGC=%f\r\n',BASE_CGC);BASE_CGC * 0.10",
					"TRACE('BASE_CGP=%f\r\n',BASE_CGP);BASE_CGP * 0.05", 
					"BASE_ESTR * 0.10", 
					"BASE_NESTR * 0.20",
					"BASE_IRPF * PORCENTAJE_IRPF/100",
				}, 
				null);
		
		contract.setSsRegime((byte)SSRegimeType.SELF_EMPLOYED.ordinal());
		contract.update();
		
		PaymentConceptRecord concept = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		addPayment(aonContext, contract, contract.getStartDate(), null, concept, "PAGA EXTRAORDINARIA", "1000.00 * DIAS_TRABAJADOS / DIAS_MES /12.00", "_P", "_P", PaymentType.CRA_0004);
		addPayment(aonContext, contract, contract.getStartDate(), null, concept, "PAGA EXTRAORDINARIA", "1000.00 * DIAS_TRABAJADOS / DIAS_MES /12.00", "_P", "_P", PaymentType.CRA_0004);
		
		Date startDate = getFirstDayOfYear(getToday());
		while ( get(startDate, Calendar.MONTH) < Calendar.SEPTEMBER ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, getLastDayOfMonth(startDate), getLastDayOfMonth(startDate), contract);
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<>(connection);
			new SmartContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(startDate, Calendar.MONTH,1);
		}
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, getLastDayOfMonth(startDate), getLastDayOfMonth(startDate), contract);
		ctx.setListener( new IListener() {
			
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				assertAnnualRemuneration(CommonUtil.round(
						1000.00 * 14  , 3),
						irpfOutcome.getIrpfResult().getAnnualRemuneration());
			}
		});
		
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
		

	}

	
	@Test
	public void testIssueOutExtras() throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Calendar calendar = Calendar.getInstance();
		// Be care that the first day of the month has value 1.
		calendar.set(DAY_OF_MONTH, 1);
		Date startDate = new Date(calendar.getTimeInMillis());

		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
				new Extra() {
					{
						this.expression = "P_1 + P_2";
						this.month = Month.JULY;
						this.start = "01/01";
						this.end = "30/06";
						this.issue = "15/07";
					}					
				}, });
		
		
		ContractRecord contract = newContract(aonContext, SSRegimeType.GENERAL,
				CCCType.PRINCIPAL, 
				startDate,
				null,
				new HashMap<String, String>() {
					{
						put(TC2.getName(), C100.getValue());
						put(ContextVariable.QUOTE_GROUP.getName(), "'01'");
					}
				}, new String[] { 
						"GTZDO(P_1 + P_2 ,1,3)",
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
						"5000.00 * DIAS_TRABAJADOS / DIAS_MES" 
				},
						new String[] {
						"BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_ESTR * 0.10", "BASE_NESTR * 0.20",
						"BASE_IRPF * PORCENTAJE_IRPF / 100.00" 
				}
				, category, null);


		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord extra = getExtra(aonContext, agreement.getId(), "15/07");
		
		calendar = Calendar.getInstance();
		calendar.add(YEAR, 1);
		calendar.set(MONTH, 6);
		calendar.set(DAY_OF_MONTH, 15);
		Date chargeDate = new Date(calendar.getTimeInMillis());
		
		ISQLContractSalaryCalculatorContext ctx  = getExtraSalaryCalculatorContext(connection, contract, extra, calendar.get(YEAR), chargeDate);
		
		Salary salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		for ( com.esferalia.aon.payroll.SalaryDeduction deduction : salary.getSalaryDeductions())
			System.out.println(deduction.getDescription() + " = " + deduction.getAmount());
		
		Assert.assertEquals(1, salary.getSalaryDeductions().size());
	}

	@Test
	public void testPaymentExtras() throws ExpressionException, SQLException {

		Consumer<IrpfResult> asserts = result -> {
			int month = result.getEffectiveDate().getMonth();

			assertAnnualRemuneration(
					CommonUtil.round(1000.00 * (14 /*- month*/), 3)
//							+ CommonUtil.round(
//									1000.00 + (month < 07 ? 1000.00 / 2 : 0.00),
//									3)
							, result.getAnnualRemuneration());
		};
		asserts = asserts.andThen(result -> {
			int month = result.getEffectiveDate().getMonth();
			assertDeduccibleExpenses(
					CommonUtil.round((1000.00) * 0.15 * (12 /*- month*/), 3)
							+ CommonUtil.round((1000.00 * 2 / 12) * 0.15
									* (12 /*- month*/), 3),
					result.getDeducciblesExpenses());
		});

		test(asserts,
				new String[] { 
				"BRUTO(1000.00 * DIAS_TRABAJADOS / DIAS_MES )", },
				new String[] { 
				"BASE_CGC * 0.10", "BASE_CGP * 0.05",
				"BASE_ESTR * 0.10", "BASE_NESTR * 0.20",
				"BASE_IRPF * PORCENTAJE_IRPF/100" }, new Extra[] {
						new Extra() {
							{
								this.expression = "P_0";
								this.month = Month.DECEMBER;
								this.start = "01/12";
								this.end = "31/12";
								this.issue = "15/12";
							}
						}, new Extra() {
							{
								this.expression = "P_0";
								this.month = Month.JULY;
								this.start = "01/07 -1";
								this.end = "30/06";
								this.issue = "01/07";
							}
						}, });
	}

	@Test
	public void testPaymentExtrasI() throws ExpressionException, SQLException {

		Consumer<IrpfResult> asserts = result -> {
			assertAnnualRemuneration(
					CommonUtil.round(1200.00 * 14, 3)
							, result.getAnnualRemuneration());
		};

		test(asserts,
				new String[] { 
				"1200.00 * DIAS_TRABAJADOS/DIAS_MES", 
				},
				new String[] { 
				"BASE_CGC * 0.10", "BASE_CGP * 0.05",
				"BASE_ESTR * 0.10", "BASE_NESTR * 0.20",
				"BASE_IRPF * PORCENTAJE_IRPF/100" }, 
				new String [] {
				"PRORRATEAR(1200.00 * DIAS_TRABAJADOS/DIAS_MES)", 
				"PRORRATEAR(1200.00 * DIAS_TRABAJADOS/DIAS_MES)", 
				});
	}

	@Test
	public void testPaymentExtrasII() throws ExpressionException, SQLException {

		Consumer<IrpfResult> asserts = result -> {
			assertAnnualRemuneration(
					CommonUtil.round(1200.00 * 14 + 100.00 * 12 , 3)
							, result.getAnnualRemuneration());
		};

		test(asserts,
				new String[] { 
				"1000.00 * DIAS_TRABAJADOS/DIAS_MES", 
				"100.00 * DIAS_TRABAJADOS/DIAS_MES", 
				"100.00 * DIAS_TRABAJADOS/DIAS_MES", 
				"P_0 * 10 / 100", 
				},
				new String[] { 
				"BASE_CGC * 0.10", "BASE_CGP * 0.05",
				"BASE_ESTR * 0.10", "BASE_NESTR * 0.20",
				"BASE_IRPF * PORCENTAJE_IRPF/100" }, 
				new String [] {
				"(P_0 + P_1 + P_2)/12", 
				"(P_0 + P_1 + P_2)/12", 
				});
	}

	@Test
	public void testPaymentExtrasIII() throws ExpressionException, SQLException, SalaryException {

		
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		PaymentConceptRecord pagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);

		PaymentConceptRecord pagaMarzo = addConcept(aonContext, "PAGA_MARZO", PaymentType.CRA_0004);
		PaymentConceptRecord pagaOctubre = addConcept(aonContext, "PAGA_OCTUBRE", PaymentType.CRA_0004);

		AgreementLevelCategoryRecord agreementLevelCategory = 
		newAgreement(aonContext,
		new Extra[] {
			new Extra() {
				{
					this.expression = "P_0";
					this.month = Month.DECEMBER;
					this.start = "01/12";
					this.end = "31/12";
					this.issue = "15/12";
					this.concept = pagaExtra.getId();
				}
			}, new Extra() {
				{
					this.expression = "P_0";
					this.month = Month.JULY;
					this.start = "01/07 -1";
					this.end = "30/06";
					this.issue = "01/07";
					this.concept = pagaExtra.getId();
				}
			},
			new Extra() {
				{
					this.expression = "P_0";
					this.month = Month.MARCH;
					this.start = "01/01 -1";
					this.end = "31/12 -1";
					this.issue = "31/03";
					this.concept = pagaMarzo.getId();
				}
			}, new Extra() {
				{
					this.expression = "P_0";
					this.month = Month.OCTOBER;
					this.start = "01/10 -1";
					this.end = "30/09";
					this.issue = "01/10";
					this.concept = pagaOctubre.getId();
				}
			},
		}
		);


		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()), 
				null, 
				Collections.emptyMap(), 
				new String[] { 
				"1000.00 * DIAS_TRABAJADOS/DIAS_MES"
				},
				new String[] { 
				"BASE_CGC * 0.10", "BASE_CGP * 0.05",
				"BASE_ESTR * 0.10", "BASE_NESTR * 0.20",
				"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, 
				agreementLevelCategory);
		
		addPayment(
		aonContext, 
		contract, 
		contract.getStartDate(), 
		contract.getEndDate(), 
		pagaMarzo, 
		"PAGA EXTRA MARZO", 
		"PRORRATEAR(P_0)", 
		"_P", 
		"_P", 
		PaymentType.CRA_0004);

		addPayment(
		aonContext, 
		contract, 
		contract.getStartDate(), 
		contract.getEndDate(), 
		pagaOctubre, 
		"PAGA EXTRA OCTUBRE", 
		"PRORRATEAR(P_0)", 
		"_P", 
		"_P", 
		PaymentType.CRA_0004);

		addPayment(
		aonContext, 
		contract, 
		contract.getStartDate(), 
		contract.getEndDate(), 
		pagaExtra, 
		"PAGA EXTRA NAVIDAD", 
		"P_0", 
		"_P", 
		"PRORRATEAR()", 
		PaymentType.CRA_0004,
		(byte) Month.DECEMBER.ordinal());

		addPayment(
		aonContext, 
		contract, 
		contract.getStartDate(), 
		contract.getEndDate(), 
		pagaExtra, 
		"PAGA EXTRA VERANO", 
		"P_0", 
		"_P", 
		"PRORRATEAR()", 
		PaymentType.CRA_0004,
		(byte) Month.JULY.ordinal());

		Calendar calendar = Calendar.getInstance();
		// Be care that the first day of the month has value 1.
		calendar.set(DAY_OF_MONTH, 1);
		calendar.set(MONTH, Calendar.JULY);
		Date start = new Date(calendar.getTimeInMillis());

		calendar.set(DAY_OF_MONTH, calendar.getActualMaximum(DAY_OF_MONTH));
		Date end = new Date(calendar.getTimeInMillis());

		Date issue = new Date(calendar.getTimeInMillis());

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, start, end, issue, contract);

		ctx.setListener(new Listener() {

			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				assertAnnualRemuneration(
						CommonUtil.round(1000.00 * 16 , 3)
								, irpfOutcome.getIrpfResult().getAnnualRemuneration());
			}
		});

		ctx.getIrpf();
		
		for ( Date date  =  getFirstDayOfYear(getToday()); get(date, MONTH) < Calendar.DECEMBER  ; date = add(date, MONTH, 1) ) {
			JooqSalaryBuilder<ISalary>  jooqSalaryBuilder =  new JooqSalaryBuilder<ISalary>(connection);
			ctx = 
			getContractSalaryCalculatorContext(connection, date, getLastDayOfMonth(date), getLastDayOfMonth(date), contract);
			ctx.setListener(new Listener() {
				@Override
				public void onIrpf(IrpfOutcome irpfOutcome) {
					assertAnnualRemuneration(
							CommonUtil.round(1000.00 * 16 , 3)
									, irpfOutcome.getIrpfResult().getAnnualRemuneration()
									, 0.05);
				}
			});
			new SmartContractSalaryCalculator<ISalary>( jooqSalaryBuilder).calculate( ctx );
			jooqSalaryBuilder.execute();
		}
		
		
	}

	@Test
	public void testLiquidExtras() throws ExpressionException, SQLException {

		Consumer<IrpfResult> asserts = result -> {
//			Assert.fail();
		};

		try {
			test(asserts,
					new String[] { "NETO(1000.00 * DIAS_TRABAJADOS / DIAS_MES )", },
					new String[] { 
							"BASE_CGC * 0.10", 
							"BASE_CGP * 0.05",
							"BASE_ESTR * 0.10", 
							"BASE_NESTR * 0.20",
							"BASE_IRPF * PORCENTAJE_IRPF/100" 
							}, new Extra[] {
							new Extra() {
								{
									this.expression = "P_0";
									this.month = Month.DECEMBER;
									this.start = "01/12";
									this.end = "31/12";
									this.issue = "15/12";
								}
							}, new Extra() {
								{
									this.expression = "P_0";
									this.month = Month.JULY;
									this.start = "01/07 -1";
									this.end = "30/06";
									this.issue = "01/07";
								}
							}, });
		} catch (RuntimeException e) {
		}
	}

	@Test
	public void testSettle() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				add(getToday(), Calendar.YEAR, -10), Collections.emptyMap(),
				new String[] {},
				new String[] { 
						"BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_ESTR * 0.10", "BASE_NESTR * 0.20",
						"BASE_IRPF * PORCENTAJE_IRPF/100" }, null);

		addPayment(aonContext, contract, getFirstDayOfYear(getToday()),
				"10000.00");

		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);
		Date issue = getLastDayOfMonth(start);
		;

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		addPayment(aonContext, contract, getFirstDayOfMonth(getToday()),
				"99999.00", SalaryType.SETTLE);
		ISQLContractSalaryCalculatorContext ctx = getContractSettleCalculatorContext(
				connection, start, end, issue, criteria);

		ctx.setListener(new Listener() {
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				int month = irpfOutcome.getIrpfResult().getEffectiveDate()
						.getMonth();
				assertAnnualRemuneration((10000.00 * 12 ) + 99999.00,
						irpfOutcome.getIrpfResult().getAnnualRemuneration());
				throw new OnIrpfOutcome(irpfOutcome);
			}
		});

		try {
			ctx.getIrpf();
			Assert.fail();
		} catch (OnIrpfOutcome e) {
			System.out.println(e.getMessage());
		}
	}
	
	@Test
	public void testActualDays() throws ExpressionException, SQLException {

		int actualDays = 0;
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
//		calendar.set(Calendar.MONTH, Calendar.JANUARY);
//		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		while(calendar.get(Calendar.MONTH) ==  month ) {
			
			int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
			if ( dayOfWeek != Calendar.SATURDAY 
					&& dayOfWeek != Calendar.SUNDAY)
				actualDays++;
			
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		final double annualRemuneration = actualDays * (12 /*- month*/) ;
		Consumer<IrpfResult> asserts = result -> assertAnnualRemuneration(
				annualRemuneration,
				result.getAnnualRemuneration());
		
		test(asserts,
			new String[] { 
				"1.00 * DIAS_EFECTIVOS",
				"TRACE('DIAS_EFECTIVOS=%f\r\n', DIAS_EFECTIVOS);0.00",
				}, 
				new String[] {
				});
	}
	
	@Test
	public void testSimpleWithIT() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemPayments(aonContext);
		
		addSSRegimePayment(aonContext 
				,SSRegimeType.GENERAL 
				,getFirstDayOfYear(getToday()) 
				,PaymentType.CRA_0000
				,"TRACE('BASE_REGULADORA: %f \r\n',BASE_REGULADORA);0.00"
				,"TRACE('DIAS_ENFERMEDAD_COMUN_21: %d \r\n',DIAS_ENFERMEDAD_COMUN_21);0.00"
				,"TRACE('P_0 + P_1 + P_2 : %f \r\n',P_0 + P_1 + P_2);0.00"
				);

		addSSRegimePayment(aonContext 
				,SSRegimeType.GENERAL 
				,getFirstDayOfYear(getToday()) 
				,PaymentType.CRA_0000
				,String.format("BASE_REGULADORA * 0.75 * %s_21",  COMMON_DISEASE_DAYS)
				,String.format("BASE_REGULADORA * 1.00 * %s_21",  COMMON_DISEASE_DAYS)
				,"_P"
				);
		
		Extra extras [] = new Extra[] {
							new Extra() {
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
							}
						};
		
		AgreementLevelCategoryRecord category = null;
		if (extras != null && extras.length > 0)
			category = newAgreement(aonContext, extras);

		Date startContract = AonDateUtils.getFirstDayOfYear(getToday());

		ContractRecord contract = newContract(aonContext, 
				startContract, 
				null, 
				new HashMap<String, String>() {
					{
						put(TC2.getName(), C100.getValue());
						put(ContextVariable.MONTH_DAYS.getName(), "30.00");
						put(ContextVariable.QUOTE_GROUP.getName(), "'01'");
					}
				}, 
				new String[] { 
				"( P_1 + P_2 ) * 0.10 ",
				"150.00 * DIAS_TRABAJADOS / DIAS_MES",
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" 
				}, 
				new String[] {
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05", 
				"BASE_ESTR * 0.10",
				"BASE_NESTR * 0.20", 
				"BASE_IRPF * PORCENTAJE_IRPF" 
				},
				null);

		
		Date salaryStart = getFirstDayOfMonth(add(startContract, Calendar.MONTH,3));
		Date salaryEnd = getLastDayOfMonth(salaryStart);
		Date salaryIssue = salaryEnd;

		Date startITDate = add(startContract, DAY_OF_MONTH,15);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				null, null);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, salaryStart, salaryEnd, salaryIssue, contract);

		ctx.setListener(new Listener() {

			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				System.out.println("Irpf : " + irpfOutcome.getIrpfResult().getIrpf() );
				System.out.println("AnnualIrpf : " + irpfOutcome.getIrpfResult().getAnnualIrpf() );
				System.out.println("AnnualRemuneration : " + irpfOutcome.getIrpfResult().getAnnualRemuneration() );
				
				assertAnnualRemuneration(400.00 * 1.10 * 12 , irpfOutcome.getIrpfResult().getAnnualRemuneration(), 8, 0.009);
//				assertAnnualRemuneration(400.00 * 1.10 * 11 + 400.00 * 1.10 * 0.75, irpfOutcome.getIrpfResult().getAnnualRemuneration(), 8, 0.009);
//				assertAnnualRemuneration(400.00 * 1.10 * 0.75 * 3 + 400.00 * 1.10 * 8, irpfOutcome.getIrpfResult().getAnnualRemuneration(), 8, 0.009);
			}
		});

		//ctx.getIrpf();
		
		Salary salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
	}

	@Test
	public void testSimpleWithITI() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemPayments(aonContext);
		
		addSSRegimePayment(aonContext 
				,SSRegimeType.GENERAL 
				,getFirstDayOfYear(getToday()) 
				,PaymentType.CRA_0000
				,"TRACE('BASE_REGULADORA: %f \r\n',BASE_REGULADORA);0.00"
				,"TRACE('DIAS_ENFERMEDAD_COMUN_21: %d \r\n',DIAS_ENFERMEDAD_COMUN_21);0.00"
				,"TRACE('P_0 + P_1 + P_2 : %f \r\n',P_0 + P_1 + P_2);0.00"
				);

		addSSRegimePayment(aonContext 
				,SSRegimeType.GENERAL 
				,getFirstDayOfYear(getToday()) 
				,PaymentType.CRA_0000
				,String.format("BASE_REGULADORA * 0.75 * %s_21",  COMMON_DISEASE_DAYS)
				,String.format("BASE_REGULADORA * 1.00 * %s_21",  COMMON_DISEASE_DAYS)
				,"_P"
				);
		
		Extra extras [] = new Extra[] {
							new Extra() {
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
							}
						};
		
		AgreementLevelCategoryRecord category = null;
		if (extras != null && extras.length > 0)
			category = newAgreement(aonContext, extras);

		Date startContract = AonDateUtils.getFirstDayOfYear(getToday());

		ContractRecord contract = newContract(aonContext, 
				startContract, 
				null, 
				new HashMap<String, String>() {
					{
						put(TC2.getName(), C100.getValue());
						put(ContextVariable.MONTH_DAYS.getName(), "30.00");
						put(ContextVariable.QUOTE_GROUP.getName(), "'01'");
					}
				}, 
				new String[] { 
				"( P_1 + P_2 ) * 0.10 ",
				"150.00 * DIAS_TRABAJADOS / DIAS_MES",
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" 
				}, 
				new String[] {
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05", 
				"BASE_ESTR * 0.10",
				"BASE_NESTR * 0.20", 
				"BASE_IRPF * PORCENTAJE_IRPF" 
				},
				null);

		
		Date salaryStart = getFirstDayOfMonth(add(startContract, Calendar.MONTH,1));
		Date salaryEnd = getLastDayOfMonth(salaryStart);
		Date salaryIssue = salaryEnd;

		Date startITDate = add(add(startContract, MONTH, 2), DAY_OF_MONTH, 10);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				null, null);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, salaryStart, salaryEnd, salaryIssue, contract);
		
		Boolean []  asserts = {false}; 

		ctx.setListener(new Listener() {

			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				System.out.println("Irpf : " + irpfOutcome.getIrpfResult().getIrpf() );
				System.out.println("AnnualIrpf : " + irpfOutcome.getIrpfResult().getAnnualIrpf() );
				System.out.println("AnnualRemuneration : " + irpfOutcome.getIrpfResult().getAnnualRemuneration() );
				
				assertAnnualRemuneration(400.00 * 1.10 * 12 , irpfOutcome.getIrpfResult().getAnnualRemuneration(), 12, 0.009);
				asserts[0] = true;
			}
		});

		try {
			Salary salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
			assertTrue(asserts[0]);
		} catch ( RuntimeException e ) {
			
		}
		
	}

	@Test
	public void testAllYearConstantI() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()), 
				null, 
				Collections.emptyMap(), 
				new String [] {
						"2500.00",
						"250.00",
				}, 
				new String [] {
						"PORCENTAJE_IRPF / 100.00 * BASE_IRPF"
				}, 
				null);
		
		List<Double[]> irpfResults = new ArrayList<Double[]>(12);
		// Be care that the first day of the month has value 1.
		Date startDate = getFirstDayOfYear(getToday());
		for ( int i = 0; i < 12 ; i++ ) {
			Date endDate = getLastDayOfMonth(startDate);
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			ctx.setListener( new Listener() {
				@Override
				public void onIrpf(IrpfOutcome irpfOutcome) {
					super.onIrpf(irpfOutcome);
					Double irpfResult [] = new Double[4]; 
					irpfResult[0] = irpfOutcome.getIrpfResult().getIrpf();
					irpfResult[1] = irpfOutcome.getIrpfResult().getBaseIrpf();
					irpfResult[2] = irpfOutcome.getIrpfResult().getAnnualIrpf();
					irpfResult[3] = irpfOutcome.getIrpfResult().getAnnualRemuneration();
					irpfResults.add(irpfResult);
					
					System.out.printf("%tB\r\n", endDate );
					
					System.out.println("Irpf:" + irpfOutcome.getIrpfResult().getIrpf());
					System.out.println("BaseIrpf:" + irpfOutcome.getIrpfResult().getBaseIrpf());
					System.out.println("AnnualIrpf:" + irpfOutcome.getIrpfResult().getAnnualIrpf());
					System.out.println("AnnualRemuneration:" + irpfOutcome.getIrpfResult().getAnnualRemuneration());

//					System.out.println("DeducciblesExpenses:" + irpfOutcome.getIrpfResult().getDeducciblesExpenses());
//					System.out.println("Irregular18_2Reduction:" + irpfOutcome.getIrpfResult().getIrregular18_2Reduction());
//					System.out.println("Irregular18_3Reduction:" + irpfOutcome.getIrpfResult().getIrregular18_3Reduction());

					if ( irpfOutcome.getIrpfRegularization() != null ) {
						System.out.println("PaidIrpf:" + irpfOutcome.getIrpfRegularization().getPaidIrpf());
						System.out.println("PaidRemuenration:" + irpfOutcome.getIrpfRegularization().getPaidRemuneration());
					}
				}
			});
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {
//				@Override
//				public void setTotalPayment(Double totalPayment) {
//					System.out.println("TOTAL PAYMENT: " + totalPayment);
//					super.setTotalPayment(totalPayment);
//				}
			};
			new SmartContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(startDate, Calendar.MONTH, 1); 
		}
		
		org.junit.Assert.assertEquals(1, irpfResults.stream().map(irpfResult -> irpfResult[1]).distinct().count());
		org.junit.Assert.assertEquals(1, irpfResults.stream().map(irpfResult -> irpfResult[3]).distinct().count());
		org.junit.Assert.assertTrue(irpfResults.stream().map(irpfResult -> irpfResult[0]).distinct().count() <= 2);
		org.junit.Assert.assertTrue(irpfResults.stream().map(irpfResult -> irpfResult[2]).distinct().count() <=2 );
		
		Double irpfs [] = 
		AON.getSalaries(aonContext, p -> p.getContractProperty().eq(contract.getId()))
		.peek(salary -> System.out.println( salary.getTotalPayment() +", "+ salary.getIrpfBase() + ", " + salary.getTotalIrpf()))
		.map(salary-> salary.getTotalIrpf())
		.toArray( Double[]::new)
		;
		
		for ( int i =0; i< 12; i++ ) {
			assertIrpf((Double) irpfs[i], 2750.00, irpfResults.get(i)[0], 0.009 ); 
					
		}
		
	}

	@Test
	public void testAllYearConstantII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()), 
				null, 
				Collections.emptyMap(), 
				new String [] {
						"2500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00  * DIAS_TRABAJADOS / DIAS_MES",
				}, 
				new String [] {
						"PORCENTAJE_IRPF / 100.00 * BASE_IRPF"
				}, 
				null);
		
		List<Double[]> irpfResults = new ArrayList<Double[]>(12);
		// Be care that the first day of the month has value 1.
		Date startDate = getFirstDayOfYear(getToday());
		for ( int i = 0; i < 12 ; i++ ) {
			Date endDate = getLastDayOfMonth(startDate);
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			ctx.setListener( new Listener() {
				@Override
				public void onIrpf(IrpfOutcome irpfOutcome) {
					super.onIrpf(irpfOutcome);
					Double irpfResult [] = new Double[4]; 
					irpfResult[0] = irpfOutcome.getIrpfResult().getIrpf();
					irpfResult[1] = irpfOutcome.getIrpfResult().getBaseIrpf();
					irpfResult[2] = irpfOutcome.getIrpfResult().getAnnualIrpf();
					irpfResult[3] = irpfOutcome.getIrpfResult().getAnnualRemuneration();
					irpfResults.add(irpfResult);
					
					System.out.println("Irpf:" + irpfOutcome.getIrpfResult().getIrpf());
					System.out.println("BaseIrpf:" + irpfOutcome.getIrpfResult().getBaseIrpf());
					System.out.println("AnnualIrpf:" + irpfOutcome.getIrpfResult().getAnnualIrpf());
					System.out.println("AnnualRemuneration:" + irpfOutcome.getIrpfResult().getAnnualRemuneration());
				}
			});
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			new SmartContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(startDate, Calendar.MONTH, 1); 
		}
		
		org.junit.Assert.assertEquals(12, irpfResults.size());

		org.junit.Assert.assertEquals(1, irpfResults.stream().map(irpfResult -> irpfResult[1]).distinct().count());
		org.junit.Assert.assertEquals(1, irpfResults.stream().map(irpfResult -> irpfResult[3]).distinct().count());
		org.junit.Assert.assertTrue(irpfResults.stream().map(irpfResult -> irpfResult[0]).distinct().count() <= 2);
		org.junit.Assert.assertTrue(irpfResults.stream().map(irpfResult -> irpfResult[2]).distinct().count() <=2 );
		
		Double irpfs [] = 
		AON.getSalaries(aonContext, p -> p.getContractProperty().eq(contract.getId()))
		.peek(salary -> System.out.println( salary.getTotalPayment() +", "+ salary.getIrpfBase() + ", " + salary.getTotalIrpf()))
		.map(salary-> salary.getTotalIrpf())
		.toArray( Double[]::new)
		;
		
		for ( int i =0; i< 12; i++ ) {
			assertIrpf((Double) irpfs[i], 2750.00 ,  irpfResults.get(i)[0], 0.009 ); 
					
		}
		
	}

	@Test
	public void testAllYearConstantIII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()), 
				null, 
				Collections.emptyMap(), 
				new String [] {
						"2500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00  * DIAS_TRABAJADOS / DIAS_MES",
				}, 
				new String [] {
						"PORCENTAJE_IRPF / 100.00 * BASE_IRPF"
				}, 
				null);
		
		List<Double[]> expectedIrpfResults = new ArrayList<Double[]>(12);
		// Be care that the first day of the month has value 1.
		Date startDate = getFirstDayOfYear(getToday());
		for ( int i = 0; i < 12 ; i++ ) {
			Date endDate = getLastDayOfMonth(startDate);
			System.out.println(startDate);
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			ctx.setListener( new Listener() {
				@Override
				public void onIrpf(IrpfOutcome irpfOutcome) {
					super.onIrpf(irpfOutcome);
					Double irpfResult [] = new Double[4]; 
					irpfResult[0] = irpfOutcome.getIrpfResult().getIrpf();
					irpfResult[1] = irpfOutcome.getIrpfResult().getBaseIrpf();
					irpfResult[2] = irpfOutcome.getIrpfResult().getAnnualIrpf();
					irpfResult[3] = irpfOutcome.getIrpfResult().getAnnualRemuneration();
					expectedIrpfResults.add(irpfResult);
					
					System.out.println("Irpf:" + irpfOutcome.getIrpfResult().getIrpf());
					System.out.println("BaseIrpf:" + irpfOutcome.getIrpfResult().getBaseIrpf());
					System.out.println("AnnualIrpf:" + irpfOutcome.getIrpfResult().getAnnualIrpf());
					System.out.println("AnnualRemuneration:" + irpfOutcome.getIrpfResult().getAnnualRemuneration());
//					System.out.println("PriorIrpf:" + irpfOutcome.getIrpfRegularization().getPriorIrpf());
//					System.out.println("PriorAnnualIrpf:" + irpfOutcome.getIrpfRegularization().getPriorAnnualIrpf());
//					System.out.println("PriorAnnualRemuneration:" + irpfOutcome.getIrpfRegularization().getPriorAnnualRemuneration());
				}
			});
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			new SmartContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(startDate, Calendar.MONTH, 1); 
		}

		
		contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()), 
				null, 
				Collections.emptyMap(), 
				new String [] {
						"2500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00  * DIAS_TRABAJADOS / DIAS_MES",
				}, 
				new String [] {
						"PORCENTAJE_IRPF / 100.00 * BASE_IRPF"
				}, 
				null);
		
		List<Double[]> irpfResults = new ArrayList<Double[]>(12);
		// Be care that the first day of the month has value 1.
		startDate = getFirstDayOfYear(getToday());
		for ( int i = 0; i < 12 ; i++ ) {
			Date endDate = getLastDayOfMonth(startDate);
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			ctx.setListener( new Listener() {
				@Override
				public void onIrpf(IrpfOutcome irpfOutcome) {
					super.onIrpf(irpfOutcome);
					Double irpfResult [] = new Double[4]; 
					irpfResult[0] = irpfOutcome.getIrpfResult().getIrpf();
					irpfResult[1] = irpfOutcome.getIrpfResult().getBaseIrpf();
					irpfResult[2] = irpfOutcome.getIrpfResult().getAnnualIrpf();
					irpfResult[3] = irpfOutcome.getIrpfResult().getAnnualRemuneration();
					irpfResults.add(irpfResult);
					
					System.out.println("--Irpf:" + irpfOutcome.getIrpfResult().getIrpf());
					System.out.println("--BaseIrpf:" + irpfOutcome.getIrpfResult().getBaseIrpf());
					System.out.println("--AnnualIrpf:" + irpfOutcome.getIrpfResult().getAnnualIrpf());
					System.out.println("--AnnualRemuneration:" + irpfOutcome.getIrpfResult().getAnnualRemuneration());
					//System.out.println("--PaidIrpf:" + irpfOutcome.getIrpfRegularization().getPaidIrpf());
				}
			});
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			new SmartContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
			//jooqSalaryBuilder.execute();
			startDate = add(startDate, Calendar.MONTH, 1); 
		}
		
		for ( int i= 0; i < 12 ; i++ ) {
			org.junit.Assert.assertEquals(expectedIrpfResults.get(i)[0], irpfResults.get(i)[0], 0.1);
			org.junit.Assert.assertEquals(expectedIrpfResults.get(i)[1], irpfResults.get(i)[1]);
			org.junit.Assert.assertEquals(expectedIrpfResults.get(i)[2], irpfResults.get(i)[2], 0.1 / 100.00 * 12  * 2750.00 );
			org.junit.Assert.assertEquals(expectedIrpfResults.get(i)[3], irpfResults.get(i)[3]);
		}
		
	}

	@Test
	public void testAllYearConstantIV() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date startContract = getFirstDayOfYear(getToday());
		startContract = add(startContract, Calendar.MONTH, 5);
		
		ContractRecord contract = newContract(aonContext, 
				startContract, 
				null, 
				Collections.emptyMap(), 
				new String [] {
						"2500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00  * DIAS_TRABAJADOS / DIAS_MES",
				}, 
				new String [] {
						"PORCENTAJE_IRPF / 100.00 * BASE_IRPF"
				}, 
				null);
		
		List<Double[]> irpfResults = new ArrayList<Double[]>(6);
		// Be care that the first day of the month has value 1.
		Date startDate = contract.getStartDate();
		for ( int i = 0; i < 6 ; i++ ) {
			Date endDate = getLastDayOfMonth(startDate);
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			ctx.setListener( new Listener() {
				@Override
				public void onIrpf(IrpfOutcome irpfOutcome) {
					super.onIrpf(irpfOutcome);
					Double irpfResult [] = new Double[4]; 
					irpfResult[0] = irpfOutcome.getIrpfResult().getIrpf();
					irpfResult[1] = irpfOutcome.getIrpfResult().getBaseIrpf();
					irpfResult[2] = irpfOutcome.getIrpfResult().getAnnualIrpf();
					irpfResult[3] = irpfOutcome.getIrpfResult().getAnnualRemuneration();
					irpfResults.add(irpfResult);
					
					System.out.println("Irpf:" + irpfOutcome.getIrpfResult().getIrpf());
					System.out.println("BaseIrpf:" + irpfOutcome.getIrpfResult().getBaseIrpf());
					System.out.println("AnnualIrpf:" + irpfOutcome.getIrpfResult().getAnnualIrpf());
					System.out.println("AnnualRemuneration:" + irpfOutcome.getIrpfResult().getAnnualRemuneration());
				}
			});
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			new SmartContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
			//jooqSalaryBuilder.execute();
			startDate = add(startDate, Calendar.MONTH, 1); 
		}
		
		org.junit.Assert.assertEquals(1, irpfResults.stream().map(irpfResult -> irpfResult[1]).distinct().count());
		org.junit.Assert.assertEquals(1, irpfResults.stream().map(irpfResult -> irpfResult[3]).distinct().count());
		org.junit.Assert.assertTrue(irpfResults.stream().map(irpfResult -> irpfResult[0]).distinct().count() <= 2);
		org.junit.Assert.assertTrue(irpfResults.stream().map(irpfResult -> irpfResult[2]).distinct().count() <=2 );
		
	}

	@Test
	public void testAllYearConstantV() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date startContract = getFirstDayOfYear(getToday());
//		startContract = add(startContract, Calendar.MONTH, 5);
		
		ContractRecord contract = newContract(aonContext, 
				startContract, 
				null, 
				Collections.emptyMap(), 
				new String [] {
						"2500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00  * DIAS_TRABAJADOS / DIAS_MES",
				}, 
				new String [] {
						"PORCENTAJE_IRPF / 100.00 * BASE_IRPF"
				}, 
				null);
		
		PaymentConceptRecord pagaExtraConcept = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		
		addPayment(aonContext, 
				contract, 
				contract.getStartDate(), 
				contract.getEndDate(), 
				pagaExtraConcept, 
				"PAGA EXTRAORDINARIA DE NAVIDAD", 
				"2500.00 * DIAS_TRABAJADOS / DIAS_MES", 
				"_P", 
				"_P/12", 
				PaymentType.CRA_0004, 
				(byte) Month.DECEMBER.ordinal());
		
		addPayment(aonContext, 
				contract, 
				contract.getStartDate(), 
				contract.getEndDate(), 
				pagaExtraConcept, 
				"PAGA EXTRAORDINARIA DE VERANO", 
				"2500.00 * DIAS_TRABAJADOS / DIAS_MES", 
				"_P", 
				"_P/12", 
				PaymentType.CRA_0004, 
				(byte) Month.JULY.ordinal());
		

		List<Double[]> irpfResults = new ArrayList<Double[]>(6);
		// Be care that the first day of the month has value 1.
		Date startDate = contract.getStartDate();
		for ( int i = 0; i < 11 ; i++ ) {
			System.out.println(startDate);
			Date endDate = getLastDayOfMonth(startDate);
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			ctx.setListener( new Listener() {
				@Override
				public void onIrpf(IrpfOutcome irpfOutcome) {
					super.onIrpf(irpfOutcome);
					Double irpfResult [] = new Double[4]; 
					irpfResult[0] = irpfOutcome.getIrpfResult().getIrpf();
					irpfResult[1] = irpfOutcome.getIrpfResult().getBaseIrpf();
					irpfResult[2] = irpfOutcome.getIrpfResult().getAnnualIrpf();
					irpfResult[3] = irpfOutcome.getIrpfResult().getAnnualRemuneration();
					irpfResults.add(irpfResult);
					
					System.out.println("Irpf:" + irpfOutcome.getIrpfResult().getIrpf());
					System.out.println("BaseIrpf:" + irpfOutcome.getIrpfResult().getBaseIrpf());
					System.out.println("AnnualIrpf:" + irpfOutcome.getIrpfResult().getAnnualIrpf());
					System.out.println("AnnualRemuneration:" + irpfOutcome.getIrpfResult().getAnnualRemuneration());
					
					if ( irpfOutcome.getIrpfRegularization() != null ) {
						System.out.println("PaidIrpf:" + irpfOutcome.getIrpfRegularization().getPaidIrpf());
						System.out.println("PaidRemuneration:" + irpfOutcome.getIrpfRegularization().getPaidRemuneration());
					}

//					System.out.println("DeducciblesExpenses:" + irpfOutcome.getIrpfResult().getDeducciblesExpenses());
//					System.out.println("SocialSecurityPensioner:" + irpfOutcome.getIrpfResult().getSocialSecurityPensioner());
//					System.out.println("MinimunPersonalFamily:" + irpfOutcome.getIrpfResult().getMinimunPersonalFamily());
				}
			});
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) ;
			new SmartContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(startDate, Calendar.MONTH, 1); 
		}
		
		org.junit.Assert.assertEquals(1, irpfResults.stream().map(irpfResult -> irpfResult[1]).distinct().count());
		org.junit.Assert.assertEquals(1, irpfResults.stream().map(irpfResult -> irpfResult[3]).distinct().count());
		org.junit.Assert.assertTrue(irpfResults.stream().map(irpfResult -> irpfResult[0]).distinct().count() <=2);
		org.junit.Assert.assertTrue(irpfResults.stream().map(irpfResult -> irpfResult[2]).distinct().count() <=2 );
		
	}


	@Test
	public void testAllYearConstantVI() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date startContract = getFirstDayOfYear(getToday());
//		startContract = add(startContract, Calendar.MONTH, 5);
		
		ContractRecord contract = newContract(aonContext, 
				startContract, 
				null, 
				Collections.emptyMap(), 
				new String [] {
						"2500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00  * DIAS_TRABAJADOS / DIAS_MES",
				}, 
				new String [] {
						"PORCENTAJE_IRPF / 100.00 * BASE_IRPF"
				}, 
				null);
		
		PaymentConceptRecord pagaExtraConcept = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		
		addPayment(aonContext, 
				contract, 
				contract.getStartDate(), 
				contract.getEndDate(), 
				pagaExtraConcept, 
				"PAGA EXTRAORDINARIA DE NAVIDAD", 
				"2500.00 * DIAS_TRABAJADOS / DIAS_MES", 
				"_P", 
				"_P/12", 
				PaymentType.CRA_0004, 
				(byte) Month.DECEMBER.ordinal());
		
		addPayment(aonContext, 
				contract, 
				contract.getStartDate(), 
				contract.getEndDate(), 
				pagaExtraConcept, 
				"PAGA EXTRAORDINARIA DE VERANO", 
				"2500.00 * DIAS_TRABAJADOS / DIAS_MES", 
				"_P", 
				"_P/12", 
				PaymentType.CRA_0004, 
				(byte) Month.JULY.ordinal());
		
		addPayment(aonContext, 
				contract, 
				contract.getStartDate(), 
				contract.getEndDate(), 
				pagaExtraConcept, 
				"PAGA EXTRAORDINARIA DE MARZO", 
				"2000.00 * DIAS_TRABAJADOS / DIAS_MES", 
				"_P", 
				"_P/12", 
				PaymentType.CRA_0004, 
				(byte) Month.MARCH.ordinal());

		addPayment(aonContext, 
				contract, 
				contract.getStartDate(), 
				contract.getEndDate(), 
				pagaExtraConcept, 
				"PAGA EXTRAORDINARIA DE SEPTIEMBRE", 
				"2000.00 * DIAS_TRABAJADOS / DIAS_MES", 
				"_P", 
				"_P/12", 
				PaymentType.CRA_0004, 
				(byte) Month.SEPTEMBER.ordinal());

		
		List<Double[]> irpfResults = new ArrayList<Double[]>(6);
		// Be care that the first day of the month has value 1.
		Date startDate = contract.getStartDate();
		for ( int i = 0; i < 11 ; i++ ) {
			System.out.println(startDate);
			Date endDate = getLastDayOfMonth(startDate);
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			ctx.setListener( new Listener() {
				@Override
				public void onIrpf(IrpfOutcome irpfOutcome) {
					super.onIrpf(irpfOutcome);
					Double irpfResult [] = new Double[4]; 
					irpfResult[0] = irpfOutcome.getIrpfResult().getIrpf();
					irpfResult[1] = irpfOutcome.getIrpfResult().getBaseIrpf();
					irpfResult[2] = irpfOutcome.getIrpfResult().getAnnualIrpf();
					irpfResult[3] = irpfOutcome.getIrpfResult().getAnnualRemuneration();
					irpfResults.add(irpfResult);
					
					System.out.println("Irpf:" + irpfOutcome.getIrpfResult().getIrpf());
					System.out.println("BaseIrpf:" + irpfOutcome.getIrpfResult().getBaseIrpf());
					System.out.println("AnnualIrpf:" + irpfOutcome.getIrpfResult().getAnnualIrpf());
					System.out.println("AnnualRemuneration:" + irpfOutcome.getIrpfResult().getAnnualRemuneration());
					
					if ( irpfOutcome.getIrpfRegularization() != null ) {
						System.out.println("PaidIrpf:" + irpfOutcome.getIrpfRegularization().getPaidIrpf());
						System.out.println("PaidRemuneration:" + irpfOutcome.getIrpfRegularization().getPaidRemuneration());
					}

				}
			});
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) ;
			new SmartContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(startDate, Calendar.MONTH, 1); 
		}
		
		org.junit.Assert.assertEquals(1, irpfResults.stream().map(irpfResult -> irpfResult[1]).distinct().count());
		org.junit.Assert.assertEquals(1, irpfResults.stream().map(irpfResult -> irpfResult[3]).distinct().count());
		org.junit.Assert.assertTrue(irpfResults.stream().map(irpfResult -> irpfResult[0]).distinct().count() <=2);
//		org.junit.Assert.assertTrue(irpfResults.stream().map(irpfResult -> irpfResult[2]).distinct().count() <=2 );
		
	}

	@Test
	public void testAllYearNoPaymentsI() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()), 
				null, 
				Collections.emptyMap(), 
				new String [] {}, 
				new String [] {
						"PORCENTAJE_IRPF / 100.00 * BASE_IRPF"
				}, 
				null);
		
		// Be care that the first day of the month has value 1.
		Date startDate = getFirstDayOfYear(getToday());
		startDate = add(startDate, Calendar.MONTH, 11); 
		Date endDate = add( getLastDayOfMonth(startDate), Calendar.DAY_OF_MONTH, -20 );
		
		addPayment(aonContext, contract, add(startDate, Calendar.MONTH, -1), null, "SALARIO BASE", "2500.00", "_P", "_P", PaymentType.CRA_0001 );
		addPayment(aonContext, contract, add(startDate, Calendar.MONTH, -1), null, "PLUS SALARIAL", "250.00", "_P", "_P", PaymentType.CRA_0001 );
		
		addPayment(aonContext, contract, contract.getStartDate(), null, "INDEMNIZACIÓN", "10000.00", "_P", null, PaymentType.CRA_0054, SalaryType.SETTLE  );

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());

		ISQLContractSalaryCalculatorContext ctx =
		getContractSettleCalculatorContext(connection, contract.getStartDate(), endDate, endDate, criteria);
		
		ctx.setListener( new Listener() {
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				super.onIrpf(irpfOutcome);
				
				System.out.printf("%tB\r\n", endDate );
				
				System.out.println("Irpf:" + irpfOutcome.getIrpfResult().getIrpf());
				System.out.println("BaseIrpf:" + irpfOutcome.getIrpfResult().getBaseIrpf());
				System.out.println("AnnualIrpf:" + irpfOutcome.getIrpfResult().getAnnualIrpf());
				System.out.println("AnnualRemuneration:" + irpfOutcome.getIrpfResult().getAnnualRemuneration());
				assertAnnualRemuneration(2750.00 * 12 + 10000.00, irpfOutcome.getIrpfResult().getAnnualRemuneration(), 0.00 );
				
//				System.out.println("PaidIrpf:" + irpfOutcome.getIrpfRegularization().getPaidIrpf());
//				System.out.println("PaidRemuenration:" + irpfOutcome.getIrpfRegularization().getPaidRemuneration());
				
				
							
			}
		});
		
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		System.out.println( "I.R.P.F : " + salary.getTotalIrpf());
		org.junit.Assert.assertTrue(salary.getTotalPayment() > 0.00 );
		org.junit.Assert.assertTrue(salary.getTotalIrpf() > 0.00 );
	}

	@Test
	public void testExtrasI() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "SALARIO_BASE + PLUS_SALARIAL";
							this.month = Month.JULY;
							this.start = "01/07 -1";
							this.end = "30/06";
							this.issue = "01/07";
						}
					}, 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "(SALARIO_BASE + PLUS_SALARIAL)";
							this.month = Month.DECEMBER;
							this.start = "01/01";
							this.end = "31/12";
							this.issue = "15/12";
						}
					}, 
				},
				new Payment[] {
						new Payment() {
							{
								this.concept = conceptSalarioBase.getId();
								this.expression = "SALARIO_MENSUAL * DIAS_TRABAJADOS/DIAS_MES";
							}
						},
						new Payment() {
							{
								this.concept = conceptPlusSalarial.getId();
								this.expression = "PLUS_MENSUAL * DIAS_TRABAJADOS/DIAS_MES";
							}
						}
				});
		
		addData(aonContext, category, getFirstDayOfYear(getToday()), null, new HashMap<String,String>(){
			{
				put("PLUS_MENSUAL", "300.00");
				put("SALARIO_MENSUAL", "3000.00");
			}
		});
		


		ContractRecord contract = newContract(
				aonContext
				,getFirstDayOfYear(getToday())
				, new HashMap<String,String>(){
					{
						put("DIAS_MES", "30.00");
					}
				}
				, new String [] {}
				, new String [] {
						"BASE_CGC * 4.70 / 100.00"
						,"BASE_CGP * 1.55 / 100.00"
						,"BASE_CGP * 0.10 / 100.00"
						,"BASE_IRPF * PORCENTAJE_IRPF / 100.00"
				}
				,category);
		
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		
		double irpf []  = { 0.00 }; 
		ctx.setListener(new Listener() {
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				irpf[0] = irpfOutcome.getIrpfResult().getIrpf();
				assertAnnualRemuneration(3300.00 * 14, irpfOutcome.getIrpfResult().getAnnualRemuneration());
				org.junit.Assert.assertEquals( 3300.00 * 14.00 * irpf[0] / 100.00, irpfOutcome.getIrpfResult().getAnnualIrpf(), 0.0);
			}
		});
		ctx.getIrpf();
		
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		calculateAndSave(connection, ctx);
		

		for ( int i = 1; i <= 5; i++ ) {
			int j = i;
			startDate = add(startDate, MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
			ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
			ctx.setListener(new Listener() {
				@Override
				public void onIrpf(IrpfOutcome irpfOutcome) {
					assertAnnualRemuneration(3300.00 * 14, irpfOutcome.getIrpfResult().getAnnualRemuneration());
					org.junit.Assert.assertEquals( 3300.00  * irpf[0] * j / 100.00, irpfOutcome.getIrpfRegularization().getPaidIrpf(), 0.0);
					org.junit.Assert.assertEquals( 3300.00 * 14.00 * irpf[0] / 100.00, irpfOutcome.getIrpfResult().getAnnualIrpf(), 0.0);
					org.junit.Assert.assertEquals(irpf[0], irpfOutcome.getIrpfResult().getIrpf(), 0.0);
				}
			});
			ctx.getIrpf();
			ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			calculateAndSave(connection, ctx);
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.JULY);
		calendar.set(DAY_OF_MONTH, 1);
		Date issueDate = new Date(calendar.getTimeInMillis());
		int year = calendar.get(Calendar.YEAR);

		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord julyExtra = getExtra(aonContext, agreement.getId(), "01/07");
		ISQLContractSalaryCalculatorContext extraCtx = getExtraSalaryCalculatorContext(connection, contract, julyExtra, year, issueDate);
		extraCtx.setListener(new Listener() {
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				assertAnnualRemuneration(3300.00 * 14, irpfOutcome.getIrpfResult().getAnnualRemuneration());
//				org.junit.Assert.assertEquals( 3300.00  * irpf[0] * 6 / 100.00, irpfOutcome.getIrpfRegularization().getPaidIrpf(), 0.0);
				org.junit.Assert.assertEquals( 3300.00 * 14.00 * irpf[0] / 100.00, irpfOutcome.getIrpfResult().getAnnualIrpf(), 0.0);
				org.junit.Assert.assertEquals(irpf[0], irpfOutcome.getIrpfResult().getIrpf(), 0.0);
			}
		});
		extraCtx.getIrpf();
		
		extraCtx = getExtraSalaryCalculatorContext(connection, contract, julyExtra, year, issueDate);
		SmartContractSalaryCalculator<Salary> contractSalaryCalculator = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder());
		Salary extra = contractSalaryCalculator.calculate(extraCtx);
		org.junit.Assert.assertEquals( 3300.00/2.00  , extra.getTotalPayment(), 0.0);
		org.junit.Assert.assertEquals( 3300.00/2.00  , extra.getIrpfBase(), 0.0);
		org.junit.Assert.assertEquals( 3300.00/2.00 * irpf[0]  / 100.00 , extra.getTotalIrpf(), 0.0);
		
		extraCtx = getExtraSalaryCalculatorContext(connection, contract, julyExtra, year, issueDate);
		calculateAndSave(connection, extraCtx);

		startDate = add(startDate, MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		ctx.setListener(new Listener() {
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				assertAnnualRemuneration(3300.00 * 14, irpfOutcome.getIrpfResult().getAnnualRemuneration());
				org.junit.Assert.assertEquals( 3300.00  * irpf[0] * 6 / 100.00, irpfOutcome.getIrpfRegularization().getPaidIrpf(), 0.0);
				org.junit.Assert.assertEquals( 3300.00 * 14.00 * irpf[0] / 100.00, irpfOutcome.getIrpfResult().getAnnualIrpf(), 0.0);
				org.junit.Assert.assertEquals(irpf[0], irpfOutcome.getIrpfResult().getIrpf(), 0.0);
			}
		});
		ctx.getIrpf();
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		calculateAndSave(connection, ctx);
		
		for ( int i = 0; i < 4; i++ ) {
			int j = i ;
			startDate = add(startDate, MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
			ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
			ctx.setListener(new Listener() {
				@Override
				public void onIrpf(IrpfOutcome irpfOutcome) {
					assertAnnualRemuneration(3300.00 * 14, irpfOutcome.getIrpfResult().getAnnualRemuneration());
					org.junit.Assert.assertEquals( 3300.00  * irpf[0] * ( 7.5 + j) / 100.00, irpfOutcome.getIrpfRegularization().getPaidIrpf(), 0.0);
					org.junit.Assert.assertEquals( 3300.00 * 14.00 * irpf[0] / 100.00, irpfOutcome.getIrpfResult().getAnnualIrpf(), 0.0);
					org.junit.Assert.assertEquals(irpf[0], irpfOutcome.getIrpfResult().getIrpf(), 0.0);
				}
			});
			ctx.getIrpf();
			ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			calculateAndSave(connection, ctx);
		}
		
		calendar.set(MONTH, Calendar.DECEMBER);
		calendar.set(DAY_OF_MONTH, 15);
		issueDate = new Date(calendar.getTimeInMillis());
		year = calendar.get(Calendar.YEAR);

		AgreementExtraRecord decemberExtra = getExtra(aonContext, agreement.getId(), "15/12");
		extraCtx = getExtraSalaryCalculatorContext(connection, contract, decemberExtra, year, issueDate);
		extraCtx.setListener(new Listener() {
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				assertAnnualRemuneration(3300.00 * 14, irpfOutcome.getIrpfResult().getAnnualRemuneration());
//				org.junit.Assert.assertEquals( 3300.00  * irpf[0] * 6 / 100.00, irpfOutcome.getIrpfRegularization().getPaidIrpf(), 0.0);
				org.junit.Assert.assertEquals( 3300.00 * 14.00 * irpf[0] / 100.00, irpfOutcome.getIrpfResult().getAnnualIrpf(), 0.0);
				org.junit.Assert.assertEquals(irpf[0], irpfOutcome.getIrpfResult().getIrpf(), 0.0);
			}
		});
		extraCtx.getIrpf();
		
		extra = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getExtraSalaryCalculatorContext(connection, contract, decemberExtra, year, issueDate));
		org.junit.Assert.assertEquals( 3300.00  , extra.getTotalPayment(), 0.0);
		org.junit.Assert.assertEquals( 3300.00  , extra.getIrpfBase(), 0.0);
		org.junit.Assert.assertEquals( 3300.00 * irpf[0]  / 100.00 , extra.getTotalIrpf(), 0.0);

	}

	@Test
	public void testExtrasII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");
		PaymentConceptRecord conceptAntiguedad = addConcept(aonContext, "ANTIGUEDAD");
		PaymentConceptRecord conceptPlusExtraSalarial = addConcept(aonContext, "PLUS_EXTRA_SALARIAL");

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
				},
				new Payment[] {
						new Payment() {
							{
								this.concept = conceptSalarioBase.getId();
								this.expression = "SALARIO_ANUAL / PAGAS * DIAS_TRABAJADOS/DIAS_MES";
							}
						},
						new Payment() {
							{
								this.concept = conceptPlusSalarial.getId();
								this.expression = "PLUS_ANUAL / PAGAS * DIAS_TRABAJADOS/DIAS_MES";
							}
						},
						new Payment() {
							{
								this.concept = conceptAntiguedad.getId();
								this.expression = "(SALARIO_BASE + PLUS_SALARIAL + PAGA_EXTRA) * 5 / 100";
							}
						},
						new Payment() {
							{
								this.concept = conceptAntiguedad.getId();
								this.expression = "(SALARIO_BASE + PLUS_SALARIAL + PAGA_EXTRA) * 0 / 100";
							}
						},
						new Payment() {
							{
								this.concept = conceptPagaExtra.getId();
								this.expression = "SALARIO_BASE + PLUS_SALARIAL";
							}
						},
						new Payment() {
							{
								this.concept = conceptPagaExtra.getId();
								this.expression = "SALARIO_BASE + PLUS_SALARIAL";
							}
						},
						new Payment() {
							{
								this.concept = conceptPlusExtraSalarial.getId();
								this.expression = "MEJORA * DIAS_TRABAJADOS/DIAS_MES";
							}
						}
				});
		
		addData(aonContext, category, getFirstDayOfYear(getToday()), null, new HashMap<String,String>(){
			{
				put("PAGAS", "14");
				put("PLUS_ANUAL", "4200.00");
				put("SALARIO_ANUAL", "42000.00");
			}
		});
		


		ContractRecord contract = newContract(
				aonContext
				,getFirstDayOfYear(getToday())
				, new HashMap<String,String>(){
					{
						put("MEJORA", "100.00");
						put("DIAS_MES", "30.00");
					}
				}
				, new String [] {}
				, new String [] {
						"BASE_CGC * 4.70 / 100.00"
						,"BASE_CGP * 1.55 / 100.00"
						,"BASE_CGP * 0.10 / 100.00"
						,"BASE_IRPF * PORCENTAJE_IRPF / 100.00"
				}
				,category);
		
		addPayment(aonContext, contract, conceptPlusExtraSalarial, "FRACCIONAR((SALARIO_BASE+PLUS_SALARIAL+PAGA_EXTRA+ANTIGUEDAD) * 10 / 100)");
		addPayment(aonContext, contract, conceptPlusExtraSalarial, "MEJORA * DIAS_TRABAJADOS/DIAS_MES");
		
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		
		double irpf []  = { 0.00 }; 
		ctx.setListener(new Listener() {
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				irpf[0] = irpfOutcome.getIrpfResult().getIrpf();
				assertAnnualRemuneration( 42000.00 + 4200.00 + 100.00*12 + (42000.00+ 4200.00) * 5 / 100.00 + (42000.00+ 4200.00 + (42000.00+ 4200.00) * 5 / 100.00) * 10 / 100.00 , irpfOutcome.getIrpfResult().getAnnualRemuneration());
//				org.junit.Assert.assertEquals( 3300.00 * 14.00 * irpf[0] / 100.00, irpfOutcome.getIrpfResult().getAnnualIrpf(), 0.0);
			}
		});
		ctx.getIrpf();
		
	}

	@Test
	@Ignore
	public void testExtrasIII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "SALARIO_MENSUAL + PLUS_MENSUAL";
							this.month = Month.JULY;
							this.start = "01/07 -1";
							this.end = "30/06";
							this.issue = "01/07";
						}
					}, 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "SALARIO_MENSUAL + PLUS_MENSUAL";
							this.month = Month.DECEMBER;
							this.start = "01/01";
							this.end = "31/12";
							this.issue = "15/12";
						}
					}, 
				},
				new Payment[] {
						new Payment() {
							{
								this.concept = conceptSalarioBase.getId();
								this.expression = "SALARIO_MENSUAL * DIAS_TRABAJADOS/DIAS_MES";
							}
						},
						new Payment() {
							{
								this.concept = conceptPlusSalarial.getId();
								this.expression = "PLUS_MENSUAL * DIAS_TRABAJADOS/DIAS_MES";
							}
						}
				});
		
		addData(aonContext, category, getFirstDayOfYear(getToday()), null, new HashMap<String,String>(){
			{
				put("PLUS_MENSUAL", "200.00");
				put("SALARIO_MENSUAL", "2000.00");
			}
		});
		


		ContractRecord contract = newContract(
				aonContext
				,getFirstDayOfYear(getToday())
				, new HashMap<String,String>(){
					{
						put("DIAS_MES", "30.00");
					}
				}
				, new String [] {}
				, new String [] {
						"BASE_CGC * 4.70 / 100.00"
						,"BASE_CGP * 1.55 / 100.00"
						,"BASE_CGP * 0.10 / 100.00"
						,"BASE_IRPF * PORCENTAJE_IRPF / 100.00"
				}
				,category);
		
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		
		double irpf []  = { 0.00 }; 
		ctx.setListener(new Listener() {
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				irpf[0] = irpfOutcome.getIrpfResult().getIrpf();
				assertAnnualRemuneration(2200.00 * 14, irpfOutcome.getIrpfResult().getAnnualRemuneration());
				org.junit.Assert.assertEquals( 2200.00 * 14.00 * irpf[0] / 100.00, irpfOutcome.getIrpfResult().getAnnualIrpf(), DELTA);
			}
		});
		ctx.getIrpf();
		
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		calculateAndSave(connection, ctx);
		

		for ( int i = 1; i <= 5; i++ ) {
			int j = i;
			startDate = add(startDate, MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
			ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
			ctx.setListener(new Listener() {
				@Override
				public void onIrpf(IrpfOutcome irpfOutcome) {
					assertAnnualRemuneration(2200.00 * 14, irpfOutcome.getIrpfResult().getAnnualRemuneration());
					org.junit.Assert.assertEquals( 2200.00  * irpf[0] * j / 100.00, irpfOutcome.getIrpfRegularization().getPaidIrpf(), DELTA);
					org.junit.Assert.assertEquals( 2200.00 * 14.00 * irpf[0] / 100.00, irpfOutcome.getIrpfResult().getAnnualIrpf(), DELTA);
					org.junit.Assert.assertEquals(irpf[0], irpfOutcome.getIrpfResult().getIrpf(), DELTA);
				}
			});
			ctx.getIrpf();
			ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			calculateAndSave(connection, ctx);
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.JULY);
		calendar.set(DAY_OF_MONTH, 1);
		Date issueDate = new Date(calendar.getTimeInMillis());
		int year = calendar.get(Calendar.YEAR);

		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord julyExtra = getExtra(aonContext, agreement.getId(), "01/07");
		ISQLContractSalaryCalculatorContext extraCtx = getExtraSalaryCalculatorContext(connection, contract, julyExtra, year, issueDate);
		extraCtx.setListener(new Listener() {
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				assertAnnualRemuneration(2200.00 * 14, irpfOutcome.getIrpfResult().getAnnualRemuneration());
//				org.junit.Assert.assertEquals( 2200.00  * irpf[0] * 6 / 100.00, irpfOutcome.getIrpfRegularization().getPaidIrpf(), 0.0);
				org.junit.Assert.assertEquals( 2200.00 * 14.00 * irpf[0] / 100.00, irpfOutcome.getIrpfResult().getAnnualIrpf(), 0.0);
				org.junit.Assert.assertEquals(irpf[0], irpfOutcome.getIrpfResult().getIrpf(), 0.0);
			}
		});
		extraCtx.getIrpf();
		
		extraCtx = getExtraSalaryCalculatorContext(connection, contract, julyExtra, year, issueDate);
		SmartContractSalaryCalculator<Salary> contractSalaryCalculator = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder());
		Salary extra = contractSalaryCalculator.calculate(extraCtx);
		org.junit.Assert.assertEquals( 2200.00/2.00  , extra.getTotalPayment(), 0.0);
		org.junit.Assert.assertEquals( 2200.00/2.00  , extra.getIrpfBase(), 0.0);
		org.junit.Assert.assertEquals( 2200.00/2.00 * irpf[0]  / 100.00 , extra.getTotalIrpf(), 0.0);
		
		extraCtx = getExtraSalaryCalculatorContext(connection, contract, julyExtra, year, issueDate);
		calculateAndSave(connection, extraCtx);

		startDate = add(startDate, MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		ctx.setListener(new Listener() {
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				assertAnnualRemuneration(2200.00 * 14, irpfOutcome.getIrpfResult().getAnnualRemuneration());
				org.junit.Assert.assertEquals( 2200.00  * irpf[0] * 6 / 100.00, irpfOutcome.getIrpfRegularization().getPaidIrpf(), 0.0);
				org.junit.Assert.assertEquals( 2200.00 * 14.00 * irpf[0] / 100.00, irpfOutcome.getIrpfResult().getAnnualIrpf(), 0.0);
				org.junit.Assert.assertEquals(irpf[0], irpfOutcome.getIrpfResult().getIrpf(), 0.0);
			}
		});
		ctx.getIrpf();
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		calculateAndSave(connection, ctx);
		
		for ( int i = 0; i < 4; i++ ) {
			int j = i ;
			startDate = add(startDate, MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
			ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
			ctx.setListener(new Listener() {
				@Override
				public void onIrpf(IrpfOutcome irpfOutcome) {
					assertAnnualRemuneration(2200.00 * 14, irpfOutcome.getIrpfResult().getAnnualRemuneration());
					org.junit.Assert.assertEquals( 2200.00  * irpf[0] * ( 7.5 + j) / 100.00, irpfOutcome.getIrpfRegularization().getPaidIrpf(), 0.0);
					org.junit.Assert.assertEquals( 2200.00 * 14.00 * irpf[0] / 100.00, irpfOutcome.getIrpfResult().getAnnualIrpf(), 0.0);
					org.junit.Assert.assertEquals(irpf[0], irpfOutcome.getIrpfResult().getIrpf(), 0.0);
				}
			});
			ctx.getIrpf();
			ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			calculateAndSave(connection, ctx);
		}
		
		calendar.set(MONTH, Calendar.DECEMBER);
		calendar.set(DAY_OF_MONTH, 15);
		issueDate = new Date(calendar.getTimeInMillis());
		year = calendar.get(Calendar.YEAR);

		AgreementExtraRecord decemberExtra = getExtra(aonContext, agreement.getId(), "15/12");
		extraCtx = getExtraSalaryCalculatorContext(connection, contract, decemberExtra, year, issueDate);
		extraCtx.setListener(new Listener() {
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				assertAnnualRemuneration(2200.00 * 14, irpfOutcome.getIrpfResult().getAnnualRemuneration());
//				org.junit.Assert.assertEquals( 2200.00  * irpf[0] * 6 / 100.00, irpfOutcome.getIrpfRegularization().getPaidIrpf(), 0.0);
				org.junit.Assert.assertEquals( 2200.00 * 14.00 * irpf[0] / 100.00, irpfOutcome.getIrpfResult().getAnnualIrpf(), 0.0);
				org.junit.Assert.assertEquals(irpf[0], irpfOutcome.getIrpfResult().getIrpf(), 0.0);
			}
		});
		extraCtx.getIrpf();
		
		extra = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getExtraSalaryCalculatorContext(connection, contract, decemberExtra, year, issueDate));
		org.junit.Assert.assertEquals( 2200.00  , extra.getTotalPayment(), 0.0);
		org.junit.Assert.assertEquals( 2200.00  , extra.getIrpfBase(), 0.0);
		org.junit.Assert.assertEquals( 2200.00 * irpf[0]  / 100.00 , extra.getTotalIrpf(), 0.0);
	}

	@Test
	public void testExtrasAtSalaryI() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");
		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);

		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.JUNE);
		calendar.set(DAY_OF_MONTH, 1);

		
		Date firstDayOfYear = getFirstDayOfYear(getToday());
		
		Date contractStart = add(firstDayOfYear, DAY_OF_MONTH, -66); 

		ContractRecord contract = newContract(
				aonContext
				,contractStart
				,new String[] {} 
				,new String[] {
					"BASE_CGC * 4.70 / 100.00"
					,"BASE_CGP * 1.55 / 100.00"
					,"BASE_CGP * 0.10 / 100.00"
					,"BASE_IRPF * PORCENTAJE_IRPF / 100.00"
				} 
				,null);
		
		
		addPayment(aonContext, contract, firstDayOfYear, null, conceptPagaExtra, "PAGA EXTRA", "SALARIO_BASE / 12", "_P", "_P", PaymentType.CRA_0004, (byte)0);
		addPayment(aonContext, contract, firstDayOfYear, null, conceptPagaExtra, "PAGA EXTRA", "SALARIO_BASE / 12", "_P", "_P", PaymentType.CRA_0004, (byte)0);
		addPayment(aonContext, contract, firstDayOfYear, null, conceptSalarioBase, "SALARIO BASE", "3000.00", "_P", "_P", PaymentType.CRA_0001);
		
		// 
		// MARCH
		//
		Date startDate = firstDayOfYear;
		Date endDate = getLastDayOfMonth(startDate);

		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		for ( SalaryDeduction deduction: salary.getSalaryDeductions() ) 
			System.out.println(deduction.getDescription() + " = " + deduction.getAmount() );
		
	}

	@Test
	public void testExtrasAtSalaryII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");
		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);

		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.JUNE);
		calendar.set(DAY_OF_MONTH, 1);

		
		Date firstDayOfYear = getFirstDayOfYear(getToday());
		
		Date contractStart = add(firstDayOfYear, DAY_OF_MONTH, -66); 

		ContractRecord contract = newContract(
				aonContext
				,contractStart
				,new String[] {} 
				,new String[] {
					"BASE_CGC * 4.70 / 100.00"
					,"BASE_CGP * 1.55 / 100.00"
					,"BASE_CGP * 0.10 / 100.00"
					,"BASE_IRPF * PORCENTAJE_IRPF / 100.00"
				} 
				,null);
		
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 1.00 * %s_1_3",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 1.00 * %s_4_15",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 1.00 * %s_16_20",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 1.00 * %s_21",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, 
				firstDayOfYear, null, conceptPagaExtra, "PAGA EXTRA", 
				"SALARIO_BASE / 12", "_P", "_P", PaymentType.CRA_0004, (byte)0)
				;
		addPayment(aonContext, contract, 
				firstDayOfYear, null, conceptPagaExtra, "PAGA EXTRA", 
				"SALARIO_BASE / 12", "_P", "_P", PaymentType.CRA_0004, (byte)0
				);
		addPayment(aonContext, contract, 
				firstDayOfYear, null, conceptSalarioBase, "SALARIO BASE", 
				"3000.00", "_P", "_P", PaymentType.CRA_0001
				);
		
		// 
		// MARCH
		//
		Date startDate = firstDayOfYear;
		Date endDate = getLastDayOfMonth(startDate);
		
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, add(startDate, DAY_OF_MONTH, 2 ) , null, null);

		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		for ( SalaryDeduction deduction: salary.getSalaryDeductions() ) 
			System.out.println(deduction.getDescription() + " = " + deduction.getAmount() );
		
	}

	@Test
	public void testSimpleWithERE() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemPayments(aonContext);
		
		addSSRegimePayment(aonContext 
				,SSRegimeType.GENERAL 
				,getFirstDayOfYear(getToday()) 
				,PaymentType.CRA_0000
				,"TRACE('BASE_REGULADORA: %f \r\n',BASE_REGULADORA);0.00"
				,"TRACE('DIAS_ENFERMEDAD_COMUN_21: %d \r\n',DIAS_ENFERMEDAD_COMUN_21);0.00"
				,"TRACE('P_0 + P_1 + P_2 : %f \r\n',P_0 + P_1 + P_2);0.00"
				);

		
		Extra extras [] = new Extra[] {
							new Extra() {
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
							}
						};
		
		AgreementLevelCategoryRecord category = null;
		if (extras != null && extras.length > 0)
			category = newAgreement(aonContext, extras);

		Date startContract = AonDateUtils.getFirstDayOfYear(getToday());

		ContractRecord contract = newContract(aonContext, 
				startContract, 
				null, 
				new HashMap<String, String>() {
					{
						put(TC2.getName(), C100.getValue());
						put(ContextVariable.MONTH_DAYS.getName(), "30.00");
						put(ContextVariable.QUOTE_GROUP.getName(), "'01'");
					}
				}, 
				new String[] { 
				"( P_1 + P_2 ) * 0.10 ",
				"150.00 * DIAS_TRABAJADOS / DIAS_MES",
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" 
				}, 
				new String[] {
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05", 
				"BASE_ESTR * 0.10",
				"BASE_NESTR * 0.20", 
				"BASE_IRPF * PORCENTAJE_IRPF" 
				},
				null);

		
		Date salaryStart = getFirstDayOfMonth(add(startContract, Calendar.MONTH,3));
		Date salaryEnd = getLastDayOfMonth(salaryStart);
		Date salaryIssue = salaryEnd;

		Date startEREDate = add(startContract, DAY_OF_MONTH,15);
		addData(aonContext, contract, startEREDate, null, ERE_FACTOR, 1.00);
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, salaryStart, salaryEnd, salaryIssue, contract);

		ctx.setListener(new Listener() {

			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				System.out.println("Irpf : " + irpfOutcome.getIrpfResult().getIrpf() );
				System.out.println("AnnualIrpf : " + irpfOutcome.getIrpfResult().getAnnualIrpf() );
				System.out.println("AnnualRemuneration : " + irpfOutcome.getIrpfResult().getAnnualRemuneration() );
				
				assertAnnualRemuneration(400.00 * 1.10 * 12 , irpfOutcome.getIrpfResult().getAnnualRemuneration(), 8, 0.009);
//				assertAnnualRemuneration(400.00 * 1.10 * 11 + 400.00 * 1.10 * 0.75, irpfOutcome.getIrpfResult().getAnnualRemuneration(), 8, 0.009);
//				assertAnnualRemuneration(400.00 * 1.10 * 0.75 * 3 + 400.00 * 1.10 * 8, irpfOutcome.getIrpfResult().getAnnualRemuneration(), 8, 0.009);
			}
		});

		//ctx.getIrpf();
		
		Salary salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
	}

	@Test
	public void testTemporaryI() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()), 
				null, 
				new HashMap<String, String>(){
					{
						put(TC2.getName(), String.format("\"%s\"", C501.getValue())); // 501 - DURACION DETERMINADA, TIEMPO PARCIAL, OBRA O SERVICIO DETERMINADO
					}
				}, 
				new String [] {
						"250.00",
						"250.00",
				}, 
				new String [] {
						"PORCENTAJE_IRPF / 100.00 * BASE_IRPF"
				}, 
				null);
		
		List<Double[]> irpfResults = new ArrayList<Double[]>(12);
		// Be care that the first day of the month has value 1.
		Date startDate = getFirstDayOfYear(getToday());
		for ( int i = 0; i < 12 ; i++ ) {
			Date endDate = getLastDayOfMonth(startDate);
			ISQLContractSalaryCalculatorContext ctx = super.getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			ctx.setListener( new Listener() {
				@Override
				public void onIrpf(IrpfOutcome irpfOutcome) {
					super.onIrpf(irpfOutcome);
					Double irpfResult [] = new Double[4]; 
					irpfResult[0] = irpfOutcome.getIrpfResult().getIrpf();
					irpfResult[1] = irpfOutcome.getIrpfResult().getBaseIrpf();
					irpfResult[2] = irpfOutcome.getIrpfResult().getAnnualIrpf();
					irpfResult[3] = irpfOutcome.getIrpfResult().getAnnualRemuneration();
					irpfResults.add(irpfResult);
					
					
					System.out.println("Irpf:" + irpfOutcome.getIrpfResult().getIrpf());
					System.out.println("BaseIrpf:" + irpfOutcome.getIrpfResult().getBaseIrpf());
					System.out.println("AnnualIrpf:" + irpfOutcome.getIrpfResult().getAnnualIrpf());
					System.out.println("AnnualRemuneration:" + irpfOutcome.getIrpfResult().getAnnualRemuneration());
				
					org.junit.Assert.assertEquals(2.00, irpfOutcome.getIrpfResult().getIrpf(), 0.00);
					org.junit.Assert.assertEquals(500.00*12 * 2 /100, irpfOutcome.getIrpfResult().getAnnualIrpf(), 0.00);
					org.junit.Assert.assertEquals(500.00*12, irpfOutcome.getIrpfResult().getAnnualRemuneration(), 0.00);
				}
			});
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			new SmartContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(startDate, Calendar.MONTH, 1); 
		}
		
		
	}
	
	
	@Test
	public void testTemporaryII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()), 
				null, 
				new HashMap<String, String>(){
					{
						put(TC2.getName(), String.format("\"%s\"", C501.getValue())); // 501 - DURACION DETERMINADA, TIEMPO PARCIAL, OBRA O SERVICIO DETERMINADO
					}
				}, 
				new String [] {
						"250.00",
						"250.00",
				}, 
				new String [] {
						"PORCENTAJE_IRPF / 100.00 * BASE_IRPF"
				}, 
				null);
		
		List<Double[]> irpfResults = new ArrayList<Double[]>(12);
		// Be care that the first day of the month has value 1.
		Date startDate = getFirstDayOfYear(getToday());
		
		addPayment(aonContext, contract, startDate, getLastDayOfMonth(startDate), "1000.00");
		
		for ( int i = 0; i < 12 ; i++ ) {
			
			Date endDate = getLastDayOfMonth(startDate);
			
			
			
			ISQLContractSalaryCalculatorContext ctx = super.getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			ctx.setListener( new Listener() {
				@Override
				public void onIrpf(IrpfOutcome irpfOutcome) {
					super.onIrpf(irpfOutcome);
					Double irpfResult [] = new Double[4]; 
					irpfResult[0] = irpfOutcome.getIrpfResult().getIrpf();
					irpfResult[1] = irpfOutcome.getIrpfResult().getBaseIrpf();
					irpfResult[2] = irpfOutcome.getIrpfResult().getAnnualIrpf();
					irpfResult[3] = irpfOutcome.getIrpfResult().getAnnualRemuneration();
					irpfResults.add(irpfResult);
					
					
					System.out.println("Irpf:" + irpfOutcome.getIrpfResult().getIrpf());
					System.out.println("BaseIrpf:" + irpfOutcome.getIrpfResult().getBaseIrpf());
					System.out.println("AnnualIrpf:" + irpfOutcome.getIrpfResult().getAnnualIrpf());
					System.out.println("AnnualRemuneration:" + irpfOutcome.getIrpfResult().getAnnualRemuneration());
				
					org.junit.Assert.assertEquals(2.00, irpfOutcome.getIrpfResult().getIrpf(), 0.00);
					org.junit.Assert.assertEquals((500.00*12 + 1000.00) * 2 /100, irpfOutcome.getIrpfResult().getAnnualIrpf(), 0.00);
					org.junit.Assert.assertEquals((500.00*12 + 1000.00), irpfOutcome.getIrpfResult().getAnnualRemuneration(), 0.00);
				}
			});
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			new SmartContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(startDate, Calendar.MONTH, 1); 
		}
		
		
	}

	@Test
	@Ignore
	public void testTemporaryIII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()), 
				null, 
				new HashMap<String, String>(){
					{
						put(TC2.getName(), String.format("\"%s\"", C501.getValue())); // 501 - DURACION DETERMINADA, TIEMPO PARCIAL, OBRA O SERVICIO DETERMINADO
					}
				}, 
				new String [] {
						"250.00",
						"250.00",
						"PLUS_NOCTURNIDAD"
				}, 
				new String [] {
						"PORCENTAJE_IRPF / 100.00 * BASE_IRPF"
				}, 
				null);
		
		List<Double[]> irpfResults = new ArrayList<Double[]>(12);
		// Be care that the first day of the month has value 1.
		Date startDate = getFirstDayOfYear(getToday());
		
		//addPayment(aonContext, contract, startDate, getLastDayOfMonth(startDate), "100.00");
		addData(aonContext, contract, startDate, getLastDayOfMonth(startDate), "PLUS_NOCTURNIDAD", "100.00");
		
		for ( int i = 0; i < 12 ; i++ ) {
			
			Date endDate = getLastDayOfMonth(startDate);
			
			ISQLContractSalaryCalculatorContext ctx = super.getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			ctx.setListener( new Listener() {
				@Override
				public void onIrpf(IrpfOutcome irpfOutcome) {
					super.onIrpf(irpfOutcome);
					Double irpfResult [] = new Double[4]; 
					irpfResult[0] = irpfOutcome.getIrpfResult().getIrpf();
					irpfResult[1] = irpfOutcome.getIrpfResult().getBaseIrpf();
					irpfResult[2] = irpfOutcome.getIrpfResult().getAnnualIrpf();
					irpfResult[3] = irpfOutcome.getIrpfResult().getAnnualRemuneration();
					irpfResults.add(irpfResult);
					
					
					System.out.println("Irpf:" + irpfOutcome.getIrpfResult().getIrpf());
					System.out.println("BaseIrpf:" + irpfOutcome.getIrpfResult().getBaseIrpf());
					System.out.println("AnnualIrpf:" + irpfOutcome.getIrpfResult().getAnnualIrpf());
					System.out.println("AnnualRemuneration:" + irpfOutcome.getIrpfResult().getAnnualRemuneration());
				
					org.junit.Assert.assertEquals(2.00, irpfOutcome.getIrpfResult().getIrpf(), 0.00);
					org.junit.Assert.assertEquals((500.00*12 + 100.00), irpfOutcome.getIrpfResult().getAnnualRemuneration(), 0.00);
					org.junit.Assert.assertEquals((500.00*12 + 100.00) * 2 /100, irpfOutcome.getIrpfResult().getAnnualIrpf(), 0.00);
				}
			});
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			new SmartContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(startDate, Calendar.MONTH, 1); 
		}
		
		
	}


	@Test
	public void testTemporaryIV() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()), 
				null, 
				new HashMap<String, String>(){
					{
						put(TC2.getName(), String.format("\"%s\"", C501.getValue())); // 501 - DURACION DETERMINADA, TIEMPO PARCIAL, OBRA O SERVICIO DETERMINADO
					}
				}, 
				new String [] {
						"250.00",
						"250.00",
				}, 
				new String [] {
						"PORCENTAJE_IRPF / 100.00 * BASE_IRPF"
				}, 
				null);
		
		List<Double[]> irpfResults = new ArrayList<Double[]>(12);
		// Be care that the first day of the month has value 1.
		Date startDate = getFirstDayOfYear(getToday());
		
		addPayment(aonContext, contract, startDate, getLastDayOfMonth(startDate), "1000.00");
		
		for ( int i = 0; i < 12 ; i++ ) {
			
			Date endDate = getLastDayOfMonth(startDate);
			
			
			
			ISQLContractSalaryCalculatorContext ctx = super.getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			ctx.setListener( new Listener() {
				@Override
				public void onIrpf(IrpfOutcome irpfOutcome) {
					super.onIrpf(irpfOutcome);
					Double irpfResult [] = new Double[4]; 
					irpfResult[0] = irpfOutcome.getIrpfResult().getIrpf();
					irpfResult[1] = irpfOutcome.getIrpfResult().getBaseIrpf();
					irpfResult[2] = irpfOutcome.getIrpfResult().getAnnualIrpf();
					irpfResult[3] = irpfOutcome.getIrpfResult().getAnnualRemuneration();
					irpfResults.add(irpfResult);
					
					
					System.out.println("Irpf:" + irpfOutcome.getIrpfResult().getIrpf());
					System.out.println("BaseIrpf:" + irpfOutcome.getIrpfResult().getBaseIrpf());
					System.out.println("AnnualIrpf:" + irpfOutcome.getIrpfResult().getAnnualIrpf());
					System.out.println("AnnualRemuneration:" + irpfOutcome.getIrpfResult().getAnnualRemuneration());
				
					org.junit.Assert.assertEquals(2.00, irpfOutcome.getIrpfResult().getIrpf(), 0.00);
					org.junit.Assert.assertEquals((500.00*12 + 1000.00) * 2 /100, irpfOutcome.getIrpfResult().getAnnualIrpf(), 0.00);
					org.junit.Assert.assertEquals((500.00*12 + 1000.00), irpfOutcome.getIrpfResult().getAnnualRemuneration(), 0.00);
				}
			});
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			new SmartContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(startDate, Calendar.MONTH, 1); 
		}
		
		
	}

	@Test
	public void testTemporaryExtra() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()), 
				null, 
				new HashMap<String, String>(){
					{
						put(TC2.getName(), String.format("\"%s\"", C501.getValue())); // 501 - DURACION DETERMINADA, TIEMPO PARCIAL, OBRA O SERVICIO DETERMINADO
					}
				}, 
				new String [] {
						"250.00",
						"250.00",
				}, 
				new String [] {
						"PORCENTAJE_IRPF / 100.00 * BASE_IRPF"
				}, 
				null);
		
		List<Double[]> irpfResults = new ArrayList<Double[]>(12);
		// Be care that the first day of the month has value 1.
		Date startDate = getFirstDayOfYear(getToday());
		
		addPayment(aonContext, contract, startDate, null, "B3N3F1C10S", "1000.00", "_P", "_P", PaymentType.CRA_0004, Month.JUNE);
		
		for ( int i = 0; i < 12 ; i++ ) {
			
			Date endDate = getLastDayOfMonth(startDate);
			
			
			
			ISQLContractSalaryCalculatorContext ctx = super.getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			ctx.setListener( new Listener() {
				@Override
				public void onIrpf(IrpfOutcome irpfOutcome) {
					super.onIrpf(irpfOutcome);
					Double irpfResult [] = new Double[4]; 
					irpfResult[0] = irpfOutcome.getIrpfResult().getIrpf();
					irpfResult[1] = irpfOutcome.getIrpfResult().getBaseIrpf();
					irpfResult[2] = irpfOutcome.getIrpfResult().getAnnualIrpf();
					irpfResult[3] = irpfOutcome.getIrpfResult().getAnnualRemuneration();
					irpfResults.add(irpfResult);
					
					
					System.out.println("Irpf:" + irpfOutcome.getIrpfResult().getIrpf());
					System.out.println("BaseIrpf:" + irpfOutcome.getIrpfResult().getBaseIrpf());
					System.out.println("AnnualIrpf:" + irpfOutcome.getIrpfResult().getAnnualIrpf());
					System.out.println("AnnualRemuneration:" + irpfOutcome.getIrpfResult().getAnnualRemuneration());
					if ( irpfOutcome.getIrpfRegularization() != null ) {
						System.out.println("PaidIrpf:" + irpfOutcome.getIrpfRegularization().getPaidIrpf());
						System.out.println("PaidRemuneration:" + irpfOutcome.getIrpfRegularization().getPaidRemuneration());
					}
				
					org.junit.Assert.assertEquals(2.00, irpfOutcome.getIrpfResult().getIrpf(), 0.00);
					org.junit.Assert.assertEquals((500.00*12 + 1000.00), irpfOutcome.getIrpfResult().getAnnualRemuneration(), 0.00);
					org.junit.Assert.assertEquals((500.00*12 + 1000.00) * 2 /100, irpfOutcome.getIrpfResult().getAnnualIrpf(), 0.00);
				}
			});
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			new SmartContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(startDate, Calendar.MONTH, 1); 
		}
		
		
	}

	@Test
	public void testTemporaryExtraII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()), 
				null, 
				new HashMap<String, String>(){
					{
						put(TC2.getName(), String.format("\"%s\"", C501.getValue())); // 501 - DURACION DETERMINADA, TIEMPO PARCIAL, OBRA O SERVICIO DETERMINADO
					}
				}, 
				new String [] {
						"250.00",
						"250.00",
				}, 
				new String [] {
						"PORCENTAJE_IRPF / 100.00 * BASE_IRPF"
				}, 
				null);
		
		List<Double[]> irpfResults = new ArrayList<Double[]>(12);
		// Be care that the first day of the month has value 1.
		Date startDate = getFirstDayOfYear(getToday());
		
		addPayment(aonContext, contract, startDate, null, "PAGA VERANO", "500.00", "_P", "_P/12", PaymentType.CRA_0004, Month.JUNE);
		addPayment(aonContext, contract, startDate, null, "PAGA NAVIDAD", "500.00", "_P", "_P/12", PaymentType.CRA_0004, Month.DECEMBER);
		
		for ( int i = 0; i < 12 ; i++ ) {
			
			Date endDate = getLastDayOfMonth(startDate);
			
			
			
			ISQLContractSalaryCalculatorContext ctx = super.getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			ctx.setListener( new Listener() {
				@Override
				public void onIrpf(IrpfOutcome irpfOutcome) {
					super.onIrpf(irpfOutcome);
					Double irpfResult [] = new Double[4]; 
					irpfResult[0] = irpfOutcome.getIrpfResult().getIrpf();
					irpfResult[1] = irpfOutcome.getIrpfResult().getBaseIrpf();
					irpfResult[2] = irpfOutcome.getIrpfResult().getAnnualIrpf();
					irpfResult[3] = irpfOutcome.getIrpfResult().getAnnualRemuneration();
					irpfResults.add(irpfResult);
					
					
					System.out.println("Irpf:" + irpfOutcome.getIrpfResult().getIrpf());
					System.out.println("BaseIrpf:" + irpfOutcome.getIrpfResult().getBaseIrpf());
					System.out.println("AnnualIrpf:" + irpfOutcome.getIrpfResult().getAnnualIrpf());
					System.out.println("AnnualRemuneration:" + irpfOutcome.getIrpfResult().getAnnualRemuneration());
					if ( irpfOutcome.getIrpfRegularization() != null ) {
						System.out.println("PaidIrpf:" + irpfOutcome.getIrpfRegularization().getPaidIrpf());
						System.out.println("PaidRemuneration:" + irpfOutcome.getIrpfRegularization().getPaidRemuneration());
					}
				
					org.junit.Assert.assertEquals(2.00, irpfOutcome.getIrpfResult().getIrpf(), 0.00);
					org.junit.Assert.assertEquals((500.00*12 + 1000.00), irpfOutcome.getIrpfResult().getAnnualRemuneration(), 0.00);
					org.junit.Assert.assertEquals((500.00*12 + 1000.00) * 2 /100, irpfOutcome.getIrpfResult().getAnnualIrpf(), 0.00);
				}
			});
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			new SmartContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(startDate, Calendar.MONTH, 1); 
		}
		
		
	}

	@Test
	public void testTemporaryBonus() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()), 
				null, 
				new HashMap<String, String>(){
					{
						put(TC2.getName(), String.format("\"%s\"", C501.getValue())); // 501 - DURACION DETERMINADA, TIEMPO PARCIAL, OBRA O SERVICIO DETERMINADO
					}
				}, 
				new String [] {
						"250.00",
						"250.00",
				}, 
				new String [] {
						"PORCENTAJE_IRPF / 100.00 * BASE_IRPF"
				}, 
				null);
		
		List<Double[]> irpfResults = new ArrayList<Double[]>(12);
		// Be care that the first day of the month has value 1.
		Date startDate = getFirstDayOfYear(getToday());
		
		addPayment(aonContext, contract, startDate, null, "BENEFICIOS", "1000.00", "_P", "_P", PaymentType.CRA_0005, Month.MARCH);
		
		for ( int i = 0; i < 12 ; i++ ) {
			
			Date endDate = getLastDayOfMonth(startDate);
			
			
			
			ISQLContractSalaryCalculatorContext ctx = super.getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			ctx.setListener( new Listener() {
				@Override
				public void onIrpf(IrpfOutcome irpfOutcome) {
					super.onIrpf(irpfOutcome);
					Double irpfResult [] = new Double[4]; 
					irpfResult[0] = irpfOutcome.getIrpfResult().getIrpf();
					irpfResult[1] = irpfOutcome.getIrpfResult().getBaseIrpf();
					irpfResult[2] = irpfOutcome.getIrpfResult().getAnnualIrpf();
					irpfResult[3] = irpfOutcome.getIrpfResult().getAnnualRemuneration();
					irpfResults.add(irpfResult);
					
					
					System.out.println(ctx.getStartDate());
					System.out.println("Irpf:" + irpfOutcome.getIrpfResult().getIrpf());
					System.out.println("BaseIrpf:" + irpfOutcome.getIrpfResult().getBaseIrpf());
					System.out.println("AnnualIrpf:" + irpfOutcome.getIrpfResult().getAnnualIrpf());
					System.out.println("AnnualRemuneration:" + irpfOutcome.getIrpfResult().getAnnualRemuneration());
					if ( irpfOutcome.getIrpfRegularization() != null ) {
						System.out.println("PaidIrpf:" + irpfOutcome.getIrpfRegularization().getPaidIrpf());
						System.out.println("PaidRemuneration:" + irpfOutcome.getIrpfRegularization().getPaidRemuneration());
					}
				
					org.junit.Assert.assertEquals(2.00, irpfOutcome.getIrpfResult().getIrpf(), 0.00);
					org.junit.Assert.assertEquals((500.00*12 + 1000.00), irpfOutcome.getIrpfResult().getAnnualRemuneration(), 0.00);
					org.junit.Assert.assertEquals((500.00*12 + 1000.00) * 2 /100, irpfOutcome.getIrpfResult().getAnnualIrpf(), 0.00);
				}
			});
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			new SmartContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(startDate, Calendar.MONTH, 1); 
		}
		
		
	}

	public void testErrorsI() {
		// Descendiente > 25  
		try {
			IrpfCalculator.calculateIrpf2022(new DefaultIrpfCalculatorContext() {
				@Override
				public Iterable<Descendiente> getDescendientes() {
					Descendiente descendiente = new Descendiente() {

						@Override
						public Integer getAñoNacimiento() {
							return 1975;
						}

						@Override
						public Integer getAñoAdopcion() {
							return 1975;
						}

						@Override
						public boolean getComputadoEntero() {
							return true;
						}

						@Override
						public boolean getMovilidadReducida() {
							return false;
						}

						@Override
						public Discapacidad getDiscapacidad() {
							return Discapacidad.GRADO0;
						}
						
					};
					return Collections.singleton(descendiente);
				}
			});
		} catch ( Exception e ) {
			if ( AonStringUtils.equalsIgnoreCase(e.getCause().getMessage(), "Error al calcular el IRPF") ) { 
				org.junit.Assert.fail();
			}
			System.out.println(e.getCause().getMessage());
		}
	
		// Ascendiente < 65    
		try {
			IrpfCalculator.calculateIrpf2022(new DefaultIrpfCalculatorContext() {
				@Override
				public Iterable<Ascendiente> getAscendientes() {
					Ascendiente ascendiente = new Ascendiente() {

						@Override
						public Integer getAñoNacimiento() {
							return 2015;
						}

						@Override
						public Convivencia getConvivecia() {
							return Convivencia.UNO;
						}

						@Override
						public boolean getMovilidadReducida() {
							// TODO Auto-generated method stub
							return false;
						}

						@Override
						public Discapacidad getDiscapacidad() {
							return Discapacidad.GRADO0;
						}
						
					};
					return Collections.singleton(ascendiente);
				}
			});
		} catch ( Exception e ) {
			if ( AonStringUtils.equalsIgnoreCase(e.getCause().getMessage(), "Error al calcular el IRPF") ) { 
				org.junit.Assert.fail();
			}
			System.out.println(e.getCause().getMessage());
		}
		
		
		try {
			IrpfCalculator.calculateIrpf2022(new DefaultIrpfCalculatorContext() {
				@Override
				public String getRetenedorNif() {
					return "987654321";
				}
			});
		} catch ( Exception e ) {
			if ( AonStringUtils.equalsIgnoreCase(e.getCause().getMessage(), "Error al calcular el IRPF") ) { 
				org.junit.Assert.fail();
			}
			System.out.println(e.getCause().getMessage());
		}

		try {
			IrpfCalculator.calculateIrpf2022(new DefaultIrpfCalculatorContext() {
				@Override
				public String getNif() {
					return "123456789";
				}
			});
		} catch ( Exception e ) {
			if ( AonStringUtils.equalsIgnoreCase(e.getCause().getMessage(), "Error al calcular el IRPF") ) { 
				org.junit.Assert.fail();
			}
			System.out.println(e.getCause().getMessage());
		}

		try {
			IrpfCalculator.calculateIrpf2022(new DefaultIrpfCalculatorContext() {
				@Override
				public boolean getPagoPrestamosVivienda() {
					return true;
				}
			});
		} catch ( Exception e ) {
			if ( AonStringUtils.equalsIgnoreCase(e.getCause().getMessage(), "Error al calcular el IRPF") ) { 
				org.junit.Assert.fail();
			}
			System.out.println(e.getCause().getMessage());
		}

		try {
			IrpfCalculator.calculateIrpf2022(new DefaultIrpfCalculatorContext() {
				@Override
				public String getRetenedorApellidosNombre() {
					return null;
				}
			});
		} catch ( Exception e ) {
			if ( AonStringUtils.equalsIgnoreCase(e.getCause().getMessage(), "Error al calcular el IRPF") ) { 
				org.junit.Assert.fail();
			}
			System.out.println(e.getCause().getMessage());
		}

		try {
			IrpfCalculator.calculateIrpf2022(new DefaultIrpfCalculatorContext() {
				@Override
				public boolean getPagoPrestamosVivienda() {
					return true;
				}
			});
		} catch ( Exception e ) {
			if ( AonStringUtils.equalsIgnoreCase(e.getCause().getMessage(), "Error al calcular el IRPF") ) { 
				org.junit.Assert.fail();
			}
			System.out.println(e.getCause().getMessage());
		}
		try {
			IrpfCalculator.calculateIrpf2022(new DefaultIrpfCalculatorContext() {
				@Override
				public SituacionFamiliar getSituacionFamiliar() {
					return SituacionFamiliar.DOS;
				}
				@Override
				public String getComunidadAutonoma() {
					return null;
				}
				@Override
				public String getNifConyuge() {
					return "666666666";
				}
				
				@Override
				public boolean getPagoPrestamosVivienda() {
					return true;
				}
			});
		} catch ( Exception e ) {
			if ( AonStringUtils.equalsIgnoreCase(e.getCause().getMessage(), "Error al calcular el IRPF") ) { 
				org.junit.Assert.fail();
			}
			System.out.println(e.getCause().getMessage());
		}

		try {
			IrpfCalculator.calculateIrpf2022(new DefaultIrpfCalculatorContext() {
				@Override
				public CausaRegularizacion getCausaRegularizacion() {
					return CausaRegularizacion.ONCE;
				}
			});
		} catch ( Exception e ) {
			if ( AonStringUtils.equalsIgnoreCase(e.getCause().getMessage(), "Error al calcular el IRPF") ) { 
				org.junit.Assert.fail();
			}
			System.out.println(e.getCause().getMessage());
		}

	}

	@Test
	public void testDisabilityLevel() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		
		ContractRecord contract = newContract(aonContext, SSRegimeType.GENERAL,
				CCCType.PRINCIPAL, 
				getFirstDayOfYear(getToday()),
				null,
				new HashMap<String, String>() {
					{
						put(TC2.getName(), C401.getValue());
						put(ContextVariable.QUOTE_GROUP.getName(), "'07'");
					}
				}, new String[] { 
						"2000.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" 
				},
						new String[] {
						"BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_ESTR * 0.10", "BASE_NESTR * 0.20",
						"BASE_IRPF * PORCENTAJE_IRPF / 100.00" 
				}
				, null
				,null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(add(startDate, Calendar.MONTH, 1));
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		ctx.setListener(new IListener() {
			
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				org.junit.Assert.assertNull(irpfOutcome.getIrpfData().getDisabilityLevel()) ;
			}
		});
		double nullIrpf = ctx.getIrpf();

		aonContext.getDslContext()
		.insertInto(IRPF_DATA)
		.set(IRPF_DATA.DOMAIN, contract.getDomain())
		.set(IRPF_DATA.CONTRACT, contract.getId())
		.set(IRPF_DATA.START_DATE, endDate)
		.set(IRPF_DATA.START_DATE, startDate)
		.set(IRPF_DATA.ISSUE_DATE, startDate)
		.set(IRPF_DATA.FAMILY_SITUATION, (byte) FamilySituation.OTHER.ordinal())
		.set(IRPF_DATA.DISABILITY_LEVEL, (byte) DisabilityLevel.GT_EQ_33_LT_65.ordinal())
		.execute()
		;

		
		ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		ctx.setListener(new IListener() {
			
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				System.out.println("DISCAPACIDAD = " + irpfOutcome.getIrpfData().getDisabilityLevel().getName(new Locale("es")) );
				org.junit.Assert.assertEquals(DisabilityLevel.GT_EQ_33_LT_65, irpfOutcome.getIrpfData().getDisabilityLevel()) ;
			}
		});
		
		double gtEq33Lt65Irpf = ctx.getIrpf();
		
		System.out.println( gtEq33Lt65Irpf + " < " + nullIrpf);
		org.junit.Assert.assertTrue(gtEq33Lt65Irpf < nullIrpf);

		startDate = add(startDate, Calendar.DAY_OF_MONTH, 1);
		endDate = getLastDayOfMonth(add(startDate, Calendar.MONTH, 1));

		aonContext.getDslContext()
		.insertInto(IRPF_DATA)
		.set(IRPF_DATA.DOMAIN, contract.getDomain())
		.set(IRPF_DATA.CONTRACT, contract.getId())
		.set(IRPF_DATA.START_DATE, endDate)
		.set(IRPF_DATA.START_DATE, startDate)
		.set(IRPF_DATA.ISSUE_DATE, startDate)
//		.set(IRPF_DATA.DEPENDENCE, (byte) 1)
		.set(IRPF_DATA.FAMILY_SITUATION, (byte) FamilySituation.OTHER.ordinal())
		.set(IRPF_DATA.DISABILITY_LEVEL, (byte) DisabilityLevel.GT_EQ_33_LT_65_DEPENDENCE.ordinal())
		.execute()
		;

		
		ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		ctx.setListener(new IListener() {
			
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				System.out.println("DISCAPACIDAD = " + irpfOutcome.getIrpfData().getDisabilityLevel().getName(new Locale("es")) );
				org.junit.Assert.assertEquals(DisabilityLevel.GT_EQ_33_LT_65_DEPENDENCE, irpfOutcome.getIrpfData().getDisabilityLevel()) ;
			}
		});
		
		gtEq33Lt65Irpf = ctx.getIrpf();
		
		System.out.println( gtEq33Lt65Irpf + " < " + nullIrpf);
		org.junit.Assert.assertTrue(gtEq33Lt65Irpf < nullIrpf);

		startDate = add(startDate, Calendar.DAY_OF_MONTH, 1);
		endDate = getLastDayOfMonth(add(startDate, Calendar.MONTH, 1));
		
		aonContext.getDslContext()
		.insertInto(IRPF_DATA)
		.set(IRPF_DATA.DOMAIN, contract.getDomain())
		.set(IRPF_DATA.CONTRACT, contract.getId())
		.set(IRPF_DATA.START_DATE, endDate)
		.set(IRPF_DATA.START_DATE, startDate)
		.set(IRPF_DATA.ISSUE_DATE, startDate)
		.set(IRPF_DATA.FAMILY_SITUATION, (byte) FamilySituation.OTHER.ordinal())
		.set(IRPF_DATA.DISABILITY_LEVEL, (byte) DisabilityLevel.GT_EQ_65.ordinal())
		.execute()
		;

		
		ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		ctx.setListener(new IListener() {
			
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				System.out.println("DISCAPACIDAD = " + irpfOutcome.getIrpfData().getDisabilityLevel().getName(new Locale("es")) );
				org.junit.Assert.assertEquals(DisabilityLevel.GT_EQ_65, irpfOutcome.getIrpfData().getDisabilityLevel()) ;
			}
		});
		
		double gtEq65Irpf = ctx.getIrpf();
		
		System.out.println( gtEq65Irpf + " < " + nullIrpf);
		org.junit.Assert.assertTrue(gtEq65Irpf < nullIrpf);

		startDate = add(startDate, Calendar.DAY_OF_MONTH, 1);
		endDate = getLastDayOfMonth(add(startDate, Calendar.MONTH, 1));
		
		aonContext.getDslContext()
		.insertInto(IRPF_DATA)
		.set(IRPF_DATA.DOMAIN, contract.getDomain())
		.set(IRPF_DATA.CONTRACT, contract.getId())
		.set(IRPF_DATA.START_DATE, endDate)
		.set(IRPF_DATA.START_DATE, startDate)
		.set(IRPF_DATA.ISSUE_DATE, startDate)
		.set(IRPF_DATA.FAMILY_SITUATION, (byte) FamilySituation.OTHER.ordinal())
		.set(IRPF_DATA.DISABILITY_LEVEL, DSL.castNull(IRPF_DATA.DISABILITY_LEVEL))
		.execute()
		;

		ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		ctx.setListener(new IListener() {
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				org.junit.Assert.assertNull(irpfOutcome.getIrpfData().getDisabilityLevel()) ;
			}
		});
		
		double irpf = ctx.getIrpf();
		
		System.out.println( irpf + " == " + nullIrpf);
		org.junit.Assert.assertTrue(irpf == nullIrpf);
	}

	@Test
	public void testDecendentsDisabilityLevel() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		
		ContractRecord contract = newContract(aonContext, SSRegimeType.GENERAL,
				CCCType.PRINCIPAL, 
				getFirstDayOfYear(getToday()),
				null,
				new HashMap<String, String>() {
					{
						put(TC2.getName(), C401.getValue());
						put(ContextVariable.QUOTE_GROUP.getName(), "'07'");
					}
				}, new String[] { 
						"2000.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" 
				},
						new String[] {
						"BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_ESTR * 0.10", "BASE_NESTR * 0.20",
						"BASE_IRPF * PORCENTAJE_IRPF / 100.00" 
				}
				, null
				,null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(add(startDate, Calendar.MONTH, 1));
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		ctx.setListener(new IListener() {
			
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				org.junit.Assert.assertNull(irpfOutcome.getIrpfData().getDisabilityLevel()) ;
			}
		});
		double nullIrpf = ctx.getIrpf();

		IrpfDataRecord irpfData = 
		aonContext.getDslContext()
		.insertInto(IRPF_DATA)
		.set(IRPF_DATA.DOMAIN, contract.getDomain())
		.set(IRPF_DATA.CONTRACT, contract.getId())
		.set(IRPF_DATA.END_DATE, endDate)
		.set(IRPF_DATA.START_DATE, startDate)
		.set(IRPF_DATA.ISSUE_DATE, startDate)
		.set(IRPF_DATA.FAMILY_SITUATION, (byte) FamilySituation.OTHER.ordinal())
		.set(IRPF_DATA.DISABILITY_LEVEL, DSL.castNull(IRPF_DATA.DISABILITY_LEVEL))
		.returning()
		.fetchOne();
		
		aonContext.getDslContext()
		.insertInto(IRPF_DATA_DESCENDIENTS)
		.set(IRPF_DATA_DESCENDIENTS.DOMAIN, irpfData.getDomain())
		.set(IRPF_DATA_DESCENDIENTS.IRPF_DATA, irpfData.getId())
		.set(IRPF_DATA_DESCENDIENTS.BIRTH_YEAR, get(startDate, Calendar.YEAR) - 5)
		.set(IRPF_DATA_DESCENDIENTS.DEPENDENCE, (byte) 1 )
		.set(IRPF_DATA_DESCENDIENTS.UNIQUE_PARENT, (byte) 1)
		.set(IRPF_DATA_DESCENDIENTS.DISABILITY_LEVEL, (byte) DisabilityLevel.GT_EQ_65.ordinal())
		.execute();
		
		

		ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		ctx.setListener(new IListener() {
			
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				System.out.println("DescendentsFirst : " + irpfOutcome.getIrpfResult().getDescendentsFirst() );
				System.out.println("Descendents65 : " + irpfOutcome.getIrpfResult().getDescendents65Entirely() );
				System.out.println("Descendents65Total : " + irpfOutcome.getIrpfResult().getDescendents65Total() );
				org.junit.Assert.assertEquals((long)1, (long)irpfOutcome.getIrpfResult().getDescendentsFirst()) ;
				org.junit.Assert.assertEquals((long)1, (long)irpfOutcome.getIrpfResult().getDescendents65Entirely()) ;
				org.junit.Assert.assertEquals((long)1, (long)irpfOutcome.getIrpfResult().getDescendents65Total()) ;

			}
		});
		
		double gt65Irpf = ctx.getIrpf();
		
		System.out.println( gt65Irpf + " < " + nullIrpf);
		org.junit.Assert.assertTrue(gt65Irpf < nullIrpf);

		startDate = add(startDate, Calendar.DAY_OF_MONTH, 1);
		endDate = getLastDayOfMonth(add(startDate, Calendar.MONTH, 1));

		irpfData = 
		aonContext.getDslContext()
		.insertInto(IRPF_DATA)
		.set(IRPF_DATA.DOMAIN, contract.getDomain())
		.set(IRPF_DATA.CONTRACT, contract.getId())
		.set(IRPF_DATA.END_DATE, endDate)
		.set(IRPF_DATA.START_DATE, startDate)
		.set(IRPF_DATA.ISSUE_DATE, startDate)
		.set(IRPF_DATA.FAMILY_SITUATION, (byte) FamilySituation.OTHER.ordinal())
		.set(IRPF_DATA.DISABILITY_LEVEL, DSL.castNull(IRPF_DATA.DISABILITY_LEVEL))
		.returning()
		.fetchOne();
		
		aonContext.getDslContext()
		.insertInto(IRPF_DATA_DESCENDIENTS)
		.set(IRPF_DATA_DESCENDIENTS.DOMAIN, irpfData.getDomain())
		.set(IRPF_DATA_DESCENDIENTS.IRPF_DATA, irpfData.getId())
		.set(IRPF_DATA_DESCENDIENTS.BIRTH_YEAR, get(startDate, Calendar.YEAR) - 5)
		.set(IRPF_DATA_DESCENDIENTS.DEPENDENCE, (byte) 1 )
		.set(IRPF_DATA_DESCENDIENTS.UNIQUE_PARENT, (byte) 1)
		.set(IRPF_DATA_DESCENDIENTS.DISABILITY_LEVEL, (byte) DisabilityLevel.GT_EQ_33_LT_65_DEPENDENCE.ordinal())
		.execute();

		ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		ctx.setListener(new IListener() {
			
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				System.out.println("DescendentsFirst : " + irpfOutcome.getIrpfResult().getDescendentsFirst() );
				System.out.println("Descendents3365 : " + irpfOutcome.getIrpfResult().getDescendents33_65Entirely() );
				System.out.println("Descendents3365Total : " + irpfOutcome.getIrpfResult().getDescendents33_65Total() );
				org.junit.Assert.assertEquals((long)1, (long)irpfOutcome.getIrpfResult().getDescendentsFirst()) ;
				org.junit.Assert.assertEquals((long)1, (long)irpfOutcome.getIrpfResult().getDescendents33_65Entirely()) ;
				org.junit.Assert.assertEquals((long)1, (long)irpfOutcome.getIrpfResult().getDescendents33_65Total()) ;

			}
		});
		

		double gt33lt65Irpf = ctx.getIrpf();
		System.out.println( gt65Irpf + " < " + gt33lt65Irpf);
		org.junit.Assert.assertTrue(gt65Irpf < gt33lt65Irpf);

		startDate = add(startDate, Calendar.DAY_OF_MONTH, 1);
		endDate = getLastDayOfMonth(add(startDate, Calendar.MONTH, 1));

	
	}

	@Test
	public void testDeductHomeLoan() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		
		ContractRecord contract = newContract(aonContext, SSRegimeType.GENERAL,
				CCCType.PRINCIPAL, 
				getFirstDayOfYear(getToday()),
				null,
				new HashMap<String, String>() {
					{
						put(TC2.getName(), C401.getValue());
						put(ContextVariable.QUOTE_GROUP.getName(), "'07'");
					}
				}, new String[] { 
						"2000.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" 
				},
						new String[] {
						"BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_ESTR * 0.10", "BASE_NESTR * 0.20",
						"BASE_IRPF * PORCENTAJE_IRPF / 100.00" 
				}
				, null
				,null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(add(startDate, Calendar.MONTH, 1));
		
		IrpfData irpfDatas [] = new IrpfData [3];
		IrpfResult irpfResults [] = new IrpfResult [3];
		IrpfRegularization irpfRegularizations [] = new IrpfRegularization [3];
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		ctx.setListener(new IListener() {
			
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				irpfDatas[0] = irpfOutcome.getIrpfData();
				irpfResults[0] = irpfOutcome.getIrpfResult();
				irpfRegularizations[0] = irpfOutcome.getIrpfRegularization();
				org.junit.Assert.assertNull(irpfOutcome.getIrpfData().getDeductHomeLoan()) ;
			}
		});
		double nullIrpf = ctx.getIrpf();

		aonContext.getDslContext()
		.insertInto(IRPF_DATA)
		.set(IRPF_DATA.DOMAIN, contract.getDomain())
		.set(IRPF_DATA.CONTRACT, contract.getId())
		.set(IRPF_DATA.END_DATE, endDate)
		.set(IRPF_DATA.START_DATE, startDate)
		.set(IRPF_DATA.ISSUE_DATE, startDate)
		.set(IRPF_DATA.FAMILY_SITUATION, DSL.castNull(Byte.class))
		.set(IRPF_DATA.DISABILITY_LEVEL, DSL.castNull(Byte.class))
		.set(IRPF_DATA.DEDUCT_HOME_LOAN, (byte) DeductHomeLoan.AFTER_01_01_2001.ordinal())
		.execute()
		;

		
		ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		ctx.setListener(new IListener() {
			
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				irpfDatas[1] = irpfOutcome.getIrpfData();
				irpfResults[1] = irpfOutcome.getIrpfResult();
				irpfRegularizations[1] = irpfOutcome.getIrpfRegularization();
				
				System.out.println("VIVIENDA = " + irpfOutcome.getIrpfData().getDeductHomeLoan().getName(new Locale("es")) );
				org.junit.Assert.assertEquals(DeductHomeLoan.AFTER_01_01_2001, irpfOutcome.getIrpfData().getDeductHomeLoan()) ;
				
			}
		});
		
		
		double deductHomeLoanIrpf = ctx.getIrpf();
		
		System.out.println( deductHomeLoanIrpf + " < " + nullIrpf);
		org.junit.Assert.assertTrue(deductHomeLoanIrpf < nullIrpf);

	}

	@Test
	public void testDefaultIrpfData() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		
		ContractRecord contract = newContract(aonContext, SSRegimeType.GENERAL,
				CCCType.PRINCIPAL, 
				getFirstDayOfYear(getToday()),
				null,
				new HashMap<String, String>() {
					{
						put(TC2.getName(), C401.getValue());
						put(ContextVariable.QUOTE_GROUP.getName(), "'07'");
					}
				}, new String[] { 
						"2000.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" 
				},
						new String[] {
						"BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_ESTR * 0.10", "BASE_NESTR * 0.20",
						"BASE_IRPF * PORCENTAJE_IRPF / 100.00" 
				}
				, null
				,null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(add(startDate, Calendar.MONTH, 1));
		
		IrpfData irpfDatas [] = new IrpfData [3];
		IrpfResult irpfResults [] = new IrpfResult [3];
		IrpfRegularization irpfRegularizations [] = new IrpfRegularization [3];
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		ctx.setListener(new IListener() {
			
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				irpfDatas[0] = irpfOutcome.getIrpfData();
				irpfResults[0] = irpfOutcome.getIrpfResult();
				irpfRegularizations[0] = irpfOutcome.getIrpfRegularization();
				org.junit.Assert.assertNull(irpfOutcome.getIrpfData().getDeductHomeLoan()) ;
			}
		});
		double nullIrpf = ctx.getIrpf();

		aonContext.getDslContext()
		.insertInto(IRPF_DATA)
		.set(IRPF_DATA.DOMAIN, contract.getDomain())
		.set(IRPF_DATA.CONTRACT, contract.getId())
		.set(IRPF_DATA.END_DATE, endDate)
		.set(IRPF_DATA.START_DATE, startDate)
		.set(IRPF_DATA.ISSUE_DATE, startDate)
		.set(IRPF_DATA.FAMILY_SITUATION, DSL.castNull(Byte.class))
		.set(IRPF_DATA.DISABILITY_LEVEL, DSL.castNull(Byte.class))
		.execute()
		;

		
		ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		ctx.setListener(new IListener() {
			
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				irpfDatas[1] = irpfOutcome.getIrpfData();
				irpfResults[1] = irpfOutcome.getIrpfResult();
				irpfRegularizations[1] = irpfOutcome.getIrpfRegularization();

				assertIrpfDataEquals(irpfDatas[0], irpfDatas[1]);
				assertIrpfResultEquals(irpfResults[0], irpfResults[1]);
			}
		});
		
		
		double irpfDataIrpf = ctx.getIrpf();
		
		System.out.println( irpfDataIrpf + " == " + nullIrpf);
		org.junit.Assert.assertTrue(irpfDataIrpf == nullIrpf);

	}

	@Test
	public void testRegularizationI() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date firstDayOfYear = getFirstDayOfYear(getToday());
		Date contractStartDate = add(firstDayOfYear, Calendar.MONTH,6);
		
		ContractRecord contract = newContract(aonContext, 
				contractStartDate, 
				null, 
				Collections.emptyMap(), 
				new String [] {
						"2500.00",
						"250.00",
				}, 
				new String [] {
						"PORCENTAJE_IRPF / 100.00 * BASE_IRPF"
				}, 
				null);
		double annualRemunerations [] = {0.00};
		Date startDate = contractStartDate;
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ctx.setListener( new Listener() {
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				super.onIrpf(irpfOutcome);
				
				System.out.println("Irpf:" + irpfOutcome.getIrpfResult().getIrpf());
				System.out.println("BaseIrpf:" + irpfOutcome.getIrpfResult().getBaseIrpf());
				System.out.println("AnnualIrpf:" + irpfOutcome.getIrpfResult().getAnnualIrpf());
				System.out.println("AnnualRemuneration:" + irpfOutcome.getIrpfResult().getAnnualRemuneration());
				
				double annualRemuneration = irpfOutcome.getIrpfResult().getAnnualRemuneration();
				assertAnnualRemuneration(2750.00 * 12.00, annualRemuneration, DELTA);
				annualRemunerations[0] = annualRemuneration;
			}
		});
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
		for ( Date date = firstDayOfYear; date.before(contractStartDate); date = add(date,Calendar.MONTH,1))
			newIrpfSalary(aonContext, contract, date, getLastDayOfMonth(date), 1000.00, 2.00, M190);
		
		
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ctx.setListener( new Listener() {
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				super.onIrpf(irpfOutcome);
				
				System.out.println("Irpf:" + irpfOutcome.getIrpfResult().getIrpf());
				System.out.println("BaseIrpf:" + irpfOutcome.getIrpfResult().getBaseIrpf());
				System.out.println("AnnualIrpf:" + irpfOutcome.getIrpfResult().getAnnualIrpf());
				System.out.println("AnnualRemuneration:" + irpfOutcome.getIrpfResult().getAnnualRemuneration());
				
				double annualRemuneration = irpfOutcome.getIrpfResult().getAnnualRemuneration();
				org.junit.Assert.assertEquals(1000.00 * 6.00 + annualRemunerations[0]/2, annualRemuneration, DELTA);
			}
		});
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
	}

	@Test
	public void testRegularizationZero() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date firstDayOfYear = getFirstDayOfYear(getToday());
		Date contractStartDate = add(firstDayOfYear, Calendar.MONTH,6);
		
		ContractRecord contract = newContract(aonContext, 
				contractStartDate, 
				null, 
				Collections.emptyMap(), 
				new String [] {
						"2500.00",
						"250.00",
				}, 
				new String [] {
						"PORCENTAJE_IRPF / 100.00 * BASE_IRPF"
				}, 
				null);
		double [] annualRemunerations = { 0.00 };
		Date startDate = contractStartDate;
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ctx.setListener( new Listener() {
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				super.onIrpf(irpfOutcome);
				
				System.out.println("Irpf:" + irpfOutcome.getIrpfResult().getIrpf());
				System.out.println("BaseIrpf:" + irpfOutcome.getIrpfResult().getBaseIrpf());
				System.out.println("AnnualIrpf:" + irpfOutcome.getIrpfResult().getAnnualIrpf());
				System.out.println("AnnualRemuneration:" + irpfOutcome.getIrpfResult().getAnnualRemuneration());
				
				double annualRemuneration = irpfOutcome.getIrpfResult().getAnnualRemuneration();
				assertAnnualRemuneration(2750.00 * 12.00, annualRemuneration, DELTA);
				annualRemunerations[0] = annualRemuneration;
			}
		});
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
		for ( Date date = firstDayOfYear; date.before(contractStartDate); date = add(date,Calendar.MONTH,1))
			newIrpfSalary(aonContext, contract, date, getLastDayOfMonth(date), 0.00, 0.00, M190);
		
		
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ctx.setListener( new Listener() {
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				super.onIrpf(irpfOutcome);
				
				System.out.println("Irpf:" + irpfOutcome.getIrpfResult().getIrpf());
				System.out.println("BaseIrpf:" + irpfOutcome.getIrpfResult().getBaseIrpf());
				System.out.println("AnnualIrpf:" + irpfOutcome.getIrpfResult().getAnnualIrpf());
				System.out.println("AnnualRemuneration:" + irpfOutcome.getIrpfResult().getAnnualRemuneration());
				
				double annualRemuneration = irpfOutcome.getIrpfResult().getAnnualRemuneration();
				org.junit.Assert.assertEquals(annualRemunerations[0] / 2, annualRemuneration, DELTA);
			}
		});
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
	}

	@Test
	public void testRegularizationII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date firstDayOfYear = getFirstDayOfYear(getToday());
		Date contractStartDate = add(firstDayOfYear, Calendar.MONTH,6);
		
		ContractRecord contract = newContract(aonContext, 
				contractStartDate, 
				null, 
				Collections.emptyMap(), 
				new String [] {
						"2500.00",
						"250.00",
				}, 
				new String [] {
						"PORCENTAJE_IRPF / 100.00 * BASE_IRPF"
				}, 
				null);
		double annualRemunerations [] = {0.00};
		Date startDate = contractStartDate;
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ctx.setListener( new Listener() {
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				super.onIrpf(irpfOutcome);
				
				System.out.println("Irpf:" + irpfOutcome.getIrpfResult().getIrpf());
				System.out.println("BaseIrpf:" + irpfOutcome.getIrpfResult().getBaseIrpf());
				System.out.println("AnnualIrpf:" + irpfOutcome.getIrpfResult().getAnnualIrpf());
				System.out.println("AnnualRemuneration:" + irpfOutcome.getIrpfResult().getAnnualRemuneration());
				
				double annualRemuneration = irpfOutcome.getIrpfResult().getAnnualRemuneration();
				assertAnnualRemuneration(2750.00 * 12.00, annualRemuneration, DELTA);
				annualRemunerations[0] = annualRemuneration;
			}
		});
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
		for ( Date date = firstDayOfYear; get(date, Calendar.MONTH) <= Calendar.MARCH; date = add(date,Calendar.MONTH,1))
			newIrpfSalary(aonContext, contract, date, getLastDayOfMonth(date), 1000.00, 2.00, M190);
		
		
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ctx.setListener( new Listener() {
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				super.onIrpf(irpfOutcome);
				
				System.out.println("Irpf:" + irpfOutcome.getIrpfResult().getIrpf());
				System.out.println("BaseIrpf:" + irpfOutcome.getIrpfResult().getBaseIrpf());
				System.out.println("AnnualIrpf:" + irpfOutcome.getIrpfResult().getAnnualIrpf());
				System.out.println("AnnualRemuneration:" + irpfOutcome.getIrpfResult().getAnnualRemuneration());
				
				double annualRemuneration = irpfOutcome.getIrpfResult().getAnnualRemuneration();
				org.junit.Assert.assertEquals(1000.00 * 3.00  + annualRemunerations[0] * 9/12  , annualRemuneration, DELTA);
			}
		});
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
	}

	@Test
	public void testRegularizationIII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date firstDayOfYear = getFirstDayOfYear(getToday());
		Date contractStartDate = add(firstDayOfYear, Calendar.MONTH,6);
		
		ContractRecord contract = newContract(aonContext, 
				contractStartDate, 
				null, 
				new HashMap<String,String>() {
		    			{
		    			    put("PORCENTAJE_IRPF", "SISTEMA('PORCENTAJE_IRPF')");
                			}
				}, 
				new String [] {
						"2500.00",
						"250.00",
				}, 
				new String [] {
						"PORCENTAJE_IRPF / 100.00 * BASE_IRPF"
				}, 
				null);
		double annualRemunerations [] = {0.00};
		Date startDate = contractStartDate;
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate,
			endDate, endDate, contract, new Listener() {
			    @Override
			    public void onIrpf(IrpfOutcome irpfOutcome) {
				super.onIrpf(irpfOutcome);

				System.out.println("Irpf:" + irpfOutcome.getIrpfResult().getIrpf());
				System.out.println("BaseIrpf:" + irpfOutcome.getIrpfResult().getBaseIrpf());
				System.out.println("AnnualIrpf:" + irpfOutcome.getIrpfResult().getAnnualIrpf());
				System.out.println(
					"AnnualRemuneration:" + irpfOutcome.getIrpfResult().getAnnualRemuneration());

				double annualRemuneration = irpfOutcome.getIrpfResult().getAnnualRemuneration();
				assertAnnualRemuneration(2750.00 * 12.00, annualRemuneration, DELTA);
				annualRemunerations[0] = annualRemuneration;
			    }
			});
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		for ( Date date = firstDayOfYear; date.before(contractStartDate); date = add(date,Calendar.MONTH,1)) {
			newIrpfSalary(aonContext, contract, date, getLastDayOfMonth(date), 1000.00, 2.00, com.esferalia.aon.occam.api.model.type.SalaryType.SALARY);
		}
		
		newIrpfSalary(aonContext, contract, add(firstDayOfYear, Calendar.MONTH, -6), add(firstDayOfYear, Calendar.MONTH, 5), 1000.00, 2.00, com.esferalia.aon.occam.api.model.type.SalaryType.DELAY);

		ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract,
			new Listener() {
			    @Override
			    public void onIrpf(IrpfOutcome irpfOutcome) {
				super.onIrpf(irpfOutcome);
				
				
				System.out.println("Irpf:" + irpfOutcome.getIrpfResult().getIrpf());
				System.out.println("BaseIrpf:" + irpfOutcome.getIrpfResult().getBaseIrpf());
				System.out.println("AnnualIrpf:" + irpfOutcome.getIrpfResult().getAnnualIrpf());
				System.out.println(
					"AnnualRemuneration:" + irpfOutcome.getIrpfResult().getAnnualRemuneration());
				
				org.junit.Assert.assertNotNull(irpfOutcome.getIrpfRegularization());
				
				System.out.println(
					"PaidRemuneration:" + irpfOutcome.getIrpfRegularization().getPaidRemuneration());
				System.out.println(
					"PaidIrpf:" + irpfOutcome.getIrpfRegularization().getPaidIrpf());

				
				org.junit.Assert.assertEquals( 1000.00 * 7, irpfOutcome.getIrpfRegularization().getPaidRemuneration(), 0.00 );
				org.junit.Assert.assertEquals( 2.00 * 7, irpfOutcome.getIrpfRegularization().getPaidIrpf(), 0.00 );

				double annualRemuneration = irpfOutcome.getIrpfResult().getAnnualRemuneration();
				org.junit.Assert.assertEquals(1000.00 * 7 + annualRemunerations[0] / 2,
					annualRemuneration, DELTA);
			    }
			});
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
	}

	@Test
	public void testIrpfChargeDate() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		
		ContractRecord contract = newContract(aonContext, SSRegimeType.GENERAL,
				CCCType.PRINCIPAL, 
				getFirstDayOfYear(getToday()),
				null,
				new HashMap<String, String>() {
					{
						put(TC2.getName(), C401.getValue());
						put(ContextVariable.QUOTE_GROUP.getName(), "'07'");
					}
				}, new String[] { 
						"2000.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" 
				},
						new String[] {
						"BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_ESTR * 0.10", "BASE_NESTR * 0.20",
						"BASE_IRPF * PORCENTAJE_IRPF / 100.00" 
				}
				, null
				,null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(add(startDate, Calendar.MONTH, 1));
		
		Date chargeDate = add(endDate, Calendar.DAY_OF_MONTH, 10); 
		
        	Criteria criteria = new Criteria();
        	criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		ISQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(connection, startDate,
			endDate, chargeDate, chargeDate, criteria) {
		    @Override
		    public double getIrpf() {
			throw new ExpressionExceptionWrapper(new CheckException("Uppps!!!! "));			
		    }
		};
		ctx.next();

		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);

		//org.junit.Assert.assertEquals( chargeDate, ctx.getIrpfDate() );

	}

	// ------------------------------------------------------------------------

	protected void assertAnnualRemuneration(double expected,
			double annualRemuneration) {
		assertEquals(expected, annualRemuneration);
	}

	protected void assertAnnualRemuneration(double expected,
			double annualRemuneration, int months) {
		assertEquals(expected, annualRemuneration);
	}

	protected void assertAnnualRemuneration(double expected,
			double annualRemuneration, double delta) {
		assertEquals(expected, annualRemuneration, delta);
	}

	protected void assertAnnualRemuneration(double expected,
			double annualRemuneration, int months, double delta) {
		assertEquals(expected, annualRemuneration, delta);
	}

	protected void assertDeduccibleExpenses(double expected,
			double deduccibleExpenses) {
		assertEquals(expected, deduccibleExpenses);
	}
	protected void assertIrpf(double expected,
			double base, double percent, double delta) {
		assertEquals(expected, base * percent / 100.00, delta);
	}

	protected ISQLContractSalaryCalculatorContext getContractSettleCalculatorContext(
			Connection connection, Date startDate, Date endDate,
			Date issueDate, Criteria criteria) throws ExpressionException,
			SQLException {
		SQLContractSettleCalculatorContext ctx = new SQLContractSettleCalculatorContext(
				connection, startDate, endDate, issueDate, criteria);
		ctx.next();
		return ctx;
	}

	// ------------------------------------------------------------------------

	private void test(Consumer<IrpfResult> c, String[] payments,
			String[] deductions) throws ExpressionException, SQLException {
		test(c, payments, deductions, new Extra[] {});
	}

	private void test(Consumer<IrpfResult> c, String[] payments,
			String[] deductions, String [] extras ) throws ExpressionException, SQLException {
		test(c, getFirstDayOfYear(getToday()), null, payments, deductions, extras);
	}

	private void test(Consumer<IrpfResult> c, String[] payments,
			String[] deductions, Payment [] extras ) throws ExpressionException, SQLException {
		test(c, getFirstDayOfYear(getToday()), null, payments, deductions, extras, SSRegimeType.GENERAL);
	}

	private void test(Consumer<IrpfResult> c, String[] payments,
			String[] deductions, Payment [] extras, SSRegimeType ssRegimeType ) throws ExpressionException, SQLException {
		test(c, getFirstDayOfYear(getToday()), null, payments, deductions, extras, ssRegimeType);
	}

	private void test(Consumer<IrpfResult> c, 
			String[] payments,
			String[] deductions, 
			Extra extras[]) throws ExpressionException,
			SQLException {
		test(c, 
				getFirstDayOfYear(getToday()),
				null,
				payments, 
				deductions, 
				extras);
	}
	
	private void test(Consumer<IrpfResult> c, 
			Date contractStart,
			Date contractEnd,
			String[] payments,
			String[] deductions, 
			Extra extras[]) throws ExpressionException,
			SQLException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		AgreementLevelCategoryRecord category = null;
		if (extras != null && extras.length > 0)
			category = newAgreement(aonContext, extras);

		ContractRecord contract = newContract(aonContext, 
				contractStart, 
				contractEnd, 
				Collections.emptyMap(), 
				payments, 
				deductions,
				category);

		Calendar calendar = Calendar.getInstance();
		// Be care that the first day of the month has value 1.
		calendar.set(DAY_OF_MONTH, 1);
		Date start = new Date(calendar.getTimeInMillis());

		calendar.set(DAY_OF_MONTH, calendar.getActualMaximum(DAY_OF_MONTH));
		Date end = new Date(calendar.getTimeInMillis());

		Date issue = new Date(calendar.getTimeInMillis());

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, start, end, issue, contract);

		ctx.setListener(new Listener() {

			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				c.accept(irpfOutcome.getIrpfResult());
			}
		});

		ctx.getIrpf();
	}

	private void test(Consumer<IrpfResult> c, 
			Date contractStart,
			Date contractEnd,
			String[] payments,
			String[] deductions, 
			String[] extras) throws ExpressionException,
			SQLException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);


		ContractRecord contract = newContract(aonContext, 
				contractStart, 
				contractEnd, 
				Collections.emptyMap(), 
				payments, 
				deductions,
				null);
		
		PaymentConceptRecord concept = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		for ( String extra : extras ) {
			addPayment(aonContext, contract, contractStart, contractEnd, concept, null, extra, "_P", "_P", PaymentType.CRA_0004);
		}

		Calendar calendar = Calendar.getInstance();
		// Be care that the first day of the month has value 1.
		calendar.set(DAY_OF_MONTH, 1);
		Date start = new Date(calendar.getTimeInMillis());

		calendar.set(DAY_OF_MONTH, calendar.getActualMaximum(DAY_OF_MONTH));
		Date end = new Date(calendar.getTimeInMillis());

		Date issue = new Date(calendar.getTimeInMillis());

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, start, end, issue, contract);

		ctx.setListener(new Listener() {

			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				c.accept(irpfOutcome.getIrpfResult());
			}
		});

		ctx.getIrpf();
	}
	
	private void test(Consumer<IrpfResult> c, 
			Date contractStart,
			Date contractEnd,
			String[] payments,
			String[] deductions, 
			Payment[] extras,
			SSRegimeType ssRegime) throws ExpressionException,
			SQLException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);


		ContractRecord contract = newContract(aonContext, 
				ssRegime, 
				CCCType.PRINCIPAL,
				contractStart, 
				contractEnd, 
				Collections.emptyMap(), 
				payments, 
				deductions,
				null);
		
		PaymentConceptRecord concept = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		for ( Payment extra : extras ) {
			addPayment(aonContext, contract, contractStart, contractEnd, concept, "PAGA EXTRAORDINARIA", extra.expression, "_P", "_P", PaymentType.CRA_0004, (byte)extra.month.ordinal());
		}

		Calendar calendar = Calendar.getInstance();
		// Be care that the first day of the month has value 1.
		calendar.set(DAY_OF_MONTH, 1);
		Date start = new Date(calendar.getTimeInMillis());

		calendar.set(DAY_OF_MONTH, calendar.getActualMaximum(DAY_OF_MONTH));
		Date end = new Date(calendar.getTimeInMillis());

		Date issue = new Date(calendar.getTimeInMillis());

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, start, end, issue, contract);

		ctx.setListener(new Listener() {

			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				c.accept(irpfOutcome.getIrpfResult());
			}
		});

		ctx.getIrpf();
	}

	private static int calculateAndSave(Connection connection,
			ISQLContractSalaryCalculatorContext ctx) throws SalaryException {
		JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder(connection);
		new SmartContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
		return jooqSalaryBuilder.execute();
	}
	
	private static void newIrpfSalary(AONContext aonContext, ContractRecord contract, Date startDate, Date endDate, Double irpfBase, Double totalIrpf , com.esferalia.aon.occam.api.model.type.SalaryType salaryType) {

		Record person = 
		aonContext.getDslContext()
		.select()
		.from(PERSON)
		.innerJoin(REGISTRY).onKey()
		.where(PERSON.REGISTRY.eq(contract.getPerson()))
		.fetchOne();
		
		Record enterprise = 
		aonContext.getDslContext()
		.select()
		.from(EnterpriseCcc.ENTERPRISE_CCC)
		.innerJoin(EnterpriseActivity.ENTERPRISE_ACTIVITY).on(EnterpriseCcc.ENTERPRISE_CCC.ENTERPRISE_ACTIVITY.eq(EnterpriseActivity.ENTERPRISE_ACTIVITY.ID))
		.innerJoin(REGISTRY).on(EnterpriseActivity.ENTERPRISE_ACTIVITY.ENTERPRISE.eq(REGISTRY.ID))
		.where(EnterpriseCcc.ENTERPRISE_CCC.ID.eq(contract.getEnterpriseCcc()))
		.fetchOne()
		;

		aonContext.getDslContext()
		.insertInto(SALARY)
		.set(SALARY.CONTRACT, contract.getId())
		.set(SALARY.DOMAIN, contract.getDomain())
		.set(SALARY.EMPLOYEE_NAME, person.get(REGISTRY.NAME))
		.set(SALARY.EMPLOYEE_DOCUMENT, person.get(REGISTRY.DOCUMENT))
		.set(SALARY.SOCIAL_SECURITY_NUMBER, person.get(PERSON.SOCIAL_SECURITY_NUM))
		.set(SALARY.CCC, enterprise.get(EnterpriseCcc.ENTERPRISE_CCC.CCC))
		.set(SALARY.ENTERPRISE_NAME, enterprise.get(REGISTRY.NAME))
		.set(SALARY.ENTERPRISE_DOCUMENT, enterprise.get(REGISTRY.DOCUMENT))

		.set(SALARY.END_DATE, endDate)
		.set(SALARY.ISSUE_DATE, endDate)
		.set(SALARY.CHARGE_DATE, endDate)
		.set(SALARY.START_DATE, startDate)
		
		.set(SALARY.TYPE, (byte) salaryType.ordinal())
		
		.set(SALARY.IRPF_BASE, irpfBase)
		.set(SALARY.MONEY_IRPF_BASE, irpfBase)
		.set(SALARY.TOTAL_IRPF, totalIrpf)

		.set(SALARY.TIME_UNITS, 30)
		.set(SALARY.REGISTRATION, Integer.MAX_VALUE)

		.execute();
		
	}
	
	private static void assertIrpfDataEquals(IrpfData irpfData1, IrpfData irpfData2) {
		org.junit.Assert.assertEquals(irpfData1.getAnnualRemuneration(), irpfData2.getAnnualRemuneration());
		org.junit.Assert.assertEquals(irpfData1.getDeducciblesExpenses(), irpfData2.getDeducciblesExpenses());
		org.junit.Assert.assertEquals(irpfData1.getDeductHomeLoan(), irpfData2.getDeductHomeLoan());
		org.junit.Assert.assertEquals(irpfData1.getDescendientCount(), irpfData2.getDescendientCount());
		org.junit.Assert.assertEquals(irpfData1.getFamilySituation(), irpfData2.getFamilySituation());
		org.junit.Assert.assertEquals(irpfData1.getFoodAnnuity(), irpfData2.getFoodAnnuity());
		org.junit.Assert.assertEquals(irpfData1.getIrregular18_2Reduction(), irpfData2.getIrregular18_2Reduction());
		org.junit.Assert.assertEquals(irpfData1.getIrregular18_3Reduction(), irpfData2.getIrregular18_3Reduction());
		org.junit.Assert.assertEquals(irpfData1.getRequestIrpf(), irpfData2.getRequestIrpf());
		org.junit.Assert.assertEquals(irpfData1.getSpousalSupport(), irpfData2.getSpousalSupport());
		org.junit.Assert.assertEquals(irpfData1.getSpouseDocument(), irpfData2.getSpouseDocument());
		org.junit.Assert.assertEquals(irpfData1.getDisabilityLevel(), irpfData2.getDisabilityLevel());
	}

	private static void assertIrpfResultEquals(IrpfResult irpfResult1, IrpfResult irpfResult2) {
		org.junit.Assert.assertEquals(irpfResult1.getDeducciblesExpenses(), irpfResult2.getDeducciblesExpenses());
		org.junit.Assert.assertEquals(irpfResult1.getDeduct80Bis(), irpfResult2.getDeduct80Bis());
		org.junit.Assert.assertEquals(irpfResult1.getDeductHomeLoanAmount(), irpfResult2.getDeductHomeLoanAmount());
		org.junit.Assert.assertEquals(irpfResult1.getIrregular18_2Reduction(), irpfResult2.getIrregular18_2Reduction());
		org.junit.Assert.assertEquals(irpfResult1.getIrregular18_3Reduction(), irpfResult2.getIrregular18_3Reduction());

		org.junit.Assert.assertEquals(irpfResult1.getMinimunAscendents(), irpfResult2.getMinimunAscendents());
		org.junit.Assert.assertEquals(irpfResult1.getAscendents33_65Entirely(), irpfResult2.getAscendents33_65Entirely());
		org.junit.Assert.assertEquals(irpfResult1.getAscendents33_65Total(), irpfResult2.getAscendents33_65Total());
		org.junit.Assert.assertEquals(irpfResult1.getAscendentsMayor75Entirely(), irpfResult2.getAscendentsMayor75Entirely());
		org.junit.Assert.assertEquals(irpfResult1.getAscendentsMayor75Total(), irpfResult2.getAscendentsMayor75Total());
		org.junit.Assert.assertEquals(irpfResult1.getAscendentsMinor75Entirely(), irpfResult2.getAscendentsMinor75Entirely());
		org.junit.Assert.assertEquals(irpfResult1.getAscendentsMinor75Total(), irpfResult2.getAscendentsMinor75Total());
		org.junit.Assert.assertEquals(irpfResult1.getAscendentsMovingEntirely(), irpfResult2.getAscendentsMovingEntirely());
		org.junit.Assert.assertEquals(irpfResult1.getAscendentsMovingTotal(), irpfResult2.getAscendentsMovingTotal());

		org.junit.Assert.assertEquals(irpfResult1.getMinimunDescendents(), irpfResult2.getMinimunDescendents());
		org.junit.Assert.assertEquals(irpfResult1.getTwoOrMoreDescendentsMin(), irpfResult2.getTwoOrMoreDescendentsMin());
		org.junit.Assert.assertEquals(irpfResult1.getDescendentsFirst(), irpfResult2.getDescendentsFirst());
		org.junit.Assert.assertEquals(irpfResult1.getDescendentsSecond(), irpfResult2.getDescendentsSecond());
		org.junit.Assert.assertEquals(irpfResult1.getDescendentsThird(), irpfResult2.getDescendentsThird());
		org.junit.Assert.assertEquals(irpfResult1.getDescendents65Total(), irpfResult2.getDescendents65Total());
		org.junit.Assert.assertEquals(irpfResult1.getDescendents65Entirely(), irpfResult2.getDescendents65Entirely());
		org.junit.Assert.assertEquals(irpfResult1.getDescendents33_65Total(), irpfResult2.getDescendents33_65Total());
		org.junit.Assert.assertEquals(irpfResult1.getDescendents33_65Entirely(), irpfResult2.getDescendents33_65Entirely());
		org.junit.Assert.assertEquals(irpfResult1.getDescendentsMovingEntirely(), irpfResult2.getDescendentsMovingEntirely());
		org.junit.Assert.assertEquals(irpfResult1.getDescendentsMovingTotal(), irpfResult2.getDescendentsMovingTotal());
		org.junit.Assert.assertEquals(irpfResult1.getDescendentsRemainderEntirely(), irpfResult2.getDescendentsRemainderEntirely());
		org.junit.Assert.assertEquals(irpfResult1.getDescendentsRemainderTotal(), irpfResult2.getDescendentsRemainderTotal());
		org.junit.Assert.assertEquals(irpfResult1.getDescendentsFourthSubsequentTotal(), irpfResult2.getDescendentsFourthSubsequentTotal());
		org.junit.Assert.assertEquals(irpfResult1.getDescendentsFourthSubsequentEntirely(), irpfResult2.getDescendentsFourthSubsequentEntirely());

		org.junit.Assert.assertEquals(irpfResult1.getFoodAnnuity(), irpfResult2.getFoodAnnuity());
		org.junit.Assert.assertEquals(irpfResult1.getMinimunPersonal(), irpfResult2.getMinimunPersonal());

		org.junit.Assert.assertEquals(irpfResult1.getMinimunPersonal(), irpfResult2.getMinimunPersonal());
		org.junit.Assert.assertEquals(irpfResult1.getMinimunDisability(), irpfResult2.getMinimunDisability());
		org.junit.Assert.assertEquals(irpfResult1.getMinimunPersonalFamily(), irpfResult2.getMinimunPersonalFamily());
		org.junit.Assert.assertEquals(irpfResult1.getSocialSecurityPensioner(), irpfResult2.getSocialSecurityPensioner());
		org.junit.Assert.assertEquals(irpfResult1.getSpousalSupport(), irpfResult2.getSpousalSupport());
		org.junit.Assert.assertEquals(irpfResult1.getWorkDisabilityReduction(), irpfResult2.getWorkDisabilityReduction());
		org.junit.Assert.assertEquals(irpfResult1.getWorkMovingReduction(), irpfResult2.getWorkMovingReduction());
		org.junit.Assert.assertEquals(irpfResult1.getWorkProlongationReduction(), irpfResult2.getWorkProlongationReduction());
		org.junit.Assert.assertEquals(irpfResult1.getWorkRemunerationReduction(), irpfResult2.getWorkRemunerationReduction());

		org.junit.Assert.assertEquals(irpfResult1.getIrpf(), irpfResult2.getIrpf());
		org.junit.Assert.assertEquals(irpfResult1.getBaseIrpf(), irpfResult2.getBaseIrpf());
		org.junit.Assert.assertEquals(irpfResult1.getAnnualIrpf(), irpfResult2.getAnnualIrpf());
		org.junit.Assert.assertEquals(irpfResult1.getAnnualRemuneration(), irpfResult2.getAnnualRemuneration());
	}
	
}
