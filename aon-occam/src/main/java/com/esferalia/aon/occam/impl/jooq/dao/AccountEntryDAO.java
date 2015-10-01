package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.AccountEntry.ACCOUNT_ENTRY;
import static com.esferalia.aon.jooq.tables.AccountEntryDetail.ACCOUNT_ENTRY_DETAIL;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.AggregateFunction;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.InsertSetMoreStep;
import org.jooq.InsertSetStep;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Select;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;

import com.esferalia.aon.jooq.tables.Account;
import com.esferalia.aon.jooq.tables.records.AccountEntryDetailRecord;
import com.esferalia.aon.jooq.tables.records.AccountEntryRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.accounting.AccMiningParameters;
import com.esferalia.aon.occam.api.model.accounting.AccountBalance;
import com.esferalia.aon.occam.api.model.accounting.AccountEntryFilter;
import com.esferalia.aon.occam.api.model.accounting.AccountEntryProperties;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.impl.jooq.validation.AccountEntryValidation;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;

public class AccountEntryDAO {
	
	// --------------------------------------------------------------- LECTURA

	private static final Account DET_ACCOUNT = ACCOUNT.as("detAcc");;
	private static final Account BAL_ACCOUNT = ACCOUNT.as("balAcc");

	public static Stream<AccountEntry> fetch(AONContext ctx
			, AccountEntryFilter filter
			, int offset
			, int numberOfRows) {
		ctx.checkRead();
		return  ctx.getDslContext()
			.select(ACCOUNT_ENTRY.ID,ACCOUNT_ENTRY.DOMAIN,ACCOUNT_ENTRY.ACCOUNT_PERIOD
					,ACCOUNT_ENTRY.ENTRY_DATE,ACCOUNT_ENTRY.ENTRY_TYPE,ACCOUNT_ENTRY.JOURNAL
					,ACCOUNT_ENTRY.SECURITY_LEVEL,ACCOUNT_ENTRY.COMMENTS,ACCOUNT_ENTRY.CREATION_USER
					,ACCOUNT_ENTRY.CREATION_DATE,ACCOUNT_ENTRY.MODIFICATION_USER,ACCOUNT_ENTRY.MODIFICATION_DATE
					)
				.from(ACCOUNT_ENTRY)
				.where(ACCOUNT_ENTRY_PROPERTIES.getConditions(filter))
				.limit(offset,numberOfRows)
				.fetch()
				.stream()
				.map( new FullAccountEntryFiller() )
				.peek( ae -> ae.setDetails( ctx.getDslContext()
						.select(ACCOUNT_ENTRY_DETAIL.ID,ACCOUNT_ENTRY_DETAIL.DOMAIN,ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY
								,ACCOUNT_ENTRY_DETAIL.LINE,ACCOUNT_ENTRY_DETAIL.ACCOUNT,ACCOUNT_ENTRY_DETAIL.CONCEPT
								,ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT,ACCOUNT_ENTRY_DETAIL.DEBIT,ACCOUNT_ENTRY_DETAIL.CREDIT
								,ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER,ACCOUNT_ENTRY_DETAIL.CREATION_USER,ACCOUNT_ENTRY_DETAIL.CREATION_DATE
								,ACCOUNT_ENTRY_DETAIL.MODIFICATION_USER,ACCOUNT_ENTRY_DETAIL.MODIFICATION_DATE
								,DET_ACCOUNT.CODE,DET_ACCOUNT.DESCRIPTION
								,BAL_ACCOUNT.CODE,BAL_ACCOUNT.DESCRIPTION
								)
							.from(ACCOUNT_ENTRY_DETAIL)
							.join(DET_ACCOUNT).on(DET_ACCOUNT.ID.eq(ACCOUNT_ENTRY_DETAIL.ACCOUNT))
							.leftOuterJoin(BAL_ACCOUNT).on(BAL_ACCOUNT.ID.eq(ACCOUNT_ENTRY_DETAIL.ACCOUNT))
							.where(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY.eq(ae.getId()))
							.orderBy(ACCOUNT_ENTRY_DETAIL.LINE)
							.fetch()
							.stream()
							.map( new FullAccountEntryDetailFiller() )
							.collect(Collectors.toCollection(LinkedList::new))
							)
							);
	}
	
