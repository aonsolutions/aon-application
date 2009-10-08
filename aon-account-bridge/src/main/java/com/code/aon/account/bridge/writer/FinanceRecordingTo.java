package com.code.aon.account.bridge.writer;

import java.util.Date;
import java.util.List;

import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.registry.RegistryBank;

public class FinanceRecordingTo implements ITransferObject {

	private static final long serialVersionUID = -7892480587508670847L;

	private AccountEntryType type;
	
	private Date date;
	
	private RegistryBank registryBank;
	
	private SecurityLevel securityLevel;
	
	private List<Finance> financeList;

	private List<FinanceBatchDetail> fbatchDetailList;

	public AccountEntryType getType() {
		return type;
	}

	public void setType(AccountEntryType type) {
		this.type = type;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public RegistryBank getRegistryBank() {
		return registryBank;
	}

	public void setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}

	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}

	public List<Finance> getFinanceList() {
		return financeList;
	}

	public void setFinanceList(List<Finance> financeList) {
		this.financeList = financeList;
	}

	public List<FinanceBatchDetail> getFBatchDetailList() {
		return fbatchDetailList;
	}

	public void setFBatchDetailList(List<FinanceBatchDetail> fbatchDetailList) {
		this.fbatchDetailList = fbatchDetailList;
	}

}