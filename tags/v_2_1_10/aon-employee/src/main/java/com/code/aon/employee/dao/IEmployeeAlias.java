package com.code.aon.employee.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.employee.ExpendituresItems;
import com.code.aon.employee.Expenditures;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IEmployeeAlias {



	/** 
	* DAOConstantsEntry for ExpendituresItems entity.
	*/ 
	DAOConstantsEntry EXPENDITURES_ITEMS_ENTRY = DAOConstants.getDAOConstant(ExpendituresItems.class);

	/** 
	* Alias value: ExpendituresItems_id
	* Hibernate value: ExpendituresItems.id
	*/
	String  EXPENDITURES_ITEMS_ID = EXPENDITURES_ITEMS_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ExpendituresItems_name
	* Hibernate value: ExpendituresItems.name
	*/
	String  EXPENDITURES_ITEMS_NAME = EXPENDITURES_ITEMS_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for Expenditures entity.
	*/ 
	DAOConstantsEntry EXPENDITURES_ENTRY = DAOConstants.getDAOConstant(Expenditures.class);

	/** 
	* Alias value: Expenditures_amount
	* Hibernate value: Expenditures.amount
	*/
	String  EXPENDITURES_AMOUNT = EXPENDITURES_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Expenditures_date
	* Hibernate value: Expenditures.date
	*/
	String  EXPENDITURES_DATE = EXPENDITURES_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Expenditures_id
	* Hibernate value: Expenditures.id
	*/
	String  EXPENDITURES_ID = EXPENDITURES_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Expenditures_item_id
	* Hibernate value: Expenditures.item.id
	*/
	String  EXPENDITURES_ITEM_ID = EXPENDITURES_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Expenditures_resource
	* Hibernate value: Expenditures.resource
	*/
	String  EXPENDITURES_RESOURCE = EXPENDITURES_ENTRY.getAliasNames()[4];


}