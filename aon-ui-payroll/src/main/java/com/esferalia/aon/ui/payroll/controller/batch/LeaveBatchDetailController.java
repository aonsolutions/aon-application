package com.esferalia.aon.ui.payroll.controller.batch;

import java.util.ArrayList;
import java.util.Iterator;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.LinesController;
import com.esferalia.aon.payroll.LeaveBatchDetail;


public class LeaveBatchDetailController extends LinesController {
	
	private ArrayList<LeaveBatchDetail> checks = new ArrayList<LeaveBatchDetail>();

	public void rowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			setRowChecked(((Boolean) event.getNewValue()).booleanValue());
		}
	}

	public boolean getRowChecked() {
		LeaveBatchDetail to = (LeaveBatchDetail) model.getRowData();
		return checks.contains(to);
	}

	public void setRowChecked(boolean rowChecked) {
		if (rowChecked) {
			LeaveBatchDetail to = (LeaveBatchDetail) model.getRowData();
			if (!checks.contains(to)) {
				checks.add(to);
			}
		} else {
			LeaveBatchDetail to = (LeaveBatchDetail) model.getRowData();
			if (checks.contains(to)) {
				checks.remove(to);
			}
		}
	}

	public ArrayList<LeaveBatchDetail> getCheckedLeaveBatchDetails() {
		return checks;
	}

	public void clearCheckedLeaveBatchDetails() {
		checks = new ArrayList<LeaveBatchDetail>();
	}

	@SuppressWarnings("unchecked")
	public void checkAll(ActionEvent event) throws ManagerBeanException{
		Iterator iterator = this.getManagerBean().getList(this.getCriteria()).iterator();
		while (iterator.hasNext()) {
			LeaveBatchDetail detail = (LeaveBatchDetail)iterator.next();
			if (!checks.contains(detail)) {
				checks.add( detail );
			}
		}
	}

	public void checkNone(ActionEvent event) {
		clearCheckedLeaveBatchDetails();
	}		

}
