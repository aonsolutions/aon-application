package com.code.aon.sales.bridge;

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
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.dao.ISalesAlias;
import com.code.aon.sales.enumeration.SalesDetailStatus;

public class SalesTransferManager {

	private IPriceStrategy priceStrategy;
	private Integer selectedSalesId;
	private List<ITransferObject> salesList;
	private List<ITransferObject> detailList;
	private DataModel salesModel;
	private DataModel detailModel;
	private ArrayList<Sales> salesChecks= new ArrayList<Sales>();
	private ArrayList<SalesDetail> detailChecks= new ArrayList<SalesDetail>();

	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
	}

	public Integer getSelectedSalesId() {
		return selectedSalesId;
	}

	public void setSelectedSalesId(Integer selectedSalesId) {
		this.selectedSalesId = selectedSalesId;
	}

	public List<ITransferObject> getSalesList() {
		return salesList;
	}
	
	public void setSalesList(List<ITransferObject> salesList) {
		this.salesList = salesList;
	}
	
    public List<ITransferObject> getDetailList() {
		return detailList;
	}
	
	public void setDetailList(List<ITransferObject> detailList) {
		this.detailList = detailList;
	}

	public DataModel getSalesModel() {
		if (salesModel == null) {
			salesModel = new ListDataModel(salesList);
		}
		return salesModel;
	}

	public void setSalesModel(DataModel model) {
		this.salesModel = model;
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

	public double getSalesTotalPrice() throws ManagerBeanException {
		Sales sales = (Sales)salesModel.getRowData();
		return getPriceStrategy().getTotalPrice(sales, sales.getCustomer());
	}

	public void onSelectSales(ActionEvent event) {
		Sales sales = (Sales)salesModel.getRowData();
		setSelectedSalesId(sales.getId());
		setDetailList(obtainSalesDetailList(sales));
		setDetailModel(null);
	}

	private List<ITransferObject> obtainSalesDetailList(Sales sales) {
		List<ITransferObject> detailList = new LinkedList<ITransferObject>();
		try {
			IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(salesDetailBean.getFieldName(ISalesAlias.SALES_DETAIL_SALES_ID), sales.getId());
			criteria.addExpression(ExpressionUtilities.getNotEqualExpression(salesDetailBean.getFieldName(ISalesAlias.SALES_DETAIL_STATUS), SalesDetailStatus.SETTLED));
			criteria.addOrder(salesDetailBean.getFieldName(ISalesAlias.SALES_DETAIL_LINE));
			Iterator<?> iterator = salesDetailBean.getList(criteria).iterator();
			while (iterator.hasNext()) {
				SalesDetail detail = (SalesDetail)iterator.next();
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
	 * SALES CHECK LIST CONTROL 
	 */

	public void salesRowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			selectSalesRow(((Boolean)event.getNewValue()).booleanValue());
		}
	}

	private void selectSalesRow(boolean rowChecked) {
		Sales sales = (Sales)salesModel.getRowData();
		setSalesRowChecked(sales, rowChecked);

		if (rowChecked) {
			checkAllDetails(obtainSalesDetailList(sales));
		} else {
			checkNoneDetails(obtainSalesDetailList(sales));
		}
	}

	public boolean getSalesRowChecked() {
		Sales sales = (Sales)salesModel.getRowData();
		return salesChecks.contains(sales);
	}
	
	public void setSalesRowChecked(boolean rowChecked) {
	}

	public void setSalesRowChecked(Sales sales, boolean rowChecked) {
		if (rowChecked) {
			if (!salesChecks.contains(sales)) {
				salesChecks.add(sales);
			}
		} else {
			if (salesChecks.contains(sales)) {
				salesChecks.remove(sales);
			}
		}
	}
	
	public ArrayList<Sales> getCheckedSales() {
		return salesChecks;
	}
	
	public void clearCheckedSales() {
		salesChecks = new ArrayList<Sales>();
	}
	
	@SuppressWarnings("unchecked")
	public void checkAllSales(ActionEvent event) {
		Iterator iterator = salesList.iterator();
		while (iterator.hasNext()) {
			Sales sales = (Sales)iterator.next();
			setSalesRowChecked(sales, true);
			checkAllDetails((sales.getId().equals(selectedSalesId)) ? detailList : obtainSalesDetailList(sales));
		}
	}

	public void checkNoneSales(ActionEvent event) {
		if (detailList != null) {
			checkNoneDetails(detailList);
		}
		clearCheckedSales();
		clearCheckedDetails();
	}

	/**
	 * SALES DETAIL CHECK LIST CONTROL 
	 */

	public void detailRowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			selectDetailRow(((Boolean)event.getNewValue()).booleanValue());
		}
	}
	
	private void selectDetailRow(boolean rowChecked) {
		SalesDetail detail = (SalesDetail)detailModel.getRowData();
		setDetailRowChecked(detail, rowChecked);

		setSalesRowChecked(detail.getSales(), true);
	}

	public boolean getDetailRowChecked() {
		SalesDetail detail = (SalesDetail)detailModel.getRowData();
		return detailChecks.contains(detail);
	}
	
	public void setDetailRowChecked(boolean rowChecked) {
	}
	
	public void setDetailRowChecked(SalesDetail detail, boolean rowChecked) {
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

	public ArrayList<SalesDetail> getCheckedDetails() {
		return detailChecks;
	}
	
	public void clearCheckedDetails() {
		detailChecks = new ArrayList<SalesDetail>();
	}
	
	@SuppressWarnings("unchecked")
	private void checkAllDetails(List<ITransferObject> salesDetailList) {
		Iterator iterator = salesDetailList.iterator();
		while (iterator.hasNext()) {
			SalesDetail detail = (SalesDetail)iterator.next();
			if (!detailChecks.contains(detail)) {
				detail.setTransfered(detail.getQuantity()-detail.getDelivered());
				detailChecks.add(detail);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void checkNoneDetails(List<ITransferObject> salesDetailList) {
		Iterator iterator = salesDetailList.iterator();
		while (iterator.hasNext()) {
			SalesDetail detail = (SalesDetail)iterator.next();
			if (detailChecks.contains(detail)) {
				detail.setTransfered(0);
				detailChecks.remove(detail);
			}
		}
	}

}
