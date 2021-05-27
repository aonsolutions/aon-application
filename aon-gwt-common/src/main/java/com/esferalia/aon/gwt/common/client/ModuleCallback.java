package com.esferalia.aon.gwt.common.client;

import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;

public interface ModuleCallback {
	
	void onChange(IAccountEntryWrapper changed);
	void onRemove(IAccountEntryWrapper removed);
	void onExit();
	void onFailure(Throwable caught);
	
}
