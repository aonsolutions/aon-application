package net.aonsolutions.db.up2date.management;

import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class AlterInvoiceAddIndexDomainStatus implements Update {

	public static final AlterInvoiceAddIndexDomainStatus ALTER_INVOICE_ADD_INDEX_DOMAIN_STATUS = new AlterInvoiceAddIndexDomainStatus();

	private AlterInvoiceAddIndexDomainStatus() {
	}

	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		try {
			//CREATE INDEX `IDX_INVOICE_DOMAIN_STATUS` ON `invoice` ( `domain`, `status` );
			dslContext.createIndex("IDX_INVOICE_DOMAIN_STATUS").on(INVOICE, INVOICE.DOMAIN, INVOICE.STATUS).execute();
		} catch (DataAccessException e) {
		}

	}

}
