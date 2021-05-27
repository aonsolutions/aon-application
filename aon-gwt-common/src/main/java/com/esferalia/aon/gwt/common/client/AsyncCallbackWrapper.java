package com.esferalia.aon.gwt.common.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class AsyncCallbackWrapper<T> implements AsyncCallback<T> {

	private AsyncCallback<T> asyncCallback;

	public AsyncCallbackWrapper(AsyncCallback<T> asyncCallback) {
		this.asyncCallback = asyncCallback;
	}

	@Override
	public void onSuccess(T result) {
		asyncCallback.onSuccess(result);
		AON.stop();
	}

	@Override
	public void onFailure(Throwable caught) {
		asyncCallback.onFailure(caught);
		AON.fail();
	}
}