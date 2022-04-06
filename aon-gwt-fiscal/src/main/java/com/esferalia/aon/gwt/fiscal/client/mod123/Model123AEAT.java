package com.esferalia.aon.gwt.fiscal.client.mod123;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod123.Model123.Model123Callback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel.IFiscalModelAdmonPanelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class Model123AEAT extends Model123Base {

	public Model123AEAT(Mod123 mod123, Model123Callback callback) {
		super(mod123,callback);
	}

	@Override
	protected void paintAdministrationTab(TabLayoutPanel tabPanel) {
		IFiscalModelAdmonPanelCallback<Mod123, Model123ModuleOptions> cbk = 
			new IFiscalModelAdmonPanelCallback<Mod123, Model123ModuleOptions>() {

				@Override
				public Model123ModuleOptions getOptions() {
					return getCallback().getOptions();
				}

				@Override
				public Mod123 getModel() {
					return Model123AEAT.this.getModel();
				}

				@Override
				public void showError(String msg) {
					getCallback().showError(msg);
				}

				@Override
				public String getValidatePrintAction() {
					return GWT.getHostPageBaseURL() + "aon_gwt_fiscal/ms/Mod123ValidatePrintAEAT";
					
				}

				@Override
				public String getDownloadFileAction() {
					return Model123Base.MODEL123_FILE;
				}

				@Override
				public String getSendAction() {
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod123SendAEAT";
				}

				@Override
				public void sendSuccessfully() {
					Model123.SERVICE.getMod123(getCallback().getOptions().getOccam(), 
							getModel().getId(), new AsyncCallback<Mod123>() {
						@Override
						public void onSuccess(Mod123 selected) {
							selectAndPopulate(selected);
						}
						@Override
						public void onFailure(Throwable caught) {
							getCallback().showError(AON.MSG.unableToReadDeclaration(caught.getMessage()));
						}
					});
				}

				@Override
				public String getCheckAction() {
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod123CheckAEAT";
				}

				@Override
				public String getCheckDataResponseDataAction() {
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod123CheckDataResponseData";
				}

				@Override
				public String getModelInformationURL() {
					return "https://sede.agenciatributaria.gob.es/Sede/procedimientoini/GH04.shtml";
				}
		};
		admonPanel = new FiscalModelAdmonPanel<>(cbk);
		tabPanel.add( admonPanel, AON.MSG.administrationName(getModel().getAdministration()));		
	}

}
