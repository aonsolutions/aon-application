package com.esferalia.aon.occam.api.model.invoice;

public class InvoiceCommunicationException extends Exception {

	private static final long serialVersionUID = -3730190195705367708L;

	private InvoiceCommunicationError invoiceCommunicationError;
	
	public InvoiceCommunicationException() {
		this(InvoiceCommunicationError.AON_9000); 
	}
	public InvoiceCommunicationException(Throwable cause) {
		this(InvoiceCommunicationError.AON_9000, cause);
	}
	public InvoiceCommunicationException(String message) {
		this(InvoiceCommunicationError.AON_9000, message);
	}
	public InvoiceCommunicationException(InvoiceCommunicationError error, String cause) {
		super(cause);
		this.invoiceCommunicationError = error;
	}
	public InvoiceCommunicationException(InvoiceCommunicationError error, Throwable cause) {
		super(error.getMessage(), cause);
		this.invoiceCommunicationError = error;
	}
	public InvoiceCommunicationException(InvoiceCommunicationError error) {
		super(error.getMessage());
		this.invoiceCommunicationError = error;
	}
	
	public InvoiceCommunicationError getInvoiceCommunicationError() {
		return invoiceCommunicationError;
	}
    
    
}