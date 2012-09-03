package com.code.aon.finance.event;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
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
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.enumeration.RectificationType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.ITaxInfo;
import com.code.aon.registry.Registry;
import com.code.aon.supplier.Supplier;
import com.esferalia.aon.entity.IEntityAlias;

public class InvoiceBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Invoice invoice = (Invoice) evt.getTo();
		checkInvoice(invoice);
		if (invoice.getType() == InvoiceType.SALES) {
			checkNumber(invoice);
			String referenceCode = StringUtils.leftPad(Integer.toString(invoice.getNumber()), 6, "0");
			if (!StringUtils.isEmpty(invoice.getSeries())) {
				referenceCode = invoice.getSeries() + "/" + referenceCode;
			}
			invoice.setReferenceCode(referenceCode);
		} else if (invoice.getType() == InvoiceType.PURCHASE || invoice.getType() == InvoiceType.EXPENSES) {
			invoice.setSeries(Integer.toString(CommonUtil.getYear(invoice.getIssueDate())));
			if (invoice.getNumber() == 0) {
				Criteria criteria = new Criteria();
				criteria.addNotEqualExpression("invoice.type", InvoiceType.SALES.ordinal());
				criteria.addNotEqualExpression("invoice.type", InvoiceType.UNDEDUCTIBLE.ordinal());
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
		if (invoice.getRectificationType() == null) {
			invoice.setRectificationType(RectificationType.NONE);
		}
		if (invoice.isUpdateEnabled()) {
			invoice.setTaxableBase(0);
			invoice.setVatQuota(0);
			invoice.setRetentionQuota(0);
			invoice.setTotal(0);
		}
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Invoice invoice = (Invoice) evt.getTo();
		if (invoice.isUpdateEnabled()) {
			checkInvoice(invoice);
			if (invoice.getType() == InvoiceType.SALES) {
				checkNumber(invoice);
				String referenceCode = StringUtils.leftPad(Integer.toString(invoice.getNumber()), 6, "0");
				if (!StringUtils.isEmpty(invoice.getSeries())) {
					referenceCode = invoice.getSeries() + "/" + referenceCode;
				}
				invoice.setReferenceCode(referenceCode);
			}
			if (checkInvoiceDate(invoice)) {
				invoice.setTaxDate(invoice.getIssueDate());
			}
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
				if (invoice.isRectifier()) {
					updateRectifiedInvoices(invoice);
				}
			} else {
				throw new ManagerBeanVetoListenerException("La Factura " + invoice.getReferenceCode() + " no se puede borrar. " +
															"Tiene Vencimientos con movimientos.");
			}
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(), e);
		}
	}

	private void checkInvoice(Invoice invoice) throws ManagerBeanVetoListenerException {
		int thisYear = CommonUtil.getYear(new Date());
		int invoiceYear = CommonUtil.getYear(invoice.getIssueDate());
		if (invoiceYear < (thisYear-5) || invoiceYear > (thisYear+1)) {
			throw new ManagerBeanVetoListenerException("La Fecha de la Factura no es correcta.");
		}
		if (StringUtils.isEmpty(invoice.getRegistryName())) {
			invoice.setRegistryName(invoice.getRegistry().getFullName());
		}
		if (StringUtils.isEmpty(invoice.getRegistryDocument())) {
			invoice.setRegistryDocument(invoice.getRegistry().getDocument());
		}
		if (invoice.isDefaultTaxInfo()) {
			fillDefaultTaxInfo(invoice);
		}
	}

	private void checkNumber(Invoice invoice) throws ManagerBeanVetoListenerException {
		String andSeries = "";
		String andId = "";
		if (StringUtils.isEmpty(invoice.getSeries())) {
			andSeries = "AND (invoice.series IS NULL OR invoice.series = '') ";
		} else {
			andSeries = "AND invoice.series = '" + invoice.getSeries() + "' ";
		}
		if (invoice.getId() != null) {
			andId = "AND invoice.id <> " + invoice.getId();
		}

		String select = "SELECT invoice.id id " +
    					"FROM invoice as invoice " +
    					"WHERE " + DomainManager.getSQLWhereClause("invoice.domain") + " " +
    					andSeries +
    					"AND invoice.number = " + invoice.getNumber() + " " +
    					"AND invoice.type = " + invoice.getType().ordinal() + " " + 
    					andId;
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		SQLQuery query = session.createSQLQuery(select);
        List<?> list = query
    		.addScalar("id", Hibernate.INTEGER)
        	.list();
        Iterator<?> iterator = list.iterator();
        if (iterator.hasNext()) {
			throw new ManagerBeanVetoListenerException("Ya existe una Factura con esa Serie/Número.");
        }
	}

	private boolean checkInvoiceDate(Invoice invoice) throws ManagerBeanVetoListenerException {
    	if (invoice.getTaxDate() == null) {
    		return true;
    	}

    	String select = "SELECT invoice.issue_date issue_date, invoice.tax_date tax_date " +
						"FROM invoice as invoice " +
						"WHERE invoice.id = " + invoice.getId();
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		SQLQuery query = session.createSQLQuery(select);
        List<?> list = query
    		.addScalar("issue_date", Hibernate.DATE)
        	.addScalar("tax_date", Hibernate.DATE)
        	.list();
        Iterator<?> iterator = list.iterator();
        if (iterator.hasNext()) {
        	Object[] obj = (Object[])iterator.next();
            Date issueDate= (Date) obj[0];
            Date taxDate= (Date) obj[1];
            if (!ObjectUtils.equals(issueDate, invoice.getIssueDate())) {
                return (taxDate == null || ObjectUtils.equals(issueDate, taxDate));
            }
        }
        return false;
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
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_ID), invoice.getId());
		criteria.addNotEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_FINANCE_STATUS), FinanceStatus.PENDING);
		if (financeBean.getCount(criteria) == 0) {
			return true;
		}
		return false;
	}

	private void removeFinanceTrackings(Invoice invoice) throws ManagerBeanException {
		IManagerBean financeTrackingBean = BeanManager.getManagerBean(FinanceTracking.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeTrackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_FINANCE_INVOICE_ID), invoice.getId());
		Iterator<ITransferObject> iter = financeTrackingBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			financeTrackingBean.remove((FinanceTracking) iter.next());
		}
	}

	private void removeFinances(Invoice invoice) throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_ID), invoice.getId());
		Iterator<ITransferObject> iter = financeBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			financeBean.remove((Finance) iter.next());
		}
	}

	private void removeInvoiceDetails(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		Iterator<ITransferObject> iter = invoiceDetailBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			InvoiceDetail invoiceDetail = (InvoiceDetail) iter.next();
			invoiceDetail.setUpdateEnabled(false);
			invoiceDetailBean.remove(invoiceDetail);
		}
	}

	private void removeInvoiceAddress(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceAddressBean = BeanManager.getManagerBean(InvoiceAddress.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceAddressBean.getFieldName(IEntityAlias.INVOICE_ADDRESS_INVOICE_ID), invoice.getId());
		Iterator<ITransferObject> iter = invoiceAddressBean.getList(criteria, 0, 1).iterator();
		if (iter.hasNext()) {
			invoiceAddressBean.remove((InvoiceAddress) iter.next());
		}
	}

	public void updateRectifiedInvoices(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		Invoice rectified = invoice.getRectificationInvoice();
		if (rectified.getRectificationInvoice() != null && rectified.getRectificationInvoice().getId() == invoice.getId()) {
			rectified.setRectificationType(RectificationType.NONE);
			rectified.setRectificationInvoice(null);
			rectified.setUpdateEnabled(false);
			invoiceBean.update(rectified);
		} else {
			Criteria criteria = new Criteria();
			criteria.addNotEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_ID), invoice.getId());
			criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_RECTIFICATION_INVOICE_ID), rectified.getId());
			if (invoiceBean.getCount(criteria) <= 1) {
				rectified.setRectificationType(RectificationType.NONE);
				for (ITransferObject ito : invoiceBean.getList(criteria)) {
					rectified.setRectificationType(RectificationType.RECTIFIED);
					rectified.setRectificationInvoice((Invoice)ito);
				}
				rectified.setUpdateEnabled(false);
				invoiceBean.update(rectified);
			}
		}
	}

}