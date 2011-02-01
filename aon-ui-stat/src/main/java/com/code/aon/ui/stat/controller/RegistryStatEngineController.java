package com.code.aon.ui.stat.controller;

import java.util.Iterator;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.sales.Sales;
import com.code.aon.sales.dao.ISalesAlias;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.dao.IWarehouseAlias;

public class RegistryStatEngineController {

	private Registry registry;
	private DataModel pendingInvoiceModel;
	private DataModel unpayedFinanceModel;
	private DataModel boughtProductModel;
	private DataModel pendingDeliveryModel;
	private DataModel pendingSalesModel;
	private DataModel pendingOfferModel;
	private List<Invoice> pendingInvoiceList;
	private List<Finance> pendingFinanceList;
	private List<InvoiceDetail> boughtProductList;
	private List<Delivery> pendingDeliveryList;
	private List<Sales> pendingSalesList;
	private List<Offer> pendingOfferList;
	private IPriceStrategy priceStrategy;
	private IPriceStrategy priceStrategy2;

	public IPriceStrategy getPriceStrategy() {
		if (priceStrategy == null) {
			priceStrategy = new InvoicePriceStrategy();
		}
		return priceStrategy;
	}

	public IPriceStrategy getPriceStrategy2() {
		if (priceStrategy2 == null) {
			priceStrategy2 = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy2;
	}

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

	public DataModel getPendingInvoiceModel() {
		if (pendingInvoiceModel == null) {
			pendingInvoiceModel = new ListDataModel(getPendingInvoiceList());
		}
		return pendingInvoiceModel;
	}

	public void setPendingInvoiceModel(DataModel pendingInvoiceModel) {
		this.pendingInvoiceModel = pendingInvoiceModel;
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

	public DataModel getPendingOfferModel() {
		if (pendingOfferModel == null) {
			pendingOfferModel = new ListDataModel(getPendingOfferList());
		}
		return pendingOfferModel;
	}

	public void setPendingOfferModel(DataModel pendingOfferModel) {
		this.pendingOfferModel = pendingOfferModel;
	}

	public Registry getRegistry() {
		return registry;
	}

	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	public List<Invoice> getPendingInvoiceList() {
		try {
			if (pendingInvoiceList == null) {
				getInvoices();
			}
			return pendingInvoiceList;
		} catch (ManagerBeanException e) {
			String msg = "Unable to load data.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	public void setPendingInvoiceList(List<Invoice> pendingInvoiceList) {
		this.pendingInvoiceList = pendingInvoiceList;
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

	public List<Offer> getPendingOfferList() {
		try {
			if (pendingOfferList == null) {
				getPendingOffers();
			}
			return pendingOfferList;
		} catch (ManagerBeanException e) {
			String msg = "Unable to load data.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	public void setPendingOfferList(List<Offer> pendingOfferList) {
		this.pendingOfferList = pendingOfferList;
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

	public void getRegistryData() {
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
	}

	@SuppressWarnings("unchecked")
	public void getInvoices() throws ManagerBeanException {
		String select = "select Invoice " + "from Invoice as Invoice " + "where Invoice.type=1  AND Invoice.registry.id = "
				+ registry.getId() + "order by Invoice.issueDate desc";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createQuery(select);
		pendingInvoiceList = query.list();
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

	@SuppressWarnings("unchecked")
	public void getPendingOffers() throws ManagerBeanException {

		String select = "select Offer " + "from Offer as Offer " + "where Offer.status = 0 AND Offer.target.registry.id = " + registry.getId()
				+ "order by Offer.issueDate desc";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createQuery(select);
		pendingOfferList = query.list();
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
	
}