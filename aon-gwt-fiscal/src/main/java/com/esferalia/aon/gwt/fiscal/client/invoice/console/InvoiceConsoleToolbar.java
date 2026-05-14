package com.esferalia.aon.gwt.fiscal.client.invoice.console;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceCommunicatorPanel;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceCommunicatorPanel.InvoiceCommunicatorPanelCallback;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceModuleOptions;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsoleParams;
import com.esferalia.aon.occam.api.model.finance.InvoiceProcessOutput;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType.InvoiceCommunicationTypeVisitor;
import com.esferalia.aon.watson.mutable.MutableBoolean;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.InlineLabel;

class InvoiceConsoleToolbar extends AonToolbar {

	static interface ToolbarAsyncCallback {
		public void onStartRunning();
		public void onEndRunning();
	}	
	
	private final AonToolbarButton showFilter;
	private final AonToolbarButton hideFilter;
	private final AonToolbarButton refresh;
	private final AonToolbarButton communicationWizardButton;
	
	private final InvoiceConsoleSelectionHandler selectionHandler;
	private final AonToolbarButton send;
	
	
	private InlineLabel runningLabel = new InlineLabel("Ejecutando");
	
	InvoiceConsoleToolbar(InvoiceModuleOptions opts, InvoiceConsoleSelectionHandler selectionHandler) {
		super("Monitor de facturas");
		
		this.selectionHandler = selectionHandler;
		
		refresh = new AonToolbarButton(AON.MSG.refresh(), AON.CSS.aonIconRefresh());
		this.add(refresh);
		
		
		showFilter = new AonToolbarButton(AON.MSG.showFilter(), AON.CSS.aonIconFilterOn());
		showFilter.setVisible(false);
		this.add(showFilter);

		hideFilter = new AonToolbarButton(AON.MSG.hideFilter(), AON.CSS.aonIconFilterOff());
		hideFilter.setVisible(true);
		this.add(hideFilter);
		
		showFilter.addClickHandler(e -> {
			hideFilter.setVisible(true);
			showFilter.setVisible(false);
		});
		hideFilter.addClickHandler(e -> {
			hideFilter.setVisible(false);
			showFilter.setVisible(true);
		});

		
		this.add(selectionHandler);
		selectionHandler.addValueChangeHandler(e -> refresh(opts));
		
		send = new AonToolbarButton(AON.MSG.issue(), AON.CSS.aonIconSend());
		send.setVisible(false);
		send.addClickHandler(e -> send(opts));
		this.add(send);
		
		communicationWizardButton = new AonToolbarButton( "Asitente comunicaci\u00F3n", AON.CSS.aonIconWizard());
		this.add(communicationWizardButton);

		refresh(opts);
		
		runningLabel.setVisible(false);
		runningLabel.setStyleName(AON.CSS.aonMarginLeft());
		runningLabel.addStyleName(AON.CSS.aonBold());
		runningLabel.getElement().getStyle().setPadding( 5, Unit.PX);
		runningLabel.getElement().getStyle().setBackgroundColor( "RoyalBlue" );
		this.add(runningLabel);
		
	}
	

	void addClickHandlerToShowFilter( ClickHandler handler ) {
		showFilter.addClickHandler(handler);
	}
	void addClickHandlerToHideFilter( ClickHandler handler ) {
		hideFilter.addClickHandler(handler);
	}

	public void addClickHandlerToRefresh(ClickHandler handler) {
		refresh.addClickHandler(handler);
	}
	
	public void addClickHandlerToWizard(ClickHandler handler) {
		communicationWizardButton.addClickHandler(handler);
	}
	
	void refresh(InvoiceModuleOptions opts) {
		runningLabel.setVisible(true);
		MutableBoolean sendVisible = new MutableBoolean(true);
		opts.optCommunicationConfig()
			.ifPresent( icc -> selectionHandler.stream()
				.filter(i -> i.getCommunicationInfo() != null )
				.forEach( i -> 
					icc.typesStream()
						.filter( cd -> cd.getCommunicationType().isPresent() )
						.forEach( cd -> {
							InvoiceCommunicationType t = cd.getCommunicationType().get();
							try {
								t.visit( new InvoiceCommunicationTypeVisitor() {
										
									@Override public void visitSERES() throws InvoiceCommunicationException 	{ /*Nothing*/ }
									@Override public void visitEMAIL() throws InvoiceCommunicationException 	{ /*Nothing*/ }
									@Override public void visitCLOSING() throws InvoiceCommunicationException 	{ /*Nothing*/ }
									@Override public void visitSII() throws InvoiceCommunicationException 		{ /*Nothing*/ } 
									@Override public void visitTBAI() throws InvoiceCommunicationException 		{ /*Nothing*/ }
									@Override public void visitLROE() throws InvoiceCommunicationException 		{ /*Nothing*/ }
									@Override public void visitFACTURAE() throws InvoiceCommunicationException 	{ /*Nothing*/ }
									
									@Override
									public void visitVERIFACTU() throws InvoiceCommunicationException {
										sendVisible.setValue( sendVisible.isTrue() &&
											i.getVerifactuInfo()
												.filter( info -> info.isPending() || info.isWrong() )
												.isPresent()
										);
									}
									
									@Override
									public void visitNO_VERIFACTU() throws InvoiceCommunicationException {
										sendVisible.setValue( sendVisible.isTrue() &&
											i.getNoVerifactuInfo()
												.filter( info -> info.isPending() || info.isWrong() )
												.isPresent()
										);
									}
									
									@Override
									public void visitSIF() throws InvoiceCommunicationException {
										sendVisible.setValue( sendVisible.isTrue() &&
											i.getSifInfo()
												.filter( info -> info.isPending() || info.isWrong() )
												.isPresent()
										);
									}
									
									
								});
								
							} catch (Exception e) {
								sendVisible.setValue(false);
							}
						} )
					)
				);
		send.setVisible( sendVisible.isTrue() );
		runningLabel.setVisible(false);
	}

	void startRun(String string) {
		runningLabel.setText(string);
		runningLabel.setVisible(true);
	}

	void endRun() {
		runningLabel.setVisible(false);
	}
	
	private void send(InvoiceModuleOptions options) {
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
		InvoiceConsoleParams params = new InvoiceConsoleParams( )
			.setDomain( options.getDomain())
			.setIds( selectionHandler
				.stream()
				.map( invRow -> invRow.getId() )
				.toArray(Integer[]::new))
			.setOffset(0)
			.setLimit( Integer.MAX_VALUE )
		;		
		InvoiceCommunicatorPanel communicatorPanel = new InvoiceCommunicatorPanel( 
			options
			,params
			,new InvoiceCommunicatorPanelCallback() {
				
				@Override
				public void onFinish( InvoiceProcessOutput output ) {
					refresh(options);
				}
				
				@Override
				public void onCancel() {
					popup.hide();
				}
			}
		);
		
		popup.add( communicatorPanel );
		popup.center();
		popup.show();
		
		Scheduler.get().scheduleDeferred(() -> communicatorPanel.setFocus(true));
	}
}
