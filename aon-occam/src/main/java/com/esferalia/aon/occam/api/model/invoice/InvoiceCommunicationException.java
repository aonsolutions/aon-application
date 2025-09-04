package com.esferalia.aon.occam.api.model.invoice;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;

import com.esferalia.aon.occam.api.GwtIncompatible;
import com.esferalia.aon.occam.api.json.invoice.InvoiceErrorJSON;
import com.esferalia.aon.occam.api.model.HasMessagesException;
import com.esferalia.aon.watson.util.AonCollectionUtils;

public class InvoiceCommunicationException extends Exception implements Serializable,HasMessagesException<InvoiceCommunicationError> {

	private static final long serialVersionUID = -3730190195705367708L;

	private List<InvoiceCommunicationError> errors = new LinkedList<>();
	
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
		errors.add(error);
	}
	public InvoiceCommunicationException(InvoiceCommunicationError error, Throwable cause) {
		super(error.getMessage(), cause);
		errors.add(error);
	}
	public InvoiceCommunicationException(InvoiceCommunicationError error) {
		super(error.getMessage());
		errors.add(error);
	}
	public InvoiceCommunicationException(List<InvoiceCommunicationError> errors) {
		if (AonCollectionUtils.isEmpty(errors)) {
			errors.add(InvoiceCommunicationError.AON_9000);
		} else {
			this.errors.addAll(errors);
		}
		
		
	}
	
   	@Override
   	public String getUniqueMessage() {
   		return AonCollectionUtils.size(errors) == 1
			? errors.get(0).getMessage()
			: null;
   	}

   	@Override
	public List<InvoiceCommunicationError> getMessages() {
		return errors;
	}
	
	@GwtIncompatible
	@Override
	public JSONArray toJSON() {
		return AonCollectionUtils.stream( getMessages() )
			.map( e ->  InvoiceErrorMessages.C050.err(InvoiceErrorKey.COMMUNICATION,e.getCode(),e.getMessage()))
			.map( InvoiceErrorJSON::toJSON )
			.collect(JSONArray::new,JSONArray::put,JSONArray::put);	
	}
    
    
}