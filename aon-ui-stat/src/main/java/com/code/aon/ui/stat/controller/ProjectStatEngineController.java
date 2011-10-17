package com.code.aon.ui.stat.controller;

import java.util.Iterator;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.commercial.Offer;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.groupware.DailyTracking;
import com.code.aon.groupware.dao.IGroupwareAlias;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;

public class ProjectStatEngineController {

	private String backAction;

	private Project project;
	private IPriceStrategy invoicePriceStrategy;
	private IPriceStrategy priceStrategy;
	private DataModel approvedOfferModel;
	private DataModel saleInvoiceModel;
	private DataModel costInvoiceModel;
	private DataModel dailyTrackingModel;
	private List<Offer> approvedOfferList;
	private List<Invoice> saleInvoiceList;
	private List<Invoice> costInvoiceList;
	private List<DailyTracking> dailyTrackingList;
	private double totalOffered;
	private double totalSales;
	private double totalCosts;
	
	public String getBackAction() {
		return backAction;
	}
	public void setBackAction(String backAction) {
		this.backAction = backAction;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public IPriceStrategy getInvoicePriceStrategy() {
		if (invoicePriceStrategy == null) {
			invoicePriceStrategy = new InvoicePriceStrategy();
		}
		return invoicePriceStrategy;
	}

	public IPriceStrategy getPriceStrategy() {
		if (priceStrategy == null) {
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
	}

	public DataModel getApprovedOfferModel() {
		if (approvedOfferModel == null) {
			approvedOfferModel = new ListDataModel(getApprovedOfferList());
		}
		return approvedOfferModel;
	}

	public void setApprovedOfferModel(DataModel approvedOfferModel) {
		this.approvedOfferModel = approvedOfferModel;
	}

	public DataModel getSaleInvoiceModel() {
		if (saleInvoiceModel == null) {
			saleInvoiceModel = new ListDataModel(getSaleInvoiceList());
		}
		return saleInvoiceModel;
	}

	public void setSaleInvoiceModel(DataModel saleInvoiceModel) {
		this.saleInvoiceModel = saleInvoiceModel;
	}

	public DataModel getCostInvoiceModel() {
		if (costInvoiceModel == null) {
			costInvoiceModel = new ListDataModel(getCostInvoiceList());
		}
		return costInvoiceModel;
	}

	public void setCostInvoiceModel(DataModel costInvoiceModel) {
		this.costInvoiceModel = costInvoiceModel;
	}
	
	public DataModel getDailyTrackingModel() {
		if (dailyTrackingModel == null) {
			dailyTrackingModel = new ListDataModel(getDailyTrackingList());
		}
		return dailyTrackingModel;
	}
	public void setDailyTrackingModel(DataModel dailyTrackingModel) {
		this.dailyTrackingModel = dailyTrackingModel;
	}

	public List<Offer> getApprovedOfferList() {
		try {
			if (approvedOfferList == null) {
				approvedOfferList = getApprovedOffers();
			}
			return approvedOfferList;
		} catch (ManagerBeanException e) {
			String msg = "Unable to load data.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	public void setApprovedOfferList(List<Offer> approvedOfferList) {
		this.approvedOfferList = approvedOfferList;
	}

	public List<Invoice> getSaleInvoiceList() {
		try {
			if (saleInvoiceList == null) {
				saleInvoiceList = getSaleInvoices();
			}
			return saleInvoiceList;
		} catch (ManagerBeanException e) {
			String msg = "Unable to load data.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	public void setSaleInvoiceList(List<Invoice> saleInvoiceList) {
		this.saleInvoiceList = saleInvoiceList;
	}

	public List<Invoice> getCostInvoiceList() {
		try {
			if (costInvoiceList == null) {
				costInvoiceList = getCostInvoices();
			}
			return costInvoiceList;
		} catch (ManagerBeanException e) {
			String msg = "Unable to load data.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	public List<DailyTracking> getDailyTrackingList() {
		try {
			if (dailyTrackingList == null) {
				dailyTrackingList = getDailyTrackings();
			}
			return dailyTrackingList;
		} catch (ManagerBeanException e) {
			String msg = "Unable to load data.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	public void setCostInvoiceList(List<Invoice> costInvoiceList) {
		this.costInvoiceList = costInvoiceList;
	}

	public double getTotalOffered() {
		if (totalOffered == 0) {
			for (Offer offer : getApprovedOfferList()) {
				totalOffered += CommonUtil.round(getPriceStrategy().getTotalPrice(offer, offer.getTarget()));
			}
		}
		return CommonUtil.round(totalOffered);
	}

	public void setTotalOffered(double totalOffered) {
		this.totalOffered = totalOffered;
	}

	public double getTotalSales() {
		if (totalSales == 0) {
			for (Invoice invoice : getSaleInvoiceList()) {
				totalSales += CommonUtil.round(getInvoicePriceStrategy().getTotalPrice(invoice, invoice));
			}
		}
		return CommonUtil.round(totalSales);
	}

	public void setTotalSales(double totalSales) {
		this.totalSales = totalSales;
	}

	public double getTotalCosts() {
		if (totalCosts == 0) {
			for (Invoice invoice : getCostInvoiceList()) {
				totalCosts += CommonUtil.round(getInvoicePriceStrategy().getTotalPrice(invoice, invoice));
			}
		}
		return CommonUtil.round(totalCosts);
	}

	public void setTotalCosts(double totalCosts) {
		this.totalCosts = totalCosts;
	}

	public double getTotalResult() {
		return CommonUtil.round(getTotalSales() - getTotalCosts());
	}

	@SuppressWarnings("unchecked")
	public List<Offer> getApprovedOffers() throws ManagerBeanException {
		String select = "select Offer from Offer as Offer where Offer.status in (1, 4) AND Offer.project.id = " + project.getId() +
						" order by Offer.issueDate desc";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createQuery(select);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<Invoice> getSaleInvoices() throws ManagerBeanException {
		String select = "select Invoice from Invoice as Invoice where Invoice.type = 1 AND Invoice.project.id = " + project.getId() + 
						" order by Invoice.issueDate desc";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createQuery(select);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<Invoice> getCostInvoices() throws ManagerBeanException {
		String select = "select Invoice from Invoice as Invoice where Invoice.type <> 1 AND Invoice.project.id = " + project.getId() + 
						" order by Invoice.issueDate desc";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createQuery(select);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<DailyTracking> getDailyTrackings() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(DailyTracking.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IGroupwareAlias.DAILY_TRACKING_PROJECT_ID), project.getId());
		List<?> list = bean.getList(criteria); 
		return (List<DailyTracking>) list;
	}

	public void getProjectData() {
		setApprovedOfferModel(null);
		setSaleInvoiceModel(null);
		setCostInvoiceModel(null);
		setApprovedOfferList(null);
		setSaleInvoiceList(null);
		setCostInvoiceList(null);
		setTotalOffered(0);
		setTotalSales(0);
		setTotalCosts(0);
	}

	public double getApprovedOfferTotal() throws ManagerBeanException {
		Offer offer = (Offer)getApprovedOfferModel().getRowData();
		return getPriceStrategy().getTotalPrice(offer, offer.getTarget());
	}

	public double getSaleInvoiceTotal() throws ManagerBeanException {
		Invoice invoice = (Invoice)getSaleInvoiceModel().getRowData();
		return getInvoicePriceStrategy().getTotalPrice(invoice, invoice);
	}

	public double getCostInvoiceTotal() throws ManagerBeanException {
		Invoice invoice = (Invoice)getCostInvoiceModel().getRowData();
		return getInvoicePriceStrategy().getTotalPrice(invoice, invoice);
	}

	public FinanceStatus getSaleInvoiceFinanceStatus() throws ManagerBeanException {
		Invoice invoice = ((Invoice)this.getSaleInvoiceModel().getRowData());
		return getInvoiceFinanceStatus(invoice);
	}

	public FinanceStatus getCostInvoiceFinanceStatus() throws ManagerBeanException {
		Invoice invoice = ((Invoice)this.getCostInvoiceModel().getRowData());
		return getInvoiceFinanceStatus(invoice);
	}

	private FinanceStatus getInvoiceFinanceStatus(Invoice invoice) throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_INVOICE_ID), invoice.getId());
		Iterator<?> iterator = financeBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			Finance finance = (Finance)iterator.next();
			if (FinanceStatus.PAID != finance.getFinanceStatus() && FinanceStatus.SETTLED != finance.getFinanceStatus()) {
				return FinanceStatus.PENDING;
			}
		}
		return (financeBean.getCount(criteria) == 0) ? FinanceStatus.PENDING : FinanceStatus.PAID;
	}
	
	public String backAction() {
		if (StringUtils.isNotBlank( getBackAction() )) {
			return 	getBackAction();
		}
		return "projectCommercial_form";
	}
	
}