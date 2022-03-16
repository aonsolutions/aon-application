package com.esferalia.aon.gwt.fiscal.client.mod111;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111.Model111Callback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel.IFiscalModelAdmonPanelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class Model111AEAT extends Model111Base {
	
	public Model111AEAT(Mod111 mod111, Model111Callback callback) {
		super(mod111, callback);
	}

	@Override
	protected void paintAdministrationTab(TabLayoutPanel tabPanel) {
		IFiscalModelAdmonPanelCallback<Mod111, Model111ModuleOptions> cbk = 
			new IFiscalModelAdmonPanelCallback<Mod111, Model111ModuleOptions>() {

				@Override
				public Model111ModuleOptions getOptions() {
					return getCallback().getOptions();
				}

				@Override
				public Mod111 getModel() {
					return Model111AEAT.this.getModel();
				}

				@Override
				public void showError(String msg) {
					getCallback().showError(msg);
				}

				@Override
				public String getValidatePrintAction() {
					return GWT.getHostPageBaseURL() + "aon_gwt_fiscal/ms/Mod111ValidatePrintAEAT";
					
				}

				@Override
				public String getDownloadFileAction() {
					return Model111Base.MODEL111_FILE;
				}

				@Override
				public String getSendAction() {
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod111SendAEAT";
				}

				@Override
				public void sendSuccessfully() {
					Model111.SERVICE.getMod111(getCallback().getOptions().getOccam(), 
							getModel().getId(), new AsyncCallback<Mod111>() {
						@Override
						public void onSuccess(Mod111 selected) {
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
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod111CheckAEAT";
				}

				@Override
				public String getCheckDataResponseDataAction() {
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod111CheckDataResponseData";
				}

				@Override
				public String getModelInformationURL() {
					return "https://sede.agenciatributaria.gob.es/Sede/procedimientoini/GH01.shtml";
				}
		};
		admonPanel = new FiscalModelAdmonPanel<>(cbk);
		tabPanel.add( admonPanel, AON.MSG.administrationName(getModel().getAdministration()));		
	}

}
