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

public class MEIInsert implements Update {
	
	public static final MEIInsert MEIINSERT = new MEIInsert();

	private static final String MEI = "MEI";
	private static final String MEI_E = "MEI_E";

	private MEIInsert() {
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
			.where(SYSTEM_DEDUCTION.DOMAIN.eq(0))
			.and(DEDUCTION_CONCEPT.CODE.eq(MEI))
			.and(SYSTEM_DEDUCTION.START_DATE.eq(startOf2023Date))
			) >= 1;

		// IF ALREADY EXISTS
				
		if ( upgraded ) 
			return;
		
		// DISABLED FOREING_KEY FOR INSERT
		
		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			

			DeductionConceptRecord meiConcept = 
			dslContext.insertInto(DEDUCTION_CONCEPT)
			.set(DEDUCTION_CONCEPT.DOMAIN, 0)
			.set(DEDUCTION_CONCEPT.CODE, MEI)
			.set(DEDUCTION_CONCEPT.TYPE,(byte)0) // Cotizaciones Sociales, Contingencias Comunes... 
			.set(DEDUCTION_CONCEPT.DESCRIPTION, "Mecanismo de Equidad Intergeneracional")
			.returning()
			.fetchOne()
			;

			dslContext.insertInto(SYSTEM_DEDUCTION)
			.set(SYSTEM_DEDUCTION.DOMAIN, 0)
			.set(SYSTEM_DEDUCTION.START_DATE, startOf2023Date)
			.set(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT, meiConcept.getId())
			.set(SYSTEM_DEDUCTION.DESCRIPTION, "0.10 %")
			.set(SYSTEM_DEDUCTION.EXPRESSION, "BASE_CGC * PORCENTAJE_MEI/100")
			.execute()
			;

			dslContext.insertInto(SYSTEM_COST)
			.set(SYSTEM_COST.DOMAIN, 0)
			.set(SYSTEM_COST.TYPE, (byte)0) // Cotizaciones Sociales, Contingencias Comunes...
			.set(SYSTEM_COST.START_DATE, startOf2023Date)
			.set(SYSTEM_COST.CODE, MEI_E)
			.set(SYSTEM_COST.DESCRIPTION, "0.50 %")
			.set(SYSTEM_COST.EXPRESSION, "BASE_CGC_E * PORCENTAJE_MEI_E/100")
			.execute()
			;
			
			dslContext.insertInto(SYSTEM_DATA)
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, "PORCENTAJE_MEI")
			.set(SYSTEM_DATA.END_DATE, (Date) null)
			.set(SYSTEM_DATA.START_DATE, startOf2023Date)
			.set(SYSTEM_DATA.EXPRESSION, "0.10" )
			.set(SYSTEM_DATA.READ_ONLY, (byte)1)
			.set(SYSTEM_DATA.COMMENTS, (String) null)
			.newRecord()
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, "PORCENTAJE_MEI_E")
			.set(SYSTEM_DATA.END_DATE, (Date) null)
			.set(SYSTEM_DATA.START_DATE, startOf2023Date)
			.set(SYSTEM_DATA.EXPRESSION, "0.50" )
			.set(SYSTEM_DATA.READ_ONLY, (byte)1)
			.set(SYSTEM_DATA.COMMENTS, (String) null)
			.execute()
			;
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
