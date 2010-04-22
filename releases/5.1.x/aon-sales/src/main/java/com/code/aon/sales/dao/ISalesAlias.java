package com.code.aon.sales.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;

/** 
* Interface for holding entity properties constants.
*/ 
public interface ISalesAlias {



	/** 
	* DAOConstantsEntry for Sales entity.
	*/ 
	DAOConstantsEntry SALES_ENTRY = DAOConstants.getDAOConstant(Sales.class);

	/** 
	* Alias value: Sales_bankAccount
	* Hibernate value: Sales.bankAccount
	*/
	String  SALES_BANK_ACCOUNT = SALES_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Sales_bank_id
	* Hibernate value: Sales.bank.id
	*/
	String  SALES_BANK_ID = SALES_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Sales_customer_id
	* Hibernate value: Sales.customer.id
	*/
	String  SALES_CUSTOMER_ID = SALES_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Sales_daysBetweenPayments
	* Hibernate value: Sales.daysBetweenPayments
	*/
	String  SALES_DAYS_BETWEEN_PAYMENTS = SALES_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Sales_daysToFirstPayment
	* Hibernate value: Sales.daysToFirstPayment
	*/
	String  SALES_DAYS_TO_FIRST_PAYMENT = SALES_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Sales_discountExpression
	* Hibernate value: Sales.discountExpression
	*/
	String  SALES_DISCOUNT_EXPRESSION = SALES_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Sales_documentType
	* Hibernate value: Sales.documentType
	*/
	String  SALES_DOCUMENT_TYPE = SALES_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Sales_id
	* Hibernate value: Sales.id
	*/
	String  SALES_ID = SALES_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Sales_issueDate
	* Hibernate value: Sales.issueDate
	*/
	String  SALES_ISSUE_DATE = SALES_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Sales_number
	* Hibernate value: Sales.number
	*/
	String  SALES_NUMBER = SALES_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Sales_numberOfPayments
	* Hibernate value: Sales.numberOfPayments
	*/
	String  SALES_NUMBER_OF_PAYMENTS = SALES_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Sales_payMethod_id
	* Hibernate value: Sales.payMethod.id
	*/
	String  SALES_PAY_METHOD_ID = SALES_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Sales_paymentDays
	* Hibernate value: Sales.paymentDays
	*/
	String  SALES_PAYMENT_DAYS = SALES_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Sales_scope_id
	* Hibernate value: Sales.scope.id
	*/
	String  SALES_SCOPE_ID = SALES_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Sales_securityLevel
	* Hibernate value: Sales.securityLevel
	*/
	String  SALES_SECURITY_LEVEL = SALES_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Sales_seller_id
	* Hibernate value: Sales.seller.id
	*/
	String  SALES_SELLER_ID = SALES_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Sales_series
	* Hibernate value: Sales.series
	*/
	String  SALES_SERIES = SALES_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Sales_shippingAddress_id
	* Hibernate value: Sales.shippingAddress.id
	*/
	String  SALES_SHIPPING_ADDRESS_ID = SALES_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Sales_status
	* Hibernate value: Sales.status
	*/
	String  SALES_STATUS = SALES_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: Sales_workPlace_id
	* Hibernate value: Sales.workPlace.id
	*/
	String  SALES_WORK_PLACE_ID = SALES_ENTRY.getAliasNames()[19];



	/** 
	* DAOConstantsEntry for SalesDetail entity.
	*/ 
	DAOConstantsEntry SALES_DETAIL_ENTRY = DAOConstants.getDAOConstant(SalesDetail.class);

	/** 
	* Alias value: SalesDetail_delivered
	* Hibernate value: SalesDetail.delivered
	*/
	String  SALES_DETAIL_DELIVERED = SALES_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SalesDetail_description
	* Hibernate value: SalesDetail.description
	*/
	String  SALES_DETAIL_DESCRIPTION = SALES_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: SalesDetail_discountExpression
	* Hibernate value: SalesDetail.discountExpression
	*/
	String  SALES_DETAIL_DISCOUNT_EXPRESSION = SALES_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: SalesDetail_id
	* Hibernate value: SalesDetail.id
	*/
	String  SALES_DETAIL_ID = SALES_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: SalesDetail_item_id
	* Hibernate value: SalesDetail.item.id
	*/
	String  SALES_DETAIL_ITEM_ID = SALES_DETAIL_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: SalesDetail_line
	* Hibernate value: SalesDetail.line
	*/
	String  SALES_DETAIL_LINE = SALES_DETAIL_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: SalesDetail_offerDetail_id
	* Hibernate value: SalesDetail.offerDetail.id
	*/
	String  SALES_DETAIL_OFFER_DETAIL_ID = SALES_DETAIL_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: SalesDetail_price
	* Hibernate value: SalesDetail.price
	*/
	String  SALES_DETAIL_PRICE = SALES_DETAIL_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: SalesDetail_quantity
	* Hibernate value: SalesDetail.quantity
	*/
	String  SALES_DETAIL_QUANTITY = SALES_DETAIL_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: SalesDetail_sales_id
	* Hibernate value: SalesDetail.sales.id
	*/
	String  SALES_DETAIL_SALES_ID = SALES_DETAIL_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: SalesDetail_source
	* Hibernate value: SalesDetail.source
	*/
	String  SALES_DETAIL_SOURCE = SALES_DETAIL_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: SalesDetail_status
	* Hibernate value: SalesDetail.status
	*/
	String  SALES_DETAIL_STATUS = SALES_DETAIL_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: SalesDetail_taxes
	* Hibernate value: SalesDetail.taxes
	*/
	String  SALES_DETAIL_TAXES = SALES_DETAIL_ENTRY.getAliasNames()[12];


}