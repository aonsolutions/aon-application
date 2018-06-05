package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.TreeMap;

import com.esferalia.aon.watson.util.AonMathUtils;

public class AccountTrialBalanceReport implements Serializable{

	private static final long serialVersionUID = 7672716172785126558L;
	
	public static class AccountTrialBalance implements Serializable {
		
		private static final long serialVersionUID = 8836372167741926367L;
		
		private Integer id;
		private String code;
		private String description;
		
		private double beforePeriodDebit;
		private double beforePeriodCredit;
		
		private double inPeriodOpeningDebit;
		private double inPeriodOpeningCredit; 
		
		private double inPeriodBeforeDebit;
		private double inPeriodBeforeCredit;
		
		private double inPeriodDebit;
		private double inPeriodCredit;
		
		public Integer getId() {
			return id;
		}
		public AccountTrialBalance setId(Integer id) {
			this.id = id;
			return this;
		}
		
		public String getCode() {
			return code;
		}
		public AccountTrialBalance setCode(String code) {
			this.code = code;
			return this;
		}
		
		public String getDescription() {
			return description;
		}
		public AccountTrialBalance setDescription(String description) {
			this.description = description;
			return this;
		}
		
		public double getBeforePeriodDebit() {
			return beforePeriodDebit;
		}
		public AccountTrialBalance setBeforePeriodDebit(double beforePeriodDebit) {
			this.beforePeriodDebit = beforePeriodDebit;
			return this;
		}
		
		public double getBeforePeriodCredit() {
			return beforePeriodCredit;
		}
		public AccountTrialBalance setBeforePeriodCredit(double beforePeriodCredit) {
			this.beforePeriodCredit = beforePeriodCredit;
			return this;
		}
		
		public double getInPeriodOpeningDebit() {
			return inPeriodOpeningDebit;
		}
		public AccountTrialBalance setInPeriodOpeningDebit(double inPeriodOpeningDebit) {
			this.inPeriodOpeningDebit = inPeriodOpeningDebit;
			return this;
		}
		
		public double getInPeriodOpeningCredit() {
			return inPeriodOpeningCredit;
		}
		public AccountTrialBalance setInPeriodOpeningCredit(double inPeriodOpeningCredit) {
			this.inPeriodOpeningCredit = inPeriodOpeningCredit;
			return this;
		}
		
		public double getInPeriodBeforeDebit() {
			return inPeriodBeforeDebit;
		}
		public AccountTrialBalance setInPeriodBeforeDebit(double inPeriodBeforeDebit) {
			this.inPeriodBeforeDebit = inPeriodBeforeDebit;
			return this;
		}
		
		public double getInPeriodBeforeCredit() {
			return inPeriodBeforeCredit;
		}
		public AccountTrialBalance setInPeriodBeforeCredit(double inPeriodBeforeCredit) {
			this.inPeriodBeforeCredit = inPeriodBeforeCredit;
			return this;
		}
		
		public double getInPeriodDebit() {
			return inPeriodDebit;
		}
		public AccountTrialBalance setInPeriodDebit(double inPeriodDebit) {
			this.inPeriodDebit = inPeriodDebit;
			return this;
		}
		
		public double getInPeriodCredit() {
			return inPeriodCredit;
		}
		public AccountTrialBalance setInPeriodCredit(double inPeriodCredit) {
			this.inPeriodCredit = inPeriodCredit;
			return this;
		}
		
		public double getBeforePeriodDebitBalance() {
			return beforePeriodDebit > beforePeriodCredit ? AonMathUtils.round(beforePeriodDebit - beforePeriodCredit): 0.0;
		}

		public double getBeforePeriodUnpaidBalance() {
			return beforePeriodCredit > beforePeriodDebit ? AonMathUtils.round(beforePeriodCredit - beforePeriodDebit): 0.0;
		}

		public double getInPeriodOpeningDebitBalance() {
			return inPeriodOpeningDebit > inPeriodOpeningCredit? AonMathUtils.round(inPeriodOpeningDebit - inPeriodOpeningCredit): 0.0;
		}

