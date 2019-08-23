
package com.esferalia.aon.occam.api.model.tedi;

public interface ITediCallback<T> {
	void onAccept(T t);
	void onCancel();
}
