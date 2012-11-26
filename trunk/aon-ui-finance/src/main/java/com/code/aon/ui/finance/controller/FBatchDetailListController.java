package com.code.aon.ui.finance.controller;

import java.util.ArrayList;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.ui.form.LinesController;

public class FBatchDetailListController extends LinesController {

	private ArrayList<FinanceBatchDetail> checks = new ArrayList<FinanceBatchDetail>();

	public void rowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			setRowChecked(((Boolean) event.getNewValue()).booleanValue());
		}
	}

	public boolean getRowChecked() {
		FinanceBatchDetail to = (FinanceBatchDetail) model.getRowData();
		return checks.contains(to);
	}

	public void setRowChecked(boolean rowChecked) {
		if (rowChecked) {
			FinanceBatchDetail to = (FinanceBatchDetail) model.getRowData();
			if (!checks.contains(to)) {
				checks.add(to);
			}
		} else {
			FinanceBatchDetail to = (FinanceBatchDetail) model.getRowData();
			if (checks.contains(to)) {
				checks.remove(to);
			}
		}
	}

	public ArrayList<FinanceBatchDetail> getCheckedFinanceBatchDetails() {
		return checks;
	}

	public void clearCheckedFinanceBatchDetails() {
		checks = new ArrayList<FinanceBatchDetail>();
	}

	public void checkAll(ActionEvent event) throws ManagerBeanException{
		for (ITransferObject ito : this.getManagerBean().getList(this.getCriteria())) {
			FinanceBatchDetail detail = (FinanceBatchDetail)ito;
			if (!checks.contains(detail)) {
				checks.add( detail );
			}
		}
	}

	public void checkNone(ActionEvent event) {
		clearCheckedFinanceBatchDetails();
	}

	public int getCheckedCount() {
		return getCheckedFinanceBatchDetails().size();
	}

}