package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementData.AGREEMENT_DATA;
import static com.esferalia.aon.jooq.tables.AgreementExtra.AGREEMENT_EXTRA;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AgreementLevelData.AGREEMENT_LEVEL_DATA;
import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.shared.AgreementIntegrity;
import com.esferalia.aon.gwt.payroll.shared.AgreementIntegrity.AgreementExtra;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;
import com.esferalia.aon.watson.util.AonStringUtils;

public class JooqAgreementIntegrity {

	private static Settings settings;
	
	// ------------------------------- Construtor
	
	private JooqAgreementIntegrity() {
		super();
	}
	
	// ------------------------------- Auxiliar Methods
	
	protected static Settings getDefaultSettings() {
		if (settings == null) {
			settings = new Settings();
			settings.setRenderSchema(false);
		}
		return settings;
	}

	public static AgreementIntegrity checkIntegrity(Connection connection, Integer domainId, Integer agreementId) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		AgreementIntegrity agreementIntegrity = new AgreementIntegrity();
		
		AgreementRecord agreement = dslContext.selectFrom(AGREEMENT).where(AGREEMENT.ID.eq(agreementId)).fetchOne();
		agreementId = agreement.getId();
		Integer agreementDomain = agreement.getDomain();
		
		agreementIntegrity.setId(agreementId);
		agreementIntegrity.setDomain(agreementDomain);
		
		Result<Record> agreementPayments = dslContext.select()
				.from(AGREEMENT_PAYMENT)
				.leftOuterJoin(PAYMENT_CONCEPT).on(PAYMENT_CONCEPT.ID.eq(AGREEMENT_PAYMENT.PAYMENT_CONCEPT))
				.where(AGREEMENT_PAYMENT.AGREEMENT.eq(agreementId)).fetch();
		
		agreementIntegrity.setAgreementPayments(parseAgreementPayments(agreementPayments));
		
		// Agreement Payments from other domain
		List<Record> otherDomainAgreementPayments = agreementPayments.stream()
				.filter(agreementPayment -> agreementPayment.get(AGREEMENT_PAYMENT.DOMAIN) != agreementDomain)
				.collect(Collectors.toList());
		
		agreementIntegrity.setOtherDomainAgreementPayments(parseAgreementPayments(otherDomainAgreementPayments));
		
		// Agreement Payments with out payment_concept
		List<Record> noPaymentConceptAgreementPayments = agreementPayments.stream()
				.filter(agreementPayment -> agreementPayment.get(PAYMENT_CONCEPT.ID) == null)
				.collect(Collectors.toList());
		
		agreementIntegrity.setNoPaymentConceptAgreementPayments(parseAgreementPayments(noPaymentConceptAgreementPayments));
		
		// Agreement Payments with payment_concept from other domain
		List<Record> otherDomainPaymentConcepts = agreementPayments.stream()
				.filter(agreementPayment -> agreementPayment.get(PAYMENT_CONCEPT.ID) != null && agreementPayment.get(PAYMENT_CONCEPT.DOMAIN) != agreementDomain)
				.collect(Collectors.toList());
		
		agreementIntegrity.setOtherDomainPaymentConcepts(parseAgreementPayments(otherDomainPaymentConcepts));
		
		// Payment Concepts with out code
		List<Record> paymentConceptsNoCode = agreementPayments.stream()
				.filter(agreementPayment -> agreementPayment.get(PAYMENT_CONCEPT.ID) != null && null == agreementPayment.get(PAYMENT_CONCEPT.CODE))
				.collect(Collectors.toList());
		
		agreementIntegrity.setPaymentConceptsNoCode(parseAgreementPayments(paymentConceptsNoCode));
		
		// Get variables
		Set<String> variables = new HashSet<String>();
		
		List<String> agreementDatas = dslContext.selectDistinct(AGREEMENT_DATA.NAME)
				.from(AGREEMENT_DATA)
				.where(AGREEMENT_DATA.AGREEMENT.eq(agreementId))
				.fetch(AGREEMENT_DATA.NAME);
		
		variables.addAll(agreementDatas);
		
