package net.aonsolutions.db.up2date.finance;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class InvoiceBatchCreation implements Update {

	public static final InvoiceBatchCreation INVOICE_BATCH_CREATION = new InvoiceBatchCreation();

	private InvoiceBatchCreation() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		System.out.println("[START]");
				
		createInvoiceStatus(dslContext);
		createInvoiceBatch(dslContext);
		createInvoiceBatchDetail(dslContext);
		
		System.out.println("[END]");
	}
	
	private void createInvoiceStatus(DSLContext dslContext) {
		System.out.println( "Creacion table `invoice_status`" );

		String sql =
			"CREATE TABLE IF NOT EXISTS `invoice_status` ("
				+"`id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',"
				+"`domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',"
				+"`invoice` int(4) NOT NULL COMMENT 'Identificador de la Factura',"
				+"`type` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Tipo de Comunicacion',"
				+"`status` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Estado de la Comunicacion',"
				+"PRIMARY KEY (`id`),"
				+"KEY `IDX_INVOICE_STATUS_DOMAIN` (`domain`),"
				+"KEY `IDX_INVOICE_STATUS_INVOICE` (`invoice`),"
				+"CONSTRAINT `FK_INVOICE_STATUS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),"
				+"CONSTRAINT `FK_INVOICE_STATUS_INVOICE` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`)"
		+"  ) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Estado Comunicaciones de Facturas';"
		;

		dslContext.execute(sql);
	}
	
	private void createInvoiceBatch(DSLContext dslContext) {
		System.out.println( "Creacion table `invoice_batch`" );

		String sql =
			"CREATE TABLE IF NOT EXISTS `invoice_batch` ("
				+"`id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',"
				+"`domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',"
				+"`date` datetime NOT NULL COMMENT 'Fecha de comunicacion',"
				+"`type` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Tipo de Comunicacion',"
				+"`operation` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Tipo de Operación',"
				+"`data_response` int(4) NOT NULL COMMENT 'Envio de la comunicacion',"
				+"`creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',"
				+"PRIMARY KEY (`id`),"
				+"KEY `IDX_INVOICE_BATCH_DOMAIN` (`domain`),"
				+"KEY `IDX_INVOICE_BATCH_DATA_RESPONSE` (`data_response`),"
				+"CONSTRAINT `FK_INVOICE_BATCH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),"
				+"CONSTRAINT `FK_INVOICE_BATCH_DATA_RESPONSE` FOREIGN KEY (`data_response`) REFERENCES `data_response` (`id`)"
		+"  ) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Comunicacion Lote Facturas';"
		;
		dslContext.execute(sql);
	}
	
	
	private void createInvoiceBatchDetail(DSLContext dslContext) {
		System.out.println( "Creacion table `invoice_batch_detail`" );

		String sql =
			"CREATE TABLE IF NOT EXISTS `invoice_batch_detail` ("
				+"`id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',"
				+"`domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',"
				+"`invoice` int(4) NOT NULL COMMENT 'Identificador de la factura',"
				+"`invoice_batch` int(4) NOT NULL COMMENT 'Identificador de invoice batch',"
				+"`status` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Estado de la comunicacion',"
				+"PRIMARY KEY (`id`),"
				+"KEY `IDX_INVOICE_BATCH_DETAIL_DOMAIN` (`domain`),"
				+"KEY `IDX_INVOICE_BATCH_DETAIL_INVOICE` (`invoice`),"
				+"KEY `IDX_INVOICE_BATCH_DETAIL_INVOICE_BATCH` (`invoice_batch`),"
				+"CONSTRAINT `FK_INVOICE_BATCH_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),"
				+"CONSTRAINT `FK_INVOICE_BATCH_DETAIL_INVOICE` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`),"
				+"CONSTRAINT `FK_INVOICE_BATCH_DETAIL_INVOICE_BATCH` FOREIGN KEY (`invoice_batch`) REFERENCES `invoice_batch` (`id`)"
		+"  ) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle Comunicacion Lote Facturas';"
		;
		dslContext.execute(sql);
	}
	
}
