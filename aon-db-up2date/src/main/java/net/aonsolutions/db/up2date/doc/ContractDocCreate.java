package net.aonsolutions.db.up2date.doc;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class ContractDocCreate implements Update {


	public static ContractDocCreate CONTRACTDOCCREATE = new ContractDocCreate();

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		//Set s3_key to max s3 file name length https://docs.aws.amazon.com/AmazonS3/latest/userguide/object-keys.html
		
		String sql =
		"CREATE TABLE IF NOT EXISTS `contract_doc` ("
		+ "  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del Documento del contrato',"
		+ "  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',"
		+ "  `contract` int(4) NOT NULL DEFAULT 0 COMMENT 'Identificador del Contrato',"
		+ "  `mimeType` tinyint(2) DEFAULT 0 COMMENT 'Mime Type del Documento',"
		+ "  `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del Documento',"
		+ "  `type` tinyint(2) DEFAULT NULL COMMENT 'Tipo de Documento (TA, IDC...)',"
		+ "  `scope` int(4) DEFAULT NULL COMMENT 'Ambito del Documento',"
		+ "  `security_level` tinyint(2) DEFAULT 0 COMMENT 'Nivel de seguridad del Documento',"
		+ "  `attach_date` datetime DEFAULT NULL COMMENT 'Fecha del Documento',"
		+ "  `s3_key` varchar(1024) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Amazon S3 Object key',"
		+ "  PRIMARY KEY (`id`),"
		+ "  KEY `IDX_CONTRACT_DOC_CONTRACT` (`contract`),"
		+ "  KEY `IDX_CONTRACT_DOC_SCOPE` (`scope`),"
		+ "  KEY `IDX_CONTRACT_DOC_DOMAIN` (`domain`),"
		+ "  CONSTRAINT `FK_CONTRACT_DOC_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),"
		+ "  CONSTRAINT `FK_CONTRACT_DOC_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),"
		+ "  CONSTRAINT `FK_CONTRACT_DOC_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)"
		+ ") ENGINE=InnoDB AUTO_INCREMENT=72070 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Documentos del contrato'" 
		;
		
		dslContext.execute(sql);

	}
}
