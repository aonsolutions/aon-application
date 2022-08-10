package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractAttach.CONTRACT_ATTACH;
import static com.esferalia.aon.jooq.tables.ContractBonus.CONTRACT_BONUS;
import static com.esferalia.aon.jooq.tables.ContractCalendarEvent.CONTRACT_CALENDAR_EVENT;
import static com.esferalia.aon.jooq.tables.ContractClause.CONTRACT_CLAUSE;
import static com.esferalia.aon.jooq.tables.ContractCost.CONTRACT_COST;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractDeduction.CONTRACT_DEDUCTION;
import static com.esferalia.aon.jooq.tables.ContractEmbargo.CONTRACT_EMBARGO;
import static com.esferalia.aon.jooq.tables.ContractInfo.CONTRACT_INFO;
import static com.esferalia.aon.jooq.tables.ContractLeave.CONTRACT_LEAVE;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.jooq.tables.Certifica2BatchDetail.CERTIFICA2_BATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.IrpfData.IRPF_DATA;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.ContractRecord;

import net.aonsolutions.db.up2date.Update;

public class ContractTransfromUnify implements Update {
	
	public static final ContractTransfromUnify CONTRACTTRANSFORMUNIFY = new ContractTransfromUnify();

	private static SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMdd");
	
	private static final String SET_FOREIGN_KEY_CHECKS_0 = "SET FOREIGN_KEY_CHECKS=0;";
	private static final String SET_FOREIGN_KEY_CHECKS_1 = "SET FOREIGN_KEY_CHECKS=1;";
	private static final String ORIGINAL_START_DATE = "ORIGINAL_START_DATE";
	private static final String ORIGINAL_END_DATE = "ORIGINAL_END_DATE";
	private static final String TRANSFORM_DATE = "TRANSFORM_DATE";
	
	private ContractTransfromUnify() {}
	
	@Override
	public void upgrade(Connection connection) {
		
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		// Establish context
		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		unifyContractTransforms(dslContext);
	}

	private void unifyContractTransforms(DSLContext dslContext) {
		Result<Record> contractTransforms = dslContext.select().from(CONTRACT)
				.innerJoin(CONTRACT_DATA)
				.on(CONTRACT_DATA.CONTRACT.eq(CONTRACT.ID))
				.where(CONTRACT_DATA.NAME.eq(ORIGINAL_END_DATE))
				.fetch();
		
		contractTransforms.forEach(contractTransform -> dslContext.transaction(t -> joinOriginalContractAndTransform(dslContext, contractTransform)));
	}
	
	private void joinOriginalContractAndTransform(DSLContext dslContext, Record contractTransform) {
		try {
			Integer personId = contractTransform.get(CONTRACT.PERSON);
			Date oldEndDate = formatDate.parse(contractTransform.get(CONTRACT_DATA.EXPRESSION));
			
			ContractRecord originalContract = dslContext.selectFrom(CONTRACT)
				.where(CONTRACT.PERSON.eq(personId))
				.and(CONTRACT.END_DATE.eq(parseSQLDate(oldEndDate)))
				.fetchOne();
		
			// If not exist original contract
			if(null == originalContract) return;
			
			// Update all original contract endDates
			closeContractEndDate(dslContext, originalContract.getId(), originalContract.getEndDate());
			
			// Update contract endDate
			updateContractEndDate(dslContext, originalContract.getId(), contractTransform.get(CONTRACT.END_DATE));
			
			// Copy contractTransform info to originalContract
			copyContractTransformData(dslContext, originalContract.getId(), contractTransform.get(CONTRACT.ID));
			
			// Create transform variables
			createTransformVariables(dslContext, originalContract, contractTransform.get(CONTRACT.START_DATE));
			
			// Copy contractTransform info to originalContract (Not needed maybe..)
			deleteContractTransformData(dslContext, contractTransform.get(CONTRACT.ID));
			
			// Clean & optimize contractData
			cleanContractData(dslContext, originalContract.getId());
			
			System.out.println("Transform updated!!! ContractTransfromId : " + contractTransform.get(CONTRACT.ID) + " --> ContractId : " + originalContract.getId());
			
		} catch (Exception e) {
			System.err.println("Can't move transform... ContractTransformId : " + contractTransform.get(CONTRACT.ID));
		}
	}

