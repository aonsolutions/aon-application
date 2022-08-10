package net.aonsolutions.db.up2date.warehouse;

import java.sql.Connection;
import java.util.logging.Logger;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class ElaborationDetailUpdate implements Update {

	private static final Logger LOGGER  = Logger.getLogger(ElaborationDetailUpdate.class.getName());
	public static final ElaborationDetailUpdate ELABORATION_DETAIL_UPDATE= new ElaborationDetailUpdate();

	private ElaborationDetailUpdate() {
		super();
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		LOGGER.info("[START]");
		LOGGER.info("Update table `elaboration_detail`");

		try {
			String addType ="ALTER TABLE `elaboration_detail` ADD `type` tinyint(2) DEFAULT 0 COMMENT 'Tipo de detalle' AFTER `elaboration`;";
			dslContext.execute(addType);
			LOGGER.info("[table 'timecontrol' Update!]");
		} catch (Exception t) {
			LOGGER.info("[table 'timecontrol' NOT Update!] " + t.getMessage());
		}
		LOGGER.info("[END]");
	}

}
