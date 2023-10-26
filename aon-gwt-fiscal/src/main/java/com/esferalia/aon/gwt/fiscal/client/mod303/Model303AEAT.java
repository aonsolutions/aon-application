package com.esferalia.aon.gwt.fiscal.client.mod303;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303.Model303Callback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel.IFiscalModelAdmonPanelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TabLayoutPanel;

abstract class Model303AEAT extends Model303Base {

	protected static final String SI_1 = "(1) SI";
	protected static final String NO_2 = "(2) NO";
	
	protected AonTextBox receiptBox;

	protected Model303AEAT(Mod303 mod303, Model303Callback cbk) {
		super(mod303, cbk);
	}

	protected void paintAdministrationTab(TabLayoutPanel tabPanel) {
		IFiscalModelAdmonPanelCallback<Mod303, Model303ModuleOptions> cbk = 
				new IFiscalModelAdmonPanelCallback<Mod303, Model303ModuleOptions>() {

					@Override
					public Model303ModuleOptions getOptions() {
						return getCallback().getOptions();
					}

					@Override
					public Mod303 getModel() {
						return Model303AEAT.this.getModel();
					}

					@Override
					public void showError(String msg) {
						getCallback().showError(msg);
					}

					@Override
					public String getValidatePrintAction() {
						return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod303ValidatePrintAEAT";
						
					}

					@Override
					public String getDownloadFileAction() {
						return Model303Base.MODEL303_FILE;
					}

					@Override
					public String getSendAction() {
						return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod303SendAEAT";
					}

					@Override
					public void sendSuccessfully() {
						Model303.service.getMod303(getCallback().getOptions().getOccam(), 
								getModel().getId(), new AsyncCallback<Mod303>() {
							@Override
							public void onSuccess(Mod303 selected) {
								selectAndPopulate(selected);
								showPaymentInfo(selected);
							}
							@Override
							public void onFailure(Throwable caught) {
								getCallback().showError(AON.MSG.unableToReadDeclaration(caught.getMessage()));
							}
						});
					}

					@Override
					public String getCheckAction() {
						return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod303CheckAEAT";
					}

					@Override
					public String getCheckDataResponseDataAction() {
						return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod303CheckDataResponseData";
					}

					@Override
					public String getModelInformationURL() {
						return "https://sede.agenciatributaria.gob.es/Sede/procedimientoini/G414.shtml";
					}
			};
			admonPanel = new FiscalModelAdmonPanel<>(cbk);
			tabPanel.add( admonPanel, AON.MSG.administrationName(getModel().getAdministration()));		
	}
	
	@Override
	protected void decorateDeclarationTab() {
		if (receiptBox != null) {
			receiptBox.setValue( getModel().getNumber() );
		}
	}
	
}
