package com.esferalia.aon.ui.sepe.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.esferalia.aon.payroll.enumeration.IPayrollTablesEnum;
import com.esferalia.aon.payroll.enumeration.certificados.CertificadosCodeTables;
import com.esferalia.aon.payroll.enumeration.contrata.ContrataCodeTables;

public class SepeTablesController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	final static String CONTRATA_ENUMERATIONS_PACKAGE_NAME 		= "com.esferalia.aon.payroll.enumeration.contrata";
	
	final static String CERTIFICADOS_ENUMERATIONS_PACKAGE_NAME 	= "com.esferalia.aon.payroll.enumeration.certificados";
	
	final static String CONTRATA_TAB_NAME 						= "contrata";
	
	final static String CERTIFICADOS_TAB_NAME 					= "certificados";
	
	private DataModel contrataTablesModel;

	private DataModel certificadosTablesModel;
	
	private DataModel codesModel;
	
	private ContrataCodeTables contrataTable;
	
	private CertificadosCodeTables certificadosTable;
	
	private String selectedTab;
	
	private String tablesFilter;

	private String codesFilter;

	private boolean activeCodes;
	
		
	public boolean isActiveCodes() {
		return activeCodes;
	}

	public void setActiveCodes(boolean activeCodes) {
		this.activeCodes = activeCodes;
	}

	public String getTablesFilter() {
		return tablesFilter;
	}

	public void setTablesFilter(String tablesFilter) {
		this.tablesFilter = tablesFilter;
	}

	public String getCodesFilter() {
		return codesFilter;
	}

	public void setCodesFilter(String codesFilter) {
		this.codesFilter = codesFilter;
	}

	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}
	
	public DataModel getContrataTablesModel() {
		return contrataTablesModel;
	}

	public void setContrataTablesModel(DataModel contrataTablesModel) {
		this.contrataTablesModel = contrataTablesModel;
	}

	public DataModel getCertificadosTablesModel() {
		return certificadosTablesModel;
	}

	public void setCertificadosTablesModel(DataModel certificadosTablesModel) {
		this.certificadosTablesModel = certificadosTablesModel;
	}

	public DataModel getCodesModel() {
		return codesModel;
	}

	public void setCodesModel(DataModel codesModel) {
		this.codesModel = codesModel;
	}

	public ContrataCodeTables getContrataTable() {
		return contrataTable;
	}

	public void setContrataTable(ContrataCodeTables contrataTable) {
		this.contrataTable = contrataTable;
	}

	public CertificadosCodeTables getCertificadosTable() {
		return certificadosTable;
	}

	public void setCertificadosTable(CertificadosCodeTables certificadosTable) {
		this.certificadosTable = certificadosTable;
	}
	
	private boolean isContrataSelected(){
		return StringUtils.equals(getSelectedTab(), CONTRATA_TAB_NAME);
	}

	private boolean isCertificadosSelected(){
		return StringUtils.equals(getSelectedTab(), CERTIFICADOS_TAB_NAME);
	}

	public String getSelectedTableLabel(){
		if( isContrataSelected() ){
			return "Contrat@ - " + getContrataTable().getDescription() + " (" + getContrataTable().getCode() + ")";
		} else if( isCertificadosSelected() ){
			return "Certific@2 - " + getCertificadosTable().getDescription() + " (" + getCertificadosTable().getCode() + ")";
		}
		return " - ";
	}
	
	public void onInit(ActionEvent event){
//		setSelectedTab("ss");
		onSelectTab(event);
	}
	public void onSelectTab(ActionEvent event){
		setTablesFilter(null);
		onInitTablesModels(event);
	}
	
	public void onInitTablesModels(ActionEvent event){
		initContrataTablesModel();
		initCertificadosTablesModel();
	}
	
	public void onInitCodesModels(ActionEvent event){
		if(isContrataSelected()){
			initContrataCodesModel();
		} else if(isCertificadosSelected()){
			initCertificadosCodesModel();
		}
	}
	
	public void onSelectContrata(ActionEvent event){
		setContrataTable( ((ContrataCodeTables)getContrataTablesModel().getRowData()) );
		setCodesFilter(null);
		setActiveCodes(true);
		initContrataCodesModel();
	}
	
	public void onSelectCertificados(ActionEvent event){
		setCertificadosTable( ((CertificadosCodeTables)getCertificadosTablesModel().getRowData()) );
		setCodesFilter(null);
		setActiveCodes(true);
		initCertificadosCodesModel();
	}
	
	private void initContrataTablesModel(){
		List<Enum<?>> list = new ArrayList<Enum<?>>();
		for (ContrataCodeTables obj : ContrataCodeTables.values()) {
			String cleanDesc = obj.getDescription().toLowerCase()
					.replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u")
					.replace("à", "a").replace("è", "e").replace("ì", "i").replace("ò", "o").replace("ù", "u");
			if ( StringUtils.isBlank(getTablesFilter()) 
					|| StringUtils.containsIgnoreCase(obj.getCode(), getTablesFilter())  
					|| StringUtils.containsIgnoreCase(cleanDesc, getTablesFilter()) ) {
				list.add(obj);
			}
		}
		setContrataTablesModel(new SerializableListDataModel(list));
	}
	
	private void initCertificadosTablesModel(){
		List<Enum<?>> list = new ArrayList<Enum<?>>();
		for (CertificadosCodeTables obj : CertificadosCodeTables.values()) {
			if ( StringUtils.isBlank(getTablesFilter()) 
					|| StringUtils.containsIgnoreCase(obj.getCode(), getCleanValue(getTablesFilter()))  
					|| StringUtils.containsIgnoreCase(getCleanValue(obj.getDescription()), getCleanValue(getTablesFilter())) ) {
				list.add(obj);
			}
		}
		setCertificadosTablesModel(new SerializableListDataModel(list));
	}
	
	private void initContrataCodesModel(){
		try {
			Class<?> clazz = Class.forName(CONTRATA_ENUMERATIONS_PACKAGE_NAME + "." + getContrataTable().getCode().trim().replace("*", ""));
			completeCodesModel(clazz);
		} catch (ClassNotFoundException e1) {
			// nothing to do
		}
	}
	
	private void initCertificadosCodesModel(){
		try {
			Class<?> clazz = Class.forName(CERTIFICADOS_ENUMERATIONS_PACKAGE_NAME + "." + getCertificadosTable().getCode().trim().replace("*", ""));
			completeCodesModel(clazz);
		} catch (ClassNotFoundException e1) {
			// nothing to do
		}
	}

	private void completeCodesModel(Class<?> clazz){
		List<IPayrollTablesEnum> list = new ArrayList<IPayrollTablesEnum>();
		for (Object obj : clazz.getEnumConstants()) {
			IPayrollTablesEnum enumeration = (IPayrollTablesEnum) obj;
			if ( StringUtils.isBlank(getCodesFilter()) 
					|| StringUtils.containsIgnoreCase(enumeration.getCode(), getCleanValue(getCodesFilter()))  
					|| StringUtils.containsIgnoreCase(getCleanValue(enumeration.getDescription()), getCleanValue(getCodesFilter())) ) {
				if(!isActiveCodes() || (isActiveCodes() && enumeration.isActive())){
					list.add(enumeration);	
				}
			}
		}
		setCodesModel(new SerializableListDataModel(list));
	}
	
	protected String getCleanValue(String value){
		return value.toLowerCase()
				.replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u")
				.replace("à", "a").replace("è", "e").replace("ì", "i").replace("ò", "o").replace("ù", "u")
				.replace("ä", "a").replace("ë", "e").replace("ï", "i").replace("ö", "o").replace("ü", "u");
	}
	
}
