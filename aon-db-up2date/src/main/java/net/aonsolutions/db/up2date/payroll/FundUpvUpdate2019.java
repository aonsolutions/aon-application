package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
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

public class FundUpvUpdate2019 implements Update {

	public static final FundUpvUpdate2019 FUNDUPV_UPDATE_2019 = new FundUpvUpdate2019();
	public static final String SALARIO_MENSUAL = "SALARIO_MENSUAL";

	private FundUpvUpdate2019() {
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
		calendar.set(Calendar.YEAR, 2018);
		LocalDate startDate2018 = new Date(calendar.getTimeInMillis()).toLocalDate();

		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		LocalDate endDate2018 = new Date(calendar.getTimeInMillis()).toLocalDate();

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2019);
		LocalDate startDate2019 = new Date(calendar.getTimeInMillis()).toLocalDate();


		int agreementId = 
		dslContext
		.select()
		.from(AGREEMENT)
		.where(AGREEMENT.DOMAIN.eq(0))
		.and(AGREEMENT.DESCRIPTION.eq("FUNDACI\u00D3N CURSOS DE VERANO DE LA UPV/EHU"))
		.fetchOne(AGREEMENT.ID)
		;
		
		
		
		int levelI =
		dslContext
		.select()
		.from(AGREEMENT_LEVEL)
		.where(AGREEMENT_LEVEL.DOMAIN.eq(0))
		.and(AGREEMENT_LEVEL.AGREEMENT.eq(agreementId))
		.and(AGREEMENT_LEVEL.DESCRIPTION.eq("I"))
		.fetchOne(AGREEMENT_LEVEL.ID)
		;

		int levelII =
		dslContext
		.select()
		.from(AGREEMENT_LEVEL)
		.where(AGREEMENT_LEVEL.DOMAIN.eq(0))
		.and(AGREEMENT_LEVEL.AGREEMENT.eq(agreementId))
		.and(AGREEMENT_LEVEL.DESCRIPTION.eq("II"))
		.fetchOne(AGREEMENT.ID)
		;

		int levelIII =
		dslContext
		.select()
		.from(AGREEMENT_LEVEL)
		.where(AGREEMENT_LEVEL.DOMAIN.eq(0))
		.and(AGREEMENT_LEVEL.AGREEMENT.eq(agreementId))
		.and(AGREEMENT_LEVEL.DESCRIPTION.eq("III"))
		.fetchOne(AGREEMENT.ID)
		;

		boolean upgraded = 
		dslContext.fetchCount(
		dslContext
		.select()
		.from(AGREEMENT_LEVEL_DATA)
		.where(AGREEMENT_LEVEL_DATA.DOMAIN.eq(0))
		.and(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL.in(levelI, levelII, levelIII))
		.and(AGREEMENT_LEVEL_DATA.START_DATE.eq(startDate2019))
		) > 0;

		if ( upgraded )
			return;
		
		

		dslContext.transaction(config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext
			.update(AGREEMENT_LEVEL_DATA)
			.set(AGREEMENT_LEVEL_DATA.END_DATE, endDate2018)
			.where(AGREEMENT_LEVEL_DATA.DOMAIN.eq(0))
			.and(AGREEMENT_LEVEL_DATA.END_DATE.isNull())
			.and(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL.in(levelI, levelII, levelIII))
			;
			
			dslContext
			.insertInto(AGREEMENT_LEVEL_DATA)
			.set(AGREEMENT_LEVEL_DATA.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL, levelI)
			.set(AGREEMENT_LEVEL_DATA.START_DATE, startDate2019)
			.set(AGREEMENT_LEVEL_DATA.END_DATE, DSL.castNull(LocalDate.class) )
			.set(AGREEMENT_LEVEL_DATA.NAME, SALARIO_MENSUAL)
			.set(AGREEMENT_LEVEL_DATA.EXPRESSION, "2312.14")
			.newRecord()
			.set(AGREEMENT_LEVEL_DATA.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL, levelII)
			.set(AGREEMENT_LEVEL_DATA.START_DATE, startDate2019)
			.set(AGREEMENT_LEVEL_DATA.END_DATE, DSL.castNull(LocalDate.class) )
			.set(AGREEMENT_LEVEL_DATA.NAME, SALARIO_MENSUAL)
			.set(AGREEMENT_LEVEL_DATA.EXPRESSION, "1890.29")
			.newRecord()
			.set(AGREEMENT_LEVEL_DATA.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL, levelIII)
			.set(AGREEMENT_LEVEL_DATA.START_DATE, startDate2019)
			.set(AGREEMENT_LEVEL_DATA.END_DATE, DSL.castNull(LocalDate.class) )
			.set(AGREEMENT_LEVEL_DATA.NAME, SALARIO_MENSUAL)
			.set(AGREEMENT_LEVEL_DATA.EXPRESSION, "1762.00")
			.execute();
			

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
