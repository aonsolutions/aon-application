package com.esferalia.aon.gwt.mod200.client.mod200.e2022;

import com.esferalia.aon.gwt.mod200.client.mod200.Model200AdmonPanel;
import com.esferalia.aon.gwt.mod200.client.mod200.e2022.Model2002022.Model2002022PageCallback;

public class PageAEAT extends PageAbs {
	
//	FiscalModel200AdmonPanel<Mod200, Model200ModuleOptions> admonPanel;
//
//	public PageAEAT(Model200PageCallback callback) {		
//		super();
//		
//		IFiscalModelAdmonPanelCallback<Mod200, Model200ModuleOptions> cbk = 
//			new IFiscalModelAdmonPanelCallback<Mod200, Model200ModuleOptions>() {
//
//				@Override
//				public Model200ModuleOptions getOptions() {
//					return callback.getMod200Callback().getOptions();
//				}
//
//				@Override
//				public Mod2002022 getModel() {					
//					return callback.getMod200Object().getMod200();					
//				}
//				
//				public boolean isDirty() {
//					return callback.isDirty();					
//				}
//
//				@Override
//				public void showError(String msg) {
//					callback.getMod200Callback().showError(msg);
//				}
//
//				@Override
//				public String getValidatePrintAction() {
//					return GWT.getHostPageBaseURL() +"aon_gwt_mod200/ms/Mod2002022ValidatePrintAEAT";					
//				}
//
//				@Override
//				public String getDownloadFileAction() {
//					return "/aon_gwt_mod200/ms/Model2002022File";
//				}
//
//				@Override
//				public String getSendAction() {
//					return GWT.getHostPageBaseURL() +"aon_gwt_mod200/ms/Mod2002022SendAEAT";					
//				}
//
//				@Override
//				public void sendSuccessfully() {
//					// FALTA - RECARGAR EL MODELO
//					//callback.reload( getModel().getId() );
//					
//				}
//
//				@Override
//				public String getCheckAction() {
//					return GWT.getHostPageBaseURL() +"aon_gwt_mod200/ms/Mod2002022CheckAEAT";					
//				}
//
//				@Override
//				public String getCheckDataResponseDataAction() {
//					return GWT.getHostPageBaseURL() +"aon_gwt_mod200/ms/Mod2002022CheckDataResponseData";					
//				}
//
//				@Override
//				public String getModelInformationURL() {
//					return "https://sede.agenciatributaria.gob.es/Sede/procedimientoini/GE04.shtml";
//				}
//
//				@Override
//				public String getImportAccountingAction() {
//					// TODO Auto-generated method stub
//					return "/aon_gwt_mod200/ms/Model2002022AccountingFile";
//				}
//		};
//		admonPanel = new FiscalModel200AdmonPanel<>(cbk);
	
	Model200AdmonPanel admonPanel;

	public PageAEAT(Model2002022PageCallback callback) {		
		super();
		admonPanel = new Model200AdmonPanel(callback);		
		initWidget(admonPanel);
	}

	@Override
	protected void initializeTable() {				
	}
	
	@Override
	protected void setEnabled() {
		admonPanel.manageLinks();		
	}
	
}
