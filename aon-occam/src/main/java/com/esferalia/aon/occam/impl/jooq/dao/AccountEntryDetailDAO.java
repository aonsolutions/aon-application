package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.AccountEntry.ACCOUNT_ENTRY;
import static com.esferalia.aon.jooq.tables.AccountEntryDetail.ACCOUNT_ENTRY_DETAIL;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

import org.jooq.Condition;
import org.jooq.exception.DataAccessException;
import org.jooq.lambda.SQL;
import org.jooq.lambda.Seq;
import org.jooq.lambda.Unchecked;
import org.jooq.types.UInteger;

import com.esferalia.aon.core.commons.AonCoreException;
import com.esferalia.aon.jooq.tables.Account;
import com.esferalia.aon.jooq.tables.records.AccountEntryDetailRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.impl.jooq.validation.AccountEntryValidation;

public class AccountEntryDetailDAO {
	
	private static final String BALANCING_ACCOUNT_NAME = "balancingAccount";
	private static final Account BALANCING_ACCOUNT = ACCOUNT.as(BALANCING_ACCOUNT_NAME);

	public static Seq<AccountEntryDetail> fetch(AONContext ctx, Integer accountEntryId) {
		Condition condition = ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY.equal(accountEntryId); 
		Function<ResultSet, AccountEntryDetail> function = Unchecked.function(rs -> new AccountEntryDetail(
			 rs.getInt(ACCOUNT_ENTRY_DETAIL.ID.getName())
			,rs.getInt(ACCOUNT_ENTRY_DETAIL.DOMAIN.getName())
			,rs.getInt(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY.getName())
			,rs.getInt(ACCOUNT_ENTRY_DETAIL.ACCOUNT.getName())
			,rs.getString(ACCOUNT.CODE.getName())
			,rs.getString(ACCOUNT.DESCRIPTION.getName())
			,rs.getInt(ACCOUNT_ENTRY_DETAIL.LINE.getName())
			,rs.getString(ACCOUNT_ENTRY_DETAIL.CONCEPT.getName())
			,rs.getDouble(ACCOUNT_ENTRY_DETAIL.DEBIT.getName())
			,rs.getDouble(ACCOUNT_ENTRY_DETAIL.CREDIT.getName())
			,rs.getInt(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT.getName())
			,rs.getString(BALANCING_ACCOUNT.CODE.getName())
			,rs.getString(BALANCING_ACCOUNT.DESCRIPTION.getName())
			,rs.getString(ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER.getName())
		));
		return fetch(ctx, condition,0,Integer.MAX_VALUE,function);
	}
	
	
	public static Seq<AccountEntryDetail> fetch(AONContext ctx, Condition condition
			, int offset, int numberOfRows
			, Function<ResultSet, AccountEntryDetail> function) {
		String sql = ctx.getDslContext()
				.select()
				.from( ACCOUNT_ENTRY_DETAIL )
				.join( ACCOUNT_ENTRY ).on(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY.equal(ACCOUNT_ENTRY.ID))
				.join( ACCOUNT ).on(ACCOUNT_ENTRY_DETAIL.ACCOUNT.equal(ACCOUNT.ID))
				.leftOuterJoin( BALANCING_ACCOUNT ).on(ACCOUNT_ENTRY_DETAIL.ACCOUNT.equal(BALANCING_ACCOUNT.ID))
			.where(condition)
			.limit(offset, numberOfRows)
			.getSQL();
		try {
			PreparedStatement stmt = ctx.getDslContext()
					.configuration()
					.connectionProvider()
					.acquire()
					.prepareStatement(sql);
			Seq<AccountEntryDetail> seq = SQL.seq(stmt,function);
			return seq;
		} catch (DataAccessException e) {
			throw new AonCoreException(e.getMessage(),e);
		} catch (SQLException e) {
			throw new AonCoreException(e.getMessage(),e);
		}
	}

	public static AccountEntryDetail fetchOne(AONContext ctx, Condition condition) {
		return populateRecord(ctx.getDslContext().fetchOne(ACCOUNT_ENTRY_DETAIL, condition));
	}

