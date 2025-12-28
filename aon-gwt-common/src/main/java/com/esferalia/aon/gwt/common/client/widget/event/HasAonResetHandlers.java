package com.esferalia.aon.gwt.common.client.widget.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasAonResetHandlers extends HasHandlers {

	HandlerRegistration addAonResetHandler(AonResetHandler handler);
}
