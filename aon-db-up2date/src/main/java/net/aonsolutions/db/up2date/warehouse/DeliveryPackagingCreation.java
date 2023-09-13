package net.aonsolutions.db.up2date.warehouse;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class DeliveryPackagingCreation implements Update {

//	#
//	# Structure for the `delivery_packaging` table :
//	#
//
//	CREATE TABLE `delivery_packaging` (
//		`id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'ID unico del vinculo',
//		`domain` int(4) NOT NULL COMMENT 'Dominio',
//		`delivery` int(4) NOT NULL COMMENT 'Identificador del albarán',
//		`item` int(4) NOT NULL COMMENT 'Articulo del Envasado',
//	    `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
//	    `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
//	    `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
// 	    `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
//		PRIMARY KEY (`id`),
//		KEY `IDX_DELIVERY_PACKAGING_DOMAIN` (`domain`),
//		KEY `IDX_DELIVERY_PACKAGING_DELIVERY` (`delivery`),
// 		KEY `IDX_DELIVERY_PACKAGING_ITEM` (`item`),
//		CONSTRAINT `FK_DELIVERY_PACKAGING_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
//		CONSTRAINT `FK_DELIVERY_PACKAGING_DELIVERY` FOREIGN KEY (`delivery`) REFERENCES `delivery` (`id`),
// 		CONSTRAINT `FK_DELIVERY_PACKAGING_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
//	) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Envasado del albaran';


	public static final DeliveryPackagingCreation DELIVERY_PACKAGING_CREATION = new DeliveryPackagingCreation();

	private DeliveryPackagingCreation() {
		super();
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		System.out.println("[START]");
		System.out.println( "Creacion table `delivery_packaging`" );

		String SQL = "CREATE TABLE IF NOT EXISTS `delivery_packaging` ("
				+ "`id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'ID unico del vinculo',"
				+ "`domain` int(4) NOT NULL COMMENT 'Dominio',"
				+ "`delivery` int(4) NOT NULL COMMENT 'Identificador del albaran',"
				+ "`item` int(4) NOT NULL COMMENT 'Articulo del Envasado',"
				+ "`creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',"
				+ "`creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',"
				+ "`modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',"
				+ "`modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',"
				+ "PRIMARY KEY (`id`),"
				+ "KEY `IDX_DELIVERY_PACKAGING_DOMAIN` (`domain`),"
				+ "KEY `IDX_DELIVERY_PACKAGING_DELIVERY` (`delivery`),"
				+ "KEY `IDX_DELIVERY_PACKAGING_ITEM` (`item`),"
				+ "CONSTRAINT `FK_DELIVERY_PACKAGING_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),"
				+ "CONSTRAINT `FK_DELIVERY_PACKAGING_DELIVERY` FOREIGN KEY (`delivery`) REFERENCES `delivery` (`id`),"
				+ "CONSTRAINT `FK_DELIVERY_PACKAGING_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`)"
				+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Envasado del albaran';";
		try {
			dslContext.execute(SQL);
			System.out.println("[table 'timecontrol' CREATED!]");
		} catch (Throwable t) {
			System.out.println("[table 'timecontrol' NOT CREATED!]");
		}
		System.out.println("[END]");
	}

}
