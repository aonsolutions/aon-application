package com.code.aon.project.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.project.ActivityType;
import com.code.aon.project.Project;
import com.code.aon.project.ProjectActivity;
import com.code.aon.project.ProjectType;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IProjectAlias {



	/** 
	* DAOConstantsEntry for ActivityType entity.
	*/ 
	DAOConstantsEntry ACTIVITY_TYPE_ENTRY = DAOConstants.getDAOConstant(ActivityType.class);

	/** 
	* Alias value: ActivityType_id
	* Hibernate value: ActivityType.id
	*/
	String  ACTIVITY_TYPE_ID = ACTIVITY_TYPE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ActivityType_description
	* Hibernate value: ActivityType.description
	*/
	String  ACTIVITY_TYPE_DESCRIPTION = ACTIVITY_TYPE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ActivityType_projectType_id
	* Hibernate value: ActivityType.projectType<id
	*/
	String  ACTIVITY_TYPE_PROJECT_TYPE_ID = ACTIVITY_TYPE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ActivityType_active
	* Hibernate value: ActivityType.active
	*/
	String  ACTIVITY_TYPE_ACTIVE = ACTIVITY_TYPE_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for Project entity.
	*/ 
	DAOConstantsEntry PROJECT_ENTRY = DAOConstants.getDAOConstant(Project.class);

	/** 
	* Alias value: Project_active
	* Hibernate value: Project.active
	*/
	String  PROJECT_ACTIVE = PROJECT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Project_alias
	* Hibernate value: Project.alias
	*/
	String  PROJECT_ALIAS = PROJECT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Project_commercial
	* Hibernate value: Project.commercial
	*/
	String  PROJECT_COMMERCIAL = PROJECT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Project_date
	* Hibernate value: Project.date
	*/
	String  PROJECT_DATE = PROJECT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Project_dossier
	* Hibernate value: Project.dossier
	*/
	String  PROJECT_DOSSIER = PROJECT_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Project_enterprise_id
	* Hibernate value: Project.enterprise.id
	*/
	String  PROJECT_ENTERPRISE_ID = PROJECT_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Project_id
	* Hibernate value: Project.id
	*/
	String  PROJECT_ID = PROJECT_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Project_name
	* Hibernate value: Project.name
	*/
	String  PROJECT_NAME = PROJECT_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Project_projectType_id
	* Hibernate value: Project.projectType.id
	*/
	String  PROJECT_PROJECT_TYPE_ID = PROJECT_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Project_registry_id
	* Hibernate value: Project.registry.id
	*/
	String  PROJECT_REGISTRY_ID = PROJECT_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Project_tas
	* Hibernate value: Project.tas
	*/
	String  PROJECT_TAS = PROJECT_ENTRY.getAliasNames()[10];



	/** 
	* DAOConstantsEntry for ProjectActivity entity.
	*/ 
	DAOConstantsEntry PROJECT_ACTIVITY_ENTRY = DAOConstants.getDAOConstant(ProjectActivity.class);

	/** 
	* Alias value: ProjectActivity_active
	* Hibernate value: ProjectActivity.active
	*/
	String  PROJECT_ACTIVITY_ACTIVE = PROJECT_ACTIVITY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ProjectActivity_activityType_id
	* Hibernate value: ProjectActivity.activityType.id
	*/
	String  PROJECT_ACTIVITY_ACTIVITY_TYPE_ID = PROJECT_ACTIVITY_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ProjectActivity_id
	* Hibernate value: ProjectActivity.id
	*/
	String  PROJECT_ACTIVITY_ID = PROJECT_ACTIVITY_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ProjectActivity_project_id
	* Hibernate value: ProjectActivity.project.id
	*/
	String  PROJECT_ACTIVITY_PROJECT_ID = PROJECT_ACTIVITY_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for ProjectType entity.
	*/ 
	DAOConstantsEntry PROJECT_TYPE_ENTRY = DAOConstants.getDAOConstant(ProjectType.class);

	/** 
	* Alias value: ProjectType_active
	* Hibernate value: ProjectType.active
	*/
	String  PROJECT_TYPE_ACTIVE = PROJECT_TYPE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ProjectType_description
	* Hibernate value: ProjectType.description
	*/
	String  PROJECT_TYPE_DESCRIPTION = PROJECT_TYPE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ProjectType_enterprise_id
	* Hibernate value: ProjectType.enterprise.id
	*/
	String  PROJECT_TYPE_ENTERPRISE_ID = PROJECT_TYPE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ProjectType_id
	* Hibernate value: ProjectType.id
	*/
	String  PROJECT_TYPE_ID = PROJECT_TYPE_ENTRY.getAliasNames()[3];


}