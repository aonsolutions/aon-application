package com.code.aon.webmail;

import com.code.aon.common.ITransferObject;



public interface IMailAccount extends ITransferObject {

	String DEFAULT_MAIL_ACCOUNT_NAME = "default";
	
	String getHost();
	
	String getName();
	
	String getEmail();
	
	String getDisplayName();
	
	String getReplyToMail();
	
	String getMailUsername();
	
	String getPasswordString();
	
	String getIncomingHost();

	int getIncomingPort();

	boolean isIncomingSsl();
	
	String getOutgoingHost();

	int getOutgoingPort();

	boolean isOutgoingSsl();

	boolean isOutgoingVerification();
	
	String getDraftFolder();

	String getSentFolder();

	String getSpamFolder();

	String getTrashFolder();
	
	boolean isDefault();
	
	boolean isDefaultAccount();
	
	void setDefaultAccount(boolean defaultAccount);

	boolean isEnterpriseAccount();
	
	ISignature getISignature();

	void setISignature(ISignature signature);
	
}