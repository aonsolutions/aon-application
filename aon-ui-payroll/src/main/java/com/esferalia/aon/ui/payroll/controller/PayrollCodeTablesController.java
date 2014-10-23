package com.esferalia.aon.ui.payroll.controller;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.enumeration.IPayrollTablesEnum;
import com.esferalia.aon.payroll.enumeration.ss.SSCodeTables;
import com.esferalia.aon.ui.sepe.controller.SepeTablesController;


public class PayrollCodeTablesController extends SepeTablesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	final static String SS_ENUMERATIONS_PACKAGE_NAME 		= "com.esferalia.aon.payroll.enumeration.ss";
	
	final static String SS_TAB_NAME 						= "ss";

	final static String QUOTE_TAB_NAME 						= "quote";
	
	private DataModel ssTablesModel;

	private SSCodeTables ssTable;

	
	private boolean isSSSelected(){
		return StringUtils.equals(getSelectedTab(), SS_TAB_NAME);
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
	
	public boolean isTableDefined(){
		try {
			SSCodeTables table = ((SSCodeTables)getSsTablesModel().getRowData());
			Class.forName(SS_ENUMERATIONS_PACKAGE_NAME + "." + table.getCode().trim().replace("*", "").replace("-", ""));
			return true;
		} catch (ClassNotFoundException e1) {
			// nothing to do
		}
		return false;
	}
		
	@Override
	public void onInit(ActionEvent event){
		setSelectedTab(QUOTE_TAB_NAME);
		QuoteValuesController quoteValues = (QuoteValuesController) AonUtil.getRegisteredBean("quoteValues");
		quoteValues.onInitialize(event);
		super.onSelectTab(event);
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
			Class<?> clazz = Class.forName(SS_ENUMERATIONS_PACKAGE_NAME + "." + getSSTable().getCode().trim().replace("*", "").replace("-", ""));
			completeCodesModel(clazz);
		} catch (ClassNotFoundException e1) {
			// nothing to do
			setCodesModel(null);
		}
	}
	
	private void completeCodesModel(Class<?> clazz){
		List<IPayrollTablesEnum> list = new ArrayList<IPayrollTablesEnum>();
		for (Object obj : clazz.getEnumConstants()) {
			IPayrollTablesEnum enumeration = (IPayrollTablesEnum) obj;
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
