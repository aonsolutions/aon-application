package net.aonsolutions.db.up2date.registry;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class AlterRegistryBankBalance implements Update {
	
	public static final AlterRegistryBankBalance ALTER_REGISTRY_BANK_BALANCE = new AlterRegistryBankBalance();

	private AlterRegistryBankBalance() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

		boolean balance = false;
		boolean availableBalance = false;
		boolean balanceDate = false;

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from rbank");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);

				if ("balance".equals(name)) 	{balance = true;}
				if ("available_balance".equals(name)) 	{availableBalance = true;}
				if ("balance_date".equals(name)) {balanceDate = true;}
				
			}
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					// nothing
				}
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					// nothing
				}
		}
		
		if (!balance) {
			try {
				System.out.println("\tAlterRegistryBankBalance. balance NOT EXISTS!");
				dslContext.execute("ALTER TABLE `rbank` ADD `balance` decimal(15,4) DEFAULT 0.0000 COMMENT 'Saldo banco'");
				System.out.println("\tAlterRegistryBankBalance. balance CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterRegistryBankBalance. balance NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterRegistryBankBalance. balance EXISTS!");
		}
		
		if (!availableBalance) {
			try {
				System.out.println("\tAlterRegistryBankBalance. available_balance NOT EXISTS!");
				dslContext.execute("ALTER TABLE `rbank` ADD `available_balance` decimal(15,4) DEFAULT 0.0000 COMMENT 'Saldo disponible banco'");
				System.out.println("\tAlterRegistryBankBalance. available_balance CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterRegistryBankBalance. available_balance NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterRegistryBankBalance. available_balance EXISTS!");
		}
		
		if (!balanceDate) {
			try {
				System.out.println("\tAlterRegistryBankBalance. balance_date NOT EXISTS!");
				dslContext.execute("ALTER TABLE `rbank` ADD `balance_date` datetime DEFAULT NULL COMMENT 'Fecha actualizacion saldo'");
				System.out.println("\tAlterRegistryBankBalance. balance_date CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterRegistryBankBalance. balance_date NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterRegistryBankBalance. balance_date EXISTS!");
		}
		
	}

}
