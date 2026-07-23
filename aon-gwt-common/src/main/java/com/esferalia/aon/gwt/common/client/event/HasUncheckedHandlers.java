package com.esferalia.aon.gwt.common.client.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasUncheckedHandlers<T> extends HasHandlers {
	
	HandlerRegistration addUncheckedHandler(AonUncheckedHandler<T> handler);
}
