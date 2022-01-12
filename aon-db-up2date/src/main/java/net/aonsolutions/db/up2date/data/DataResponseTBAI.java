package net.aonsolutions.db.up2date.data;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.DataResponse;
import com.esferalia.aon.jooq.tables.DataResponseDetail;

import net.aonsolutions.db.up2date.Update;

public class DataResponseTBAI implements Update {
public static final DataResponseTBAI DATA_RESPONSE_TBAI = new DataResponseTBAI();
	
	private static final Logger LOGGER  = Logger.getLogger(DataResponseTBAI.class.getName());

	private DataResponseTBAI() {
		super();
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		
		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		LOGGER.info("[START]");
		LOGGER.info("Update `data_response` source TBAI(6) TO TBAI_TEST(22)");

		dslContext.update(DataResponse.DATA_RESPONSE)
		.set(DataResponse.DATA_RESPONSE.SOURCE, (byte) 22)
		.where(DataResponse.DATA_RESPONSE.SOURCE.eq((byte) 6))
		.execute();
		
		LOGGER.info("[END]");
	}

}
