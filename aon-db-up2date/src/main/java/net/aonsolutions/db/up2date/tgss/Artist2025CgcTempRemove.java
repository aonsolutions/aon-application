package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemCost.SYSTEM_COST;
import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.InsertValuesStep7;
import org.jooq.SQLDialect;
import org.jooq.UpdateConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.SystemCost;
import com.esferalia.aon.jooq.tables.records.SystemDataRecord;

import net.aonsolutions.db.up2date.Update;

public class Artist2025CgcTempRemove implements Update {
	
	private static final String CGC_E_TEMP = "CGC_E_TEMP";

	public static Artist2025CgcTempRemove ARTIST2025CGCTEMPREMOVE = new Artist2025CgcTempRemove();

	private Artist2025CgcTempRemove() {
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
		
		boolean upgraded = 
		dslContext.fetchCount(
		dslContext.selectFrom(SYSTEM_COST)
		.where(SYSTEM_COST.CODE.eq(CGC_E_TEMP))
		.and(SYSTEM_COST.DOMAIN.eq(-108))) > 0;
		
		if (upgraded) {
			// If already upgraded, do nothing
			return;
		}

		// DISABLED FOREING_KEY FOR INSERT
		
		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			// START_DATE 01/01/2010
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, Calendar.JANUARY);
			calendar.set(Calendar.YEAR, 2010);
			
			Date startOf2010 = new Date(calendar.getTimeInMillis());

			dslContext
			.insertInto(SYSTEM_COST)
			.set(SYSTEM_COST.DOMAIN, -108)
			.set(SYSTEM_COST.TYPE, (byte)0)
			.set(SYSTEM_COST.CODE, CGC_E_TEMP)
			.set(SYSTEM_COST.START_DATE, startOf2010)
			.set(SYSTEM_COST.END_DATE, DSL.castNull(Date.class))
			.set(SYSTEM_COST.EXPRESSION, "REMOVE()")
			.set(SYSTEM_COST.DESCRIPTION, "COT. ADICIONAL CONTRATOS CORTA DURACI\u00F3N")
			.execute()
			;
		
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
