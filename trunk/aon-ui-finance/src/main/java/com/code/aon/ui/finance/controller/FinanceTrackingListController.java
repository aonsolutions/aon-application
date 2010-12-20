package com.code.aon.ui.finance.controller;

import java.util.ArrayList;
import java.util.Iterator;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ui.form.BasicController;

public class FinanceTrackingListController extends BasicController {

	private ArrayList<FinanceTracking> checks = new ArrayList<FinanceTracking>();

	public void setExpression(ValueChangeEvent event) throws AbortProcessingException {
		try {
			String financeAlias = "Finance_";
			String trackingAlias = "FinanceTracking_finance_";
			if (event.getComponent().getId().contains(IFinanceAlias.FINANCE_AMOUNT)) {
				financeAlias = IFinanceAlias.FINANCE_AMOUNT;
				trackingAlias = IFinanceAlias.FINANCE_TRACKING_AMOUNT;
			} else if (event.getComponent().getId().contains(IFinanceAlias.FINANCE_DUE_DATE)) {
				financeAlias = IFinanceAlias.FINANCE_DUE_DATE;
				trackingAlias = IFinanceAlias.FINANCE_TRACKING_TRACKING_DATE;
			} else if (event.getComponent().getId().contains(IFinanceAlias.FINANCE_SECURITY_LEVEL)) {
				financeAlias = IFinanceAlias.FINANCE_SECURITY_LEVEL;
				trackingAlias = IFinanceAlias.FINANCE_TRACKING_SECURITY_LEVEL;
			}
			event.getComponent().setId(event.getComponent().getId().replace(financeAlias, trackingAlias));

			if (event.getComponent().getId().contains("-From")) {
				addGreaterThanOrEqualExpression(event);
			} else if (event.getComponent().getId().contains("-To")) {
				addLessThanOrEqualExpression(event);
			} else {
				addExpression(event);
			}
		} catch (ManagerBeanException e) {
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public void onSearch(ActionEvent event) {
		clearCheckedTrackings();
		super.onSearch(event);
	}

	public void rowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			setRowChecked(((Boolean)event.getNewValue()).booleanValue());
		}
	}
	
	public boolean getRowChecked() {
		FinanceTracking to = (FinanceTracking) model.getRowData();
		return checks.contains(to);
	}
	
	public void setRowChecked(boolean rowChecked) {
		if (rowChecked) {
			FinanceTracking to = (FinanceTracking) model.getRowData();
			if (!checks.contains(to)) {
				checks.add(to);
			}
		} else {
			FinanceTracking to = (FinanceTracking) model.getRowData();
			if (checks.contains(to)) {
				checks.remove(to);
			}
		}
	}
	
	public ArrayList<FinanceTracking> getCheckedTrackings() {
		return checks;
	}
	
	public void clearCheckedTrackings() {
		checks = new ArrayList<FinanceTracking>();
	}
	
	@SuppressWarnings("unchecked")
	public void checkAll(ActionEvent event) throws ManagerBeanException {
		Iterator iterator = this.getManagerBean().getList(this.getCriteria()).iterator();
		while (iterator.hasNext()) {
			FinanceTracking tracking = (FinanceTracking)iterator.next();
			if (!checks.contains(tracking)) {
				checks.add(tracking);
			}
		}
	}

	public void checkNone(ActionEvent event) {
		clearCheckedTrackings();
	}


	public int getConceptLength() throws ManagerBeanException {
		int length = 0;
		if (getModel().isRowAvailable()) {
			FinanceTracking to = (FinanceTracking)getModel().getRowData();
			length = to.getFinance().getConcept().length();
		}
		return length;
	}

	public int getRegistryNameLength() throws ManagerBeanException {
		int length = 0;
		if (getModel().isRowAvailable()) {
			FinanceTracking to = (FinanceTracking)getModel().getRowData();
			length = to.getFinance().getRegistryName().length();
		}
		return length;
	}

}