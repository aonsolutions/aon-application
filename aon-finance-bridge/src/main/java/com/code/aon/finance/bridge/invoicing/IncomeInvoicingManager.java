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
import com.code.aon.finance.invoicing.ProgressionInvoicingFeedBack;
import com.code.aon.finance.invoicing.engine.IInvoicingEngine;
import com.code.aon.finance.invoicing.engine.InvoicingEngineFactory;
import com.code.aon.finance.invoicing.engine.income.IncomeInvoicingDAO;
import com.code.aon.finance.invoicing.engine.income.IncomeInvoicingEngine;
import com.code.aon.finance.invoicing.finance.FinanceGenerator;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.ql.Criteria;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.IncomeDetail;
import com.code.aon.warehouse.enumeration.IncomeStatus;
import com.esferalia.aon.entity.IEntityAlias;

public class IncomeInvoicingManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(IncomeInvoicingManager.class.getName());

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

	public Invoice invoice(Income income, String referenceCode, Date issueDate) throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.beginTransaction(sessionName);

			Invoice invoice = createInvoice(income, referenceCode, issueDate);
			createInvoiceDetails(invoice, income);
			double invoiceTotal = getPriceStrategy().getTotalPrice(invoice, invoice);
			if (invoiceTotal != 0) {
				if (income.getPayMethod() != null && income.getPayMethod().getId() != null) {
					getFinanceGenerator().generateFinances(invoice, income, invoiceTotal, true, true);
				} else {
					getFinanceGenerator().generateFinances(invoice, invoiceTotal, true);
				}
			}
			updateIncomeStatus(sessionName, income);

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

	private Invoice createInvoice(Income income, String referenceCode, Date issueDate) throws ManagerBeanException {
		Invoice invoice = new Invoice();
		invoice.setProject(income.getProject());
		invoice.setReferenceCode(referenceCode);
		invoice.setRegistry(income.getSupplier().getRegistry());
		invoice.setRegistryDocument(income.getSupplier().getRegistry().getDocument());
		invoice.setRegistryDocumentType(income.getSupplier().getRegistry().getDocumentType());
		invoice.setRegistryDocumentCountry(income.getSupplier().getRegistry().getDocumentCountry());
		invoice.setRegistryName(income.getSupplier().getRegistry().getFullName());
		invoice.setRegistryAddress(income.getRegistryAddress());
		invoice.setIssueDate(issueDate);
		invoice.setTaxDate(issueDate);
		invoice.setSecurityLevel(income.getSecurityLevel());
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setType(InvoiceType.PURCHASE);
		invoice.setScope(income.getScope());

		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		invoiceBean.restoreNullSubPOJOs(invoice);
		return (Invoice)invoiceBean.insert(invoice);
	}

	private void createInvoiceDetails(Invoice invoice, Income income) throws ManagerBeanException {
		int line = 0;
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(incomeDetailBean.getFieldName(IEntityAlias.INCOME_DETAIL_INCOME_ID), income.getId());
		criteria.addOrder(incomeDetailBean.getFieldName(IEntityAlias.INCOME_DETAIL_LINE));
		List<ITransferObject> incomeDetailList = incomeDetailBean.getList(criteria);
		for (ITransferObject ito : incomeDetailList) {
			IncomeDetail incomeDetail = (IncomeDetail)ito;
			InvoiceDetail invoiceDetail = new InvoiceDetail();
			invoiceDetail.setInvoice(invoice);
			invoiceDetail.setProject(incomeDetail.getProject());
			invoiceDetail.setLine(++line);
			invoiceDetail.setItem(incomeDetail.getItem());
			invoiceDetail.setDescription(incomeDetail.getDescription());
			invoiceDetail.setQuantity(incomeDetail.getQuantity());
			invoiceDetail.setPrice(incomeDetail.getPrice());
			invoiceDetail.setDiscountExpression(incomeDetail.getDiscountExpression());
			invoiceDetail.setWorkPlace(income.getWorkPlace());
			invoiceDetail.setSource(InvoiceSource.INCOME);
			invoiceDetail.setSourceId(incomeDetail.getId());
			invoiceDetail.setTaxableBase(getPriceStrategy().getBasePrice(invoiceDetail));
			invoiceDetail.getInvoice().setUpdateEnabled(line == incomeDetailList.size());
			invoiceDetailBean.restoreNullSubPOJOs(invoiceDetail);
			invoiceDetailBean.insert(invoiceDetail);
		}
	}

	public void transferIncomes(Invoice invoice, List<Income> incomeList, List<ITransferObject> invoicedIncomeList) throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.beginTransaction(sessionName);

			for (ITransferObject ito : invoicedIncomeList) {
				Income income = (Income)ito;
				if (!incomeList.contains(income)) {
					removeInvoicedIncome(invoice, income);
				}
				incomeList.remove(income);
			}

			InvoicingEngineFactory.register(InvoicingEngineFactory.INCOME_ENGINE_KEY, new IncomeInvoicingEngine());
			IInvoicingEngine engine = InvoicingEngineFactory.getInvoicingEngine(InvoicingEngineFactory.INCOME_ENGINE_KEY);
			engine.setInvoicingDAO(new IncomeInvoicingDAO());
			engine.setInvoicingFeedBack(new ProgressionInvoicingFeedBack());
			engine.setHibernateSession(HibernateUtil.getSession(sessionName));
			((IncomeInvoicingEngine)engine).invoiceIncomeList(invoice, incomeList);

			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);
		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.error(msg,daoe);
			}
			LOGGER.error(e.getMessage());
			throw new ManagerBeanException(e.getMessage(),e);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	private void removeInvoicedIncome(Invoice invoice, Income income) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(incomeDetailBean.getFieldName(IEntityAlias.INCOME_DETAIL_INCOME_ID), income.getId());
		List<ITransferObject> incomeDetailList = incomeDetailBean.getList(criteria);
		for (ITransferObject ito : incomeDetailList) {
			IncomeDetail incomeDetail = (IncomeDetail)ito;
			criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_SOURCE), InvoiceSource.INCOME);
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_SOURCE_ID), incomeDetail.getId());
			if (invoiceDetailBean.getList(criteria).iterator().hasNext()) {
				InvoiceDetail invoiceDetail = (InvoiceDetail)invoiceDetailBean.getList(criteria).iterator().next();
				invoiceDetail.setUpdateEnabled(incomeDetailList.indexOf(incomeDetail) == (incomeDetailList.size() - 1));
				invoiceDetailBean.remove(invoiceDetail);
			}
		}
	}

	private void updateIncomeStatus(String sessionName, Income income) throws ManagerBeanException {
		IManagerBean incomeBean = BeanManager.getManagerBean(Income.class);
		income.setStatus(IncomeStatus.INVOICED);
		incomeBean.restoreNullSubPOJOs(income);
		income = (Income)HibernateUtil.getSession(sessionName).merge(income);	
		income = (Income)incomeBean.update(income);
	}

}
