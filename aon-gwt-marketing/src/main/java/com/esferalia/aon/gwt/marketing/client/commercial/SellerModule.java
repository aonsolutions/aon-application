package com.esferalia.aon.gwt.marketing.client.commercial;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.marketing.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;

public class SellerModule extends MainEntryPoint {
	
	private static final Logger LOGGER = Logger.getLogger(SellerModule.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	private static CommonServiceAsync COMMON_SERVICE;
	
	private SellerModuleOptions options;
	
	private DeckLayoutPanel deckLayoutPanel;
	private SellerModulePanel sellerModulePanel;
	private SellerEntryPanel sellerEntryPanel;
	
	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		options = new SellerModuleOptions();
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
		
		sellerModulePanel = new SellerModulePanel(options) {

			@Override
			protected void onSellerSelect(Seller seller) {
				showSelectedSeller(seller);
			}

			@Override
			protected void onSellerCreate(Seller seller) {
				showCreatedSeller(seller);
			}
		
		};
		
		sellerEntryPanel = new SellerEntryPanel(options) {

			@Override
			protected void onBackClick() {
				showSellerList();
				sellerModulePanel.getSearchTextBox().setValue(null, false);
			}
			
			@Override
			protected void onSellerDeleteClick(Integer sellerId) {
				deleteSeller(sellerId);
			}

			@Override
			protected Integer getSellerListPosition(Integer sellerId) {
				return sellerModulePanel.getSellerListPosition(sellerId);
			}

			@Override
			protected Integer getSellerListCount() {
				return sellerModulePanel.getSellerListCount();
			}

			@Override
			protected Seller getPreviusSeller(Integer sellerId) {
				return sellerModulePanel.getPreviusSeller(sellerId);
			}

			@Override
			protected Seller getNextSeller(Integer sellerId) {
				return sellerModulePanel.getNextSeller(sellerId);
			}

			@Override
			protected void onSellerSelectionChange(Seller seller) {
				showSelectedSeller(seller);
			}
		};
			
		deckLayoutPanel.add(sellerModulePanel);
		deckLayoutPanel.add(sellerEntryPanel);
		deckLayoutPanel.showWidget(sellerModulePanel);
		
		dockLayoutPanel.add( deckLayoutPanel );
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		root.add(dockLayoutPanel);
	}
	
	private void showSellerList() {
		deckLayoutPanel.showWidget(sellerModulePanel);
		sellerModulePanel.onSearch(options);
	}
	
	private void showSelectedSeller(Seller seller) {
		deckLayoutPanel.showWidget(sellerEntryPanel);
		sellerEntryPanel.setSeller(seller, true);
	}
	
	private void showCreatedSeller(Seller seller) {
		deckLayoutPanel.showWidget(sellerEntryPanel);
		sellerEntryPanel.setSeller(seller, false);
	}
	
	private void deleteSeller(Integer sellerId) {
		COMMON_SERVICE.deleteSeller(options.getDomainName(), options.getDomain(), options.getUser(), sellerId, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				showSellerList();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error borrado: " + caught.getMessage());
			}
		});
	}
	
}
