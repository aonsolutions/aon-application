package com.esferalia.aon.gwt.issues.client;

import com.esferalia.aon.gwt.issues.shared.AonData;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IIssuesAsync {

	void getLoggedUser(AsyncCallback<String> callback);

	void getAonData(String domainName, Integer domainId, AsyncCallback<AonData> callback);
	
}
