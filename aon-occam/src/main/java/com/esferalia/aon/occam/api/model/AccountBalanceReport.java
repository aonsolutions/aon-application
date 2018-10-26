package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;

import com.esferalia.aon.occam.api.model.accounting.AccountBalance;

public class AccountBalanceReport implements Serializable{

	private static final long serialVersionUID = 7672716172785126558L;
	
	public static class BalanceLine implements Serializable {
		
		private static final long serialVersionUID = 8836372167741926367L;
		
		private Integer id;
		private int level;
		private boolean leaf;
		private String code;
		private String prefix;
		private String description;
		private String accounts;
		private HashMap<String,Double> amounts = new HashMap<String,Double>();
		
		public Integer getId() {
			return id;
		}
		public BalanceLine setId(Integer id) {
			this.id = id;
			return this;
		}
		
		public int getLevel() {
			return level;
		}
		public BalanceLine setLevel(int level) {
			this.level = level;
			return this;
		}
		
		public boolean isLeaf() {
			return leaf;
		}
		public BalanceLine setLeaf(boolean leaf) {
			this.leaf = leaf;
			return this;
		}
		
		public String getCode() {
			return code;
		}
		public BalanceLine setCode(String code) {
			this.code = code;
			return this;
		}
		
		public String getPrefix() {
			return prefix;
		}
		public BalanceLine setPrefix(String prefix) {
			this.prefix = prefix;
			return this;
		}
		
		public String getDescription() {
			return description;
		}
		public BalanceLine setDescription(String description) {
			this.description = description;
			return this;
		}
		
		public String getAccounts() {
			return accounts;
		}
		public BalanceLine setAccounts(String accounts) {
			this.accounts = accounts;
			return this;
		}

		public Map<String,Double> getAmounts() {
			return amounts;
		}
	}
	private ReportMetadata metadata;	
	private AccountPeriod selectedPeriod;
	private EnterpriseActivity selectedActivity;
	private AccountingReportParams params;
	private Map<String, BalanceLine> balances = new LinkedHashMap<String, BalanceLine>();
	private LinkedHashSet<String> periods = new LinkedHashSet<String>();
	private Map<String, LinkedList<AccountBalance>> unreadAccounts = new LinkedHashMap<String, LinkedList<AccountBalance>>();
	
	
	public ReportMetadata getMetadata() {
		return metadata;
	}
	public AccountBalanceReport setMetadata(ReportMetadata metadata) {
		this.metadata = metadata;
		return this;
	}
	
	public AccountPeriod getSelectedPeriod() {
		return selectedPeriod;
	}
	public AccountBalanceReport setSelectedPeriod(AccountPeriod period) {
		this.selectedPeriod = period;
		return this;
	}
	public EnterpriseActivity getSelectedActivity() {
		return selectedActivity;
	}
	public AccountBalanceReport setSelectedActivity(EnterpriseActivity selectedActivity) {
		this.selectedActivity = selectedActivity;
		return this;
	}
	
	public AccountingReportParams getParams() {
		return params;
	}
	public AccountBalanceReport setParams(AccountingReportParams params) {
		this.params = params;
		return this;
	}
	
	public Map<String, BalanceLine> getBalances() {
		return balances;
	}
//	public void putBalances(String bal,Map<String, BalanceLine> newBalances) {
//		this.balances.put(bal,  newBalances);
//	}
	public boolean isEmpty() {
		return balances == null || balances.size() == 0;
	}
	
	public void setAmount(String key, String bal, double amount) {
		if (!periods.contains(bal)) periods.add(bal);
		balances.get(key).getAmounts().put(bal,amount);
	}
	
	public LinkedHashSet<String> getPeriods() {
		return periods;
	}
	public Map<String, LinkedList<AccountBalance>> getUnreadAccounts() {
		return unreadAccounts;
	}
}
