package com.code.aon.finance.event;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
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
import com.code.aon.finance.invoicing.InvoicingException;
import com.code.aon.finance.invoicing.remover.IInvoiceDetailRemover;
import com.code.aon.finance.invoicing.remover.InvoiceRemoverFactory;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.Registry;
import com.code.aon.supplier.Supplier;

public class InvoiceBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Invoice invoice = (Invoice) evt.getTo();
		if (invoice.getType() == InvoiceType.SALES) {
			StringBuilder sb = new StringBuilder();
			if (!StringUtils.isEmpty(invoice.getSeries())) {
				sb.append(invoice.getSeries());
				sb.append("/");
			}
			sb.append(invoice.getNumber());
			invoice.setReferenceCode(sb.toString());
		} else {
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(invoice.getIssueDate());
			invoice.setSeries(Integer.toString(calendar.get(Calendar.YEAR)));
			if (invoice.getNumber() == 0) {
				Criteria criteria = new Criteria();
				criteria.addExpression(ExpressionUtilities.getNotEqualExpression("invoice.type", InvoiceType.SALES.ordinal()));
				invoice.setNumber(SeriesNumberUtil.obtainNumber(invoice.getSeries(), "Invoice", criteria));
			}
		}
		if (invoice.getTaxDate() == null) {
			invoice.setTaxDate(invoice.getIssueDate());
		}
		if (invoice.getScope() == null || invoice.getScope().getId() == null) {
			invoice.setScope(obtainInvoiceScope(invoice.getType(), invoice.getRegistry()));
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
		} catch (InvoicingException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(), e);
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
	private void removeInvoiceDetails(Invoice invoice) throws InvoicingException, ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		Iterator iter = invoiceDetailBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			InvoiceDetail detail = (InvoiceDetail) iter.next();
			IInvoiceDetailRemover remover = InvoiceRemoverFactory.getInvoiceDetailRemover(detail.getSource());
			remover.removeDetail(detail);
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