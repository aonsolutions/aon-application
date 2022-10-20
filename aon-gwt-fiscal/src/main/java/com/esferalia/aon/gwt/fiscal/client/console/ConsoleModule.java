package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
 
public class ConsoleModule extends MainEntryPoint {
	
	private static final Logger LOGGER = Logger.getLogger(ConsoleModule.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}
	
	static final ConsoleServiceAsync CONSOLE_SERVICE;
	static {
		ConsoleServiceAsync consoleServiceRaw = GWT.create(ConsoleService.class);
		CONSOLE_SERVICE = new ConsoleServiceAsyncDecorator(consoleServiceRaw); 
	}

	private ConsoleModuleOptions options;

	private ConsoleModuleOptions getOptions() {
		if (this.options == null) {
			this.options = new ConsoleModuleOptions();
		}
		return this.options;
	}

	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		ConsoleModuleOptions opts = new ConsoleModuleOptions();
		opts.setParentWidget(root);
		opts.setDomainName(getCurrentDomainName());
		opts.setDomain(getCurrentDomain());
		opts.setUser(getCurrentUser());
		onModuleLoad( opts );
	
	}
	
	public void onModuleLoad(ConsoleModuleOptions options) {
		this.options = options;
		AON.ensureInjected();
		getOptions().getParentWidget().add(new ConsoleDomainModule(options));
	}

	
	public static void run() {
		GWT.runAsync(ConsoleModule.class, new RunAsyncCallback() {
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(AON.MSG.loadError("ConsoleModule"));
			}

			@Override
			public void onSuccess() {
				ConsoleModule domainIntegrityCheck = new ConsoleModule();
				domainIntegrityCheck.onModuleLoad();
			}
			
		});
	}
	
}
