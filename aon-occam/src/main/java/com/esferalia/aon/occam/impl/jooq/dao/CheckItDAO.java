package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.BankStatement.BANK_STATEMENT;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Rbank.RBANK;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.jooq.AggregateFunction;
import org.jooq.InsertValuesStep11;
import org.jooq.Record2;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.BankStatementRecord;
import com.esferalia.aon.jooq.tables.records.RbankRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.BankStatement;
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
		try (AONContext aonContext = AONContext.getAONContext(domainName, domainId, user)) {
			Integer enterpriseId = getEnterpriseId(aonContext, domainId);
			return AON.getEnterprise(domainName, domainId, user, enterpriseId);
		}
	}
	
	public static Integer getParentDomain(String domainName, Integer domainId, String user) {
		try (AONContext aonContext = AONContext.getAONContext(domainName, domainId, user)) {
			return aonContext.getDslContext()
			.select(DOMAIN.PARENT)
			.from(DOMAIN)
			.where(DOMAIN.ID.eq(domainId))
			.fetchAnyInto(DOMAIN)
			.getParent();
		}
	}
	
	public static boolean saveCheckItEnterpriseId(String domainName, Integer domainId, String user, Integer checkItEnterpriseid) {
		
		if (checkItEnterpriseid == null)
			return false;
		
		try (AONContext aonContext = AONContext.getAONContext(domainName, domainId, user)) {
			return aonContext.getDslContext().transactionResult(confi ->
				aonContext.getDslContext()
				.insertInto(APP_PARAM, APP_PARAM.DOMAIN, APP_PARAM.NAME, APP_PARAM.VALUE)
				.values(domainId
						, com.esferalia.aon.occam.api.model.type.AppParam.CHECK_IT_ENTERPRISE_ID.toString()
						, AonNumberUtils.toString(checkItEnterpriseid))
				.execute()
			) > 0;
		}
	}
	
	public static void completeBankStatements(AONContext aonContext, Integer domainId, String iban, List<BankStatement> bankStatements) {
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
	
	public static Integer insertStatements(AONContext aonContext, List<BankStatement> bankStatements) throws AonCoreException {
		
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
		return aonContext.getDslContext().select( lot )
			.from(BANK_STATEMENT)
			.where(BANK_STATEMENT.RBANK.eq(rbank.getId()))
			.and(BANK_STATEMENT.DOMAIN.eq(domainId))
			.fetch()
			.stream()
			.map( r -> r.get(lot))
			.findFirst()
			.orElse(1);
	}

	public static Date getLastOperationDateDB(AONContext aonContext, Integer domainId, RegistryBank rbank) {
		
		Pair<String, Date> max = getMaxMovementIdAndDate(aonContext, domainId, rbank);
		String maxId = null;
		Date lastOperationDate = null;
		if (max == null) {
			java.sql.Date date = (java.sql.Date) aonContext.getDslContext()
					.select(DSL.max(BANK_STATEMENT.OPERATION_DATE).as("date"))
					.from(BANK_STATEMENT)
					.where(BANK_STATEMENT.DOMAIN.eq(domainId))
					.and(BANK_STATEMENT.RBANK.eq(rbank.getId()))
					.fetchSingle().get("date");
					if (date == null)
						lastOperationDate = null;
					else
						lastOperationDate = new Date(date.getTime());
		} else {
			maxId = max.getKey();
			if (maxId != null) {				
				lastOperationDate = max.getValue();
				// AÑADIR 1 DÍA MÁS PARA QUE NO HAYA DUPLICADOS
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(lastOperationDate);
				calendar.add(Calendar.DATE, 1);
				lastOperationDate = calendar.getTime();
			}
		}

		if (maxId == null && lastOperationDate == null) {
			lastOperationDate = cleanDate(31, Calendar.DECEMBER, Calendar.getInstance().get(Calendar.YEAR) - 1);
		}
		return lastOperationDate;
	}
	
	public static Pair<String, Date> getMaxMovementIdAndDate(AONContext aonContext, Integer domainId, RegistryBank rbank) {
		Record2<String, java.sql.Date> result = aonContext.getDslContext()
		.select(DSL.max(BANK_STATEMENT.REFERENCE2).as("maximum"), BANK_STATEMENT.OPERATION_DATE)
		.from(BANK_STATEMENT)
		.where(BANK_STATEMENT.REFERENCE1.eq(CHECKIT_R1))
		.and(BANK_STATEMENT.RBANK.eq(rbank.getId()))
		.and(BANK_STATEMENT.DOMAIN.eq(domainId))
		.fetchSingle();
		
		String id = (String) result.get("maximum");
		java.sql.Date date = result.get(BANK_STATEMENT.OPERATION_DATE);
		Date utilDate = date != null ? new Date(date.getTime()) : null;
		
		if (id == null || id.isEmpty()) {
			return null;
		} else
			return new Pair<String, Date>(id, utilDate);
	}
	
	public static List<String> getActiveIbans(String domainName, Integer domainId, String user) {
		try (AONContext aonContext = AONContext.getAONContext(domainName, domainId, user)) {
			return aonContext.getDslContext()
			.select(RBANK.BANK_ACCOUNT)
			.from(RBANK)
			.where(RBANK.DOMAIN.eq(domainId))
			.fetchStreamInto(RBANK)
			.map(RbankRecord::getBankAccount)
			.collect(Collectors.toList());
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
}
