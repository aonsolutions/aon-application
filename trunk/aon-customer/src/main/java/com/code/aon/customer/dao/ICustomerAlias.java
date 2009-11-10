package com.code.aon.customer.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.customer.Customer;

/** 
* Interface for holding entity properties constants.
*/ 
public interface ICustomerAlias {



	/** 
	* DAOConstantsEntry for Customer entity.
	*/ 
	DAOConstantsEntry CUSTOMER_ENTRY = DAOConstants.getDAOConstant(Customer.class);

	/** 
	* Alias value: Customer_id
	* Hibernate value: Customer.id
	*/
	String  CUSTOMER_ID = CUSTOMER_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Customer_registry_id
	* Hibernate value: Customer.registry.id
	*/
	String  CUSTOMER_REGISTRY_ID = CUSTOMER_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Customer_status
	* Hibernate value: Customer.status
	*/
	String  CUSTOMER_STATUS = CUSTOMER_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Customer_surcharge
	* Hibernate value: Customer.surcharge
	*/
	String  CUSTOMER_SURCHARGE = CUSTOMER_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Customer_tariff_id
	* Hibernate value: Customer.tariff.id
	*/
	String  CUSTOMER_TARIFF_ID = CUSTOMER_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Customer_registry_name
	* Hibernate value: Customer.registry.name
	*/
	String  CUSTOMER_REGISTRY_NAME = CUSTOMER_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Customer_registry_surname
	* Hibernate value: Customer.registry.surname
	*/
	String  CUSTOMER_REGISTRY_SURNAME = CUSTOMER_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Customer_registry_alias
	* Hibernate value: Customer.registry.alias
	*/
	String  CUSTOMER_REGISTRY_ALIAS = CUSTOMER_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Customer_registry_document
	* Hibernate value: Customer.registry.document
	*/
	String  CUSTOMER_REGISTRY_DOCUMENT = CUSTOMER_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Customer_withholding
	* Hibernate value: Customer.withholding
	*/
	String  CUSTOMER_WITHHOLDING = CUSTOMER_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Customer_transaction
	* Hibernate value: Customer.transaction
	*/
	String  CUSTOMER_TRANSACTION = CUSTOMER_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Customer_scope_id
	* Hibernate value: Customer.scope.id
	*/
	String  CUSTOMER_SCOPE_ID = CUSTOMER_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Customer_eInvoice
	* Hibernate value: Customer.eInvoice
	*/
	String  CUSTOMER_E_INVOICE = CUSTOMER_ENTRY.getAliasNames()[12];


}