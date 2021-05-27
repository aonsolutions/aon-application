package com.code.aon.finance.bridge.invoicing;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.IProgression;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.finance.FinanceGenerator;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.ql.Criteria;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.enumeration.SalesStatus;
import com.esferalia.aon.entity.IEntityAlias;

public class SalesInvoicingManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(SalesInvoicingManager.class.getName());

	private IPriceStrategy priceStrategy;
	private FinanceGenerator financeGenerator;
	private IProgression progression;
	
	public IPriceStrategy getPriceStrategy() {
		if (priceStrategy == null) {
			priceStrategy = new InvoicePriceStrategy();
		}
		return priceStrategy;
	}

	public FinanceGenerator getFinanceGenerator() {
		if (financeGenerator == null) {
			financeGenerator = new FinanceGenerator();
		}
		return financeGenerator;
	}

	public void setProgression(IProgression progression) {
		this.progression = progression;
	}

	private void updateProgress( int current, int total ) {
		if (this.progression != null) {
			this.progression.setProgressionCurrentValue(Math.round((current * 100.0)/total));
		}
	}
	
	public Invoice invoice(Sales sales, String series, int number, Date issueDate) throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.beginTransaction(sessionName);

			Invoice invoice = createInvoice(sales, series, number, issueDate);
			createInvoiceDetails(invoice, sales);
			double invoiceTotal = getPriceStrategy().getTotalPrice(invoice, invoice);
			if (invoiceTotal != 0) {
				if (sales.getPayMethod() != null && sales.getPayMethod().getId() != null) {
					getFinanceGenerator().generateFinances(invoice, sales, invoiceTotal, true, true);
				} else {
					getFinanceGenerator().generateFinances(invoice, invoiceTotal, true);
				}
			}
			updateSalesStatus(sessionName, sales);

			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);

			return invoice;
		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.error(msg,daoe);
			}
			LOGGER.error(e.getMessage());
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	private Invoice createInvoice(Sales sales, String series, int number, Date issueDate) throws ManagerBeanException {
		Invoice invoice = new Invoice();
		invoice.setProject(sales.getProject());
		invoice.setSeries(StringUtils.isNotBlank(series) ? series : null);
		invoice.setNumber((number > 0) ? number : obtainMaxNumber(series));
		invoice.setRegistry(sales.getCustomer().getRegistry());
		invoice.setRegistryDocument(sales.getCustomer().getRegistry().getDocument());
		invoice.setRegistryDocumentType(sales.getCustomer().getRegistry().getDocumentType());
		invoice.setRegistryDocumentCountry(sales.getCustomer().getRegistry().getDocumentCountry());
		invoice.setRegistryName(sales.getCustomer().getRegistry().getFullName());
		invoice.setRegistryAddress(sales.getShippingAddress());
		invoice.setIssueDate(issueDate);
		invoice.setTaxDate(issueDate);
		invoice.setSecurityLevel(sales.getSecurityLevel());
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setType(InvoiceType.SALES);
		invoice.setComments(StringUtils.isNotBlank(sales.getPurchaseReference()) ? "Ref. compra: " + sales.getPurchaseReference() : null);
		invoice.setScope(sales.getScope());
		invoice.setSeller(sales.getSeller());

		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		invoiceBean.restoreNullSubPOJOs(invoice);
		return (Invoice)invoiceBean.insert(invoice);
	}

	private int obtainMaxNumber(String seriesId) throws ManagerBeanException {
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression("invoice.type", InvoiceType.SALES.ordinal());
    	return SeriesNumberUtil.obtainNumber(seriesId, "Invoice", criteria);
	}

	private void createInvoiceDetails(Invoice invoice, Sales sales) throws ManagerBeanException {
		int line = 0;
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_SALES_ID), sales.getId());
		criteria.addOrder(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_LINE));
		List<ITransferObject> salesDetailList = salesDetailBean.getList(criteria);
		for (ITransferObject ito : salesDetailList) {
			SalesDetail salesDetail = (SalesDetail)ito;
			InvoiceDetail invoiceDetail = new InvoiceDetail();
			invoiceDetail.setInvoice(invoice);
			invoiceDetail.setProject(sales.getProject());
			invoiceDetail.setLine(++line);
			invoiceDetail.setItem(salesDetail.getItem());
			invoiceDetail.setDescription(salesDetail.getDescription());
			invoiceDetail.setQuantity(salesDetail.getQuantity());
			invoiceDetail.setPrice(salesDetail.getPrice());
			invoiceDetail.setDiscountExpression(salesDetail.getDiscountExpression());
			invoiceDetail.setSource(InvoiceSource.SALES);
			invoiceDetail.setSourceId(salesDetail.getId());
			invoiceDetail.setTaxableBase(getPriceStrategy().getBasePrice(invoiceDetail));
			invoiceDetail.setSeller(sales.getSeller());
			invoiceDetail.setWorkPlace(sales.getWorkPlace());
			invoiceDetail.getInvoice().setUpdateEnabled(line == salesDetailList.size());
			invoiceDetailBean.restoreNullSubPOJOs(invoiceDetail);
			invoiceDetailBean.insert(invoiceDetail);
			updateProgress(line, salesDetailList.size());
		}
	}

	private void updateSalesStatus(String sessionName, Sales sales) throws ManagerBeanException {
		IManagerBean salesBean = BeanManager.getManagerBean(Sales.class);
		sales.setStatus(SalesStatus.INVOICED);
		salesBean.restoreNullSubPOJOs(sales);
		sales = (Sales)HibernateUtil.getSession(sessionName).merge(sales);	
		salesBean.update(sales);
	}

}
