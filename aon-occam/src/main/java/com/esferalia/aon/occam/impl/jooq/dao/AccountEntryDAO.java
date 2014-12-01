package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AccountEntry.ACCOUNT_ENTRY;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.function.Function;
import java.util.logging.Logger;

import org.jooq.AggregateFunction;
import org.jooq.Condition;
import org.jooq.Record1;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.jooq.lambda.SQL;
import org.jooq.lambda.Seq;
import org.jooq.lambda.Unchecked;

import com.esferalia.aon.jooq.tables.records.AccountEntryRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.impl.jooq.validation.AccountEntryValidation;
import com.esferalia.aon.watson.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;

public class AccountEntryDAO {
	
	private final static Logger LOGGER = Logger.getLogger(AccountEntryDAO.class.getName()); 

	public static Seq<AccountEntry> fetch(AONContext ctx
				, Condition condition
				, int offset
				, int numberOfRows) {
		Function<ResultSet, AccountEntry> function = Unchecked.function(rs -> new AccountEntry(
			 rs.getInt(ACCOUNT_ENTRY.ID.getName())
			,rs.getInt(ACCOUNT_ENTRY.ACCOUNT_PERIOD.getName())
			,rs.getInt(ACCOUNT_ENTRY.DOMAIN.getName())
			,rs.getDate(ACCOUNT_ENTRY.ENTRY_DATE.getName())
			,rs.getByte(ACCOUNT_ENTRY.ENTRY_TYPE.getName())
			,rs.getInt(ACCOUNT_ENTRY.JOURNAL.getName())
			,rs.getByte(ACCOUNT_ENTRY.SECURITY_LEVEL.getName())
			,rs.getString(ACCOUNT_ENTRY.COMMENTS.getName())
				));
		return fetch(ctx, condition,offset,numberOfRows,function);
	}
	
	
	public static Seq<AccountEntry> fetch(AONContext ctx
			, Condition condition
			, int offset
			, int numberOfRows
			, Function<ResultSet, AccountEntry> function) {
		ctx.checkRead();
		String sql = ctx.getDslContext()
			.selectFrom( ACCOUNT_ENTRY )
			.where(condition)
			.limit(offset, numberOfRows)
			.getSQL();
		try {
			PreparedStatement stmt = ctx.getDslContext()
					.configuration()
					.connectionProvider()
					.acquire()
					.prepareStatement(sql);
			Seq<AccountEntry> seq = SQL.seq(stmt,function);
			return seq;
		} catch (DataAccessException e) {
			throw new AonCoreException(e.getMessage(),e);
		} catch (SQLException e) {
			throw new AonCoreException(e.getMessage(),e);
		}
	}
	
	public static String fetchCSV(AONContext ctx
			, Condition condition
			, int offset
			, int numberOfRows) {
		ctx.checkRead();
		return ctx.getDslContext()
			.selectFrom( ACCOUNT_ENTRY )
			.where(condition)
			.limit(offset, numberOfRows)
			.fetch().formatCSV();
	}

	public static AccountEntry fetchOne(AONContext ctx, Condition condition) {
		return populateRecord(ctx.getDslContext().fetchOne(ACCOUNT_ENTRY, condition));
	}

	public static void insert(AONContext ctx, AccountEntry ae) {
		ctx.checkWrite();
		if (ae.getAccountPeriod() == null && ae.isPeriodCreationEnabled()) {
			LOGGER.info(MessageFormat.format(
				"Creación automática de periodo contable para la fecha {0,date,dd/MM/yyy}: "
				,ae.getEntryDate()));
			AccountPeriod accountPeriod = new AccountPeriod();
			accountPeriod.setDomain( ae.getDomain() );
			accountPeriod.setName( Integer.toString( AonDateUtils.getYear(ae.getEntryDate())));
			accountPeriod.setInitiationDate(AonDateUtils.getYearFirstDay(ae.getEntryDate()));
			accountPeriod.setDeadline(AonDateUtils.getYearLastDay(ae.getEntryDate()));
			AccountPeriodDAO.insert(ctx, accountPeriod);
			LOGGER.info( MessageFormat.format(
				"Periodo contable creado ID:{0}; DOMAIN:{1}: "
				,accountPeriod.getId(),accountPeriod.getDomain()));
			ae.setAccountPeriod(accountPeriod.getId());
		}
		AccountEntryValidation.validateEntry(ctx, ae);
		increaseJournal(ctx, ae);
		AccountEntryRecord record = ctx.getDslContext()
			.insertInto(ACCOUNT_ENTRY)
				.set(ACCOUNT_ENTRY.DOMAIN,ae.getDomain())
				.set(ACCOUNT_ENTRY.ACCOUNT_PERIOD,ae.getAccountPeriod())
				.set(ACCOUNT_ENTRY.ENTRY_DATE,AonDateUtils.toSql(ae.getEntryDate()))
				.set(ACCOUNT_ENTRY.ENTRY_TYPE, AonEnumUtils.getByte(ae.getEntryType())) 
				.set(ACCOUNT_ENTRY.JOURNAL,ae.getJournal())
				.set(ACCOUNT_ENTRY.SECURITY_LEVEL, AonEnumUtils.getByte(ae.getSecurityLevel()))
				.set(ACCOUNT_ENTRY.COMMENTS,ae.getComments())
				.returning()
				.fetchOne();
		populateRecord(record, ae);
		int line = 0;
		for (AccountEntryDetail detail : ae.getDetails() ) {
			detail.setAccountEntry(ae.getId());
			detail.setDomain(ae.getDomain());
			detail.setLine(++line);
		}
		AccountEntryDetailDAO.batchInsert(ctx, ae.getDetails());
	}


