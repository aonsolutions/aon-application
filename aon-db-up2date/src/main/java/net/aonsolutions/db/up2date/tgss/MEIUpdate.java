package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.ContractCost.CONTRACT_COST;
import static com.esferalia.aon.jooq.tables.DeductionConcept.DEDUCTION_CONCEPT;
import static com.esferalia.aon.jooq.tables.SalaryCost.SALARY_COST;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SystemCost.SYSTEM_COST;
import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;
import static com.esferalia.aon.jooq.tables.SystemDeduction.SYSTEM_DEDUCTION;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.ContractCost;
import com.esferalia.aon.jooq.tables.SalaryCost;
import com.esferalia.aon.jooq.tables.SalaryDeduction;
import com.esferalia.aon.jooq.tables.records.DeductionConceptRecord;

import net.aonsolutions.db.up2date.Update;

public class MEIUpdate implements Update {
	
	public static final MEIUpdate MEIUPDATE = new MEIUpdate();

	private static final String MEI = "MEI";
	private static final String MEI_E = "MEI_E";

	private MEIUpdate() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		// Establish context
		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		boolean upgraded = 
		dslContext.fetchCount(
			dslContext.select()
			.from(DEDUCTION_CONCEPT)
			.where(DEDUCTION_CONCEPT.CODE.eq(MEI))
			.and(DEDUCTION_CONCEPT.TYPE.eq((byte) 13))
			) >= 1;

		// IF ALREADY EXISTS
				
		if ( upgraded ) 
			return;
		
		// DISABLED FOREING_KEY FOR INSERT
		
		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			

			dslContext.update(DEDUCTION_CONCEPT)
			.set(DEDUCTION_CONCEPT.TYPE,(byte)13) //MEI
			.where(DEDUCTION_CONCEPT.CODE.eq(MEI))
			.execute();

			dslContext.update(SALARY_DEDUCTION)
			.set(SALARY_DEDUCTION.TYPE, (byte) 13) // MEI
			.where(SALARY_DEDUCTION.DEDUCTION_CONCEPT.eq(MEI))
			.execute()
			;

			dslContext.update(SYSTEM_COST)
			.set(SYSTEM_COST.TYPE, (byte) 13) // MEI
			.where(SYSTEM_COST.CODE.eq(MEI_E))
			.execute()
			;
			
			dslContext.update(CONTRACT_COST)
			.set(CONTRACT_COST.TYPE, (byte) 13) // MEI
			.where(CONTRACT_COST.CODE.eq(MEI_E))
			.execute()
			;

			dslContext.update(SALARY_COST)
			.set(SALARY_COST.TYPE, (byte) 13) // MEI
			.where(SALARY_COST.COST_CONCEPT.eq(MEI_E))
			.execute()
			;
			

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
