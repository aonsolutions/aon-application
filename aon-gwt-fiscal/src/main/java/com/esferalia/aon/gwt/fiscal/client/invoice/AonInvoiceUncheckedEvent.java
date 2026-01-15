package com.esferalia.aon.gwt.fiscal.client.invoice;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.google.gwt.event.shared.GwtEvent;

public class AonInvoiceUncheckedEvent extends GwtEvent<AonInvoiceUncheckedHandler> {

	private static final Type<AonInvoiceUncheckedHandler> TYPE = new Type<>();
	private final Invoice invoice;
	
	public AonInvoiceUncheckedEvent(HasInvoiceUncheckedHandlers source, Invoice invoice) {
		setSource(source);
		this.invoice = invoice;
	}

	public Invoice getInvoice() {
		return this.invoice;
	}
	
    public static Type<AonInvoiceUncheckedHandler> getType() {
        return TYPE;
    }

    @Override
    public Type<AonInvoiceUncheckedHandler> getAssociatedType() {
        return getType();
    }
    
	@Override
	protected void dispatch(AonInvoiceUncheckedHandler handler) {
		handler.onUncheck(this);
	}

	public static void fire(HasInvoiceUncheckedHandlers source, Invoice invoice) {
		source.fireEvent(new AonInvoiceUncheckedEvent(source, invoice));
	}

}

