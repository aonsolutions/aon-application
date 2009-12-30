package com.code.aon.webmail.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.Signature;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IWebMailAlias {



	/** 
	* DAOConstantsEntry for MailAccount entity.
	*/ 
	DAOConstantsEntry MAIL_ACCOUNT_ENTRY = DAOConstants.getDAOConstant(MailAccount.class);

	/** 
	* Alias value: MailAccount_blackList
	* Hibernate value: MailAccount.blackList
	*/
	String  MAIL_ACCOUNT_BLACK_LIST = MAIL_ACCOUNT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: MailAccount_email
	* Hibernate value: MailAccount.email
	*/
	String  MAIL_ACCOUNT_EMAIL = MAIL_ACCOUNT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: MailAccount_host
	* Hibernate value: MailAccount.host
	*/
	String  MAIL_ACCOUNT_HOST = MAIL_ACCOUNT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: MailAccount_id
	* Hibernate value: MailAccount.id
	*/
	String  MAIL_ACCOUNT_ID = MAIL_ACCOUNT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: MailAccount_incomingHost
	* Hibernate value: MailAccount.incomingHost
	*/
	String  MAIL_ACCOUNT_INCOMING_HOST = MAIL_ACCOUNT_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: MailAccount_incomingPort
	* Hibernate value: MailAccount.incomingPort
	*/
	String  MAIL_ACCOUNT_INCOMING_PORT = MAIL_ACCOUNT_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: MailAccount_incomingSsl
	* Hibernate value: MailAccount.incomingSsl
	*/
	String  MAIL_ACCOUNT_INCOMING_SSL = MAIL_ACCOUNT_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: MailAccount_mailUsername
	* Hibernate value: MailAccount.mailUsername
	*/
	String  MAIL_ACCOUNT_MAIL_USERNAME = MAIL_ACCOUNT_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: MailAccount_outgoingHost
	* Hibernate value: MailAccount.outgoingHost
	*/
	String  MAIL_ACCOUNT_OUTGOING_HOST = MAIL_ACCOUNT_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: MailAccount_outgoingPort
	* Hibernate value: MailAccount.outgoingPort
	*/
	String  MAIL_ACCOUNT_OUTGOING_PORT = MAIL_ACCOUNT_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: MailAccount_outgoingSsl
	* Hibernate value: MailAccount.outgoingSsl
	*/
	String  MAIL_ACCOUNT_OUTGOING_SSL = MAIL_ACCOUNT_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: MailAccount_outgoingVerification
	* Hibernate value: MailAccount.outgoingVerification
	*/
	String  MAIL_ACCOUNT_OUTGOING_VERIFICATION = MAIL_ACCOUNT_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: MailAccount_password
	* Hibernate value: MailAccount.password
	*/
	String  MAIL_ACCOUNT_PASSWORD = MAIL_ACCOUNT_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: MailAccount_protocol
	* Hibernate value: MailAccount.protocol
	*/
	String  MAIL_ACCOUNT_PROTOCOL = MAIL_ACCOUNT_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: MailAccount_status
	* Hibernate value: MailAccount.status
	*/
	String  MAIL_ACCOUNT_STATUS = MAIL_ACCOUNT_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: MailAccount_user_id
	* Hibernate value: MailAccount.user.id
	*/
	String  MAIL_ACCOUNT_USER_ID = MAIL_ACCOUNT_ENTRY.getAliasNames()[15];



	/** 
	* DAOConstantsEntry for Signature entity.
	*/ 
	DAOConstantsEntry SIGNATURE_ENTRY = DAOConstants.getDAOConstant(Signature.class);

	/** 
	* Alias value: Signature_active
	* Hibernate value: Signature.active
	*/
	String  SIGNATURE_ACTIVE = SIGNATURE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Signature_id
	* Hibernate value: Signature.id
	*/
	String  SIGNATURE_ID = SIGNATURE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Signature_mailAccount_id
	* Hibernate value: Signature.mailAccount.id
	*/
	String  SIGNATURE_MAIL_ACCOUNT_ID = SIGNATURE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Signature_name
	* Hibernate value: Signature.name
	*/
	String  SIGNATURE_NAME = SIGNATURE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Signature_signature
	* Hibernate value: Signature.signature
	*/
	String  SIGNATURE_SIGNATURE = SIGNATURE_ENTRY.getAliasNames()[4];


}