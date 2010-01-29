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
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.IncomeDetail;
import com.code.aon.warehouse.dao.IWarehouseAlias;

public class IncomeTransferManager {

	private IPriceStrategy priceStrategy;
	private List<ITransferObject> incomeList;
	private List<ITransferObject> detailList;
	private DataModel incomeModel;
	private DataModel detailModel;
	private List<ITransferObject> invoicedIncomeList;
	private ArrayList<Income> incomeChecks= new ArrayList<Income>();

	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
	}

	public List<ITransferObject> getIncomeList() {
		return incomeList;
	}
	
	public void setIncomeList(List<ITransferObject> incomeList) {
		this.incomeList = incomeList;
	}
	
    public List<ITransferObject> getDetailList() {
		return detailList;
	}
	
	public void setDetailList(List<ITransferObject> detailList) {
		this.detailList = detailList;
	}

	public DataModel getIncomeModel() {
		if (incomeModel == null) {
			incomeModel = new ListDataModel(incomeList);
		}
		return incomeModel;
	}

	public void setIncomeModel(DataModel model) {
		this.incomeModel = model;
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

	public List<ITransferObject> getInvoicedIncomeList() {
		return invoicedIncomeList;
	}
	
	public void setInvoicedIncomeList(List<ITransferObject> invoicedIncomeList) {
		this.invoicedIncomeList = invoicedIncomeList;
	}
	
	public double getIncomeTotalPrice() throws ManagerBeanException {
		Income income = (Income)incomeModel.getRowData();
		return getPriceStrategy().getTotalPrice(income, income.getSupplier());
	}

	public void onSelectIncome(ActionEvent event) {
		if (incomeModel.isRowAvailable()) {
			Income income = (Income)incomeModel.getRowData();
			setDetailList(obtainIncomeDetailList(income));
			setDetailModel(null);
		}
	}

	private List<ITransferObject> obtainIncomeDetailList(Income income) {
		List<ITransferObject> detailList = new LinkedList<ITransferObject>();
		try {
			IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(incomeDetailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_INCOME_ID), income.getId());
			criteria.addOrder(incomeDetailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_LINE));
			Iterator<?> iterator = incomeDetailBean.getList(criteria).iterator();
			while (iterator.hasNext()) {
				IncomeDetail detail = (IncomeDetail)iterator.next();
				detailList.add(detail);
			}
		} catch (ManagerBeanException e) {
		}
		return detailList;
	}

	/**
	 * INCOME CHECK LIST CONTROL 
	 */

	public void incomeRowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			selectIncomeRow(((Boolean)event.getNewValue()).booleanValue());
		}
	}

	private void selectIncomeRow(boolean rowChecked) {
		if (incomeModel.isRowAvailable()) {
			Income income = (Income)incomeModel.getRowData();
			setIncomeRowChecked(income, rowChecked);

			setDetailList(obtainIncomeDetailList(income));
			setDetailModel(null);
		}
	}

	public boolean getIncomeRowChecked() {
		Income income = (Income)incomeModel.getRowData();
		return incomeChecks.contains(income);
	}
	
	public void setIncomeRowChecked(boolean rowChecked) {
	}

	public void setIncomeRowChecked(Income income, boolean rowChecked) {
		if (rowChecked) {
			if (!incomeChecks.contains(income)) {
				incomeChecks.add(income);
			}
		} else {
			if (incomeChecks.contains(income)) {
				incomeChecks.remove(income);
			}
		}
	}
	
	public ArrayList<Income> getCheckedIncome() {
		return incomeChecks;
	}
	
	public void clearCheckedIncome() {
		incomeChecks = new ArrayList<Income>();
	}
	
	@SuppressWarnings("unchecked")
	public void checkAllDeliveries(ActionEvent event) {
		Iterator iterator = incomeList.iterator();
		while (iterator.hasNext()) {
			Income income = (Income)iterator.next();
			if (!incomeChecks.contains(income)) {
				incomeChecks.add(income);
			}
		}
	}

	public void checkNoneDeliveries(ActionEvent event) {
		clearCheckedIncome();
	}

}
