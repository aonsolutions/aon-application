package com.esferalia.aon.gwt.fiscal.client.invoice;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.google.gwt.event.shared.GwtEvent;

public class AonInvoiceCheckedEvent extends GwtEvent<AonInvoiceCheckedHandler> {

	private static final Type<AonInvoiceCheckedHandler> TYPE = new Type<>();
	private final Invoice invoice;
	
	public AonInvoiceCheckedEvent(HasInvoiceCheckedHandlers source, Invoice invoice) {
		setSource(source);
		this.invoice = invoice;
	}

	public Invoice getInvoice() {
		return this.invoice;
	}
	
    public static Type<AonInvoiceCheckedHandler> getType() {
        return TYPE;
    }

    @Override
    public Type<AonInvoiceCheckedHandler> getAssociatedType() {
        return getType();
    }
    
	@Override
	protected void dispatch(AonInvoiceCheckedHandler handler) {
		handler.onCheck(this);
	}

	public static void fire(HasInvoiceCheckedHandlers source, Invoice invoice) {
		source.fireEvent(new AonInvoiceCheckedEvent(source, invoice));
	}

}

