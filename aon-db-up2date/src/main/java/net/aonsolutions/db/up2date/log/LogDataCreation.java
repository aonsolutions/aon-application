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
		String sql = "CREATE_TABLE IF NOT EXISTS `log_data `("
				+"`id` int (5) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',"				
				+"`date` date NOT NULL COMMENT 'Fecha del error',"
				+"`stacktrace` text NOT NULL COMMENT 'Traza del error',"
				+"PRIMARY KEY (`id`),"
				+"KEY `IDX_LOG_DATA_DATE` (`date`),"
				+"  ) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Historico de errores.';"
				;
		dslContext.execute(sql);
	}
}
