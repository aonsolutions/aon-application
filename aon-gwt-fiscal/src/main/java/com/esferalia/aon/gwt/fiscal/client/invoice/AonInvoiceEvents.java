package com.esferalia.aon.gwt.fiscal.client.invoice;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.event.shared.GwtEvent.Type;

public class AonInvoiceEvents {

	// -------------------------------------------------------------------
	// --------------------------------------- Invoice Record Event ------
	// -------------------------------------------------------------------
	public interface HasInvoiceRecordHandlers extends HasHandlers {
		HandlerRegistration addInvoiceRecordHandler(AonInvoiceRecordHandler handler);
	}
	
	public interface AonInvoiceRecordHandler extends EventHandler {
		void onRecord(AonInvoiceRecordEvent event);
	}
	
	public static class AonInvoiceRecordEvent extends GwtEvent<AonInvoiceRecordHandler> {
	
		private static final Type<AonInvoiceRecordHandler> TYPE = new Type<>();
		private final Invoice invoice;
		
		public AonInvoiceRecordEvent(HasInvoiceRecordHandlers source, Invoice invoice) {
			setSource(source);
			this.invoice = invoice;
		}
	
		public Invoice getInvoice() {
			return this.invoice;
		}
		
	    public static Type<AonInvoiceRecordHandler> getType() {
	        return TYPE;
	    }
	
	    @Override
	    public Type<AonInvoiceRecordHandler> getAssociatedType() {
	        return getType();
	    }
	    
		@Override
		protected void dispatch(AonInvoiceRecordHandler handler) {
			handler.onRecord(this);
		}
	
		public static void fire(HasInvoiceRecordHandlers source, Invoice invoice) {
			source.fireEvent(new AonInvoiceRecordEvent(source, invoice));
		}
	}
	
	// -------------------------------------------------------------------
	// -------------------------------------- Invoice Checked Event ------
	// -------------------------------------------------------------------

	public interface HasInvoiceCheckedHandlers extends HasHandlers {
		HandlerRegistration addInvoiceCheckedHandler(AonInvoiceCheckedHandler handler);
	}
	
	public interface AonInvoiceCheckedHandler extends EventHandler {
		void onCheck(AonInvoiceCheckedEvent event);
	}
	
	public static class AonInvoiceCheckedEvent extends GwtEvent<AonInvoiceCheckedHandler> {

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

	// -------------------------------------------------------------------
	// ------------------------------------ Invoice Unchecked Event ------
	// -------------------------------------------------------------------

	public interface HasInvoiceUncheckedHandlers extends HasHandlers {
		HandlerRegistration addInvoiceUncheckedHandler(AonInvoiceUncheckedHandler handler);
	}
	
	public interface AonInvoiceUncheckedHandler extends EventHandler {
		void onUncheck(AonInvoiceUncheckedEvent event);
	}
	
	public static class AonInvoiceUncheckedEvent extends GwtEvent<AonInvoiceUncheckedHandler> {

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
}

