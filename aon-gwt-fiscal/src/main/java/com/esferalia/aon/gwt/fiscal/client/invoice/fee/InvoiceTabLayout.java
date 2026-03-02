package com.esferalia.aon.gwt.fiscal.client.invoice.fee;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCloseTab;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTabLayoutPanel;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceDockPanel.InvoiceDockPanelCallback;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceViewer;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.invoice.console.InvoiceConsoleTextPanel;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.google.gwt.dom.client.Style.Unit;

class InvoiceTabLayout extends AonTabLayoutPanel {
	
	InvoiceTabLayout(InvoiceModuleOptions opts, Invoice invoice, InvoiceDockPanelCallback callback) {
		super(26, Unit.PX);
		this.setWidth("100%");
		
		InvoiceViewer viewer = new InvoiceViewer( invoice );
		this.add(viewer, new AonCloseTab(AON.MSG.invoice(), false));

		InvoiceConsoleTextPanel feePanel = new InvoiceConsoleTextPanel( invoice );
		this.add(feePanel, new AonCloseTab(AON.MSG.document(), false));
	}
	
	
}
