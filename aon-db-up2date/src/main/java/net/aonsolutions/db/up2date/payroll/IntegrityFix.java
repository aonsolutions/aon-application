package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static org.jooq.impl.SQLDataType.CHAR;
import static org.jooq.impl.SQLDataType.INTEGER;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class IntegrityFix implements Update {

	public static final IntegrityFix INTEGRITYFIX = new IntegrityFix();
	
	private IntegrityFix() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		
		dslContext.transaction( (config) -> {
			
			//dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			int updated = 
			dslContext
			.update(CONTRACT)
			.set(CONTRACT.AGREEMENT_LEVEL, DSL.castNull(INTEGER))
			.where(CONTRACT.AGREEMENT_LEVEL.in(DSL.select(AGREEMENT_LEVEL.ID).from(AGREEMENT_LEVEL).where(AGREEMENT_LEVEL.AGREEMENT.notIn(DSL.select(AGREEMENT.ID).from(AGREEMENT)))))
			.execute();
			if ( updated > 0 )
				System.out.printf( "Clean %d contracts with not exist agreement level \r\n", updated);
			
			updated =
			dslContext
			.update(SALARY)
			.set(SALARY.CCC, DSL.castNull(CHAR))
			.where(SALARY.CCC.isNotNull())
			.and(SALARY.CONTRACT.in(DSL.select(CONTRACT.ID).from(CONTRACT).where(CONTRACT.SS_REGIME.eq((byte)3))))
			.execute();
			if ( updated > 0 )
				System.out.printf( "Clean %d salaries of RETA \r\n", updated);
						
			//dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
