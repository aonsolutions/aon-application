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

public class FundUpvUpdate implements Update {

	public static final FundUpvUpdate FUNDUPV_UPDATE = new FundUpvUpdate();
	
	private static final String WARNNING = "HIDE(\""
	+"<div>Existe una nueva versi&oacute;n de este convenio 'FUNDACI&Oacute;N CURSOS DE VERANO DE LA UPV/EHU'.</div>"
	+"<div>Si encuentra alg&uacute;n error comun&iacute;quese con nosotros.</div>"
	+"<div>&nbsp;</div><div class='aon-text-right'>Disculpe las molestias, <span class='aon-icon aon-icon-logo' />aon Solutions</div>\");"
	;
	

	private FundUpvUpdate() {
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
		.from(AGREEMENT)
		.where(AGREEMENT.DOMAIN.eq(0))
		.and(AGREEMENT.DESCRIPTION.eq("FUNDACI\u00D3N CURSOS DE VERANO DE LA UPV/EHU"))
		) > 0;

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
			.insertInto(AGREEMENT)
			.set(AGREEMENT.DOMAIN, 0)
			.set(AGREEMENT.DESCRIPTION, "FUNDACI\u00D3N CURSOS DE VERANO DE LA UPV/EHU")
			.returning()
			.fetchOne()
			.getId()
			;
			
			dslContext
			.insertInto(AGREEMENT_DATA)
			.set(AGREEMENT_DATA.DOMAIN, 0)
			.set(AGREEMENT_DATA.AGREEMENT, agreementId)
			.set(AGREEMENT_DATA.NAME, "PORCENTAJE_GEROA")
			.set(AGREEMENT_DATA.EXPRESSION, "0.20")
			.set(AGREEMENT_DATA.START_DATE, _2017StartDate )
			.set(AGREEMENT_DATA.END_DATE, _2018_3_18_Date )
			.newRecord()
			.set(AGREEMENT_DATA.DOMAIN, 0)
			.set(AGREEMENT_DATA.AGREEMENT, agreementId)
			.set(AGREEMENT_DATA.NAME, "PORCENTAJE_GEROA")
			.set(AGREEMENT_DATA.EXPRESSION, "0.20")
			.set(AGREEMENT_DATA.START_DATE, _2018_3_19_Date)
			.set(AGREEMENT_DATA.END_DATE, DSL.castNull(Date.class) )
			.newRecord()
			.set(AGREEMENT_DATA.DOMAIN, 0)
			.set(AGREEMENT_DATA.AGREEMENT, agreementId)
			.set(AGREEMENT_DATA.NAME, "HORAS_CONVENIO")
			.set(AGREEMENT_DATA.EXPRESSION, "37.50")
			.set(AGREEMENT_DATA.START_DATE, _2017StartDate )
			.set(AGREEMENT_DATA.END_DATE, _2018_3_18_Date )
			.newRecord()
			.set(AGREEMENT_DATA.DOMAIN, 0)
			.set(AGREEMENT_DATA.AGREEMENT, agreementId)
			.set(AGREEMENT_DATA.NAME, "HORAS_CONVENIO")
			.set(AGREEMENT_DATA.EXPRESSION, "37.50")
			.set(AGREEMENT_DATA.START_DATE, _2018_3_19_Date )
			.set(AGREEMENT_DATA.END_DATE, DSL.castNull(Date.class) )
			.execute()
			;
			
