package com.esferalia.aon.gwt.office.client;

import java.util.List;

import com.esferalia.aon.gwt.office.shared.Notice;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AonHubServiceAsync {

	void getNotices(AsyncCallback<List<Notice>> callback);
	

}
