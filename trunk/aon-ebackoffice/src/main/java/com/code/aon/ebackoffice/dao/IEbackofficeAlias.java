package com.code.aon.ebackoffice.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.ebackoffice.EcTarget;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IEbackofficeAlias {



	/** 
	* DAOConstantsEntry for EcTarget entity.
	*/ 
	DAOConstantsEntry EC_TARGET_ENTRY = DAOConstants.getDAOConstant(EcTarget.class);

	/** 
	* Alias value: EcTarget_id
	* Hibernate value: EcTarget.id
	*/
	String  EC_TARGET_ID = EC_TARGET_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: EcTarget_lastAccess
	* Hibernate value: EcTarget.lastAccess
	*/
	String  EC_TARGET_LAST_ACCESS = EC_TARGET_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: EcTarget_login
	* Hibernate value: EcTarget.login
	*/
	String  EC_TARGET_LOGIN = EC_TARGET_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: EcTarget_password
	* Hibernate value: EcTarget.password
	*/
	String  EC_TARGET_PASSWORD = EC_TARGET_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: EcTarget_target_id
	* Hibernate value: EcTarget.target.id
	*/
	String  EC_TARGET_TARGET_ID = EC_TARGET_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: EcTarget_type
	* Hibernate value: EcTarget.type
	*/
	String  EC_TARGET_TYPE = EC_TARGET_ENTRY.getAliasNames()[5];


}