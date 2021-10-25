package com.esferalia.aon.gwt.common.client;

import java.io.Serializable;

public interface AonModuleCallback<T> extends Serializable {
	
	void onChange(T changed);
	void onRemove(T removed);
	void onExit(T edited);
	void onFailure(Throwable caught);
	
}
