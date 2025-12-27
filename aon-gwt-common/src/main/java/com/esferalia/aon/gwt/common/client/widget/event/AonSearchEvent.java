package com.esferalia.aon.gwt.common.client.widget.event;

import com.google.gwt.event.shared.GwtEvent;

public class AonSearchEvent extends GwtEvent<AonSearchHandler> {

	private static Type<AonSearchHandler> type = new Type<>();
	
	public AonSearchEvent(HasAonSearchHandlers source) {
		setSource(source);
	}

	public static Type<AonSearchHandler> getType() {
		return type;
	}

	public Type<AonSearchHandler> getAssociatedType() {
	    return getType();
	}

	@Override
	protected void dispatch(AonSearchHandler handler) {
		handler.onSearch(this);
	}

	public static <T> void fire(HasAonSearchHandlers source) {
		source.fireEvent(new AonSearchEvent(source));
	}

}

