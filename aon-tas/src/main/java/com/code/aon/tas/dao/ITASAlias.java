package com.code.aon.tas.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.tas.Appraiser;
import com.code.aon.tas.Make;
import com.code.aon.tas.Model;
import com.code.aon.tas.SupportOrder;
import com.code.aon.tas.SupportOrderInsurance;
import com.code.aon.tas.TasItem;

/** 
* Interface for holding entity properties constants.
*/ 
public interface ITASAlias {



	/** 
	* DAOConstantsEntry for Appraiser entity.
	*/ 
	DAOConstantsEntry APPRAISER_ENTRY = DAOConstants.getDAOConstant(Appraiser.class);

	/** 
	* Alias value: Appraiser_id
	* Hibernate value: Appraiser.id
	*/
	String  APPRAISER_ID = APPRAISER_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Appraiser_registry_id
	* Hibernate value: Appraiser.registry.id
	*/
	String  APPRAISER_REGISTRY_ID = APPRAISER_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Appraiser_registry_name
	* Hibernate value: Appraiser.registry.name
	*/
	String  APPRAISER_REGISTRY_NAME = APPRAISER_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Appraiser_registry_surname
	* Hibernate value: Appraiser.registry.surname
	*/
	String  APPRAISER_REGISTRY_SURNAME = APPRAISER_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Appraiser_registry_alias
	* Hibernate value: Appraiser.registry.alias
	*/
	String  APPRAISER_REGISTRY_ALIAS = APPRAISER_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Appraiser_registry_document
	* Hibernate value: Appraiser.registry.document
	*/
	String  APPRAISER_REGISTRY_DOCUMENT = APPRAISER_ENTRY.getAliasNames()[5];



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
	* DAOConstantsEntry for SupportOrder entity.
	*/ 
	DAOConstantsEntry SUPPORT_ORDER_ENTRY = DAOConstants.getDAOConstant(SupportOrder.class);

	/** 
	* Alias value: SupportOrder_counterti
	* Hibernate value: SupportOrder.counterti
	*/
	String  SUPPORT_ORDER_COUNTERTI = SUPPORT_ORDER_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SupportOrder_description
	* Hibernate value: SupportOrder.description
	*/
	String  SUPPORT_ORDER_DESCRIPTION = SUPPORT_ORDER_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: SupportOrder_employee_id
	* Hibernate value: SupportOrder.employee.id
	*/
	String  SUPPORT_ORDER_EMPLOYEE_ID = SUPPORT_ORDER_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: SupportOrder_finalDate
	* Hibernate value: SupportOrder.finalDate
	*/
	String  SUPPORT_ORDER_FINAL_DATE = SUPPORT_ORDER_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: SupportOrder_id
	* Hibernate value: SupportOrder.id
	*/
	String  SUPPORT_ORDER_ID = SUPPORT_ORDER_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: SupportOrder_number
	* Hibernate value: SupportOrder.number
	*/
	String  SUPPORT_ORDER_NUMBER = SUPPORT_ORDER_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: SupportOrder_series
	* Hibernate value: SupportOrder.series
	*/
	String  SUPPORT_ORDER_SERIES = SUPPORT_ORDER_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: SupportOrder_startDate
	* Hibernate value: SupportOrder.startDate
	*/
	String  SUPPORT_ORDER_START_DATE = SUPPORT_ORDER_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: SupportOrder_status
	* Hibernate value: SupportOrder.status
	*/
	String  SUPPORT_ORDER_STATUS = SUPPORT_ORDER_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: SupportOrder_operation
	* Hibernate value: SupportOrder.operation
	*/
	String  SUPPORT_ORDER_OPERATION = SUPPORT_ORDER_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: SupportOrder_target_id
	* Hibernate value: SupportOrder.target.id
	*/
	String  SUPPORT_ORDER_TARGET_ID = SUPPORT_ORDER_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: SupportOrder_target_registry_document
	* Hibernate value: SupportOrder.target.registry.document
	*/
	String  SUPPORT_ORDER_TARGET_REGISTRY_DOCUMENT = SUPPORT_ORDER_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: SupportOrder_tasItem_id
	* Hibernate value: SupportOrder.tasItem.id
	*/
	String  SUPPORT_ORDER_TAS_ITEM_ID = SUPPORT_ORDER_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: SupportOrder_tasItem_publicCode
	* Hibernate value: SupportOrder.tasItem.publicCode
	*/
	String  SUPPORT_ORDER_TAS_ITEM_PUBLIC_CODE = SUPPORT_ORDER_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: SupportOrder_workPlace_id
	* Hibernate value: SupportOrder.workPlace.id
	*/
	String  SUPPORT_ORDER_WORK_PLACE_ID = SUPPORT_ORDER_ENTRY.getAliasNames()[14];



	/** 
	* DAOConstantsEntry for SupportOrderInsurance entity.
	*/ 
	DAOConstantsEntry SUPPORT_ORDER_INSURANCE_ENTRY = DAOConstants.getDAOConstant(SupportOrderInsurance.class);

	/** 
	* Alias value: SupportOrderInsurance_appraiser_id
	* Hibernate value: SupportOrderInsurance.appraiser.id
	*/
	String  SUPPORT_ORDER_INSURANCE_APPRAISER_ID = SUPPORT_ORDER_INSURANCE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SupportOrderInsurance_claimNumber
	* Hibernate value: SupportOrderInsurance.claimNumber
	*/
	String  SUPPORT_ORDER_INSURANCE_CLAIM_NUMBER = SUPPORT_ORDER_INSURANCE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: SupportOrderInsurance_franchise
	* Hibernate value: SupportOrderInsurance.franchise
	*/
	String  SUPPORT_ORDER_INSURANCE_FRANCHISE = SUPPORT_ORDER_INSURANCE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: SupportOrderInsurance_id
	* Hibernate value: SupportOrderInsurance.id
	*/
	String  SUPPORT_ORDER_INSURANCE_ID = SUPPORT_ORDER_INSURANCE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: SupportOrderInsurance_insurance_id
	* Hibernate value: SupportOrderInsurance.insurance.id
	*/
	String  SUPPORT_ORDER_INSURANCE_INSURANCE_ID = SUPPORT_ORDER_INSURANCE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: SupportOrderInsurance_policiType
	* Hibernate value: SupportOrderInsurance.policiType
	*/
	String  SUPPORT_ORDER_INSURANCE_POLICI_TYPE = SUPPORT_ORDER_INSURANCE_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: SupportOrderInsurance_supportOrder_id
	* Hibernate value: SupportOrderInsurance.supportOrder.id
	*/
	String  SUPPORT_ORDER_INSURANCE_SUPPORT_ORDER_ID = SUPPORT_ORDER_INSURANCE_ENTRY.getAliasNames()[6];



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
	* Alias value: TasItem_privateCode
	* Hibernate value: TasItem.privateCode
	*/
	String  TAS_ITEM_PRIVATE_CODE = TAS_ITEM_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: TasItem_publicCode
	* Hibernate value: TasItem.publicCode
	*/
	String  TAS_ITEM_PUBLIC_CODE = TAS_ITEM_ENTRY.getAliasNames()[5];


}