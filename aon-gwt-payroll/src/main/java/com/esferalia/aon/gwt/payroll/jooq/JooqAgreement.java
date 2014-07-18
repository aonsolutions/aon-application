package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;

import org.jooq.AggregateFunction;
import org.jooq.DSLContext;
import org.jooq.Identity;
import org.jooq.InsertSetMoreStep;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.jooq.tables.ContractPayment;
import com.esferalia.aon.jooq.tables.records.AgreementPaymentRecord;
import com.esferalia.aon.payroll.calculator.jooq.JooqCommon;

public class JooqAgreement extends org.jooq.impl.AbstractKeys {

	public static void updatePayment(Connection conn, Payment payment)
			throws SQLException {
		updatePayment(DSL.using(conn, JooqCommon.getDefaultSettings()), payment);
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
		removePayment(DSL.using(conn, JooqCommon.getDefaultSettings()), payment);
	}

	public static void removePayment(DSLContext dslContext, Payment payment)
			throws SQLException {

		dslContext.delete(AGREEMENT_PAYMENT)
				.where(AGREEMENT_PAYMENT.ID.eq(payment.getId())).execute();

		if (payment.getConceptId() != null && payment.getConceptId() < 0){
			int conceptId = payment.getConceptId();
			//@formatter:on
			dslContext.update(CONTRACT_PAYMENT)
			.set(CONTRACT_PAYMENT.TYPE, (byte)payment.getType().ordinal())
			.where(CONTRACT_PAYMENT.TYPE.isNull()
			.and(CONTRACT_PAYMENT.PAYMENT_CONCEPT.eq(conceptId)))
			.execute();
			dslContext.update(CONTRACT_PAYMENT)
			.set(CONTRACT_PAYMENT.DESCRIPTION, payment.getDescription())
			.where(CONTRACT_PAYMENT.DESCRIPTION.isNull()
			.and(CONTRACT_PAYMENT.PAYMENT_CONCEPT.eq(conceptId)))
			.execute();
			dslContext.update(CONTRACT_PAYMENT)
			.set(CONTRACT_PAYMENT.EXPRESSION, payment.getExpression())
			.where(CONTRACT_PAYMENT.EXPRESSION.isNull()
			.and(CONTRACT_PAYMENT.PAYMENT_CONCEPT.eq(conceptId)))
			.execute();
			dslContext.update(CONTRACT_PAYMENT)
			.set(CONTRACT_PAYMENT.IRPF_EXPRESSION, payment.getIrpfExpression())
			.where(CONTRACT_PAYMENT.IRPF_EXPRESSION.isNull()
			.and(CONTRACT_PAYMENT.PAYMENT_CONCEPT.eq(conceptId)))
			.execute();
			dslContext.update(CONTRACT_PAYMENT)
			.set(CONTRACT_PAYMENT.QUOTE_EXPRESSION, payment.getQuoteExpression())
			.where(CONTRACT_PAYMENT.QUOTE_EXPRESSION.isNull()
			.and(CONTRACT_PAYMENT.PAYMENT_CONCEPT.eq(conceptId)))
			.execute();
			dslContext.update(CONTRACT_PAYMENT)
			.set(CONTRACT_PAYMENT.PAYMENT_CONCEPT, (Integer)null)
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
		return insertPayment(DSL.using(conn, JooqCommon.getDefaultSettings()),
				domainId, agreementId, payment);
	}

	public static int insertPayment(DSLContext dslContext, Integer domainId,
			Integer agreementId, Payment payment) throws SQLException {

		int paymentId = max(dslContext, AGREEMENT_PAYMENT.getIdentity())+1;

		if (payment.getConceptId() == null) {
			int min = min(dslContext, PAYMENT_CONCEPT.getIdentity());
			int conceptId = Math.min(-1, min - 1);
			insertPaymentConcept(dslContext, domainId, conceptId, payment);
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
			Integer domainId, Integer conceptId, Payment payment)
			throws SQLException {

		Payment.Type type = payment.getType();

		//@formatter:off
		dslContext.insertInto(PAYMENT_CONCEPT)
		.set(PAYMENT_CONCEPT.ID, conceptId)
		.set(PAYMENT_CONCEPT.DOMAIN, domainId)
		.set(PAYMENT_CONCEPT.TYPE, (byte) type.ordinal())
		.set(PAYMENT_CONCEPT.CODE, String.format("__%d", Math.abs(conceptId) ))
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

	public static int min(DSLContext dslContext, Identity<?, Integer> identity) {
		AggregateFunction<Integer> minFunc = DSL.min(identity.getField());

		Integer min = dslContext.select(minFunc).from(identity.getTable())
				.forUpdate().fetchOne(minFunc);

		return min == null ? 0 : min; // null if the query returned no records.
	}

	public static void main(String[] args) throws ClassNotFoundException,
			SQLException {

		Class.forName("com.mysql.jdbc.Driver");
		Connection connection = DriverManager.getConnection(
				"jdbc:mysql://127.0.0.1:3306/pro-aonsolutions-net", "aon",
				"40n");
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		DSLContext dslContext = DSL.using(connection, settings);

	}
}
