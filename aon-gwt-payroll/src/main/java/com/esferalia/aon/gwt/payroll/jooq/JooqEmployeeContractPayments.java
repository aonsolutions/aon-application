package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.jooq.tables.ContractDeduction.CONTRACT_DEDUCTION;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.ContractDeduction;
import com.esferalia.aon.gwt.payroll.shared.ContractPayment;
import com.esferalia.aon.gwt.payroll.shared.ContractPaymentData;

public class JooqEmployeeContractPayments {

	private static Settings SETTINGS = null;
	
	// ------------------------------- Auxiliar Methods
	
	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}

	private static Date parseToSQLDate(java.util.Date date) {
		if(null == date)
			return null;

//		DateUtils.resetTime(date);
		return new Date(date.getTime());
	}
	
	// ------------------------------- Database Methods

	public static ContractPaymentData getContractPayements(Connection conn, Integer domainId, Integer contractId) {
		return getContractPayements(DSL.using(conn, getDefaultSettings()), domainId, contractId);	
	}

	public static void updateContractPayments(Connection conn, Integer domainId, Integer contractId, ContractPaymentData contractPaymentData) {
		updateContractPayments(DSL.using(conn, getDefaultSettings()), domainId, contractId, contractPaymentData);
	}

	private static ContractPaymentData getContractPayements(DSLContext dslContext, Integer domainId, Integer contractId) {
		ContractPaymentData contractPaymentData = new ContractPaymentData();
		
		// Contract Payments
		
		Result<Record> contractPaymentRecords = dslContext.select().from(CONTRACT_PAYMENT)
			.where(CONTRACT_PAYMENT.CONTRACT.eq(contractId))
			.and(CONTRACT_PAYMENT.DOMAIN.eq(domainId))
			.fetch();
		
		if(contractPaymentRecords.isEmpty())
			contractPaymentData.setContractPayments(new ArrayList<ContractPayment>());
		else {
			List<ContractPayment> contractPayments = new ArrayList<ContractPayment>();
			for(Record record : contractPaymentRecords) {
				ContractPayment contractPayment = new ContractPayment();
				contractPayment.setId(record.get(CONTRACT_PAYMENT.ID))
								.setType(record.get(CONTRACT_PAYMENT.TYPE))
								.setDescription(record.get(CONTRACT_PAYMENT.DESCRIPTION))
								.setExpression(record.get(CONTRACT_PAYMENT.EXPRESSION))
								.setStartDate(record.get(CONTRACT_PAYMENT.START_DATE))
								.setEndDate(record.get(CONTRACT_PAYMENT.END_DATE))
								.setHasChange(false);
				contractPayments.add(contractPayment);
			}
			contractPaymentData.setContractPayments(contractPayments);
		}
		
		// Contract Deductions
		
		Result<Record> contractDeductionRecords = dslContext.select().from(CONTRACT_DEDUCTION)
			.where(CONTRACT_DEDUCTION.CONTRACT.eq(contractId))
			.and(CONTRACT_DEDUCTION.DOMAIN.eq(domainId))
			.fetch();
		
		if(contractDeductionRecords.isEmpty())
			contractPaymentData.setContractDeductions(new ArrayList<ContractDeduction>());
		else {
			List<ContractDeduction> contractDeductions = new ArrayList<ContractDeduction>();
			for(Record record : contractDeductionRecords) {
				ContractDeduction contractDeduction = new ContractDeduction();
				contractDeduction.setId(record.get(CONTRACT_DEDUCTION.ID))
								.setType(record.get(CONTRACT_DEDUCTION.TYPE))
								.setDescription(record.get(CONTRACT_DEDUCTION.DESCRIPTION))
								.setExpression(record.get(CONTRACT_DEDUCTION.EXPRESSION))
								.setStartDate(record.get(CONTRACT_DEDUCTION.START_DATE))
								.setEndDate(record.get(CONTRACT_DEDUCTION.END_DATE))
								.setHasChange(false);
				contractDeductions.add(contractDeduction);
			}
			contractPaymentData.setContractDeductions(contractDeductions);
		}
		
		return contractPaymentData;
	}

	private static void updateContractPayments(DSLContext dslContext, Integer domainId, Integer contractId, ContractPaymentData contractPaymentData) {
		List<ContractPayment> contractPayments = contractPaymentData.getContractPayments();
		List<ContractDeduction> contractDeductions = contractPaymentData.getContractDeductions();
		
		// ContractPayments
		
		for(ContractPayment contractPayment : contractPayments) {
			if(contractPayment.getId() < 0) {
				Integer id = -1 * contractPayment.getId();
				dslContext.delete(CONTRACT_PAYMENT)
					.where(CONTRACT_PAYMENT.ID.eq(id))
					.execute();
			} else if(contractPayment.getHasChange())
				dslContext.update(CONTRACT_PAYMENT)
					.set(CONTRACT_PAYMENT.DESCRIPTION, contractPayment.getDescription())
					.set(CONTRACT_PAYMENT.TYPE, contractPayment.getType())
					.set(CONTRACT_PAYMENT.EXPRESSION, contractPayment.getExpression())
					.set(CONTRACT_PAYMENT.START_DATE, parseToSQLDate(contractPayment.getStartDate()))
					.set(CONTRACT_PAYMENT.END_DATE, parseToSQLDate(contractPayment.getEndDate()))
					.where(CONTRACT_PAYMENT.ID.eq(contractPayment.getId()))
					.execute();
		}
		
		// ContractDeductions
		
		for(ContractDeduction contractDeduction : contractDeductions) {
			if(contractDeduction.getId() < 0) {
				Integer id = -1 * contractDeduction.getId();
				dslContext.delete(CONTRACT_DEDUCTION)
					.where(CONTRACT_DEDUCTION.ID.eq(id))
					.execute();
			} else if(contractDeduction.getHasChange())
				dslContext.update(CONTRACT_DEDUCTION)
					.set(CONTRACT_DEDUCTION.DESCRIPTION, contractDeduction.getDescription())
					.set(CONTRACT_DEDUCTION.TYPE, contractDeduction.getType())
					.set(CONTRACT_DEDUCTION.EXPRESSION, contractDeduction.getExpression())
					.set(CONTRACT_DEDUCTION.START_DATE, parseToSQLDate(contractDeduction.getStartDate()))
					.set(CONTRACT_DEDUCTION.END_DATE, parseToSQLDate(contractDeduction.getEndDate()))
					.where(CONTRACT_DEDUCTION.ID.eq(contractDeduction.getId()))
					.execute();
		}
	}
}
