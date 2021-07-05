package net.aonsolutions.db.up2date.security;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import com.esferalia.aon.jooq.tables.User;

import net.aonsolutions.db.up2date.Update;

public class AuthAttachCreation implements Update {

//	#
//	# Structure for the `auth_attach` table :
//	#
//
//	CREATE TABLE `auth_attach` (
//	  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
//	  `auth` BINARY(16) NOT NULL COMMENT 'Identificador de Auth',
//	  `mimeType` tinyint(2) DEFAULT '0' COMMENT 'Mime Type del Archivo Adjunto',
//	  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
//	  `type` tinyint(2) DEFAULT NULL COMMENT 'Tipo de Archivo Adjunto',
//	  PRIMARY KEY (`id`)
//	) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ficheros de Auth';



	public static final AuthAttachCreation AUTH_ATTACH_CREATION = new AuthAttachCreation();

	private AuthAttachCreation() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		System.out.println("[START]");
		System.out.println( "Creacion table AUTH ATTACH" );

		String SQL =
				"CREATE TABLE IF NOT EXISTS `auth_attach` (" + 
				"  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico'," + 
				"  `auth` BINARY(16) NOT NULL COMMENT 'Identificador de Auth'," + 
				"  `mimeType` tinyint(2) DEFAULT '0' COMMENT 'Mime Type del Archivo Adjunto'," + 
				"  `data` mediumblob COMMENT 'Archivo Adjunto en binario'," + 
				"  `type` tinyint(2) DEFAULT NULL COMMENT 'Tipo de Archivo Adjunto'," + 
				"  PRIMARY KEY (`id`)" + 
				") ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ficheros de Auth';"
		;

		dslContext.execute(SQL);
		
		System.out.println("[END]");
	}

}
