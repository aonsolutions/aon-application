package com.esferalia.aon.gwt.fiscal.client.invoice;

import java.util.HashMap;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationHistory;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationHistoryMapValue;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class InvoiceInfoHistoryPanel extends SimpleLayoutPanel implements HasValueChangeHandlers<Invoice>{
	private static final Logger LOGGER = Logger.getLogger(InvoiceInfoHistoryPanel.class.getName());
	
	private static final InvoiceServiceAsync SERVICE;
	static {
		InvoiceServiceAsync serviceRaw = GWT.create(InvoiceService.class);
		SERVICE = new InvoiceServiceAsyncDecorator(serviceRaw);
	}
	
	public InvoiceInfoHistoryPanel(InvoiceModuleOptions options, Invoice invoice, InvoiceInfo info) {
		super();
		setWidget( getDetails(options, invoice, info) );
	}
	
	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Invoice> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}
	private void fire( Invoice invoice ) {
		ValueChangeEvent.<Invoice>fire(InvoiceInfoHistoryPanel.this, invoice );
	}
	
	private Widget getDetails(InvoiceModuleOptions options, Invoice invoice, InvoiceInfo invoiceInfo) {
		SimpleLayoutPanel basePanel = new SimpleLayoutPanel();
		ScrollPanel scroll = new ScrollPanel();
		basePanel.setWidget(scroll);
		FlowPanel container = new FlowPanel();
		scroll.setWidget(container);

		SERVICE.communicationHistory(options.getOccam(), invoice.getId(), new AsyncCallback<HashMap<InvoiceCommunicationType,InvoiceCommunicationHistoryMapValue>>() {
			@Override
			public void onSuccess(HashMap<InvoiceCommunicationType,InvoiceCommunicationHistoryMapValue> map) {
				if ( AonCollectionUtils.isEmpty(map) ) {
					scroll.setWidget( new Label( AON.MSG.noData() ) );
					return;
				}
				AonCollectionUtils.stream(map)
					.filter( entry -> entry.getKey() == invoiceInfo.getType() )
					.findFirst()
					.ifPresent( entry -> container.add( getTypePanel(options, invoice, entry.getKey(), entry.getValue())));
			}


			@Override
			public void onFailure(Throwable th) {
				Window.alert( th.getMessage() );
				LOGGER.severe(th.getStackTrace().toString());			
				AonMessageDialog.error( th.getMessage() );
			}
		});
		return basePanel;
	}
	
	
	private Widget getTypePanel(InvoiceModuleOptions options, Invoice invoice, InvoiceCommunicationType type, InvoiceCommunicationHistoryMapValue map) {
		InvoiceInfo info = map.getInfo();
		
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.setStyleName(AON.CSS.aonWidthAlmostAll());
		mainPanel.addStyleName(AON.CSS.aonBlockCenter());
		mainPanel.addStyleName(AON.CSS.aonMarginTop());
		
		
		FlowPanel infoPanel = new FlowPanel();
		infoPanel.addStyleName(AON.CSS.aonFlexBetween());
		
			FlowPanel typePanel = new FlowPanel();
			typePanel.setStyleName(AON.CSS.aonNowrap());
			typePanel.addStyleName(AON.CSS.aonFlexBetween());
				InlineLabel typeIconLabel = new InvoiceCommunicationIcon( options, invoice, type, info.getStatus() );
				typePanel.add(typeIconLabel);
			
				InlineLabel typeLabel = new InlineLabel( type.name() );
				typeLabel.setStyleName( AON.CSS.aonFontLarger() );
				typeLabel.addStyleName( AON.CSS.aonBold() );
				typePanel.add(typeLabel);
			infoPanel.add(typePanel);
		
			InlineLabel statusLabel = new InvoiceCommunicationStatusLabel(info.getStatus());
			infoPanel.add(statusLabel);

			InlineLabel auditLabel = new InvoiceCommunicationAuditLabel(info.getCreationDate(), info.getCreationUser());
			auditLabel.setStyleName( AON.CSS.aonNowrap() );
			auditLabel.addStyleName( AON.CSS.aonFlexGrow1() );
		infoPanel.add(auditLabel);
		
		mainPanel.add(infoPanel);

		AonDisplayTable hist = new AonDisplayTable();
		hist.addStyleName(AON.CSS.aonMarginTop());
		hist.addStyleName(AON.CSS.aonMarginLeft());
		hist.addStyleName(AON.CSS.aonWidthAlmostAll());
		hist.addStyleName(AON.CSS.aonBlockCenter());
		
		for (InvoiceCommunicationHistory history : map.getHistory() ) {
			AonTableButton requestButton = new AonTableButton( AON.MSG.viewRequest() , AON.CSS.aonIconUpload() );
			AonTableButton responseButton = new AonTableButton( AON.MSG.viewResponse() , AON.CSS.aonIconDownload() );
			Label iconLabel = new Label("");
			iconLabel.setStyleName( AON.CSS.aonIconLabel() );
			iconLabel.addStyleName( AON.CSS.aonIconBullet() );

			FlowPanel histInfoPanel = new FlowPanel();
			histInfoPanel.addStyleName(AON.CSS.aonFlexBetween());
			histInfoPanel.add(new Label(history.getOperation().getDescription() ));
			histInfoPanel.add( new InvoiceCommunicationStatusLabel(history.getStatus()));
			InvoiceCommunicationAuditLabel histAuditLabel = new InvoiceCommunicationAuditLabel(history.getDate(), history.getCreationUser());
			histAuditLabel.addStyleName( AON.CSS.aonFlexGrow1() );
			histInfoPanel.add( histAuditLabel );
			hist.addRow()
				.addCell( iconLabel , AON.CSS.aonWidth20() )
				.addCell( histInfoPanel, AON.CSS.aonWidthAuto())
				.addCell( requestButton, AON.CSS.aonWidth20() )
				.addCell( responseButton, AON.CSS.aonWidth20() )
			;
			if (AonCollectionUtils.isNotEmpty(history.getResponseMessages()) ) {
				for (String msg : history.getResponseMessages() ) {
					Label msgLabel = new Label(msg);
					msgLabel.setStyleName( AON.CSS.aonLabelWithIcon() );
					msgLabel.addStyleName( AON.CSS.aonIconError() );
					hist.addRow()
						.addCell( new Label(""))
						.addCell( msgLabel)
						.addCell( new Label("") )
						.addCell( new Label("") )
					;
				}
			}
		}
		mainPanel.add(hist);
		return mainPanel;
	}

}
