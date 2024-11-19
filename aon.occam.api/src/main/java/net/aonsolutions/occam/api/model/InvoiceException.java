package net.aonsolutions.occam.api.model;

import com.esferalia.aon.watson.error.AonCoreException;

import net.aonsolutions.occam.api.model.type.InvoiceErrorLevel;

public class InvoiceException extends AonCoreException {

	private static final long serialVersionUID = -8186171398557911306L;
	
	private final Invoice invoice;
	
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
   		msg.append(" ");
   		invoice.messageStream()
    		.filter( e -> e.getLevel() == InvoiceErrorLevel.ERR)
    		.forEach( e -> msg.append("[" + e.getMessage()+"] ") );
    	return msg.toString();
    }
    
}