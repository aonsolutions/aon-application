package net.aonsolutions.db.up2date.timecontrol;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import com.esferalia.aon.jooq.tables.Timecontrol;

import net.aonsolutions.db.up2date.Update;

public class TimeControlUpdate implements Update {


	public static TimeControlUpdate TIMECONTROL_UPDATE= new TimeControlUpdate();

	private TimeControlUpdate() {
		super();
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		System.out.println("[START]");
		System.out.println( "Update table `timecontrol`" );

		try {
			String addCause ="ALTER TABLE `timecontrol` ADD `cause` tinyint(2) DEFAULT 0 COMMENT 'causa de fichaje';";
			
			String addCreationUser = "ALTER TABLE `timecontrol` ADD `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion';";
			String addCreationDate = "ALTER TABLE `timecontrol` ADD `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion';";
			
			String addModificationUser = "ALTER TABLE `timecontrol` ADD `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion';";
			String addModificationDate = "ALTER TABLE `timecontrol` ADD `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion';";
			
			String addModificatedTimeControl = "ALTER TABLE `timecontrol` ADD `modificated_timecontrol` int(4) DEFAULT NULL COMMENT 'id timecontrol modificado';";
		
			dslContext.execute(addCause);
			
			dslContext.execute(addCreationUser);
			dslContext.execute(addCreationDate);
			
			dslContext.execute(addModificationUser);
			dslContext.execute(addModificationDate);

			dslContext.execute(addModificatedTimeControl);

			System.out.println("[table 'timecontrol' Update!]");
		} catch (Throwable t) {
			System.out.println("[table 'timecontrol' NOT Update!] " + t.getMessage());
		}
		System.out.println("[END]");
	}

}
