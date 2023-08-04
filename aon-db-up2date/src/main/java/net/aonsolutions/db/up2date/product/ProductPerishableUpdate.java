package net.aonsolutions.db.up2date.product;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;


public class ProductPerishableUpdate implements Update {

	public static final ProductPerishableUpdate PRODUCT_PEISHABLE_UPDATE = new ProductPerishableUpdate();

	private ProductPerishableUpdate() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		DSLContext dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

		System.out.println("[START]");
		System.out.println( "Update table `product`" );
		try {
			
			String sql = "ALTER TABLE `product` ADD COLUMN `perishable` tinyint(1) DEFAULT '0' Comment 'Indica si el producto es perecedero' AFTER `purchase_account`";
			dslContext.execute(sql);
			String sql2 = "ALTER TABLE `product` ADD COLUMN `days_to_expire` int DEFAULT '0' Comment 'Numero de dias en los que expira el producto' AFTER `perishable`";
			dslContext.execute(sql2);
			
			String sql3 = "ALTER TABLE `item` ADD COLUMN `expire_date` date DEFAULT NULL Comment 'Fecha de caducidad del articulo' AFTER `serial_date`";
			dslContext.execute(sql3);

			System.out.println("[table 'product' Update!]");
		} catch (Throwable t) {
			System.out.println("[table 'product' NOT Update!] " + t.getMessage());
		}
		System.out.println("[END]");
	}
	
}
