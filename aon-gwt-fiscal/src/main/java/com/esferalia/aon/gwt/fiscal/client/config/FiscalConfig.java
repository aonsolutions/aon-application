package com.esferalia.aon.gwt.fiscal.client.config;


import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.fiscal.client.FiscalMSService;
import com.esferalia.aon.gwt.fiscal.client.FiscalMSServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalMSServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

public class FiscalConfig extends MainEntryPoint {

	
	private static final Logger LOGGER = Logger.getLogger(FiscalConfig.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	protected static final FiscalMSServiceAsync FISCAL_SERVICE;
	static {
		FiscalMSServiceAsync fiscalServiceRaw = GWT.create(FiscalMSService.class);
		FISCAL_SERVICE = new FiscalMSServiceAsyncDecorator(fiscalServiceRaw);
	}
	
	private ScrollPanel scrollPanel; 
	
	@Override
	public void onModuleLoad() {
		FISCAL_SERVICE.getAonData(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), new AsyncCallback<AonData>() {

			@Override public void onFailure(Throwable caught) { /* Nothing */ }

			@Override
			public void onSuccess(AonData aonData) {
				RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
				FiscalConfigModuleOptions options = new FiscalConfigModuleOptions();
				options.setParentWidget(root);
				options.setDomainName(getCurrentDomainName());
				options.setDomain(getCurrentDomain());
				options.setUser(getCurrentUser());
				options.setAonData(aonData);
				Window.alert("getCurrentDomain() ...: " + getCurrentDomain() + "\n"
						+ "aonData...: " + aonData.getDomain().getId());
				onModuleLoad(options);
			}
		});
	}
	
	public void onModuleLoad(FiscalConfigModuleOptions options) {
		AON.ensureInjected();
		
		DockLayoutPanel dockLayout = new DockLayoutPanel(Unit.PX);
		scrollPanel = new ScrollPanel();
		dockLayout.add(scrollPanel);
		options.getParentWidget().add(dockLayout);
	}

}
