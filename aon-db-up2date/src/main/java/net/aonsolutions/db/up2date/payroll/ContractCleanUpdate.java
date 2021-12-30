package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.Certifica2Batch.CERTIFICA2_BATCH;
import static com.esferalia.aon.jooq.tables.Certifica2BatchDetail.CERTIFICA2_BATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractAttach.CONTRACT_ATTACH;
import static com.esferalia.aon.jooq.tables.ContractBatch.CONTRACT_BATCH;
import static com.esferalia.aon.jooq.tables.ContractBatchDetail.CONTRACT_BATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.ContractBonus.CONTRACT_BONUS;
import static com.esferalia.aon.jooq.tables.ContractCalendarEvent.CONTRACT_CALENDAR_EVENT;
import static com.esferalia.aon.jooq.tables.ContractClause.CONTRACT_CLAUSE;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractDeduction.CONTRACT_DEDUCTION;
import static com.esferalia.aon.jooq.tables.ContractEmbargo.CONTRACT_EMBARGO;
import static com.esferalia.aon.jooq.tables.ContractInfo.CONTRACT_INFO;
import static com.esferalia.aon.jooq.tables.ContractLeave.CONTRACT_LEAVE;
import static com.esferalia.aon.jooq.tables.ContractLeaveDetail.CONTRACT_LEAVE_DETAIL;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
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
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class ContractCleanUpdate implements Update {

	public static final ContractCleanUpdate CONTRACTCLEANUPDATE = new ContractCleanUpdate();
	
	private ContractCleanUpdate() {}
	
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
			
			removeTrashContracts(dslContext);

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
			
		});
	}

	private void removeTrashContracts(DSLContext dslContext) {
		
		List<Integer> contractIds = dslContext.select(CONTRACT.ID).from(CONTRACT)
				.where(CONTRACT.ID.lt(0)).fetch(CONTRACT.ID);
		
		// ----------------------------------IRPF-------------------------------------------

		SelectConditionStep<Record1<Integer>> irpfSelect = dslContext
				// formatter:off
				.select(IRPF_DATA.ID).from(IRPF_DATA)
				.where(IRPF_DATA.CONTRACT.in(contractIds));
		// formatter:on

		dslContext.delete(IRPF_DATA_ASCENDANTS)
				.where(IRPF_DATA_ASCENDANTS.IRPF_DATA.in(irpfSelect)).execute();
		dslContext.delete(IRPF_DATA_DESCENDIENTS)
				.where(IRPF_DATA_DESCENDIENTS.IRPF_DATA.in(irpfSelect))
				.execute();

		dslContext.delete(IRPF_REGULARIZATION)
				.where(IRPF_REGULARIZATION.CONTRACT.in(contractIds)).execute();
		dslContext.delete(IRPF_RESULT).where(IRPF_RESULT.CONTRACT.in(contractIds))
				.execute();
		dslContext.delete(IRPF_DATA).where(IRPF_DATA.CONTRACT.in(contractIds))
				.execute();

		// ----------------------------------Salary------------------------------------------

		SelectConditionStep<Record1<Integer>> salariesSelect = dslContext
				// formatter:off
				.select(SALARY.ID).from(SALARY)
				.where(SALARY.CONTRACT.in(contractIds));
		// formatter:on

		dslContext.delete(SALARY_PAYMENT)
				.where(SALARY_PAYMENT.SALARY.in(salariesSelect)).execute();
		dslContext.delete(SALARY_EMBARGO)
				.where(SALARY_EMBARGO.SALARY.in(salariesSelect)).execute();
		dslContext.delete(SALARY_DEDUCTION)
				.where(SALARY_DEDUCTION.SALARY.in(salariesSelect)).execute();
		dslContext.delete(SALARY_DATA).where(SALARY_DATA.SALARY.in(salariesSelect))
				.execute();
		dslContext.delete(SALARY_COST).where(SALARY_COST.SALARY.in(salariesSelect))
				.execute();
		dslContext.delete(SALARY_BONUS)
				.where(SALARY_BONUS.SALARY.in(salariesSelect)).execute();

		dslContext.delete(SALARY).where(SALARY.CONTRACT.in(contractIds)).execute();

		// --------------------------------CERTIFICA2_BATCH------------------------------------

		Result<Record1<Integer>> certifica2DetailSelect = dslContext
				// formatter:off
				.select(CERTIFICA2_BATCH_DETAIL.CERTIFICA2_BATCH)
				.from(CERTIFICA2_BATCH_DETAIL)
				.where(CERTIFICA2_BATCH_DETAIL.CONTRACT.in(contractIds)).fetch();
		// formatter:on

		dslContext.delete(CERTIFICA2_BATCH_DETAIL)
				.where(CERTIFICA2_BATCH_DETAIL.CONTRACT.in(contractIds))
				.execute();
		dslContext.delete(CERTIFICA2_BATCH)
				.where(CERTIFICA2_BATCH_DETAIL.ID.in(certifica2DetailSelect))
				.execute();

		// ----------------------------------LEAVE_BATCH--------------------------------------

		Result<Record1<Integer>> leaveBatchDetail = dslContext
				.select(LEAVE_BATCH_DETAIL.LEAVE_BATCH)
				.from(LEAVE_BATCH_DETAIL
						.join(CONTRACT_LEAVE_DETAIL)
						.on(LEAVE_BATCH_DETAIL.CONTRACT_LEAVE_DETAIL
								.eq(CONTRACT_LEAVE_DETAIL.ID))
						.join(CONTRACT_LEAVE)
						.on(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE.in(contractIds)))
				.fetch();

		dslContext.delete(LEAVE_BATCH_DETAIL)
				.where(LEAVE_BATCH_DETAIL.LEAVE_BATCH.in(leaveBatchDetail))
				.execute();
		dslContext.delete(LEAVE_BATCH).where(LEAVE_BATCH.ID.in(leaveBatchDetail))
				.execute();

		// ----------------------------------Contract------------------------------------------

		dslContext.delete(CONTRACT_ATTACH)
				.where(CONTRACT_ATTACH.CONTRACT.in(contractIds)).execute();

		Result<Record1<Integer>> batchDetailSelect = dslContext
				.select(CONTRACT_BATCH_DETAIL.CONTRACT_BATCH)
				.from(CONTRACT_BATCH_DETAIL)
				.where(CONTRACT_BATCH_DETAIL.CONTRACT.in(contractIds)).fetch();

		dslContext.delete(CONTRACT_BATCH_DETAIL)
				.where(CONTRACT_BATCH_DETAIL.CONTRACT.in(contractIds)).execute();

		dslContext.delete(CONTRACT_BATCH)
				.where(CONTRACT_BATCH.ID.in(batchDetailSelect)).execute();

		dslContext.delete(CONTRACT_BONUS)
				.where(CONTRACT_BONUS.CONTRACT.in(contractIds)).execute();

		dslContext.delete(CONTRACT_CALENDAR_EVENT)
				.where(CONTRACT_CALENDAR_EVENT.CONTRACT.in(contractIds))
				.execute();

		dslContext.delete(CONTRACT_CLAUSE)
				.where(CONTRACT_CLAUSE.CONTRACT.in(contractIds)).execute();

		dslContext.delete(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.in(contractIds)).execute();

		dslContext.delete(CONTRACT_DEDUCTION)
				.where(CONTRACT_DEDUCTION.CONTRACT.in(contractIds)).execute();

		dslContext.delete(CONTRACT_EMBARGO)
				.where(CONTRACT_EMBARGO.CONTRACT.in(contractIds)).execute();

		dslContext.delete(CONTRACT_INFO)
				.where(CONTRACT_INFO.CONTRACT.in(contractIds)).execute();

		SelectConditionStep<Record1<Integer>> leaveDetailSelect = dslContext
				.select(CONTRACT_LEAVE.ID).from(CONTRACT_LEAVE)
				.where(CONTRACT_LEAVE.CONTRACT.in(contractIds));

		dslContext.delete(CONTRACT_LEAVE_DETAIL)
				.where(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE
						.in(leaveDetailSelect)).execute();
		dslContext.delete(CONTRACT_INFO)
				.where(CONTRACT_INFO.CONTRACT.in(contractIds)).execute();

		dslContext.delete(CONTRACT_PAYMENT)
				.where(CONTRACT_PAYMENT.CONTRACT.in(contractIds)).execute();

		dslContext.delete(CONTRACT).where(CONTRACT.ID.in(contractIds))
				.execute();
		
		System.out.println("Contracts deleted = " + contractIds.size());
		
	}
	
}
