package net.aonsolutions.db.up2date.warehouse;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class SalesInfoCreation implements Update {

	public static final SalesInfoCreation SALES_INFO_CREATION = new SalesInfoCreation();

	private SalesInfoCreation() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		System.out.println("[START]");

		createSalesInfo(dslContext);
		
		System.out.println("[END]");
	}
	
	private void createSalesInfo(DSLContext dslContext) {
		System.out.println( "Creacion table `sales_info`" );
		String sql =
			"CREATE TABLE IF NOT EXISTS `sales_info` ("
				+"`id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',"
				+"`domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',"
				+"`sales` int(4) NOT NULL COMMENT 'Identificador del Pedido',"
				+"`type` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Tipo de Comunicacion',"
				+"`status` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Estado de la Comunicacion',"
				+"PRIMARY KEY (`id`),"
				+"KEY `IDX_SALES_INFO_DOMAIN` (`domain`),"
				+"KEY `IDX_SALES_INFO_SALES` (`sales`),"
				+"CONSTRAINT `FK_SALES_INFO_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),"
				+"CONSTRAINT `FK_SALES_INFO_SALES` FOREIGN KEY (`sales`) REFERENCES `sales` (`id`)"
		+"  ) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Estado Comunicaciones de Pedidos';"
		;

		dslContext.execute(sql);
	}
}