	// ------------------------------------------------------------- ESCRITURA
	
	public static Integer insert(AONContext ctx, AccountEntry ae) {
		ctx.checkWrite();
		AccountEntryValidation.validateEntry(ctx, ae);
		increaseJournal(ctx, ae);
		AccountEntryRecord record = ctx.getDslContext()
			.insertInto(ACCOUNT_ENTRY)
				.set(ACCOUNT_ENTRY.DOMAIN,ae.getDomain())
				.set(ACCOUNT_ENTRY.ACCOUNT_PERIOD,ae.getAccountPeriod())
				.set(ACCOUNT_ENTRY.ENTRY_DATE,AonDateUtils.toSql(ae.getEntryDate()))
				.set(ACCOUNT_ENTRY.ENTRY_TYPE, AonEnumUtils.getByte(ae.getEntryType())) 
				.set(ACCOUNT_ENTRY.JOURNAL, ae.getJournal())
				.set(ACCOUNT_ENTRY.SECURITY_LEVEL, AonEnumUtils.getByte(ae.getSecurityLevel()))
				.set(ACCOUNT_ENTRY.COMMENTS,ae.getComments())
				.set(ACCOUNT_ENTRY.CREATION_USER,ae.getComments())
				.set(ACCOUNT_ENTRY.CREATION_DATE, new Timestamp( System.currentTimeMillis()) )
				.returning(ACCOUNT_ENTRY.ID)
				.fetchOne();
		ae.setId(record.getValue(ACCOUNT_ENTRY.ID));
		batchInsert(ctx, ae);
		return record.getValue(ACCOUNT_ENTRY.ID); 
	}

	private static void batchInsert(AONContext ctx, AccountEntry ae) {
		InsertSetStep<AccountEntryDetailRecord> insert = ctx.getDslContext().insertInto(ACCOUNT_ENTRY_DETAIL);
		InsertSetMoreStep<AccountEntryDetailRecord>  insertMore = null;
		int line = 0;
		for (AccountEntryDetail detail : ae.getDetails()) {
			AccountEntryValidation.validateDetail(ctx, detail);
			if (insertMore != null) {
				insert = insertMore.newRecord();
			}
			insertMore =  insert
				.set(ACCOUNT_ENTRY_DETAIL.DOMAIN,ae.getDomain())
				.set(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY,ae.getId())
				.set(ACCOUNT_ENTRY_DETAIL.ACCOUNT,detail.getAccount())
				.set(ACCOUNT_ENTRY_DETAIL.LINE,UInteger.valueOf( ++line ))
				.set(ACCOUNT_ENTRY_DETAIL.CONCEPT,detail.getConcept()) 
				.set(ACCOUNT_ENTRY_DETAIL.DEBIT,detail.getDebit())
				.set(ACCOUNT_ENTRY_DETAIL.CREDIT,detail.getCredit())
				.set(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT,detail.getBalancingAccount())
				.set(ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER,detail.getDocumentNumber())
			;
		}
		if (insertMore != null) 
			insertMore.execute();
	}

	public static void update(AONContext ctx, AccountEntry ae) {
		ctx.checkWrite();
		AccountEntryValidation.validateEntry(ctx, ae);
		ctx.getDslContext()
			.update(ACCOUNT_ENTRY)
			.set(ACCOUNT_ENTRY.DOMAIN,ae.getDomain())
			.set(ACCOUNT_ENTRY.ACCOUNT_PERIOD,ae.getAccountPeriod())
			.set(ACCOUNT_ENTRY.ENTRY_DATE, 
					AonDateUtils.toSql(ae.getEntryDate()))
			.set(ACCOUNT_ENTRY.ENTRY_TYPE, AonEnumUtils.getByte(ae.getEntryType())) 
			.set(ACCOUNT_ENTRY.JOURNAL,ae.getJournal())
			.set(ACCOUNT_ENTRY.SECURITY_LEVEL, AonEnumUtils.getByte(ae.getSecurityLevel()))
			.set(ACCOUNT_ENTRY.COMMENTS,ae.getComments())
			.where(ACCOUNT_ENTRY.ID.equal( ae.getId()))
			.execute();
	}

