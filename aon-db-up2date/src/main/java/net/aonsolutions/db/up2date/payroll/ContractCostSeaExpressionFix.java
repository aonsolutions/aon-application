package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.ContractCost.CONTRACT_COST;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class ContractCostSeaExpressionFix implements Update {

	public static final ContractCostSeaExpressionFix CONTRACT_COST_SEA_EXPRESSION_FIX = new ContractCostSeaExpressionFix();

	// Triple-paren ROUND(((SEA_21= is unique to old expression; new has ROUND((SEA_21=
	private static final String OLD_FINGERPRINT = "ROUND(((SEA_21=7.36";

	// Matches /*read-only*/SEA = ... /**/ regardless of the variable 163.84 value
	private static final String OLD_BODY_PATTERN = "/\\*read-only\\*/SEA = .*?/\\*\\*/";

	private static final String NEW_BODY = "/*read-only*/"
			+ "SEA = ("
				+ "BASE_CGC_E * "
				+ "( SELF.isDef(\"DIAS_TRABAJADOS\") ? "
				+ "ROUND("
				+ "(SEA_21=7.36 * ( 1 + ( (BASE_CGC_E / (SELF.isDef(\"JORNADAS_REALES\") ? JORNADAS_REALES_TOTALES : 1) ) - (SELF.isDef(\"JORNADAS_REALES\") ? 42.90 : 986.7 / DIAS_MES * DIAS_NOMINA) )/ (BASE_CGC_E / (SELF.isDef(\"JORNADAS_REALES\") ? JORNADAS_REALES_TOTALES : 1) ) * 2.52 * 6.15 / 7.36 )) + (8.1 - SEA_21) / 10 * (AÑO(INICIO_NOMINA) - 2021)"
				+ ",2) "
				+ ": 18.21 ) / 100.0"
			+ ");"
			+ "( !SELF.isDef(\"DIAS_TRABAJADOS\") || (CGC_E - SEA) > L=(SELF.isDef(\"JORNADAS_REALES\") ? CGC_E_MIN_DIA *JORNADAS_REALES_TOTALES : (CGC_E_MIN_MES / DIAS_MES * DIAS_NOMINA) )) ? -1 * SEA : -1 * MAX(CGC_E - L,0)/**/" 
			;
	private ContractCostSeaExpressionFix() {}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

		boolean upgraded = dslContext.fetchCount(
			dslContext.select()
			.from(CONTRACT_COST)
			.where(CONTRACT_COST.EXPRESSION.contains(OLD_FINGERPRINT))
		) == 0;

		if (upgraded)
			return;

		dslContext.transaction(config -> {

			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");

			dslContext
			.update(CONTRACT_COST)
			.set(CONTRACT_COST.EXPRESSION,
				DSL.regexpReplaceFirst(CONTRACT_COST.EXPRESSION, OLD_BODY_PATTERN, NEW_BODY))
			.where(CONTRACT_COST.EXPRESSION.contains(OLD_FINGERPRINT))
			.execute();

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
