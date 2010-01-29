package com.code.aon.commercial.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferDetail;
import com.code.aon.commercial.Target;
import com.code.aon.commercial.CommercialActivity;
import com.code.aon.commercial.CommercialSegment;
import com.code.aon.commercial.CommercialTracking;
import com.code.aon.commercial.TargetItem;
import com.code.aon.commercial.TargetSegment;
import com.code.aon.commercial.TargetSeller;

/** 
* Interface for holding entity properties constants.
*/ 
public interface ICommercialAlias {



	/** 
	* DAOConstantsEntry for Offer entity.
	*/ 
	DAOConstantsEntry OFFER_ENTRY = DAOConstants.getDAOConstant(Offer.class);

	/** 
	* Alias value: Offer_address_id
	* Hibernate value: Offer.address.id
	*/
	String  OFFER_ADDRESS_ID = OFFER_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Offer_discountExpression
	* Hibernate value: Offer.discountExpression
	*/
	String  OFFER_DISCOUNT_EXPRESSION = OFFER_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Offer_id
	* Hibernate value: Offer.id
	*/
	String  OFFER_ID = OFFER_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Offer_issueDate
	* Hibernate value: Offer.issueDate
	*/
	String  OFFER_ISSUE_DATE = OFFER_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Offer_number
	* Hibernate value: Offer.number
	*/
	String  OFFER_NUMBER = OFFER_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Offer_payMethod_id
	* Hibernate value: Offer.payMethod.id
	*/
	String  OFFER_PAY_METHOD_ID = OFFER_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Offer_securityLevel
	* Hibernate value: Offer.securityLevel
	*/
	String  OFFER_SECURITY_LEVEL = OFFER_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Offer_seller_id
	* Hibernate value: Offer.seller.id
	*/
	String  OFFER_SELLER_ID = OFFER_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Offer_series
	* Hibernate value: Offer.series
	*/
	String  OFFER_SERIES = OFFER_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Offer_status
	* Hibernate value: Offer.status
	*/
	String  OFFER_STATUS = OFFER_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Offer_target_id
	* Hibernate value: Offer.target.id
	*/
	String  OFFER_TARGET_ID = OFFER_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Offer_tariff_id
	* Hibernate value: Offer.tariff.id
	*/
	String  OFFER_TARIFF_ID = OFFER_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Offer_type
	* Hibernate value: Offer.type
	*/
	String  OFFER_TYPE = OFFER_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Offer_workPlace_id
	* Hibernate value: Offer.workPlace.id
	*/
	String  OFFER_WORK_PLACE_ID = OFFER_ENTRY.getAliasNames()[13];



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
	* Alias value: Target_advertising
	* Hibernate value: Target.advertising
	*/
	String  TARGET_ADVERTISING = TARGET_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Target_id
	* Hibernate value: Target.id
	*/
	String  TARGET_ID = TARGET_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Target_registry_id
	* Hibernate value: Target.registry.id
	*/
	String  TARGET_REGISTRY_ID = TARGET_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Target_registry_name
	* Hibernate value: Target.registry.name
	*/
	String  TARGET_REGISTRY_NAME = TARGET_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Target_registry_surname
	* Hibernate value: Target.registry.surname
	*/
	String  TARGET_REGISTRY_SURNAME = TARGET_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Target_registry_alias
	* Hibernate value: Target.registry.alias
	*/
	String  TARGET_REGISTRY_ALIAS = TARGET_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Target_registry_document
	* Hibernate value: Target.registry.document
	*/
	String  TARGET_REGISTRY_DOCUMENT = TARGET_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Target_withholding
	* Hibernate value: Target.withholding
	*/
	String  TARGET_WITHHOLDING = TARGET_ENTRY.getAliasNames()[7];



	/** 
	* DAOConstantsEntry for CommercialActivity entity.
	*/ 
	DAOConstantsEntry COMMERCIAL_ACTIVITY_ENTRY = DAOConstants.getDAOConstant(CommercialActivity.class);

	/** 
	* Alias value: CommercialActivity_id
	* Hibernate value: CommercialActivity.id
	*/
	String  COMMERCIAL_ACTIVITY_ID = COMMERCIAL_ACTIVITY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: CommercialActivity_name
	* Hibernate value: CommercialActivity.name
	*/
	String  COMMERCIAL_ACTIVITY_NAME = COMMERCIAL_ACTIVITY_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for CommercialSegment entity.
	*/ 
	DAOConstantsEntry COMMERCIAL_SEGMENT_ENTRY = DAOConstants.getDAOConstant(CommercialSegment.class);

