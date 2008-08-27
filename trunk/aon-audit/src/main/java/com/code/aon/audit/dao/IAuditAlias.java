package com.code.aon.audit.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.audit.Domain;
import com.code.aon.audit.Application;
import com.code.aon.audit.User;
import com.code.aon.audit.LoginAudit;

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



	/** 
	* DAOConstantsEntry for Application entity.
	*/ 
	DAOConstantsEntry APPLICATION_ENTRY = DAOConstants.getDAOConstant(Application.class);

	/** 
	* Alias value: Application_domain_id
	* Hibernate value: Application.domain.id
	*/
	String  APPLICATION_DOMAIN_ID = APPLICATION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Application_id
	* Hibernate value: Application.id
	*/
	String  APPLICATION_ID = APPLICATION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Application_name
	* Hibernate value: Application.name
	*/
	String  APPLICATION_NAME = APPLICATION_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for User entity.
	*/ 
	DAOConstantsEntry USER_ENTRY = DAOConstants.getDAOConstant(User.class);

	/** 
	* Alias value: User_domain_id
	* Hibernate value: User.domain.id
	*/
	String  USER_DOMAIN_ID = USER_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: User_id
	* Hibernate value: User.id
	*/
	String  USER_ID = USER_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: User_login
	* Hibernate value: User.login
	*/
	String  USER_LOGIN = USER_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for LoginAudit entity.
	*/ 
	DAOConstantsEntry LOGIN_AUDIT_ENTRY = DAOConstants.getDAOConstant(LoginAudit.class);

	/** 
	* Alias value: LoginAudit_end
	* Hibernate value: LoginAudit.end
	*/
	String  LOGIN_AUDIT_END = LOGIN_AUDIT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: LoginAudit_id
	* Hibernate value: LoginAudit.id
	*/
	String  LOGIN_AUDIT_ID = LOGIN_AUDIT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: LoginAudit_sessionId
	* Hibernate value: LoginAudit.sessionId
	*/
	String  LOGIN_AUDIT_SESSION_ID = LOGIN_AUDIT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: LoginAudit_start
	* Hibernate value: LoginAudit.start
	*/
	String  LOGIN_AUDIT_START = LOGIN_AUDIT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: LoginAudit_user_id
	* Hibernate value: LoginAudit.user.id
	*/
	String  LOGIN_AUDIT_USER_ID = LOGIN_AUDIT_ENTRY.getAliasNames()[4];


}