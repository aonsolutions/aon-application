package net.aonsolutions.db.up2date.payroll;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class AlterSalaryDeduction implements Update {

	public static final AlterSalaryDeduction ALTERSALARYDEDUCTION = new AlterSalaryDeduction();
	
	private AlterSalaryDeduction() {}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		
		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		dslContext.execute("ALTER TABLE `salary_deduction` MODIFY `deduction_concept` varchar(25) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo del concepto'");
	}

}
