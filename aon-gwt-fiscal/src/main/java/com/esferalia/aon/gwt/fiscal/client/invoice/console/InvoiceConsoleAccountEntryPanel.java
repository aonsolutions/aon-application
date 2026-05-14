package com.esferalia.aon.gwt.fiscal.client.invoice.console;

import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.AccountPreviewPanel;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceModuleOptions;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryWrapper;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

class InvoiceConsoleAccountEntryPanel extends SimpleLayoutPanel {

	public InvoiceConsoleAccountEntryPanel() {
		
	}

	public void paint(InvoiceModuleOptions opts, Invoice inv, AccountEntry entry) {
		this.clear();
		AccountEntryModuleOptions entryOptions = new AccountEntryModuleOptions()
			.setDomainName(opts.getDomainName())
			.setDomain(opts.getDomain())
			.setUser(opts.getUser())
		;
		AccountEntryWrapper wrapper = new AccountEntryWrapper( entry );
		AccountPreviewPanel p = new AccountPreviewPanel(entryOptions, wrapper);
		this.add( p );
	}

}
