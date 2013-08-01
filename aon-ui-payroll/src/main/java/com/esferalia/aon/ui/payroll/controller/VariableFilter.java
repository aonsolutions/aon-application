package com.esferalia.aon.ui.payroll.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;

import com.esferalia.aon.payroll.IVariableData;
import com.esferalia.aon.payroll.SystemData;
import com.esferalia.aon.payroll.enumeration.InactiveLastPeriod;


public class VariableFilter {
	
	private boolean searchCurrentVariables;

	private Date inactiveDate;
	
	private InactiveLastPeriod inactiveLastPeriod;

	private String selectedVariableFilter;
	
	private DataModel variablesModel;
	
	private DataModel undefinedVariablesModel;
	
	private List<SelectItem> availableVariables;
	

	public VariableFilter(DataModel variablesModel, DataModel undefinedVariablesModel){
		searchCurrentVariables = true;
		this.variablesModel = variablesModel;
		this.undefinedVariablesModel = undefinedVariablesModel;
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.MONTH, -1);
		setInactiveDate(cal.getTime());
	}

	public boolean isSearchCurrentVariables() {
		return searchCurrentVariables;
	}
	public void setSearchCurrentVariables(boolean searchCurrentVariables) {
		this.searchCurrentVariables = searchCurrentVariables;
	}
	public Date getInactiveDate() {
		
		return inactiveDate;
	}
	public void setInactiveDate(Date inactiveDate) {
		this.inactiveDate = inactiveDate;
	}
	public InactiveLastPeriod getInactiveLastPeriod() {
		return inactiveLastPeriod;
	}
	public void setInactiveLastPeriod(InactiveLastPeriod inactiveLastPeriod) {
		this.inactiveLastPeriod = inactiveLastPeriod;
	}
	public String getSelectedVariableFilter() {
		return selectedVariableFilter;
	}
	public void setSelectedVariableFilter(String selectedVariableFilter) {
		this.selectedVariableFilter = selectedVariableFilter;
	}
	
	public List<SelectItem> getAvailableVariables(){
		if(availableVariables==null){
			List<String> items = new ArrayList<String>();
			if(variablesModel!=null && variablesModel.getRowCount()>0){
				for(Object to: (List<?>)variablesModel.getWrappedData()){
					IVariableData data = (IVariableData) to;
					String name = data.getName();
					if( !items.contains(name) ){
						items.add(name);
					}
				}
			}
			if(undefinedVariablesModel!=null && undefinedVariablesModel.getRowCount()>0){
				for(Object to: (List<?>)undefinedVariablesModel.getWrappedData()){
					IVariableData data = (IVariableData) to;
					String name = data.getName();
					if( !items.contains(name) ){
						items.add(name);
					}
				}
			}
			availableVariables = new LinkedList<SelectItem>();
			Collections.sort(items);
			for(String name: items){
				SelectItem item = new SelectItem(name);
				availableVariables.add(item);
			}
		}
		return availableVariables;
	}

	public void onChangeSearchCurrent( ActionEvent event ) {
		availableVariables = null;
		setSelectedVariableFilter(null);
	}

	public void onChangeInactiveDate( ActionEvent event ) {
		if(getInactiveDate()==null && getInactiveLastPeriod()!=InactiveLastPeriod.ALL){
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -1);
			setInactiveDate(cal!=null?cal.getTime():null);
		}
		availableVariables = null;
		setSelectedVariableFilter(null);
	}
	
	public void onChangeLastPeriod( ActionEvent event ) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		if(getInactiveLastPeriod()==InactiveLastPeriod.LAST_MONTH){
			cal.add(Calendar.MONTH, -1);
		} else if(getInactiveLastPeriod()==InactiveLastPeriod.LAST_QUARTER){
			cal.add(Calendar.MONTH, -3);
		} else if(getInactiveLastPeriod()==InactiveLastPeriod.LAST_SEMESTER){
			cal.add(Calendar.MONTH, -6);
		} else if(getInactiveLastPeriod()==InactiveLastPeriod.LAST_YEAR){
			cal.add(Calendar.YEAR, -1);
		} else if(getInactiveLastPeriod()==InactiveLastPeriod.ALL){
			cal = null;
		}
		setInactiveDate(cal!=null?cal.getTime():null);
		availableVariables = null;
		setSelectedVariableFilter(null);
	}
	
}
