package com.code.aon.ui.stat.controller;

import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.commercial.Offer;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.registry.Registry;
import com.code.aon.sales.Sales;
import com.code.aon.warehouse.Delivery;

public class RegistryStatEngineController {

	private Registry  registry;
	private DataModel pendingInvoiceModel;
	private DataModel scoredInvoiceModel;
	private DataModel boughtProductModel;
	private DataModel pendingDeliveryModel;
	private DataModel pendingSalesModel;
	private DataModel pendingOfferModel;
	private List<Invoice> pendingInvoiceList;
	private List<Invoice> scoredInvoiceList;
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
	
	public IPriceStrategy getPriceStrategy2(){
		if(priceStrategy2 == null){
			priceStrategy2 = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy2;
	}

	public double getPendingInvoicesTotalPrice() throws ManagerBeanException{
		Invoice invoice = (Invoice)this.pendingInvoiceModel.getRowData();
		return getPriceStrategy().getTotalPrice(invoice, invoice);
	}
	
	public double getScoredInvoicesTotalPrice() throws ManagerBeanException{
		Invoice invoice = (Invoice)this.scoredInvoiceModel.getRowData();
		return getPriceStrategy().getTotalPrice(invoice, invoice);
	}
	
	public double getSalesTotalPrice() throws ManagerBeanException {
		Sales sales = (Sales)this.pendingSalesModel.getRowData();
		return getPriceStrategy2().getTotalPrice(sales, sales.getCustomer());
	}
	
	public double getOfferTotalPrice() throws ManagerBeanException {
		Offer offer = (Offer)this.getPendingOfferModel().getRowData();
		return getPriceStrategy2().getTotalPrice(offer, offer.getTarget());
	}
	
	public double getDeliveryTotalPrice() throws ManagerBeanException {
		Delivery delivery = (Delivery)this.pendingDeliveryModel.getRowData();
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
	public DataModel getScoredInvoiceModel() {
		if (scoredInvoiceModel == null) {
			scoredInvoiceModel = new ListDataModel(getScoredInvoiceList());
		}
		return scoredInvoiceModel;
	}
	public void setScoredInvoiceModel(DataModel scoredInvoiceModel) {
		this.scoredInvoiceModel = scoredInvoiceModel;
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
		return pendingInvoiceList;
	}
	public void setPendingInvoiceList(List<Invoice> pendingInvoiceList) {
		this.pendingInvoiceList = pendingInvoiceList;
	}
	public List<Invoice> getScoredInvoiceList() {
		return scoredInvoiceList;
	}
	public void setScoredInvoiceList(List<Invoice> scoredInvoiceList) {
		this.scoredInvoiceList = scoredInvoiceList;
	}
	public List<InvoiceDetail> getBoughtProductList() {
		return boughtProductList;
	}
	public void setBoughtProductList(List<InvoiceDetail> boughtProductList) {
		this.boughtProductList = boughtProductList;
	}
	public List<Delivery> getPendingDeliveryList() {
		return pendingDeliveryList;
	}
	public void setPendingDeliveryList(List<Delivery> pendingDeliveryList) {
		this.pendingDeliveryList = pendingDeliveryList;
	}
	public List<Sales> getPendingSalesList() {
		return pendingSalesList;
	}
	public void setPendingSalesList(List<Sales> pendingSalesList) {
		this.pendingSalesList = pendingSalesList;
	}
	public List<Offer> getPendingOfferList() {
		return pendingOfferList;
	}
	public void setPendingOfferList(List<Offer> pendingOfferList) {
		this.pendingOfferList = pendingOfferList;
	}
	
	public void onRegistryStats(ActionEvent e){
		try {
			setPendingInvoiceModel(null);
			setScoredInvoiceModel(null);
			setBoughtProductModel(null);
			setPendingDeliveryModel(null);
			setPendingSalesModel(null);
			setPendingOfferModel(null);
			getPendingInvoices();
			getScoredInvoices();
			getBoughtProducts();
			getPendingDeliveries();
			getPendingSales();
			getPendingOffers();
		} catch (ManagerBeanException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void getRegistryData(){
		try {
			setPendingInvoiceModel(null);
			setScoredInvoiceModel(null);
			setBoughtProductModel(null);
			setPendingDeliveryModel(null);
			setPendingSalesModel(null);
			setPendingOfferModel(null);
			getPendingInvoices();
			getScoredInvoices();
			getBoughtProducts();
			getPendingDeliveries();
			getPendingSales();
			getPendingOffers();
		} catch (ManagerBeanException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
			
	public void getPendingInvoices() throws ManagerBeanException {
		String select = "select Invoice "
			+ "from Invoice as Invoice "
			+ "where Invoice.type=1 AND Invoice.status =0 AND Invoice.registry.id = " + registry.getId() 
			+ "order by Invoice.issueDate desc";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createQuery(select);
		pendingInvoiceList = query.list();
	}
	
	public void getScoredInvoices() throws ManagerBeanException {
		String select = "select Invoice "
			+ "from Invoice as Invoice "
			+ "where Invoice.type=1 AND Invoice.status =1 AND Invoice.registry.id = " +registry.getId()
			+ "order by Invoice.issueDate desc";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createQuery(select);
		scoredInvoiceList = query.list();
	}
	
	public void getBoughtProducts() throws ManagerBeanException {
		String select = "select distinct(InvoiceDetail) "
			+ "from InvoiceDetail as InvoiceDetail "
			+ "where InvoiceDetail.invoice.type = 1 AND InvoiceDetail.invoice.registry.id = " +registry.getId()
			+ "order by InvoiceDetail.invoice.issueDate desc";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createQuery(select);
		boughtProductList = query.list();
	}
	
	public void getPendingDeliveries() throws ManagerBeanException {
		String select = "select Delivery "
			+ "from Delivery as Delivery "
			+ "where Delivery.status = 0 AND Delivery.customer.registry.id = " +registry.getId()
			+ "order by Delivery.issueTime desc";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createQuery(select);
		pendingDeliveryList = query.list();
		
	}
	
	public void getPendingSales() throws ManagerBeanException {
		String select = "select Sales "
			+ "from Sales as Sales "
			+ "where Sales.status = 0 AND Sales.customer.registry.id = " +registry.getId()
			+ "order by Sales.issueDate desc";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createQuery(select);
		pendingSalesList= query.list();
	}
	
	public void getPendingOffers() throws ManagerBeanException {

		String select = "select Offer "
			+ "from Offer as Offer "
			+ "where Offer.status = 0 AND Offer.target.registry.id = " +registry.getId()
			+ "order by Offer.issueDate desc";			
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createQuery(select);
		pendingOfferList= query.list();
	}
	
	public String getRowId(){
		Invoice invoice = (Invoice)this.pendingInvoiceModel.getRowData();
		return invoice.getReferenceCode();
	}
}