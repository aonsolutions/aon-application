package com.esferalia.aon.occam.test.faker;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.TreeMap;
import java.util.TreeSet;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountOperatingAccount;
import com.esferalia.aon.occam.api.model.AccountOperatingReport;
import com.esferalia.aon.occam.api.model.AccountOperatingReport.AccountOperatingStatement;
import com.esferalia.aon.occam.api.model.AccountOperatingReport.AccountOperatingStatementType;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport.AccountTrialBalance;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.DateInterval;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.github.javafaker.Faker;

public class AccountingFaker {
	private static Faker faker = new Faker( new Locale("es") );

	public static AccountPeriod getTodayActiveAccountPeriod(AONContext ctx) {
		return getAccountPeriod(ctx, new Date(), AccountPeriodStatus.ACTIVE); 
	}

	public static AccountPeriod getAccountPeriod(AONContext ctx, Date date, AccountPeriodStatus status) {
		return  new AccountPeriod()
			.setDomain(ctx.getDomainId())	
			.setDomain(ctx.getDomainId())
			.setName(AonNumberUtils.toString( AonDateUtils.getYear(date)))
			.setInitiationDate(AonDateUtils.getYearFirstDay(date))
			.setDeadline(AonDateUtils.getYearLastDay(date))
			.setStatus(status);
	}
	
	public static Account getAccount(AONContext ctx, String code) {
		byte level = (byte) (code.length() > 4 ? 5 : code.length());
		return  new Account()
				.setActive(true)
				.setAlias(AonStringUtils.substring(faker.artist().name(), 0, 32))
				.setCode(code)
				.setCostCenter(AonStringUtils.substring(faker.address().cityName(), 0, 32))
				.setDescription(AonStringUtils.substring(faker.gameOfThrones().house(), 0, 128))
				.setDomain(ctx.getDomainId())
				.setLevel(level)
				.setEntryEnabled(level == 5)
				;
	}

	public static AccountingReportParams getAccountingReportParams(AONContext ctx) {
		return new AccountingReportParams()
			.setDomainName(faker.internet().domainName())
			.setDomain( AonRandom.getInt(0, 10000000) )
			.setUser( AonRandom.string(5, 15 ))
			.setPeriod( AonRandom.integer(15, 1000) )
			.setFromDate( AonRandom.getPastDate(10) )
			.setToDate( AonRandom.getPastDate(10) )
			.setAccount( AonRandom.getAccount(ctx,80))
			.setLevel( AonRandom.getInt(0, 9) )
			.setActivity( AonRandom.integer(75, 1000) )
			.setSecurityLevel( AonRandom.getRandomSecurityLevel())
			.setDocumentNumber( AonRandom.string(5, 15 ))
			.setPreviousPeriods( AonRandom.getInt(0, 9) )
			.setLowLevelAccountVisible(AonRandom.gt( 50 ))
			.setNoActivityAccountVisible(AonRandom.gt( 50 ))
			.setNoBalanceAccountExcluded(AonRandom.gt( 50 ))
			.setPercentsEnabled(AonRandom.gt( 50 ))
			.setByMonth(AonRandom.gt( 50 ))
			.setOpeningEntriesExcluded(AonRandom.gt( 50 ))
			.setOperatingEntriesExcluded(AonRandom.gt( 50 ))
			.setClosingEntriesExcluded(AonRandom.gt( 50 ))
			.setReverseOrder(AonRandom.gt( 50 ))
			.setBalanceType(AonRandom.getRandomBalanceType())
			.setSelectedPeriod(AonRandom.getAccountPeriod(ctx, 85))
			.setSelectedActivity(AonRandom.getRandomActivity(ctx))
			.setSelectedAccount( AonRandom.getAccount(ctx,80))
			.setBreakdownEnabled(AonRandom.gt( 50 ))
			.setLedgerAccount( AonRandom.integer(75, 1000) )
			.setLedgerDebitBalance( AonRandom.getDouble(0, 1000, 2))
			.setLedgerUnpaidBalance( AonRandom.getDouble(0, 1000, 2))
			.setConsolidation(AonRandom.gt( 50 ))
			.setRegistry( AonRandom.integer(75, 1000) )
			.setOutput(AonRandom.gt( 50 ))
			.setVatSummaryType(AonRandom.getRandomVatSummaryType())
			.setPercent( AonRandom.getDouble(0, 100, 2)) 
			.setRectificationType(AonRandom.getRandomRectificationType())
			.setSurcharge(AonRandom.gt( 50 ))
			.setFarmerRegime(AonRandom.gt( 50 ))
			.setAccrualRegime(AonRandom.gt( 50 ))
			.setInvestment(AonRandom.gt( 50 ))
			.setService(AonRandom.gt( 50 ))
			.setTitle( AonRandom.string(50, 100 ))
			.setSubject( AonRandom.string(50, 100 ))
			.setShowCover(AonRandom.gt( 50 ))
			.setPageOffset( AonRandom.getInt(0, 50 ))
			.setPageOffsetText(AonRandom.string(50, 100 ))
			.setHideFilter(AonRandom.gt( 50 ))
			.setHeaderText(AonRandom.string(50, 100 ))
			.setHideDateTimeOnFooter(AonRandom.gt( 50 ))
			.setFooterText(AonRandom.string(50, 100 ))
		;
//		private Integer[] invoices;
//		private LinkedList<Domain> domains;
//		private HashSet<String> costCenters;
	}

