package com.esferalia.aon.gwt.fiscal.client.mod200;

import com.esferalia.aon.occam.api.model.fiscal.mod200.Mod200Key;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.ui.ValueBoxBase;

public class Mod200ChangedEvent<T extends ValueBoxBase<?>> extends ChangeEvent {
	private Mod200Key key;
	private T sourceWidget;
	
	public Mod200ChangedEvent(Mod200Key key, T sourceWidget) {
		this.key = key;
		this.sourceWidget = sourceWidget;
	}

	public Mod200Key getKey() {
		return key;
	}

	public T getSourceWidget() {
		return sourceWidget;
	}
	
}
