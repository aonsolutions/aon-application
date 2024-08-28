package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractBonus.CONTRACT_BONUS;
import static com.esferalia.aon.jooq.tables.ContractCost.CONTRACT_COST;
import static com.esferalia.aon.jooq.tables.ContractDeduction.CONTRACT_DEDUCTION;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.jooq.tables.ContractEmbargo.CONTRACT_EMBARGO;
import static com.esferalia.aon.jooq.tables.DeductionConcept.DEDUCTION_CONCEPT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.BonusConcept.BONUS_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemCost.SYSTEM_COST;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.shared.ContractConcept;
import com.esferalia.aon.gwt.payroll.shared.ContractConceptCalc;
import com.esferalia.aon.gwt.payroll.shared.ContractConceptCalc.ContractConceptCalcType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.gwt.payroll.shared.ContractConcepts;
import com.esferalia.aon.gwt.payroll.shared.ContractPaymentData;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Salary;

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
	
	private static Date getContractStartDate(DSLContext dslContext, Integer contractId) {
		return dslContext.select(CONTRACT.START_DATE).from(CONTRACT).where(CONTRACT.ID.eq(contractId)).fetchOne(CONTRACT.START_DATE);
	}
	
	// ------------------------------- Database Methods

	public static ContractPaymentData getContractConceptCalcs(Connection conn, Integer domainId, Integer contractId) {
		return getContractConceptCalcs(DSL.using(conn, getDefaultSettings()), domainId, contractId);	
	}

	public static void updateContractPayments(Connection conn, ContractPaymentData contractPaymentData) {
		updateContractPayments(DSL.using(conn, getDefaultSettings()), contractPaymentData);
	}

	public static void createContractPayment(Connection conn, Integer domainId, Integer contractId, ContractConceptCalc contractConceptCalc) {
		createNewContractPayment(DSL.using(conn, getDefaultSettings()), domainId, contractId, contractConceptCalc);
	}
	
	public static void createContractPayment(Connection conn, Integer domainId, Integer contractId, List<ContractConceptCalc> contractConceptCalcList) {
		contractConceptCalcList.forEach(contractConceptCalc -> createNewContractPayment(DSL.using(conn, getDefaultSettings()), domainId, contractId, contractConceptCalc));
	}

	public static ContractConcepts getAllConcepts(Connection conn, Integer domainId) {
		return getAllConcepts(DSL.using(conn, getDefaultSettings()), domainId);
	}

	private static ContractPaymentData getContractConceptCalcs(DSLContext dslContext, Integer domainId, Integer contractId) {
		ContractPaymentData contractPaymentData = new ContractPaymentData();
		
		List<ContractConceptCalc> contractPayments = getContractPayments(dslContext, domainId, contractId);
		List<ContractConceptCalc> contractDeductions = getContractDeductions(dslContext, domainId, contractId);
		List<ContractConceptCalc> contractBonuses = getContractBonuses(dslContext, domainId, contractId);
		List<ContractConceptCalc> contractCosts = getContractCosts(dslContext, domainId, contractId);
		List<ContractConceptCalc> contractEmbargos = getContractEmbargos(dslContext, domainId, contractId);
		
		List<ContractConceptCalc> contractConceptCalcs = new ArrayList<>();
		contractConceptCalcs.addAll(contractPayments);
		contractConceptCalcs.addAll(contractDeductions);
		contractConceptCalcs.addAll(contractBonuses);
		contractConceptCalcs.addAll(contractCosts);
		contractConceptCalcs.addAll(contractEmbargos);
		
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
				Byte paymentCRA = contractPaymentRecord.get(CONTRACT_PAYMENT.TYPE);
				if(null != paymentConceptId) {
					code = dslContext.select(PAYMENT_CONCEPT.CODE).from(PAYMENT_CONCEPT).where(PAYMENT_CONCEPT.ID.eq(paymentConceptId)).fetchOne(PAYMENT_CONCEPT.CODE);
					if(null == paymentCRA)
						paymentCRA = dslContext.select(PAYMENT_CONCEPT.TYPE).from(PAYMENT_CONCEPT).where(PAYMENT_CONCEPT.ID.eq(paymentConceptId)).fetchOne(PAYMENT_CONCEPT.TYPE);
				}
				
				contractPayment.setId(contractPaymentRecord.get(CONTRACT_PAYMENT.ID));
				contractPayment.setDomain(contractPaymentRecord.get(CONTRACT_PAYMENT.DOMAIN));
				contractPayment.setConceptId(paymentConceptId);
				contractPayment.setType(null == paymentCRA ? Payment.Type.CRA_0001 :Payment.Type.getByCode(paymentCRA.intValue()));
				contractPayment.setName(code);
				contractPayment.setDescription(contractPaymentRecord.get(CONTRACT_PAYMENT.DESCRIPTION));
				contractPayment.setExpression(contractPaymentRecord.get(CONTRACT_PAYMENT.EXPRESSION));
				contractPayment.setIrpfExpression(contractPaymentRecord.get(CONTRACT_PAYMENT.IRPF_EXPRESSION));
				contractPayment.setQuoteExpression(contractPaymentRecord.get(CONTRACT_PAYMENT.QUOTE_EXPRESSION));
				contractPayment.setMonth(null == contractPaymentRecord.get(CONTRACT_PAYMENT.MONTH) ? null : contractPaymentRecord.get(CONTRACT_PAYMENT.MONTH).shortValue());
				contractPayment.setStartDate(contractPaymentRecord.get(CONTRACT_PAYMENT.START_DATE));
				contractPayment.setEndDate(contractPaymentRecord.get(CONTRACT_PAYMENT.END_DATE));
				
				contractPayment.setSalaryType(Salary.Type.values()[contractPaymentRecord.get(CONTRACT_PAYMENT.SALARY_TYPE)]);
				contractPayment.setContractConceptCalcType(ContractConceptCalcType.PAYMENT);
				contractPayment.setHasChange(false);
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
				
				contractDeduction.setId(contractDeductionRecord.get(CONTRACT_DEDUCTION.ID));
				contractDeduction.setDomain(contractDeductionRecord.get(CONTRACT_DEDUCTION.DOMAIN));
				contractDeduction.setConceptId(deductionConceptId);
				contractDeduction.setCodeType(null == contractDeductionRecord.get(CONTRACT_DEDUCTION.TYPE) ? null : contractDeductionRecord.get(CONTRACT_DEDUCTION.TYPE).toString());
				contractDeduction.setName(code);
				contractDeduction.setDescription(contractDeductionRecord.get(CONTRACT_DEDUCTION.DESCRIPTION));
				contractDeduction.setExpression(contractDeductionRecord.get(CONTRACT_DEDUCTION.EXPRESSION));
				contractDeduction.setMonth(null == contractDeductionRecord.get(CONTRACT_DEDUCTION.MONTH) ? null : contractDeductionRecord.get(CONTRACT_DEDUCTION.MONTH).shortValue());
				contractDeduction.setStartDate(contractDeductionRecord.get(CONTRACT_DEDUCTION.START_DATE));
				contractDeduction.setEndDate(contractDeductionRecord.get(CONTRACT_DEDUCTION.END_DATE));
				
				contractDeduction.setSalaryType(Salary.Type.SALARY);
				contractDeduction.setContractConceptCalcType(ContractConceptCalcType.DEDUCTION);
				contractDeduction.setHasChange(false);
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
				
				Byte type = null;
				Integer bonusConceptId = contractBonusRecord.get(CONTRACT_BONUS.BONUS_CONCEPT);
				if(null != bonusConceptId)
					type = dslContext.select(BONUS_CONCEPT.TYPE).from(BONUS_CONCEPT).where(BONUS_CONCEPT.ID.eq(bonusConceptId)).fetchOne(BONUS_CONCEPT.TYPE);
				
				contractBonus.setId(contractBonusRecord.get(CONTRACT_BONUS.ID));
				contractBonus.setDomain(contractBonusRecord.get(CONTRACT_BONUS.DOMAIN));
				contractBonus.setConceptId(bonusConceptId);
				contractBonus.setCodeType(null == type ? null : type.toString());
				contractBonus.setDescription(contractBonusRecord.get(CONTRACT_BONUS.DESCRIPTION));
				contractBonus.setExpression(contractBonusRecord.get(CONTRACT_BONUS.EXPRESSION));
				contractBonus.setStartDate(contractBonusRecord.get(CONTRACT_BONUS.START_DATE));
				contractBonus.setEndDate(contractBonusRecord.get(CONTRACT_BONUS.END_DATE));
				
				contractBonus.setSalaryType(Salary.Type.SALARY);
				contractBonus.setContractConceptCalcType(ContractConceptCalcType.BONUS);
				contractBonus.setHasChange(false);
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
				
				contractCost.setId(contractCostRecord.get(CONTRACT_COST.ID));
				contractCost.setDomain(contractCostRecord.get(CONTRACT_COST.DOMAIN));
				contractCost.setCodeType(null == contractCostRecord.get(CONTRACT_COST.TYPE) ? null : contractCostRecord.get(CONTRACT_COST.TYPE).toString());
				contractCost.setName(contractCostRecord.get(CONTRACT_COST.CODE));
				contractCost.setDescription(contractCostRecord.get(CONTRACT_COST.DESCRIPTION));
				contractCost.setExpression(contractCostRecord.get(CONTRACT_COST.EXPRESSION));
				contractCost.setStartDate(contractCostRecord.get(CONTRACT_COST.START_DATE));
				contractCost.setEndDate(contractCostRecord.get(CONTRACT_COST.END_DATE));
				
				contractCost.setContractConceptCalcType(ContractConceptCalcType.COST);
				contractCost.setSalaryType(Salary.Type.SALARY);
				contractCost.setHasChange(false);
				contractCosts.add(contractCost);
			}
			
			return contractCosts;
		}
	}
	
	private static List<ContractConceptCalc> getContractEmbargos(DSLContext dslContext, Integer domainId, Integer contractId) {
		Result<Record> contractEmbargoRecords = dslContext.select().from(CONTRACT_EMBARGO)
				.where(CONTRACT_EMBARGO.CONTRACT.eq(contractId))
				.and(CONTRACT_EMBARGO.DOMAIN.eq(domainId))
				.fetch();
			
		if(contractEmbargoRecords.isEmpty())
			return new ArrayList<>();
		else {
			List<ContractConceptCalc> contractEmbargos = new ArrayList<>();
			
			for(Record contractEmbargoRecord : contractEmbargoRecords) {
				ContractConceptCalc contractEmbargo = new ContractConceptCalc();
				
				contractEmbargo.setId(contractEmbargoRecord.get(CONTRACT_EMBARGO.ID));
				contractEmbargo.setDomain(contractEmbargoRecord.get(CONTRACT_EMBARGO.DOMAIN));
				contractEmbargo.setDescription(contractEmbargoRecord.get(CONTRACT_EMBARGO.DESCRIPTION));
				contractEmbargo.setExpression(contractEmbargoRecord.get(CONTRACT_EMBARGO.EXPRESSION));
				contractEmbargo.setAmount(contractEmbargoRecord.get(CONTRACT_EMBARGO.AMOUNT));
				contractEmbargo.setStartDate(contractEmbargoRecord.get(CONTRACT_EMBARGO.START_DATE));
				contractEmbargo.setEndDate(contractEmbargoRecord.get(CONTRACT_EMBARGO.END_DATE));
				
				contractEmbargo.setContractConceptCalcType(ContractConceptCalcType.EMBARGO);
				contractEmbargo.setSalaryType(Salary.Type.SALARY);
				contractEmbargo.setHasChange(false);
				contractEmbargos.add(contractEmbargo);
			}
			
			return contractEmbargos;
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
				case EMBARGO:
					updateDeleteContractEmbargos(dslContext, contractConceptCalc);
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
				.set(CONTRACT_PAYMENT.TYPE, (byte) contractConceptCalc.getType().ordinal())
				.set(CONTRACT_PAYMENT.DESCRIPTION, contractConceptCalc.getDescription())
				.set(CONTRACT_PAYMENT.EXPRESSION, contractConceptCalc.getExpression())
				.set(CONTRACT_PAYMENT.IRPF_EXPRESSION, contractConceptCalc.getIrpfExpression())
				.set(CONTRACT_PAYMENT.QUOTE_EXPRESSION, contractConceptCalc.getQuoteExpression())
				.set(CONTRACT_PAYMENT.MONTH, null == contractConceptCalc.getMonth() ? null : contractConceptCalc.getMonth().byteValue())
				.set(CONTRACT_PAYMENT.START_DATE, parseToSQLDate(contractConceptCalc.getStartDate()))
				.set(CONTRACT_PAYMENT.END_DATE, parseToSQLDate(contractConceptCalc.getEndDate()))
				.set(CONTRACT_PAYMENT.SALARY_TYPE, (byte) contractConceptCalc.getSalaryType().ordinal())
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
				.set(CONTRACT_DEDUCTION.TYPE, AonStringUtils.isBlank(contractConceptCalc.getCodeType()) ? null : Byte.parseByte(contractConceptCalc.getCodeType()))
				.set(CONTRACT_DEDUCTION.DESCRIPTION, contractConceptCalc.getDescription())
				.set(CONTRACT_DEDUCTION.EXPRESSION, contractConceptCalc.getExpression())
				.set(CONTRACT_DEDUCTION.MONTH, null == contractConceptCalc.getMonth() ? null : contractConceptCalc.getMonth().byteValue())
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
				.set(CONTRACT_COST.TYPE, AonStringUtils.isBlank(contractConceptCalc.getCodeType()) ? null : Byte.parseByte(contractConceptCalc.getCodeType()))
				.set(CONTRACT_COST.CODE, contractConceptCalc.getName())
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
		} else if(contractConceptCalc.getHasChange()) {
			dslContext.update(CONTRACT_BONUS)
				.set(CONTRACT_BONUS.DESCRIPTION, contractConceptCalc.getDescription())
				.set(CONTRACT_BONUS.EXPRESSION, contractConceptCalc.getExpression())
				.set(CONTRACT_BONUS.START_DATE, parseToSQLDate(contractConceptCalc.getStartDate()))
				.set(CONTRACT_BONUS.END_DATE, parseToSQLDate(contractConceptCalc.getEndDate()))
				.where(CONTRACT_BONUS.ID.eq(contractConceptCalc.getId()))
				.execute();
			
			Integer bonusConceptId = contractConceptCalc.getConceptId();
			if(null == bonusConceptId)
				dslContext.insertInto(BONUS_CONCEPT)
					.set(BONUS_CONCEPT.DOMAIN, contractConceptCalc.getDomain())
					.set(BONUS_CONCEPT.DESCRIPTION, contractConceptCalc.getDescription())
					.set(BONUS_CONCEPT.EXPRESSION, contractConceptCalc.getExpression())
					.set(BONUS_CONCEPT.TYPE, AonStringUtils.isBlank(contractConceptCalc.getCodeType()) ? null : Byte.parseByte(contractConceptCalc.getCodeType()))
					.execute();
			else
				dslContext.update(BONUS_CONCEPT)
					.set(BONUS_CONCEPT.TYPE, AonStringUtils.isBlank(contractConceptCalc.getCodeType()) ? null : Byte.parseByte(contractConceptCalc.getCodeType()))
					.where(BONUS_CONCEPT.ID.eq(bonusConceptId))
					.execute();
				
		}
	}
	
	private static void updateDeleteContractEmbargos(DSLContext dslContext, ContractConceptCalc contractConceptCalc) {
		if(contractConceptCalc.getId() < 0) {
			Integer id = -1 * contractConceptCalc.getId();
			dslContext.delete(CONTRACT_EMBARGO)
				.where(CONTRACT_EMBARGO.ID.eq(id))
				.execute();
		} else if(contractConceptCalc.getHasChange()) {
			dslContext.update(CONTRACT_EMBARGO)
				.set(CONTRACT_EMBARGO.DESCRIPTION, contractConceptCalc.getDescription())
				.set(CONTRACT_EMBARGO.EXPRESSION, contractConceptCalc.getExpression())
				.set(CONTRACT_EMBARGO.AMOUNT, contractConceptCalc.getAmount())
				.set(CONTRACT_EMBARGO.START_DATE, parseToSQLDate(contractConceptCalc.getStartDate()))
				.set(CONTRACT_EMBARGO.END_DATE, parseToSQLDate(contractConceptCalc.getEndDate()))
				.where(CONTRACT_EMBARGO.ID.eq(contractConceptCalc.getId()))
				.execute();
		}
	}
	
	private static void createNewContractPayment(DSLContext dslContext, Integer domainId, Integer contractId, ContractConceptCalc contractConceptCalc) {
		Integer conceptId = contractConceptCalc.getConceptId();
		if(null == conceptId) {
			conceptId = createNewConcept(dslContext, domainId, contractConceptCalc);
			contractConceptCalc.setConceptId(conceptId);
		}
		
		switch (contractConceptCalc.getContractConceptCalcType()) {
			case PAYMENT:
				createContractPayment(dslContext, domainId, contractId, contractConceptCalc);
				break;
			case DEDUCTION:
				createContractDeduction(dslContext, domainId, contractId, contractConceptCalc);
				break;
			case COST:
				createContractCost(dslContext, domainId, contractId, contractConceptCalc);
				break;
			case BONUS:
				createContractBonus(dslContext, domainId, contractId, contractConceptCalc);
				break;
			case EMBARGO:
				createContractEmbargo(dslContext, domainId, contractId, contractConceptCalc);
				break;
			default:
				break;
		}
		
	}
	
	private static Integer createNewConcept(DSLContext dslContext, Integer domainId, ContractConceptCalc contractConceptCalc) {
		switch (contractConceptCalc.getContractConceptCalcType()) {
			case PAYMENT:
				return dslContext.insertInto(PAYMENT_CONCEPT)
							.set(PAYMENT_CONCEPT.DOMAIN, domainId)
							.set(PAYMENT_CONCEPT.CODE, parseCode(contractConceptCalc.getName()))
							.set(PAYMENT_CONCEPT.TYPE, (byte)contractConceptCalc.getType().ordinal())
							.set(PAYMENT_CONCEPT.DESCRIPTION, contractConceptCalc.getDescription())
							.set(PAYMENT_CONCEPT.EXPRESSION, contractConceptCalc.getExpression())
							.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, contractConceptCalc.getIrpfExpression())
							.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, contractConceptCalc.getQuoteExpression())
							.returning(PAYMENT_CONCEPT.ID)
							.fetchOne().getId();
			case DEDUCTION:
				return dslContext.insertInto(DEDUCTION_CONCEPT)
						.set(DEDUCTION_CONCEPT.DOMAIN, domainId)
						.set(DEDUCTION_CONCEPT.CODE, parseCode(contractConceptCalc.getName()))
						.set(DEDUCTION_CONCEPT.TYPE, AonStringUtils.isBlank(contractConceptCalc.getCodeType()) ? null : Byte.parseByte(contractConceptCalc.getCodeType()))
						.set(DEDUCTION_CONCEPT.DESCRIPTION, contractConceptCalc.getDescription())
						.set(DEDUCTION_CONCEPT.EXPRESSION, contractConceptCalc.getExpression())
						.returning(DEDUCTION_CONCEPT.ID)
						.fetchOne().getId();
			case BONUS:
				return dslContext.insertInto(BONUS_CONCEPT)
						.set(BONUS_CONCEPT.DOMAIN, domainId)
						.set(BONUS_CONCEPT.DESCRIPTION, contractConceptCalc.getDescription())
						.set(BONUS_CONCEPT.EXPRESSION, contractConceptCalc.getExpression())
						.set(BONUS_CONCEPT.TYPE, AonStringUtils.isBlank(contractConceptCalc.getCodeType()) ? null : Byte.parseByte(contractConceptCalc.getCodeType()))
						.returning(BONUS_CONCEPT.ID)
						.fetchOne().getId();
			case COST:
				return null;
			case EMBARGO:
				return null;
			default:
				return null;
		}
	}

	private static String parseCode(String code) {
		return AonStringUtils.isBlank(code) ? code : code.replaceAll("\\s", "_").toUpperCase();
	}

	private static void createContractPayment(DSLContext dslContext, Integer domainId, Integer contractId, ContractConceptCalc contractConceptCalc) {
		dslContext.insertInto(CONTRACT_PAYMENT)
			.set(CONTRACT_PAYMENT.DOMAIN, domainId)
			.set(CONTRACT_PAYMENT.CONTRACT, contractId)
			.set(CONTRACT_PAYMENT.TYPE, (byte)contractConceptCalc.getType().ordinal())
			.set(CONTRACT_PAYMENT.PAYMENT_CONCEPT, contractConceptCalc.getConceptId())
			.set(CONTRACT_PAYMENT.DESCRIPTION, contractConceptCalc.getDescription())
			.set(CONTRACT_PAYMENT.EXPRESSION, contractConceptCalc.getExpression())
			.set(CONTRACT_PAYMENT.IRPF_EXPRESSION, contractConceptCalc.getIrpfExpression())
			.set(CONTRACT_PAYMENT.QUOTE_EXPRESSION, contractConceptCalc.getQuoteExpression())
			.set(CONTRACT_PAYMENT.MONTH, null == contractConceptCalc.getMonth() ? null : contractConceptCalc.getMonth().byteValue())
			.set(CONTRACT_PAYMENT.START_DATE, null == contractConceptCalc.getStartDate() ? getContractStartDate(dslContext, contractId) : parseToSQLDate(contractConceptCalc.getStartDate()))
			.set(CONTRACT_PAYMENT.END_DATE, parseToSQLDate(contractConceptCalc.getEndDate()))
			.set(CONTRACT_PAYMENT.SALARY_TYPE, (byte) contractConceptCalc.getSalaryType().ordinal())
			.execute();
	}

	private static void createContractDeduction(DSLContext dslContext, Integer domainId, Integer contractId, ContractConceptCalc contractConceptCalc) {
		dslContext.insertInto(CONTRACT_DEDUCTION)
			.set(CONTRACT_DEDUCTION.DOMAIN, domainId)
			.set(CONTRACT_DEDUCTION.CONTRACT, contractId)
			.set(CONTRACT_DEDUCTION.TYPE, AonStringUtils.isBlank(contractConceptCalc.getCodeType()) ? null : Byte.parseByte(contractConceptCalc.getCodeType()))
			.set(CONTRACT_DEDUCTION.DEDUCTION_CONCEPT, contractConceptCalc.getConceptId())
			.set(CONTRACT_DEDUCTION.DESCRIPTION, contractConceptCalc.getDescription())
			.set(CONTRACT_DEDUCTION.EXPRESSION, contractConceptCalc.getExpression())
			.set(CONTRACT_DEDUCTION.MONTH, null == contractConceptCalc.getMonth() ? null : contractConceptCalc.getMonth().byteValue())
			.set(CONTRACT_DEDUCTION.START_DATE, null == contractConceptCalc.getStartDate() ? getContractStartDate(dslContext, contractId) : parseToSQLDate(contractConceptCalc.getStartDate()))
			.set(CONTRACT_DEDUCTION.END_DATE, parseToSQLDate(contractConceptCalc.getEndDate()))
			.execute();
	}

	private static void createContractCost(DSLContext dslContext, Integer domainId, Integer contractId, ContractConceptCalc contractConceptCalc) {
		dslContext.insertInto(CONTRACT_COST)
			.set(CONTRACT_COST.DOMAIN, domainId)
			.set(CONTRACT_COST.CONTRACT, contractId)
			.set(CONTRACT_COST.TYPE, AonStringUtils.isBlank(contractConceptCalc.getCodeType()) ? null : Byte.parseByte(contractConceptCalc.getCodeType()))
			.set(CONTRACT_COST.CODE, parseCode(contractConceptCalc.getName()))
			.set(CONTRACT_COST.DESCRIPTION, contractConceptCalc.getDescription())
			.set(CONTRACT_COST.EXPRESSION, contractConceptCalc.getExpression())
			.set(CONTRACT_COST.START_DATE, null == contractConceptCalc.getStartDate() ? getContractStartDate(dslContext, contractId) : parseToSQLDate(contractConceptCalc.getStartDate()))
			.set(CONTRACT_COST.END_DATE, parseToSQLDate(contractConceptCalc.getEndDate()))
			.execute();		
	}

	private static void createContractBonus(DSLContext dslContext, Integer domainId, Integer contractId, ContractConceptCalc contractConceptCalc) {
		dslContext.insertInto(CONTRACT_BONUS)
			.set(CONTRACT_BONUS.DOMAIN, domainId)
			.set(CONTRACT_BONUS.CONTRACT, contractId)
			.set(CONTRACT_BONUS.BONUS_CONCEPT, contractConceptCalc.getConceptId())
			.set(CONTRACT_BONUS.DESCRIPTION, contractConceptCalc.getDescription())
			.set(CONTRACT_BONUS.EXPRESSION, contractConceptCalc.getExpression())
			.set(CONTRACT_BONUS.START_DATE, null == contractConceptCalc.getStartDate() ? getContractStartDate(dslContext, contractId) : parseToSQLDate(contractConceptCalc.getStartDate()))
			.set(CONTRACT_BONUS.END_DATE, parseToSQLDate(contractConceptCalc.getEndDate()))
			.execute();	
	}
	
	private static void createContractEmbargo(DSLContext dslContext, Integer domainId, Integer contractId, ContractConceptCalc contractConceptCalc) {
		dslContext.insertInto(CONTRACT_EMBARGO)
			.set(CONTRACT_EMBARGO.DOMAIN, domainId)
			.set(CONTRACT_EMBARGO.CONTRACT, contractId)
			.set(CONTRACT_EMBARGO.DESCRIPTION, contractConceptCalc.getDescription())
			.set(CONTRACT_EMBARGO.EXPRESSION, contractConceptCalc.getExpression())
			.set(CONTRACT_EMBARGO.AMOUNT, contractConceptCalc.getAmount())
			.set(CONTRACT_EMBARGO.START_DATE, null == contractConceptCalc.getStartDate() ? getContractStartDate(dslContext, contractId) : parseToSQLDate(contractConceptCalc.getStartDate()))
			.set(CONTRACT_EMBARGO.END_DATE, parseToSQLDate(contractConceptCalc.getEndDate()))
			.execute();	
	}

	private static ContractConcepts getAllConcepts(DSLContext dslContext, Integer domainId) {
		ContractConcepts contractConcept = new ContractConcepts();
		contractConcept.setPaymentConcepts(getPaymentConcepts(dslContext, domainId));
		contractConcept.setDeductionConcepts(getDeductionConcepts(dslContext, domainId));
		contractConcept.setBonusConcepts(getBonusConcepts(dslContext, domainId));
		contractConcept.setCostConcepts(getCostConcepts(dslContext, domainId));
		return contractConcept;
	}

	private static Set<ContractConcept> getPaymentConcepts(DSLContext dslContext, Integer domainId) {
		Set<ContractConcept> paymentConcepts = new HashSet<>();
		
		Result<Record> paymentConceptRecords = dslContext.select().from(PAYMENT_CONCEPT).where(PAYMENT_CONCEPT.DOMAIN.eq(0)).fetch();
		
		for(Record paymentConceptRecord : paymentConceptRecords) {
			ContractConcept contractConcept = new ContractConcept();
			contractConcept.setId(paymentConceptRecord.get(PAYMENT_CONCEPT.ID))
							.setCode(paymentConceptRecord.get(PAYMENT_CONCEPT.CODE))
							.setType(paymentConceptRecord.get(PAYMENT_CONCEPT.TYPE))
							.setDescription(paymentConceptRecord.get(PAYMENT_CONCEPT.DESCRIPTION))
							.setExpression(paymentConceptRecord.get(PAYMENT_CONCEPT.EXPRESSION));
			paymentConcepts.add(contractConcept);
		}
		
		return paymentConcepts;
	}

	private static Set<ContractConcept> getDeductionConcepts(DSLContext dslContext, Integer domainId) {
		Set<ContractConcept> deductionConcepts = new HashSet<>();
		
		Result<Record> deductionConceptRecords = dslContext.select().from(DEDUCTION_CONCEPT).where(DEDUCTION_CONCEPT.DOMAIN.eq(0)).fetch();
		
		for(Record deductionConceptRecord : deductionConceptRecords) {
			ContractConcept contractConcept = new ContractConcept();
			contractConcept.setId(deductionConceptRecord.get(DEDUCTION_CONCEPT.ID))
							.setCode(deductionConceptRecord.get(DEDUCTION_CONCEPT.CODE))
							.setType(deductionConceptRecord.get(DEDUCTION_CONCEPT.TYPE))
							.setDescription(deductionConceptRecord.get(DEDUCTION_CONCEPT.DESCRIPTION))
							.setExpression(deductionConceptRecord.get(DEDUCTION_CONCEPT.EXPRESSION));
			deductionConcepts.add(contractConcept);
		}
		
		return deductionConcepts;
	}

	private static Set<ContractConcept> getBonusConcepts(DSLContext dslContext, Integer domainId) {
		Set<ContractConcept> bonusConcepts = new HashSet<>();
		
		Result<Record> bonusConceptRecords = dslContext.select().from(BONUS_CONCEPT).where(BONUS_CONCEPT.DOMAIN.eq(0)).fetch();
		
		for(Record bonusConceptRecord : bonusConceptRecords) {
			ContractConcept contractConcept = new ContractConcept();
			contractConcept.setId(bonusConceptRecord.get(BONUS_CONCEPT.ID))
							.setType(bonusConceptRecord.get(BONUS_CONCEPT.TYPE))
							.setDescription(bonusConceptRecord.get(BONUS_CONCEPT.DESCRIPTION))
							.setExpression(bonusConceptRecord.get(BONUS_CONCEPT.EXPRESSION));
			bonusConcepts.add(contractConcept);
		}
		
		return bonusConcepts;
	}
	
	private static Set<ContractConcept> getCostConcepts(DSLContext dslContext, Integer domainId) {
		Set<ContractConcept> costConcepts = new HashSet<>();
		
		Result<Record> costConceptRecords = dslContext.select().from(SYSTEM_COST).where(SYSTEM_COST.DOMAIN.eq(0)).fetch();
		
		for(Record costConceptRecord : costConceptRecords) {
			ContractConcept contractConcept = new ContractConcept();
			contractConcept.setId(costConceptRecord.get(SYSTEM_COST.ID))
							.setCode(costConceptRecord.get(SYSTEM_COST.CODE))
							.setType(costConceptRecord.get(SYSTEM_COST.TYPE))
							.setDescription(costConceptRecord.get(SYSTEM_COST.DESCRIPTION))
							.setExpression(costConceptRecord.get(SYSTEM_COST.EXPRESSION));
			costConcepts.add(contractConcept);
		}
		
		return costConcepts;
	}

}
