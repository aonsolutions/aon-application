package com.code.aon.account.bridge.writer.pricing;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.TaxAccount;
import com.code.aon.account.bridge.enumeration.TaxAccountType;
import com.code.aon.accounting.DefaultAccounts;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tax;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class AccountInvoicePriceStrategy extends InvoicePriceStrategy {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AccountInvoicePriceStrategy.class.getName());
	
	private AccountingUtil accountingUtil;

	private AccountingUtil getAccountingUtil() {
		if (accountingUtil == null) {
			accountingUtil = new AccountingUtil();
		}
		return accountingUtil;
	}

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
			TaxAccountType taxAccountType = (invoiceType.equals(InvoiceType.SALES)) ? TaxAccountType.SALES : TaxAccountType.PURCHASE;
			try {
				IManagerBean taxAccountBean = BeanManager.getManagerBean(TaxAccount.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(taxAccountBean.getFieldName(IEntityAlias.TAX_ACCOUNT_TAX_ID), tax.getId());
				criteria.addEqualExpression(taxAccountBean.getFieldName(IEntityAlias.TAX_ACCOUNT_TYPE), taxAccountType);
				List<ITransferObject> list = taxAccountBean.getList(criteria);
				if (list != null && list.size() > 0) {
					TaxAccount taxAccount = (TaxAccount) list.get(0);
					return taxAccount.getAccount();
				}
			} catch (ManagerBeanException e) {
				LOGGER.error("Error obtaining Tax Account", e);
			}
		}
		
		try {
			if (taxType.equals(TaxType.RETENTION)) {
				if (invoiceType.equals(InvoiceType.SALES)) {
					return getAccountingUtil().obtainDefaultAccount(DefaultAccounts.PAID_RETENTION_ACCOUNT);
				} 
				return getAccountingUtil().obtainDefaultAccount(DefaultAccounts.CHARGED_RETENTION_ACCOUNT);
			} 
			if (invoiceType.equals(InvoiceType.SALES)) {
				return getAccountingUtil().obtainDefaultAccount(DefaultAccounts.CHARGE_VAT_ACCOUNT);
			} 
			return getAccountingUtil().obtainDefaultAccount(DefaultAccounts.PAID_VAT_ACCOUNT);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining Tax Account", e);
		}

		return null;
	}

}