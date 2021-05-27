package net.aonsolutions.db.up2date.timecontrol;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class CoordinatesUpdate implements Update {

	
	public static CoordinatesUpdate COORDINATES_UPDATE = new CoordinatesUpdate();

	private CoordinatesUpdate() {
		super();
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		
		System.out.println("[START]");
		System.out.println( "ALTER TABLE `location`" );
			
		String dropCoordinates = "ALTER TABLE `location` DROP `coordinates`;";
		String addLatitude = "ALTER TABLE `location` ADD `latitude` double DEFAULT NULL COMMENT 'Latitud de las coordenadas.';";
		String addLongitude = "ALTER TABLE `location` ADD `longitude` double DEFAULT NULL COMMENT 'Longitud de las coordenadas.';";
		
		dslContext.execute(dropCoordinates);
		dslContext.execute(addLatitude);
		dslContext.execute(addLongitude);

		System.out.println( "ALTER TABLE `timecontrol`" );
		
		String dropCoordinates2 = "ALTER TABLE `timecontrol` DROP `coordinates`;";
		String addLatitude2 = "ALTER TABLE `timecontrol` ADD `latitude` double DEFAULT NULL COMMENT 'Latitud de las coordenadas.';";
		String addLongitude2 = "ALTER TABLE `timecontrol` ADD `longitude` double DEFAULT NULL COMMENT 'Longitud de las coordenadas.';";
	
		dslContext.execute(dropCoordinates2);
		dslContext.execute(addLatitude2);
		dslContext.execute(addLongitude2);

		System.out.println("[END]");
	}

}
