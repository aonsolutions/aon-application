package net.aonsolutions.db.up2date.finance;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class InvoiceInfoAddAudit implements Update {
	
	public static final InvoiceInfoAddAudit INVOICE_INFO_ADD_AUDIT = new InvoiceInfoAddAudit();

	private InvoiceInfoAddAudit() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
	
		String sql = "ALTER TABLE `invoice_info` "
			+ "ADD COLUMN `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion'";
		String sql2 = "ALTER TABLE `invoice_info` "
			+ "ADD COLUMN `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion'";
		String sql3 = "ALTER TABLE `invoice_info` "
			+ "ADD COLUMN `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion'";
		String sql4 = "ALTER TABLE `invoice_info` "
			+ "ADD COLUMN `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion'";

		dslContext.execute(sql);
		dslContext.execute(sql2);
		dslContext.execute(sql3);
		dslContext.execute(sql4);
	}	

}
