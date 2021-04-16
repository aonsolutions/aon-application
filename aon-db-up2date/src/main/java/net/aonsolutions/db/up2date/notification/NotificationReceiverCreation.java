package net.aonsolutions.db.up2date.notification;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class NotificationReceiverCreation implements Update {

//	#
//	# Structure for the `notification_receiver` table :
//	#
//
//	CREATE TABLE `notification_receiver` (
//	  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'ID unico del vinculo',
//	  `notification` int(4) NOT NULL COMMENT 'ID unico de notification',
//	  `auth` binary(16) NOT NULL COMMENT 'ID de AUTH',
//	  `status` tinyint(2) NOT NULL COMMENT 'Estado de la notificacion',
//	  PRIMARY KEY (`id`),
//	  KEY `FK_NOTIFICATION_RECEIVER` (`notification`),
//	  KEY `IDX_NOTIFICATION_RECEIVER_AUTH` (`auth`),
//	  CONSTRAINT `FK_NOTIFICATION_RECEIVER` FOREIGN KEY (`notification`) REFERENCES `notification` (`id`),
//	  CONSTRAINT `FK_NOTIFICATION_RECEIVER_AUTH` FOREIGN KEY (`auth`) REFERENCES `auth` (`id`)
//	) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Notification receiver';


	public static NotificationReceiverCreation NOTIFICATION_RECEIVER_CREATION = new NotificationReceiverCreation();

	private NotificationReceiverCreation() {
		super();
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		System.out.println("[START]");
		System.out.println( "Creacion table `notification_receiver`" );

		String SQL = "CREATE TABLE `notification_receiver` (\n" + 
				"				`id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'ID unico del vinculo',\n" + 
				"				`notification` int(4) NOT NULL COMMENT 'ID unico de notification',\n" + 
				"				`auth` BINARY(16) NOT NULL COMMENT 'ID de AUTH',\n" + 
				"				`status` tinyint(2) NOT NULL COMMENT 'Estado de la notificacion',\n" + 
				"				PRIMARY KEY (`id`),\n" + 
				"				KEY `FK_NOTIFICATION_RECEIVER` (`notification`),\n" + 
				"		        KEY `IDX_NOTIFICATION_RECEIVER_AUTH` (`auth`),\n" + 
				"				CONSTRAINT `FK_NOTIFICATION_RECEIVER` FOREIGN KEY (`notification`) REFERENCES `notification` (`id`),\n" + 
				"			    CONSTRAINT `FK_NOTIFICATION_RECEIVER_AUTH` FOREIGN KEY (`auth`) REFERENCES `auth` (`id`)\n" + 
				"			) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Notification receiver';";

		try {
			dslContext.execute(SQL);
			System.out.println("[table 'notification_receiver' CREATED!]");
		} catch (Throwable t) {
			System.out.println("[table 'notification_receiver' NOT CREATED!]");
		}
		System.out.println("[END]");
	}

}
