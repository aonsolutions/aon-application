package com.esferalia.aon.gwt.office.client;

import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.gwt.office.shared.Notice;
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
	public void getNotices(AsyncCallback<List<Notice>> callback)
			throws IllegalArgumentException {
		AON.start();
		aonHubServiceAsync.getNotices(new AsyncCallbackWrapper<List<Notice>>(
				callback));
	}

}
