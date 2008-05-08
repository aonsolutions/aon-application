package com.code.aon.purchase.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.Supplier;
import com.code.aon.purchase.SupplierSegment;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IPurchaseAlias {



	/** 
	* DAOConstantsEntry for PurchaseDetail entity.
	*/ 
	DAOConstantsEntry PURCHASE_DETAIL_ENTRY = DAOConstants.getDAOConstant(PurchaseDetail.class);

	/** 
	* Alias value: PurchaseDetail_description
	* Hibernate value: PurchaseDetail.description
	*/
	String  PURCHASE_DETAIL_DESCRIPTION = PURCHASE_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: PurchaseDetail_discountExpression
	* Hibernate value: PurchaseDetail.discountExpression
	*/
	String  PURCHASE_DETAIL_DISCOUNT_EXPRESSION = PURCHASE_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: PurchaseDetail_id
	* Hibernate value: PurchaseDetail.id
	*/
	String  PURCHASE_DETAIL_ID = PURCHASE_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: PurchaseDetail_item_id
	* Hibernate value: PurchaseDetail.item.id
	*/
	String  PURCHASE_DETAIL_ITEM_ID = PURCHASE_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: PurchaseDetail_line
	* Hibernate value: PurchaseDetail.line
	*/
	String  PURCHASE_DETAIL_LINE = PURCHASE_DETAIL_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: PurchaseDetail_price
	* Hibernate value: PurchaseDetail.price
	*/
	String  PURCHASE_DETAIL_PRICE = PURCHASE_DETAIL_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: PurchaseDetail_purchase_id
	* Hibernate value: PurchaseDetail.purchase.id
	*/
	String  PURCHASE_DETAIL_PURCHASE_ID = PURCHASE_DETAIL_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: PurchaseDetail_quantity
	* Hibernate value: PurchaseDetail.quantity
	*/
	String  PURCHASE_DETAIL_QUANTITY = PURCHASE_DETAIL_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: PurchaseDetail_status
	* Hibernate value: PurchaseDetail.status
	*/
	String  PURCHASE_DETAIL_STATUS = PURCHASE_DETAIL_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: PurchaseDetail_taxes
	* Hibernate value: PurchaseDetail.taxes
	*/
	String  PURCHASE_DETAIL_TAXES = PURCHASE_DETAIL_ENTRY.getAliasNames()[9];



	/** 
	* DAOConstantsEntry for Purchase entity.
	*/ 
	DAOConstantsEntry PURCHASE_ENTRY = DAOConstants.getDAOConstant(Purchase.class);

	/** 
	* Alias value: Purchase_id
	* Hibernate value: Purchase.id
	*/
	String  PURCHASE_ID = PURCHASE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Purchase_documentType
	* Hibernate value: Purchase.documentType
	*/
	String  PURCHASE_DOCUMENT_TYPE = PURCHASE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Purchase_series
	* Hibernate value: Purchase.series
	*/
	String  PURCHASE_SERIES = PURCHASE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Purchase_number
	* Hibernate value: Purchase.number
	*/
	String  PURCHASE_NUMBER = PURCHASE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Purchase_supplier_id
	* Hibernate value: Purchase.supplier.id
	*/
	String  PURCHASE_SUPPLIER_ID = PURCHASE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Purchase_discountExpression
	* Hibernate value: Purchase.discountExpression
	*/
	String  PURCHASE_DISCOUNT_EXPRESSION = PURCHASE_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Purchase_issueDate
	* Hibernate value: Purchase.issueDate
	*/
	String  PURCHASE_ISSUE_DATE = PURCHASE_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Purchase_payMethod_id
	* Hibernate value: Purchase.payMethod.id
	*/
	String  PURCHASE_PAY_METHOD_ID = PURCHASE_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Purchase_securityLevel
	* Hibernate value: Purchase.securityLevel
	*/
	String  PURCHASE_SECURITY_LEVEL = PURCHASE_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Purchase_status
	* Hibernate value: Purchase.status
	*/
	String  PURCHASE_STATUS = PURCHASE_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Purchase_supplier_name
	* Hibernate value: Purchase.supplier.name
	*/
	String  PURCHASE_SUPPLIER_NAME = PURCHASE_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Purchase_withholding
	* Hibernate value: Purchase.withholding
	*/
	String  PURCHASE_WITHHOLDING = PURCHASE_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Purchase_supplierSegment_id
	* Hibernate value: Purchase.supplierSegment.id
	*/
	String  PURCHASE_SUPPLIER_SEGMENT_ID = PURCHASE_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Purchase_workPlace_id
	* Hibernate value: Purchase.workPlace.id
	*/
	String  PURCHASE_WORK_PLACE_ID = PURCHASE_ENTRY.getAliasNames()[13];



	/** 
	* DAOConstantsEntry for Supplier entity.
	*/ 
	DAOConstantsEntry SUPPLIER_ENTRY = DAOConstants.getDAOConstant(Supplier.class);

	/** 
	* Alias value: Supplier_alias
	* Hibernate value: Supplier.alias
	*/
	String  SUPPLIER_ALIAS = SUPPLIER_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Supplier_document
	* Hibernate value: Supplier.document
	*/
	String  SUPPLIER_DOCUMENT = SUPPLIER_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Supplier_id
	* Hibernate value: Supplier.id
	*/
	String  SUPPLIER_ID = SUPPLIER_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Supplier_name
	* Hibernate value: Supplier.name
	*/
	String  SUPPLIER_NAME = SUPPLIER_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Supplier_surname
	* Hibernate value: Supplier.surname
	*/
	String  SUPPLIER_SURNAME = SUPPLIER_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Supplier_type
	* Hibernate value: Supplier.type
	*/
	String  SUPPLIER_TYPE = SUPPLIER_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Supplier_scope_id
	* Hibernate value: Supplier.scope.id
	*/
	String  SUPPLIER_SCOPE_ID = SUPPLIER_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Supplier_status
	* Hibernate value: Supplier.status
	*/
	String  SUPPLIER_STATUS = SUPPLIER_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Supplier_supplierSegment_id
	* Hibernate value: Supplier.supplierSegment.id
	*/
	String  SUPPLIER_SUPPLIER_SEGMENT_ID = SUPPLIER_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Supplier_withholding
	* Hibernate value: Supplier.withholding
	*/
	String  SUPPLIER_WITHHOLDING = SUPPLIER_ENTRY.getAliasNames()[9];



	/** 
	* DAOConstantsEntry for SupplierSegment entity.
	*/ 
	DAOConstantsEntry SUPPLIER_SEGMENT_ENTRY = DAOConstants.getDAOConstant(SupplierSegment.class);

	/** 
	* Alias value: SupplierSegment_description
	* Hibernate value: SupplierSegment.description
	*/
	String  SUPPLIER_SEGMENT_DESCRIPTION = SUPPLIER_SEGMENT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SupplierSegment_id
	* Hibernate value: SupplierSegment.id
	*/
	String  SUPPLIER_SEGMENT_ID = SUPPLIER_SEGMENT_ENTRY.getAliasNames()[1];


}