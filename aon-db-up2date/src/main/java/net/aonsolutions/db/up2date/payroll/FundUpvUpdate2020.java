package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AgreementLevelCategory.AGREEMENT_LEVEL_CATEGORY;
import static com.esferalia.aon.jooq.tables.AgreementLevelData.AGREEMENT_LEVEL_DATA;

import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class FundUpvUpdate2020 implements Update {

	public static final FundUpvUpdate2020 FUNDUPV_UPDATE_2020 = new FundUpvUpdate2020();
	public static final String SALARIO_MENSUAL = "SALARIO_MENSUAL";

	private FundUpvUpdate2020() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2019);
		LocalDate startDate2019 = new Date(calendar.getTimeInMillis()).toLocalDate();

		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		LocalDate endDate2019 = new Date(calendar.getTimeInMillis()).toLocalDate();

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2020);
		LocalDate startDate2020 = new Date(calendar.getTimeInMillis()).toLocalDate();


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
		
		Integer levelI =
		dslContext
		.select()
		.from(AGREEMENT_LEVEL)
		.where(AGREEMENT_LEVEL.DOMAIN.eq(0))
		.and(AGREEMENT_LEVEL.AGREEMENT.eq(agreementId))
		.and(AGREEMENT_LEVEL.DESCRIPTION.eq("I"))
		.fetchOne(AGREEMENT_LEVEL.ID)
		;

		if ( levelI == null )
			return;

		Integer levelII =
		dslContext
		.select()
		.from(AGREEMENT_LEVEL)
		.where(AGREEMENT_LEVEL.DOMAIN.eq(0))
		.and(AGREEMENT_LEVEL.AGREEMENT.eq(agreementId))
		.and(AGREEMENT_LEVEL.DESCRIPTION.eq("II"))
		.fetchOne(AGREEMENT.ID)
		;

		if ( levelII == null )
			return;

		Integer levelIII =
		dslContext
		.select()
		.from(AGREEMENT_LEVEL)
		.where(AGREEMENT_LEVEL.DOMAIN.eq(0))
		.and(AGREEMENT_LEVEL.AGREEMENT.eq(agreementId))
		.and(AGREEMENT_LEVEL.DESCRIPTION.eq("III"))
		.fetchOne(AGREEMENT.ID)
		;


		if ( levelIII == null )
			return;

		boolean upgraded = 
		dslContext.fetchCount(
		dslContext
		.select()
		.from(AGREEMENT_LEVEL_DATA)
		.where(AGREEMENT_LEVEL_DATA.DOMAIN.eq(0))
		.and(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL.in(levelI, levelII, levelIII))
		.and(AGREEMENT_LEVEL_DATA.START_DATE.eq(startDate2020))
		) > 0;

		if ( upgraded )
			return;
		
		

		dslContext.transaction(config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext
			.update(AGREEMENT_LEVEL_DATA)
			.set(AGREEMENT_LEVEL_DATA.END_DATE, endDate2019)
			.where(AGREEMENT_LEVEL_DATA.DOMAIN.eq(0))
			.and(AGREEMENT_LEVEL_DATA.END_DATE.isNull())
			.and(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL.in(levelI, levelII, levelIII))
			;
			
			int levelIV =
			dslContext
			.insertInto(AGREEMENT_LEVEL)
			.set(AGREEMENT_LEVEL.DOMAIN, 0)
			.set(AGREEMENT_LEVEL.AGREEMENT, agreementId)
			.set(AGREEMENT_LEVEL.DESCRIPTION, "IV")
			.returning()
			.fetchOne()
			.getId();

			int levelV =
			dslContext
			.insertInto(AGREEMENT_LEVEL)
			.set(AGREEMENT_LEVEL.DOMAIN, 0)
			.set(AGREEMENT_LEVEL.AGREEMENT, agreementId)
			.set(AGREEMENT_LEVEL.DESCRIPTION, "V")
			.returning()
			.fetchOne()
			.getId();

			dslContext
			.insertInto(AGREEMENT_LEVEL_CATEGORY)
			.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, levelI)
			.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION, "Responsable gabinete prensa")
			.newRecord()
			.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, levelIV)
			.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION, "Telefonista-recepcionista")
			.newRecord()
			.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, levelV)
			.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION, "Auxiliar administrativo")
			.newRecord()
			.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, levelV)
			.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION, "Auxiliar Telefonista-recepcionista")
			.execute();

			dslContext
			.insertInto(AGREEMENT_LEVEL_DATA)
			.set(AGREEMENT_LEVEL_DATA.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL, levelI)
			.set(AGREEMENT_LEVEL_DATA.START_DATE, startDate2020)
			.set(AGREEMENT_LEVEL_DATA.END_DATE, DSL.castNull(LocalDate.class) )
			.set(AGREEMENT_LEVEL_DATA.NAME, SALARIO_MENSUAL)
			.set(AGREEMENT_LEVEL_DATA.EXPRESSION, "2339.84")
			.newRecord()
			.set(AGREEMENT_LEVEL_DATA.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL, levelII)
			.set(AGREEMENT_LEVEL_DATA.START_DATE, startDate2020)
			.set(AGREEMENT_LEVEL_DATA.END_DATE, DSL.castNull(LocalDate.class) )
			.set(AGREEMENT_LEVEL_DATA.NAME, SALARIO_MENSUAL)
			.set(AGREEMENT_LEVEL_DATA.EXPRESSION, "1912.97")
			.newRecord()
			.set(AGREEMENT_LEVEL_DATA.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL, levelIII)
			.set(AGREEMENT_LEVEL_DATA.START_DATE, startDate2020)
			.set(AGREEMENT_LEVEL_DATA.END_DATE, DSL.castNull(LocalDate.class) )
			.set(AGREEMENT_LEVEL_DATA.NAME, SALARIO_MENSUAL)
			.set(AGREEMENT_LEVEL_DATA.EXPRESSION, "1774.45")
			.newRecord()
			.set(AGREEMENT_LEVEL_DATA.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL, levelIV)
			.set(AGREEMENT_LEVEL_DATA.START_DATE, startDate2020)
			.set(AGREEMENT_LEVEL_DATA.END_DATE, DSL.castNull(LocalDate.class) )
			.set(AGREEMENT_LEVEL_DATA.NAME, SALARIO_MENSUAL)
			.set(AGREEMENT_LEVEL_DATA.EXPRESSION, "1563.17")
			.newRecord()
			.set(AGREEMENT_LEVEL_DATA.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL, levelV)
			.set(AGREEMENT_LEVEL_DATA.START_DATE, startDate2020)
			.set(AGREEMENT_LEVEL_DATA.END_DATE, DSL.castNull(LocalDate.class) )
			.set(AGREEMENT_LEVEL_DATA.NAME, SALARIO_MENSUAL)
			.set(AGREEMENT_LEVEL_DATA.EXPRESSION, "1377.29")
			.execute();
			

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
