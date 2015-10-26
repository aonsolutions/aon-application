package com.esferalia.aon.gwt.office.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AonHubServiceAsync {

	void getIssues(AsyncCallback<String> callback)
			throws IllegalArgumentException;

}
