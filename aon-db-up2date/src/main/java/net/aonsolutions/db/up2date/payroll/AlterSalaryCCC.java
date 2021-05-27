package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static org.jooq.impl.SQLDataType.VARCHAR;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import net.aonsolutions.db.up2date.Update;

public class AlterSalaryCCC implements Update {

	public static final AlterSalaryCCC ALTER_SALARY_CCC = new AlterSalaryCCC();
	
	private AlterSalaryCCC() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		dslContext
		.alterTable(SALARY)
		.alterColumn(SALARY.CCC)
		.set(VARCHAR.length(15))
		.execute()
		;
	}

}
