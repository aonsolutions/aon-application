package com.code.aon.tas.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.tas.Make;
import com.code.aon.tas.Model;
import com.code.aon.tas.ProjectTas;
import com.code.aon.tas.TasItem;

/** 
* Interface for holding entity properties constants.
*/ 
public interface ITASAlias {



	/** 
	* DAOConstantsEntry for Make entity.
	*/ 
	DAOConstantsEntry MAKE_ENTRY = DAOConstants.getDAOConstant(Make.class);

	/** 
	* Alias value: Make_id
	* Hibernate value: Make.id
	*/
	String  MAKE_ID = MAKE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Make_name
	* Hibernate value: Make.name
	*/
	String  MAKE_NAME = MAKE_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for Model entity.
	*/ 
	DAOConstantsEntry MODEL_ENTRY = DAOConstants.getDAOConstant(Model.class);

	/** 
	* Alias value: Model_id
	* Hibernate value: Model.id
	*/
	String  MODEL_ID = MODEL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Model_make_id
	* Hibernate value: Model.make.id
	*/
	String  MODEL_MAKE_ID = MODEL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Model_make_name
	* Hibernate value: Model.make.name
	*/
	String  MODEL_MAKE_NAME = MODEL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Model_name
	* Hibernate value: Model.name
	*/
	String  MODEL_NAME = MODEL_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for ProjectTas entity.
	*/ 
	DAOConstantsEntry PROJECT_TAS_ENTRY = DAOConstants.getDAOConstant(ProjectTas.class);

	/** 
	* Alias value: ProjectTas_id
	* Hibernate value: ProjectTas.id
	*/
	String  PROJECT_TAS_ID = PROJECT_TAS_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ProjectTas_project_id
	* Hibernate value: ProjectTas.project.id
	*/
	String  PROJECT_TAS_PROJECT_ID = PROJECT_TAS_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ProjectTas_series
	* Hibernate value: ProjectTas.series
	*/
	String  PROJECT_TAS_SERIES = PROJECT_TAS_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ProjectTas_number
	* Hibernate value: ProjectTas.number
	*/
	String  PROJECT_TAS_NUMBER = PROJECT_TAS_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ProjectTas_target_id
	* Hibernate value: ProjectTas.target.id
	*/
	String  PROJECT_TAS_TARGET_ID = PROJECT_TAS_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: ProjectTas_tasItem_id
	* Hibernate value: ProjectTas.tasItem.id
	*/
	String  PROJECT_TAS_TAS_ITEM_ID = PROJECT_TAS_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: ProjectTas_counter
	* Hibernate value: ProjectTas.counter
	*/
	String  PROJECT_TAS_COUNTER = PROJECT_TAS_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: ProjectTas_taskHolder_id
	* Hibernate value: ProjectTas.taskHolder.id
	*/
	String  PROJECT_TAS_TASK_HOLDER_ID = PROJECT_TAS_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: ProjectTas_comments
	* Hibernate value: ProjectTas.comments
	*/
	String  PROJECT_TAS_COMMENTS = PROJECT_TAS_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: ProjectTas_status
	* Hibernate value: ProjectTas.status
	*/
	String  PROJECT_TAS_STATUS = PROJECT_TAS_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: ProjectTas_statusDate
	* Hibernate value: ProjectTas.statusDate
	*/
	String  PROJECT_TAS_STATUS_DATE = PROJECT_TAS_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: ProjectTas_project_name
	* Hibernate value: ProjectTas.project.name
	*/
	String  PROJECT_TAS_PROJECT_NAME = PROJECT_TAS_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: ProjectTas_project_alias
	* Hibernate value: ProjectTas.project.alias
	*/
	String  PROJECT_TAS_PROJECT_ALIAS = PROJECT_TAS_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: ProjectTas_project_date
	* Hibernate value: ProjectTas.project.date
	*/
	String  PROJECT_TAS_PROJECT_DATE = PROJECT_TAS_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: ProjectTas_project_tas
	* Hibernate value: ProjectTas.project.tas
	*/
	String  PROJECT_TAS_PROJECT_TAS = PROJECT_TAS_ENTRY.getAliasNames()[14];



	/** 
	* DAOConstantsEntry for TasItem entity.
	*/ 
	DAOConstantsEntry TAS_ITEM_ENTRY = DAOConstants.getDAOConstant(TasItem.class);

	/** 
	* Alias value: TasItem_addInfo
	* Hibernate value: TasItem.addInfo
	*/
	String  TAS_ITEM_ADD_INFO = TAS_ITEM_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: TasItem_description
	* Hibernate value: TasItem.description
	*/
	String  TAS_ITEM_DESCRIPTION = TAS_ITEM_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: TasItem_id
	* Hibernate value: TasItem.id
	*/
	String  TAS_ITEM_ID = TAS_ITEM_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: TasItem_model_id
	* Hibernate value: TasItem.model.id
	*/
	String  TAS_ITEM_MODEL_ID = TAS_ITEM_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: TasItem_model_name
	* Hibernate value: TasItem.model.name
	*/
	String  TAS_ITEM_MODEL_NAME = TAS_ITEM_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: TasItem_model_make_id
	* Hibernate value: TasItem.model.make.id
	*/
	String  TAS_ITEM_MODEL_MAKE_ID = TAS_ITEM_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: TasItem_model_make_name
	* Hibernate value: TasItem.model.make.name
	*/
	String  TAS_ITEM_MODEL_MAKE_NAME = TAS_ITEM_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: TasItem_privateCode
	* Hibernate value: TasItem.privateCode
	*/
	String  TAS_ITEM_PRIVATE_CODE = TAS_ITEM_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: TasItem_publicCode
	* Hibernate value: TasItem.publicCode
	*/
	String  TAS_ITEM_PUBLIC_CODE = TAS_ITEM_ENTRY.getAliasNames()[8];


}