package com.esferalia.aon.gwt.fiscal.shared;


public interface IRequestParamsNames {

	String DOMAIN = "domain";
	String REGISTRY = "registry";
	String ACTIVITY = "activity";
	String FROM_DATE = "fromDate";
	String TO_DATE = "toDate";
	String PERCENT = "percent";
	String TYPE = "type";
	String OUTPUT = "output";
	String SURCHARGE = "surcharge";
	String FARMER_REGIME = "farmerRegime";
	String ACCRUAL_REGIME = "accrualRegime";
	String INVESTMENT = "investment";
	String SERVICE = "service";
	
	String DOMAIN_ID = "domainId";
	String DOMAIN_NAME = "domainName";

	String INVOICE_TYPE_SALES = "invoiceTypeSales";
	String INVOICE_TYPE_PURCHASES = "invoiceTypePurchases";
	String INVOICE_TYPE_EXPENSES = "invoiceTypeExpenses";
	String INVOICE_TYPE_UNDEDUCTIBLE = "invoiceTypeUndeductible";
	
	String OFFER_STATUS_PENDING = "offerStatusPending";
	String OFFER_STATUS_APPROVED = "offerStatusApproved";
	String OFFER_STATUS_REFUSED = "offerStatusRefused";
	String OFFER_STATUS_BLOCKED = "offerStatusBlocked";
	String OFFER_STATUS_INVOICED = "offerStatusInvoiced";
	
	String ORDER_STATUS_PENDING = "orderStatusPending";
	String ORDER_STATUS_BLOCKED = "orderStatusBlocked";
	String ORDER_STATUS_SERVED = "orderStatusServed";
	String ORDER_STATUS_CLOSED = "orderStatusClosed";
	String ORDER_STATUS_INVOICED = "orderStatusInvoiced";
	
	
	String INVOICE_TYPES = "invoiceTypes";
	String CATEGORY_IDS = "categoryIds";
	String WORKPLACE_IDS = "workplaceIds";
	String SELLER_IDS = "sellerIds";

}
