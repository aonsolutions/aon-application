package net.aonsolutions.db.up2date.irpf;

import static com.esferalia.aon.jooq.tables.GeozoneIrpf.GEOZONE_IRPF;
import static com.esferalia.aon.jooq.tables.GeozoneIrpfDescendant.GEOZONE_IRPF_DESCENDANT;
import static com.esferalia.aon.jooq.tables.GeozoneIrpfHandicap.GEOZONE_IRPF_HANDICAP;

import java.sql.Connection;
import java.sql.Date;
import java.util.Arrays;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.InsertSetStep;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.UpdateConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.GeozoneIrpfDescendantRecord;
import com.esferalia.aon.jooq.tables.records.GeozoneIrpfHandicapRecord;
import com.esferalia.aon.jooq.tables.records.GeozoneIrpfRecord;

import net.aonsolutions.db.up2date.Update;

public class IrpfEuskadi2025Insert implements Update {
	
	private static final Double [] LEVELS  = {
		0.00,
		20000.01,
		20390.01,
		21170.01,
		22010.01,
		23080.01,
		23880.01,
		
		25040.01,
		27010.01,
		29350.01,
		
		32120.01,
		35470.01,
		40060.01,
		43730.01,
		
		47140.01,
		51170.01,
		
		55850.01,
		60930.01,
		64750.01,
		69050.01,
		73920.01,
		
		79550.01,
		85420.01,
		90760.01,
		96820.01,
		103500.01,
		111430.01,
		
		120120.01,
		130140.01,
		141450.01,
		154810.01,
		169550.01,
		
		187400.01,
		209450.01,
		232180.01,
	};

	private static final double [] [] DESCENDANTS_IRPFS  = {
		{0, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40},
		{0, 5, 6, 7,  9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40},
		{0, 3, 4, 6,  7,  8,  9, 10, 11, 13, 14, 15, 16, 17, 18, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 36, 37, 38, 39, 40},
		{0, 0, 1, 2,  4,  5,  6,  7,  9, 10, 12, 13, 15, 16, 17, 18, 20, 21, 22, 23, 24, 25, 26, 27, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39},
		{0, 0, 0, 0,  0,  1,  3,  4,  6,  8,  9, 11, 13, 14, 15, 17, 18, 19, 21, 22, 23, 24, 25, 27, 28, 29, 30, 31, 32, 33, 35, 36, 37, 38, 39},
		{0, 0, 0, 0,  0,  0,  0,  0,  2,  4,  6,  8, 10, 12, 13, 15, 16, 18, 19, 20, 22, 23, 24, 25, 27, 28, 29, 30, 32, 33, 34, 35, 36, 37, 39},
		{0, 0, 0, 0,  0,  0,  0,  0,  0,  0,  0,  0,  2,  5,  7,  9, 11, 13, 14, 16, 17, 19, 21, 22, 23, 25, 26, 28, 29, 31, 32, 33, 35, 36, 37},
        };
        
        private static final double [] [] HANDICAP_IRPFS = {
        		{ 9, 9, 9, 9, 9, 9, 9	, 7, 7, 7	, 6, 6, 6, 6,	 5, 5	, 4, 4, 4, 4, 4		, 3, 3, 3, 3, 3, 3		, 2, 2, 2, 2, 2		, 1, 1, 1},
        		{12,12,12,12,12,12,12	,12,12,12	,10,10,10,10	,10,10	, 8, 8, 8, 8, 8		, 6, 6, 6, 6, 6, 6		, 5, 5, 5, 5, 5		, 3, 3, 3},
        		{12,12,12,12,12,12,12	,12,12,12	,10,10,10,10	,10,10	, 8, 8, 8, 8, 8		, 6, 6, 6, 6, 6, 6		, 5, 5, 5, 5, 5		, 3, 3, 3},
        };

	private static final String ARABA 	= "01";
	private static final String BIZKAIA 	= "48";
	private static final String GIPUZKOA 	= "20";
	
	private static final String [] GEOZONES = { ARABA, BIZKAIA, GIPUZKOA };
	

	public static final IrpfEuskadi2025Insert IRPFEUSKADI2025INSERT = new IrpfEuskadi2025Insert();
	
