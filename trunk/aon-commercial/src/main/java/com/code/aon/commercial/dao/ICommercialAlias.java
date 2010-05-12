package com.code.aon.commercial.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.commercial.CommercialActivity;
import com.code.aon.commercial.CommercialTracking;
import com.code.aon.commercial.CommercialTerm;
import com.code.aon.commercial.Commission;
import com.code.aon.commercial.CommissionCategory;
import com.code.aon.commercial.CommissionItem;
import com.code.aon.commercial.CommissionTypeCommission;
import com.code.aon.commercial.Expense;
import com.code.aon.commercial.ExpenseAccount;
import com.code.aon.commercial.ExpenseAccountDetail;
import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferAttachment;
import com.code.aon.commercial.OfferDetail;
import com.code.aon.commercial.OfferDetailCommission;
import com.code.aon.commercial.OfferTerm;
import com.code.aon.commercial.Target;
import com.code.aon.commercial.TargetItem;
import com.code.aon.commercial.TargetSeller;
import com.code.aon.commercial.TargetThirdParty;

/** 
* Interface for holding entity properties constants.
*/ 
public interface ICommercialAlias {



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
	* DAOConstantsEntry for CommercialTerm entity.
	*/ 
	DAOConstantsEntry COMMERCIAL_TERM_ENTRY = DAOConstants.getDAOConstant(CommercialTerm.class);

	/** 
	* Alias value: CommercialTerm_description
	* Hibernate value: CommercialTerm.description
	*/
	String  COMMERCIAL_TERM_DESCRIPTION = COMMERCIAL_TERM_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: CommercialTerm_general
	* Hibernate value: CommercialTerm.general
	*/
	String  COMMERCIAL_TERM_GENERAL = COMMERCIAL_TERM_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: CommercialTerm_id
	* Hibernate value: CommercialTerm.id
	*/
	String  COMMERCIAL_TERM_ID = COMMERCIAL_TERM_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: CommercialTerm_name
	* Hibernate value: CommercialTerm.name
	*/
	String  COMMERCIAL_TERM_NAME = COMMERCIAL_TERM_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for Commission entity.
	*/ 
	DAOConstantsEntry COMMISSION_ENTRY = DAOConstants.getDAOConstant(Commission.class);

	/** 
	* Alias value: Commission_endDate
	* Hibernate value: Commission.endDate
	*/
	String  COMMISSION_END_DATE = COMMISSION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Commission_id
	* Hibernate value: Commission.id
	*/
	String  COMMISSION_ID = COMMISSION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Commission_name
	* Hibernate value: Commission.name
	*/
	String  COMMISSION_NAME = COMMISSION_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Commission_startDate
	* Hibernate value: Commission.startDate
	*/
	String  COMMISSION_START_DATE = COMMISSION_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for CommissionCategory entity.
	*/ 
	DAOConstantsEntry COMMISSION_CATEGORY_ENTRY = DAOConstants.getDAOConstant(CommissionCategory.class);

	/** 
	* Alias value: CommissionCategory_category_id
	* Hibernate value: CommissionCategory.category.id
	*/
	String  COMMISSION_CATEGORY_CATEGORY_ID = COMMISSION_CATEGORY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: CommissionCategory_commission_id
	* Hibernate value: CommissionCategory.commission.id
	*/
	String  COMMISSION_CATEGORY_COMMISSION_ID = COMMISSION_CATEGORY_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: CommissionCategory_id
	* Hibernate value: CommissionCategory.id
	*/
	String  COMMISSION_CATEGORY_ID = COMMISSION_CATEGORY_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: CommissionCategory_quantity
	* Hibernate value: CommissionCategory.quantity
	*/
	String  COMMISSION_CATEGORY_QUANTITY = COMMISSION_CATEGORY_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: CommissionCategory_rate
	* Hibernate value: CommissionCategory.rate
	*/
	String  COMMISSION_CATEGORY_RATE = COMMISSION_CATEGORY_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for CommissionItem entity.
	*/ 
	DAOConstantsEntry COMMISSION_ITEM_ENTRY = DAOConstants.getDAOConstant(CommissionItem.class);

