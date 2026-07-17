package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.Alcatraz.ALCATRAZ;
import static com.esferalia.aon.jooq.tables.Certifica2Batch.CERTIFICA2_BATCH;
import static com.esferalia.aon.jooq.tables.Certifica2BatchDetail.CERTIFICA2_BATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractAttach.CONTRACT_ATTACH;
import static com.esferalia.aon.jooq.tables.ContractBatch.CONTRACT_BATCH;
import static com.esferalia.aon.jooq.tables.ContractBatchDetail.CONTRACT_BATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.ContractBonus.CONTRACT_BONUS;
import static com.esferalia.aon.jooq.tables.ContractCalendarEvent.CONTRACT_CALENDAR_EVENT;
import static com.esferalia.aon.jooq.tables.ContractClause.CONTRACT_CLAUSE;
import static com.esferalia.aon.jooq.tables.ContractCost.CONTRACT_COST;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractDeduction.CONTRACT_DEDUCTION;
import static com.esferalia.aon.jooq.tables.ContractDoc.CONTRACT_DOC;
import static com.esferalia.aon.jooq.tables.ContractEmbargo.CONTRACT_EMBARGO;
import static com.esferalia.aon.jooq.tables.ContractExtra.CONTRACT_EXTRA;
import static com.esferalia.aon.jooq.tables.ContractInfo.CONTRACT_INFO;
import static com.esferalia.aon.jooq.tables.ContractLeave.CONTRACT_LEAVE;
import static com.esferalia.aon.jooq.tables.ContractLeaveDetail.CONTRACT_LEAVE_DETAIL;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.jooq.tables.ContrataBatch.CONTRATA_BATCH;
import static com.esferalia.aon.jooq.tables.ContrataBatchDetail.CONTRATA_BATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.IrpfData.IRPF_DATA;
import static com.esferalia.aon.jooq.tables.IrpfDataAscendants.IRPF_DATA_ASCENDANTS;
import static com.esferalia.aon.jooq.tables.IrpfDataDescendients.IRPF_DATA_DESCENDIENTS;
import static com.esferalia.aon.jooq.tables.IrpfRegularization.IRPF_REGULARIZATION;
import static com.esferalia.aon.jooq.tables.IrpfResult.IRPF_RESULT;
import static com.esferalia.aon.jooq.tables.LeaveBatch.LEAVE_BATCH;
import static com.esferalia.aon.jooq.tables.LeaveBatchDetail.LEAVE_BATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryBonus.SALARY_BONUS;
import static com.esferalia.aon.jooq.tables.SalaryCost.SALARY_COST;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SalaryEmbargo.SALARY_EMBARGO;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;

