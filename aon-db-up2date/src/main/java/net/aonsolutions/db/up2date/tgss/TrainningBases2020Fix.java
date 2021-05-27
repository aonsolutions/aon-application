package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.SQLDialect;
import org.jooq.UpdateConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.SystemDataRecord;

import net.aonsolutions.db.up2date.Update;

public class TrainningBases2020Fix implements Update {

	public static TrainningBases2020Fix TRAINNINGBASES2020FIX = new TrainningBases2020Fix();

	private static final String DIAS_MES = "DIAS_MES";
	private static final String BASE_CGP_MIN = "BASE_CGP_MIN";
	private static final String BASE_REGULADORA = "BASE_REGULADORA";
	
	private TrainningBases2020Fix() {
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
		calendar.set(Calendar.YEAR, 2019);
		
		Date _2019StartDate = new Date(calendar.getTimeInMillis());


		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(-101))
		.and(SYSTEM_DATA.NAME.eq("BASE_CGP_MIN"))
		.and(SYSTEM_DATA.START_DATE.eq(_2019StartDate))) == 1;
		


		if ( upgraded ) 
			return;
		
		InsertSetMoreStep<SystemDataRecord> insert = dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN , -101)
		.set(SYSTEM_DATA.NAME , BASE_CGP_MIN)
		.set(SYSTEM_DATA.START_DATE , _2019StartDate)
		.set(SYSTEM_DATA.EXPRESSION, "(POR_HORAS() ? 6.33 * HORAS_NOMINA : 1050.00 * (DIAS_NOMINA/DIAS_MES) * COEFICIENTE_PARCIALIDAD)") 
		.newRecord()
		.set(SYSTEM_DATA.DOMAIN , -101)
		.set(SYSTEM_DATA.NAME , BASE_REGULADORA)
		.set(SYSTEM_DATA.START_DATE , _2019StartDate)
		.set(SYSTEM_DATA.EXPRESSION, "BASE_CGP_MIN / DIAS_NOMINA") 
		
		;

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			insert.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
