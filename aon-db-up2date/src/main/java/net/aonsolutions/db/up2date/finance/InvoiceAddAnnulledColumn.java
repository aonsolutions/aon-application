package net.aonsolutions.db.up2date.finance;

import java.sql.Connection;
import java.util.logging.Logger;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class InvoiceAddAnnulledColumn implements Update {

	private static final Logger LOGGER  = Logger.getLogger(InvoiceAddAnnulledColumn.class.getName());
	public static final InvoiceAddAnnulledColumn INVOICE_ADD_ANNULLED_COLUMN = new InvoiceAddAnnulledColumn();

	private InvoiceAddAnnulledColumn() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		LOGGER.info("[START]");
		LOGGER.info("Alter table `invoice`");

		String sql = "ALTER TABLE `invoice` ADD COLUMN `annulled` tinyint(1) DEFAULT '0' Comment 'Indica si la Factura esta anulada' AFTER `total`";

		dslContext.execute(sql);
	
		LOGGER.info("[END]");
	}

}
