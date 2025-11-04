package net.aonsolutions.db.up2date.product;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class ProductBookingCreation implements Update {

//	#
//	# Structure for the product_booking :
//	#
//
//	CREATE TABLE `product_booking` (
//			  `product` int(11) NOT NULL COMMENT 'Identificador unico del producto',
//			  `domain` int(11) NOT NULL COMMENT 'Identificador del Dominio',
//			  `type` tinyint(2) DEFAULT '0' COMMENT 'Tipo de producto (pack, servicio, ...)',
//	  		  `pos` int(11) DEFAULT '0' COMMENT 'Orden para mostrar lista',
//			  `json` text DEFAULT NULL COLLATE latin1_spanish_ci COMMENT 'Informacion relativa al producto en formato JSON',
//	  		  `workgroup` int(11) DEFAULT NULL COMMENT 'Grupo trabajo asociado al producto',
//	  		  `task_holder` int(11) DEFAULT NULL COMMENT 'Operario asociado al producto',
//			  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
//			  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
//			  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
//			  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
//			  PRIMARY KEY (`product`),
//			  KEY `IDX_PRODCUT_BOOKING_DOMAIN` (`domain`),
//	  		  KEY `IDX_PRODCUT_BOOKING_WORKGROUP` (`workgroup`),
//	  		  KEY `IDX_PRODCUT_BOOKING_TASK_HOLDER` (`task_holder`),
//	  		  CONSTRAINT `FK_PRODCUT_BOOKING_PRODUCT` FOREIGN KEY (`product`) REFERENCES `product` (`id`),
//			  CONSTRAINT `FK_PRODCUT_BOOKING_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
//	  		  CONSTRAINT `FK_PRODCUT_BOOKING_WORKGROUP` FOREIGN KEY (`workgroup`) REFERENCES `workgroup` (`id`),
//	  		  CONSTRAINT `FK_PRODCUT_BOOKING_TASK_HOLDER` FOREIGN KEY (`task_holder`) REFERENCES `task_holder` (`registry`),
//			) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Productos contratacion';


	public static ProductBookingCreation PRODUCT_BOOKING_CREATION = new ProductBookingCreation();

	private ProductBookingCreation() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		System.out.println("[START]");
		System.out.println( "Creacion table 'product_booking'" );

		String SQL = "CREATE TABLE `product_booking` ("
				+ "			  `product` int(11) NOT NULL DEFAULT '0' COMMENT 'Identificador unico del producto',"
				+ "			  `domain` int(11) NOT NULL COMMENT 'Identificador del dominio',"
				+ "			  `type` tinyint(2) DEFAULT '0' COMMENT 'Tipo de producto (pack, servicio, ...)',"
				+ "			  `pos` int(11) DEFAULT '0' COMMENT 'Orden para mostrar lista',"	
				+ "			  `json` text DEFAULT NULL COLLATE latin1_spanish_ci COMMENT 'Informacion relativa al producto en formato JSON',"
				+ "	  		  `workgroup` int(11) DEFAULT NULL COMMENT 'Grupo trabajo asociado al producto',"
				+ "	  		  `task_holder` int(11) DEFAULT NULL COMMENT 'Operario asociado al producto',"
				+ "			  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',"
				+ "			  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',"
				+ "			  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',"
				+ "			  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',"
				+ "			  PRIMARY KEY (`product`),"
				+ "			  KEY `IDX_PRODCUT_BOOKING_DOMAIN` (`domain`),"
				+ "	  		  KEY `IDX_PRODCUT_BOOKING_WORKGROUP` (`workgroup`),"
				+ "	  		  KEY `IDX_PRODCUT_BOOKING_TASK_HOLDER` (`task_holder`),"
				+ "	  		  CONSTRAINT `FK_PRODCUT_BOOKING_PRODUCT` FOREIGN KEY (`product`) REFERENCES `product` (`id`),"
				+ "			  CONSTRAINT `FK_PRODCUT_BOOKING_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),"
				+ "	  		  CONSTRAINT `FK_PRODCUT_BOOKING_WORKGROUP` FOREIGN KEY (`workgroup`) REFERENCES `workgroup` (`id`),"
				+ "	  		  CONSTRAINT `FK_PRODCUT_BOOKING_TASK_HOLDER` FOREIGN KEY (`task_holder`) REFERENCES `task_holder` (`registry`)"
				+ "			) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Productos contratacion';"
				;
		
		try {
			dslContext.execute(SQL);
			System.out.println("[table 'product_booking' CREATED!]");
		} catch (Throwable t) {
			System.out.println("[table 'product_booking' NOT CREATED!]");
		}
	}

}
