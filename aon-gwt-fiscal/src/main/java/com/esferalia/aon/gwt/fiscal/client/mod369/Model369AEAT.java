package com.esferalia.aon.gwt.fiscal.client.mod369;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod369.Model369.Model369Callback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel.IFiscalModelAdmonPanelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod369;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class Model369AEAT extends Model369Base {

//	public Model369AEAT(Model369Callback cbk,Mod369 mod369, Integer selectedDetailIndex) {
	public Model369AEAT(Model369Callback cbk, Mod369 mod369) {
		super(cbk, mod369);
		
		TabLayoutPanel tabPanel = new TabLayoutPanel(26, Unit.PX);
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		paintDeclarationTab(tabPanel);
//		paintEntityTab(tabPanel);
//		paintDetailTab(tabPanel, selectedDetailIndex);
//		paintPartnersTab(tabPanel, selectedPartnerIndex);
		paintDetailsTab(tabPanel);
		paintDetailsOtherTab(tabPanel);
		paintCorrectionsTab(tabPanel);
		paintAdministrationTab(tabPanel);
		
		tabPanel.addSelectionHandler( event -> cbk.setSelectedTab(event.getSelectedItem()));
		if (cbk.getSelectedTab() == null || cbk.getSelectedTab() < 0 || cbk.getSelectedTab() >= tabPanel.getWidgetCount()) {
			//cbk.setSelectedTab(1);
			cbk.setSelectedTab(0);
		}
		tabPanel.selectTab(cbk.getSelectedTab(), false);
	}
	
	protected void paintAdministrationTab(TabLayoutPanel tabPanel) {
		IFiscalModelAdmonPanelCallback<Mod369, Model369ModuleOptions> cbk = 
				new IFiscalModelAdmonPanelCallback<Mod369, Model369ModuleOptions>() {

					@Override
					public Model369ModuleOptions getOptions() {
						return getCallback().getOptions();
					}

					@Override
					public Mod369 getModel() {
						return Model369AEAT.this.getModel();
					}

					@Override
					public void showError(String msg) {
						getCallback().showError(msg);
					}

					@Override
					public String getValidatePrintAction() {
						return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod369ValidatePrintAEAT";						
					}

					@Override
					public String getDownloadFileAction() {
						return Model369Base.MODEL369_FILE;
					}

					@Override
					public String getSendAction() {
						return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod369SendAEAT";
					}

					@Override
					public void sendSuccessfully() {
						Model369.SERVICE.get( getOptions().getOccam(), getModel().getId() , new AsyncCallback<Mod369>() {
							@Override
							public void onSuccess(Mod369 selected) {
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
						return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod369CheckAEAT";
					}

					@Override
					public String getCheckDataResponseDataAction() {
						return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod369CheckDataResponseData";
					}

					@Override
					public String getModelInformationURL() {
						return "https://sede.agenciatributaria.gob.es/Sede/procedimientoini/G420.shtml";
					}
			};
			admonPanel = new FiscalModelAdmonPanel<>(cbk);
			tabPanel.add( admonPanel, AON.MSG.administrationName(getModel().getAdministration()));		
	}

}
