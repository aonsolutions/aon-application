package com.esferalia.aon.gwt.fiscal.client.config;


import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;

public class FiscalConfig extends MainEntryPoint {

	
	private static final Logger LOGGER = Logger.getLogger(FiscalConfig.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	protected static final FiscalConfigServiceAsync FISCAL_CONFIG_SERVICE;
	static {
		FiscalConfigServiceAsync fiscalConfigServiceRaw = GWT.create(FiscalConfigService.class);
		FISCAL_CONFIG_SERVICE = new FiscalConfigServiceAsyncDecorator(fiscalConfigServiceRaw);
	}
	
	@Override
	public void onModuleLoad() {
		FISCAL_CONFIG_SERVICE.getConfiguration(getOccam(), new AsyncCallback<AonConfiguration>() {
			@Override 
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage()); 
			}

			@Override
			public void onSuccess(AonConfiguration config) {
				RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
				FiscalConfigModuleOptions options = new FiscalConfigModuleOptions();
				options.setParentWidget(root);
				options.setDomainName(getCurrentDomainName());
				options.setDomain(getCurrentDomain());
				options.setUser(getCurrentUser());
				options.setConfiguration(config);
				onModuleLoad(options);
			}
		});
	}
	
	public void onModuleLoad(FiscalConfigModuleOptions options) {
		AON.ensureInjected();
		DockLayoutPanel dockLayout = new DockLayoutPanel(Unit.PX);
		FiscalConfigPanel panel = new FiscalConfigPanel(options);
		dockLayout.add(panel);
		options.getParentWidget().add(dockLayout);
	}

	
}
