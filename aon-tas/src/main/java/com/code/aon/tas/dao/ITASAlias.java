package com.code.aon.tas.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.tas.Make;
import com.code.aon.tas.Model;
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