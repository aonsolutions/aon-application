package com.esferalia.aon.gwt.fiscal.client.invoice;

import java.util.Objects;
import java.util.Optional;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceCommunicatorPanel.InvoiceCommunicatorPanelCallback;
import com.esferalia.aon.gwt.fiscal.client.invoice.console.InvoiceConsoleAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.invoice.console.InvoiceConsoleService;
import com.esferalia.aon.gwt.fiscal.client.invoice.console.InvoiceConsoleServiceAsync;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsoleParams;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.finance.InvoiceProcessOutput;
import com.esferalia.aon.occam.api.model.invoice.CommunicationData;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;

public class InvoiceCommunicationPanel extends FlowPanel implements HasValueChangeHandlers<Invoice> {
	
	static final InvoiceConsoleServiceAsync INVOICE_SERVICE;
	static {
		InvoiceConsoleServiceAsync fiscalServiceRaw = GWT.create(InvoiceConsoleService.class);
		INVOICE_SERVICE = new InvoiceConsoleAsyncDecorator(fiscalServiceRaw);
	}

	public InvoiceCommunicationPanel(InvoiceModuleOptions options,  Invoice invoice ) {
		this(options, invoice, true);
	}
	public InvoiceCommunicationPanel(InvoiceModuleOptions options,  Invoice invoice , boolean clickable) {
		setStyleName(AON.CSS.aonNowrap());
		addStyleName(AON.CSS.aonFlexBetween());
		paint(options, invoice);
	}
	
	private void paint(InvoiceModuleOptions options, Invoice invoice) {
		this.clear();
		if (AonCollectionUtils.isEmpty(invoice.getCommunicationInfo())) {
			options.getCommunicationConfiguration()
				.ifPresentOrElse( 
					icc -> {
						icc.typesStream( invoice.getType(), invoice.getExpDate())
							.map( CommunicationData::getCommunicationType )
							.flatMap( Optional::stream )
							.forEach( type -> add( new InvoiceCommunicationIcon( options, invoice, type, null ) ) );
						if (icc.isNoSif( invoice.getExpDate())) {
							add( new InvoiceCommunicationIcon( options, invoice ));		
						}
					}
					, () -> add( new InvoiceCommunicationIcon( options, invoice, null, null )) 
			);
		} else {
			AonCollectionUtils.valuesStream(invoice.getCommunicationInfo())
				.filter( Objects::nonNull )
				.filter(info -> info.getType() != null)
				.map( info ->  {
					InvoiceCommunicationType type = info.getType();
					InvoiceCommunicationStatus status = info.getStatus();
					InvoiceCommunicationIcon i = new InvoiceCommunicationIcon( options, invoice, type, status ) ;
					if (InvoiceCommunicationStatus.isPending( type, status )) {
						i.addClickHandler(e-> issue(options, invoice, info) );
					} else {
						i.addClickHandler(e-> showHistory(options, invoice, info));
					}
					return i;
				})
				.forEach( label -> add(label))
			;
		}
	}

	private void issue(InvoiceModuleOptions options, Invoice invoice, InvoiceInfo info) {
		InvoiceCommunicationConfiguration icc = options.getCommunicationConfiguration().orElse(null);
		if (icc == null) {
			AonMessageDialog.error("No hay configuraci\u00F3n de comunicaci\u00F3n de facturas.");
			return;
		}
		
		AonCustomDialog popup = new AonCustomDialog();
		popup.showCloseButton(false);
		popup.setAnimationEnabled(true);
		popup.setGlassEnabled(true);
		popup.setModal(true);
		popup.showCloseButton(true);

		InvoiceCommunicatorPanelCallback callback = new InvoiceCommunicatorPanelCallback() {
			
			@Override
			public void onFinish( InvoiceProcessOutput output ) {
				INVOICE_SERVICE.getInvoice( options.getOccam(), invoice.getDomain(), invoice.getId(), new AsyncCallback<Invoice>() {
					@Override public void onFailure(Throwable arg0) { /* nothing */ }
					@Override public void onSuccess(Invoice inv) { fireValueChangeEvent(inv); }
				});
			}
			
			@Override
			public void onCancel() {
				popup.hide();
			}
		};
		
		InvoiceConsoleParams params = InvoiceConsoleParams.createForInvoice( invoice );
		InvoiceCommunicatorPanel communicatorPanel = new InvoiceCommunicatorPanel( options, params, callback);
		popup.add( communicatorPanel );
		
		if ( icc.isCertificateNeeded()) {
			popup.center();
			popup.show();
		} else {
			communicatorPanel.send( options, params, callback);
		}
		
		Scheduler.get().scheduleDeferred(() -> communicatorPanel.setFocus(true));
	}

	private void showHistory(InvoiceModuleOptions options, Invoice invoice, InvoiceInfo info) {
		AonCustomPopup history = new AonCustomPopup();
		history.setWidth("800px");
		history.setHeight("500px");
		history.setAnimationEnabled(true);
		history.setGlassEnabled(true);
		history.setModal(true);
		history.setCaption(AON.MSG.communication());
		history.add(new InvoiceInfoHistoryPanel(options, invoice, info));
		history.center();
		history.show();
	}

	private void fireValueChangeEvent( Invoice invoice ) {
		ValueChangeEvent.fire( this, invoice );
	}
	
	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Invoice> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

}

