package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementData.AGREEMENT_DATA;
import static com.esferalia.aon.jooq.tables.AgreementExtra.AGREEMENT_EXTRA;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AgreementLevelCategory.AGREEMENT_LEVEL_CATEGORY;
import static com.esferalia.aon.jooq.tables.AgreementLevelData.AGREEMENT_LEVEL_DATA;
import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.BonusConcept.BONUS_CONCEPT;
import static com.esferalia.aon.jooq.tables.Calendar.CALENDAR;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractBonus.CONTRACT_BONUS;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractDeduction.CONTRACT_DEDUCTION;
import static com.esferalia.aon.jooq.tables.ContractEmbargo.CONTRACT_EMBARGO;
import static com.esferalia.aon.jooq.tables.ContractLeave.CONTRACT_LEAVE;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Holiday.HOLIDAY;
import static com.esferalia.aon.jooq.tables.HolidayDetail.HOLIDAY_DETAIL;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.SystemCost.SYSTEM_COST;
import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;
import static com.esferalia.aon.jooq.tables.SystemDeduction.SYSTEM_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PAYMENT;
import static com.esferalia.aon.salary.enumeration.SalaryType.EXTRA;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_YEAR;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.commons.lang.time.DateUtils;
import org.jooq.Configuration;
import org.jooq.Record;
import org.jooq.TransactionalCallable;
import org.junit.After;
import org.junit.Before;

import com.code.aon.common.enumeration.Month;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.master.VersionManager;
import com.code.aon.person.enumeration.Gender;
import com.code.aon.person.enumeration.MaritalStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.RegistryType;
import com.esferalia.aon.jooq.tables.records.AgreementExtraRecord;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.AgreementLevelRecord;
import com.esferalia.aon.jooq.tables.records.AgreementPaymentRecord;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;
import com.esferalia.aon.jooq.tables.records.BonusConceptRecord;
import com.esferalia.aon.jooq.tables.records.CalendarRecord;
import com.esferalia.aon.jooq.tables.records.ContractBonusRecord;
import com.esferalia.aon.jooq.tables.records.ContractEmbargoRecord;
import com.esferalia.aon.jooq.tables.records.ContractLeaveRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseActivityRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseCccRecord;
import com.esferalia.aon.jooq.tables.records.HolidayRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.jooq.tables.records.RaddressRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.jooq.tables.records.ScopeRecord;
import com.esferalia.aon.jooq.tables.records.WorkplaceRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.RoundSalaryBuilder;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.BonusType;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;

import net.aonsolutions.core.dbutils.AonSQLException;

public abstract class AbstractSQLTestCase {

	private static final String UNSET = "UNSET";

	public static class Extra {
		public Month month;
		public String start;
		public String end;
		public String issue;
		public Integer concept;
		public String expression;
		public String quoteExpression = UNSET;
	}

	public static class Payment {
		public Month month;
		public Integer concept;
		public String expression;
		public SalaryType salary = SalaryType.SALARY;
	}

	private Connection connection;

	public Connection getConnection() {
		return connection;
	}

	@Before
	public void setUp() throws ClassNotFoundException, SQLException,
			AonSQLException {
		shutUp();
		connection = connect();
		AONContext context = new AONContext(connection);
		cleanSystemData(context);
		cleanSystemCosts(context);
		cleanSystemPayments(context);
	}

	@After
	public void tearDown() throws SQLException {
		if (connection != null)
			connection.close();
	}

	// ------------------------------------------------------------------------

