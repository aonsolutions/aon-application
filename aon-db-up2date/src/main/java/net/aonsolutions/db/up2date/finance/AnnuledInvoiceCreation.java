package net.aonsolutions.db.up2date.finance;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class AnnuledInvoiceCreation implements Update {

//	#
//	# Table structure for table `invoice`
//	#
//
//	CREATE TABLE `invoice_tracking` (
//	  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Factura',
//	  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
//	  `series` char(5) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Serie de la Factura',
//	  `number` int NOT NULL DEFAULT '0' COMMENT 'Numero de la Factura',
//	  `reference_code` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de referencia de la Factura',
//	  `issue_date` date DEFAULT NULL COMMENT 'Fecha de emision de la Factura',
//	  `rdocument` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Documento del Cliente o Proveedor',
//	  `rname` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre completo del Cliente o Proveedor',
//	  `status` tinyint DEFAULT '0' COMMENT 'Estado del Seguimiento de Factura',
//	  `type` tinyint DEFAULT '0' COMMENT 'Tipo de Factura (Compra o Venta)',
//	  `total` decimal(15,4) DEFAULT '0' COMMENT 'Total Factura',
//	  `json` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Factura en formato JSON',
//	  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
//	  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
//	  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
//	  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
//	  PRIMARY KEY (`id`),
//	  UNIQUE KEY `IDX_UNQ_INVOICE_TRACKING_DOMAIN_SERIES_NUMBER_TYPE` (`domain`,`series`,`number`,`type`),
//	  KEY `IDX_INVOICE_TRACKING_ISSUE_DATE` (`issue_date`),
//	  KEY `IDX_INVOICE_TRACKING_DOMAIN` (`domain`),
//	  KEY `IDX_INVOICE_TRACKING_REFERENCE_CODE` (`reference_code`),
//	  CONSTRAINT `FK_INVOICE_TRACKING_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
//	) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Seguimiento de Facturas';
	
	public static final AnnuledInvoiceCreation ANNULED_INVOICE_CREATION = new AnnuledInvoiceCreation();

	private AnnuledInvoiceCreation() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		System.out.println("[START]");
		System.out.println( "Creation table `invoice_tracking`" );

		String sql = "CREATE TABLE IF NOT EXISTS `invoice_tracking` ("
				+ "`id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la Factura',"
				+ "`domain` int NOT NULL COMMENT 'Identificador del Dominio',"
				+ "`series` char(5) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Serie de la Factura',"
				+ "`number` int NOT NULL DEFAULT '0' COMMENT 'Numero de la Factura',"
				+ "`reference_code` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de referencia de la Factura',"
				+ "`issue_date` date DEFAULT NULL COMMENT 'Fecha de emision de la Factura',"
				+ "`rdocument` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Documento del Cliente o Proveedor',"
				+ "`rname` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre completo del Cliente o Proveedor',"
				+ "`status` tinyint DEFAULT '0' COMMENT 'Estado del Seguimiento de Factura',"
				+ "`type` tinyint DEFAULT '0' COMMENT 'Tipo de Factura (Compra o Venta)',"
				+ "`total` decimal(15,4) DEFAULT '0' COMMENT 'Total Factura',"
				+ "`json` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Factura en formato JSON',"
				+ "`creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',"
				+ "`creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',"
				+ "`modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',"
				+ "`modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',"
				+ "PRIMARY KEY (`id`),"
				+ "UNIQUE KEY `IDX_UNQ_INVOICE_TRACKING_DOMAIN_SERIES_NUMBER_TYPE` (`domain`,`series`,`number`,`type`),"
				+ "KEY `IDX_INVOICE_TRACKING_ISSUE_DATE` (`issue_date`),"
				+ "KEY `IDX_INVOICE_TRACKING_DOMAIN` (`domain`),"
				+ "KEY `IDX_INVOICE_TRACKING_REFERENCE_CODE` (`reference_code`),"
				+ "CONSTRAINT `FK_INVOICE_TRACKING_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)"
				+ ") ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Seguimiento de Facturas';";

		dslContext.execute(sql);
		
		System.out.println("[END]");
	}

}
