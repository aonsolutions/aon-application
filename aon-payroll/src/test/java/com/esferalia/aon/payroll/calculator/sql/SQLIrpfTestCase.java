package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractDeduction.CONTRACT_DEDUCTION;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PAYMENT;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_YEAR;
import static junit.framework.Assert.assertEquals;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.function.Consumer;

import org.jooq.Configuration;
import org.jooq.TransactionalCallable;
import org.junit.Test;

import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.person.enumeration.Gender;
import com.code.aon.person.enumeration.MaritalStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.RegistryType;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseActivityRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseCccRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.jooq.tables.records.RaddressRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.jooq.tables.records.ScopeRecord;
import com.esferalia.aon.jooq.tables.records.WorkplaceRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.IrpfOutcome;
import com.esferalia.aon.payroll.IrpfResult;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext.IListener;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.IExpression;
import com.esferalia.aon.salary.expression.ITimedVariable;

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

	// ------------------------------------------------------------------------

	private void test(Consumer<IrpfResult> c, String [] payments, String [] deductions)
			throws ExpressionException, SQLException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection, "", 0);

		ContractRecord contract = newContract(aonContext, payments ,
				deductions);

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

	private ContractRecord newContract(AONContext aonContext,
			String [] payments, String ...deductions ) {
		return aonContext.getDslContext().transactionResult(
				new TransactionalCallable<ContractRecord>() {
					@Override
					public ContractRecord run(Configuration configuration)
							throws Exception {
						// Add a domain, with a generated ID
						DomainRecord domain = aonContext
								.getDslContext()
								.insertInto(DOMAIN)
								.set(DOMAIN.NAME,
										String.valueOf(System
												.currentTimeMillis()))
								.set(DOMAIN.OWNER, "")
								.set(DOMAIN.DESCRIPTION, "").returning()
								.fetchOne();

						ScopeRecord scope = aonContext.getDslContext()
								.insertInto(SCOPE)
								.set(SCOPE.DOMAIN, domain.getId())
								.set(SCOPE.DESCRIPTION, "").returning()
								.fetchOne();

						RegistryRecord enterprise = aonContext
								.getDslContext()
								.insertInto(REGISTRY)
								.set(REGISTRY.DOMAIN, domain.getId())
								.set(REGISTRY.NAME, "")
								.set(REGISTRY.ALIAS, "")
								.set(REGISTRY.DOCUMENT, "")
								.set(REGISTRY.DOCUMENT_COUNTRY, "")
								.set(REGISTRY.DOCUMENT_TYPE,
										(byte) DocumentType.OTHER.ordinal())
								.set(REGISTRY.NATIONALITY, "")
								.set(REGISTRY.TYPE,
										(byte) RegistryType.LEGAL.ordinal())
								.returning().fetchOne();

						aonContext.getDslContext().insertInto(ENTERPRISE)
								.set(ENTERPRISE.DOMAIN, domain.getId())
								.set(ENTERPRISE.REGISTRY, enterprise.getId())
								.set(ENTERPRISE.SCOPE, scope.getId()).execute();

						EnterpriseActivityRecord enterpriseActivity = aonContext
								.getDslContext()
								.insertInto(ENTERPRISE_ACTIVITY)
								.set(ENTERPRISE_ACTIVITY.DOMAIN, domain.getId())
								.set(ENTERPRISE_ACTIVITY.ENTERPRISE, enterprise.getId())
								.set(ENTERPRISE_ACTIVITY.DESCRIPTION, "")
								.set(ENTERPRISE_ACTIVITY.TYPE, (byte)SSRegimeType.GENERAL.ordinal())
								.returning().fetchOne();

						EnterpriseCccRecord enterpriseCcc = aonContext
								.getDslContext()
								.insertInto(ENTERPRISE_CCC)
								.set(ENTERPRISE_CCC.DOMAIN, domain.getId())
								.set(ENTERPRISE_CCC.TYPE, (byte)CCCType.PRINCIPAL.ordinal())
								.set(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY, enterpriseActivity.getId())
								.returning().fetchOne();

						RaddressRecord raddress = aonContext
								.getDslContext()
								.insertInto(RADDRESS)
								.set(RADDRESS.DOMAIN, domain.getId())
								.set(RADDRESS.REGISTRY, enterprise.getId())
								.set(RADDRESS.TYPE,
										(byte) AddressType.MAIN.ordinal())
								.returning().fetchOne();

						WorkplaceRecord workplace = aonContext
								.getDslContext()
								.insertInto(WORKPLACE)
								.set(WORKPLACE.DOMAIN, domain.getId())
								.set(WORKPLACE.ENTERPRISE, enterprise.getId())
								.set(WORKPLACE.ACTIVE, (byte) 1)
								.set(WORKPLACE.ECONOMICAGREEMENT,
										(byte) Administration.COMMON_TERRITORY
												.ordinal())
								.set(WORKPLACE.DESCRIPTION, "")
								.set(WORKPLACE.ADDRESS, raddress.getId())
								.set(WORKPLACE.SCOPE, scope.getId())
								.returning().fetchOne();

						aonContext
								.getDslContext()
								.insertInto(PAYROLL_WORKPLACE)
								.set(PAYROLL_WORKPLACE.DOMAIN, domain.getId())
								.set(PAYROLL_WORKPLACE.WORKPLACE,
										workplace.getId()).execute();

						RegistryRecord person = aonContext
								.getDslContext()
								.insertInto(REGISTRY)
								.set(REGISTRY.DOMAIN, domain.getId())
								.set(REGISTRY.NAME, "")
								.set(REGISTRY.ALIAS, "")
								.set(REGISTRY.DOCUMENT, "")
								.set(REGISTRY.DOCUMENT_COUNTRY, "")
								.set(REGISTRY.DOCUMENT_TYPE,
										(byte) DocumentType.OTHER.ordinal())
								.set(REGISTRY.NATIONALITY, "")
								.set(REGISTRY.TYPE,
										(byte) RegistryType.NATURAL.ordinal())
								.returning().fetchOne();

						aonContext
								.getDslContext()
								.insertInto(PERSON)
								.set(PERSON.DOMAIN, domain.getId())
								.set(PERSON.REGISTRY, person.getId())
								.set(PERSON.NAME, "")
								.set(PERSON.FIRST_SURNAME, "")
								.set(PERSON.SECOND_SURNAME, "")
								// .set(PERSON.BIRTH_DATE, null)
								.set(PERSON.SOCIAL_SECURITY_NUM, "")
								.set(PERSON.GENDER,
										(byte) Gender.UNKNOWN.ordinal())
								.set(PERSON.MARITAL_STATUS,
										(byte) MaritalStatus.UNKNOWN.ordinal())
								.execute();

						Calendar calendar = Calendar.getInstance();
						// Be care that the first day of the year has value 1.
						calendar.set(DAY_OF_YEAR, 1);
						Date startDate = new Date(calendar.getTimeInMillis());

						ContractRecord contract = aonContext.getDslContext()
								.insertInto(CONTRACT)
								.set(CONTRACT.DOMAIN, domain.getId())
								.set(CONTRACT.PERSON, person.getId())
								.set(CONTRACT.WORKPLACE, workplace.getId())
								.set(CONTRACT.START_DATE, startDate)
								.set(CONTRACT.ENTERPRISE_CCC, enterpriseCcc.getId())
								.set(CONTRACT.ENTERPRISE_ACTIVITY, enterpriseActivity.getId())
								.returning().fetchOne();

						for (int i = 0; i < payments.length; i++) {
							String payment = payments[i];
							PaymentConceptRecord concept = aonContext
									.getDslContext()
									.insertInto(PAYMENT_CONCEPT)
									.set(PAYMENT_CONCEPT.DOMAIN, domain.getId())
									.set(PAYMENT_CONCEPT.CODE,
											String.format("P_%d", i))
									.set(PAYMENT_CONCEPT.TYPE,
											(byte) PaymentType.CRA_0000
													.ordinal())
									.set(PAYMENT_CONCEPT.DESCRIPTION, payment)
									.set(PAYMENT_CONCEPT.EXPRESSION, payment)
									.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION,
											PAYMENT.getName())
									.set(PAYMENT_CONCEPT.IRPF_EXPRESSION,
											PAYMENT.getName()).returning()
									.fetchOne();

							aonContext
									.getDslContext()
									.insertInto(CONTRACT_PAYMENT)
									.set(CONTRACT_PAYMENT.DOMAIN,
											domain.getId())
									.set(CONTRACT_PAYMENT.CONTRACT,
											contract.getId())
									.set(CONTRACT_PAYMENT.START_DATE, startDate)
									.set(CONTRACT_PAYMENT.PAYMENT_CONCEPT,
											concept.getId())
									.set(CONTRACT_PAYMENT.SALARY_TYPE,
											(byte) SalaryType.SALARY.ordinal())
									.execute();
						}
						
						for (String deduction: deductions ) {
							DeductionType type = DeductionType.COMMON_CONTINGENCY;
							if ( deduction.contains("BASE_IRPF")) 
								type = DeductionType.IRPF;
							else if ( deduction.contains("BASE_ESTR")) 
								type = DeductionType.STRUCTURAL_OVERTIME;
							else if ( deduction.contains("BASE_NESTR")) 
								type = DeductionType.NON_STRUCTURAL_OVERTIME;
							
							aonContext
							.getDslContext()
							.insertInto(CONTRACT_DEDUCTION)
							.set(CONTRACT_DEDUCTION.DOMAIN,
									domain.getId())
							.set(CONTRACT_DEDUCTION.CONTRACT,
									contract.getId())
							.set(CONTRACT_DEDUCTION.START_DATE, startDate)
							.set(CONTRACT_DEDUCTION.DESCRIPTION, deduction )
							.set(CONTRACT_DEDUCTION.EXPRESSION, deduction )
							.set(CONTRACT_DEDUCTION.TYPE,  (byte) type.ordinal())
							.execute();
						}

						return contract;
					}
				});

	}
}
