package net.aonsolutions.db.up2date.registry;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class RItemAddCustomerFeeColumn implements Update {

	public static final RItemAddCustomerFeeColumn RITEM_ADD_CUSTOMER_FEE_COLUMN = new RItemAddCustomerFeeColumn();
	
	private RItemAddCustomerFeeColumn() {}
	
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
					"ADD COLUMN `customer_fee` INT(11) DEFAULT NULL COMMENT 'Cuota del cliente asociado' AFTER `edi_sales_code`, " +
					"ADD CONSTRAINT FK_RITEM_CUSTOMER_FEE " + 
						"FOREIGN KEY (customer_fee) " +
						"REFERENCES customer_fee(id), " +
					"ADD INDEX IDX_RITEM_CUSTOMER_FEE (customer_fee)";
			dslContext.execute(sql);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

}
