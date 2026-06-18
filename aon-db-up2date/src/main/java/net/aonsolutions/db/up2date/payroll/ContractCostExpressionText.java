package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.ContractCost.CONTRACT_COST;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class ContractCostExpressionText implements Update {

	public static final ContractCostExpressionText CONTRACT_COST_EXPRESSION_TEXT = new ContractCostExpressionText();

	private ContractCostExpressionText() {}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

		boolean alreadyText = dslContext.select()
			.from("information_schema.columns")
			.where(DSL.field("table_schema").eq(DSL.currentSchema()))
			.and(DSL.field("table_name").eq(DSL.inline(CONTRACT_COST.getName())))
			.and(DSL.field("column_name").eq(DSL.inline("expression")))
			.and(DSL.field("data_type").eq(DSL.inline("text")))
			.fetchOptional()
			.isPresent();

		if (alreadyText) {
			System.out.println("\tAlterContractCostExpressionText. expression already is text.");
			return;
		}

		dslContext.execute("ALTER TABLE `contract_cost` MODIFY `expression` text COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Formula'");
		System.out.println("\tAlterContractCostExpressionText. expression UPDATED to text.");
	}

}
