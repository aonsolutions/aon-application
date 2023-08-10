package net.aonsolutions.aon.invoice.communication.visitor;

import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationType;

public class InvoiceCommunicationRequest {

	InvoiceCommunicationType type;
	byte[] request;
	byte[] response;
	
	public InvoiceCommunicationType getType() {
		return type;
	}
	
	public void setType(InvoiceCommunicationType type) {
		this.type = type;
	}
	
	public byte[] getRequest() {
		return request;
	}
	
	public void setRequest(byte[] request) {
		this.request = request;
	}
	
	public byte[] getResponse() {
		return response;
	}
	
	public void setResponse(byte[] response) {
		this.response = response;
	}
}
