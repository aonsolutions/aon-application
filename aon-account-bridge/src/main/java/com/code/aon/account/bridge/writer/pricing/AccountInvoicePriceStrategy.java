package com.code.aon.account.bridge.writer.pricing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.Account;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.config.Tax;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.TaxBreakDown;

public class AccountInvoicePriceStrategy extends InvoicePriceStrategy {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AccountInvoicePriceStrategy.class.getName());

	protected void setTaxBreakDownAddInfo(TaxBreakDown breakDown, InvoiceDetail invoiceDetail) {
		InvoiceType invoiceType = invoiceDetail.getInvoice().getType();
		InvoiceTransactionType tran = invoiceDetail.getInvoice().getTransaction();
		breakDown.setAccount(obtainTaxAccount(breakDown.getTaxType(), invoiceType, invoiceDetail));
		
		// Este IF es para que aparezca el iva contrario en las contabilización
		// de las facturas intracomunitarias y de ISP.
		if (breakDown.getTaxType() == TaxType.VAT && invoiceType != InvoiceType.SALES &&
				(tran == InvoiceTransactionType.INTRACOMMUNITY ||
				tran == InvoiceTransactionType.OTHER_ISP) ) {
			breakDown.setBalancingAccount(obtainTaxAccount(breakDown.getTaxType(), InvoiceType.SALES, invoiceDetail));
		}
	}

	private Account obtainTaxAccount(TaxType taxType, InvoiceType invoiceType, InvoiceDetail invoiceDetail) {
		Item item = invoiceDetail.getItem();
		if (item != null && item.getId() != null) {
			Tax tax = (taxType.equals(TaxType.RETENTION)) ? item.getProduct().getRetention() : item.getProduct().getVat();
			Account taxAccount = (invoiceType.equals(InvoiceType.SALES)) ? tax.getSalesAccount() : tax.getPurchaseAccount();
			if (taxAccount != null && taxAccount.getId() != null) {
				return taxAccount;
			}
		}
		
		try {
			if (taxType.equals(TaxType.RETENTION)) {
				if (invoiceType.equals(InvoiceType.SALES)) {
					return AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_PAID_RET_ACC);
				} 
				return AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_CHARGED_RET_ACC);
			} 
			if (invoiceType.equals(InvoiceType.SALES)) {
				return AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_CHARGED_VAT_ACC);
			} 
			return AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_PAID_VAT_ACC);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining Tax Account", e);
		}

		return null;
	}

}