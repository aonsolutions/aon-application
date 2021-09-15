package net.aonsolutions.db.up2date.irpf;

import static com.esferalia.aon.jooq.tables.GeozoneIrpf.GEOZONE_IRPF;

import java.sql.Connection;
import java.time.LocalDate;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class Irpf2020Update implements Update {
	
	public static final Irpf2020Update IRPF2020UPDATE = new Irpf2020Update();
	
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
		

		dslContext.transaction(config -> {
			
			Result<Record2<String, LocalDate>> result = 
			dslContext.select(
			GEOZONE_IRPF.GEOZONE_CODE,
			DSL.max(GEOZONE_IRPF.END_DATE)
			)
			.from(GEOZONE_IRPF)
			.groupBy(GEOZONE_IRPF.GEOZONE_CODE)
			.fetch()
			;
			
			for ( Record2<String, LocalDate> record2 : result ) {
				dslContext
				.update(GEOZONE_IRPF)
				.set(GEOZONE_IRPF.END_DATE, DSL.castNull(LocalDate.class))
				.where(GEOZONE_IRPF.GEOZONE_CODE.eq(record2.value1()))
				.and(GEOZONE_IRPF.END_DATE.eq(record2.value2()))
				.execute()
				;
			}
			
		});
		
	}
	
	

}
