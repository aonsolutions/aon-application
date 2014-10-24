package com.esferalia.aon.gwt.payroll.sql;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Date;

import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Contract;
import com.esferalia.aon.jooq.tables.ContractAttach;
import com.esferalia.aon.jooq.tables.ContractBatch;
import com.esferalia.aon.jooq.tables.ContractBatchDetail;
import com.esferalia.aon.jooq.tables.ContractBonus;
import com.esferalia.aon.jooq.tables.ContractCalendarEvent;
import com.esferalia.aon.jooq.tables.ContractClause;
import com.esferalia.aon.jooq.tables.ContractData;
import com.esferalia.aon.jooq.tables.ContractDeduction;
import com.esferalia.aon.jooq.tables.ContractEmbargo;
import com.esferalia.aon.jooq.tables.ContractInfo;
import com.esferalia.aon.jooq.tables.ContractLeave;
import com.esferalia.aon.jooq.tables.ContractLeaveDetail;
import com.esferalia.aon.jooq.tables.ContractPayment;
import com.esferalia.aon.jooq.tables.Salary;
import com.esferalia.aon.jooq.tables.SalaryBonus;
import com.esferalia.aon.jooq.tables.SalaryCost;
import com.esferalia.aon.jooq.tables.SalaryData;
import com.esferalia.aon.jooq.tables.SalaryDeduction;
import com.esferalia.aon.jooq.tables.SalaryEmbargo;
import com.esferalia.aon.jooq.tables.SalaryPayment;
import com.esferalia.aon.jooq.tables.records.ContractBatchDetailRecord;
import com.esferalia.aon.jooq.tables.records.ContractBonusRecord;
import com.esferalia.aon.jooq.tables.records.ContractDeductionRecord;
import com.esferalia.aon.jooq.tables.records.ContractLeaveRecord;
import com.esferalia.aon.jooq.tables.records.ContractPaymentRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.SalaryRecord;

