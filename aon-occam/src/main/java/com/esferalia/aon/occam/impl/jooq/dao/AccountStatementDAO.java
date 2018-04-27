package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.AccountEntry.ACCOUNT_ENTRY;
import static com.esferalia.aon.jooq.tables.AccountEntryDetail.ACCOUNT_ENTRY_DETAIL;

import java.text.MessageFormat;
import java.util.Date;
import java.util.EnumMap;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Account;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountStatement;
import com.esferalia.aon.occam.api.model.AccountStatementParams;
import com.esferalia.aon.occam.api.model.AccountStatementReport;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AccountStatementPeriod;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.AccountStatementPeriod.IAccountStatementPeriodVisitor;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.mutable.MutableDouble;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountStatementDAO {
	
	private static final Account DET_ACCOUNT = ACCOUNT.as("detAcc");;
	private static final Account BAL_ACCOUNT = ACCOUNT.as("balAcc");

	public static Stream<AccountStatement> balance(AONContext ctx , final AccountStatementParams params ) {
		ctx.checkRead();
		AccountPeriod ap = (params.getPeriod() == null)?null: AccountPeriodDAO.getPeriod(ctx, params.getPeriod());
		if (params.getPeriod() == null) {
			if (params.getFromDate() == null) params.setFromDate(AccountPeriodDAO.getMinDate(ctx));
			if (params.getToDate() == null) params.setToDate(AccountPeriodDAO.getMaxDate(ctx));
		} else {
			if (ap == null) {
				throw new AonCoreException("Periodo no encontrado");
			}
			if (params.getFromDate() == null) params.setFromDate(ap.getInitiationDate());
			if (params.getToDate() == null) params.setToDate(ap.getDeadline());
		}
		final MutableDouble sdebitBalance = new MutableDouble(0.0);
		final MutableDouble sunpaidBalance = new MutableDouble(0.0);
		
		EnumMap<AccountStatementPeriod,AccountStatement> map = new EnumMap<AccountStatementPeriod,AccountStatement>(AccountStatementPeriod.class);
		
		ctx.getDslContext()
			.select(ACCOUNT_ENTRY.ENTRY_DATE,ACCOUNT_ENTRY.ENTRY_TYPE,ACCOUNT_ENTRY.ACCOUNT_PERIOD
					,ACCOUNT_ENTRY_DETAIL.DEBIT,ACCOUNT_ENTRY_DETAIL.CREDIT)
				.from(ACCOUNT_ENTRY_DETAIL)
				.join(ACCOUNT_ENTRY).on(ACCOUNT_ENTRY.ID.eq(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY))
				.where(getCondition(ctx, params, false))
				.orderBy(ACCOUNT_ENTRY.ENTRY_DATE)
				.fetch()
				.stream()
				.forEach(rec -> {
					AccountStatementPeriod period = getAccountStatementPeriod(params,ap
							,rec.getValue( ACCOUNT_ENTRY.ENTRY_DATE )
							,AccountEntryType.safeValueOf(rec.getValue(ACCOUNT_ENTRY.ENTRY_TYPE))
							,rec.getValue( ACCOUNT_ENTRY.ACCOUNT_PERIOD ));
					AccountStatement as = map.get(period);
					if (as == null) {
						as = new AccountStatement().setPeriod(period);
						map.put(period, as);
					}
					as.setDebit(AonMathUtils.round(as.getDebit() + rec.getValue(ACCOUNT_ENTRY_DETAIL.DEBIT)))
					  .setCredit(AonMathUtils.round(as.getCredit() + rec.getValue(ACCOUNT_ENTRY_DETAIL.CREDIT)));
				});
		return map.values()
			.stream()
			.filter(as -> (as.getPeriod() == AccountStatementPeriod.IN_PERIOD) || AonMathUtils.isNotZero(as.getDebit() - as.getCredit()))
			.peek( as -> {
				as.setConcept( getMessage(as.getPeriod(),params.getFromDate(), params.getToDate(), ap ) );
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
	
    private static AccountStatementPeriod getAccountStatementPeriod(AccountStatementParams params, AccountPeriod ap, Date date, AccountEntryType type, Integer period) {
    	
		if (params.getPeriod() != null) {
        	if (date.before(ap.getInitiationDate())) return AccountStatementPeriod.BEFORE_PERIOD;
        	if (date.after(ap.getDeadline())) return AccountStatementPeriod.AFTER_PERIOD;
        	
        	if (AonNumberUtils.equals( params.getPeriod() , period) && type == AccountEntryType.OPENING) return AccountStatementPeriod.IN_PERIOD_OPENING;
//        	if (AonNumberUtils.equals( params.getPeriod() , period) && type == AccountEntryType.OPERATING) return AccountStatementPeriod.IN_PERIOD_OPERATING;
        	if (AonNumberUtils.equals( params.getPeriod() , period) && type == AccountEntryType.CLOSING) return AccountStatementPeriod.IN_PERIOD_CLOSING;
        	
        	if (AonNumberUtils.equals( params.getPeriod() , period) && date.before(params.getFromDate())) return AccountStatementPeriod.IN_PERIOD_BEFORE; 
        	if (AonNumberUtils.equals( params.getPeriod() , period) && date.after(params.getToDate())) return AccountStatementPeriod.IN_PERIOD_AFTER;
        	
    	} else {
        	if (date.before(params.getFromDate())) return AccountStatementPeriod.BEFORE_PERIOD;
        	if (date.after(params.getToDate())) return AccountStatementPeriod.AFTER_PERIOD;
    	}
		return AccountStatementPeriod.IN_PERIOD;    	
    }

    public static Stream<AccountStatement> statement(AONContext ctx , AccountStatementParams params ) {
		ctx.checkRead();
		return  ctx.getDslContext()
			.select(ACCOUNT_ENTRY.ID
					,ACCOUNT_ENTRY.JOURNAL
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
				.where(getCondition(ctx, params, true))
				.and(params.getPeriod()==null?DSL.trueCondition():ACCOUNT_ENTRY.ENTRY_TYPE.notIn(AccountEntryType.OPENING.getValue(),AccountEntryType.CLOSING.getValue()))
				.orderBy(ACCOUNT_ENTRY.ENTRY_DATE,ACCOUNT_ENTRY.JOURNAL,ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY)
				.fetch()
				.stream()
				.map( new StatementFiller() )
		;
	}
	private static Condition getCondition(AONContext ctx , AccountStatementParams params, boolean applyDateFilterIfNeeded ) {
		Condition condition = ACCOUNT_ENTRY_DETAIL.DOMAIN.equal(ctx.getDomainId());
		if (params.getAccount() != null) {
			condition = condition.and(ACCOUNT_ENTRY_DETAIL.ACCOUNT.equal(params.getAccount()));	
		}
		if (applyDateFilterIfNeeded) {
			java.sql.Date sqlStart = null;
			java.sql.Date sqlEnd = null;
			if (params.getPeriod() == null) {
				Date fromDate = params.getFromDate()!=null ?params.getFromDate() :AccountPeriodDAO.getMinDate(ctx);
				Date toDate = params.getToDate()!=null ?params.getToDate() :AccountPeriodDAO.getMaxDate(ctx);
				sqlStart = AonDateUtils.toSql(fromDate );
				sqlEnd = AonDateUtils.toSql(toDate);
			} else {
				AccountPeriod ap = AccountPeriodDAO.getPeriod(ctx, params.getPeriod());
				if (ap == null) {
					throw new AonCoreException("Periodo no encontrado");
				}
				sqlStart = AonDateUtils.toSql(params.getFromDate()==null?ap.getInitiationDate():params.getFromDate());
				sqlEnd = AonDateUtils.toSql(params.getToDate()==null?ap.getDeadline():params.getToDate());
			}
			condition = condition.and(ACCOUNT_ENTRY.ENTRY_DATE.between(sqlStart,sqlEnd));
		}
		User user = SecurityDAO.getUser(ctx);
		if (user.hasConfidentialityRole()) {
			if ( params.getSecurityLevel() != null ) {
				condition = condition.and( ACCOUNT_ENTRY.SECURITY_LEVEL.eq( params.getSecurityLevel().value() ));
			}
		} else {
			condition = condition.and( ACCOUNT_ENTRY.SECURITY_LEVEL.eq( SecurityLevel.OFFICIAL.value() ));
		}
		if ( params.areOpeningEntriesExcluded() ) {
			condition = condition.and( ACCOUNT_ENTRY.ENTRY_TYPE.ne( AccountEntryType.OPENING.getValue()));	
		}
		if ( params.areOperatingEntriesExcluded() ) {
			condition = condition.and( ACCOUNT_ENTRY.ENTRY_TYPE.ne( AccountEntryType.OPERATING.getValue()));	
		}
		if ( params.areClosingEntriesExcluded() ) {
			condition = condition.and( ACCOUNT_ENTRY.ENTRY_TYPE.ne( AccountEntryType.CLOSING.getValue()));	
		}
		if ( AonStringUtils.isNotBlank(params.getDocumentNumber()) ) {
			condition = condition.and( ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER.eq( params.getDocumentNumber() ));
		}
		return condition;
	}
	
	private static class StatementFiller  implements Function<Record,AccountStatement> {
		@Override
		public AccountStatement apply(Record record) {
			return new AccountStatement()
				.setAccountEntry( record.getValue(ACCOUNT_ENTRY.ID))
				.setJournal( record.getValue(ACCOUNT_ENTRY.JOURNAL))
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
		final MutableDouble creditBalance = new MutableDouble(0.0);
		AccountStatement beforeAccStmt = null;
		for (AccountStatement as : report.getSummary()) {
			if (as.getPeriod() == AccountStatementPeriod.IN_PERIOD) {
				break;
			}
			beforeAccStmt = as;			
		}
		if (beforeAccStmt != null) {
			debitBalance.setValue( beforeAccStmt.getDebitBalance() );			
			creditBalance.setValue( beforeAccStmt.getUnpaidBalance());
		}
		
		report.getDetails()
			.stream()
			.forEach( as -> {
				double db = AonMathUtils.round(debitBalance.getValue() - creditBalance.getValue() + as.getDebit() - as.getCredit());
				double ub = AonMathUtils.round(creditBalance.getValue() - debitBalance.getValue() - as.getDebit() + as.getCredit());
				db = AonMathUtils.isNegative(db)?0.0:db;
				ub = AonMathUtils.isNegative(ub)?0.0:ub;
				debitBalance.setValue( db );
				creditBalance.setValue( ub );
				as.setDebitBalance(db);
				as.setUnpaidBalance(ub); 
			});
		return report;
	}

	private static String getMessage(AccountStatementPeriod period,Date fromDate,Date toDate, AccountPeriod ap) {
		StringBuffer msg = new StringBuffer();
		IAccountStatementPeriodVisitor descriptionVisitor = new IAccountStatementPeriodVisitor() {
			
			@Override public void visitBeforePeriod() {
				msg.append( MessageFormat.format("Saldo anterior al {0,date,dd/MM/yyyy}", fromDate));
			}
			@Override public void visitInPeriodOpening() {
				msg.append( "Saldo asiento apertura"); 
			}
			@Override public void visitInPeriodBefore() {
				msg.append( MessageFormat.format("Saldo entre el {0,date,dd/MM/yyyy} y el {1,date,dd/MM/yyyy}", ap.getInitiationDate(), fromDate));
			}
			@Override public void visitInPeriod() {
				msg.append( MessageFormat.format("Saldo periodo del {0,date,dd/MM/yyyy} al {1,date,dd/MM/yyyy}", fromDate, toDate));
			}
			@Override public void visitInPeriodAfter() {
				msg.append( MessageFormat.format("Saldo entre el {0,date,dd/MM/yyyy} y el {1,date,dd/MM/yyyy}", toDate, ap.getDeadline()));
			}
//			@Override public void visitInPeriodOperating() {
//				msg.append( "Saldo asiento explotaci\u00F3n");
//			}
			@Override public void visitInPeriodClosing() {
				msg.append( "Saldo asiento cierre");
			}
			@Override public void visitAfterPeriod() {
				msg.append( MessageFormat.format("Saldo posterior al {0,date,dd/MM/yyyy}", toDate));
			}
		};
		period.accept(descriptionVisitor);
		return msg.toString();
	}
}
