package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.ContractCost.CONTRACT_COST;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class ExoneEreFMayComp3707Fix implements Update {

	public static final ExoneEreFMayComp3707Fix EXONEEREFMAYCOMP3707FIX = new ExoneEreFMayComp3707Fix();
	
	private ExoneEreFMayComp3707Fix() {
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
			
			///*epoch:1785238672922,pec:37,quota:07*//*read-only*/_D=(( -( CGC_E + IT_E + IMS_E + FP_E + DESMPL_E + FOGASA_E + MEI_E ) ) * 90.00 / 100.00);_D == 0.00 ? REMOVE() : _D /**/
					
			dslContext
			.update(CONTRACT_COST)
			.set(CONTRACT_COST.EXPRESSION, DSL.replace(CONTRACT_COST.EXPRESSION, "CGC_E + IT_E + IMS_E + FP_E + DESMPL_E + FOGASA_E + MEI_E", "CGC_E + FP_E + DESMPL_E + FOGASA_E"))
			.where(CONTRACT_COST.EXPRESSION.likeRegex(".*pec:37,quota:07.*"))
			.execute(); 

		});
	}

}
