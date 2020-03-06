package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementData.AGREEMENT_DATA;
import static com.esferalia.aon.jooq.tables.AgreementExtra.AGREEMENT_EXTRA;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AgreementLevelCategory.AGREEMENT_LEVEL_CATEGORY;
import static com.esferalia.aon.jooq.tables.AgreementLevelData.AGREEMENT_LEVEL_DATA;
import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class FundUpvUpdateIV implements Update {

	public static final FundUpvUpdateIV FUNDUPV_UPDATE_IV = new FundUpvUpdateIV();
	

	private FundUpvUpdateIV() {
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
		.select(AGREEMENT.ID)
		.from(AGREEMENT)
		.where(AGREEMENT.DOMAIN.eq(0))
		.and(AGREEMENT.DESCRIPTION.eq("FUNDACI\u00D3N CURSOS DE VERANO DE LA UPV/EHU"))
		.fetchOne(AGREEMENT.ID)
		;
		
		Integer agreementLevelIIIId = 
		dslContext
		.select(AGREEMENT_LEVEL.ID)
		.from(AGREEMENT_LEVEL)
		.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreementId))
		.and(AGREEMENT_LEVEL.DESCRIPTION.eq("III"))
		.fetchOne(AGREEMENT_LEVEL.ID)
		;

		Integer agreementLevelIVId = 
		dslContext
		.select(AGREEMENT_LEVEL.ID)
		.from(AGREEMENT_LEVEL)
		.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreementId))
		.and(AGREEMENT_LEVEL.DESCRIPTION.eq("IV"))
		.fetchOne(AGREEMENT_LEVEL.ID)
		;

		Integer agreementLevelVId = 
		dslContext
		.select(AGREEMENT_LEVEL.ID)
		.from(AGREEMENT_LEVEL)
		.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreementId))
		.and(AGREEMENT_LEVEL.DESCRIPTION.eq("V"))
		.fetchOne(AGREEMENT_LEVEL.ID)
		;
		

		dslContext.transaction( (config) -> {
			
			dslContext
			.update(AGREEMENT_LEVEL_DATA)
			.set(AGREEMENT_LEVEL_DATA.EXPRESSION, "1753.61")
			.where(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL.eq(agreementLevelIIIId))
			.and(AGREEMENT_LEVEL_DATA.NAME.eq("SALARIO_MENSUAL"))
			.execute()
			;
			
			dslContext
			.update(AGREEMENT_LEVEL_DATA)
			.set(AGREEMENT_LEVEL_DATA.EXPRESSION, "1544.79")
			.where(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL.eq(agreementLevelIVId))
			.and(AGREEMENT_LEVEL_DATA.NAME.eq("SALARIO_MENSUAL"))
			.execute()
			;

			dslContext
			.update(AGREEMENT_LEVEL_DATA)
			.set(AGREEMENT_LEVEL_DATA.EXPRESSION, "1269.61")
			.where(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL.eq(agreementLevelVId))
			.and(AGREEMENT_LEVEL_DATA.NAME.eq("SALARIO_MENSUAL"))
			.execute()
			;
			
		});
	}

}
