package com.esferalia.aon.gwt.fiscal.client.invoice.fee;

import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentDomain;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentDomainName;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentUser;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getRootPanel;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceModuleOptions;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class InvoiceFeeModule implements EntryPoint {

	final static InvoiceFeeServiceAsync SERVICE;
	static {
		InvoiceFeeServiceAsync serviceRaw = GWT.create(InvoiceFeeService.class);
		SERVICE = new InvoiceFeeServiceAsyncDecorator(serviceRaw);
	}
	
	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		InvoiceModuleOptions options = new InvoiceModuleOptions();
		options.setParentWidget(root);
		options.setDomainName(getCurrentDomainName());
		options.setDomain(getCurrentDomain());
		options.setUser(getCurrentUser());
		this.onModuleLoad(options);
	}

	public void onModuleLoad(final InvoiceModuleOptions opts) {
		AON.ensureInjected();
		
		if (opts.getConfiguration() == null) {
			SERVICE.getAonConfiguration(opts.getOccam(), new AsyncCallback<AonConfiguration>() {
				@Override
				public void onSuccess(AonConfiguration result) {
					opts.setConfiguration(result);
					loadModule(opts);
				}

				@Override
				public void onFailure(Throwable caught) {
					AonMessageDialog.error(AON.MSG.loadError( " [Interno: " + caught.getMessage() + "]"));
				}
			});
		} else {
			loadModule(opts);
		}
	}
	
	private void loadModule(final InvoiceModuleOptions opts) {
		opts.getParentWidget().add(new InvoiceFeeDockPanel(opts));
	}

	public static void run() {
		GWT.runAsync(InvoiceFeeModule.class, new RunAsyncCallback() {
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(AON.MSG.loadError("InvoiceFeeModule"));
			}
			
			@Override
			public void onSuccess() {
				InvoiceFeeModule operationReport = new InvoiceFeeModule();
				operationReport.onModuleLoad();
			}
		});
	}
	
}