import java.sql.Connection;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class ContractForeignKeysAddIfNotExist implements Update {

	public static final ContractForeignKeysAddIfNotExist CONTRACTFOREIGNKEYSADDIFNOTEXITS = new ContractForeignKeysAddIfNotExist();

	private static final String TABLE = "contract";

	// constraint name, column, referenced table, referenced column - as defined in create.database.sql
	private static final String[][] FOREIGN_KEYS = {
			{ "FK_CONTRACT_AGREEMENT_LEVEL", "agreement_level", "agreement_level", "id" },
			{ "FK_CONTRACT_CALENDAR", "calendar", "calendar", "id" },
			{ "FK_CONTRACT_DOMAIN", "domain", "domain", "id" },
			{ "FK_CONTRACT_ENTERPRISE_ACTIVITY", "enterprise_activity", "enterprise_activity", "id" },
			{ "FK_CONTRACT_ENTERPRISE_CCC", "enterprise_ccc", "enterprise_ccc", "id" },
			{ "FK_CONTRACT_PERSON", "person", "person", "registry" },
			{ "FK_CONTRACT_WORKPLACE", "workplace", "workplace", "id" }
	};

	private ContractForeignKeysAddIfNotExist() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		String database = dslContext.fetchOne("SELECT DATABASE()").getValue(0, String.class);

		dslContext.transaction(config -> {

			DSLContext ctx = DSL.using(config);

			for (String[] foreignKey : FOREIGN_KEYS) {
				String constraint = foreignKey[0];
				String column = foreignKey[1];
				String referencedTable = foreignKey[2];
				String referencedColumn = foreignKey[3];

				if (!checkFK(ctx, constraint, database)) {

					fixViolations(ctx, column, referencedTable, referencedColumn);

					ctx.execute("ALTER TABLE `" + TABLE + "` ADD CONSTRAINT `" + constraint + "` "
							+ "FOREIGN KEY (`" + column + "`) REFERENCES `" + referencedTable + "` (`" + referencedColumn + "`)");
				}
			}
		});
	}

	private boolean checkFK(DSLContext dslContext, String constraint, String database) {
		return dslContext.fetchExists(dslContext.selectOne().from("information_schema.table_constraints")
				.where(DSL.field("table_name").eq(DSL.inline(TABLE)))
				.and(DSL.field("constraint_type").eq(DSL.inline("FOREIGN KEY")))
				.and(DSL.field("constraint_name").eq(DSL.inline(constraint)))
				.and(DSL.field("table_schema").eq(DSL.inline(database))));
	}

	// Finds contracts whose value for `column` no longer points to an existing
	// row in `referencedTable`/`referencedColumn` and either nulls the column out
	// (when it's allowed to be NULL) or removes the contract - and everything that
	// depends on it - so the FK constraint can be added afterwards.
	@SuppressWarnings("unchecked")
	private void fixViolations(DSLContext ctx, String column, String referencedTable, String referencedColumn) {

		Field<Integer> contractColumn = (Field<Integer>) CONTRACT.field(column);
		Field<Integer> refColumn = DSL.field(DSL.name(referencedColumn), Integer.class);

		List<Integer> violatingIds = ctx.select(CONTRACT.ID).from(CONTRACT)
				.where(contractColumn.isNotNull())
				.andNotExists(ctx.selectOne().from(DSL.table(DSL.name(referencedTable)))
						.where(refColumn.eq(contractColumn)))
				.fetch(CONTRACT.ID);

		if (violatingIds.isEmpty())
			return;

		if (contractColumn.getDataType().nullable()) {
			ctx.update(CONTRACT).set(contractColumn, (Integer) null)
					.where(CONTRACT.ID.in(violatingIds)).execute();

			System.out.println("Contracts with orphan " + column + " set to NULL = " + violatingIds.size());
		} else {
			removeContracts(ctx, violatingIds);

			System.out.println("Contracts removed due to orphan " + column + " = " + violatingIds.size());
		}
	}

	// Removes contracts and every row in other tables that depends on them.
	private void removeContracts(DSLContext ctx, List<Integer> contractIds) {

		// ----------------------------------IRPF-------------------------------------------

		SelectConditionStep<Record1<Integer>> irpfSelect = ctx
				// formatter:off
				.select(IRPF_DATA.ID).from(IRPF_DATA)
				.where(IRPF_DATA.CONTRACT.in(contractIds));
		// formatter:on

		ctx.delete(IRPF_DATA_ASCENDANTS).where(IRPF_DATA_ASCENDANTS.IRPF_DATA.in(irpfSelect)).execute();
		ctx.delete(IRPF_DATA_DESCENDIENTS).where(IRPF_DATA_DESCENDIENTS.IRPF_DATA.in(irpfSelect)).execute();

		ctx.delete(IRPF_REGULARIZATION).where(IRPF_REGULARIZATION.CONTRACT.in(contractIds)).execute();
		ctx.delete(IRPF_RESULT).where(IRPF_RESULT.CONTRACT.in(contractIds)).execute();
		ctx.delete(IRPF_DATA).where(IRPF_DATA.CONTRACT.in(contractIds)).execute();

		// ----------------------------------Salary------------------------------------------

		SelectConditionStep<Record1<Integer>> salariesSelect = ctx
				// formatter:off
				.select(SALARY.ID).from(SALARY)
				.where(SALARY.CONTRACT.in(contractIds));
		// formatter:on

		ctx.delete(ALCATRAZ)
				.where(ALCATRAZ.SALARY.in(salariesSelect).or(ALCATRAZ.LIQUIDATION.in(salariesSelect))).execute();

		ctx.delete(SALARY_PAYMENT).where(SALARY_PAYMENT.SALARY.in(salariesSelect)).execute();
		ctx.delete(SALARY_EMBARGO).where(SALARY_EMBARGO.SALARY.in(salariesSelect)).execute();
		ctx.delete(SALARY_DEDUCTION).where(SALARY_DEDUCTION.SALARY.in(salariesSelect)).execute();
		ctx.delete(SALARY_DATA).where(SALARY_DATA.SALARY.in(salariesSelect)).execute();
		ctx.delete(SALARY_COST).where(SALARY_COST.SALARY.in(salariesSelect)).execute();
		ctx.delete(SALARY_BONUS).where(SALARY_BONUS.SALARY.in(salariesSelect)).execute();

		ctx.delete(SALARY).where(SALARY.CONTRACT.in(contractIds)).execute();

		// --------------------------------CERTIFICA2_BATCH------------------------------------

		Result<Record1<Integer>> certifica2BatchSelect = ctx
				// formatter:off
				.select(CERTIFICA2_BATCH_DETAIL.CERTIFICA2_BATCH)
				.from(CERTIFICA2_BATCH_DETAIL)
				.where(CERTIFICA2_BATCH_DETAIL.CONTRACT.in(contractIds)).fetch();
		// formatter:on

		ctx.delete(CERTIFICA2_BATCH_DETAIL).where(CERTIFICA2_BATCH_DETAIL.CONTRACT.in(contractIds)).execute();
		ctx.delete(CERTIFICA2_BATCH).where(CERTIFICA2_BATCH.ID.in(certifica2BatchSelect)).execute();

		// --------------------------------CONTRATA_BATCH------------------------------------

		Result<Record1<Integer>> contrataBatchSelect = ctx
				.select(CONTRATA_BATCH_DETAIL.CONTRATA_BATCH).from(CONTRATA_BATCH_DETAIL)
				.where(CONTRATA_BATCH_DETAIL.CONTRACT.in(contractIds)).fetch();

		ctx.delete(CONTRATA_BATCH_DETAIL).where(CONTRATA_BATCH_DETAIL.CONTRACT.in(contractIds)).execute();
		ctx.delete(CONTRATA_BATCH).where(CONTRATA_BATCH.ID.in(contrataBatchSelect)).execute();

		// ----------------------------------LEAVE_BATCH--------------------------------------

		SelectConditionStep<Record1<Integer>> contractLeaveSelect = ctx
				.select(CONTRACT_LEAVE.ID).from(CONTRACT_LEAVE)
				.where(CONTRACT_LEAVE.CONTRACT.in(contractIds));

		Result<Record1<Integer>> leaveBatchSelect = ctx
				.select(LEAVE_BATCH_DETAIL.LEAVE_BATCH)
				.from(LEAVE_BATCH_DETAIL
						.join(CONTRACT_LEAVE_DETAIL)
						.on(LEAVE_BATCH_DETAIL.CONTRACT_LEAVE_DETAIL.eq(CONTRACT_LEAVE_DETAIL.ID)))
				.where(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE.in(contractLeaveSelect))
				.fetch();

		ctx.delete(LEAVE_BATCH_DETAIL).where(LEAVE_BATCH_DETAIL.LEAVE_BATCH.in(leaveBatchSelect)).execute();
		ctx.delete(LEAVE_BATCH).where(LEAVE_BATCH.ID.in(leaveBatchSelect)).execute();

		ctx.delete(CONTRACT_LEAVE_DETAIL).where(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE.in(contractLeaveSelect))
				.execute();

		// ----------------------------------Contract------------------------------------------

		ctx.delete(CONTRACT_ATTACH).where(CONTRACT_ATTACH.CONTRACT.in(contractIds)).execute();

		Result<Record1<Integer>> contractBatchSelect = ctx
				.select(CONTRACT_BATCH_DETAIL.CONTRACT_BATCH).from(CONTRACT_BATCH_DETAIL)
				.where(CONTRACT_BATCH_DETAIL.CONTRACT.in(contractIds)).fetch();

		ctx.delete(CONTRACT_BATCH_DETAIL).where(CONTRACT_BATCH_DETAIL.CONTRACT.in(contractIds)).execute();
		ctx.delete(CONTRACT_BATCH).where(CONTRACT_BATCH.ID.in(contractBatchSelect)).execute();

		ctx.delete(CONTRACT_BONUS).where(CONTRACT_BONUS.CONTRACT.in(contractIds)).execute();
		ctx.delete(CONTRACT_CALENDAR_EVENT).where(CONTRACT_CALENDAR_EVENT.CONTRACT.in(contractIds)).execute();
		ctx.delete(CONTRACT_CLAUSE).where(CONTRACT_CLAUSE.CONTRACT.in(contractIds)).execute();
		ctx.delete(CONTRACT_COST).where(CONTRACT_COST.CONTRACT.in(contractIds)).execute();
		ctx.delete(CONTRACT_DATA).where(CONTRACT_DATA.CONTRACT.in(contractIds)).execute();
		ctx.delete(CONTRACT_DEDUCTION).where(CONTRACT_DEDUCTION.CONTRACT.in(contractIds)).execute();
		ctx.delete(CONTRACT_DOC).where(CONTRACT_DOC.CONTRACT.in(contractIds)).execute();

		SelectConditionStep<Record1<Integer>> contractEmbargoSelect = ctx
				.select(CONTRACT_EMBARGO.ID).from(CONTRACT_EMBARGO)
				.where(CONTRACT_EMBARGO.CONTRACT.in(contractIds));

		ctx.update(SALARY_EMBARGO).set(SALARY_EMBARGO.CONTRACT_EMBARGO, (Integer) null)
				.where(SALARY_EMBARGO.CONTRACT_EMBARGO.in(contractEmbargoSelect)).execute();

		ctx.delete(CONTRACT_EMBARGO).where(CONTRACT_EMBARGO.CONTRACT.in(contractIds)).execute();
		ctx.delete(CONTRACT_EXTRA).where(CONTRACT_EXTRA.CONTRACT.in(contractIds)).execute();
		ctx.delete(CONTRACT_INFO).where(CONTRACT_INFO.CONTRACT.in(contractIds)).execute();
		ctx.delete(CONTRACT_PAYMENT).where(CONTRACT_PAYMENT.CONTRACT.in(contractIds)).execute();

		// break self-referencing CONTRACT_LEAVE.PARENT links before deleting, since
		// FOREIGN_KEY_CHECKS stays on and a single DELETE can't rely on row order
		ctx.update(CONTRACT_LEAVE)
		.set(CONTRACT_LEAVE.PARENT, (Integer) null)
		.where(CONTRACT_LEAVE.CONTRACT.in(contractIds).and(CONTRACT_LEAVE.PARENT.isNotNull())).execute();

		ctx.delete(CONTRACT_LEAVE).where(CONTRACT_LEAVE.CONTRACT.in(contractIds)).execute();

		ctx.delete(CONTRACT).where(CONTRACT.ID.in(contractIds)).execute();
	}

}
