package com.esferalia.aon.gwt.fiscal.client;

import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.gwt.fiscal.shared.invoice.ICResponse;
import com.esferalia.aon.gwt.fiscal.shared.invoice.InvoiceParams;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.finance.SiiConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SiiServiceAsyncDecorator implements SiiServiceAsync {

	private SiiServiceAsync ssa;

	public SiiServiceAsyncDecorator(SiiServiceAsync siiServiceAsync) {
		this.ssa = siiServiceAsync;
	}
	
	// --------------------------------------------------------------- FINANCE
	@Override
	public void getSiiConfiguration(String domainName, int domainId, String user, AsyncCallback<SiiConfiguration> callback) {
		AON.start();
		ssa.getSiiConfiguration(domainName, domainId, user, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void getInvoices(String domainName, int domainId, String user, InvoiceParams params, AsyncCallback<List<Invoice>> callback) {
		AON.start();
		ssa.getInvoices(domainName, domainId, user, params, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void altaLroe140(String domainName, int domainId, String user, InvoiceCommunicationType communicationType,
			Invoice invoice, AEATParams aeatParams, AsyncCallback<ICResponse> callback) {
		AON.start();
		ssa.altaLroe140(domainName, domainId, user, communicationType, invoice, aeatParams, new AsyncCallbackWrapper<>(callback));
		
	}

	@Override
	public void bajaLroe140(String domainName, int domainId, String user, InvoiceCommunicationType communicationType,
			Invoice invoice, AEATParams aeatParams, AsyncCallback<String> callback) {
		AON.start();
		ssa.bajaLroe140(domainName, domainId, user, communicationType, invoice, aeatParams, new AsyncCallbackWrapper<>(callback));		
	}
	
	@Override
	public void altaLroe240(String domainName, int domainId, String user, InvoiceCommunicationType communicationType,
			Invoice invoice, AEATParams aeatParams, AsyncCallback<ICResponse> callback) {
		AON.start();
		ssa.altaLroe240(domainName, domainId, user, communicationType, invoice, aeatParams, new AsyncCallbackWrapper<>(callback));
		
	}

	@Override
	public void bajaLroe240(String domainName, int domainId, String user, InvoiceCommunicationType communicationType,
			Invoice invoice, AEATParams aeatParams, AsyncCallback<String> callback) {
		AON.start();
		ssa.bajaLroe240(domainName, domainId, user, communicationType, invoice, aeatParams, new AsyncCallbackWrapper<>(callback));		
	}
	
	@Override
	public void altaSii(String domainName, int domainId, String user, InvoiceCommunicationType communicationType,
			Invoice invoice, AEATParams aeatParams, AsyncCallback<String> callback) {
		AON.start();
		ssa.altaSii(domainName, domainId, user, communicationType, invoice, aeatParams, new AsyncCallbackWrapper<>(callback));
		
	}

	@Override
	public void bajaSii(String domainName, int domainId, String user, InvoiceCommunicationType communicationType,
			Invoice invoice, AEATParams aeatParams, AsyncCallback<String> callback) {
		AON.start();
		ssa.bajaSii(domainName, domainId, user, communicationType, invoice, aeatParams, new AsyncCallbackWrapper<>(callback));		
	}
	
	@Override
	public void refresh140(String domainName, int domainId, String user, InvoiceCommunicationType communicationType, Invoice invoice, AEATParams aeatParams, AsyncCallback<Boolean> callback) {
		AON.start();
		ssa.refresh140(domainName, domainId, user, communicationType, invoice, aeatParams, new AsyncCallbackWrapper<>(callback));		
	}
	
	@Override
	public void refresh240(String domainName, int domainId, String user, InvoiceCommunicationType communicationType, Invoice invoice, AEATParams aeatParams, AsyncCallback<Boolean> callback) {
		AON.start();
		ssa.refresh240(domainName, domainId, user, communicationType, invoice, aeatParams, new AsyncCallbackWrapper<>(callback));		
	}
	
}
