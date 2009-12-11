package com.code.aon.account.bridge.writer.pricing;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.TaxAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.enumeration.TaxAccountType;
import com.code.aon.accounting.DefaultAccounts;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.Tax;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.product.strategy.TaxKey;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.ITaxInfo;

public class AccountInvoicePriceStrategy extends InvoicePriceStrategy {
	
	private static final Logger LOGGER = Logger.getLogger(AccountInvoicePriceStrategy.class.getName());
	
	private AccountingUtil accountingUtil;

	private AccountingUtil getAccountingUtil() {
		if (accountingUtil == null) {
			accountingUtil = new AccountingUtil();
		}
		return accountingUtil;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<TaxBreakDown> getTaxBreakDowns(ICalculableContainer icc, ITaxInfo iti) {
		List<TaxBreakDown> taxBreakDowns = new LinkedList<TaxBreakDown>();
		if(!iti.isTaxFree()){
			Iterator iter = icc.getDetailList().iterator();
			Map map = new HashMap();
			while(iter.hasNext()){
				InvoiceDetail invoiceDetail = (InvoiceDetail)iter.next();
				Iterator breakDownIter =  invoiceDetail.getTaxBreakDowns().iterator();
				while(breakDownIter.hasNext()){
					TaxBreakDown breakDown = (TaxBreakDown)breakDownIter.next();
					breakDown.setAccount(obtainTaxAccount(breakDown.getTaxType(), invoiceDetail));
					TaxKey key = new TaxKey();
					key.setType(breakDown.getTaxType());
					key.setPercent(breakDown.getTaxPercent());
					TaxBreakDown mapBreakDown;
					if(map.containsKey(key)){
						mapBreakDown = (TaxBreakDown)map.get(key);
						mapBreakDown.setBase(mapBreakDown.getBase() + breakDown.getBase());
					} else {
						mapBreakDown = breakDown;
					}
					map.put(key, mapBreakDown);
				}
			}
			Iterator iterator = map.values().iterator();
			while(iterator.hasNext()){
				TaxBreakDown tbd = (TaxBreakDown)iterator.next();
				tbd.setTaxQuota(CommonUtil.round(tbd.getBase() * tbd.getTaxPercent()/100));
				if(iti.isSurcharge()){
					tbd.setSurchargeQuota(CommonUtil.round(tbd.getBase() * tbd.getSurchargePercent()/100));
				}else{
					tbd.setSurchargeQuota(0.0);
					tbd.setSurchargePercent(0.0);
				}
				taxBreakDowns.add(tbd);
			}
		}
		return taxBreakDowns;
	}

	@SuppressWarnings("unchecked")
	private Account obtainTaxAccount(TaxType taxType, InvoiceDetail invoiceDetail) {
		Item item = invoiceDetail.getItem();
		InvoiceType invoiceType = invoiceDetail.getInvoice().getType();
		if (item != null && item.getId() != null) {
			Tax tax = (taxType.equals(TaxType.RETENTION)) ? item.getProduct().getRetention() : item.getProduct().getVat();
			TaxAccountType taxAccountType = (invoiceType.equals(InvoiceType.SALES)) ? TaxAccountType.SALES : TaxAccountType.PURCHASE;
			try {
				IManagerBean taxAccountBean = BeanManager.getManagerBean(TaxAccount.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(taxAccountBean.getFieldName(IAccountBridgeAlias.TAX_ACCOUNT_TAX_ID), tax.getId());
				criteria.addEqualExpression(taxAccountBean.getFieldName(IAccountBridgeAlias.TAX_ACCOUNT_TYPE), taxAccountType);
				Iterator iterator = taxAccountBean.getList(criteria).iterator();
				if (iterator.hasNext()) {
					TaxAccount taxAccount = (TaxAccount)iterator.next();
					return taxAccount.getAccount();
				}
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error obtaining Tax Account", e);
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
			LOGGER.log(Level.SEVERE, "Error obtaining Tax Account", e);
		}

		return null;
	}

}