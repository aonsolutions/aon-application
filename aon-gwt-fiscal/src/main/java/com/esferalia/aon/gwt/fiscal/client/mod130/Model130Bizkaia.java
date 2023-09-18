package com.esferalia.aon.gwt.fiscal.client.mod130;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod130.Model130.Model130Callback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel.IFiscalModelAdmonPanelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class Model130Bizkaia extends Model130Base {
	
	public Model130Bizkaia(Mod130 mod130, Model130Callback callback) {
		super(mod130, callback);
	}

	@Override
	protected void paintAdministrationTab(TabLayoutPanel tabPanel) {
		IFiscalModelAdmonPanelCallback<Mod130, Model130ModuleOptions> cbk = 
			new IFiscalModelAdmonPanelCallback<Mod130, Model130ModuleOptions>() {

				@Override
				public Model130ModuleOptions getOptions() {
					return getCallback().getOptions();
				}

				@Override
				public Mod130 getModel() {
					return Model130Bizkaia.this.getModel();
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
					return Model130Base.MODEL130_FILE;
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
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod130CheckDataResponseData";
				}

				@Override
				public String getModelInformationURL() {
					return "https://www.bizkaia.eus/ogasuna/ereduak/modelos.asp?textomodelo=130&idioma=CA&aceptar=Buscar&Tem_Codigo=2093&dpto_biz=5&codpath_biz=5%7C3587%7C2093";
				}
		};
		admonPanel = new FiscalModelAdmonPanel<>(cbk);
		tabPanel.add( admonPanel, AON.MSG.administrationName(getModel().getAdministration()));		
	}

}
