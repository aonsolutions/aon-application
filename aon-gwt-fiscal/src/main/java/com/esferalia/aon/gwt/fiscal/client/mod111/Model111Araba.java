package com.esferalia.aon.gwt.fiscal.client.mod111;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111.Model111Callback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel.IFiscalModelAdmonPanelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class Model111Araba extends Model111Base {
	 
	public Model111Araba(Mod111 mod111,Model111Callback callback) {
		super(mod111, callback);
	}
	
	@Override
	protected void paintAdministrationTab(TabLayoutPanel tabPanel) {
		IFiscalModelAdmonPanelCallback<Mod111, Model111ModuleOptions> cbk = 
			new IFiscalModelAdmonPanelCallback<Mod111, Model111ModuleOptions>() {

				@Override
				public Model111ModuleOptions getOptions() {
					return getCallback().getOptions();
				}

				@Override
				public Mod111 getModel() {
					return Model111Araba.this.getModel();
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
					return Model111Base.MODEL111_FILE;
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
					return null;
				}

				@Override
				public String getModelInformationURL() {
					return getModel().getPeriod().isMonthPeriod()
						?"https://egoitza.araba.eus/es/-/modelo-111-retenciones-e-ingresos-a-cuenta-del-impuesto-sobre-la-renta-de-las-personas-fisicas"
						:"https://egoitza.araba.eus/es/-/modelo-110-retenciones-e-ingresos-a-cuenta-del-impuesto-sobre-la-renta-de-las-personas-f%C3%ADsicas"
						;
				}
		};
		admonPanel = new FiscalModelAdmonPanel<>(cbk);
		tabPanel.add( admonPanel, AON.MSG.administrationName(getModel().getAdministration()));		
	}

}
