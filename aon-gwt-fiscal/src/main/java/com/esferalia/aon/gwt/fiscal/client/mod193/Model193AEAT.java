package com.esferalia.aon.gwt.fiscal.client.mod193;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod193.Model193.Model193Callback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel.IFiscalModelAdmonPanelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model193AEAT extends Model193Base {

	private static final int PERCEPTORS_TAB = 1;

	public Model193AEAT(Model193Callback cbk,Mod193 mod193,Integer selectedIndex) {
		super(cbk, mod193 );
		
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
		IFiscalModelAdmonPanelCallback<Mod193, Model193ModuleOptions> cbk = 
				new IFiscalModelAdmonPanelCallback<Mod193, Model193ModuleOptions>() {

					@Override
					public Model193ModuleOptions getOptions() {
						return getCallback().getOptions();
					}

					@Override
					public Mod193 getModel() {
						return Model193AEAT.this.getModel();
					}

					@Override
					public void showError(String msg) {
						getCallback().showError(msg);
					}

					@Override
					public String getValidatePrintAction() {
						return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod193ValidatePrintAEAT";						
					}

					@Override
					public String getDownloadFileAction() {
						return Model193Base.MODEL193_FILE;
					}

					@Override
					public String getSendAction() {
						return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod193SendAEAT";
					}

					@Override
					public void sendSuccessfully() {
						Model193.SERVICE.get( getOptions().getOccam(), getModel().getId() , new AsyncCallback<Mod193>() {
							@Override
							public void onSuccess(Mod193 selected) {
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
						return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod193CheckAEAT";
					}

					@Override
					public String getCheckDataResponseDataAction() {
						return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod193CheckDataResponseData";
					}

					@Override
					public String getModelInformationURL() {
						if (getModel().isAEAT()) return "https://sede.agenciatributaria.gob.es/Sede/procedimientoini/GI12.shtml";
						else if (getModel().isAraba()) return "https://egoitza.araba.eus/es/-/modelo-193";
						else if (getModel().isGipuzkoa()) return "https://www.gipuzkoa.eus/es/web/ogasuna/impuestos/modelo/193";
						else if (getModel().isBizkaia()) return "https://www.bizkaia.eus/ogasuna/ereduak/modelos.asp?textomodelo=193&idioma=CA&aceptar=Buscar&Tem_Codigo=2093&dpto_biz=5&codpath_biz=5%7C3587%7C2093";
						else if (getModel().isNavarra()) return "https://www.navarra.es/es/tramites/on/-/line/Retencion-rentas-de-capital-mobiliario-193";
						else return null;
					}
			};
			admonPanel = new FiscalModelAdmonPanel<>(cbk);
			tabPanel.add( admonPanel, AON.MSG.administrationName(getModel().getAdministration()));		
	}

	private void paintPerceptorsTab(TabLayoutPanel tabPanel, Integer selectedIndex) {
		setDetailManager( new Model193AEATDetail2016( getCallback() , getModel(), selectedIndex ));
		tabPanel.add( (Widget) getDetailManager(),  AON.MSG.receiverList() );
	}
	
}