	/** 
	* Alias value: CommissionItem_amount
	* Hibernate value: CommissionItem.amount
	*/
	String  COMMISSION_ITEM_AMOUNT = COMMISSION_ITEM_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: CommissionItem_commission_id
	* Hibernate value: CommissionItem.commission.id
	*/
	String  COMMISSION_ITEM_COMMISSION_ID = COMMISSION_ITEM_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: CommissionItem_id
	* Hibernate value: CommissionItem.id
	*/
	String  COMMISSION_ITEM_ID = COMMISSION_ITEM_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: CommissionItem_item_id
	* Hibernate value: CommissionItem.item.id
	*/
	String  COMMISSION_ITEM_ITEM_ID = COMMISSION_ITEM_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: CommissionItem_quantity
	* Hibernate value: CommissionItem.quantity
	*/
	String  COMMISSION_ITEM_QUANTITY = COMMISSION_ITEM_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: CommissionItem_rate
	* Hibernate value: CommissionItem.rate
	*/
	String  COMMISSION_ITEM_RATE = COMMISSION_ITEM_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for CommissionTypeCommission entity.
	*/ 
	DAOConstantsEntry COMMISSION_TYPE_COMMISSION_ENTRY = DAOConstants.getDAOConstant(CommissionTypeCommission.class);

	/** 
	* Alias value: CommissionTypeCommission_id
	* Hibernate value: CommissionTypeCommission.id
	*/
	String  COMMISSION_TYPE_COMMISSION_ID = COMMISSION_TYPE_COMMISSION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: CommissionTypeCommission_commissionType_id
	* Hibernate value: CommissionTypeCommission.commissionType.id
	*/
	String  COMMISSION_TYPE_COMMISSION_COMMISSION_TYPE_ID = COMMISSION_TYPE_COMMISSION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: CommissionTypeCommission_commission_id
	* Hibernate value: CommissionTypeCommission.commission.id
	*/
	String  COMMISSION_TYPE_COMMISSION_COMMISSION_ID = COMMISSION_TYPE_COMMISSION_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: CommissionTypeCommission_commission_name
	* Hibernate value: CommissionTypeCommission.commission.name
	*/
	String  COMMISSION_TYPE_COMMISSION_COMMISSION_NAME = COMMISSION_TYPE_COMMISSION_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for Expense entity.
	*/ 
	DAOConstantsEntry EXPENSE_ENTRY = DAOConstants.getDAOConstant(Expense.class);

	/** 
	* Alias value: Expense_description
	* Hibernate value: Expense.description
	*/
	String  EXPENSE_DESCRIPTION = EXPENSE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Expense_id
	* Hibernate value: Expense.id
	*/
	String  EXPENSE_ID = EXPENSE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Expense_unitPrice
	* Hibernate value: Expense.unitPrice
	*/
	String  EXPENSE_UNIT_PRICE = EXPENSE_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for ExpenseAccount entity.
	*/ 
	DAOConstantsEntry EXPENSE_ACCOUNT_ENTRY = DAOConstants.getDAOConstant(ExpenseAccount.class);

	/** 
	* Alias value: ExpenseAccount_comments
	* Hibernate value: ExpenseAccount.comments
	*/
	String  EXPENSE_ACCOUNT_COMMENTS = EXPENSE_ACCOUNT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ExpenseAccount_description
	* Hibernate value: ExpenseAccount.description
	*/
	String  EXPENSE_ACCOUNT_DESCRIPTION = EXPENSE_ACCOUNT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ExpenseAccount_id
	* Hibernate value: ExpenseAccount.id
	*/
	String  EXPENSE_ACCOUNT_ID = EXPENSE_ACCOUNT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ExpenseAccount_issueDate
	* Hibernate value: ExpenseAccount.issueDate
	*/
	String  EXPENSE_ACCOUNT_ISSUE_DATE = EXPENSE_ACCOUNT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ExpenseAccount_registry_id
	* Hibernate value: ExpenseAccount.registry.id
	*/
	String  EXPENSE_ACCOUNT_REGISTRY_ID = EXPENSE_ACCOUNT_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: ExpenseAccount_status
	* Hibernate value: ExpenseAccount.status
	*/
	String  EXPENSE_ACCOUNT_STATUS = EXPENSE_ACCOUNT_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: ExpenseAccount_type
	* Hibernate value: ExpenseAccount.type
	*/
	String  EXPENSE_ACCOUNT_TYPE = EXPENSE_ACCOUNT_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for ExpenseAccountDetail entity.
	*/ 
	DAOConstantsEntry EXPENSE_ACCOUNT_DETAIL_ENTRY = DAOConstants.getDAOConstant(ExpenseAccountDetail.class);

