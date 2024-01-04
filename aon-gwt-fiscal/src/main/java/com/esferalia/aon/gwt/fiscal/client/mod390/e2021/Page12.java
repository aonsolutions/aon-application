package com.esferalia.aon.gwt.fiscal.client.mod390.e2021;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod390.Model390.Model390Callback;
import com.esferalia.aon.gwt.fiscal.client.mod390.Model390ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2021.Model3902021.Model3902021Callback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel.IFiscalModelAdmonPanelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902021;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ResizeComposite;

class Page12 extends ResizeComposite {
	
	FiscalModelAdmonPanel<Mod390, Model390ModuleOptions> admonPanel;

	public Page12(final Model390Callback callback, Model3902021Callback cbk2021) {
		super();
		
		IFiscalModelAdmonPanelCallback<Mod390, Model390ModuleOptions> cbk = 
			new IFiscalModelAdmonPanelCallback<Mod390, Model390ModuleOptions>() {

				@Override
				public Model390ModuleOptions getOptions() {
					return callback.getOptions();
				}

				@Override
				public Mod390 getModel() {
					return cbk2021.getModel();
				}

				@Override
				public void showError(String msg) {
					callback.showError(msg);
				}

				@Override
				public String getValidatePrintAction() {
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod3902021ValidatePrintAEAT";					
				}

				@Override
				public String getDownloadFileAction() {
					return "/aon_gwt_fiscal/Model3902021File";
				}

				@Override
				public String getSendAction() {
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod3902021SendAEAT";
				}

				@Override
				public void sendSuccessfully() {
					Model3902021.MOD3902021_SERVICE.get(callback.getOptions().getOccam(), getModel(), new AsyncCallback<Mod3902021>() {
						@Override
						public void onSuccess(Mod3902021 selected) {
							if (selected == null) {
								callback.showError(AON.MSG.unableToFindDeclaration());
							} else {
								cbk2021.setModel(selected);
								cbk2021.refreshDeclarationToolbarPanel();
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
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod3902021CheckAEAT";
				}

				@Override
				public String getCheckDataResponseDataAction() {
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod3902021CheckDataResponseData";
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
