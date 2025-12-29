package com.esferalia.aon.gwt.common.client.widget.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasAonErrorHandlers extends HasHandlers {

	HandlerRegistration addAonErrorHandler(AonErrorHandler handler);
}
