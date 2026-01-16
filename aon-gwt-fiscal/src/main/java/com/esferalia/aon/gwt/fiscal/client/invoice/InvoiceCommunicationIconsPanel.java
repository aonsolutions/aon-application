package com.esferalia.aon.gwt.fiscal.client.invoice;

import java.util.Objects;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.google.gwt.user.client.ui.FlowPanel;

public class InvoiceCommunicationIconsPanel extends FlowPanel {
	
	public InvoiceCommunicationIconsPanel(InvoiceModuleOptions options,  Invoice invoice ) {
		setStyleName(AON.CSS.aonNowrap());
		addStyleName(AON.CSS.aonFlexBetween());
		boolean hasAdministration = (options.getConfiguration() == null 
			|| options.getConfiguration().getCommunicationConfig()==null
			|| options.getConfiguration().getCommunicationConfig().getAdministration()!=null
		);
		Administration admon = hasAdministration 
			? options.getConfiguration().getCommunicationConfig().getAdministration() 
			: Administration.COMMON_TERRITORY;
		AonCollectionUtils.valuesStream(invoice.getCommunicationInfo())
			.filter( Objects::nonNull )
			.filter(info -> info.getType() != null)
			.map( info ->  {
				InvoiceCommunicationIcon i = new InvoiceCommunicationIcon( admon, info.getType(), info.getStatus() ) ;	
				i.addClickHandler(e-> showHistory(options, info));
				return i;
			})
			.forEach( label -> add(label))
		;
	}

	private void showHistory(InvoiceModuleOptions options, InvoiceInfo info) {
		AonCustomPopup history = new AonCustomPopup();
		history.setWidth("800px");
		history.setHeight("500px");
		history.setAnimationEnabled(true);
		history.setGlassEnabled(true);
		history.setModal(true);
		history.setCaption(AON.MSG.communication());
		history.add(new InvoiceInfoHistoryPanel(options, info));
		history.center();
		history.show();
	}
}

