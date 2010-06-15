package com.code.aon.warehouse.bridge;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.ql.Criteria;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.dao.IWarehouseAlias;

public class DeliveryTransferManager {

	private IPriceStrategy priceStrategy;
	private Integer selectedDeliveryId;
	private List<ITransferObject> deliveryList;
	private List<ITransferObject> detailList;
	private DataModel deliveryModel;
	private DataModel detailModel;
	private List<ITransferObject> invoicedDeliveryList;
	private ArrayList<Delivery> deliveryChecks= new ArrayList<Delivery>();

	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
	}

	public Integer getSelectedDeliveryId() {
		return selectedDeliveryId;
	}

	public void setSelectedDeliveryId(Integer selectedDeliveryId) {
		this.selectedDeliveryId = selectedDeliveryId;
	}

	public List<ITransferObject> getDeliveryList() {
		return deliveryList;
	}
	
	public void setDeliveryList(List<ITransferObject> deliveryList) {
		this.deliveryList = deliveryList;
	}
	
    public List<ITransferObject> getDetailList() {
		return detailList;
	}
	
	public void setDetailList(List<ITransferObject> detailList) {
		this.detailList = detailList;
	}

	public DataModel getDeliveryModel() {
		if (deliveryModel == null) {
			deliveryModel = new ListDataModel(deliveryList);
		}
		return deliveryModel;
	}

	public void setDeliveryModel(DataModel model) {
		this.deliveryModel = model;
	}

	public DataModel getDetailModel() {
		if (detailModel == null) {
			detailModel = new ListDataModel(detailList);
		}
		return detailModel;
	}

	public void setDetailModel(DataModel model) {
		this.detailModel = model;
	}

	public List<ITransferObject> getInvoicedDeliveryList() {
		return invoicedDeliveryList;
	}
	
	public void setInvoicedDeliveryList(List<ITransferObject> invoicedDeliveryList) {
		this.invoicedDeliveryList = invoicedDeliveryList;
	}
	
	public double getDeliveryTotalPrice() throws ManagerBeanException {
		Delivery delivery = (Delivery)deliveryModel.getRowData();
		return getPriceStrategy().getTotalPrice(delivery, delivery.getCustomer());
	}

	public void onSelectDelivery(ActionEvent event) {
		if (deliveryModel.isRowAvailable()) {
			Delivery delivery = (Delivery)deliveryModel.getRowData();
			setSelectedDeliveryId(delivery.getId());
			setDetailList(obtainDeliveryDetailList(delivery));
			setDetailModel(null);
		}
	}

	private List<ITransferObject> obtainDeliveryDetailList(Delivery delivery) {
		List<ITransferObject> detailList = new LinkedList<ITransferObject>();
		try {
			IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_DELIVERY_ID), delivery.getId());
			criteria.addOrder(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_LINE));
			Iterator<?> iterator = deliveryDetailBean.getList(criteria).iterator();
			while (iterator.hasNext()) {
				DeliveryDetail detail = (DeliveryDetail)iterator.next();
				detailList.add(detail);
			}
		} catch (ManagerBeanException e) {
		}
		return detailList;
	}

	/**
	 * DELIVERY CHECK LIST CONTROL 
	 */

	public void deliveryRowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			selectDeliveryRow(((Boolean)event.getNewValue()).booleanValue());
		}
	}

	private void selectDeliveryRow(boolean rowChecked) {
		if (deliveryModel.isRowAvailable()) {
			Delivery delivery = (Delivery)deliveryModel.getRowData();
			setDeliveryRowChecked(delivery, rowChecked);
		}
	}

	public boolean getDeliveryRowChecked() {
		Delivery delivery = (Delivery)deliveryModel.getRowData();
		return deliveryChecks.contains(delivery);
	}
	
	public void setDeliveryRowChecked(boolean rowChecked) {
	}

	public void setDeliveryRowChecked(Delivery delivery, boolean rowChecked) {
		if (rowChecked) {
			if (!deliveryChecks.contains(delivery)) {
				deliveryChecks.add(delivery);
			}
		} else {
			if (deliveryChecks.contains(delivery)) {
				deliveryChecks.remove(delivery);
			}
		}
	}
	
	public ArrayList<Delivery> getCheckedDelivery() {
		return deliveryChecks;
	}
	
	public void clearCheckedDelivery() {
		deliveryChecks = new ArrayList<Delivery>();
	}
	
	@SuppressWarnings("unchecked")
	public void checkAllDeliveries(ActionEvent event) {
		Iterator iterator = deliveryList.iterator();
		while (iterator.hasNext()) {
			Delivery delivery = (Delivery)iterator.next();
			if (!deliveryChecks.contains(delivery)) {
				deliveryChecks.add(delivery);
			}
		}
	}

	public void checkNoneDeliveries(ActionEvent event) {
		clearCheckedDelivery();
	}

}
