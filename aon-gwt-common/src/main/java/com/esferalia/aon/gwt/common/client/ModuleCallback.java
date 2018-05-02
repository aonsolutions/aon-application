package com.esferalia.aon.gwt.common.client;

public interface ModuleCallback<T> {
	
	void onChange(T changed);
	void onRemove(T removed);
	void onExit();
	void onFailure(Throwable caught);
	
}
