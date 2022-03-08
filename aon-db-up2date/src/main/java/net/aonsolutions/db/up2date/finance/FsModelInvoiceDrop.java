package net.aonsolutions.db.up2date.finance;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class FsModelInvoiceDrop implements Update {
	
	public static final FsModelInvoiceDrop FS_MODEL_INVOICE_DROP = new FsModelInvoiceDrop();

	private FsModelInvoiceDrop() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		System.out.println("[START]");
		System.out.println( "Drop table `fs_model_invoice`" );
		String sql = "DROP TABLE IF EXISTS `fs_model_invoice`;";
		dslContext.execute(sql);
		System.out.println("[END]");
	}
}
