package com.code.aon.ui.warehouse.controller;

import java.util.Iterator;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.Stock;
import com.esferalia.aon.entity.IEntityAlias;

public class DeliveryDetailController extends LinesController implements IWarehouseConstants {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private IPriceStrategy priceStrategy;
	private boolean stockWarning;
	private boolean longDescription;
	private boolean showItemPackageWindow;
	
	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
	}

	public boolean isStockWarning() {
		return stockWarning;
	}

	public void setStockWarning(boolean stockWarning) {
		this.stockWarning = stockWarning;
	}

	public boolean isLongDescription() {
		return longDescription;
	}

	public void setLongDescription(boolean longDescription) {
		this.longDescription = longDescription;
	}

	public boolean isShowItemPackageWindow() {
		return showItemPackageWindow;
	}

	public void setShowItemPackageWindow(boolean value) {
		this.showItemPackageWindow = value;
	}

	public void onLongDescription(ActionEvent event) {
		setLongDescription(true);

		DeliveryDetail deliveryDetail = (DeliveryDetail)getTo();
		if (StringUtils.equals(deliveryDetail.getItem().getFullName().trim(), deliveryDetail.getDescription().trim())) {
			String longDescription = deliveryDetail.getItem().getDescription();
			if (!StringUtils.isEmpty(longDescription)) {
				deliveryDetail.setDescription(deliveryDetail.getDescription() + "\r\n" + longDescription);
			}
		}
	}

	public void onShortDescription(ActionEvent event) {
		setLongDescription(false);
	}

	public boolean isEditable() throws ManagerBeanException {
		DeliveryDetail deliveryDetail = (DeliveryDetail)this.getTo();
		if (deliveryDetail != null) {
			return isEditable(deliveryDetail);
		}
		return false;
	}

	public boolean isModelEditable() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			return isEditable((DeliveryDetail)this.getModel().getRowData());
		}
		return false;
	}

	private boolean isEditable(DeliveryDetail deliveryDetail) throws ManagerBeanException {
		return (deliveryDetail.getSalesDetail() == null || deliveryDetail.getSalesDetail().getId() == null);
	}

	public void onItemChanged(LookupChangeEvent event) {
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			Delivery delivery = (Delivery)getMasterController().getTo();

			DeliveryDetail deliveryDetail = (DeliveryDetail)getTo();
			deliveryDetail.setItem(item);
			deliveryDetail.setDescription(item.getFullName());
			if (deliveryDetail.getQuantity() == 0) {
				deliveryDetail.setQuantity(1);
			}
			deliveryDetail.setPrice(getPriceStrategy().getUnitPrice(deliveryDetail, delivery.getIssueTime(), delivery.getCustomer()));
		}
	}	

	public void onQuantityChanged(ValueChangeEvent event) {
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Delivery delivery = (Delivery)getMasterController().getTo();
			DeliveryDetail deliveryDetail = (DeliveryDetail)getTo();
			if (deliveryDetail.getItem() != null && deliveryDetail.getItem().getId() != null) {
				deliveryDetail.setQuantity((Double)event.getNewValue());
				deliveryDetail.setPrice(getPriceStrategy().getUnitPrice(deliveryDetail, delivery.getIssueTime(), delivery.getCustomer()));
			}
		}
	}

	public double getStock() throws ManagerBeanException {
		double stock = 0;
		DeliveryDetail to = (DeliveryDetail)getTo();
		if (to != null && to.getItem() != null && to.getItem().getId() != null) {
			IManagerBean stockBean = BeanManager.getManagerBean(Stock.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(stockBean.getFieldName(IEntityAlias.STOCK_ITEM_ID), to.getItem().getId());
			criteria.addEqualExpression(stockBean.getFieldName(IEntityAlias.STOCK_WAREHOUSE_ID), to.getWarehouse().getId());
			Iterator<?> iterator = stockBean.getList(criteria).iterator();
			if (iterator.hasNext()) {
				stock = ((Stock)iterator.next()).getQuantity();
			}
			stock += getRowStock(to);
		}
		return stock;
	}

	private double getRowStock(DeliveryDetail to) throws ManagerBeanException {
		double rowStock = 0;
		if (!isNevv()) {
			IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
			DeliveryDetail deliveryDetail = (DeliveryDetail)deliveryDetailBean.get(to.getId());
			if (to.getItem().equals(deliveryDetail.getItem())) {
				rowStock = deliveryDetail.getQuantity();
			}
		}
		return rowStock;
	}

	public Double getRowStock() throws ManagerBeanException {
		double rowStock = 0;
		DeliveryDetail to = (DeliveryDetail)getTo();
		if (to != null && to.getItem() != null && to.getItem().getId() != null) {
			rowStock = getRowStock(to);
		}
		return new Double(rowStock);
	}

	public void checkStockWarning(ActionEvent event) throws ManagerBeanException {
		DeliveryDetail to = (DeliveryDetail)getTo();
		stockWarning = to.getItem().getProduct().isInventoriable() && (getStock() < to.getQuantity());
		if (!stockWarning) {
			onAccept(event);
		}
	}

	public void onItemPackageShow(ActionEvent event) {
		DeliveryDetail deliveryDetail = (DeliveryDetail)getTo();
		deliveryDetail.getItem().initializePackQuantities(deliveryDetail.getQuantity());
	}

	public void onAssignItemPackage(ActionEvent event) {
		DeliveryDetail deliveryDetail = (DeliveryDetail)getTo();
		deliveryDetail.setQuantity(deliveryDetail.getItem().getPackStockQuantity());
	}

	public double getAmount() {
		return getPriceStrategy().getBasePrice((ICalculable)this.getTo());
	}

	public double getModelAmount() throws ManagerBeanException {
		return getPriceStrategy().getBasePrice((ICalculable)this.getModel().getRowData());
	}

	public String getLineSourceInfo() throws ManagerBeanException {
		StringBuffer info = new StringBuffer(64);

		DeliveryDetail deliveryDetail = (DeliveryDetail)this.getModel().getRowData();
		if (deliveryDetail.getSalesDetail() != null && deliveryDetail.getSalesDetail().getId() != null) {
			info.append(AonUtil.getMessage(ICommonMessages.SOURCE));
			info.append(" ");
			info.append(AonUtil.getMessage(ICommonMessages.INVOICE_SALES));
			info.append(" ");
			info.append(deliveryDetail.getSalesDetail().getSales().getReferenceCode());
			info.append(" - ");
			info.append(AonUtil.getMessage(ICommonMessages.LINE));
			info.append(" ");
			info.append(deliveryDetail.getSalesDetail().getLine());
		}
		return info.toString();
	}

	public void onLoadSales(ActionEvent event) throws ManagerBeanException {
		DeliveryDetail deliveryDetail = (DeliveryDetail)this.getModel().getRowData();
		BasicController salesController = (BasicController)AonUtil.getRegisteredBean(SALES_CONTROLLER_NAME);
		salesController.onLoad(event, deliveryDetail.getSalesDetail().getSales().getId(), DELIVERY_FORM_NAME, null);
	}
	
	public void onRefresh(ActionEvent event) {
		initializeModel();
	}

}