	private void closeContractEndDate(DSLContext dslContext, Integer contractId, java.sql.Date endDate) {
		dslContext.update(CONTRACT_DATA)
			.set(CONTRACT_DATA.END_DATE, endDate)
			.where(CONTRACT_DATA.CONTRACT.eq(contractId))
			.and(CONTRACT_DATA.END_DATE.isNull())
			.execute();
		
		dslContext.update(CONTRACT_INFO)
			.set(CONTRACT_INFO.END_DATE, endDate)
			.where(CONTRACT_INFO.CONTRACT.eq(contractId))
			.and(CONTRACT_INFO.END_DATE.isNull())
			.execute();
	
		dslContext.update(CONTRACT_BONUS)
			.set(CONTRACT_BONUS.END_DATE, endDate)
			.where(CONTRACT_BONUS.CONTRACT.eq(contractId))
			.and(CONTRACT_BONUS.END_DATE.isNull())
			.execute();
	
		dslContext.update(CONTRACT_DEDUCTION)
			.set(CONTRACT_DEDUCTION.END_DATE, endDate)
			.where(CONTRACT_DEDUCTION.CONTRACT.eq(contractId))
			.and(CONTRACT_DEDUCTION.END_DATE.isNull())
			.execute();
	
		dslContext.update(CONTRACT_EMBARGO)
			.set(CONTRACT_EMBARGO.END_DATE, endDate)
			.where(CONTRACT_EMBARGO.CONTRACT.eq(contractId))
			.and(CONTRACT_EMBARGO.END_DATE.isNull())
			.execute();
	
		dslContext.update(CONTRACT_LEAVE)
			.set(CONTRACT_LEAVE.END_DATE, endDate)
			.where(CONTRACT_LEAVE.CONTRACT.eq(contractId))
			.and(CONTRACT_LEAVE.END_DATE.isNull())
			.execute();
	
		dslContext.update(CONTRACT_PAYMENT)
			.set(CONTRACT_PAYMENT.END_DATE, endDate)
			.where(CONTRACT_PAYMENT.CONTRACT.eq(contractId))
			.and(CONTRACT_PAYMENT.END_DATE.isNull())
			.execute();
	}

	private void updateContractEndDate(DSLContext dslContext, Integer contractId, java.sql.Date newEndDate) {
		dslContext.update(CONTRACT)
			.set(CONTRACT.END_DATE, newEndDate)
			.where(CONTRACT.ID.eq(contractId))
			.execute();
	}

	private void copyContractTransformData(DSLContext dslContext, Integer originalContractId, Integer transformContractId) {
		dslContext.execute(SET_FOREIGN_KEY_CHECKS_0);
		
		dslContext.update(CONTRACT_ATTACH)
			.set(CONTRACT_ATTACH.CONTRACT, originalContractId)
			.where(CONTRACT_ATTACH.CONTRACT.eq(transformContractId))
			.execute();
		
		dslContext.update(CONTRACT_BONUS)
			.set(CONTRACT_BONUS.CONTRACT, originalContractId)
			.where(CONTRACT_BONUS.CONTRACT.eq(transformContractId))
			.execute();
		
		dslContext.update(CONTRACT_CALENDAR_EVENT)
			.set(CONTRACT_CALENDAR_EVENT.CONTRACT, originalContractId)
			.where(CONTRACT_CALENDAR_EVENT.CONTRACT.eq(transformContractId))
			.execute();
		
		dslContext.update(CONTRACT_CLAUSE)
			.set(CONTRACT_CLAUSE.CONTRACT, originalContractId)
			.where(CONTRACT_CLAUSE.CONTRACT.eq(transformContractId))
			.execute();

		dslContext.update(CONTRACT_COST)
			.set(CONTRACT_COST.CONTRACT, originalContractId)
			.where(CONTRACT_COST.CONTRACT.eq(transformContractId))
			.execute();
		
		dslContext.update(CONTRACT_DATA)
			.set(CONTRACT_DATA.CONTRACT, originalContractId)
			.where(CONTRACT_DATA.CONTRACT.eq(transformContractId))
			.execute();
		
		dslContext.update(CONTRACT_DEDUCTION)
			.set(CONTRACT_DEDUCTION.CONTRACT, originalContractId)
			.where(CONTRACT_DEDUCTION.CONTRACT.eq(transformContractId))
			.execute();
		
		dslContext.update(CONTRACT_EMBARGO)
			.set(CONTRACT_EMBARGO.CONTRACT, originalContractId)
			.where(CONTRACT_EMBARGO.CONTRACT.eq(transformContractId))
			.execute();
		
		dslContext.update(CONTRACT_INFO)
			.set(CONTRACT_INFO.CONTRACT, originalContractId)
			.where(CONTRACT_INFO.CONTRACT.eq(transformContractId))
			.execute();
		
		dslContext.update(CONTRACT_LEAVE)
			.set(CONTRACT_LEAVE.CONTRACT, originalContractId)
			.where(CONTRACT_LEAVE.CONTRACT.eq(transformContractId))
			.execute();
		
		dslContext.update(CONTRACT_PAYMENT)
			.set(CONTRACT_PAYMENT.CONTRACT, originalContractId)
			.where(CONTRACT_PAYMENT.CONTRACT.eq(transformContractId))
			.execute();
		
		dslContext.update(CERTIFICA2_BATCH_DETAIL)
			.set(CERTIFICA2_BATCH_DETAIL.CONTRACT, originalContractId)
			.where(CERTIFICA2_BATCH_DETAIL.CONTRACT.eq(transformContractId))
			.execute();
		
		dslContext.update(IRPF_DATA)
			.set(IRPF_DATA.CONTRACT, originalContractId)
			.where(IRPF_DATA.CONTRACT.eq(transformContractId))
			.execute();
		
		dslContext.update(SALARY)
			.set(SALARY.CONTRACT, originalContractId)
			.where(SALARY.CONTRACT.eq(transformContractId))
			.execute();
			
		dslContext.execute(SET_FOREIGN_KEY_CHECKS_1);
	}

