package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.Keys.FK_AGREEMENT_LEVEL_CATEGORY_AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.Keys.FK_AGREEMENT_LEVEL_DATA_AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.Keys.FK_CONTRACT_AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementData.AGREEMENT_DATA;
import static com.esferalia.aon.jooq.tables.AgreementExtra.AGREEMENT_EXTRA;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AgreementLevelCategory.AGREEMENT_LEVEL_CATEGORY;
import static com.esferalia.aon.jooq.tables.AgreementLevelData.AGREEMENT_LEVEL_DATA;
import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.jooq.tables.EnterpriseData.ENTERPRISE_DATA;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;
import static com.esferalia.aon.payroll.calculator.jooq.JooqCommon.getDefaultSettings;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ALL;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PRORATION;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.jooq.AggregateFunction;
import org.jooq.Condition;
import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.Identity;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record7;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Agreement.Level;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.jooq.tables.records.AgreementDataRecord;
import com.esferalia.aon.jooq.tables.records.AgreementExtraRecord;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.AgreementLevelDataRecord;
import com.esferalia.aon.jooq.tables.records.AgreementLevelRecord;
import com.esferalia.aon.jooq.tables.records.AgreementPaymentRecord;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;
import com.esferalia.aon.jooq.tables.records.ContractPaymentRecord;
import com.esferalia.aon.jooq.tables.records.PayrollWorkplaceRecord;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.watson.util.AonStringUtils;

public class JooqAgreement extends org.jooq.impl.AbstractKeys {

	private static final java.sql.Date SQL_FOREVER = (java.sql.Date) null;
	private static final Date EPOCH = new Date(0);
	private static final java.sql.Date SQL_EPOCH = new java.sql.Date(EPOCH.getTime());

	public static void moveAgreement2ParentDomain(Connection conn,
			Integer parentDomain, Integer id) throws SQLException {

		DSLContext dslContext = DSL.using(conn, getDefaultSettings());

		dslContext.update(AGREEMENT_DATA)
				.set(AGREEMENT_DATA.DOMAIN, parentDomain)
				.where(AGREEMENT_DATA.AGREEMENT.eq(id)).execute();

		dslContext.update(AGREEMENT_EXTRA)
				.set(AGREEMENT_EXTRA.DOMAIN, parentDomain)
				.where(AGREEMENT_EXTRA.AGREEMENT.eq(id)).execute();
		
		dslContext
				.update(AGREEMENT_LEVEL_DATA)
				.set(AGREEMENT_LEVEL_DATA.DOMAIN, parentDomain)
				.where(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL.in(dslContext
						.select(AGREEMENT_LEVEL.ID).from(AGREEMENT_LEVEL)
						.where(AGREEMENT_LEVEL.AGREEMENT.eq(id)))).execute();

		dslContext
				.update(AGREEMENT_LEVEL_CATEGORY)
				.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, parentDomain)
				.where(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL.in(dslContext
						.select(AGREEMENT_LEVEL.ID).from(AGREEMENT_LEVEL)
						.where(AGREEMENT_LEVEL.AGREEMENT.eq(id)))).execute();

		dslContext.update(AGREEMENT_LEVEL)
				.set(AGREEMENT_LEVEL.DOMAIN, parentDomain)
				.where(AGREEMENT_LEVEL.AGREEMENT.eq(id)).execute();

		dslContext.update(AGREEMENT_PAYMENT)
				.set(AGREEMENT_PAYMENT.DOMAIN, parentDomain)
				.where(AGREEMENT_PAYMENT.AGREEMENT.eq(id)).execute();

