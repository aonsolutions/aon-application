package net.aonsolutions.db.up2date.finance;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class FsModelInvoiceCreation implements Update {
	
	public static final FsModelInvoiceCreation FS_MODEL_INVOICE_CREATION = new FsModelInvoiceCreation();

	private FsModelInvoiceCreation() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		System.out.println("[START]");
		System.out.println( "Creacion table `fs_model_invoice`" );
		
		String sql =
			"CREATE TABLE IF NOT EXISTS `fs_model_invoice` ("
				+"`id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',"
				+"`domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',"
				+"`fs_model` int(4) NOT NULL COMMENT 'Identificador del Modelo',"
				+"`invoice` int(4) NOT NULL COMMENT 'Identificador de la Factura',"
				+"PRIMARY KEY (`id`),"
				+"KEY `IDX_FS_MODEL_INVOICE_DOMAIN` (`domain`),"
				+"KEY `IDX_FS_MODEL_INVOICE_FS_MODEL` (`fs_model`),"
				+"KEY `IDX_FS_MODEL_INVOICE_INVOICE` (`invoice`),"
				+"CONSTRAINT `FK_FS_MODEL_INVOICE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),"
				+"CONSTRAINT `FK_FS_MODEL_INVOICE_FS_MODEL` FOREIGN KEY (`fs_model`) REFERENCES `fs_model` (`id`),"
				+"CONSTRAINT `FK_FS_MODEL_INVOICE_INVOICE` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`)"
			+"  ) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Facturas del Modelo';"
		;

		dslContext.execute(sql);
		
		System.out.println("[END]");
	}
}
