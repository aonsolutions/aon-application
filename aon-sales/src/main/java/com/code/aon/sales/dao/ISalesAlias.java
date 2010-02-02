package com.code.aon.sales.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.sales.CustomerFee;
import com.code.aon.sales.FinanceSales;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesAttachment;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.SalesType;
import com.code.aon.sales.Seller;
import com.code.aon.sales.PointOfSale;

/** 
* Interface for holding entity properties constants.
*/ 
public interface ISalesAlias {



	/** 
	* DAOConstantsEntry for CustomerFee entity.
	*/ 
	DAOConstantsEntry CUSTOMER_FEE_ENTRY = DAOConstants.getDAOConstant(CustomerFee.class);

	/** 
	* Alias value: CustomerFee_id
	* Hibernate value: CustomerFee.id
	*/
	String  CUSTOMER_FEE_ID = CUSTOMER_FEE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: CustomerFee_customer_id
	* Hibernate value: CustomerFee.customer.id
	*/
	String  CUSTOMER_FEE_CUSTOMER_ID = CUSTOMER_FEE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: CustomerFee_customer_registry_name
	* Hibernate value: CustomerFee.customer.registry.name
	*/
	String  CUSTOMER_FEE_CUSTOMER_REGISTRY_NAME = CUSTOMER_FEE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: CustomerFee_customer_registry_surname
	* Hibernate value: CustomerFee.customer.registry.surname
	*/
	String  CUSTOMER_FEE_CUSTOMER_REGISTRY_SURNAME = CUSTOMER_FEE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: CustomerFee_customer_status
	* Hibernate value: CustomerFee.customer.status
	*/
	String  CUSTOMER_FEE_CUSTOMER_STATUS = CUSTOMER_FEE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: CustomerFee_item_id
	* Hibernate value: CustomerFee.item.id
	*/
	String  CUSTOMER_FEE_ITEM_ID = CUSTOMER_FEE_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: CustomerFee_description
	* Hibernate value: CustomerFee.description
	*/
	String  CUSTOMER_FEE_DESCRIPTION = CUSTOMER_FEE_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: CustomerFee_quantity
	* Hibernate value: CustomerFee.quantity
	*/
	String  CUSTOMER_FEE_QUANTITY = CUSTOMER_FEE_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: CustomerFee_price
	* Hibernate value: CustomerFee.price
	*/
	String  CUSTOMER_FEE_PRICE = CUSTOMER_FEE_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: CustomerFee_discountExpression
	* Hibernate value: CustomerFee.discountExpression
	*/
	String  CUSTOMER_FEE_DISCOUNT_EXPRESSION = CUSTOMER_FEE_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: CustomerFee_initialDate
	* Hibernate value: CustomerFee.initialDate
	*/
	String  CUSTOMER_FEE_INITIAL_DATE = CUSTOMER_FEE_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: CustomerFee_finalDate
	* Hibernate value: CustomerFee.finalDate
	*/
	String  CUSTOMER_FEE_FINAL_DATE = CUSTOMER_FEE_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: CustomerFee_billingDate
	* Hibernate value: CustomerFee.billingDate
	*/
	String  CUSTOMER_FEE_BILLING_DATE = CUSTOMER_FEE_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: CustomerFee_period
	* Hibernate value: CustomerFee.period
	*/
	String  CUSTOMER_FEE_PERIOD = CUSTOMER_FEE_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: CustomerFee_securityLevel
	* Hibernate value: CustomerFee.securityLevel
	*/
	String  CUSTOMER_FEE_SECURITY_LEVEL = CUSTOMER_FEE_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: CustomerFee_workPlace_id
	* Hibernate value: CustomerFee.workPlace.id
	*/
	String  CUSTOMER_FEE_WORK_PLACE_ID = CUSTOMER_FEE_ENTRY.getAliasNames()[15];



	/** 
	* DAOConstantsEntry for FinanceSales entity.
	*/ 
	DAOConstantsEntry FINANCE_SALES_ENTRY = DAOConstants.getDAOConstant(FinanceSales.class);

	/** 
	* Alias value: FinanceSales_finance_id
	* Hibernate value: FinanceSales.finance.id
	*/
	String  FINANCE_SALES_FINANCE_ID = FINANCE_SALES_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: FinanceSales_id
	* Hibernate value: FinanceSales.id
	*/
	String  FINANCE_SALES_ID = FINANCE_SALES_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: FinanceSales_sales_id
	* Hibernate value: FinanceSales.sales.id
	*/
	String  FINANCE_SALES_SALES_ID = FINANCE_SALES_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for Sales entity.
	*/ 
	DAOConstantsEntry SALES_ENTRY = DAOConstants.getDAOConstant(Sales.class);

