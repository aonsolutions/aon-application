package com.esferalia.aon.gwt.fiscal.client.invoice.console;

import java.util.Objects;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus.InvoiceCommunicationStatusVisitor;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;

public class InvoiceCommunicationWidget extends FlowPanel {
	
	public InvoiceCommunicationWidget( Invoice invoice ) {
		setStyleName(AON.CSS.aonNowrap());
		addStyleName(AON.CSS.aonFlexBetween());
		AonCollectionUtils.valuesStream(invoice.getCommunicationInfo())
			.filter( Objects::nonNull )
			.filter(info -> info.getType() != null)
			.map(this::getLabel )
			.forEach( label -> add(label))
		;
	}
	
	private Label getLabel( InvoiceInfo info ) {
		InlineLabel label = new InlineLabel();
		label.setStyleName(AON.CSS.aonLabelWithIcon());
		label.addStyleName(AON.CSS.aonMarginRight());
		label.addStyleName(AON.CSS.aonDisplayFlex());
		if (info.getStatus() != null) {
			info.getStatus().accept(new InvoiceCommunicationStatusVisitor() {
				@Override
				public void visitPending() {
					label.addStyleName(AON.CSS.aonIconQrCodeOrange());
					label.setTitle(info.getType().name() + ". Factura pendiente de comunicaci\u00f3n");
				}

				@Override
				public void visitAccepted() {
					label.addStyleName(AON.CSS.aonIconQrCodeGreen());
					label.setTitle(info.getType().name() + ". Factura comunicada correctamente");
				}

				@Override
				public void visitAcceptedWithErrors() {
					label.addStyleName(AON.CSS.aonIconQrCodeGreen());
					label.setTitle(info.getType().name() + ". Factura comunicada con errores");
				}

				@Override
				public void visitWrong() {
					label.addStyleName(AON.CSS.aonIconQrCodeRed());
					label.setTitle(info.getType().name() + ". Factura comunicaci\u00f3n rechazada");
				}

				@Override
				public void visitCancelled() {
					label.addStyleName(AON.CSS.aonIconClose());
					label.setTitle(info.getType().name() + ". Factura anulada");
				}
			});
		}
		return label;
	}
}

