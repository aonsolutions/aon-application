package com.esferalia.aon.ui.sepe.controller;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.lang.StringUtils;

import com.esferalia.aon.payroll.certificados.enumeration.CertificadosCodeTables;
import com.esferalia.aon.payroll.contrata.enumeration.ContrataCodeTables;
import com.esferalia.aon.payroll.sepe.CertificadosCodeTablesWriter;
import com.esferalia.aon.payroll.sepe.ContrataCodeTablesWriter;

public class SepeTablesController {

	final static String CONTRATA_ENUMERATIONS_PACKAGE_NAME 		= ContrataCodeTablesWriter.ENUMERATION_CLASS_PACKAGE_NAME;
	
	final static String CERTIFICADOS_ENUMERATIONS_PACKAGE_NAME 	= CertificadosCodeTablesWriter.ENUMERATION_CLASS_PACKAGE_NAME;
	
	private DataModel contrataTablesModel;

	private DataModel certificadosTablesModel;
	
	private DataModel codesModel;
	
	private ContrataCodeTables contrataTable;
	
	private CertificadosCodeTables certificadosTable;
	
	private String selectedTab;
	
	private String filter;
	
	
	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
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
		return getSelectedTab().equals("contrata");
	}

	private boolean isCertificadosSelected(){
		return getSelectedTab().equals("certificados");
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
		setSelectedTab("contrata");
		setFilter(null);
		onInitModels(event);
	}
	
	public void onInitModels(ActionEvent event){
		initContrataTablesModel();
		initCertificadosTablesModel();
	}
	
	
	private void initContrataTablesModel(){
		List<Enum<?>> list = new ArrayList<Enum<?>>();
		for (ContrataCodeTables obj : ContrataCodeTables.values()) {
			if ( StringUtils.isBlank(getFilter()) 
					|| StringUtils.containsIgnoreCase(obj.getCode(), getFilter())  
					|| StringUtils.containsIgnoreCase(obj.getDescription(), getFilter()) ) {
				list.add(obj);
			}
		}
		setContrataTablesModel(new ListDataModel(list));
	}
	
	private void initCertificadosTablesModel(){
		List<Enum<?>> list = new ArrayList<Enum<?>>();
		for (CertificadosCodeTables obj : CertificadosCodeTables.values()) {
			if ( StringUtils.isBlank(getFilter()) 
					|| StringUtils.containsIgnoreCase(obj.getCode(), getFilter())  
					|| StringUtils.containsIgnoreCase(obj.getDescription(), getFilter()) ) {
				list.add(obj);
			}
		}
		setCertificadosTablesModel(new ListDataModel(list));
	}
	
	public void onSelectContrata(ActionEvent event){
		List<Enum<?>> list = new ArrayList<Enum<?>>();
		setContrataTable( ((ContrataCodeTables)getContrataTablesModel().getRowData()) );
		try {
			Class<?> clazz = Class.forName(CONTRATA_ENUMERATIONS_PACKAGE_NAME + "." + getContrataTable().getCode().trim().replace("*", ""));
			clazz.getEnumConstants();
			for (Object obj : clazz.getEnumConstants()) {
				list.add((Enum<?>) obj);	
			}
		} catch (ClassNotFoundException e1) {
			// nothing to do
		}
		
		setCodesModel(new ListDataModel(list));
	}
	
	public void onSelectCertificados(ActionEvent event){
		List<Enum<?>> list = new ArrayList<Enum<?>>();
		setCertificadosTable( ((CertificadosCodeTables)getCertificadosTablesModel().getRowData()) );
		try {
			Class<?> clazz = Class.forName(CERTIFICADOS_ENUMERATIONS_PACKAGE_NAME + "." + getCertificadosTable().getCode().trim().replace("*", ""));
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
