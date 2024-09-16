package net.aonsolutions.db.up2date.finance;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class InvoiceDataCreation implements Update {

	public static final InvoiceDataCreation INVOICE_DATA_CREATION = new InvoiceDataCreation();

	private InvoiceDataCreation() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		System.out.println("[START]");

		createInvoiceData(dslContext);
		
		System.out.println("[END]");
	}
	
	private void createInvoiceData(DSLContext dslContext) {
		System.out.println( "Creation table `invoice_data`" );
		String sql =
			"CREATE TABLE IF NOT EXISTS `invoice_data` ("
				+"`id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',"
				+"`domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',"
				+"`invoice` int(4) NOT NULL COMMENT 'Identificador de la Factura',"
				+"`name` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre',"
				+"`value` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Valor',"
				+"`start_date` date NOT NULL COMMENT 'Fecha de inicio',"
				+"`end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion',"
				+"PRIMARY KEY (`id`),"
				+"KEY `IDX_INVOICE_DATA_DOMAIN` (`domain`),"
				+"KEY `IDX_INVOICE_DATA_INVOICE` (`invoice`),"
				+"CONSTRAINT `FK_INVOICE_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),"
				+"CONSTRAINT `FK_INVOICE_DATA_INVOICE` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`)"
		+"  ) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos adicionales de la factura.';"
		;

		dslContext.execute(sql);
	}
	
}
