package com.esferalia.aon.gwt.fiscal.client.invoice;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus.InvoiceCommunicationStatusVisitor;
import com.google.gwt.user.client.ui.InlineLabel;

class InvoiceCommunicationStatusLabel extends InlineLabel {
	
	InvoiceCommunicationStatusLabel(InvoiceCommunicationStatus status) {
		super();
		setStyleName(AON.CSS.aonNowrap());
		if (status != null) {
			setTitle( status.getDescription() );
			setText( status.getDescription() );
			status.accept(new InvoiceCommunicationStatusVisitor() {
				@Override public void visitPending() {addStyleName( AON.CSS.aonColorOrange());}
				@Override public void visitAccepted() {addStyleName( AON.CSS.aonColorGreen());}
				@Override public void visitAcceptedWithErrors() {addStyleName( AON.CSS.aonColorGreen());}
				@Override public void visitWrong() {addStyleName( AON.CSS.aonColorRed());}
				@Override public void visitCancelled() {addStyleName( AON.CSS.aonColorGray());}
				@Override public void visitExternallyCommunicated() { addStyleName( AON.CSS.aonColorBlue());}
			});
		}
	}
}
