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

public class SolidarityInsert implements Update {
	
	public static final SolidarityInsert SOLIDARITYINSERT = new SolidarityInsert();

	private static final String SOLIDARIDAD_I = "SOLIDARIDAD_I";
	private static final String SOLIDARIDAD_II = "SOLIDARIDAD_II";
	private static final String SOLIDARIDAD_III = "SOLIDARIDAD_III";
	
	private static final String SOLIDARIDAD_I_E = "SOLIDARIDAD_I_E";
	private static final String SOLIDARIDAD_II_E = "SOLIDARIDAD_II_E";
	private static final String SOLIDARIDAD_III_E = "SOLIDARIDAD_III_E";

	private SolidarityInsert() {
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
		calendar.set(Calendar.YEAR, 2025);
		
		Date startOf2025Date = new Date(calendar.getTimeInMillis());
		
		boolean upgraded = 
		dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DEDUCTION)
			.innerJoin(DEDUCTION_CONCEPT).onKey()
			.where(SYSTEM_DEDUCTION.DOMAIN.eq(0))
			.and(DEDUCTION_CONCEPT.CODE.eq(SOLIDARIDAD_I))
			.and(SYSTEM_DEDUCTION.START_DATE.eq(startOf2025Date))
			) >= 1;

		// IF ALREADY EXISTS
				
		if ( upgraded ) 
			return;
		
		// DISABLED FOREING_KEY FOR INSERT
		
		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext.execute("ALTER TABLE `system_cost` MODIFY `description` varchar(256) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion';");
			dslContext.execute("ALTER TABLE `deduction_concept` MODIFY `description` varchar(256) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion';");
			
			//I
			DeductionConceptRecord solidarityIConcept = 
			dslContext.insertInto(DEDUCTION_CONCEPT)
			.set(DEDUCTION_CONCEPT.DOMAIN, 0)
			.set(DEDUCTION_CONCEPT.CODE, SOLIDARIDAD_I)
			.set(DEDUCTION_CONCEPT.TYPE,(byte)14)  
			.set(DEDUCTION_CONCEPT.DESCRIPTION, "Solidaridad. Exceso primer tramo (hasta el 10% base máxima)")
			.returning()
			.fetchOne()
			;

			dslContext.insertInto(SYSTEM_DEDUCTION)
			.set(SYSTEM_DEDUCTION.DOMAIN, 0)
			.set(SYSTEM_DEDUCTION.START_DATE, startOf2025Date)
			.set(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT, solidarityIConcept.getId())
			.set(SYSTEM_DEDUCTION.EXPRESSION, "BASE_SOLIDARIDAD_I * PORCENTAJE_SOLIDARIDAD_I/100")
			.execute()
			;

			dslContext.insertInto(SYSTEM_COST)
			.set(SYSTEM_COST.DOMAIN, 0)
			.set(SYSTEM_COST.TYPE, (byte)14) 
			.set(SYSTEM_COST.START_DATE, startOf2025Date)
			.set(SYSTEM_COST.CODE, SOLIDARIDAD_I_E)
			.set(SYSTEM_COST.DESCRIPTION, "Solidaridad. Exceso primer tramo (hasta el 10% base máxima)")
			.set(SYSTEM_COST.EXPRESSION, "BASE_SOLIDARIDAD_I * PORCENTAJE_SOLIDARIDAD_I_E/100")
			.execute()
			;
			

			dslContext.insertInto(SYSTEM_DATA)
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, "BASE_SOLIDARIDAD_I")
			.set(SYSTEM_DATA.END_DATE, (Date) null)
			.set(SYSTEM_DATA.START_DATE, startOf2025Date)
			.set(SYSTEM_DATA.EXPRESSION, 
			"_SUM_BASE_CGC_MAX = SUM(BASE_CGC_MAX); "
			+ "_SUM_BASE_CGC_BRUTA = SUM(BASE_CGC_BRUTA); "
			+ "_SUM_BASE_CGC_MAX_10 = ROUND(_SUM_BASE_CGC_MAX * 0.10,2);"
			+ "_EXCESO=ROUND(_SUM_BASE_CGC_BRUTA - _SUM_BASE_CGC_MAX,2);"
			+ "_EXCESO > 0.00 ? MIN(_EXCESO, _SUM_BASE_CGC_MAX_10) : REMOVE()" )
			.set(SYSTEM_DATA.READ_ONLY, (byte)1)
			.set(SYSTEM_DATA.COMMENTS, (String) null)
			.newRecord()
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, "PORCENTAJE_SOLIDARIDAD_I")
			.set(SYSTEM_DATA.END_DATE, (Date) null)
			.set(SYSTEM_DATA.START_DATE, startOf2025Date)
			.set(SYSTEM_DATA.EXPRESSION, "ROUND(0.92 * 4.70 / 28.30,2)" )
			.set(SYSTEM_DATA.READ_ONLY, (byte)1)
			.set(SYSTEM_DATA.COMMENTS, (String) null)
			.newRecord()
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, "PORCENTAJE_SOLIDARIDAD_I_E")
			.set(SYSTEM_DATA.END_DATE, (Date) null)
			.set(SYSTEM_DATA.START_DATE, startOf2025Date)
			.set(SYSTEM_DATA.EXPRESSION, "0.92 - PORCENTAJE_SOLIDARIDAD_I" )
			.set(SYSTEM_DATA.READ_ONLY, (byte)1)
			.set(SYSTEM_DATA.COMMENTS, (String) null)
			.execute()
			;
			
			// II
			DeductionConceptRecord solidarityIIConcept = 
			dslContext.insertInto(DEDUCTION_CONCEPT)
			.set(DEDUCTION_CONCEPT.DOMAIN, 0)
			.set(DEDUCTION_CONCEPT.CODE, SOLIDARIDAD_II)
			.set(DEDUCTION_CONCEPT.TYPE,(byte)14)  
			.set(DEDUCTION_CONCEPT.DESCRIPTION, "Solidaridad. Exceso segundo tramo (desde el 10% hasta el 50% base máxima)")
			.returning()
			.fetchOne()
			;

			dslContext.insertInto(SYSTEM_DEDUCTION)
			.set(SYSTEM_DEDUCTION.DOMAIN, 0)
			.set(SYSTEM_DEDUCTION.START_DATE, startOf2025Date)
			.set(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT, solidarityIIConcept.getId())
			.set(SYSTEM_DEDUCTION.EXPRESSION, "BASE_SOLIDARIDAD_II * PORCENTAJE_SOLIDARIDAD_II/100")
			.execute()
			;

			dslContext.insertInto(SYSTEM_COST)
			.set(SYSTEM_COST.DOMAIN, 0)
			.set(SYSTEM_COST.TYPE, (byte)14) 
			.set(SYSTEM_COST.START_DATE, startOf2025Date)
			.set(SYSTEM_COST.CODE, SOLIDARIDAD_II_E)
			.set(SYSTEM_COST.DESCRIPTION, "Solidaridad. Exceso segundo tramo (desde el 10% hasta el 50% base máxima)")
			.set(SYSTEM_COST.EXPRESSION, "BASE_SOLIDARIDAD_II * PORCENTAJE_SOLIDARIDAD_II_E/100")
			.execute()
			;
			
			dslContext.insertInto(SYSTEM_DATA)
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, "BASE_SOLIDARIDAD_II")
			.set(SYSTEM_DATA.END_DATE, (Date) null)
			.set(SYSTEM_DATA.START_DATE, startOf2025Date)
			.set(SYSTEM_DATA.EXPRESSION, 
			"_SUM_BASE_CGC_MAX = SUM(BASE_CGC_MAX); "
			+ "_SUM_BASE_CGC_BRUTA = SUM(BASE_CGC_BRUTA); "
			+ "_SUM_BASE_CGC_MAX_10 = _SUM_BASE_CGC_MAX * 0.10;"
			+ "_SUM_BASE_CGC_MAX_50 = _SUM_BASE_CGC_MAX * 0.50;"
			+ "_SUM_BASE_CGC_MAX_10_50 = ROUND( _SUM_BASE_CGC_MAX_50 - _SUM_BASE_CGC_MAX_10,2);"
			+ "_EXCESO=ROUND( _SUM_BASE_CGC_BRUTA - _SUM_BASE_CGC_MAX - _SUM_BASE_CGC_MAX_10,2); "
			+ "_EXCESO > 0.00 ? MIN( _EXCESO, _SUM_BASE_CGC_MAX_10_50 ): REMOVE() "
			)
			.set(SYSTEM_DATA.READ_ONLY, (byte)1)
			.set(SYSTEM_DATA.COMMENTS, (String) null)
			.newRecord()
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, "PORCENTAJE_SOLIDARIDAD_II")
			.set(SYSTEM_DATA.END_DATE, (Date) null)
			.set(SYSTEM_DATA.START_DATE, startOf2025Date)
			.set(SYSTEM_DATA.EXPRESSION, "ROUND(1.00 * 4.70 / 28.30,2)" )
			.set(SYSTEM_DATA.READ_ONLY, (byte)1)
			.set(SYSTEM_DATA.COMMENTS, (String) null)
			.newRecord()
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, "PORCENTAJE_SOLIDARIDAD_II_E")
			.set(SYSTEM_DATA.END_DATE, (Date) null)
			.set(SYSTEM_DATA.START_DATE, startOf2025Date)
			.set(SYSTEM_DATA.EXPRESSION, "1.00 - PORCENTAJE_SOLIDARIDAD_II" )
			.set(SYSTEM_DATA.READ_ONLY, (byte)1)
			.set(SYSTEM_DATA.COMMENTS, (String) null)
			.execute()
			;
			
			// III
			DeductionConceptRecord solidarityIIIConcept = 
			dslContext.insertInto(DEDUCTION_CONCEPT)
			.set(DEDUCTION_CONCEPT.DOMAIN, 0)
			.set(DEDUCTION_CONCEPT.CODE, SOLIDARIDAD_III)
			.set(DEDUCTION_CONCEPT.TYPE,(byte)14)  
			.set(DEDUCTION_CONCEPT.DESCRIPTION, "Solidaridad. Exceso tercer tramo (superior al 50% de la base máxima)")
			.returning()
			.fetchOne()
			;

			dslContext.insertInto(SYSTEM_DEDUCTION)
			.set(SYSTEM_DEDUCTION.DOMAIN, 0)
			.set(SYSTEM_DEDUCTION.START_DATE, startOf2025Date)
			.set(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT, solidarityIIIConcept.getId())
			.set(SYSTEM_DEDUCTION.EXPRESSION, "BASE_SOLIDARIDAD_III * PORCENTAJE_SOLIDARIDAD_III/100")
			.execute()
			;

			dslContext.insertInto(SYSTEM_COST)
			.set(SYSTEM_COST.DOMAIN, 0)
			.set(SYSTEM_COST.TYPE, (byte)14) 
			.set(SYSTEM_COST.START_DATE, startOf2025Date)
			.set(SYSTEM_COST.CODE, SOLIDARIDAD_III_E)
			.set(SYSTEM_COST.DESCRIPTION, "Solidaridad. Exceso tercer tramo (superior al 50% de la base máxima)")
			.set(SYSTEM_COST.EXPRESSION, "BASE_SOLIDARIDAD_III * PORCENTAJE_SOLIDARIDAD_III_E/100")
			.execute()
			;
			
			dslContext.insertInto(SYSTEM_DATA)
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, "BASE_SOLIDARIDAD_III")
			.set(SYSTEM_DATA.END_DATE, (Date) null)
			.set(SYSTEM_DATA.START_DATE, startOf2025Date)
			.set(SYSTEM_DATA.EXPRESSION, 
			"_SUM_BASE_CGC_MAX = SUM(BASE_CGC_MAX); "
			+ "_SUM_BASE_CGC_BRUTA = SUM(BASE_CGC_BRUTA); "
			+ "_SUM_BASE_CGC_MAX_50 = _SUM_BASE_CGC_MAX * 0.50 ;"
			+ "_EXCESO=ROUND(_SUM_BASE_CGC_BRUTA - _SUM_BASE_CGC_MAX - _SUM_BASE_CGC_MAX_50, 2);"
			+ "_EXCESO > 0.00 ? _EXCESO : REMOVE() "
			)
			.set(SYSTEM_DATA.READ_ONLY, (byte)1)
			.set(SYSTEM_DATA.COMMENTS, (String) null)
			.newRecord()
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, "PORCENTAJE_SOLIDARIDAD_III")
			.set(SYSTEM_DATA.END_DATE, (Date) null)
			.set(SYSTEM_DATA.START_DATE, startOf2025Date)
			.set(SYSTEM_DATA.EXPRESSION, "ROUND(1.17 * 4.70 / 28.30,2)" )
			.set(SYSTEM_DATA.READ_ONLY, (byte)1)
			.set(SYSTEM_DATA.COMMENTS, (String) null)
			.newRecord()
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, "PORCENTAJE_SOLIDARIDAD_III_E")
			.set(SYSTEM_DATA.END_DATE, (Date) null)
			.set(SYSTEM_DATA.START_DATE, startOf2025Date)
			.set(SYSTEM_DATA.EXPRESSION, "1.17 - PORCENTAJE_SOLIDARIDAD_III" )
			.set(SYSTEM_DATA.READ_ONLY, (byte)1)
			.set(SYSTEM_DATA.COMMENTS, (String) null)
			.execute()
			;

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
