package com.esferalia.aon.gwt.fiscal.client.mod180;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod180.Model180.Model180Callback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel.IFiscalModelAdmonPanelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class Model180AEAT extends Model180Base {

	private static final int PERCEPTORS_TAB = 1;

	public Model180AEAT(Model180Callback cbk, Mod180 mod180, Integer selectedIndex) {
		super(cbk, mod180);
		
		TabLayoutPanel tabPanel = new TabLayoutPanel(26, Unit.PX);
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		paintDeclarationTab(tabPanel);
		paintPerceptorsTab(tabPanel, selectedIndex);
		paintAdministrationTab(tabPanel);
		
		tabPanel.selectTab(PERCEPTORS_TAB, false);
		
	}
	
	protected void paintAdministrationTab(TabLayoutPanel tabPanel) {
		IFiscalModelAdmonPanelCallback<Mod180, Model180ModuleOptions> cbk = 
				new IFiscalModelAdmonPanelCallback<Mod180, Model180ModuleOptions>() {

					@Override
					public Model180ModuleOptions getOptions() {
						return getCallback().getOptions();
					}

					@Override
					public Mod180 getModel() {
						return Model180AEAT.this.getModel();
					}

					@Override
					public void showError(String msg) {
						getCallback().showError(msg);
					}

					@Override
					public String getValidatePrintAction() {
						return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod180ValidatePrintAEAT";
						
					}

					@Override
					public String getDownloadFileAction() {
						return Model180Base.MODEL180_FILE;
					}

					@Override
					public String getSendAction() {
						return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod180SendAEAT";
					}

					@Override
					public void sendSuccessfully() {												
						Model180.SERVICE.get( getOptions().getOccam(), getModel().getId() , new AsyncCallback<Mod180>() {
							@Override
							public void onSuccess(Mod180 selected) {
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
						return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod180CheckAEAT";						
					}

					@Override
					public String getCheckDataResponseDataAction() {
						return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod180CheckDataResponseData";						
					}

					@Override
					public String getModelInformationURL() {
						return "https://sede.agenciatributaria.gob.es/Sede/procedimientoini/GI00.shtml";
					}
			};
			admonPanel = new FiscalModelAdmonPanel<>(cbk);
			tabPanel.add( admonPanel, AON.MSG.administrationName(getModel().getAdministration()));		
	}

}
