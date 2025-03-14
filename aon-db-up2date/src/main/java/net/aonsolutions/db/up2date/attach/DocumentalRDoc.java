package net.aonsolutions.db.up2date.attach;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class DocumentalRDoc implements Update {
	
	public static final DocumentalRDoc DOCUMENTAL_RDOC = new DocumentalRDoc();

	private DocumentalRDoc() {
		
	}

	@Override
	public void upgrade(Connection conn) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		DSLContext dslContext = DSL.using(conn, SQLDialect.MARIADB, settings);
		
		// Creación de la tabla rdoc donde se va a almacenar la información de los documentos que se van a alojar en el s3
		String sql = "CREATE TABLE IF NOT EXISTS rdoc ("
				+ "    id                INT AUTO_INCREMENT PRIMARY KEY,"
				+ "    domain            INT NOT NULL COMMENT 'dominio del documento',"
				+ "    registry          INT NOT NULL DEFAULT 0 COMMENT 'registry al que pertenece el documento????'," // ?????????????
				+ "    category          INT NULL COMMENT 'categoria del documento',"
				+ "    size              INT NULL COMMENT 'tamaño en bytes del documento',"
				+ "    mimeType          TINYINT NULL DEFAULT 0 COMMENT 'tipo de extension del documento',"
				+ "    name              VARCHAR(64) NULL COMMENT 'nombre del documento',"
				+ "    scope             INT NULL COMMENT '????'," // ??????????????
				+ "    security_level    TINYINT NULL DEFAULT 0 COMMENT '????'," // ??????????????
				+ "    document_date     DATE NULL COMMENT 'fecha a la que pertence el documento',"
				+ "    s3                VARCHAR(128) NULL COMMENT 'key del s3 donde esta el documento',"
				+ "    s3_bucket         VARCHAR(128) NULL COMMENT 'bucket del s3 donde esta el documento',"
				+ "    creation_user     VARCHAR(16) NULL,"
				+ "    creation_date     DATETIME NULL,"
				+ "    modification_user VARCHAR(16) NULL,"
				+ "    modification_date DATETIME NULL,"
				+ "    INDEX idx_rdoc_domain (domain),"
				+ "    INDEX idx_rdoc_registry (registry),"
				+ "    INDEX idx_rdoc_category (category),"
				+ "    INDEX idx_rdoc_scope (scope),"
				+ "    CONSTRAINT fk_rdoc_domain FOREIGN KEY (domain) REFERENCES domain(id),"
				+ "    CONSTRAINT fk_rdoc_registry FOREIGN KEY (registry) REFERENCES registry(id),"
				+ "    CONSTRAINT fk_rdoc_category FOREIGN KEY (category) REFERENCES category(id),"
				+ "    CONSTRAINT fk_rdoc_scope FOREIGN KEY (scope) REFERENCES scope(id)"
				+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Documentos del documental en el s3';";
		dslContext.execute(sql);
	}
}
