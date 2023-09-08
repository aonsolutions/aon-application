package com.esferalia.aon.gwt.fiscal.client.mod349;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod349.Model349.Model349Callback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel.IFiscalModelAdmonPanelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class Model349AEAT extends Model349Base {
	
	private static final int OPERATORS_TAB = 1;

	public Model349AEAT(Model349Callback cbk,Mod349 mod349,Integer selectedIndex) {
		super(cbk, mod349);

		TabLayoutPanel tabPanel = new TabLayoutPanel(26, Unit.PX);
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		paintDeclarationTab(tabPanel);
		paintOperatorsTab(tabPanel, selectedIndex);
		paintAdministrationTab(tabPanel);
		
		tabPanel.selectTab(OPERATORS_TAB, false);		
		
	}

	protected void paintAdministrationTab(TabLayoutPanel tabPanel) {
		IFiscalModelAdmonPanelCallback<Mod349, Model349ModuleOptions> cbk = 
				new IFiscalModelAdmonPanelCallback<Mod349, Model349ModuleOptions>() {

					@Override
					public Model349ModuleOptions getOptions() {
						return getCallback().getOptions();
					}

					@Override
					public Mod349 getModel() {
						return Model349AEAT.this.getModel();
					}

					@Override
					public void showError(String msg) {
						getCallback().showError(msg);
					}

					@Override
					public String getValidatePrintAction() {
						return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod349ValidatePrintAEAT";
						
					}

					@Override
					public String getDownloadFileAction() {
						return Model349Base.MODEL349_FILE;
					}

					@Override
					public String getSendAction() {
						return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod349SendAEAT";
					}

					@Override
					public void sendSuccessfully() {
						Model349.SERVICE.get( getOptions().getOccam(), getModel().getId() , new AsyncCallback<Mod349>() {
							@Override
							public void onSuccess(Mod349 selected) {
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
						return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod349CheckAEAT";
					}

					@Override
					public String getCheckDataResponseDataAction() {
						return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod349CheckDataResponseData";
					}

					@Override
					public String getModelInformationURL() {
						return "https://sede.agenciatributaria.gob.es/Sede/procedimientoini/GI28.shtml";
					}
			};
			admonPanel = new FiscalModelAdmonPanel<>(cbk);
			tabPanel.add( admonPanel, AON.MSG.administrationName(getModel().getAdministration()));		
	}
	
}
