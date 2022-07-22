package com.esferalia.aon.gwt.common.client;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;

public interface ModuleCallback extends Serializable {
	
	default void onChange(IAccountEntryWrapper changed) { }
	default void onRemove(IAccountEntryWrapper removed) { }
	default void onExit() { }
	default void onFailure(Throwable caught) { }
	
}
