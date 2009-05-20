package com.code.aon.finance.event;

import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.product.Tax;
import com.code.aon.product.TaxDetail;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.ql.Criteria;

/**
 * The InvoiceDetailBeanListener. Listener to be added to InvoiceDetail.class
 */
public class InvoiceDetailBeanListener extends ManagerBeanListenerAdapter {
	
	/** The LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(InvoiceDetailBeanListener.class.getName());

	/**
	 * Bean inserted. Inserts the related InvoiceTax when an InvoiceDetail is added.
	 * 
	 * @param evt the evt
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@Override
	public void beanInserted(ManagerBeanEvent evt) throws ManagerBeanException {
		InvoiceDetail invoiceDetail = (InvoiceDetail)evt.getTo();
		if(invoiceDetail.getItem() != null){
			InvoiceTax invoiceDetailVat = getInvoiceTax(invoiceDetail, invoiceDetail.getItem().getProduct().getVat());
			IManagerBean invoiceTaxBean = BeanManager.getManagerBean(InvoiceTax.class);
			invoiceTaxBean.insert(invoiceDetailVat);
			if(invoiceDetail.getInvoice().isWithholding() && invoiceDetail.getItem().getProduct().getRetention() != null){
				InvoiceTax invoiceDetailRetention = getInvoiceTax(invoiceDetail, invoiceDetail.getItem().getProduct().getRetention());
				invoiceTaxBean.insert(invoiceDetailRetention);
			}
		}
	}
	
	/**
	 * Bean updated. Inserts the related InvoiceTax when an InvoiceDetail is updated.
	 * 
	 * @param evt the evt
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@Override
	public void beanUpdated(ManagerBeanEvent evt) throws ManagerBeanException {
		InvoiceDetail invoiceDetail = (InvoiceDetail)evt.getTo();
		if(invoiceDetail.getItem() != null){
			InvoiceTax invoiceDetailVat = getInvoiceTax(invoiceDetail, invoiceDetail.getItem().getProduct().getVat());
			IManagerBean invoiceTaxBean = BeanManager.getManagerBean(InvoiceTax.class);
			invoiceTaxBean.insert(invoiceDetailVat);
			if(invoiceDetail.getInvoice().isWithholding() && invoiceDetail.getItem().getProduct().getRetention() != null){
				InvoiceTax invoiceDetailRetention = getInvoiceTax(invoiceDetail, invoiceDetail.getItem().getProduct().getRetention());
				invoiceTaxBean.insert(invoiceDetailRetention);
			}
		}
	}
	
	/**
	 * Gets the invoiceTax related with the parameter invoiceDetail and completes its surchage and percentage.
	 * 
	 * @param invoiceDetail the invoice detail
	 * @param dataBase if the invoiceDetail has to be retrieved from the database
	 * 
	 * @return the invoice tax
	 */
	private InvoiceTax getInvoiceTax(InvoiceDetail invoiceDetail, Tax tax) {
		Date date = invoiceDetail.getInvoice().getIssueDate();
		if(date.before(tax.getStartDate())){
			tax = obtainTax(tax.getId(),date);
		}
		InvoiceTax invoiceTax = new InvoiceTax();
		invoiceTax.setInvoiceDetail(invoiceDetail);
		invoiceTax.setTaxType(tax.getType());
		double surcharge = 0.0;
		double percentage = 0.0;
		if(!invoiceDetail.getInvoice().isTaxFree()){
			percentage = tax.getPercentage();
			if(invoiceDetail.getInvoice().isSurcharge()){
				surcharge = tax.getSurcharge();
			}
		}
		invoiceTax.setPercentage(percentage);
		invoiceTax.setSurcharge(surcharge);
		return invoiceTax;
	}

	/**
	 * Gets the Tax with id equals to the parameter id, and valid with the date passed as parameter.
	 * 
	 * @param date the date
	 * @param id the id
	 * 
	 * @return the tax
	 */
	@SuppressWarnings("unchecked")
	private Tax obtainTax(Integer id, Date date) {
		try {
			IManagerBean taxDetailBean = BeanManager.getManagerBean(TaxDetail.class);
        	Criteria criteria = new Criteria();
        	criteria.addEqualExpression(taxDetailBean.getFieldName(IProductAlias.TAX_DETAIL_TAX_ID),id);
        	criteria.addLessThanExpression(taxDetailBean.getFieldName(IProductAlias.TAX_DETAIL_START_DATE),date);
        	criteria.addGreaterThanExpression(taxDetailBean.getFieldName(IProductAlias.TAX_DETAIL_END_DATE),date);
        	Iterator iter = taxDetailBean.getList(criteria).iterator();
        	while(iter.hasNext()){
        		TaxDetail taxDetail = (TaxDetail)iter.next();
        		Tax tax = new Tax();
        		tax.setId(taxDetail.getTax().getId());
        		tax.setPercentage(taxDetail.getValue());
        		tax.setSurcharge(taxDetail.getSurcharge());
        		tax.setType(taxDetail.getTax().getType());
        		return tax;
        	}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error getting tax for category with id= " + id, e);
		}
		return null;
	}
}
