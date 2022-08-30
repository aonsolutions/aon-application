package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule.IAccountEntryModuleCallback;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;

public interface IInvoicePanelCallback extends IAccountEntryModuleCallback{
	AccountingInvoice getInvoice();
	AccountingRegistry getLastRegistry();
	boolean isInvestAssetsAvailable();
	void paintEntry();
}
