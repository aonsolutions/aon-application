package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.BankStatement.BANK_STATEMENT;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Rbank.RBANK;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jooq.AggregateFunction;
import org.jooq.InsertValuesStep11;
import org.jooq.Record2;
import org.jooq.exception.NoDataFoundException;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.BankStatementRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.BankStatement;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItBankAccount;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItBankStatement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountBalance;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankAccount;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.impl.jooq.validation.BankStatementValidator;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.Pair;

public class CheckItDAO {
	
	public static final String CHECKIT_R1 = "CHECKIT";
	
	private CheckItDAO() {
	    throw new IllegalStateException("Utility class");
	}
	
	private static Integer getEnterpriseId(AONContext aonContext, Integer domainId) {
			return aonContext.getDslContext()
			.select(ENTERPRISE.REGISTRY)
			.from(ENTERPRISE)
			.where(ENTERPRISE.DOMAIN.eq(domainId))
			.fetchAnyInto(ENTERPRISE).getRegistry();
	}
	
	public static com.esferalia.aon.occam.api.model.Enterprise getEnterprise(Integer domainId, String domainName, String user) {
		try (CloseableAONContext aonContext = AONContext.getAONContext(domainName, domainId, user)) {
			Integer enterpriseId = getEnterpriseId(aonContext, domainId);
			return AON.getEnterprise(domainName, domainId, user, enterpriseId);
		}
	}
	
	public static Integer getParentDomain(String domainName, Integer domainId, String user) {
		try (CloseableAONContext aonContext = AONContext.getAONContext(domainName, domainId, user)) {
			return aonContext.getDslContext()
			.select(DOMAIN.PARENT)
			.from(DOMAIN)
			.where(DOMAIN.ID.eq(domainId))
			.fetchAnyInto(DOMAIN)
			.getParent();
		}
	}
	
	public static boolean saveCheckItEnterpriseId(String domainName, Integer domainId, String user, Integer checkItEnterpriseid) {
		
		if (checkItEnterpriseid == null || checkItEnterpriseid == 0)
			return false;
		
		try (CloseableAONContext aonContext = AONContext.getAONContext(domainName, domainId, user)) {
			return aonContext.getDslContext().transactionResult(config ->
				aonContext.getDslContext()
				.insertInto(APP_PARAM, APP_PARAM.DOMAIN, APP_PARAM.NAME, APP_PARAM.VALUE)
				.values(domainId
						, com.esferalia.aon.occam.api.model.type.AppParam.CHECK_IT_ENTERPRISE_ID.toString()
						, AonNumberUtils.toString(checkItEnterpriseid))
				.execute()
			) > 0;
		}
	}
	
	public static <E extends BankStatement> void completeBankStatements(AONContext aonContext, Integer domainId, String iban, List<E> bankStatements) {
		RegistryBank rbank = getRbankByIban(aonContext, iban);
		Integer lotNumber = getNextLotNumber(aonContext, domainId, rbank);
		bankStatements.forEach(bankStatement -> {
			if (bankStatement != null) {
				bankStatement.setDomain(domainId);
				bankStatement.setRegistryBank(rbank);
				bankStatement.setLotNumber(lotNumber);
			}
		});
	}
	
	public static Integer insertStatements(AONContext aonContext, List<CheckItBankStatement> bankStatements) throws AonCoreException {
		
		InsertValuesStep11<BankStatementRecord, Integer, Integer, Integer, java.sql.Date, Byte, Byte, Double, String, Byte, String, String> query =
				aonContext.getDslContext()
				.insertInto(
						  BANK_STATEMENT
						, BANK_STATEMENT.DOMAIN
						, BANK_STATEMENT.RBANK
						, BANK_STATEMENT.LOT_NUMBER
						, BANK_STATEMENT.OPERATION_DATE
						, BANK_STATEMENT.COMMON_CONCEPT
						, BANK_STATEMENT.PAYMENT
						, BANK_STATEMENT.AMOUNT
						, BANK_STATEMENT.DESCRIPTION
						, BANK_STATEMENT.STATUS
						, BANK_STATEMENT.REFERENCE1
						, BANK_STATEMENT.REFERENCE2
					);
		for (BankStatement bankStatement : bankStatements) {
			// BankStatement Validation
			BankStatementValidator.validate(aonContext, bankStatement);
			
			query = query.values(
					  bankStatement.getDomain()
					, bankStatement.getRegistryBank() != null ? bankStatement.getRegistryBank().getId() : null
					, bankStatement.getLotNumber()
					, bankStatement.getOperationDate() != null ? new java.sql.Date(bankStatement.getOperationDate().getTime()) : null
					, bankStatement.getCommonConcept().value()
					, AonEnumUtils.getByte(bankStatement.isPayment())
					, bankStatement.getAmount()
					, bankStatement.getDescription()
					, bankStatement.getStatus().value()
					, bankStatement.getReference1()
					, bankStatement.getReference2()
			);
		}
		
		return query.execute();
	}

	public static RegistryBank getRbankByIban(AONContext aonContext, String iban) {
		return AON.getRBank(aonContext.getDomainName()
				, aonContext.getDomainId()
				, aonContext.getUser()
				, f -> f.getDomainProperty().eq(aonContext.getDomainId()).and(f.getBankAccountProperty().eq(iban))
		);
	}

