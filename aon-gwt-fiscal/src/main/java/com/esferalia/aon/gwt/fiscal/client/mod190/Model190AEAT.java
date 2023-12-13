package com.esferalia.aon.gwt.fiscal.client.mod190;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod190.Model190.Model190Callback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel.IFiscalModelAdmonPanelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model190AEAT extends Model190Base {

	private static final int PERCEPTORS_TAB = 1;

	public Model190AEAT(Model190Callback cbk,Mod190 mod190,Integer selectedIndex) {
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
						return Model190AEAT.this.getModel();
					}

					@Override
					public void showError(String msg) {
						getCallback().showError(msg);
					}

					@Override
					public String getValidatePrintAction() {
						return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod190ValidatePrintAEAT";
						
					}

					@Override
					public String getDownloadFileAction() {
						return Model190Base.MODEL190_FILE;
					}

					@Override
					public String getSendAction() {
						return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod190SendAEAT";
					}

					@Override
					public void sendSuccessfully() {
						Model190.SERVICE.getMod190( getOptions().getOccam(), getModel().getId() , new AsyncCallback<Mod190>() {
							@Override
							public void onSuccess(Mod190 selected) {
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
						return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod190CheckAEAT";
					}

					@Override
					public String getCheckDataResponseDataAction() {
						return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod190CheckDataResponseData";
					}

					@Override
					public String getModelInformationURL() {
						return "https://sede.agenciatributaria.gob.es/Sede/procedimientoini/GI10.shtml";
					}
			};
			admonPanel = new FiscalModelAdmonPanel<>(cbk);
			tabPanel.add( admonPanel, AON.MSG.administrationName(getModel().getAdministration()));		
	}
	
	protected void paintPerceptorsTab(TabLayoutPanel tabPanel, Integer selectedIndex) {
		if ( getModel().getYear() < 2015) {
			setDetailManager( new Model190AEATDetail2014( getCallback() , getModel(), selectedIndex ));
		} else if ( getModel().getYear() == 2015) {
			setDetailManager( new Model190AEATDetail2015( getCallback() , getModel(), selectedIndex ));
		} else if ( getModel().getYear() == 2016) {
			setDetailManager( new Model190AEATDetail2016( getCallback() , getModel(), selectedIndex ));
		} else if ( getModel().getYear() >= 2017 && getModel().getYear() <= 2021) {
			setDetailManager( new Model190AEATDetail2017( getCallback() , getModel(), selectedIndex ));
		} else if ( getModel().getYear() == 2022) {
			setDetailManager( new Model190AEATDetail2022( getCallback() , getModel(), selectedIndex ));
		} else {
			setDetailManager( new Model190AEATDetail2023( getCallback() , getModel(), selectedIndex ));
		}
		tabPanel.add( (Widget) getDetailManager(),  AON.MSG.receiverList() );
	}
	
}
