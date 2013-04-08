package com.esferalia.aon.ui.payroll.controller;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.esferalia.aon.payroll.contrata.enumeration.ContrataCodeTables;

public class SepeTablesController {

	final static String ENUMERATIONS_PACKAGE_NAME 		= "com.esferalia.aon.payroll.contrata.enumeration";
	
	private DataModel tablesModel;
	private DataModel codesModel;
	private ContrataCodeTables table;

	public DataModel getTablesModel() {
		return tablesModel;
	}

	public void setTablesModel(DataModel tablesModel) {
		this.tablesModel = tablesModel;
	}

	public DataModel getCodesModel() {
		return codesModel;
	}

	public void setCodesModel(DataModel codesModel) {
		this.codesModel = codesModel;
	}
	
	public ContrataCodeTables getTable() {
		return table;
	}

	public void setTable(ContrataCodeTables table) {
		this.table = table;
	}

	public String getSelectedTableLabel(){
		return getTable().getDescription() + " (" + getTable().getCode() + ")";
	}
	
	public void onInit(ActionEvent event){
		List<Enum<?>> list = new ArrayList<Enum<?>>();
		ContrataCodeTables[] el = ContrataCodeTables.values();
		for (ContrataCodeTables obj : el) {
			list.add(obj);
		}
		setTablesModel(new ListDataModel(list));
	}
	
	public void onSelect(ActionEvent event){
		List<Enum<?>> list = new ArrayList<Enum<?>>();
		setTable(((ContrataCodeTables)getTablesModel().getRowData()));
		try {
			Class<?> clazz = Class.forName(ENUMERATIONS_PACKAGE_NAME+"."+getTable().getCode().replace("*", ""));
			clazz.getEnumConstants();
			for (Object obj : clazz.getEnumConstants()) {
				list.add((Enum<?>) obj);	
			}
		} catch (ClassNotFoundException e1) {
			// nothing to do
		}
		
		setCodesModel(new ListDataModel(list));
	}
	
}
