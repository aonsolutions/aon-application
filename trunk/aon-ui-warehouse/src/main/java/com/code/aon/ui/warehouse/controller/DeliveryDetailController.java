package com.code.aon.ui.warehouse.controller;

import java.util.Date;
import java.util.Iterator;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tariff;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.Stock;
import com.esferalia.aon.entity.IEntityAlias;

public class DeliveryDetailController extends LinesController implements IWarehouseConstants {

	private IPriceStrategy priceStrategy;
	private boolean stockWarning;
	private boolean longDescription;
	
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

	public void onLongDescription(ActionEvent event) {
		setLongDescription(true);

		DeliveryDetail deliveryDetail = (DeliveryDetail)getTo();
		if (StringUtils.equals(deliveryDetail.getItem().getProduct().getName().trim(), deliveryDetail.getDescription().trim())) {
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
		DeliveryDetail deliveryDetail = (DeliveryDetail)getTo();
		double price = 0;
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			deliveryDetail.setItem(item);
			deliveryDetail.setDescription(item.getFullName());
			if (deliveryDetail.getQuantity() == 0) {
				deliveryDetail.setQuantity(1);
			}

			DeliveryController master = (DeliveryController)getMasterController();
			Date date = ((Delivery)master.getTo()).getIssueTime();
			Tariff tariff = ((Delivery)master.getTo()).getCustomer().getTariff();
			price = getPriceStrategy().getUnitPrice(deliveryDetail, date, tariff);
		}
		deliveryDetail.setPrice(price);
	}	

	public void onQuantityChanged(ValueChangeEvent event) {
		DeliveryDetail deliveryDetail = (DeliveryDetail)getTo();
		if (deliveryDetail.getItem() != null && deliveryDetail.getItem().getId() != null) {
			double price = 0;
			if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
				deliveryDetail.setQuantity((Double)event.getNewValue());
	
				DeliveryController master = (DeliveryController)getMasterController();
				Date date = ((Delivery)master.getTo()).getIssueTime();
				Tariff tariff = ((Delivery)master.getTo()).getCustomer().getTariff();
				price = getPriceStrategy().getUnitPrice(deliveryDetail, date, tariff);
			}
			deliveryDetail.setPrice(price);
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
		if (!isNew()) {
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
			info.append(AonUtil.getMessage("warehouseBundle", "warehouse_delivery_source"));
			info.append(" ");
			info.append(AonUtil.getMessage("salesBundle", "sales_sales"));
			info.append(" ");
			info.append(deliveryDetail.getSalesDetail().getSales().getReferenceCode());
			info.append(" - ");
			info.append(AonUtil.getMessage("warehouseBundle", "warehouse_delivery_detail_line"));
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

}