	public static void insert(AONContext ctx, AccountEntryDetail detail) {
		ctx.getDslContext().transaction(configuration -> {
			AccountEntryValidation.validateDetail(ctx, detail);
			AccountEntryDetailRecord record =ctx.getDslContext()
				.insertInto(ACCOUNT_ENTRY_DETAIL)
				.set(ACCOUNT_ENTRY_DETAIL.DOMAIN,detail.getDomain())
				.set(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY,detail.getAccountEntry())
				.set(ACCOUNT_ENTRY_DETAIL.ACCOUNT, detail.getAccount())
				.set(ACCOUNT_ENTRY_DETAIL.LINE, UInteger.valueOf( detail.getLine()))
				.set(ACCOUNT_ENTRY_DETAIL.CONCEPT, detail.getConcept()) 
				.set(ACCOUNT_ENTRY_DETAIL.DEBIT, detail.getDebit())
				.set(ACCOUNT_ENTRY_DETAIL.CREDIT, detail.getCredit())
				.set(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT, detail.getBalancingAccount())
				.set(ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER, detail.getDocumentNumber())
				.returning()
				.fetchOne();
			populateRecord(record, detail);
		});
	}

	public static void insertNoTransaction(AONContext ctx, AccountEntryDetail detail) {
		AccountEntryValidation.validateDetail(ctx, detail);
		ctx.getDslContext()
			.insertInto(ACCOUNT_ENTRY_DETAIL)
			.set(ACCOUNT_ENTRY_DETAIL.DOMAIN,detail.getDomain())
			.set(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY,detail.getAccountEntry())
			.set(ACCOUNT_ENTRY_DETAIL.ACCOUNT, detail.getAccount())
			.set(ACCOUNT_ENTRY_DETAIL.LINE, UInteger.valueOf( detail.getLine()))
			.set(ACCOUNT_ENTRY_DETAIL.CONCEPT, detail.getConcept()) 
			.set(ACCOUNT_ENTRY_DETAIL.DEBIT, detail.getDebit())
			.set(ACCOUNT_ENTRY_DETAIL.CREDIT, detail.getCredit())
			.set(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT, detail.getBalancingAccount())
			.set(ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER, detail.getDocumentNumber())
			.execute();
	}

	private static AccountEntryDetail populateRecord(AccountEntryDetailRecord record) {
		if (record == null) return null;
		AccountEntryDetail detail = new AccountEntryDetail();
		return populateRecord(record, detail);
	}

	private static AccountEntryDetail populateRecord(AccountEntryDetailRecord record, AccountEntryDetail detail) {
		detail.setId(record.getId());
		detail.setDomain(record.getDomain());
		detail.setAccountEntry(record.getAccountEntry());
		detail.setAccount(record.getAccount());
		// TODO Test if line can be null.
		UInteger line = record.getLine();
		detail.setLine( line.intValue() );
		detail.setConcept(record.getConcept());
		detail.setDebit(record.getDebit());
		detail.setCredit(record.getCredit());
		detail.setBalancingAccount(record.getBalancingAccount());
		detail.setDocumentNumber(record.getDocumentNumber());
		return detail;
	}

	public static void update(AONContext ctx, AccountEntryDetail detail) {
		ctx.getDslContext().transaction( configuration -> {
			AccountEntryValidation.validateDetail(ctx, detail);
			ctx.getDslContext()
				.update(ACCOUNT_ENTRY_DETAIL)
				.set(ACCOUNT_ENTRY_DETAIL.DOMAIN,detail.getDomain())
				.set(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY,detail.getAccountEntry())
				.set(ACCOUNT_ENTRY_DETAIL.ACCOUNT, detail.getAccount())
				.set(ACCOUNT_ENTRY_DETAIL.CONCEPT, detail.getConcept()) 
				.set(ACCOUNT_ENTRY_DETAIL.DEBIT, detail.getDebit())
				.set(ACCOUNT_ENTRY_DETAIL.CREDIT, detail.getCredit())
				.set(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT, detail.getBalancingAccount())
				.set(ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER, detail.getDocumentNumber())
				.where(ACCOUNT_ENTRY_DETAIL.ID.equal( detail.getId()))
				.execute();
		});
	}

	public static void delete(AONContext ctx, AccountEntryDetail detail) {
		ctx.getDslContext()
			.delete(ACCOUNT_ENTRY_DETAIL)
			.where(ACCOUNT_ENTRY_DETAIL.ID.equal(detail.getId()))
			.execute();
	}

	public static void deleteEntry(AONContext ctx, Integer accountEntryId) {
		ctx.getDslContext()
			.delete(ACCOUNT_ENTRY_DETAIL)
			.where(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY.equal(accountEntryId))
			.execute();
	}
}
