package com.code.aon.config.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.User;
import com.code.aon.config.Scope;
import com.code.aon.config.UserScope;
import com.code.aon.config.Series;
import com.code.aon.config.WorkGroup;
import com.code.aon.config.UserWorkGroup;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IConfigAlias {



	/** 
	* DAOConstantsEntry for ApplicationParameter entity.
	*/ 
	DAOConstantsEntry APPLICATION_PARAMETER_ENTRY = DAOConstants.getDAOConstant(ApplicationParameter.class);

	/** 
	* Alias value: ApplicationParameter_name
	* Hibernate value: ApplicationParameter.name
	*/
	String  APPLICATION_PARAMETER_NAME = APPLICATION_PARAMETER_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ApplicationParameter_value
	* Hibernate value: ApplicationParameter.value
	*/
	String  APPLICATION_PARAMETER_VALUE = APPLICATION_PARAMETER_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for User entity.
	*/ 
	DAOConstantsEntry USER_ENTRY = DAOConstants.getDAOConstant(User.class);

	/** 
	* Alias value: User_aon_key
	* Hibernate value: User.aon_key
	*/
	String  USER_AON_KEY = USER_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: User_available
	* Hibernate value: User.available
	*/
	String  USER_AVAILABLE = USER_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: User_id
	* Hibernate value: User.id
	*/
	String  USER_ID = USER_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: User_login
	* Hibernate value: User.login
	*/
	String  USER_LOGIN = USER_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: User_name
	* Hibernate value: User.name
	*/
	String  USER_NAME = USER_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: User_status
	* Hibernate value: User.status
	*/
	String  USER_STATUS = USER_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: User_validate
	* Hibernate value: User.validate
	*/
	String  USER_VALIDATE = USER_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for Scope entity.
	*/ 
	DAOConstantsEntry SCOPE_ENTRY = DAOConstants.getDAOConstant(Scope.class);

	/** 
	* Alias value: Scope_description
	* Hibernate value: Scope.description
	*/
	String  SCOPE_DESCRIPTION = SCOPE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Scope_id
	* Hibernate value: Scope.id
	*/
	String  SCOPE_ID = SCOPE_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for UserScope entity.
	*/ 
	DAOConstantsEntry USER_SCOPE_ENTRY = DAOConstants.getDAOConstant(UserScope.class);

	/** 
	* Alias value: UserScope_id
	* Hibernate value: UserScope.id
	*/
	String  USER_SCOPE_ID = USER_SCOPE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: UserScope_scope_id
	* Hibernate value: UserScope.scope.id
	*/
	String  USER_SCOPE_SCOPE_ID = USER_SCOPE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: UserScope_user_id
	* Hibernate value: UserScope.user.id
	*/
	String  USER_SCOPE_USER_ID = USER_SCOPE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: UserScope_scope_description
	* Hibernate value: UserScope.scope.description
	*/
	String  USER_SCOPE_SCOPE_DESCRIPTION = USER_SCOPE_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for Series entity.
	*/ 
	DAOConstantsEntry SERIES_ENTRY = DAOConstants.getDAOConstant(Series.class);

	/** 
	* Alias value: Series_active
	* Hibernate value: Series.active
	*/
	String  SERIES_ACTIVE = SERIES_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Series_description
	* Hibernate value: Series.description
	*/
	String  SERIES_DESCRIPTION = SERIES_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Series_id
	* Hibernate value: Series.id
	*/
	String  SERIES_ID = SERIES_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Series_securityLevel
	* Hibernate value: Series.securityLevel
	*/
	String  SERIES_SECURITY_LEVEL = SERIES_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Series_workPlace_id
	* Hibernate value: Series.workPlace.id
	*/
	String  SERIES_WORK_PLACE_ID = SERIES_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for WorkGroup entity.
	*/ 
	DAOConstantsEntry WORK_GROUP_ENTRY = DAOConstants.getDAOConstant(WorkGroup.class);

	/** 
	* Alias value: WorkGroup_description
	* Hibernate value: WorkGroup.description
	*/
	String  WORK_GROUP_DESCRIPTION = WORK_GROUP_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: WorkGroup_id
	* Hibernate value: WorkGroup.id
	*/
	String  WORK_GROUP_ID = WORK_GROUP_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: WorkGroup_status
	* Hibernate value: WorkGroup.status
	*/
	String  WORK_GROUP_STATUS = WORK_GROUP_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for UserWorkGroup entity.
	*/ 
	DAOConstantsEntry USER_WORK_GROUP_ENTRY = DAOConstants.getDAOConstant(UserWorkGroup.class);

	/** 
	* Alias value: UserWorkGroup_id
	* Hibernate value: UserWorkGroup.id
	*/
	String  USER_WORK_GROUP_ID = USER_WORK_GROUP_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: UserWorkGroup_user_id
	* Hibernate value: UserWorkGroup.user.id
	*/
	String  USER_WORK_GROUP_USER_ID = USER_WORK_GROUP_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: UserWorkGroup_workGroup_id
	* Hibernate value: UserWorkGroup.workGroup.id
	*/
	String  USER_WORK_GROUP_WORK_GROUP_ID = USER_WORK_GROUP_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: UserWorkGroup_user_name
	* Hibernate value: UserWorkGroup.user.name
	*/
	String  USER_WORK_GROUP_USER_NAME = USER_WORK_GROUP_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: UserWorkGroup_workGroup_description
	* Hibernate value: UserWorkGroup.workGroup.description
	*/
	String  USER_WORK_GROUP_WORK_GROUP_DESCRIPTION = USER_WORK_GROUP_ENTRY.getAliasNames()[4];


}