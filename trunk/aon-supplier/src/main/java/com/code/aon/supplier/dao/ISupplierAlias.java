package com.code.aon.supplier.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.supplier.Supplier;

/** 
* Interface for holding entity properties constants.
*/ 
public interface ISupplierAlias {



	/** 
	* DAOConstantsEntry for Supplier entity.
	*/ 
	DAOConstantsEntry SUPPLIER_ENTRY = DAOConstants.getDAOConstant(Supplier.class);

	/** 
	* Alias value: Supplier_id
	* Hibernate value: Supplier.id
	*/
	String  SUPPLIER_ID = SUPPLIER_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Supplier_registry_alias
	* Hibernate value: Supplier.registry.alias
	*/
	String  SUPPLIER_REGISTRY_ALIAS = SUPPLIER_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Supplier_registry_document
	* Hibernate value: Supplier.registry.document
	*/
	String  SUPPLIER_REGISTRY_DOCUMENT = SUPPLIER_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Supplier_registry_id
	* Hibernate value: Supplier.registry.id
	*/
	String  SUPPLIER_REGISTRY_ID = SUPPLIER_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Supplier_registry_name
	* Hibernate value: Supplier.registry.name
	*/
	String  SUPPLIER_REGISTRY_NAME = SUPPLIER_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Supplier_registry_surname
	* Hibernate value: Supplier.registry.surname
	*/
	String  SUPPLIER_REGISTRY_SURNAME = SUPPLIER_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Supplier_registry_type
	* Hibernate value: Supplier.registry.type
	*/
	String  SUPPLIER_REGISTRY_TYPE = SUPPLIER_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Supplier_scope_id
	* Hibernate value: Supplier.scope.id
	*/
	String  SUPPLIER_SCOPE_ID = SUPPLIER_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Supplier_status
	* Hibernate value: Supplier.status
	*/
	String  SUPPLIER_STATUS = SUPPLIER_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Supplier_withholding
	* Hibernate value: Supplier.withholding
	*/
	String  SUPPLIER_WITHHOLDING = SUPPLIER_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Supplier_transaction
	* Hibernate value: Supplier.transaction
	*/
	String  SUPPLIER_TRANSACTION = SUPPLIER_ENTRY.getAliasNames()[10];


}