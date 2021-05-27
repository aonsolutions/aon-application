package com.esferalia.aon.occam.api.model.accounting.analytical;

import java.io.Serializable;
import java.util.HashMap;

public class AnalyticalCostCenter implements Serializable {
	
	private static final long serialVersionUID = 4411510092968918129L;
	
	private String name;
	private double percent;
	private boolean main;
	private HashMap<String, AnalyticalAccount> accounts;
	
	public String getName() {
		return name;
	}
	public AnalyticalCostCenter setName(String name) {
		this.name = name;
		return this;
	}
	public double getPercent() {
		return percent;
	}
	public AnalyticalCostCenter setPercent(double percent) {
		this.percent = percent;
		return this;
	}
	public boolean isMain() {
		return main;
	}
	public AnalyticalCostCenter setMain(boolean main) {
		this.main = main;
		return this;
	}
	public HashMap<String, AnalyticalAccount> getAccounts() {
		if (accounts == null) {
			setAccounts(new HashMap<String, AnalyticalAccount>());
		}
		return accounts;
	}
	public AnalyticalCostCenter setAccounts(HashMap<String, AnalyticalAccount> accounts) {
		this.accounts = accounts;
		return this;
	}
	public void addAccount(AnalyticalAccount account) {
		getAccounts().put(account.getCode(), account);	
	}
	
	

}
