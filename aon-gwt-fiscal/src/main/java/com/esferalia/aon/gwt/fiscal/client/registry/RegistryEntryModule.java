package com.esferalia.aon.gwt.fiscal.client.registry;

import java.util.logging.Logger;

import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentDomain;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentDomainName;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentUser;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getRootPanel;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getRegistrySource;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.removeRegistrySource;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.registry.RegistrySource;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class RegistryEntryModule implements EntryPoint {
	
	private static final Logger LOGGER = Logger.getLogger(RegistryEntryModule.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	private static CommonServiceAsync COMMON_SERVICE;
	
	private static RegistryModuleOptions options;
	private static RegistrySource registrySource;
	
	private static RegistryEntryPanel registryEntryPanel;

	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		registrySource = RegistrySource.safeValueOf(getRegistrySource());
		removeRegistrySource();
		
		options = new RegistryModuleOptions();
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
	
	private void moduleLoad() {
		AON.ensureInjected();
		
		registryEntryPanel = new RegistryEntryPanel(options, registrySource) {
			@Override protected void onBack() {}

			@Override protected void onPrev(Integer registryId) {}

			@Override protected void onNext(Integer registryId) {}
		};
		
		options.getParentWidget().add(registryEntryPanel);
	}

}
