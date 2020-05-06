package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.ContractDeduction.CONTRACT_DEDUCTION;
import static com.esferalia.aon.jooq.tables.DeductionConcept.DEDUCTION_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import com.esferalia.aon.jooq.tables.ContractDeduction;

import net.aonsolutions.db.up2date.Update;

public class EmbargarFix implements Update {

	public static final EmbargarFix EMBARGARFIX = new EmbargarFix();
	
	private EmbargarFix() {
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
			.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.EXPRESSION, "def (EMBARGO) {"
					+ " PENDIENTE = ( EMBARGO + EMBARGADO );"
					+ " E=((PENDIENTE > 0) ? MIN(MAX_EMBARGABLE(TOTAL_LIQUIDO), PENDIENTE ) : 0.00);"
					+ " SELF.addVariable('PENDIENTE',PENDIENTE - E);"
					+ " SELF.addVariable('EMBARGADO',-1*(EMBARGADO - E));"
					+ " E;"
					+ " }")
			.where(SYSTEM_DATA.NAME.eq("EMBARGAR"))
			.and(SYSTEM_DATA.DOMAIN.eq(0))
			.execute();
			
			dslContext
			.delete(DEDUCTION_CONCEPT)
			.where(DEDUCTION_CONCEPT.DOMAIN.eq(0))
			.and(DEDUCTION_CONCEPT.TYPE.eq((byte) -1))
			.execute();
			
			
			dslContext.alterTable(DEDUCTION_CONCEPT)
			.alterColumn(DEDUCTION_CONCEPT.DESCRIPTION).set(SQLDataType.VARCHAR.length(128))
			.execute();
			
			dslContext.alterTable(CONTRACT_DEDUCTION)
			.alterColumn(CONTRACT_DEDUCTION.DESCRIPTION).set(SQLDataType.VARCHAR.length(128))
			.execute();

			dslContext.insertInto(DEDUCTION_CONCEPT)
			.set(DEDUCTION_CONCEPT.DOMAIN, 0)
			.set(DEDUCTION_CONCEPT.TYPE, (byte) -1)
			.set(DEDUCTION_CONCEPT.DESCRIPTION, "CONDENA PREST. ALIMENTICIA")
			.newRecord()
			.set(DEDUCTION_CONCEPT.DOMAIN, 0)
			.set(DEDUCTION_CONCEPT.TYPE, (byte) -1)
			.set(DEDUCTION_CONCEPT.EXPRESSION, "EMBARGAR(/*user*/0.00/**/)")
			.set(DEDUCTION_CONCEPT.DESCRIPTION, "PENDIENTE: @{PENDIENTE}\u20AC, EMBARGADO: @{EMBARGADO}\u20AC")
			.execute()
			;

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
