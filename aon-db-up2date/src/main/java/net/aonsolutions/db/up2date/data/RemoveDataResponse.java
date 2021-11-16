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

public class RemoveDataResponse implements Update {


	public static final RemoveDataResponse REMOVE_DATA_RESPONSE = new RemoveDataResponse();
	
	private static final Logger LOGGER  = Logger.getLogger(RemoveDataResponse.class.getName());

	private RemoveDataResponse() {
		super();
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		
		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		LOGGER.info("[START]");
		LOGGER.info("Remove `data_response` wiht source TEDI_INVOICE (6) & INVOICE(22)");
		
		LinkedList<Integer> drIds = dslContext
				.select(DataResponse.DATA_RESPONSE.ID)
				.from(DataResponse.DATA_RESPONSE)
				.where(DataResponse.DATA_RESPONSE.SOURCE.in((byte) 6, (byte) 22))
				.fetch().stream().map(r -> r.getValue(DataResponse.DATA_RESPONSE.ID))
				.collect(Collectors.toCollection(LinkedList::new));
		
		dslContext
		.delete(DataResponseDetail.DATA_RESPONSE_DETAIL)
		.where(DataResponseDetail.DATA_RESPONSE_DETAIL.DATA_RESPONSE.in(drIds))
		.execute();
		
		dslContext
			.delete(DataResponse.DATA_RESPONSE)
			.where(DataResponse.DATA_RESPONSE.SOURCE.in((byte) 6, (byte) 22))
			.execute();
		
		LOGGER.info("[END]");
	}

}
