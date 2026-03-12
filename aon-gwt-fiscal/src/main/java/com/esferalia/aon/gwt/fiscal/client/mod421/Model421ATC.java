package com.esferalia.aon.gwt.fiscal.client.mod421;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod421.Model421.Model421Callback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel.IFiscalModelAdmonPanelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TabLayoutPanel;

abstract class Model421ATC extends Model421Base {

	protected AonTextBox receiptBox;

	protected Model421ATC(Mod421 mod421, Model421Callback cbk) {
		super(mod421, cbk);
	}

	protected void paintAdministrationTab(TabLayoutPanel tabPanel) {
		IFiscalModelAdmonPanelCallback<Mod421, Model421ModuleOptions> cbk = 
				new IFiscalModelAdmonPanelCallback<Mod421, Model421ModuleOptions>() {

					@Override
					public Model421ModuleOptions getOptions() {
						return getCallback().getOptions();
					}

					@Override
					public Mod421 getModel() {
						return Model421ATC.this.getModel();
					}

					@Override
					public void showError(String msg) {
						getCallback().showError(msg);
					}

					@Override
					public String getValidatePrintAction() {
						return GWT.getHostPageBaseURL() + "aon_gwt_fiscal/ms/Mod421ValidatePrintATC";
						
					}

					@Override
					public String getDownloadFileAction() {
						return GWT.getHostPageBaseURL() + "aon_gwt_fiscal/ms/Model421File";
					}

					@Override
					public String getSendAction() {
						return null;
					}

					@Override
					public void sendSuccessfully() {
						Model421.service.getMod421(getCallback().getOptions().getOccam(), 
								getModel().getId(), new AsyncCallback<Mod421>() {
							@Override
							public void onSuccess(Mod421 selected) {
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
						return null;
					}

					@Override
					public String getCheckDataResponseDataAction() {
						return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod421CheckDataResponseData";
					}

					@Override
					public String getModelInformationURL() {
						return "https://www3.gobiernodecanarias.org/tributos/atc/w/modelo-421";
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
