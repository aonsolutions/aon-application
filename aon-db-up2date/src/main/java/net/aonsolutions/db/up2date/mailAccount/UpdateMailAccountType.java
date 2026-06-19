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
			
			/*
			 * YA SE HA EJECUTADO CON ANTERIORIDAD
			 * 
			 * 
			int typeUserUpdates = config.dsl().update(MAIL_ACCOUNT)
					.set(MAIL_ACCOUNT.TYPE, (byte)0) // Usuario
					.where(MAIL_ACCOUNT.PROTOCOL.eq("aon"))
					.execute();
				
				
			System.out.printf("%d mailAccount user type updates \r\n", typeUserUpdates );
			*/
			
			/*
			 * Las siguiente Updates se pueden ejecutar como arreglo varias veces, previe nulls que existan
			 * */
			
			int dafaultIncomingPort = config.dsl().update(MAIL_ACCOUNT)
					.set(MAIL_ACCOUNT.INCOMING_PORT, 0) // Port by default
					.where(MAIL_ACCOUNT.PROTOCOL.eq("aon"))
					.and(MAIL_ACCOUNT.INCOMING_PORT.isNull())
					.execute();
				
				
			System.out.printf("%d mailAccount default incoming port updates \r\n", dafaultIncomingPort );
			
			int dafaultOutgoingPort = config.dsl().update(MAIL_ACCOUNT)
					.set(MAIL_ACCOUNT.OUTGOING_PORT, 25) // Port by default
					.where(MAIL_ACCOUNT.PROTOCOL.eq("aon"))
					.and(MAIL_ACCOUNT.OUTGOING_PORT.isNull())
					.execute();
				
				
			System.out.printf("%d mailAccount default outgoing port updates \r\n", dafaultOutgoingPort );
			
			int dafaultAccount = config.dsl().update(MAIL_ACCOUNT)
					.set(MAIL_ACCOUNT.DEFAULT_ACCOUNT, (byte)0) // Port by default
					.where(MAIL_ACCOUNT.PROTOCOL.eq("aon"))
					.and(MAIL_ACCOUNT.DEFAULT_ACCOUNT.isNull())
					.execute();
				
				
			System.out.printf("%d mailAccount default accounts updates \r\n", dafaultAccount );
			
		});
	}

	
}