	private static AccountEntry populateRecord(AccountEntryRecord record) {
		if (record == null) return null;
		AccountEntry entry = new AccountEntry();
		return populateRecord(record, entry);
	}

	private static AccountEntry populateRecord(AccountEntryRecord record, AccountEntry ae) {
		ae.setId(record.getId());
		ae.setDomain(record.getDomain());
		ae.setAccountPeriod(record.getAccountPeriod());
		ae.setEntryDate(AonDateUtils.toSql(record.getEntryDate()));
		ae.setEntryType(AccountEntryType.values()[record.getEntryType()]);
		ae.setJournal(record.getJournal());
		ae.setSecurityLevel(SecurityLevel.values()[record.getSecurityLevel()]);
		ae.setComments(record.getComments());
		return ae;
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

	public static void delete(AONContext ctx, AccountEntry accountEntry) {
		ctx.checkWrite();
		AccountEntryDetailDAO.deleteEntry(ctx,accountEntry.getId());
		ctx.getDslContext()
			.delete(ACCOUNT_ENTRY)
			.where(ACCOUNT_ENTRY.ID.equal(accountEntry.getId()))
			.execute();
		afterRemove(ctx, accountEntry);
	}

	public static boolean existsAnyEntry(AONContext ctx, Integer period, AccountEntryType accountEntryType) {
		ctx.checkRead();
		int count = ctx.getDslContext()
				.selectFrom(ACCOUNT_ENTRY)
				.where(ACCOUNT_ENTRY.ACCOUNT_PERIOD.equal(period))
				.and(ACCOUNT_ENTRY.ENTRY_TYPE.equal( AonEnumUtils.getByte( accountEntryType))).fetchCount();
		return (count > 0);
	}
	
	// -----------------------------------------
	// ------------------------- PRIVATE METHODS
	// -----------------------------------------

	private static synchronized void increaseJournal(AONContext ctx,AccountEntry accountEntry) {
		// Se averigua el último numero de diario y se graba incrementandolo en uno.
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

	private static void afterRemove(AONContext ctx,AccountEntry accountEntry) {
		/*
		 * Gestión del estado del ejercicio.
		 *  
		 * Al borrar un apunte de apertura, cierre o explotación se 
		 * comprueba si existen los correspondientes
		 * apuntes para poner el estado del ejercicio correspondiente.
		 */
		if (accountEntry.getEntryType() == AccountEntryType.OPENING
			|| accountEntry.getEntryType() == AccountEntryType.OPERATING
			|| accountEntry.getEntryType() == AccountEntryType.CLOSING) {

			AccountPeriod period = AccountPeriodDAO.fetchOne(ctx, accountEntry.getAccountPeriod());
			if (accountEntry.getEntryType() == AccountEntryType.OPENING) {
				if (!AccountEntryDAO.existsAnyEntry(ctx, period.getId(),AccountEntryType.OPENING)) {
					// Si después de borrar apertura, existe otro apertura, se mantiene 
					// el estado (o se modifica si era errroneo).
					period.setStatus( AccountPeriodStatus.OPENING );
				} else {
					// Si después de borrar apertura, no existe otro apertura, se activa.
					period.setStatus( AccountPeriodStatus.ACTIVE );
				}
			} else if (accountEntry.getEntryType() == AccountEntryType.CLOSING ) {
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
			} else if (accountEntry.getEntryType() == AccountEntryType.OPERATING) {
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

}
