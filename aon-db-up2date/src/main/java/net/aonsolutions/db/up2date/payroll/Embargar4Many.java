package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.ContractEmbargo.CONTRACT_EMBARGO;
import static com.esferalia.aon.jooq.tables.DeductionConcept.DEDUCTION_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import net.aonsolutions.db.up2date.Update;

public class Embargar4Many implements Update {

	public static final Embargar4Many BIEMBARGAR4MANY = new Embargar4Many();
	
	private Embargar4Many() {
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
			
			dslContext
			.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.EXPRESSION, "def (EMBARGO, EMBARGABLE) {"
					+ " EMBARGABLE=MAX(EMBARGABLE, 0.00);"
					+ " PENDIENTE = ( EMBARGO + EMBARGADO );"
					+ " E=((PENDIENTE > 0) ? MIN(EMBARGABLE, PENDIENTE ) : 0.00);"
					+ " SELF.addVariable('PENDIENTE',PENDIENTE - E);"
					+ " SELF.addVariable('EMBARGADO',-1*(EMBARGADO - E));"
					+ " E;"
					+ " }")
			.where(SYSTEM_DATA.NAME.eq("EMBARGAR"))
			.and(SYSTEM_DATA.DOMAIN.eq(0))
			.execute();
			
			dslContext
			.update(CONTRACT_EMBARGO)
			.set(CONTRACT_EMBARGO.EXPRESSION, 
			DSL.regexpReplaceAll(CONTRACT_EMBARGO.EXPRESSION, 
				"MAX_EMBARGABLE\\s*\\(\\s*[^\\)]*\\s*\\)", 
				"$0-TOTAL_EMBARGADO"
				))
			.where(CONTRACT_EMBARGO.EXPRESSION.notContains("TOTAL_EMBARGADO"))
			.execute();
			
			dslContext
			.update(DEDUCTION_CONCEPT)
			.set(DEDUCTION_CONCEPT.EXPRESSION, 
				DSL.regexpReplaceAll(DEDUCTION_CONCEPT.EXPRESSION, 
					"MAX_EMBARGABLE\\s*\\(\\s*[^\\)]*\\s*\\)", 
					"$0-TOTAL_EMBARGADO"
					))
			.where(DEDUCTION_CONCEPT.EXPRESSION.notContains("TOTAL_EMBARGADO"))
			.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}
}
