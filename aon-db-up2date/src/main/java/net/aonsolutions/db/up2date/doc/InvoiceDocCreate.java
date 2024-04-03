package net.aonsolutions.db.up2date.doc;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class InvoiceDocCreate implements Update {


	public static final InvoiceDocCreate INVOICEDOCCREATE = new InvoiceDocCreate();

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		String sql = "CREATE TABLE IF NOT EXISTS `invoice_doc` ("
		+ "  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',"
		+ "  `domain` int NOT NULL COMMENT 'Identificador del Dominio',"
		+ "  `invoice` int NOT NULL COMMENT 'Identificador de la Factura',"
		+ "  `mimeType` tinyint DEFAULT '0' COMMENT 'Mime Type del Archivo Adjunto',"
		+ "  `description` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Archivo Adjunto',"
		+ "  `type` tinyint DEFAULT '0' COMMENT 'Tipo de Archivo Adjunto',"
		+ "  `attach_date` date DEFAULT NULL COMMENT 'Fecha del Archivo Adjunto',"	
		+ "  `s3_key` varchar(1024) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Amazon S3 Object key',"
		+ "  PRIMARY KEY (`id`),"
		+ "  KEY `IDX_INVOICE_DOC_INVOICE` (`invoice`),"
		+ "  KEY `IDX_INVOICE_DOC_DOMAIN` (`domain`),"
		+ "  CONSTRAINT `FK_INVOICE_DOC_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),"
		+ "  CONSTRAINT `FK_INVOICE_DOC_INVOICE` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`)"
		+ ") ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de Facturas';";
		
		dslContext.execute(sql);

	}
}
