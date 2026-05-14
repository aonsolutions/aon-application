package com.esferalia.aon.gwt.fiscal.client.invoice;

import com.google.gwt.event.shared.GwtEvent;

public class AonOpenIfNeededEvent extends GwtEvent<AonOpenIfNeededHandler> {

	private static final Type<AonOpenIfNeededHandler> TYPE = new Type<>();
	
	public AonOpenIfNeededEvent(HasOpenIfNeededHandlers source) {
		setSource(source);
	}

    public static Type<AonOpenIfNeededHandler> getType() {
        return TYPE;
    }

    @Override
    public Type<AonOpenIfNeededHandler> getAssociatedType() {
        return getType();
    }
    
	@Override
	protected void dispatch(AonOpenIfNeededHandler handler) {
		handler.onOpenIfNeeded(this);
	}

	public static void fire(HasOpenIfNeededHandlers source) {
		source.fireEvent(new AonOpenIfNeededEvent(source));
	}

}

