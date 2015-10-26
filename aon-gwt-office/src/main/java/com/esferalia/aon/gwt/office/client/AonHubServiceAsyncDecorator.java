package com.esferalia.aon.gwt.office.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author amtzdelagos
 *
 */

public class AonHubServiceAsyncDecorator implements AonHubServiceAsync {

	private AonHubServiceAsync aonHubServiceAsync;

	public AonHubServiceAsyncDecorator(AonHubServiceAsync aonHubServiceAsync) {
		this.aonHubServiceAsync = aonHubServiceAsync;
	}

	@Override
	public void getIssues(AsyncCallback<String> callback)
			throws IllegalArgumentException {
		AON.start();
		aonHubServiceAsync.getIssues(new AsyncCallbackWrapper<String>(
				callback));
	}

}
