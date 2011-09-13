package com.code.aon.ui.stat.controller;

import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.commercial.Offer;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.project.Project;
import com.code.aon.ui.util.AonUtil;

public class ProjectStatEngineController {

	private Project project;
	private IPriceStrategy invoicePriceStrategy;
	private IPriceStrategy priceStrategy;
	private DataModel approvedOfferModel;
	private DataModel saleInvoiceModel;
	private DataModel costInvoiceModel;
	private List<Offer> approvedOfferList;
	private List<Invoice> saleInvoiceList;
	private List<Invoice> costInvoiceList;
	private double totalOffered;
	private double totalSales;
	private double totalCosts;

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






/*
	public double getPendingInvoicesTotalPrice() throws ManagerBeanException {
		Invoice invoice = (Invoice) this.pendingInvoiceModel.getRowData();
		return getPriceStrategy().getTotalPrice(invoice, invoice);
	}

	public double getSalesTotalPrice() throws ManagerBeanException {
		Sales sales = (Sales) this.pendingSalesModel.getRowData();
		return getPriceStrategy2().getTotalPrice(sales, sales.getCustomer());
	}

	public double getOfferTotalPrice() throws ManagerBeanException {
		Offer offer = (Offer) this.getPendingOfferModel().getRowData();
		return getPriceStrategy2().getTotalPrice(offer, offer.getTarget());
	}

	public double getDeliveryTotalPrice() throws ManagerBeanException {
		Delivery delivery = (Delivery) this.pendingDeliveryModel.getRowData();
		return getPriceStrategy2().getTotalPrice(delivery, delivery.getCustomer());
	}

	public DataModel getUnpayedFinanceModel() {
		if (unpayedFinanceModel == null) {
			unpayedFinanceModel = new ListDataModel(getPendingFinanceList());
		}
		return unpayedFinanceModel;
	}

	public void setUnpayedFinanceModel(DataModel model) {
		this.unpayedFinanceModel = model;
	}

	public DataModel getBoughtProductModel() {
		if (boughtProductModel == null) {
			boughtProductModel = new ListDataModel(getBoughtProductList());
		}
		return boughtProductModel;
	}

	public void setBoughtProductModel(DataModel boughtProductModel) {
		this.boughtProductModel = boughtProductModel;
	}

	public DataModel getPendingDeliveryModel() {
		if (pendingDeliveryModel == null) {
			pendingDeliveryModel = new ListDataModel(getPendingDeliveryList());
		}
		return pendingDeliveryModel;
	}

	public void setPendingDeliveryModel(DataModel pendingDeliveryModel) {
		this.pendingDeliveryModel = pendingDeliveryModel;
	}

	public DataModel getPendingSalesModel() {
		if (pendingSalesModel == null) {
			pendingSalesModel = new ListDataModel(getPendingSalesList());
		}
		return pendingSalesModel;
	}

	public void setPendingSalesModel(DataModel pendingSalesModel) {
		this.pendingSalesModel = pendingSalesModel;
	}

	public List<Finance> getPendingFinanceList() {
		try {
			if (pendingFinanceList == null) {
				getPendingFinances();
			}
			return pendingFinanceList;
		} catch (ManagerBeanException e) {
			String msg = "Unable to load data.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	public void setPendingFinanceList(List<Finance> list) {
		this.pendingFinanceList = list;
	}

	public List<InvoiceDetail> getBoughtProductList() {
		try {
			if (boughtProductList == null) {
				getBoughtProducts();
			}
			return boughtProductList;
		} catch (ManagerBeanException e) {
			String msg = "Unable to load data.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	public void setBoughtProductList(List<InvoiceDetail> boughtProductList) {
		this.boughtProductList = boughtProductList;
	}

	public List<Delivery> getPendingDeliveryList() {
		try {
			if (pendingDeliveryList == null) {
				getPendingDeliveries();
			}
			return pendingDeliveryList;
		} catch (ManagerBeanException e) {
			String msg = "Unable to load data.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	public void setPendingDeliveryList(List<Delivery> pendingDeliveryList) {
		this.pendingDeliveryList = pendingDeliveryList;
	}

	public List<Sales> getPendingSalesList() {
		try {
			if (pendingSalesList == null) {
				getPendingSales();
			}
			return pendingSalesList;
		} catch (ManagerBeanException e) {
			String msg = "Unable to load data.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	public void setPendingSalesList(List<Sales> pendingSalesList) {
		this.pendingSalesList = pendingSalesList;
	}

	public void onRegistryStats(ActionEvent event) {
		try {
			setPendingInvoiceModel(null);
			setUnpayedFinanceModel(null);
			setBoughtProductModel(null);
			setPendingDeliveryModel(null);
			setPendingSalesModel(null);
			setPendingOfferModel(null);
			setPendingInvoiceList(null);
			setPendingFinanceList(null);
			setBoughtProductList(null);
			setPendingDeliveryList(null);
			setPendingSalesList(null);
			setPendingOfferList(null);
			getInvoices();
			getPendingFinances();
			getBoughtProducts();
			getPendingDeliveries();
			getPendingSales();
			getPendingOffers();
		} catch (ManagerBeanException e) {
			String msg = "Unable to load data.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	@SuppressWarnings("unchecked")
	public void getPendingFinances() throws ManagerBeanException {
		String select = "select Finance " + "from Finance as Finance " + "where Finance.invoice.type=1 AND Finance.financeStatus = 0 AND Finance.invoice.registry.id = "
				+ registry.getId() + "order by Finance.invoice.issueDate desc";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createQuery(select);
		pendingFinanceList = query.list();
	}

	@SuppressWarnings("unchecked")
	public void getBoughtProducts() throws ManagerBeanException {
		String select = "select distinct(InvoiceDetail) " + "from InvoiceDetail as InvoiceDetail "
				+ "where InvoiceDetail.invoice.type = 1 AND InvoiceDetail.invoice.registry.id = " + registry.getId()
				+ "order by InvoiceDetail.invoice.issueDate desc";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createQuery(select);
		boughtProductList = query.list();
	}

	@SuppressWarnings("unchecked")
	public void getPendingDeliveries() throws ManagerBeanException {
		String select = "select Delivery " + "from Delivery as Delivery " + "where Delivery.status = 0 AND Delivery.customer.registry.id = "
				+ registry.getId() + "order by Delivery.issueTime desc";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createQuery(select);
		pendingDeliveryList = query.list();
	}

	@SuppressWarnings("unchecked")
	public void getPendingSales() throws ManagerBeanException {
		String select = "select Sales " + "from Sales as Sales " + "where Sales.status = 0 AND Sales.customer.registry.id = " + registry.getId()
				+ "order by Sales.issueDate desc";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createQuery(select);
		pendingSalesList = query.list();
	}

	public String getRowId() {
		Invoice invoice = (Invoice) this.pendingInvoiceModel.getRowData();
		return invoice.getReferenceCode();
	}
	
	public FinanceStatus getFinanceStatus() throws ManagerBeanException {
		Invoice invoice = ((Invoice) this.getPendingInvoiceModel().getRowData());
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_INVOICE_ID), invoice.getId());
		Iterator<ITransferObject> iterator = financeBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			Finance finance = (Finance)iterator.next();
			if (FinanceStatus.PAID != finance.getFinanceStatus() && FinanceStatus.SETTLED != finance.getFinanceStatus()) {
				return FinanceStatus.PENDING;
			}
		}
		return (financeBean.getCount(criteria) == 0) ? FinanceStatus.PENDING : FinanceStatus.PAID;
	}	
	
	public void onInvoicePdf(ActionEvent event) throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_ID), ((Invoice) this.getPendingInvoiceModel().getRowData()).getId());
		FormUtil.getController("invoicePrint").setCriteria(criteria);
	}
	public void onFinancePdf(ActionEvent event) throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_ID), ((Finance) this.getUnpayedFinanceModel().getRowData()).getInvoice().getId());
		FormUtil.getController("invoicePrint").setCriteria(criteria);
	}
	public void onDeliveryPdf(ActionEvent event) throws ManagerBeanException {
		IManagerBean deliveryBean = BeanManager.getManagerBean(Delivery.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_ID), ((Delivery) this.getPendingDeliveryModel().getRowData()).getId());
		FormUtil.getController("delivery").setCriteria(criteria);
	}
	public void onSalesPdf(ActionEvent event) throws ManagerBeanException {
		IManagerBean salesBean = BeanManager.getManagerBean(Sales.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(salesBean.getFieldName(ISalesAlias.SALES_ID), ((Sales) this.getPendingSalesModel().getRowData()).getId());
		FormUtil.getController("sales").setCriteria(criteria);
	}
	public void onOfferPdf(ActionEvent event) throws ManagerBeanException {
		IManagerBean offerBean = BeanManager.getManagerBean(Offer.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(offerBean.getFieldName(ICommercialAlias.OFFER_ID), ((Offer) this.getPendingOfferModel().getRowData()).getId());
		FormUtil.getController("offer").setCriteria(criteria);
	}
*/	
}