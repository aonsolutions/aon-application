package com.esferalia.aon.gwt.common.client;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;

public interface ModuleCallback extends Serializable {
	
	void onChange(IAccountEntryWrapper changed);
	void onRemove(IAccountEntryWrapper removed);
	void onExit();
	void onFailure(Throwable caught);
	
}
