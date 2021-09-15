package net.aonsolutions.db.up2date.irpf;

import static com.esferalia.aon.jooq.tables.GeozoneIrpf.GEOZONE_IRPF;
import static com.esferalia.aon.jooq.tables.GeozoneIrpfDescendant.GEOZONE_IRPF_DESCENDANT;
import static com.esferalia.aon.jooq.tables.GeozoneIrpfHandicap.GEOZONE_IRPF_HANDICAP;

import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;
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

public class IrpfNavarra2021Update implements Update {
	
	private static final Double[] LEVELS = {
			0.00,
			11251.00,
			12751.00,
			14251.00,
			16751.00,
			19751.00,
			23251.00,
			25751.00,
			28251.00,
			32251.00,
			35751.00,
			41251.00,
			48001.00,
			55001.00,
			62001.00,
			69251.00,
			75251.00,
			82251.00,
			94751.00,
		    107251.00,
			120001.00,
			132751.00,
			146001.00,
			200001.00,
			280001.00,
			350001.00,
	};

	private static final double[][] DESCENDANTS_IRPFS = {
			{0,4,6,8,10,12,13.5,14.6,15.8,17,18.1,20,22.1,24.1,26.1,28.3,29.6,30.8,32.2,33.5,35.1,36.2,38,40,42,43},
			{0,2,4,6,8,11,12.2,13.3,14.5,16,17.1,19,21.5,23.5,25.5,27.7,29.2,30.5,31.7,33,34.8,36,37.5,39.8,41.5,42.9},
			{0,0,2,4,6,9,11.5,12.6,13.7,15,16.5,18.3,20.9,23,24.5,27,28.3,30,31.2,32.6,34.6,35.8,37,39.4,41.2,42.8},
			{0,0,0,2,4,7,8.7,9.9,12.4,13.6,14.7,16.9,18.9,22,24,26.6,27.3,29.4,30.8,32.3,34,35,36.5,39,40.8,42.5},
			{0,0,0,0,1,6,7.7,9.2,10.7,12.5,14,16.2,18.3,21.5,23.5,25.5,27.2,29,30.4,32,33.5,34.5,35.5,38.5,40,41.5},
			{0,0,0,0,0,4,5.7,7.8,8.9,10.8,13.3,15.5,17.7,20.9,22.9,25,26.2,28.3,30,31.5,32.9,33.9,34.9,38,39.5,41},
			{0,0,0,0,0,0,3.6,5.8,7.9,10,12.2,13.7,16.6,19.8,21.9,24.4,26.1,27.7,29.5,31,32.4,33.4,34.4,37.5,39,40.5},
			{0,0,0,0,0,0,0,3.8,5.9,8,10.2,12.4,15.6,18.7,21.4,22.9,24.5,26.6,28.9,30,31.3,32.8,34,35.5,37,38.5},
			{0,0,0,0,0,0,0,0.8,3.9,7,9.1,11.3,14.5,17.7,19.8,21.8,23.4,25.6,27.8,29.5,30.7,32,33.5,34.5,36,37.5},
			{0,0,0,0,0,0,0,0,0.9,5,7.1,10.2,13.4,16.6,18.7,20.3,22.3,24.5,26.8,28.5,29.7,31,32,33.7,35.2,36.7},
			{0,0,0,0,0,0,0,0,0,2,6.1,9.2,12.3,15,17.2,19.3,21.3,23.9,25.7,28,29.1,30,31.5,33,34.5,36},
	};
	
	private static final double[][] HANDICAP_IRPFS = {
			{ 5,  5,  5,  5,  5,   5, 3,  3,  3,  3,  3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
			{15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8},
			{15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8},
	};

	private static final String NAVARRA = "31";
	
	private static final String[] GEOZONES = { NAVARRA };
	

	public static final IrpfNavarra2021Update IRPFNAVARRA2021UPDATE = new IrpfNavarra2021Update();
	
	private IrpfNavarra2021Update() {
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
		calendar.set(Calendar.YEAR, 2021);
		
		LocalDate startDate = new Date(calendar.getTimeInMillis()).toLocalDate();

		boolean upgraded = false;
		for ( String geozoneCode : GEOZONES ) { 
			Double[] amounts = dslContext
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
		LocalDate endDate2020 = new Date(calendar.getTimeInMillis()).toLocalDate();
		
		UpdateConditionStep<GeozoneIrpfRecord> close2019Update = dslContext
		.update(GEOZONE_IRPF)
		.set(GEOZONE_IRPF.END_DATE, endDate2020)
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

		dslContext.transaction(config -> {
			close2019Update.execute();
			geozoneInsertSetMoreStep.execute();
			descendantInsertSetMoreStep.execute();
			handicapInsertSetMoreStep.execute();
		});
		
	}
	
	

}
