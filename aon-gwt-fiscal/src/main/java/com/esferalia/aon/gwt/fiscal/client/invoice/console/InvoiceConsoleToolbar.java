package com.esferalia.aon.gwt.fiscal.client.invoice.console;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceModuleOptions;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType.InvoiceCommunicationTypeVisitor;
import com.esferalia.aon.watson.mutable.MutableBoolean;
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
	private final AonToolbarButton analyze;
	
	private final InvoiceConsoleSelectionHandler selectionHandler;
	private final AonToolbarButton send;
	
	
	private InlineLabel runningLabel = new InlineLabel("Ejecutando");
	
	InvoiceConsoleToolbar(InvoiceModuleOptions opts, InvoiceConsoleSelectionHandler selectionHandler) {
		super("Monitor de facturas");
		
		this.selectionHandler = selectionHandler;
		
		refresh = new AonToolbarButton(AON.MSG.refresh(), AON.CSS.aonIconRefresh());
		this.add(refresh);
		
		analyze = new AonToolbarButton(AON.MSG.analysis(), AON.CSS.aonIconWizard());
		this.add(analyze);
		
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
		this.add(send);
		
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
	void addClickHandlerToAnalyze(ClickHandler handler) {
		analyze.addClickHandler(handler);
	}
	
	public void addClickHandlerToRefresh(ClickHandler handler) {
		refresh.addClickHandler(handler);
	}
	
	void refresh(InvoiceModuleOptions opts) {
		runningLabel.setVisible(false);
		MutableBoolean sendVisible = new MutableBoolean(false);
		opts.getCommunicationConfiguration()
			.ifPresent( icc -> selectionHandler.stream()
				.filter(i -> i.getCommunicationInfo() != null )
				.forEach( i -> 
					icc.getTypes()
						.stream()
						.forEach( t -> {
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
	}

	void startRun(String string) {
		runningLabel.setText(string);
		runningLabel.setVisible(true);
	}

	void endRun() {
		runningLabel.setVisible(false);
	}
	
}
