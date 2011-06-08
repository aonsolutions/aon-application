package com.code.aon.ui.finance.controller;

import java.util.ArrayList;
import java.util.Iterator;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Finance;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class FinanceListController extends BasicController implements IFinanceConstants {

	private boolean showFinanceSearchWindow;
	private boolean showFinanceFractionWindow;
	private ArrayList<Finance> checks = new ArrayList<Finance>();

	public boolean isShowFinanceSearchWindow() {
		return showFinanceSearchWindow;
	}

	public void setShowFinanceSearchWindow(boolean value) {
		this.showFinanceSearchWindow = value;
	}

	public boolean isShowFinanceFractionWindow() {
		return showFinanceFractionWindow;
	}

	public void setShowFinanceFractionWindow(boolean value) {
		this.showFinanceFractionWindow = value;
	}

	public void onSearch(ActionEvent event) {
		clearCheckedFinances();
		super.onSearch(event);
	}

	public void rowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			setRowChecked(((Boolean)event.getNewValue()).booleanValue());
		}
	}
	
	public boolean getRowChecked() {
		Finance to = (Finance) model.getRowData();
		return checks.contains(to);
	}
	
	public void setRowChecked(boolean rowChecked) {
		if (rowChecked) {
			Finance to = (Finance) model.getRowData();
			if (!checks.contains(to)) {
				checks.add(to);
			}
		} else {
			Finance to = (Finance) model.getRowData();
			if (checks.contains(to)) {
				checks.remove(to);
			}
		}
	}
	
	public ArrayList<Finance> getCheckedFinances() {
		return checks;
	}
	
	public void clearCheckedFinances() {
		checks = new ArrayList<Finance>();
	}
	
	@SuppressWarnings("unchecked")
	public void checkAll(ActionEvent event) throws ManagerBeanException {
		Iterator iterator = this.getManagerBean().getList(this.getCriteria()).iterator();
		while (iterator.hasNext()) {
			Finance finance = (Finance)iterator.next();
			if (!checks.contains(finance)) {
				checks.add(finance);
			}
		}
	}

	public void checkNone(ActionEvent event) {
		clearCheckedFinances();
	}


	public int getConceptLength() throws ManagerBeanException {
		int length = 0;
		if (getModel().isRowAvailable()) {
			Finance to = (Finance)getModel().getRowData();
			length = to.getConcept().length();
		}
		return length;
	}

	public int getRegistryNameLength() throws ManagerBeanException {
		int length = 0;
		if (getModel().isRowAvailable()) {
			Finance to = (Finance)getModel().getRowData();
			length = to.getRegistryName().length();
		}
		return length;
	}

	public void onLoadFinance(ActionEvent event) throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			Finance finance = (Finance)this.getModel().getRowData();

			FinanceController financeController = (FinanceController) AonUtil.getRegisteredBean(FINANCE_CONTROLLER_NAME);
			financeController.setPayment(finance.isPayment());
			financeController.onLoadFinance(event, finance, FINANCE_BATCH_FORM_NAME, FINANCE_BATCH_CONTROLLER_NAME + ".loadAvailableFinances");
		}
	}

}