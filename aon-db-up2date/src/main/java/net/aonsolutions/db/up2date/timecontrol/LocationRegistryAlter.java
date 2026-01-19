package net.aonsolutions.db.up2date.timecontrol;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class LocationRegistryAlter implements Update {
	
	public static LocationRegistryAlter LOCATION_REGISTRY_ALTER = new LocationRegistryAlter();

	private LocationRegistryAlter() {
		super();
	}
	
	@Override
	public void upgrade(Connection connection) {
	    Settings settings = new Settings();
	    settings.setRenderSchema(false);
	    settings.setParamType(ParamType.INLINED);

	    DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

	    System.out.println("[START]");
	    System.out.println("Alter table `location`");

	    String SQL =
	        "ALTER TABLE `location` " +
	        "ADD COLUMN `registry` int(4) DEFAULT NULL COMMENT 'Empresa / Operario', " +
	        "ADD COLUMN `type` TINYINT DEFAULT NULL COMMENT 'Tipo', " +
	        "ADD KEY `IDX_LOCATION_REGISTRY` (`registry`), " +
	        "ADD CONSTRAINT `FK_LOCATION_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`);";

	    try {
	        dslContext.execute(SQL);
	        System.out.println("[table 'location' ALTERED!]");
	    } catch (Throwable t) {
	        System.out.println("[table 'location' NOT ALTERED!]");
	        t.printStackTrace();
	    }

	    System.out.println("[END]");
	}


}
