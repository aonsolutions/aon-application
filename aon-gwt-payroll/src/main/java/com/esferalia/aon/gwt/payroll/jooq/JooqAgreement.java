package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementData.AGREEMENT_DATA;
import static com.esferalia.aon.jooq.tables.AgreementExtra.AGREEMENT_EXTRA;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;
import static com.esferalia.aon.payroll.calculator.jooq.JooqCommon.getDefaultSettings;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.jooq.AggregateFunction;
import org.jooq.DSLContext;
import org.jooq.Identity;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.jooq.tables.PayrollWorkplace;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;

public class JooqAgreement extends org.jooq.impl.AbstractKeys {

	public static void updatePayment(Connection conn, Payment payment)
			throws SQLException {
		updatePayment(DSL.using(conn, getDefaultSettings()), payment);
	}

	public static void updatePayment(DSLContext dslContext, Payment payment)
			throws SQLException {
		if (payment.getConceptId() != null && payment.getConceptId() < 0) {
			updatePaymentConcept(dslContext, payment);
			updateAgreementPayment(dslContext, payment.getId(),
					payment.getSalaryType(), payment.getMonth(),
					payment.getStartDate(), payment.getEndDate());
		} else {
			updateAgreementPayment(dslContext, payment);
		}

	}

	public static void removePayment(Connection conn, Payment payment)
			throws SQLException {
		removePayment(DSL.using(conn, getDefaultSettings()), payment);
	}

