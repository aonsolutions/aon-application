package net.aonsolutions.db.up2date.config;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class AddColumnActivityExemptionCause implements Update {

	public static final AddColumnActivityExemptionCause ADD_COLUMN_ACTIVITY_EXEMPTION_CAUSE = new AddColumnActivityExemptionCause();

	private AddColumnActivityExemptionCause() {
		
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		String sql = "ALTER TABLE `enterprise_activity` ADD COLUMN `vat_exemption_cause` tinyint DEFAULT NULL Comment 'Causa de exencion de IVA' AFTER `vat_regime`";

		dslContext.execute(sql);


	}

}
