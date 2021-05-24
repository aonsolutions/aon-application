package net.aonsolutions.db.up2date.finance;

import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class UpdateInvoiceSource implements Update {

	public static final UpdateInvoiceSource UPDATE_INVOICE_SOURCE = new UpdateInvoiceSource();

	private UpdateInvoiceSource() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		dslContext.update(INVOICE_DETAIL).set(INVOICE_DETAIL.SOURCE, (byte) 7).where(INVOICE_DETAIL.SOURCE.eq((byte) 10)).execute();

	}

}
