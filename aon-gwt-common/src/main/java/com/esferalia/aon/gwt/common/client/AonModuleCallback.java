package com.esferalia.aon.gwt.common.client;

public interface AonModuleCallback<T> {
	
	void onChange(T changed);
	void onRemove(T removed);
	void onExit(T edited);
	void onFailure(Throwable caught);
	
}
