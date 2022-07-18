package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementData.AGREEMENT_DATA;
import static com.esferalia.aon.jooq.tables.AgreementExtra.AGREEMENT_EXTRA;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AgreementLevelCategory.AGREEMENT_LEVEL_CATEGORY;
import static com.esferalia.aon.jooq.tables.AgreementLevelData.AGREEMENT_LEVEL_DATA;
import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.AgreementDataRecord;
import com.esferalia.aon.jooq.tables.records.AgreementExtraRecord;
import com.esferalia.aon.jooq.tables.records.AgreementLevelRecord;
import com.esferalia.aon.jooq.tables.records.AgreementPaymentRecord;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;

import net.aonsolutions.db.up2date.Update;

public class AgreementsToChildUpdate implements Update {
	
	public static final AgreementsToChildUpdate AGREEMENTSTOCHILDUPDATE = new AgreementsToChildUpdate();

	private AgreementsToChildUpdate() {}
	
	private static Integer agreementMovs = 0;
	private static Integer agreementDomainMovs = 0;

	@Override
	public void upgrade(Connection connection) {
		
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		// Establish context
		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		moveAgreementsToChild(dslContext);
	}

	private void moveAgreementsToChild(DSLContext dslContext) {
		Result<AgreementRecord> agreementRecords = dslContext.selectFrom(AGREEMENT).where(AGREEMENT.ID.ge(0)).fetch();
		agreementMovs = 0;
		agreementDomainMovs = 0;
		
		for(AgreementRecord agreementRecord : agreementRecords) {
			Integer agreementDomain = agreementRecord.getDomain();
			Integer agreementId = agreementRecord.getId();
			
			// Get agreementLevels ids
			List<Integer> agreementLevels = dslContext.select(AGREEMENT_LEVEL.ID).from(AGREEMENT_LEVEL)
					.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreementId)).fetch(AGREEMENT_LEVEL.ID);
			
			// Get distinc domains for searching agreementLevels
			List<Integer> agreementDomains = dslContext.selectDistinct(CONTRACT.DOMAIN).from(CONTRACT)
				.where(CONTRACT.AGREEMENT_LEVEL.in(agreementLevels))
				.fetch(CONTRACT.DOMAIN);
			
