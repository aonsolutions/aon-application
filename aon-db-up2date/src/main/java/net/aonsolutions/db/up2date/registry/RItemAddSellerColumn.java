package net.aonsolutions.db.up2date.registry;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class RItemAddSellerColumn implements Update {

	public static final RItemAddSellerColumn RITEM_ADD_SELLER_COLUMN = new RItemAddSellerColumn();
	
	private RItemAddSellerColumn() {}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

		try {
			String sql = 
					"ALTER TABLE `ritem` " +
					"ADD COLUMN `seller` INT(11) DEFAULT NULL COMMENT 'Agente asociado al cliente' AFTER `customer_fee`, " +
					"ADD CONSTRAINT FK_RITEM_SELLER " + 
						"FOREIGN KEY (seller) " +
						"REFERENCES seller(registry), " +
					"ADD INDEX IDX_RITEM_SELLER (seller)";
			dslContext.execute(sql);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

}
