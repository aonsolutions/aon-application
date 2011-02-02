package com.code.aon.account;

import com.code.aon.common.ITransferObject;

public interface IAccount {

	Account getAccount();
	void setAccount(Account account);
	
	ITransferObject getLinkedTo();
	void setLinkedTo(ITransferObject to);
	
	String getAccountDescription();
}
