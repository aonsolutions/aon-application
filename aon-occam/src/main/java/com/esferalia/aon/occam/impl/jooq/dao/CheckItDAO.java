package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.BankStatement.BANK_STATEMENT;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jooq.AggregateFunction;
import org.jooq.InsertValuesStep11;
import org.jooq.Record2;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.BankStatementRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.BankStatement;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.watson.util.AonEnumUtils;

public class CheckItDAO {
	
	public static final String CHECKIT_R1 = "CHECKIT";
	
	private CheckItDAO() {
	    throw new IllegalStateException("Utility class");
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
	
	public static Integer insertStatements(AONContext aonContext, List<BankStatement> bankStatements) {
		
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
			// BankStatemetn Validation
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
		AggregateFunction<Integer> LOT = DSL.max(BANK_STATEMENT.LOT_NUMBER);
		return aonContext.getDslContext().select( LOT )
			.from(BANK_STATEMENT)
			.where(BANK_STATEMENT.RBANK.eq(rbank.getId()))
			.and(BANK_STATEMENT.DOMAIN.eq(domainId))
			.fetch()
			.stream()
			.map( r -> r.get(LOT))
			.findFirst()
			.orElse(1);
	}

	public static Date getLastOperationDateDB(AONContext aonContext, Integer domainId, RegistryBank rbank) {
		
		Map<String, Date> max = getMaxMovementIdAndDate(aonContext, domainId, rbank);
		String maxId = null;
		Date lastOperationDate = null;

		if (max.isEmpty()) {
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
			maxId = max.keySet().stream().findFirst().orElse(null);
			if (maxId != null) {				
				lastOperationDate = max.get(maxId);
				// AÑADIR 1 DÍA MÁS PARA QUE NO HAYA DUPLICADOS
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(lastOperationDate);
				calendar.add(Calendar.DATE, 1);
				lastOperationDate = calendar.getTime();
			}
		}

		if (maxId == null && lastOperationDate == null) {
			lastOperationDate = cleanDate(1, Calendar.JANUARY, Calendar.getInstance().get(Calendar.YEAR));
		}
		return lastOperationDate;
	}
	
	public static Map<String, Date> getMaxMovementIdAndDate(AONContext aonContext, Integer domainId, RegistryBank rbank) {
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
			return Collections.emptyMap();
		} else
			return Collections.singletonMap(id, utilDate);
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
