package com.esferalia.aon.gwt.fiscal.client.invoice;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog.AonMessageDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.invoice.console.InvoiceConsoleTextPanel;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class InvoicePanel extends AonDockLayout {
	
	public interface InvoicePanelCallback {
		Invoice getInvoice();
		void onSave( Invoice invoice );
		void onDelete( Invoice invoice );
		void onError( String message);
	}

	private static final Logger LOGGER = Logger.getLogger(InvoicePanel.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}
	private static final InvoiceServiceAsync SERVICE;
	static {
		InvoiceServiceAsync serviceRaw = GWT.create(InvoiceService.class);
		SERVICE = new InvoiceServiceAsyncDecorator(serviceRaw);
	}
	
	private FlowPanel messagePanel = new FlowPanel();
	
//	public InvoicePanel(InvoiceModuleOptions options, Invoice invoice) {
//		this(options, invoice, null);
//	}
	public InvoicePanel(InvoiceModuleOptions options, Invoice invoice, InvoicePanelCallback callback) {
		super( AON.MSG.invoice() );
		if (options.getConfiguration() != null) {
			load(options, invoice, callback);
		} else {
			SERVICE.getAonConfiguration(options.getOccam(), new AsyncCallback<AonConfiguration>() {
				@Override
				public void onSuccess(AonConfiguration result) {
					options.setConfiguration(result);
					load(options, invoice, callback);
				}
				@Override
				public void onFailure(Throwable caught) {
					showMainError(AON.MSG.loadError("InvoicePanel [Interno: " + caught.getMessage()+ "]"), null);
				}
			});
		}
	}
	
	private void showMainError( String cause, AonMessageDialogCallback callback) {
		AonMessageDialog.error( cause , callback);
	}

	private boolean checkLoad(InvoiceModuleOptions options, Invoice invoice) {
		Integer optionsDomain = options.getDomain();
		Integer configurationDomain = options.getConfiguration().getDomain().getId();
		Integer invoiceDomain = options.getConfiguration().getDomain().getId();
		if ( AonNumberUtils.notEquals(optionsDomain, configurationDomain) ) {
			showMainError( "Diferentes dominios en 'options' y 'configuration'", InvoicePanel.this::clear);
			return false;
		} else  if ( AonNumberUtils.notEquals(invoiceDomain, configurationDomain) ) {
			showMainError( "Diferentes dominios en 'factura' y 'configuration'", InvoicePanel.this::clear);
			return false;
		}
		return true;
	}
	
	private void load(InvoiceModuleOptions options, Invoice invoice, InvoicePanelCallback callback) {
		if (checkLoad(options, invoice)) {
			paint(options, invoice, callback);
		}
	}

	private void paint(InvoiceModuleOptions options, Invoice invoice, InvoicePanelCallback callback) {
		resetWidget();
		fillTooolbar(options, invoice, callback);
		fillContent(options, invoice);
	}

	private void fillTooolbar(InvoiceModuleOptions options, Invoice invoice, InvoicePanelCallback callback) {
		// -----------------------------------------------------------------
		// ------------------------------------------------- Save button ---
		// -----------------------------------------------------------------
		AonToolbarButton saveButton = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave() );
		saveButton.addClickHandler(e -> {
			startAction(saveButton);
			SERVICE.save( options.getOccam(), invoice, new AsyncCallback<Invoice>() {
				@Override
				public void onSuccess(Invoice invoice) {
					finalizeAction(saveButton, AON.MSG.saveSuccess());
					paint(options, invoice, callback);
					if (callback != null) {
						callback.onSave( invoice );
					}
				}
				@Override
				public void onFailure(Throwable caught) {
					finalizeAction(saveButton, caught);
				}
			});
			
		});
		this.getToolbar().add(saveButton);
		
		// -----------------------------------------------------------------
		// ------------------------------------------ Real Delete button ---
		// -----------------------------------------------------------------
		if (invoice.getId() != null) {
			AonToolbarButton deleteButton = new AonToolbarButton( AON.MSG.deleteAction(), AON.CSS.aonIconDelete() );
			deleteButton.addClickHandler(e -> {
				startAction(deleteButton);
				SERVICE.save( options.getOccam(), invoice, new AsyncCallback<Invoice>() {
					@Override
					public void onSuccess(Invoice invoice) {
						finalizeAction(deleteButton, AON.MSG.deleteSuccess());
						resetWidget();
						if (callback != null) {
							callback.onDelete( invoice );
						}
					}
					@Override
					public void onFailure(Throwable caught) {
						finalizeAction(deleteButton, caught);
					}
				});
			});
			this.getToolbar().add(deleteButton);
		}
		
		// -----------------------------------------------------------------
		// ----------------------------------------- Logic Delete button ---
		// -----------------------------------------------------------------
		if (invoice.getId() == null) {
			AonToolbarButton deleteButton = new AonToolbarButton( AON.MSG.deleteAction(), AON.CSS.aonIconDelete() );
			deleteButton.addClickHandler(e -> {
				resetWidget();
				if (callback != null) {
					callback.onDelete( invoice );
				}
			});
			this.getToolbar().add(deleteButton);
		}
		
	}
	
	private void startAction(AonToolbarButton button) {
		AonMessagePanel.hideMessage(messagePanel);
		button.setEnabled(false);
	}
	private void finalizeAction(AonToolbarButton button) {
		button.setEnabled(true);
	}
	private void finalizeAction(AonToolbarButton button, String message) {
		finalizeAction(button);
		AonMessagePanel.showSuccess(messagePanel, message);
	}
	private void finalizeAction(AonToolbarButton button, Throwable caught) {
		finalizeAction(button);
		AonMessagePanel.showError(messagePanel, caught.getMessage());
	}
	
	private void fillContent(InvoiceModuleOptions options, Invoice invoice) {
		FlowPanel container = new FlowPanel();
		container.addStyleName(AON.CSS.aonFlexColumn());
		container.add(messagePanel);
		
		SimpleLayoutPanel contextContainer = new SimpleLayoutPanel();
		contextContainer.setHeight("100%");
		contextContainer.getElement().getStyle().setProperty("margin-left", "1rem");
		container.add(contextContainer);
		
		this.add(container);
		
		InvoiceConsoleTextPanel invoicePanel = new InvoiceConsoleTextPanel( invoice );
		contextContainer.setWidget( invoicePanel );
	}

}
