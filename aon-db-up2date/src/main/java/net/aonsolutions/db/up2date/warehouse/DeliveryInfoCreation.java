package net.aonsolutions.db.up2date.warehouse;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class DeliveryInfoCreation implements Update {

	public static final DeliveryInfoCreation DELIVERY_INFO_CREATION = new DeliveryInfoCreation();

	private DeliveryInfoCreation() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		System.out.println("[START]");

		createDeliveryInfo(dslContext);
		
		System.out.println("[END]");
	}
	
	private void createDeliveryInfo(DSLContext dslContext) {
		System.out.println( "Creacion table `delivery_info`" );
		String sql =
			"CREATE TABLE IF NOT EXISTS `delivery_info` ("
				+"`id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',"
				+"`domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',"
				+"`delivery` int(4) NOT NULL COMMENT 'Identificador del Albaran de Venta',"
				+"`type` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Tipo de Comunicacion',"
				+"`status` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Estado de la Comunicacion',"
				+"PRIMARY KEY (`id`),"
				+"KEY `IDX_DELIVERY_INFO_DOMAIN` (`domain`),"
				+"KEY `IDX_DELIVERY_INFO_DELIVERY` (`delivery`),"
				+"CONSTRAINT `FK_DELIVERY_INFO_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),"
				+"CONSTRAINT `FK_DELIVERY_INFO_DELIVERY` FOREIGN KEY (`delivery`) REFERENCES `delivery` (`id`)"
		+"  ) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Estado Comunicaciones de Albaranes de Venta';"
		;

		dslContext.execute(sql);
	}
}
