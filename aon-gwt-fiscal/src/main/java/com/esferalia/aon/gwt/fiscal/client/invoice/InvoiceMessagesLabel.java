package com.esferalia.aon.gwt.fiscal.client.invoice;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorLevel.InvoiceErrorLevelVisitor;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.KeyCodes;

public class InvoiceMessagesLabel extends AonTableButton {
	
	public InvoiceMessagesLabel( Invoice invoice ) {
		super( "" );
		if ( !invoice.isRecorded()) {
			if ( invoice.hasMessages()) {
				addStyleName(AON.CSS.aonClickableLabel());
				invoice.getMoreSeriousLevel()
					.map( l -> { setTitle( l.getLabel() );return l;} ) 
					.map( l -> l.visit(new BackgroundErrorLevelVisitor()) )
					.ifPresent(s -> addStyleName(s))
				;
				addClickHandler(event -> {
					final AonCustomPopup infoPanel = new AonCustomPopup();
					infoPanel.setAutoHideEnabled( true );
					infoPanel.setWidth( "600px");
					infoPanel.setHeight("400px");
					InvoiceMassagesList messagesPanel = new InvoiceMassagesList( invoice );
					infoPanel.add(messagesPanel);
					messagesPanel.addClickHandler( e -> infoPanel.hide() );
					messagesPanel.addKeyUpHandler( e -> {
						if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER || e.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
							infoPanel.hide();
						}
					});
					infoPanel.center();
					infoPanel.show();
					Scheduler.get().scheduleDeferred(() -> messagesPanel.setFocus( true ) );
					event.stopPropagation();
					
				});
			} else {
				addStyleName(AON.CSS.aonIconCircleGreen());
			}
		} else {
		}
	}

	private static class BackgroundErrorLevelVisitor implements InvoiceErrorLevelVisitor<String> {
		@Override public String visitINF() {return AON.CSS.aonIconCircleYellow();}
		@Override public String visitWRN() {return AON.CSS.aonIconCircleOrange();}
		@Override public String visitERR() {return AON.CSS.aonIconCircleRed();}
	}		

}

