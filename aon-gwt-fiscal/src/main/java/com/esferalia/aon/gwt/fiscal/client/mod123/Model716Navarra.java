package com.esferalia.aon.gwt.fiscal.client.mod123;


import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod123.Model123.Model123Callback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel.IFiscalModelAdmonPanelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class Model716Navarra extends Model123Base {

	public Model716Navarra(Mod123 mod123,Model123Callback callback) {
		super(mod123,callback);
	}
	
	@Override
	protected void paintAdministrationTab(TabLayoutPanel tabPanel) {
		IFiscalModelAdmonPanelCallback<Mod123, Model123ModuleOptions> cbk = 
			new IFiscalModelAdmonPanelCallback<Mod123, Model123ModuleOptions>() {

				@Override
				public Model123ModuleOptions getOptions() {
					return getCallback().getOptions();
				}

				@Override
				public Mod123 getModel() {
					return Model716Navarra.this.getModel();
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
					return Model123Base.MODEL123_FILE;
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
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod123CheckDataResponseData";
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
