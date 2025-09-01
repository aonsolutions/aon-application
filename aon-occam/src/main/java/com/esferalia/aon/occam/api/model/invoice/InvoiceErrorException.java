package com.esferalia.aon.occam.api.model.invoice;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;

import com.esferalia.aon.occam.api.GwtIncompatible;
import com.esferalia.aon.occam.api.json.invoice.InvoiceErrorJSON;
import com.esferalia.aon.occam.api.model.HasMessagesException;
import com.esferalia.aon.watson.util.AonCollectionUtils;

public class InvoiceErrorException extends Exception implements HasMessagesException<InvoiceError> {
    
    private static final long serialVersionUID = 7843473763228539718L;
    
	private List<InvoiceError> errors = new LinkedList<>();
    
    public InvoiceErrorException(List<InvoiceError> errors) {
    	this.errors = errors;
    }
    	
   	public InvoiceErrorException(InvoiceError invoiceError) {
    	errors.add(invoiceError);
    }
    
	@Override
	public List<InvoiceError> getMessages() {
		return errors;
	}

	@GwtIncompatible
	@Override
	public JSONArray toJSON() {
		return AonCollectionUtils.stream( getMessages() )
			.map( InvoiceErrorJSON::toJSON )
			.collect(JSONArray::new,JSONArray::put,JSONArray::put);	
	}

}