	/** 
	* Alias value: CommercialSegment_id
	* Hibernate value: CommercialSegment.id
	*/
	String  COMMERCIAL_SEGMENT_ID = COMMERCIAL_SEGMENT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: CommercialSegment_name
	* Hibernate value: CommercialSegment.name
	*/
	String  COMMERCIAL_SEGMENT_NAME = COMMERCIAL_SEGMENT_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for CommercialTracking entity.
	*/ 
	DAOConstantsEntry COMMERCIAL_TRACKING_ENTRY = DAOConstants.getDAOConstant(CommercialTracking.class);

	/** 
	* Alias value: CommercialTracking_activity_id
	* Hibernate value: CommercialTracking.activity.id
	*/
	String  COMMERCIAL_TRACKING_ACTIVITY_ID = COMMERCIAL_TRACKING_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: CommercialTracking_comments
	* Hibernate value: CommercialTracking.comments
	*/
	String  COMMERCIAL_TRACKING_COMMENTS = COMMERCIAL_TRACKING_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: CommercialTracking_date
	* Hibernate value: CommercialTracking.date
	*/
	String  COMMERCIAL_TRACKING_DATE = COMMERCIAL_TRACKING_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: CommercialTracking_id
	* Hibernate value: CommercialTracking.id
	*/
	String  COMMERCIAL_TRACKING_ID = COMMERCIAL_TRACKING_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: CommercialTracking_next_id
	* Hibernate value: CommercialTracking.next.id
	*/
	String  COMMERCIAL_TRACKING_NEXT_ID = COMMERCIAL_TRACKING_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: CommercialTracking_seller_id
	* Hibernate value: CommercialTracking.seller.id
	*/
	String  COMMERCIAL_TRACKING_SELLER_ID = COMMERCIAL_TRACKING_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: CommercialTracking_status
	* Hibernate value: CommercialTracking.status
	*/
	String  COMMERCIAL_TRACKING_STATUS = COMMERCIAL_TRACKING_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: CommercialTracking_target_id
	* Hibernate value: CommercialTracking.target.id
	*/
	String  COMMERCIAL_TRACKING_TARGET_ID = COMMERCIAL_TRACKING_ENTRY.getAliasNames()[7];



	/** 
	* DAOConstantsEntry for TargetItem entity.
	*/ 
	DAOConstantsEntry TARGET_ITEM_ENTRY = DAOConstants.getDAOConstant(TargetItem.class);

	/** 
	* Alias value: TargetItem_id
	* Hibernate value: TargetItem.id
	*/
	String  TARGET_ITEM_ID = TARGET_ITEM_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: TargetItem_item_id
	* Hibernate value: TargetItem.item.id
	*/
	String  TARGET_ITEM_ITEM_ID = TARGET_ITEM_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: TargetItem_status
	* Hibernate value: TargetItem.status
	*/
	String  TARGET_ITEM_STATUS = TARGET_ITEM_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: TargetItem_target_id
	* Hibernate value: TargetItem.target.id
	*/
	String  TARGET_ITEM_TARGET_ID = TARGET_ITEM_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for TargetSegment entity.
	*/ 
	DAOConstantsEntry TARGET_SEGMENT_ENTRY = DAOConstants.getDAOConstant(TargetSegment.class);

	/** 
	* Alias value: TargetSegment_id
	* Hibernate value: TargetSegment.id
	*/
	String  TARGET_SEGMENT_ID = TARGET_SEGMENT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: TargetSegment_segment_id
	* Hibernate value: TargetSegment.segment.id
	*/
	String  TARGET_SEGMENT_SEGMENT_ID = TARGET_SEGMENT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: TargetSegment_target_id
	* Hibernate value: TargetSegment.target.id
	*/
	String  TARGET_SEGMENT_TARGET_ID = TARGET_SEGMENT_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for TargetSeller entity.
	*/ 
	DAOConstantsEntry TARGET_SELLER_ENTRY = DAOConstants.getDAOConstant(TargetSeller.class);

	/** 
	* Alias value: TargetSeller_endDate
	* Hibernate value: TargetSeller.endDate
	*/
	String  TARGET_SELLER_END_DATE = TARGET_SELLER_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: TargetSeller_id
	* Hibernate value: TargetSeller.id
	*/
	String  TARGET_SELLER_ID = TARGET_SELLER_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: TargetSeller_seller_id
	* Hibernate value: TargetSeller.seller.id
	*/
	String  TARGET_SELLER_SELLER_ID = TARGET_SELLER_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: TargetSeller_startDate
	* Hibernate value: TargetSeller.startDate
	*/
	String  TARGET_SELLER_START_DATE = TARGET_SELLER_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: TargetSeller_status
	* Hibernate value: TargetSeller.status
	*/
	String  TARGET_SELLER_STATUS = TARGET_SELLER_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: TargetSeller_target_id
	* Hibernate value: TargetSeller.target.id
	*/
	String  TARGET_SELLER_TARGET_ID = TARGET_SELLER_ENTRY.getAliasNames()[5];


}