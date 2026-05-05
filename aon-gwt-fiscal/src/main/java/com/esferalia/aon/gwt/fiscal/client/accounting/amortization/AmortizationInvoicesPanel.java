package com.esferalia.aon.gwt.fiscal.client.accounting.amortization;

import com.esferalia.aon.gwt.fiscal.client.accounting.amortization.AmortizationPanel.AmortizationPanelCallback;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

class AmortizationInvoicesPanel extends DockLayoutPanel {

	AmortizationInvoicesPanel(AmortizationModuleOptions opts, AmortizationPanelCallback callback) {
		super(Unit.PX);
		
		SimpleLayoutPanel contentPanel = new SimpleLayoutPanel();
		AmortizationInvoiceTable table = new AmortizationInvoiceTable(opts, callback);
		contentPanel.setWidget(table);
		add(contentPanel);
		
	}
	
	
}
