package net.aonsolutions.db.up2date.notification;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class NotificationReceiverAuthIndex implements Update {



	public static final NotificationReceiverAuthIndex NOTIFICATION_RECEIVER_AUTH_INDEX = new NotificationReceiverAuthIndex();

	private NotificationReceiverAuthIndex() {
		// private constructor to prevent instantiation
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		dslContext.resultQuery("SHOW INDEX FROM `notification_receiver` WHERE `column_name` = 'auth'")
		.fetchOptional()
		.ifPresentOrElse(
			index -> System.out.println("Index on 'auth' already exists in 'notification_receiver' table."),
			() ->  dslContext.execute("ALTER TABLE notification_receiver ADD INDEX `IDX_NOTIFICATION_RECEIVER_AUTH` (`auth`);")
		);
	}

}
