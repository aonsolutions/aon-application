package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.DeductionConcept.DEDUCTION_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemCost.SYSTEM_COST;
import static com.esferalia.aon.jooq.tables.SystemDeduction.SYSTEM_DEDUCTION;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class TrainingQuote2023Fix implements Update {

	private static final int DOMAIN = -101;

	private static final String BASE_CGP_MIN = "BASE_CGP_MIN";

	public static final TrainingQuote2023Fix TRAINNINGPERCENTAGES2023UPDATE = new TrainingQuote2023Fix();

	private TrainingQuote2023Fix() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings ;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		dslContext.transaction( config -> {

			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			// ---------------------------------------------------------
			// CGC & MEI
			
			SelectConditionStep<Record1<Integer>> cgcAndMeiConcept = 
			DSL.select(DEDUCTION_CONCEPT.ID)
			.from(DEDUCTION_CONCEPT)
			.where(DEDUCTION_CONCEPT.DOMAIN.eq(0))
			.and(DEDUCTION_CONCEPT.CODE.in("CGC", "MEI"));
			
			// Fix  CGC & MEI TOTAL_DEVENGADO
			dslContext.update(SYSTEM_DEDUCTION)
			.set(SYSTEM_DEDUCTION.EXPRESSION, DSL.replace(SYSTEM_DEDUCTION.EXPRESSION, "TOTAL_DEVENGADO", "TOTAL_BASE_CGC"))
			.where(SYSTEM_DEDUCTION.DOMAIN.eq(DOMAIN))
			.and(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT.in(cgcAndMeiConcept))
			.and(SYSTEM_DEDUCTION.EXPRESSION.like("%TOTAL_DEVENGADO%"))
			.execute();

			// Fix  CGC & MEI constant
			dslContext.update(SYSTEM_DEDUCTION)
			.set(SYSTEM_DEDUCTION.EXPRESSION, DSL.concat(DSL.concat("TOTAL_BASE_CGC > 0.00 ? " , SYSTEM_DEDUCTION.EXPRESSION ) , " : 0.00"))
			.where(SYSTEM_DEDUCTION.DOMAIN.eq(DOMAIN))
			.and(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT.in(cgcAndMeiConcept))
			.and(SYSTEM_DEDUCTION.EXPRESSION.notLike("%BASE_%"))
			.execute();

			// Fix  CGC_E & MEI_E constant
			dslContext.update(SYSTEM_COST)
			.set(SYSTEM_COST.EXPRESSION, DSL.concat(DSL.concat("TOTAL_BASE_CGC_E > 0.00 ? " , SYSTEM_COST.EXPRESSION ) , " : 0.00"))
			.where(SYSTEM_COST.DOMAIN.eq(DOMAIN))
			.and(SYSTEM_COST.CODE.in("CGC_E", "MEI_E"))
			.and(SYSTEM_COST.EXPRESSION.notLike("%BASE_%"))
			.execute();

			// ---------------------------------------------------------
			// ALL OTHERS

			// Fix  OTHERS TOTAL_DEVENGADO
			dslContext.update(SYSTEM_DEDUCTION)
			.set(SYSTEM_DEDUCTION.EXPRESSION, DSL.replace(SYSTEM_DEDUCTION.EXPRESSION, "TOTAL_DEVENGADO", "TOTAL_BASE_CGP"))
			.where(SYSTEM_DEDUCTION.DOMAIN.eq(DOMAIN))
			.and(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT.notIn(cgcAndMeiConcept))
			.and(SYSTEM_DEDUCTION.EXPRESSION.like("%TOTAL_DEVENGADO%"))
			.execute();
			
			// Fix  OTHERS constant
			dslContext.update(SYSTEM_DEDUCTION)
			.set(SYSTEM_DEDUCTION.EXPRESSION, DSL.concat(DSL.concat("TOTAL_BASE_CGP > 0.00 ? " , SYSTEM_DEDUCTION.EXPRESSION ) , " : 0.00"))
			.where(SYSTEM_DEDUCTION.DOMAIN.eq(DOMAIN))
			.and(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT.notIn(cgcAndMeiConcept))
			.and(SYSTEM_DEDUCTION.EXPRESSION.notLike("%BASE_%"))
			.execute();

			dslContext.update(SYSTEM_COST)
			.set(SYSTEM_COST.EXPRESSION, DSL.concat(DSL.concat("TOTAL_BASE_CGP_E > 0.00 ? " , SYSTEM_COST.EXPRESSION ) , " : 0.00"))
			.where(SYSTEM_COST.DOMAIN.eq(DOMAIN))
			.and(SYSTEM_COST.CODE.notIn("CGC_E", "MEI_E"))
			.and(SYSTEM_COST.EXPRESSION.notLike("%BASE_%"))
			.execute();

			// Fix  BASE_CGP_MIN
			dslContext.update(SYSTEM_DEDUCTION)
			.set(SYSTEM_DEDUCTION.EXPRESSION, DSL.concat(DSL.concat("TOTAL_BASE_CGP > 0.00 ? " , SYSTEM_DEDUCTION.EXPRESSION ) , " : 0.00"))
			.where(SYSTEM_DEDUCTION.DOMAIN.eq(DOMAIN))
			.and(SYSTEM_DEDUCTION.EXPRESSION.like("%BASE_CGP_MIN%"))
			.execute();
			
			dslContext.update(SYSTEM_COST)
			.set(SYSTEM_COST.EXPRESSION, DSL.concat(DSL.concat("TOTAL_BASE_CGP_E > 0.00 ? " , SYSTEM_COST.EXPRESSION ) , " : 0.00"))
			.where(SYSTEM_COST.DOMAIN.eq(DOMAIN))
			.and(SYSTEM_COST.EXPRESSION.like("%BASE_CGP_MIN%"))
			.execute();

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
