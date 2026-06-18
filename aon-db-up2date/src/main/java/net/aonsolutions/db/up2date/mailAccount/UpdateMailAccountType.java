package net.aonsolutions.db.up2date.mailAccount;

import static com.esferalia.aon.jooq.tables.MailAccount.MAIL_ACCOUNT;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class UpdateMailAccountType implements Update {
	
	public static final UpdateMailAccountType UPDATEMAILACCOUNTTYPE = new UpdateMailAccountType();
	

	private UpdateMailAccountType() {}

	@Override
	public void upgrade(Connection connection) {
		
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		// Establish context
		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		dslContext.transaction( (config) -> {
			
			int typeUserUpdates = config.dsl().update(MAIL_ACCOUNT)
					.set(MAIL_ACCOUNT.TYPE, (byte)0) // Usuario
					.execute();
				
				
			System.out.printf("%d mailAccount user type updates \r\n", typeUserUpdates );
			
		});
	}

	
}
