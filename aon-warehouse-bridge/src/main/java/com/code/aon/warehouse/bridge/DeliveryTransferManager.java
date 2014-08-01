package com.code.aon.warehouse.bridge;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.esferalia.aon.entity.IEntityAlias;

public class DeliveryTransferManager extends DataScrollerState {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private IPriceStrategy priceStrategy;
	private Integer selectedDeliveryId;
	private List<ITransferObject> deliveryList;
	private List<ITransferObject> detailList;
	private DataScrollerState detailState;
	private List<ITransferObject> invoicedDeliveryList;
	private ArrayList<Delivery> deliveryChecks= new ArrayList<Delivery>();

	public DeliveryTransferManager() {
		setBeanName("deliveryTransfer");
		setPageLimit(10);
	}

	public IPriceStrategy getPriceStrategy() {
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
		setModel(deliveryList != null ? new SerializableListDataModel(deliveryList) : null);
	}
	
    public List<ITransferObject> getDetailList() {
		return detailList;
	}
	
	public void setDetailList(List<ITransferObject> detailList) {
		this.detailList = detailList;
	}

	public DataScrollerState getDetailState() {
		if (detailState == null) {
			detailState = new DataScrollerState(new SerializableListDataModel(detailList), "deliveryDetailTransfer");
			detailState.setPageLimit(-1);
		}
		return detailState;
	}

	public void setDetailState(DataScrollerState detailState) {
		this.detailState = detailState;
	}

	public List<ITransferObject> getInvoicedDeliveryList() {
		return invoicedDeliveryList;
	}
	
	public void setInvoicedDeliveryList(List<ITransferObject> invoicedDeliveryList) {
		this.invoicedDeliveryList = invoicedDeliveryList;
	}
	
	public double getDeliveryTotalPrice() {
		Delivery delivery = (Delivery)getDirectModel().getRowData();
		return getPriceStrategy().getTotalPrice(delivery, delivery.getCustomer());
	}

	public void onSelectDelivery(ActionEvent event) {
		if (getDirectModel().isRowAvailable()) {
			Delivery delivery = (Delivery)getDirectModel().getRowData();
			setSelectedDeliveryId(delivery.getId());
			setDetailList(obtainDeliveryDetailList(delivery));
			setDetailState(null);
		}
	}

	private List<ITransferObject> obtainDeliveryDetailList(Delivery delivery) {
		List<ITransferObject> detailList = new LinkedList<ITransferObject>();
		try {
			IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(deliveryDetailBean.getFieldName(IEntityAlias.DELIVERY_DETAIL_DELIVERY_ID), delivery.getId());
			criteria.addOrder(deliveryDetailBean.getFieldName(IEntityAlias.DELIVERY_DETAIL_LINE));
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
		if (getDirectModel().isRowAvailable()) {
			Delivery delivery = (Delivery)getDirectModel().getRowData();
			setDeliveryRowChecked(delivery, rowChecked);
		}
	}

	public boolean getDeliveryRowChecked() {
		Delivery delivery = (Delivery)getDirectModel().getRowData();
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
	
	public void checkAllDeliveries(ActionEvent event) {
		for (ITransferObject ito : deliveryList) {
			Delivery delivery = (Delivery)ito;
			if (!deliveryChecks.contains(delivery)) {
				deliveryChecks.add(delivery);
			}
		}
	}

	public void checkNoneDeliveries(ActionEvent event) {
		clearCheckedDelivery();
	}

}
