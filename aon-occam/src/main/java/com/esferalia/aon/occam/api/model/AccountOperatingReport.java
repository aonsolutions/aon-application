package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.TreeMap;
import java.util.TreeSet;

import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountOperatingReport implements Serializable {

	private static final long serialVersionUID = -3255950085921957296L;

	public static enum AccountOperatingStatementType implements Serializable {
//		+ IMPORTE NETO CIFRA DE NEGOCIOS
//			700		701		702		703		704
//			705		706		708		709
		SALES (false,"IMPORTE NETO CIFRA DE NEGOCIOS") { 
			@Override
			public boolean accept(String code) {
				return AonStringUtils.startsWith(code, "700")
					|| AonStringUtils.startsWith(code, "701")
					|| AonStringUtils.startsWith(code, "702")
					|| AonStringUtils.startsWith(code, "703")
					|| AonStringUtils.startsWith(code, "704")
					|| AonStringUtils.startsWith(code, "705")
					|| AonStringUtils.startsWith(code, "706")
					|| AonStringUtils.startsWith(code, "708")
					|| AonStringUtils.startsWith(code, "709")
					;
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
//		+/- VARIACIÓN DE EXISTENCIAS PT
//			71
		,STOCK (false,"VARIACI\u00D3N DE EXISTENCIAS") {
			@Override
			public boolean accept(String code) {
				return AonStringUtils.startsWith(code, "71");
			}
			@Override
			public AccountOperatingStatementType modifies() {
				return STOCK_TOTAL;
			}
		}
		,STOCK_TOTAL (true,"Total VARIACI\u00D3N DE EXISTENCIAS") {
			@Override
			public AccountOperatingStatementType modifies() {
				return GROSS_MARGIN;
			}
		}
//		+ TRABAJOS REALIZADOS POR LA EMPRESA PARA SU ACTIVO
//			73
		,WORK (false,"TRABAJOS REALIZADOS POR LA EMPRESA PARA SU ACTIVO"){
			@Override
			public boolean accept(String code) {
				return AonStringUtils.startsWith(code, "73");
			}
			@Override
			public AccountOperatingStatementType modifies() {
				return WORK_TOTAL;
			}
		}
		,WORK_TOTAL (true,"Total TRABAJOS REALIZADOS POR LA EMPRESA PARA SU ACTIVO") {
			@Override
			public AccountOperatingStatementType modifies() {
				return GROSS_MARGIN;
			}
		}
//		- APROVISIONAMIENTOS
//			600		601		602		606		607
//			608		609		61
		,PURCHASES (false,"APROVISIONAMIENTOS"){
			@Override
			public boolean accept(String code) {
				return AonStringUtils.startsWith(code, "600")
					|| AonStringUtils.startsWith(code, "601")
					|| AonStringUtils.startsWith(code, "602")
					|| AonStringUtils.startsWith(code, "606")
					|| AonStringUtils.startsWith(code, "607")
					|| AonStringUtils.startsWith(code, "608")
					|| AonStringUtils.startsWith(code, "609")
					|| AonStringUtils.startsWith(code, "61");
			}
			@Override
			public AccountOperatingStatementType modifies() {
				return PURCHASES_TOTAL;
			}
		}
		,PURCHASES_TOTAL (true,"Total APROVISIONAMIENTOS"){
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
//		+ OTROS INGRESOS DE EXPLOTACIÓN
//			75		740		747
		,OTHER_INCOMES (false, "OTROS INGRESOS DE EXPLOTACI\u00D3N") {
			@Override
			public boolean accept(String code) {
				return AonStringUtils.startsWith(code, "75")
					|| AonStringUtils.startsWith(code, "740")
					|| AonStringUtils.startsWith(code, "747")
					;
			}
			@Override
			public AccountOperatingStatementType modifies() {
				return OTHER_INCOMES_TOTAL;
			}
		}
		,OTHER_INCOMES_TOTAL (true,"Total OTROS INGRESOS DE EXPLOTACI\u00D3N"){
			@Override
			public AccountOperatingStatementType modifies() {
				return EBITDA;
			}
		}
//		- GASTOS DE PERSONAL
//			64
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
//		- GASTOS FUNCIONAMIENTO
//		62		65
		,OPERATING_EXPENSES (false,"Total GASTOS FUNCIONAMIENTO"){
			@Override
			public boolean accept(String code) {
				return AonStringUtils.startsWith(code, "62")
					|| AonStringUtils.startsWith(code, "65");
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
		
//		+/- INGRESOS Y GASTOS EXCEPCIONALES
//			746		778		678		77		67		768		668
		,EXTRA_INCOME_EXPENSES (false,"INGRESOS Y GASTOS EXCEPCIONALES") {
			@Override
			public boolean accept(String code) {
				return AonStringUtils.startsWith(code, "746")
					|| AonStringUtils.startsWith(code, "778")
					|| AonStringUtils.startsWith(code, "678")
					|| AonStringUtils.startsWith(code, "77")
					|| AonStringUtils.startsWith(code, "67")
					|| AonStringUtils.startsWith(code, "768")
					|| AonStringUtils.startsWith(code, "668")
					;
			}
			@Override
			public AccountOperatingStatementType modifies() {
				return EXTRA_INCOME_EXPENSES_TOTAL;
			}
		}
		,EXTRA_INCOME_EXPENSES_TOTAL (true, "Total INGRESOS Y GASTOS EXCEPCIONALES"){
			@Override
			public AccountOperatingStatementType modifies() {
				return RESULT;
			}
		}
//		+/- INGRESOS Y GASTOS FINANCIEROS
//			760		761		762		767		769
//			660		661		662		664		665		669
		,FINANCIAL_INCOME_EXPENSES (false,"INGRESOS Y GASTOS FINANCIEROS") {
			@Override
			public boolean accept(String code) {
				return AonStringUtils.startsWith(code, "760")
					|| AonStringUtils.startsWith(code, "761")
					|| AonStringUtils.startsWith(code, "762")
					|| AonStringUtils.startsWith(code, "767")
					|| AonStringUtils.startsWith(code, "769")
					|| AonStringUtils.startsWith(code, "660")
					|| AonStringUtils.startsWith(code, "661")
					|| AonStringUtils.startsWith(code, "662")
					|| AonStringUtils.startsWith(code, "664")
					|| AonStringUtils.startsWith(code, "665")
					|| AonStringUtils.startsWith(code, "669")
					;
			}
			@Override
			public AccountOperatingStatementType modifies() {
				return FINANCIAL_INCOME_EXPENSES_TOTAL;
			}
		}
		,FINANCIAL_INCOME_EXPENSES_TOTAL (true, "Total INGRESOS Y GASTOS FINANCIEROS"){
			@Override
			public AccountOperatingStatementType modifies() {
				return RESULT;
			}
		}
//		- IMPUESTOS
//			633		638		6300		6311		631
//			634		636		639		
		,TAXES (false,"IMPUESTOS") {
			@Override
			public boolean accept(String code) {
				return AonStringUtils.startsWith(code, "633")
					|| AonStringUtils.startsWith(code, "638")
					|| AonStringUtils.startsWith(code, "6300")
					|| AonStringUtils.startsWith(code, "6301")
					|| AonStringUtils.startsWith(code, "6311")
					|| AonStringUtils.startsWith(code, "631")
					|| AonStringUtils.startsWith(code, "634")
					|| AonStringUtils.startsWith(code, "636")
					|| AonStringUtils.startsWith(code, "639")
					;
			}
			@Override
			public AccountOperatingStatementType modifies() {
				return TAXES_TOTAL;
			}
		}
		,TAXES_TOTAL (true, "Total IMPUESTOS"){
			@Override
			public AccountOperatingStatementType modifies() {
				return RESULT;
			}
		}
//		- AMORTIZACIÓN
//		68
		,AMORTIZATION (false,"AMORTIZACI\u00D3N") {
			@Override
			public boolean accept(String code) {
				return AonStringUtils.startsWith(code, "68")
				;
			}
			@Override
			public AccountOperatingStatementType modifies() {
				return AMORTIZATION_TOTAL;
			}
		}
		,AMORTIZATION_TOTAL (true, "Total AMORTIZACI\u00D3N"){
			@Override
			public AccountOperatingStatementType modifies() {
				return RESULT;
			}
		}
//		+/- PROVISIONES
//			7950		7957		6930		7930		6931		6932		6933
//			7931		7932		7933		694		695		794		7954
//			7951		7952		7955		7956		690		691		692
//			790		791		792		663		763		666		667		673		675		696
//			697		698		699		766		773		775		796		797		798		799
		,PROVISION (false,"PROVISIONES") {
			@Override
			public boolean accept(String code) {
				return AonStringUtils.startsWith(code, "7950")
					|| AonStringUtils.startsWith(code, "7957")
					|| AonStringUtils.startsWith(code, "6930")
					|| AonStringUtils.startsWith(code, "7930")
					|| AonStringUtils.startsWith(code, "6931")
					|| AonStringUtils.startsWith(code, "6932")
					|| AonStringUtils.startsWith(code, "6933")
					|| AonStringUtils.startsWith(code, "7931")
					|| AonStringUtils.startsWith(code, "7932")
					|| AonStringUtils.startsWith(code, "7933")
					|| AonStringUtils.startsWith(code, "694")
					|| AonStringUtils.startsWith(code, "695")
					|| AonStringUtils.startsWith(code, "794")
					|| AonStringUtils.startsWith(code, "7954")
					|| AonStringUtils.startsWith(code, "7951")
					|| AonStringUtils.startsWith(code, "7952")
					|| AonStringUtils.startsWith(code, "7955")
					|| AonStringUtils.startsWith(code, "7956")
					|| AonStringUtils.startsWith(code, "690")
					|| AonStringUtils.startsWith(code, "691")
					|| AonStringUtils.startsWith(code, "692")
					|| AonStringUtils.startsWith(code, "790")
					|| AonStringUtils.startsWith(code, "791")
					|| AonStringUtils.startsWith(code, "792")
					|| AonStringUtils.startsWith(code, "663")
					|| AonStringUtils.startsWith(code, "763")
					|| AonStringUtils.startsWith(code, "666")
					|| AonStringUtils.startsWith(code, "667")
					|| AonStringUtils.startsWith(code, "673")
					|| AonStringUtils.startsWith(code, "675")
					|| AonStringUtils.startsWith(code, "696")
					|| AonStringUtils.startsWith(code, "697")
					|| AonStringUtils.startsWith(code, "698")
					|| AonStringUtils.startsWith(code, "699")
					|| AonStringUtils.startsWith(code, "766")
					|| AonStringUtils.startsWith(code, "773")
					|| AonStringUtils.startsWith(code, "775")
					|| AonStringUtils.startsWith(code, "796")
					|| AonStringUtils.startsWith(code, "797")
					|| AonStringUtils.startsWith(code, "798")
					|| AonStringUtils.startsWith(code, "799")
					;
			}
			@Override
			public AccountOperatingStatementType modifies() {
				return PROVISION_TOTAL;
			}
		}
		,PROVISION_TOTAL (true, "Total PROVISIONES"){
			@Override
			public AccountOperatingStatementType modifies() {
				return RESULT;
			}
		}
		
		,OTHER (false,"OTROS CONCEPTOS") {
			@Override
			public boolean accept(String code) {
				return !SALES.accept(code) 
					&& !STOCK.accept(code)
					&& !WORK.accept(code)
					&& !PURCHASES.accept(code)
					&& !OTHER_INCOMES.accept(code)
					&& !SALARIES.accept(code)
					&& !OPERATING_EXPENSES.accept(code)
					&& !EXTRA_INCOME_EXPENSES.accept(code)
					&& !FINANCIAL_INCOME_EXPENSES.accept(code)
					&& !TAXES.accept(code)
					&& !PROVISION.accept(code)
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
		private String month;
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
					.setMonth(month)
					;
		}

		public AccountOperatingAccount getAccount() {
			return account;
		}
		public AccountOperatingStatement setAccount(AccountOperatingAccount account) {
			this.account = account;
			return this;
		}
		public String getMonth() {
			return month;
		}
		public AccountOperatingStatement setMonth(String month) {
			this.month = month;
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

	private AccountingReportParams params;
	private AccountPeriod selectedPeriod;
	private EnterpriseActivity selectedActivity;
	private TreeSet<AccountOperatingAccount> accounts = new TreeSet<AccountOperatingAccount>();
	private TreeSet<DateInterval> intervals = new TreeSet<DateInterval>();
	private TreeMap<String, TreeMap<DateInterval, AccountOperatingStatement>> map = 
			new TreeMap<String, TreeMap<DateInterval, AccountOperatingStatement>>();

	public AccountingReportParams getParams() {
		return params;
	}
	public AccountOperatingReport setParams(AccountingReportParams params) {
		this.params = params;
		return this;
	}
	
	public AccountPeriod getSelectedPeriod() {
		return selectedPeriod;
	}
	public AccountOperatingReport setSelectedPeriod(AccountPeriod period) {
		this.selectedPeriod = period;
		return this;
	}
	
	public EnterpriseActivity getSelectedActivity() {
		return selectedActivity;
	}
	public AccountOperatingReport setSelectedActivity(EnterpriseActivity selectedActivity) {
		this.selectedActivity = selectedActivity;
		return this;
	}
	
	public void put(DateInterval inter, AccountOperatingStatement aos) {
		String code = ensureAccount(aos);
		ensureInterval( inter);
		AccountOperatingStatement exist = map.get(code).get(inter);
		if ( exist == null) {
			map.get(code).put(inter, aos.clone());
		} else {
			exist.setDebit( AonMathUtils.round(exist.getDebit() + aos.getDebit() ));
			exist.setCredit( AonMathUtils.round(exist.getCredit() + aos.getCredit() ));
		}
		
		AccountOperatingStatementType modifies = aos.getAccount().getType().modifies();
		if (modifies != null) {
			AccountOperatingStatement total = new  AccountOperatingStatement()
				.setAccount(new AccountOperatingAccount()
					.setType(modifies)
					.setCode(modifies.toString())
					.setDescription(modifies.getDescription()))
				.setMonth( aos.getMonth())
				.setDebit(aos.getDebit())
				.setCredit(aos.getCredit());
			put(inter, total);
		}
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

	public TreeSet<AccountOperatingAccount> getAccounts() {
		return accounts;
	}
	public void setAccounts(TreeSet<AccountOperatingAccount> accounts) {
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
