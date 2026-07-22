package com.esferalia.aon.gwt.common.client.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasCheckedHandlers<T> extends HasHandlers {
	
	HandlerRegistration addCheckedHandler(AonCheckedHandler<T> handler);
}
