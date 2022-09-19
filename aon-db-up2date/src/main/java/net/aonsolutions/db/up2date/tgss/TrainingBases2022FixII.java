package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.DeductionConcept.DEDUCTION_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemCost.SYSTEM_COST;
import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;
import static com.esferalia.aon.jooq.tables.SystemDeduction.SYSTEM_DEDUCTION;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class TrainingBases2022FixII implements Update {

	private static final String DESMPL = "DESMPL";
	private static final String DESMPL_E = "DESMPL_E";
	private static final String BASE_CGP_MIN = "BASE_CGP_MIN";
	private static final String PORCENTAJE_DESMPL = "PORCENTAJE_DESMPL";
	private static final String PORCENTAJE_DESMPL_E = "PORCENTAJE_DESMPL_E";

	public static final TrainingBases2022FixII TRAININGBASES2022FIXII = new TrainingBases2022FixII();
	
	private TrainingBases2022FixII() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2022);
		
		Date startDate = new Date(calendar.getTimeInMillis());
		
		boolean upgraded = dslContext.fetchCount(
			DSL
			.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.in(-101, -105))
			.and(SYSTEM_DATA.START_DATE.eq(startDate))
			.and(SYSTEM_DATA.NAME.eq(PORCENTAJE_DESMPL))
			) == 2 ;
				
		if ( upgraded )
			return;

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext
			.delete(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.in(-101, -105))
			.and(SYSTEM_DATA.START_DATE.eq(startDate))
			.and(SYSTEM_DATA.NAME.eq(BASE_CGP_MIN))
			.execute()
			;
			
			dslContext
			.insertInto(SYSTEM_DATA)
			.set(SYSTEM_DATA.DOMAIN,-101)
			.set(SYSTEM_DATA.NAME, BASE_CGP_MIN)
			.set(SYSTEM_DATA.START_DATE, startDate)
			.set(SYSTEM_DATA.EXPRESSION, "1166.70 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)")
			.newRecord()
			.set(SYSTEM_DATA.DOMAIN,-105)
			.set(SYSTEM_DATA.NAME, BASE_CGP_MIN)
			.set(SYSTEM_DATA.START_DATE, startDate)
			.set(SYSTEM_DATA.EXPRESSION, "1166.70 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)")
			.execute()
			;

			dslContext
			.delete(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.in(-101, -105))
			.and(SYSTEM_DATA.START_DATE.eq(startDate))
			.and(SYSTEM_DATA.NAME.eq(PORCENTAJE_DESMPL))
			.execute()
			;
			
			dslContext
			.insertInto(SYSTEM_DATA)
			.set(SYSTEM_DATA.DOMAIN,-101)
			.set(SYSTEM_DATA.NAME, PORCENTAJE_DESMPL)
			.set(SYSTEM_DATA.START_DATE, startDate)
			.set(SYSTEM_DATA.EXPRESSION, "1.55")
			.newRecord()
			.set(SYSTEM_DATA.DOMAIN,-105)
			.set(SYSTEM_DATA.NAME, PORCENTAJE_DESMPL)
			.set(SYSTEM_DATA.START_DATE, startDate)
			.set(SYSTEM_DATA.EXPRESSION, "1.55")
			.execute()
			;

			dslContext
			.delete(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.in(-101, -105))
			.and(SYSTEM_DATA.START_DATE.eq(startDate))
			.and(SYSTEM_DATA.NAME.eq(PORCENTAJE_DESMPL_E))
			.execute()
			;

			dslContext
			.insertInto(SYSTEM_DATA)
			.set(SYSTEM_DATA.DOMAIN,-101)
			.set(SYSTEM_DATA.NAME, PORCENTAJE_DESMPL_E)
			.set(SYSTEM_DATA.START_DATE, startDate)
			.set(SYSTEM_DATA.EXPRESSION, "5.50")
			.newRecord()
			.set(SYSTEM_DATA.DOMAIN,-105)
			.set(SYSTEM_DATA.NAME, PORCENTAJE_DESMPL_E)
			.set(SYSTEM_DATA.START_DATE, startDate)
			.set(SYSTEM_DATA.EXPRESSION, "5.50")
			.execute()
			;

			dslContext
			.update(SYSTEM_DEDUCTION)
			.set(SYSTEM_DEDUCTION.EXPRESSION, DSL.concat(PORCENTAJE_DESMPL+";", SYSTEM_DEDUCTION.EXPRESSION))
			.where(SYSTEM_DEDUCTION.DOMAIN.in(-101, -105))
			.and(SYSTEM_DEDUCTION.START_DATE.eq(startDate))
			.and(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT.in(
			DSL.select(DEDUCTION_CONCEPT.ID)
			.from(DEDUCTION_CONCEPT)
			.where(DEDUCTION_CONCEPT.DOMAIN.eq(0))
			.and(DEDUCTION_CONCEPT.CODE.eq(DESMPL))))
			.execute()
			;
			
			dslContext
			.update(SYSTEM_COST)
			.set(SYSTEM_COST.EXPRESSION, DSL.concat(PORCENTAJE_DESMPL_E+";", SYSTEM_COST.EXPRESSION))
			.where(SYSTEM_COST.DOMAIN.in(-101, -105))
			.and(SYSTEM_COST.START_DATE.eq(startDate))
			.and(SYSTEM_COST.CODE.eq(DESMPL_E))
			.execute()
			;

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
			
		});
	}

}
