package com.esferalia.aon.gwt.fiscal.client.mod115;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod115.Model115.Model115Callback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel.IFiscalModelAdmonPanelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class Model115AEAT extends Model115Base {
	
	public Model115AEAT(Mod115 mod115,Model115Callback callback) {
		super(mod115, callback);
	}
	
	@Override
	protected void paintAdministrationTab(TabLayoutPanel tabPanel) {
		IFiscalModelAdmonPanelCallback<Mod115, Model115ModuleOptions> cbk = 
			new IFiscalModelAdmonPanelCallback<Mod115, Model115ModuleOptions>() {

				@Override
				public Model115ModuleOptions getOptions() {
					return getCallback().getOptions();
				}

				@Override
				public Mod115 getModel() {
					return Model115AEAT.this.getModel();
				}

				@Override
				public void showError(String msg) {
					getCallback().showError(msg);
				}

				@Override
				public String getValidatePrintAction() {
					return GWT.getHostPageBaseURL() + "aon_gwt_fiscal/ms/Mod115ValidatePrintAEAT";
					
				}

				@Override
				public String getDownloadFileAction() {
					return Model115Base.MODEL115_FILE;
				}

				@Override
				public String getSendAction() {
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod115SendAEAT";
				}

				@Override
				public void sendSuccessfully() {
					Model115.SERVICE.getMod115(getCallback().getOptions().getOccam(), 
							getModel().getId(), new AsyncCallback<Mod115>() {
						@Override
						public void onSuccess(Mod115 selected) {
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
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod115CheckAEAT";
				}

				@Override
				public String getCheckDataResponseDataAction() {
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod115CheckDataResponseData";
				}

				@Override
				public String getModelInformationURL() {
					return "https://sede.agenciatributaria.gob.es/Sede/procedimientoini/GH02.shtml";
				}
		};
		admonPanel = new FiscalModelAdmonPanel<>(cbk);
		tabPanel.add( admonPanel, AON.MSG.administrationName(getModel().getAdministration()));		
	}

}
