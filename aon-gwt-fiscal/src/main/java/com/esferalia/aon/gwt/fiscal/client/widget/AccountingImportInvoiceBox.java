package com.esferalia.aon.gwt.fiscal.client.widget;

import java.util.LinkedList;

import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryService;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Occam;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AccountingImportInvoiceBox extends AccountingInvoiceBox {
	
	private static AccountEntryServiceAsync SERVICE;
	static {
		AccountEntryServiceAsync serviceRaw = GWT.create(AccountEntryService.class);
		SERVICE = new AccountEntryServiceAsyncDecorator(serviceRaw );
	}

	public AccountingImportInvoiceBox(final Occam occam) {
		this(occam,null,true);
	}
	public AccountingImportInvoiceBox(final Occam occam,final AonConfiguration config) {
		this(occam,config,true);
	}
	
	public AccountingImportInvoiceBox(final Occam occam,final AonConfiguration config, boolean showDescription) {
		super(occam,config,showDescription);
	}
	
	@Override
	protected void getSuggestedData(Occam occam, String query, AsyncCallback<LinkedList<AccountingInvoice>> dataCallback) {
		SERVICE.getPendingImportAccountingInvoices(occam,query,	dataCallback);
	}

}
   