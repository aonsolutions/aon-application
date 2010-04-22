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
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.Stock;
import com.code.aon.warehouse.dao.IWarehouseAlias;

public class DeliveryDetailController extends LinesController {

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

	public boolean isSalesSource() throws ManagerBeanException {
		DeliveryDetail deliveryDetail = (DeliveryDetail)getTo();
		if (deliveryDetail != null) {
			return (deliveryDetail.getSalesDetail() != null && deliveryDetail.getSalesDetail().getId() != null);
		}
		return false;
	}

	public void onItemChanged(LookupChangeEvent event) {
		DeliveryDetail deliveryDetail = (DeliveryDetail)getTo();
		double price = 0;
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			deliveryDetail.setItem(item);
			deliveryDetail.setDescription(item.getProduct().getName() + (item.getDetail() != null ? " " + item.getDetail() : ""));
			if (deliveryDetail.getQuantity() == 0) {
				deliveryDetail.setQuantity(1);
			}

			Date date = deliveryDetail.getDelivery().getIssueTime();
			DeliveryController master = (DeliveryController)getMasterController();
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
	
				Date date = deliveryDetail.getDelivery().getIssueTime();
				DeliveryController master = (DeliveryController)getMasterController();
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
			criteria.addEqualExpression(stockBean.getFieldName(IWarehouseAlias.STOCK_ITEM_ID), to.getItem().getId());
			criteria.addEqualExpression(stockBean.getFieldName(IWarehouseAlias.STOCK_WAREHOUSE_ID), to.getWarehouse().getId());
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

	public String getLineStatusInfo() throws ManagerBeanException {
		StringBuffer info = new StringBuffer(64);

		DeliveryDetail deliveryDetail = (DeliveryDetail)this.getModel().getRowData();
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_SOURCE), InvoiceSource.DELIVERY);
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_SOURCE_ID), deliveryDetail.getId());
		Iterator<?> iterator = invoiceDetailBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			InvoiceDetail invoiceDetail = (InvoiceDetail)iterator.next();
			info.append(AonUtil.getMessage("warehouseBundle", "warehouse_delivery_transfered_to"));
			info.append(" ");
			info.append(AonUtil.getMessage("financeBundle", "finance_invoice"));
			info.append(" ");
			info.append(invoiceDetail.getInvoice().getReferenceCode());
			info.append(" - ");
			info.append(AonUtil.getMessage("warehouseBundle", "warehouse_delivery_detail_line"));
			info.append(" ");
			info.append(invoiceDetail.getLine());
		}
		return info.toString();
	}

}