			// Change agreement domain
			if(agreementDomains.size() == 1)
				moveDomainAgreement(dslContext, agreementId, agreementDomains.get(0));
			else
				// Filter same agreement and contract domain
				agreementDomains.stream().filter(domain -> agreementDomain.equals(domain))
					.forEach(domain -> moveAgreementToDomain(dslContext, agreementId, domain));
		}
		
		System.out.println("Agreements download to child : " + agreementMovs);
		System.out.println("Agreements domain change : " + agreementDomainMovs);
	}

	// --------- Move agreement to domain

	private static void moveAgreementToDomain(DSLContext dslContext, Integer agreementId, Integer newDomain) {

		dslContext.transaction(t -> {
			
			/** TABLES TO COPY
			 	agreement
				agreement_data
				agreement_extra
				agreement_level
				agreement_level_category
				agreement_level_data
				agreement_payment                  
			 */
			
			agreementMovs++;
			
			// Agreement
			AgreementRecord agreementRecord = dslContext.selectFrom(AGREEMENT).where(AGREEMENT.ID.eq(agreementId)).fetchOne();
			System.out.println(agreementRecord.getDescription() + " || move from domain : " + agreementRecord.getDomain() + ", to : " + newDomain);
			Integer newAgreementId = duplicateAgreement(dslContext, agreementRecord, newDomain);
			
			// Agreement_data
			duplicateAgreementData(dslContext, agreementRecord.getId(), newAgreementId, newDomain);
			
			// Agreement_payment (also payment_concept)
			// return map <oldId, newId>
			Map<Integer, Integer> agreementPaymentIds = duplicateAgreementPayment(dslContext, agreementRecord.getId(), newAgreementId, newDomain);
			
			// Agreement_extra
			duplicateAgreementExtra(dslContext, agreementRecord.getId(), newAgreementId, newDomain, agreementPaymentIds);
			
			// Agreement_level
			// return map <oldId, newId>
			Map<Integer, Integer> agreementLevelIds = duplicateAgreementLevel(dslContext, agreementRecord.getId(), newAgreementId, newDomain);
			
			// Agreement_level_category
			duplicateAgreementLevelCategory(dslContext, agreementRecord.getId(), newDomain, agreementLevelIds);
			
			// Agreement_level_data
			duplicateAgreementLevelData(dslContext, agreementRecord.getId(), newDomain, agreementLevelIds);
			
			// Update contracts using old agreement to the new agreement levels on own domain
			updateContractsToNewAgreement(dslContext, agreementRecord.getId(), newDomain, agreementLevelIds);
		});

	}

	private static Integer duplicateAgreement(DSLContext dslContext, AgreementRecord agreementRecord, Integer newDomain) {
		return dslContext.insertInto(AGREEMENT)
				.set(AGREEMENT.DOMAIN, newDomain)
				.set(AGREEMENT.CALENDAR, agreementRecord.getCalendar())
				.set(AGREEMENT.DESCRIPTION, agreementRecord.getDescription())
				.set(AGREEMENT.SS_NUMBER, agreementRecord.getSsNumber())
				.set(AGREEMENT.OWNER, agreementRecord.getOwner())
				.returning()
				.fetchOne()
				.getId();
	}

	private static void duplicateAgreementData(DSLContext dslContext, Integer agreementId, Integer newAgreementId, Integer newDomain) {
		Result<AgreementDataRecord> agreementDatas = dslContext.selectFrom(AGREEMENT_DATA).where(AGREEMENT_DATA.AGREEMENT.eq(agreementId)).fetch();
		
		agreementDatas.forEach(agreementData ->
			dslContext.insertInto(AGREEMENT_DATA)
				.set(AGREEMENT_DATA.DOMAIN, newDomain)
				.set(AGREEMENT_DATA.NAME, agreementData.getName())
				.set(AGREEMENT_DATA.AGREEMENT, newAgreementId)
				.set(AGREEMENT_DATA.EXPRESSION, "/*inherit*/" + agreementData.getExpression() + "/**/")
				.set(AGREEMENT_DATA.START_DATE, agreementData.getStartDate())
				.set(AGREEMENT_DATA.END_DATE, agreementData.getEndDate())
				.execute()
		);
	}
	
	private static Map<Integer, Integer> duplicateAgreementPayment(DSLContext dslContext, Integer agreementId, Integer newAgreementId, Integer newDomain) {
		Map<Integer, Integer> agreementPaymentIds = new HashMap<>();
		// Map <oldId, newId>
		Map<Integer, Integer> paymentConceptIds = new HashMap<>();
		
		Result<AgreementPaymentRecord> agreementPayments = dslContext.selectFrom(AGREEMENT_PAYMENT).where(AGREEMENT_PAYMENT.AGREEMENT.eq(agreementId)).fetch();
		
		agreementPayments.forEach(agreementPayment -> {
			// Duplicate payment_concept if exists
			Integer newPaymentConceptId = null;
			if(agreementPayment.getPaymentConcept() != null && null == paymentConceptIds.get(agreementPayment.getPaymentConcept())) {
				newPaymentConceptId = duplicatePaymentConcept(dslContext, agreementPayment.getPaymentConcept(), newDomain);
				paymentConceptIds.put(agreementPayment.getPaymentConcept(), newPaymentConceptId);
			} else
				newPaymentConceptId = paymentConceptIds.getOrDefault(agreementPayment.getPaymentConcept(), null);
			
			Integer newAgreementPaymentId = dslContext.insertInto(AGREEMENT_PAYMENT)
					.set(AGREEMENT_PAYMENT.DOMAIN, newDomain)
					.set(AGREEMENT_PAYMENT.AGREEMENT, newAgreementId)
					.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, newPaymentConceptId)
					.set(AGREEMENT_PAYMENT.TYPE, agreementPayment.getType())
					.set(AGREEMENT_PAYMENT.EXPRESSION, "/*inherit*/" + agreementPayment.getExpression() + "/**/")
					.set(AGREEMENT_PAYMENT.DESCRIPTION, agreementPayment.getDescription())
					.set(AGREEMENT_PAYMENT.START_DATE, agreementPayment.getStartDate())
					.set(AGREEMENT_PAYMENT.END_DATE, agreementPayment.getEndDate())
					.set(AGREEMENT_PAYMENT.MONTH, agreementPayment.getMonth())
					.set(AGREEMENT_PAYMENT.SALARY_TYPE, agreementPayment.getSalaryType())
					.set(AGREEMENT_PAYMENT.DESCRIPTION_DECORABLE, agreementPayment.getDescriptionDecorable())
					.set(AGREEMENT_PAYMENT.IRPF_EXPRESSION, agreementPayment.getIrpfExpression())
					.set(AGREEMENT_PAYMENT.QUOTE_EXPRESSION, agreementPayment.getQuoteExpression())
					.returning(AGREEMENT_PAYMENT.ID)
					.fetchOne()
					.value1();
			
			agreementPaymentIds.put(agreementPayment.getId(), newAgreementPaymentId);
		});
		
		return agreementPaymentIds;
	}

	private static Integer duplicatePaymentConcept(DSLContext dslContext, Integer paymentConceptId, Integer newDomain) {
		PaymentConceptRecord paymentConceptRecord = dslContext.selectFrom(PAYMENT_CONCEPT).where(PAYMENT_CONCEPT.ID.eq(paymentConceptId)).fetchOne();
		
		return dslContext.insertInto(PAYMENT_CONCEPT)
				.set(PAYMENT_CONCEPT.DOMAIN, newDomain)
				.set(PAYMENT_CONCEPT.CODE, paymentConceptRecord.getCode())
				.set(PAYMENT_CONCEPT.DESCRIPTION, paymentConceptRecord.getDescription())
				.set(PAYMENT_CONCEPT.TYPE, paymentConceptRecord.getType())
				.set(PAYMENT_CONCEPT.DESCRIPTION_DECORABLE, paymentConceptRecord.getDescriptionDecorable())
				.set(PAYMENT_CONCEPT.EXPRESSION, paymentConceptRecord.getExpression())
				.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, paymentConceptRecord.getIrpfExpression())
				.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, paymentConceptRecord.getQuoteExpression())
				.returning(PAYMENT_CONCEPT.ID)
				.fetchOne()
				.value1();
	}

	private static void duplicateAgreementExtra(DSLContext dslContext, Integer agreementId, Integer newAgreementId, Integer newDomain, Map<Integer, Integer> agreementPaymentIds) {
		Result<AgreementExtraRecord> agreementExtras = dslContext.selectFrom(AGREEMENT_EXTRA).where(AGREEMENT_EXTRA.AGREEMENT.eq(agreementId)).fetch();
		
		agreementExtras.forEach(agreementExtra -> {
			Integer agreementPayment = null;
			if(null != agreementExtra.getAgreementPayment()) agreementPayment = agreementPaymentIds.get(agreementExtra.getAgreementPayment());
			
			dslContext.insertInto(AGREEMENT_EXTRA)
				.set(AGREEMENT_EXTRA.DOMAIN, newDomain)
				.set(AGREEMENT_EXTRA.AGREEMENT, newAgreementId)
				.set(AGREEMENT_EXTRA.AGREEMENT_PAYMENT, agreementPayment)
				.set(AGREEMENT_EXTRA.START_DATE, agreementExtra.getStartDate())
				.set(AGREEMENT_EXTRA.END_DATE, agreementExtra.getEndDate())
				.set(AGREEMENT_EXTRA.ISSUE_DATE, agreementExtra.getIssueDate())
				.execute();
		});
	}

	private static Map<Integer, Integer> duplicateAgreementLevel(DSLContext dslContext, Integer agreementId, Integer newAgreementId, Integer newDomain) {
		Map<Integer, Integer> agreementLevelIds = new HashMap<>();
		
		Result<AgreementLevelRecord> agreementLevels = dslContext.selectFrom(AGREEMENT_LEVEL).where(AGREEMENT_LEVEL.AGREEMENT.eq(agreementId)).fetch();
		
		agreementLevels.forEach(agreementLevel -> {
			Integer newAgreementLevelId = dslContext.insertInto(AGREEMENT_LEVEL)
				.set(AGREEMENT_LEVEL.DOMAIN, newDomain)
				.set(AGREEMENT_LEVEL.AGREEMENT, newAgreementId)
				.set(AGREEMENT_LEVEL.DESCRIPTION, agreementLevel.getDescription())
				.returning(AGREEMENT_LEVEL.ID)
				.fetchOne()
				.value1();
			
			agreementLevelIds.put(agreementLevel.getId(), newAgreementLevelId);
		});
		
		return agreementLevelIds;
	}

	private static void duplicateAgreementLevelCategory(DSLContext dslContext, Integer agreementId, Integer newDomain, Map<Integer, Integer> agreementLevelIds) {
		Result<Record> agreementLevelCategories = dslContext.select().from(AGREEMENT_LEVEL_CATEGORY)
			.innerJoin(AGREEMENT_LEVEL)
			.on(AGREEMENT_LEVEL.ID.eq(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL))
			.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreementId))
			.fetch();
		
		agreementLevelCategories.forEach(agreementLevelCategory ->
			dslContext.insertInto(AGREEMENT_LEVEL_CATEGORY)
				.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, newDomain)
				.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, agreementLevelIds.get(agreementLevelCategory.get(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL)))
				.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION, agreementLevelCategory.get(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION))
				.execute()
		);
	}
	
	private static void duplicateAgreementLevelData(DSLContext dslContext, Integer agreementId, Integer newDomain, Map<Integer, Integer> agreementLevelIds) {
		Result<Record> agreementLevelDatas = dslContext.select().from(AGREEMENT_LEVEL_DATA)
			.innerJoin(AGREEMENT_LEVEL)
			.on(AGREEMENT_LEVEL.ID.eq(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL))
			.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreementId))
			.fetch();
		
		agreementLevelDatas.forEach(agreementLevelData ->
			dslContext.insertInto(AGREEMENT_LEVEL_DATA)
				.set(AGREEMENT_LEVEL_DATA.DOMAIN, newDomain)
				.set(AGREEMENT_LEVEL_DATA.NAME, agreementLevelData.get(AGREEMENT_LEVEL_DATA.NAME))
				.set(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL, agreementLevelIds.get(agreementLevelData.get(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL)))
				.set(AGREEMENT_LEVEL_DATA.EXPRESSION, "/*inherit*/" + agreementLevelData.get(AGREEMENT_LEVEL_DATA.EXPRESSION) + "/**/")
				.set(AGREEMENT_LEVEL_DATA.START_DATE, agreementLevelData.get(AGREEMENT_LEVEL_DATA.START_DATE))
				.set(AGREEMENT_LEVEL_DATA.END_DATE, agreementLevelData.get(AGREEMENT_LEVEL_DATA.END_DATE))
				.execute()
		);
	}

	private static void updateContractsToNewAgreement(DSLContext dslContext, Integer agreementId, Integer newDomain, Map<Integer, Integer> agreementLevelIds) {
		Result<Record> contracts = dslContext.select().from(CONTRACT)
				.innerJoin(AGREEMENT_LEVEL)
				.on(AGREEMENT_LEVEL.ID.eq(CONTRACT.AGREEMENT_LEVEL))
				.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreementId))
				.and(CONTRACT.DOMAIN.eq(newDomain))
				.fetch();
		
		contracts.forEach(contract ->
			dslContext.update(CONTRACT)
				.set(CONTRACT.AGREEMENT_LEVEL, agreementLevelIds.get(contract.get(CONTRACT.AGREEMENT_LEVEL)))
				.where(CONTRACT.ID.eq(contract.get(CONTRACT.ID)))
				.execute()
		);
	}
	
	// --------- Change domain of agreement
	
	private void moveDomainAgreement(DSLContext dslContext, Integer agreementId, Integer domain) {
		dslContext.transaction(t -> {
			
			agreementDomainMovs++;
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext.update(AGREEMENT)
				.set(AGREEMENT.DOMAIN, domain)
				.where(AGREEMENT.ID.eq(agreementId))
				.execute();
			
			dslContext.update(AGREEMENT_DATA)
				.set(AGREEMENT_DATA.DOMAIN, domain)
				.where(AGREEMENT_DATA.AGREEMENT.eq(agreementId))
				.execute();
			
			dslContext.update(AGREEMENT_PAYMENT)
				.set(AGREEMENT_PAYMENT.DOMAIN, domain)
				.where(AGREEMENT_PAYMENT.AGREEMENT.eq(agreementId))
				.execute();
			
			dslContext.update(AGREEMENT_EXTRA)
				.set(AGREEMENT_EXTRA.DOMAIN, domain)
				.where(AGREEMENT_EXTRA.AGREEMENT.eq(agreementId))
				.execute();
			
			dslContext.update(AGREEMENT_LEVEL)
				.set(AGREEMENT_LEVEL.DOMAIN, domain)
				.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreementId))
				.execute();
			
			List<Integer> agreementLevels = dslContext.select(AGREEMENT_LEVEL.ID).from(AGREEMENT_LEVEL)
					.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreementId))
					.fetch(AGREEMENT_LEVEL.ID);
			
			dslContext.update(AGREEMENT_LEVEL_CATEGORY)
				.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, domain)
				.where(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL.in(agreementLevels))
				.execute();
			
			dslContext.update(AGREEMENT_LEVEL_DATA)
				.set(AGREEMENT_LEVEL_DATA.DOMAIN, domain)
				.where(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL.in(agreementLevels))
				.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}
}
