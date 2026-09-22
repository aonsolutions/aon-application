package com.esferalia.aon.gwt.fiscal.client.invoice;

import java.util.Objects;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.google.gwt.user.client.ui.FlowPanel;

public class InvoiceCommunicationPanel extends FlowPanel {
	public InvoiceCommunicationPanel(InvoiceModuleOptions options,  Invoice invoice ) {
		this(options, invoice, true);
	}
	public InvoiceCommunicationPanel(InvoiceModuleOptions options,  Invoice invoice , boolean clickable ) {
		setStyleName(AON.CSS.aonNowrap());
		addStyleName(AON.CSS.aonFlexBetween());
		if (AonCollectionUtils.isEmpty(invoice.getCommunicationInfo())) {
			options.getCommunicationConfiguration()
				.ifPresentOrElse(icc -> {
					AonCollectionUtils.stream( icc.getTypes( invoice.getType(), invoice.getExpDate() ))
						.forEach( type -> add( new InvoiceCommunicationIcon( options, invoice, type, null ) ) );
					if (icc.isNoSif( invoice.getExpDate() )) {
						add( new InvoiceCommunicationIcon( options, invoice ) );
					}
				}
				, () -> add( new InvoiceCommunicationIcon( options, invoice, null, null ) )
			);
		} else {
			AonCollectionUtils.valuesStream(invoice.getCommunicationInfo())
				.filter( Objects::nonNull )
				.filter(info -> info.getType() != null)
				.map( info ->  {
					InvoiceCommunicationIcon i = new InvoiceCommunicationIcon( options, invoice, info.getType(), info.getStatus() ) ;
					if (clickable) {
						i.addStyleName(AON.CSS.aonClickable());
						i.addClickHandler(e-> showHistory(options, invoice, info));
					}
					return i;
				})
				.forEach( label -> add(label))
			;
		}
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
}