	public static void removePayment(DSLContext dslContext, Payment payment)
			throws SQLException {

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
			//@formatter:off
			dslContext.delete(PAYMENT_CONCEPT)
					.where(PAYMENT_CONCEPT.ID.eq(payment.getConceptId()))
					.execute();
		}
	}

	public static int insertPayment(Connection conn, Integer domainId,
			Integer agreementId, Payment payment) throws SQLException {
		return insertPayment(DSL.using(conn, getDefaultSettings()),
				domainId, agreementId, payment);
	}

	public static int insertPayment(DSLContext dslContext, Integer domainId,
			Integer agreementId, Payment payment) throws SQLException {

		int paymentId = max(dslContext, AGREEMENT_PAYMENT.getIdentity())+1;

		if (payment.getConceptId() == null) {
			int min = min(dslContext, PAYMENT_CONCEPT.getIdentity());
			int conceptId = Math.min(-1, min - 1);
			insertPaymentConcept(dslContext, domainId, paymentId, conceptId, payment);
			insertAgreementPayment(dslContext, domainId, agreementId,
					conceptId, paymentId, payment.getSalaryType(),
					payment.getMonth(), payment.getStartDate(),
					payment.getEndDate());
		} else {
			insertAgreementPayment(dslContext, domainId, agreementId,
					paymentId, payment);
		}

		return paymentId;
	}
	
	public static void insertExtra(Connection conn, Integer domainId,
			Integer agreementId, Extra extra) throws SQLException {
		insertExtra(DSL.using(conn, getDefaultSettings()), domainId, agreementId, extra);
	}

	public static void insertExtra(DSLContext dslContext, Integer domainId,
			Integer agreementId, Extra extra) throws SQLException {
		//@formatter:off
		dslContext.insertInto(AGREEMENT_EXTRA)
		.set(AGREEMENT_EXTRA.DOMAIN, domainId)
		.set(AGREEMENT_EXTRA.AGREEMENT, agreementId)
		.set(AGREEMENT_EXTRA.START_DATE, extra.getStartDate())
		.set(AGREEMENT_EXTRA.END_DATE, extra.getEndDate())
		.set(AGREEMENT_EXTRA.ISSUE_DATE, extra.getIssueDate())
		.set(AGREEMENT_EXTRA.AGREEMENT_PAYMENT, extra.getPaymentId())
		.execute();
		//@formatter:on

		if (extra.getPaymentId() == null)
			return;

		//@formatter:off
		dslContext.update(AGREEMENT_PAYMENT)
		.set(AGREEMENT_PAYMENT.MONTH, getExtraMonth(extra.getIssueDate()))
		.where(AGREEMENT_PAYMENT.ID.eq(extra.getPaymentId()))
		.execute();
		//@formatter:on
	}

	public static void updateExtra(Connection conn, Extra extra)
			throws SQLException {
		updateExtra(DSL.using(conn, getDefaultSettings()), extra);
	}

	public static void updateExtra(DSLContext dslContext, Extra extra)
			throws SQLException {

		//@formatter:off
		dslContext.update(AGREEMENT_EXTRA)
		.set(AGREEMENT_EXTRA.START_DATE, extra.getStartDate())
		.set(AGREEMENT_EXTRA.END_DATE, extra.getEndDate())
		.set(AGREEMENT_EXTRA.ISSUE_DATE, extra.getIssueDate())
		.set(AGREEMENT_EXTRA.AGREEMENT_PAYMENT, extra.getPaymentId())
		.where(AGREEMENT_EXTRA.ID.eq(extra.getId()))
		.execute();
		//@formatter:on

		if (extra.getPaymentId() == null)
			return;

		//@formatter:off
		dslContext.update(AGREEMENT_PAYMENT)
		.set(AGREEMENT_PAYMENT.MONTH, getExtraMonth(extra.getIssueDate()))
		.where(AGREEMENT_PAYMENT.ID.eq(extra.getPaymentId()))
		.execute();
		//@formatter:on

	}

	public static void removeExtra(Connection conn, Integer extraId)
			throws SQLException {
		removeExtra(DSL.using(conn, getDefaultSettings()), extraId);
	}

	public static void removeExtra(DSLContext dslContext, Integer extraId)
			throws SQLException {
		//@formatter:off
		dslContext.delete(AGREEMENT_EXTRA)
		.where(AGREEMENT_EXTRA.ID.eq(extraId))
		.execute();
		//@formatter:on
	}

	public static List<Agreement> getAgreements(Connection conn, int offset,
			int limit, Integer... domains) throws SQLException {
		return getAgreements(DSL.using(conn, getDefaultSettings()), offset, limit,
				domains);
	}

	public static List<Agreement> getAgreements(DSLContext dslContext,
			int offset, int limit, Integer... domains) throws SQLException {

		//@formatter:off
			Result<AgreementRecord> result = dslContext
			.select()
			.from(AGREEMENT)
			.where(AGREEMENT.DOMAIN.in(domains))
			.orderBy(AGREEMENT.DESCRIPTION)
			.fetchInto(AGREEMENT)
			;
			//@formatter:on

		List<Agreement> agreements = new LinkedList<Agreement>();
		for (AgreementRecord record : result) {
			Agreement agreement = new Agreement();

			agreement.setId(record.getId()); // Not NULL
			agreement.setDomain(record.getDomain());
			agreement.setDescription(record.getDescription());
			// agreement.setLevelsWithoutCategories(false);
			// agreement.setEmployees(rs.getInt("EMPLOYEEs"));
			// agreement.setRedefined(rs.getInt("REDEFINED"));
			agreements.add(agreement);

		}
		return agreements;
	}
	
	private static final String SET_FOREIGN_KEY_CHECKS_0 = "SET FOREIGN_KEY_CHECKS=0;";
	private static final String SET_FOREIGN_KEY_CHECKS_1 = "SET FOREIGN_KEY_CHECKS=1;";
	
	public static void updateAgreementId(Connection conn, Integer domainId, Agreement agreement) {
		
		try {			
			
			Statement sOpen = conn.createStatement();
			sOpen.execute(SET_FOREIGN_KEY_CHECKS_0);
			System.out.println("Claves referenciales deshabilitadas");
			sOpen.close();
			
			// -----------------------------
			
			DSLContext dslContext = DSL.using(conn, SQLDialect.MYSQL, 
					getDefaultSettings());
			
			//@formatter:off
			
			dslContext.update(AGREEMENT_DATA)
			.set(AGREEMENT_DATA.AGREEMENT, -(agreement.getId()))
			.where(AGREEMENT_DATA.AGREEMENT.eq(agreement.getId())
					.and(AGREEMENT_DATA.DOMAIN.eq(domainId))).execute();
			
			dslContext.update(AGREEMENT_LEVEL)
			.set(AGREEMENT_LEVEL.AGREEMENT, -(agreement.getId()))
			.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreement.getId())
					.and(AGREEMENT_LEVEL.DOMAIN.eq(domainId))).execute();
			
			dslContext.update(AGREEMENT_PAYMENT)
			.set(AGREEMENT_PAYMENT.AGREEMENT, -(agreement.getId()))
			.where(AGREEMENT_PAYMENT.AGREEMENT.eq(agreement.getId())
					.and(AGREEMENT_PAYMENT.DOMAIN.eq(domainId))).execute();
			
			dslContext.update(PAYROLL_WORKPLACE)
			.set(PAYROLL_WORKPLACE.AGREEMENT, -(agreement.getId()))
			.where(PAYROLL_WORKPLACE.AGREEMENT.eq(agreement.getId())
					.and(PAYROLL_WORKPLACE.DOMAIN.eq(domainId))).execute();
			
			dslContext.update(AGREEMENT)
					  .set(AGREEMENT.ID, -(agreement.getId()))
					  .where(AGREEMENT.DOMAIN.eq(domainId)
							 .and(AGREEMENT.ID.eq(agreement.getId())))
					  .execute();
			//@formatter:on

			
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
	
	

	// ------------------------------------------------------------------------

	private static void insertAgreementPayment(DSLContext dslContext,
			Integer domainId, Integer agreementId, Integer paymentId,
			Payment payment) throws SQLException {

		//@formatter:off
		dslContext.insertInto(AGREEMENT_PAYMENT)
		.set(AGREEMENT_PAYMENT.ID, paymentId )
		.set(AGREEMENT_PAYMENT.DOMAIN, domainId)
		.set(AGREEMENT_PAYMENT.AGREEMENT, agreementId)
		.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, payment.getConceptId())
		.set(AGREEMENT_PAYMENT.EXPRESSION, payment.getExpression())
		.set(AGREEMENT_PAYMENT.DESCRIPTION, payment.getDescription())
		.set(AGREEMENT_PAYMENT.IRPF_EXPRESSION, payment.getIrpfExpression())
		.set(AGREEMENT_PAYMENT.QUOTE_EXPRESSION, payment.getQuoteExpression())
		.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte)payment.getSalaryType().ordinal())
		.set(AGREEMENT_PAYMENT.START_DATE, new java.sql.Date( payment.getStartDate().getTime()))
		.set(AGREEMENT_PAYMENT.MONTH, payment.getMonth() != null ? payment.getMonth().byteValue(): null )
		.set(AGREEMENT_PAYMENT.TYPE, payment.getType() != null ? (byte)payment.getType().ordinal(): null )
		.set(AGREEMENT_PAYMENT.END_DATE, payment.getEndDate() != null ? new java.sql.Date( payment.getEndDate().getTime()): null )
		.execute();
		//@formatter:on

	}

	private static void updateAgreementPayment(DSLContext dslContext,
			Payment payment) throws SQLException {

		//@formatter:off
		dslContext.update(AGREEMENT_PAYMENT)
		.set(AGREEMENT_PAYMENT.EXPRESSION, payment.getExpression())
		.set(AGREEMENT_PAYMENT.DESCRIPTION, payment.getDescription())
		.set(AGREEMENT_PAYMENT.IRPF_EXPRESSION, payment.getIrpfExpression())
		.set(AGREEMENT_PAYMENT.QUOTE_EXPRESSION, payment.getQuoteExpression())
		.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte)payment.getSalaryType().ordinal())
		.set(AGREEMENT_PAYMENT.START_DATE, new java.sql.Date( payment.getStartDate().getTime()))
		.set(AGREEMENT_PAYMENT.MONTH, payment.getMonth() != null ? payment.getMonth().byteValue() : null )
		.set(AGREEMENT_PAYMENT.TYPE, payment.getType() != null ? (byte)payment.getType().ordinal() : null )
		.set(AGREEMENT_PAYMENT.END_DATE, payment.getEndDate() != null ? new java.sql.Date( payment.getEndDate().getTime()): null)
		.where(AGREEMENT_PAYMENT.ID.eq(payment.getId()))
		.execute()
		;
		//@formatter:on

	}

	private static void insertAgreementPayment(DSLContext dslContext,
			int domainId, int agreementId, int conceptId, int paymentId,
			Salary.Type salaryType, Short month, Date startDate, Date endDate)
			throws SQLException {

		//@formatter:off
		dslContext.insertInto(AGREEMENT_PAYMENT)
		.set(AGREEMENT_PAYMENT.ID, paymentId)
		.set(AGREEMENT_PAYMENT.DOMAIN, domainId)
		.set(AGREEMENT_PAYMENT.AGREEMENT, agreementId)
		.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, conceptId)
		.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte)salaryType.ordinal())
		.set(AGREEMENT_PAYMENT.MONTH, month != null ? month.byteValue(): null )
		.set(AGREEMENT_PAYMENT.START_DATE, new java.sql.Date( startDate.getTime()))
		.set(AGREEMENT_PAYMENT.END_DATE, endDate != null ? new java.sql.Date( endDate.getTime()): null)
		.execute();
		//@formatter:on

	}

	private static void updateAgreementPayment(DSLContext dslContext,
			int paymentId, Salary.Type salaryType, Short month, Date startDate,
			Date endDate) throws SQLException {

		//@formatter:off
		dslContext.update(AGREEMENT_PAYMENT)
		.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte)salaryType.ordinal())
		.set(AGREEMENT_PAYMENT.MONTH, month != null ? month.byteValue(): null )
		.set(AGREEMENT_PAYMENT.START_DATE, new java.sql.Date( startDate.getTime()))
		.set(AGREEMENT_PAYMENT.END_DATE, endDate != null ? new java.sql.Date( endDate.getTime()): null)
		.where(AGREEMENT_PAYMENT.ID.eq(paymentId))
		.execute();
		
		//@formatter:on

	}

	private static void insertPaymentConcept(DSLContext dslContext,
			Integer domainId, Integer paymentId, Integer conceptId,
			Payment payment) throws SQLException {

		Payment.Type type = payment.getType();

		//@formatter:off
		dslContext.insertInto(PAYMENT_CONCEPT)
		.set(PAYMENT_CONCEPT.ID, conceptId)
		.set(PAYMENT_CONCEPT.DOMAIN, domainId)
		.set(PAYMENT_CONCEPT.TYPE, (byte) type.ordinal())
		.set(PAYMENT_CONCEPT.CODE, String.format("__%d", Math.abs(paymentId) ))
		.set(PAYMENT_CONCEPT.EXPRESSION, payment.getExpression())
		.set(PAYMENT_CONCEPT.DESCRIPTION, payment.getDescription())
		.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, payment.getIrpfExpression())
		.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, payment.getQuoteExpression())
		.execute();
		//@formatter:on

	}

	private static void updatePaymentConcept(DSLContext dslContext,
			Payment payment) throws SQLException {

		Payment.Type type = payment.getType();

		//@formatter:off
		dslContext.update(PAYMENT_CONCEPT)
		.set(PAYMENT_CONCEPT.TYPE, (byte) type.ordinal())
		.set(PAYMENT_CONCEPT.EXPRESSION, payment.getExpression())
		.set(PAYMENT_CONCEPT.DESCRIPTION, payment.getDescription())
		.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, payment.getIrpfExpression())
		.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, payment.getQuoteExpression())
		.where(PAYMENT_CONCEPT.ID.eq(payment.getConceptId()))
		.execute();
		//@formatter:on

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

}
