package com.code.aon.audit.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.audit.Domain;
import com.code.aon.audit.Application;
import com.code.aon.audit.User;
import com.code.aon.audit.Session;
import com.code.aon.audit.Action;
import com.code.aon.audit.ActionExecution;
import com.code.aon.audit.DomainApplication;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IAuditAlias {



	/** 
	* DAOConstantsEntry for Domain entity.
	*/ 
	DAOConstantsEntry DOMAIN_ENTRY = DAOConstants.getDAOConstant(Domain.class);

	/** 
	* Alias value: Domain_enableAudit
	* Hibernate value: Domain.enableAudit
	*/
	String  DOMAIN_ENABLE_AUDIT = DOMAIN_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Domain_id
	* Hibernate value: Domain.id
	*/
	String  DOMAIN_ID = DOMAIN_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Domain_name
	* Hibernate value: Domain.name
	*/
	String  DOMAIN_NAME = DOMAIN_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for Application entity.
	*/ 
	DAOConstantsEntry APPLICATION_ENTRY = DAOConstants.getDAOConstant(Application.class);

	/** 
	* Alias value: Application_id
	* Hibernate value: Application.id
	*/
	String  APPLICATION_ID = APPLICATION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Application_name
	* Hibernate value: Application.name
	*/
	String  APPLICATION_NAME = APPLICATION_ENTRY.getAliasNames()[1];



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
	* DAOConstantsEntry for Session entity.
	*/ 
	DAOConstantsEntry SESSION_ENTRY = DAOConstants.getDAOConstant(Session.class);

	/** 
	* Alias value: Session_application_id
	* Hibernate value: Session.application.id
	*/
	String  SESSION_APPLICATION_ID = SESSION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Session_endDate
	* Hibernate value: Session.endDate
	*/
	String  SESSION_END_DATE = SESSION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Session_id
	* Hibernate value: Session.id
	*/
	String  SESSION_ID = SESSION_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Session_remoteAddress
	* Hibernate value: Session.remoteAddress
	*/
	String  SESSION_REMOTE_ADDRESS = SESSION_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Session_remoteHost
	* Hibernate value: Session.remoteHost
	*/
	String  SESSION_REMOTE_HOST = SESSION_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Session_sessionId
	* Hibernate value: Session.sessionId
	*/
	String  SESSION_SESSION_ID = SESSION_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Session_startDate
	* Hibernate value: Session.startDate
	*/
	String  SESSION_START_DATE = SESSION_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Session_user_id
	* Hibernate value: Session.user.id
	*/
	String  SESSION_USER_ID = SESSION_ENTRY.getAliasNames()[7];



	/** 
	* DAOConstantsEntry for Action entity.
	*/ 
	DAOConstantsEntry ACTION_ENTRY = DAOConstants.getDAOConstant(Action.class);

	/** 
	* Alias value: Action_application_id
	* Hibernate value: Action.application.id
	*/
	String  ACTION_APPLICATION_ID = ACTION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Action_id
	* Hibernate value: Action.id
	*/
	String  ACTION_ID = ACTION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Action_name
	* Hibernate value: Action.name
	*/
	String  ACTION_NAME = ACTION_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for ActionExecution entity.
	*/ 
	DAOConstantsEntry ACTION_EXECUTION_ENTRY = DAOConstants.getDAOConstant(ActionExecution.class);

	/** 
	* Alias value: ActionExecution_action_id
	* Hibernate value: ActionExecution.action.id
	*/
	String  ACTION_EXECUTION_ACTION_ID = ACTION_EXECUTION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ActionExecution_executionDate
	* Hibernate value: ActionExecution.executionDate
	*/
	String  ACTION_EXECUTION_EXECUTION_DATE = ACTION_EXECUTION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ActionExecution_id
	* Hibernate value: ActionExecution.id
	*/
	String  ACTION_EXECUTION_ID = ACTION_EXECUTION_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ActionExecution_session_id
	* Hibernate value: ActionExecution.session.id
	*/
	String  ACTION_EXECUTION_SESSION_ID = ACTION_EXECUTION_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for DomainApplication entity.
	*/ 
	DAOConstantsEntry DOMAIN_APPLICATION_ENTRY = DAOConstants.getDAOConstant(DomainApplication.class);

	/** 
	* Alias value: DomainApplication_application_id
	* Hibernate value: DomainApplication.application.id
	*/
	String  DOMAIN_APPLICATION_APPLICATION_ID = DOMAIN_APPLICATION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: DomainApplication_auditLevel
	* Hibernate value: DomainApplication.auditLevel
	*/
	String  DOMAIN_APPLICATION_AUDIT_LEVEL = DOMAIN_APPLICATION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: DomainApplication_domain_id
	* Hibernate value: DomainApplication.domain.id
	*/
	String  DOMAIN_APPLICATION_DOMAIN_ID = DOMAIN_APPLICATION_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: DomainApplication_id
	* Hibernate value: DomainApplication.id
	*/
	String  DOMAIN_APPLICATION_ID = DOMAIN_APPLICATION_ENTRY.getAliasNames()[3];


}