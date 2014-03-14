package com.esferalia.aon.ui.payroll.controller;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.AonVersion;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.esferalia.aon.payroll.enumeration.ss.SSCodeTables;
import com.esferalia.aon.payroll.sepe.SSCodeTablesWriter;
import com.esferalia.aon.payroll.sepe.SSCodeTablesWriter.ISSEnum;
import com.esferalia.aon.ui.sepe.controller.SepeTablesController;


public class PayrollCodeTablesController extends SepeTablesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	final static String SS_ENUMERATIONS_PACKAGE_NAME 		= SSCodeTablesWriter.ENUMERATION_CLASS_PACKAGE_NAME;
	
	private DataModel ssTablesModel;

	private SSCodeTables ssTable;

	
	private boolean isSSSelected(){
		return getSelectedTab().equals("ss");
	}
	
	public DataModel getSsTablesModel() {
		return ssTablesModel;
	}

	public SSCodeTables getSSTable() {
		return ssTable;
	}

	public void setSSTable(SSCodeTables ssTable) {
		this.ssTable = ssTable;
	}
	
	public void setSSTablesModel(DataModel ssTablesModel) {
		this.ssTablesModel = ssTablesModel;
	}
	
	@Override
	public String getSelectedTableLabel(){
		if( isSSSelected() ){
			return "S.S. - " + getSSTable().getDescription() + " (" + getSSTable().getCode() + ")";
		} else {
			return super.getSelectedTableLabel();
		}
	}
	
	@Override
	public void onInit(ActionEvent event){
		super.onInit(event);
		setSelectedTab("ss");
	}
	
	@Override
	public void onInitTablesModels(ActionEvent event){
		initSSTablesModel();
		super.onInitTablesModels(event);
	}
	
	@Override
	public void onInitCodesModels(ActionEvent event){
		if( isSSSelected() ){
			initSSCodesModel();
		} else {
			super.onInitCodesModels(event);
		}
	}

	public void onInitSSCodesModels(ActionEvent event){
		initSSCodesModel();
	}
	
	public void onSelectSS(ActionEvent event){
		selectSSTable( ((SSCodeTables)getSsTablesModel().getRowData()) );
	}

	public void selectSSTable(SSCodeTables table){
		setSSTable( table );
		setCodesFilter(null);
		setActiveCodes(true);
		initSSCodesModel();
	}
	
	private void initSSTablesModel(){
		List<Enum<?>> list = new ArrayList<Enum<?>>();
		for (SSCodeTables obj : SSCodeTables.values()) {
			if ( StringUtils.isBlank(getTablesFilter()) 
					|| StringUtils.containsIgnoreCase(obj.getCode(), getTablesFilter())  
					|| StringUtils.containsIgnoreCase(obj.getDescription(), getTablesFilter()) ) {
				list.add(obj);
			}
		}
		setSSTablesModel(new SerializableListDataModel(list));
	}
	
	private void initSSCodesModel(){
		try {
			Class<?> clazz = Class.forName(SS_ENUMERATIONS_PACKAGE_NAME + "." + getSSTable().getCode().trim().replace("*", ""));
			completeCodesModel(clazz);
		} catch (ClassNotFoundException e1) {
			// nothing to do
		}
	}
	
	private void completeCodesModel(Class<?> clazz){
		List<ISSEnum> list = new ArrayList<ISSEnum>();
		for (Object obj : clazz.getEnumConstants()) {
			ISSEnum enumeration = (ISSEnum) obj;
			if ( StringUtils.isBlank(getCodesFilter()) 
					|| StringUtils.containsIgnoreCase(enumeration.getCode(), getCodesFilter())  
					|| StringUtils.containsIgnoreCase(enumeration.getDescription(), getCodesFilter()) ) {
				if(!isActiveCodes() || (isActiveCodes() && enumeration.isActive())){
					list.add(enumeration);	
				}
			}
		}
		setCodesModel(new SerializableListDataModel(list));
	}	
}
