package com.esferalia.aon.gwt.common.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface AonCheckedHandler<T> extends EventHandler {
	
	void onCheck(AonCheckedEvent<T> event);
	
}
