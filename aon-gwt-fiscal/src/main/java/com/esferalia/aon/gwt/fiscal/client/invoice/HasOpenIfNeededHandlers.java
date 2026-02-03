package com.esferalia.aon.gwt.fiscal.client.invoice;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasOpenIfNeededHandlers extends HasHandlers {
	
	HandlerRegistration addOpenIfNeededHandler(AonOpenIfNeededHandler handler);
}
