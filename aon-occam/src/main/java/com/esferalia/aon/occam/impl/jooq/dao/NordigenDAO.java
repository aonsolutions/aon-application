package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.BankStatement.BANK_STATEMENT;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.AggregateFunction;
import org.jooq.InsertValuesStep11;
import org.jooq.Record2;
import org.jooq.exception.NoDataFoundException;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.BankStatementRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankAccount;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankStatement;
import com.esferalia.aon.occam.api.model.registry.RegistryAddInfo;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.StatementConcept;
import com.esferalia.aon.occam.api.model.type.StatementReliability;
import com.esferalia.aon.occam.api.model.type.StatementStatus;
import com.esferalia.aon.occam.impl.jooq.validation.BankStatementValidator;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;

public class NordigenDAO {
	
	public static final String NORDIGEN_R1 = "NORDIGEN";
	private static final String RADD_INFO_REQUISITION_ATTRIBUTE_PATTERN = "NORDIGEN\\[(?<rbank>\\d+)\\]";
	
	private NordigenDAO() {
	    throw new IllegalStateException("Utility class");
	}
		
	private static Integer getRbankIdFromRaddinfo(RegistryAddInfo raddinfo) {
		if (raddinfo == null) {
			return null;
		}
		final Pattern aatrRegex = Pattern.compile(RADD_INFO_REQUISITION_ATTRIBUTE_PATTERN);
		Matcher matcher = aatrRegex.matcher(null);
		if (matcher.matches()) {
			String id = matcher.group("rbank");
			if (AonStringUtils.isNotBlank(id)) {
				return AonNumberUtils.toInteger(id);
			}
		}
		return null;
	}
	
	
	public static RegistryAddInfo getRaddInfoByRaddressId(Domain domain, String login, Integer rbankId) {
		return AON.getRegistryAddInfo(domain.getName(), domain.getId(), login, f -> f.getAttributeProperty().eq("NORDIGEN[" + rbankId + "]")).orElse(null);
	}
	
	public static Stream<RegistryAddInfo> getAllNordigenRaddInfos(Domain domain, String login) {
		return AON.getRegistryAddInfoStream(domain.getName(), domain.getId(), login, f -> f.getAttributeProperty().like("NORDIGEN[%]"));
	}
	
	public static List<RegistryBank> getLinkedRbanks(Domain domain, String login) {
		return getAllNordigenRaddInfos(domain, login)
		.map(raddInfo -> AON.getRegistryBank(domain, login, f -> f.getIdProperty().eq(getRbankIdFromRaddinfo(raddInfo))))
		.collect(Collectors.toList());
	}
	
	public static Date getLastMovementDate(Domain domain, String login, Integer rbankId) {
		try (CloseableAONContext aonContext = AONContext.getAONContext(domain, login)) {
			java.sql.Date date = (java.sql.Date) aonContext.getDslContext()
			.select(DSL.max(BANK_STATEMENT.OPERATION_DATE).as("maxdate"))
			.from(BANK_STATEMENT)
			.where(BANK_STATEMENT.RBANK.eq(rbankId))
			.fetchSingle()
			.get("maxdate");
			if (date != null) {
				return new Date(date.getTime());
			}
		} catch (NoDataFoundException e) {			
			return null;
		}
		return null;
	}
	
	public static Pair<String, Date> getMaxMovementIdAndDate(Domain domain, String login, Integer rbankId) {
		try (CloseableAONContext aonContext = AONContext.getAONContext(domain, login)) {
			RegistryBank rbank = AON.getRegistryBank(domain, login, f -> f.getIdProperty().eq(rbankId));
			Record2<String, java.sql.Date> result = aonContext.getDslContext()
			.select(DSL.max(BANK_STATEMENT.REFERENCE2).as("maximum"), BANK_STATEMENT.OPERATION_DATE)
			.from(BANK_STATEMENT)
			.where(BANK_STATEMENT.REFERENCE1.eq(NORDIGEN_R1))
			.and(BANK_STATEMENT.RBANK.eq(rbank.getId()))
			.and(BANK_STATEMENT.DOMAIN.eq(domain.getId()))
			.fetchSingle();
			
			String id = (String) result.get("maximum");
			java.sql.Date date = result.get(BANK_STATEMENT.OPERATION_DATE);
			Date utilDate = date != null ? new Date(date.getTime()) : null;
			
			if (id == null || id.isEmpty()) {
				return null;
			} else {
				return new Pair<>(id, utilDate);				
			}
		} catch (NoDataFoundException e) {
			return null;
		}
		
	}
	
