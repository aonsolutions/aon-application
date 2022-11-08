package net.aonsolutions.db.up2date.payroll;

import java.sql.Connection;
import java.sql.SQLException;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

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

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		try {
			dslContext
	//		.createIndex("IDX_SALARY_SOCIAL_SECURITY_NUMBER")
	//		.on(SALARY, SALARY.SOCIAL_SECURITY_NUMBER)
			.execute("CREATE INDEX `IDX_SALARY_SOCIAL_SECURITY_NUMBER` ON `salary` (`social_security_number`) ");
	//		;
		} catch ( DataAccessException  e) {
			
		}
		
	}

}