	private static int getNextLotNumber(AONContext aonContext, Integer domainId, RegistryBank rbank) {
		AggregateFunction<Integer> lot = DSL.max(BANK_STATEMENT.LOT_NUMBER);
		try {			
			return AonNumberUtils.zeroIfNull(aonContext.getDslContext().select( lot )
					.from(BANK_STATEMENT)
					.where(BANK_STATEMENT.RBANK.eq(rbank.getId()))
					.and(BANK_STATEMENT.DOMAIN.eq(domainId))
					.fetchSingle().get(lot)) + 1;
		} catch (NoDataFoundException e) {
			return 1;
		}
	}
	
	public static Date getLastMovementDateNoId(AONContext aonContext, Integer domainId, RegistryBank rbank) {
		try {
			java.sql.Date date = (java.sql.Date) aonContext.getDslContext()
					.select(DSL.max(BANK_STATEMENT.OPERATION_DATE).as("date"))
					.from(BANK_STATEMENT)
					.where(BANK_STATEMENT.DOMAIN.eq(domainId))
					.and(BANK_STATEMENT.RBANK.eq(rbank.getId()))
					.fetchSingle().get("date");
			if (date != null) {
				return new Date(date.getTime());
			}
			return null;			
		} catch (NoDataFoundException e) {
			return null;			
		}
	}

	public static Date getLastOperationDateDB(AONContext aonContext, Integer domainId, RegistryBank rbank) {
		
		Pair<String, Date> maxWithId = getMaxMovementIdAndDate(aonContext, domainId, rbank);
		Date maxWithNoId = getLastMovementDateNoId(aonContext, domainId, rbank);
		
		Date lastOperationDate = null;
		if (com.esferalia.aon.watson.util.AonDateUtils.compare(maxWithNoId, maxWithId != null ? maxWithId.getValue() : null) > 0) {
			lastOperationDate = maxWithNoId;
		} else {
			lastOperationDate = maxWithId != null ? maxWithId.getValue() : null;
		}
		
		if (lastOperationDate == null) {
			lastOperationDate = cleanDate(31, Calendar.DECEMBER, Calendar.getInstance().get(Calendar.YEAR) - 1);
		}
		return lastOperationDate;
	}
	
	public static Pair<String, Date> getMaxMovementIdAndDate(AONContext aonContext, Integer domainId, RegistryBank rbank) {		
		
		try {
			Record2<String, java.sql.Date> result = aonContext.getDslContext()
					.select(BANK_STATEMENT.REFERENCE2, BANK_STATEMENT.OPERATION_DATE)
					.from(BANK_STATEMENT)
					.where(BANK_STATEMENT.REFERENCE1.eq(CHECKIT_R1))
					.and(BANK_STATEMENT.RBANK.eq(rbank.getId()))
					.and(BANK_STATEMENT.DOMAIN.eq(domainId))
					.and(BANK_STATEMENT.REFERENCE2.eq(
							DSL.select(DSL.max(BANK_STATEMENT.REFERENCE2))
							.from(BANK_STATEMENT)
							.where(BANK_STATEMENT.REFERENCE1.eq(CHECKIT_R1))
							.and(BANK_STATEMENT.RBANK.eq(rbank.getId()))
							.and(BANK_STATEMENT.DOMAIN.eq(domainId)))
					).fetchSingle();
					
					String id = (String) result.get(BANK_STATEMENT.REFERENCE2);
					java.sql.Date date = result.get(BANK_STATEMENT.OPERATION_DATE);
					Date utilDate = date != null ? new Date(date.getTime()) : null;
					
					if (id == null || id.isEmpty()) {
						return null;
					} else
						return new Pair<>(id, utilDate);
		} catch (NoDataFoundException e) {
			return null;
		}
	}
	
	public static List<String> getActiveIbans(String domainName, Integer domainId, String user) {
		try {			
			Company company = AON.getCompany(domainName, domainId, user, f -> f.getDomainProperty().eq(domainId));
			return AON.getRBankList(domainName
					, domainId
					, user
					, f -> f.getDomainProperty().eq(domainId)
					.and(f.getRegistryProperty().eq(company.getId()))
					.and(f.getActiveProperty().eq(AonEnumUtils.getByte(true))))
					.stream()
					.filter(Objects::nonNull)
					.map(rb -> rb.getBankAccount() != null ? rb.getBankAccount().getIban() : null)
					.filter(Objects::nonNull)
					.collect(Collectors.toList());
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}
	
	
	private static Date cleanDate(int day, int month, int year) {
		try {
			Calendar calendar = Calendar.getInstance(new Locale("es", "ES"));
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.HOUR, 0);
			calendar.set(Calendar.DAY_OF_MONTH, day);
			calendar.set(Calendar.MONTH, month);
			calendar.set(Calendar.YEAR, year);
			return calendar.getTime();
		} catch (Exception e) {
			return null;
		}
	}
	
	
	public static void updateRegistryBank(String domainName, Integer domainId, String user, CheckItBankAccount account) {
		try (CloseableAONContext aonContext = AONContext.getAONContext(domainName, domainId, user)) {
			String iban = account.getCcc();
			RegistryBank rb = getRbankByIban(aonContext, iban);
			if (rb != null && !rb.isEmpty()) {
				double balance = account.getBalance();
				double remainder = account.getRemainder();
				aonContext.getDslContext().update(RBANK)
					.set(RBANK.BALANCE, AonNumberUtils.zeroIfNull(balance))
					.set(RBANK.AVAILABLE_BALANCE, AonNumberUtils.zeroIfNull(remainder))
					.set(RBANK.BALANCE_DATE, new Timestamp(new Date().getTime()))
					.where(RBANK.ID.eq(rb.getId()))
					.and(RBANK.REQUISITION.isNull())	// SKIP NORDIGEN BANKS.
					.execute();
			}
		}
	}
	

}
