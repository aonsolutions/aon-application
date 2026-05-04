package com.esferalia.aon.occam.impl.jooq;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.jooq.Field;
import org.jooq.Identity;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.AgreementPaymentRecord;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.IAgreement;
import com.esferalia.aon.occam.api.model.Agreement;
import com.esferalia.aon.occam.api.model.Payment;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AgreementImpl implements IAgreement {

	@Override
	public void save(AONContext ctx, Agreement... agreements)
			throws AonCoreException {

		// Streams ? .
		InsertSetMoreStep<AgreementRecord> agreementInsertSetMoreStep = null;
		InsertSetMoreStep<PaymentConceptRecord> conceptInsertSetMoreStep = null;
		InsertSetMoreStep<AgreementPaymentRecord> agreementPaymentInsertSetMoreStep = null;
		
		int nextAgreementId = getNextId(ctx, AGREEMENT.getIdentity());
		int nextAgreementPaymentId = getNextId(ctx, AGREEMENT_PAYMENT.getIdentity());

		for (Agreement agreement : agreements) {
			if (agreement.getId() == null)
				agreement.setId(nextAgreementId++);

			//@formatter:off
			agreementInsertSetMoreStep = ( 
					agreementInsertSetMoreStep != null ? 
					agreementInsertSetMoreStep.newRecord() : 
					ctx.getDslContext().insertInto(AGREEMENT) ) 
					.set(AGREEMENT.ID, agreement.getId())
					.set(AGREEMENT.DOMAIN, agreement.getDomain())
					.set(AGREEMENT.DESCRIPTION, agreement.getDescription())
					;
			//@formatter:on

			for (Payment payment : agreement.getPayments()) {
				if (payment.getId() == null)
					payment.setId(nextAgreementPaymentId++);
				
				PaymentConceptRecord concept = null;
				try {
					concept = getConcept4(ctx, agreement, payment);
					//@formatter:off
					agreementPaymentInsertSetMoreStep = ( 
							agreementPaymentInsertSetMoreStep != null ? 
							agreementPaymentInsertSetMoreStep.newRecord() : 
							ctx.getDslContext().insertInto(AGREEMENT_PAYMENT) ) 
							.set(AGREEMENT_PAYMENT.ID, payment.getId())
							.set(AGREEMENT_PAYMENT.DOMAIN, payment.getDomain())
							.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, concept.getId())
							.set(AGREEMENT_PAYMENT.AGREEMENT, agreement.getId())
							.set(AGREEMENT_PAYMENT.START_DATE,new Date(payment.getStartDate().getTime()))
							.set(AGREEMENT_PAYMENT.END_DATE,new Date(payment.getStartDate().getTime()))
							.set(AGREEMENT_PAYMENT.MONTH, (byte) payment.getMonth().ordinal());
					//@formatter:on

					if (AonStringUtils.equals(payment.getDescription(),
							concept.getDescription()))
						agreementPaymentInsertSetMoreStep = agreementPaymentInsertSetMoreStep
								.set(AGREEMENT_PAYMENT.DESCRIPTION,
										payment.getDescription());

					if (AonStringUtils.equals(payment.getExpression(),
							concept.getExpression()))
						agreementPaymentInsertSetMoreStep = agreementPaymentInsertSetMoreStep
								.set(AGREEMENT_PAYMENT.EXPRESSION,
										payment.getExpression());

					if (AonStringUtils.equals(payment.getIrpfExpression(),
							concept.getIrpfExpression()))
						agreementPaymentInsertSetMoreStep = agreementPaymentInsertSetMoreStep
								.set(AGREEMENT_PAYMENT.IRPF_EXPRESSION,
										payment.getIrpfExpression());

					if (AonStringUtils.equals(payment.getQuoteExpression(),
							concept.getQuoteExpression()))
						agreementPaymentInsertSetMoreStep = agreementPaymentInsertSetMoreStep
								.set(AGREEMENT_PAYMENT.QUOTE_EXPRESSION,
										payment.getQuoteExpression());

					if (payment.getType().ordinal() == concept.getType())
						agreementPaymentInsertSetMoreStep = agreementPaymentInsertSetMoreStep
								.set(AGREEMENT_PAYMENT.TYPE, (byte) payment
										.getType().ordinal());

					;
				} catch (NoSuchElementException e) {
					//@formatter:off
					conceptInsertSetMoreStep = ( 
							conceptInsertSetMoreStep != null ? 
							conceptInsertSetMoreStep.newRecord() : 
							ctx.getDslContext().insertInto(PAYMENT_CONCEPT ))
							.set(PAYMENT_CONCEPT.ID, (-1)*payment.getId())
							.set(PAYMENT_CONCEPT.CODE, payment.getCode() )
							.set(PAYMENT_CONCEPT.DOMAIN, payment.getDomain())
							.set(PAYMENT_CONCEPT.DESCRIPTION,payment.getDescription())
							.set(PAYMENT_CONCEPT.EXPRESSION,payment.getExpression())
							.set(PAYMENT_CONCEPT.IRPF_EXPRESSION,payment.getIrpfExpression())
							.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION,payment.getQuoteExpression())
							.set(PAYMENT_CONCEPT.TYPE, (byte) payment.getType().ordinal())
							;
					//@formatter:on

					//@formatter:off
					agreementPaymentInsertSetMoreStep = ( 
							agreementPaymentInsertSetMoreStep != null ? 
							agreementPaymentInsertSetMoreStep.newRecord() : 
							ctx.getDslContext().insertInto(AGREEMENT_PAYMENT) ) 
							.set(AGREEMENT_PAYMENT.ID, payment.getId())
							.set(AGREEMENT_PAYMENT.DOMAIN, payment.getDomain())
							.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, (-1)*payment.getId())
							.set(AGREEMENT_PAYMENT.AGREEMENT, agreement.getId())
							.set(AGREEMENT_PAYMENT.START_DATE,new Date(payment.getStartDate().getTime()))
							.set(AGREEMENT_PAYMENT.END_DATE,new Date(payment.getStartDate().getTime()))
							.set(AGREEMENT_PAYMENT.MONTH,  AonEnumUtils.getByte( payment.getMonth() ) )
							;
					//@formatter:on
				}

			}

		}

		agreementInsertSetMoreStep.execute();
		conceptInsertSetMoreStep.execute();
		agreementPaymentInsertSetMoreStep.execute();
	}

	private static PaymentConceptRecord getConcept4(AONContext ctx,
			Agreement agreement, Payment payment) {
		if (AonStringUtils.isBlank(payment.getCode()))
			throw new NoSuchElementException();

		final List<Integer> domains = Arrays.asList(agreement.getDomain(),
				payment.getDomain(), 0);

		// @formatter:off
		return
		ctx.getDslContext()
		.select()
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.CODE.eq(payment.getCode()))
		.and(PAYMENT_CONCEPT.DOMAIN.in(domains))
		.fetchInto(PAYMENT_CONCEPT)
		.stream()
		.sorted((c1, c2)-> domains.indexOf(c1.getDomain())-domains.indexOf(c2.getDomain()))
		.findFirst()
		.get();
		// @formatter:on

	}

	private static <R extends Record> int getNextId(AONContext ctx,
			Identity<R, Integer> id) {
		Field<Integer> max = DSL.max(id.getField());
		Integer next = ctx.getDslContext().select(max).from(id.getTable()).fetchOne(max);
		return next == null ? 1 : next + 1;
	}

	@Override
	public List<Agreement> getAgreements(CloseableAONContext ctx, Integer domainId) throws AonCoreException {
		Result<AgreementRecord> agreementRecords = ctx.getDslContext().selectFrom(AGREEMENT)
		 .where(AGREEMENT.DOMAIN.eq(domainId))
		 .fetch();
		
		List<Agreement> agreements = new ArrayList<Agreement>();
		
		agreementRecords.forEach(a -> {
			agreements.add(
				new Agreement()
					.setId(a.getId())
					.setDomain(a.getDomain())
					.setDescription(a.getDescription())
			);
		});
		
		return agreements;
	}

}
