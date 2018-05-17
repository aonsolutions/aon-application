package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.AccountEntry.ACCOUNT_ENTRY;
import static com.esferalia.aon.jooq.tables.AccountEntryDetail.ACCOUNT_ENTRY_DETAIL;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.AggregateFunction;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountOperatingAccount;
import com.esferalia.aon.occam.api.model.AccountOperatingParams;
import com.esferalia.aon.occam.api.model.AccountOperatingReport;
import com.esferalia.aon.occam.api.model.AccountOperatingReport.AccountOperatingStatement;
import com.esferalia.aon.occam.api.model.AccountOperatingReport.AccountOperatingStatementType;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountStatement;
import com.esferalia.aon.occam.api.model.AccountStatementParams;
import com.esferalia.aon.occam.api.model.AccountStatementReport;
import com.esferalia.aon.occam.api.model.DateInterval;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AccountStatementPeriod;
import com.esferalia.aon.occam.api.model.type.AccountStatementPeriod.IAccountStatementPeriodVisitor;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.mutable.MutableDouble;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountStatementDAO {
	
	private static final com.esferalia.aon.jooq.tables.Account DET_ACCOUNT = ACCOUNT.as("detAcc");;
	private static final com.esferalia.aon.jooq.tables.Account BAL_ACCOUNT = ACCOUNT.as("balAcc");

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
	
	private static Condition getCondition(AONContext ctx , AccountOperatingParams params) {
		Condition condition = ACCOUNT_ENTRY_DETAIL.DOMAIN.equal(ctx.getDomainId());
		if (params.getPeriod() == null) {
			Date fromDate = params.getFromDate()!=null ?params.getFromDate() :AccountPeriodDAO.getMinDate(ctx);
			Date toDate = params.getToDate()!=null ?params.getToDate() :AccountPeriodDAO.getMaxDate(ctx);
			condition = condition.and(ACCOUNT_ENTRY.ENTRY_DATE.between(AonDateUtils.toSql(fromDate ),AonDateUtils.toSql(toDate)));
		} else {
			condition = condition.and(ACCOUNT_ENTRY.ACCOUNT_PERIOD.eq(params.getPeriod()));	
			if (params.getFromDate()!=null) {
				condition = condition.and(ACCOUNT_ENTRY.ENTRY_DATE.ge(AonDateUtils.toSql(params.getFromDate())));
			}
			if (params.getToDate()!=null) {
				condition = condition.and(ACCOUNT_ENTRY.ENTRY_DATE.le(AonDateUtils.toSql(params.getToDate())));
			}
		}
		User user = SecurityDAO.getUser(ctx);
		if (user.hasConfidentialityRole()) {
			if ( params.getSecurityLevel() != null ) {
				condition = condition.and( ACCOUNT_ENTRY.SECURITY_LEVEL.eq( params.getSecurityLevel().value() ));
			}
		} else {
			condition = condition.and( ACCOUNT_ENTRY.SECURITY_LEVEL.eq( SecurityLevel.OFFICIAL.value() ));
		}
		
		
		if (params.getCostCenters() != null && params.getCostCenters().size() > 0) {
			Condition c = null;
			if (params.getCostCenters().contains(AccountOperatingParams.EMPTY_COST_CENTER_ACCOUNT)) {
				@SuppressWarnings("unchecked")
				HashSet<String> cloned = (HashSet<String>) params.getCostCenters().clone();
				cloned.remove(AccountOperatingParams.EMPTY_COST_CENTER_ACCOUNT);
				c = ACCOUNT.COST_CENTER.isNull().or(ACCOUNT.COST_CENTER.in( cloned ));	
			} else {
				c = ACCOUNT.COST_CENTER.in( params.getCostCenters());
			}
			condition = condition.and( c );
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
	public static AccountOperatingReport operatingReport(AONContext ctx , final AccountOperatingParams params ) {
		ctx.checkRead();
		AccountOperatingReport report = new AccountOperatingReport();
		report.setParams(params);
		LinkedHashMap<DateInterval,AccountOperatingParams> intervals = getDateIntervals(ctx,params);
		for (DateInterval inter : intervals.keySet()) {
			operatingAccount(ctx, intervals.get(inter)).forEach(aos -> report.put(inter, aos) ); 
		}
		calculate(report);
		return report;
	}

	private static LinkedHashMap<DateInterval,AccountOperatingParams> getDateIntervals(AONContext ctx, AccountOperatingParams params) {
		if (params.getPeriod() == null) {
			throw new AonCoreException("Es necesario indicar el ejercicio contable");			
		}
		LinkedList<AccountPeriod> periods = AccountPeriodDAO.getPeriods(ctx, p -> p.getDomainProperty().eq(ctx.getDomainId()))
				.collect(Collectors.toCollection(LinkedList::new));
		LinkedHashMap<DateInterval,AccountOperatingParams> map = new LinkedHashMap<DateInterval,AccountOperatingParams>();
		for (AccountPeriod ap : periods ) {
			if (AonNumberUtils.equals(ap.getId(),params.getPeriod())) {
				DateInterval inter = new DateInterval()
						.setStart(params.getFromDate())
						.setEnd(params.getToDate())
						.setName(ap.getName());
				map.put(inter,params);
			} else if (map.size() > 0 && params.getPreviousPeriods() >= map.size() ) {
				AccountOperatingParams cloned = params.clone();
				cloned.setPeriod(ap.getId());
				cloned.setFromDate( AonDateUtils.add(params.getFromDate(), Calendar.YEAR, (map.size() * (-1)) ));
				cloned.setToDate( AonDateUtils.add(params.getToDate(), Calendar.YEAR,  (map.size() * (-1)) ));
				DateInterval inter = new DateInterval()
						.setStart(cloned.getFromDate())
						.setEnd(cloned.getToDate())
						.setName(ap.getName());
				map.put(inter,cloned);
			}
		}
		if (map.size() == 0) {
			throw new AonCoreException("Ejercicio contable no encontrado");			
		}
		return map;
	}

	public static Stream<AccountOperatingStatement> operatingAccount(AONContext ctx , final AccountOperatingParams params ) {
		ctx.checkRead();
		if (params.getPeriod() == null) {
			throw new AonCoreException("Es necesario indicar el ejercicio contable");			
		}
		AccountPeriod ap = AccountPeriodDAO.getPeriod(ctx, params.getPeriod());
		if (ap == null) {
			throw new AonCoreException("Ejercicio contable no encontrado");			
		}
		int level = params.getLevel();
		if (level != 2 && level != 3 && level != 4 && level != 9) level = 4;
		Field<String> CODE = DSL.substring(ACCOUNT.CODE, 1, level);
		AggregateFunction<BigDecimal> SUM_DEBIT = DSL.sum( ACCOUNT_ENTRY_DETAIL.DEBIT);
		AggregateFunction<BigDecimal> SUM_CREDIT = DSL.sum( ACCOUNT_ENTRY_DETAIL.CREDIT);
		return  ctx.getDslContext()
			.select( CODE,SUM_DEBIT,SUM_CREDIT)
				.from(ACCOUNT_ENTRY_DETAIL)
				.join(ACCOUNT_ENTRY).on(ACCOUNT_ENTRY.ID.eq(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY))
				.join(ACCOUNT).on(ACCOUNT.ID.eq(ACCOUNT_ENTRY_DETAIL.ACCOUNT))
				.where(getCondition(ctx, params))
				.and(ACCOUNT_ENTRY.ENTRY_TYPE.notIn(AccountEntryType.OPERATING.getValue(),AccountEntryType.CLOSING.getValue()))
				.and(ACCOUNT.CODE.like("6%").or(ACCOUNT.CODE.like("7%")) )
				.groupBy(CODE)
				.fetch()
				.stream()
				.map( rec -> new  AccountOperatingStatement()
						.setAccount( new AccountOperatingAccount()
								.setType( AccountOperatingStatementType.getType(rec.getValue(CODE)))
								.setCode(rec.getValue(CODE))
								)
						.setDebit(rec.getValue(SUM_DEBIT).doubleValue())
						.setCredit(rec.getValue(SUM_CREDIT).doubleValue())
					)
				.peek( st -> {
					Account account = AccountDAO.get(ctx, st.getAccount().getCode());
					if (account != null) {
						st.getAccount().setId(account.getId());
						st.getAccount().setDescription(account.getDescription());
					} else {
						st.getAccount().setDescription("Cuenta contable no encontrada.");
					}
				})
				;
	}
	
	private static void calculate(AccountOperatingReport report) {
		if (report.isEmpty()) return;
		for (AccountOperatingStatementType type : AccountOperatingStatementType.values()) {
			if (type.isCalculated()) {
				report.put(new AccountOperatingStatement()
						.setAccount(new AccountOperatingAccount()
								.setType(type)
								.setCode(type.toString())
								.setDescription(type.getDescription())));
			}
		}
		for (AccountOperatingAccount account : report.getAccounts() ) {
			AccountOperatingStatementType type = account.getType().modifies();
			if (type != null) {
				for (DateInterval inter : report.getIntervals() ) {
					AccountOperatingStatement item = report.get(account.getCode(), inter);
					if (item != null) {
						AccountOperatingStatement sum = report.get(type.toString(), inter);
						sum.setDebit( AonMathUtils.round(sum.getDebit() + item.getDebit()));
						sum.setCredit( AonMathUtils.round(sum.getCredit() + item.getCredit()));
						if (account.getType() == AccountOperatingStatementType.RESULT) {
							System.out.println( AonStringUtils.isNumeric(item.getAccount().getCode())+ " -" + item.getAccount().getCode() + "  - " + item.getDebit() + " - " + item.getCredit());
						}
					}
				}
			}
		}
		if (report.showRatios()) {
			calculateRatios(report);
		}
		if (report.showIncreasePercent()) {
			calculateIncreasePercent(report);
		}
		//ARRGGGHHHHHH!!! be elegant!!
		LinkedList<AccountOperatingAccount> list = new LinkedList<AccountOperatingAccount>();
		list.addAll(report.getAccounts());
		list.sort(new Comparator<AccountOperatingAccount>() {

			@Override
			public int compare(AccountOperatingAccount first, AccountOperatingAccount second) {
				if (first.getType() != second.getType())
					return first.getType().compareTo(second.getType());	
				return AonStringUtils.compare(first.getCode(), second.getCode());
			}
		});
		LinkedHashSet<AccountOperatingAccount> accounts = new LinkedHashSet<AccountOperatingAccount>();
		accounts.addAll(list);
		report.setAccounts( accounts );
		//ARRGGGHHHHHH!!! be elegant!!
	}

	private static void calculateRatios(AccountOperatingReport report) {
		for (DateInterval inter : report.getIntervals() ) {
			AccountOperatingStatement sal = report.get(AccountOperatingStatementType.SALES_TOTAL.toString(), inter);
			double s = (sal!=null)?AonMathUtils.absRounded( sal.getDebitBalance() - sal.getUnpaidBalance()):0.0;
			AccountOperatingStatement pur = report.get(AccountOperatingStatementType.PURCHASES_TOTAL.toString(), inter);
			double p = (pur!=null)?AonMathUtils.absRounded( pur.getDebitBalance() - pur.getUnpaidBalance()):0.0;
			AccountOperatingStatement exp = report.get(AccountOperatingStatementType.EXPENSES_TOTAL.toString(), inter);
			double e = (exp!=null)?AonMathUtils.absRounded( exp.getDebitBalance() - exp.getUnpaidBalance()):0.0;
			for (AccountOperatingAccount account : report.getAccounts() ) {
				AccountOperatingStatement itm = report.get(account.getCode(), inter);
				if (itm != null) {
					double i = AonMathUtils.absRounded( itm.getDebitBalance() - itm.getUnpaidBalance());
					itm.setSalesRatio(AonMathUtils.round( s==0?0.0:i*100/s ));
					itm.setPurchasesRatio(AonMathUtils.round( p==0?0.0:i*100/p ));
					itm.setExpensesRatio(AonMathUtils.round( e==0?0.0:i*100/e ));
				}
			}
		}
	}
	private static void calculateIncreasePercent(AccountOperatingReport report) {
		DateInterval previous = null; 
		for (DateInterval inter : report.getIntervals() ) {
			if (previous != null) {
				for (AccountOperatingAccount account : report.getAccounts() ) {
					AccountOperatingStatement itm = report.get(account.getCode(), inter);
					AccountOperatingStatement pre = report.get(account.getCode(), previous);
					if (itm != null && pre != null) {
						double p = (pre!=null)?AonMathUtils.absRounded( pre.getDebitBalance() - pre.getUnpaidBalance()):0.0;
						double i = AonMathUtils.absRounded( itm.getDebitBalance() - itm.getUnpaidBalance());
						itm.setIncreasePercent(AonMathUtils.round( p==0?100.0:(((i-p)*100)/p) ));
					}
				}
			}
			previous = inter;
		}
	}
	
}
