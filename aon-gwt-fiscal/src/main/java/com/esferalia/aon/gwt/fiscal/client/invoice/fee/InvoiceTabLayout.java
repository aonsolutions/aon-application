package com.esferalia.aon.gwt.fiscal.client.invoice.fee;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCloseTab;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTabLayoutPanel;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.AonInvoiceViewer;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoicePanel;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoicePanel.InvoicePanelCallback;
import com.esferalia.aon.gwt.fiscal.client.invoice.console.InvoiceConsoleTextPanel;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.google.gwt.dom.client.Style.Unit;

class InvoiceTabLayout extends AonTabLayoutPanel {
	
	InvoiceTabLayout(InvoiceFeeModuleOptions opts, Invoice invoice, InvoicePanelCallback callback) {
		super(26, Unit.PX);
		this.setWidth("100%");
		
		InvoiceModuleOptions options = new InvoiceModuleOptions()
			.setDomainName(opts.getDomainName())
			.setDomain(opts.getDomain())
			.setUser(opts.getUser())
			.setConfiguration(opts.getConfiguration())
			.setAdvancedMode(opts.isAdvancedMode())
		;
		InvoicePanel invoicePanel = new InvoicePanel( options, invoice, callback );
		this.add(invoicePanel, new AonCloseTab(AON.MSG.invoice(), false));
		
		AonInvoiceViewer viewer = new AonInvoiceViewer( invoice );
		this.add(viewer, new AonCloseTab(AON.MSG.document(), false));

		InvoiceConsoleTextPanel feePanel = new InvoiceConsoleTextPanel( invoice );
		this.add(feePanel, new AonCloseTab(AON.MSG.fees(), false));
	}
	
	
}