	public static Date getLastOperationDateDB(Domain domain, String login, Integer rbankId) {		
		Date lastOperationDate = null;
		try (CloseableAONContext aonContext = AONContext.getAONContext(domain, login)) {
			Pair<String, Date> max = getMaxMovementIdAndDate(domain, login, rbankId);
			String maxId = null;
			if (max == null) {
				java.sql.Date date = (java.sql.Date) aonContext.getDslContext()
						.select(DSL.max(BANK_STATEMENT.OPERATION_DATE).as("date"))
						.from(BANK_STATEMENT)
						.where(BANK_STATEMENT.DOMAIN.eq(domain.getId()))
						.and(BANK_STATEMENT.RBANK.eq(rbankId))
						.fetchSingle().get("date");
				if (date == null)
					lastOperationDate = null;
				else
					lastOperationDate = new Date(date.getTime());
			} else {
				maxId = max.getKey();
				if (maxId != null) {			
					lastOperationDate = max.getValue();
				}
			}
			
			if (maxId == null && lastOperationDate == null) {
				lastOperationDate = cleanDate(31, Calendar.DECEMBER, Calendar.getInstance().get(Calendar.YEAR) - 1);
			}
		} catch (NoDataFoundException e) {
			lastOperationDate = cleanDate(31, Calendar.DECEMBER, Calendar.getInstance().get(Calendar.YEAR) - 1);
		}
		return lastOperationDate;
	}
	
	private static int getNextLotNumber(AONContext aonContext, Integer domainId, RegistryBank rbank) {
		try  {			
			AggregateFunction<Integer> lot = DSL.max(BANK_STATEMENT.LOT_NUMBER);
			return AonNumberUtils.zeroIfNull(aonContext.getDslContext().select( lot )
					.from(BANK_STATEMENT)
					.where(BANK_STATEMENT.RBANK.eq(rbank.getId()))
					.and(BANK_STATEMENT.DOMAIN.eq(domainId))
					.fetchSingle().get(lot)) + 1;
		} catch (NoDataFoundException e) {
			return 1;
		}
	}
	
	public static Integer insertStatements(Domain domain, String user, NordigenBankAccount account) throws AonCoreException {
		try (CloseableAONContext aonContext = AONContext.getAONContext(domain, user)) {
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
			
			List<NordigenBankStatement> bankStatements = account.getNotInsertedMovements();
			
			if (bankStatements != null) {
				
				int lotNumber = getNextLotNumber(aonContext, domain.getId(), account.getRbank());
				
				for (NordigenBankStatement bankStatement : bankStatements) {
					if (!bankStatement.isPending()) {						
						bankStatement.setLotNumber(lotNumber);
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
					
				}
			}
			final InsertValuesStep11<BankStatementRecord, Integer, Integer, Integer, java.sql.Date, Byte, Byte, Double, String, Byte, String, String> finalQuery = query;
			return aonContext.getDslContext().transactionResult(cnf -> finalQuery.execute());
		}
	}
	
	public static List<NordigenBankStatement> getBankStatements(Domain domain, String user, RegistryBank rbank, Date dateFrom, Date dateTo) {
		try (CloseableAONContext aonContext = AONContext.getAONContext(domain, user)) {
			return aonContext.getDslContext()
			.select()
			.from(BANK_STATEMENT)
			.where(BANK_STATEMENT.RBANK.eq(rbank.getId()))
			.and(BANK_STATEMENT.OPERATION_DATE.ge(AonDateUtils.toSql(dateFrom)))
			.and(BANK_STATEMENT.OPERATION_DATE.le(AonDateUtils.toSql(dateTo)))
			.orderBy(BANK_STATEMENT.OPERATION_DATE.desc(), BANK_STATEMENT.ID.desc())
			.fetchStreamInto(BANK_STATEMENT)
			.map(bs -> dbToNordigenBankStatement(bs, rbank))
			.collect(Collectors.toList());
		}
	}
	
	private static NordigenBankStatement dbToNordigenBankStatement(BankStatementRecord record, RegistryBank rbank) {
		NordigenBankStatement bs = new NordigenBankStatement();
		bs.setAmount(record.getAmount());
		bs.setComments(record.getComments());
		bs.setCommonConcept(AonEnumUtils.enumValue(StatementConcept.class, record.getCommonConcept()));
		bs.setDescription(record.getDescription());
		bs.setDocument(record.getDocument());
		bs.setDomain(record.getDomain());
		bs.setId(record.getId());
		bs.setLotNumber(record.getLotNumber());
		bs.setOperationDate(record.getOperationDate());
		bs.setOwnConcept(record.getOwnConcept());
		bs.setPayment(AonEnumUtils.getBoolean(record.getPayment()));
		bs.setReference1(record.getReference1());
		bs.setReference2(record.getReference2());
		bs.setRegistryBank(rbank);
		bs.setReliability(AonEnumUtils.enumValue(StatementReliability.class, record.getReliability()));
		bs.setSecurityLevel(AonEnumUtils.enumValue(SecurityLevel.class, record.getSecurityLevel()));
		bs.setStatus(AonEnumUtils.enumValue(StatementStatus.class, record.getStatus()));
		return bs;
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
