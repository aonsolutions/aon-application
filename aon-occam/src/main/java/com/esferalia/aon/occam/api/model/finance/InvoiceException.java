package com.esferalia.aon.occam.api.model.finance;

import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorLevel;
import com.esferalia.aon.watson.error.AonCoreException;

public class InvoiceException extends AonCoreException {

	private static final long serialVersionUID = -8186171398557911306L;
	
	private Invoice invoice;
	
    public InvoiceException(Invoice invoice, Throwable cause) {
    	super(cause);
    	this.invoice = invoice;
    }
    
    public InvoiceException(Invoice invoice, String msg) {
    	super(msg);
    	this.invoice = invoice;
    }
    
    public Invoice getInvoice() {
		return invoice;
	}
    
    @Override
    public String getMessage() {
    	StringBuilder msg = new StringBuilder(super.getMessage());
    	if (invoice != null && invoice.getMessages() != null) {
    		msg.append(" ");
    		invoice.getMessages().stream()
    		.filter( e -> e.getLevel() == InvoiceErrorLevel.ERR)
    		.forEach( e -> msg.append("[" + e.getMessage()+"] ") );
    	}
    	return msg.toString();
    }
    
}