		dslContext.update(AGREEMENT).set(AGREEMENT.DOMAIN, parentDomain)
				.where(AGREEMENT.ID.eq(id)).execute();

	}

	public static void updatePayment(Connection conn, Integer agreementId, Payment payment)
			throws SQLException {
		updatePayment(DSL.using(conn, getDefaultSettings()), agreementId, payment);
	}

	public static void updatePayment(DSLContext dslContext, Integer agreementId, Payment payment)
			throws SQLException {
		if (payment.getConceptId() != null && payment.getConceptId() < 0) {
			updatePaymentConcept(dslContext, payment);
			updateAgreementPayment(dslContext, payment.getId(),
					payment.getDescription(),
					payment.getSalaryType(), 
					payment.getMonth(),
					EPOCH, 
					null);
		} else {
			updateAgreementPayment(dslContext, payment);
		}
		
		updateContractPayments(dslContext, agreementId, payment);
	}

	private static void updateContractPayments(DSLContext dslContext, Integer agreementId, Payment payment) {
		
		/**
		 * SELECT * FROM contract_payment 
		 * INNER JOIN contract ON ( contract_payment.contract = contract.id ) 
		 * INNER JOIN payment_concept ON ( contract_payment.payment_concept = payment_concept.id ) 
		 * INNER JOIN agreement_level ON ( contract.agreement_level = agreement_level.id ) 
		 * WHERE agreement_level.agreement = 212 AND payment_concept = 1;
		 * */
		
		// Get all contract associated to an agreement
		List<Integer> agreementContractIds = dslContext.select(CONTRACT.ID).from(CONTRACT)
				.where(CONTRACT.AGREEMENT_LEVEL.in(
						dslContext.select(AGREEMENT_LEVEL.ID).from(AGREEMENT_LEVEL)
							.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreementId))
				)).fetch(CONTRACT.ID);
		
		// Contract payments expressions associated to an paymentConcept and same expression
		List<Record> contractPaymentExpressionRecords = dslContext.select().from(CONTRACT_PAYMENT)
			.where(CONTRACT_PAYMENT.CONTRACT.in(agreementContractIds))
			.and(CONTRACT_PAYMENT.PAYMENT_CONCEPT.eq(payment.getConceptId()))
			.and(CONTRACT_PAYMENT.EXPRESSION.eq(
					dslContext.select(PAYMENT_CONCEPT.EXPRESSION).from(PAYMENT_CONCEPT)
						.where(PAYMENT_CONCEPT.ID.eq(payment.getConceptId()))
						.fetchOne(PAYMENT_CONCEPT.EXPRESSION)
			)).fetch();
		
		contractPaymentExpressionRecords.forEach(r -> {
			ContractPaymentRecord contractPaymentR = r.into(CONTRACT_PAYMENT);
			contractPaymentR.setExpression(payment.getExpression());
			contractPaymentR.update();
		});
		
		// Contract payments description associated to an paymentConcept and same description
		List<Record> contractPaymentDescriptionRecords = dslContext.select().from(CONTRACT_PAYMENT)
			.where(CONTRACT_PAYMENT.CONTRACT.in(agreementContractIds))
			.and(CONTRACT_PAYMENT.PAYMENT_CONCEPT.eq(payment.getConceptId()))
			.and(CONTRACT_PAYMENT.DESCRIPTION.eq(
					dslContext.select(PAYMENT_CONCEPT.DESCRIPTION).from(PAYMENT_CONCEPT)
						.where(PAYMENT_CONCEPT.ID.eq(payment.getConceptId()))
						.fetchOne(PAYMENT_CONCEPT.DESCRIPTION)
			)).fetch();
		
		contractPaymentDescriptionRecords.forEach(r -> {
			ContractPaymentRecord contractPaymentR = r.into(CONTRACT_PAYMENT);
			contractPaymentR.setDescription(payment.getDescription());
			contractPaymentR.update();
		});
		
	}

	public static void removePayment(Connection conn, Integer agreementId, Payment payment)
			throws SQLException {
		removePayment(DSL.using(conn, getDefaultSettings()), agreementId, payment);
	}

	public static void removePayment(DSLContext dslContext, Integer agreementId, Payment payment)
			throws SQLException {
		
		// Try to remove extra, only remove if extra agreement_payment
		dslContext.delete(AGREEMENT_EXTRA)
			.where(AGREEMENT_EXTRA.AGREEMENT_PAYMENT.eq(payment.getId())).execute();
		
		tryToModifyOtherExtra(dslContext, payment);
		
		dslContext.delete(AGREEMENT_PAYMENT)
				.where(AGREEMENT_PAYMENT.ID.eq(payment.getId())).execute();

		if (payment.getConceptId() != null && payment.getConceptId() < 0) {
			int conceptId = payment.getConceptId();
			// @formatter:on
			dslContext
					.update(CONTRACT_PAYMENT)
					.set(CONTRACT_PAYMENT.TYPE,
							(byte) payment.getType().ordinal())
					.where(CONTRACT_PAYMENT.TYPE.isNull().and(
							CONTRACT_PAYMENT.PAYMENT_CONCEPT.eq(conceptId)))
					.execute();
			dslContext
					.update(CONTRACT_PAYMENT)
					.set(CONTRACT_PAYMENT.DESCRIPTION, payment.getDescription())
					.where(CONTRACT_PAYMENT.DESCRIPTION.isNull().and(
							CONTRACT_PAYMENT.PAYMENT_CONCEPT.eq(conceptId)))
					.execute();
			dslContext
					.update(CONTRACT_PAYMENT)
					.set(CONTRACT_PAYMENT.EXPRESSION, payment.getExpression())
					.where(CONTRACT_PAYMENT.EXPRESSION.isNull().and(
							CONTRACT_PAYMENT.PAYMENT_CONCEPT.eq(conceptId)))
					.execute();
			dslContext
					.update(CONTRACT_PAYMENT)
					.set(CONTRACT_PAYMENT.IRPF_EXPRESSION,
							payment.getIrpfExpression())
					.where(CONTRACT_PAYMENT.IRPF_EXPRESSION.isNull().and(
							CONTRACT_PAYMENT.PAYMENT_CONCEPT.eq(conceptId)))
					.execute();
			dslContext
					.update(CONTRACT_PAYMENT)
					.set(CONTRACT_PAYMENT.QUOTE_EXPRESSION,
							payment.getQuoteExpression())
					.where(CONTRACT_PAYMENT.QUOTE_EXPRESSION.isNull().and(
							CONTRACT_PAYMENT.PAYMENT_CONCEPT.eq(conceptId)))
					.execute();
			dslContext.update(CONTRACT_PAYMENT)
					.set(CONTRACT_PAYMENT.PAYMENT_CONCEPT, (Integer) null)
					.where(CONTRACT_PAYMENT.PAYMENT_CONCEPT.eq(conceptId))
					.execute();
			// @formatter:off
			dslContext.delete(PAYMENT_CONCEPT)
					.where(PAYMENT_CONCEPT.ID.eq(payment.getConceptId()))
					.execute();
		}
	}

	private static void tryToModifyOtherExtra(DSLContext dslContext, Payment payment) {
		Result<Record> agreementExtraRecords = dslContext.select().from(AGREEMENT_EXTRA)
			.where(AGREEMENT_EXTRA.AGREEMENT_PAYMENT.in(
				dslContext.select(AGREEMENT_PAYMENT.ID).from(AGREEMENT_PAYMENT)
					.where(AGREEMENT_PAYMENT.PAYMENT_CONCEPT.eq(payment.getConceptId()))
					.and(AGREEMENT_PAYMENT.AGREEMENT.eq(
							dslContext.select(AGREEMENT_PAYMENT.AGREEMENT).from(AGREEMENT_PAYMENT)
								.where(AGREEMENT_PAYMENT.ID.eq(payment.getId()))
					))
			)).fetch();
		
		if(agreementExtraRecords.isNotEmpty()) {
			Record agreementExtraRecord = agreementExtraRecords.get(0);
			String startDate = agreementExtraRecord.get(AGREEMENT_EXTRA.START_DATE);
			String newStartDte = startDate;
			if(AonStringUtils.equalsIgnoreCase(startDate, "1/7"))
				newStartDte = "1/1";
			if(AonStringUtils.equalsIgnoreCase(startDate, "1/1"))
				newStartDte = "1/7 -1";
			
			dslContext.update(AGREEMENT_EXTRA)
				.set(AGREEMENT_EXTRA.START_DATE, newStartDte)
				.where(AGREEMENT_EXTRA.ID.eq(agreementExtraRecord.get(AGREEMENT_EXTRA.ID)))
				.execute();
		}
	
	}

	public static int insertPayment(Connection conn, Integer domainId,
			Integer agreementId, Payment payment) throws SQLException {
		return insertPayment(DSL.using(conn, getDefaultSettings()), domainId,
				agreementId, payment);
	}

	public static int insertPayment(DSLContext dslContext, Integer domainId,
			Integer agreementId, Payment payment) throws SQLException {

		int paymentId = max(dslContext, AGREEMENT_PAYMENT.getIdentity()) + 1;

		if (payment.getConceptId() == null) {
			int min = min(dslContext, PAYMENT_CONCEPT.getIdentity());
			int conceptId = Math.min(-1, min - 1);
			insertPaymentConcept(dslContext, domainId, paymentId, conceptId,
					payment);
			insertAgreementPayment(dslContext, domainId, agreementId,
					conceptId, 
					paymentId, 
					payment.getSalaryType(),
					payment.getMonth(), 
					payment.getStartDate(),
					payment.getEndDate());
		} else {
			insertAgreementPayment(dslContext, domainId, agreementId,
					paymentId, payment);
		}

		return paymentId;
	}

	public static void insertExtra(Connection conn, Integer domainId,
			Integer agreementId, Extra extra) throws SQLException {
		insertExtra(DSL.using(conn, getDefaultSettings()), domainId,
				agreementId, extra);
	}

	public static void insertExtra(DSLContext dslContext, Integer domainId,
			Integer agreementId, Extra extra) throws SQLException {
		// @formatter:off
		dslContext.insertInto(AGREEMENT_EXTRA)
				.set(AGREEMENT_EXTRA.DOMAIN, domainId)
				.set(AGREEMENT_EXTRA.AGREEMENT, agreementId)
				.set(AGREEMENT_EXTRA.START_DATE, extra.getStartDate())
				.set(AGREEMENT_EXTRA.END_DATE, extra.getEndDate())
				.set(AGREEMENT_EXTRA.ISSUE_DATE, extra.getIssueDate())
				.set(AGREEMENT_EXTRA.AGREEMENT_PAYMENT, extra.getPaymentId())
				.execute();
		// @formatter:on

		if (extra.getPaymentId() == null)
			return;

		// @formatter:off
		dslContext
				.update(AGREEMENT_PAYMENT)
				.set(AGREEMENT_PAYMENT.MONTH,
						getExtraMonth(extra.getIssueDate()))
				.set(AGREEMENT_PAYMENT.SALARY_TYPE,
						(byte) SalaryType.EXTRA.ordinal())
				.set(AGREEMENT_PAYMENT.QUOTE_EXPRESSION,
						String.format("%s()",PRORATION))
				.where(AGREEMENT_PAYMENT.ID.eq(extra.getPaymentId())).execute();
		// @formatter:on
	}

	public static void updateExtra(Connection conn, Integer agreementId, Extra extra)
			throws SQLException {
		updateExtra(DSL.using(conn, getDefaultSettings()), agreementId, extra);
	}

	public static void updateExtra(DSLContext dslContext, Integer agreementId, Extra extra)
			throws SQLException {

		// @formatter:off
		dslContext
				.update(AGREEMENT_PAYMENT)
				.set(AGREEMENT_PAYMENT.SALARY_TYPE,
						(byte) SalaryType.SALARY.ordinal())
				.set(AGREEMENT_PAYMENT.QUOTE_EXPRESSION,ALL)
				.where(AGREEMENT_PAYMENT.ID.eq(extra.getPaymentId()))
				.and(AGREEMENT_PAYMENT.ID.notIn(DSL.select(AGREEMENT_EXTRA.AGREEMENT_PAYMENT).from(AGREEMENT_EXTRA).where(AGREEMENT_EXTRA.AGREEMENT.eq(agreementId))))
				.execute();
		// @formatter:on

		// @formatter:off
		dslContext.update(AGREEMENT_EXTRA)
				.set(AGREEMENT_EXTRA.START_DATE, extra.getStartDate())
				.set(AGREEMENT_EXTRA.END_DATE, extra.getEndDate())
				.set(AGREEMENT_EXTRA.ISSUE_DATE, extra.getIssueDate())
				.set(AGREEMENT_EXTRA.AGREEMENT_PAYMENT, extra.getPaymentId())
				.where(AGREEMENT_EXTRA.ID.eq(extra.getId())).execute();
		// @formatter:on

		if (extra.getPaymentId() == null)
			return;

		// @formatter:off
		dslContext
				.update(AGREEMENT_PAYMENT)
				.set(AGREEMENT_PAYMENT.MONTH,
						getExtraMonth(extra.getIssueDate()))
				.set(AGREEMENT_PAYMENT.SALARY_TYPE,
						(byte) SalaryType.EXTRA.ordinal())
				.set(AGREEMENT_PAYMENT.QUOTE_EXPRESSION,
						String.format("%s()",PRORATION))
				.where(AGREEMENT_PAYMENT.ID.eq(extra.getPaymentId())).execute();
		// @formatter:on

	}

	public static void removeExtra(Connection conn, Integer agreementId, Extra extra)
			throws SQLException {
		removeExtra(DSL.using(conn, getDefaultSettings()), agreementId, extra);
	}

	public static void removeExtra(DSLContext dslContext, Integer agreementId, Extra extra)
			throws SQLException {
		// @formatter:off
		dslContext.delete(AGREEMENT_EXTRA)
				.where(AGREEMENT_EXTRA.ID.eq(extra.getId())).execute();
		// @formatter:on
		
		// @formatter:off
		dslContext
				.update(AGREEMENT_PAYMENT)
				.set(AGREEMENT_PAYMENT.SALARY_TYPE,
						(byte) SalaryType.SALARY.ordinal())
				.set(AGREEMENT_PAYMENT.QUOTE_EXPRESSION,ALL)
				.where(AGREEMENT_PAYMENT.ID.eq(extra.getPaymentId()))
				.and(AGREEMENT_PAYMENT.ID.notIn(DSL.select(AGREEMENT_EXTRA.AGREEMENT_PAYMENT).from(AGREEMENT_EXTRA).where(AGREEMENT_EXTRA.AGREEMENT.eq(agreementId))))
				.execute();
		// @formatter:on
		
		// @formatter:off
		dslContext.delete(AGREEMENT_PAYMENT)
				.where(AGREEMENT_PAYMENT.ID.eq(extra.getPaymentId())).execute();
		// @formatter:on		
	}

	public static List<Extra> getDomainsExtras(Connection conn, Integer... domains) throws SQLException {
		return getExtras(DSL.using(conn, getDefaultSettings()), CONTRACT.DOMAIN.in(domains));
	
	}

	public static List<Extra> getEmployeesExtras(Connection conn, Integer... employees) throws SQLException {
		return getExtras(DSL.using(conn, getDefaultSettings()), CONTRACT.ID.in(employees));
	
	}

	public static List<Extra> getWorkplacesExtras(Connection conn, Integer... workplaces) throws SQLException {
		return getExtras(DSL.using(conn, getDefaultSettings()), CONTRACT.WORKPLACE.in(workplaces));
	
	}

	public static List<Agreement> getAgreements(Connection conn, int offset,
			int limit, Integer... domains) throws SQLException {
		return getAgreements(DSL.using(conn, getDefaultSettings()), offset,
				limit, domains);
	}

	public static Agreement getAgreement(Connection conn, Integer agreementId) throws SQLException {
		return getAgreement(DSL.using(conn, getDefaultSettings()), agreementId);
	}

	public static List<Agreement> getAgreements(DSLContext dslContext,
			int offset, int limit, Integer... domains) throws SQLException {
		
		
		// @formatter:off
		Result<AgreementRecord> result = dslContext.select().from(AGREEMENT)
				.where(AGREEMENT.DOMAIN.in(domains))

				.or(AGREEMENT.ID.in(
						dslContext
						.select(DSL.cast(APP_PARAM.VALUE, Integer.class))
						.from(APP_PARAM)
						.where(APP_PARAM.DOMAIN.in(domains))
						.and(APP_PARAM.NAME.eq("PAY_SYSTEM_AGREEMENT"))
				))
				.or(AGREEMENT.ID.in(
						dslContext
						.select(DSL.cast(APP_PARAM.VALUE, Integer.class))
						.from(APP_PARAM)
						.where(APP_PARAM.DOMAIN.equal(0))
						.and(APP_PARAM.NAME.eq("PAY_SYSTEM_AGREEMENT"))
				))
				.or(AGREEMENT.ID.eq(0))
				
				.orderBy(AGREEMENT.DOMAIN.desc(), AGREEMENT.DESCRIPTION)
				
				.offset(offset)
				.limit(limit)
				.fetchInto(AGREEMENT);
		// @formatter:on

		List<Agreement> agreements = new LinkedList<Agreement>();
		for (AgreementRecord record : result) {
			Agreement agreement = new Agreement();

			agreement.setId(record.getId()); // Not NULL
			agreement.setDomain(record.getDomain());
			agreement.setDescription(record.getDescription());
			agreement.setSSNumber(record.getSsNumber());
			
			agreement.setLevels(Collections.emptySet());
			//agreement.setLevels(getAgreementLevel(dslContext, record.getId(), agreement));

			boolean hasContracts = hasContract(dslContext, record.getId());
			agreement.setHasContract(hasContracts);
			// agreement.setLevelsWithoutCategories(false);
			// agreement.setEmployees(rs.getInt("EMPLOYEEs"));
			// agreement.setRedefined(rs.getInt("REDEFINED"));
			agreements.add(agreement);

		}
		return agreements;
	}

	public static Agreement getAgreement(DSLContext dslContext,
			Integer agrementId) throws SQLException {
		
		
		// @formatter:off
		AgreementRecord record = dslContext.select().from(AGREEMENT)
				.where(AGREEMENT.ID.eq(agrementId))
				.fetchOneInto(AGREEMENT)
				;
		// @formatter:on

		Agreement agreement = new Agreement();

		agreement.setId(record.getId()); // Not NULL
		agreement.setDomain(record.getDomain());
		agreement.setDescription(record.getDescription());
		agreement.setSSNumber(record.getSsNumber());
		
		agreement.setLevels(getAgreementLevel(dslContext, record.getId(), agreement));

		return agreement;
	}

	private static Set<Level> getAgreementLevel(DSLContext dslContext, Integer id, Agreement agreement) {
		Set<Level>  levelsList = new HashSet();
		Map<Integer, Set<String>> categoriesMap = new HashMap<>();
		Result<Record> levels = dslContext.select().from(AGREEMENT_LEVEL).where(AGREEMENT_LEVEL.AGREEMENT.eq(id)).fetch();
		for(Record r: levels){
			Level level =  new Level();
			level.setId(r.get(AGREEMENT_LEVEL.ID));
			level.setDescription(r.get(AGREEMENT_LEVEL.DESCRIPTION));
				
			Result<Record> categories = dslContext.select().from(AGREEMENT_LEVEL_CATEGORY)
				.where(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL.eq(r.get(AGREEMENT_LEVEL.ID))).fetch();
			
			
			categoriesMap.put(r.get(AGREEMENT_LEVEL.ID), new HashSet());
			for(Record rc : categories){
				categoriesMap.get(r.get(AGREEMENT_LEVEL.ID)).add(rc.get(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION));
			}
			
			levelsList.add(level);
		}
		agreement.setCategoriesMap(categoriesMap);
		return levelsList;
	}

	public static List<Extra> getExtras(DSLContext dslContext, Condition ...conditions) throws SQLException {
		
		// @formatter:off
		Cursor<Record7<Integer,Integer,String,String,String,String,String>> result =		
			dslContext
				.selectDistinct(
				AGREEMENT_EXTRA.ID,
				AGREEMENT_EXTRA.DOMAIN,
				AGREEMENT_EXTRA.END_DATE,
				AGREEMENT_EXTRA.START_DATE,
				AGREEMENT_EXTRA.ISSUE_DATE,
				AGREEMENT_PAYMENT.DESCRIPTION,
				AGREEMENT.DESCRIPTION
				)
				.from(CONTRACT)
				.join(AGREEMENT_LEVEL).on(CONTRACT.AGREEMENT_LEVEL.eq(AGREEMENT_LEVEL.ID))
				//.join(AGREEMENT_LEVEL_CATEGORY).on(CONTRACT.AGREEMENT_LEVEL_CATEGORY.eq(AGREEMENT_LEVEL_CATEGORY.ID))
				//.join(AGREEMENT_LEVEL).on(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL.eq(AGREEMENT_LEVEL.ID))
				.join(AGREEMENT_EXTRA).on(AGREEMENT_LEVEL.AGREEMENT.eq(AGREEMENT_EXTRA.AGREEMENT))
				.join(AGREEMENT_PAYMENT).on(AGREEMENT_EXTRA.AGREEMENT_PAYMENT.eq(AGREEMENT_PAYMENT.ID))
				.join(AGREEMENT).on(AGREEMENT_PAYMENT.AGREEMENT.eq(AGREEMENT.ID))
				.where(conditions)
				.orderBy(AGREEMENT_EXTRA.START_DATE)
				.fetchLazy()
				;
		// @formatter:on

		List<Extra> extras = new LinkedList<Extra>();
		for (Record7<Integer,Integer,String,String,String,String,String> record : result) {

			Extra extra = new Extra();
			extra.setId(record.getValue(AGREEMENT_EXTRA.ID)); 
			extra.setDomain(record.getValue(AGREEMENT_EXTRA.DOMAIN)); 
			extra.setEndDate(record.getValue(AGREEMENT_EXTRA.END_DATE)); 
			extra.setStartDate(record.getValue(AGREEMENT_EXTRA.START_DATE)); 
			extra.setIssueDate(record.getValue(AGREEMENT_EXTRA.ISSUE_DATE)); 

			extra.setAgreementDescription(record.getValue(AGREEMENT.DESCRIPTION)); 
			extra.setPaymentDescription(record.getValue(AGREEMENT_PAYMENT.DESCRIPTION)); 

			extras.add(extra);

		}
		return extras;
	}

	private static boolean hasContract(DSLContext dslContext,
			Integer agreementId) throws SQLException {

		Cursor<Record> cursor = dslContext
				.select()
				.from(CONTRACT)
//				.where(CONTRACT.AGREEMENT_LEVEL_CATEGORY.in(
				.where(CONTRACT.AGREEMENT_LEVEL.in(
						
						dslContext
						.select(AGREEMENT_LEVEL.ID)
						.from(AGREEMENT_LEVEL)
						.where(AGREEMENT_LEVEL.AGREEMENT
								.in(agreementId))))

		.fetchLazy();

//				dslContext
//						.select(AGREEMENT_LEVEL_CATEGORY.ID)
//						.from(AGREEMENT_LEVEL_CATEGORY)
//						.where(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL.in(
//
//						dslContext
//								.select(AGREEMENT_LEVEL.ID)
//								.from(AGREEMENT_LEVEL)
//								.where(AGREEMENT_LEVEL.AGREEMENT
//										.in(agreementId))))))
//
//				.fetchLazy();

		return (cursor.hasNext()) ? true : false;

	}

	public static void deleteAgreement(Connection conn, Integer domain,
			Agreement agreement) throws SQLException {

		DSLContext dslContext = DSL.using(conn, SQLDialect.MYSQL,
				getDefaultSettings());

		SelectConditionStep<Record1<Integer>> agreementLevelId = dslContext
				.select(AGREEMENT_LEVEL.ID).from(AGREEMENT_LEVEL)
				.where(AGREEMENT_LEVEL.AGREEMENT.in(agreement.getId()));

		dslContext
				.delete(AGREEMENT_LEVEL_CATEGORY)
				.where(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL
						.in(agreementLevelId)).execute();

		dslContext
				.delete(AGREEMENT_LEVEL_DATA)
				.where(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL
						.in(agreementLevelId)).execute();

		dslContext.delete(AGREEMENT_LEVEL)
				.where(AGREEMENT_LEVEL.AGREEMENT.in(agreement.getId()))
				.execute();

		dslContext.delete(AGREEMENT_EXTRA)
				.where(AGREEMENT_EXTRA.AGREEMENT.in(agreement.getId()))
				.execute();

		dslContext.delete(AGREEMENT_DATA)
				.where(AGREEMENT_DATA.AGREEMENT.in(agreement.getId()))
				.execute();

		dslContext.delete(AGREEMENT_PAYMENT)
				.where(AGREEMENT_PAYMENT.AGREEMENT.in(agreement.getId()))
				.execute();

		dslContext.delete(PAYROLL_WORKPLACE)
				.where(PAYROLL_WORKPLACE.AGREEMENT.in(agreement.getId()))
				.execute();

		dslContext.delete(AGREEMENT).where(AGREEMENT.ID.in(agreement.getId()))
				.execute();
	}

	private static final String SET_FOREIGN_KEY_CHECKS_0 = "SET FOREIGN_KEY_CHECKS=0;";
	private static final String SET_FOREIGN_KEY_CHECKS_1 = "SET FOREIGN_KEY_CHECKS=1;";

	public static void trashRestoreAgreement(Connection conn, Integer agreementId, boolean delete) {
		Statement fkStmt = null;
		try { 
			fkStmt = conn.createStatement();
			fkStmt.execute(SET_FOREIGN_KEY_CHECKS_0);
	
			DSLContext dslContext = DSL.using(conn, SQLDialect.MYSQL,
					getDefaultSettings());
			
			Integer oldId =agreementId;
			Integer newId =agreementId*-1;
			
			Optional<AgreementRecord> mirrorAgreement = 
			dslContext
			.select()
			.from(AGREEMENT)
			.where(AGREEMENT.ID.eq(newId))
			.fetchOptionalInto(AGREEMENT);
			mirrorAgreement
			.ifPresent(a -> 
				a.delete());
			dslContext
			.select()
			.from(AGREEMENT)
			.where(AGREEMENT.ID.eq(oldId))
			.fetchOptionalInto(AGREEMENT)
			.ifPresent(a ->  {
				if ( delete )
					a.delete();
				AgreementRecord copy = a.copy(); 
				copy.setId(newId) ; 
				copy.insert(); });
			
			mirrorAgreement.ifPresent(a -> { 
				AgreementRecord copy = a.copy(); 
				copy.setId(oldId) ; 
				copy.insert(); });
			
			// payments
			Result<AgreementPaymentRecord> mirrorPayments = 
					dslContext.select()
					.from(AGREEMENT_PAYMENT)
					.where(AGREEMENT_PAYMENT.AGREEMENT.eq(newId))
					.fetchInto(AGREEMENT_PAYMENT);
			mirrorPayments.forEach(p -> p.delete() );
			dslContext.select()
			.from(AGREEMENT_PAYMENT)
			.where(AGREEMENT_PAYMENT.AGREEMENT.eq(oldId))
			.fetchInto(AGREEMENT_PAYMENT)
			.forEach( p -> {
				if ( delete )
					p.delete();
				AgreementPaymentRecord copy = p.copy();
				copy.setAgreement(newId);
				copy.setId(p.getId()*-1);
				copy.insert();
			});
			mirrorPayments.forEach(p -> {
				AgreementPaymentRecord copy = p.copy();
				copy.setAgreement(oldId);
				copy.setId(p.getId()*-1);
				copy.insert();
			});
			
			// extras
			Result<AgreementExtraRecord> mirrorExtras = 
					dslContext.select()
					.from(AGREEMENT_EXTRA)
					.where(AGREEMENT_EXTRA.AGREEMENT.eq(newId))
					.fetchInto(AGREEMENT_EXTRA);
			mirrorExtras.forEach(p -> p.delete() );
			dslContext.select()
			.from(AGREEMENT_EXTRA)
			.where(AGREEMENT_EXTRA.AGREEMENT.eq(oldId))
			.fetchInto(AGREEMENT_EXTRA)
			.forEach( p -> {
				if ( delete )
					p.delete();
				AgreementExtraRecord copy = p.copy();
				copy.setAgreement(newId);
				copy.setAgreementPayment(p.getAgreementPayment()*-1);
				copy.setId(p.getId()*-1);
				copy.insert();
			});
			mirrorExtras.forEach(p -> {
				AgreementExtraRecord copy = p.copy();
				copy.setAgreement(oldId);
				copy.setAgreementPayment(p.getAgreementPayment()*-1);
				copy.setId(p.getId()*-1);
				copy.insert();
			});

			// datas
			Result<AgreementDataRecord> mirrorDatas = 
					dslContext.select()
					.from(AGREEMENT_DATA)
					.where(AGREEMENT_DATA.AGREEMENT.eq(newId))
					.fetchInto(AGREEMENT_DATA);
			mirrorDatas.forEach(p -> p.delete() );
			dslContext.select()
			.from(AGREEMENT_DATA)
			.where(AGREEMENT_DATA.AGREEMENT.eq(oldId))
			.fetchInto(AGREEMENT_DATA)
			.forEach( p -> {
				if ( delete )
					p.delete();
				AgreementDataRecord copy = p.copy();
				copy.setAgreement(newId);
				copy.setId(p.getId()*-1);
				copy.insert();
			});
			mirrorDatas.forEach(p -> {
				AgreementDataRecord copy = p.copy();
				copy.setAgreement(oldId);
				copy.setId(p.getId()*-1);
				copy.insert();
			});

			// contracts
			dslContext.select()
			.from(CONTRACT)
			.join(AGREEMENT_LEVEL).onKey(FK_CONTRACT_AGREEMENT_LEVEL)
			.where(AGREEMENT_LEVEL.AGREEMENT.eq(oldId))
			.and(CONTRACT.AGREEMENT_LEVEL.notIn(DSL.select(AGREEMENT_LEVEL.ID.mul(-1)).from(AGREEMENT_LEVEL).where(AGREEMENT_LEVEL.AGREEMENT.eq(newId))))
			.fetchInto(CONTRACT)
			.forEach( c -> {
				c.setAgreementLevel(c.getAgreementLevel()*-1);
				c.update();
			});

			// categories
			Result<AgreementLevelCategoryRecord> mirrorCategories = 
					dslContext.select()
					.from(AGREEMENT_LEVEL_CATEGORY)
					.join(AGREEMENT_LEVEL).onKey(FK_AGREEMENT_LEVEL_CATEGORY_AGREEMENT_LEVEL)
					.where(AGREEMENT_LEVEL.AGREEMENT.eq(newId))
					.fetchInto(AGREEMENT_LEVEL_CATEGORY);
			mirrorCategories.forEach(p -> p.delete() );
			dslContext.select()
			.from(AGREEMENT_LEVEL_CATEGORY)
			.join(AGREEMENT_LEVEL).onKey(FK_AGREEMENT_LEVEL_CATEGORY_AGREEMENT_LEVEL)
			.where(AGREEMENT_LEVEL.AGREEMENT.eq(oldId))
			.fetchInto(AGREEMENT_LEVEL_CATEGORY)
			.forEach( p -> {
				if ( delete )
					p.delete();
				AgreementLevelCategoryRecord copy = p.copy();
				copy.setAgreementLevel(p.getAgreementLevel()*-1);
				copy.setId(p.getId()*-1);
				copy.insert();
			});
			mirrorCategories.forEach(p -> {
				AgreementLevelCategoryRecord copy = p.copy();
				copy.setAgreementLevel(p.getAgreementLevel()*-1);
				copy.setId(p.getId()*-1);
				copy.insert();
			});

			// level datas
			Result<AgreementLevelDataRecord> mirrorLDatas = 
					dslContext.select()
					.from(AGREEMENT_LEVEL_DATA)
					.join(AGREEMENT_LEVEL).onKey(FK_AGREEMENT_LEVEL_DATA_AGREEMENT_LEVEL)
					.where(AGREEMENT_LEVEL.AGREEMENT.eq(newId))
					.fetchInto(AGREEMENT_LEVEL_DATA);
			mirrorLDatas.forEach(p -> p.delete() );
			dslContext.select()
			.from(AGREEMENT_LEVEL_DATA)
			.join(AGREEMENT_LEVEL).onKey(FK_AGREEMENT_LEVEL_DATA_AGREEMENT_LEVEL)
			.where(AGREEMENT_LEVEL.AGREEMENT.eq(oldId))
			.fetchInto(AGREEMENT_LEVEL_DATA)
			.forEach( p -> {
				if ( delete )
					p.delete();
				AgreementLevelDataRecord copy = p.copy();
				copy.setAgreementLevel(p.getAgreementLevel()*-1);
				copy.setId(p.getId()*-1);
				copy.insert();
			});
			mirrorLDatas.forEach(p -> {
				AgreementLevelDataRecord copy = p.copy();
				copy.setAgreementLevel(p.getAgreementLevel()*-1);
				copy.setId(p.getId()*-1);
				copy.insert();
			});

			// levels
			Result<AgreementLevelRecord> mirrorLevels = 
					dslContext.select()
					.from(AGREEMENT_LEVEL)
					.where(AGREEMENT_LEVEL.AGREEMENT.eq(newId))
					.fetchInto(AGREEMENT_LEVEL);
			mirrorLevels.forEach(p -> p.delete() );
			dslContext.select()
			.from(AGREEMENT_LEVEL)
			.where(AGREEMENT_LEVEL.AGREEMENT.eq(oldId))
			.fetchInto(AGREEMENT_LEVEL)
			.forEach( p -> {
				if ( delete )
					p.delete();
				AgreementLevelRecord copy = p.copy();
				copy.setAgreement(newId);
				copy.setId(p.getId()*-1);
				copy.insert();
			});
			mirrorLevels.forEach(p -> {
				AgreementLevelRecord copy = p.copy();
				copy.setAgreement(oldId);
				copy.setId(p.getId()*-1);
				copy.insert();
			});
			
			// workplace
			dslContext.select()
			.from(PAYROLL_WORKPLACE)
			.where(PAYROLL_WORKPLACE.AGREEMENT.eq(oldId))
			.and(PAYROLL_WORKPLACE.AGREEMENT.notIn(DSL.select(AGREEMENT.ID).from(AGREEMENT) ))
			.fetchInto(PAYROLL_WORKPLACE)
			.forEach( w -> {
				w.setAgreement(w.getAgreement()*-1);
				w.update();
			});

		}
		catch ( SQLException e ) {
			
		} finally {
			try {
				if ( fkStmt == null )
					return;
				
				fkStmt.execute(SET_FOREIGN_KEY_CHECKS_1);
				fkStmt.close();
			} catch ( SQLException e ) {
				
			}
		}
	}

	public static void updateAgreementId2delete(Connection conn, Integer domainId,
			Agreement agreement) {

		try {

			Statement sOpen = conn.createStatement();
			sOpen.execute(SET_FOREIGN_KEY_CHECKS_0);
			System.out.println("Claves referenciales deshabilitadas");
			sOpen.close();

			// -----------------------------

			DSLContext dslContext = DSL.using(conn, SQLDialect.MYSQL,
					getDefaultSettings());

			// @formatter:off

			SelectConditionStep<Record1<Integer>> agreementLevelId = dslContext
					.select(AGREEMENT_LEVEL.ID).from(AGREEMENT_LEVEL)
					.where(AGREEMENT_LEVEL.AGREEMENT.in(agreement.getId()));

			SelectConditionStep<Record1<Integer>> agreementLevelCategoryId = dslContext
					.select(AGREEMENT_LEVEL_CATEGORY.ID)
					.from(AGREEMENT_LEVEL_CATEGORY)
					.where(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL
							.in(agreementLevelId));

			dslContext.update(AGREEMENT)
					.set(AGREEMENT.ID, AGREEMENT.ID.mul(-1))
					.where(AGREEMENT.ID.eq(agreement.getId())).execute();

			dslContext
					.update(AGREEMENT_DATA)
					.set(AGREEMENT_DATA.ID, AGREEMENT_DATA.ID.mul(-1))
					.set(AGREEMENT_DATA.AGREEMENT,
							AGREEMENT_DATA.AGREEMENT.mul(-1))
					.where(AGREEMENT_DATA.AGREEMENT.eq(agreement.getId()))
					.execute();

			dslContext
					.update(AGREEMENT_EXTRA)
					.set(AGREEMENT_EXTRA.ID, AGREEMENT_EXTRA.ID.mul(-1))
					.set(AGREEMENT_EXTRA.AGREEMENT,
							AGREEMENT_EXTRA.AGREEMENT.mul(-1))

					.set(AGREEMENT_EXTRA.AGREEMENT_PAYMENT,
							(AGREEMENT_EXTRA.AGREEMENT_PAYMENT != null) ? AGREEMENT_EXTRA.AGREEMENT_PAYMENT
									.mul(-1)
									: AGREEMENT_EXTRA.AGREEMENT_PAYMENT)

					.where(AGREEMENT_EXTRA.AGREEMENT.eq(agreement.getId()))
					.execute();

			dslContext
					.update(CONTRACT)
					.set(CONTRACT.AGREEMENT_LEVEL,
							(CONTRACT.AGREEMENT_LEVEL != null) ? CONTRACT.AGREEMENT_LEVEL
									.mul(-1)
									: CONTRACT.AGREEMENT_LEVEL)

					.where(CONTRACT.AGREEMENT_LEVEL
							.in(agreementLevelId)).execute();

//					.set(CONTRACT.AGREEMENT_LEVEL_CATEGORY,
//							(CONTRACT.AGREEMENT_LEVEL_CATEGORY != null) ? CONTRACT.AGREEMENT_LEVEL_CATEGORY
//									.mul(-1)
//									: CONTRACT.AGREEMENT_LEVEL_CATEGORY)
//
//					.where(CONTRACT.AGREEMENT_LEVEL_CATEGORY
//							.in(agreementLevelCategoryId)).execute();

			dslContext
					.update(AGREEMENT_LEVEL_CATEGORY)
					.set(AGREEMENT_LEVEL_CATEGORY.ID,
							AGREEMENT_LEVEL_CATEGORY.ID.mul(-1))
					.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL,
							AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL.mul(-1))
					.where(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL
							.in(agreementLevelId)).execute();

			dslContext
					.update(AGREEMENT_LEVEL_DATA)
					.set(AGREEMENT_LEVEL_DATA.ID,
							AGREEMENT_LEVEL_DATA.ID.mul(-1))
					.set(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL,
							AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL.mul(-1))
					.where(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL
							.in(agreementLevelId)).execute();

			dslContext
					.update(AGREEMENT_LEVEL)
					.set(AGREEMENT_LEVEL.ID, AGREEMENT_LEVEL.ID.mul(-1))
					.set(AGREEMENT_LEVEL.AGREEMENT,
							AGREEMENT_LEVEL.AGREEMENT.mul(-1))
					.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreement.getId()))
					.execute();

			dslContext.update(AGREEMENT_PAYMENT)
					.set(AGREEMENT_PAYMENT.ID, AGREEMENT_PAYMENT.ID.mul(-1))
					.set(AGREEMENT_PAYMENT.AGREEMENT, -(agreement.getId()))
					.where(AGREEMENT_PAYMENT.AGREEMENT.eq(agreement.getId()))
					.execute();
			dslContext
					.update(PAYROLL_WORKPLACE)
					.set(PAYROLL_WORKPLACE.ID, PAYROLL_WORKPLACE.ID.mul(-1)) // ????
					.set(PAYROLL_WORKPLACE.AGREEMENT,
							PAYROLL_WORKPLACE.AGREEMENT.mul(-1))
					.where(PAYROLL_WORKPLACE.AGREEMENT.eq(agreement.getId()))
					.execute();

			dslContext
					.update(ENTERPRISE_DATA)
					.set(ENTERPRISE_DATA.EXPRESSION,
							ENTERPRISE_DATA.EXPRESSION.mul(-1))
					.where(ENTERPRISE_DATA.NAME.eq(AGREEMENT.getName()))
					.and(ENTERPRISE_DATA.EXPRESSION.eq(Integer.toString(agreement.getId())))
					.execute();

			// @formatter:on

			// -----------------------------

			Statement sClose = conn.createStatement();
			sClose.execute(SET_FOREIGN_KEY_CHECKS_1);
			System.out.println("Claves referenciales habilitadas");
			sClose.close();

		} catch (SQLException ex) {
			throw new IllegalArgumentException();
		} catch (Exception ex) {
			throw new IllegalArgumentException();
		}
	}

	public static Agreement copyAgreement(Connection conn, Integer domain,
			Integer id) {
		return copyAgreement(DSL.using(conn, getDefaultSettings()), domain, id);
	}

	private static Agreement copyAgreement(DSLContext dslContext,
			Integer domain, Integer id) {

		try {
			// @formatter:off
			Record agreementRecord = dslContext.selectFrom(AGREEMENT)
					.where(AGREEMENT.ID.eq(id)).fetchOne();
			// @formatter:on

			// EL CAMPO DESCRIPTION DE AGREEMENT TIENE UN MAX DE 64 CARACTERES.
			// COMPRUEBO QUE AL ADD 'COPIA DE' NO SE PASE.
			// EVITO OTRO QUEBRADERO DE CABEZA....

			int lenght = agreementRecord.field(AGREEMENT.DESCRIPTION)
					.getDataType().length();
			String aux = "COPIA DE "
					+ agreementRecord.getValue(AGREEMENT.DESCRIPTION);
			String description = (aux.length() > lenght) ? aux.substring(0,
					lenght) : aux;
			String ssNumber = agreementRecord.getValue(AGREEMENT.SS_NUMBER);

			int newAgreementId = dslContext
					.insertInto(AGREEMENT)
					.set(AGREEMENT.DOMAIN, domain)
					.set(AGREEMENT.CALENDAR,
							agreementRecord.getValue(AGREEMENT.CALENDAR))
					.set(AGREEMENT.DESCRIPTION, description)
					.returning(AGREEMENT.ID).fetchOne().getId();

			Agreement agreement = new Agreement();
			agreement.setId(newAgreementId);
			agreement.setDescription(description);
			agreement.setSSNumber(ssNumber);
			agreement.setDomain(domain);
			agreement.setHasContract(false);

			copy2AgreementData(dslContext, id, newAgreementId, domain);
			copy2AgreementLevel(dslContext, id, newAgreementId, domain);
			copy2AgreementPayment(dslContext, id, newAgreementId, domain);

			return agreement;

		} catch (Exception ex) {
			throw new IllegalArgumentException();
		}
	}

	private static void copy2AgreementData(DSLContext dslContext,
			Integer oldAgreementId, Integer newAgreementId, Integer domain)
			throws Exception {

		InsertSetMoreStep<AgreementDataRecord> insert = null;

		// @formatter:off
		List<AgreementDataRecord> agreementDataRecord = dslContext
				.selectFrom(AGREEMENT_DATA)
				.where(AGREEMENT_DATA.AGREEMENT.eq(oldAgreementId))
				.fetchInto(AGREEMENT_DATA);
		// @formatter:on

		if (agreementDataRecord != null) {

			for (AgreementDataRecord record : agreementDataRecord) {

				insert = dslContext
						.insertInto(AGREEMENT_DATA)
						.set(AGREEMENT_DATA.DOMAIN, domain)
						.set(AGREEMENT_DATA.NAME,
								record.getValue(AGREEMENT_DATA.NAME))
						.set(AGREEMENT_DATA.AGREEMENT, newAgreementId)
						.set(AGREEMENT_DATA.EXPRESSION,
								record.getValue(AGREEMENT_DATA.EXPRESSION))
						.set(AGREEMENT_DATA.START_DATE,
								record.getValue(AGREEMENT_DATA.START_DATE))
						.set(AGREEMENT_DATA.END_DATE,
								record.getValue(AGREEMENT_DATA.END_DATE));

				insert.execute();
			}
		}
	}

	private static void copy2AgreementLevel(DSLContext dslContext,
			Integer oldAgreementId, Integer newAgreementId, Integer domain)
			throws Exception {
		InsertSetMoreStep<AgreementLevelRecord> insert = null;

		// @formatter:off
		List<AgreementLevelRecord> agreementLevelRecord = dslContext
				.selectFrom(AGREEMENT_LEVEL)
				.where(AGREEMENT_LEVEL.AGREEMENT.eq(oldAgreementId))
				.fetchInto(AGREEMENT_LEVEL);

		if (agreementLevelRecord != null) {

			for (AgreementLevelRecord record : agreementLevelRecord) {

				insert = dslContext
						.insertInto(AGREEMENT_LEVEL)
						.set(AGREEMENT_LEVEL.DOMAIN, domain)
						.set(AGREEMENT_LEVEL.AGREEMENT, newAgreementId)
						.set(AGREEMENT_LEVEL.DESCRIPTION,
								record.getValue(AGREEMENT_LEVEL.DESCRIPTION));

				int newLevelId = insert.returning(AGREEMENT_LEVEL.ID)
						.fetchOne().getId();
				int oldLevelId = record.getValue(AGREEMENT_LEVEL.ID);

				copy2AgreementLevelInner(dslContext, domain, oldLevelId,
						newLevelId);

			}
		}
	}

	private static void copy2AgreementLevelInner(DSLContext dslContext,
			Integer domain, Integer oldLevelId, Integer newLevelId)
			throws Exception {

		InsertSetMoreStep<AgreementLevelDataRecord> insertLevelData = null;
		InsertSetMoreStep<AgreementLevelCategoryRecord> insertLevelCategory = null;

		List<AgreementLevelDataRecord> selectLevelData = dslContext
				.selectFrom(AGREEMENT_LEVEL_DATA)
				.where(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL.eq(oldLevelId))
				.fetchInto(AGREEMENT_LEVEL_DATA);

		if (selectLevelData != null) {

			for (AgreementLevelDataRecord record : selectLevelData) {

				insertLevelData = dslContext
						.insertInto(AGREEMENT_LEVEL_DATA)
						.set(AGREEMENT_LEVEL_DATA.DOMAIN, domain)
						.set(AGREEMENT_LEVEL_DATA.NAME,
								record.getValue(AGREEMENT_LEVEL_DATA.NAME))
						.set(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL, newLevelId)
						.set(AGREEMENT_LEVEL_DATA.EXPRESSION,
								record.getValue(AGREEMENT_LEVEL_DATA.EXPRESSION))
						.set(AGREEMENT_LEVEL_DATA.START_DATE,
								record.getValue(AGREEMENT_LEVEL_DATA.START_DATE))
						.set(AGREEMENT_LEVEL_DATA.END_DATE,
								record.getValue(AGREEMENT_LEVEL_DATA.END_DATE));

				insertLevelData.execute();
			}
		}

		List<AgreementLevelCategoryRecord> selectLevelCategory = dslContext
				.selectFrom(AGREEMENT_LEVEL_CATEGORY)
				.where(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL.eq(oldLevelId))
				.fetchInto(AGREEMENT_LEVEL_CATEGORY);

		if (selectLevelCategory != null) {

			for (AgreementLevelCategoryRecord record : selectLevelCategory) {

				insertLevelCategory = dslContext
						.insertInto(AGREEMENT_LEVEL_CATEGORY)
						.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, domain)
						.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL,
								newLevelId)
						.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION,
								record.getValue(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION));

				insertLevelCategory.execute();
			}
		}
	}

	private static void copy2AgreementPayment(DSLContext dslContext,
			Integer oldAgreementId, Integer newAgreementId, Integer domain)
			throws Exception {

		InsertSetMoreStep<AgreementPaymentRecord> insertPayment = null;

		// @formatter:off
		List<AgreementPaymentRecord> agreementPaymentRecord = dslContext
				.selectFrom(AGREEMENT_PAYMENT)
				.where(AGREEMENT_PAYMENT.AGREEMENT.eq(oldAgreementId))
				.fetchInto(AGREEMENT_PAYMENT);
		// @formatter:on

		if (agreementPaymentRecord != null) {

			for (AgreementPaymentRecord record : agreementPaymentRecord) {
				
				//If payment_concept negative must be copy to like a new one
				Integer paymentConceptId = record.get(AGREEMENT_PAYMENT.PAYMENT_CONCEPT);
				if(paymentConceptId < 0) {
					//Get smallestId
					Record1<Integer> smallestIdR = dslContext.select(DSL.min(PAYMENT_CONCEPT.ID)).from(PAYMENT_CONCEPT)
							.fetchOne();
					
					//New smallestId
					Integer smallestId = smallestIdR.value1() - 1;
					
					Record paymentConceptRecord = dslContext.select().from(PAYMENT_CONCEPT)
							.where(PAYMENT_CONCEPT.ID.eq(paymentConceptId))
							.fetchOne();
					
					dslContext.insertInto(PAYMENT_CONCEPT)
						.set(PAYMENT_CONCEPT.ID, smallestId)
						.set(PAYMENT_CONCEPT.DOMAIN, paymentConceptRecord.get(PAYMENT_CONCEPT.DOMAIN))
						.set(PAYMENT_CONCEPT.CODE, paymentConceptRecord.get(PAYMENT_CONCEPT.CODE))
						.set(PAYMENT_CONCEPT.DESCRIPTION, paymentConceptRecord.get(PAYMENT_CONCEPT.DESCRIPTION))
						.set(PAYMENT_CONCEPT.TYPE, paymentConceptRecord.get(PAYMENT_CONCEPT.TYPE))
						.set(PAYMENT_CONCEPT.DESCRIPTION_DECORABLE, paymentConceptRecord.get(PAYMENT_CONCEPT.DESCRIPTION_DECORABLE))
						.set(PAYMENT_CONCEPT.EXPRESSION, paymentConceptRecord.get(PAYMENT_CONCEPT.EXPRESSION))
						.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, paymentConceptRecord.get(PAYMENT_CONCEPT.IRPF_EXPRESSION))
						.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, paymentConceptRecord.get(PAYMENT_CONCEPT.QUOTE_EXPRESSION))
						.execute();
					
					//Set new id
					paymentConceptId = smallestId;
				}

				insertPayment = dslContext
						.insertInto(AGREEMENT_PAYMENT)
						.set(AGREEMENT_PAYMENT.DOMAIN, domain)
						.set(AGREEMENT_PAYMENT.AGREEMENT, newAgreementId)
						.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, paymentConceptId)
