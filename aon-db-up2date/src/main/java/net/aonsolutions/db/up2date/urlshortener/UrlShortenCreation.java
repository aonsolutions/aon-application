package net.aonsolutions.db.up2date.urlshortener;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class UrlShortenCreation implements Update {



	public static UrlShortenCreation URLSHORTEN_CREATION = new UrlShortenCreation();

	private UrlShortenCreation() {
		super();
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		String ddl = 
				"""
				CREATE TABLE IF NOT EXISTS  `url_shorten`( 
				`id` int NOT NULL AUTO_INCREMENT COMMENT 'ID unico de la url', 
				`domain` int NOT NULL COMMENT 'Dominio', 
				`url` TEXT NOT NULL COMMENT 'URL original',
				`uuid` varchar(128) NOT NULL COMMENT 'Identificador unico de la URL',
				`count` int NOT NULL DEFAULT 0 COMMENT 'Peticiones',
				`expiration_date` datetime DEFAULT NULL COMMENT 'Fecha de caducidad' ,
				`creation_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha de creacion', 
				PRIMARY KEY (`id`),
				KEY `IDX_URL_SHORTEN_DOMAIN` (`domain`), 
				UNIQUE KEY `IDX_URL_SHORTEN_UUID` (`uuid`), 
				CONSTRAINT `FK_URL_SHORTEN_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
				) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='URLs ofuscadas';
				""";
		dslContext.execute(ddl);
	}

}
