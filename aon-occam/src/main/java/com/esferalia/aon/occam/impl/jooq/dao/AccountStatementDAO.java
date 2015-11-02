package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.AccountEntry.ACCOUNT_ENTRY;
import static com.esferalia.aon.jooq.tables.AccountEntryDetail.ACCOUNT_ENTRY_DETAIL;

import java.text.MessageFormat;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Account;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AccountStatement;
import com.esferalia.aon.occam.api.model.AccountStatementReport;
import com.esferalia.aon.watson.mutable.MutableDouble;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

public class AccountStatementDAO {
	
	// --------------------------------------------------------------- LECTURA

	private static final Account DET_ACCOUNT = ACCOUNT.as("detAcc");;
	private static final Account BAL_ACCOUNT = ACCOUNT.as("balAcc");

	private static final int BEFORE = 0;
	private static final int PERIOD = 1;
	private static final int AFTER =  2;

	private static final String BEFORE_MSG = "Saldo anterior al {0,date,dd/MM/yyyy}";
	private static final String PERIOD_MSG = "Saldo periodo del {0,date,dd/MM/yyyy} al {1,date,dd/MM/yyyy}";
	private static final String AFTER_MSG  = "Saldo posterior al {0,date,dd/MM/yyyy}";
	
	public static Stream<AccountStatement> balance(AONContext ctx , Integer account, final Date start, final Date end) {
		ctx.checkRead();
		final java.sql.Date sqlStart = AonDateUtils.toSql(start);
		final java.sql.Date sqlEnd = AonDateUtils.toSql(end);
		
		
		final MutableDouble sdebitBalance = new MutableDouble(0.0);
		final MutableDouble sunpaidBalance = new MutableDouble(0.0);

		Field<Integer> when = DSL.decode()
		   .when(ACCOUNT_ENTRY.ENTRY_DATE.lessThan(sqlStart), BEFORE )
		   .when(ACCOUNT_ENTRY.ENTRY_DATE.between(sqlStart,sqlEnd), PERIOD )
		   .when(ACCOUNT_ENTRY.ENTRY_DATE.greaterThan(sqlEnd), AFTER );
		return ctx.getDslContext()
			.select(when 
					,DSL.sum(ACCOUNT_ENTRY_DETAIL.DEBIT)
					,DSL.sum(ACCOUNT_ENTRY_DETAIL.CREDIT))
				.from(ACCOUNT_ENTRY_DETAIL)
				.join(ACCOUNT_ENTRY).on(ACCOUNT_ENTRY.ID.eq(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY))
				.join(ACCOUNT).on(ACCOUNT.ID.eq(ACCOUNT_ENTRY_DETAIL.ACCOUNT))
				.where(ACCOUNT_ENTRY_DETAIL.DOMAIN.equal(ctx.getDomainId()))
				.and(ACCOUNT_ENTRY_DETAIL.ACCOUNT.equal(account))
				.groupBy(when)
				.orderBy(when)
				.fetch()
				.stream()
				.map(rec -> new AccountStatement()
						.setType(rec.getValue(when))
						.setConcept( (rec.getValue(when) == BEFORE)
								?MessageFormat.format(BEFORE_MSG,start)
								:((rec.getValue(when) == PERIOD)
										?MessageFormat.format(PERIOD_MSG,start,end)
										:MessageFormat.format(AFTER_MSG,end)))
						.setDebit(rec.getValue(DSL.sum(ACCOUNT_ENTRY_DETAIL.DEBIT)).doubleValue())
						.setCredit(rec.getValue(DSL.sum(ACCOUNT_ENTRY_DETAIL.CREDIT)).doubleValue())
					)
				.filter(as -> AonMathUtils.isNotZero(as.getDebit() - as.getCredit()))
				.peek( as -> {
						double db = sdebitBalance.getValue() - sunpaidBalance.getValue() + as.getDebit() - as.getCredit();
						double ub = sunpaidBalance.getValue() - sdebitBalance.getValue() - as.getDebit() + as.getCredit();
						db = AonMathUtils.isNegative(db)?0.0:db;
						ub = AonMathUtils.isNegative(ub)?0.0:ub;
						sdebitBalance.setValue( db );
						sunpaidBalance.setValue( ub );
						as.setDebitBalance(db);
						as.setUnpaidBalance(ub);
				});
	}
	public static Stream<AccountStatement> statement(AONContext ctx , Integer account, Date start, Date end) {
		ctx.checkRead();
		return  ctx.getDslContext()
			.select(ACCOUNT_ENTRY.ID
					,ACCOUNT_ENTRY.ENTRY_DATE
					,ACCOUNT_ENTRY_DETAIL.ACCOUNT
					,DET_ACCOUNT.CODE
					,DET_ACCOUNT.DESCRIPTION
					,ACCOUNT_ENTRY_DETAIL.CONCEPT
					,ACCOUNT_ENTRY_DETAIL.DEBIT
					,ACCOUNT_ENTRY_DETAIL.CREDIT
					,ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT
					,BAL_ACCOUNT.CODE
					,BAL_ACCOUNT.DESCRIPTION
					,ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER)
				.from(ACCOUNT_ENTRY_DETAIL)
				.join(ACCOUNT_ENTRY).on(ACCOUNT_ENTRY.ID.eq(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY))
				.join(DET_ACCOUNT).on(DET_ACCOUNT.ID.eq(ACCOUNT_ENTRY_DETAIL.ACCOUNT))
				.leftOuterJoin(BAL_ACCOUNT).on(BAL_ACCOUNT.ID.eq(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT))
				.where(ACCOUNT_ENTRY_DETAIL.DOMAIN.equal(ctx.getDomainId()))
				.and(ACCOUNT_ENTRY_DETAIL.ACCOUNT.equal(account))
				.and(ACCOUNT_ENTRY.ENTRY_DATE.between( AonDateUtils.toSql(start),  AonDateUtils.toSql(end)))
				.orderBy(ACCOUNT_ENTRY.ENTRY_DATE,ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY)
				.fetch()
				.stream()
				.map( new StatementFiller() )
		;
	}
	
