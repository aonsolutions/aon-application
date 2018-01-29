package com.code.aon.finance.event;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.code.aon.AonVersion;
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
import com.code.aon.finance.util.FinanceUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.ITaxInfo;
import com.code.aon.registry.Registry;
import com.code.aon.supplier.Supplier;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.EnterpriseActivity;

public class InvoiceBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Invoice invoice = (Invoice) evt.getTo();
		Company company = getCompany();
		checkInvoice(invoice, company);
		if (invoice.isSales()) {
			checkNumber(invoice);
			String referenceCode = StringUtils.leftPad(Integer.toString(invoice.getNumber()), SeriesNumberUtil.getNumberMinimumLength(), "0");
			if (!StringUtils.isBlank(invoice.getSeries())) {
				referenceCode = invoice.getSeries() + "/" + referenceCode;
			}
			invoice.setReferenceCode(referenceCode);
		} else if (invoice.isPurchase() || invoice.isExpense()) {
			invoice.setSeries(Integer.toString(CommonUtil.getYear(invoice.getIssueDate())));
			if (invoice.getNumber() == 0) {
				Criteria criteria = new Criteria();
				criteria.addNotEqualExpression("invoice.type", InvoiceType.SALES.ordinal());
				criteria.addNotEqualExpression("invoice.type", InvoiceType.UNDEDUCTIBLE.ordinal());
				invoice.setNumber(SeriesNumberUtil.obtainNumber(invoice.getSeries(), "Invoice", criteria));
			}
		} else if (invoice.isUndeductible()) {
			invoice.setSeries(Integer.toString(CommonUtil.getYear(invoice.getIssueDate())));
			if (invoice.getNumber() == 0) {
				Criteria criteria = new Criteria();
				criteria.addEqualExpression("invoice.type", InvoiceType.UNDEDUCTIBLE.ordinal());
				invoice.setNumber(SeriesNumberUtil.obtainNumber(invoice.getSeries(), "Invoice", criteria));
			}
		}
		if ((invoice.getActivity() == null || invoice.getActivity().getId() == null) && !invoice.isSkipCalculateMainActivity()) {
			invoice.setActivity(obtainPrincipalActivity(company));
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
			Company company = getCompany();
			checkInvoice(invoice, company);
			if (invoice.isSales()) {
				checkNumber(invoice);
				String referenceCode = StringUtils.leftPad(Integer.toString(invoice.getNumber()), SeriesNumberUtil.getNumberMinimumLength(), "0");
				if (!StringUtils.isBlank(invoice.getSeries())) {
					referenceCode = invoice.getSeries() + "/" + referenceCode;
				}
				invoice.setReferenceCode(referenceCode);
			}
			if (changeTaxDate(invoice)) {
				invoice.setTaxDate(invoice.getIssueDate());
			}
			invoice.setUpdateDetails(updateDetailsNeeded(invoice));
		}
	}

	@Override
	public void vetoableBeanRemoved(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Invoice invoice = (Invoice) evt.getTo();
		checkLimitDate(invoice);
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

	private Company getCompany() throws ManagerBeanVetoListenerException {
		try {
			IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
			for (ITransferObject ito : companyBean.getList(null, 0, 1)) {
				return (Company)ito;
			}
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(), e);
		}
		return null;
	}
	
	private void checkInvoice(Invoice invoice, Company company) throws ManagerBeanVetoListenerException {
		checkLimitDate(invoice);
		checkInvoiceYear(invoice);
		if (!invoice.isRectifier()) {
			if (StringUtils.isEmpty(invoice.getRegistryName())) {
				invoice.setRegistryName(invoice.getRegistry().getFullName());
			}
			if (StringUtils.isEmpty(invoice.getRegistryDocument())) {
				invoice.setRegistryDocument(invoice.getRegistry().getDocument());
			}
		}
		if (invoice.isDefaultTaxInfo()) {
			fillDefaultTaxInfo(invoice, company);
		}
	}

	private void checkLimitDate(Invoice invoice) throws ManagerBeanVetoListenerException {
		if (!FinanceUtil.isValidLimitDate(invoice)) {
			throw new ManagerBeanVetoListenerException("La Fecha de la Factura rebasa la Fecha Limite de Operaciones.");
		}
	}

	private void checkInvoiceYear(Invoice invoice) throws ManagerBeanVetoListenerException {
		int thisYear = CommonUtil.getYear(new Date());
		int invoiceYear = CommonUtil.getYear(invoice.getIssueDate());
		if (invoiceYear < (thisYear-5) || invoiceYear > (thisYear+1)) {
			throw new ManagerBeanVetoListenerException("El Año de la Factura no es correcto.");
		}
	}

	private void checkNumber(Invoice invoice) throws ManagerBeanVetoListenerException {
		String andSeries = "";
		if (StringUtils.isBlank(invoice.getSeries())) {
			andSeries = "AND (invoice.series IS NULL OR invoice.series = '') ";
		} else {
			andSeries = "AND invoice.series = '" + invoice.getSeries() + "' ";
		}
		String andId = (invoice.getId() != null) ? "AND invoice.id <> " + invoice.getId() : "";

		String select = "SELECT invoice.id id " +
    					"FROM invoice as invoice " +
    					"WHERE " + DomainManager.getSQLWhereClause("invoice.domain") + " " +
    					andSeries +
    					"AND invoice.number = " + invoice.getNumber() + " " +
    					"AND invoice.type = " + invoice.getType().ordinal() + " " + 
    					andId;
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		SQLQuery query = session.createSQLQuery(select);
        List<?> list = query.addScalar("id", Hibernate.INTEGER).list();
        if (!list.isEmpty()) {
			throw new ManagerBeanVetoListenerException("Ya existe una Factura con esa Serie/Número.");
        }
	}

	private boolean changeTaxDate(Invoice invoice) throws ManagerBeanVetoListenerException {
    	if (invoice.getTaxDate() == null) {
    		return true;
    	}

    	String select = "SELECT invoice.issue_date issue_date, invoice.tax_date tax_date " +
						"FROM invoice as invoice " +
						"WHERE invoice.id = " + invoice.getId();
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		SQLQuery query = session.createSQLQuery(select);
        List<?> list = query.addScalar("issue_date", Hibernate.DATE).addScalar("tax_date", Hibernate.DATE).list();
        if (!list.isEmpty()) {
        	Object[] obj = (Object[])list.get(0);
            Date issueDate = (Date)obj[0];
            Date taxDate = (Date)obj[1];
            if (!ObjectUtils.equals(issueDate, invoice.getIssueDate())) {
                return (taxDate == null || ObjectUtils.equals(issueDate, taxDate));
            }
        }
        return false;
	}

	private boolean updateDetailsNeeded(Invoice invoice) throws ManagerBeanVetoListenerException {
    	String select = "SELECT invoice.issue_date issue_date, invoice.registry registry " +
						"FROM invoice as invoice " +
						"WHERE invoice.id = " + invoice.getId();
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		SQLQuery query = session.createSQLQuery(select);
        List<?> list = query.addScalar("issue_date", Hibernate.DATE).addScalar("registry", Hibernate.INTEGER).list();
        if (!list.isEmpty()) {
        	Object[] obj = (Object[])list.get(0);
            Date issueDate = (Date)obj[0];
            Integer registry = (Integer)obj[1];
            return (!ObjectUtils.equals(issueDate, invoice.getIssueDate()) || !ObjectUtils.equals(registry, invoice.getRegistry().getId()));
        }
        return false;
	}

	private EnterpriseActivity obtainPrincipalActivity(Company company) throws ManagerBeanVetoListenerException {
		try {
			IManagerBean activityBean = BeanManager.getManagerBean(EnterpriseActivity.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(activityBean.getFieldName(IEntityAlias.ENTERPRISE_ACTIVITY_ENTERPRISE_ID), company.getId());
			criteria.addOrder(activityBean.getFieldName(IEntityAlias.ENTERPRISE_ACTIVITY_PRINCIPAL), Boolean.FALSE);
			criteria.addOrder(activityBean.getFieldName(IEntityAlias.ENTERPRISE_ACTIVITY_DESCRIPTION));
			for (ITransferObject ito : activityBean.getList(criteria)) {
				return (EnterpriseActivity)ito;
			}
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(), e);
		}
		return null;
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

	private void fillDefaultTaxInfo(Invoice invoice, Company company) throws ManagerBeanVetoListenerException {
		try {
			IManagerBean bean;
			if (invoice.isSales()) {
				bean = BeanManager.getManagerBean(Customer.class);
			} else if (invoice.isPurchase()) {
				bean = BeanManager.getManagerBean(Supplier.class);
			} else {
				bean = BeanManager.getManagerBean(Creditor.class);
			}
			ITaxInfo taxInfo = (ITaxInfo)bean.get(invoice.getRegistry().getId());

			invoice.setTransaction(taxInfo.getTransaction());
			invoice.setSurcharge((invoice.isSales()) ? taxInfo.isSurcharge() : (invoice.isPurchase()) ? company.isSurcharge() : false);
			invoice.setWithholding((invoice.isSales()) ? company.isWithholding() && taxInfo.isWithholding() : taxInfo.isWithholding());
			invoice.setWithholdingFarmer((invoice.isSales()) ? company.isWithholdingFarmer() && taxInfo.isWithholding() : taxInfo.isWithholdingFarmer());
			invoice.setVatAccrualPayment((isVatAccrualPaymentAvailable(invoice)) ? company.isVatAccrualPayment() || taxInfo.isVatAccrualPayment() : false);
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(), e);
		}
	}

	private boolean isVatAccrualPaymentAvailable(Invoice invoice) {
		Date controlDate = CommonUtil.getDate(2014, 0, 1);
		return !invoice.isUndeductible() && invoice.isNational() && !invoice.getIssueDate().before(controlDate);
	}

	private boolean isRemovable(Invoice invoice) throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_ID), invoice.getId());
		criteria.addNotEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_FINANCE_STATUS), FinanceStatus.PENDING);
		criteria.addNotEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_ADVANCE), true);
		if (financeBean.getCount(criteria) == 0) {
			return true;
		}
		return false;
	}

	private void removeFinanceTrackings(Invoice invoice) throws ManagerBeanException {
		IManagerBean financeTrackingBean = BeanManager.getManagerBean(FinanceTracking.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeTrackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_FINANCE_INVOICE_ID), invoice.getId());
		criteria.addNotEqualExpression(financeTrackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_FINANCE_ADVANCE), true);
		for (ITransferObject ito : financeTrackingBean.getList(criteria)) {
			financeTrackingBean.remove((FinanceTracking)ito);
		}
	}

	private void removeFinances(Invoice invoice) throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_ID), invoice.getId());
		for (ITransferObject ito : financeBean.getList(criteria)) {
			Finance finance = (Finance)ito;
			if (finance.isAdvance()) {
				finance.setInvoice(null);
				if (finance.getRemarks() != null && finance.getRemarks().indexOf("[") >= 0 && finance.getRemarks().indexOf("]") >= 0) {
					finance.setConcept(finance.getRemarks().substring(finance.getRemarks().indexOf("[")+1, finance.getRemarks().indexOf("]")));
					finance.setRemarks(finance.getRemarks().substring(finance.getRemarks().indexOf("]")+1));
				}
				financeBean.update(finance);
			} else {
				financeBean.remove(finance);
			}
		}
	}

	private void removeInvoiceDetails(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		for (ITransferObject ito : invoiceDetailBean.getList(criteria)) {
			InvoiceDetail invoiceDetail = (InvoiceDetail)ito;
			invoiceDetail.setUpdateEnabled(false);
			invoiceDetailBean.remove(invoiceDetail);
		}
	}

	private void removeInvoiceAddress(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceAddressBean = BeanManager.getManagerBean(InvoiceAddress.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceAddressBean.getFieldName(IEntityAlias.INVOICE_ADDRESS_INVOICE_ID), invoice.getId());
		for (ITransferObject ito : invoiceAddressBean.getList(criteria)) {
			invoiceAddressBean.remove((InvoiceAddress)ito);
		}
	}

	public void updateRectifiedInvoices(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		Invoice rectified = invoice.getRectificationInvoice();
		if (rectified.getRectificationInvoice() != null && rectified.getRectificationInvoice().getId().intValue() == invoice.getId().intValue()) {
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