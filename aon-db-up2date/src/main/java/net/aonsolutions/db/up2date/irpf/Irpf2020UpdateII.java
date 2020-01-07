package net.aonsolutions.db.up2date.irpf;

import static com.esferalia.aon.jooq.tables.GeozoneIrpf.GEOZONE_IRPF;
import static com.esferalia.aon.jooq.tables.GeozoneIrpfDescendant.GEOZONE_IRPF_DESCENDANT;
import static com.esferalia.aon.jooq.tables.GeozoneIrpfHandicap.GEOZONE_IRPF_HANDICAP;

import java.sql.Connection;
import java.sql.Date;
import java.util.Arrays;
import java.util.Calendar;

import org.jooq.DSLContext;
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

public class Irpf2020UpdateII implements Update {
	
	private static final Double LEVELS [] = {
			0.00, //0.01,
			11860.01,
			12340.01,
			12850.01,
			13410.01,
			14020.01,
			14690.01,
			15420.01,
			16310.01,
			17540.01,
			18970.01,
			20500.01,
			21840.01,
			23340.01,
			25080.01,
			27250.01,
			29790.01,
			32880.01,
			37070.01,
			40210.01,
			43320.01,
			46760.01,
			50350.01,
			54520.01,
			57900.01,
			61740.01,
			66140.01,
			71180.01,
			76720.01,
			81370.01,
			86760.01,
			92930.01,
			100050.01,
			107900.01,
			116860.01,
			127460.01,
			139130.01,
			152870.01,
			168970.01,
			188840.01,
			209480.01
	};

	private static final double DESCENDANTS_IRPFS [] [] = {
			{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40},
			{0,0,0,1,2,3,4,5,6,7,8,10,11,12,13,14,15,16,17,18,19,20,21,22,23,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40},
			{0,0,0,0,0,0,2,3,4,5,7,8,9,10,11,13,14,15,16,17,18,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,36,37,38,39,40},
			{0,0,0,0,0,0,0,0,0,2,3,5,6,8,9,10,12,13,15,16,17,18,19,21,22,23,24,25,26,27,29,30,31,32,33,34,35,36,37,38,39},
			{0,0,0,0,0,0,0,0,0,0,0,1,3,4,6,8,9,11,13,14,15,17,18,19,21,22,23,24,25,27,28,29,30,31,32,33,35,36,37,38,39},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,1,2,4,6,8,10,12,13,15,16,18,19,20,22,23,24,25,27,28,29,30,32,33,34,35,36,37,39},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,5,7,9,11,12,14,16,17,19,20,22,23,25,26,28,29,31,32,33,35,36,37}
	};
	
	private static final double HANDICAP_IRPFS [] [] = {
			{ 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 7, 7, 7, 6, 6, 6, 6, 5, 5, 4, 4, 4, 4, 4, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2, 2, 1, 1, 1},
			{12,12,12,12,12,12,12,12,12,12,12,12,12,12,12,12,10,10,10,10,10,10, 8, 8, 8, 8, 8, 6, 6, 6, 6, 6, 6, 5, 5, 5, 5, 5, 3, 3, 3},
			{12,12,12,12,12,12,12,12,12,12,12,12,12,12,12,12,10,10,10,10,10,10, 8, 8, 8, 8, 8, 6, 6, 6, 6, 6, 6, 5, 5, 5, 5, 5, 3, 3, 3},
	};

	private static final String ARABA 		= "01";
	private static final String BIZKAIA 	= "48";
	private static final String GIPUZKOA 	= "20";
	
	private static final String GEOZONES [] = { ARABA, BIZKAIA, GIPUZKOA };
	

	public static Irpf2020UpdateII IRPF2020UPDATEII = new Irpf2020UpdateII();
	
	private Irpf2020UpdateII() {
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
		calendar.set(Calendar.YEAR, 2020);
		
		Date startDate = new Date(calendar.getTimeInMillis());

		boolean upgraded = false;
		for ( String geozoneCode : GEOZONES ) { 
			Double amounts [] = dslContext
				.select(GEOZONE_IRPF.AMOUNT)
				.from(GEOZONE_IRPF)
				.where(GEOZONE_IRPF.GEOZONE_CODE.eq(geozoneCode)
				.and(GEOZONE_IRPF.START_DATE.eq(startDate)))
				.orderBy(GEOZONE_IRPF.AMOUNT.asc())
				.fetchArray(GEOZONE_IRPF.AMOUNT);
			 upgraded &= Arrays.equals(amounts, LEVELS); 
		}
		
		if ( upgraded ) 
			return;
		

		calendar.add(Calendar.DAY_OF_MONTH, -1);
		Date _2019EndDate = new Date(calendar.getTimeInMillis());
		
		UpdateConditionStep<GeozoneIrpfRecord> close2019Update = dslContext
		.update(GEOZONE_IRPF)
		.set(GEOZONE_IRPF.END_DATE, _2019EndDate)
		.where(GEOZONE_IRPF.END_DATE.isNull())
		;

		SelectConditionStep<Record1<Integer>> geozone2020Irpfs = 
			dslContext
			.select(GEOZONE_IRPF.ID)
			.from(GEOZONE_IRPF)
			.where(GEOZONE_IRPF.START_DATE.eq(startDate));
		dslContext
			.delete(GEOZONE_IRPF_DESCENDANT)
			.where(GEOZONE_IRPF_DESCENDANT.GEOZONE_IRPF.in(geozone2020Irpfs))
			.execute();

		dslContext
			.delete(GEOZONE_IRPF_HANDICAP)
			.where(GEOZONE_IRPF_HANDICAP.GEOZONE_IRPF.in(geozone2020Irpfs))
			.execute();
		dslContext
			.delete(GEOZONE_IRPF)
			.where(GEOZONE_IRPF.START_DATE.eq(startDate))
			.execute();
		
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
					.set(GEOZONE_IRPF.START_DATE, startDate)
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

		dslContext.transaction( (config) -> {
			close2019Update.execute();
			geozoneInsertSetMoreStep.execute();
			descendantInsertSetMoreStep.execute();
			handicapInsertSetMoreStep.execute();
		});
		
	}
	
	

}
