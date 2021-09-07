package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.ContractBonus.CONTRACT_BONUS;
import static com.esferalia.aon.jooq.tables.ContractCost.CONTRACT_COST;
import static com.esferalia.aon.jooq.tables.ContractDeduction.CONTRACT_DEDUCTION;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.jooq.tables.DeductionConcept.DEDUCTION_CONCEPT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.shared.ContractConceptCalc;
import com.esferalia.aon.gwt.payroll.shared.ContractConceptCalc.ContractConceptCalcType;
import com.esferalia.aon.gwt.payroll.shared.ContractPaymentData;

public class JooqEmployeeContractPayments {

	private static Settings settings;
	
	// ------------------------------- Construtor
	
	private JooqEmployeeContractPayments() {
		super();
	}
	
	// ------------------------------- Auxiliar Methods
	
	protected static Settings getDefaultSettings() {
		if (settings == null) {
			settings = new Settings();
			settings.setRenderSchema(false);
		}
		return settings;
	}

	private static Date parseToSQLDate(java.util.Date date) {
		if(null == date)
			return null;

		return new Date(date.getTime());
	}
	
	// ------------------------------- Database Methods

	public static ContractPaymentData getContractConceptCalcs(Connection conn, Integer domainId, Integer contractId) {
		return getContractConceptCalcs(DSL.using(conn, getDefaultSettings()), domainId, contractId);	
	}

	public static void updateContractPayments(Connection conn, ContractPaymentData contractPaymentData) {
		updateContractPayments(DSL.using(conn, getDefaultSettings()), contractPaymentData);
	}

	private static ContractPaymentData getContractConceptCalcs(DSLContext dslContext, Integer domainId, Integer contractId) {
		ContractPaymentData contractPaymentData = new ContractPaymentData();
		
		List<ContractConceptCalc> contractPayments = getContractPayments(dslContext, domainId, contractId);
		List<ContractConceptCalc> contractDeductions = getContractDeductions(dslContext, domainId, contractId);
		List<ContractConceptCalc> contractBonuses = getContractBonuses(dslContext, domainId, contractId);
		List<ContractConceptCalc> contractCosts = getContractCosts(dslContext, domainId, contractId);
		
		List<ContractConceptCalc> contractConceptCalcs = new ArrayList<>();
		contractConceptCalcs.addAll(contractPayments);
		contractConceptCalcs.addAll(contractDeductions);
		contractConceptCalcs.addAll(contractBonuses);
		contractConceptCalcs.addAll(contractCosts);
		
		contractPaymentData.setContractConceptCalcs(contractConceptCalcs);
		
		return contractPaymentData;
	}
	
	private static List<ContractConceptCalc> getContractPayments(DSLContext dslContext, Integer domainId, Integer contractId) {
		Result<Record> contractPaymentRecords = dslContext.select().from(CONTRACT_PAYMENT)
				.where(CONTRACT_PAYMENT.CONTRACT.eq(contractId))
				.and(CONTRACT_PAYMENT.DOMAIN.eq(domainId))
				.fetch();
			
		if(contractPaymentRecords.isEmpty())
			return new ArrayList<>();
		else {
			List<ContractConceptCalc> contractPayments = new ArrayList<>();
			
			for(Record contractPaymentRecord : contractPaymentRecords) {
				ContractConceptCalc contractPayment = new ContractConceptCalc();
				
				String code = null;
				Integer paymentConceptId = contractPaymentRecord.get(CONTRACT_PAYMENT.PAYMENT_CONCEPT);
				if(null != paymentConceptId)
					code = dslContext.select(PAYMENT_CONCEPT.CODE).from(PAYMENT_CONCEPT).where(PAYMENT_CONCEPT.ID.eq(paymentConceptId)).fetchOne(PAYMENT_CONCEPT.CODE);
				
				contractPayment.setId(contractPaymentRecord.get(CONTRACT_PAYMENT.ID))
								.setCode(code)
								.setContractConceptCalcType(ContractConceptCalcType.PAYMENT)
								.setDescription(contractPaymentRecord.get(CONTRACT_PAYMENT.DESCRIPTION))
								.setExpression(contractPaymentRecord.get(CONTRACT_PAYMENT.EXPRESSION))
								.setStartDate(contractPaymentRecord.get(CONTRACT_PAYMENT.START_DATE))
								.setEndDate(contractPaymentRecord.get(CONTRACT_PAYMENT.END_DATE))
								.setHasChange(false);
				contractPayments.add(contractPayment);
			}
			
			return contractPayments;
		}
	}

