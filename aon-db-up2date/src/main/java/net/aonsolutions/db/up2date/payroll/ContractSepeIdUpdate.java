package net.aonsolutions.db.up2date.payroll;

import java.sql.Connection;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import com.esferalia.aon.jooq.tables.Contract;
import net.aonsolutions.db.up2date.Update;

public class ContractSepeIdUpdate implements Update {

	public static ContractSepeIdUpdate CONTRACT_SEPE_ID_UPDATE = new ContractSepeIdUpdate();

	private ContractSepeIdUpdate() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		System.out.println("[START]");
		System.out.println( "alter table CONTRACT add column sepe_id" );

		dslContext.alterTable(Contract.CONTRACT).addColumnIfNotExists("sepe_id", SQLDataType.VARCHAR(16).nullable(true)).execute();
		
		System.out.println("[END]");
	}

}
