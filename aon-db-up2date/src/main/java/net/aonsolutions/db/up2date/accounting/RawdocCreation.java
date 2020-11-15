package net.aonsolutions.db.up2date.accounting;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class RawdocCreation implements Update {

//	#
//	# Structure for the rawdoc :
//	#
//
//	CREATE TABLE `rawdoc` (
//			  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del documento',
//			  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
//			  `nature` tinyint(2) DEFAULT '0' COMMENT 'Naturaleza del documento (Factura, Nomina, etc )',
//			  `type` tinyint(2) DEFAULT '0' COMMENT 'Tipo de Documento (Recibido o Emitido)',
//			  `status` tinyint(2) DEFAULT '0' COMMENT 'Estado del Documento (Inbox, Rechazado, Papelera)',
//			  `json` text COLLATE latin1_spanish_ci COMMENT 'Documento en formato JSON',
//			  `log` text COLLATE latin1_spanish_ci COMMENT 'Documento en formato JSON',
//			  `mime_type` tinyint(2) DEFAULT '0' COMMENT 'MIME Type',
//			  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
//			  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
//			  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
//			  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
//			  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
//			  PRIMARY KEY (`id`),
//			  KEY `IDX_RAW_DOCUMENT_DOMAIN` (`domain`),
//			  CONSTRAINT `FK_RAW_DOCUMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
//			) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Documentos a procesar';
//
// 	ALTER TABLE `user` ADD `auth` binary(16) DEFAULT NULL COMMENT `uuid auth`;


	public static RawdocCreation RAWDOC_CREATION = new RawdocCreation();

	private RawdocCreation() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		System.out.println("[START]");
		System.out.println( "Creacion table AUTH" );

		String SQL =
				 "CREATE TABLE `rawdoc` ("
				+"		  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del documento',"
				+"		  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',"
				+"		  `nature` tinyint(2) DEFAULT '0' COMMENT 'Naturaleza del documento (Factura, Nomina, etc )',"
				+"		  `type` tinyint(2) DEFAULT '0' COMMENT 'Tipo de Documento (Recibido o Emitido)',"
				+"		  `status` tinyint(2) DEFAULT '0' COMMENT 'Estado del Documento (Inbox, Rechazado, Papelera)',"
				+"		  `json` text COLLATE latin1_spanish_ci COMMENT 'Documento en formato JSON',"
				+"		  `log` text COLLATE latin1_spanish_ci COMMENT 'Documento en formato JSON',"
				+"		  `mime_type` tinyint(2) DEFAULT '0' COMMENT 'MIME Type',"
				+"		  `data` mediumblob COMMENT 'Archivo Adjunto en binario',"
				+"		  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',"
				+"		  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',"
				+"		  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',"
				+"		  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',"
				+"		  PRIMARY KEY (`id`),"
				+"		  KEY `IDX_RAW_DOCUMENT_DOMAIN` (`domain`),"
				+"		  CONSTRAINT `FK_RAW_DOCUMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)"
				+"		) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Documentos a procesar';"
		;
		try {
			dslContext.execute(SQL);
			System.out.println("[table 'rawdoc' CREATED!]");
		} catch (Throwable t) {
			System.out.println("[table 'rawdoc' NOT CREATED!]");
		}
	}

}