	private static List<ContractConceptCalc> getContractDeductions(DSLContext dslContext, Integer domainId, Integer contractId) {
		Result<Record> contractDeductionRecords = dslContext.select().from(CONTRACT_DEDUCTION)
				.where(CONTRACT_DEDUCTION.CONTRACT.eq(contractId))
				.and(CONTRACT_DEDUCTION.DOMAIN.eq(domainId))
				.fetch();
			
		if(contractDeductionRecords.isEmpty())
			return new ArrayList<>();
		else {
			List<ContractConceptCalc> contractDeductions = new ArrayList<>();
			
			for(Record contractDeductionRecord : contractDeductionRecords) {
				ContractConceptCalc contractDeduction = new ContractConceptCalc();
				
				String code = null;
				Integer deductionConceptId = contractDeductionRecord.get(CONTRACT_DEDUCTION.DEDUCTION_CONCEPT);
				if(null != deductionConceptId)
					code = dslContext.select(DEDUCTION_CONCEPT.CODE).from(DEDUCTION_CONCEPT).where(DEDUCTION_CONCEPT.ID.eq(deductionConceptId)).fetchOne(DEDUCTION_CONCEPT.CODE);
				
				contractDeduction.setId(contractDeductionRecord.get(CONTRACT_PAYMENT.ID))
								.setCode(code)
								.setContractConceptCalcType(ContractConceptCalcType.DEDUCTION)
								.setDescription(contractDeductionRecord.get(CONTRACT_PAYMENT.DESCRIPTION))
								.setExpression(contractDeductionRecord.get(CONTRACT_PAYMENT.EXPRESSION))
								.setStartDate(contractDeductionRecord.get(CONTRACT_PAYMENT.START_DATE))
								.setEndDate(contractDeductionRecord.get(CONTRACT_PAYMENT.END_DATE))
								.setHasChange(false);
				contractDeductions.add(contractDeduction);
			}
			
			return contractDeductions;
		}
	}

	private static List<ContractConceptCalc> getContractBonuses(DSLContext dslContext, Integer domainId, Integer contractId) {
		Result<Record> contractBonusRecords = dslContext.select().from(CONTRACT_BONUS)
				.where(CONTRACT_BONUS.CONTRACT.eq(contractId))
				.and(CONTRACT_BONUS.DOMAIN.eq(domainId))
				.fetch();
			
		if(contractBonusRecords.isEmpty())
			return new ArrayList<>();
		else {
			List<ContractConceptCalc> contractBonuses = new ArrayList<>();
			
			for(Record contractBonusRecord : contractBonusRecords) {
				ContractConceptCalc contractBonus = new ContractConceptCalc();
				
				String code = null;
				
				contractBonus.setId(contractBonusRecord.get(CONTRACT_BONUS.ID))
								.setCode(code)
								.setContractConceptCalcType(ContractConceptCalcType.BONUS)
								.setDescription(contractBonusRecord.get(CONTRACT_BONUS.DESCRIPTION))
								.setExpression(contractBonusRecord.get(CONTRACT_BONUS.EXPRESSION))
								.setStartDate(contractBonusRecord.get(CONTRACT_BONUS.START_DATE))
								.setEndDate(contractBonusRecord.get(CONTRACT_BONUS.END_DATE))
								.setHasChange(false);
				contractBonuses.add(contractBonus);
			}
			
			return contractBonuses;
		}
	}

	private static List<ContractConceptCalc> getContractCosts(DSLContext dslContext, Integer domainId, Integer contractId) {
		Result<Record> contractCostRecords = dslContext.select().from(CONTRACT_COST)
				.where(CONTRACT_COST.CONTRACT.eq(contractId))
				.and(CONTRACT_COST.DOMAIN.eq(domainId))
				.fetch();
			
		if(contractCostRecords.isEmpty())
			return new ArrayList<>();
		else {
			List<ContractConceptCalc> contractCosts = new ArrayList<>();
			
			for(Record contractCostRecord : contractCostRecords) {
				ContractConceptCalc contractCost = new ContractConceptCalc();
				
				contractCost.setId(contractCostRecord.get(CONTRACT_COST.ID))
								.setCode(contractCostRecord.get(CONTRACT_COST.CODE))
								.setContractConceptCalcType(ContractConceptCalcType.COST)
								.setDescription(contractCostRecord.get(CONTRACT_COST.DESCRIPTION))
								.setExpression(contractCostRecord.get(CONTRACT_COST.EXPRESSION))
								.setStartDate(contractCostRecord.get(CONTRACT_COST.START_DATE))
								.setEndDate(contractCostRecord.get(CONTRACT_COST.END_DATE))
								.setHasChange(false);
				contractCosts.add(contractCost);
			}
			
			return contractCosts;
		}
	}

