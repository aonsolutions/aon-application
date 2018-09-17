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

public class FundUpvUpdateII implements Update {

	public static final FundUpvUpdateII FUNDUPV_UPDATE_II = new FundUpvUpdateII();
	

	private FundUpvUpdateII() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		boolean upgraded = 
		dslContext.fetchCount(
		dslContext
		.select()
		.from(AGREEMENT_PAYMENT)
		.where(AGREEMENT_PAYMENT.DOMAIN.eq(0))
		.and(AGREEMENT_PAYMENT.DESCRIPTION.eq("[52] PAGA EXTRAORDINARIA OTO\u00D1O"))
		) >= 2;

		if ( upgraded )
			return;
		
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 1970);
		Date _1970StartDate = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.YEAR, 2017);
		Date _2017StartDate = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 18);
		calendar.set(Calendar.MONTH, Calendar.MARCH);
		calendar.set(Calendar.YEAR, 2018);
		Date _2018_3_18_Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 19);
		Date _2018_3_19_Date = new Date(calendar.getTimeInMillis());

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			int agreementId = 
			dslContext
			.select(AGREEMENT.ID)
			.from(AGREEMENT)
			.where(AGREEMENT.DOMAIN.eq(0))
			.and(AGREEMENT.DESCRIPTION.eq("FUNDACI\u00D3N CURSOS DE VERANO DE LA UPV/EHU"))
			.fetchOne(AGREEMENT.ID)
			;
			

			int pagaExtraConcept = 
			dslContext
			.select(PAYMENT_CONCEPT.ID)
			.from(PAYMENT_CONCEPT)
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.CODE.eq("PAGA_EXTRA"))
			.fetchOne(PAYMENT_CONCEPT.ID)
			;
			
			int extraSeptiembreId = 
			dslContext
			.select(AGREEMENT_PAYMENT.ID)
			.from(AGREEMENT_PAYMENT)
			.where(AGREEMENT_PAYMENT.AGREEMENT.eq(agreementId))
			.and(AGREEMENT_PAYMENT.DESCRIPTION.eq("[52] PAGA EXTRAORDINARIA OTO\u00D1O"))
			.fetchOne(AGREEMENT_PAYMENT.ID);
			
			dslContext
			.delete(AGREEMENT_EXTRA)
			.where(AGREEMENT_EXTRA.AGREEMENT_PAYMENT.eq(extraSeptiembreId))
			.execute();

			dslContext
			.delete(AGREEMENT_PAYMENT)
			.where(AGREEMENT_PAYMENT.ID.eq(extraSeptiembreId))
			.execute();
			
			
			
			int extraSeptiembreIdI = 
			dslContext
			.insertInto(AGREEMENT_PAYMENT)
			.set(AGREEMENT_PAYMENT.DOMAIN, 0)
			.set(AGREEMENT_PAYMENT.AGREEMENT, agreementId)
			.set(AGREEMENT_PAYMENT.START_DATE, _1970StartDate)
			.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte) 1) // EXTRA 
			.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, pagaExtraConcept)
			.set(AGREEMENT_PAYMENT.DESCRIPTION, "[52] PAGA EXTRAORDINARIA OTO\u00D1O") 
			.set(AGREEMENT_PAYMENT.EXPRESSION, "(MES(INICIO_NOMINA) <= 9 )?INPUT(\"(/*user*/SALARIO_BASE + ANTIGUEDAD/**/) * 9.00 / 12.00 \",PAGA_EXTRA_HELP):HIDE()") 
			.set(AGREEMENT_PAYMENT.MONTH, (byte)8)
			.returning()
			.fetchOne()
			.getId();

			int extraSeptiembreIdII = 
			dslContext
			.insertInto(AGREEMENT_PAYMENT)
			.set(AGREEMENT_PAYMENT.DOMAIN, 0)
			.set(AGREEMENT_PAYMENT.AGREEMENT, agreementId)
			.set(AGREEMENT_PAYMENT.START_DATE, _1970StartDate)
			.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte) 1) // EXTRA 
			.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, pagaExtraConcept)
			.set(AGREEMENT_PAYMENT.DESCRIPTION, "[52] PAGA EXTRAORDINARIA OTO\u00D1O") 
			.set(AGREEMENT_PAYMENT.EXPRESSION, "(MES(INICIO_NOMINA) > 9 )?INPUT(\"(/*user*/SALARIO_BASE + ANTIGUEDAD/**/) * 3.00 / 12.00 \",PAGA_EXTRA_HELP):HIDE()") 
			.set(AGREEMENT_PAYMENT.MONTH, (byte)11)
			.returning()
			.fetchOne()
			.getId();
			
			dslContext
			.insertInto(AGREEMENT_EXTRA)
			.set(AGREEMENT_EXTRA.DOMAIN, 0)
			.set(AGREEMENT_EXTRA.AGREEMENT, agreementId)
			.set(AGREEMENT_EXTRA.AGREEMENT_PAYMENT, extraSeptiembreIdI)
			.set(AGREEMENT_EXTRA.START_DATE, "1/1")
			.set(AGREEMENT_EXTRA.END_DATE, "30/9")
			.set(AGREEMENT_EXTRA.ISSUE_DATE, "30/9")
			.newRecord()
			.set(AGREEMENT_EXTRA.DOMAIN, 0)
			.set(AGREEMENT_EXTRA.AGREEMENT, agreementId)
			.set(AGREEMENT_EXTRA.AGREEMENT_PAYMENT, extraSeptiembreIdII)
			.set(AGREEMENT_EXTRA.START_DATE, "1/10")
			.set(AGREEMENT_EXTRA.END_DATE, "31/12")
			.set(AGREEMENT_EXTRA.ISSUE_DATE, "31/12")
			.execute()
			;
			
//			int levelIV =
//			dslContext
//			.insertInto(AGREEMENT_LEVEL)
//			.set(AGREEMENT_LEVEL.DOMAIN, 0)
//			.set(AGREEMENT_LEVEL.AGREEMENT, agreementId)
//			.set(AGREEMENT_LEVEL.DESCRIPTION, "IV")
//			.returning()
//			.fetchOne()
//			.getId();
//			;
//			
//			dslContext
//			.insertInto(AGREEMENT_LEVEL_CATEGORY)
//			.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, 0)
//			.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, levelIV)
//			.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION, "-")
//			.execute();
//
//			dslContext
//			.insertInto(AGREEMENT_LEVEL_DATA)
//			.set(AGREEMENT_LEVEL_DATA.DOMAIN, 0)
//			.set(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL, levelIV)
//			.set(AGREEMENT_LEVEL_DATA.START_DATE, _2017StartDate)
//			.set(AGREEMENT_LEVEL_DATA.END_DATE, _2018_3_18_Date)
//			.set(AGREEMENT_LEVEL_DATA.NAME, "SALARIO_MENSUAL")
//			.set(AGREEMENT_LEVEL_DATA.EXPRESSION, "1498.5")
//			.newRecord()
//			.set(AGREEMENT_LEVEL_DATA.DOMAIN, 0)
//			.set(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL, levelIV)
//			.set(AGREEMENT_LEVEL_DATA.START_DATE, _2018_3_19_Date)
//			.set(AGREEMENT_LEVEL_DATA.END_DATE, DSL.castNull(Date.class) )
//			.set(AGREEMENT_LEVEL_DATA.NAME, "SALARIO_MENSUAL")
//			.set(AGREEMENT_LEVEL_DATA.EXPRESSION, "1869.72")
//			.execute();
			

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
