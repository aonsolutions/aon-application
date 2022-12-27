package com.esferalia.aon.occam.api.model.finance.checkit;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.AonConfiguration;

public class CheckItConfiguration implements Serializable {

	private static final long serialVersionUID = -8823272010569243856L;
	
	private AonConfiguration configuration;
	private Integer enterpriseId;
	private LinkedList<CheckItBankAccount> cheItBanks;
	private List<CheckitUnlinkedBankAccount> checkItUnlinkedBanks;
	private List<CheckItBank> bankIds;
	private boolean isDown;
	private boolean registrationFailed;

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

	public List<CheckitUnlinkedBankAccount> getCheckItUnlinkedBanks() {
		return checkItUnlinkedBanks;
	}
	
	public CheckItConfiguration setCheckItUnlinkedBanks(List<CheckitUnlinkedBankAccount> checkItUnlinkedBanks) {
		this.checkItUnlinkedBanks = checkItUnlinkedBanks;
		return this;
	}
	public List<CheckItBank> getBankIds() {
		return bankIds;
	}
	public CheckItConfiguration setBankIds(List<CheckItBank> bankIds) {
		this.bankIds = bankIds;
		return this;
	}
	public boolean isDown() {
		return isDown;
	}
	public CheckItConfiguration setDown(boolean isDown) {
		this.isDown = isDown;
		return this;
	}
	public boolean isRegistrationFailed() {
		return registrationFailed;
	}
	public CheckItConfiguration setRegistrationFailed(boolean registrationFailed) {
		this.registrationFailed = registrationFailed;
		return this;

	}
}
