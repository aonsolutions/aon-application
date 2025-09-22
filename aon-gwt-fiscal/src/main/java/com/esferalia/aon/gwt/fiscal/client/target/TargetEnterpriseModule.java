package com.esferalia.aon.gwt.fiscal.client.target;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;

import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.ui.DeckLayoutPanel;

import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.*;
import com.google.gwt.core.client.EntryPoint;

public class TargetEnterpriseModule  implements EntryPoint {
	
	private static final Logger LOGGER = Logger.getLogger(TargetEnterpriseModule.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	private RegistryModuleOptions options;
	
	private DeckLayoutPanel deckLayoutPanel;
	private TargetEnterpriseList targetList;
	
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
		
		targetList = new TargetEnterpriseList(options);
		deckLayoutPanel.add(targetList);
		deckLayoutPanel.showWidget(targetList);
		
		options.getParentWidget().add(deckLayoutPanel);
	}
	
}
