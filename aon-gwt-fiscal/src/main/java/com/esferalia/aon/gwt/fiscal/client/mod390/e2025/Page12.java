package com.esferalia.aon.gwt.fiscal.client.mod390.e2025;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod390.Model390.Model390Callback;
import com.esferalia.aon.gwt.fiscal.client.mod390.Model390ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2025.Model3902025.Model3902025Callback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel.IFiscalModelAdmonPanelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902025;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ResizeComposite;

class Page12 extends ResizeComposite {
	
	FiscalModelAdmonPanel<Mod390, Model390ModuleOptions> admonPanel;

	public Page12(final Model390Callback callback, final Model3902025Callback cbk2025) {
		super();
		
		IFiscalModelAdmonPanelCallback<Mod390, Model390ModuleOptions> cbk = 
			new IFiscalModelAdmonPanelCallback<Mod390, Model390ModuleOptions>() {

				@Override
				public Model390ModuleOptions getOptions() {
					return callback.getOptions();
				}

				@Override
				public Mod390 getModel() {
					return cbk2025.getModel();
				}

				@Override
				public void showError(String msg) {
					callback.showError(msg);
				}

				@Override
				public String getValidatePrintAction() {
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod3902025ValidatePrintAEAT";					
				}

				@Override
				public String getDownloadFileAction() {
					return "/aon_gwt_fiscal/Model3902025File";
				}

				@Override
				public String getSendAction() {
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod3902025SendAEAT";
				}

				@Override
				public void sendSuccessfully() {
					Model3902025.MOD3902025_SERVICE.get(callback.getOptions().getOccam(), getModel(), new AsyncCallback<Mod3902025>() {
						@Override
						public void onSuccess(Mod3902025 selected) {
							if (selected == null) {
								callback.showError(AON.MSG.unableToFindDeclaration());
							} else {
								cbk2025.setModel(selected);
								cbk2025.refreshDeclarationToolbarPanel();
								admonPanel.manageLinks();								
							}
						}

						@Override
						public void onFailure(Throwable caught) {
							callback.showError(AON.MSG.unableToReadDeclaration(caught.getMessage()));
						}
					});
				}

				@Override
				public String getCheckAction() {
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod3902025CheckAEAT";
				}

				@Override
				public String getCheckDataResponseDataAction() {
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod3902025CheckDataResponseData";
				}

				@Override
				public String getModelInformationURL() {
					return "https://sede.agenciatributaria.gob.es/Sede/procedimientoini/G412.shtml";
				}
		};
		admonPanel = new FiscalModelAdmonPanel<>(cbk);
		initWidget(admonPanel);
	}
	
}
