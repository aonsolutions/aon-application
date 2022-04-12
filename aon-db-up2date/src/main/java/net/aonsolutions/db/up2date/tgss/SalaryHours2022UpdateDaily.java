package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.Field;
import org.jooq.InsertSetMoreStep;
import org.jooq.SQLDialect;
import org.jooq.UpdateConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.SystemDataRecord;

import net.aonsolutions.db.up2date.Update;

public class SalaryHours2022UpdateDaily implements Update {

	public static SalaryHours2022UpdateDaily SALARYHOURS2022UPDATEDAILY = new SalaryHours2022UpdateDaily();

	
	private SalaryHours2022UpdateDaily() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		
		
		
		// Establish context
		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2022);
		
		Date start2022Date = new Date(calendar.getTimeInMillis());
		

		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.eq("BASE_CGC_MIN_HORA"))
		.and(SYSTEM_DATA.START_DATE.eq(start2022Date))) > 0;

		if ( upgraded ) 
			return;
		
		
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
		calendar.set(Calendar.YEAR, 2021);
		Date startSeptember2021Date = new Date(calendar.getTimeInMillis());
		
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		calendar.set(Calendar.YEAR, 2021);
		Date end2021Date = new Date(calendar.getTimeInMillis());

		Integer maxBaseCgcMinHora = 
		dslContext.select(DSL.max(SYSTEM_DATA.ID))
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.eq("BASE_CGC_MIN_HORA"))
		.and(SYSTEM_DATA.START_DATE.eq(startSeptember2021Date))
		.fetchOne().value1();

		DeleteConditionStep<SystemDataRecord> deleteDuplicates2021BaseCgcMinHora 
		= dslContext.delete(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.eq("BASE_CGC_MIN_HORA"))
		.and(SYSTEM_DATA.START_DATE.eq(startSeptember2021Date))
		.and(SYSTEM_DATA.ID.lt(	maxBaseCgcMinHora));
		
		
		UpdateConditionStep<SystemDataRecord> 
		close2021BaseCgcMinHora = dslContext.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.END_DATE, end2021Date)
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.eq("BASE_CGC_MIN_HORA"))
		.and(SYSTEM_DATA.START_DATE.eq(startSeptember2021Date));
		 
		
		InsertSetMoreStep<SystemDataRecord> insert2022BaseCgcMinHora = 
		dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN, 0)
		.set(SYSTEM_DATA.NAME, "BASE_CGC_MIN_HORA")
		.set(SYSTEM_DATA.START_DATE, start2022Date)
		.set(SYSTEM_DATA.END_DATE, (Date)null)
		.set(SYSTEM_DATA.EXPRESSION, 
		"["
		+"\"01\":9.82,"
		+"\"02\":8.14,"
		+"\"03\":7.08,"
		+"\"04\":7.03,"
		+"\"05\":7.03,"
		+"\"06\":7.03,"
		+"\"07\":7.03,"
		+"\"08\":7.03,"
		+"\"09\":7.03,"
		+"\"10\":7.03,"
		+"\"11\":7.03][GRUPO_COTIZACION]" 
		)
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		;

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			deleteDuplicates2021BaseCgcMinHora.execute();
			close2021BaseCgcMinHora.execute();
			insert2022BaseCgcMinHora.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