	protected ISQLContractSalaryCalculatorContext getContractSalaryCalculatorContext(
			Connection connection, Date startDate, Date endDate,
			Date issueDate, Criteria criteria, IContractSalaryCalculatorContext.IListener listener) throws ExpressionException,
			SQLException {
		ISQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, criteria);
		if ( listener != null)
			ctx.setListener(listener);
		ctx.next();
		return ctx;
	}

	protected ISQLContractSalaryCalculatorContext getContractSalaryCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate, Criteria criteria) throws ExpressionException, SQLException {
		return getContractSalaryCalculatorContext(connection, startDate, endDate, issueDate, criteria, null);
	}

	protected ISQLContractSalaryCalculatorContext getContractSalaryCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate, ContractRecord contract,
			IContractSalaryCalculatorContext.IListener listener) throws ExpressionException, SQLException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		return getContractSalaryCalculatorContext(connection, startDate, endDate, issueDate, criteria, listener);
	}

	protected ISQLContractSalaryCalculatorContext getContractSalaryCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate, ContractRecord contract)
			throws ExpressionException, SQLException {
		return getContractSalaryCalculatorContext(connection, startDate, endDate, issueDate, contract, null);
	}
	// ------------------------------------------------------- static 'library'

	protected final void cleanSystemData(AONContext aonContext) {
		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=0");

		aonContext.getDslContext().delete(SYSTEM_DATA).execute();

		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=1");
	}

	protected final void addSystemData(AONContext aonContext, Date startDate, Date endDate, Map<String, String> datas) {
		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=0");

		for (Map.Entry<String, String> data : datas.entrySet()) {
			aonContext.getDslContext().insertInto(SYSTEM_DATA).set(SYSTEM_DATA.DOMAIN, 0)
					.set(SYSTEM_DATA.START_DATE, startDate).set(SYSTEM_DATA.END_DATE, endDate)
					.set(SYSTEM_DATA.NAME, data.getKey()).set(SYSTEM_DATA.EXPRESSION, data.getValue()).execute();

		}
		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=1");
	}

	protected final void addSSRegimeData(AONContext aonContext, SSRegimeType ssRegimetype, Date startDate, Date endDate,
			Map<String, String> datas) {
		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=0");

		for (Map.Entry<String, String> data : datas.entrySet()) {
			aonContext.getDslContext().insertInto(SYSTEM_DATA).set(SYSTEM_DATA.DOMAIN, (-1) * ssRegimetype.ordinal())
					.set(SYSTEM_DATA.START_DATE, startDate).set(SYSTEM_DATA.END_DATE, endDate)
					.set(SYSTEM_DATA.NAME, data.getKey()).set(SYSTEM_DATA.EXPRESSION, data.getValue()).execute();

		}
		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=1");
	}

	protected final void addCCCData(AONContext aonContext, CCCType cccType, Date startDate, Date endDate,
			Map<String, String> datas) {
		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=0");

		for (Map.Entry<String, String> data : datas.entrySet()) {
			aonContext.getDslContext().insertInto(SYSTEM_DATA).set(SYSTEM_DATA.DOMAIN, (-1) * (100 + cccType.ordinal()))
					.set(SYSTEM_DATA.START_DATE, startDate).set(SYSTEM_DATA.END_DATE, endDate)
					.set(SYSTEM_DATA.NAME, data.getKey()).set(SYSTEM_DATA.EXPRESSION, data.getValue()).execute();

		}
		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=1");
	}

	protected final void cleanSystemCosts(AONContext aonContext) {
		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=0");

		aonContext.getDslContext().delete(SYSTEM_COST).execute();

		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=1");
	}

	protected final void cleanSystemPayments(AONContext aonContext) {
		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=0");

		aonContext.getDslContext().delete(SYSTEM_PAYMENT).execute();

		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=1");
	}

	protected final void cleanSystemDeductions(AONContext aonContext) {
		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=0");

		aonContext.getDslContext().delete(SYSTEM_DEDUCTION).execute();

		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=1");
	}

	protected final void addCCCCost(AONContext aonContext, CCCType cccType, Date startDate, String code,
			DeductionType type, String expression) {
		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=0");

		aonContext.getDslContext().insertInto(SYSTEM_COST).set(SYSTEM_COST.CODE, code)
				.set(SYSTEM_COST.START_DATE, startDate)
				.set(SYSTEM_COST.TYPE, (byte) (type != null ? type.ordinal() : DeductionType.OTHER.ordinal()))
				.set(SYSTEM_COST.DOMAIN, (-1) * (100 + cccType.ordinal())).set(SYSTEM_COST.EXPRESSION, expression)
				.execute();

		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=1");
	}

	protected final void addSSRegimeCost(AONContext aonContext, SSRegimeType ssRegimetype, Date startDate, String code,
			DeductionType type, String expression) {
		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=0");

		aonContext.getDslContext().insertInto(SYSTEM_COST)
				.set(SYSTEM_COST.CODE, code)
				.set(SYSTEM_COST.START_DATE, startDate)
				.set(SYSTEM_COST.TYPE, (byte) (type != null ? type.ordinal() : DeductionType.OTHER.ordinal()))
				.set(SYSTEM_COST.DOMAIN, (-1) * ssRegimetype.ordinal())
				.set(SYSTEM_COST.EXPRESSION, expression)
				.execute();

		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=1");
	}

	protected final void addSSRegimePayment(AONContext aonContext,
			SSRegimeType ssRegimetype,
			Date startDate,
			PaymentConceptRecord concept,
			PaymentType paymentType,
			String expression,
			String quoteExpression,
			String irpfExpression,
			SalaryType salaryType) {
		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=0");

		aonContext.getDslContext().insertInto(SYSTEM_PAYMENT)
				.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, concept != null? concept.getId(): null)
				.set(SYSTEM_PAYMENT.START_DATE, startDate)
				.set(SYSTEM_PAYMENT.EXPRESSION, expression)
				.set(SYSTEM_PAYMENT.IRPF_EXPRESSION, irpfExpression)
				.set(SYSTEM_PAYMENT.QUOTE_EXPRESSION, quoteExpression)
				.set(SYSTEM_PAYMENT.DOMAIN, (-1) * ssRegimetype.ordinal())
				.set(SYSTEM_PAYMENT.TYPE,
						(byte) (paymentType != null ? paymentType.ordinal() : PaymentType.CRA_0001.ordinal()))
				.set(SYSTEM_PAYMENT.SALARY_TYPE,
						(byte) (salaryType != null ? salaryType.ordinal() : SalaryType.SALARY.ordinal()))

				.execute();

		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=1");
	}

	protected final void addSSRegimePayment(AONContext aonContext,
			SSRegimeType ssRegimetype,
			Date startDate,
			PaymentType paymentType,
			String expression,
			String quoteExpression,
			String irpfExpression,
			SalaryType salaryType) {
		addSSRegimePayment(aonContext,
				ssRegimetype,
				startDate,
				null,
				paymentType,
				expression,
				quoteExpression,
				irpfExpression,
				salaryType);
	}

	protected final void addSSRegimePayment(AONContext aonContext, SSRegimeType ssRegimetype, Date startDate,
			PaymentType paymentType, String expression, String quoteExpression, String irpfExpression) {
		addSSRegimePayment(aonContext, ssRegimetype, startDate, null, paymentType, expression, quoteExpression,
				irpfExpression, null);
	}

	protected final void addSSRegimePayment(AONContext aonContext, SSRegimeType ssRegimetype, Date startDate,
			PaymentType type, String expression) {
		addSSRegimePayment(aonContext, ssRegimetype, startDate, type, expression, ContextVariable.ALL,
				ContextVariable.ALL);
	}

	protected final void addSSRegimeDeduction(AONContext aonContext, SSRegimeType ssRegimetype, Date startDate,
			DeductionType deductionType, String expression) {
		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=0");

		aonContext.getDslContext().insertInto(SYSTEM_DEDUCTION)

				.set(SYSTEM_DEDUCTION.START_DATE, startDate)
				.set(SYSTEM_DEDUCTION.EXPRESSION, expression)
				.set(SYSTEM_DEDUCTION.DOMAIN, (-1) * ssRegimetype.ordinal())
				.set(SYSTEM_DEDUCTION.TYPE,
						(byte) (deductionType != null ? deductionType.ordinal() : DeductionType.OTHER.ordinal()))
				.execute();

		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=1");
	}


	public static String getDbPort() {
		return System.getProperty("dbPort", "3306");
	}

	public static String getDbHost() {
		return System.getProperty("dbHost", "127.0.0.1");
	}

	public static String getDbName() {
		return System.getProperty("dbName", "aon_reveng");
	}

	public static String getDbUser() {
		return System.getProperty("dbUser", "dbuser");
	}

	public static String getDbPasswd() {
		return System.getProperty("dbPasswd", "serubd2000");
	}

	public static String getDbUseSSL() {
		return System.getProperty("dbUseSSL", "false");
	}

	public static String getDbTimeZone() {
		return System.getProperty("dbTimeZone", TimeZone.getDefault().getID());
	}

	public static Date addMonths(Date date, int value) {
		return add(date, MONTH, value);
	}

	public static Date addDays(Date date, int value) {
		return add(date, DAY_OF_MONTH, value);
	}

	public static Date add(Date date, int field, int value) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(field, value);
		return new Date(calendar.getTimeInMillis());
	}

	public static Date getToday() {
		Calendar calendar = Calendar.getInstance();
		calendar = DateUtils.truncate(calendar, DAY_OF_MONTH);
		return new Date(calendar.getTimeInMillis());
		// return new Date(calendar.get(YEAR), calendar.get(MONTH),
		// calendar.get(DAY_OF_MONTH));
	}

	public static Connection connect() throws ClassNotFoundException, SQLException, AonSQLException {
		// first of all load JDBC driver
		Class.forName("com.mysql.jdbc.Driver");

		String dbHost = getDbHost();
		String dbPort = getDbPort();
		String dbName = getDbName();
		String dbUser = getDbUser();
		String dbPasswd = getDbPasswd();
		String dbUseSSL = getDbUseSSL();
		String dbTimeZone = getDbTimeZone();

		Properties properties = new Properties();
		properties.setProperty("user", dbUser);
		properties.setProperty("password", dbPasswd);
		properties.setProperty("useSSL", dbUseSSL);
		properties.setProperty("serverTimezone", dbTimeZone);
		String url = String.format("jdbc:mysql://%s:%s", dbHost, dbPort, dbName);
		Connection connection = DriverManager.getConnection(url, properties);

		ResultSet rs = connection.createStatement().executeQuery("SHOW DATABASES");
		while (rs.next()) {
			if (rs.getString(1).startsWith(dbName)) {
				connection.createStatement().execute("use " + rs.getString(1));
				new VersionManager().uptodateDatabase(connection);
				System.out.println("use " + rs.getString(1));
				return connection;
			}
		}

		VersionManager versionManager = new VersionManager();
		versionManager.createDatabase(connection, dbName);
		versionManager.uptodateDatabase(connection);

		return connection;
	}

	public static final DomainRecord newDomain(AONContext aonContext) {
		return aonContext.getDslContext().insertInto(DOMAIN)
				.set(DOMAIN.NAME, java.util.UUID.randomUUID().toString()).set(DOMAIN.OWNER, "")
				.set(DOMAIN.DESCRIPTION, "").returning().fetchOne();
	}

	public static final DomainRecord newDomain(AONContext aonContext, int parent) {
		return aonContext.getDslContext().insertInto(DOMAIN)
				.set(DOMAIN.NAME, java.util.UUID.randomUUID().toString()).set(DOMAIN.OWNER, "")
				.set(DOMAIN.PARENT, parent).set(DOMAIN.DESCRIPTION, "").returning().fetchOne();
	}

	public static final AgreementRecord newAgreement(AONContext aonContext) {
		// Add a domain, with a generated ID
		DomainRecord domain = newDomain(aonContext);

		return aonContext.getDslContext().insertInto(AGREEMENT).set(AGREEMENT.DOMAIN, domain.getId())
				.set(AGREEMENT.DESCRIPTION, "").returning().fetchOne();
	}

	public static AgreementRecord getAgreement(AONContext aonContext, int agreementLevel) {
		return aonContext.getDslContext().select().from(AGREEMENT).join(AGREEMENT_LEVEL)
				.on(AGREEMENT.ID.eq(AGREEMENT_LEVEL.AGREEMENT)).where(AGREEMENT_LEVEL.ID.eq(agreementLevel))
				.fetchOneInto(AGREEMENT);

	}

	public static AgreementRecord getAgreement(AONContext aonContext, String description) {
		return aonContext.getDslContext().select().from(AGREEMENT)
				.where(AGREEMENT.DESCRIPTION.eq(description))
				.fetchOneInto(AGREEMENT);

	}

	public static final AgreementLevelCategoryRecord newAgreementCategory(AONContext aonContext,
			AgreementRecord agreement, String levelDescription, String categoryDescription) {
		AgreementLevelRecord level = aonContext.getDslContext().insertInto(AGREEMENT_LEVEL)
				.set(AGREEMENT_LEVEL.DOMAIN, agreement.getDomain()).set(AGREEMENT_LEVEL.AGREEMENT, agreement.getId())
				.set(AGREEMENT_LEVEL.DESCRIPTION, levelDescription).returning().fetchOne();

		return aonContext.getDslContext().insertInto(AGREEMENT_LEVEL_CATEGORY)
				.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, agreement.getDomain())
				.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION, categoryDescription)
				.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, level.getId()).returning().fetchOne();

	}

	public static final AgreementLevelCategoryRecord newAgreementCategory(AONContext aonContext,
			AgreementRecord agreement) {
		return newAgreementCategory(aonContext, agreement, "", "");
	}

	public static final AgreementLevelCategoryRecord newAgreement(AONContext aonContext, Extra extras[]) {
		return newAgreement(aonContext, extras, Collections.emptyMap());
	}

	public static final AgreementLevelCategoryRecord newAgreement(AONContext aonContext, Extra extras[],
			Map<String, String> datas) {
		return newAgreement(aonContext, extras, new Payment[] {}, datas);
	}

	public static final AgreementLevelCategoryRecord newAgreement(AONContext aonContext, Extra extras[],
			Payment payments[]) {
		return newAgreement(aonContext, extras, payments, Collections.emptyMap());
	}

	public static final AgreementLevelCategoryRecord newAgreement(AONContext aonContext, Extra extras[],
			Payment payments[], Map<String, String> datas) {
		return aonContext.getDslContext().transactionResult(new TransactionalCallable<AgreementLevelCategoryRecord>() {
			@Override
			public AgreementLevelCategoryRecord run(Configuration arg0) throws Exception {
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

	public static void addExtras(AONContext aonContext, AgreementRecord agreement, Date startDate, Extra extras[]) {
		for (Extra extra : extras) {
			AgreementPaymentRecord payment = aonContext.getDslContext().insertInto(AGREEMENT_PAYMENT)
					.set(AGREEMENT_PAYMENT.DOMAIN, agreement.getDomain())
					.set(AGREEMENT_PAYMENT.AGREEMENT, agreement.getId())
					.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, extra.concept)
					.set(AGREEMENT_PAYMENT.MONTH, (byte) extra.month.ordinal())
					.set(AGREEMENT_PAYMENT.TYPE, (byte) PaymentType.CRA_0004.ordinal())
					.set(AGREEMENT_PAYMENT.START_DATE, startDate)
					.set(AGREEMENT_PAYMENT.EXPRESSION, extra.expression)
					.set(AGREEMENT_PAYMENT.DESCRIPTION, extra.expression)
					.set(AGREEMENT_PAYMENT.IRPF_EXPRESSION, PAYMENT.getName())
					.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte) EXTRA.ordinal())
					.set(AGREEMENT_PAYMENT.QUOTE_EXPRESSION, extra.quoteExpression == UNSET ? String.format("%s/12", PAYMENT.getName()): extra.quoteExpression).returning()
					.fetchOne();

			aonContext.getDslContext().insertInto(AGREEMENT_EXTRA).set(AGREEMENT_EXTRA.DOMAIN, agreement.getDomain())
					.set(AGREEMENT_EXTRA.AGREEMENT, agreement.getId())
					.set(AGREEMENT_EXTRA.AGREEMENT_PAYMENT, payment.getId())
					.set(AGREEMENT_EXTRA.START_DATE, extra.start).set(AGREEMENT_EXTRA.END_DATE, extra.end)
					.set(AGREEMENT_EXTRA.ISSUE_DATE, extra.issue).returning().fetchOne();
		}

	}

	public static AgreementExtraRecord addExtra(AONContext aonContext, AgreementPaymentRecord payment, Date startDate,
			Extra extra) {

		aonContext.getDslContext().update(AGREEMENT_PAYMENT).set(AGREEMENT_PAYMENT.QUOTE_EXPRESSION, "_P/12")
				.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte) EXTRA.ordinal())
				.set(AGREEMENT_PAYMENT.MONTH, (byte) extra.month.ordinal())
				.where(AGREEMENT_PAYMENT.ID.eq(payment.getId())).execute();

		return aonContext.getDslContext().insertInto(AGREEMENT_EXTRA).set(AGREEMENT_EXTRA.DOMAIN, payment.getDomain())
				.set(AGREEMENT_EXTRA.AGREEMENT, payment.getAgreement())
				.set(AGREEMENT_EXTRA.AGREEMENT_PAYMENT, payment.getId()).set(AGREEMENT_EXTRA.START_DATE, extra.start)
				.set(AGREEMENT_EXTRA.END_DATE, extra.end).set(AGREEMENT_EXTRA.ISSUE_DATE, extra.issue).returning()
				.fetchOne();
	}

	public static AgreementExtraRecord getExtra(AONContext aonContext, int agreement, String issueDate) {

		return aonContext.getDslContext().select().from(AGREEMENT_EXTRA).where(AGREEMENT_EXTRA.AGREEMENT.eq(agreement))
				.and(AGREEMENT_EXTRA.ISSUE_DATE.eq(issueDate)).fetchOneInto(AGREEMENT_EXTRA);
	}

	public static AgreementPaymentRecord [] getAgreementPayments(AONContext aonContext, int agreement) {

		Record records [] = aonContext.getDslContext().select().from(AGREEMENT_PAYMENT).where(AGREEMENT_PAYMENT.AGREEMENT.eq(agreement))
				.fetchArray();
		AgreementPaymentRecord payments [] = new AgreementPaymentRecord [records.length];
		for ( int i = 0; i < records.length; i++ )
				payments[i] = (AgreementPaymentRecord)records[i];
		return payments;
	}

	public static PaymentConceptRecord getPaymentConcept(AONContext aonContext, int id) {
		return aonContext.getDslContext().select().from(PAYMENT_CONCEPT).where(PAYMENT_CONCEPT.ID.eq(id))
				.fetchOneInto(PAYMENT_CONCEPT);
	}

	public static void addPayments(AONContext aonContext, AgreementRecord agreement, Date startDate,
			Payment payments[]) {
		addPayments(aonContext, agreement.getDomain(), agreement, startDate, payments);
	}

	public static void addPayments(AONContext aonContext, int domainId, AgreementRecord agreement, Date startDate,
			Payment payments[]) {
		for (Payment payment : payments) {
			aonContext.getDslContext().insertInto(AGREEMENT_PAYMENT).set(AGREEMENT_PAYMENT.DOMAIN, domainId)
					.set(AGREEMENT_PAYMENT.AGREEMENT, agreement.getId())
					.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, payment.concept)
					.set(AGREEMENT_PAYMENT.EXPRESSION, payment.expression)
					.set(AGREEMENT_PAYMENT.START_DATE, startDate)
					.set(AGREEMENT_PAYMENT.MONTH, payment.month != null ? (byte) payment.month.ordinal() : null )
					.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte) payment.salary.ordinal()).returning().fetchOne();
		}
	}

	public static AgreementPaymentRecord addPayment(AONContext aonContext, AgreementRecord agreement, Date startDate,
			Payment payment) {
		return addPayment(aonContext, agreement.getDomain(), agreement, startDate, payment);
	}

	public static AgreementPaymentRecord addPayment(AONContext aonContext, int domainId, AgreementRecord agreement,
			Date startDate, Payment payment) {
		return aonContext.getDslContext().insertInto(AGREEMENT_PAYMENT).set(AGREEMENT_PAYMENT.DOMAIN, domainId)
				.set(AGREEMENT_PAYMENT.AGREEMENT, agreement.getId())
				.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, payment.concept)
				.set(AGREEMENT_PAYMENT.EXPRESSION, payment.expression).set(AGREEMENT_PAYMENT.START_DATE, startDate)
				.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte) payment.salary.ordinal()).returning().fetchOne();
	}

	public static void addData(AONContext aonContext, AgreementRecord agreement, Date startDate,
			Map<String, String> datas) {
		for (Map.Entry<String, String> data : datas.entrySet()) {
			aonContext.getDslContext()
			.insertInto(AGREEMENT_DATA)
			.set(AGREEMENT_DATA.DOMAIN, agreement.getDomain())
			.set(AGREEMENT_DATA.AGREEMENT, agreement.getId())
			.set(AGREEMENT_DATA.START_DATE, startDate)
			.set(AGREEMENT_DATA.NAME, data.getKey())
			.set(AGREEMENT_DATA.EXPRESSION, data.getValue()).execute();

		}
	}

	public static void addData(AONContext aonContext, AgreementRecord agreement, Date startDate,
			Date endDate, Map<String, String> datas) {
		for (Map.Entry<String, String> data : datas.entrySet()) {
			aonContext.getDslContext()
			.insertInto(AGREEMENT_DATA).set(AGREEMENT_DATA.DOMAIN, agreement.getDomain())
			.set(AGREEMENT_DATA.AGREEMENT, agreement.getId())
			.set(AGREEMENT_DATA.START_DATE, startDate)
			.set(AGREEMENT_DATA.END_DATE, endDate)
			.set(AGREEMENT_DATA.NAME, data.getKey())
			.set(AGREEMENT_DATA.EXPRESSION, data.getValue()).execute();

		}
	}

	public ISQLContractSalaryCalculatorContext getExtraSalaryCalculatorContext(Connection connection,
			ContractRecord contract, AgreementExtraRecord extra, int year, Date chargeDate)
			throws SQLException, ExpressionException {

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		SQLExtraSalaryCalculatorContext ctx = new SQLExtraSalaryCalculatorContext(connection, extra.getId(), year,
				chargeDate, criteria);
		ctx.next();
		return ctx;

	}

	public ISQLContractSalaryCalculatorContext getExtraSalaryCalculatorContext(Connection connection,
			ContractRecord contract, AgreementExtraRecord extra, int year, Date endDate, Date chargeDate)
			throws SQLException, ExpressionException {

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		SQLExtraSalaryCalculatorContext ctx = new SQLExtraSalaryCalculatorContext(connection, extra.getId(), year,
				endDate, chargeDate, criteria);
		ctx.next();
		return ctx;

	}

	protected ISQLContractSalaryCalculatorContext getSQLContractSettleContext(Connection connection, Date contractStart,
			Date contractEnd, ContractRecord contract) throws SQLException, ExpressionException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		ISQLContractSalaryCalculatorContext ctx = new SQLContractSettleCalculatorContext(connection, contractStart,
				contractEnd, contractEnd, criteria);
		ctx.next();
		return ctx;
	}

	protected ISQLContractSalaryCalculatorContext getSQLContractSettleContext(Connection connection, Date contractStart,
			ContractRecord contract) throws SQLException, ExpressionException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		ISQLContractSalaryCalculatorContext ctx = new SQLContractSettleCalculatorContext(connection, contractStart,
				getToday(), getToday(), criteria);
		ctx.next();
		return ctx;
	}

	public static final ContractRecord newContract(AONContext aonContext, Date startDate, Map<String, String> data,
			AgreementLevelCategoryRecord category) {
		return newContract(aonContext, startDate, data, new String[0], new String[0], category);
	}

	public static final ContractRecord newContract(AONContext aonContext, Date startDate, Map<String, String> data) {
		return newContract(aonContext, startDate, data, new String[0], new String[0], null);
	}

	public static final ContractRecord newContract(AONContext aonContext, String[] payments, String[] deductions) {
		return newContract(aonContext, payments, deductions, null);
	}

	public static final ContractRecord newContract(AONContext aonContext, Date startDate, String[] payments, String[] deductions,
			AgreementLevelCategoryRecord category) {
		return newContract(aonContext, startDate, Collections.emptyMap(), payments, deductions, category);
	}

	public static final ContractRecord newContract(AONContext aonContext, String[] payments, String[] deductions,
			AgreementLevelCategoryRecord category) {
		Calendar calendar = Calendar.getInstance();
		// Be care that the first day of the year has value 1.
		calendar.set(DAY_OF_YEAR, 1);
		Date startDate = new Date(calendar.getTimeInMillis());
		return newContract(aonContext, startDate, Collections.emptyMap(), payments, deductions, category);
	}

	public static final ContractRecord newContract(AONContext aonContext, Date startDate, Map<String, String> data,
			String[] payments, String[] deductions, AgreementLevelCategoryRecord category) {
		return newContract(aonContext, SSRegimeType.GENERAL, CCCType.PRINCIPAL, startDate, null, data, payments,
				deductions, category, null);
	}

	public static final ContractRecord newContract(AONContext aonContext, Date startDate, Map<String, String> data,
			String[] payments, String[] deductions, AgreementLevelCategoryRecord category, CalendarRecord calendar) {
		return newContract(aonContext, SSRegimeType.GENERAL, CCCType.PRINCIPAL, startDate, null, data, payments,
				deductions, category, calendar);
	}

	public static final ContractRecord newContract(AONContext aonContext, Date startDate, Date endDate,
			Map<String, String> data, String[] payments, String[] deductions, AgreementLevelCategoryRecord category) {
		return newContract(aonContext, SSRegimeType.GENERAL, CCCType.PRINCIPAL, startDate, endDate, data, payments,
				deductions, category, null);
	}

	public static final ContractRecord newContract(AONContext aonContext, SSRegimeType ssRegimeType, CCCType cccType,
			Date startDate, Map<String, String> data, String[] payments, String[] deductions,
			AgreementLevelCategoryRecord category) {
		return newContract(aonContext, ssRegimeType, cccType, startDate, null, data, payments, deductions, category, null);
	}
	
	public static final ContractRecord newContract(AONContext aonContext, SSRegimeType ssRegimeType, CCCType cccType,
			Date startDate, Date endDate, Map<String, String> data, String[] payments, String[] deductions,
			AgreementLevelCategoryRecord category) {
		return newContract(aonContext, ssRegimeType, cccType, startDate, endDate, data, payments, deductions, category, null);
	}

	public static final ContractRecord newContract(AONContext aonContext, SSRegimeType ssRegimeType, CCCType cccType,
			Date startDate, Date endDate, Map<String, String> data, String[] payments, String[] deductions,
			AgreementLevelCategoryRecord category, int domainId, int personId, int workplaceId, int enterpriseCccId,
			int enterpriseActivityId) {
		ContractRecord contract = aonContext.getDslContext().insertInto(CONTRACT).set(CONTRACT.DOMAIN, domainId)
				.set(CONTRACT.PERSON, personId).set(CONTRACT.WORKPLACE, workplaceId).set(CONTRACT.START_DATE, startDate)
				.set(CONTRACT.SENIORITY_DATE, startDate).set(CONTRACT.END_DATE, endDate)
				.set(CONTRACT.ENTERPRISE_CCC, enterpriseCccId).set(CONTRACT.ENTERPRISE_ACTIVITY, enterpriseActivityId)
				.set(CONTRACT.AGREEMENT_LEVEL, category != null ? category.getAgreementLevel() : null).returning()
//				.set(CONTRACT.AGREEMENT_LEVEL_CATEGORY, category != null ? category.getId() : null).returning()
				.fetchOne();

		for (int i = 0; i < payments.length; i++) {
			String payment = payments[i];
			PaymentConceptRecord concept = aonContext.getDslContext().insertInto(PAYMENT_CONCEPT)
					.set(PAYMENT_CONCEPT.DOMAIN, domainId).set(PAYMENT_CONCEPT.CODE, String.format("P_%d", i))
					.set(PAYMENT_CONCEPT.TYPE, (byte) PaymentType.CRA_0000.ordinal())
					.set(PAYMENT_CONCEPT.DESCRIPTION, payment).set(PAYMENT_CONCEPT.EXPRESSION, payment)
					.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, PAYMENT.getName())
					.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, PAYMENT.getName()).returning().fetchOne();

			aonContext.getDslContext().insertInto(CONTRACT_PAYMENT).set(CONTRACT_PAYMENT.DOMAIN, domainId)
					.set(CONTRACT_PAYMENT.CONTRACT, contract.getId()).set(CONTRACT_PAYMENT.START_DATE, startDate)
					.set(CONTRACT_PAYMENT.PAYMENT_CONCEPT, concept.getId())
					.set(CONTRACT_PAYMENT.SALARY_TYPE, (byte) SalaryType.SALARY.ordinal()).execute();
		}

		for (String deduction : deductions) {
			DeductionType type = DeductionType.COMMON_CONTINGENCY;
			if (deduction.contains("BASE_IRPF"))
				type = DeductionType.IRPF;
			else if (deduction.contains("BASE_ESTR"))
				type = DeductionType.STRUCTURAL_OVERTIME;
			else if (deduction.contains("BASE_NESTR"))
				type = DeductionType.NON_STRUCTURAL_OVERTIME;

			aonContext.getDslContext().insertInto(CONTRACT_DEDUCTION).set(CONTRACT_DEDUCTION.DOMAIN, domainId)
					.set(CONTRACT_DEDUCTION.CONTRACT, contract.getId()).set(CONTRACT_DEDUCTION.START_DATE, startDate)
					.set(CONTRACT_DEDUCTION.DESCRIPTION, deduction).set(CONTRACT_DEDUCTION.EXPRESSION, deduction)
					.set(CONTRACT_DEDUCTION.TYPE, (byte) type.ordinal()).execute();
		}

		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), data);

		return contract;
	}

	public static final EnterpriseActivityRecord newEnterpriseActivity(AONContext aonContext, int domainId, int scopeId,
			SSRegimeType ssRegimeType) {

		RegistryRecord enterprise = aonContext.getDslContext().insertInto(REGISTRY).set(REGISTRY.DOMAIN, domainId)
				.set(REGISTRY.NAME, "").set(REGISTRY.ALIAS, "").set(REGISTRY.DOCUMENT, "")
				.set(REGISTRY.DOCUMENT_COUNTRY, "").set(REGISTRY.DOCUMENT_TYPE, (byte) DocumentType.OTHER.ordinal())
				.set(REGISTRY.NATIONALITY, "").set(REGISTRY.TYPE, (byte) RegistryType.LEGAL.ordinal()).returning()
				.fetchOne();

		aonContext.getDslContext().insertInto(ENTERPRISE).set(ENTERPRISE.DOMAIN, domainId)
				.set(ENTERPRISE.REGISTRY, enterprise.getId()).set(ENTERPRISE.SCOPE, scopeId).execute();

		EnterpriseActivityRecord enterpriseActivity = aonContext.getDslContext().insertInto(ENTERPRISE_ACTIVITY)
				.set(ENTERPRISE_ACTIVITY.DOMAIN, domainId).set(ENTERPRISE_ACTIVITY.ENTERPRISE, enterprise.getId())
				.set(ENTERPRISE_ACTIVITY.DESCRIPTION, "").set(ENTERPRISE_ACTIVITY.TYPE, (byte) ssRegimeType.ordinal())
				.returning().fetchOne();

		return enterpriseActivity;

	}

	public static final EnterpriseCccRecord newEnterpriseCcc(AONContext aonContext, int domainId, int scopeId,
			int enterpriseActivityId, CCCType cccType, String ccc) {
		EnterpriseCccRecord enterpriseCcc = aonContext.getDslContext().insertInto(ENTERPRISE_CCC)
				.set(ENTERPRISE_CCC.DOMAIN, domainId).set(ENTERPRISE_CCC.TYPE, (byte) cccType.ordinal())
				.set(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY, enterpriseActivityId).set(ENTERPRISE_CCC.CCC, ccc).returning()
				.fetchOne();

		return enterpriseCcc;
	}

	public static final WorkplaceRecord newWorkplace(AONContext aonContext, int domainId, int scopeId,
			int enterpriseId) {
		return newWorkplace(aonContext, domainId, scopeId, enterpriseId, null);
	}

	public static final WorkplaceRecord newWorkplace(AONContext aonContext, int domainId, int scopeId,
			int enterpriseId, Integer calendarId) {

		RaddressRecord raddress = aonContext.getDslContext().insertInto(RADDRESS).set(RADDRESS.DOMAIN, domainId)
				.set(RADDRESS.REGISTRY, enterpriseId).set(RADDRESS.TYPE, (byte) AddressType.MAIN.ordinal()).returning()
				.fetchOne();

		WorkplaceRecord workplace = aonContext.getDslContext().insertInto(WORKPLACE).set(WORKPLACE.DOMAIN, domainId)
				.set(WORKPLACE.ENTERPRISE, enterpriseId).set(WORKPLACE.ACTIVE, (byte) 1)
				.set(WORKPLACE.ECONOMICAGREEMENT, (byte) Administration.COMMON_TERRITORY.ordinal())
				.set(WORKPLACE.DESCRIPTION, "").set(WORKPLACE.ADDRESS, raddress.getId()).set(WORKPLACE.SCOPE, scopeId)
				.returning().fetchOne();

		aonContext.getDslContext().insertInto(PAYROLL_WORKPLACE)
				.set(PAYROLL_WORKPLACE.DOMAIN, domainId)
				.set(PAYROLL_WORKPLACE.WORKPLACE, workplace.getId())
				.set(PAYROLL_WORKPLACE.CALENDAR, calendarId)
				.execute();
		return workplace;
	}

	public static CalendarRecord newCalendar(AONContext aonContext,
			Integer domainId,
			Integer holidayId,
			Double mondayHours,
			Double tuesdayHours,
			Double wednesdayHours,
			Double thursdayHours,
			Double fridayHours,
			Double saturdayHours,
			Double sundayHours) {
		return
		aonContext.getDslContext()
		.insertInto(CALENDAR)

		.set(CALENDAR.DOMAIN, domainId)
		.set(CALENDAR.HOLIDAY, holidayId)

		.set(CALENDAR.MONDAY_HOURS, mondayHours)
		.set(CALENDAR.TUESDAY_HOURS, tuesdayHours)
		.set(CALENDAR.TUESDAY_HOURS, wednesdayHours)
		.set(CALENDAR.THURSDAY_HOURS, thursdayHours)
		.set(CALENDAR.FRIDAY_HOURS, fridayHours)
		.set(CALENDAR.SATURDAY_HOURS, saturdayHours)
		.set(CALENDAR.SUNDAY_HOURS, sundayHours)

		.set(CALENDAR.MONDAY, mondayHours != null ? (byte)0 : (byte)1 )
		.set(CALENDAR.TUESDAY, tuesdayHours != null ? (byte)0 : (byte)1 )
		.set(CALENDAR.TUESDAY, wednesdayHours != null ? (byte)0 : (byte)1 )
		.set(CALENDAR.THURSDAY, thursdayHours != null ? (byte)0 : (byte)1 )
		.set(CALENDAR.FRIDAY, fridayHours != null ? (byte)0 : (byte)1 )
		.set(CALENDAR.SATURDAY, saturdayHours != null ? (byte)0 : (byte)1 )
		.set(CALENDAR.SUNDAY, sundayHours != null ? (byte)0 : (byte)1 )

		.returning()
		.fetchOne()
		;

	}

	public static HolidayRecord newHoliday(AONContext aonContext, Integer domainId, Integer parentId, Date ...holidays) {
		HolidayRecord holidayRecord =

		aonContext.getDslContext()
		.insertInto(HOLIDAY)
		.set(HOLIDAY.DOMAIN, domainId)
		.set(HOLIDAY.HOLIDAY_, parentId)
		.returning()
		.fetchOne()
		;


		for (Date holiday : holidays)
			aonContext.getDslContext()
			.insertInto(HOLIDAY_DETAIL)
			.set(HOLIDAY_DETAIL.DATE, holiday)
			.set(HOLIDAY_DETAIL.HOLIDAY, holidayRecord.getId())
			.set(HOLIDAY_DETAIL.DOMAIN, holidayRecord.getDomain())
			.execute()
			;

		return holidayRecord;

	}



	public static final RegistryRecord newPerson(AONContext aonContext, int domainId, String document) {
		RegistryRecord person = aonContext.getDslContext().insertInto(REGISTRY).set(REGISTRY.DOMAIN, domainId)
				.set(REGISTRY.NAME, "").set(REGISTRY.ALIAS, "").set(REGISTRY.DOCUMENT, document)
				.set(REGISTRY.DOCUMENT_COUNTRY, "").set(REGISTRY.DOCUMENT_TYPE, (byte) DocumentType.OTHER.ordinal())
				.set(REGISTRY.NATIONALITY, "").set(REGISTRY.TYPE, (byte) RegistryType.NATURAL.ordinal()).returning()
				.fetchOne();

		aonContext.getDslContext().insertInto(PERSON).set(PERSON.DOMAIN, domainId).set(PERSON.REGISTRY, person.getId())
				.set(PERSON.NAME, "").set(PERSON.FIRST_SURNAME, "").set(PERSON.SECOND_SURNAME, "")
				// .set(PERSON.BIRTH_DATE, null)
				.set(PERSON.SOCIAL_SECURITY_NUM, "").set(PERSON.GENDER, (byte) Gender.UNKNOWN.ordinal())
				.set(PERSON.MARITAL_STATUS, (byte) MaritalStatus.UNKNOWN.ordinal()).execute();
		return person;
	}

	public static ScopeRecord newScope(AONContext aonContext, int domainId) {
		ScopeRecord scope = aonContext.getDslContext().insertInto(SCOPE).set(SCOPE.DOMAIN, domainId)
				.set(SCOPE.DESCRIPTION, "").returning().fetchOne();
		return scope;
	}

	public static final ContractRecord newContract(AONContext aonContext, SSRegimeType ssRegimeType, CCCType cccType,
			Date startDate, Date endDate, Map<String, String> data, String[] payments, String[] deductions,
			AgreementLevelCategoryRecord category, CalendarRecord calendar) {
		return aonContext.getDslContext().transactionResult(new TransactionalCallable<ContractRecord>() {
			@Override
			public ContractRecord run(Configuration configuration) throws Exception {
				// Add a domain, with a generated ID
				DomainRecord domain = aonContext.getDslContext().insertInto(DOMAIN)
						.set(DOMAIN.NAME, java.util.UUID.randomUUID().toString()).set(DOMAIN.OWNER, "")
						.set(DOMAIN.DESCRIPTION, "").returning().fetchOne();

				ScopeRecord scope = newScope(aonContext, domain.getId());

				EnterpriseActivityRecord enterpriseActivity = newEnterpriseActivity(aonContext, domain.getId(),
						scope.getId(), ssRegimeType);

				EnterpriseCccRecord enterpriseCcc = newEnterpriseCcc(aonContext, domain.getId(), scope.getId(),
						enterpriseActivity.getId(), cccType, null);

				WorkplaceRecord workplace = newWorkplace(aonContext, domain.getId(), scope.getId(),
						enterpriseActivity.getEnterprise(), calendar != null ? calendar.getId() : null );

				RegistryRecord person = newPerson(aonContext, domain.getId(), "");

				return newContract(aonContext, ssRegimeType, cccType, startDate, endDate, data, payments, deductions,
						category, domain.getId(), person.getId(), workplace.getId(), enterpriseCcc.getId(),
						enterpriseActivity.getId());
			}
		});

	}


	public static final void addData(AONContext aonContext, ContractRecord contract, Date startDate, Date endDate,
			Map<String, String> datas) {
		for (Map.Entry<String, String> data : datas.entrySet()) {

			aonContext.getDslContext().delete(CONTRACT_DATA).where(CONTRACT_DATA.CONTRACT.eq(contract.getId()))
					.and(CONTRACT_DATA.NAME.eq(data.getKey())).and(CONTRACT_DATA.START_DATE.eq(startDate)).execute();

			aonContext.getDslContext().insertInto(CONTRACT_DATA).set(CONTRACT_DATA.DOMAIN, contract.getDomain())
					.set(CONTRACT_DATA.CONTRACT, contract.getId()).set(CONTRACT_DATA.START_DATE, startDate)
					.set(CONTRACT_DATA.END_DATE, endDate).set(CONTRACT_DATA.NAME, data.getKey())
					.set(CONTRACT_DATA.EXPRESSION, data.getValue()).execute();

		}
	}

	public static final void setData(AONContext aonContext, ContractRecord contract,String name, String expression) {
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), name, expression);
	}

	public static final void addData(AONContext aonContext, ContractRecord contract, Date startDate, Date endDate,
			String name, String expression) {
		aonContext.getDslContext().insertInto(CONTRACT_DATA).set(CONTRACT_DATA.DOMAIN, contract.getDomain())
				.set(CONTRACT_DATA.CONTRACT, contract.getId()).set(CONTRACT_DATA.START_DATE, startDate)
				.set(CONTRACT_DATA.END_DATE, endDate).set(CONTRACT_DATA.NAME, name)
				.set(CONTRACT_DATA.EXPRESSION, expression).execute();
	}

	public static final void addData(AONContext aonContext, ContractRecord contract, Date startDate, Date endDate,
			ContextVariable variable, String expression) {
		addData(aonContext, contract, startDate, endDate, variable.getName(), expression);
	}

	public static final void addData(AONContext aonContext, ContractRecord contract, Date startDate, Date endDate,
			ContextVariable variable, Double value) {
		addData(aonContext, contract, startDate, endDate, variable.getName(), String.format(Locale.US,"%f", value));
	}

	public static final void setData(AONContext aonContext, AgreementLevelCategoryRecord category, String name, String expression) {

		aonContext.getDslContext()
		.update(AGREEMENT_DATA)
					.set(AGREEMENT_DATA.EXPRESSION, expression)
					.where(AGREEMENT_DATA.NAME.eq(name))
					.execute();
	}
	public static final void cleanData(AONContext aonContext, ContractRecord contract,String name) {
		aonContext.getDslContext().delete(CONTRACT_DATA).where(CONTRACT_DATA.CONTRACT.eq(contract.getId()))
		.and(CONTRACT_DATA.NAME.eq(name)).execute();

	}
	

	public static final void addData(AONContext aonContext, AgreementLevelCategoryRecord category, Date startDate,
			Date endDate, Map<String, String> datas) {
		for (Map.Entry<String, String> data : datas.entrySet()) {
			aonContext.getDslContext().insertInto(AGREEMENT_LEVEL_DATA)
					.set(AGREEMENT_LEVEL_DATA.DOMAIN, category.getDomain())
					.set(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL, category.getAgreementLevel())
					.set(AGREEMENT_LEVEL_DATA.START_DATE, startDate).set(AGREEMENT_LEVEL_DATA.END_DATE, endDate)
					.set(AGREEMENT_LEVEL_DATA.NAME, data.getKey()).set(AGREEMENT_LEVEL_DATA.EXPRESSION, data.getValue())
					.execute();

		}
	}

	public static final void addData(AONContext aonContext, Integer domain, AgreementLevelCategoryRecord category,
			Date startDate, Date endDate, Map<String, String> datas) {
		for (Map.Entry<String, String> data : datas.entrySet()) {
			aonContext.getDslContext().insertInto(AGREEMENT_LEVEL_DATA).set(AGREEMENT_LEVEL_DATA.DOMAIN, domain)
					.set(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL, category.getAgreementLevel())
					.set(AGREEMENT_LEVEL_DATA.START_DATE, startDate).set(AGREEMENT_LEVEL_DATA.END_DATE, endDate)
					.set(AGREEMENT_LEVEL_DATA.NAME, data.getKey()).set(AGREEMENT_LEVEL_DATA.EXPRESSION, data.getValue())
					.execute();

		}
	}

	public static final void addData(AONContext aonContext, Integer domain, AgreementRecord agreement,
			Date startDate, Date endDate, Map<String, String> datas) {
		for (Map.Entry<String, String> data : datas.entrySet()) {
			aonContext.getDslContext().insertInto(AGREEMENT_DATA)
					.set(AGREEMENT_DATA.DOMAIN, domain)
					.set(AGREEMENT_DATA.AGREEMENT, agreement.getId())
					.set(AGREEMENT_DATA.START_DATE, startDate)
					.set(AGREEMENT_DATA.END_DATE, endDate)
					.set(AGREEMENT_DATA.NAME, data.getKey())
					.set(AGREEMENT_DATA.EXPRESSION, data.getValue())
					.execute();

		}
	}

	public static final PaymentConceptRecord addConcept(AONContext aonContext, String code) {
		DomainRecord domain = newDomain(aonContext);
		return addConcept(aonContext, code, PaymentType.CRA_0001);
	}

	public static final PaymentConceptRecord addConcept(AONContext aonContext, String code, PaymentType type) {
		DomainRecord domain = newDomain(aonContext);
		return aonContext.getDslContext()
				.insertInto(PAYMENT_CONCEPT)
				.set(PAYMENT_CONCEPT.DOMAIN, domain.getId())
				.set(PAYMENT_CONCEPT.CODE, code)
				.set(PAYMENT_CONCEPT.TYPE, (byte) type.ordinal())
				.set(PAYMENT_CONCEPT.DESCRIPTION, code)
				.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, "_P")
				.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, "_P").returning()
				.fetchOne();

	}


	public static final void addPayment(AONContext aonContext, ContractRecord contract, PaymentConceptRecord concept,
			String expression) {
		addPayment(aonContext, contract, concept, expression, "_P");
	}

	public static final void addPayment(AONContext aonContext, ContractRecord contract, PaymentConceptRecord concept,
			String expression, String quoteExpression) {
		addPayment(aonContext, contract, concept, expression, quoteExpression, PaymentType.CRA_0001);
	}

	public static final void addPayment(AONContext aonContext, ContractRecord contract, PaymentConceptRecord concept,
			String expression, String quoteExpression, Byte month) {
		addPayment(aonContext, contract, contract.getStartDate(), contract.getEndDate(), concept, null, expression, "_P", quoteExpression, PaymentType.CRA_0004, month);
	}


	public static final void addPayment(AONContext aonContext, ContractRecord contract, PaymentConceptRecord concept,
			String expression, String quoteExpression, PaymentType type) {
		addPayment(aonContext, contract, concept, null, expression, "_P", quoteExpression, type);
	}

	public static final void addPayment(AONContext aonContext, ContractRecord contract, Date startDate, Date endDate,
			PaymentConceptRecord concept, String expression) {
		addPayment(aonContext, contract, startDate, endDate, concept, expression, expression, "_P", "_P", PaymentType.CRA_0001);
	}

	public static final void addPayment(AONContext aonContext, ContractRecord contract, PaymentConceptRecord concept,
			String description, String expression, String irpfExpression, String quoteExpression, PaymentType type) {
		addPayment(aonContext, contract, contract.getStartDate(), contract.getEndDate(), concept, description, expression, irpfExpression, quoteExpression, type);
	}

	public static final void addPayment(AONContext aonContext,
			ContractRecord contract,
			Date startDate,
			Date endDate,
			PaymentConceptRecord concept,
			String description,
			String expression,
			String irpfExpression,
			String quoteExpression,
			PaymentType type) {
		aonContext.getDslContext().insertInto(CONTRACT_PAYMENT)
				.set(CONTRACT_PAYMENT.DOMAIN, contract.getDomain())
				.set(CONTRACT_PAYMENT.PAYMENT_CONCEPT, concept.getId())
				.set(CONTRACT_PAYMENT.CONTRACT, contract.getId())
				.set(CONTRACT_PAYMENT.START_DATE, startDate)
				.set(CONTRACT_PAYMENT.END_DATE, endDate)
				.set(CONTRACT_PAYMENT.DESCRIPTION, description)
				.set(CONTRACT_PAYMENT.EXPRESSION, expression)
				.set(CONTRACT_PAYMENT.QUOTE_EXPRESSION, quoteExpression)
				.set(CONTRACT_PAYMENT.IRPF_EXPRESSION, irpfExpression)
				.set(CONTRACT_PAYMENT.TYPE, (byte) type.ordinal())
				.set(CONTRACT_PAYMENT.SALARY_TYPE, (byte) SalaryType.SALARY.ordinal()).execute();

	}

	public static final void addPayment(AONContext aonContext,
			ContractRecord contract,
			Date startDate,
			Date endDate,
			PaymentConceptRecord concept,
			String description,
			String expression,
			String irpfExpression,
			String quoteExpression,
			PaymentType type,
			Byte month) {
		aonContext.getDslContext().insertInto(CONTRACT_PAYMENT)
				.set(CONTRACT_PAYMENT.DOMAIN, contract.getDomain())
				.set(CONTRACT_PAYMENT.PAYMENT_CONCEPT, concept.getId())
				.set(CONTRACT_PAYMENT.CONTRACT, contract.getId())
				.set(CONTRACT_PAYMENT.START_DATE, startDate)
				.set(CONTRACT_PAYMENT.END_DATE, endDate)
				.set(CONTRACT_PAYMENT.DESCRIPTION, description)
				.set(CONTRACT_PAYMENT.EXPRESSION, expression)
				.set(CONTRACT_PAYMENT.QUOTE_EXPRESSION, quoteExpression)
				.set(CONTRACT_PAYMENT.IRPF_EXPRESSION, irpfExpression)
				.set(CONTRACT_PAYMENT.TYPE, type != null ? (byte) type.ordinal(): null)
				.set(CONTRACT_PAYMENT.SALARY_TYPE, (byte) SalaryType.SALARY.ordinal())
				.set(CONTRACT_PAYMENT.MONTH, month )
				.execute();

	}

	public static final void addPayment(AONContext aonContext, ContractRecord contract, Date startDate,
			String expression) {
		addPayment(aonContext, contract, startDate, contract.getEndDate(), expression, SalaryType.SALARY);
	}

	public static final void addPayment(AONContext aonContext, ContractRecord contract, Date startDate,Date endDate,
			String expression) {
		addPayment(aonContext, contract, startDate, endDate, expression, SalaryType.SALARY);
	}

	public static final void addPayment(AONContext aonContext, ContractRecord contract, Date startDate,
			String expression, SalaryType salaryType) {

		addPayment(aonContext, contract, null, expression, "_P", "_P", PaymentType.CRA_0001, salaryType);

	}

	public static final void addPayment(AONContext aonContext, ContractRecord contract, Date startDate,
			Date endDate, String expression, SalaryType salaryType) {

		addPayment(aonContext, contract, startDate, endDate, null, expression, "_P", "_P", PaymentType.CRA_0001, salaryType);

	}

	public static final void addPayment(AONContext aonContext, ContractRecord contract, String description,
			String expression, String irpfExpression, String quoteExpression, PaymentType type) {
		addPayment(aonContext, contract, description, expression, irpfExpression, quoteExpression, type, SalaryType.SALARY);
	}

	public static final void addPayment(AONContext aonContext, ContractRecord contract, String description,
			String expression, String irpfExpression, String quoteExpression, PaymentType type, SalaryType salaryType) {
		aonContext.getDslContext().insertInto(CONTRACT_PAYMENT).set(CONTRACT_PAYMENT.DOMAIN, contract.getDomain())
				.set(CONTRACT_PAYMENT.CONTRACT, contract.getId())
				.set(CONTRACT_PAYMENT.START_DATE, contract.getStartDate())
				.set(CONTRACT_PAYMENT.END_DATE, contract.getEndDate()).set(CONTRACT_PAYMENT.DESCRIPTION, description)
				.set(CONTRACT_PAYMENT.EXPRESSION, expression).set(CONTRACT_PAYMENT.QUOTE_EXPRESSION, quoteExpression)
				.set(CONTRACT_PAYMENT.IRPF_EXPRESSION, irpfExpression).set(CONTRACT_PAYMENT.TYPE, (byte) type.ordinal())
				.set(CONTRACT_PAYMENT.SALARY_TYPE, (byte) salaryType.ordinal()).execute();

	}

	public static final void addPayment(AONContext aonContext, ContractRecord contract, Date startDate, Date endDate, String description,
			String expression, String irpfExpression, String quoteExpression, PaymentType type, SalaryType salaryType) {
		aonContext.getDslContext().insertInto(CONTRACT_PAYMENT).set(CONTRACT_PAYMENT.DOMAIN, contract.getDomain())
				.set(CONTRACT_PAYMENT.CONTRACT, contract.getId())
				.set(CONTRACT_PAYMENT.START_DATE, startDate)
				.set(CONTRACT_PAYMENT.END_DATE, endDate)
				.set(CONTRACT_PAYMENT.DESCRIPTION, description)
				.set(CONTRACT_PAYMENT.EXPRESSION, expression)
				.set(CONTRACT_PAYMENT.QUOTE_EXPRESSION, quoteExpression)
				.set(CONTRACT_PAYMENT.IRPF_EXPRESSION, irpfExpression)
				.set(CONTRACT_PAYMENT.TYPE, (byte) type.ordinal())
				.set(CONTRACT_PAYMENT.SALARY_TYPE, (byte) salaryType.ordinal()).execute();

	}

	public static final ContractLeaveRecord addIT(AONContext aonContext, ContractRecord contract, LeaveType type, Date startDate,
			Date endDate, Double regulatoryBase) {
		return
		aonContext.getDslContext().insertInto(CONTRACT_LEAVE).set(CONTRACT_LEAVE.DOMAIN, contract.getDomain())
				.set(CONTRACT_LEAVE.CONTRACT, contract.getId())
				.set(CONTRACT_LEAVE.START_DATE, startDate)
				.set(CONTRACT_LEAVE.END_DATE, endDate)
				.set(CONTRACT_LEAVE.DAILY_REG_BASE, regulatoryBase)
				// .set(CONTRACT_LEAVE.DAILY_CGC_BASE, regulatoryBase)
				// .set(CONTRACT_LEAVE.DAILY_CGP_BASE, regulatoryBase)
				.set(CONTRACT_LEAVE.TYPE, (byte) type.ordinal())
				.set(CONTRACT_LEAVE.DISCHARGE_CAUSE, (byte) type.ordinal())
				.returning()
				.fetchOne();

	}

	public static final ContractLeaveRecord addIT(AONContext aonContext, ContractRecord contract, LeaveType type, Date startDate,
			Date endDate, Double regulatoryBase, Integer parent) {
		return 
		aonContext.getDslContext().insertInto(CONTRACT_LEAVE).set(CONTRACT_LEAVE.DOMAIN, contract.getDomain())
				.set(CONTRACT_LEAVE.CONTRACT, contract.getId())
				.set(CONTRACT_LEAVE.START_DATE, startDate)
				.set(CONTRACT_LEAVE.END_DATE, endDate)
				.set(CONTRACT_LEAVE.DAILY_REG_BASE, regulatoryBase)
				.set(CONTRACT_LEAVE.TYPE, (byte) type.ordinal())
				.set(CONTRACT_LEAVE.DISCHARGE_CAUSE, (byte) type.ordinal())
				.set(CONTRACT_LEAVE.PARENT, parent )
				.returning()
				.fetchOne();

	}

	public static void addPayment(AONContext aonContext, ContractRecord contract, String expression) {
		addPayment(aonContext, contract, expression, "_P");
	}

	public static void addPayment(AONContext aonContext, ContractRecord contract, String expression,
			String quoteExpression) {
		aonContext.getDslContext().insertInto(CONTRACT_PAYMENT)
				.set(CONTRACT_PAYMENT.DOMAIN, contract.getDomain())
				.set(CONTRACT_PAYMENT.CONTRACT, contract.getId())
				.set(CONTRACT_PAYMENT.START_DATE, contract.getStartDate())
				.set(CONTRACT_PAYMENT.END_DATE, contract.getEndDate())
				.set(CONTRACT_PAYMENT.EXPRESSION, expression)
				.set(CONTRACT_PAYMENT.IRPF_EXPRESSION, "_P")
				.set(CONTRACT_PAYMENT.QUOTE_EXPRESSION, quoteExpression)
				.set(CONTRACT_PAYMENT.TYPE, (byte) PaymentType.CRA_0001.ordinal())
				.set(CONTRACT_PAYMENT.SALARY_TYPE, (byte) SalaryType.SALARY.ordinal()).execute();

	}

	public static ContractEmbargoRecord addEmbargo(AONContext aonContext, ContractRecord contract, String expression) {
		return aonContext.getDslContext().insertInto(CONTRACT_EMBARGO)
				.set(CONTRACT_EMBARGO.DOMAIN, contract.getDomain())
				.set(CONTRACT_EMBARGO.CONTRACT, contract.getId())
				.set(CONTRACT_EMBARGO.START_DATE, contract.getStartDate())
				.set(CONTRACT_EMBARGO.END_DATE, contract.getEndDate())
				.set(CONTRACT_EMBARGO.EXPRESSION, expression)
				.returning().fetchOne();

	}

	public static ContractEmbargoRecord addEmbargo(AONContext aonContext, ContractRecord contract, String description, String expression) {
		return aonContext.getDslContext().insertInto(CONTRACT_EMBARGO)
				.set(CONTRACT_EMBARGO.DOMAIN, contract.getDomain())
				.set(CONTRACT_EMBARGO.CONTRACT, contract.getId())
				.set(CONTRACT_EMBARGO.START_DATE, contract.getStartDate())
				.set(CONTRACT_EMBARGO.END_DATE, contract.getEndDate())
				.set(CONTRACT_EMBARGO.DESCRIPTION, description)
				.set(CONTRACT_EMBARGO.EXPRESSION, expression)
				.returning().fetchOne();

	}

	public static final BonusConceptRecord addBonusConcept(AONContext aonContext, BonusType type, String expression) {
		DomainRecord domain = newDomain(aonContext);
		return addBonusConcept(aonContext, domain.getId(), type, expression);

	}

	public static final BonusConceptRecord addBonusConcept(AONContext aonContext, int domain, BonusType type,
			String expression) {
		return aonContext.getDslContext().insertInto(BONUS_CONCEPT).set(BONUS_CONCEPT.DOMAIN, domain)
				.set(BONUS_CONCEPT.TYPE, type != null ? (byte) type.ordinal() : null)
				.set(BONUS_CONCEPT.EXPRESSION, expression)
				.set(BONUS_CONCEPT.DESCRIPTION, type != null ? type.getName(new Locale("es", "ES")) : expression)
				.returning().fetchOne();
	}

	public static final ContractBonusRecord addBonus(AONContext aonContext, ContractRecord contract,
			BonusConceptRecord concept, String expression) {
		return aonContext.getDslContext().insertInto(CONTRACT_BONUS).set(CONTRACT_BONUS.DOMAIN, contract.getDomain())
				.set(CONTRACT_BONUS.BONUS_CONCEPT, concept.getId()).set(CONTRACT_BONUS.CONTRACT, contract.getId())
				.set(CONTRACT_BONUS.START_DATE, contract.getStartDate())
				.set(CONTRACT_BONUS.END_DATE, contract.getEndDate()).set(CONTRACT_BONUS.EXPRESSION, expression)
				.returning().fetchOne();

	}

	public static final ContractBonusRecord addBonus(AONContext aonContext, ContractRecord contract,
			Date startDate, BonusConceptRecord concept) {
		return aonContext.getDslContext().insertInto(CONTRACT_BONUS).set(CONTRACT_BONUS.DOMAIN, contract.getDomain())
				.set(CONTRACT_BONUS.BONUS_CONCEPT, concept.getId()).set(CONTRACT_BONUS.CONTRACT, contract.getId())
				.set(CONTRACT_BONUS.START_DATE, startDate)
				.set(CONTRACT_BONUS.END_DATE, contract.getEndDate())
				.returning().fetchOne();

	}

	public static int smartCalculateAndSave(Connection connection,
			ISQLContractSalaryCalculatorContext ctx) throws SalaryException {
		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
		RoundSalaryBuilder<ISalary> roundSalaryBuilder = new RoundSalaryBuilder<ISalary>(jooqSalaryBuilder,
				d -> Math.round(d*100.00)/100.00);

		new SmartContractSalaryCalculator<ISalary>(roundSalaryBuilder).calculate(ctx);
		return jooqSalaryBuilder.execute();
	}
	
	
	private static void shutUp() {
		PrintStream devnull = new PrintStream(new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				// TODO Auto-generated method stub
			}
		});
		System.setOut(devnull);
		System.setErr(devnull);
	}


}