public class SQLEmployee implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9219247789189250364L;

	private static final Contract CONTRACT = Contract.CONTRACT;
	private static final ContractAttach CONTRACT_ATTACH = ContractAttach.CONTRACT_ATTACH; //	no
	private static final ContractBatch CONTRACT_BATCH = ContractBatch.CONTRACT_BATCH; //no
	private static final ContractBatchDetail CONTRACT_BATCH_DETAIL = ContractBatchDetail.CONTRACT_BATCH_DETAIL; //no
	private static final ContractBonus CONTRACT_BONUS = ContractBonus.CONTRACT_BONUS;
	private static final ContractCalendarEvent CONTRACT_CALENDAR_EVENT = ContractCalendarEvent.CONTRACT_CALENDAR_EVENT; //no
	private static final ContractClause CONTRACT_CLAUSE = ContractClause.CONTRACT_CLAUSE; //no
	private static final ContractData CONTRACT_DATA = ContractData.CONTRACT_DATA;//fecha ?? alguna copiar otras no
	private static final ContractDeduction CONTRACT_DEDUCTION = ContractDeduction.CONTRACT_DEDUCTION; //segun check
	private static final ContractEmbargo CONTRACT_EMBARGO = ContractEmbargo.CONTRACT_EMBARGO;
	private static final ContractInfo CONTRACT_INFO = ContractInfo.CONTRACT_INFO; 
	private static final ContractLeave CONTRACT_LEAVE = ContractLeave.CONTRACT_LEAVE;
	private static final ContractLeaveDetail CONTRACT_LEAVE_DETAIL = ContractLeaveDetail.CONTRACT_LEAVE_DETAIL;
	private static final ContractPayment CONTRACT_PAYMENT = ContractPayment.CONTRACT_PAYMENT; //segun check 

	private static final Salary SALARY = Salary.SALARY;
	private static final SalaryBonus SALARY_BONUS = SalaryBonus.SALARY_BONUS;
	private static final SalaryCost SALARY_COST = SalaryCost.SALARY_COST;
	private static final SalaryData SALARY_DATA = SalaryData.SALARY_DATA;
	private static final SalaryDeduction SALARY_DEDUCTION = SalaryDeduction.SALARY_DEDUCTION;
	private static final SalaryEmbargo SALARY_EMBARGO = SalaryEmbargo.SALARY_EMBARGO;
	private static final SalaryPayment SALARY_PAYMENT = SalaryPayment.SALARY_PAYMENT;

	private static Settings SETTINGS = null;

	public static void save(Connection conn, int domain, int workplaceId, int personId,
			Date startDate, Date endDate, boolean check) {
		
		DSLContext create = DSL.using(conn, SQLDialect.MYSQL,
				getDefaultSettings());
		
		try {

			insertPasteContract(create, conn, domain, workplaceId, personId, startDate, endDate);
			
			if(check) 
				insertDependCheck(create, conn, domain, personId, startDate, endDate); 

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void delete(Connection conn, int personId) {

		try {

			deleteContract(conn, personId);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private static void insertPasteContract(DSLContext create, Connection conn, int domain,
			int workplaceId, int personId, Date startDate, Date endDate) throws Exception {

		ContractRecord contract = create.selectFrom(CONTRACT)
				.where(CONTRACT.ID.eq(personId)).fetchAny();
		
		if(contract != null)
			paste2Contract(create, contract, domain, workplaceId, personId, startDate, endDate);
		
		// --------------------------------------------------------------------------
		
		ContractBonusRecord bonus = create.selectFrom(CONTRACT_BONUS)
				.where(CONTRACT_BONUS.CONTRACT.eq(personId)).fetchAny();
		
		if(bonus != null) 
			paste2ContractBonus(create, bonus, domain, personId, startDate, endDate);
				
	}
	
	private static void insertDependCheck(DSLContext create, Connection conn, int domain,
			int personId, Date startDate, Date endDate) throws Exception {
		
		ContractDeductionRecord deduction = create.selectFrom(CONTRACT_DEDUCTION)
				.where(CONTRACT_DEDUCTION.CONTRACT.eq(personId)).fetchAny();
		
		if(deduction != null)
			paste2ContractDeduction(create, deduction, domain, personId, startDate, endDate);
		
		// --------------------------------------------------------------------------		
		
		ContractPaymentRecord payment = create.selectFrom(CONTRACT_PAYMENT)
				.where(CONTRACT_PAYMENT.CONTRACT.eq(personId)).fetchAny();
		
		if(payment != null)
			paste2ContractPayment(create, deduction, domain, personId, startDate, endDate);	
		
	}

/*	SQL [delete from `salary` where `salary`.`contract` = ?]; Cannot delete or update a parent row: 
		a foreign key constraint fails (`pro-aonsolutions-net`.`salary_cost`, CONSTRAINT `FK_SALARY_COST_SALARY` 
				FOREIGN KEY (`salary`) REFERENCES `salary` (`id`))
*/	
	private static void deleteContract(Connection conn, int personId)
			throws Exception {

		DSLContext create = DSL.using(conn, SQLDialect.MYSQL,
				getDefaultSettings());
		
		// ----------------------------------Salary------------------------------------------

		SalaryRecord salary = create.selectFrom(SALARY)
				.where(SALARY.CONTRACT.eq(personId)).fetchAny();

		if (salary != null) {
			
			create.delete(SALARY_PAYMENT)
					.where(SALARY_PAYMENT.SALARY.eq(salary.getId())).execute();
			
			create.delete(SALARY_EMBARGO)
					.where(SALARY_EMBARGO.SALARY.eq(salary.getId())).execute();

			create.delete(SALARY_DEDUCTION)
					.where(SALARY_DEDUCTION.SALARY.eq(salary.getId()))
					.execute();

			create.delete(SALARY_DATA)
					.where(SALARY_DATA.SALARY.eq(salary.getId())).execute();

			create.delete(SALARY_COST)
					.where(SALARY_COST.SALARY.eq(salary.getId())).execute();

			create.delete(SALARY_BONUS)
					.where(SALARY_BONUS.SALARY.eq(salary.getId())).execute();
			
			create.delete(SALARY).where(SALARY.CONTRACT.eq(personId)).execute();

		}
		// ----------------------------------------------------------------------------------


		create.delete(CONTRACT_ATTACH)
				.where(CONTRACT_ATTACH.CONTRACT.eq(personId)).execute();

		// ----------------------------------------------------------------------------------

		ContractBatchDetailRecord batchDetail = create
				.selectFrom(CONTRACT_BATCH_DETAIL)
				.where(CONTRACT_BATCH_DETAIL.CONTRACT.eq(personId)).fetchAny();

		if (batchDetail != null) {
			create.delete(CONTRACT_BATCH_DETAIL)
					.where(CONTRACT_BATCH_DETAIL.CONTRACT.eq(personId))
					.execute();

			create.delete(CONTRACT_BATCH)
					.where(CONTRACT_BATCH.ID.eq(batchDetail.getContractBatch()))
					.execute();
		}

		// ----------------------------------------------------------------------------------

		create.delete(CONTRACT_BONUS)
				.where(CONTRACT_BONUS.CONTRACT.eq(personId)).execute();

		// ----------------------------------------------------------------------------------

		create.delete(CONTRACT_CALENDAR_EVENT)
				.where(CONTRACT_CALENDAR_EVENT.CONTRACT.eq(personId)).execute();

		// ----------------------------------------------------------------------------------

		create.delete(CONTRACT_CLAUSE)
				.where(CONTRACT_CLAUSE.CONTRACT.eq(personId)).execute();

		// ----------------------------------------------------------------------------------

		create.delete(CONTRACT_DATA).where(CONTRACT_DATA.CONTRACT.eq(personId))
				.execute();

		// ----------------------------------------------------------------------------------

		create.delete(CONTRACT_DEDUCTION)
				.where(CONTRACT_DEDUCTION.CONTRACT.eq(personId)).execute();

		// ----------------------------------------------------------------------------------

		create.delete(CONTRACT_EMBARGO)
				.where(CONTRACT_EMBARGO.CONTRACT.eq(personId)).execute();

		// ----------------------------------------------------------------------------------

		create.delete(CONTRACT_INFO).where(CONTRACT_INFO.CONTRACT.eq(personId))
				.execute();

		// ----------------------------------------------------------------------------------

		ContractLeaveRecord leave = create.selectFrom(CONTRACT_LEAVE)
				.where(CONTRACT_LEAVE.CONTRACT.eq(personId)).fetchAny();

		if (leave != null) {
			create.delete(CONTRACT_LEAVE_DETAIL)
					.where(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE.eq(leave
							.getContract())).execute();

			create.delete(CONTRACT_LEAVE)
					.where(CONTRACT_LEAVE.CONTRACT.eq(personId)).execute();
		}

		// ----------------------------------------------------------------------------------

		create.delete(CONTRACT_PAYMENT)
				.where(CONTRACT_PAYMENT.CONTRACT.eq(personId)).execute();

		// ----------------------------------------------------------------------------------
	
		create.delete(CONTRACT).where(Contract.CONTRACT.ID.eq(personId))
				.execute();

	}
	
	protected static void paste2Contract(DSLContext create, ContractRecord contract, 
			int domain, int workplaceId, int personId, Date startDate, Date endDate) {
		
		InsertSetMoreStep<ContractRecord> insertContract = create
				.insertInto(CONTRACT)

				.set(CONTRACT.DOMAIN, domain)
				.set(CONTRACT.PERSON, contract.getValue(CONTRACT.PERSON))
				.set(CONTRACT.WORKPLACE, workplaceId)
				.set(CONTRACT.ENTERPRISE_CCC,
						contract.getValue(CONTRACT.ENTERPRISE_CCC))
				.set(CONTRACT.START_DATE,
						new java.sql.Date(startDate.getTime()))
				.set(CONTRACT.CALENDAR, contract.getValue(CONTRACT.CALENDAR))
				.set(CONTRACT.DESCRIPTION,
						contract.getValue(CONTRACT.DESCRIPTION))
				.set(CONTRACT.SEPE_STATUS,
						contract.getValue(CONTRACT.SEPE_STATUS))
				.set(CONTRACT.REGISTRATION,
						contract.getValue(CONTRACT.REGISTRATION))
				.set(CONTRACT.SENIORITY_DATE,
						contract.getValue(CONTRACT.SENIORITY_DATE))
				.set(CONTRACT.ENTERPRISE_ACTIVITY,
						contract.getValue(CONTRACT.ENTERPRISE_ACTIVITY))
				.set(CONTRACT.SS_REGIME, contract.getValue(CONTRACT.SS_REGIME))
				.set(CONTRACT.AGREEMENT_LEVEL_CATEGORY,
						contract.getValue(CONTRACT.AGREEMENT_LEVEL_CATEGORY))
				.set(CONTRACT.MODEL, contract.getValue(CONTRACT.MODEL))
				.set(CONTRACT.CATEGORY_DESCRIPTION,
						contract.getValue(CONTRACT.CATEGORY_DESCRIPTION))
				.set(CONTRACT.SS_STATUS, contract.getValue(CONTRACT.SS_STATUS));

		if (endDate != null)
			insertContract = insertContract.set(Contract.CONTRACT.END_DATE, new java.sql.Date(
					endDate.getTime()));

		insertContract.execute();
	}
	
	protected static void paste2ContractBonus(DSLContext create, ContractBonusRecord bonus, 
			int domain, int personId, Date startDate, Date endDate) throws Exception {
		
		InsertSetMoreStep<ContractBonusRecord> insert = create.insertInto(CONTRACT_BONUS)
				
				.set(CONTRACT_BONUS.ID, domain)
				.set(CONTRACT_BONUS.CONTRACT, personId)
				.set(CONTRACT_BONUS.DESCRIPTION, bonus.getValue(CONTRACT_BONUS.DESCRIPTION))
				.set(CONTRACT_BONUS.EXPRESSION, bonus.getValue(CONTRACT_BONUS.EXPRESSION))
				.set(CONTRACT_BONUS.START_DATE, new java.sql.Date(startDate.getTime()));
		
		if(endDate != null)
			insert = insert.set(CONTRACT_BONUS.END_DATE, new java.sql.Date(endDate.getTime()));		
		
		insert.execute();
	}
	
	protected static void paste2ContractDeduction (DSLContext create, ContractDeductionRecord deduction,
			int domain, int personId, Date startDate, Date endDate) throws Exception {
		
		InsertSetMoreStep<ContractDeductionRecord> insert = create.insertInto(CONTRACT_DEDUCTION)
				
				.set(CONTRACT_DEDUCTION.DOMAIN, domain)
				.set(CONTRACT_DEDUCTION.TYPE, deduction.getValue(CONTRACT_DEDUCTION.TYPE))
				.set(CONTRACT_DEDUCTION.DEDUCTION_CONCEPT, deduction.getValue(CONTRACT_DEDUCTION.DEDUCTION_CONCEPT))
				.set(CONTRACT_DEDUCTION.CONTRACT, personId)
				.set(CONTRACT_DEDUCTION.DESCRIPTION, deduction.getValue(CONTRACT_DEDUCTION.DESCRIPTION))
				.set(CONTRACT_DEDUCTION.DESCRIPTION_DECORABLE, deduction.getValue(CONTRACT_DEDUCTION.DESCRIPTION_DECORABLE))
				.set(CONTRACT_DEDUCTION.EXPRESSION, deduction.getValue(CONTRACT_DEDUCTION.EXPRESSION))
				.set(CONTRACT_DEDUCTION.START_DATE, new java.sql.Date(startDate.getTime()));
		
		if(endDate != null)
			insert = insert.set(CONTRACT_DEDUCTION.END_DATE, new java.sql.Date(endDate.getTime()));
		
		insert.execute();
	}
	
	protected static void paste2ContractPayment (DSLContext create, ContractDeductionRecord deduction,
			int domain, int personId, Date startDate, Date endDate) throws Exception {
		
		InsertSetMoreStep<ContractPaymentRecord> insert = create.insertInto(CONTRACT_PAYMENT)
				
				.set(CONTRACT_PAYMENT.DOMAIN, domain)
				.set(CONTRACT_PAYMENT.TYPE, deduction.getValue(CONTRACT_PAYMENT.TYPE))
				.set(CONTRACT_PAYMENT.CONTRACT, personId)
				.set(CONTRACT_PAYMENT.PAYMENT_CONCEPT, deduction.getValue(CONTRACT_PAYMENT.PAYMENT_CONCEPT))
				.set(CONTRACT_PAYMENT.DESCRIPTION, deduction.getValue(CONTRACT_PAYMENT.DESCRIPTION))
				.set(CONTRACT_PAYMENT.DESCRIPTION_DECORABLE, deduction.getValue(CONTRACT_PAYMENT.DESCRIPTION_DECORABLE))
				.set(CONTRACT_PAYMENT.EXPRESSION, deduction.getValue(CONTRACT_PAYMENT.EXPRESSION))
				.set(CONTRACT_PAYMENT.IRPF_EXPRESSION, deduction.getValue(CONTRACT_PAYMENT.IRPF_EXPRESSION))
				.set(CONTRACT_PAYMENT.QUOTE_EXPRESSION, deduction.getValue(CONTRACT_PAYMENT.QUOTE_EXPRESSION))
				.set(CONTRACT_PAYMENT.START_DATE, new java.sql.Date(startDate.getTime()))
				.set(CONTRACT_PAYMENT.MONTH, deduction.getValue(CONTRACT_PAYMENT.MONTH))
				.set(CONTRACT_PAYMENT.SALARY_TYPE, deduction.getValue(CONTRACT_PAYMENT.SALARY_TYPE));
		
		if(endDate != null)
			insert = insert.set(CONTRACT_PAYMENT.END_DATE, new java.sql.Date(endDate.getTime()));
		
		insert.execute();				
	}
	

	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}


}
