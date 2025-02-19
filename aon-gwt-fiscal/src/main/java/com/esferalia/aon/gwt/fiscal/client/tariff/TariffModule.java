package com.esferalia.aon.gwt.fiscal.client.tariff;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.occam.api.model.tariff.Tariff;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.ui.DeckLayoutPanel;

public class TariffModule extends MainEntryPoint {
	
	private static final Logger LOGGER = Logger.getLogger(TariffModule.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	private RegistryModuleOptions options;
	
	private DeckLayoutPanel deckLayoutPanel;
	private TariffList tariffList;
	private TariffEntry tariffEntry;
	
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
		
		tariffEntry = new TariffEntry(options) {

			@Override
			protected void onBackClick() {
				showTariffList();
			}
			
		};
		
		tariffList = new TariffList(options) {

			@Override
			protected void onTariffSelect(Tariff tariff) {
				showSelectedTariff(tariff);
			}
		
		};
			
		deckLayoutPanel.add(tariffList);
		deckLayoutPanel.add(tariffEntry);
		deckLayoutPanel.showWidget(tariffList);
		
		options.getParentWidget().add(deckLayoutPanel);
	}
	
	private void showTariffList() {
		deckLayoutPanel.showWidget(tariffList);
		tariffList.onSearch();
	}
	
	private void showSelectedTariff(Tariff tariff) {
		deckLayoutPanel.showWidget(tariffEntry);
		tariffEntry.setTariff(tariff.getId());
	}
	
}
