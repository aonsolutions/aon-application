package com.code.aon.purchase.bridge;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;

import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.purchase.enumeration.PurchaseDetailStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.esferalia.aon.entity.IEntityAlias;

public class PurchaseTransferManager implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private IPriceStrategy priceStrategy;
	private Integer selectedPurchaseId;
	private List<ITransferObject> purchaseList;
	private List<ITransferObject> detailList;
	private DataModel purchaseModel;
	private DataModel detailModel;
	private ArrayList<Purchase> purchaseChecks= new ArrayList<Purchase>();
	private ArrayList<PurchaseDetail> detailChecks= new ArrayList<PurchaseDetail>();

	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
	}

	public Integer getSelectedPurchaseId() {
		return selectedPurchaseId;
	}

	public void setSelectedPurchaseId(Integer selectedPurchaseId) {
		this.selectedPurchaseId = selectedPurchaseId;
	}

	public List<ITransferObject> getPurchaseList() {
		return purchaseList;
	}

	public void setPurchaseList(List<ITransferObject> purchaseList) {
		this.purchaseList = purchaseList;
	}

    public List<ITransferObject> getDetailList() {
		return detailList;
	}

	public void setDetailList(List<ITransferObject> detailList) {
		this.detailList = detailList;
	}

	public DataModel getPurchaseModel() {
		if (purchaseModel == null) {
			purchaseModel = new SerializableListDataModel(purchaseList);
		}
		return purchaseModel;
	}

	public void setPurchaseModel(DataModel model) {
		this.purchaseModel = model;
	}

	public DataModel getDetailModel() {
		if (detailModel == null) {
			detailModel = new SerializableListDataModel(detailList);
		}
		return detailModel;
	}

	public void setDetailModel(DataModel model) {
		this.detailModel = model;
	}

	public double getPurchaseTotalPrice() throws ManagerBeanException {
		Purchase purchase = (Purchase)purchaseModel.getRowData();
		return getPriceStrategy().getTotalPrice(purchase, purchase.getSupplier());
	}

	public void onSelectPurchase(ActionEvent event) {
		Purchase purchase = (Purchase)purchaseModel.getRowData();
		setSelectedPurchaseId(purchase.getId());
		setDetailList(obtainPurchaseDetailList(purchase));
		setDetailModel(null);
	}

	private List<ITransferObject> obtainPurchaseDetailList(Purchase purchase) {
		List<ITransferObject> detailList = new LinkedList<ITransferObject>();
		try {
			IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_PURCHASE_ID), purchase.getId());
			criteria.addNotEqualExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_STATUS), PurchaseDetailStatus.SETTLED);
			criteria.addOrder(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_LINE));
			Iterator<?> iterator = purchaseDetailBean.getList(criteria).iterator();
			while (iterator.hasNext()) {
				PurchaseDetail detail = (PurchaseDetail)iterator.next();
				if (detailChecks.contains(detail)) {
					detail = detailChecks.get(detailChecks.indexOf(detail));
				}
				detailList.add(detail);
			}
		} catch (ManagerBeanException e) {
		}
		return detailList;
	}

	public void onTransferedChanged(ActionEvent event) {
		PurchaseDetail purchaseDetail = (PurchaseDetail) getDetailModel().getRowData();
		if(purchaseDetail.getPendingQuantity() > 0 && purchaseDetail.getTransfered() < 0 ){
			purchaseDetail.setTransfered(0);
		} else if( purchaseDetail.getPendingQuantity() < 0 && purchaseDetail.getTransfered() > 0 ){
			purchaseDetail.setTransfered(0);
		}
		if( purchaseDetail.getTransfered()!=0 && !detailChecks.contains(purchaseDetail)){
			detailChecks.add(purchaseDetail);
		} else if( purchaseDetail.getTransfered()==0 && detailChecks.contains(purchaseDetail)){
			detailChecks.remove(purchaseDetail);
		}
		purchaseDetail.setForcePendingQuantityCancel(false);
	}
	public void onTransferedChanged(ValueChangeEvent event) {
		double value = (event.getNewValue()!=null) ? ((Double)event.getNewValue()).doubleValue() : 0;
		selectDetailRow(value > 0);
	}

	/**
	 * PURCHASE CHECK LIST CONTROL
	 */

	public void purchaseRowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			selectPurchaseRow(((Boolean)event.getNewValue()).booleanValue());
		}
	}

	private void selectPurchaseRow(boolean rowChecked) {
		Purchase purchase = (Purchase)purchaseModel.getRowData();
		setPurchaseRowChecked(purchase, rowChecked);

		if (rowChecked) {
			checkAllDetails((purchase.getId().equals(selectedPurchaseId)) ? detailList : obtainPurchaseDetailList(purchase));
		} else {
			checkNoneDetails(obtainPurchaseDetailList(purchase));
		}
	}

	public boolean getPurchaseRowChecked() {
		Purchase purchase = (Purchase)purchaseModel.getRowData();
		return purchaseChecks.contains(purchase);
	}

	public void setPurchaseRowChecked(boolean rowChecked) {
	}

	public void setPurchaseRowChecked(Purchase purchase, boolean rowChecked) {
		if (rowChecked) {
			if (!purchaseChecks.contains(purchase)) {
				purchaseChecks.add(purchase);
			}
		} else {
			if (purchaseChecks.contains(purchase)) {
				purchaseChecks.remove(purchase);
			}
		}
	}

	public ArrayList<Purchase> getCheckedPurchase() {
		return purchaseChecks;
	}

	public void clearCheckedPurchase() {
		purchaseChecks = new ArrayList<Purchase>();
	}

	public void checkAllPurchases(ActionEvent event) {
		Iterator<ITransferObject> iterator = purchaseList.iterator();
		while (iterator.hasNext()) {
			Purchase purchase = (Purchase)iterator.next();
			setPurchaseRowChecked(purchase, true);
			checkAllDetails((purchase.getId().equals(selectedPurchaseId)) ? detailList : obtainPurchaseDetailList(purchase));
		}
	}

	public void checkNonePurchases(ActionEvent event) {
		if (detailList != null) {
			checkNoneDetails(detailList);
		}
		clearCheckedPurchase();
		clearCheckedDetails();
	}

	/**
	 * PURCHASE DETAIL CHECK LIST CONTROL
	 */

	public void detailRowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			selectDetailRow(((Boolean)event.getNewValue()).booleanValue());
		}
	}

	private void selectDetailRow(boolean rowChecked) {
		PurchaseDetail detail = (PurchaseDetail)detailModel.getRowData();
		setDetailRowChecked(detail, rowChecked);

		setPurchaseRowChecked(detail.getPurchase(), true);
	}

	public boolean getDetailRowChecked() {
		PurchaseDetail detail = (PurchaseDetail)detailModel.getRowData();
		return detailChecks.contains(detail);
	}

	public void setDetailRowChecked(boolean rowChecked) {
	}

	public void setDetailRowChecked(PurchaseDetail detail, boolean rowChecked) {
		if (rowChecked) {
			if (!detailChecks.contains(detail)) {
				detail.setTransfered(detail.getQuantity()-detail.getDelivered());
				detailChecks.add(detail);
			}
		} else {
			if (detailChecks.contains(detail)) {
				detail.setTransfered(0);
				detailChecks.remove(detail);
			}
		}
		detail.setForcePendingQuantityCancel(false);
	}

	public ArrayList<PurchaseDetail> getCheckedDetails() {
		return detailChecks;
	}

	public void clearCheckedDetails() {
		detailChecks = new ArrayList<PurchaseDetail>();
	}

	private void checkAllDetails(List<ITransferObject> purchaseDetailList) {
		Iterator<ITransferObject> iterator = purchaseDetailList.iterator();
		while (iterator.hasNext()) {
			PurchaseDetail detail = (PurchaseDetail)iterator.next();
			if (!detailChecks.contains(detail)) {
				detail.setTransfered(detail.getQuantity()-detail.getDelivered());
				detailChecks.add(detail);
			}
		}
	}

	private void checkNoneDetails(List<ITransferObject> purchaseDetailList) {
		Iterator<ITransferObject> iterator = purchaseDetailList.iterator();
		while (iterator.hasNext()) {
			PurchaseDetail detail = (PurchaseDetail)iterator.next();
			if (detailChecks.contains(detail)) {
				detail.setTransfered(0);
				detailChecks.remove(detail);
			}
		}
	}
	
	public boolean isTransferedGreatherThanPending() {
		return isTransferedGreatherThanPending((PurchaseDetail) getDetailModel().getRowData());
	}
	
	public boolean isTransferedGreatherThanPending(PurchaseDetail purchaseDetail) {
		if( (purchaseDetail.getPendingQuantity() > 0 && purchaseDetail.getTransfered() > purchaseDetail.getPendingQuantity())
				|| (purchaseDetail.getPendingQuantity() < 0 && purchaseDetail.getTransfered() < purchaseDetail.getPendingQuantity()) ){
			return true;
		}
		return false;
	}

	public boolean isTransferedLessThanPending() {
		return isTransferedLessThanPending((PurchaseDetail) getDetailModel().getRowData());
	}

	public boolean isTransferedLessThanPending(PurchaseDetail purchaseDetail) {
		if( purchaseDetail.getTransfered() < purchaseDetail.getPendingQuantity() ){
			return true;
		}
		return false;
	}

	/**
	 * PURCHASE DETAIL TO CLOSE CHECK LIST CONTROL
	 */
	
	public void detailToCloseRowSelected(ActionEvent event){
		PurchaseDetail detail = (PurchaseDetail)detailModel.getRowData();
		detail.setForcePendingQuantityCancel(!detail.isForcePendingQuantityCancel());
	}
	
}
