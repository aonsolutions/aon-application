package solutions.aon.audit;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.CreateIndexIncludeStep;
import org.jooq.DDLExportConfiguration;
import org.jooq.DDLFlag;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Name;
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

	static AuditTable<?>[] getAuditTables4(Schema schema) {
		return schema.getTables().stream()
				.map(AuditTable::new)
				.toArray(AuditTable<?>[]::new);
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
	
	
	static Stream<CreateIndexIncludeStep> getUniqueIndexes(DSLContext dslContext, Schema schema, Field<?> field) {
		AuditTable<?>[] auditTables = getAuditTables4(schema);
		return Arrays.stream(auditTables)
		.map(table -> dslContext.createUniqueIndex( String.format("IDX_%s_%s", table.getName().toUpperCase(), field.getName().toUpperCase()) ).on(table.getDelegate(), AuditTable.AuditFields.AUDIT_MD5))
		//.toArray(CreateIndexIncludeStep[]::new)
		;
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
		System.out.println(getUniqueIndexes(dslContext, AonMaster.AON_MASTER, AuditTable.AuditFields.AUDIT_MD5).map(CreateIndexIncludeStep::getSQL).collect(Collectors.joining(";\n")));
	}

}
