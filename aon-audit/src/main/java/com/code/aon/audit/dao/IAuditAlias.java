package com.code.aon.audit.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.audit.Domain;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IAuditAlias {



	/** 
	* DAOConstantsEntry for Domain entity.
	*/ 
	DAOConstantsEntry DOMAIN_ENTRY = DAOConstants.getDAOConstant(Domain.class);

	/** 
	* Alias value: Domain_id
	* Hibernate value: Domain.id
	*/
	String  DOMAIN_ID = DOMAIN_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Domain_name
	* Hibernate value: Domain.name
	*/
	String  DOMAIN_NAME = DOMAIN_ENTRY.getAliasNames()[1];


}