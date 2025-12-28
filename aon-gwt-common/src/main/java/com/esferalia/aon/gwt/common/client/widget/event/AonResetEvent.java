package com.esferalia.aon.gwt.common.client.widget.event;

import com.google.gwt.event.shared.GwtEvent;

public class AonResetEvent extends GwtEvent<AonResetHandler> {

	private static Type<AonResetHandler> type = new Type<>();
	
	public AonResetEvent(HasAonResetHandlers source) {
		setSource(source);
	}

	public static Type<AonResetHandler> getType() {
		return type;
	}

	public Type<AonResetHandler> getAssociatedType() {
	    return getType();
	}

	@Override
	protected void dispatch(AonResetHandler handler) {
		handler.onReset(this);
	}

	public static <T> void fire(HasAonResetHandlers source) {
		source.fireEvent(new AonResetEvent(source));
	}

}

