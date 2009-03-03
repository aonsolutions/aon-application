package com.code.aon.seller.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.seller.Seller;

/** 
* Interface for holding entity properties constants.
*/ 
public interface ISellerAlias {



	/** 
	* DAOConstantsEntry for Seller entity.
	*/ 
	DAOConstantsEntry SELLER_ENTRY = DAOConstants.getDAOConstant(Seller.class);

	/** 
	* Alias value: Seller_id
	* Hibernate value: Seller.id
	*/
	String  SELLER_ID = SELLER_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Seller_description
	* Hibernate value: Seller.description
	*/
	String  SELLER_DESCRIPTION = SELLER_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Seller_status
	* Hibernate value: Seller.status
	*/
	String  SELLER_STATUS = SELLER_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Seller_registry_id
	* Hibernate value: Seller.registry.id
	*/
	String  SELLER_REGISTRY_ID = SELLER_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Seller_registry_name
	* Hibernate value: Seller.registry.name
	*/
	String  SELLER_REGISTRY_NAME = SELLER_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Seller_registry_surname
	* Hibernate value: Seller.registry.surname
	*/
	String  SELLER_REGISTRY_SURNAME = SELLER_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Seller_registry_alias
	* Hibernate value: Seller.registry.alias
	*/
	String  SELLER_REGISTRY_ALIAS = SELLER_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Seller_registry_document
	* Hibernate value: Seller.registry.document
	*/
	String  SELLER_REGISTRY_DOCUMENT = SELLER_ENTRY.getAliasNames()[7];


}