		List<String> agreementLevelDatas = dslContext.selectDistinct(AGREEMENT_LEVEL_DATA.NAME)
				.from(AGREEMENT_LEVEL_DATA)
				.join(AGREEMENT_LEVEL).on(AGREEMENT_LEVEL.ID.eq(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL))
				.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreementId))
				.fetch(AGREEMENT_LEVEL_DATA.NAME);
		
		variables.addAll(agreementLevelDatas);
		
		// Expressions with var like payment_concept code
		List<Record> codeInExpression = agreementPayments.stream()
				.filter(agreementPayment -> 
						agreementPayment.get(PAYMENT_CONCEPT.ID) != null && 
						null != agreementPayment.get(PAYMENT_CONCEPT.CODE) && 
						(
							expressionCodeCheck(agreementPayment.get(PAYMENT_CONCEPT.CODE), agreementPayment.get(AGREEMENT_PAYMENT.EXPRESSION)) ||
							expressionCodeCheck(agreementPayment.get(PAYMENT_CONCEPT.CODE), agreementPayment.get(PAYMENT_CONCEPT.EXPRESSION))
						)
				)
				.collect(Collectors.toList());
		
		agreementIntegrity.setCodeInExpression(parseAgreementPayments(codeInExpression));
		
		// Var like payment_concept code
		List<String> variableLikeCodes = agreementPayments.stream()
				.filter(agreementPayment -> 
						agreementPayment.get(PAYMENT_CONCEPT.ID) != null && 
						null != agreementPayment.get(PAYMENT_CONCEPT.CODE) && 
						variables.contains(agreementPayment.get(PAYMENT_CONCEPT.CODE))
				)
				.map(agreementPayment -> agreementPayment.get(PAYMENT_CONCEPT.CODE))
				.collect(Collectors.toList());
		
		agreementIntegrity.setVariableLikeCodes(variableLikeCodes);
		
		// Contract Payments
		Result<Record> contractPayments = dslContext.select()
				.from(CONTRACT_PAYMENT)
				.join(CONTRACT).on(CONTRACT.ID.eq(CONTRACT_PAYMENT.CONTRACT))
				.join(AGREEMENT_LEVEL).on(AGREEMENT_LEVEL.ID.eq(CONTRACT.AGREEMENT_LEVEL))
				.leftOuterJoin(PAYMENT_CONCEPT).on(PAYMENT_CONCEPT.ID.eq(CONTRACT_PAYMENT.PAYMENT_CONCEPT))
				.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreementId)).fetch();
		
		// Contract Payments with payment_concept from other domain (only accepts other domain if its agreement domain)
		List<Record> otherDomainPaymentConceptContracts = contractPayments.stream()
				.filter(contractPayment -> 
					contractPayment.get(PAYMENT_CONCEPT.ID) != null && 
					contractPayment.get(PAYMENT_CONCEPT.DOMAIN) != agreementDomain &&
					contractPayment.get(PAYMENT_CONCEPT.DOMAIN) != contractPayment.get(CONTRACT.DOMAIN))
				.collect(Collectors.toList());
		
		
		agreementIntegrity.setOtherDomainPaymentConceptContracts(parseAgreementPayments(otherDomainPaymentConceptContracts));
		
		// Payment Concepts with out code
		List<Record> paymentConceptsNoCodeContracts = contractPayments.stream()
				.filter(contractPayment -> 
					contractPayment.get(PAYMENT_CONCEPT.ID) != null && 
					null == contractPayment.get(PAYMENT_CONCEPT.CODE))
				.collect(Collectors.toList());
		
		agreementIntegrity.setPaymentConceptsNoCodeContracts(parseAgreementPayments(paymentConceptsNoCodeContracts));
		
		// Payment Concept with no agreement_payment or contract_payment
		Result<Record> paymentConceptsNoRef = dslContext.select()
				.from(PAYMENT_CONCEPT)
				.leftJoin(AGREEMENT_PAYMENT).on(AGREEMENT_PAYMENT.PAYMENT_CONCEPT.eq(PAYMENT_CONCEPT.ID))
				.leftJoin(CONTRACT_PAYMENT).on(CONTRACT_PAYMENT.PAYMENT_CONCEPT.eq(PAYMENT_CONCEPT.ID))
				.where(AGREEMENT_PAYMENT.PAYMENT_CONCEPT.isNull())
				.and(CONTRACT_PAYMENT.PAYMENT_CONCEPT.isNull())
				.and(PAYMENT_CONCEPT.DOMAIN.gt(0).and(PAYMENT_CONCEPT.DOMAIN.eq(domainId)))
				.fetch();
		
		agreementIntegrity.setPaymentConceptsNoRef(parseAgreementPayments(paymentConceptsNoRef));
		
		// Agreement Payment Extra wrong format 
		Result<Record> agreementExtras = dslContext.select()
				.from(AGREEMENT_EXTRA)
				.join(AGREEMENT_PAYMENT).on(AGREEMENT_PAYMENT.ID.eq(AGREEMENT_EXTRA.AGREEMENT_PAYMENT))
				.leftOuterJoin(PAYMENT_CONCEPT).on(PAYMENT_CONCEPT.ID.eq(AGREEMENT_PAYMENT.PAYMENT_CONCEPT))
				.where(AGREEMENT_PAYMENT.AGREEMENT.eq(agreementId))
				.and(
					AGREEMENT_EXTRA.START_DATE.notLikeRegex("^\\d{2}/\\d{2}( -1)?$")
		            .or(AGREEMENT_EXTRA.END_DATE.notLikeRegex("^\\d{2}/\\d{2}( -1)?$"))
		            .or(AGREEMENT_EXTRA.ISSUE_DATE.notLikeRegex("^\\d{2}/\\d{2}$"))
				).fetch();
		
		agreementIntegrity.setAgreementExtras(parseAgreementExtras(agreementExtras));
		
		return agreementIntegrity;
	}

	private static boolean expressionCodeCheck(String code, String expression) {
		if(AonStringUtils.isBlank(code) || AonStringUtils.isBlank(expression)) return false;
		
		Pattern pattern = Pattern.compile("\\b" + Pattern.quote(code) + "\\b");
        Matcher matcher = pattern.matcher(expression);
		return matcher.find() ;
	}

	private static List<Payment> parseAgreementPayments(List<Record> payments) {
		List<Payment> otherDomainAgreementPayments = new ArrayList<Payment>();
		
		payments.forEach(paymentIt -> {
			
			Payment payment = new Payment();
			payment.setId(null != paymentIt.get(AGREEMENT_PAYMENT.ID) ? paymentIt.get(AGREEMENT_PAYMENT.ID) : paymentIt.get(PAYMENT_CONCEPT.ID));
			payment.setDomain(null != paymentIt.get(AGREEMENT_PAYMENT.DOMAIN) ? paymentIt.get(AGREEMENT_PAYMENT.DOMAIN) : paymentIt.get(PAYMENT_CONCEPT.DOMAIN));
			
			String description = AonStringUtils.isBlank(paymentIt.get(AGREEMENT_PAYMENT.DESCRIPTION)) ? paymentIt.get(PAYMENT_CONCEPT.DESCRIPTION) : paymentIt.get(AGREEMENT_PAYMENT.DESCRIPTION);
			payment.setDescription(description);
			
			otherDomainAgreementPayments.add(payment);
		
		});
		
		return otherDomainAgreementPayments;
	}

	private static List<AgreementExtra> parseAgreementExtras(Result<Record> agreementExtras) {
		List<AgreementExtra> agreementExtraList = new ArrayList<AgreementExtra>();
		
		agreementExtras.forEach(agreementExtraIt -> {
			Payment payment = new Payment();
			payment.setId(null != agreementExtraIt.get(AGREEMENT_PAYMENT.ID) ? agreementExtraIt.get(AGREEMENT_PAYMENT.ID) : agreementExtraIt.get(PAYMENT_CONCEPT.ID));
			payment.setDomain(null != agreementExtraIt.get(AGREEMENT_PAYMENT.DOMAIN) ? agreementExtraIt.get(AGREEMENT_PAYMENT.DOMAIN) : agreementExtraIt.get(PAYMENT_CONCEPT.DOMAIN));
			
			String description = AonStringUtils.isBlank(agreementExtraIt.get(AGREEMENT_PAYMENT.DESCRIPTION)) ? agreementExtraIt.get(PAYMENT_CONCEPT.DESCRIPTION) : agreementExtraIt.get(AGREEMENT_PAYMENT.DESCRIPTION);
			payment.setDescription(description);
			
			AgreementExtra extra = new AgreementExtra()
					.setId(agreementExtraIt.get(AGREEMENT_EXTRA.ID))
					.setAgreementPayment(payment)
					.setStartDate(agreementExtraIt.get(AGREEMENT_EXTRA.START_DATE))
					.setEndDate(agreementExtraIt.get(AGREEMENT_EXTRA.END_DATE))
					.setIssueDate(agreementExtraIt.get(AGREEMENT_EXTRA.ISSUE_DATE))
					;
			
			agreementExtraList.add(extra);
		});
		
		return agreementExtraList;
	}
	
	// ------------------------------- Database Methods

	
}
