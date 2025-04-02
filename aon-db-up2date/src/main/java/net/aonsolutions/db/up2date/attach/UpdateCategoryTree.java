package net.aonsolutions.db.up2date.attach;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class UpdateCategoryTree implements Update {
	
	public static final UpdateCategoryTree UPDATE_CATEGORY_TREE = new UpdateCategoryTree();

	private UpdateCategoryTree() {
		
	}
	
	@Override
	public void upgrade(Connection conn) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		DSLContext dslContext = DSL.using(conn, SQLDialect.MARIADB, settings);
		System.out.println("[START]");
		System.out.println("Alter table `category_tree`");
		String database = dslContext.fetchOne("SELECT DATABASE()").getValue(0, String.class);
		System.out.println(database);
		if(checkFK(dslContext, "fk_category",database)) {
			dslContext.execute("ALTER TABLE category_tree DROP FOREIGN KEY fk_category;");
		}
		if(checkFK(dslContext, "fk_parent", database)) {
			dslContext.execute("ALTER TABLE category_tree DROP FOREIGN KEY fk_parent;");
		}
		if(checkIndexCreation(dslContext, "idx_category_parent", database)) {
			dropCategoryTreeOldIndex(dslContext);
		}
		if(checkColumn(dslContext, "id_category", database)) {
			dslContext.execute("ALTER TABLE category_tree CHANGE COLUMN id_category category INT NOT NULL;");
		}
		if(checkColumn(dslContext, "id_parent", database)) {
			dslContext.execute("ALTER TABLE category_tree CHANGE COLUMN id_parent parent INT;");
		}
		if(!checkFK(dslContext, "FK_CATEGORY_TREE_CATEGORY", database)) {
			createCategoryFK(dslContext);
		}
		if(!checkFK(dslContext, "FK_CATEGORY_TREE_PARENT", database)) {
			createParentFK(dslContext);
		}
		if(!checkColumn(dslContext, "domain", database)) {
			createDomainColumn(dslContext);
			createDomainFK(dslContext);
			fillDomainCategoryTree(dslContext);
		}		
	}
	
	private boolean checkFK(DSLContext dslContext, String fk, String database) {
		return dslContext.fetchExists(dslContext.selectOne().from("information_schema.table_constraints")
		        .where(DSL.field("table_name").eq(DSL.inline("category_tree"))
		        .and(DSL.field("constraint_type").eq(DSL.inline("FOREIGN KEY"))
		        .and(DSL.field("constraint_name").eq(DSL.inline(fk))
		        .and(DSL.field("table_schema").eq(DSL.inline(database)))
		        		))));
	}
	
	private boolean checkColumn(DSLContext dslContext, String column, String database) {
		return dslContext.fetchExists(dslContext.selectOne().from("information_schema.columns")
		        .where(DSL.field("table_name").eq(DSL.inline("category_tree"))
		        .and(DSL.field("column_name").eq(DSL.inline(column))
		        .and(DSL.field("table_schema").eq(DSL.inline(database)))
		        		)));
	}
	
	private boolean checkIndexCreation(DSLContext dslContext, String index, String database) {
		return dslContext.fetchExists(dslContext.selectOne().from("information_schema.STATISTICS")
		        .where(DSL.field("table_name").eq(DSL.inline("category_tree"))
		        .and(DSL.field("INDEX_NAME").eq(DSL.inline(index))
		        .and(DSL.field("table_schema").eq(DSL.inline(database)))
		        		)));
	}
	
	private void createCategoryFK(DSLContext dslContext) {
		dslContext.execute("ALTER TABLE category_tree "
				+ "ADD CONSTRAINT FK_CATEGORY_TREE_CATEGORY "
				+ "FOREIGN KEY (category) "
				+ "REFERENCES category (id);");
	}
	
	private void createParentFK(DSLContext dslContext) {
		dslContext.execute("ALTER TABLE category_tree "
				+ "ADD CONSTRAINT FK_CATEGORY_TREE_PARENT "
				+ "FOREIGN KEY (parent) "
				+ "REFERENCES category (id);");
	}
	
	private void createDomainFK(DSLContext dslContext) {
		dslContext.execute("ALTER TABLE category_tree "
				+ "ADD CONSTRAINT FK_CATEGORY_TREE_DOMAIN "
				+ "FOREIGN KEY (domain) "
				+ "REFERENCES domain(id);");
	}
	
	private void createDomainColumn(DSLContext dslContext) {
		dslContext.execute("ALTER TABLE category_tree ADD COLUMN domain INT NOT NULL COMMENT 'Identificador del Dominio';");
	}
	
	private void fillDomainCategoryTree(DSLContext dslContext) {
		dslContext.execute("UPDATE category_tree ct "
				+ "JOIN category c ON ct.category = c.id "
				+ "SET ct.domain = c.domain;");
	}
	
	private void dropCategoryTreeOldIndex(DSLContext dslContext) {
		dslContext.execute("ALTER TABLE category_tree DROP INDEX idx_category_parent;");
	}
}