//						.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT,
//								record.getValue(AGREEMENT_PAYMENT.PAYMENT_CONCEPT))
						.set(AGREEMENT_PAYMENT.TYPE,
								record.getValue(AGREEMENT_PAYMENT.TYPE))
						.set(AGREEMENT_PAYMENT.EXPRESSION,
								record.getValue(AGREEMENT_PAYMENT.EXPRESSION))
						.set(AGREEMENT_PAYMENT.DESCRIPTION,
								record.getValue(AGREEMENT_PAYMENT.DESCRIPTION))
						.set(AGREEMENT_PAYMENT.START_DATE,
								record.getValue(AGREEMENT_PAYMENT.START_DATE))
						.set(AGREEMENT_PAYMENT.END_DATE,
								record.getValue(AGREEMENT_PAYMENT.END_DATE))
						.set(AGREEMENT_PAYMENT.MONTH,
								record.getValue(AGREEMENT_PAYMENT.MONTH))
						.set(AGREEMENT_PAYMENT.SALARY_TYPE,
								record.getValue(AGREEMENT_PAYMENT.SALARY_TYPE))
						.set(AGREEMENT_PAYMENT.DESCRIPTION_DECORABLE,
								record.getValue(AGREEMENT_PAYMENT.DESCRIPTION_DECORABLE))
						.set(AGREEMENT_PAYMENT.IRPF_EXPRESSION,
								record.getValue(AGREEMENT_PAYMENT.IRPF_EXPRESSION))
						.set(AGREEMENT_PAYMENT.QUOTE_EXPRESSION,
								record.getValue(AGREEMENT_PAYMENT.QUOTE_EXPRESSION));

				int newPaymentId = insertPayment
						.returning(AGREEMENT_PAYMENT.ID).fetchOne().getId();
				int oldPaymentId = record.getValue(AGREEMENT_PAYMENT.ID);

				insert2AgreementExtra(dslContext, domain, oldAgreementId,
						newAgreementId, oldPaymentId, newPaymentId);
			}
		}
	}

	private static void insert2AgreementExtra(DSLContext dslContext,
			Integer domain, Integer oldAgreementId, Integer newAgreementId,
			Integer oldPaymentId, Integer newPaymentId) throws Exception {

		InsertSetMoreStep<AgreementExtraRecord> insertExtra = null;

		// @formatter:off
		// List<AgreementExtraRecord> agreementExtraRecord = dslContext
		// .selectFrom(AGREEMENT_EXTRA)
		// .where(AGREEMENT_EXTRA.AGREEMENT_PAYMENT.eq(oldPaymentId))
		// .fetchInto(AGREEMENT_EXTRA);
		// @formatter:on

		// @formatter:off
		List<AgreementExtraRecord> agreementExtraRecord = dslContext
				.selectFrom(AGREEMENT_EXTRA)
				.where(AGREEMENT_EXTRA.AGREEMENT.eq(oldAgreementId))
				.and(AGREEMENT_EXTRA.AGREEMENT_PAYMENT.eq(oldPaymentId))
				.fetchInto(AGREEMENT_EXTRA);
		// @formatter:on

		if (agreementExtraRecord != null) {

			for (AgreementExtraRecord record : agreementExtraRecord) {

				insertExtra = dslContext
						.insertInto(AGREEMENT_EXTRA)
						.set(AGREEMENT_EXTRA.DOMAIN, domain)
						.set(AGREEMENT_EXTRA.AGREEMENT, newAgreementId)
						.set(AGREEMENT_EXTRA.AGREEMENT_PAYMENT, newPaymentId)
						.set(AGREEMENT_EXTRA.START_DATE,
								record.getValue(AGREEMENT_EXTRA.START_DATE))
						.set(AGREEMENT_EXTRA.END_DATE,
								record.getValue(AGREEMENT_EXTRA.END_DATE))
						.set(AGREEMENT_EXTRA.ISSUE_DATE,
								record.getValue(AGREEMENT_EXTRA.ISSUE_DATE));

				insertExtra.execute();
			}
		}
	}

	private static void insert2PayrollWorkplace(DSLContext dslContext,
			Integer id, Integer domain) throws Exception {

		InsertSetMoreStep<PayrollWorkplaceRecord> insertPayroll = null;

		// @formatter:off
		List<PayrollWorkplaceRecord> payrollWorkplaceRecord = dslContext
				.selectFrom(PAYROLL_WORKPLACE)
				.where(PAYROLL_WORKPLACE.AGREEMENT.eq(id))
				.fetchInto(PAYROLL_WORKPLACE);
		// @formatter:on

		if (payrollWorkplaceRecord != null) {

			for (PayrollWorkplaceRecord record : payrollWorkplaceRecord) {

				insertPayroll = dslContext
						.insertInto(PAYROLL_WORKPLACE)
						.set(PAYROLL_WORKPLACE.DOMAIN, domain)
						.set(PAYROLL_WORKPLACE.WORKPLACE,
								record.getValue(PAYROLL_WORKPLACE.WORKPLACE))
						.set(PAYROLL_WORKPLACE.AGREEMENT, id)
						.set(PAYROLL_WORKPLACE.ENTERPRISE_ACTIVITY,
								record.getValue(PAYROLL_WORKPLACE.ENTERPRISE_ACTIVITY))
						.set(PAYROLL_WORKPLACE.CALENDAR,
								record.getValue(PAYROLL_WORKPLACE.CALENDAR));

				insertPayroll.execute();
			}
		}
	}

	// ------------------------------------------------------------------------

	private static void insertAgreementPayment(DSLContext dslContext,
			Integer domainId, Integer agreementId, Integer paymentId,
			Payment payment) throws SQLException {

		// @formatter:off
		dslContext
				.insertInto(AGREEMENT_PAYMENT)
				.set(AGREEMENT_PAYMENT.ID, paymentId)
				.set(AGREEMENT_PAYMENT.DOMAIN, domainId)
				.set(AGREEMENT_PAYMENT.AGREEMENT, agreementId)
				.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, payment.getConceptId())
				.set(AGREEMENT_PAYMENT.EXPRESSION, payment.getExpression())
				.set(AGREEMENT_PAYMENT.DESCRIPTION, payment.getDescription())
				.set(AGREEMENT_PAYMENT.IRPF_EXPRESSION,
						payment.getIrpfExpression())
				.set(AGREEMENT_PAYMENT.QUOTE_EXPRESSION,
						payment.getQuoteExpression())
				.set(AGREEMENT_PAYMENT.SALARY_TYPE,
						(byte) payment.getSalaryType().ordinal())
				.set(AGREEMENT_PAYMENT.START_DATE,SQL_EPOCH)
				.set(AGREEMENT_PAYMENT.MONTH,
						payment.getMonth() != null ? payment.getMonth()
								.byteValue() : null)
				.set(AGREEMENT_PAYMENT.TYPE,
						payment.getType() != null ? (byte) payment.getType()
								.ordinal() : null)
				.set(AGREEMENT_PAYMENT.END_DATE,SQL_FOREVER)
				.execute();
		// @formatter:on

	}

	private static void updateAgreementPayment(DSLContext dslContext,
			Payment payment) throws SQLException {

		// @formatter:off
		dslContext
				.update(AGREEMENT_PAYMENT)
				.set(AGREEMENT_PAYMENT.EXPRESSION, payment.getExpression())
				.set(AGREEMENT_PAYMENT.DESCRIPTION, payment.getDescription())
				.set(AGREEMENT_PAYMENT.IRPF_EXPRESSION,
						payment.getIrpfExpression())
				.set(AGREEMENT_PAYMENT.QUOTE_EXPRESSION,
						payment.getQuoteExpression())
				.set(AGREEMENT_PAYMENT.SALARY_TYPE,
						(byte) payment.getSalaryType().ordinal())
				.set(AGREEMENT_PAYMENT.MONTH,
						payment.getMonth() != null ? payment.getMonth()
								.byteValue() : null)
				.set(AGREEMENT_PAYMENT.TYPE,
						payment.getType() != null ? (byte) payment.getType()
								.ordinal() : null)
				.set(AGREEMENT_PAYMENT.START_DATE,SQL_EPOCH)
				.set(AGREEMENT_PAYMENT.END_DATE, SQL_FOREVER)
				.where(AGREEMENT_PAYMENT.ID.eq(payment.getId())).execute();
		// @formatter:on

	}

	private static void insertAgreementPayment(DSLContext dslContext,
			int domainId, int agreementId, int conceptId, int paymentId,
			Salary.Type salaryType, Short month, Date startDate, Date endDate)
			throws SQLException {

		// @formatter:off
		dslContext
				.insertInto(AGREEMENT_PAYMENT)
				.set(AGREEMENT_PAYMENT.ID, paymentId)
				.set(AGREEMENT_PAYMENT.DOMAIN, domainId)
				.set(AGREEMENT_PAYMENT.AGREEMENT, agreementId)
				.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, conceptId)
				.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte) salaryType.ordinal())
				.set(AGREEMENT_PAYMENT.MONTH,
						month != null ? month.byteValue() : null)
				.set(AGREEMENT_PAYMENT.START_DATE,SQL_EPOCH)
				.set(AGREEMENT_PAYMENT.END_DATE,SQL_FOREVER)
				.execute();
		// @formatter:on

	}

	private static void updateAgreementPayment(DSLContext dslContext,
			int paymentId, String description, Salary.Type salaryType, Short month, Date startDate,
			Date endDate) throws SQLException {

		// @formatter:off
		dslContext
				.update(AGREEMENT_PAYMENT)
				.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte) salaryType.ordinal())
				.set(AGREEMENT_PAYMENT.DESCRIPTION, description)
				.set(AGREEMENT_PAYMENT.MONTH,
						month != null ? month.byteValue() : null)
				.set(AGREEMENT_PAYMENT.START_DATE,
						new java.sql.Date(startDate.getTime()))
				.set(AGREEMENT_PAYMENT.END_DATE,
						endDate != null ? new java.sql.Date(endDate.getTime())
								: null)
				.where(AGREEMENT_PAYMENT.ID.eq(paymentId)).execute();

		// @formatter:on

	}

	private static void insertPaymentConcept(DSLContext dslContext,
			Integer domainId, Integer paymentId, Integer conceptId,
			Payment payment) throws SQLException {

		
		Payment.Type type = payment.getType();

		// @formatter:off
		dslContext
				.insertInto(PAYMENT_CONCEPT)
				.set(PAYMENT_CONCEPT.ID, conceptId)
				.set(PAYMENT_CONCEPT.DOMAIN, domainId)
				.set(PAYMENT_CONCEPT.TYPE, (byte) type.ordinal())
				.set(PAYMENT_CONCEPT.EXPRESSION, payment.getExpression())
				.set(PAYMENT_CONCEPT.DESCRIPTION, payment.getDescription())
				.set(PAYMENT_CONCEPT.CODE,getConceptCode(paymentId,payment))
				.set(PAYMENT_CONCEPT.IRPF_EXPRESSION,
						payment.getIrpfExpression())
				.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION,
						payment.getQuoteExpression()).execute();
		// @formatter:on

	}

	private static void updatePaymentConcept(DSLContext dslContext,
			Payment payment) throws SQLException {

		Payment.Type type = payment.getType();

		// @formatter:off
		dslContext
				.update(PAYMENT_CONCEPT)
				.set(PAYMENT_CONCEPT.TYPE, (byte) type.ordinal())
				.set(PAYMENT_CONCEPT.EXPRESSION, payment.getExpression())
				.set(PAYMENT_CONCEPT.DESCRIPTION, payment.getDescription())
				.set(PAYMENT_CONCEPT.CODE,getConceptCode(payment.getConceptId(),payment))
				.set(PAYMENT_CONCEPT.IRPF_EXPRESSION,
						payment.getIrpfExpression())
				.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION,
						payment.getQuoteExpression())
				.where(PAYMENT_CONCEPT.ID.eq(payment.getConceptId())).execute();
		// @formatter:on

	}

	private static int max(DSLContext dslContext, Identity<?, Integer> identity) {
		AggregateFunction<Integer> maxFunc = DSL.max(identity.getField());

		Integer max = dslContext.select(maxFunc).from(identity.getTable())
				.forUpdate().fetchOne(maxFunc);

		return max == null ? 0 : max; // null if the query returned no records.
	}

	private static int min(DSLContext dslContext, Identity<?, Integer> identity) {
		AggregateFunction<Integer> minFunc = DSL.min(identity.getField());

		Integer min = dslContext.select(minFunc).from(identity.getTable())
				.forUpdate().fetchOne(minFunc);

		return min == null ? 0 : min; // null if the query returned no records.
	}

	private static Byte getExtraMonth(String extraDate) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("d/M");
		try {
			Date date = dateFormat.parse(extraDate);
			return (byte) date.getMonth();
		} catch (ParseException e) {
			return null;
		}
	}
	
	private static String getConceptCode(Integer paymentId, Payment payment) {
		if ( AonStringUtils.isNotBlank(payment.getName()))
			return payment.getName();
		
		return String.format("__%d", Math.abs(paymentId));

	}

}
