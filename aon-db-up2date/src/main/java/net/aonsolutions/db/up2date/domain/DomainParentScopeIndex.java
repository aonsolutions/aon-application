package net.aonsolutions.db.up2date.domain;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class DomainParentScopeIndex implements Update {



	public static final DomainParentScopeIndex DOMAIN_PARENT_SCOPE_INDEX = new DomainParentScopeIndex();

	private DomainParentScopeIndex() {
		// private constructor to prevent instantiation
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		dslContext.resultQuery("SHOW INDEX FROM `domain` WHERE column_name = 'scope' AND `Seq_in_index` = 2;")
		.fetchOptional()
		.ifPresentOrElse(
			index -> System.out.println("Index on `parent` and `scope` already exists in `domain` table."),
			() ->  dslContext.execute("ALTER TABLE domain ADD INDEX `IDX_DELIVERY_DOMAIN_PARENT_SCOPE` (`parent`,`scope`);")
		);
	}

}
