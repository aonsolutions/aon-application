package com.code.aon.account.bridge.writer;

import java.util.HashMap;
import java.util.Map;

import com.code.aon.account.Account;
import com.code.aon.common.ITransferObject;

public class TaxRecordingTo implements ITransferObject {

	private static final long serialVersionUID = -7892480587508670847L;

	private Map<Account, Double> taxQuotaAccountMap;

	public Map<Account, Double> getTaxQuotaAccountMap() {
		return taxQuotaAccountMap;
	}

	public void setTaxQuotaAccountMap(Map<Account, Double> taxQuotaAccountMap) {
		this.taxQuotaAccountMap = taxQuotaAccountMap;
	}

	public void addTaxQuotaAccount(Account account, double taxQuota) {
		if (taxQuotaAccountMap == null) {
			taxQuotaAccountMap = new HashMap<Account, Double>();
		}
		double totalQuota = taxQuota + ((taxQuotaAccountMap.containsKey(account)) ? taxQuotaAccountMap.get(account).doubleValue() : 0);
		taxQuotaAccountMap.put(account, new Double(totalQuota));
	}

}