package com.esferalia.aon.gwt.fiscal.client.accounting.wizard;

import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule.IAccountEntryModuleCallback;
import com.esferalia.aon.occam.api.model.AccountingInvoice;

public interface IInvoicePanelCallback extends IAccountEntryModuleCallback{
	AccountingInvoice getInvoice();
	boolean isInvestAssetsAvailable();
	void paintEntry();
	void enableInvoiceTotal(boolean enable);
	
	void setFocusOnRegistry();
	void setFocusOnWithholding();
	void setFocusOnPayDate();
}
