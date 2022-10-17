package com.esferalia.aon.occam.api.model.finance.nordigen;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItBank;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItBankAccount;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckitUnlinkedBankAccount;

public class NordigenConfiguration implements Serializable {

	private static final long serialVersionUID = -8823272010569243856L;
	
	private AonConfiguration configuration;
	private LinkedList<CheckItBankAccount> cheItBanks;
	private List<CheckitUnlinkedBankAccount> checkItUnlinkedBanks;
	private List<CheckItBank> bankIds;

	public AonConfiguration getConfiguration() {
		return configuration;
	}
	public NordigenConfiguration setConfiguration(AonConfiguration configuration) {
		this.configuration = configuration;
		return this;
	}
	
	public LinkedList<CheckItBankAccount> getCheItBanks() {
		return cheItBanks;
	}
	public NordigenConfiguration setCheItBanks(LinkedList<CheckItBankAccount> cheItBanks) {
		this.cheItBanks = cheItBanks;
		return this;
	}

	public List<CheckitUnlinkedBankAccount> getCheckItUnlinkedBanks() {
		return checkItUnlinkedBanks;
	}
	public NordigenConfiguration setCheckItUnlinkedBanks(List<CheckitUnlinkedBankAccount> checkItUnlinkedBanks) {
		this.checkItUnlinkedBanks = checkItUnlinkedBanks;
		return this;
	}
	public List<CheckItBank> getBankIds() {
		return bankIds;
	}
	public NordigenConfiguration setBankIds(List<CheckItBank> bankIds) {
		this.bankIds = bankIds;
		return this;
	}
}
