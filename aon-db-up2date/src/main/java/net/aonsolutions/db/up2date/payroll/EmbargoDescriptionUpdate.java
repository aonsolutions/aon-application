package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.DeductionConcept.DEDUCTION_CONCEPT;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class EmbargoDescriptionUpdate implements Update {

	public static final EmbargoDescriptionUpdate EMBARGODESCRIPTIONUPDATE = new EmbargoDescriptionUpdate();
	
	private EmbargoDescriptionUpdate() {
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
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext
			.update(DEDUCTION_CONCEPT)
			.set(DEDUCTION_CONCEPT.DESCRIPTION, "IMPORTE")
			.where(DEDUCTION_CONCEPT.DESCRIPTION.like("CONDENA%PREST%ALIMENTICIA"))
			.execute()
			;

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