	/** 
	* Alias value: ExpenseAccountDetail_amount
	* Hibernate value: ExpenseAccountDetail.amount
	*/
	String  EXPENSE_ACCOUNT_DETAIL_AMOUNT = EXPENSE_ACCOUNT_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ExpenseAccountDetail_expenseAccount_id
	* Hibernate value: ExpenseAccountDetail.expenseAccount.id
	*/
	String  EXPENSE_ACCOUNT_DETAIL_EXPENSE_ACCOUNT_ID = EXPENSE_ACCOUNT_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ExpenseAccountDetail_expense_id
	* Hibernate value: ExpenseAccountDetail.expense.id
	*/
	String  EXPENSE_ACCOUNT_DETAIL_EXPENSE_ID = EXPENSE_ACCOUNT_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ExpenseAccountDetail_id
	* Hibernate value: ExpenseAccountDetail.id
	*/
	String  EXPENSE_ACCOUNT_DETAIL_ID = EXPENSE_ACCOUNT_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ExpenseAccountDetail_price
	* Hibernate value: ExpenseAccountDetail.price
	*/
	String  EXPENSE_ACCOUNT_DETAIL_PRICE = EXPENSE_ACCOUNT_DETAIL_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: ExpenseAccountDetail_quantity
	* Hibernate value: ExpenseAccountDetail.quantity
	*/
	String  EXPENSE_ACCOUNT_DETAIL_QUANTITY = EXPENSE_ACCOUNT_DETAIL_ENTRY.getAliasNames()[5];



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
	* Alias value: Offer_bankAccount
	* Hibernate value: Offer.bankAccount
	*/
	String  OFFER_BANK_ACCOUNT = OFFER_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Offer_bank_id
	* Hibernate value: Offer.bank.id
	*/
	String  OFFER_BANK_ID = OFFER_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Offer_comments
	* Hibernate value: Offer.comments
	*/
	String  OFFER_COMMENTS = OFFER_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Offer_daysBetweenPayments
	* Hibernate value: Offer.daysBetweenPayments
	*/
	String  OFFER_DAYS_BETWEEN_PAYMENTS = OFFER_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Offer_daysToFirstPayment
	* Hibernate value: Offer.daysToFirstPayment
	*/
	String  OFFER_DAYS_TO_FIRST_PAYMENT = OFFER_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Offer_discountExpression
	* Hibernate value: Offer.discountExpression
	*/
	String  OFFER_DISCOUNT_EXPRESSION = OFFER_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Offer_id
	* Hibernate value: Offer.id
	*/
	String  OFFER_ID = OFFER_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Offer_issueDate
	* Hibernate value: Offer.issueDate
	*/
	String  OFFER_ISSUE_DATE = OFFER_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Offer_number
	* Hibernate value: Offer.number
	*/
	String  OFFER_NUMBER = OFFER_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Offer_numberOfPayments
	* Hibernate value: Offer.numberOfPayments
	*/
	String  OFFER_NUMBER_OF_PAYMENTS = OFFER_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Offer_payMethod_id
	* Hibernate value: Offer.payMethod.id
	*/
	String  OFFER_PAY_METHOD_ID = OFFER_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Offer_paymentDays
	* Hibernate value: Offer.paymentDays
	*/
	String  OFFER_PAYMENT_DAYS = OFFER_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Offer_scope_id
	* Hibernate value: Offer.scope.id
	*/
	String  OFFER_SCOPE_ID = OFFER_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Offer_securityLevel
	* Hibernate value: Offer.securityLevel
	*/
	String  OFFER_SECURITY_LEVEL = OFFER_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Offer_seller_id
	* Hibernate value: Offer.seller.id
	*/
	String  OFFER_SELLER_ID = OFFER_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Offer_series
	* Hibernate value: Offer.series
	*/
	String  OFFER_SERIES = OFFER_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Offer_signed
	* Hibernate value: Offer.signed
	*/
	String  OFFER_SIGNED = OFFER_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Offer_status
	* Hibernate value: Offer.status
	*/
	String  OFFER_STATUS = OFFER_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: Offer_target_id
	* Hibernate value: Offer.target.id
	*/
	String  OFFER_TARGET_ID = OFFER_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: Offer_tariff_id
	* Hibernate value: Offer.tariff.id
	*/
	String  OFFER_TARIFF_ID = OFFER_ENTRY.getAliasNames()[20];

