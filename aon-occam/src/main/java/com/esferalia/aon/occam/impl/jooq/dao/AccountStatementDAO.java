package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.AccountEntry.ACCOUNT_ENTRY;
import static com.esferalia.aon.jooq.tables.AccountEntryDetail.ACCOUNT_ENTRY_DETAIL;
import static com.esferalia.aon.jooq.tables.AccountPeriod.ACCOUNT_PERIOD;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.AggregateFunction;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IDAOCallback;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountBalanceReport;
import com.esferalia.aon.occam.api.model.AccountBalanceReport.BalanceLine;
import com.esferalia.aon.occam.api.model.AccountOperatingAccount;
import com.esferalia.aon.occam.api.model.AccountOperatingReport;
import com.esferalia.aon.occam.api.model.AccountOperatingReport.AccountOperatingStatement;
import com.esferalia.aon.occam.api.model.AccountOperatingReport.AccountOperatingStatementType;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountStatement;
import com.esferalia.aon.occam.api.model.AccountStatementReport;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport.AccountTrialBalance;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.DateInterval;
import com.esferalia.aon.occam.api.model.FlatAccountEntryDetail;
import com.esferalia.aon.occam.api.model.IAccountParams;
import com.esferalia.aon.occam.api.model.accounting.AccMiningParameters;
import com.esferalia.aon.occam.api.model.accounting.AccountBalance;
import com.esferalia.aon.occam.api.model.accounting.BalanceType;
import com.esferalia.aon.occam.api.model.accounting.IAccMiningKeyAccept;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AccountStatementPeriod;
import com.esferalia.aon.occam.api.model.type.AccountStatementPeriod.IAccountStatementPeriodVisitor;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.server.accounting.AccBOEBalanceAbbreviateKey;
import com.esferalia.aon.occam.server.accounting.AccBOEBalanceNormalKey;
import com.esferalia.aon.occam.server.accounting.AccBOEBalancePYMESKey;
import com.esferalia.aon.occam.server.accounting.AccBOEPyGAbbreviateKey;
import com.esferalia.aon.occam.server.accounting.AccBOEPyGNormalKey;
import com.esferalia.aon.occam.server.accounting.AccMiningMVELContext;
import com.esferalia.aon.occam.server.accounting.IBalanceKey;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.mutable.MutableDouble;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountStatementDAO {
	
	// private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");	
	private static final DateFormat MONTH_DATE_FORMAT = new SimpleDateFormat("MM/yyyy");	
	private static final com.esferalia.aon.jooq.tables.Account DET_ACCOUNT = ACCOUNT.as("detAcc");;
	private static final com.esferalia.aon.jooq.tables.Account BAL_ACCOUNT = ACCOUNT.as("balAcc");
	
	public static Stream<FlatAccountEntryDetail> ledger(AONContext ctx , final AccountingReportParams params, int offset, int limit, IDAOCallback callback ) {
		System.out.println( "ledger ..: offset : " + offset + " limit ..: " + limit);
		ctx.checkRead();
		MutableInt oldAccountId = new MutableInt(-1);
		MutableDouble debitBalance = new MutableDouble(0);
		MutableDouble unpaidBalance = new MutableDouble(0);
		return ctx.getDslContext()
			.select(ACCOUNT_ENTRY.ID,ACCOUNT_ENTRY.DOMAIN,ACCOUNT_ENTRY.ACCOUNT_PERIOD
					,ACCOUNT_PERIOD.NAME,ACCOUNT_ENTRY.ENTRY_DATE,ACCOUNT_ENTRY.ENTRY_TYPE
					,ACCOUNT_ENTRY.ACTIVITY,ACCOUNT_ENTRY.JOURNAL,ACCOUNT_ENTRY.SECURITY_LEVEL
					,ACCOUNT_ENTRY.COMMENTS
					,ACCOUNT_ENTRY.CREATION_USER,ACCOUNT_ENTRY.CREATION_DATE
					,ACCOUNT_ENTRY.MODIFICATION_USER,ACCOUNT_ENTRY.MODIFICATION_DATE
					,ACCOUNT_ENTRY_DETAIL.ID,ACCOUNT_ENTRY_DETAIL.ACCOUNT,DET_ACCOUNT.CODE
					,DET_ACCOUNT.DESCRIPTION,ACCOUNT_ENTRY_DETAIL.CONCEPT
					,ACCOUNT_ENTRY_DETAIL.DEBIT,ACCOUNT_ENTRY_DETAIL.CREDIT
					,ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT,BAL_ACCOUNT.CODE,BAL_ACCOUNT.DESCRIPTION
					,ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER
					,ENTERPRISE_ACTIVITY.DESCRIPTION)
				.from(ACCOUNT_ENTRY)
				.innerJoin(ACCOUNT_PERIOD).on(ACCOUNT_ENTRY.ACCOUNT_PERIOD.eq(ACCOUNT_PERIOD.ID))
				.innerJoin(ACCOUNT_ENTRY_DETAIL).on(ACCOUNT_ENTRY.ID.eq(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY))
				.innerJoin(DET_ACCOUNT).on(DET_ACCOUNT.ID.eq(ACCOUNT_ENTRY_DETAIL.ACCOUNT))
				.leftOuterJoin(BAL_ACCOUNT).on(BAL_ACCOUNT.ID.eq(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT))
				.leftOuterJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_ACTIVITY.ID.equal(ACCOUNT_ENTRY.ACTIVITY))
				.where(getLedgerCondition(ctx, params))
				.orderBy(DET_ACCOUNT.CODE,ACCOUNT_ENTRY.ENTRY_DATE,ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY)
				.limit(offset, limit)
				.fetch()
				.stream()
				.onClose(new Runnable() {
					@Override
					public void run() {
						if (callback != null) {
							callback.onFinish();
						}
					}
				})
				.map( record -> {
					FlatAccountEntryDetail flat = new FlatAccountEntryDetail( )
						.setEntryId(record.getValue(ACCOUNT_ENTRY.ID))
						.setEntryDomain(record.getValue(ACCOUNT_ENTRY.DOMAIN))
						.setEntryPperiod(record.getValue(ACCOUNT_ENTRY.ACCOUNT_PERIOD))
						.setEntryPeriodName(record.getValue(ACCOUNT_PERIOD.NAME))
						.setEntryDate(record.getValue(ACCOUNT_ENTRY.ENTRY_DATE))
						.setEntryType(AccountEntryType.safeValueOf( record.getValue(ACCOUNT_ENTRY.ENTRY_TYPE)))
						.setActivity(record.getValue(ACCOUNT_ENTRY.ACTIVITY))
						.setActivityName(record.getValue(ENTERPRISE_ACTIVITY.DESCRIPTION))
						.setJournal(record.getValue(ACCOUNT_ENTRY.JOURNAL))
						.setComments(record.getValue(ACCOUNT_ENTRY.COMMENTS))
						.setEntrySecurityLevel(SecurityLevel.safeValueOf(record.getValue(ACCOUNT_ENTRY.SECURITY_LEVEL)))
						.setEntryCreationUser(record.getValue(ACCOUNT_ENTRY.CREATION_USER))
						.setEntryCreationDate(record.getValue(ACCOUNT_ENTRY.CREATION_DATE))
						.setEntryModificationUser(record.getValue(ACCOUNT_ENTRY.MODIFICATION_USER))
						.setEntryModificationDate(record.getValue(ACCOUNT_ENTRY.MODIFICATION_DATE))
						.setDetailId(record.getValue(ACCOUNT_ENTRY_DETAIL.ID) )
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
					if (!AonNumberUtils.equals(oldAccountId.getValue(), flat.getAccount())) {
						oldAccountId.setValue(flat.getAccount());
						ctx.getDslContext().select(
								 DSL.sum(ACCOUNT_ENTRY_DETAIL.DEBIT)
								,DSL.sum(ACCOUNT_ENTRY_DETAIL.CREDIT)
							)
							.from(ACCOUNT_ENTRY_DETAIL)
							.innerJoin(ACCOUNT_ENTRY).on(ACCOUNT_ENTRY.ID.eq(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY))			
							.where(ACCOUNT_ENTRY_DETAIL.DOMAIN.eq(flat.getEntryDomain()))
							  .and(ACCOUNT_ENTRY_DETAIL.ACCOUNT.eq(flat.getAccount()))
							  .and(ACCOUNT_ENTRY.ENTRY_DATE.lessThan(AonDateUtils.toSql( flat.getEntryDate())))
							.fetch()
							.stream()
							.forEach(sumRec -> {
								BigDecimal sumDebit  = sumRec.getValue(DSL.sum(ACCOUNT_ENTRY_DETAIL.DEBIT));
								BigDecimal sumCredit = sumRec.getValue(DSL.sum(ACCOUNT_ENTRY_DETAIL.CREDIT));
								if (sumDebit == null) sumDebit = new BigDecimal(0);
								if (sumCredit == null) sumCredit = new BigDecimal(0);
								double db = AonMathUtils.round(sumDebit.doubleValue() - sumCredit.doubleValue());
								double ub = AonMathUtils.round(sumCredit.doubleValue() - sumDebit.doubleValue());
								flat.setInitialDebitBalance(AonMathUtils.isGreatherThanZero(db)?db:0.0);
								flat.setInitialUnpaidBalance(AonMathUtils.isGreatherThanZero(ub)?ub:0.0);
								debitBalance.setValue(AonMathUtils.isGreatherThanZero(db)?db:0.0);
								unpaidBalance.setValue(AonMathUtils.isGreatherThanZero(ub)?ub:0.0);
							});
						;
					}
					double db = AonMathUtils.round(debitBalance.getValue()  - unpaidBalance.getValue() + flat.getDebit()  - flat.getCredit());
					double ub = AonMathUtils.round(unpaidBalance.getValue() - debitBalance.getValue()  + flat.getCredit() - flat.getDebit());
					debitBalance.setValue(AonMathUtils.isGreatherThanZero(db)?db:0.0);
					unpaidBalance.setValue(AonMathUtils.isGreatherThanZero(ub)?ub:0.0);
					flat.setDebitBalance(AonMathUtils.isGreatherThanZero(db)?db:0.0);
					flat.setUnpaidBalance(AonMathUtils.isGreatherThanZero(ub)?ub:0.0);
					return flat;
				}
			);
	}

	public static Stream<AccountStatement> balance(AONContext ctx , final AccountingReportParams params ) {
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
			.select(ACCOUNT_ENTRY.ENTRY_DATE,ACCOUNT_ENTRY.ENTRY_TYPE,ACCOUNT_ENTRY_DETAIL.DEBIT,ACCOUNT_ENTRY_DETAIL.CREDIT)
				.from(ACCOUNT_ENTRY_DETAIL)
				.join(ACCOUNT_ENTRY).on(ACCOUNT_ENTRY.ID.eq(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY))
				.where(getBalanceCondition(ctx, params, false))
				.orderBy(ACCOUNT_ENTRY.ENTRY_DATE)
				.fetch()
				.stream()
				.forEach(rec -> {
					AccountStatementPeriod period = getAccountStatementPeriod(params,ap
							,rec.getValue( ACCOUNT_ENTRY.ENTRY_DATE )
							,AccountEntryType.safeValueOf(rec.getValue(ACCOUNT_ENTRY.ENTRY_TYPE)));
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
	
    private static AccountStatementPeriod getAccountStatementPeriod(IAccountParams params, AccountPeriod ap, Date date, AccountEntryType type) {
    	
		if (params.getPeriod() != null) {
        	if (date.before(ap.getInitiationDate())) return AccountStatementPeriod.BEFORE_PERIOD;
        	if (date.after(ap.getDeadline())) return AccountStatementPeriod.AFTER_PERIOD;
        	
        	if (AonNumberUtils.equals( params.getPeriod() , ap.getId()) && type == AccountEntryType.OPENING) return AccountStatementPeriod.IN_PERIOD_OPENING;
        	if (AonNumberUtils.equals( params.getPeriod() , ap.getId()) && type == AccountEntryType.CLOSING) return AccountStatementPeriod.IN_PERIOD_CLOSING;
        	
        	if (AonNumberUtils.equals( params.getPeriod() , ap.getId()) && params.getFromDate() != null && date.before(params.getFromDate())) return AccountStatementPeriod.IN_PERIOD_BEFORE; 
        	if (AonNumberUtils.equals( params.getPeriod() , ap.getId()) && params.getToDate() != null && date.after(params.getToDate())) return AccountStatementPeriod.IN_PERIOD_AFTER;
        	
    	} else {
        	if (date.before(params.getFromDate())) return AccountStatementPeriod.BEFORE_PERIOD;
        	if (date.after(params.getToDate())) return AccountStatementPeriod.AFTER_PERIOD;
    	}
		return AccountStatementPeriod.IN_PERIOD;    	
    }

    public static Stream<AccountStatement> statement(AONContext ctx , AccountingReportParams params ) {
		ctx.checkRead();
		ensureParamsAccount( ctx , params );
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
				.where(getBalanceCondition(ctx, params, true))
				.and(params.getPeriod()==null?DSL.trueCondition():ACCOUNT_ENTRY.ENTRY_TYPE.notIn(AccountEntryType.OPENING.getValue(),AccountEntryType.CLOSING.getValue()))
				.orderBy(ACCOUNT_ENTRY.ENTRY_DATE,ACCOUNT_ENTRY.JOURNAL,ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY)
				.fetch()
				.stream()
				.map( new StatementFiller() )
		;
	}

	public static void ensureParamsAccount(AONContext ctx, AccountingReportParams params) {
		if (params.getAccount() == null) {
				throw new AonCoreException("Debe indicar una cuenta contable");
		}
		if (params.getAccount().getId() == null) {
			if (AonStringUtils.isBlank(params.getAccount().getCode())) {
				throw new AonCoreException("Debe indicar una cuenta contable");
			}
			Account account = AccountDAO.get(ctx, params.getAccount().getCode());
			if (account == null) {
				throw new AonCoreException("Cuenta contable '" + params.getAccount().getCode() +"' no encontrada");
			}
			params.setAccount( account );
		}
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
		if (report.getParams().isReverseOrder()) {
			Collections.reverse(report.getDetails());
		}
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
	public static AccountOperatingReport operatingReport(AONContext ctx , final AccountingReportParams params ) {
		ctx.checkRead();
		if (params.getPeriod() == null) {
			throw new AonCoreException("Es necesario indicar el ejercicio contable");			
		}
		AccountOperatingReport report = new AccountOperatingReport();
		report.setParams(params);
		report.setSelectedPeriod( AccountPeriodDAO.getPeriod(ctx, params.getPeriod()) );
		if (!params.isByMonth() && params.getFromDate() != null && params.getFromDate().before(report.getSelectedPeriod().getInitiationDate())) {
			throw new AonCoreException("La fecha desde indicada es anterior al inicio del ejercicio");
		}
		if (!params.isByMonth() && params.getToDate() != null && params.getToDate().after(report.getSelectedPeriod().getDeadline())) {
			throw new AonCoreException("La fecha hasta indicada es posterior al final del ejercicio");
		}
		if (params.getActivity() != null) {
			report.setSelectedActivity( CompanyDAO.getEnterpriseActivity(ctx, params.getActivity()) );
		}
		
		String totalPeriodName = "Total " + report.getSelectedPeriod().getName(); 
		if (   (report.getParams().getFromDate() != null && !AonDateUtils.isSameDay(report.getParams().getFromDate(),report.getSelectedPeriod().getInitiationDate()))
			|| (report.getParams().getToDate() != null && !AonDateUtils.isSameDay(report.getParams().getToDate(),report.getSelectedPeriod().getDeadline()))
			) {
			totalPeriodName = "Total periodo";
		}
				
		DateInterval totalPeriod = new DateInterval()
				.setStart(report.getSelectedPeriod().getDeadline())	// Para que aparezca al final.
				.setEnd(report.getSelectedPeriod().getDeadline())
				.setName(totalPeriodName);
		
		LinkedHashMap<DateInterval,AccountingReportParams> intervals = getDateIntervals(ctx,params);
		for (DateInterval inter : intervals.keySet()) {
			operatingAccount(ctx, intervals.get(inter))
				.forEach(aos -> {
					if (params.isByMonth()) {
						int year = AonNumberUtils.toint( AonStringUtils.substring( aos.getMonth(),0 , 4));
						int month = AonNumberUtils.toint( AonStringUtils.substring( aos.getMonth(), 4));
						Date firstDay = AonDateUtils.getDate(year, (month - 1), 1);
						Date lastDay = AonDateUtils.getMonthLastDay(firstDay);
						DateInterval i = new DateInterval()
								.setStart(firstDay)
								.setEnd(lastDay)
								.setName(MONTH_DATE_FORMAT.format(firstDay));
						report.put(i, aos);
						report.put(totalPeriod, aos);	
					} else {
						report.put(inter, aos);	
					}
				}); 
		}
		calculate(report);
		return report;
	}

	private static LinkedHashMap<DateInterval,AccountingReportParams> getDateIntervals(AONContext ctx, AccountingReportParams params) {
		LinkedList<AccountPeriod> periods = AccountPeriodDAO.getPeriods(ctx, p -> p.getDomainProperty().eq(ctx.getDomainId()))
				.collect(Collectors.toCollection(LinkedList::new));
		LinkedHashMap<DateInterval,AccountingReportParams> map = new LinkedHashMap<DateInterval,AccountingReportParams>();
		for (AccountPeriod ap : periods ) {
			if (AonNumberUtils.equals(ap.getId(),params.getPeriod())) {
				DateInterval inter = new DateInterval()
						.setStart(params.getFromDate())
						.setEnd(params.getToDate())
						.setName(ap.getName());
				map.put(inter,params);
			} else if (map.size() > 0 && params.getPreviousPeriods() >= map.size() ) {
				AccountingReportParams cloned = params.clone();
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

	public static Stream<AccountOperatingStatement> operatingAccount(AONContext ctx , final AccountingReportParams params ) {
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
		Field<String> MONTH = DSL.concat(DSL.year(ACCOUNT_ENTRY.ENTRY_DATE),DSL.month(ACCOUNT_ENTRY.ENTRY_DATE));
		Field<String> CODE = DSL.substring(ACCOUNT.CODE, 1, level);
		AggregateFunction<BigDecimal> SUM_DEBIT = DSL.sum( ACCOUNT_ENTRY_DETAIL.DEBIT);
		AggregateFunction<BigDecimal> SUM_CREDIT = DSL.sum( ACCOUNT_ENTRY_DETAIL.CREDIT);
		return  ctx.getDslContext()
			.select( CODE,SUM_DEBIT,SUM_CREDIT, (params.isByMonth()? MONTH:CODE) )
				.from(ACCOUNT_ENTRY_DETAIL)
				.join(ACCOUNT_ENTRY).on(ACCOUNT_ENTRY.ID.eq(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY))
				.join(ACCOUNT).on(ACCOUNT.ID.eq(ACCOUNT_ENTRY_DETAIL.ACCOUNT))
				.where(getOperatingCondition(ctx, params))
				.and(ACCOUNT_ENTRY.ENTRY_TYPE.notIn(AccountEntryType.OPERATING.getValue(),AccountEntryType.CLOSING.getValue()))
				.and(ACCOUNT.CODE.like("6%").or(ACCOUNT.CODE.like("7%")) )
				.groupBy(CODE,(params.isByMonth()? MONTH:CODE))
				.fetch()
				.stream()
				.map( rec -> new  AccountOperatingStatement()
						.setAccount( new AccountOperatingAccount()
								.setType( AccountOperatingStatementType.getType(rec.getValue(CODE)))
								.setCode(rec.getValue(CODE))
								)
						.setMonth((params.isByMonth()? rec.getValue(MONTH) : ""))
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
		if (report.getParams().isByMonth() && report.getIntervals() != null) {
			DateInterval first = report.getIntervals().first();
			Date start = first.getStart();
			if (report.getParams().getFromDate() != null && report.getParams().getFromDate().before(start)) {
				start = report.getParams().getFromDate();
			}
			Date end = report.getSelectedPeriod().getDeadline();
			if (report.getParams().getToDate() != null && report.getParams().getToDate().before(end)) {
				end = report.getParams().getToDate();
			}
			while (start.before(end)) {
				Date lastDay = AonDateUtils.getMonthLastDay(start);
				DateInterval inter = new DateInterval()
						.setStart(start)
						.setEnd(lastDay)
						.setName( MONTH_DATE_FORMAT.format(start) );
				report.put(inter,new  AccountOperatingStatement()
						.setAccount( new AccountOperatingAccount()
								.setType( AccountOperatingStatementType.RESULT )
								.setCode(AccountOperatingStatementType.RESULT.toString())
								.setDescription(AccountOperatingStatementType.RESULT.getDescription()))
							.setMonth( inter.getName() ));
				start = AonDateUtils.addMonths(start, 1);
				start = AonDateUtils.getMonthFirstDay(start);
			}
			
/*			
			int toMonth = AonDateUtils.getMonth(end);
			if (month > toMonth) {
				toMonth += 11;
			}
			for (;month <= toMonth; month++) {
				System.out.println( month);
				if (month > 11) {
					month = 0;
					year = year + 1;
				}
				Date firstDay = AonDateUtils.getDate(year, month, 1);
				Date lastDay = AonDateUtils.getMonthLastDay(firstDay);
				DateInterval inter = new DateInterval()
						.setStart(firstDay)
						.setEnd(lastDay)
						.setName(((month+1)<10?"0":"") + (month+1) +  "/" + year);
				report.put(inter,new  AccountOperatingStatement()
						.setAccount( new AccountOperatingAccount()
								.setType( AccountOperatingStatementType.RESULT )
								.setCode(AccountOperatingStatementType.RESULT.toString())
								.setDescription(AccountOperatingStatementType.RESULT.getDescription()))
							.setMonth( inter.getName() ));
			}
*/
		}
		if (report.showRatios()) {
			calculateRatios(report);
		}
		if (report.showIncreasePercent()) {
			calculateIncreasePercent(report);
		}
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
	
	// *********************************************************************************************************
	// ********************************* BALANCE DE SUMAS Y SALDOS *********************************************
	// *********************************************************************************************************
	
	public static AccountTrialBalanceReport trialBalance(AONContext ctx, AccountingReportParams params) {
		ctx.checkRead();
		AccountPeriod ap = null;
		if (params.getPeriod() != null) {
			ap = AccountPeriodDAO.getPeriod(ctx, params.getPeriod());
			if (ap == null) {
				throw new AonCoreException("Ejercicio contable no encontrado");			
			}
		}
		java.sql.Date sqlStart = null;
		java.sql.Date sqlEnd = null;
		if (ap == null) {
			Date fromDate = params.getFromDate()!=null ?params.getFromDate() :AccountPeriodDAO.getMinDate(ctx);
			Date toDate = params.getToDate()!=null ?params.getToDate() :AccountPeriodDAO.getMaxDate(ctx);
			sqlStart = AonDateUtils.toSql(fromDate);
			sqlEnd = AonDateUtils.toSql(toDate);
		} else {
			sqlStart = AonDateUtils.toSql(params.getFromDate()!=null ?params.getFromDate() : ap.getInitiationDate());
			sqlEnd = AonDateUtils.toSql(params.getToDate()==null?ap.getDeadline():params.getToDate());
		}
		
		AggregateFunction<BigDecimal> SUM_DEBIT = DSL.sum( ACCOUNT_ENTRY_DETAIL.DEBIT);
		AggregateFunction<BigDecimal> SUM_CREDIT = DSL.sum( ACCOUNT_ENTRY_DETAIL.CREDIT);

		// Se busca si existe un asiento de apertura en el ejercicio seleccionado.
		boolean hasOpeningAmounts = false;
		if (ap != null) {
			hasOpeningAmounts = ctx.getDslContext().fetchExists(
				ctx.getDslContext().select()
				.from(ACCOUNT_ENTRY)
				.where(ACCOUNT_ENTRY.DOMAIN.eq(params.getDomain()))
				.and(ACCOUNT_ENTRY.ACCOUNT_PERIOD.eq(ap.getId()))
				.and(ACCOUNT_ENTRY.ENTRY_TYPE.eq(AccountEntryType.OPENING.getValue())));
		}

		// Se busca si existen saldo anteriores a la fecha seleccionada. Si existe un asiento de apertura, no 
		// deberían existan errores en la contabilidad.
		boolean hasBeforePeriodAmounts =  !hasOpeningAmounts;
//		ctx.getDslContext()
//			.select(SUM_DEBIT, SUM_CREDIT)
//			.from( ACCOUNT_ENTRY )
//			.join(ACCOUNT_ENTRY_DETAIL).on(ACCOUNT_ENTRY.ID.equal(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY))
//			.where(getCondition(ctx, params)
//				.and(ACCOUNT_ENTRY.ENTRY_DATE.lt(sqlStart)))
//			.fetch()
//			.stream()
//			.map( rec ->  AonNumberUtils.equals(rec.getValue(SUM_DEBIT), rec.getValue(SUM_CREDIT)) )
//			.filter(bool ->  bool == null )
//			.findFirst()
//			.orElse( false );
		

		// Se busca si existen saldo desde el inicio del ejericio hasta la fecha selecciona
		boolean hasInPeriodPreviousAmounts = ap != null && params.getFromDate().after(ap.getInitiationDate());
		
		
		AccountTrialBalanceReport report = new AccountTrialBalanceReport()
			.setSelectedPeriod(ap)
			.setParams(params)
			.setHasBeforePeriodAmounts(hasBeforePeriodAmounts)
			.setHasOpeningAmounts(hasOpeningAmounts)
			.setHasInPeriodPreviousAmounts( hasInPeriodPreviousAmounts )
			;
		if (params.getActivity() != null) {
			report.setSelectedActivity( CompanyDAO.getEnterpriseActivity(ctx, params.getActivity()) );
		}

		// Configuramos los periodos de fechas:
		EnumMap<AccountStatementPeriod,Condition> conditions = new EnumMap<AccountStatementPeriod,Condition>(AccountStatementPeriod.class);

		// 1) Periodos anteriores:
		if (hasBeforePeriodAmounts) {
			conditions.put(AccountStatementPeriod.BEFORE_PERIOD
				,getTrialBalanceCondition(ctx, params)
				.and(ACCOUNT_ENTRY.ENTRY_DATE.lt(sqlStart)));
		}
		// 2) Saldo asiento de apertura
		if (hasOpeningAmounts) {
			conditions.put(AccountStatementPeriod.IN_PERIOD_OPENING
				,getTrialBalanceCondition(ctx, params)
				.and(ACCOUNT_ENTRY.ACCOUNT_PERIOD.eq(ap.getId()))
				.and(ACCOUNT_ENTRY.ENTRY_TYPE.eq(AccountEntryType.OPENING.getValue())));
		}
		// 3) Saldo del ejercicio anterior a la fecha seleccinada.
		if (hasInPeriodPreviousAmounts) {
			conditions.put(AccountStatementPeriod.IN_PERIOD_BEFORE
				,getTrialBalanceCondition(ctx, params)
				.and(ACCOUNT_ENTRY.ACCOUNT_PERIOD.eq(ap.getId()))
				.and(ACCOUNT_ENTRY.ENTRY_TYPE.ne(AccountEntryType.OPENING.getValue()))
				.and(ACCOUNT_ENTRY.ENTRY_DATE.ge(AonDateUtils.toSql(ap.getInitiationDate())))
				.and(ACCOUNT_ENTRY.ENTRY_DATE.lt(sqlStart))
				);
		}

		// 4) Sumas del rango de fechas seleccionado.
		conditions.put(AccountStatementPeriod.IN_PERIOD
			,getTrialBalanceCondition(ctx, params)
			.and(ap==null?DSL.trueCondition():ACCOUNT_ENTRY.ACCOUNT_PERIOD.eq(ap.getId()))
			.and(ACCOUNT_ENTRY.ENTRY_TYPE.ne(AccountEntryType.OPENING.getValue()))
			.and(ACCOUNT_ENTRY.ENTRY_DATE.ge(sqlStart))
			.and(ACCOUNT_ENTRY.ENTRY_DATE.le(sqlEnd))
		);

		for (AccountStatementPeriod accountStatementPeriod : conditions.keySet() ) {
			ctx.getDslContext()
				.select(ACCOUNT.ID,ACCOUNT.CODE,ACCOUNT.DESCRIPTION, SUM_DEBIT, SUM_CREDIT)
				.from( ACCOUNT_ENTRY )
				.join(ACCOUNT_ENTRY_DETAIL).on(ACCOUNT_ENTRY.ID.equal(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY))
				.join(ACCOUNT).on(ACCOUNT_ENTRY_DETAIL.ACCOUNT.equal(ACCOUNT.ID))
				.where(conditions.get(accountStatementPeriod))
				.groupBy(ACCOUNT.ID)
				.fetch()
				.stream()
				.forEach( record -> {
					String code = record.getValue(ACCOUNT.CODE);
					Account account = (params.getLevel() == 9)  
						?new Account().setId(record.getValue(ACCOUNT.ID))
							.setCode(code)
							.setDescription(record.getValue(ACCOUNT.DESCRIPTION))
						:new Account()
							.setCode(AonStringUtils.substring(code, 0,params.getLevel())); 
					addToTrialBalance(ctx,report
						,account
						,accountStatementPeriod
						,record.getValue(SUM_DEBIT).doubleValue()
						,record.getValue(SUM_CREDIT).doubleValue())
						;
				}
			);
		}
		if (!report.getParams().isNoActivityAccountVisible()) {
			TreeMap<String, AccountTrialBalance> balances = new TreeMap<String, AccountTrialBalance>();
			for (String account : report.getBalances().keySet()) {
				if (report.getBalances().get(account).hasPeriodEntries() 
				 || report.getBalances().get(account).hasInPeriodOpeningEntries()) {
					balances.put(account, report.getBalances().get(account));
				}
			}
			report.setBalances( balances );
		}
		return report;
	}
	
	private static void addToTrialBalance(AONContext ctx, AccountTrialBalanceReport report
		, Account account, AccountStatementPeriod accountStatementPeriod, Double debit, Double credit) {
		
		if (report.getParams().isLowLevelAccountVisible()) {
			int l = account.getCode().length();
			if (l == 9) addToTrialBalance(ctx,report, new Account().setCode( AonStringUtils.substring(account.getCode(), 0,4)), accountStatementPeriod, debit,credit); 
			else if (l == 4) addToTrialBalance(ctx,report, new Account().setCode( AonStringUtils.substring(account.getCode(), 0,3)), accountStatementPeriod, debit,credit);
			else if (l == 3) addToTrialBalance(ctx,report, new Account().setCode( AonStringUtils.substring(account.getCode(), 0,2)), accountStatementPeriod, debit,credit);
			else if (l == 2) addToTrialBalance(ctx,report, new Account().setCode( AonStringUtils.substring(account.getCode(), 0,1)), accountStatementPeriod, debit,credit);
			else if (l == 1) addToTrialBalance(ctx,report, new Account().setCode( AonStringUtils.substring(account.getCode(), 0,0)), accountStatementPeriod, debit,credit);
		}
		if (!report.getBalances().containsKey(account.getCode())) {
			if (account.getId() == null) {
				Account acc = AccountDAO.get(ctx, account.getCode());
				if (acc != null) {
					account.setId(acc.getId());
					account.setDescription(acc.getDescription());
				}
			}
			report.getBalances().put(account.getCode(), new AccountTrialBalance()
						.setId(account.getId())
						.setCode(account.getCode())
						.setDescription(account.getDescription()));
		}
		final AccountTrialBalance tot = report.getTotalBalance();
		final AccountTrialBalance bal = report.getBalances().get(account.getCode());
		
		IAccountStatementPeriodVisitor sumVisitor = new IAccountStatementPeriodVisitor() {
			@Override public void visitBeforePeriod() {
				bal.setBeforePeriodDebit(bal.getBeforePeriodDebit() + debit);
				bal.setBeforePeriodCredit(bal.getBeforePeriodCredit() + credit);
				tot.setBeforePeriodDebit(tot.getBeforePeriodDebit() + debit);
				tot.setBeforePeriodCredit(tot.getBeforePeriodCredit() + credit);
			}
			@Override public void visitInPeriodOpening() {
				bal.setInPeriodOpeningDebit(bal.getInPeriodOpeningDebit()  + debit);
				bal.setInPeriodOpeningCredit(bal.getInPeriodOpeningCredit() + credit);
				tot.setInPeriodOpeningDebit(tot.getInPeriodOpeningDebit()  + debit);
				tot.setInPeriodOpeningCredit(tot.getInPeriodOpeningCredit() + credit);
			}
			@Override public void visitInPeriodBefore() {
				bal.setInPeriodBeforeDebit(bal.getInPeriodBeforeDebit() + debit);
				bal.setInPeriodBeforeCredit(bal.getInPeriodBeforeCredit() + credit);
				tot.setInPeriodBeforeDebit(tot.getInPeriodBeforeDebit() + debit);
				tot.setInPeriodBeforeCredit(tot.getInPeriodBeforeCredit() + credit);
			}
			@Override public void visitInPeriod() {
				bal.setInPeriodDebit(bal.getInPeriodDebit()  + debit);
				bal.setInPeriodCredit(bal.getInPeriodCredit() + credit);
				tot.setInPeriodDebit(tot.getInPeriodDebit()  + debit);
				tot.setInPeriodCredit(tot.getInPeriodCredit() + credit);
			}
			@Override public void visitInPeriodAfter() {}
			@Override public void visitInPeriodClosing() {}
			@Override public void visitAfterPeriod() {}
		};
		accountStatementPeriod.accept(sumVisitor);
	}
	
	private static Condition getBasicCondition(AONContext ctx , IAccountParams params, boolean applyDateFilterIfNeeded ) {
		Condition condition = ACCOUNT_ENTRY_DETAIL.DOMAIN.equal(ctx.getDomainId());
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
		if (params.getActivity() != null) {
			if (AonMathUtils.isNegative(params.getActivity())) {
				// Sólo las comunes. Los apuntes sin activdad.
				condition = condition.and( ACCOUNT_ENTRY.ACTIVITY.isNull());
			} else {
				condition = condition.and( ACCOUNT_ENTRY.ACTIVITY.eq( params.getActivity() ));
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
		return condition;
	}

	private static Condition getBalanceCondition(AONContext ctx , AccountingReportParams params, boolean applyDateFilterIfNeeded ) {
		Condition condition = getBasicCondition(ctx, params, applyDateFilterIfNeeded);
		if (params.getAccount() != null && params.getAccount().getId() != null) {
			condition = condition.and(ACCOUNT_ENTRY_DETAIL.ACCOUNT.equal(params.getAccount().getId()));	
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
	
	private static Condition getOperatingCondition(AONContext ctx , AccountingReportParams params) {
		Condition condition = getBasicCondition(ctx, params, true);
		condition = appendCostCenterCondition(condition,params);
		return condition;
	}
	
	
	private static Condition appendCostCenterCondition(Condition condition,AccountingReportParams params) {
		if (params.getCostCenters() != null && params.getCostCenters().size() > 0) {
			Condition c = null;
			if (params.getCostCenters().contains(AccountingReportParams.EMPTY_COST_CENTER_ACCOUNT)) {
				@SuppressWarnings("unchecked")
				HashSet<String> cloned = (HashSet<String>) params.getCostCenters().clone();
				cloned.remove(AccountingReportParams.EMPTY_COST_CENTER_ACCOUNT);
				c = ACCOUNT.COST_CENTER.isNull().or(ACCOUNT.COST_CENTER.in( cloned ));	
			} else {
				c = ACCOUNT.COST_CENTER.in( params.getCostCenters());
			}
			condition = condition.and( c );
		}
		return condition;
	}

	private static Condition getTrialBalanceCondition(AONContext ctx , AccountingReportParams params) {
		Condition condition = getBasicCondition(ctx, params, false);
		if (params.getAccount() != null && AonStringUtils.isNotBlank(params.getAccount().getCode())) {
			Condition accountCondition = null;
			String[] accounts = AonStringUtils.split(params.getAccount().getCode(), '|');
			for (String account : accounts) {
				account = AonStringUtils.replace(account, AonStringUtils.ASTERISK, AonStringUtils.EMPTY);
				if (AonStringUtils.isNotBlank(account)) {
					if (AonStringUtils.isNumeric(account)) {
						String code = AonStringUtils.replace(account, AonStringUtils.ASTERISK, AonStringUtils.PERCENT);
						if (!AonStringUtils.endsWith(code, AonStringUtils.PERCENT)) {
							code = code + AonStringUtils.PERCENT;
						}	
						Condition codeCondition = ACCOUNT.CODE.like(code); 
						accountCondition = accountCondition == null
								?codeCondition
								:accountCondition.or( codeCondition );
					} else {
						String descr = AonStringUtils.replace(account, AonStringUtils.ASTERISK, AonStringUtils.PERCENT);
						if (!AonStringUtils.startsWith(descr, AonStringUtils.PERCENT)) {
							descr = AonStringUtils.PERCENT + descr;
						}
						if (!AonStringUtils.endsWith(descr, AonStringUtils.PERCENT)) {
							descr = descr + AonStringUtils.PERCENT;
						}	
						Condition descAliasCondition = ACCOUNT.DESCRIPTION.like(descr).or(ACCOUNT.ALIAS.like(descr)); 
						accountCondition = accountCondition == null
								?descAliasCondition
								:accountCondition.or( descAliasCondition );
					}
				}
			}
			if(accountCondition != null) {
				condition = condition.and(accountCondition);
			}
		}
		condition = appendCostCenterCondition(condition,params);
		return condition;
	}
	
	private static Condition getLedgerCondition(AONContext ctx , AccountingReportParams params) {
		Condition condition = getBasicCondition(ctx, params, true);
		if (params.getAccount() != null && AonStringUtils.isNotBlank(params.getAccount().getCode())) {
			Condition accountCondition = null;
			String[] accounts = AonStringUtils.split(params.getAccount().getCode(), '|');
			for (String account : accounts) {
				account = AonStringUtils.replace(account, AonStringUtils.ASTERISK, AonStringUtils.EMPTY);
				if (AonStringUtils.isNotBlank(account)) {
					if (AonStringUtils.isNumeric(account)) {
						String code = AonStringUtils.replace(account, AonStringUtils.ASTERISK, AonStringUtils.PERCENT);
						if (!AonStringUtils.endsWith(code, AonStringUtils.PERCENT)) {
							code = code + AonStringUtils.PERCENT;
						}	
						Condition codeCondition = DET_ACCOUNT.CODE.like(code); 
						accountCondition = accountCondition == null
								?codeCondition
								:accountCondition.or( codeCondition );
					} else {
						String descr = AonStringUtils.replace(account, AonStringUtils.ASTERISK, AonStringUtils.PERCENT);
						if (!AonStringUtils.startsWith(descr, AonStringUtils.PERCENT)) {
							descr = AonStringUtils.PERCENT + descr;
						}
						if (!AonStringUtils.endsWith(descr, AonStringUtils.PERCENT)) {
							descr = descr + AonStringUtils.PERCENT;
						}	
						Condition descAliasCondition = DET_ACCOUNT.DESCRIPTION.like(descr).or(DET_ACCOUNT.ALIAS.like(descr)); 
						accountCondition = accountCondition == null
								?descAliasCondition
								:accountCondition.or( descAliasCondition );
					}
				}
			}
			if(accountCondition != null) {
				condition = condition.and(accountCondition);
			}
		}
		condition = appendCostCenterCondition(condition,params);
		return condition;
	}
	
	// *********************************************************************************************************
	// ********************************* BALANCE DE SUMAS Y SALDOS *********************************************
	// *********************************************************************************************************
	private static interface IBalanceKeyCallback {
		IAccMiningKeyAccept getAccepter();
		IBalanceKey getKey(String value);
	}
	
	public static AccountBalanceReport balanceReport(AONContext ctx, AccountingReportParams params) {
		if (params.getBalanceType() == BalanceType.BALANCE_ABBREVIATE) {
			return balanceReport(ctx,params,AccBOEBalanceAbbreviateKey.values(), new IBalanceKeyCallback() {
				
				@Override
				public IBalanceKey getKey(String value) {
					return AccBOEBalanceAbbreviateKey.valueOf(value);
				}
				
				@Override
				public IAccMiningKeyAccept getAccepter() {
					return new IAccMiningKeyAccept() {
						
						@Override
						public boolean acceptKey(Object key) {
							try {
								return (AccBOEBalanceAbbreviateKey.valueOf((String) key) != null);	
							} catch (IllegalArgumentException e) {
								return false;
							}
						}
					};
				}
			}); 
		} else if (params.getBalanceType() == BalanceType.BALANCE_PYMES) {
			return balanceReport(ctx,params,AccBOEBalancePYMESKey.values(), new IBalanceKeyCallback() {
				
				@Override
				public IBalanceKey getKey(String value) {
					return AccBOEBalancePYMESKey.valueOf(value);
				}
				
				@Override
				public IAccMiningKeyAccept getAccepter() {
					return new IAccMiningKeyAccept() {
						
						@Override
						public boolean acceptKey(Object key) {
							try {
								return (AccBOEBalanceAbbreviateKey.valueOf((String) key) != null);	
							} catch (IllegalArgumentException e) {
								return false;
							}
						}
					};
				}
			}); 
		} else if (params.getBalanceType() == BalanceType.BALANCE_NORMAL) {
			return balanceReport(ctx,params,AccBOEBalanceNormalKey.values(), new IBalanceKeyCallback() {
				
				@Override
				public IBalanceKey getKey(String value) {
					return AccBOEBalanceNormalKey.valueOf(value);
				}
				
				@Override
				public IAccMiningKeyAccept getAccepter() {
					return new IAccMiningKeyAccept() {
				
						@Override
						public boolean acceptKey(Object key) {
							try {
								return (AccBOEBalanceNormalKey.valueOf((String) key) != null);	
							} catch (IllegalArgumentException e) {
								return false;
							}
						}
					};
				}
			}); 
		} else if (params.getBalanceType() == BalanceType.PYG_NORMAL) {
			return balanceReport(ctx,params,AccBOEPyGNormalKey.values(), new IBalanceKeyCallback() {
				
				@Override
				public IBalanceKey getKey(String value) {
					return AccBOEPyGNormalKey.valueOf(value);
				}
				
				@Override
				public IAccMiningKeyAccept getAccepter() {
					return new IAccMiningKeyAccept() {
				
						@Override
						public boolean acceptKey(Object key) {
							try {
								return (AccBOEPyGNormalKey.valueOf((String) key) != null);	
							} catch (IllegalArgumentException e) {
								return false;
							}
						}
					};
				}
			}); 
		} else if (params.getBalanceType() == BalanceType.PYG_ABBREVIATE) {
			return balanceReport(ctx,params,AccBOEPyGAbbreviateKey.values(), new IBalanceKeyCallback() {
				
				@Override
				public IBalanceKey getKey(String value) {
					return AccBOEPyGAbbreviateKey.valueOf(value);
				}
				
				@Override
				public IAccMiningKeyAccept getAccepter() {
					return new IAccMiningKeyAccept() {
				
						@Override
						public boolean acceptKey(Object key) {
							try {
								return (AccBOEPyGAbbreviateKey.valueOf((String) key) != null);	
							} catch (IllegalArgumentException e) {
								return false;
							}
						}
					};
				}
			}); 
		} 
		throw new AonCoreException("No se ha indicado un tipo de balance adecuado");	
	}

	public static AccountBalanceReport balanceReport(AONContext ctx, AccountingReportParams params, IBalanceKey[] keys,IBalanceKeyCallback callback) {
		AccountBalanceReport report = new AccountBalanceReport();
		report.setParams(params);
		report.setSelectedPeriod( AccountPeriodDAO.getPeriod(ctx, params.getPeriod()) );
		if (report.getSelectedPeriod() == null) {
			throw new AonCoreException("No se ha indicado ejercicio contable");
		}
		if (params.getFromDate() != null && params.getFromDate().before(report.getSelectedPeriod().getInitiationDate())) {
			throw new AonCoreException("La fecha desde indicada es anterior al inicio del ejercicio");
		}
		if (params.getToDate() != null && params.getToDate().after(report.getSelectedPeriod().getDeadline())) {
			throw new AonCoreException("La fecha hasta indicada es posterior al final del ejercicio");
		}
		if (params.getActivity() != null) {
			report.setSelectedActivity( CompanyDAO.getEnterpriseActivity(ctx, params.getActivity()) );
		}
		LinkedHashMap<DateInterval,AccountingReportParams> intervals = getDateIntervals(ctx,params);
		for (DateInterval inter : intervals.keySet()) {
			fillReport(ctx, intervals.get(inter), keys, callback, report, inter.getName());
		}
		
		return report;
	}

	private static void fillReport(AONContext ctx, AccountingReportParams params, IBalanceKey[] keys, IBalanceKeyCallback callback, AccountBalanceReport report, String bal) {
		AccMiningMVELContext mvelCtx = new AccMiningMVELContext( callback.getAccepter() );
		LinkedHashMap<String,String> initialMap = new LinkedHashMap<String,String>();
		LinkedHashMap<String,String> computeMap = new LinkedHashMap<String,String>();  
		
		AccMiningParameters mParams = new AccMiningParameters();
		mParams.setDomain(ctx.getDomainId());
		mParams.setStartDate( params.getFromDate() );
		mParams.setEndDate( params.getToDate() );
		Map<String, AccountBalance> accounts = ACCOUNTING.getAccountBalances(ctx, mParams
				, params.getBalanceType()==BalanceType.PYG_NORMAL || params.getBalanceType()==BalanceType.PYG_ABBREVIATE); 
		mvelCtx.setAccounts( accounts );
		
		for (IBalanceKey key : keys) {
			if ( !report.getBalances().containsKey(key.getCode()) ) {
				report.getBalances().put( key.getCode(), new BalanceLine()
						.setLevel(key.getLevel())
						.setPrefix(key.getPrefix())
						.setCode(key.getCode())
						.setDescription(key.getName())
						.setLeaf(key.isLeaf())
						.setAccounts( parseExpression(key.getInitialExpression()) ));
				mvelCtx.put(key.getCode(), 0.0 );
			}
			String exp = key.getInitialExpression();
			if (AonStringUtils.isNotBlank(exp)) {
				initialMap.put(key.getCode(), exp);
			}
			String computeExp = key.getComputeExpression();
			if (AonStringUtils.isNotBlank(computeExp)) {
				computeMap.put(key.getCode(), computeExp);
			}
		}
		mvelCtx.setExpressionMap(initialMap);
		for (String keyCode : mvelCtx.getExpressionMap().keySet()) {
			String initialExp = mvelCtx.getExpressionMap().get(keyCode);
			mvelCtx.put(keyCode, 0.0 );
			if (AonStringUtils.isNotBlank(initialExp)) {
				Object ret = mvelCtx.evaluateExpression(keyCode,initialExp);
				mvelCtx.put(keyCode, ret );
				report.setAmount(keyCode,bal,AonNumberUtils.todouble(ret));
			}
		}
		
		// Se chequean las cuentas que no se han tenido en cuenta, para facilitar al cliene la búsqueda del descuadre.
		LinkedList<AccountBalance> unreadBalances = new LinkedList<AccountBalance>();
		for (String code : mvelCtx.getAccounts().keySet()) {
				AccountBalance accountBalance = mvelCtx.getAccounts().get(code);
				if (!accountBalance.isChecked() ) {
					if (AonStringUtils.length(code) == 4
					&& !mvelCtx.getAccounts().get(AonStringUtils.substring(code, 0,3)).isChecked()
					&& !mvelCtx.getAccounts().get(AonStringUtils.substring(code, 0,2)).isChecked()
					&& !mvelCtx.getAccounts().get(AonStringUtils.substring(code, 0,1)).isChecked()) {
					Account account = AccountDAO.get(ctx, code);
					accountBalance.setAccountDescription(account!=null?account.getDescription():null);
					accountBalance.setAccountCode(code);
					unreadBalances.add(accountBalance);			
				}
			}
		}
		report.getUnreadAccounts().put(bal, unreadBalances);
		/// [fin chequeo]
		
		mvelCtx.getExpressionMap().clear();
		mvelCtx.setExpressionMap(computeMap);
		for (String keyCode : mvelCtx.getExpressionMap().keySet()) {
			String exp = mvelCtx.getExpressionMap().get(keyCode);
			if (AonStringUtils.isNotBlank(exp)) {
				Object ret = mvelCtx.evaluateExpression(keyCode,exp);
				mvelCtx.put(keyCode, ret );
				report.setAmount(keyCode,bal,AonNumberUtils.todouble(ret));
			}
		}
	}

	private static String parseExpression(String initialExpression) {
		if (AonStringUtils.isBlank(initialExpression)) return null; 
		Pattern p = Pattern.compile("-?\\d+");
		Matcher m = p.matcher(initialExpression);
		StringBuffer buf = new StringBuffer();
		while (m.find()) {
			if (buf.length() > 0) {
				buf.append('|');	
			}
			buf.append(m.group());
		}
		return buf.toString();
	}
}
