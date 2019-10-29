
package com.esferalia.aon.occam.api.model.tedi;

public interface ITediCallback<T> {
	ICallback getCallback();
	void onAccept(T t);
	void onCancel();
}
