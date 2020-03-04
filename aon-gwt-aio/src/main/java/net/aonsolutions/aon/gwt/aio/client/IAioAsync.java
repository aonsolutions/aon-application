package net.aonsolutions.aon.gwt.aio.client;

import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IAioAsync {
	
	void getAonData(String domainName, Integer domainId, String login, AsyncCallback<AonData> callback);
	void getAonData(String token, Integer domainId, AsyncCallback<AonData> callback);
}
