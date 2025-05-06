package com.esferalia.aon.gwt.fiscal.client.sales;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.ui.DeckLayoutPanel;

public class SalesModule extends MainEntryPoint {
	
	private static final Logger LOGGER = Logger.getLogger(SalesModule.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	private RegistryModuleOptions options;
	
	private DeckLayoutPanel deckLayoutPanel;
	private SalesList salesList;
//	private TariffEntry tariffEntry;
	
	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		
		options = new RegistryModuleOptions();
		options.setParentWidget(root);
		options.setDomainName(getCurrentDomainName());
		options.setDomain(getCurrentDomain());
		options.setUser(getCurrentUser());
		
		moduleLoad();
	}
	
	public void moduleLoad() {
		AON.ensureInjected();
		
		deckLayoutPanel = new DeckLayoutPanel();
		
//		tariffEntry = new TariffEntry(options) {
//
//			@Override
//			protected void onBackClick() {
//				showTariffList();
//			}
//			
//		};
//		
		salesList = new SalesList(options) {

			@Override
			protected void onSaleSelect(Sales sale) {
				showSelectedSale(sale);
			}
		
		};
		deckLayoutPanel.add(salesList);
//		deckLayoutPanel.add(tariffEntry);
		deckLayoutPanel.showWidget(salesList);
		
		options.getParentWidget().add(deckLayoutPanel);
	}
	
//	private void showTariffList() {
//		deckLayoutPanel.showWidget(tariffList);
//		tariffList.onSearch();
//	}
	
	private void showSelectedSale(Sales sale) {
//		Window.alert("showSelectedSale: " + sale.getId());
//		deckLayoutPanel.showWidget(saleEntry);
//		saleEntry.setSale(sale.getId());
	}
	
}
