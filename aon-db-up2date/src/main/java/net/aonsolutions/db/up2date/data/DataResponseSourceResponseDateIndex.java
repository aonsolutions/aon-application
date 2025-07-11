package net.aonsolutions.db.up2date.data;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class DataResponseSourceResponseDateIndex implements Update {



	public static final DataResponseSourceResponseDateIndex DATA_RESPONSE_SOURCE_RESPONSE_DATE_INDEX = new DataResponseSourceResponseDateIndex();

	private DataResponseSourceResponseDateIndex() {
		// private constructor to prevent instantiation
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		dslContext.resultQuery("SHOW INDEX FROM `data_response` WHERE `column_name` = 'response_date' AND `Seq_in_index` = 2;")
		.fetchOptional()
		.ifPresentOrElse(
			index -> System.out.println("Index on "),
			() ->  dslContext.execute("ALTER TABLE data_response ADD INDEX `IDX_DATA_RESPONSE_SOURCE_RESPONSE_DATE` (`source`,`response_date`);")
		);
	}

}
