package com.esferalia.aon.gwt.fiscal.client.mod390.e2015;

import com.esferalia.aon.gwt.fiscal.client.mod390.Model390.Model390Callback;
import com.esferalia.aon.gwt.fiscal.client.mod390.Model390ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel.IFiscalModelAdmonPanelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902015;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ResizeComposite;

class Page12 extends ResizeComposite {

	public Page12(final Mod3902015 m390, final Model390Callback callback) {
		super();
		
		IFiscalModelAdmonPanelCallback<Mod390, Model390ModuleOptions> cbk = 
			new IFiscalModelAdmonPanelCallback<Mod390, Model390ModuleOptions>() {

				@Override
				public Model390ModuleOptions getOptions() {
					return callback.getOptions();
				}

				@Override
				public Mod390 getModel() {
					return m390;
				}

				@Override
				public void showError(String msg) {
					callback.showError(msg);
				}

				@Override
				public String getValidatePrintAction() {
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod390ValidatePrintAEAT";
					
				}

				@Override
				public String getDownloadFileAction() {
					return "/aon_gwt_fiscal/Model3902015File";
				}

				@Override
				public String getSendAction() {
					return null;
				}

				@Override
				public void sendSuccessfully() {
					// Nothing
				}

				@Override
				public String getCheckAction() {
					return null;
				}

				@Override
				public String getCheckDataResponseDataAction() {
					return null;
				}

				@Override
				public String getModelInformationURL() {
					return "https://sede.agenciatributaria.gob.es/Sede/procedimientoini/G412.shtml";
				}
		};
		FiscalModelAdmonPanel<Mod390, Model390ModuleOptions> admonPanel = new FiscalModelAdmonPanel<>(cbk);
		initWidget(admonPanel);
	}
	
}
