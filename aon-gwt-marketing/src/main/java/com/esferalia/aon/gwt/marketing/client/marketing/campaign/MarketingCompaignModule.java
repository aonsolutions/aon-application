package com.esferalia.aon.gwt.marketing.client.marketing.campaign;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.marketing.client.MainEntryPoint;
import com.esferalia.aon.gwt.marketing.client.marketing.MarketingModuleOptions;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.MarketingCampaign;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;

public class MarketingCompaignModule extends MainEntryPoint {
	
	private static final Logger LOGGER = Logger.getLogger(MarketingCompaignModule.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	private static CommonServiceAsync COMMON_SERVICE;
	
	private MarketingModuleOptions options;
	
	private DeckLayoutPanel deckLayoutPanel;
	private MarketingCampaignModulePanel marketignCampaignModulePanel;
	private MarketingCampaignEntryPanel marketignCampaignEntryPanel;
	
	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		options = new MarketingModuleOptions();
		options.setParentWidget(root);
		options.setDomainName(getCurrentDomainName());
		options.setDomain(getCurrentDomain());
		options.setUser(getCurrentUser());
		
		COMMON_SERVICE.getAonConfiguration(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), new AsyncCallback<AonConfiguration>() {
			
			@Override
			public void onSuccess(AonConfiguration config) {
				options.setConfiguration(config);
				moduleLoad();
			}
			
			@Override public void onFailure(Throwable caught) {
				Window.alert( "Error al cargar el module" );
				moduleLoad();
			}
		});
	}
	
	public void moduleLoad() {
		AON.ensureInjected();
		
		DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		
		deckLayoutPanel = new DeckLayoutPanel();
		
		marketignCampaignEntryPanel = new MarketingCampaignEntryPanel(options) {

			@Override
			protected void onBackClick() {
				showMarketingCampaignList();
			}

			@Override
			protected void onCampaignDeleteClick(MarketingCampaign marketingCampaign) {
				deleteMarketingCampaign(marketingCampaign);
			}
			
		};
		
		marketignCampaignModulePanel = new MarketingCampaignModulePanel(options) {

			@Override
			protected void onMarketingCampaignSelect(MarketingCampaign marketingCampaign) {
				showSelectedMarketingCampaign(marketingCampaign);
			}
		
		};
			
		deckLayoutPanel.add(marketignCampaignModulePanel);
		deckLayoutPanel.add(marketignCampaignEntryPanel);
		deckLayoutPanel.showWidget(marketignCampaignModulePanel);
		
		dockLayoutPanel.add( deckLayoutPanel );
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		root.add(dockLayoutPanel);
	}
	
	private void showMarketingCampaignList() {
		deckLayoutPanel.showWidget(marketignCampaignModulePanel);
		marketignCampaignModulePanel.onSearch(options);
	}
	
	private void showSelectedMarketingCampaign(MarketingCampaign marketingCampaign) {
		deckLayoutPanel.showWidget(marketignCampaignEntryPanel);
		marketignCampaignEntryPanel.setMarketingCampaign(marketingCampaign);
	}
	
	private void deleteMarketingCampaign(MarketingCampaign marketingCampaign) {
		COMMON_SERVICE.deleteMarketingCampaign(options.getDomainName(), options.getDomain(), options.getUser(), marketingCampaign.getId(), new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				showMarketingCampaignList();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error borrado: " + caught.getMessage());
			}
		});
	}
	
}
