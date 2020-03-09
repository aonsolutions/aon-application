package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleTEDI.IAccountEntryModuleCallback;
import com.esferalia.aon.occam.api.model.AccountingInvoice;

public interface IInvoicePanelCallback extends IAccountEntryModuleCallback{
	AccountingInvoice getInvoice();
	boolean isInvestAssetsAvailable();
	void paintEntry();
}