	/** 
	* Alias value: Offer_thirdParty_id
	* Hibernate value: Offer.thirdParty.id
	*/
	String  OFFER_THIRD_PARTY_ID = OFFER_ENTRY.getAliasNames()[21];

	/** 
	* Alias value: Offer_type
	* Hibernate value: Offer.type
	*/
	String  OFFER_TYPE = OFFER_ENTRY.getAliasNames()[22];

	/** 
	* Alias value: Offer_workPlace_id
	* Hibernate value: Offer.workPlace.id
	*/
	String  OFFER_WORK_PLACE_ID = OFFER_ENTRY.getAliasNames()[23];



	/** 
	* DAOConstantsEntry for OfferAttachment entity.
	*/ 
	DAOConstantsEntry OFFER_ATTACHMENT_ENTRY = DAOConstants.getDAOConstant(OfferAttachment.class);

	/** 
	* Alias value: OfferAttachment_data
	* Hibernate value: OfferAttachment.data
	*/
	String  OFFER_ATTACHMENT_DATA = OFFER_ATTACHMENT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: OfferAttachment_description
	* Hibernate value: OfferAttachment.description
	*/
	String  OFFER_ATTACHMENT_DESCRIPTION = OFFER_ATTACHMENT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: OfferAttachment_id
	* Hibernate value: OfferAttachment.id
	*/
	String  OFFER_ATTACHMENT_ID = OFFER_ATTACHMENT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: OfferAttachment_mimeType
	* Hibernate value: OfferAttachment.mimeType
	*/
	String  OFFER_ATTACHMENT_MIME_TYPE = OFFER_ATTACHMENT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: OfferAttachment_offer_id
	* Hibernate value: OfferAttachment.offer.id
	*/
	String  OFFER_ATTACHMENT_OFFER_ID = OFFER_ATTACHMENT_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: OfferAttachment_size
	* Hibernate value: OfferAttachment.size
	*/
	String  OFFER_ATTACHMENT_SIZE = OFFER_ATTACHMENT_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for OfferDetail entity.
	*/ 
	DAOConstantsEntry OFFER_DETAIL_ENTRY = DAOConstants.getDAOConstant(OfferDetail.class);

	/** 
	* Alias value: OfferDetail_id
	* Hibernate value: OfferDetail.id
	*/
	String  OFFER_DETAIL_ID = OFFER_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: OfferDetail_offer_id
	* Hibernate value: OfferDetail.offer.id
	*/
	String  OFFER_DETAIL_OFFER_ID = OFFER_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: OfferDetail_line
	* Hibernate value: OfferDetail.line
	*/
	String  OFFER_DETAIL_LINE = OFFER_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: OfferDetail_item_id
	* Hibernate value: OfferDetail.item.id
	*/
	String  OFFER_DETAIL_ITEM_ID = OFFER_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: OfferDetail_item_product_type
	* Hibernate value: OfferDetail.item.product.type
	*/
	String  OFFER_DETAIL_ITEM_PRODUCT_TYPE = OFFER_DETAIL_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: OfferDetail_description
	* Hibernate value: OfferDetail.description
	*/
	String  OFFER_DETAIL_DESCRIPTION = OFFER_DETAIL_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: OfferDetail_quantity
	* Hibernate value: OfferDetail.quantity
	*/
	String  OFFER_DETAIL_QUANTITY = OFFER_DETAIL_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: OfferDetail_price
	* Hibernate value: OfferDetail.price
	*/
	String  OFFER_DETAIL_PRICE = OFFER_DETAIL_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: OfferDetail_discountExpression
	* Hibernate value: OfferDetail.discountExpression
	*/
	String  OFFER_DETAIL_DISCOUNT_EXPRESSION = OFFER_DETAIL_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: OfferDetail_status
	* Hibernate value: OfferDetail.status
	*/
	String  OFFER_DETAIL_STATUS = OFFER_DETAIL_ENTRY.getAliasNames()[9];



