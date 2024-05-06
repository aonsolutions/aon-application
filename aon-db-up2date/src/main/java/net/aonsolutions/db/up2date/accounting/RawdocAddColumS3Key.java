package net.aonsolutions.db.up2date.accounting;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class RawdocAddColumS3Key implements Update {

public static final RawdocAddColumS3Key RAWDOC_ADD_COLUMN_S3_KEY = new RawdocAddColumS3Key();
	
	private RawdocAddColumS3Key() {
		
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

		try {
			String sql = "ALTER TABLE `rawdoc` ADD `s3_key` varchar(1024) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Amazon S3 Object key' AFTER `data`";
			dslContext.execute(sql);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
}
