package com.code.aon.finance.event;

import java.util.Date;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Company;
import com.code.aon.config.IScopable;
import com.code.aon.config.Scope;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceAddress;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.ITaxInfo;
import com.code.aon.registry.Registry;
import com.code.aon.supplier.Supplier;

public class InvoiceBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Invoice invoice = (Invoice) evt.getTo();
		checkInvoice(invoice);
		if (invoice.getType() == InvoiceType.SALES) {
	    	String referenceCode = StringUtils.leftPad(Integer.toString(invoice.getNumber()), 6, "0");
			if (!StringUtils.isEmpty(invoice.getSeries())) {
				referenceCode = invoice.getSeries() + "/" + referenceCode;
			}
			invoice.setReferenceCode(referenceCode);
		} else if (invoice.getType() == InvoiceType.PURCHASE || invoice.getType() == InvoiceType.EXPENSES) {
			invoice.setSeries(Integer.toString(CommonUtil.getYear(invoice.getIssueDate())));
			if (invoice.getNumber() == 0) {
				Criteria criteria = new Criteria();
				criteria.addExpression(ExpressionUtilities.getNotEqualExpression("invoice.type", InvoiceType.SALES.ordinal()));
				invoice.setNumber(SeriesNumberUtil.obtainNumber(invoice.getSeries(), "Invoice", criteria));
			}
		} else if (invoice.getType() == InvoiceType.UNDEDUCTIBLE) {
			invoice.setSeries(Integer.toString(CommonUtil.getYear(invoice.getIssueDate())));
			if (invoice.getNumber() == 0) {
				Criteria criteria = new Criteria();
				criteria.addEqualExpression("invoice.type", InvoiceType.UNDEDUCTIBLE.ordinal());
				invoice.setNumber(SeriesNumberUtil.obtainNumber(invoice.getSeries(), "Invoice", criteria));
			}
		}
		if (invoice.getSecurityLevel() == null) {
			invoice.setSecurityLevel(SecurityLevel.OFFICIAL);
		}
		if (invoice.getTaxDate() == null) {
			invoice.setTaxDate(invoice.getIssueDate());
		}
		if (invoice.getScope() == null || invoice.getScope().getId() == null) {
			invoice.setScope(obtainInvoiceScope(invoice.getType(), invoice.getRegistry()));
		}
		if (invoice.isDefaultTaxInfo()) {
			fillDefaultTaxInfo(invoice);
		}
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Invoice invoice = (Invoice) evt.getTo();
		checkInvoice(invoice);
		if (InvoiceType.SALES == invoice.getType()) {
			invoice.setTaxDate(invoice.getIssueDate());
		}
		if (invoice.isDefaultTaxInfo()) {
			fillDefaultTaxInfo(invoice);
		}
	}

	@Override
	public void vetoableBeanRemoved(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Invoice invoice = (Invoice) evt.getTo();
		try {
			if (isRemovable(invoice)) {
				removeFinanceTrackings(invoice);
				removeFinances(invoice);
				removeInvoiceDetails(invoice);
				removeInvoiceAddress(invoice);
			} else {
				throw new ManagerBeanVetoListenerException("La factura "
						+ invoice.getReferenceCode()
						+ " no se puede borrar. Tiene vencimientos con movimientos.");
			}
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(), e);
		}
	}

	private void checkInvoice(Invoice invoice) throws ManagerBeanVetoListenerException {
		int thisYear = CommonUtil.getYear(new Date());
		int invoiceYear = CommonUtil.getYear(invoice.getIssueDate());
		if (invoiceYear < (thisYear-5) || invoiceYear > (thisYear+1)) {
			throw new ManagerBeanVetoListenerException("La fecha de la factura no esta dentro del rango válido");
		}
	}

	private Scope obtainInvoiceScope(InvoiceType type, Registry registry) throws ManagerBeanVetoListenerException {
		try {
			IManagerBean bean;
			if (type == InvoiceType.SALES) {
				bean = BeanManager.getManagerBean(Customer.class);
			} else if (type == InvoiceType.PURCHASE) {
				bean = BeanManager.getManagerBean(Supplier.class);
			} else {
				bean = BeanManager.getManagerBean(Creditor.class);
			}
			IScopable scopable = (IScopable)bean.get(registry.getId());
			return scopable.getScope();
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(), e);
		}
	}

	private void fillDefaultTaxInfo(Invoice invoice) throws ManagerBeanVetoListenerException {
		InvoiceType type = invoice.getType();
		try {
			Company company = getCompany();
			IManagerBean bean;
			if (type == InvoiceType.SALES) {
				bean = BeanManager.getManagerBean(Customer.class);
			} else if (type == InvoiceType.PURCHASE) {
				bean = BeanManager.getManagerBean(Supplier.class);
			} else {
				bean = BeanManager.getManagerBean(Creditor.class);
			}
			ITaxInfo taxInfo = (ITaxInfo)bean.get(invoice.getRegistry().getId());

			invoice.setWithholding((type == InvoiceType.SALES) ? company.isWithholding() && taxInfo.isWithholding() : taxInfo.isWithholding());
			invoice.setSurcharge((type == InvoiceType.SALES) ? taxInfo.isSurcharge() : (type == InvoiceType.PURCHASE) ? company.isSurcharge() : false);
			invoice.setTaxFree(taxInfo.isTaxFree());
			invoice.setTransaction(taxInfo.getTransaction());
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(), e);
		}
	}

	private Company getCompany() throws ManagerBeanException {
		IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
		Iterator<ITransferObject> iterator = companyBean.getList(null, 0, 1).iterator();
		if (iterator.hasNext()) {
			return (Company) iterator.next();
		}
		return null;
	}

	private boolean isRemovable(Invoice invoice) throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_INVOICE_ID), invoice.getId());
		criteria.addExpression(ExpressionUtilities.getNotEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_FINANCE_STATUS), FinanceStatus.PENDING));
		if (financeBean.getCount(criteria) == 0) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private void removeFinanceTrackings(Invoice invoice) throws ManagerBeanException {
		IManagerBean financeTrackingBean = BeanManager.getManagerBean(FinanceTracking.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeTrackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_FINANCE_INVOICE_ID), invoice.getId());
		Iterator iter = financeTrackingBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			financeTrackingBean.remove((FinanceTracking) iter.next());
		}
	}

	@SuppressWarnings("unchecked")
	private void removeFinances(Invoice invoice) throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_INVOICE_ID), invoice.getId());
		Iterator iter = financeBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			financeBean.remove((Finance) iter.next());
		}
	}

	@SuppressWarnings("unchecked")
	private void removeInvoiceDetails(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		Iterator iter = invoiceDetailBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			invoiceDetailBean.remove((InvoiceDetail) iter.next());
		}
	}

	@SuppressWarnings("unchecked")
	private void removeInvoiceAddress(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceAddressBean = BeanManager.getManagerBean(InvoiceAddress.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceAddressBean.getFieldName(IFinanceAlias.INVOICE_ADDRESS_INVOICE_ID), invoice.getId());
		Iterator iter = invoiceAddressBean.getList(criteria, 0, 1).iterator();
		if (iter.hasNext()) {
			invoiceAddressBean.remove((InvoiceAddress) iter.next());
		}
	}

}