package net.aonsolutions.db.up2date.warehouse;

import java.sql.Connection;
import java.util.logging.Logger;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Up2DateUtils;
import net.aonsolutions.db.up2date.Update;

public class CarrierPackingAddQrColumn implements Update {

	private static final Logger LOGGER = Logger.getLogger(CarrierPackingAddQrColumn.class.getName());
	public static final CarrierPackingAddQrColumn CARRIER_PACKING_ADD_QR_COLUMN = new CarrierPackingAddQrColumn();

	private CarrierPackingAddQrColumn() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

		LOGGER.info("[START]");
		LOGGER.info("Alter table `carrier_packing`");
		if (Up2DateUtils.columnNotExists(connection, LOGGER, "carrier_packing", "qr")) {
			String sql = "ALTER TABLE `carrier_packing` ADD COLUMN `qr` varchar(256) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Url del codigo QR de la hoja de ruta' AFTER `reception_end_date`";
			dslContext.execute(sql);
			LOGGER.info("Columna `qr` agregada a la tabla `carrier_packing`");
		}
		LOGGER.info("[END]");
	}
}
