package net.aonsolutions.db.up2date.doc;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class InvoiceDocAddS3Bucket implements Update {


	public static final InvoiceDocAddS3Bucket INVOICEDOC_ADDS3BUCKET = new InvoiceDocAddS3Bucket();

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		String sql = "ALTER TABLE `invoice_doc` "
				+ "ADD COLUMN `s3_bucket` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Amazon S3 Bucket' AFTER `attach_date`";
				
		dslContext.execute(sql);
	}
}
