package com.code.aon.ui.finance.controller;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.ui.form.BasicController;

public class FBatchListController extends BasicController {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private ArrayList<FinanceBatch> checks = new ArrayList<FinanceBatch>();

	public void onSearch(ActionEvent event) {
		clearCheckedBatches();
		super.onSearch(event);
	}

	public void uniqueRowSelected(ValueChangeEvent event) {
		clearCheckedBatches();
		rowSelected(event);
	}
	
	public void rowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			setRowChecked(((Boolean)event.getNewValue()).booleanValue());
		}
	}
	
	public boolean getRowChecked() {
		FinanceBatch to = (FinanceBatch) model.getRowData();
		return checks.contains(to);
	}
	
	public void setRowChecked(boolean rowChecked) {
		if (rowChecked) {
			FinanceBatch to = (FinanceBatch) model.getRowData();
			if (!checks.contains(to)) {
				checks.add(to);
			}
		} else {
			FinanceBatch to = (FinanceBatch) model.getRowData();
			if (checks.contains(to)) {
				checks.remove(to);
			}
		}
	}
	
	public ArrayList<FinanceBatch> getCheckedBatches() {
		return checks;
	}
	
	public void clearCheckedBatches() {
		checks = new ArrayList<FinanceBatch>();
	}
	
	public void checkAll(ActionEvent event) throws ManagerBeanException {
		List<ITransferObject> list = this.getManagerBean().getList(this.getCriteria());
		for (ITransferObject to : list ) {
			FinanceBatch batch = (FinanceBatch) to;
			if (!checks.contains(batch)) {
				checks.add(batch);
			}
		}
	}

	public void checkNone(ActionEvent event) {
		clearCheckedBatches();
	}


	public int getDescriptionLength() throws ManagerBeanException {
		int length = 0;
		if (getModel().isRowAvailable()) {
			FinanceBatch to = (FinanceBatch)getModel().getRowData();
			length = to.getDescription().length();
		}
		return length;
	}

}