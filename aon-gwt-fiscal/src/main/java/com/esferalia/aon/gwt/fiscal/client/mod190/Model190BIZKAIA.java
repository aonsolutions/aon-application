package com.esferalia.aon.gwt.fiscal.client.mod190;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod190.Model190.Model190Callback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel.IFiscalModelAdmonPanelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model190BIZKAIA extends Model190Base {

	private static final int PERCEPTORS_TAB = 1;

	public Model190BIZKAIA(Model190Callback cbk,Mod190 mod190,Integer selectedIndex) {
		super(cbk, mod190);
		
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
		IFiscalModelAdmonPanelCallback<Mod190, Model190ModuleOptions> cbk = 
			new IFiscalModelAdmonPanelCallback<Mod190, Model190ModuleOptions>() {

				@Override
				public Model190ModuleOptions getOptions() {
					return getCallback().getOptions();
				}

				@Override
				public Mod190 getModel() {
					return Model190BIZKAIA.this.getModel();
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
					return Model190Base.MODEL190_FILE;
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
					return "https://www.bizkaia.eus/ogasuna/ereduak/modelos.asp?textomodelo=190&idioma=CA&aceptar=Buscar&Tem_Codigo=2093&dpto_biz=5&codpath_biz=5%7C3587%7C2093";
				}
		};
		admonPanel = new FiscalModelAdmonPanel<>(cbk);
		tabPanel.add( admonPanel, AON.MSG.administrationName(getModel().getAdministration()));		
	}

	protected void paintPerceptorsTab(TabLayoutPanel tabPanel, Integer selectedIndex) {
		if ( getModel().getYear() >= 2022) {
			setDetailManager( new Model190BIZKAIADetail2022( getCallback() , getModel(), selectedIndex ));
		} else if ( getModel().getYear() >= 2017) {
			setDetailManager( new Model190BIZKAIADetail2017( getCallback() , getModel(), selectedIndex ));
		} else {
			setDetailManager( new Model190BIZKAIADetail2016( getCallback() , getModel(), selectedIndex ));
		}
		tabPanel.add( (Widget) getDetailManager(),  AON.MSG.receiverList());
	}
}