	/** 
	* DAOConstantsEntry for OfferDetailCommission entity.
	*/ 
	DAOConstantsEntry OFFER_DETAIL_COMMISSION_ENTRY = DAOConstants.getDAOConstant(OfferDetailCommission.class);

	/** 
	* Alias value: OfferDetailCommission_amount
	* Hibernate value: OfferDetailCommission.amount
	*/
	String  OFFER_DETAIL_COMMISSION_AMOUNT = OFFER_DETAIL_COMMISSION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: OfferDetailCommission_commission
	* Hibernate value: OfferDetailCommission.commission
	*/
	String  OFFER_DETAIL_COMMISSION_COMMISSION = OFFER_DETAIL_COMMISSION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: OfferDetailCommission_id
	* Hibernate value: OfferDetailCommission.id
	*/
	String  OFFER_DETAIL_COMMISSION_ID = OFFER_DETAIL_COMMISSION_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: OfferDetailCommission_offerDetail_id
	* Hibernate value: OfferDetailCommission.offerDetail.id
	*/
	String  OFFER_DETAIL_COMMISSION_OFFER_DETAIL_ID = OFFER_DETAIL_COMMISSION_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: OfferDetailCommission_offerDetail_item_id
	* Hibernate value: OfferDetailCommission.offerDetail.item.id
	*/
	String  OFFER_DETAIL_COMMISSION_OFFER_DETAIL_ITEM_ID = OFFER_DETAIL_COMMISSION_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: OfferDetailCommission_offerDetail_item_product_type
	* Hibernate value: OfferDetailCommission.offerDetail.item.product.type
	*/
	String  OFFER_DETAIL_COMMISSION_OFFER_DETAIL_ITEM_PRODUCT_TYPE = OFFER_DETAIL_COMMISSION_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: OfferDetailCommission_offerDetail_description
	* Hibernate value: OfferDetailCommission.offerDetail.description
	*/
	String  OFFER_DETAIL_COMMISSION_OFFER_DETAIL_DESCRIPTION = OFFER_DETAIL_COMMISSION_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: OfferDetailCommission_offerDetail_status
	* Hibernate value: OfferDetailCommission.offerDetail.status
	*/
	String  OFFER_DETAIL_COMMISSION_OFFER_DETAIL_STATUS = OFFER_DETAIL_COMMISSION_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: OfferDetailCommission_offerDetail_offer_id
	* Hibernate value: OfferDetailCommission.offerDetail.offer.id
	*/
	String  OFFER_DETAIL_COMMISSION_OFFER_DETAIL_OFFER_ID = OFFER_DETAIL_COMMISSION_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: OfferDetailCommission_offerDetail_offer_issueDate
	* Hibernate value: OfferDetailCommission.offerDetail.offer.issueDate
	*/
	String  OFFER_DETAIL_COMMISSION_OFFER_DETAIL_OFFER_ISSUE_DATE = OFFER_DETAIL_COMMISSION_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: OfferDetailCommission_offerDetail_offer_number
	* Hibernate value: OfferDetailCommission.offerDetail.offer.number
	*/
	String  OFFER_DETAIL_COMMISSION_OFFER_DETAIL_OFFER_NUMBER = OFFER_DETAIL_COMMISSION_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: OfferDetailCommission_offerDetail_offer_scope_id
	* Hibernate value: OfferDetailCommission.offerDetail.offer.scope.id
	*/
	String  OFFER_DETAIL_COMMISSION_OFFER_DETAIL_OFFER_SCOPE_ID = OFFER_DETAIL_COMMISSION_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: OfferDetailCommission_offerDetail_offer_securityLevel
	* Hibernate value: OfferDetailCommission.offerDetail.offer.securityLevel
	*/
	String  OFFER_DETAIL_COMMISSION_OFFER_DETAIL_OFFER_SECURITY_LEVEL = OFFER_DETAIL_COMMISSION_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: OfferDetailCommission_offerDetail_offer_seller_id
	* Hibernate value: OfferDetailCommission.offerDetail.offer.seller.id
	*/
	String  OFFER_DETAIL_COMMISSION_OFFER_DETAIL_OFFER_SELLER_ID = OFFER_DETAIL_COMMISSION_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: OfferDetailCommission_offerDetail_offer_series
	* Hibernate value: OfferDetailCommission.offerDetail.offer.series
	*/
	String  OFFER_DETAIL_COMMISSION_OFFER_DETAIL_OFFER_SERIES = OFFER_DETAIL_COMMISSION_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: OfferDetailCommission_offerDetail_offer_status
	* Hibernate value: OfferDetailCommission.offerDetail.offer.status
	*/
	String  OFFER_DETAIL_COMMISSION_OFFER_DETAIL_OFFER_STATUS = OFFER_DETAIL_COMMISSION_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: OfferDetailCommission_offerDetail_offer_target_id
	* Hibernate value: OfferDetailCommission.offerDetail.offer.target.id
	*/
	String  OFFER_DETAIL_COMMISSION_OFFER_DETAIL_OFFER_TARGET_ID = OFFER_DETAIL_COMMISSION_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: OfferDetailCommission_offerDetail_offer_thirdParty_id
	* Hibernate value: OfferDetailCommission.offerDetail.offer.thirdParty.id
	*/
	String  OFFER_DETAIL_COMMISSION_OFFER_DETAIL_OFFER_THIRD_PARTY_ID = OFFER_DETAIL_COMMISSION_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: OfferDetailCommission_offerDetail_offer_type
	* Hibernate value: OfferDetailCommission.offerDetail.offer.type
	*/
	String  OFFER_DETAIL_COMMISSION_OFFER_DETAIL_OFFER_TYPE = OFFER_DETAIL_COMMISSION_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: OfferDetailCommission_offerDetail_offer_signed
	* Hibernate value: OfferDetailCommission.offerDetail.offer.signed
	*/
	String  OFFER_DETAIL_COMMISSION_OFFER_DETAIL_OFFER_SIGNED = OFFER_DETAIL_COMMISSION_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: OfferDetailCommission_offerDetail_offer_workplace_id
	* Hibernate value: OfferDetailCommission.offerDetail.offer.workplace.id
	*/
	String  OFFER_DETAIL_COMMISSION_OFFER_DETAIL_OFFER_WORKPLACE_ID = OFFER_DETAIL_COMMISSION_ENTRY.getAliasNames()[20];

