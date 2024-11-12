package com.esferalia.aon.gwt.fiscal.client.mod369;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod369.Model369.Model369Callback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel.IFiscalModelAdmonPanelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod369;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

//FALTA - POR AHORA SOLO ESTA PREPARADO EL DE LA AEAT
public class Model369NAVARRA extends Model369Base {

	public Model369NAVARRA(Model369Callback cbk,Mod369 mod369,Integer selectedIncomeIndex,Integer selectedPartnerIndex) {
		super(cbk,mod369);
		
		TabLayoutPanel tabPanel = new TabLayoutPanel(26, Unit.PX);
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		paintDeclarationTab(tabPanel);
//		paintEntityTab(tabPanel);
//		paintIncomeTab(tabPanel, selectedIncomeIndex);
//		paintPartnersTab(tabPanel, selectedPartnerIndex);
		paintAdministrationTab(tabPanel);

		tabPanel.addSelectionHandler( event -> cbk.setSelectedTab(event.getSelectedItem()));
		if (cbk.getSelectedTab() == null || cbk.getSelectedTab() < 0 || cbk.getSelectedTab() >= tabPanel.getWidgetCount()) {
			cbk.setSelectedTab(1);
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
					return Model369NAVARRA.this.getModel();
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
					return Model369Base.MODEL369_FILE;
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
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod369CheckDataResponseData";
				}

				@Override
				public String getModelInformationURL() {
					return "https://egoitza.araba.eus/es/-/modelo-369";
				}
		};
		admonPanel = new FiscalModelAdmonPanel<>(cbk);
		tabPanel.add( admonPanel, AON.MSG.administrationName(getModel().getAdministration()));		
	}
	
	
}
