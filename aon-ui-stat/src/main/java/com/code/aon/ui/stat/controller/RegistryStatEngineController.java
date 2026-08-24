package com.code.aon.ui.stat.controller;

import static com.code.aon.ui.stat.controller.IStatConstants.DELIVERY_CONTROLLER_NAME;
import static com.code.aon.ui.stat.controller.IStatConstants.OFFER_CONTROLLER_NAME;
import static com.code.aon.ui.stat.controller.IStatConstants.SALES_CONTROLLER_NAME;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import org.hibernate.Query;
import org.hibernate.Session;
import org.json.JSONObject;

import com.code.aon.AonVersion;
import com.code.aon.commercial.Offer;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.sales.Sales;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Delivery;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.IJsonNames;

public class RegistryStatEngineController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Registry registry;
	private DataScrollerState pendingInvoiceState;
	private DataScrollerState unpayedFinanceState;
	private DataScrollerState boughtProductState;
	private DataScrollerState pendingDeliveryState;
	private DataScrollerState pendingSalesState;
	private DataScrollerState pendingOfferState;
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
		Invoice invoice = (Invoice) getPendingInvoiceModel().getRowData();
		return getPriceStrategy().getTotalPrice(invoice, invoice);
	}

	public double getSalesTotalPrice() throws ManagerBeanException {
		Sales sales = (Sales) getPendingSalesModel().getRowData();
		return getPriceStrategy2().getTotalPrice(sales, sales.getCustomer());
	}

	public double getOfferTotalPrice() throws ManagerBeanException {
		Offer offer = (Offer) this.getPendingOfferModel().getRowData();
		return getPriceStrategy2().getTotalPrice(offer, offer.getTarget());
	}

	public double getDeliveryTotalPrice() throws ManagerBeanException {
		Delivery delivery = (Delivery) getPendingDeliveryModel().getRowData();
		return getPriceStrategy2().getTotalPrice(delivery, delivery.getCustomer());
	}

	public DataModel getPendingInvoiceModel() {
		return getPendingInvoiceState().getDirectModel();
	}

	public void setPendingInvoiceModel(DataModel pendingInvoiceModel) {
		if ( pendingInvoiceModel == null ) {
			setPendingInvoiceState(null);
		} else {
			getPendingInvoiceState().setModel(pendingInvoiceModel);
		}				
	}
	
	public DataScrollerState getPendingInvoiceState() {
		if (pendingInvoiceState == null) {
			pendingInvoiceState = new DataScrollerState(new SerializableListDataModel(getPendingInvoiceList()), "pending");
		}								
		return pendingInvoiceState;
	}

	public void setPendingInvoiceState(DataScrollerState pendingInvoiceState) {
		this.pendingInvoiceState = pendingInvoiceState;
	}

	public DataModel getUnpayedFinanceModel() {
		return getUnpayedFinanceState().getDirectModel();
	}

	public void setUnpayedFinanceModel(DataModel model) {
		if ( model == null ) {
			setUnpayedFinanceState(null);
		} else {
			getUnpayedFinanceState().setModel(model);
		}				
	}
	
	public DataScrollerState getUnpayedFinanceState() {
		if (unpayedFinanceState == null) {
			unpayedFinanceState = new DataScrollerState(new SerializableListDataModel(getPendingFinanceList()), "scored");
		}						
		return unpayedFinanceState;
	}

	public void setUnpayedFinanceState(DataScrollerState unpayedFinanceState) {
		this.unpayedFinanceState = unpayedFinanceState;
	}

	public DataModel getBoughtProductModel() {
		return getBoughtProductState().getDirectModel();
	}

	public void setBoughtProductModel(DataModel boughtProductModel) {
		if ( boughtProductModel == null ) {
			setBoughtProductState(null);
		} else {
			getBoughtProductState().setModel(boughtProductModel);
		}				
	}
	
	public DataScrollerState getBoughtProductState() {
		if (boughtProductState == null) {
			boughtProductState = new DataScrollerState(new SerializableListDataModel(getBoughtProductList()), "products");
		}				
		return boughtProductState;
	}

	public void setBoughtProductState(DataScrollerState boughtProductState) {
		this.boughtProductState = boughtProductState;
	}

	public DataModel getPendingDeliveryModel() {
		return getPendingDeliveryState().getDirectModel();
	}

	public void setPendingDeliveryModel(DataModel pendingDeliveryModel) {
		if ( pendingDeliveryModel == null ) {
			setPendingDeliveryState(null);
		} else {
			getPendingDeliveryState().setModel(pendingDeliveryModel);
		}				
	}

	public DataScrollerState getPendingDeliveryState() {
		if (pendingDeliveryState == null) {
			pendingDeliveryState = new DataScrollerState(new SerializableListDataModel(getPendingDeliveryList()), "deliveries");
		}						
		return pendingDeliveryState;
	}

	public void setPendingDeliveryState(DataScrollerState pendingDeliveryState) {
		this.pendingDeliveryState = pendingDeliveryState;
	}

	public DataModel getPendingSalesModel() {
		return getPendingSalesState().getDirectModel();
	}

	public void setPendingSalesModel(DataModel pendingSalesModel) {
		if ( pendingSalesModel == null ) {
			setPendingSalesState(null);
		} else {
			getPendingSalesState().setModel(pendingSalesModel);
		}						
	}

	public DataScrollerState getPendingSalesState() {
		if (pendingSalesState == null) {
			pendingSalesState = new DataScrollerState(new SerializableListDataModel(getPendingSalesList()), "sales");
		}						
		return pendingSalesState;
	}

	public void setPendingSalesState(DataScrollerState pendingSalesState) {
		this.pendingSalesState = pendingSalesState;
	}

	public DataModel getPendingOfferModel() {
		return getPendingOfferState().getDirectModel();
	}

	public void setPendingOfferModel(DataModel pendingOfferModel) {
		if ( pendingOfferModel == null ) {
			setPendingOfferState(null);
		} else {
			getPendingOfferState().setModel(pendingOfferModel);
		}						
	}
	
	public DataScrollerState getPendingOfferState() {
		if (pendingOfferState == null) {
			pendingOfferState = new DataScrollerState(new SerializableListDataModel(getPendingOfferList()), "offer");
		}								
		return pendingOfferState;
	}

	public void setPendingOfferState(DataScrollerState pendingOfferState) {
		this.pendingOfferState = pendingOfferState;
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
		String select = "select Invoice " + "from Invoice as Invoice " 
				+ "where "+DomainManager.getSQLWhereClause("Invoice.domain")
				+ " AND Invoice.type=1"
				+ " AND (Invoice.annulled=0 or Invoice.annulled is null)"
				+ " AND Invoice.registry.id = " + registry.getId() 
				+ " order by Invoice.issueDate desc";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createQuery(select);
		pendingInvoiceList = query.list();
	}

	@SuppressWarnings("unchecked")
	public void getPendingFinances() throws ManagerBeanException {
		String select = "select Finance " + "from Finance as Finance " + "where "+DomainManager.getSQLWhereClause("Finance.domain")+" AND Finance.invoice.type=1 AND Finance.financeStatus = 0 AND Finance.invoice.registry.id = "
				+ registry.getId() + "order by Finance.invoice.issueDate desc";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createQuery(select);
		pendingFinanceList = query.list();
	}

	@SuppressWarnings("unchecked")
	public void getBoughtProducts() throws ManagerBeanException {
		String select = "select distinct(InvoiceDetail) " + "from InvoiceDetail as InvoiceDetail "
				+ "where "+DomainManager.getSQLWhereClause("InvoiceDetail.domain")
				+ " AND InvoiceDetail.invoice.type = 1"
				+ " AND (InvoiceDetail.invoice.annulled=0 or InvoiceDetail.invoice.annulled is null)"
				+ " AND InvoiceDetail.invoice.registry.id = " + registry.getId()
				+ " order by InvoiceDetail.invoice.issueDate desc";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createQuery(select);
		boughtProductList = query.list();
	}

	@SuppressWarnings("unchecked")
	public void getPendingDeliveries() throws ManagerBeanException {
		String select = "select Delivery from Delivery as Delivery where "+DomainManager.getSQLWhereClause("Delivery.domain")+" AND Delivery.status = 0 AND Delivery.customer.registry.id = "
				+ registry.getId() + "order by Delivery.issueTime desc";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createQuery(select);
		pendingDeliveryList = query.list();
	}

	@SuppressWarnings("unchecked")
	public void getPendingSales() throws ManagerBeanException {
		String select = "select Sales " + "from Sales as Sales " + "where "+DomainManager.getSQLWhereClause("Sales.domain")+" AND Sales.status = 0 AND Sales.customer.registry.id = " + registry.getId()
				+ "order by Sales.issueDate desc";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createQuery(select);
		pendingSalesList = query.list();
	}

	@SuppressWarnings("unchecked")
	public void getPendingOffers() throws ManagerBeanException {

		String select = "select Offer " + "from Offer as Offer " + "where "+DomainManager.getSQLWhereClause("Offer.domain")+" AND Offer.status = 0 AND Offer.target.registry.id = " + registry.getId()
				+ "order by Offer.issueDate desc";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createQuery(select);
		pendingOfferList = query.list();
	}

	public String getRowId() {
		Invoice invoice = (Invoice) getPendingInvoiceModel().getRowData();
		return invoice.getReferenceCode();
	}
	
	public FinanceStatus getFinanceStatus() throws ManagerBeanException {
		Invoice invoice = ((Invoice) this.getPendingInvoiceModel().getRowData());
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_ID), invoice.getId());
		Iterator<ITransferObject> iterator = financeBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			Finance finance = (Finance)iterator.next();
			if (!finance.isPaid() && !finance.isSettled()) {
				return FinanceStatus.PENDING;
			}
		}
		return (financeBean.getCount(criteria) == 0) ? FinanceStatus.PENDING : FinanceStatus.PAID;
	}	
	
	public void onInvoicePdf(ActionEvent event) throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_ID), ((Invoice) this.getPendingInvoiceModel().getRowData()).getId());
		FormUtil.getController("invoicePrint").setCriteria(criteria);
	}
	public void onFinancePdf(ActionEvent event) throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_ID), ((Finance) this.getUnpayedFinanceModel().getRowData()).getInvoice().getId());
		FormUtil.getController("invoicePrint").setCriteria(criteria);
	}
	public void onDeliveryPdf(ActionEvent event) throws ManagerBeanException {
		Integer deliveryId = ((Delivery) this.getPendingDeliveryModel().getRowData()).getId();
		BasicController controller = (BasicController) FormUtil.getController(DELIVERY_CONTROLLER_NAME);
		controller.select(event, deliveryId);
	}
	public void onSalesPdf(ActionEvent event) throws ManagerBeanException {
		IManagerBean salesBean = BeanManager.getManagerBean(Sales.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(salesBean.getFieldName(IEntityAlias.SALES_ID), ((Sales) this.getPendingSalesModel().getRowData()).getId());
		FormUtil.getController(SALES_CONTROLLER_NAME).setCriteria(criteria);
	}

	public void onOfferPdf(ActionEvent event) throws ManagerBeanException {
		Integer offerId = ((Offer) this.getPendingOfferModel().getRowData()).getId();
		BasicController controller = (BasicController) FormUtil.getController(OFFER_CONTROLLER_NAME);
		controller.select(event, offerId);
	}

	public String getDownloadURL(Integer id) {
		com.esferalia.aon.occam.api.model.Domain domain = AON.getDomain(AonUtil.getDomainName(), DomainManager.getCurrentDomain(), "");
		JSONObject json = new JSONObject()
				.put(IJsonNames.ID, id)
				.put(IJsonNames.SOURCE, "invoice")
				.put("domain_id", domain.getId())
				.put("domain_name", domain.getName())
				.put(IJsonNames.LOGIN, UserUtils.getInstance().getLoggedUser().getLogin());		
		return "/ms/api/download_invoice_pdf?json=" + Base64.getEncoder().encodeToString(json.toString().getBytes(StandardCharsets.UTF_8));
	}
	
}