	private static void updateContractPayments(DSLContext dslContext, ContractPaymentData contractPaymentData) {
		List<ContractConceptCalc> contractConceptCalcs = contractPaymentData.getCcontractConceptCalcs();
		
		for(ContractConceptCalc contractConceptCalc : contractConceptCalcs) {
			ContractConceptCalcType contractConceptCalcType = contractConceptCalc.getContractConceptCalcType();
			switch (contractConceptCalcType) {
				case PAYMENT:
					updateDeleteContractPayment(dslContext, contractConceptCalc);
					break;
				case DEDUCTION:
					updateDeleteContractDeduction(dslContext, contractConceptCalc);
					break;
				case COST:
					updateDeleteContractCost(dslContext, contractConceptCalc);
					break;
				case BONUS:
					updateDeleteContractBonus(dslContext, contractConceptCalc);
					break;
				default:
					break;
			}
		}
	}

	private static void updateDeleteContractPayment(DSLContext dslContext, ContractConceptCalc contractConceptCalc) {
		if(contractConceptCalc.getId() < 0) {
			Integer id = -1 * contractConceptCalc.getId();
			dslContext.delete(CONTRACT_PAYMENT)
				.where(CONTRACT_PAYMENT.ID.eq(id))
				.execute();
		} else if(contractConceptCalc.getHasChange())
			dslContext.update(CONTRACT_PAYMENT)
				.set(CONTRACT_PAYMENT.DESCRIPTION, contractConceptCalc.getDescription())
				.set(CONTRACT_PAYMENT.EXPRESSION, contractConceptCalc.getExpression())
				.set(CONTRACT_PAYMENT.START_DATE, parseToSQLDate(contractConceptCalc.getStartDate()))
				.set(CONTRACT_PAYMENT.END_DATE, parseToSQLDate(contractConceptCalc.getEndDate()))
				.where(CONTRACT_PAYMENT.ID.eq(contractConceptCalc.getId()))
				.execute();
	}

	private static void updateDeleteContractDeduction(DSLContext dslContext, ContractConceptCalc contractConceptCalc) {
		if(contractConceptCalc.getId() < 0) {
			Integer id = -1 * contractConceptCalc.getId();
			dslContext.delete(CONTRACT_DEDUCTION)
				.where(CONTRACT_DEDUCTION.ID.eq(id))
				.execute();
		} else if(contractConceptCalc.getHasChange())
			dslContext.update(CONTRACT_DEDUCTION)
				.set(CONTRACT_DEDUCTION.DESCRIPTION, contractConceptCalc.getDescription())
				.set(CONTRACT_DEDUCTION.EXPRESSION, contractConceptCalc.getExpression())
				.set(CONTRACT_DEDUCTION.START_DATE, parseToSQLDate(contractConceptCalc.getStartDate()))
				.set(CONTRACT_DEDUCTION.END_DATE, parseToSQLDate(contractConceptCalc.getEndDate()))
				.where(CONTRACT_DEDUCTION.ID.eq(contractConceptCalc.getId()))
				.execute();
	}

	private static void updateDeleteContractCost(DSLContext dslContext, ContractConceptCalc contractConceptCalc) {
		if(contractConceptCalc.getId() < 0) {
			Integer id = -1 * contractConceptCalc.getId();
			dslContext.delete(CONTRACT_COST)
				.where(CONTRACT_COST.ID.eq(id))
				.execute();
		} else if(contractConceptCalc.getHasChange())
			dslContext.update(CONTRACT_COST)
				.set(CONTRACT_COST.DESCRIPTION, contractConceptCalc.getDescription())
				.set(CONTRACT_COST.EXPRESSION, contractConceptCalc.getExpression())
				.set(CONTRACT_COST.START_DATE, parseToSQLDate(contractConceptCalc.getStartDate()))
				.set(CONTRACT_COST.END_DATE, parseToSQLDate(contractConceptCalc.getEndDate()))
				.where(CONTRACT_COST.ID.eq(contractConceptCalc.getId()))
				.execute();
	}

	private static void updateDeleteContractBonus(DSLContext dslContext, ContractConceptCalc contractConceptCalc) {
		if(contractConceptCalc.getId() < 0) {
			Integer id = -1 * contractConceptCalc.getId();
			dslContext.delete(CONTRACT_BONUS)
				.where(CONTRACT_BONUS.ID.eq(id))
				.execute();
		} else if(contractConceptCalc.getHasChange())
			dslContext.update(CONTRACT_BONUS)
				.set(CONTRACT_BONUS.DESCRIPTION, contractConceptCalc.getDescription())
				.set(CONTRACT_BONUS.EXPRESSION, contractConceptCalc.getExpression())
				.set(CONTRACT_BONUS.START_DATE, parseToSQLDate(contractConceptCalc.getStartDate()))
				.set(CONTRACT_BONUS.END_DATE, parseToSQLDate(contractConceptCalc.getEndDate()))
				.where(CONTRACT_BONUS.ID.eq(contractConceptCalc.getId()))
				.execute();
	}
}
