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
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.IncomeDetail;
import com.esferalia.aon.entity.IEntityAlias;

public class IncomeTransferManager extends DataScrollerState {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private IPriceStrategy priceStrategy;
	private Integer selectedIncomeId;
	private List<ITransferObject> incomeList;
	private List<ITransferObject> detailList;
	private DataScrollerState detailState;
	private List<ITransferObject> invoicedIncomeList;
	private ArrayList<Income> incomeChecks= new ArrayList<Income>();

	public IncomeTransferManager() {
		setBeanName("incomeTransfer");
		setPageLimit(10);
	}

	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
	}

	public Integer getSelectedIncomeId() {
		return selectedIncomeId;
	}

	public void setSelectedIncomeId(Integer selectedIncomeId) {
		this.selectedIncomeId = selectedIncomeId;
	}

	public List<ITransferObject> getIncomeList() {
		return incomeList;
	}
	
	public void setIncomeList(List<ITransferObject> incomeList) {
		this.incomeList = incomeList;
		setModel(incomeList != null ? new SerializableListDataModel(incomeList) : null);
	}
	
    public List<ITransferObject> getDetailList() {
		return detailList;
	}
	
	public void setDetailList(List<ITransferObject> detailList) {
		this.detailList = detailList;
	}

	public DataScrollerState getDetailState() {
		if (detailState == null) {
			detailState = new DataScrollerState(new SerializableListDataModel(detailList), "incomeDetailTransfer");
			detailState.setPageLimit(-1);
		}
		return detailState;
	}

	public void setDetailState(DataScrollerState detailState) {
		this.detailState = detailState;
	}

	public List<ITransferObject> getInvoicedIncomeList() {
		return invoicedIncomeList;
	}
	
	public void setInvoicedIncomeList(List<ITransferObject> invoicedIncomeList) {
		this.invoicedIncomeList = invoicedIncomeList;
	}
	
	public double getIncomeTotalPrice() {
		Income income = (Income)getDirectModel().getRowData();
		return getPriceStrategy().getTotalPrice(income, income.getSupplier());
	}

	public void onSelectIncome(ActionEvent event) {
		if (getDirectModel().isRowAvailable()) {
			Income income = (Income)getDirectModel().getRowData();
			setSelectedIncomeId(income.getId());
			setDetailList(obtainIncomeDetailList(income));
			setDetailState(null);
		}
	}

	private List<ITransferObject> obtainIncomeDetailList(Income income) {
		List<ITransferObject> detailList = new LinkedList<ITransferObject>();
		try {
			IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(incomeDetailBean.getFieldName(IEntityAlias.INCOME_DETAIL_INCOME_ID), income.getId());
			criteria.addOrder(incomeDetailBean.getFieldName(IEntityAlias.INCOME_DETAIL_LINE));
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
		if (getDirectModel().isRowAvailable()) {
			Income income = (Income)getDirectModel().getRowData();
			setIncomeRowChecked(income, rowChecked);
		}
	}

	public boolean getIncomeRowChecked() {
		Income income = (Income)getDirectModel().getRowData();
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
	
	public void checkAllIncomes(ActionEvent event) {
		for (ITransferObject ito : incomeList) {
			Income income = (Income)ito;
			if (!incomeChecks.contains(income)) {
				incomeChecks.add(income);
			}
		}
	}

	public void checkNoneIncomes(ActionEvent event) {
		clearCheckedIncome();
	}

}
