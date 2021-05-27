package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;

import com.esferalia.aon.occam.api.model.accounting.analytical.Analytical;
import com.esferalia.aon.occam.api.model.accounting.analytical.AnalyticalAccount;
import com.esferalia.aon.occam.api.model.accounting.analytical.AnalyticalCostCenter;
import com.esferalia.aon.watson.util.AonMathUtils;

public class AccountingAnalyticalReport implements Serializable {

	private static final long serialVersionUID = -3452585307541273807L;
	private static final Logger LOGGER = Logger.getLogger(AccountingAnalyticalReport.class.getName());  
	
	private AccountingReportParams params;
	private Analytical analytical;
	private TreeSet<AccountOperatingAccount> accounts = new TreeSet<AccountOperatingAccount>();
	private LinkedHashSet<AccountingAnalyticalColumn> columns = new LinkedHashSet<AccountingAnalyticalColumn>();
	private TreeMap<String, TreeMap<AccountingAnalyticalColumn, AccountingAnalyticalStatement>> map = 
			new TreeMap<String, TreeMap<AccountingAnalyticalColumn, AccountingAnalyticalStatement>>();
	
	public AccountingAnalyticalReport() {
		super();
	}
	
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
	
	public LinkedHashSet<AccountingAnalyticalColumn> getColumns() {
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
	public AccountingAnalyticalStatement get(String accountCode, String columnName) {
		for ( AccountingAnalyticalColumn column : getColumns()) {
			if ( columnName != null && columnName.equals(column.getName())) {
				return get(accountCode,column); 
			}
		}
		return null;
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
			map.get(code).put(column, aas.duplicate());
		} else {
			exist.setDebit( AonMathUtils.round(exist.getDebit() + aas.getDebit() ));
			exist.setCredit( AonMathUtils.round(exist.getCredit() + aas.getCredit() ));
		}
	}
	
	public boolean isEmpty() {
		return getAccounts().size() == 0;
	}
	
	public void updatePercent(AccountingAnalyticalColumn column, AccountOperatingAccount account, Double value) {
		if (account == null) {
			column.setPercent(value);
			getAnalytical().getCostCenters().get(column.getName()).setPercent(value);
			double sum = 0.0;
			for ( AnalyticalCostCenter ccc : getAnalytical().getCostCenters().values() ) {
				sum = AonMathUtils.round(sum + (ccc.isMain()?0.0:ccc.getPercent()));
			}
			sum = AonMathUtils.round( 100 - sum );
			AnalyticalCostCenter defAnalyticalCostCenter = getAnalytical().getCostCenters().get( getAnalytical().getDefaultCostCenter() );
			defAnalyticalCostCenter.setPercent(sum);
		} else {
			get(account.getCode(), column).setPercent(value);
			ensureConfig(account.getCode(), column.getName()).setPercent(value);
			double sum = 0.0;
			for ( AccountingAnalyticalColumn col : getColumns() ) {
				if (!col.isTotalColumn()) {
					AccountingAnalyticalStatement aas = get(account.getCode(), col);
					LOGGER.info (aas==null?"NULL":"NOT NULL");
					sum = AonMathUtils.round(sum + (col.isMain()?0.0:aas.getPercent()));
					LOGGER.info( "["+account.getCode()+"],["+col.getName()+"] ---> " + sum + " (" + (col.isMain()?0.0:aas.getPercent()) + ")");
					ensureConfig(account.getCode(), col.getName()).setPercent(aas.getPercent());
				}
			}
			sum = AonMathUtils.round( 100 - sum );
			AccountingAnalyticalStatement defAas =  get(account.getCode(), getAnalytical().getDefaultCostCenter());
			defAas.setPercent(sum);
			ensureConfig(account.getCode(), getAnalytical().getDefaultCostCenter()).setPercent(sum);
		}
	}

	private AnalyticalAccount ensureConfig(String account, String column) {
		AnalyticalAccount acc = getAnalytical().getCostCenters().get(column).getAccounts().get(account);
		if (acc == null) {
			acc = new AnalyticalAccount().setCode(account);
			getAnalytical().getCostCenters().get(column).getAccounts().put(account, acc);	
		}
		return acc;
	}
	
}
