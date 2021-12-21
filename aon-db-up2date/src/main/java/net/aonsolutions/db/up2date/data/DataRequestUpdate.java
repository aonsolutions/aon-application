package net.aonsolutions.db.up2date.data;

import java.sql.Connection;
import java.util.logging.Logger;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class DataRequestUpdate implements Update {

	private static final Logger LOGGER  = Logger.getLogger(DataRequestUpdate.class.getName());
	public static final DataRequestUpdate DATA_REQUEST_UPDATE = new DataRequestUpdate();

	private DataRequestUpdate() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		LOGGER.info("[START]");
		LOGGER.info("UPDATE table `data_request`");
		
//		String sql1 = "ALTER TABLE `data_response` DROP CONSTRAINT `FK_DATA_RESPONSE_DATA_REQUEST`";
//		String sql2 = "ALTER TABLE `data_request` MODIFY COLUMN `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'ID unico del vinculo'";
//		String sql3 = "ALTER TABLE `data_response` ADD CONSTRAINT `FK_DATA_RESPONSE_DATA_REQUEST` FOREIGN KEY (`data_request`) REFERENCES `data_request` (`id`)";
		
		String sql4 = "SET FOREIGN_KEY_CHECKS = 0;";
		String sql5 = "ALTER TABLE `data_request` MODIFY COLUMN `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'ID unico del vinculo'";
		String sql6 = "SET FOREIGN_KEY_CHECKS = 1;";
		
		
//		dslContext.execute(sql1);
//		dslContext.execute(sql2);
//		dslContext.execute(sql3);
		
		dslContext.execute(sql4);
		dslContext.execute(sql5);
		dslContext.execute(sql6);
	
		LOGGER.info("[END]");
	}

}
