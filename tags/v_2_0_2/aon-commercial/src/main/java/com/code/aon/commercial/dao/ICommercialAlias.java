package com.code.aon.commercial.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferDetail;
import com.code.aon.commercial.Target;

/** 
* Interface for holding entity properties constants.
*/ 
public interface ICommercialAlias {



	/** 
	* DAOConstantsEntry for Offer entity.
	*/ 
	DAOConstantsEntry OFFER_ENTRY = DAOConstants.getDAOConstant(Offer.class);

	/** 
	* Alias value: Offer_discountExpression
	* Hibernate value: Offer.discountExpression
	*/
	String  OFFER_DISCOUNT_EXPRESSION = OFFER_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Offer_id
	* Hibernate value: Offer.id
	*/
	String  OFFER_ID = OFFER_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Offer_issueDate
	* Hibernate value: Offer.issueDate
	*/
	String  OFFER_ISSUE_DATE = OFFER_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Offer_number
	* Hibernate value: Offer.number
	*/
	String  OFFER_NUMBER = OFFER_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Offer_payMethod_id
	* Hibernate value: Offer.payMethod.id
	*/
	String  OFFER_PAY_METHOD_ID = OFFER_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Offer_seller_id
	* Hibernate value: Offer.seller.id
	*/
	String  OFFER_SELLER_ID = OFFER_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Offer_series
	* Hibernate value: Offer.series
	*/
	String  OFFER_SERIES = OFFER_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Offer_status
	* Hibernate value: Offer.status
	*/
	String  OFFER_STATUS = OFFER_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Offer_target_id
	* Hibernate value: Offer.target.id
	*/
	String  OFFER_TARGET_ID = OFFER_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Offer_type
	* Hibernate value: Offer.type
	*/
	String  OFFER_TYPE = OFFER_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Offer_workPlace_id
	* Hibernate value: Offer.workPlace.id
	*/
	String  OFFER_WORK_PLACE_ID = OFFER_ENTRY.getAliasNames()[10];



	/** 
	* DAOConstantsEntry for OfferDetail entity.
	*/ 
	DAOConstantsEntry OFFER_DETAIL_ENTRY = DAOConstants.getDAOConstant(OfferDetail.class);

	/** 
	* Alias value: OfferDetail_description
	* Hibernate value: OfferDetail.description
	*/
	String  OFFER_DETAIL_DESCRIPTION = OFFER_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: OfferDetail_discountExpression
	* Hibernate value: OfferDetail.discountExpression
	*/
	String  OFFER_DETAIL_DISCOUNT_EXPRESSION = OFFER_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: OfferDetail_id
	* Hibernate value: OfferDetail.id
	*/
	String  OFFER_DETAIL_ID = OFFER_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: OfferDetail_item_id
	* Hibernate value: OfferDetail.item.id
	*/
	String  OFFER_DETAIL_ITEM_ID = OFFER_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: OfferDetail_offer_id
	* Hibernate value: OfferDetail.offer.id
	*/
	String  OFFER_DETAIL_OFFER_ID = OFFER_DETAIL_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: OfferDetail_price
	* Hibernate value: OfferDetail.price
	*/
	String  OFFER_DETAIL_PRICE = OFFER_DETAIL_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: OfferDetail_quantity
	* Hibernate value: OfferDetail.quantity
	*/
	String  OFFER_DETAIL_QUANTITY = OFFER_DETAIL_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: OfferDetail_status
	* Hibernate value: OfferDetail.status
	*/
	String  OFFER_DETAIL_STATUS = OFFER_DETAIL_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: OfferDetail_item_product_type
	* Hibernate value: OfferDetail.item.product.type
	*/
	String  OFFER_DETAIL_ITEM_PRODUCT_TYPE = OFFER_DETAIL_ENTRY.getAliasNames()[8];



	/** 
	* DAOConstantsEntry for Target entity.
	*/ 
	DAOConstantsEntry TARGET_ENTRY = DAOConstants.getDAOConstant(Target.class);

	/** 
	* Alias value: Target_id
	* Hibernate value: Target.id
	*/
	String  TARGET_ID = TARGET_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Target_registry_id
	* Hibernate value: Target.registry.id
	*/
	String  TARGET_REGISTRY_ID = TARGET_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Target_registry_name
	* Hibernate value: Target.registry.name
	*/
	String  TARGET_REGISTRY_NAME = TARGET_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Target_registry_surname
	* Hibernate value: Target.registry.surname
	*/
	String  TARGET_REGISTRY_SURNAME = TARGET_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Target_registry_alias
	* Hibernate value: Target.registry.alias
	*/
	String  TARGET_REGISTRY_ALIAS = TARGET_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Target_registry_document
	* Hibernate value: Target.registry.document
	*/
	String  TARGET_REGISTRY_DOCUMENT = TARGET_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Target_withholding
	* Hibernate value: Target.withholding
	*/
	String  TARGET_WITHHOLDING = TARGET_ENTRY.getAliasNames()[6];


}