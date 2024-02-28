package net.aonsolutions.db.up2date.domain;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class DomainAddDomainPayerColumn implements Update {

	public static final DomainAddDomainPayerColumn DOMAIN_ADD_DOMAIN_PAYER_COLUMN = new DomainAddDomainPayerColumn();
	
	private DomainAddDomainPayerColumn() {
		
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
			String sql = "ALTER TABLE `domain` ADD `domain_payer` int DEFAULT NULL COMMENT 'Dominio Pagador' AFTER `enableHeredity`";
			dslContext.execute(sql);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

}
