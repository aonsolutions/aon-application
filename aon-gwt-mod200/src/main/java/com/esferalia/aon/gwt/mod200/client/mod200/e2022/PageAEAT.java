package com.esferalia.aon.gwt.mod200.client.mod200.e2022;

import com.esferalia.aon.gwt.mod200.client.FiscalModelAdmonPanel;
import com.esferalia.aon.gwt.mod200.client.FiscalModelAdmonPanel.IFiscalModelAdmonPanelCallback;
import com.esferalia.aon.gwt.mod200.client.mod200.Model200.Model200Callback;
import com.esferalia.aon.gwt.mod200.client.mod200.e2022.Model2002022.Model200PageCallback;
import com.esferalia.aon.gwt.mod200.client.mod200.Model200ModuleOptions;
import com.esferalia.aon.occam.mod200.api.model.Mod200;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ResizeComposite;

//class PageAEAT extends ResizeComposite {
public class PageAEAT extends PageAbs {
	
	FiscalModelAdmonPanel<Mod200, Model200ModuleOptions> admonPanel;

	//public PageAEAT(Mod2002022 m200, Model200Callback callback) {
	public PageAEAT(Model200PageCallback callback) {		
		super();
		
		IFiscalModelAdmonPanelCallback<Mod200, Model200ModuleOptions> cbk = 
			new IFiscalModelAdmonPanelCallback<Mod200, Model200ModuleOptions>() {

				@Override
				public Model200ModuleOptions getOptions() {
					return callback.getMod200Callback().getOptions();
				}

				@Override
				public Mod2002022 getModel() {					
					return callback.getMod200Object().getMod200();					
				}
				
				public boolean isDirty() {
					return callback.isDirty();					
				}

				@Override
				public void showError(String msg) {
					callback.getMod200Callback().showError(msg);
				}

				@Override
				public String getValidatePrintAction() {
					return GWT.getHostPageBaseURL() +"aon_gwt_mod200/ms/Mod2002022ValidatePrintAEAT";					
				}

				@Override
				public String getDownloadFileAction() {
					return "/aon_gwt_mod200/ms/Model2002022File";
				}

				@Override
				public String getSendAction() {
					return GWT.getHostPageBaseURL() +"aon_gwt_mod200/ms/Mod2002022SendAEAT";					
				}

				@Override
				public void sendSuccessfully() {
					// FALTA - RECARGAR EL MODELO
					//callback.reload( getModel().getId() );
				}

				@Override
				public String getCheckAction() {
					return GWT.getHostPageBaseURL() +"aon_gwt_mod200/ms/Mod2002022CheckAEAT";					
				}

				@Override
				public String getCheckDataResponseDataAction() {
					return GWT.getHostPageBaseURL() +"aon_gwt_mod200/ms/Mod2002022CheckDataResponseData";					
				}

				@Override
				public String getModelInformationURL() {
					return "https://sede.agenciatributaria.gob.es/Sede/procedimientoini/GE04.shtml";
				}
		};
		admonPanel = new FiscalModelAdmonPanel<>(cbk);		
		initWidget(admonPanel);
	}

	@Override
	protected void initializeTable() {				
	}
	
	@Override
	protected void setEnabled() {
		admonPanel.manageLinks();		
		// FALTA - IGUAL TAMBIEN HABRIA QUE LIMPIAR LA PANTALLA ??
	}
	
}
