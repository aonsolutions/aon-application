package com.code.aon.ui.stat.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.product.Item;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.purchase.enumeration.PurchaseDetailStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.enumeration.SalesDetailStatus;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Stock;
import com.esferalia.aon.entity.IEntityAlias;

public class ProductStatEngineController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(CommercialStatEngineController.class);

	private Item  item;
	private Double pendingSaleQuantity;
	private Double pendingPurhaseQuantity;
	private Double totalStock;
	private Double averagePurchasePrice;
	private DataScrollerState latestEntriesState;
	private DataScrollerState latestShipmentsState;
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
			LOGGER.error(e1.getMessage(), e1);
		}
	}
	
	public void onLoadInvoice(ActionEvent event) throws ManagerBeanException {
		InvoiceDetail detail = (InvoiceDetail) latestShipmentsState.getModel().getRowData();
		if (detail != null && detail.getId() != null) {
			Invoice invoice = detail.getInvoice();
			String beanName = null; 
			if(invoice.getType()==InvoiceType.SALES)
				beanName = IStatConstants.SALES_INVOICE_CONTROLLER_NAME;
			else if(invoice.getType()==InvoiceType.PURCHASE)
				beanName = IStatConstants.PURCHASE_INVOICE_CONTROLLER_NAME;
			else if(invoice.getType()==InvoiceType.EXPENSES)
				beanName = IStatConstants.EXPENSE_INVOICE_CONTROLLER_NAME;
			else if(invoice.getType()==InvoiceType.UNDEDUCTIBLE)
				beanName = IStatConstants.UNDEDUCTIBLE_INVOICE_CONTROLLER_NAME;
			BasicController invoiceController = (BasicController)AonUtil.getRegisteredBean(beanName);
			invoiceController.onLoad(event, invoice.getId(), IStatConstants.PRODUCT_STATS_ACTION, null);
		}
	}
	
	public String getInvoiceAction() throws ManagerBeanException {
		InvoiceDetail detail = (InvoiceDetail) latestShipmentsState.getModel().getRowData();
		if (detail != null && detail.getId() != null) {
			Invoice invoice = detail.getInvoice();
			if(invoice.getType()==InvoiceType.SALES)
				return IStatConstants.SALES_INVOICE_FORM;
			else if(invoice.getType()==InvoiceType.PURCHASE)
				return IStatConstants.PURCHASE_INVOICE_FORM;
			else if(invoice.getType()==InvoiceType.EXPENSES)
				return IStatConstants.EXPENSE_INVOICE_FORM;
			else if(invoice.getType()==InvoiceType.UNDEDUCTIBLE)
				return IStatConstants.UNDEDUCTIBLE_INVOICE_FORM;
		}
		return null;
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
			LOGGER.error(e1.getMessage(), e1);			
		}
	}

	public DataModel getLatestEntriesModel() {
		return getLatestEntriesState().getDirectModel();
	}

	public void setLatestEntriesModel(DataModel latestEntriesModel) {
		if ( latestEntriesModel == null ) {
			setLatestEntriesState(null);
		} else {
			getLatestEntriesState().setModel(latestEntriesModel);
		}						
	}

	public DataScrollerState getLatestEntriesState() {
		if (latestEntriesState == null) {
			latestEntriesState = new DataScrollerState(new SerializableListDataModel(getLatestEntriesList()), "latestEntries");
		}								
		return latestEntriesState;
	}

	public void setLatestEntriesState(DataScrollerState latestEntriesState) {
		this.latestEntriesState = latestEntriesState;
	}

	public DataModel getLatestShipmentsModel() {
		return getLatestShipmentsState().getDirectModel();
	}

	public void setLatestShipmentsModel(DataModel latestShipmentsModel) {
		if ( latestShipmentsModel == null ) {
			setLatestShipmentsState(null);
		} else {
			getLatestShipmentsState().setModel(latestShipmentsModel);
		}								
	}
	
	public DataScrollerState getLatestShipmentsState() {
		if (latestShipmentsState == null) {
			latestShipmentsState = new DataScrollerState(new SerializableListDataModel(getLatestShipmentsList()), "latestShipments");
		}										
		return latestShipmentsState;
	}

	public void setLatestShipmentsState(DataScrollerState latestShipmentsState) {
		this.latestShipmentsState = latestShipmentsState;
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
		criteria.addEqualExpression(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_ITEM_ID),item.getId());
		criteria.addNotEqualExpression(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_STATUS),SalesDetailStatus.SETTLED);
		Projection projection = Projection.sum(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_QUANTITY));
		Object value = salesDetailBean.getUniqueResult(projection, criteria);
		setPendingSaleQuantity((value != null) ? ((Double)value) :0);
	}
	
	public void getPendingPurchases() throws ManagerBeanException {
		IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_ITEM_ID),item.getId());
		criteria.addNotEqualExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_STATUS),PurchaseDetailStatus.SETTLED);
		Projection projection = Projection.sum(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_QUANTITY));
		Object value = purchaseDetailBean.getUniqueResult(projection, criteria);
		setPendingPurhaseQuantity((value != null) ? ((Double)value) :0);
	}
	
	public void getTotalStocks() throws ManagerBeanException {
		IManagerBean stockBean = BeanManager.getManagerBean(Stock.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(stockBean.getFieldName(IEntityAlias.STOCK_ITEM_ID),item.getId());
		Projection projection = Projection.sum(stockBean.getFieldName(IEntityAlias.STOCK_QUANTITY));
		Object value = stockBean.getUniqueResult(projection, criteria);
		setTotalStock((value != null) ? ((Double)value) :0);
		}
	
	public void getAveragePurchasesPrice() throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_ITEM_ID),item.getId());
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_TYPE),InvoiceType.PURCHASE);
		Projection projection = Projection.avg(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_PRICE));
		Object value = invoiceDetailBean.getUniqueResult(projection, criteria);
		setAveragePurchasePrice((value != null) ? ((Double)value) :0);
		}
	
	public Double getTheoricStock() {		
		return (totalStock-pendingPurhaseQuantity)-pendingSaleQuantity;
		}
	
}