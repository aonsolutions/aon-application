package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementData.AGREEMENT_DATA;
import static com.esferalia.aon.jooq.tables.AgreementExtra.AGREEMENT_EXTRA;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AgreementLevelCategory.AGREEMENT_LEVEL_CATEGORY;
import static com.esferalia.aon.jooq.tables.AgreementLevelData.AGREEMENT_LEVEL_DATA;
import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.EnterpriseData.ENTERPRISE_DATA;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;

import java.sql.Connection;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.AgreementRecord;

import net.aonsolutions.db.up2date.Update;

public class AgreementClean implements Update {

	public static final AgreementClean AGREEMENTCLEAN = new AgreementClean();
	
	private AgreementClean() {}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			removeUnusedAgreements(dslContext);

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
			
		});
	}

	private void removeUnusedAgreements(DSLContext dslContext) {
		
		deleteTrashAgreements(dslContext);
		deleteAgreements(dslContext);
		
	}

	private void deleteTrashAgreements(DSLContext dslContext) {
		Result<AgreementRecord> trashAgreements = dslContext.select().from(AGREEMENT)
				.where(AGREEMENT.ID.lt(0))
				.fetchInto(AGREEMENT);
		
		trashAgreements.stream().forEach(agreement -> {
			removeContractReference(dslContext, agreement.get(AGREEMENT.ID));
			deleteAgreement(dslContext, agreement.get(AGREEMENT.ID));
		});
	}
	
	private void deleteAgreements(DSLContext dslContext) {
		Result<AgreementRecord> agreements = dslContext.select().from(AGREEMENT)
				.where(AGREEMENT.ID.gt(0))
				.fetchInto(AGREEMENT);
		
		Integer agreementsDelete = 0;
		
		for(AgreementRecord agreeemnt : agreements) {
			if(hasContract(dslContext, agreeemnt.getId(), agreeemnt.getDomain()))
				continue;
			
			agreementsDelete++;
			deleteAgreement(dslContext, agreeemnt.getId());
		}
		
		System.out.println("Delete Agreements : " + agreementsDelete + " records");
	}
	
	// Remove contract reference of trash agreements
	private void removeContractReference(DSLContext dslContext, Integer agreementId) {
		SelectConditionStep<Record1<Integer>> agreementLevelId = dslContext
				.select(AGREEMENT_LEVEL.ID).from(AGREEMENT_LEVEL)
				.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreementId));
		
		// Poner a null todos los contratos que apuntan al convenio borrado
		dslContext.update(CONTRACT)
			.set(CONTRACT.AGREEMENT_LEVEL, DSL.val(null, CONTRACT.AGREEMENT_LEVEL))
			.set(CONTRACT.CATEGORY_DESCRIPTION, DSL.val(null, CONTRACT.CATEGORY_DESCRIPTION))
			.where(CONTRACT.AGREEMENT_LEVEL.in(agreementLevelId))
			.execute();
	}

	// Return if agreement has contracts asociate
	private static boolean hasContract(DSLContext dslContext, Integer agreementId, Integer domainId) {
		
		List<Integer> domainChildIds = dslContext.select(DOMAIN.ID).from(DOMAIN)
				.where(DOMAIN.PARENT.eq(domainId))
				.fetch(DOMAIN.ID);
		
		Result<Record> agreementContracts = dslContext.select().from(CONTRACT)
			.where(CONTRACT.AGREEMENT_LEVEL.in(
					dslContext.select(AGREEMENT_LEVEL.ID).from(AGREEMENT_LEVEL)
						.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreementId))
						.fetch(AGREEMENT_LEVEL.ID)
			)).and(CONTRACT.DOMAIN.eq(domainId).or(CONTRACT.DOMAIN.in(domainChildIds)))
			.limit(1)
			.fetch();
		
		return agreementContracts.isNotEmpty();

	}
	
	// Delete agreement
	private void deleteAgreement(DSLContext dslContext, Integer agreementId) {
		SelectConditionStep<Record1<Integer>> agreementLevelId = dslContext
				.select(AGREEMENT_LEVEL.ID).from(AGREEMENT_LEVEL)
				.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreementId));
		
		dslContext.delete(AGREEMENT_LEVEL_CATEGORY)
				.where(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL
				.in(agreementLevelId)).execute();

		dslContext.delete(AGREEMENT_LEVEL_DATA)
				.where(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL
				.in(agreementLevelId)).execute();

		dslContext.delete(AGREEMENT_LEVEL)
				.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreementId))
				.execute();

		dslContext.delete(AGREEMENT_EXTRA)
				.where(AGREEMENT_EXTRA.AGREEMENT.eq(agreementId))
				.execute();

		dslContext.delete(AGREEMENT_DATA)
				.where(AGREEMENT_DATA.AGREEMENT.eq(agreementId))
				.execute();

		dslContext.delete(AGREEMENT_PAYMENT)
				.where(AGREEMENT_PAYMENT.AGREEMENT.eq(agreementId))
				.execute();

		dslContext.update(PAYROLL_WORKPLACE)
				.set(PAYROLL_WORKPLACE.AGREEMENT, DSL.castNull(PAYROLL_WORKPLACE.AGREEMENT))
				.where(PAYROLL_WORKPLACE.AGREEMENT.eq(agreementId))
				.execute();

		dslContext.delete(ENTERPRISE_DATA)
				.where(ENTERPRISE_DATA.NAME.eq(AGREEMENT.getName()))
				.and(ENTERPRISE_DATA.EXPRESSION.eq(agreementId.toString()))
				.execute();
			
		dslContext.delete(AGREEMENT).where(AGREEMENT.ID.eq(agreementId))
				.execute();
	}
	
}
