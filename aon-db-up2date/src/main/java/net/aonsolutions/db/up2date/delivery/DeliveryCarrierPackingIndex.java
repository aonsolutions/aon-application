package net.aonsolutions.db.up2date.delivery;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class DeliveryCarrierPackingIndex implements Update {



	public static final DeliveryCarrierPackingIndex DELIVERY_CARRIER_PACKING_INDEX = new DeliveryCarrierPackingIndex();

	private DeliveryCarrierPackingIndex() {
		// private constructor to prevent instantiation
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		dslContext.resultQuery("SHOW INDEX FROM `delivery` WHERE `column_name` = 'carrier_packing';")
		.fetchOptional()
		.ifPresentOrElse(
			index -> System.out.println("Index on `carrier_packing` already exists in `delivery` table."),
			() ->  dslContext.execute("ALTER TABLE delivery ADD INDEX `IDX_DELIVERY_CARRIER_PACKING` (`carrier_packing`);")
		);
	}

}
