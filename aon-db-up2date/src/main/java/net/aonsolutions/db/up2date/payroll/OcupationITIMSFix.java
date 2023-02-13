package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractEmbargo.CONTRACT_EMBARGO;
import static com.esferalia.aon.jooq.tables.DeductionConcept.DEDUCTION_CONCEPT;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import com.esferalia.aon.jooq.tables.ContractData;
import com.esferalia.aon.jooq.tables.ContractEmbargo;

import net.aonsolutions.db.up2date.Update;

public class OcupationITIMSFix implements Update {

	public static final OcupationITIMSFix OCUPATIONITIMSFIX = new OcupationITIMSFix();
	
	private OcupationITIMSFix() {
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
			.update(CONTRACT_DATA)
			.set(CONTRACT_DATA.NAME, "PORCENTAJE_IT")
			.where(CONTRACT_DATA.NAME.eq("TARIFA_IT"))
			.execute();
			
			dslContext
			.update(CONTRACT_DATA)
			.set(CONTRACT_DATA.NAME, "PORCENTAJE_IMS")
			.where(CONTRACT_DATA.NAME.eq("TARIFA_IMS"))
			.execute();
			
			dslContext
			.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.NAME.eq("OCUPACION"))
			.and(CONTRACT_DATA.EXPRESSION.eq("\"n\""))
			.execute();

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
