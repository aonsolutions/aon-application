package com.esferalia.aon.gwt.fiscal.client;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.gwt.fiscal.shared.invoice.ICResponse;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationParams;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationTracking;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class InvoiceCommunicationServiceAsyncDecorator implements InvoiceCommunicationServiceAsync {

	private InvoiceCommunicationServiceAsync ssa;

	public InvoiceCommunicationServiceAsyncDecorator(InvoiceCommunicationServiceAsync siiServiceAsync) {
		this.ssa = siiServiceAsync;
	}
	
	// --------------------------------------------------------------- FINANCE
	@Override
	public void getConfiguration(String domainName, int domainId, String user, AsyncCallback<InvoiceCommunicationConfiguration> callback) {
		AON.start();
		ssa.getConfiguration(domainName, domainId, user, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void getInvestAssets(String domainName, int domainId, String user, AsyncCallback<LinkedList<InvestAsset>> callback) {
		AON.start();
		ssa.getInvestAssets(domainName, domainId, user, callback);
	}
	
	@Override
	public void assignInvestAsset2Invoice(String domainName, int domainId, String user, String investAsset, Invoice invoice, AsyncCallback<Void> callback) {
		AON.start();
		ssa.assignInvestAsset2Invoice(domainName, domainId, user, investAsset, invoice, callback);
	}
	
	@Override
	public void addDocumentInvoice(String domainName, int domainId, String user, Invoice invoice, AsyncCallback<Void> callback) {
		AON.start();
		ssa.addDocumentInvoice(domainName, domainId, user, invoice, callback);
	}
	
	@Override
	public void getInvoices(Occam occam, InvoiceCommunicationParams params, AsyncCallback<LinkedList<Invoice>> callback) {
		AON.start();
		ssa.getInvoices(occam, params, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void altaLroe(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams, AsyncCallback<ICResponse> callback) {
		AON.start();
		ssa.altaLroe(domainName, domainId, user, invoice, aeatParams, new AsyncCallbackWrapper<>(callback));
		
	}

	@Override
	public void bajaLroe140(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams, AsyncCallback<String> callback) {
		AON.start();
		ssa.bajaLroe140(domainName, domainId, user, invoice, aeatParams, new AsyncCallbackWrapper<>(callback));		
	}

	@Override
	public void bajaLroe240(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams, AsyncCallback<String> callback) {
		AON.start();
		ssa.bajaLroe240(domainName, domainId, user, invoice, aeatParams, new AsyncCallbackWrapper<>(callback));		
	}
	
	@Override
	public void altaSii(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams, AsyncCallback<ICResponse> callback) {
		AON.start();
		ssa.altaSii(domainName, domainId, user, invoice, aeatParams, new AsyncCallbackWrapper<>(callback));
		
	}

	@Override
	public void bajaSii(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams, AsyncCallback<String> callback) {
		AON.start();
		ssa.bajaSii(domainName, domainId, user, invoice, aeatParams, new AsyncCallbackWrapper<>(callback));		
	}
	
	@Override
	public void refresh140(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams, AsyncCallback<Boolean> callback) {
		AON.start();
		ssa.refresh140(domainName, domainId, user, invoice, aeatParams, new AsyncCallbackWrapper<>(callback));		
	}
	
	@Override
	public void refresh240(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams, AsyncCallback<Boolean> callback) {
		AON.start();
		ssa.refresh240(domainName, domainId, user, invoice, aeatParams, new AsyncCallbackWrapper<>(callback));		
	}
	
	@Override
	public void cancel(String domainName, int domainId, String user, InvoiceCommunicationType type, Invoice invoice, AEATParams aeatParams, AsyncCallback<String> callback) {
		AON.start();
		ssa.cancel(domainName, domainId, user, type, invoice, aeatParams, new AsyncCallbackWrapper<>(callback));		
	}
	
	
	@Override
	public void getInvoiceCommunicationTrackings(String domainName, int domainId, String login, Integer invoice, AsyncCallback<LinkedList<InvoiceCommunicationTracking>> callback) {
		AON.start();
		ssa.getInvoiceCommunicationTrackings(domainName, domainId, login, invoice, new AsyncCallbackWrapper<>(callback));	
	}
	
	@Override
	public void getRequestUrl(String domainName, int domainId, String login, Integer dataResponse, AsyncCallback<String> callback) {
		AON.start();
		ssa.getRequestUrl(domainName, domainId, login, dataResponse, new AsyncCallbackWrapper<>(callback));	
	}
	
	@Override
	public void getResponseUrl(String domainName, int domainId, String login, Integer dataResponse, AsyncCallback<String> callback) {
		AON.start();
		ssa.getResponseUrl(domainName, domainId, login, dataResponse, new AsyncCallbackWrapper<>(callback));	
	}

	@Override
	public void prepareNewSii(String domainName, int domainId, String user, Integer year, AsyncCallback<Void> callback) {
		AON.start();
		ssa.prepareNewSii(domainName, domainId, user, year, new AsyncCallbackWrapper<>(null));
	}
}
