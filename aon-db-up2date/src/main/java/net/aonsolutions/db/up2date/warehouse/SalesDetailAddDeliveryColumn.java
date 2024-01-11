package net.aonsolutions.db.up2date.warehouse;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class SalesDetailAddDeliveryColumn implements Update {

	public static final SalesDetailAddDeliveryColumn SALES_DETAIL_ADD_DELIVERY_COLUMN = new SalesDetailAddDeliveryColumn();

	private SalesDetailAddDeliveryColumn() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		String sql1 = "ALTER TABLE `sales_detail` ADD `delivery` int DEFAULT NULL COMMENT 'Identificador del Albaran de Venta' AFTER `carrier_packing`;";
		String sql2 = "ALTER TABLE `sales_detail` ADD KEY `IDX_SALES_DETAIL_DELIVERY` (`delivery`)";
		String sql3 = "ALTER TABLE `sales_detail` ADD CONSTRAINT `FK_SALES_DETAIL_DELIVERY` FOREIGN KEY (`delivery`) REFERENCES `delivery` (`id`)";

		dslContext.execute(sql1);
		dslContext.execute(sql2);
		dslContext.execute(sql3);		
	}
}
