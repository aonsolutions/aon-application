package com.esferalia.aon.gwt.common.client.widget.event;

import com.google.gwt.event.shared.GwtEvent;

public class AonErrorEvent extends GwtEvent<AonErrorHandler> {

	private static Type<AonErrorHandler> type = new Type<>();
	private String message;
	
	public AonErrorEvent(HasAonErrorHandlers source, String message) {
		setSource(source);
		setMessage(message);
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public Type<AonErrorHandler> getAssociatedType() {
		return getType();
	}

	@Override
	protected void dispatch(AonErrorHandler handler) {
		handler.onError(this);
	}

	public static Type<AonErrorHandler> getType() {
		return type;
	}
	public static <T> void fire(HasAonErrorHandlers source, String message) {
		source.fireEvent(new AonErrorEvent(source, message));
	}

}

