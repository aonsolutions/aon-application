package net.aonsolutions.db.up2date.elaboration;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class ElaborationSourceSourceIdIndex implements Update {



	public static final ElaborationSourceSourceIdIndex ELABORATION_SOURCE_SOURCEID_INDEX = new ElaborationSourceSourceIdIndex();

	private ElaborationSourceSourceIdIndex() {
		// private constructor to prevent instantiation
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		dslContext.resultQuery("SHOW INDEX FROM `elaboration` WHERE `column_name` = 'source_id';")
		.fetchOptional()
		.ifPresentOrElse(
			index -> System.out.println("Index on `source` and `source_id` already exists in `elaboration` table."),
			() ->  dslContext.execute("ALTER TABLE elaboration ADD INDEX `IDX_ELABORATION_SOURCE_SOURCEID` (`source`,`source_id`);")
		);
	}

}
