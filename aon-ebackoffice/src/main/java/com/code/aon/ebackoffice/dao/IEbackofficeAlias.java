package com.code.aon.ebackoffice.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.ebackoffice.Ectarget;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IEbackofficeAlias {



	/** 
	* DAOConstantsEntry for Ectarget entity.
	*/ 
	DAOConstantsEntry ECTARGET_ENTRY = DAOConstants.getDAOConstant(Ectarget.class);

	/** 
	* Alias value: Ectarget_id
	* Hibernate value: Ectarget.id
	*/
	String  ECTARGET_ID = ECTARGET_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Ectarget_lastAccess
	* Hibernate value: Ectarget.lastAccess
	*/
	String  ECTARGET_LAST_ACCESS = ECTARGET_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Ectarget_login
	* Hibernate value: Ectarget.login
	*/
	String  ECTARGET_LOGIN = ECTARGET_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Ectarget_password
	* Hibernate value: Ectarget.password
	*/
	String  ECTARGET_PASSWORD = ECTARGET_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Ectarget_target_id
	* Hibernate value: Ectarget.target.id
	*/
	String  ECTARGET_TARGET_ID = ECTARGET_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Ectarget_type
	* Hibernate value: Ectarget.type
	*/
	String  ECTARGET_TYPE = ECTARGET_ENTRY.getAliasNames()[5];


}