package com.esferalia.aon.ui.payroll.controller.launcher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.ui.company.controller.EnterpriseListController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.util.AonUtil;

public class LauncherEnterpriseFilter {
	
	private final String INCLUDED_TAB = "includedTab";
	
	private final String AVAILABLE_TAB = "availableTab";
	
	private List<Enterprise> includedList;
	
	private ArrayList<Enterprise> includedChecks = new ArrayList<Enterprise>();

	private DataModel includedModel;
	
	private String selectedTab;
	
	public LauncherEnterpriseFilter(){
		EnterpriseListController listController = (EnterpriseListController) AonUtil.getRegisteredBean(ICompanyConstants.ENTERPRISE_LIST_CONTROLLER_NAME);
		listController.onSearch(null);
		setSelectedTab(AVAILABLE_TAB);
	}
	
	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}
	
	public List<Enterprise> getIncludedList() {
		if(includedList==null){
			includedList = new ArrayList<Enterprise>();
		}
		return includedList;
	}

	public void setIncludedList(List<Enterprise> includedList) {
		this.includedList = includedList;
	}

	public DataModel getIncludedModel() {
		if(includedModel==null){
			includedModel = new ListDataModel(getIncludedList());
		}
		return includedModel;
	}

	public void setIncludedModel(DataModel includedModel) {
		this.includedModel = includedModel;
	}

		
	/////////////////////////
	// INCLUDED ENTERPRISES
	/////////////////////////

	public void includedRowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			setIncludedRowChecked(((Boolean) event.getNewValue()).booleanValue());
		}
	}

	public boolean getIncludedRowChecked() {
		if(getIncludedModel().isRowAvailable()){
			return includedChecks.contains(getIncludedModel().getRowData());
		}
		return false;
	}

	public void setIncludedRowChecked(boolean rowChecked) {
		if (rowChecked) {
			if (!includedChecks.contains(getIncludedModel().getRowData())) {
				includedChecks.add((Enterprise) getIncludedModel().getRowData());
			}
		} else {
			if (includedChecks.contains(getIncludedModel().getRowData())) {
				includedChecks.remove(getIncludedModel().getRowData());
			}
		}
	}

	public ArrayList<Enterprise> getIncludedCheckedList() {
		return includedChecks;
	}

	public int getIncludedCheckedCount() {
		return includedChecks!=null?includedChecks.size():0;
	}

	public void clearIncludedCheckedList() {
		includedChecks = new ArrayList<Enterprise>();
	}

	@SuppressWarnings("unchecked")
	public void includedCheckAll(ActionEvent event) throws ManagerBeanException {
		Iterator<ITransferObject> iterator = ((List<ITransferObject>) getIncludedModel().getWrappedData()).iterator();
		while (iterator.hasNext()) {
			Object o = iterator.next();
			if (!includedChecks.contains(o)) {
				includedChecks.add((Enterprise) o);
			}
		}
	}

	public void includedCheckNone(ActionEvent event) {
		clearIncludedCheckedList();
	}
	
	public void onSearchAvailableEnterprises(ActionEvent event) {
		EnterpriseListController listController = (EnterpriseListController) AonUtil.getRegisteredBean(ICompanyConstants.ENTERPRISE_LIST_CONTROLLER_NAME);
		listController.setUnavailableEnterpriseList(getIncludedList());
		listController.onSearch(event);
	}
	
	public void onRemoveIncluded(ActionEvent event) {
		EnterpriseListController listController = (EnterpriseListController) AonUtil.getRegisteredBean(ICompanyConstants.ENTERPRISE_LIST_CONTROLLER_NAME);
		for(Enterprise enterprise: getIncludedCheckedList()){
			listController.getList().add(enterprise);
			getIncludedList().remove(enterprise);
		}
		getIncludedCheckedList().clear();
		if(getIncludedList().isEmpty()){
			setSelectedTab(AVAILABLE_TAB);
		}
	}
	
	public void onAddAvailable(ActionEvent event) {
		EnterpriseListController listController = (EnterpriseListController) AonUtil.getRegisteredBean(ICompanyConstants.ENTERPRISE_LIST_CONTROLLER_NAME);
		for(Enterprise enterprise: listController.getCheckedList()){
			getIncludedList().add(enterprise);
			listController.getList().remove(enterprise);
		}
		listController.getCheckedList().clear();
		setSelectedTab(INCLUDED_TAB);
	}
	
	
}
