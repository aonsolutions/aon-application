package com.esferalia.aon.occam.api.model.finance.checkit;

import java.io.Serializable;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.AonConfiguration;

public class CheckItConfiguration implements Serializable {

	private static final long serialVersionUID = -8823272010569243856L;
	
	private AonConfiguration configuration;
	private Integer enterpriseId;
	private LinkedList<CheckItBankAccount> cheItBanks;

	public AonConfiguration getConfiguration() {
		return configuration;
	}
	public CheckItConfiguration setConfiguration(AonConfiguration configuration) {
		this.configuration = configuration;
		return this;
	}

	public Integer getEnterpriseId() {
		return enterpriseId;
	}
	public CheckItConfiguration setEnterpriseId(Integer enterpriseId) {
		this.enterpriseId = enterpriseId;
		return this;
	}
	
	public LinkedList<CheckItBankAccount> getCheItBanks() {
		return cheItBanks;
	}
	public CheckItConfiguration setCheItBanks(LinkedList<CheckItBankAccount> cheItBanks) {
		this.cheItBanks = cheItBanks;
		return this;
	}

}