	/** 
	* Alias value: Sales_customer_id
	* Hibernate value: Sales.customer.id
	*/
	String  SALES_CUSTOMER_ID = SALES_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Sales_discountExpression
	* Hibernate value: Sales.discountExpression
	*/
	String  SALES_DISCOUNT_EXPRESSION = SALES_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Sales_documentType
	* Hibernate value: Sales.documentType
	*/
	String  SALES_DOCUMENT_TYPE = SALES_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Sales_id
	* Hibernate value: Sales.id
	*/
	String  SALES_ID = SALES_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Sales_issueDate
	* Hibernate value: Sales.issueDate
	*/
	String  SALES_ISSUE_DATE = SALES_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Sales_number
	* Hibernate value: Sales.number
	*/
	String  SALES_NUMBER = SALES_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Sales_payMethod_id
	* Hibernate value: Sales.payMethod.id
	*/
	String  SALES_PAY_METHOD_ID = SALES_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Sales_pos_id
	* Hibernate value: Sales.pos.id
	*/
	String  SALES_POS_ID = SALES_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Sales_securityLevel
	* Hibernate value: Sales.securityLevel
	*/
	String  SALES_SECURITY_LEVEL = SALES_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Sales_seller_id
	* Hibernate value: Sales.seller.id
	*/
	String  SALES_SELLER_ID = SALES_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Sales_series
	* Hibernate value: Sales.series
	*/
	String  SALES_SERIES = SALES_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Sales_shippingAddress_id
	* Hibernate value: Sales.shippingAddress.id
	*/
	String  SALES_SHIPPING_ADDRESS_ID = SALES_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Sales_status
	* Hibernate value: Sales.status
	*/
	String  SALES_STATUS = SALES_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Sales_workPlace_id
	* Hibernate value: Sales.workPlace.id
	*/
	String  SALES_WORK_PLACE_ID = SALES_ENTRY.getAliasNames()[13];



	/** 
	* DAOConstantsEntry for SalesAttachment entity.
	*/ 
	DAOConstantsEntry SALES_ATTACHMENT_ENTRY = DAOConstants.getDAOConstant(SalesAttachment.class);

	/** 
	* Alias value: SalesAttachment_data
	* Hibernate value: SalesAttachment.data
	*/
	String  SALES_ATTACHMENT_DATA = SALES_ATTACHMENT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SalesAttachment_id
	* Hibernate value: SalesAttachment.id
	*/
	String  SALES_ATTACHMENT_ID = SALES_ATTACHMENT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: SalesAttachment_mimeType
	* Hibernate value: SalesAttachment.mimeType
	*/
	String  SALES_ATTACHMENT_MIME_TYPE = SALES_ATTACHMENT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: SalesAttachment_sales_id
	* Hibernate value: SalesAttachment.sales.id
	*/
	String  SALES_ATTACHMENT_SALES_ID = SALES_ATTACHMENT_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for SalesDetail entity.
	*/ 
	DAOConstantsEntry SALES_DETAIL_ENTRY = DAOConstants.getDAOConstant(SalesDetail.class);

	/** 
	* Alias value: SalesDetail_description
	* Hibernate value: SalesDetail.description
	*/
	String  SALES_DETAIL_DESCRIPTION = SALES_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SalesDetail_discountExpression
	* Hibernate value: SalesDetail.discountExpression
	*/
	String  SALES_DETAIL_DISCOUNT_EXPRESSION = SALES_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: SalesDetail_id
	* Hibernate value: SalesDetail.id
	*/
	String  SALES_DETAIL_ID = SALES_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: SalesDetail_item_id
	* Hibernate value: SalesDetail.item.id
	*/
	String  SALES_DETAIL_ITEM_ID = SALES_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: SalesDetail_line
	* Hibernate value: SalesDetail.line
	*/
	String  SALES_DETAIL_LINE = SALES_DETAIL_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: SalesDetail_price
	* Hibernate value: SalesDetail.price
	*/
	String  SALES_DETAIL_PRICE = SALES_DETAIL_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: SalesDetail_quantity
	* Hibernate value: SalesDetail.quantity
	*/
	String  SALES_DETAIL_QUANTITY = SALES_DETAIL_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: SalesDetail_salesDetailStatus
	* Hibernate value: SalesDetail.salesDetailStatus
	*/
	String  SALES_DETAIL_SALES_DETAIL_STATUS = SALES_DETAIL_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: SalesDetail_sales_id
	* Hibernate value: SalesDetail.sales.id
	*/
	String  SALES_DETAIL_SALES_ID = SALES_DETAIL_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: SalesDetail_taxes
	* Hibernate value: SalesDetail.taxes
	*/
	String  SALES_DETAIL_TAXES = SALES_DETAIL_ENTRY.getAliasNames()[9];



	/** 
	* DAOConstantsEntry for SalesType entity.
	*/ 
	DAOConstantsEntry SALES_TYPE_ENTRY = DAOConstants.getDAOConstant(SalesType.class);

	/** 
	* Alias value: SalesType_description
	* Hibernate value: SalesType.description
	*/
	String  SALES_TYPE_DESCRIPTION = SALES_TYPE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SalesType_id
	* Hibernate value: SalesType.id
	*/
	String  SALES_TYPE_ID = SALES_TYPE_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for Seller entity.
	*/ 
	DAOConstantsEntry SELLER_ENTRY = DAOConstants.getDAOConstant(Seller.class);

	/** 
	* Alias value: Seller_description
	* Hibernate value: Seller.description
	*/
	String  SELLER_DESCRIPTION = SELLER_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Seller_id
	* Hibernate value: Seller.id
	*/
	String  SELLER_ID = SELLER_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Seller_registry_id
	* Hibernate value: Seller.registry.id
	*/
	String  SELLER_REGISTRY_ID = SELLER_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for PointOfSale entity.
	*/ 
	DAOConstantsEntry POINT_OF_SALE_ENTRY = DAOConstants.getDAOConstant(PointOfSale.class);

	/** 
	* Alias value: PointOfSale_RAddress_id
	* Hibernate value: PointOfSale.RAddress.id
	*/
	String  POINT_OF_SALE_RADDRESS_ID = POINT_OF_SALE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: PointOfSale_description
	* Hibernate value: PointOfSale.description
	*/
	String  POINT_OF_SALE_DESCRIPTION = POINT_OF_SALE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: PointOfSale_id
	* Hibernate value: PointOfSale.id
	*/
	String  POINT_OF_SALE_ID = POINT_OF_SALE_ENTRY.getAliasNames()[2];


}