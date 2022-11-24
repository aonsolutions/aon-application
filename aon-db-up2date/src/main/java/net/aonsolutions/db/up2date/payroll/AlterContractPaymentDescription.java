package net.aonsolutions.db.up2date.payroll;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class AlterContractPaymentDescription implements Update {

	public static final AlterContractPaymentDescription ALTERCONTRACTPAYMENTDESCRIPTION = new AlterContractPaymentDescription();
	
	private AlterContractPaymentDescription() {}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		
		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		dslContext.execute("ALTER TABLE `contract_payment` MODIFY `description` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion'");
	}

}