	public static AccountTrialBalance getAccountTrialBalance(AONContext ctx) {
		return new AccountTrialBalance()
			.setId( AonRandom.integer(5, 1000000 ))
			.setCode(AonRandom.string( 5, 9 ))
			.setDescription(AonRandom.string( 5, 39 ))
			.setBeforePeriodDebit(AonRandom.getDouble(0, 150000))
			.setBeforePeriodCredit(AonRandom.getDouble(0, 150000))
			.setInPeriodOpeningDebit(AonRandom.getDouble(0, 150000))
			.setInPeriodOpeningCredit(AonRandom.getDouble(0, 150000))
			.setInPeriodBeforeDebit(AonRandom.getDouble(0, 150000))
			.setInPeriodBeforeCredit(AonRandom.getDouble(0, 150000))
			.setInPeriodDebit(AonRandom.getDouble(0, 150000))
			.setInPeriodCredit(AonRandom.getDouble(0, 150000))
			;
	}

	public static AccountTrialBalanceReport getAccountTrialBalanceReport(AONContext ctx) {
		AccountTrialBalanceReport report = new AccountTrialBalanceReport()
				.setParams(getAccountingReportParams(ctx))
				.setHasBeforePeriodAmounts(AonRandom.gt( 50 ))
				.setHasOpeningAmounts(AonRandom.gt( 50 ))
				.setHasInPeriodPreviousAmounts(AonRandom.gt( 50 ))
				.setTotalBalance(getAccountTrialBalance(ctx));
		Integer times = AonRandom.integer(10, 50);
		if (times != null) {
			AccountTrialBalance bal = getAccountTrialBalance(ctx);
			if (AonStringUtils.isNotBlank( bal.getCode())){
				if (report.getBalances() == null) {
					report.setBalances(new TreeMap<String, AccountTrialBalance>());
				}
				report.getBalances().put(bal.getCode(), bal);
			}
		}
		return report;
	}

	public static AccountOperatingReport getAccountOperatingReport (AONContext ctx) {
		AccountOperatingReport report = new AccountOperatingReport();
		report.setParams(getAccountingReportParams(ctx));
		
		TreeSet<AccountOperatingAccount> accounts = new TreeSet<AccountOperatingAccount>();
		for (int i=0; i<AonRandom.getInt(1, 20); i++) {
			AccountOperatingAccount account = new AccountOperatingAccount()
				.setCode(String.valueOf(AonRandom.getInt(0, 999999999)))
				.setDescription(AonRandom.lorem(25, 9))
				.setId(AonRandom.getInt(0, 5));
			AccountOperatingStatementType[] types = AccountOperatingStatementType.values();
			account.setType(types[AonRandom.getInt(0, types.length-1)]);
			accounts.add(account);
		}
		
		report.setAccounts(accounts);
		
		TreeSet<DateInterval> intervals = new TreeSet<DateInterval>();
		for (int i=0; i<AonRandom.getInt(1, 12); i++) {
			DateInterval interval = new DateInterval();
			interval.setName(AonRandom.name(25, 10));
			Date sDate = AonRandom.getPastDate(0);
			sDate = sDate != null ? sDate : new Date(0);
			interval.setStart(sDate);
			long startTime = interval.getStart()!=null?interval.getStart().getTime():0;
			long endTime = startTime + AonRandom.getInt(0, 1000000000);
			interval.setEnd(new Date(endTime));
			intervals.add(interval);
			for (int j=0; j<AonRandom.getInt(1, 10); j++) {
				AccountOperatingStatement statement = new AccountOperatingStatement()
				.setAccount(accounts.stream().findAny().orElseGet(null))
				.setCredit(AonRandom.getDouble(0, 10000))
				.setDebit(AonRandom.getDouble(0, 10000))
				.setExpensesRatio(AonRandom.getDouble(0, 100))
				.setIncreasePercent(AonRandom.getDouble(0, 100))
				.setMonth(AonRandom.string(80, 10))
				.setPurchasesRatio(AonRandom.getDouble(0, 100))
				.setSalesRatio(AonRandom.getDouble(0, 100));
				report.put(interval, statement);
			}
		}
		report.setIntervals(intervals);
		return report;
	}
	
	public static Collection<AccountPeriod> getAccountPeriods(AONContext ctx) {
		Collection<AccountPeriod> periods = new LinkedList<AccountPeriod>();
		for (int i=0; i<AonRandom.getInt(0, 10); i++) {
			periods.add(AonRandom.getAccountPeriod(ctx, 80));
		}
		return periods;
	}
}
