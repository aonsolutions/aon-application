package net.aonsolutions.aon.gwt.aio.client;

import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IAioAsync {

	void selectedMenu(AsyncCallback<Void> callback);

	void getAonData(String domainName, Integer domainId, String login, AsyncCallback<AonData> callback);

}
