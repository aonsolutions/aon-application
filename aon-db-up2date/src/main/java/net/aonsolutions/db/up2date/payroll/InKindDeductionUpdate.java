package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.SystemDeduction.SYSTEM_DEDUCTION;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class InKindDeductionUpdate implements Update {

	public static final InKindDeductionUpdate INKIND_DEDUCTION_UPDATE= new InKindDeductionUpdate();
	
	private InKindDeductionUpdate() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");

			dslContext.update(SYSTEM_DEDUCTION)
			.set(SYSTEM_DEDUCTION.EXPRESSION, "/*read-only*/ isdef _EN_ESPECIE ? SUM(_EN_ESPECIE) : HIDE() /**/")
			.where(SYSTEM_DEDUCTION.EXPRESSION.eq("/*read-only*/ isdef _EN_ESPECIE ? _EN_ESPECIE : HIDE() /**/"))
			.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
