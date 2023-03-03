package net.aonsolutions.db.up2date.finance;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class UpdateBankAccountUpperCase implements Update {

	public static final UpdateBankAccountUpperCase UPDATE_BANK_ACCOUNT_UPPERCASE = new UpdateBankAccountUpperCase();
	
	private UpdateBankAccountUpperCase() {}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		dslContext.transaction( config -> {
			updateBankAccountUpperCase(dslContext);
		});
	}

	private void updateBankAccountUpperCase(DSLContext dslContext) {
		try {
			List<Table<?>> tables = dslContext.meta().getTables();
			List<Table<?>> bankAccountTables = new ArrayList<>();
			
			for(Table<?> table : tables) {
				try {
					if(table.field("bank_account") != null) bankAccountTables.add(table);
				} catch (Exception e) {}
			}
			
			for(Table<?> table : bankAccountTables) {
				Result<Record2<Integer, String>> wrongBankAccounts = dslContext.select(DSL.field(table.getName() + ".id", Integer.class), DSL.field(table.getName() + ".bank_account", String.class)).from(table.getName()).where(table.getName() +".bank_account like binary 'es%'").fetch();				
				if(wrongBankAccounts.isNotEmpty())
					System.out.println("Tabla : " + table.getName() + " actualizada. BankAccount con 'es' : " + wrongBankAccounts.size());
				wrongBankAccounts.forEach(wrongBankAccount -> {
					Integer id = (int) wrongBankAccount.get(0);
					
					String bankAccount = (String) wrongBankAccount.get(1);
					bankAccount = bankAccount.toUpperCase();
					
					dslContext.update(table)
						.set(DSL.field(table.getName() + ".bank_account"), bankAccount)
						.where(DSL.field(table.getName() + ".id").eq(id))
						.execute();
				});
			}
		} catch (Exception e) {}
	}
	
}
