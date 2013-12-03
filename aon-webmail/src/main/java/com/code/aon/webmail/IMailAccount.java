package com.code.aon.webmail;

import com.code.aon.common.ITransferObject;
import com.code.aon.webmail.enumeration.ConnectionSecurity;



public interface IMailAccount extends ITransferObject {

	String getName();
	
	String getEmail();
	
	String getDisplayName();
	
	String getReplyToMail();
	
	String getMailUsername();
	
	String getPasswordString();
	
	String getProtocol();
	
	String getIncomingHost();

	int getIncomingPort();

	ConnectionSecurity getIncomingSecurity();
	
	String getOutgoingHost();

	int getOutgoingPort();

	ConnectionSecurity getOutgoingSecurity();

	boolean isOutgoingVerification();
	
	String getDraftFolder();

	String getSentFolder();

	String getSpamFolder();

	String getTrashFolder();
	
	boolean isDefaultAccount();
	
	void setDefaultAccount(boolean defaultAccount);

	boolean isEnterpriseAccount();
	
	ISignature getISignature();

	void setISignature(ISignature signature);
	
	boolean isIMAP();
	
}