	private IrpfEuskadi2025Insert() {
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
		calendar.set(Calendar.MONTH, Calendar.JULY);
		calendar.set(Calendar.YEAR, 2025);
		
		Date startJuly2025Date = new Date(calendar.getTimeInMillis());

		boolean upgraded = true;
		for ( String geozoneCode : GEOZONES ) { 
			Double [] amounts = dslContext
				.select(GEOZONE_IRPF.AMOUNT)
				.from(GEOZONE_IRPF)
				.where(GEOZONE_IRPF.GEOZONE_CODE.eq(geozoneCode)
				.and(GEOZONE_IRPF.START_DATE.eq(startJuly2025Date)))
				.orderBy(GEOZONE_IRPF.AMOUNT.asc())
				.fetchArray(GEOZONE_IRPF.AMOUNT);
			 upgraded &= Arrays.equals(amounts, LEVELS); 
		}
		
		if ( upgraded ) 
			return;
		

		calendar.add(Calendar.DAY_OF_MONTH, -1);
		Date endJune2025Date = new Date(calendar.getTimeInMillis());
		
		UpdateConditionStep<GeozoneIrpfRecord> closeJune2025Update = dslContext
		.update(GEOZONE_IRPF)
		.set(GEOZONE_IRPF.END_DATE, endJune2025Date)
		.where(GEOZONE_IRPF.END_DATE.isNull())
		.and(GEOZONE_IRPF.GEOZONE_CODE.in(GEOZONES));

		SelectConditionStep<Record1<Integer>> geozone2024Irpfs = 
			dslContext
			.select(GEOZONE_IRPF.ID)
			.from(GEOZONE_IRPF)
			.where(GEOZONE_IRPF.START_DATE.eq(startJuly2025Date))
			.and(GEOZONE_IRPF.GEOZONE_CODE.in(GEOZONES));
		
		DeleteConditionStep<GeozoneIrpfDescendantRecord> delete2024GeozoneIrpfDescendant = dslContext
			.delete(GEOZONE_IRPF_DESCENDANT)
			.where(GEOZONE_IRPF_DESCENDANT.GEOZONE_IRPF.in(geozone2024Irpfs));
		
		DeleteConditionStep<GeozoneIrpfHandicapRecord> delete2024GeozoneIrpfHandicap = dslContext
			.delete(GEOZONE_IRPF_HANDICAP)
			.where(GEOZONE_IRPF_HANDICAP.GEOZONE_IRPF.in(geozone2024Irpfs));

		DeleteConditionStep<GeozoneIrpfRecord> delete2024GeozoneIrpf = dslContext
			.delete(GEOZONE_IRPF)
			.where(GEOZONE_IRPF.START_DATE.eq(startJuly2025Date))
			.and(GEOZONE_IRPF.GEOZONE_CODE.in(GEOZONES));
		
		int geozoneIrpfID = dslContext
		.select(DSL.max(GEOZONE_IRPF.ID))
		.from(GEOZONE_IRPF)
		.fetchOptional(DSL.max(GEOZONE_IRPF.ID))
		.orElse(0);
		
		
		InsertSetStep<GeozoneIrpfRecord> geozoneIrpfInsertSetStep  = 
				dslContext.insertInto(GEOZONE_IRPF);
		InsertSetMoreStep<GeozoneIrpfRecord> geozoneIrpfInsertSetMoreStep = null; 

		InsertSetStep<GeozoneIrpfDescendantRecord> irpfDescendantInsertSetStep  = 
				dslContext.insertInto(GEOZONE_IRPF_DESCENDANT);
		InsertSetMoreStep<GeozoneIrpfDescendantRecord> irpfDescendantInsertSetMoreStep = null; 

		InsertSetStep<GeozoneIrpfHandicapRecord> irpfHandicapInsertSetStep  = 
				dslContext.insertInto(GEOZONE_IRPF_HANDICAP);
		InsertSetMoreStep<GeozoneIrpfHandicapRecord> irpfHandicapInsertSetMoreStep = null; 

		for ( String geozone : GEOZONES ) {
			for ( int level = 0; level <  LEVELS.length; level++ ) {
				geozoneIrpfInsertSetMoreStep = geozoneIrpfInsertSetStep
					.set(GEOZONE_IRPF.ID, ++geozoneIrpfID)
					.set(GEOZONE_IRPF.GEOZONE_CODE, geozone)
					.set(GEOZONE_IRPF.GEOZONE_CODE, geozone)
					.set(GEOZONE_IRPF.START_DATE, startJuly2025Date)
					.set(GEOZONE_IRPF.END_DATE, DSL.castNull(GEOZONE_IRPF.END_DATE))
					.set(GEOZONE_IRPF.AMOUNT, LEVELS[level]);
				geozoneIrpfInsertSetStep = geozoneIrpfInsertSetMoreStep.newRecord();
				for ( int descendants = 0; descendants < DESCENDANTS_IRPFS.length; descendants++ ) {
					irpfDescendantInsertSetMoreStep = irpfDescendantInsertSetStep
							.set(GEOZONE_IRPF_DESCENDANT.GEOZONE_IRPF, geozoneIrpfID)
							.set(GEOZONE_IRPF_DESCENDANT.DESCENDANT, (byte)descendants )
							.set(GEOZONE_IRPF_DESCENDANT.PERCENT, DESCENDANTS_IRPFS[descendants][level])
							;
					irpfDescendantInsertSetStep = irpfDescendantInsertSetMoreStep.newRecord();
				}
				for ( int handicaps = 0; handicaps < HANDICAP_IRPFS.length; handicaps++ ) {
					irpfHandicapInsertSetMoreStep = irpfHandicapInsertSetStep
							.set(GEOZONE_IRPF_HANDICAP.GEOZONE_IRPF, geozoneIrpfID)
							.set(GEOZONE_IRPF_HANDICAP.HANDICAP, (byte)handicaps )
							.set(GEOZONE_IRPF_HANDICAP.PERCENT, HANDICAP_IRPFS[handicaps][level])
							;
					irpfHandicapInsertSetStep = irpfHandicapInsertSetMoreStep.newRecord();
				}
			}
		}
		
		// Make then efective final.  
		InsertSetMoreStep<GeozoneIrpfRecord> geozoneInsertSetMoreStep = geozoneIrpfInsertSetMoreStep; 
		InsertSetMoreStep<GeozoneIrpfDescendantRecord> descendantInsertSetMoreStep = irpfDescendantInsertSetMoreStep; 
		InsertSetMoreStep<GeozoneIrpfHandicapRecord> handicapInsertSetMoreStep = irpfHandicapInsertSetMoreStep; 

		dslContext.transaction( config -> {
			closeJune2025Update.execute();
			
			delete2024GeozoneIrpfDescendant.execute();
			delete2024GeozoneIrpfHandicap.execute();
			delete2024GeozoneIrpf.execute();
			
			geozoneInsertSetMoreStep.execute();
			descendantInsertSetMoreStep.execute();
			handicapInsertSetMoreStep.execute();
		});
		
	}
	
	

}
