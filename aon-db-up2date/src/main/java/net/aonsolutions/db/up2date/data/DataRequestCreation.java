package net.aonsolutions.db.up2date.data;

import java.sql.Connection;
import java.util.logging.Logger;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class DataRequestCreation implements Update {

	private static final Logger LOGGER  = Logger.getLogger(DataRequestCreation.class.getName());
	public static final DataRequestCreation DATA_REQUEST_CREATION = new DataRequestCreation();

	private DataRequestCreation() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		LOGGER.info("[START]");
		LOGGER.info("Create table `data_request`");

		String sql =
		"CREATE TABLE IF NOT EXISTS `data_request` ("
			  + "`id` int(4) NOT NULL COMMENT 'ID unico del vinculo',"
			  + "`domain` int(4) NOT NULL DEFAULT 0 COMMENT 'Dominio',"
			  + "`date` datetime DEFAULT NULL COMMENT 'Fecha'," 
			  + "`type` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Tipo',"
			  + "`black_box` text COLLATE latin1_spanish_ci COMMENT 'Información necesaria para replicar la petición',"
			  + "`md5` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Hash md5',"
		  + "PRIMARY KEY (`id`),"
		  + "KEY `IDX_DATA_REQUEST_DOMAIN` (`domain`)," 
		  + "CONSTRAINT `FK_DATA_REQUEST_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)"
		+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Data Request';";

		dslContext.execute(sql);

		LOGGER.info("Alter table `data_response`");

		String sql2 = "ALTER TABLE `data_response` ADD COLUMN `data_request` int(4) DEFAULT NULL Comment 'identificador de data_request' AFTER `source_id`";
		String sql3 = "ALTER TABLE `data_response` ADD KEY `IDX_DATA_RESPONSE_DATA_REQUEST` (`data_request`)";
		String sql4 = "ALTER TABLE `data_response` ADD CONSTRAINT `FK_DATA_RESPONSE_DATA_REQUEST` FOREIGN KEY (`data_request`) REFERENCES `data_request` (`id`)";

		dslContext.execute(sql2);
		dslContext.execute(sql3);
		dslContext.execute(sql4);
	
		LOGGER.info("[END]");
	}

}
