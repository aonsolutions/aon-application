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

import com.esferalia.aon.jooq.tables.SystemData;
import com.esferalia.aon.jooq.tables.records.DeductionConceptRecord;

import net.aonsolutions.db.up2date.Update;

public class MEITrainingBaseCgcMinUndo implements Update {
	
	public static final MEITrainingBaseCgcMinUndo MEITRAININGBASECGCMINUNDO = new MEITrainingBaseCgcMinUndo();

	private static final String MEI = "MEI";
	private static final String MEI_E = "MEI_E";
	
	private static final int FELLOWS_DOMAIN = -105;
	private static final int TRAINING_DOMAIN = -101;

	private MEITrainingBaseCgcMinUndo() {
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
		
		
		// DISABLED FOREING_KEY FOR INSERT
		
		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			

			DeductionConceptRecord meiConcept = 
			dslContext.select()
			.from(DEDUCTION_CONCEPT)
			.where(DEDUCTION_CONCEPT.CODE.eq(MEI))
			.fetchOneInto(DEDUCTION_CONCEPT);
			

			dslContext.update(SYSTEM_DEDUCTION)
			.set(SYSTEM_DEDUCTION.EXPRESSION, "1260.00 * PORCENTAJE_MEI/100")
			.where(SYSTEM_DEDUCTION.DOMAIN.in(FELLOWS_DOMAIN, TRAINING_DOMAIN))
			.and(SYSTEM_DEDUCTION.START_DATE.eq(startOf2023Date))
			.and(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT.eq(meiConcept.getId()))
			.execute();
			
			dslContext.update(SYSTEM_COST)
			.set(SYSTEM_COST.EXPRESSION, "1260.00 * PORCENTAJE_MEI_E/100")
			.where(SYSTEM_COST.DOMAIN.in(FELLOWS_DOMAIN, TRAINING_DOMAIN))
			.and(SYSTEM_COST.START_DATE.eq(startOf2023Date))
			.and(SYSTEM_COST.CODE.eq(MEI_E))
			.execute();

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
