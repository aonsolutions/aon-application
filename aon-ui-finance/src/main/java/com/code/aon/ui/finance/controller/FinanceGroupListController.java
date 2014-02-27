package com.code.aon.ui.finance.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Finance;
import com.code.aon.ui.form.DataScrollerState;

public class FinanceGroupListController extends DataScrollerState {

	private ArrayList<Finance> checks = new ArrayList<Finance>();
	private List<Finance> groupList;
	
	public List<Finance> getGroupList() {
		if(groupList==null){
			groupList = new LinkedList<Finance>();
		}
		return groupList;
	}

	public void setGroupList(List<Finance> groupList) {
		this.groupList = groupList;
	}

	public DataModel getModel() {
		if (getDirectModel()==null) {
			setModel(new ListDataModel(getGroupList()));
		}
		return getDirectModel();
	}

	public void init(){
		setGroupList(null);
		setModel(null);
		clearCheckedFinances();
	}
	
	public void rowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			setRowChecked(((Boolean) event.getNewValue()).booleanValue());
		}
	}

	public boolean getRowChecked() {
		Finance to = (Finance) getDirectModel().getRowData();
		return checks.contains(to);
	}

	public void setRowChecked(boolean rowChecked) {
		if (rowChecked) {
			Finance to = (Finance) getDirectModel().getRowData();
			if (!checks.contains(to)) {
				checks.add(to);
			}
		} else {
			Finance to = (Finance) getDirectModel().getRowData();
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

	public void checkAll(ActionEvent event) throws ManagerBeanException{
		for (ITransferObject ito : this.getGroupList()) {
			Finance detail = (Finance)ito;
			if (!checks.contains(detail)) {
				checks.add( detail );
			}
		}
	}

	public void checkNone(ActionEvent event) {
		clearCheckedFinances();
	}

	public int getCheckedCount() {
		return getCheckedFinances().size();
	}
	

}