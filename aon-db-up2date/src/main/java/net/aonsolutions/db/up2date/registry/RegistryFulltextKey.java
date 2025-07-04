package net.aonsolutions.db.up2date.registry;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class RegistryFulltextKey implements Update {

	public static final RegistryFulltextKey REGISTRY_FULLTEXT_KEY = new RegistryFulltextKey();

	private RegistryFulltextKey() {}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		

		String dropNameKey = "ALTER TABLE registry DROP INDEX `IDX_REGISTRY_NAME`;";
		String dropDocumentKey = "ALTER TABLE registry DROP INDEX `IDX_REGISTRY_DOCUMENT`;";

		String addNameFulltextKey = "ALTER TABLE registry ADD FULLTEXT KEY `IDX_REGISTRY_NAME` (`name`);";
		String addDocumentFulltextKey = "ALTER TABLE registry ADD FULLTEXT KEY `IDX_REGISTRY_DOCUMENT` (`document`);";
		
		dslContext.execute(dropNameKey);
		dslContext.execute(dropDocumentKey);
		dslContext.execute(addNameFulltextKey);
		dslContext.execute(addDocumentFulltextKey);
		
	}

}
