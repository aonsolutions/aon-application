package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.BankStatement.BANK_STATEMENT;
import static com.esferalia.aon.jooq.tables.Rbank.RBANK;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.DatePart;
import org.jooq.InsertValuesStep11;
import org.jooq.Record;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.BankStatementRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Filter.NordigenBankStatementFilter;
import com.esferalia.aon.occam.api.model.finance.BankStatement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountBalance;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBalanceType;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankAccount;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankStatement;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.StatementConcept;
import com.esferalia.aon.occam.api.model.type.StatementReliability;
import com.esferalia.aon.occam.api.model.type.StatementStatus;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.NordigenBankStatementPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.validation.BankStatementValidator;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class NordigenDAO {
	
//	public static final String NORDIGEN_R1 = "NORDIGEN";
//	private static final String RADD_INFO_REQUISITION_ATTRIBUTE_PATTERN = "NORDIGEN\\[(?<rbank>\\d+)\\]";

	private NordigenDAO() {
	    throw new IllegalStateException("Utility class");
	}

	public static List<NordigenBankAccount> getAllAccounts(AONContext ctx) {
		Company company = CompanyDAO.getCompany(ctx, ctx.getDomainId());
		return RegistryBankDAO.getStream(ctx
			,f -> f.getActiveProperty().eq(AonEnumUtils.getByte(true))
				.and(f.getRegistryProperty().eq(company.getId())))
			.map(rbank -> new NordigenBankAccount()
				.setRbank(rbank)
				.setIban(rbank != null && rbank.getBankAccount() != null ? rbank.getBankAccount().getIban() : null)
				.setBankAlias(rbank != null ? rbank.getAlias() : null)
				.setLinked(AonStringUtils.isNotBlank(rbank.getRequisition()))
				.setRequisitionId(rbank.getRequisition())
				.setLastMovementDate(  BankStatementDAO.getLastMovementDate(ctx, rbank.getId()) ))
			.collect(Collectors.toList());
	}
	
	public static NordigenBankAccount getAccountByIban(AONContext ctx, String iban) {
	    Company company = CompanyDAO.getCompany(ctx, ctx.getDomainId());
	    
	    return RegistryBankDAO.getStream(ctx
	            , f -> f.getActiveProperty().eq(AonEnumUtils.getByte(true))
	                .and(f.getRegistryProperty().eq(company.getId()))
	                .and(f.getBankAccountProperty().eq(iban))) 
	            .map(rbank -> new NordigenBankAccount()
	                .setRbank(rbank)
	                .setIban(rbank != null && rbank.getBankAccount() != null ? rbank.getBankAccount().getIban() : null)
	                .setBankAlias(rbank != null ? rbank.getAlias() : null)
	                .setLinked(AonStringUtils.isNotBlank(rbank.getRequisition()))
	                .setRequisitionId(rbank.getRequisition())
	                .setLastMovementDate(BankStatementDAO.getLastMovementDate(ctx, rbank.getId())))
	            .findFirst()  
	            .orElse(null); 
	}

	
	//FUNCION GET MOVEMENTS BASE DATOS BANK_STATEMENT, NO SON NORDIGEN BANKSTATEMENT
	
	private static final NordigenBankStatementPropertiesDAO STATEMENT_PROPERTIES = new NordigenBankStatementPropertiesDAO();
	
	public static Stream<BankStatement> getMovementsFromDB(AONContext ctx , NordigenBankStatementFilter filter, Integer page, Integer perPage){
		return ctx.getDslContext()
		.select()
		.from(BANK_STATEMENT)
		.where(STATEMENT_PROPERTIES.getConditions(filter))
		.orderBy(BANK_STATEMENT.OPERATION_DATE.desc())
		.limit(perPage).offset(perPage * (page -1))
		.fetch()
		.stream()
		.map(new BankStatementFiller());
	}
	
	public static long getBankMovementsCount(AONContext ctx, NordigenBankStatementFilter filter) {
		return ctx.getDslContext().select()
				.from(BANK_STATEMENT)
				.where(STATEMENT_PROPERTIES.getConditions(filter))
				.fetch()
				.stream()
				.count();
	}
	
	static class BankStatementFiller extends Filler implements Function<Record, BankStatement>{

		@Override
		public BankStatement apply(Record r) {
			return buildStatement(r);
		}
		
		static BankStatement buildStatement(Record r) {
			return new BankStatement()
					.setId(r.getValue(BANK_STATEMENT.ID))
					.setDomain(r.getValue(BANK_STATEMENT.DOMAIN))
					.setOperationDate(r.getValue(BANK_STATEMENT.OPERATION_DATE))
					.setAmount(r.getValue(BANK_STATEMENT.AMOUNT))
					.setDescription(r.getValue(BANK_STATEMENT.DESCRIPTION))
					.setStatus(StatementStatus.values()[r.getValue(BANK_STATEMENT.STATUS)]);
		}
	}
	
	public static NordigenBankAccount updateRegistryBank(AONContext ctx, NordigenBankAccount account) {
		RegistryBank rb = account.getRbank();
		Double balance = 0.0;
		Double remainder = 0.0;
		List<NordigenAccountBalance> balances = account.getBalances();
		NordigenAccountBalance consolidado = filterConsolidado(balances);
		NordigenAccountBalance real = filterReal(balances);
		if (consolidado != null && consolidado.getBalanceAmount() != null) {
			balance += AonNumberUtils.zeroIfNull(consolidado.getBalanceAmount().getAmount());
		}
		if (real == null || real.getBalanceAmount() == null) {
			real = consolidado;
		}
		if (real != null && real.getBalanceAmount() != null) {
			remainder += AonNumberUtils.zeroIfNull(real.getBalanceAmount().getAmount());
		}
		
		ctx.getDslContext().update(RBANK)
			.set(RBANK.BALANCE, AonNumberUtils.zeroIfNull(balance))
			.set(RBANK.AVAILABLE_BALANCE, AonNumberUtils.zeroIfNull(remainder))
			.set(RBANK.BALANCE_DATE, new Timestamp(new Date().getTime()))
			.where(RBANK.ID.eq(rb.getId()))
			.execute();
		return account;
	}

	private static NordigenAccountBalance filterConsolidado(List<NordigenAccountBalance> balances) {
		NordigenAccountBalance consolidado = balances.stream()
			.filter(bal -> NordigenBalanceType.CLOSING_BOOKED.equals(bal.getBalanceType()))
			.findFirst().orElse(null);
		if (consolidado == null && !balances.isEmpty()) {
			return balances.get(0);
		}
		return consolidado;
	}
	
	private static NordigenAccountBalance filterReal(List<NordigenAccountBalance> balances) {
		NordigenAccountBalance real = balances.stream()
			.filter(bal -> !NordigenBalanceType.CLOSING_BOOKED.equals(bal.getBalanceType()))
			.findFirst().orElse(null);
		if (real == null) {
			return filterConsolidado(balances);
		}
		return real;
	}

	public static Integer insertStatements(AONContext ctx, NordigenBankAccount account) throws AonCoreException {
			InsertValuesStep11<BankStatementRecord, Integer, Integer, Integer, java.sql.Date, Byte, Byte, Double, String, Byte, String, String> query =
			ctx.getDslContext().insertInto(
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
			int lotNumber = BankStatementDAO.getNextLotNumber(ctx, ctx.getDomainId(), account.getRbank());
			for (NordigenBankStatement bankStatement : bankStatements) {
				if (!bankStatement.isPending() 
					&& bankStatement.getOperationDate() != null
					&& AonDateUtils.isLessThanToday( bankStatement.getOperationDate()))
				{
					bankStatement.setLotNumber(lotNumber);
					// BankStatement Validation
					BankStatementValidator.validate(ctx, bankStatement);
					query = query.values(bankStatement.getDomain()
						,bankStatement.getRegistryBank() != null ? bankStatement.getRegistryBank().getId() : null
						,bankStatement.getLotNumber()
						,AonDateUtils.toSql( bankStatement.getOperationDate())
						,bankStatement.getCommonConcept().value()
						,AonEnumUtils.getByte(bankStatement.isPayment())
						,bankStatement.getAmount()
						,bankStatement.getDescription()
						,bankStatement.getStatus().value()
						,bankStatement.getReference1()
						,bankStatement.getReference2());
				}				
			}
		}
		
		final InsertValuesStep11<BankStatementRecord, Integer, Integer, Integer, java.sql.Date, Byte, Byte, Double, String, Byte, String, String> finalQuery = query;
		return ctx.getDslContext().transactionResult(cnf -> finalQuery.execute());
	}

	public static List<NordigenBankStatement> getBankStatements(AONContext ctx, RegistryBank rbank, Date dateFrom, Date dateTo) {
		return ctx.getDslContext()
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

	private static NordigenBankStatement dbToNordigenBankStatement(BankStatementRecord rec, RegistryBank rbank) {
		NordigenBankStatement bs = new NordigenBankStatement();
		bs.setAmount(rec.getAmount());
		bs.setComments(rec.getComments());
		bs.setCommonConcept(AonEnumUtils.enumValue(StatementConcept.class, rec.getCommonConcept()));
		bs.setDescription(rec.getDescription());
		bs.setDocument(rec.getDocument());
		bs.setDomain(rec.getDomain());
		bs.setId(rec.getId());
		bs.setLotNumber(rec.getLotNumber());
		bs.setOperationDate(rec.getOperationDate());
		bs.setOwnConcept(rec.getOwnConcept());
		bs.setPayment(AonEnumUtils.getBoolean(rec.getPayment()));
		bs.setReference1(rec.getReference1());
		bs.setReference2(rec.getReference2());
		bs.setRegistryBank(rbank);
		bs.setReliability(AonEnumUtils.enumValue(StatementReliability.class, rec.getReliability()));
		bs.setSecurityLevel(AonEnumUtils.enumValue(SecurityLevel.class, rec.getSecurityLevel()));
		bs.setStatus(AonEnumUtils.enumValue(StatementStatus.class, rec.getStatus()));
		return bs;
	}
	
	public static boolean compareBalanceDate(AONContext ctx , Integer variable) {
	    AtomicBoolean result = new AtomicBoolean(false);
	    Date today = new Date();
	    Timestamp todayTS = new Timestamp(today.getTime());
	    AtomicInteger atomicVariable = variable != null && variable != 0 ? new AtomicInteger(variable* 60 * 60 * 1000) : new AtomicInteger(86400000);
	    ctx.getDslContext()
	        .select(DSL.timestampDiff(DatePart.MILLISECOND, RBANK.BALANCE_DATE, todayTS))
	        .from(RBANK)
	        .where(RBANK.DOMAIN.eq(ctx.getDomainId())
	        		.and(RBANK.BALANCE_DATE.isNotNull())
	        		.and(RBANK.REQUISITION.isNotNull()))	            
	        .fetch()
	        .stream()
	        .forEach(r -> {
	            if ((Integer) r.getValue(0) >= atomicVariable.get()) {
	                result.set(true);
	            }
	        });
	    return result.get();
	}
	// ***********************************************************************************
	// ***********************************************************************************
	// ***********************************************************************************
	
//	private static Integer getRbankIdFromRaddinfo(RegistryAddInfo raddinfo) {
//		if (raddinfo == null) {
//			return null;
//		}
//		final Pattern aatrRegex = Pattern.compile(RADD_INFO_REQUISITION_ATTRIBUTE_PATTERN);
//		Matcher matcher = aatrRegex.matcher(null);
//		if (matcher.matches()) {
//			String id = matcher.group("rbank");
//			if (AonStringUtils.isNotBlank(id)) {
//				return AonNumberUtils.toInteger(id);
//			}
//		}
//		return null;
//	}
//	
//	
//	private static RegistryAddInfo getRaddInfoByRaddressId(Domain domain, String login, Integer rbankId) {
//		return AON.getRegistryAddInfo(domain.getName(), domain.getId(), login, f -> f.getAttributeProperty().eq("NORDIGEN[" + rbankId + "]")).orElse(null);
//	}
//	
//	private static Stream<RegistryAddInfo> getAllNordigenRaddInfos(Domain domain, String login) {
//		return AON.getRegistryAddInfoStream(domain.getName(), domain.getId(), login, f -> f.getAttributeProperty().like("NORDIGEN[%]"));
//	}
//	
//	private static List<RegistryBank> getLinkedRbanks(Domain domain, String login) {
//		return getAllNordigenRaddInfos(domain, login)
//		.map(raddInfo -> AON.getRegistryBank(domain, login, f -> f.getIdProperty().eq(getRbankIdFromRaddinfo(raddInfo))))
//		.collect(Collectors.toList());
//	}
//	
//	private static Pair<String, Date> getMaxMovementIdAndDate(Domain domain, String login, Integer rbankId) {
//		try (CloseableAONContext aonContext = AONContext.getAONContext(domain, login)) {
//			RegistryBank rbank = AON.getRegistryBank(domain, login, f -> f.getIdProperty().eq(rbankId));
//			Record2<String, java.sql.Date> result = aonContext.getDslContext()
//			.select(DSL.max(BANK_STATEMENT.REFERENCE2).as("maximum"), BANK_STATEMENT.OPERATION_DATE)
//			.from(BANK_STATEMENT)
//			.where(BANK_STATEMENT.REFERENCE1.eq(NORDIGEN_R1))
//			.and(BANK_STATEMENT.RBANK.eq(rbank.getId()))
//			.and(BANK_STATEMENT.DOMAIN.eq(domain.getId()))
//			.fetchSingle();
//			
//			String id = (String) result.get("maximum");
//			java.sql.Date date = result.get(BANK_STATEMENT.OPERATION_DATE);
//			Date utilDate = date != null ? new Date(date.getTime()) : null;
//			
//			if (id == null || id.isEmpty()) {
//				return null;
//			} else {
//				return new Pair<>(id, utilDate);				
//			}
//		} catch (NoDataFoundException e) {
//			return null;
//		}
//		
//	}
//	
//	private static Date getLastOperationDateDB(Domain domain, String login, Integer rbankId) {		
//		Date lastOperationDate = null;
//		try (CloseableAONContext aonContext = AONContext.getAONContext(domain, login)) {
//			Pair<String, Date> max = getMaxMovementIdAndDate(domain, login, rbankId);
//			String maxId = null;
//			if (max == null) {
//				java.sql.Date date = (java.sql.Date) aonContext.getDslContext()
//						.select(DSL.max(BANK_STATEMENT.OPERATION_DATE).as("date"))
//						.from(BANK_STATEMENT)
//						.where(BANK_STATEMENT.DOMAIN.eq(domain.getId()))
//						.and(BANK_STATEMENT.RBANK.eq(rbankId))
//						.fetchSingle().get("date");
//				if (date == null)
//					lastOperationDate = null;
//				else
//					lastOperationDate = new Date(date.getTime());
//			} else {
//				maxId = max.getKey();
//				if (maxId != null) {			
//					lastOperationDate = max.getValue();
//				}
//			}
//			
//			if (maxId == null && lastOperationDate == null) {
//				lastOperationDate = cleanDate(31, Calendar.DECEMBER, Calendar.getInstance().get(Calendar.YEAR) - 1);
//			}
//		} catch (NoDataFoundException e) {
//			lastOperationDate = cleanDate(31, Calendar.DECEMBER, Calendar.getInstance().get(Calendar.YEAR) - 1);
//		}
//		return lastOperationDate;
//	}
	
//	private static List<NordigenBankStatement> getBankStatements(Domain domain, String user, RegistryBank rbank, Date dateFrom, Date dateTo) {
//		try (CloseableAONContext aonContext = AONContext.getAONContext(domain, user)) {
//			return getBankStatements(aonContext, rbank, dateFrom, dateTo);		
//		}
//	}
	
//	private static Date cleanDate(int day, int month, int year) {
//		try {
//			Calendar calendar = Calendar.getInstance(new Locale("es", "ES"));
//			calendar.set(Calendar.MILLISECOND, 0);
//			calendar.set(Calendar.SECOND, 0);
//			calendar.set(Calendar.MINUTE, 0);
//			calendar.set(Calendar.HOUR, 0);
//			calendar.set(Calendar.DAY_OF_MONTH, day);
//			calendar.set(Calendar.MONTH, month);
//			calendar.set(Calendar.YEAR, year);
//			return calendar.getTime();
//		} catch (Exception e) {
//			return null;
//		}
//	}
	
	// *******************************************************************
	// *******************************************************************
	// *******************************************************************
	// *******************************************************************
	
}
