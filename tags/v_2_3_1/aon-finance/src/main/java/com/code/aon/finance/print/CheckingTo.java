package com.code.aon.finance.print;

import java.util.List;

import com.code.aon.common.ITransferObject;

public class CheckingTo implements ITransferObject {

	private List<ITransferObject> noPaymethodList;
	
	private List<ITransferObject> negotiableNoBankAccountList;
	
	private List<ITransferObject> noNegotiableBankAccountList;
	

	public List<ITransferObject> getNoPaymethodList() {
		return noPaymethodList;
	}

	public void setNoPaymethodList(List<ITransferObject> noPaymethodList) {
		this.noPaymethodList = noPaymethodList;
	}

	public List<ITransferObject> getNegotiableNoBankAccountList() {
		return negotiableNoBankAccountList;
	}

	public void setNegotiableNoBankAccountList(List<ITransferObject> negotiableNoBankAccountList) {
		this.negotiableNoBankAccountList = negotiableNoBankAccountList;
	}

	public List<ITransferObject> getNoNegotiableBankAccountList() {
		return noNegotiableBankAccountList;
	}

	public void setNoNegotiableBankAccountList(List<ITransferObject> noNegotiableBankAccountList) {
		this.noNegotiableBankAccountList = noNegotiableBankAccountList;
	}
}