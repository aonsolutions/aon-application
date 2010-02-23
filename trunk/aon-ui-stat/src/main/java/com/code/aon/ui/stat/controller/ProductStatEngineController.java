package com.code.aon.ui.stat.controller;

import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.product.Item;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.purchase.dao.IPurchaseAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.dao.ISalesAlias;
import com.code.aon.warehouse.Stock;
import com.code.aon.warehouse.dao.IWarehouseAlias;

public class ProductStatEngineController {

	private Item  item;
	private Double pendingSaleQuantity;
	private Double pendingPurhaseQuantity;
	private Double totalStock;
	private Double averagePurchasePrice;
	private DataModel latestEntriesModel;
	private DataModel latestShipmentsModel;
	private List<InvoiceDetail> latestEntriesList;
	private List<InvoiceDetail> latestShipmentsList;
	
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}
	
	public Double getPendingSaleQuantity() {
		return pendingSaleQuantity;
	}

	public void setPendingSaleQuantity(Double pendingSaleQuantity) {
		this.pendingSaleQuantity = pendingSaleQuantity;
	}

	public Double getPendingPurhaseQuantity() {
		return pendingPurhaseQuantity;
	}

	public void setPendingPurhaseQuantity(Double pendingPurhaseQuantity) {
		this.pendingPurhaseQuantity = pendingPurhaseQuantity;
	}
	
	
	public Double getTotalStock() {
		return totalStock;
	}

	public void setTotalStock(Double totalStock) {
		this.totalStock = totalStock;
	}
	
	public Double getAveragePurchasePrice() {
		return averagePurchasePrice;
	}

	public void setAveragePurchasePrice(Double averagePurchasePrice) {
		this.averagePurchasePrice = averagePurchasePrice;
	}

	public void onProductStats(ActionEvent e){
		try {
			setLatestEntriesModel(null);
			setLatestShipmentsModel(null);
			getLatestEntries();
			getLatestShipments();
			getPendingPurchases();
			getPendingSales();
			getTotalStocks();
			getAveragePurchasesPrice();			
		} catch (ManagerBeanException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void getProductData(){
		try {
			setLatestEntriesModel(null);
			setLatestShipmentsModel(null);
			getLatestEntries();
			getLatestShipments();
			getPendingSales();
			getPendingPurchases();
			getTotalStocks();
			getAveragePurchasesPrice();
		} catch (ManagerBeanException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public DataModel getLatestEntriesModel() {
		if (latestEntriesModel == null) {
			latestEntriesModel = new ListDataModel(getLatestEntriesList());
		}
		return latestEntriesModel;
	}

	public void setLatestEntriesModel(DataModel latestEntriesModel) {
		this.latestEntriesModel = latestEntriesModel;
	}

	public DataModel getLatestShipmentsModel() {
		if (latestShipmentsModel == null) {
			latestShipmentsModel = new ListDataModel(getLatestShipmentsList());
		}
		return latestShipmentsModel;
	}

	public void setLatestShipmentsModel(DataModel latestShipmentsModel) {
		this.latestShipmentsModel = latestShipmentsModel;
	}

	public List<InvoiceDetail> getLatestEntriesList() {
		return latestEntriesList;
	}

	public void setLatestEntriesList(List<InvoiceDetail> latestEntriesList) {
		this.latestEntriesList = latestEntriesList;
	}

	public List<InvoiceDetail> getLatestShipmentsList() {
		return latestShipmentsList;
	}

	public void setLatestShipmentsList(List<InvoiceDetail> latestShipmentsList) {
		this.latestShipmentsList = latestShipmentsList;
	}
	
	public void getLatestEntries() throws ManagerBeanException {
		String select = "select InvoiceDetail "
			+ "from InvoiceDetail as InvoiceDetail "
			+ "where InvoiceDetail.invoice.type = 0 AND InvoiceDetail.item.id = " +item.getId()
			+ "order by InvoiceDetail.invoice.issueDate desc";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createQuery(select);
		latestEntriesList = query.list();
	}
	
	public void getLatestShipments() throws ManagerBeanException {
		String select = "select InvoiceDetail "
			+ "from InvoiceDetail as InvoiceDetail "
			+ "where InvoiceDetail.invoice.type = 1 AND InvoiceDetail.item.id = " +item.getId()
			+ "order by InvoiceDetail.invoice.issueDate desc";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createQuery(select);
		latestShipmentsList = query.list();
	}
	
	public void getPendingSales() throws ManagerBeanException {
		IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(salesDetailBean.getFieldName(ISalesAlias.SALES_DETAIL_ITEM_ID),item.getId());
		Projection projection = Projection.sum(salesDetailBean.getFieldName(ISalesAlias.SALES_DETAIL_QUANTITY));
		Object value = salesDetailBean.getUniqueResult(projection, criteria);
		setPendingSaleQuantity((value != null) ? ((Double)value) :0);
	}
	
	public void getPendingPurchases() throws ManagerBeanException {
		IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(purchaseDetailBean.getFieldName(IPurchaseAlias.PURCHASE_DETAIL_ITEM_ID),item.getId());
		Projection projection = Projection.sum(purchaseDetailBean.getFieldName(IPurchaseAlias.PURCHASE_DETAIL_QUANTITY));
		Object value = purchaseDetailBean.getUniqueResult(projection, criteria);
		setPendingPurhaseQuantity((value != null) ? ((Double)value) :0);
	}
	
	public void getTotalStocks() throws ManagerBeanException {
		IManagerBean stockBean = BeanManager.getManagerBean(Stock.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(stockBean.getFieldName(IWarehouseAlias.STOCK_ITEM_ID),item.getId());
		Projection projection = Projection.sum(stockBean.getFieldName(IWarehouseAlias.STOCK_QUANTITY));
		Object value = stockBean.getUniqueResult(projection, criteria);
		setTotalStock((value != null) ? ((Double)value) :0);
		}
	
	public void getAveragePurchasesPrice() throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_ITEM_ID),item.getId());
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_TYPE),InvoiceType.PURCHASE);
		Projection projection = Projection.avg(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_PRICE));
		Object value = invoiceDetailBean.getUniqueResult(projection, criteria);
		setAveragePurchasePrice((value != null) ? ((Double)value) :0);
		}
	
	public Double getTheoricStock() {		
		return (totalStock-pendingPurhaseQuantity)-pendingSaleQuantity;
		}
	
}