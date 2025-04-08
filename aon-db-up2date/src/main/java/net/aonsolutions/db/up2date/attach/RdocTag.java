package net.aonsolutions.db.up2date.attach;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class RdocTag implements Update {

	public static final RdocTag RDOC_TAG = new RdocTag();
	
	@Override
	public void upgrade(Connection conn) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		DSLContext dslContext = DSL.using(conn, SQLDialect.MARIADB, settings);
		
		System.out.println("[START]");
		System.out.println("Creation table `rdoc_tag`");
		
		String database = dslContext.fetchOne("SELECT DATABASE()").getValue(0, String.class);
		boolean existsTable = dslContext.fetchExists(dslContext.selectOne().from("information_schema.tables")
		        .where(DSL.field("table_name").eq(DSL.inline("rdoc_tag"))
		        .and(DSL.field("table_schema").eq(DSL.inline(database)))));
		
		if(!existsTable) {
			String create_table = 
					"CREATE TABLE IF NOT EXISTS rdoc_tag ("
					+ "    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,"
					+ "    domain INT NOT NULL COMMENT 'identificador del dominio',"
					+ "    rdoc INT NOT NULL COMMENT 'identificador del documento almacenado en rdoc',"
					+ "    tag INT NOT NULL COMMENT 'identificador del tag',"
					+ "    CONSTRAINT FK_RDOC_TAG_DOMAIN FOREIGN KEY (domain) REFERENCES domain(id),"
					+ "    CONSTRAINT FK_RDOC_TAG_RDOC FOREIGN KEY (rdoc) REFERENCES rdoc(id),"
					+ "    CONSTRAINT FK_RDOC_TAG_TAG FOREIGN KEY (tag) REFERENCES tag(id)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Asociacion N:N de rdocs con tags.';";
			dslContext.execute(create_table);
		}
		System.out.println("[END]");
	}
	
}
