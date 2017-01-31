package com.code.aon.account.bridge.event;

import java.util.List;

import com.code.aon.AonVersion;
import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.account.bridge.InvoiceDetailAccount;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.finance.Invoice;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class AccountEntryDetailBeanListener extends ManagerBeanListenerAdapter {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void beanUpdated(ManagerBeanEvent evt) throws ManagerBeanException {
        AccountEntryDetail to = (AccountEntryDetail)evt.getTo();
        if (to.getSavedAccountId() != null && !to.getSavedAccountId().equals(to.getAccount().getId())) {
        	IManagerBean accEntryInvoiceBean = BeanManager.getManagerBean(AccountEntryInvoice.class);
        	Criteria criteria = new Criteria();
        	criteria.addEqualExpression(accEntryInvoiceBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_INVOICE_ACCOUNT_ENTRY_ID), to.getAccountEntry().getId());
        	List<ITransferObject> entryInvoiceList = accEntryInvoiceBean.getList(criteria);
        	if (entryInvoiceList.size() > 0) {
        		Invoice invoice = ((AccountEntryInvoice)entryInvoiceList.get(0)).getInvoice();
        		IManagerBean invoiceDetailAccBean = BeanManager.getManagerBean(InvoiceDetailAccount.class);
            	criteria = new Criteria();
            	criteria.addEqualExpression(invoiceDetailAccBean.getFieldName(IEntityAlias.INVOICE_DETAIL_ACCOUNT_INVOICE_DETAIL_INVOICE_ID), invoice.getId());
            	criteria.addEqualExpression(invoiceDetailAccBean.getFieldName(IEntityAlias.INVOICE_DETAIL_ACCOUNT_ACCOUNT_ID), to.getSavedAccountId());
            	for (ITransferObject ito : invoiceDetailAccBean.getList(criteria)) {
            		InvoiceDetailAccount invoiceDetailAccount = (InvoiceDetailAccount)ito;
            		invoiceDetailAccount.setAccount(to.getAccount());
            		invoiceDetailAccBean.update(invoiceDetailAccount);
            	}
        	}
        }
	}

}
