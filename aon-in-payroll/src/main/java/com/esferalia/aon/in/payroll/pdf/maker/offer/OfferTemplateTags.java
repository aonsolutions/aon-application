package com.esferalia.aon.in.payroll.pdf.maker.offer;

public class OfferTemplateTags {

    /** HEADER DATA */
	public final static String REFERENCE_NUMBER = "referenceNumber";
	public final static String INVOICE_DATE = "invoiceDate";
	public final static String NIF = "nif";
	public final static String REGISTRY_NAME = "registryName";
	public final static String ADDRESS = "address";
	public final static String ADDRESS_LINE_TWO = "addressLineTwo";
	
	
	/** DETAILS */
	public final static String DETAIL_DESCRIPTION = "detailDescription";
	public final static String DETAIL_AMOUNT = "detailAmount";
	public final static String DETAIL_PRICE = "detailPrice";
	public final static String DETAIL_DISCOUNT = "detailDiscount";
	public final static String DETAIL_TOTAL = "detailTotal";
	
	/** TAXES */
	public final static String TAX_BASE = "taxBase";
	public final static String TAX_PERCENTAGE = "taxPercentage";
	public final static String TAX_TYPE = "taxBankType";	
	public final static String TAX_QUOTE = "taxBankQuote";	
	
	/** FINANCES */
	public final static String FINANCE_DATE = "financeDate";	
	public final static String FINANCE_PAY_METHOD = "financePayMethod";	
	public final static String FINANCE_BANK_ACCOUNT = "financeBankAccount";	
	public final static String FINANCE_AMOUNT = "financeAmount";	
	
	
	/** GENERAL DATA */
	public final static String INVOICE_TOTAL = "invoiceTotal";
	
}