	public static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();
		Integer accountPeriodId = null;
		AccountEntryType type = null;
		Record record = ctx.getDslContext().select(ACCOUNT_ENTRY.ACCOUNT_PERIOD,ACCOUNT_ENTRY.ENTRY_TYPE)
			.from(ACCOUNT_ENTRY)
			.where(ACCOUNT_ENTRY.ID.eq(id))
			.fetchOne();
		accountPeriodId = record.getValue(ACCOUNT_ENTRY.ACCOUNT_PERIOD);
		type = AccountEntryType.values()[record.getValue(ACCOUNT_ENTRY.ENTRY_TYPE)];
		// Se borran las lineas
		ctx.getDslContext()
			.delete(ACCOUNT_ENTRY_DETAIL)
			.where(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY.equal(id))
			.execute();
		// Se borra la cabecera
		ctx.getDslContext()
			.delete(ACCOUNT_ENTRY)
			.where(ACCOUNT_ENTRY.ID.equal(id))
			.execute();
		afterRemove(ctx, type, accountPeriodId);
	}

	public static boolean existsAnyEntry(AONContext ctx, Integer period, AccountEntryType accountEntryType) {
		ctx.checkRead();
		Select<Record> select = ctx.getDslContext()
				.select()
				.from(ACCOUNT_ENTRY)
				.where(ACCOUNT_ENTRY.ACCOUNT_PERIOD.equal(period))
				.and(ACCOUNT_ENTRY.ENTRY_TYPE.equal( AonEnumUtils.getByte( accountEntryType)));
		return ctx.getDslContext().fetchCount(select) > 0;
	}
	
	
	public static LinkedHashMap<String, AccountBalance> fetchBalance(
			AONContext ctx, AccMiningParameters params) {
		java.sql.Date start = AonDateUtils.toSql(params.getStartDate()!= null? params.getStartDate() : AonDateUtils.getYearFirstDay(0));
		java.sql.Date end = AonDateUtils.toSql(params.getEndDate()!= null? params.getEndDate() : AonDateUtils.getYearLastDay(9999));
		
		Field<String> accountField = DSL.substring(ACCOUNT.CODE, 1, params.getAccountLevel()); 
		Field<BigDecimal> sumDebit = DSL.sum(ACCOUNT_ENTRY_DETAIL.DEBIT); 
		Field<BigDecimal> sumCredit = DSL.sum(ACCOUNT_ENTRY_DETAIL.CREDIT); 
		LinkedHashMap<String, AccountBalance> map = new LinkedHashMap<String, AccountBalance>();
		ctx.getDslContext()
			.select(ACCOUNT_ENTRY.ENTRY_TYPE, accountField, sumDebit, sumCredit)
			.from( ACCOUNT_ENTRY )
			.join(ACCOUNT_ENTRY_DETAIL).on(ACCOUNT_ENTRY.ID.equal(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY))
			.join(ACCOUNT).on(ACCOUNT_ENTRY_DETAIL.ACCOUNT.equal(ACCOUNT.ID))
			.where(ACCOUNT_ENTRY.DOMAIN.equal(params.getDomain()))
			.and(ACCOUNT_ENTRY.ENTRY_DATE.between(start,end))
			.and(ACCOUNT_ENTRY.ENTRY_TYPE.ne(AccountEntryType.CLOSING.getValue()) )
			.groupBy(ACCOUNT_ENTRY.ENTRY_TYPE, accountField)
			.fetch()
			.stream()
			.forEach( record -> {
				byte type = record.getValue(ACCOUNT_ENTRY.ENTRY_TYPE);
				String account = record.getValue(accountField);
				double debit = record.getValue(sumDebit).doubleValue();
				double credit = record.getValue(sumCredit).doubleValue();
				putAccountBalance(map,type,account.substring(0,1), debit,credit);
				putAccountBalance(map,type,account.substring(0,2), debit,credit);
				putAccountBalance(map,type,account.substring(0,3), debit,credit);
				putAccountBalance(map,type,account, debit,credit);
			});
		return map;
	}

	private static synchronized void increaseJournal(AONContext ctx,AccountEntry accountEntry) {
		// Se averigua el último numero de diario y se incrementa en uno.
		AggregateFunction<Integer> maxFunc = DSL.max(ACCOUNT_ENTRY.JOURNAL);
		Record1<Integer> record = ctx.getDslContext()
				.select(maxFunc)
				.from(ACCOUNT_ENTRY)
				.where(ACCOUNT_ENTRY.DOMAIN.equal(accountEntry.getDomain()))
				.and(ACCOUNT_ENTRY.ACCOUNT_PERIOD.equal(accountEntry.getAccountPeriod()))
				.fetchOne();
		Integer lastJournal = record.getValue(maxFunc);
		if (lastJournal == null) {
			lastJournal = 0;
		}
		accountEntry.setJournal(lastJournal + 1);
	}

	private static void afterRemove(AONContext ctx,AccountEntryType removed, Integer periodId) {
		/*
		 * Gestión del estado del ejercicio.
		 *  
		 * Al borrar un apunte de apertura, cierre o explotación se 
		 * comprueba si existen los correspondientes
		 * apuntes para poner el estado del ejercicio correspondiente.
		 */
		if (removed == AccountEntryType.OPENING
			|| removed == AccountEntryType.OPERATING
			|| removed == AccountEntryType.CLOSING) {

			AccountPeriod period = AccountPeriodDAO.fetchOne(ctx, periodId);
			if (removed == AccountEntryType.OPENING) {
				if (!AccountEntryDAO.existsAnyEntry(ctx, period.getId(),AccountEntryType.OPENING)) {
					// Si después de borrar apertura, existe otro apertura, se mantiene 
					// el estado (o se modifica si era errroneo).
					period.setStatus( AccountPeriodStatus.OPENING );
				} else {
					// Si después de borrar apertura, no existe otro apertura, se activa.
					period.setStatus( AccountPeriodStatus.ACTIVE );
				}
			} else if (removed == AccountEntryType.CLOSING ) {
				if (AccountEntryDAO.existsAnyEntry(ctx, period.getId(),AccountEntryType.CLOSING)) {
					// Si después de borrar cierre, existe otro cierre, se
					// mantiene el estado.
					period.setStatus( AccountPeriodStatus.CLOSED );
				} else if (AccountEntryDAO.existsAnyEntry(ctx, period.getId(),AccountEntryType.OPERATING)) {
					// Si después de borrar cierre, existe explotación.
					period.setStatus( AccountPeriodStatus.OPERATING );
				} else if (AccountEntryDAO.existsAnyEntry(ctx, period.getId(),AccountEntryType.OPENING)) {
					// Si después de borrar cierre, no existe explotación y sí apertura.
					period.setStatus( AccountPeriodStatus.OPENING );
				} else {
					// Si después de borrar cierre, no existe explotación ni apertura. Se activa.
					period.setStatus( AccountPeriodStatus.ACTIVE );
				}
			} else if (removed == AccountEntryType.OPERATING) {
				if (AccountEntryDAO.existsAnyEntry(ctx, period.getId(),AccountEntryType.OPERATING)) {
					// Si después de borrar explotación, existe otro
					// explotación, se mantiene el estado.
					period.setStatus(AccountPeriodStatus.OPERATING);
				} else if (AccountEntryDAO.existsAnyEntry(ctx, period.getId(),AccountEntryType.OPENING)) {
					// Si después de borrar explotación, existe apertura.
					period.setStatus(AccountPeriodStatus.OPENING);
				} else {
					// Si después de borrar cierre, no existe apertura.
					period.setStatus(AccountPeriodStatus.ACTIVE);
				}
			}
			AccountPeriodDAO.update(ctx,period);
		}
	}

	private static void putAccountBalance(Map<String, AccountBalance> map,int type, String account,double debit, double credit) {
		if (map.containsKey(account)) {
			AccountBalance ac = map.get(account);
			ac.add(type,debit, credit);
		} else {
			map.put(account, new AccountBalance(type,debit,credit));	
		}
	}
	
	private static class FullAccountEntryFiller  implements Function<Record,AccountEntry> {
		@Override
		public AccountEntry apply(Record record) {
			return new AccountEntry()
				.setId( record.getValue(ACCOUNT_ENTRY.ID) )
				.setAccountPeriod( record.getValue(ACCOUNT_ENTRY.ACCOUNT_PERIOD))
				.setDomain( record.getValue(ACCOUNT_ENTRY.DOMAIN))
				.setEntryDate( record.getValue(ACCOUNT_ENTRY.ENTRY_DATE))
				.setEntryType( AccountEntryType.values()[record.getValue(ACCOUNT_ENTRY.ENTRY_TYPE)])
				.setJournal( record.getValue(ACCOUNT_ENTRY.JOURNAL))
				.setSecurityLevel(SecurityLevel.values()[record.getValue(ACCOUNT_ENTRY.SECURITY_LEVEL)])
				.setComments( record.getValue(ACCOUNT_ENTRY.COMMENTS))
				;
		}

			
	}

	private static class FullAccountEntryDetailFiller  implements Function<Record,AccountEntryDetail> {
		@Override
		public AccountEntryDetail apply(Record record) {
			return new AccountEntryDetail()
				.setId( record.getValue(ACCOUNT_ENTRY_DETAIL.ID) )
				.setDomain( record.getValue(ACCOUNT_ENTRY_DETAIL.DOMAIN))
				.setAccountEntry( record.getValue(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY))
				.setAccount(record.getValue(ACCOUNT_ENTRY_DETAIL.ACCOUNT))
				.setAccountCode(record.getValue(DET_ACCOUNT.CODE))
				.setAccountDescription(record.getValue(DET_ACCOUNT.DESCRIPTION))
				.setLine( record.getValue(ACCOUNT_ENTRY_DETAIL.LINE).intValue() )
				.setConcept(record.getValue(ACCOUNT_ENTRY_DETAIL.CONCEPT))
				.setDebit(record.getValue(ACCOUNT_ENTRY_DETAIL.DEBIT))
				.setCredit(record.getValue(ACCOUNT_ENTRY_DETAIL.CREDIT))
				.setBalancingAccount(record.getValue(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT))
				.setBalancingAccountCode(record.getValue(BAL_ACCOUNT.CODE))
				.setBalancingAccountDescription(record.getValue(BAL_ACCOUNT.DESCRIPTION))
				.setDocumentNumber(record.getValue(ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER))
				;
		}
	}

	// ---------------------------------------------------------- FILTRO
	private static final AccountEntryPropertiesDAO ACCOUNT_ENTRY_PROPERTIES = new AccountEntryPropertiesDAO();
	private static class AccountEntryPropertiesDAO implements AccountEntryProperties {

		private Condition[] getConditions(AccountEntryFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null)
				return new Condition[0];

			return new Condition[] { filterDAO.getCondition() };
		}

		@Override
		public Property<Integer> getIdProperty() {
			return new FilterDAO.PropertyDAO<Integer>(ACCOUNT_ENTRY.ID);
		}

		@Override
		public Property<Integer> getDomainProperty() {
			return new FilterDAO.PropertyDAO<Integer>(ACCOUNT_ENTRY.DOMAIN);
		}

		@Override
		public Property<Integer> getAccountPeriodProperty() {
			return new FilterDAO.PropertyDAO<Integer>(ACCOUNT_ENTRY.ACCOUNT_PERIOD);
		}
		
		@Override
		public Property<Date> getEntryDateProperty() {
			return new FilterDAO.DatePropertyDAO(ACCOUNT_ENTRY.ENTRY_DATE);
		}

		@Override
		public Property<Byte> getEntryTypeProperty() {
			return new FilterDAO.PropertyDAO<Byte>(ACCOUNT_ENTRY.ENTRY_TYPE);
		}

		@Override
		public Property<Byte> getConfidentialProperty() {
			return new FilterDAO.PropertyDAO<Byte>(ACCOUNT_ENTRY.SECURITY_LEVEL);
		}
	}
}

