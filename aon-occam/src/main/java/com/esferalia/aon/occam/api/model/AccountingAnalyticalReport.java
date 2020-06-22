package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.TreeMap;
import java.util.TreeSet;

import com.esferalia.aon.occam.api.model.AccountOperatingReport.AccountOperatingStatementType;
import com.esferalia.aon.occam.api.model.accounting.analytical.Analytical;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountingAnalyticalReport implements Serializable {

	private static final long serialVersionUID = -3452585307541273807L;
	
	/**
	 * @author ecastellano
	 *
	 */
	/**
	 * @author ecastellano
	 *
	 */
	public static class AccountingAnalyticalColumn implements Serializable, Comparable<AccountingAnalyticalColumn> {

		private static final long serialVersionUID = 3412611142698585498L;
		
		private String name;
		private double percent;
		private boolean totalColumn;
		private boolean defaultColumn;

		public String getName() {
			return name;
		}
		public AccountingAnalyticalColumn setName(String name) {
			this.name = name;
			return this;
		}
		public double getPercent() {
			return percent;
		}
		public AccountingAnalyticalColumn setPercent(double percent) {
			this.percent = percent;
			return this;
		}
		
		public boolean isTotalColumn() {
			return totalColumn;
		}
		public AccountingAnalyticalColumn setTotalColumn(boolean totalColumn) {
			this.totalColumn = totalColumn;
			return this;
		}
		public boolean isDefaultColumn() {
			return defaultColumn;
		}
		public AccountingAnalyticalColumn setDefaultColumn(boolean defaultColumn) {
			this.defaultColumn = defaultColumn;
			return this;
		}
		@Override
		public int compareTo(AccountingAnalyticalColumn other) {
			return AonStringUtils.compare(getName(), other.getName());
		}
	}
	
	public static class AccountingAnalyticalStatement implements Serializable, Cloneable {

		private static final long serialVersionUID = -1037494768508954210L;
		
		private AccountOperatingAccount account; 
		private double debit;
		private double credit;
		private double percent;

		protected AccountingAnalyticalStatement clone() {
			return new AccountingAnalyticalStatement()
					.setAccount( getAccount().clone())
					.setDebit(debit)
					.setCredit(credit)
					.setPercent(percent)
					;
		}
		public boolean isCalculated() {
			return getAccount() != null && getAccount().getType() != null && getAccount().getType().isCalculated();
		}
		public boolean modifies() {
			return getAccount() != null && getAccount().getType() != null && getAccount().getType().modifies() != null;
		}
		
		public AccountOperatingAccount getAccount() {
			return account;
		}
		public AccountingAnalyticalStatement setAccount(AccountOperatingAccount account) {
			this.account = account;
			return this;
		}
		
		public double getDebit() {
			return debit;
		}
		public AccountingAnalyticalStatement setDebit(double debit) {
			this.debit = debit;
			return this;
		}

		public double getCredit() {
			return credit;
		}
		public AccountingAnalyticalStatement setCredit(double credit) {
			this.credit = credit;
			return this;
		}
		
		public double getPercent() {
			return percent;
		}
		public AccountingAnalyticalStatement setPercent(double percent) {
			this.percent = percent;
			return this;
		}

		public double getBalance() {
			return AonMathUtils.round( credit - debit);
		}
		public double getAmount() {
			return AonMathUtils.round( (credit - debit) * percent / 100);
		}
		public double getPercentDebit() {
			return AonMathUtils.round( debit * percent / 100);
		}
		public double getPercentCredit() {
			return AonMathUtils.round( credit * percent / 100);
		}

		public double getDebitBalance() {
			double d = AonMathUtils.round( debit - credit);
			return d>0?d:0.0;
		}
		
		public double getUnpaidBalance() {
			double d = AonMathUtils.round( credit - debit);
			return d>0?d:0.0;
		}
	}
	private AccountingReportParams params;
	private Analytical analytical;
	private TreeSet<AccountOperatingAccount> accounts = new TreeSet<AccountOperatingAccount>();
	
	private TreeSet<AccountingAnalyticalColumn> columns = new TreeSet<AccountingAnalyticalColumn>();
	private TreeMap<String, TreeMap<AccountingAnalyticalColumn, AccountingAnalyticalStatement>> map = 
			new TreeMap<String, TreeMap<AccountingAnalyticalColumn, AccountingAnalyticalStatement>>();
	
	public AccountingReportParams getParams() {
		return params;
	}
	public AccountingAnalyticalReport setParams(AccountingReportParams params) {
		this.params = params;
		return this;
	}
	public Analytical getAnalytical() {
		return analytical;
	}
	public AccountingAnalyticalReport setAnalytical(Analytical analytical) {
		this.analytical = analytical;
		return this;
	}
	
	public TreeSet<AccountOperatingAccount> getAccounts() {
		return accounts;
	}
	public AccountingAnalyticalReport setAccounts(TreeSet<AccountOperatingAccount> accounts) {
		this.accounts = accounts;
		return this;
	}
	
	public TreeSet<AccountingAnalyticalColumn> getColumns() {
		return columns;
	}

	public void ensureColumn(AccountingAnalyticalColumn column) {
		if (column != null && !columns.contains(column)) {
			columns.add(column);
		}
	}
	
	private String ensureAccount(AccountingAnalyticalStatement aas) {
		AccountOperatingAccount account = aas.getAccount();
		if (!accounts.contains(account)) {
			accounts.add(account);
		}
		if (!map.containsKey(account.getCode())) {
			map.put(account.getCode(), new TreeMap<AccountingAnalyticalColumn, AccountingAnalyticalStatement>());
		}
		return account.getCode();
	}

	public AccountingAnalyticalStatement get(String accountCode, AccountingAnalyticalColumn column) {
		if (map.containsKey(accountCode)) {
			return map.get(accountCode).get(column);
		}
		return null;
	}

	public void put(AccountingAnalyticalColumn column, AccountingAnalyticalStatement aas) {
		String code = ensureAccount(aas);
		ensureColumn( column);
		AccountingAnalyticalStatement exist = map.get(code).get(column);
		if ( exist == null) {
			map.get(code).put(column, aas.clone());
		} else {
			exist.setDebit( AonMathUtils.round(exist.getDebit() + aas.getDebit() ));
			exist.setCredit( AonMathUtils.round(exist.getCredit() + aas.getCredit() ));
		}
//		AccountOperatingStatementType modifies = aas.getAccount().getType().modifies();
//		if (modifies != null) {
//			AccountingAnalyticalStatement total = new  AccountingAnalyticalStatement()
//				.setAccount(new AccountOperatingAccount()
//					.setType(modifies)
//					.setCode(modifies.toString())
//					.setDescription(modifies.getDescription()))
//				.setDebit(aas.getDebit())
//				.setCredit(aas.getCredit())
//				.setPercent(-1);
//			put(column, total);
//		}
	}
	
	public boolean isEmpty() {
		return getAccounts().size() == 0;
	}
	
	public void calculate() {
		for (AccountOperatingAccount account : getAccounts() ) {
			for (AccountingAnalyticalColumn column : getColumns() ) {
				AccountingAnalyticalStatement aas = get(account.getCode(), column);
				if (aas != null && aas.modifies()) {
					AccountOperatingStatementType modifies = aas.getAccount().getType().modifies();
					if (modifies != null) {
						AccountingAnalyticalStatement total = new  AccountingAnalyticalStatement()
								.setAccount(new AccountOperatingAccount()
										.setType(modifies)
										.setCode(modifies.toString())
										.setDescription(modifies.getDescription()))
								.setDebit((aas.isCalculated() || column.isTotalColumn())?aas.getDebit():aas.getPercentDebit())
								.setCredit((aas.isCalculated() || column.isTotalColumn())?aas.getCredit():aas.getPercentCredit())
								.setPercent(-1);
						put(column, total);
					}
				}
			}
		}
	}
}
