package com.esferalia.aon.gwt.fiscal.client.stats;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasYearClickHandler extends HasHandlers {
	HandlerRegistration addYearClickHandler(YearClickHandler handler);
}
