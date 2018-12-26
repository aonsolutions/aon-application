package com.esferalia.aon.gwt.api.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IApiAsync {

	void base(String str, AsyncCallback<String> callback);

}
