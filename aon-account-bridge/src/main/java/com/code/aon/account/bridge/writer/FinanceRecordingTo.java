package com.code.aon.account.bridge.writer;

import java.util.Date;
import java.util.List;

import com.code.aon.account.enumeration.AccountEntryType;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.finance.Finance;
import com.code.aon.finance.RegistryBank;

public class FinanceRecordingTo implements ITransferObject {

	private RegistryBank registryBank;
	
	private Date date;
	
	private AccountEntryType type;
	
	private SecurityLevel securityLevel;
	
	private List<Finance> financeList;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<Finance> getFinanceList() {
		return financeList;
	}

	public void setFinanceList(List<Finance> financeList) {
		this.financeList = financeList;
	}

	public RegistryBank getRegistryBank() {
		return registryBank;
	}

	public void setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
	}

	public AccountEntryType getType() {
		return type;
	}

	public void setType(AccountEntryType type) {
		this.type = type;
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}

	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}
}