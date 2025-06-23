package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.DeductionConcept.DEDUCTION_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemCost.SYSTEM_COST;
import static com.esferalia.aon.jooq.tables.SystemDeduction.SYSTEM_DEDUCTION;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class TrainingExcessDeductionsNamesFix implements Update {

	private static final int DOMAIN = -101;

	public static final TrainingExcessDeductionsNamesFix TRAININGEXCESSDEDUCTIONSNAMESFIX = new TrainingExcessDeductionsNamesFix();

	private TrainingExcessDeductionsNamesFix() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings ;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);



		boolean	upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_COST)
			.where(SYSTEM_COST.DOMAIN.eq(DOMAIN))
			.and(SYSTEM_COST.CODE.like("EXCESS_%"))
			.and(SYSTEM_COST.EXPRESSION.contains("BASE_EXCESO"))) > 0;


		if ( upgraded )
			return;

		dslContext.transaction( config -> {

			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			// Deductions
			// Cgc
			int excessCgcConcept =
			dslContext.insertInto(DEDUCTION_CONCEPT)
			.set(DEDUCTION_CONCEPT.DOMAIN, DOMAIN)
			.set(DEDUCTION_CONCEPT.TYPE, (byte) 0 )
			.set(DEDUCTION_CONCEPT.CODE, "EXCESS_CGC")
			.set(DEDUCTION_CONCEPT.DESCRIPTION, "Contingencias Comunes Cotización por Exceso" )
			.returning().fetchOne().getId();

			dslContext.update(SYSTEM_DEDUCTION
			.join(DEDUCTION_CONCEPT).on(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT.eq(DEDUCTION_CONCEPT.ID)))
			.set(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT,excessCgcConcept)
			.where(SYSTEM_DEDUCTION.DOMAIN.eq(DOMAIN))
			.and(DEDUCTION_CONCEPT.CODE.eq("CGC"))
			.and(SYSTEM_DEDUCTION.EXPRESSION.contains("BASE_EXCESO"))
			.execute();
			
			// Fp
			int excessFpConcept =
			dslContext.insertInto(DEDUCTION_CONCEPT)
			.set(DEDUCTION_CONCEPT.DOMAIN, DOMAIN)
			.set(DEDUCTION_CONCEPT.TYPE, (byte) 3 )
			.set(DEDUCTION_CONCEPT.CODE, "EXCESS_FP")
			.set(DEDUCTION_CONCEPT.DESCRIPTION, "Formación Profesional Cotización por Exceso" )
			.returning().fetchOne().getId();
			
			dslContext.update(SYSTEM_DEDUCTION
			.join(DEDUCTION_CONCEPT).on(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT.eq(DEDUCTION_CONCEPT.ID)))
			.set(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT,excessFpConcept)
			.where(SYSTEM_DEDUCTION.DOMAIN.eq(DOMAIN))
			.and(DEDUCTION_CONCEPT.CODE.eq("FP"))
			.and(SYSTEM_DEDUCTION.EXPRESSION.contains("BASE_EXCESO"))
			.execute();
			
			// Desmpl
			int excessDesmplConcept =
			dslContext.insertInto(DEDUCTION_CONCEPT)
			.set(DEDUCTION_CONCEPT.DOMAIN, DOMAIN)
			.set(DEDUCTION_CONCEPT.TYPE, (byte) 2 )
			.set(DEDUCTION_CONCEPT.CODE, "EXCESS_DESMPL")
			.set(DEDUCTION_CONCEPT.DESCRIPTION, "Desempleo Cotización por Exceso" )
			.returning().fetchOne().getId();
			
			dslContext.update(SYSTEM_DEDUCTION
			.join(DEDUCTION_CONCEPT).on(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT.eq(DEDUCTION_CONCEPT.ID)))
			.set(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT,excessDesmplConcept)
			.where(SYSTEM_DEDUCTION.DOMAIN.eq(DOMAIN))
			.and(DEDUCTION_CONCEPT.CODE.eq("DESMPL"))
			.and(SYSTEM_DEDUCTION.EXPRESSION.contains("BASE_EXCESO"))
			.execute();
			
			
			// Costs
			dslContext.update(SYSTEM_COST)
				.set(SYSTEM_COST.CODE, DSL.concat("EXCESS_", SYSTEM_COST.CODE))
				.where(SYSTEM_COST.DOMAIN.eq(DOMAIN))
				.and(SYSTEM_COST.EXPRESSION.contains("BASE_EXCESO"))
				.execute();
			

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
