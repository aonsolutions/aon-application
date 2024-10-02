package net.aonsolutions.db.up2date.log;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class LogDataCreation implements Update{

	public static final LogDataCreation LOG_DATA_CREATION = new LogDataCreation();
	
	private LogDataCreation() {
		super();
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		System.out.println("[START]");

		createLogData(dslContext);
		
		System.out.println("[END]");
	}
	
	private void createLogData(DSLContext dslContext) {
		String sql = "CREATE TABLE IF NOT EXISTS `log_data`("
				+"`id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',"
				+"`domain` int NOT NULL COMMENT 'Identificador dominio',"
				+"`date` date NOT NULL COMMENT 'Fecha del error',"
				+"`message` text NOT NULL COMMENT 'Mensaje',"
				+"PRIMARY KEY (`id`),"
				+"KEY `IDX_LOG_DATA_DOMAIN` (`domain`),"
				+"KEY `IDX_LOG_DATA_DATE` (`date`),"
				+"CONSTRAINT `FK_LOG_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)"
				+"  ) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Historico de errores.';"
				;
		dslContext.execute(sql);
	}
}
