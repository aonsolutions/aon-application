package com.code.aon.account.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.account.Account;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IAccountAlias {



	/** 
	* DAOConstantsEntry for Account entity.
	*/ 
	DAOConstantsEntry ACCOUNT_ENTRY = DAOConstants.getDAOConstant(Account.class);

	/** 
	* Alias value: Account_alias
	* Hibernate value: Account.alias
	*/
	String  ACCOUNT_ALIAS = ACCOUNT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Account_description
	* Hibernate value: Account.description
	*/
	String  ACCOUNT_DESCRIPTION = ACCOUNT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Account_entryEnabled
	* Hibernate value: Account.entryEnabled
	*/
	String  ACCOUNT_ENTRY_ENABLED = ACCOUNT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Account_id
	* Hibernate value: Account.id
	*/
	String  ACCOUNT_ID = ACCOUNT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Account_level
	* Hibernate value: Account.level
	*/
	String  ACCOUNT_LEVEL = ACCOUNT_ENTRY.getAliasNames()[4];


}