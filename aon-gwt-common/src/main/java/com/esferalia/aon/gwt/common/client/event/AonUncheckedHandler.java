package com.esferalia.aon.gwt.common.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface AonUncheckedHandler<T> extends EventHandler {
	
	void onUncheck(AonUncheckedEvent<T> event);
	
}
