package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.Alcatraz.ALCATRAZ;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.Name;
import org.jooq.Nullability;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import net.aonsolutions.db.up2date.Update;

public class AlterAlcatraz4Liquidation implements Update {

	private static final Name LIQUIDATION = DSL.name("liquidation");

	public static final AlterAlcatraz4Liquidation ALTER_ALCATRAZ_4_LIQUIDATION = new AlterAlcatraz4Liquidation();
	

	private AlterAlcatraz4Liquidation() {}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		
		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		// Check if the alcatraz table already has the liquidation field
		try {
			dslContext
				.select(DSL.field(LIQUIDATION))
				.from(ALCATRAZ)
				.limit(1)
				.execute();
		} catch (Exception e) {
			// If it fails, it means the field does not exist, so we can proceed to add it
			// Otherwise, we assume the field already exists and do nothing
			dslContext
			.alterTable(ALCATRAZ)
			.addColumn(
					DSL.field(
					LIQUIDATION, 
					SQLDataType.INTEGER.nullable(true).defaultValue((Integer)null),
					DSL.comment("Id de la liquidacion y/o calculo de la TGSS")
					))
			.execute();

			dslContext
			.createIndex(DSL.name("IDX_ALCATRAZ_LIQUIDATION")).on(ALCATRAZ.getQualifiedName(), LIQUIDATION)
			.execute();

			dslContext
			.alterTable(ALCATRAZ)
			.add(DSL.constraint(DSL.name("FK_ALCATRAZ_LIQUIDATION"))
					.foreignKey(DSL.field(LIQUIDATION))
					.references(SALARY.getQualifiedName(), SALARY.ID.getQualifiedName()) )
			.execute();

		}
		
	}

}