	// ------------------------------------------------------------- ESCRITURA

	private static class StatementFiller  implements Function<Record,AccountStatement> {
		@Override
		public AccountStatement apply(Record record) {
			return new AccountStatement()
				.setAccountEntry( record.getValue(ACCOUNT_ENTRY.ID))
				.setEntryDate( record.getValue(ACCOUNT_ENTRY.ENTRY_DATE))
				.setAccount(record.getValue(ACCOUNT_ENTRY_DETAIL.ACCOUNT))
				.setAccountCode(record.getValue(DET_ACCOUNT.CODE))
				.setAccountDescription(record.getValue(DET_ACCOUNT.DESCRIPTION))
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
	
	public static AccountStatementReport calculate(AccountStatementReport report) {
		final MutableDouble debitBalance = new MutableDouble(0.0);
		final MutableDouble unpaidBalance = new MutableDouble(0.0);
		AccountStatement beforeAccStmt = 	
		report.getSummary()
			.stream()
			.filter(accs -> (accs.getType() == BEFORE) )
			.findFirst()
			.orElse(null);
		if (beforeAccStmt != null) {
			debitBalance.setValue( beforeAccStmt.getDebitBalance() );			
			unpaidBalance.setValue( beforeAccStmt.getUnpaidBalance());
		}
		
		report.getDetails()
			.stream()
			.forEach( as -> {
				double db = debitBalance.getValue() - unpaidBalance.getValue() + as.getDebit() - as.getCredit();
				double ub = unpaidBalance.getValue() - debitBalance.getValue() - as.getDebit() + as.getCredit();
				db = AonMathUtils.isNegative(db)?0.0:db;
				ub = AonMathUtils.isNegative(ub)?0.0:ub;
				debitBalance.setValue( db );
				unpaidBalance.setValue( ub );
				as.setDebitBalance(db);
				as.setUnpaidBalance(ub); 
			});
		return report;
	}

}
