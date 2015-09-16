package com.code.aon.warehouse.bridge;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
	private ArrayList<Delivery> deliveryChecks = new ArrayList<Delivery>();
	private ArrayList<Delivery> restoreInvoicedDeliveryChecks = new ArrayList<Delivery>();
	private FilterParams filterParams;

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

	public Integer getDeliveryListCount() {
		return deliveryList!=null?deliveryList.size():-1;
	}
	
    public List<ITransferObject> getDetailList() {
		return detailList;
	}
	
	public void setDetailList(List<ITransferObject> detailList) {
		this.detailList = detailList;
	}

	public Integer getDetailListCount() {
		return detailList!=null?detailList.size():-1;
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

	public FilterParams getFilterParams() {
		if(filterParams==null){
			filterParams = new FilterParams();
		}
		return filterParams;
	}

	public void setFilterParams(FilterParams filterParams) {
		this.filterParams = filterParams;
	}
	
	public List<ITransferObject> getInvoicedDeliveryList() {
		return invoicedDeliveryList;
	}
	
	public void setInvoicedDeliveryList(List<ITransferObject> invoicedDeliveryList) {
		this.invoicedDeliveryList = invoicedDeliveryList;
	}
	
	public Integer getInvoicedDeliveryCount() {
		return invoicedDeliveryList!=null?invoicedDeliveryList.size():-1;
	}
	
	public boolean isInvoicedDelivery(){
		Delivery delivery = (Delivery)getDirectModel().getRowData();
		return invoicedDeliveryList!=null && invoicedDeliveryList.contains(delivery) ;
	}
	
	public double getDeliveryTotalPrice() {
		Delivery delivery = (Delivery)getDirectModel().getRowData();
		return getPriceStrategy().getTotalPrice(delivery, delivery.getCustomer());
	}
	
	public void onShowTransfered(ActionEvent event) {
		setSelectedDeliveryId(null);
		setDetailList(null);
		setDetailState(null);
		setModel(invoicedDeliveryList != null ? new SerializableListDataModel(invoicedDeliveryList) : null);
	}
	
	public void onShowAvailables(ActionEvent event) {
		setSelectedDeliveryId(null);
		setDetailList(null);
		setDetailState(null);
		setModel(deliveryList != null ? new SerializableListDataModel(deliveryList) : null);
	}
	
	public void onShowSelected(ActionEvent event) {
		setSelectedDeliveryId(null);
		setDetailList(null);
		setDetailState(null);
		setModel(deliveryChecks != null ? new SerializableListDataModel(deliveryChecks) : null);
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
	
	public void addDeliveryRowSelected(ActionEvent event) {
		selectDeliveryRow(Boolean.TRUE);
		Delivery delivery = (Delivery)getDirectModel().getRowData();
		getDeliveryList().remove(delivery);
	}

	public void removeDeliveryRowSelected(ActionEvent event) {
		Delivery delivery = (Delivery)getDirectModel().getRowData();
		getDeliveryList().add(delivery);
		selectDeliveryRow(Boolean.FALSE);
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
	
	public Integer getCheckedDeliveryCount() {
		return deliveryChecks!=null?deliveryChecks.size():-1;
	}
	
	
	public void clearCheckedDelivery() {
		deliveryChecks.clear();;
	}
	
	public void checkAllDeliveries(ActionEvent event) {
		for (ITransferObject ito : deliveryList) {
			Delivery delivery = (Delivery)ito;
			if (!deliveryChecks.contains(delivery)) {
				deliveryChecks.add(delivery);
			}
		}
		getDeliveryList().clear();
	}

	public void checkNoneDeliveries(ActionEvent event) {
		for (ITransferObject ito : deliveryChecks) {
			Delivery delivery = (Delivery)ito;
			if (!getDeliveryList().contains(delivery)) {
				getDeliveryList().add(delivery);
			}
		}
		clearCheckedDelivery();
	}

	/**
	 * INVOICED DELIVERY CHECK LIST CONTROL 
	 */
	public void restoreInvoicedDelivery(ActionEvent event) {
		Delivery delivery = (Delivery)getDirectModel().getRowData();
		if(restoreInvoicedDeliveryChecks.contains(delivery)){
			restoreInvoicedDeliveryChecks.remove(delivery);
		}
	}
	
	public void removeInvoicedDelivery(ActionEvent event) {
		Delivery delivery = (Delivery)getDirectModel().getRowData();
		if(!restoreInvoicedDeliveryChecks.contains(delivery)){
			restoreInvoicedDeliveryChecks.add(delivery);
		}
	}
	
	public boolean isRestoredDelivery(){
		Delivery delivery = (Delivery)getDirectModel().getRowData();
		return restoreInvoicedDeliveryChecks.contains(delivery);
	}
	
	public ArrayList<Delivery> getCheckedRestoreInvoicedDelivery() {
		return restoreInvoicedDeliveryChecks;
	}
	
	public Integer getCheckedRestoreInvoicedDeliveryCount() {
		return restoreInvoicedDeliveryChecks!=null?restoreInvoicedDeliveryChecks.size():-1;
	}
	
	public static class FilterParams implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private Date fromDate;
		private Date toDate;
		public Date getFromDate() {
			return fromDate;
		}
		public void setFromDate(Date fromDate) {
			this.fromDate = fromDate;
		}
		public Date getToDate() {
			return toDate;
		}
		public void setToDate(Date toDate) {
			this.toDate = toDate;
		}
	}

	
}
