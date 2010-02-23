package com.code.aon.audit.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.audit.Action;
import com.code.aon.audit.ActionDenied;
import com.code.aon.audit.ActionEntry;
import com.code.aon.audit.ActionFavorite;
import com.code.aon.audit.Application;
import com.code.aon.audit.Session;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IAuditAlias {



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
	* Alias value: Action_menu
	* Hibernate value: Action.menu
	*/
	String  ACTION_MENU = ACTION_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Action_name
	* Hibernate value: Action.name
	*/
	String  ACTION_NAME = ACTION_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for ActionDenied entity.
	*/ 
	DAOConstantsEntry ACTION_DENIED_ENTRY = DAOConstants.getDAOConstant(ActionDenied.class);

	/** 
	* Alias value: ActionDenied_action_id
	* Hibernate value: ActionDenied.action.id
	*/
	String  ACTION_DENIED_ACTION_ID = ACTION_DENIED_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ActionDenied_id
	* Hibernate value: ActionDenied.id
	*/
	String  ACTION_DENIED_ID = ACTION_DENIED_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ActionDenied_user_id
	* Hibernate value: ActionDenied.user.id
	*/
	String  ACTION_DENIED_USER_ID = ACTION_DENIED_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for ActionEntry entity.
	*/ 
	DAOConstantsEntry ACTION_ENTRY_ENTRY = DAOConstants.getDAOConstant(ActionEntry.class);

	/** 
	* Alias value: ActionEntry_action_id
	* Hibernate value: ActionEntry.action.id
	*/
	String  ACTION_ENTRY_ACTION_ID = ACTION_ENTRY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ActionEntry_executionDate
	* Hibernate value: ActionEntry.executionDate
	*/
	String  ACTION_ENTRY_EXECUTION_DATE = ACTION_ENTRY_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ActionEntry_id
	* Hibernate value: ActionEntry.id
	*/
	String  ACTION_ENTRY_ID = ACTION_ENTRY_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ActionEntry_session_id
	* Hibernate value: ActionEntry.session.id
	*/
	String  ACTION_ENTRY_SESSION_ID = ACTION_ENTRY_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for ActionFavorite entity.
	*/ 
	DAOConstantsEntry ACTION_FAVORITE_ENTRY = DAOConstants.getDAOConstant(ActionFavorite.class);

	/** 
	* Alias value: ActionFavorite_action_id
	* Hibernate value: ActionFavorite.action.id
	*/
	String  ACTION_FAVORITE_ACTION_ID = ACTION_FAVORITE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ActionFavorite_id
	* Hibernate value: ActionFavorite.id
	*/
	String  ACTION_FAVORITE_ID = ACTION_FAVORITE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ActionFavorite_position
	* Hibernate value: ActionFavorite.position
	*/
	String  ACTION_FAVORITE_POSITION = ACTION_FAVORITE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ActionFavorite_user_id
	* Hibernate value: ActionFavorite.user.id
	*/
	String  ACTION_FAVORITE_USER_ID = ACTION_FAVORITE_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for Application entity.
	*/ 
	DAOConstantsEntry APPLICATION_ENTRY = DAOConstants.getDAOConstant(Application.class);

	/** 
	* Alias value: Application_auditLevel
	* Hibernate value: Application.auditLevel
	*/
	String  APPLICATION_AUDIT_LEVEL = APPLICATION_ENTRY.getAliasNames()[0];

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


}