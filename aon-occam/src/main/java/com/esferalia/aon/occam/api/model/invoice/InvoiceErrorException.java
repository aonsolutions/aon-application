package com.esferalia.aon.occam.api.model.invoice;

public class InvoiceErrorException extends Exception {
    
    private static final long serialVersionUID = 7843473763228539718L;
    
	private InvoiceError invoiceError;
    
    public InvoiceErrorException(InvoiceError invoiceError) {
    	this.invoiceError = invoiceError;
    }
    
    public InvoiceError getInvoiceError() {
    	return invoiceError;
    }

}
