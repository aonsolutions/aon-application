package com.esferalia.aon.gwt.fiscal.client.invoice.console;


import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsoleParams;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;


public class InvoiceConsoleModule extends MainEntryPoint {
	
	private static final CommonServiceAsync COMMON_SERVICE;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
	}

	static final InvoiceConsoleServiceAsync INVOICE_SERVICE;
	static {
		InvoiceConsoleServiceAsync fiscalServiceRaw = GWT.create(InvoiceConsoleService.class);
		INVOICE_SERVICE = new InvoiceConsoleAsyncDecorator(fiscalServiceRaw);
	}

	private SimpleLayoutPanel content;

	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		InvoiceConsoleModuleOptions opts = new InvoiceConsoleModuleOptions();
		opts.setParentWidget(root);
		opts.setDomainName(getCurrentDomainName());
		opts.setDomain(getCurrentDomain());
		opts.setUser(getCurrentUser());
		this.onModuleLoad( opts );
	}
	
	public void onModuleLoad( InvoiceConsoleModuleOptions opts ) {
		AON.ensureInjected();
		COMMON_SERVICE.getAonConfiguration(opts.getOccam(),new AsyncCallback<AonConfiguration>() {
			@Override
			public void onSuccess(AonConfiguration result) {
				opts.setConfiguration(result);
				loadModule( opts );
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert( AON.MSG.loadError("Operation Report"));
			}
		});

	}
	
	private void loadModule( InvoiceConsoleModuleOptions opts ) {
		AonLayoutPanel aonLayoutPanel = new AonLayoutPanel(Unit.PX);
		AonToolbar toolbar = new AonToolbar("Monitor de facturas");
		aonLayoutPanel.addNorth(toolbar, AonToolbar.HEIGTH);
		InvoiceConsoleFilter filterPanel = new InvoiceConsoleFilter(opts);
		filterPanel.addValueChangeHandler(e -> search(opts, e.getValue()) );
		aonLayoutPanel.addNorth(filterPanel, 70);
		
		content = new SimpleLayoutPanel();
		content.setStyleName(AON.CSS.aonSelector());
		aonLayoutPanel.add(content);
		opts.getParentWidget().add(aonLayoutPanel);
		
		filterPanel.initialize(opts);
	}
	
	
	private void search(InvoiceConsoleModuleOptions opts, InvoiceConsoleParams params) {
		content.clear();
		content.setWidget(new InvoiceConsoleTable(opts, params ));	
	}

	public static void run() {
		GWT.runAsync(InvoiceConsoleModule.class, new RunAsyncCallback() {
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(AON.MSG.loadError("InvoiceConsoleModule"));
			}
			
			@Override
			public void onSuccess() {
				InvoiceConsoleModule operationReport = new InvoiceConsoleModule();
				operationReport.onModuleLoad();
			}
		});
	}
		
}