	/** 
	* Alias value: OfferDetailCommission_payDate
	* Hibernate value: OfferDetailCommission.payDate
	*/
	String  OFFER_DETAIL_COMMISSION_PAY_DATE = OFFER_DETAIL_COMMISSION_ENTRY.getAliasNames()[21];

	/** 
	* Alias value: OfferDetailCommission_status
	* Hibernate value: OfferDetailCommission.status
	*/
	String  OFFER_DETAIL_COMMISSION_STATUS = OFFER_DETAIL_COMMISSION_ENTRY.getAliasNames()[22];



	/** 
	* DAOConstantsEntry for OfferTerm entity.
	*/ 
	DAOConstantsEntry OFFER_TERM_ENTRY = DAOConstants.getDAOConstant(OfferTerm.class);

	/** 
	* Alias value: OfferTerm_description
	* Hibernate value: OfferTerm.description
	*/
	String  OFFER_TERM_DESCRIPTION = OFFER_TERM_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: OfferTerm_general
	* Hibernate value: OfferTerm.general
	*/
	String  OFFER_TERM_GENERAL = OFFER_TERM_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: OfferTerm_id
	* Hibernate value: OfferTerm.id
	*/
	String  OFFER_TERM_ID = OFFER_TERM_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: OfferTerm_line
	* Hibernate value: OfferTerm.line
	*/
	String  OFFER_TERM_LINE = OFFER_TERM_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: OfferTerm_name
	* Hibernate value: OfferTerm.name
	*/
	String  OFFER_TERM_NAME = OFFER_TERM_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: OfferTerm_offer_id
	* Hibernate value: OfferTerm.offer.id
	*/
	String  OFFER_TERM_OFFER_ID = OFFER_TERM_ENTRY.getAliasNames()[5];



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
	* Alias value: Target_advertising
	* Hibernate value: Target.advertising
	*/
	String  TARGET_ADVERTISING = TARGET_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Target_surcharge
	* Hibernate value: Target.surcharge
	*/
	String  TARGET_SURCHARGE = TARGET_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Target_withholding
	* Hibernate value: Target.withholding
	*/
	String  TARGET_WITHHOLDING = TARGET_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Target_transaction
	* Hibernate value: Target.transaction
	*/
	String  TARGET_TRANSACTION = TARGET_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Target_status
	* Hibernate value: Target.status
	*/
	String  TARGET_STATUS = TARGET_ENTRY.getAliasNames()[10];



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



