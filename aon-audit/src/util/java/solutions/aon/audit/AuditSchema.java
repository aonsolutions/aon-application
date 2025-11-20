package solutions.aon.audit;

import org.jooq.DDLExportConfiguration;
import org.jooq.DDLFlag;
import org.jooq.DSLContext;
import org.jooq.Queries;
import org.jooq.SQLDialect;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.conf.ParamType;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.AonMaster;

public class AuditSchema {

	static Table<?>[] getAuditTables4(Schema schema) {
		return schema.getTables().stream()
				.map(AuditTable::new)
				.toArray(Table<?>[]::new);
	}
	
	static Queries getDDL(DSLContext dslContext,Schema schema) {
		Table<?>[] auditTables = getAuditTables4(schema);
		DDLExportConfiguration config = 
				new DDLExportConfiguration()
				
				.respectColumnOrder(true)
				.respectCatalogOrder(true)

				.createTableIfNotExists(true)
				.createSchemaIfNotExists(true)
				
				
				.flags(DDLFlag.TABLE, DDLFlag.INDEX)
				;
		return dslContext.ddl(auditTables, config);
	} 
	

	public static void main(String[] args) {
		
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setRenderFormatted(true);
		settings.setParamType(ParamType.INLINED);
		settings.setRenderQuotedNames(RenderQuotedNames.EXPLICIT_DEFAULT_QUOTED);
		DSLContext dslContext = DSL.using(SQLDialect.MYSQL, settings);
		
		String database = args.length > 0 ?  args[0] : "aon_audit";
		System.out.println(dslContext.createDatabaseIfNotExists(database).getSQL()+";");
		System.out.printf("use `%s`;%n", database);
		System.out.println(getDDL(dslContext, AonMaster.AON_MASTER).getSQL());
	}

}
