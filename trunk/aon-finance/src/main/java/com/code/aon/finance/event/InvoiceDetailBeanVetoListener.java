package com.code.aon.finance.event;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.ql.Criteria;

/**
 * The InvoiceBeanListener. VetoListener to be added to InvoiceDetail.class
 */
public class InvoiceDetailBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	/** The LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(InvoiceDetailBeanVetoListener.class.getName());

	/**
	 * Bean removed. Removes the related InvoiceTax before removing the InvoiceDetail
	 * 
	 * @param evt the evt
	 * 
	 * @throws ManagerBeanVetoListenerException the manager bean veto listener exception
	 */
	@Override
	public void vetoableBeanRemoved(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		InvoiceDetail invoiceDetail = (InvoiceDetail)evt.getTo();
		if(!invoiceDetail.getSource().equals(InvoiceSource.ACCOUNT)){
			try {
				IManagerBean invoiceTaxBean = BeanManager.getManagerBean(InvoiceTax.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(invoiceTaxBean.getFieldName(IFinanceAlias.INVOICE_TAX_INVOICE_DETAIL_ID),invoiceDetail.getId());
				Iterator iter = invoiceTaxBean.getList(criteria).iterator();
				while(iter.hasNext()){
					invoiceTaxBean.remove((InvoiceTax)iter.next());
				}
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error removing invoiceTax for invoiceDetail with id= " + invoiceDetail.getId(), e);
			}
		}
	}
}
