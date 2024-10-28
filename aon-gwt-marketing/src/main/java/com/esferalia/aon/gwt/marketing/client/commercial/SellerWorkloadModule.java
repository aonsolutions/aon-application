package com.esferalia.aon.gwt.marketing.client.commercial;

import java.util.function.Consumer;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.marketing.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.SellerWorkloadParams;
import com.esferalia.aon.occam.api.model.registry.SellerWorkload;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;

public class SellerWorkloadModule extends MainEntryPoint {
	
	private static final Logger LOGGER = Logger.getLogger(SellerWorkloadModule.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	private static CommonServiceAsync COMMON_SERVICE;
	
	private SellerModuleOptions options;
	
	private DeckLayoutPanel deckLayoutPanel;
	private SellerWorkloadModulePanel sellerWorkloadModulePanel;
	private SellerWorkloadEntryPanel sellerWorkloadEntryPanel;
	
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
		
		sellerWorkloadModulePanel = new SellerWorkloadModulePanel(options) {

			@Override
			protected void onSellerWorkloadSelect(SellerWorkload sellerWorkload) {
				showSelectedSellerWorkload(sellerWorkload);
			}
		
		};
		
		sellerWorkloadEntryPanel = new SellerWorkloadEntryPanel(options) {

			@Override
			protected void onBackClick() {
				showSellerWorkloadList();
				sellerWorkloadModulePanel.getSearchTextBox().setValue(null, false);
			}

			@Override
			protected void getSellerWorkloadListCount(Consumer<Integer> finish) {
				sellerWorkloadModulePanel.getSellerListCount(count -> finish.accept(count));
			}

			@Override
			protected void onSellerWorkloadSelectionChange(SellerWorkload sellerWorkload, Integer position) {
				showSelectedSellerWorkload(sellerWorkload, position);
			}

			@Override
			protected SellerWorkloadParams getSellerWorkloadListParams() {
				return sellerWorkloadModulePanel.getSellerListParams();
			}
		};
			
		deckLayoutPanel.add(sellerWorkloadModulePanel);
		deckLayoutPanel.add(sellerWorkloadEntryPanel);
		deckLayoutPanel.showWidget(sellerWorkloadModulePanel);
		
		dockLayoutPanel.add( deckLayoutPanel );
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		root.add(dockLayoutPanel);
	}
	
	private void showSellerWorkloadList() {
		deckLayoutPanel.showWidget(sellerWorkloadModulePanel);
		sellerWorkloadModulePanel.onSearch(options);
	}
	
	private void showSelectedSellerWorkload(SellerWorkload sellerWorkload) {
		deckLayoutPanel.showWidget(sellerWorkloadEntryPanel);
		sellerWorkloadEntryPanel.setSellerWorkload(sellerWorkload, sellerWorkloadModulePanel.getSellerListPosition(sellerWorkload.getId()));
	}
	
	private void showSelectedSellerWorkload(SellerWorkload sellerWorkload, Integer position) {
		deckLayoutPanel.showWidget(sellerWorkloadEntryPanel);
		sellerWorkloadEntryPanel.setSellerWorkload(sellerWorkload, position);
	}
	
}
