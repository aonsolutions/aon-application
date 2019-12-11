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

public class FundUpvUpdateIII implements Update {

	public static final FundUpvUpdateIII FUNDUPV_UPDATE_III = new FundUpvUpdateIII();
	

	private FundUpvUpdateIII() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			Integer agreementId = 
			dslContext
			.select(AGREEMENT.ID)
			.from(AGREEMENT)
			.where(AGREEMENT.DOMAIN.eq(0))
			.and(AGREEMENT.DESCRIPTION.eq("FUNDACI\u00D3N CURSOS DE VERANO DE LA UPV/EHU"))
			.fetchOne(AGREEMENT.ID)
			;
			

			Integer extraMarzoId = 
			dslContext
			.select(AGREEMENT_PAYMENT.ID)
			.from(AGREEMENT_PAYMENT)
			.where(AGREEMENT_PAYMENT.AGREEMENT.eq(agreementId))
			.and(AGREEMENT_PAYMENT.DESCRIPTION.eq("[50] PAGA EXTRAORDINARIA MARZO"))
			.fetchOne(AGREEMENT_PAYMENT.ID);
			
			dslContext
			.update(AGREEMENT_EXTRA)
			.set(AGREEMENT_EXTRA.START_DATE, "1/1 -1")
			.set(AGREEMENT_EXTRA.END_DATE, "31/12 -1")
			.where(AGREEMENT_EXTRA.AGREEMENT_PAYMENT.eq(extraMarzoId))
			.execute();

			

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
