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

import com.esferalia.aon.jooq.tables.records.DeductionConceptRecord;

import net.aonsolutions.db.up2date.Update;

public class MEITrainingRemove implements Update {
	
	public static final MEITrainingRemove MEITRAININGREMOVE = new MEITrainingRemove();

	private static final String MEI = "MEI";
	private static final String MEI_E = "MEI_E";
	
	private static final int FELLOWS_DOMAIN = -105;
	private static final int TRAINING_DOMAIN = -101;

	private MEITrainingRemove() {
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
		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		// START_DATE 01/01/2023
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2023);
		
		Date startOf2023Date = new Date(calendar.getTimeInMillis());
		
		boolean upgraded = 
		dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DEDUCTION)
			.innerJoin(DEDUCTION_CONCEPT).onKey()
			.where(SYSTEM_DEDUCTION.DOMAIN.in(
				FELLOWS_DOMAIN, 
				TRAINING_DOMAIN))
			.and(DEDUCTION_CONCEPT.CODE.eq(MEI))
			.and(SYSTEM_DEDUCTION.START_DATE.eq(startOf2023Date))
			) > 1;

		// IF ALREADY EXISTS
				
		if ( upgraded ) 
			return;
		
		// DISABLED FOREING_KEY FOR INSERT
		
		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			

			DeductionConceptRecord meiConcept = 
			dslContext.select()
			.from(DEDUCTION_CONCEPT)
			.where(DEDUCTION_CONCEPT.CODE.eq(MEI))
			.fetchOneInto(DEDUCTION_CONCEPT)
			;

			dslContext.insertInto(SYSTEM_DEDUCTION)
			.set(SYSTEM_DEDUCTION.DOMAIN, FELLOWS_DOMAIN)
			.set(SYSTEM_DEDUCTION.START_DATE, startOf2023Date)
			.set(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT, meiConcept.getId())
			.set(SYSTEM_DEDUCTION.EXPRESSION, "REMOVE()")
			.newRecord()
			.set(SYSTEM_DEDUCTION.DOMAIN, TRAINING_DOMAIN)
			.set(SYSTEM_DEDUCTION.START_DATE, startOf2023Date)
			.set(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT, meiConcept.getId())
			.set(SYSTEM_DEDUCTION.EXPRESSION, "REMOVE()")
			.execute()
			;

			dslContext.insertInto(SYSTEM_COST)
			.set(SYSTEM_COST.DOMAIN, FELLOWS_DOMAIN)
			.set(SYSTEM_COST.TYPE, (byte)0) 
			.set(SYSTEM_COST.START_DATE, startOf2023Date)
			.set(SYSTEM_COST.CODE, MEI_E)
			.set(SYSTEM_COST.EXPRESSION, "REMOVE()")
			.newRecord()
			.set(SYSTEM_COST.DOMAIN, TRAINING_DOMAIN)
			.set(SYSTEM_COST.TYPE, (byte)0) 
			.set(SYSTEM_COST.START_DATE, startOf2023Date)
			.set(SYSTEM_COST.CODE, MEI_E)
			.set(SYSTEM_COST.EXPRESSION, "REMOVE()")
			.execute()
			;
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
