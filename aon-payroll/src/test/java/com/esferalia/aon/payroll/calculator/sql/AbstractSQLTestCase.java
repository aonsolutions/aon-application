package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementData.AGREEMENT_DATA;
import static com.esferalia.aon.jooq.tables.AgreementExtra.AGREEMENT_EXTRA;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AgreementLevelCategory.AGREEMENT_LEVEL_CATEGORY;
import static com.esferalia.aon.jooq.tables.AgreementLevelData.AGREEMENT_LEVEL_DATA;
import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
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
import static com.esferalia.aon.salary.enumeration.SalaryType.EXTRA;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_YEAR;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.jooq.Configuration;
import org.jooq.TransactionalCallable;
import org.junit.After;
import org.junit.Before;

import com.code.aon.common.enumeration.Month;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.dbutils.AonSQLException;
import com.code.aon.master.CreateDB;
import com.code.aon.master.VersionManager;
import com.code.aon.person.enumeration.Gender;
import com.code.aon.person.enumeration.MaritalStatus;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.RegistryType;
import com.esferalia.aon.jooq.tables.AgreementData;
import com.esferalia.aon.jooq.tables.AgreementLevelData;
import com.esferalia.aon.jooq.tables.AgreementPayment;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.AgreementLevelRecord;
import com.esferalia.aon.jooq.tables.records.AgreementPaymentRecord;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;
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
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;

public abstract class AbstractSQLTestCase {

	protected static class Extra {
		Month month;
		String start;
		String end;
		String issue;
		String expression;
	}

	protected static class Payment {
		Integer concept;
		String expression;
		SalaryType salary = SalaryType.SALARY;
	}

	private Connection connection;

	private static String getDbPort() {
		return System.getProperty("dbPort", "3306");
	}

	private static String getDbHost() {
		return System.getProperty("dbHost", "127.0.0.1");
	}

	private static String getDbName() {
		return System.getProperty("dbName", "aon_reveng");
	}

	private static String getDbUser() {
		return System.getProperty("dbUser", "dbuser");
	}

	private static String getDbPasswd() {
		return System.getProperty("dbPasswd", "serubd2000");
	}

	protected static Date getToday() {
		Calendar calendar = Calendar.getInstance();
		calendar = DateUtils.truncate(calendar, DAY_OF_MONTH);
		return new Date(calendar.getTimeInMillis() );
		//return new Date(calendar.get(YEAR), calendar.get(MONTH), calendar.get(DAY_OF_MONTH));
	}


	protected static Date addMonths(Date date, int value) {
		return add(date, MONTH, value);
	}

	protected static Date addDays(Date date, int value) {
		return add(date, DAY_OF_MONTH, value);
	}