	/** 
	* DAOConstantsEntry for TargetThirdParty entity.
	*/ 
	DAOConstantsEntry TARGET_THIRD_PARTY_ENTRY = DAOConstants.getDAOConstant(TargetThirdParty.class);

	/** 
	* Alias value: TargetThirdParty_id
	* Hibernate value: TargetThirdParty.id
	*/
	String  TARGET_THIRD_PARTY_ID = TARGET_THIRD_PARTY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: TargetThirdParty_target_id
	* Hibernate value: TargetThirdParty.target.id
	*/
	String  TARGET_THIRD_PARTY_TARGET_ID = TARGET_THIRD_PARTY_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: TargetThirdParty_thirdParty_id
	* Hibernate value: TargetThirdParty.thirdParty.id
	*/
	String  TARGET_THIRD_PARTY_THIRD_PARTY_ID = TARGET_THIRD_PARTY_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: TargetThirdParty_thirdParty_registry_name
	* Hibernate value: TargetThirdParty.thirdParty.registry.name
	*/
	String  TARGET_THIRD_PARTY_THIRD_PARTY_REGISTRY_NAME = TARGET_THIRD_PARTY_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: TargetThirdParty_thirdParty_registry_surname
	* Hibernate value: TargetThirdParty.thirdParty.registry.surname
	*/
	String  TARGET_THIRD_PARTY_THIRD_PARTY_REGISTRY_SURNAME = TARGET_THIRD_PARTY_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: TargetThirdParty_targetExternalCode
	* Hibernate value: TargetThirdParty.targetExternalCode
	*/
	String  TARGET_THIRD_PARTY_TARGET_EXTERNAL_CODE = TARGET_THIRD_PARTY_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: TargetThirdParty_tariff_id
	* Hibernate value: TargetThirdParty.tariff.id
	*/
	String  TARGET_THIRD_PARTY_TARIFF_ID = TARGET_THIRD_PARTY_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: TargetThirdParty_payMethod_id
	* Hibernate value: TargetThirdParty.payMethod.id
	*/
	String  TARGET_THIRD_PARTY_PAY_METHOD_ID = TARGET_THIRD_PARTY_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: TargetThirdParty_numberOfPayments
	* Hibernate value: TargetThirdParty.numberOfPayments
	*/
	String  TARGET_THIRD_PARTY_NUMBER_OF_PAYMENTS = TARGET_THIRD_PARTY_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: TargetThirdParty_daysToFirstPayment
	* Hibernate value: TargetThirdParty.daysToFirstPayment
	*/
	String  TARGET_THIRD_PARTY_DAYS_TO_FIRST_PAYMENT = TARGET_THIRD_PARTY_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: TargetThirdParty_daysBetweenPayments
	* Hibernate value: TargetThirdParty.daysBetweenPayments
	*/
	String  TARGET_THIRD_PARTY_DAYS_BETWEEN_PAYMENTS = TARGET_THIRD_PARTY_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: TargetThirdParty_paymentDays
	* Hibernate value: TargetThirdParty.paymentDays
	*/
	String  TARGET_THIRD_PARTY_PAYMENT_DAYS = TARGET_THIRD_PARTY_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: TargetThirdParty_bank_id
	* Hibernate value: TargetThirdParty.bank.id
	*/
	String  TARGET_THIRD_PARTY_BANK_ID = TARGET_THIRD_PARTY_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: TargetThirdParty_bankAccount
	* Hibernate value: TargetThirdParty.bankAccount
	*/
	String  TARGET_THIRD_PARTY_BANK_ACCOUNT = TARGET_THIRD_PARTY_ENTRY.getAliasNames()[13];


}