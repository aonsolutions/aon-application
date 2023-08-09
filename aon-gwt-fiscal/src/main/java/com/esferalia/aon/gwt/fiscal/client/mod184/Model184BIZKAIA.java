package com.esferalia.aon.gwt.fiscal.client.mod184;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod184.Model184.Model184Callback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel.IFiscalModelAdmonPanelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class Model184BIZKAIA extends Model184Base {

	public Model184BIZKAIA(Model184Callback cbk,Mod184 mod184,Integer selectedIncomeIndex,Integer selectedPartnerIndex) {
		super(cbk,mod184);
		
		TabLayoutPanel tabPanel = new TabLayoutPanel(26, Unit.PX);
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		paintDeclarationTab(tabPanel);
		paintEntityTab(tabPanel);
		paintIncomeTab(tabPanel, selectedIncomeIndex);
		paintPartnersTab(tabPanel, selectedPartnerIndex);
		paintAdministrationTab(tabPanel);

		tabPanel.addSelectionHandler( event -> cbk.setSelectedTab(event.getSelectedItem()));
		if (cbk.getSelecttedTab() == null || cbk.getSelecttedTab() < 0 || cbk.getSelecttedTab() >= tabPanel.getWidgetCount()) {
			cbk.setSelectedTab(1);
		}
		tabPanel.selectTab(cbk.getSelecttedTab(), false);
	}

	protected void paintAdministrationTab(TabLayoutPanel tabPanel) {
		IFiscalModelAdmonPanelCallback<Mod184, Model184ModuleOptions> cbk = 
			new IFiscalModelAdmonPanelCallback<Mod184, Model184ModuleOptions>() {

				@Override
				public Model184ModuleOptions getOptions() {
					return getCallback().getOptions();
				}

				@Override
				public Mod184 getModel() {
					return Model184BIZKAIA.this.getModel();
				}

				@Override
				public void showError(String msg) {
					getCallback().showError(msg);
				}

				@Override
				public String getValidatePrintAction() {
					return null;
					
				}

				@Override
				public String getDownloadFileAction() {
					return Model184Base.MODEL184_FILE;
				}

				@Override
				public String getSendAction() {
					return null;
				}

				@Override
				public void sendSuccessfully() {
					// Nothing
				}

				@Override
				public String getCheckAction() {
					return null;
				}

				@Override
				public String getCheckDataResponseDataAction() {
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod184CheckDataResponseData";
				}

				@Override
				public String getModelInformationURL() {
					return "https://egoitza.araba.eus/es/-/modelo-184";
				}
		};
		admonPanel = new FiscalModelAdmonPanel<>(cbk);
		tabPanel.add( admonPanel, AON.MSG.administrationName(getModel().getAdministration()));		
	}
	
	
}