	private void createTransformVariables(DSLContext dslContext, ContractRecord originalContract, java.sql.Date transformStartDate) {
		dslContext.insertInto(CONTRACT_DATA)
			.set(CONTRACT_DATA.DOMAIN, originalContract.getDomain())
			.set(CONTRACT_DATA.NAME, TRANSFORM_DATE)
			.set(CONTRACT_DATA.CONTRACT, originalContract.getId())
			.set(CONTRACT_DATA.EXPRESSION, formatDate.format(transformStartDate))
			.set(CONTRACT_DATA.START_DATE, transformStartDate)
			.set(CONTRACT_DATA.END_DATE, DSL.castNull(CONTRACT_DATA.END_DATE))
			.execute();
	}

	private void deleteContractTransformData(DSLContext dslContext, Integer transformContractId) {
		dslContext.execute(SET_FOREIGN_KEY_CHECKS_0);
		
		dslContext.delete(CONTRACT_ATTACH)
			.where(CONTRACT_ATTACH.CONTRACT.eq(transformContractId))
			.execute();
		
		dslContext.delete(CONTRACT_BONUS)
			.where(CONTRACT_BONUS.CONTRACT.eq(transformContractId))
			.execute();
		
		dslContext.delete(CONTRACT_CALENDAR_EVENT)
			.where(CONTRACT_CALENDAR_EVENT.CONTRACT.eq(transformContractId))
			.execute();
		
		dslContext.delete(CONTRACT_CLAUSE)
			.where(CONTRACT_CLAUSE.CONTRACT.eq(transformContractId))
			.execute();

		dslContext.delete(CONTRACT_COST)
			.where(CONTRACT_COST.CONTRACT.eq(transformContractId))
			.execute();
		
		dslContext.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.CONTRACT.eq(transformContractId))
			.execute();
		
		dslContext.delete(CONTRACT_DEDUCTION)
			.where(CONTRACT_DEDUCTION.CONTRACT.eq(transformContractId))
			.execute();
		
		dslContext.delete(CONTRACT_EMBARGO)
			.where(CONTRACT_EMBARGO.CONTRACT.eq(transformContractId))
			.execute();
		
		dslContext.delete(CONTRACT_INFO)
			.where(CONTRACT_INFO.CONTRACT.eq(transformContractId))
			.execute();
		
		dslContext.delete(CONTRACT_LEAVE)
			.where(CONTRACT_LEAVE.CONTRACT.eq(transformContractId))
			.execute();
		
		dslContext.delete(CONTRACT_PAYMENT)
			.where(CONTRACT_PAYMENT.CONTRACT.eq(transformContractId))
			.execute();
		
		dslContext.delete(CONTRACT)
			.where(CONTRACT.ID.eq(transformContractId))
			.execute();
		
		dslContext.delete(CERTIFICA2_BATCH_DETAIL)
			.where(CERTIFICA2_BATCH_DETAIL.CONTRACT.eq(transformContractId))
			.execute();
		
		dslContext.delete(IRPF_DATA)
			.where(IRPF_DATA.CONTRACT.eq(transformContractId))
			.execute();
		
		dslContext.delete(SALARY)
			.where(SALARY.CONTRACT.eq(transformContractId))
			.execute();
			
		dslContext.execute(SET_FOREIGN_KEY_CHECKS_1);
	}
	
	private void cleanContractData(DSLContext dslContext, Integer contractId) {
		dslContext.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.CONTRACT.eq(contractId))
			.and(CONTRACT_DATA.NAME.eq(ORIGINAL_START_DATE))
			.or(CONTRACT_DATA.NAME.eq(ORIGINAL_END_DATE))
			.execute();
	}

	private java.sql.Date parseSQLDate(Date date) {
		return null == date ? null : new java.sql.Date(date.getTime());
	}

}
