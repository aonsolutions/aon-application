package com.code.aon.finance.bridge.invoicing;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.finance.FinanceGenerator;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.purchase.enumeration.PurchaseStatus;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class PurchaseInvoicingManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseInvoicingManager.class.getName());

	private IPriceStrategy priceStrategy;
	private FinanceGenerator financeGenerator;

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

	public Invoice invoice(Purchase purchase, String referenceCode, Date issueDate) throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.beginTransaction(sessionName);

			Invoice invoice = createInvoice(purchase, referenceCode, issueDate);
			createInvoiceDetails(invoice, purchase);
			double invoiceTotal = getPriceStrategy().getTotalPrice(invoice, invoice);
			if (invoiceTotal != 0) {
				if (purchase.getPayMethod() != null && purchase.getPayMethod().getId() != null) {
					getFinanceGenerator().generateFinances(invoice, purchase, invoiceTotal, true);
				} else {
					getFinanceGenerator().generateFinances(invoice, invoiceTotal, true);
				}
			}
			updatePurchaseStatus(sessionName, purchase);

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

	private Invoice createInvoice(Purchase purchase, String referenceCode, Date issueDate) throws ManagerBeanException {
		Invoice invoice = new Invoice();
		invoice.setProject(purchase.getProject());
		invoice.setReferenceCode(referenceCode);
		invoice.setRegistry(purchase.getSupplier().getRegistry());
		invoice.setRegistryDocument(purchase.getSupplier().getRegistry().getDocument());
		invoice.setRegistryDocumentType(purchase.getSupplier().getRegistry().getDocumentType());
		invoice.setRegistryDocumentCountry(purchase.getSupplier().getRegistry().getDocumentCountry());
		invoice.setRegistryName(purchase.getSupplier().getRegistry().getFullName());
		invoice.setRegistryAddress(purchase.getRegistryAddress());
		invoice.setIssueDate(issueDate);
		invoice.setTaxDate(issueDate);
		invoice.setSecurityLevel(purchase.getSecurityLevel());
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setType(InvoiceType.PURCHASE);
		invoice.setScope(purchase.getScope());

		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		invoiceBean.restoreNullSubPOJOs(invoice);
		return (Invoice)invoiceBean.insert(invoice);
	}

	private void createInvoiceDetails(Invoice invoice, Purchase purchase) throws ManagerBeanException {
		int line = 0;
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_PURCHASE_ID), purchase.getId());
		criteria.addOrder(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_LINE));
		List<ITransferObject> purchaseDetailList = purchaseDetailBean.getList(criteria);
		for (ITransferObject ito : purchaseDetailList) {
			PurchaseDetail purchaseDetail = (PurchaseDetail)ito;
			InvoiceDetail invoiceDetail = new InvoiceDetail();
			invoiceDetail.setInvoice(invoice);
			invoiceDetail.setProject(purchaseDetail.getProject());
			invoiceDetail.setLine(++line);
			invoiceDetail.setItem(purchaseDetail.getItem());
			invoiceDetail.setDescription(purchaseDetail.getDescription());
			invoiceDetail.setQuantity(purchaseDetail.getQuantity());
			invoiceDetail.setPrice(purchaseDetail.getPrice());
			invoiceDetail.setDiscountExpression(purchaseDetail.getDiscountExpression());
			invoiceDetail.setWorkPlace(purchase.getWorkPlace());
			invoiceDetail.setSource(InvoiceSource.PURCHASE);
			invoiceDetail.setSourceId(purchaseDetail.getId());
			invoiceDetail.setTaxableBase(getPriceStrategy().getBasePrice(invoiceDetail));
			invoiceDetail.getInvoice().setUpdateEnabled(line == purchaseDetailList.size());
			invoiceDetailBean.restoreNullSubPOJOs(invoiceDetail);
			invoiceDetailBean.insert(invoiceDetail);
		}
	}

	private void updatePurchaseStatus(String sessionName, Purchase purchase) throws ManagerBeanException {
		IManagerBean purchaseBean = BeanManager.getManagerBean(Purchase.class);
		purchase.setStatus(PurchaseStatus.INVOICED);
		purchaseBean.restoreNullSubPOJOs(purchase);
		purchase = (Purchase)HibernateUtil.getSession(sessionName).merge(purchase);	
		purchaseBean.update(purchase);
	}

}
