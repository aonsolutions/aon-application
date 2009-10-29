package com.code.aon.ui.finance.remover;

import com.code.aon.finance.enumeration.InvoiceSource;

public class InvoiceRemoverFactory {
	
	private static DeliveryInvoiceDetailRemover deliveryInvoiceDetailRemover;
	
	private static DirectInvoiceDetailRemover directInvoiceDetailRemover;

	private static FeeInvoiceDetailRemover feeInvoiceDetailRemover;

	private static AccountInvoiceDetailRemover accountInvoiceDetailRemover;

	private static DeliveryInvoiceDetailRemover getDeliveryInvoiceDetailRemover() {
		if(deliveryInvoiceDetailRemover == null){
			deliveryInvoiceDetailRemover = new DeliveryInvoiceDetailRemover();
		}
		return deliveryInvoiceDetailRemover;
	}

	private static DirectInvoiceDetailRemover getDirectInvoiceDetailRemover() {
		if(directInvoiceDetailRemover == null){
			directInvoiceDetailRemover = new DirectInvoiceDetailRemover();
		}
		return directInvoiceDetailRemover;
	}

	private static FeeInvoiceDetailRemover getFeeInvoiceDetailRemover() {
		if(feeInvoiceDetailRemover == null){
			feeInvoiceDetailRemover = new FeeInvoiceDetailRemover();
		}
		return feeInvoiceDetailRemover;
	}

	public static AccountInvoiceDetailRemover getAccountInvoiceDetailRemover() {
		if(accountInvoiceDetailRemover == null){
			accountInvoiceDetailRemover = new AccountInvoiceDetailRemover();
		}
		return accountInvoiceDetailRemover;
	}

	public static IInvoiceDetailRemover getInvoiceDetailRemover(InvoiceSource source){
		if(source.equals(InvoiceSource.DELIVERY)){
			return getDeliveryInvoiceDetailRemover();
		}else if(source.equals(InvoiceSource.DIRECT_SALES) || source.equals(InvoiceSource.DIRECT_INVOICE)){
			return getDirectInvoiceDetailRemover();
		}else if(source.equals(InvoiceSource.FEE)){
			return getFeeInvoiceDetailRemover();
		}else if(source.equals(InvoiceSource.ACCOUNT)){
			return getAccountInvoiceDetailRemover();
		}
		return null;
	}
}