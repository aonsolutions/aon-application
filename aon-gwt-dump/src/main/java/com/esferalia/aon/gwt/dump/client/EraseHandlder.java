package com.esferalia.aon.gwt.dump.client;

import com.google.gwt.event.shared.EventHandler;

public interface EraseHandlder extends EventHandler {

	  /**
	   * Called when a erase event is fired.
	   * 
	   * @param event the {@link EraseEvent} that was fired
	   */
	  void onErase(EraseEvent event);

}
