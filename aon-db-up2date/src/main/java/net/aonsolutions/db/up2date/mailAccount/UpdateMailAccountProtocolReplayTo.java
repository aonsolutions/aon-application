package net.aonsolutions.db.up2date.mailAccount;

import static com.esferalia.aon.jooq.tables.MailAccount.MAIL_ACCOUNT;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class UpdateMailAccountProtocolReplayTo implements Update {
	
	public static final UpdateMailAccountProtocolReplayTo UPDATEMAILACCOUNTPROTOCOLREPLAYTO = new UpdateMailAccountProtocolReplayTo();
	

	private UpdateMailAccountProtocolReplayTo() {}

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
			
			int typeEnterpriseUpdates = config.dsl().update(MAIL_ACCOUNT)
				.set(MAIL_ACCOUNT.TYPE, (byte)1) // Empresa
				.where(MAIL_ACCOUNT.USER_ID.isNull())
				.execute();
			
			
			System.out.printf("%d mailAccount enterprise type updates \r\n", typeEnterpriseUpdates );
			
			int typeUserUpdates = config.dsl().update(MAIL_ACCOUNT)
					.set(MAIL_ACCOUNT.TYPE, (byte)0) // Usuario
					.where(MAIL_ACCOUNT.USER_ID.isNotNull())
					.execute();
				
				
			System.out.printf("%d mailAccount user type updates \r\n", typeUserUpdates );
			
			int outgoingVerificationUpdates = config.dsl().update(MAIL_ACCOUNT)
			        .set(MAIL_ACCOUNT.OUTGOING_VERIFICATION, (byte) 1) // Activar nuevo isBCCInclude
			        .where(MAIL_ACCOUNT.PROTOCOL.eq("aon"))
			        .and(MAIL_ACCOUNT.REPLYTO_MAIL.isNotNull()
			        		.and(DSL.length(DSL.trim(MAIL_ACCOUNT.REPLYTO_MAIL)).gt(0)))
			        .execute();
				
				
			System.out.printf("%d mailAccount eoutgoing verification updates \r\n", outgoingVerificationUpdates );
			

			int replayToOutgoingVerificationUpdates = config.dsl().update(MAIL_ACCOUNT)
			        .set(MAIL_ACCOUNT.REPLYTO_MAIL, DSL.trim(MAIL_ACCOUNT.EMAIL)) 		// ReplayTo = Email
			        .set(MAIL_ACCOUNT.OUTGOING_VERIFICATION, (byte) 0)					// Desactivar nuevo isBCCInclude
			        .where(MAIL_ACCOUNT.REPLYTO_MAIL.isNull()
			        		.or(DSL.length(DSL.trim(MAIL_ACCOUNT.REPLYTO_MAIL)).gt(0)))
			        .and(MAIL_ACCOUNT.PROTOCOL.eq("aon"))
			        .execute();
				
				
			System.out.printf("%d mailAccount replayTo outgoing verification updates \r\n", replayToOutgoingVerificationUpdates );
			
		});
	}

	
}
