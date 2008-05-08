package com.code.aon.tasDelivery.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.tasDelivery.TasDelivery;

/** 
* Interface for holding entity properties constants.
*/ 
public interface ITasDeliveryAlias {



	/** 
	* DAOConstantsEntry for TasDelivery entity.
	*/ 
	DAOConstantsEntry TAS_DELIVERY_ENTRY = DAOConstants.getDAOConstant(TasDelivery.class);

	/** 
	* Alias value: TasDelivery_delivery_id
	* Hibernate value: TasDelivery.delivery.id
	*/
	String  TAS_DELIVERY_DELIVERY_ID = TAS_DELIVERY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: TasDelivery_id
	* Hibernate value: TasDelivery.id
	*/
	String  TAS_DELIVERY_ID = TAS_DELIVERY_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: TasDelivery_offer_id
	* Hibernate value: TasDelivery.offer.id
	*/
	String  TAS_DELIVERY_OFFER_ID = TAS_DELIVERY_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: TasDelivery_supportOrder_id
	* Hibernate value: TasDelivery.supportOrder.id
	*/
	String  TAS_DELIVERY_SUPPORT_ORDER_ID = TAS_DELIVERY_ENTRY.getAliasNames()[3];


}