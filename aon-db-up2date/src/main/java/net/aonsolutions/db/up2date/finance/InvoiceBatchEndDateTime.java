package net.aonsolutions.db.up2date.finance;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class InvoiceBatchEndDateTime implements Update {

	public static final InvoiceBatchEndDateTime INVOICEBATCH_ENDDATETIME = new InvoiceBatchEndDateTime();

	private InvoiceBatchEndDateTime() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		try {
			String modifyEndDate = "ALTER TABLE `invoice_batch` MODIFY COLUMN `end_date` datetime DEFAULT NULL COMMENT 'Fecha de finalizacion' ";

			dslContext.execute(modifyEndDate);
		} catch (Exception e) {
			System.err.println("Error modifying end_date column in invoice_batch table: " + e.getMessage());
		}
		
	}
}
