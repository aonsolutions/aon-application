package com.esferalia.aon.gwt.fiscal.client.mod115;


import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod115.Model115.Model115Callback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel.IFiscalModelAdmonPanelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class Model760Navarra extends Model115Base {
	
	public Model760Navarra(Mod115 mod115,Model115Callback callback) {
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
					return Model760Navarra.this.getModel();
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
					return Model115Base.MODEL115_FILE;
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
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod115CheckDataResponseData";
				}

				@Override
				public String getModelInformationURL() {
					return "https://www.navarra.es/es/tramites/on/-/line/Retenciones";
				}
		};
		admonPanel = new FiscalModelAdmonPanel<>(cbk);
		tabPanel.add( admonPanel, AON.MSG.administrationName(getModel().getAdministration()));		
	}
}
