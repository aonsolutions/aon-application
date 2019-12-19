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
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.SelectHavingStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.GeozoneIrpfDescendantRecord;
import com.esferalia.aon.jooq.tables.records.GeozoneIrpfHandicapRecord;
import com.esferalia.aon.jooq.tables.records.GeozoneIrpfRecord;

import net.aonsolutions.db.up2date.Update;

public class Irpf2020Update implements Update {
	
	private static final String ARABA 		= "01";
	private static final String BIZKAIA 	= "48";
	private static final String GIPUZKOA 	= "20";
	
	private static final String GEOZONES [] = { ARABA, BIZKAIA, GIPUZKOA };
	

	public static Irpf2020Update IRPF2020UPDATE = new Irpf2020Update();
	
	private Irpf2020Update() {
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
		
		
		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(GEOZONE_IRPF)
		.where(GEOZONE_IRPF.END_DATE.isNull())) > 0;
		
		if ( upgraded ) 
			return;
		

		dslContext.transaction( (config) -> {
			
			Result<Record2<String, Date>> result = 
			dslContext.select(
			GEOZONE_IRPF.GEOZONE_CODE,
			DSL.max(GEOZONE_IRPF.END_DATE)
			)
			.from(GEOZONE_IRPF)
			.groupBy(GEOZONE_IRPF.GEOZONE_CODE)
			.fetch()
			;
			
			for ( Record2<String, Date> record2 : result ) {
				dslContext
				.update(GEOZONE_IRPF)
				.set(GEOZONE_IRPF.END_DATE, DSL.castNull(Date.class))
				.where(GEOZONE_IRPF.GEOZONE_CODE.eq(record2.value1()))
				.and(GEOZONE_IRPF.END_DATE.eq(record2.value2()))
				.execute()
				;
			}
			
		});
		
	}
	
	

}
