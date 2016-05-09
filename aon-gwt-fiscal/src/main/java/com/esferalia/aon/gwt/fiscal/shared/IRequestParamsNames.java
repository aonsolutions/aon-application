package com.esferalia.aon.gwt.fiscal.shared;


public interface IRequestParamsNames {

	String DOMAIN_ID = "domainId";
	String DOMAIN_NAME = "domainName";

	String FROM_DATE = "fromDate";
	String TO_DATE = "toDate";
	
	String INVOICE_TYPE_SALES = "invoiceTypeSales";
	String INVOICE_TYPE_PURCHASES = "invoiceTypePurchases";
	String INVOICE_TYPE_EXPENSES = "invoiceTypeExpenses";
	String INVOICE_TYPE_UNDEDUCTIBLE = "invoiceTypeUndeductible";
	
	String OFFER_STATUS_PENDING = "offerStatusPending";
	String OFFER_STATUS_APPROVED = "offerStatusApproved";
	String OFFER_STATUS_REFUSED = "offerStatusRefused";
	String OFFER_STATUS_BLOCKED = "offerStatusBlocked";
	String OFFER_STATUS_INVOICED = "offerStatusInvoiced";
	
	String INVOICE_TYPES = "invoiceTypes";
	String CATEGORY_IDS = "categoryIds";
	String WORKPLACE_IDS = "workplaceIds";
	String SELLER_IDS = "sellerIds";

}
