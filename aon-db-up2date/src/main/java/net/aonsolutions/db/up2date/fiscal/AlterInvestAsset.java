package net.aonsolutions.db.up2date.fiscal;

import static com.esferalia.aon.jooq.tables.InvestAsset.INVEST_ASSET;
import static org.jooq.impl.SQLDataType.CLOB;
import static org.jooq.impl.SQLDataType.JSON;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;

import net.aonsolutions.db.up2date.Update;

public class AlterInvestAsset implements Update {
	
	public static final AlterInvestAsset ALTER_INVEST_ASSET = new AlterInvestAsset();

	private AlterInvestAsset() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		
		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		Field<JSON> properties = DSL.field(DSL.name("properties"), JSON.nullable(true), DSL.comment("Propiedades de los Bienes Afecto o de Inversion"));
		
		addPropertiesColumn(dslContext, INVEST_ASSET, properties);
		
	}
	
	private void addPropertiesColumn(DSLContext dslContext, TableImpl<?> table, Field<JSON> userField) {
		// Add properties column if not exists
		
		try {
			dslContext.select(userField).from(table).limit(1).fetch();
			
			// Add coment to new properties column
			dslContext.execute("ALTER TABLE invest_asset MODIFY COLUMN properties JSON COMMENT 'Propiedades de los Bienes Afecto o de Inversion'");

			System.out.println("Change properties column from text to Json in InvestAsset");
		} catch ( Exception e ) {
			dslContext.alterTable(table)
				.addColumn(userField)
				.execute();
			
			// Add coment to new properties column
			dslContext.execute("ALTER TABLE invest_asset MODIFY COLUMN properties JSON COMMENT 'Propiedades de los Bienes Afecto o de Inversion'");

			System.out.println("Add properties column to InvestAsset");
		}
	}
}