		public double getInPeriodOpeningUnpaidBalance() {
			return inPeriodOpeningCredit > inPeriodOpeningDebit? AonMathUtils.round(inPeriodOpeningCredit - inPeriodOpeningDebit): 0.0;
		}

		public double getInPeriodBeforeDebitBalance() {
			return inPeriodBeforeDebit > inPeriodBeforeCredit? AonMathUtils.round(inPeriodBeforeDebit - inPeriodBeforeCredit): 0.0;
		}

		public double getInPeriodBeforeUnpaidBalance() {
			return inPeriodBeforeCredit > inPeriodBeforeDebit ? AonMathUtils.round(inPeriodBeforeCredit - inPeriodBeforeDebit): 0.0;
		}

		public double getInPeriodDebitBalance() {
			return inPeriodDebit > inPeriodCredit ? AonMathUtils.round(inPeriodDebit - inPeriodCredit) : 0.0;
		}

		public double getInPeriodUnpaidBalance() {
			return inPeriodCredit > inPeriodDebit ? AonMathUtils.round(inPeriodCredit - inPeriodDebit) : 0.0;
		}

		public double getAfterPeriodDebitBalance() {
			double d = AonMathUtils.round( 
				(beforePeriodDebit+inPeriodOpeningDebit+inPeriodBeforeDebit+inPeriodDebit)
			   -(beforePeriodCredit+inPeriodOpeningCredit+inPeriodBeforeCredit+inPeriodCredit)
			);
			return AonMathUtils.isGreatherThanZero(d)?d:0.0;
		}

		public double getAfterPeriodUnpaidBalance() {
			double d = AonMathUtils.round( 
					(beforePeriodCredit+inPeriodOpeningCredit+inPeriodBeforeCredit+inPeriodCredit
				   -(beforePeriodDebit+inPeriodOpeningDebit+inPeriodBeforeDebit+inPeriodDebit))
				);
				return AonMathUtils.isGreatherThanZero(d)?d:0.0;
		}
		public boolean hasPeriodEntries() {
			return AonMathUtils.isNotZero(getInPeriodDebit()) || AonMathUtils.isNotZero(getInPeriodCredit());
		}
	}
	
	private AccountPeriod period;
	private AccountingReportParams params;
	private TreeMap<String, AccountTrialBalance> balances = new TreeMap<String, AccountTrialBalance>();
	private boolean hasBeforePeriodAmounts;
	private boolean hasOpeningAmounts;
	private boolean hasInPeriodPreviousAmounts;
	private AccountTrialBalance totalBalance = new AccountTrialBalance();
	
	public AccountPeriod getPeriod() {
		return period;
	}
	public AccountTrialBalanceReport setPeriod(AccountPeriod period) {
		this.period = period;
		return this;
	}
	public AccountingReportParams getParams() {
		return params;
	}
	public AccountTrialBalanceReport setParams(AccountingReportParams params) {
		this.params = params;
		return this;
	}
	
	public TreeMap<String, AccountTrialBalance> getBalances() {
		return balances;
	}
	public void setBalances(TreeMap<String, AccountTrialBalance> newBalances) {
		this.balances = newBalances;
	}
	public boolean isEmpty() {
		return balances == null || balances.size() == 0;
	}
	
	public boolean hasBeforePeriodAmounts() {
		return hasBeforePeriodAmounts;
	}
	public AccountTrialBalanceReport setHasBeforePeriodAmounts(boolean hasBeforePeriodAmounts) {
		this.hasBeforePeriodAmounts = hasBeforePeriodAmounts;
		return this;
	}
	public boolean hasOpeningAmounts() {
		return hasOpeningAmounts;
	}
	public AccountTrialBalanceReport setHasOpeningAmounts(boolean hasOpeningAmounts) {
		this.hasOpeningAmounts = hasOpeningAmounts;
		return this;
	}
	public boolean hasInPeriodPreviousAmounts() {
		return hasInPeriodPreviousAmounts;
	}
	public AccountTrialBalanceReport setHasInPeriodPreviousAmounts(boolean hasInPeriodPreviousAmounts) {
		this.hasInPeriodPreviousAmounts = hasInPeriodPreviousAmounts;
		return this;
	}
	public AccountTrialBalance getTotalBalance() {
		return totalBalance;
	}
	
}
