package net.aonsolutions.db.up2date.security;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class DomainAppCreation implements Update {

//	#
//	# Structure for the `domain_app` table :
//	#
//
//	CREATE TABLE `domain_app` (
//	  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'ID unico del vinculo',
//	  `domain` int(4) NOT NULL DEFAULT 0 COMMENT 'Dominio',
//	  `app` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'App',
//	  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Indica si la Aplicacion del Dominio esta activa o no',
//	  PRIMARY KEY (`id`),
//    UNIQUE KEY `IDX_UNQ_DOMAIN_APP` (`domain`),
//	  KEY `IDX_DOMAIN_APP_DOMAIN` (`domain`),
//	  CONSTRAINT `FK_DOMAIN_APP_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
//	) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Aplicacion web';


	public static DomainAppCreation DOMAIN_APP_CREATION = new DomainAppCreation();

	private DomainAppCreation() {
		super();
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		System.out.println("[START]");
		System.out.println( "Creacion table `domain_app`" );

		String SQL =
		"CREATE TABLE IF NOT EXISTS `domain_app` ("
			  + "`id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'ID unico del vinculo',"
			  + "`domain` int(4) NOT NULL DEFAULT 0 COMMENT 'Dominio',"
			  + "`app` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'App',"
			  + "`active` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Indica si la Aplicacion del Dominio esta activa o no',"
		  + "PRIMARY KEY (`id`),"
		  + "UNIQUE KEY `IDX_UNQ_DOMAIN_APP` (`domain`),"
		  + "KEY `IDX_DOMAIN_APP_DOMAIN` (`domain`),"
		  + "CONSTRAINT `FK_DOMAIN_APP_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)"
		+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Aplicacion web';"
		;

		dslContext.execute(SQL);

		System.out.println("[END]");
	}

}
