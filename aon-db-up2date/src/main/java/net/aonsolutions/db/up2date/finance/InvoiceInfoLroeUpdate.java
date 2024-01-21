package net.aonsolutions.db.up2date.finance;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.InvoiceInfo;

import net.aonsolutions.db.up2date.Update;

public class InvoiceInfoLroeUpdate implements Update {

	public static final InvoiceInfoLroeUpdate INVOICE_INFO_LROE_UPDATE = new InvoiceInfoLroeUpdate();

	private InvoiceInfoLroeUpdate() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
	
		dslContext.update(InvoiceInfo.INVOICE_INFO).set(InvoiceInfo.INVOICE_INFO.TYPE, (byte) 2).execute();
	}	
}
