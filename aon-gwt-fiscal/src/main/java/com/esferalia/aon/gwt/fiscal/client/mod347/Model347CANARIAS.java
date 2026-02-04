package com.esferalia.aon.gwt.fiscal.client.mod347;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod347.Model347.Model347Callback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel.IFiscalModelAdmonPanelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class Model347CANARIAS extends Model347Base {

	public Model347CANARIAS(Model347Callback cbk, Mod347 mod347, Integer selectedIndexDeclared, Integer selectedIndexAsset, int tabPanelIndex) {
		super(cbk, mod347);
		
		TabLayoutPanel tabPanel = new TabLayoutPanel(26, Unit.PX);
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.AON_CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		paintDeclarationTab(tabPanel);
		paintDeclaredTab(tabPanel, selectedIndexDeclared);
		paintAssetsTab(tabPanel, selectedIndexAsset);
		paintAdministrationTab(tabPanel);
		addTabPanelSelectionHandler(tabPanel);
		
		tabPanel.selectTab(tabPanelIndex, true);
	}

	protected void paintAdministrationTab(TabLayoutPanel tabPanel) {
		IFiscalModelAdmonPanelCallback<Mod347, Model347ModuleOptions> cbk = 
				new IFiscalModelAdmonPanelCallback<Mod347, Model347ModuleOptions>() {

					@Override
					public Model347ModuleOptions getOptions() {
						return getCallback().getOptions();
					}

					@Override
					public Mod347 getModel() {
						return Model347CANARIAS.this.getModel();
					}

					@Override
					public void showError(String msg) {
						getCallback().showError(msg);
					}

					@Override
					public String getValidatePrintAction() {
						// FALTA
//						return GWT.getHostPageBaseURL() + "aon_gwt_fiscal/ms/Mod415ValidatePrintATC";
						return null;
					}

					@Override
					public String getDownloadFileAction() {
						// FALTA
//						return GWT.getHostPageBaseURL() + "aon_gwt_fiscal/ms/Model415File";
						return null; 
					}

					@Override
					public String getSendAction() {
						return null;
					}

					@Override
					public void sendSuccessfully() {
						Model347.SERVICE.get( getOptions().getOccam(), getModel().getId() , new AsyncCallback<Mod347>() {
							@Override
							public void onSuccess(Mod347 selected) {
								if (selected == null) {
									getCallback().showError(AON.MSG.unableToFindDeclaration());
								} else {
									select(selected);									
									admonPanel.manageLinks();
								}
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
						return GWT.getHostPageBaseURL() + "aon_gwt_fiscal/ms/Mod347CheckDataResponseData";
					}

					@Override
					public String getModelInformationURL() {
						return "https://www3.gobiernodecanarias.org/tributos/atc/w/modelo-415";
						
					}
					
					// FALTA
					// Otro link para la descarga del fichero TXT y poder importar los declarados desde el programa de ayuda
//					@Override
//					public boolean isBoeFormatEnabled() {
//						return true;						
//					}
					
			};
			
			admonPanel = new FiscalModelAdmonPanel<>(cbk);
			tabPanel.add( admonPanel, AON.MSG.administrationName(getModel().getAdministration()));		
	}
	
}
