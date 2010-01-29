package com.code.aon.purchase.bridge;

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
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.purchase.dao.IPurchaseAlias;
import com.code.aon.purchase.enumeration.PurchaseDetailStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;

public class PurchaseTransferManager {

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
			purchaseModel = new ListDataModel(purchaseList);
		}
		return purchaseModel;
	}

	public void setPurchaseModel(DataModel model) {
		this.purchaseModel = model;
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
			criteria.addEqualExpression(purchaseDetailBean.getFieldName(IPurchaseAlias.PURCHASE_DETAIL_PURCHASE_ID), purchase.getId());
			criteria.addExpression(ExpressionUtilities.getNotEqualExpression(purchaseDetailBean.getFieldName(IPurchaseAlias.PURCHASE_DETAIL_STATUS), PurchaseDetailStatus.SETTLED));
			criteria.addOrder(purchaseDetailBean.getFieldName(IPurchaseAlias.PURCHASE_DETAIL_LINE));
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
			checkAllDetails(obtainPurchaseDetailList(purchase));
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

	@SuppressWarnings("unchecked")
	public void checkAllPurchases(ActionEvent event) {
		Iterator iterator = purchaseList.iterator();
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
	}

	public ArrayList<PurchaseDetail> getCheckedDetails() {
		return detailChecks;
	}

	public void clearCheckedDetails() {
		detailChecks = new ArrayList<PurchaseDetail>();
	}

	@SuppressWarnings("unchecked")
	private void checkAllDetails(List<ITransferObject> purchaseDetailList) {
		Iterator iterator = purchaseDetailList.iterator();
		while (iterator.hasNext()) {
			PurchaseDetail detail = (PurchaseDetail)iterator.next();
			if (!detailChecks.contains(detail)) {
				detail.setTransfered(detail.getQuantity()-detail.getDelivered());
				detailChecks.add(detail);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void checkNoneDetails(List<ITransferObject> purchaseDetailList) {
		Iterator iterator = purchaseDetailList.iterator();
		while (iterator.hasNext()) {
			PurchaseDetail detail = (PurchaseDetail)iterator.next();
			if (detailChecks.contains(detail)) {
				detail.setTransfered(0);
				detailChecks.remove(detail);
			}
		}
	}

}
