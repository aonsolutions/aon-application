package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementData.AGREEMENT_DATA;
import static com.esferalia.aon.jooq.tables.AgreementExtra.AGREEMENT_EXTRA;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AgreementLevelCategory.AGREEMENT_LEVEL_CATEGORY;
import static com.esferalia.aon.jooq.tables.AgreementLevelData.AGREEMENT_LEVEL_DATA;
import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Contract;

import net.aonsolutions.db.up2date.Update;

public class FundUpvUpdateV implements Update {

	public static final FundUpvUpdateV FUNDUPV_UPDATE_V = new FundUpvUpdateV();
	

	private FundUpvUpdateV() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		Integer agreementId = 
		dslContext
		.select()
		.from(AGREEMENT)
		.where(AGREEMENT.DOMAIN.eq(0))
		.and(AGREEMENT.DESCRIPTION.eq("FUNDACI\u00D3N CURSOS DE VERANO DE LA UPV/EHU"))
		.fetchOne(AGREEMENT.ID)
		;
		
		if ( agreementId == null )
			return;
		
		Integer domainId =
		dslContext
		.select()
		.from(DOMAIN)
		.where(DOMAIN.NAME.eq("fundupv.aonsolutions.net"))
		.fetchOne(DOMAIN.ID)
		;

		
		SelectConditionStep<Record1<Integer>> agreementLevelIds = 
		DSL.select(AGREEMENT_LEVEL.ID)
		.from(AGREEMENT_LEVEL)
		.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreementId));

		dslContext.transaction( (config) -> {
			
			if ( domainId != null ) {
				dslContext
				.update(AGREEMENT)
				.set(AGREEMENT.DOMAIN, domainId)
				.where(AGREEMENT.ID.eq(agreementId))
				.execute();
				
				dslContext
				.update(AGREEMENT_LEVEL)
				.set(AGREEMENT_LEVEL.DOMAIN, domainId)
				.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreementId))
				.execute();
				
				dslContext
				.update(AGREEMENT_EXTRA)
				.set(AGREEMENT_EXTRA.DOMAIN, domainId)
				.where(AGREEMENT_EXTRA.AGREEMENT.eq(agreementId))
				.execute();
	
				dslContext
				.update(AGREEMENT_PAYMENT)
				.set(AGREEMENT_PAYMENT.DOMAIN, domainId)
				.where(AGREEMENT_PAYMENT.AGREEMENT.eq(agreementId))
				.execute();
				
				dslContext
				.update(AGREEMENT_DATA)
				.set(AGREEMENT_DATA.DOMAIN, domainId)
				.where(AGREEMENT_DATA.AGREEMENT.eq(agreementId))
				.execute();
	
				dslContext
				.update(AGREEMENT_LEVEL_DATA)
				.set(AGREEMENT_LEVEL_DATA.DOMAIN, domainId)
				.where(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL.in(agreementLevelIds))
				.execute();
	
				dslContext
				.update(AGREEMENT_LEVEL_CATEGORY)
				.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, domainId)
				.where(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL.in(agreementLevelIds))
				.execute();
			} else {
				dslContext
				.delete(AGREEMENT_LEVEL_DATA)
				.where(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL.in(agreementLevelIds))
				.execute();
	
				dslContext
				.delete(AGREEMENT_LEVEL_CATEGORY)
				.where(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL.in(agreementLevelIds))
				.execute();

				dslContext
				.delete(AGREEMENT_LEVEL)
				.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreementId))
				.execute();
				
				dslContext
				.delete(AGREEMENT_EXTRA)
				.where(AGREEMENT_EXTRA.AGREEMENT.eq(agreementId))
				.execute();
	
				dslContext
				.delete(AGREEMENT_PAYMENT)
				.where(AGREEMENT_PAYMENT.AGREEMENT.eq(agreementId))
				.execute();
				
				dslContext
				.delete(AGREEMENT_DATA)
				.where(AGREEMENT_DATA.AGREEMENT.eq(agreementId))
				.execute();

				dslContext
				.delete(AGREEMENT)
				.where(AGREEMENT.ID.eq(agreementId))
				.execute();
				
	
			}

		});
	}

}
