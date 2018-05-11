package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountOperatingReport implements Serializable {

	private static final long serialVersionUID = -3255950085921957296L;

	public static enum AccountOperatingStatementType implements Serializable {
		SALES (false,"INGRESOS") { 
			@Override
			public boolean accept(String code) {
				return AonStringUtils.startsWith(code, "70")
					|| AonStringUtils.startsWith(code, "71")
					|| AonStringUtils.startsWith(code, "72")
					|| AonStringUtils.startsWith(code, "73")
					|| AonStringUtils.startsWith(code, "75")
					|| AonStringUtils.startsWith(code, "76");
			}
			@Override
			public AccountOperatingStatementType modifies() {
				return SALES_TOTAL;
			}
		}
		,SALES_TOTAL (true,"Total INGRESOS") {
			@Override
			public AccountOperatingStatementType modifies() {
				return GROSS_MARGIN;
			}
		}
		,PURCHASES (false,"COMPRAS"){
			@Override
			public boolean accept(String code) {
				return AonStringUtils.startsWith(code, "60")
					|| AonStringUtils.startsWith(code, "61");
			}
			@Override
			public AccountOperatingStatementType modifies() {
				return PURCHASES_TOTAL;
			}
		}
		,PURCHASES_TOTAL (true,"Total COMPRAS"){
			@Override
			public AccountOperatingStatementType modifies() {
				return GROSS_MARGIN;
			}
		}
		,GROSS_MARGIN (true,"MARGEN BRUTO "){
			@Override
			public AccountOperatingStatementType modifies() {
				return EBITDA;
			}
		}
		,OPERATING_EXPENSES (false,"Total GASTOS FUNCIONAMIENTO"){
			@Override
			public boolean accept(String code) {
				return AonStringUtils.startsWith(code, "62")
					|| AonStringUtils.startsWith(code, "65")
					|| AonStringUtils.startsWith(code, "66");
			}
			@Override
			public AccountOperatingStatementType modifies() {
				return OPERATING_EXPENSES_TOTAL;
			}
		}
		,OPERATING_EXPENSES_TOTAL (true,"Total GASTOS FUNCIONAMIENTO"){
			@Override
			public AccountOperatingStatementType modifies() {
				return EXPENSES_TOTAL;
			}
		}
		
		,SALARIES (false,"Total GASTOS DE PERSONAL"){
			@Override
			public boolean accept(String code) {
				return AonStringUtils.startsWith(code, "64");
			}
			@Override
			public AccountOperatingStatementType modifies() {
				return SALARIES_TOTAL;
			}
		}
		,SALARIES_TOTAL (true,"Total GASTOS DE PERSONAL"){
			@Override
			public AccountOperatingStatementType modifies() {
				return EXPENSES_TOTAL;
			}
		}
		,EXPENSES_TOTAL (true, "Total GASTOS"){
			@Override
			public AccountOperatingStatementType modifies() {
				return EBITDA;
			}
		}
		,EBITDA (true,"EBITDA") {
			@Override
			public AccountOperatingStatementType modifies() {
				return RESULT;
			}
		}
		,OTHER (false,"OTROS CONCEPTOS") {
			@Override
			public boolean accept(String code) {
				return !SALES_TOTAL.accept(code) 
					&& !PURCHASES.accept(code) 
					&& !OPERATING_EXPENSES.accept(code)
					&& !SALARIES.accept(code)
					&& AonStringUtils.isNumeric(code)
				;
			}
			@Override
			public AccountOperatingStatementType modifies() {
				return OTHER_TOTAL;
			}
		}
		,OTHER_TOTAL (true,"Total OTROS CONCEPTOS") {
			@Override
			public AccountOperatingStatementType modifies() {
				return RESULT;
			}
		}
		,RESULT (true,"RESULTADO")
		;
		private boolean calculated;
		private String description;
		
		private AccountOperatingStatementType(boolean calculated,String description) {
			this.calculated = calculated;
			this.description = description;
		}
		public String getDescription() {
			return description;
		}
		public boolean isCalculated() {
			return calculated;
		}
		public AccountOperatingStatementType modifies() {
			return null;
		}
		public boolean accept(String code) {
			return false;
		}
		public static AccountOperatingStatementType getType(String code) {
			for (AccountOperatingStatementType type : AccountOperatingStatementType.values()) {
				if (type.accept(code)) return type;
			}
			return OTHER;
		}
	}

	public static class AccountOperatingStatement implements Serializable, Cloneable {

		private static final long serialVersionUID = -1037494768508954210L;
		
		private AccountOperatingAccount account; 
		private double debit;
		private double credit;
		
		private double salesRatio;
		private double purchasesRatio;
		private double expensesRatio;
		private double increasePercent;

		protected AccountOperatingStatement clone() {
			return new AccountOperatingStatement()
					.setAccount( getAccount() )
					.setDebit(debit)
					.setCredit(credit)
					.setSalesRatio(salesRatio)
					.setPurchasesRatio(purchasesRatio)
					.setExpensesRatio(expensesRatio)
					.setIncreasePercent(increasePercent)
					;
		}

		public AccountOperatingAccount getAccount() {
			return account;
		}
		public AccountOperatingStatement setAccount(AccountOperatingAccount account) {
			this.account = account;
			return this;
		}

		public double getDebit() {
			return debit;
		}
		public AccountOperatingStatement setDebit(double debit) {
			this.debit = debit;
			return this;
		}

		public double getCredit() {
			return credit;
		}
		public AccountOperatingStatement setCredit(double credit) {
			this.credit = credit;
			return this;
		}
		
		public double getDebitBalance() {
			double d = AonMathUtils.round( debit - credit);
			return d>0?d:0.0;
		}
		
		public double getUnpaidBalance() {
			double d = AonMathUtils.round( credit - debit);
			return d>0?d:0.0;
		}
		public double getSalesRatio() {
			return salesRatio;
		}
		public AccountOperatingStatement setSalesRatio(double salesRatio) {
			this.salesRatio = salesRatio;
			return this;
		}
		public double getPurchasesRatio() {
			return purchasesRatio;
		}
		public AccountOperatingStatement setPurchasesRatio(double purchasesRatio) {
			this.purchasesRatio = purchasesRatio;
			return this;
		}
		public double getExpensesRatio() {
			return expensesRatio;
		}
		public AccountOperatingStatement setExpensesRatio(double expensesRatio) {
			this.expensesRatio = expensesRatio;
			return this;
		}
		public double getIncreasePercent() {
			return increasePercent;
		}
		public AccountOperatingStatement setIncreasePercent(double increasePercent) {
			this.increasePercent = increasePercent;
			return this;
		}
	}

	private AccountOperatingParams params;
	
	private LinkedHashSet<AccountOperatingAccount> accounts = new LinkedHashSet<AccountOperatingAccount>();
	
	private TreeSet<DateInterval> intervals = new TreeSet<DateInterval>();
	
	private TreeMap<String, TreeMap<DateInterval, AccountOperatingStatement>> map = 
			new TreeMap<String, TreeMap<DateInterval, AccountOperatingStatement>>();

	public AccountOperatingParams getParams() {
		return params;
	}
	public AccountOperatingReport setParams(AccountOperatingParams params) {
		this.params = params;
		
		return this;
	}
	
	public void put(DateInterval inter, AccountOperatingStatement aos) {
		String code = ensureAccount(aos);
		ensureInterval( inter);
		map.get(code).put(inter, aos);
	}
	private void ensureInterval(DateInterval inter) {
		if (inter != null && !intervals.contains(inter)) {
			intervals.add(inter);
		}
	}
	public void put(AccountOperatingStatement aos) {
		String code = ensureAccount( aos );
		for (DateInterval interval : intervals) {
			ensureInterval( interval);
			map.get(code).put(interval, aos.clone());	
		}
	}

	private String ensureAccount(AccountOperatingStatement aos) {
		AccountOperatingAccount account = aos.getAccount();
		if (!accounts.contains(account)) {
			accounts.add(account);
		}
		if (!map.containsKey(account.getCode())) {
			map.put(account.getCode(), new TreeMap<DateInterval, AccountOperatingStatement>());
		}
		return account.getCode();
	}
	
	public AccountOperatingStatement get(String accountCode, DateInterval inter) {
		if (map.containsKey(accountCode)) {
			return map.get(accountCode).get(inter);
		}
		return null;
	}

	public LinkedHashSet<AccountOperatingAccount> getAccounts() {
		return accounts;
	}
	public void setAccounts(LinkedHashSet<AccountOperatingAccount> accounts) {
		this.accounts = accounts;
	}
	
	public TreeSet<DateInterval> getIntervals() {
		return intervals;
	}
	public boolean showRatios() {
		return getParams() != null && getParams().isPercentsEnabled() && getParams().showRatios();
	}
	public boolean showIncreasePercent() {
		return getParams() != null && getParams().isPercentsEnabled() && getParams().showIncreasePercent();
	}
	public boolean isEmpty() {
		return getAccounts().size() == 0;
	}
	
}
