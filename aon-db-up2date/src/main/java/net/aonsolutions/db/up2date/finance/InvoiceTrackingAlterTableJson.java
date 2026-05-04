package net.aonsolutions.db.up2date.finance;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class InvoiceTrackingAlterTableJson implements Update {

	public static final InvoiceTrackingAlterTableJson INVOICE_TRACKING_ALTER_TABLE_JSON = new InvoiceTrackingAlterTableJson();

	private InvoiceTrackingAlterTableJson() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		System.out.println("[START]");
		try {
			dslContext.execute("ALTER TABLE `invoice_tracking` MODIFY `json` MEDIUMTEXT;");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("[END]");
	}
}
