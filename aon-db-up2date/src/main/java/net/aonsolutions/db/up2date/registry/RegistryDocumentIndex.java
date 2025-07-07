package net.aonsolutions.db.up2date.registry;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class RegistryDocumentIndex implements Update {



	public static final RegistryDocumentIndex REGISTRY_DOCUMENT_INDEX = new RegistryDocumentIndex();

	private RegistryDocumentIndex() {
		// private constructor to prevent instantiation
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		dslContext.resultQuery("SHOW INDEX FROM `registry` WHERE `column_name` = 'document' AND `index_type` = 'BTREE'")
		.fetchOptional()
		.ifPresentOrElse(
			index -> System.out.println("Index btree on 'document' already exists in 'registry' table."),
			() ->  dslContext.execute("ALTER TABLE registry ADD INDEX `IDX_BTREE_REGISTRY_DOCUMENT` (`document`);")
		);
	}

}
