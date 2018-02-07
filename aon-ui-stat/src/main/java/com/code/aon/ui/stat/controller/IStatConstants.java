package com.code.aon.ui.stat.controller;

public interface IStatConstants {

	String EMPTY_STRING = "";
	String FORM_SUFFIX = "_form";
	
	// ************************************************************
	// BEAN
	// ************************************************************
	String COMPANY_COLLECTIONS_CONTROLLER_NAME = "companyCollections";
	String DELIVERY_CONTROLLER_NAME = "delivery";
	String EXPENSE_INVOICE_CONTROLLER_NAME = "expenseInvoice";
	String INCOME_CONTROLLER_NAME = "income";
	String OFFER_CONTROLLER_NAME = "offer";
	String PROJECT_TAS_CONTROLLER_NAME = "projectTas";
	String PURCHASE_CONTROLLER_NAME = "purchase";
	String PURCHASE_INVOICE_CONTROLLER_NAME = "purchaseInvoice";
	String SALES_CONTROLLER_NAME = "sales";
	String SALES_INVOICE_CONTROLLER_NAME = "saleInvoice";
	String STAT_CONTROLLER_NAME = "stat";
	String TAS_STAT_CONTROLLER_NAME = "tasStat";
	String COMMERCIAL_TRACKING_CONTROLLER_NAME = "commercialTracking";
	String UNDEDUCTIBLE_INVOICE_CONTROLLER_NAME = "undeductibleInvoice";

	// ************************************************************
	// NAVIGATION
	// ************************************************************
	String DELIVERY_FORM = DELIVERY_CONTROLLER_NAME + FORM_SUFFIX;
	String EXPENSE_INVOICE_FORM = EXPENSE_INVOICE_CONTROLLER_NAME + FORM_SUFFIX;
	String INCOME_FORM = INCOME_CONTROLLER_NAME + FORM_SUFFIX;
	String OFFER_FORM = OFFER_CONTROLLER_NAME + FORM_SUFFIX;
	String PRODUCT_STATS_ACTION = "product_stats";
	String PROJECT_TAS_FORM = PROJECT_TAS_CONTROLLER_NAME + FORM_SUFFIX;
	String PURCHASE_FORM = PURCHASE_CONTROLLER_NAME + FORM_SUFFIX;
	String PURCHASE_INVOICE_FORM = PURCHASE_INVOICE_CONTROLLER_NAME + FORM_SUFFIX;
	String SALES_FORM = SALES_CONTROLLER_NAME + FORM_SUFFIX;
	String SALES_INVOICE_FORM = SALES_INVOICE_CONTROLLER_NAME + FORM_SUFFIX;
	String TAS_STAT_TAS_ITEM_FORM = "tasStat_tasItem_form";
	String UNDEDUCTIBLE_INVOICE_FORM = UNDEDUCTIBLE_INVOICE_CONTROLLER_NAME + FORM_SUFFIX;

}