	protected static Date add(Date date, int field, int value) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(field, value);
		return new Date(calendar.getTimeInMillis());
	}

	public Connection getConnection() {
		return connection;
	}

	@Before
	public void setUp() throws ClassNotFoundException, SQLException, AonSQLException {
		// first of all load JDBC driver
		Class.forName("org.gjt.mm.mysql.Driver");

		String dbHost = getDbHost();
		String dbPort = getDbPort();
		String dbName = getDbName();
		String dbUser = getDbUser();
		String dbPasswd = getDbPasswd();

		String url = String
				.format("jdbc:mysql://%s:%s", dbHost, dbPort, dbName);
		connection = DriverManager.getConnection(url, dbUser, dbPasswd);

		ResultSet rs = connection.createStatement().executeQuery(
				"SHOW DATABASES");
		while (rs.next()) {
			if (rs.getString(1).startsWith(dbName)) {
				connection.createStatement().execute("use " + rs.getString(1));
				return;
			}
		}
		
		new VersionManager().createDatabase(connection, dbName);
		connection.createStatement().execute("use " + dbName );
	}

	@After
	public void tearDown() throws SQLException {
		if (connection != null)
			connection.close();
	}

	protected final DomainRecord newDomain(AONContext aonContext) {
		return aonContext
				.getDslContext()
				.insertInto(DOMAIN)
				.set(DOMAIN.NAME,
						String.valueOf(System
								.currentTimeMillis()))
				.set(DOMAIN.OWNER, "")
				.set(DOMAIN.DESCRIPTION, "").returning()
				.fetchOne();
	}	

	protected final AgreementRecord newAgreement(AONContext aonContext) {
		// Add a domain, with a generated ID
		DomainRecord domain = newDomain(aonContext);

		return aonContext.getDslContext()
				.insertInto(AGREEMENT)
				.set(AGREEMENT.DOMAIN, domain.getId())
				.set(AGREEMENT.DESCRIPTION, "").returning()
				.fetchOne();
	}
	
	protected final AgreementLevelCategoryRecord newAgreementCategory(AONContext aonContext,
			AgreementRecord agreement) {
		AgreementLevelRecord level = aonContext
				.getDslContext()
				.insertInto(AGREEMENT_LEVEL)
				.set(AGREEMENT_LEVEL.DOMAIN, agreement.getDomain())
				.set(AGREEMENT_LEVEL.AGREEMENT,
						agreement.getId())
				.set(AGREEMENT_LEVEL.DESCRIPTION, "")
				.returning().fetchOne();

		return aonContext
				.getDslContext()
				.insertInto(AGREEMENT_LEVEL_CATEGORY)
				.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN,
						agreement.getDomain())
				.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION, "")
				.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL,
						level.getId()).returning().fetchOne();
	}

	protected final AgreementLevelCategoryRecord newAgreement(AONContext aonContext,
			Extra extras []) {
		return newAgreement(aonContext, extras, Collections.emptyMap());
	}
	
	protected final AgreementLevelCategoryRecord newAgreement(AONContext aonContext,
			Extra extras [], Map<String, String> datas) {
		return newAgreement(aonContext,  extras, new Payment[]{},datas);
	}

	protected final AgreementLevelCategoryRecord newAgreement(AONContext aonContext,
			Extra extras [], Payment payments []) {
		return newAgreement(aonContext, extras, payments, Collections.emptyMap());
	}
	
	protected final AgreementLevelCategoryRecord newAgreement(AONContext aonContext,
			Extra extras [], Payment payments [], Map<String, String> datas) {
		return aonContext.getDslContext().transactionResult(
				new TransactionalCallable<AgreementLevelCategoryRecord>() {
					@Override
					public AgreementLevelCategoryRecord run(Configuration arg0)
							throws Exception {
						// Add a domain, with a generated ID
						

						AgreementRecord agreement = newAgreement(aonContext);

						AgreementLevelCategoryRecord category = newAgreementCategory(aonContext, agreement);

						Calendar calendar = Calendar.getInstance();
						// Be care that the first day of the year has value 1.
						calendar.set(DAY_OF_YEAR, 1);
						calendar.add(YEAR, -2);
						Date startDate = new Date(calendar.getTimeInMillis());

						addExtras(aonContext, agreement, startDate, extras);
						
						addPayments(aonContext, agreement, startDate, payments);

						addData(aonContext, agreement, startDate, datas);
						
						return category;
					}
			
				});
	}
	
	
	protected void addExtras(AONContext aonContext, AgreementRecord agreement, Date startDate, Extra extras []) {
		for (Extra extra : extras) {
			AgreementPaymentRecord payment = aonContext
					.getDslContext()
					.insertInto(AGREEMENT_PAYMENT)
					.set(AGREEMENT_PAYMENT.DOMAIN,
							agreement.getDomain())
					.set(AGREEMENT_PAYMENT.AGREEMENT,
							agreement.getId())
					.set(AGREEMENT_PAYMENT.MONTH,
							(byte) extra.month.ordinal())
					.set(AGREEMENT_PAYMENT.TYPE,
							(byte) PaymentType.CRA_0004
									.ordinal())
					.set(AGREEMENT_PAYMENT.START_DATE,
							startDate)
					.set(AGREEMENT_PAYMENT.EXPRESSION,
							extra.expression)
					.set(AGREEMENT_PAYMENT.DESCRIPTION,
							extra.expression)
					.set(AGREEMENT_PAYMENT.IRPF_EXPRESSION,
							PAYMENT.getName())
					.set(AGREEMENT_PAYMENT.SALARY_TYPE,
							(byte) EXTRA.ordinal())
					.set(AGREEMENT_PAYMENT.QUOTE_EXPRESSION,
							String.format("%s/12",
									PAYMENT.getName()))
					.returning().fetchOne();

			aonContext
					.getDslContext()
					.insertInto(AGREEMENT_EXTRA)
					.set(AGREEMENT_EXTRA.DOMAIN, agreement.getDomain())
					.set(AGREEMENT_EXTRA.AGREEMENT,
							agreement.getId())
					.set(AGREEMENT_EXTRA.AGREEMENT_PAYMENT,
							payment.getId())
					.set(AGREEMENT_EXTRA.START_DATE,
							extra.start)
					.set(AGREEMENT_EXTRA.END_DATE, extra.end)
					.set(AGREEMENT_EXTRA.ISSUE_DATE,
							extra.issue).returning().fetchOne();
		}
		
	}
	
	protected void addPayments(AONContext aonContext, AgreementRecord agreement, Date startDate, Payment payments []) {
		for (Payment payment : payments) {
			aonContext
			.getDslContext()
			.insertInto(AGREEMENT_PAYMENT)
			.set(AGREEMENT_PAYMENT.DOMAIN, agreement.getDomain())
			.set(AGREEMENT_PAYMENT.AGREEMENT,
					agreement.getId())
			.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT,
					payment.concept)
			.set(AGREEMENT_PAYMENT.EXPRESSION,
					payment.expression)
			.set(AGREEMENT_PAYMENT.START_DATE,
					startDate)
			.set(AGREEMENT_PAYMENT.SALARY_TYPE,
					(byte) payment.salary.ordinal())
			.returning().fetchOne();
		}
	}
	
	
	protected void addData(AONContext aonContext, AgreementRecord agreement, Date startDate, Map<String, String> datas ) {
		for (Map.Entry<String, String> data : datas.entrySet()) {
			aonContext.getDslContext().insertInto(AGREEMENT_DATA)
					.set(AGREEMENT_DATA.DOMAIN, agreement.getDomain())
					.set(AGREEMENT_DATA.AGREEMENT, agreement.getId())
					.set(AGREEMENT_DATA.START_DATE, startDate)
					.set(AGREEMENT_DATA.NAME, data.getKey())
					.set(AGREEMENT_DATA.EXPRESSION, data.getValue()).execute();

		}
	}	

	protected final ContractRecord newContract(AONContext aonContext, Date startDate, Map<String, String> data, AgreementLevelCategoryRecord category) {
		return newContract(aonContext, startDate, data, new String[0],
				new String[0], category);
	}

	protected final ContractRecord newContract(AONContext aonContext, Date startDate,
			Map<String, String> data) {
		return newContract(aonContext, startDate, data, new String[0],
				new String[0], null);
	}

	protected final ContractRecord newContract(AONContext aonContext,
			String[] payments, String[] deductions) {
		return newContract(aonContext, payments, deductions, null);
	}

	protected final ContractRecord newContract(AONContext aonContext,
			String[] payments, String[] deductions,
			AgreementLevelCategoryRecord category) {
		Calendar calendar = Calendar.getInstance();
		// Be care that the first day of the year has value 1.
		calendar.set(DAY_OF_YEAR, 1);
		Date startDate = new Date(calendar.getTimeInMillis());
		return newContract(aonContext, startDate, Collections.emptyMap(),
				payments, deductions, category);
	}

	protected final ContractRecord newContract(AONContext aonContext, Date startDate,
			Map<String, String> data, String[] payments, String[] deductions,
			AgreementLevelCategoryRecord category) {
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
								.set(ENTERPRISE_ACTIVITY.ENTERPRISE,
										enterprise.getId())
								.set(ENTERPRISE_ACTIVITY.DESCRIPTION, "")
								.set(ENTERPRISE_ACTIVITY.TYPE,
										(byte) SSRegimeType.GENERAL.ordinal())
								.returning().fetchOne();

						EnterpriseCccRecord enterpriseCcc = aonContext
								.getDslContext()
								.insertInto(ENTERPRISE_CCC)
								.set(ENTERPRISE_CCC.DOMAIN, domain.getId())
								.set(ENTERPRISE_CCC.TYPE,
										(byte) CCCType.PRINCIPAL.ordinal())
								.set(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY,
										enterpriseActivity.getId()).returning()
								.fetchOne();

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

						ContractRecord contract = aonContext
								.getDslContext()
								.insertInto(CONTRACT)
								.set(CONTRACT.DOMAIN, domain.getId())
								.set(CONTRACT.PERSON, person.getId())
								.set(CONTRACT.WORKPLACE, workplace.getId())
								.set(CONTRACT.START_DATE, startDate)
								.set(CONTRACT.ENTERPRISE_CCC,
										enterpriseCcc.getId())
								.set(CONTRACT.ENTERPRISE_ACTIVITY,
										enterpriseActivity.getId())
								.set(CONTRACT.AGREEMENT_LEVEL_CATEGORY,
										category != null ? category.getId()
												: null).returning().fetchOne();

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

						for (String deduction : deductions) {
							DeductionType type = DeductionType.COMMON_CONTINGENCY;
							if (deduction.contains("BASE_IRPF"))
								type = DeductionType.IRPF;
							else if (deduction.contains("BASE_ESTR"))
								type = DeductionType.STRUCTURAL_OVERTIME;
							else if (deduction.contains("BASE_NESTR"))
								type = DeductionType.NON_STRUCTURAL_OVERTIME;

							aonContext
									.getDslContext()
									.insertInto(CONTRACT_DEDUCTION)
									.set(CONTRACT_DEDUCTION.DOMAIN,
											domain.getId())
									.set(CONTRACT_DEDUCTION.CONTRACT,
											contract.getId())
									.set(CONTRACT_DEDUCTION.START_DATE,
											startDate)
									.set(CONTRACT_DEDUCTION.DESCRIPTION,
											deduction)
									.set(CONTRACT_DEDUCTION.EXPRESSION,
											deduction)
									.set(CONTRACT_DEDUCTION.TYPE,
											(byte) type.ordinal()).execute();
						}

						addData(aonContext, contract, contract.getStartDate(),
								contract.getEndDate(), data);

						return contract;
					}
				});

	}

	protected final void addData(AONContext aonContext, ContractRecord contract,
			Date startDate, Date endDate, Map<String, String> datas) {
		for (Map.Entry<String, String> data : datas.entrySet()) {
			aonContext.getDslContext().insertInto(CONTRACT_DATA)
					.set(CONTRACT_DATA.DOMAIN, contract.getDomain())
					.set(CONTRACT_DATA.CONTRACT, contract.getId())
					.set(CONTRACT_DATA.START_DATE, startDate)
					.set(CONTRACT_DATA.END_DATE, endDate)
					.set(CONTRACT_DATA.NAME, data.getKey())
					.set(CONTRACT_DATA.EXPRESSION, data.getValue()).execute();

		}
	}

	protected final void addData(AONContext aonContext, AgreementLevelCategoryRecord category,
			Date startDate, Date endDate, Map<String, String> datas) {
		for (Map.Entry<String, String> data : datas.entrySet()) {
			aonContext.getDslContext().insertInto(AGREEMENT_LEVEL_DATA)
					.set(AGREEMENT_LEVEL_DATA.DOMAIN, category.getDomain())
					.set(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL, category.getAgreementLevel())
					.set(AGREEMENT_LEVEL_DATA.START_DATE, startDate)
					.set(AGREEMENT_LEVEL_DATA.END_DATE, endDate)
					.set(AGREEMENT_LEVEL_DATA.NAME, data.getKey())
					.set(AGREEMENT_LEVEL_DATA.EXPRESSION, data.getValue()).execute();

		}
	}

	protected final void addData(AONContext aonContext, Integer domain, AgreementLevelCategoryRecord category,
			Date startDate, Date endDate, Map<String, String> datas) {
		for (Map.Entry<String, String> data : datas.entrySet()) {
			aonContext.getDslContext().insertInto(AGREEMENT_LEVEL_DATA)
					.set(AGREEMENT_LEVEL_DATA.DOMAIN, domain)
					.set(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL, category.getAgreementLevel())
					.set(AGREEMENT_LEVEL_DATA.START_DATE, startDate)
					.set(AGREEMENT_LEVEL_DATA.END_DATE, endDate)
					.set(AGREEMENT_LEVEL_DATA.NAME, data.getKey())
					.set(AGREEMENT_LEVEL_DATA.EXPRESSION, data.getValue()).execute();

		}
	}

	protected final PaymentConceptRecord addConcept(AONContext aonContext, String code) {
		DomainRecord domain = newDomain(aonContext);
		return aonContext
				.getDslContext()
				.insertInto(PAYMENT_CONCEPT)
				.set(PAYMENT_CONCEPT.DOMAIN, domain.getId())
				.set(PAYMENT_CONCEPT.CODE, code)
				.set(PAYMENT_CONCEPT.TYPE,
						(byte) PaymentType.CRA_0001.ordinal())
				.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, "_P")
				.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, "_P")
				.returning().fetchOne();
	
	}

	protected final void addPayment(AONContext aonContext, ContractRecord contract, PaymentConceptRecord concept,
			String expression) {
				aonContext
						.getDslContext()
						.insertInto(CONTRACT_PAYMENT)
						.set(CONTRACT_PAYMENT.DOMAIN, contract.getDomain())
						.set(CONTRACT_PAYMENT.PAYMENT_CONCEPT, concept.getId())
						.set(CONTRACT_PAYMENT.CONTRACT, contract.getId())
						.set(CONTRACT_PAYMENT.START_DATE, contract.getStartDate())
						.set(CONTRACT_PAYMENT.END_DATE, contract.getEndDate())
						.set(CONTRACT_PAYMENT.EXPRESSION, expression)
						.set(CONTRACT_PAYMENT.QUOTE_EXPRESSION, "_P")
						.set(CONTRACT_PAYMENT.IRPF_EXPRESSION, "_P")
						.set(CONTRACT_PAYMENT.TYPE,
								(byte) PaymentType.CRA_0001.ordinal())
						.set(CONTRACT_PAYMENT.SALARY_TYPE,
								(byte) SalaryType.SALARY.ordinal()).execute();
			
			}

	protected final void addPayment(AONContext aonContext, ContractRecord contract, Date startDate,
			String expression) {
				aonContext
						.getDslContext()
						.insertInto(CONTRACT_PAYMENT)
						.set(CONTRACT_PAYMENT.DOMAIN, contract.getDomain())
						.set(CONTRACT_PAYMENT.CONTRACT, contract.getId())
						.set(CONTRACT_PAYMENT.START_DATE, startDate)
						.set(CONTRACT_PAYMENT.END_DATE, contract.getEndDate())
						.set(CONTRACT_PAYMENT.EXPRESSION, expression)
						.set(CONTRACT_PAYMENT.QUOTE_EXPRESSION, "_P")
						.set(CONTRACT_PAYMENT.IRPF_EXPRESSION, "_P")
						.set(CONTRACT_PAYMENT.TYPE,
								(byte) PaymentType.CRA_0001.ordinal())
						.set(CONTRACT_PAYMENT.SALARY_TYPE,
								(byte) SalaryType.SALARY.ordinal()).execute();
			
			}
}