			int salarioBaseConcept = 
			dslContext
			.select(PAYMENT_CONCEPT.ID)
			.from(PAYMENT_CONCEPT)
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.CODE.eq("SALARIO_BASE"))
			.and(PAYMENT_CONCEPT.DESCRIPTION.eq("SALARIO BASE MENSUAL"))
			.fetchOne(PAYMENT_CONCEPT.ID)
			;

			int antiguedadConcept = 
			dslContext
			.select(PAYMENT_CONCEPT.ID)
			.from(PAYMENT_CONCEPT)
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.CODE.eq("ANTIGUEDAD"))
			.fetchOne(PAYMENT_CONCEPT.ID)
			;

			int pagaExtraConcept = 
			dslContext
			.select(PAYMENT_CONCEPT.ID)
			.from(PAYMENT_CONCEPT)
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.CODE.eq("PAGA_EXTRA"))
			.fetchOne(PAYMENT_CONCEPT.ID)
			;

			int geroaConcept = 
			dslContext
			.select(PAYMENT_CONCEPT.ID)
			.from(PAYMENT_CONCEPT)
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.CODE.eq("GEROA"))
			.fetchOne(PAYMENT_CONCEPT.ID)
			;

			dslContext
			.insertInto(AGREEMENT_PAYMENT)
			.set(AGREEMENT_PAYMENT.DOMAIN, 0)
			.set(AGREEMENT_PAYMENT.AGREEMENT, agreementId)
			.set(AGREEMENT_PAYMENT.START_DATE, _1970StartDate)
			.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte) 0) // SALARY 
			.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, salarioBaseConcept)
			.set(AGREEMENT_PAYMENT.DESCRIPTION, "[1] SALARIO BASE MENSUAL") 
			.set(AGREEMENT_PAYMENT.EXPRESSION, "/*user*/SALARIO_MENSUAL/**/ * DIAS_TRABAJADOS / DIAS_MES") 
			.newRecord()
			.set(AGREEMENT_PAYMENT.DOMAIN, 0)
			.set(AGREEMENT_PAYMENT.AGREEMENT, agreementId)
			.set(AGREEMENT_PAYMENT.START_DATE, _1970StartDate)
			.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte) 0) // SALARY 
			.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, antiguedadConcept)
			.set(AGREEMENT_PAYMENT.DESCRIPTION, "[2] COMPLEMENTO PERSONAL DE ANTIG\u00DCEDAD") 
			.set(AGREEMENT_PAYMENT.EXPRESSION, "INPUT(\"/*user*/ANTIG\u00DCEDAD(SALARIO_BASE * 2.05/100, QUINQUENIO) /**/\",ANTIGUEDAD_HELP)") 
			.newRecord()
			.set(AGREEMENT_PAYMENT.DOMAIN, 0)
			.set(AGREEMENT_PAYMENT.AGREEMENT, agreementId)
			.set(AGREEMENT_PAYMENT.START_DATE, _1970StartDate)
			.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte) 0) // GEROA 
			.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, geroaConcept)
			.set(AGREEMENT_PAYMENT.DESCRIPTION, "[60] GEROA PENTSIOAK BGAE/EPSV") 
			.set(AGREEMENT_PAYMENT.EXPRESSION, "/*read-only*/TOTAL_DEVENGADO * PORCENTAJE_GEROA * 0.00/**/") 
			.execute();
			
			
			int extraNavidadId = 
			dslContext
			.insertInto(AGREEMENT_PAYMENT)
			.set(AGREEMENT_PAYMENT.DOMAIN, 0)
			.set(AGREEMENT_PAYMENT.AGREEMENT, agreementId)
			.set(AGREEMENT_PAYMENT.START_DATE, _1970StartDate)
			.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte) 1) // EXTRA 
			.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, pagaExtraConcept)
			.set(AGREEMENT_PAYMENT.DESCRIPTION, "[53] PAGA EXTRAORDINARIA NAVIDAD") 
			.set(AGREEMENT_PAYMENT.EXPRESSION, "INPUT(\"/*user*/SALARIO_BASE + ANTIGUEDAD/**/\",PAGA_EXTRA_HELP)") 
			.set(AGREEMENT_PAYMENT.MONTH, (byte)11)
			.returning()
			.fetchOne()
			.getId();
			
			int extraSeptiembreId = 
			dslContext
			.insertInto(AGREEMENT_PAYMENT)
			.set(AGREEMENT_PAYMENT.DOMAIN, 0)
			.set(AGREEMENT_PAYMENT.AGREEMENT, agreementId)
			.set(AGREEMENT_PAYMENT.START_DATE, _1970StartDate)
			.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte) 1) // EXTRA 
			.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, pagaExtraConcept)
			.set(AGREEMENT_PAYMENT.DESCRIPTION, "[52] PAGA EXTRAORDINARIA OTO\u00D1O") 
			.set(AGREEMENT_PAYMENT.EXPRESSION, "INPUT(\"/*user*/SALARIO_BASE + ANTIGUEDAD/**/\",PAGA_EXTRA_HELP)") 
			.set(AGREEMENT_PAYMENT.MONTH, (byte)8)
			.returning()
			.fetchOne()
			.getId();

			int extraMarzoId = 
			dslContext
			.insertInto(AGREEMENT_PAYMENT)
			.set(AGREEMENT_PAYMENT.DOMAIN, 0)
			.set(AGREEMENT_PAYMENT.AGREEMENT, agreementId)
			.set(AGREEMENT_PAYMENT.START_DATE, _1970StartDate)
			.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte) 1) // EXTRA 
			.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, pagaExtraConcept)
			.set(AGREEMENT_PAYMENT.DESCRIPTION, "[50] PAGA EXTRAORDINARIA MARZO") 
			.set(AGREEMENT_PAYMENT.EXPRESSION, "INPUT(\"/*user*/SALARIO_BASE + ANTIGUEDAD/**/\",PAGA_EXTRA_HELP)") 
			.set(AGREEMENT_PAYMENT.MONTH, (byte)2)
			.returning()
			.fetchOne()
			.getId();

			int extraVeranoId = 
			dslContext
			.insertInto(AGREEMENT_PAYMENT)
			.set(AGREEMENT_PAYMENT.DOMAIN, 0)
			.set(AGREEMENT_PAYMENT.AGREEMENT, agreementId)
			.set(AGREEMENT_PAYMENT.START_DATE, _1970StartDate)
			.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte) 1) // EXTRA 
			.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, pagaExtraConcept)
			.set(AGREEMENT_PAYMENT.DESCRIPTION, "[51] PAGA EXTRAORDINARIA VERANO") 
			.set(AGREEMENT_PAYMENT.EXPRESSION, "INPUT(\"/*user*/SALARIO_BASE + ANTIGUEDAD/**/\",PAGA_EXTRA_HELP)") 
			.set(AGREEMENT_PAYMENT.MONTH, (byte)5)
			.returning()
			.fetchOne()
			.getId();
			
			dslContext
			.insertInto(AGREEMENT_EXTRA)
			.set(AGREEMENT_EXTRA.DOMAIN, 0)
			.set(AGREEMENT_EXTRA.AGREEMENT, agreementId)
			.set(AGREEMENT_EXTRA.AGREEMENT_PAYMENT, extraVeranoId)
			.set(AGREEMENT_EXTRA.START_DATE, "1/1")
			.set(AGREEMENT_EXTRA.END_DATE, "30/6")
			.set(AGREEMENT_EXTRA.ISSUE_DATE, "30/6")
			.newRecord()
			.set(AGREEMENT_EXTRA.DOMAIN, 0)
			.set(AGREEMENT_EXTRA.AGREEMENT, agreementId)
			.set(AGREEMENT_EXTRA.AGREEMENT_PAYMENT, extraNavidadId)
			.set(AGREEMENT_EXTRA.START_DATE, "1/7")
			.set(AGREEMENT_EXTRA.END_DATE, "31/12")
			.set(AGREEMENT_EXTRA.ISSUE_DATE, "21/12")
			.newRecord()
			.set(AGREEMENT_EXTRA.DOMAIN, 0)
			.set(AGREEMENT_EXTRA.AGREEMENT, agreementId)
			.set(AGREEMENT_EXTRA.AGREEMENT_PAYMENT, extraSeptiembreId)
			.set(AGREEMENT_EXTRA.START_DATE, "1/10 -1")
			.set(AGREEMENT_EXTRA.END_DATE, "30/9")
			.set(AGREEMENT_EXTRA.ISSUE_DATE, "30/9")
			.newRecord()
			.set(AGREEMENT_EXTRA.DOMAIN, 0)
			.set(AGREEMENT_EXTRA.AGREEMENT, agreementId)
			.set(AGREEMENT_EXTRA.AGREEMENT_PAYMENT, extraMarzoId)
			.set(AGREEMENT_EXTRA.START_DATE, "1/1")
			.set(AGREEMENT_EXTRA.END_DATE, "31/12")
			.set(AGREEMENT_EXTRA.ISSUE_DATE, "31/3")
			.execute()
			;
			
			int levelI =
			dslContext
			.insertInto(AGREEMENT_LEVEL)
			.set(AGREEMENT_LEVEL.DOMAIN, 0)
			.set(AGREEMENT_LEVEL.AGREEMENT, agreementId)
			.set(AGREEMENT_LEVEL.DESCRIPTION, "I")
			.returning()
			.fetchOne()
			.getId();
			;

			int levelII =
			dslContext
			.insertInto(AGREEMENT_LEVEL)
			.set(AGREEMENT_LEVEL.DOMAIN, 0)
			.set(AGREEMENT_LEVEL.AGREEMENT, agreementId)
			.set(AGREEMENT_LEVEL.DESCRIPTION, "II")
			.returning()
			.fetchOne()
			.getId();
			;

			int levelIII =
			dslContext
			.insertInto(AGREEMENT_LEVEL)
			.set(AGREEMENT_LEVEL.DOMAIN, 0)
			.set(AGREEMENT_LEVEL.AGREEMENT, agreementId)
			.set(AGREEMENT_LEVEL.DESCRIPTION, "III")
			.returning()
			.fetchOne()
			.getId();
			;
			
			dslContext
			.insertInto(AGREEMENT_LEVEL_CATEGORY)
			.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, levelI)
			.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION, "-")
			.newRecord()
			.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, levelII)
			.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION, "2. mailako burua / Jefe de 2a")
			.newRecord()
			.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, levelIII)
			.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION, "1go mailako ofiziala / Oficial de 1a")
			.newRecord()
			.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, levelIII)
			.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION, "Programatzailea/ Programador")
			.execute();

			dslContext
			.insertInto(AGREEMENT_LEVEL_DATA)
			.set(AGREEMENT_LEVEL_DATA.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL, levelI)
			.set(AGREEMENT_LEVEL_DATA.START_DATE, _2018_3_19_Date)
			.set(AGREEMENT_LEVEL_DATA.END_DATE, DSL.castNull(Date.class) )
			.set(AGREEMENT_LEVEL_DATA.NAME, "SALARIO_MENSUAL")
			.set(AGREEMENT_LEVEL_DATA.EXPRESSION, "2286.98")
			.newRecord()
			.set(AGREEMENT_LEVEL_DATA.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL, levelII)
			.set(AGREEMENT_LEVEL_DATA.START_DATE, _2017StartDate)
			.set(AGREEMENT_LEVEL_DATA.END_DATE, _2018_3_18_Date)
			.set(AGREEMENT_LEVEL_DATA.NAME, "SALARIO_MENSUAL")
			.set(AGREEMENT_LEVEL_DATA.EXPRESSION, "1498.5")
			.newRecord()
			.set(AGREEMENT_LEVEL_DATA.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL, levelII)
			.set(AGREEMENT_LEVEL_DATA.START_DATE, _2018_3_19_Date)
			.set(AGREEMENT_LEVEL_DATA.END_DATE, DSL.castNull(Date.class) )
			.set(AGREEMENT_LEVEL_DATA.NAME, "SALARIO_MENSUAL")
			.set(AGREEMENT_LEVEL_DATA.EXPRESSION, "1869.72")
			.newRecord()
			.set(AGREEMENT_LEVEL_DATA.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL, levelIII)
			.set(AGREEMENT_LEVEL_DATA.START_DATE, _2017StartDate)
			.set(AGREEMENT_LEVEL_DATA.END_DATE, _2018_3_18_Date)
			.set(AGREEMENT_LEVEL_DATA.NAME, "SALARIO_MENSUAL")
			.set(AGREEMENT_LEVEL_DATA.EXPRESSION, "1389.96")
			.newRecord()
			.set(AGREEMENT_LEVEL_DATA.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL, levelIII)
			.set(AGREEMENT_LEVEL_DATA.START_DATE, _2018_3_19_Date)
			.set(AGREEMENT_LEVEL_DATA.END_DATE, DSL.castNull(Date.class) )
			.set(AGREEMENT_LEVEL_DATA.NAME, "SALARIO_MENSUAL")
			.set(AGREEMENT_LEVEL_DATA.EXPRESSION, "1742.83")
			.execute();
			
			List<Integer> domainsIds =
			dslContext
			.select()
			.from(DOMAIN)
			.where(DOMAIN.NAME.like("%fundupv%"))
			.fetch(DOMAIN.ID)
			;
			
			for ( Integer domainId: domainsIds ) {
				dslContext
				.insertInto(APP_PARAM)
				.set(APP_PARAM.DOMAIN, domainId)
				.set(APP_PARAM.NAME, "PAY_SYSTEM_AGREEMENT")
				.set(APP_PARAM.VALUE, Integer.toString(agreementId))
				.execute();

				List<Integer> oldAgreementsIds =
						dslContext
						.select(PAYROLL_WORKPLACE.AGREEMENT)
						.from(PAYROLL_WORKPLACE)
						.where(PAYROLL_WORKPLACE.DOMAIN.eq(domainId))
						.and(PAYROLL_WORKPLACE.AGREEMENT.isNotNull())
						.groupBy(PAYROLL_WORKPLACE.AGREEMENT)
						.fetch(PAYROLL_WORKPLACE.AGREEMENT)
						;
				for ( Integer oldAgreementId: oldAgreementsIds )
					dslContext.insertInto(AGREEMENT_PAYMENT)
					.set(AGREEMENT_PAYMENT.DOMAIN, domainId)
					.set(AGREEMENT_PAYMENT.AGREEMENT,oldAgreementId)
					.set(AGREEMENT_PAYMENT.TYPE, (byte) 1)
					.set(AGREEMENT_PAYMENT.START_DATE, _1970StartDate)
					.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte) 1)
					.set(AGREEMENT_PAYMENT.EXPRESSION, WARNNING)
					.set(AGREEMENT_PAYMENT.DESCRIPTION, "WARNNING")
					.execute();
			}
			

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
