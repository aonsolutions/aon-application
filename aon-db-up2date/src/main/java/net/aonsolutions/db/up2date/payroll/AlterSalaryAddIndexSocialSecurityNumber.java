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

public class AlterSalaryAddIndexSocialSecurityNumber implements Update {

	public static final AlterSalaryAddIndexSocialSecurityNumber ALTER_SALARY_ADD_INDEX_SOCIALSECURITYNUMBER = new AlterSalaryAddIndexSocialSecurityNumber();
	
	private AlterSalaryAddIndexSocialSecurityNumber() {
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
		.createIndexIfNotExists("IDX_SALARY_SOCIAL_SECURITY_NUMBER")
		.on(SALARY, SALARY.SOCIAL_SECURITY_NUMBER)
		.execute()
		;
	}

}
