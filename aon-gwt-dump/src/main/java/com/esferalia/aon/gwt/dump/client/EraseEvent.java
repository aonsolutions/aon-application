package com.esferalia.aon.gwt.dump.client;

import com.google.gwt.event.dom.client.DomEvent;

public class EraseEvent extends DomEvent<EraseHandlder> {

	public static final String ERASE = "erase";

	/**
	 * Event type for erase events. Represents the meta-data associated with
	 * this event.
	 */
	private static final Type<EraseHandlder> TYPE = new Type<EraseHandlder>(ERASE, new EraseEvent());

	/**
	 * Gets the event type associated with change events.
	 * 
	 * @return the handler type
	 */
	public static Type<EraseHandlder> getType() {
		return TYPE;
	}

	public static <T> void fire(ProgressInfo source) {
		EraseEvent event = new EraseEvent();
		source.fireEvent(event);
	}

	@Override
	public com.google.gwt.event.dom.client.DomEvent.Type<EraseHandlder> getAssociatedType() {
		return TYPE;
	}

	protected EraseEvent() {
	}

	@Override
	protected void dispatch(EraseHandlder handler) {
		handler.onErase(this);
	}

}
